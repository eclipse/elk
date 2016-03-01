/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.compaction;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.compaction.oned.CNode;
import org.eclipse.elk.alg.layered.compaction.oned.ISpacingsHandler;
import org.eclipse.elk.alg.layered.compaction.oned.OneDimensionalCompactor;
import org.eclipse.elk.alg.layered.compaction.oned.algs.ICompactionAlgorithm;
import org.eclipse.elk.alg.layered.compaction.oned.algs.IConstraintCalculationAlgorithm;
import org.eclipse.elk.alg.layered.compaction.oned.algs.ScanlineConstraintCalculator;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.alg.layered.properties.Spacings;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * This processor applies additional compaction to an already routed graph and can be executed after
 * {@link de.cau.cs.kieler.klay.layered.p5edges.OrthogonalEdgeRouter OrthogonalEdgeRouter}.
 * Therefore nodes and vertical segments of edges are repositioned in the specified direction where
 * the position is minimal considering the desired spacing between elements.
 * 
 * <p>
 *  Since the locking functionality in {@link CLNode} and {@link CLEdge} relies on the direction of
 *  incoming and outgoing edges, this processor is required to be executed before the
 *  {@link de.cau.cs.kieler.klay.layered.intermediate.ReversedEdgeRestorer ReversedEdgeRestorer}.
 * </p>
 * 
 * <dl>
 *  <dt>Precondition:</dt>
 *   <dd>The edges are routed orthogonally</dd>
 *  <dt>Postcondition:</dt>
 *   <dd>Nodes and edges are positioned compact without colliding.</dd>
 *  <dt>Slots:</dt>
 *   <dd>After phase 5.</dd>
 *  <dt>Same-slot dependencies:</dt>
 *   <dd>After {@link de.cau.cs.kieler.klay.layered.intermediate.LabelDummyRemover LabelDummyRemover}
 *   </dd>
 *   <dd>Before {@link de.cau.cs.kieler.klay.layered.intermediate.ReversedEdgeRestorer
 *       ReversedEdgeRestorer}</dd>
 * </dl>
 */
public class HorizontalGraphCompactor implements ILayoutProcessor {

    /**
     * Compaction algorithm based on the network simplex algorithm presented by Gansner et al. in
     * the context of layering.
     * 
     * @see de.cau.cs.kieler.klay.layered.networksimplex.NetworkSimplex
     */
    public static final ICompactionAlgorithm NETWORK_SIMPLEX_COMPACTION =
            new NetworkSimplexCompaction();
    
    private static final IConstraintCalculationAlgorithm EDGE_AWARE_SCANLINE_CONSTRAINTS =
            new EdgeAwareScanlineConstraintCalculation();
    
    private LGraph lGraph;
    
    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor progressMonitor) {

        GraphCompactionStrategy strategy = layeredGraph.getProperty(LayeredOptions.COMPACTION_POST_COMPACTION_STRATEGY);
        if (strategy == GraphCompactionStrategy.NONE) {
            return;
        }
        progressMonitor.begin("Horizontal Compaction", 1);
        
        this.lGraph = layeredGraph;
        
        // the layered graph is transformed into a CGraph that is passed to OneDimensionalCompactor
        LGraphToCGraphTransformer transformer = new LGraphToCGraphTransformer();
        OneDimensionalCompactor odc =
                new OneDimensionalCompactor(transformer.transform(layeredGraph));
        
        odc.setSpacingsHandler(specialSpacingsHandler);
        
        // ---
        // select constraint algorithm
        // - 
        switch (layeredGraph.getProperty(LayeredOptions.COMPACTION_POST_COMPACTION_CONSTRAINTS)) {
            case SCANLINE:
                odc.setConstraintAlgorithm(EDGE_AWARE_SCANLINE_CONSTRAINTS);
                break;
            default:
                odc.setConstraintAlgorithm(OneDimensionalCompactor.QUADRATIC_CONSTRAINTS);
        }

        // ---
        // select compaction strategy
        // - 
        switch (strategy) {
        case LEFT:
            odc.compact();
            break;
            
        case RIGHT:
            odc.changeDirection(Direction.RIGHT)
               .compact();
            break;
            
        case LEFT_RIGHT_CONSTRAINT_LOCKING:
            // the default locking strategy locks CNodes if they are not constrained
            odc.compact()
               .changeDirection(Direction.RIGHT)
               .applyLockingStrategy()
               .compact();
            break;
            
        case LEFT_RIGHT_CONNECTION_LOCKING:
            // compacting left, locking all CNodes that have fewer connections to the right,
            // then compacting right to shorten unnecessary long edges
            odc.compact()
               .changeDirection(Direction.RIGHT)
               .setLockingStrategy((pair) -> !pair.getFirst().lock.get(pair.getSecond()))
               .applyLockingStrategy()
               .compact();
            break;
         
        case EDGE_LENGTH:
            
            odc.setCompactionAlgorithm(NETWORK_SIMPLEX_COMPACTION)
               .compact();
            break;

        default:
            // nobody should get here
            break;
        }
        
        // since changeDirection may transform hitboxes, the final direction has to be LEFT again
        odc.finish();
        
        // applying the compacted positions to the LGraph and updating its size and offset
        transformer.applyLayout();
        
        progressMonitor.done();
    }
    
    /**
     * An extended scanline procedure that is able to properly handle different spacing values
     * between nodes and edges.
     */
    private static final class EdgeAwareScanlineConstraintCalculation extends
            ScanlineConstraintCalculator {

        private final Function<CNode, Double> defaultSpacingFun = n -> compactor.direction
                .isHorizontal() ? n.getVerticalSpacing() : n.getHorizontalSpacing();

        public void calculateConstraints(final OneDimensionalCompactor theCompactor) {

            this.compactor = theCompactor;

            // constraints between vertical segments
            sweep(n -> n instanceof CLEdge, defaultSpacingFun);
            // constraints between regular nodes
            sweep(n -> n instanceof CLNode, defaultSpacingFun);

            // find the minimum spacing
            double minSpacing = Double.POSITIVE_INFINITY;
            for (CNode n : compactor.cGraph.cNodes) {
                // ignore external ports since their spacing is internally set to 0
                if (n instanceof CLNode
                        && ((CLNode) n).getlNode().getType() == NodeType.EXTERNAL_PORT) {
                    continue;
                }
                minSpacing = Math.min(minSpacing, defaultSpacingFun.apply(n));
            }
            // solitary external edges may exist ... 
            if (minSpacing == Double.POSITIVE_INFINITY) {
                minSpacing = 0;
            }
            final double finalMinSpacing = minSpacing;
            // constraints between nodes and vertical segments
            sweep(n -> true, n -> finalMinSpacing);
        }
    }
    
    /**
     * An implementation of a {@link ISpacingsHandler} that is able to cope with the special
     * requirements of {@link LGraph}s. For instance, there are special cases for the spacing
     * between {@link CLEdge}s as opposed to {@link CLNode}s.
     */
    private final ISpacingsHandler<CNode> specialSpacingsHandler = new ISpacingsHandler<CNode>() {
        
        /**
         * {@inheritDoc}
         */
        @Override
        public double getHorizontalSpacing(final CNode cNode1, final CNode cNode2) {
            
            // joining north/south segments that belong to the same edge 
            // by setting their spacing to 0
            if (isVerticalSegmentsOfSameEdge(cNode1, cNode2)) {
                return 0;
            }
            
            // at this point we know that the passed CNodes are either a CLNode or a CLEdge
            
            LNode node1 = null;
            if (cNode1 instanceof CLNode) {
                node1 = ((CLNode) cNode1).getlNode();
            }
            LNode node2 = null;
            if (cNode2 instanceof CLNode) {
                node2 = ((CLNode) cNode2).getlNode();
            } 

            // if either of the two involved nodes represents an external port,
            //  it's ok to move the port as close as possible since it will be 
            //  positioned correctly by another processor later on
            if ((node1 != null && node1.getType() == NodeType.EXTERNAL_PORT)
                    || (node2 != null && node2.getType() == NodeType.EXTERNAL_PORT)) {
                return 0;
            }
            
            // default behavior, query the Spacings object
            Spacings spacings = lGraph.getProperty(InternalProperties.SPACINGS);
            
            // either get the spacing for the node's type or if there is no 
            //  corresponding element in the original graph, we use the 
            //  long edge dummy spacing.
            return spacings.getHorizontalSpacing(
                    node1 != null ? node1.getType() : NodeType.LONG_EDGE, 
                    node2 != null ? node2.getType() : NodeType.LONG_EDGE);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public double getVerticalSpacing(final CNode cNode1, final CNode cNode2) {

            // joining north/south segments that belong to the same edge 
            // by setting their vertical spacing to 1, 
            // i.e. they overlap in the y dimension which results in a constraint
            if (isVerticalSegmentsOfSameEdge(cNode1, cNode2)) {
                return 1;
            }

            return Math.min(cNode1.getVerticalSpacing(), cNode2.getVerticalSpacing());
        }
        
        /**
         * @return true if both passed nodes originate from the same (set) of
         *         {@link de.cau.cs.kieler.klay.layered.graph.LEdge LEdge}.
         */
        private boolean isVerticalSegmentsOfSameEdge(final CNode cNode1, final CNode cNode2) {
            return 
                    // if we only want north/south segments we could use the following
                    // cNode1.parentNode != null && cNode2.parentNode != null
                    (cNode1 instanceof CLEdge && cNode2 instanceof CLEdge)
                    // this might seem quite expensive but in most cases the sets 
                    // contain only one element
                    && !Sets.intersection(((CLEdge) cNode1).originalLEdges,
                            ((CLEdge) cNode2).originalLEdges).isEmpty();
        }
    };

}
