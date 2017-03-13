/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.wrapping;

import java.util.Collection;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.LongEdgeJoiner;
import org.eclipse.elk.alg.layered.intermediate.ReversedEdgeRestorer;
import org.eclipse.elk.alg.layered.intermediate.wrapping.BreakingPointInserter.BPInfo;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Lists;

/**
 * The processor locates any {@link NodeType#BREAKING_POINT} end dummies 
 * and removes them together with the start dummy and the edge dummies from the graph.
 * Before removing though it transfers the split edge routes to the original (split) edge.
 * Care has to be taken when assembling the new edge route. Different {@link EdgeRouting} styles 
 * require different bend points. 
 * 
 * The {@link LongEdgeJoiner} took care of joining long edges an in-layer dummies,
 * therefore the scenario we are given is:
 * <pre>node --nodeStartEdge--> startMarker --startEndEdge--> endMarker --e--> node</pre>
 * 
 * <dl>
 *   <dt>Preconditions:</dt>
 *     <dd>the graph is fully laid out, in particular, edges have been routed</dd>
 *   <dt>Postconditions:</dt>
 *     <dd>{@link NodeType#BREAKING_POINT}s have been removed.</dd>
 *     <dd>the 'backward' wrapping edges already point into the correct direction, 
 *         the {@link ReversedEdgeRestorer} does <em>not</em> have to revert them.</dd>
 *   <dt>Slots:</dt>
 *     <dd>After phase 5.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>Must run before {@link org.eclipse.elk.alg.layered.intermediate.LongEdgeJoiner LongEdgeJoiner}</dd>
 * </dl>
 * 
 * @see BreakingPointInserter
 * @see BreakingPointProcessor
 */
public class BreakingPointRemover implements ILayoutProcessor<LGraph> {

    private EdgeRouting edgeRouting;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final LGraph graph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Breaking Point Removing", 1);
        
        edgeRouting = graph.getProperty(LayeredOptions.EDGE_ROUTING);
        for (Layer l : graph.getLayers()) {
            for (LNode node : Lists.newArrayList(l.getNodes())) {
                
                if (BPInfo.isEnd(node)) {
                    BPInfo bpi = node.getProperty(InternalProperties.BREAKING_POINT_INFO);
                    if (bpi.next == null) {
                        remove(graph, bpi);
                    }
                }
            }
        }
        
        progressMonitor.done();
    }
    
    private void remove(final LGraph graph, final BPInfo bpi) {
      
        // assemble the new edge route
        KVectorChain newBends = new KVectorChain();
        
//        System.out.println("NS: " + bpi.nodeStartEdge.getBendPoints());
//        System.out.println("EN: " + bpi.originalEdge.getBendPoints());
        
        // depending on the edge routing style we have to act differently
        switch (edgeRouting) {
                
            // for splines it should be enough to add the bpi.start and bpi.end
            // once to the overall list of bend points, since they will be 
            // control points through which the bezier curve runs
            case SPLINES:
                newBends.addAll(bpi.nodeStartEdge.getBendPoints());
                newBends.add(bpi.start.getPosition());
                newBends.addAll(Lists.reverse(bpi.startEndEdge.getBendPoints()));
                newBends.add(bpi.end.getPosition());
                newBends.addAll(bpi.originalEdge.getBendPoints());
                break;
                
            // after executing the polyline router the edge segments are simply joined,
            // note that the positions of bpi.start and bpi.end must be added as bend points 
            case POLYLINE:
                newBends.addAll(bpi.nodeStartEdge.getBendPoints());
                newBends.add(bpi.start.getPosition());
                newBends.addAll(Lists.reverse(bpi.startEndEdge.getBendPoints()));
                newBends.add(bpi.end.getPosition());
                newBends.addAll(bpi.originalEdge.getBendPoints());
                break;

            // for orthogonal edge routing it is guaranteed, that incident edges of a node  
            // enter the node parallel to the x axis. Thus, the positions 
            // of bpi.start and bpi.end can be dropped since they lie on a straight line
            default: // ORTHOGONAL
                newBends.addAll(bpi.nodeStartEdge.getBendPoints());
                newBends.addAll(Lists.reverse(bpi.startEndEdge.getBendPoints()));
                newBends.addAll(bpi.originalEdge.getBendPoints());
        }

        // restore original edge
        bpi.originalEdge.getBendPoints().clear();
        bpi.originalEdge.getBendPoints().addAll(newBends);
        bpi.originalEdge.setSource(bpi.nodeStartEdge.getSource());
        
        // collect any created junction points (order can be arbitrary)
        KVectorChain junctionPointsOne = bpi.nodeStartEdge.getProperty(LayeredOptions.JUNCTION_POINTS);
        KVectorChain junctionPointsTwo = bpi.startEndEdge.getProperty(LayeredOptions.JUNCTION_POINTS);
        KVectorChain junctionPointsThree = bpi.originalEdge.getProperty(LayeredOptions.JUNCTION_POINTS);
        if (junctionPointsOne != null || junctionPointsTwo != null || junctionPointsThree != null) {
            KVectorChain newJunctionPoints = new KVectorChain();
            addNullSafe(newJunctionPoints, junctionPointsThree);
            addNullSafe(newJunctionPoints, junctionPointsTwo);
            addNullSafe(newJunctionPoints, junctionPointsOne);
            bpi.originalEdge.setProperty(LayeredOptions.JUNCTION_POINTS, newJunctionPoints);
        }
        
        // remove all the dummy stuff
        bpi.startEndEdge.setSource(null);
        bpi.startEndEdge.setTarget(null);
        bpi.nodeStartEdge.setSource(null);
        bpi.nodeStartEdge.setTarget(null);
        bpi.end.setLayer(null);
        bpi.start.setLayer(null);
        
        if (bpi.prev != null) {
            remove(graph, bpi.prev);
        }
    }
    
    private <T> boolean addNullSafe(final Collection<T> container, final Collection<T> toAdd) {
        if (container == null || toAdd == null) {
            return false;
        }
        return container.addAll(toAdd);
    }
}
