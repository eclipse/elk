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
package org.eclipse.elk.alg.layered.p4nodes;

import java.util.Map;

import org.eclipse.elk.alg.layered.ILayoutPhase;
import org.eclipse.elk.alg.layered.IntermediateProcessingConfiguration;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.layered.networksimplex.NEdge;
import org.eclipse.elk.alg.layered.networksimplex.NGraph;
import org.eclipse.elk.alg.layered.networksimplex.NNode;
import org.eclipse.elk.alg.layered.networksimplex.NetworkSimplex;
import org.eclipse.elk.alg.layered.properties.GraphProperties;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.alg.layered.properties.Spacings;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Maps;

/**
 * Implements the node placement strategy as described by Gansner et al. in the following paper. It
 * is based on the idea to convert the problem into an auxiliary graph which is layered using the
 * network simplex algorithm.
 * <ul>
 *   <li>Emden R. Gansner, Eleftherios Koutsofios, Stephen C. North, Kiem-Phong Vo, A technique for
 *       drawing directed graphs. <i>Software Engineering</i> 19(3), pp. 214-230, 1993.</li>
 * </ul>
 * 
 * <h2>Restrictions</h2> 
 * In this approach node positions are represented by layers, and layers are integral. 
 * This means in particular that we cannot support rational positions, neither for nodes nor for ports.
 * The same is true for margin and spacing values.
 * In case such values are present in the graph we resolve them as follows:
 * <ul>
 *   <li>Margin: The top margin is set to Math.ceil(margin.top).</li>
 *   <li>Nodes: The position, height, margin, and spacing of a node are summed and ceiled.</li> 
 *   <li>Ports: A port's position is altered to the closest integer position.</li>
 * </ul>
 */
public class NetworkSimplexPlacer implements ILayoutPhase {
    
    /** additional processor dependencies for graphs with hierarchical ports. */
    private static final IntermediateProcessingConfiguration HIERARCHY_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase5(IntermediateProcessorStrategy.HIERARCHICAL_PORT_POSITION_PROCESSOR);

    /**
     * {@inheritDoc}
     */
    public IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(
            final LGraph graph) {
        
        if (graph.getProperty(InternalProperties.GRAPH_PROPERTIES)
                .contains(GraphProperties.EXTERNAL_PORTS)) {
            return HIERARCHY_PROCESSING_ADDITIONS;
        } else {
            return null;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor progressMonitor) {
     
        final Spacings spacings = layeredGraph.getProperty(InternalProperties.SPACINGS);
        
        final Map<LNode, NNode> nodeMap = Maps.newHashMap();
        int nodeCnt = 0;
        NGraph graph = new NGraph();
        
        // -------------------------------
        // #1 transform nodes & edges
        // -------------------------------
        for (Layer l : layeredGraph) {
            
            LNode prevL = null;
            NNode prev = null;
            for (LNode lNode : l) {
                nodeCnt++;
                
                NNode nNode = NNode.of().create(graph);
                nNode.origin = lNode;
                nodeMap.put(lNode, nNode);
                
                // integral margin
                lNode.getMargin().top = Math.ceil(lNode.getMargin().top);
                
                if (prev != null) {
                    
                    NEdge nEdge = new NEdge();
                    nEdge.weight = 0;
                    nEdge.delta =
                            // ceil the value to assert integrality and minimum spacing 
                            (int) Math.floor(
                                    prevL.getMargin().top
                                    + prevL.getSize().y 
                                    + prevL.getMargin().bottom 
                                    + spacings.getVerticalSpacing((LNode) prev.origin, lNode)
                            );
                    
                    nEdge.source = prev;
                    nEdge.target = nNode;
                    prev.getOutgoingEdges().add(nEdge);
                    nNode.getIncomingEdges().add(nEdge);
                }
                
                prevL = lNode;
                prev = nNode;
            }
        }
        
        // "integerify" port positions
        for (Layer l : layeredGraph) {
            for (LNode lNode : l) {

                for (LPort p : lNode.getPorts()) {
                    double y = p.getPosition().y + p.getAnchor().y;
                    if (y != Math.floor(y)) {
                        double offset = y - Math.round(y);
                        p.getPosition().y -= offset;
                    }
                }
                
            }
        }
        
        // convert the edges
        for (Layer l : layeredGraph) {
            for (LNode lNode : l) {
                
                for (LEdge lEdge : lNode.getOutgoingEdges()) {
                    
                    // no self loops
                    if (lEdge.isSelfLoop()) {
                        continue;
                    }
                    
                    // no inlayer edges
                    if (lEdge.getTarget().getNode().getLayer() == l) {
                        continue;
                    }
              
                    // port offsets (top margin and port position should be integers by now)
                    double sourceY =
                            lEdge.getSource().getNode().getMargin().top
                                    + lEdge.getSource().getPosition().y
                                    + lEdge.getSource().getAnchor().y;
                    double targetY =
                            lEdge.getTarget().getNode().getMargin().top
                                   + lEdge.getTarget().getPosition().y
                                   + lEdge.getTarget().getAnchor().y;

                    double delta = targetY - sourceY;
                    int portOffset = (int) delta;
                    
                    // check that the offset is integer
                    assert delta == Math.floor(delta);

                    // a dummy node
                    NNode dummy = NNode.of().create(graph);
                    
                    // an edge to the source 
                    NEdge leftEdge = new NEdge();
                    leftEdge.origin = lEdge;
                    leftEdge.weight = getEdgeWeight(lEdge);
                    
                    leftEdge.delta = portOffset > 0 ? portOffset : 0;
                    
                    leftEdge.source = dummy;
                    leftEdge.target = nodeMap.get(lEdge.getSource().getNode());
                    leftEdge.source.getOutgoingEdges().add(leftEdge);
                    leftEdge.target.getIncomingEdges().add(leftEdge);
                    
                    // an edge to the target
                    NEdge rightEdge = new NEdge();
                    rightEdge.origin = lEdge;
                    rightEdge.weight = getEdgeWeight(lEdge);
                    
                    rightEdge.delta = portOffset < 0 ? -portOffset : 0;

                    rightEdge.source = dummy;
                    rightEdge.target = nodeMap.get(lEdge.getTarget().getNode());
                    rightEdge.source.getOutgoingEdges().add(rightEdge);
                    rightEdge.target.getIncomingEdges().add(rightEdge);
                }
            }
        }
        
        // --------------------------------
        // #2 execute the network simplex
        // --------------------------------
        int iterLimit = layeredGraph.getProperty(LayeredOptions.THOROUGHNESS) * (int) Math.sqrt(nodeCnt);
        
        NetworkSimplex.forGraph(graph)
            .withIterationLimit(iterLimit)
            .withBalancing(false)
            .execute(progressMonitor.subTask(1));

        // --------------------------------
        // #3 apply positions
        // --------------------------------
        for (NNode n : graph.nodes) {
            if (n.origin != null) {
                LNode lNode = (LNode) n.origin;
                lNode.getPosition().y = n.layer + lNode.getMargin().top;
            }

        }
        
    }
    
    /**
     * @return for the passed edge, the individual edge weight as specified by Gansner et al. The
     *         idea is to use a higher weight for long edge dummies such that long edges are drawn
     *         straight with higher priority. Additionally, we consider the
     *         {@link org.eclipse.elk.alg.layered.properties.PRIORITY PRIORITY} layout option.
     */
    private int getEdgeWeight(final LEdge edge) {
        
        int priority = Math.max(1, edge.getProperty(LayeredOptions.PRIORITY));
        
        int edgeTypeWeight;
        if (edge.getSource().getNode().getType() == NodeType.NORMAL
                && edge.getTarget().getNode().getType() == NodeType.NORMAL) {
            edgeTypeWeight = 1;
        } else if (edge.getSource().getNode().getType() == NodeType.NORMAL
                || edge.getTarget().getNode().getType() == NodeType.NORMAL) {
            edgeTypeWeight = 2;
        } else {
            edgeTypeWeight = 8; // SUPPRESS CHECKSTYLE MagicNumber
        }
        
        return priority * edgeTypeWeight;
    }
}
