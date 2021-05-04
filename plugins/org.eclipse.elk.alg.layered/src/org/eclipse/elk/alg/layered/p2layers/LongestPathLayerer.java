/*******************************************************************************
 * Copyright (c) 2010, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p2layers;

import java.util.List;
import java.util.Stack;

import org.eclipse.elk.alg.layered.LayeredPhases;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * The most basic layering algorithm, which assign layers according to the
 * longest path to a sink.
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>the graph has no cycles</dd>
 *   <dt>Postcondition:</dt><dd>all nodes have been assigned a layer such that
 *     edges connect only nodes from layers with increasing indices</dd>
 * </dl>
 */
public final class LongestPathLayerer implements ILayoutPhase<LayeredPhases, LGraph> {
    
    /** intermediate processing configuration. */
    private static final LayoutProcessorConfiguration<LayeredPhases, LGraph> BASELINE_PROCESSING_CONFIGURATION =
        LayoutProcessorConfiguration.<LayeredPhases, LGraph>create()
            .addBefore(LayeredPhases.P1_CYCLE_BREAKING,
                    IntermediateProcessorStrategy.EDGE_AND_LAYER_CONSTRAINT_EDGE_REVERSER)
            .addBefore(LayeredPhases.P2_LAYERING, IntermediateProcessorStrategy.LAYER_CONSTRAINT_PREPROCESSOR)
            .addBefore(LayeredPhases.P3_NODE_ORDERING, IntermediateProcessorStrategy.LAYER_CONSTRAINT_POSTPROCESSOR);
    
    /** the layered graph to which layers are added. */
    private LGraph layeredGraph;
    /** map of nodes to their height in the layering. */
    private int[] nodeHeights;
    
    @Override
    public LayoutProcessorConfiguration<LayeredPhases, LGraph> getLayoutProcessorConfiguration(final LGraph graph) {
        return BASELINE_PROCESSING_CONFIGURATION;
    }
    
    @Override
    public void process(final LGraph thelayeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Longest path layering", 1);
        
        layeredGraph = thelayeredGraph;
        List<LNode> nodes = layeredGraph.getLayerlessNodes();
        
        // initialize values required for the computation
        nodeHeights = new int[nodes.size()];
        int index = 0;
        for (LNode node : nodes) {
            // the node id is used as index for the nodeHeights array
            node.id = index;
            // -1 will indicate 'non-visited', 
            // 0 will indicate 'visited-once during dfs descent'
            // anything > 0 indicates the position within the layering
            nodeHeights[index] = -1;
            index++;
        }
        
        // process all nodes
        for (LNode node : nodes) {
            visit(node);
        }
        
        // empty the list of unlayered nodes
        nodes.clear();
        
        // release the created resources
        this.layeredGraph = null;
        this.nodeHeights = null;
        
        monitor.done();
    }
    

    /**
     * Visit a node: if not already visited, find the longest path to a sink.
     * 
     * @param node node to visit
     * @return height of the given node in the layered graph
     */
    private void visit(final LNode start) {
        if (nodeHeights[start.id] > 0) {
            // the node was already visited (the case height == 0 should never occur)
            return;
        } else {
            Stack<LNode> stack = new Stack<>();
            stack.push(start);

            while (!stack.isEmpty()) {
                final LNode node = stack.peek();

                if (nodeHeights[node.id] == -1) {
                    // descent
                    nodeHeights[node.id] = 0;
                    boolean hasSuccessors = false;
                    for (LPort port : node.getPorts()) {
                        for (LEdge edge : port.getOutgoingEdges()) {
                            hasSuccessors = true;
                            LNode target = edge.getTarget().getNode();
                            // only push successors that haven't been positioned so far
                            if (nodeHeights[target.id] == -1) {
                                stack.push(target);
                            }
                        }
                    }
                    // position the leaves
                    if (!hasSuccessors) {
                        putNode(node, 1);
                        stack.pop();
                    }
                } else if (nodeHeights[node.id] == 0) {
                    // ascent
                    int maxHeight = 1;
                    for (LPort port : node.getPorts()) {
                        for (LEdge edge : port.getOutgoingEdges()) {
                            LNode target = edge.getTarget().getNode();
                            assert nodeHeights[target.id] > 0;
                            maxHeight = Math.max(maxHeight, nodeHeights[target.id]);
                        }
                    }
                    putNode(node, maxHeight + 1);
                    stack.pop();
                } else {
                    // node has already been placed in the meantime
                    stack.pop();
                }
            }
        }
    }
    
    /**
     * Puts the given node into the layered graph, adding new layers as necessary.
     * 
     * @param node a node
     * @param height height of the layer where the node shall be added
     *          (height = number of layers - layer index)
     */
    private void putNode(final LNode node, final int height) {
        List<Layer> layers = layeredGraph.getLayers();
        
        // add layers so as to guarantee that number of layers >= height
        for (int i = layers.size(); i < height; i++) {
            layers.add(0, new Layer(layeredGraph));
        }
        
        // layer index = number of layers - height
        node.setLayer(layers.get(layers.size() - height));
        nodeHeights[node.id] = height;
    }

}
 