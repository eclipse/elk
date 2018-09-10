/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.p3cycles;

import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Heuristic implementation of cycle breaking. Breaks the cycles in the layered graph of the layout
 * context. The cycle breakers of the Layered algorithm break cycles by reversing edges. That's not
 * what we want here. Instead, we break cycles by splitting one of the two end points of an edge that
 * would otherwise be reversed. While a node usually represents both the start and end point of a
 * message, split nodes only represent one of the end points each. With that, the corresponding message
 * is not drawn horizontally anymore.
 */
public final class SCycleBreaker implements ILayoutPhase<SequencePhases, LayoutContext> {
    
    /** A node with this ID was not visited yet. */
    private static final int NOT_VISITED = 0;
    /** A node with this ID was already visited, but not as part of the current path. */
    private static final int VISITED_OTHER_PATH = 1;
    /** A node with this ID was already visited on the current path. */
    private static final int VISITED_CURRENT_PATH = 2;
    
    /** The list of nodes that have to be split. */
    private Set<LNode> split;
    /** The list of nodes that constitute our current DFS path. */
    private List<LNode> path;
    /** Array indicating which nodes have already been visited. Indexed by node ID. */
    private boolean[] visited;


    @Override
    public LayoutProcessorConfiguration<SequencePhases, LayoutContext> getLayoutProcessorConfiguration(
            final LayoutContext graph) {
        
        return LayoutProcessorConfiguration.create();
    }

    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Cycle Breaking", 1);
        
        // Initialize data
        split = Sets.newHashSet();
        path = Lists.newArrayListWithCapacity(context.lgraph.getLayerlessNodes().size());
        visited = new boolean[context.lgraph.getLayerlessNodes().size()];
        
        // Initialize node IDs
        int nextId = 0;
        for (LNode node : context.lgraph.getLayerlessNodes()) {
            node.id = nextId++;
        }
        
        // DFS run through the graph to find the set of nodes that will have to be split
        for (LNode node : context.lgraph.getLayerlessNodes()) {
            if (!visited[node.id]) {
                dfs(node);
            }
        }

        // Split nodes
        for (LNode node : split) {
            splitNode(context.lgraph, node);
        }
        
        // Release memory
        split = null;
        path = null;
        visited = null;

        progressMonitor.done();
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // DFS
    
    private void dfs(final LNode node) {
        if (visited[node.id]) {
            // We may have found a cycle. Track back along the current path until we find this node again and pick out
            // the node that represents the message with the uppermost y coordinate
            LNode uppermostNode = findUppermostNodeIfCycle(node);
            if (uppermostNode != null) {
                split.add(uppermostNode);
            }
            
        } else {
            visited[node.id] = true;
            path.add(node);
            
            // Start DFS on all outgoing edges
            for (LEdge edge : node.getOutgoingEdges()) {
                dfs(edge.getTarget().getNode());
            }
            
            assert path.get(path.size() - 1) == node;
            path.remove(path.size() - 1);
        }
    }
    
    /**
     * If the current DFS path constitutes a cycle that ends in the given cyclic node, we return the uppermost node
     * in the cycle. If there is no cycle, we return {@code null}.
     */
    private LNode findUppermostNodeIfCycle(final LNode cyclicNode) {
        LNode uppermostNode = null;
        double uppermostPos = Double.MAX_VALUE;
        
        int index = path.size() - 1;
        while (index >= 0) {
            LNode currentNode = path.get(index);
            
            // Check if the current node on the path represents a message (only those can be uppermost nodes)
            Object currentOrigin = currentNode.getProperty(InternalSequenceProperties.ORIGIN);
            if (currentOrigin instanceof SMessage) {
                // Find the current node's y position
                SMessage message = (SMessage) currentOrigin;
                double currentPos = Math.min(message.getSourceYPos(), message.getTargetYPos());
                
                if (uppermostNode == null || currentPos < uppermostPos) {
                    uppermostNode = currentNode;
                    uppermostPos = currentPos;
                }
            }
            
            if (currentNode == cyclicNode) {
                // We have found a cycle and know what the uppermost node is
                return uppermostNode;
            }
            
            index--;
        }
        
        // If we haven't returned during the loop, there was no cycle
        return null;
    }

    /**
     * Process a depth first search starting with the given node and check for cycles.
     * 
     * @param node
     *            the node to start with
     */
    private void _dfs(final LNode node) {
        if (node.id == VISITED_CURRENT_PATH) {
            // This node was already visited in current path
            // Find uppermost LNode in current chain and add it to split
            _addUppermostNode(node);
        } else {
            // This node has not been visited in current path
            node.id = VISITED_CURRENT_PATH;
            path.add(node);

            // Process successors
            for (LEdge edge : node.getOutgoingEdges()) {
                _dfs(edge.getTarget().getNode());
            }
            
            // Mark as visited in previous path
            node.id = VISITED_OTHER_PATH;
            path.remove(path.size() - 1);
        }
    }

    /**
     * Find uppermost LNode in the current cyclic chain and add it to the set of LNodes to be split.
     * 
     * @param foundNode
     *            the uppermost node in the current chain
     */
    private void _addUppermostNode(final LNode foundNode) {
        LNode uppermost = foundNode;
        double uppermostPos = Double.MAX_VALUE;
        int foundIndex = path.indexOf(foundNode);
        
        for (int i = foundIndex; i < path.size(); i++) {
            LNode node = path.get(i);
            SMessage message = (SMessage) node.getProperty(InternalSequenceProperties.ORIGIN);
            ElkEdge edge = (ElkEdge) message.getProperty(InternalSequenceProperties.ORIGIN);
            
            // Compare only sourcePositions since messages can only lead downwards or horizontal
            double sourceYPos = ElkGraphUtil.firstEdgeSection(edge, false, false).getStartY();
            if (sourceYPos < uppermostPos) {
                uppermostPos = sourceYPos;
                uppermost = node;
            }
        }
        
        split.add(uppermost);
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Node Splitting

    /**
     * Split the given node into two nodes for each of the corresponding lifelines. Rearrange edges
     * in order to have only edges showing the order at one lifeline.
     * 
     * @param node
     *            the node to be split
     */
    private void splitNode(final LGraph lgraph, final LNode node) {
        // Create new node in the layered graph
        LNode newNode = new LNode(lgraph);
        lgraph.getLayerlessNodes().add(newNode);

        SMessage message = (SMessage) node.getProperty(InternalSequenceProperties.ORIGIN);
        SLifeline sourceLL = message.getSource();
        SLifeline targetLL = message.getTarget();
        
        for (LEdge edge : node.getConnectedEdges()) {
            SLifeline belongsTo = edge.getProperty(InternalSequenceProperties.BELONGS_TO_LIFELINE);
            // If edge belongs to targetLifeline, rebase it to newNode. Otherwise, leave things as they are
            if (belongsTo == targetLL) {
                if (edge.getSource().getNode() == node) {
                    edge.getSource().setNode(newNode);
                } else if (edge.getTarget().getNode() == node) {
                    edge.getTarget().setNode(newNode);
                }
            }
        }
        
        node.setProperty(InternalSequenceProperties.BELONGS_TO_LIFELINE, sourceLL);
        newNode.setProperty(InternalSequenceProperties.BELONGS_TO_LIFELINE, targetLL);
        newNode.setProperty(InternalSequenceProperties.ORIGIN, message);
    }
    
}
