/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.elk.alg.layered.ILayoutPhase;
import org.eclipse.elk.alg.layered.IntermediateProcessingConfiguration;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.layered.p3order.counting.AbstractInitializer;
import org.eclipse.elk.alg.layered.properties.GraphProperties;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * A crossing minimizer that allows user interaction by respecting previous node positions.
 * 
 * <dl>
 *   <dt>Preconditions:</dt>
 *     <dd>The graph has a proper layering</dd>
 *     <dd>All nodes have at least fixed port sides.</dd>
 *   <dt>Postconditions:</dt>
 *     <dd>The order of nodes in each layer is rearranged according to previous positions give
 *       by the input graph.</dd>
 *     <dd>Long edge dummy nodes have their calculated original position set as property
 *       {@link InternalProperties#ORIGINAL_DUMMY_NODE_POSITION}.</dd>
 * </dl>
 *
 * @author msp
 * @author cds
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating proposed yellow by msp
 */
public final class InteractiveCrossingMinimizer implements ILayoutPhase {

    /** intermediate processing configuration. */
    private static final IntermediateProcessingConfiguration INTERMEDIATE_PROCESSING_CONFIGURATION =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase3(IntermediateProcessorStrategy.LONG_EDGE_SPLITTER)
            .addBeforePhase4(IntermediateProcessorStrategy.IN_LAYER_CONSTRAINT_PROCESSOR)
            .addAfterPhase5(IntermediateProcessorStrategy.LONG_EDGE_JOINER);
    
    /**
     * {@inheritDoc}
     */
    public IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(
            final LGraph graph) {
        IntermediateProcessingConfiguration configuration =
                IntermediateProcessingConfiguration.fromExisting(INTERMEDIATE_PROCESSING_CONFIGURATION);
        
        if (graph.getProperty(InternalProperties.GRAPH_PROPERTIES).contains(
                GraphProperties.NON_FREE_PORTS)) {
            
            configuration.addBeforePhase3(IntermediateProcessorStrategy.PORT_LIST_SORTER);
        }
        
        return configuration;
    }

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Interactive crossing minimization", 1);
        LNode[][] nodeOrder = layeredGraph.toNodeArray();
        AbstractBarycenterPortDistributor portDistributor = new NodeRelativePortDistributor(nodeOrder);
        AbstractInitializer.init(Arrays.asList(portDistributor));
        int portCount = 0;
        int layerIndex = 0;
        for (Layer layer : layeredGraph) {
            // determine a horizontal position for edge bend points comparison
            double horizPos = 0;
            int nodeCount = 0;
            for (LNode node : layer.getNodes()) {
                if (node.getPosition().x > 0) {
                    horizPos += node.getPosition().x + node.getSize().x / 2;
                    nodeCount++;
                }
                for (LPort port : node.getPorts()) {
                    port.id = portCount++;
                }
            }
            horizPos /= nodeCount;
            
            // create an array of vertical node positions
            final double[] pos = new double[layer.getNodes().size()];
            int nextIndex = 0;
            for (LNode node : layer) {
                node.id = nextIndex++;
                pos[node.id] = getPos(node, horizPos);
                
                // if we have a long edge dummy node, save the calculated position in a property
                // to be used by the interactive node placer (for dummy nodes other than long edge
                // dummies, we haven't calculated meaningful positions)
                if (node.getType() == NodeType.LONG_EDGE) {
                    node.setProperty(InternalProperties.ORIGINAL_DUMMY_NODE_POSITION, pos[node.id]);
                }
            }
            
            // sort the nodes using the position array
            Collections.sort(layer.getNodes(), new Comparator<LNode>() {
                public int compare(final LNode node1, final LNode node2) {
                    int compare = Double.compare(pos[node1.id], pos[node2.id]);
                    
                    if (compare == 0) {
                        // The two nodes have the same y coordinate. Check for node successor
                        // constraints
                        List<LNode> node1Successors =
                                node1.getProperty(InternalProperties.IN_LAYER_SUCCESSOR_CONSTRAINTS);
                        List<LNode> node2Successors =
                                node2.getProperty(InternalProperties.IN_LAYER_SUCCESSOR_CONSTRAINTS);
                        
                        if (node1Successors.contains(node2)) {
                            return -1;
                        } else if (node2Successors.contains(node1)) {
                            return 1;
                        }
                    }
                    
                    return compare;
                }
            });
            portDistributor.distributePortsWhileSweeping(nodeOrder, layerIndex, true);
            layerIndex++;
        }
        
        monitor.done();
    }
    
    /**
     * Determine a vertical position for the given node.
     * 
     * @param node a node
     * @param horizPos the horizontal position at which to measure (relevant for edges)
     * @return the vertical position used for sorting
     */
    private double getPos(final LNode node, final double horizPos) {
        switch (node.getType()) {
        case LONG_EDGE:
            LEdge edge = (LEdge) node.getProperty(InternalProperties.ORIGIN);
            // reconstruct the original bend points from the node annotations
            KVectorChain bendpoints = edge.getProperty(InternalProperties.ORIGINAL_BENDPOINTS);
            if (bendpoints == null) {
                bendpoints = new KVectorChain();
            } else if (edge.getProperty(InternalProperties.REVERSED)) {
                bendpoints = KVectorChain.reverse(bendpoints);
            }
            LPort source = node.getProperty(InternalProperties.LONG_EDGE_SOURCE);
            KVector sourcePoint = source.getAbsoluteAnchor();
            if (horizPos <= sourcePoint.x) {
                return sourcePoint.y;
            }
            bendpoints.addFirst(sourcePoint);
            LPort target = node.getProperty(InternalProperties.LONG_EDGE_TARGET);
            KVector targetPoint = target.getAbsoluteAnchor();
            if (targetPoint.x <= horizPos) {
                return targetPoint.y;
            }
            bendpoints.addLast(targetPoint);
            
            Iterator<KVector> pointIter = bendpoints.iterator();
            KVector point1 = pointIter.next();
            KVector point2 = pointIter.next();
            while (point2.x < horizPos && pointIter.hasNext()) {
                point1 = point2;
                point2 = pointIter.next();
            }
            return point1.y + (horizPos - point1.x) / (point2.x - point1.x) * (point2.y - point1.y);
            
        case NORTH_SOUTH_PORT:
            // Get one of the ports the dummy node was created for, and its original node
            LPort originPort = (LPort) node.getPorts().get(0).getProperty(InternalProperties.ORIGIN);
            LNode originNode = originPort.getNode();
            
            switch (originPort.getSide()) {
            case NORTH:
                // Use the position of the node's northern side. This causes northern dummies to
                // have the same y coordinate as the node they were created from if TOP_LEFT is
                // used as the anchor point. We solve this when sorting the nodes by y coordinate
                // by respecting node successor constraints.
                return originNode.getPosition().y;
            
            case SOUTH:
                // Use the position of the node's southern side
                return originNode.getPosition().y + originNode.getSize().y;
            }
            
            break;

        // FIXME What about the other node types?
        }
        
        // the fallback solution is to take the previous position of the node's anchor point
        return node.getInteractiveReferencePoint().y;
    }

}
