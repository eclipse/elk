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
package org.eclipse.elk.alg.sequence.p1sorting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SArea;
import org.eclipse.elk.alg.sequence.graph.SGraph;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.options.MessageType;
import org.eclipse.elk.alg.sequence.options.SequenceDiagramOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.HashBiMap;

/**
 * Lifeline sorting algorithm that tries to minimize the length of message. The algorithm is
 * inspired by the heuristic solution to the linear arrangement problem as proposed by McAllister in
 * <em>A new heuristic algorithm for the Linear Arrangement problem</em>.
 */
public final class ShortMessageLifelineSorter implements ILayoutPhase<SequencePhases, LayoutContext> {

    /** The map of lifeline <-> node correspondences. */
    private HashBiMap<SLifeline, LifelineNode> lifelineToNodeMap;
    

    @Override
    public LayoutProcessorConfiguration<SequencePhases, LayoutContext> getLayoutProcessorConfiguration(
            final LayoutContext graph) {
        
        return LayoutProcessorConfiguration.create();
    }
    
    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Short Message Lifeline Sorting", 1);

        // Create the simple graph representation that this algorithm works with.
        createAuxiliaryGraph(context);
        
        // Place lifeline nodes
        LifelineNode first = context.lgraph.getProperty(SequenceDiagramOptions.DEGREE_BASED_LEFTMOST_LIFELINE)
                ? degreeBasedFirstNode()
                : layerBasedFirstNode(context.lgraph);
        List<LifelineNode> placedNodes = placeNodes(context, first);

        // Apply the order
        List<SLifeline> lifelines = context.sgraph.getLifelines();
        lifelines.clear();
        
        int slot = 0;
        for (LifelineNode node : placedNodes) {
            SLifeline lifeline = lifelineToNodeMap.inverse().get(node);
            lifelines.add(lifeline);
            lifeline.setHorizontalSlot(slot++);
        }

        // Free memory
        placedNodes = null;
        lifelineToNodeMap = null;
        
        progressMonitor.done();
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Graph Creation

    /**
     * Create the lightweight graph implementation out of the SGraph.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void createAuxiliaryGraph(final LayoutContext context) {
        List<SLifeline> lifelines = context.sgraph.getLifelines();

        // Create a node for each lifeline and remember the mapping
        lifelineToNodeMap = HashBiMap.create(lifelines.size());
        
        for (SLifeline lifeline : lifelines) {
            LifelineNode node = new LifelineNode();
            lifelineToNodeMap.put(lifeline, node);
        }

        // If the considerAreas option is set, increase the weight of every edge whose message is contained in an area.
        // Prepare this by filling a map of (message <-> number of its areas) pairs.
        HashMap<SMessage, Integer> areaMessages = new HashMap<SMessage, Integer>();
        if (context.groupAreasWhenSorting) {
            for (SArea area : context.sgraph.getAreas()) {
                for (SMessage message : area.getMessages()) {
                    Integer messageEntry = areaMessages.get(message);
                    if (messageEntry == null) {
                        areaMessages.put(message, 1);
                    } else {
                        areaMessages.put(message, areaMessages.get(messageEntry) + 1);
                    }
                }
            }
        }

        // Insert edges
        for (SLifeline lifeline : lifelines) {
            // Get the corresponding node
            LifelineNode node = lifelineToNodeMap.get(lifeline);

            // Update or create entry in the edges map for every message
            for (SMessage message : lifeline.getOutgoingMessages()) {
                int increaseValue = 1;
                
                // Apply additional weight if present
                if (areaMessages.containsKey(message)) {
                    increaseValue += areaMessages.get(message);
                }

                SLifeline target = message.getTarget();
                LifelineNode oppositeNode = lifelineToNodeMap.get(target);
                if (oppositeNode != null) {
                    if (node.edges.containsKey(oppositeNode)) {
                        // Increment edge-weight to represent this message (on both incident lifelines)
                        node.edges.put(oppositeNode, node.edges.get(oppositeNode) + increaseValue);
                        oppositeNode.edges.put(node, oppositeNode.edges.get(node) + increaseValue);
                        
                    } else {
                        // Insert edge (at both incident lifelines)
                        node.edges.put(oppositeNode, increaseValue);
                        oppositeNode.edges.put(node, increaseValue);
                    }
                }

                // Give a "penalty" to the TL-value of the node if there are messages leading to the
                // surrounding interaction. This is necessary, because these messages point to the
                // right border of the diagram and are not considered in the normal algorithm.
                MessageType messageType = message.getProperty(SequenceDiagramOptions.TYPE_MESSAGE);
                if (oppositeNode == null && messageType != MessageType.LOST) {
                    node.tl--;
                }
            }

            // Give an "advantage" to the TL-value of the node if there are messages coming from the
            // surrounding interaction. This is necessary, because these messages come from the left
            // border of the diagram and are not considered in the normal algorithm.
            for (SMessage message : lifeline.getIncomingMessages()) {
                SLifeline source = message.getSource();
                LifelineNode oppositeNode = lifelineToNodeMap.get(source);
                MessageType messageType = message.getProperty(SequenceDiagramOptions.TYPE_MESSAGE);
                if (oppositeNode == null && messageType != MessageType.FOUND) {
                    node.tl++;
                }
            }
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Start Node Selection

    /**
     * Calculate the first node to be set. Standard way in the linear arrangement problem is to
     * choose the one with the fewest connected edges.
     * 
     * @return the node that should be placed in first position
     */
    private LifelineNode degreeBasedFirstNode() {
        return lifelineToNodeMap.values().stream()
            .min((node1, node2) -> node1.getWeightedDegree() - node2.getWeightedDegree())
            .get();
    }

    /**
     * Calculate the first node to be set. Sequence diagram specific algorithm that chooses the lifeline with the
     * highest outgoing message.
     * 
     * @param lgraph
     *            the layered graph
     * @return the node that should be placed in first location
     */
    private LifelineNode layerBasedFirstNode(final LGraph lgraph) {
        List<LNode> candidates = lgraph.getLayerlessNodes().stream()
                .filter(node -> !node.getIncomingEdges().iterator().hasNext())
                .collect(Collectors.toList());
        
        if (candidates.size() > 1) {
            return candidates.stream()
                    .map(node -> ((SMessage) node.getProperty(InternalSequenceProperties.ORIGIN)).getSource())
                    .map(ll -> lifelineToNodeMap.get(ll))
                    .min((node1, node2) -> node1.getWeightedDegree() - node2.getWeightedDegree())
                    .get();
            
        } else {
            // If there is just one message in the first layer, return the node corresponding to its source lifeline
            SMessage message = (SMessage) candidates.get(0).getProperty(InternalSequenceProperties.ORIGIN);
            SLifeline sourceLifeline = message.getSource();
            
            LifelineNode lifelineNode = lifelineToNodeMap.get(sourceLifeline);
            if (lifelineNode == null) {
                // Found messages have no source lifeline. Therefore their target is the first lifeline 
                lifelineNode = lifelineToNodeMap.get(message.getTarget());
            }
            
            return lifelineNode;
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Placement Algorithm
    
    /**
     * Places all lifelines, starting with the first.
     */
    private List<LifelineNode> placeNodes(final LayoutContext context, final LifelineNode firstNode) {
        List<LifelineNode> placedNodes = new ArrayList<>();
        
        firstNode.placed = true;
        placedNodes.add(firstNode);
        
        // Update the TL-values for connected nodes
        firstNode.incrementNeighborsTL();

        // Calculate following nodes one by one
        for (int i = 2; i <= context.sgraph.getLifelines().size(); i++) {
            LifelineNode next = calculateNextNode(context.sgraph);
            
            next.placed = true;
            placedNodes.add(next);
            
            // Update the TL-value for connected nodes
            next.incrementNeighborsTL();
        }
        
        return placedNodes;
    }

    /**
     * Calculate the next node to be set.
     * 
     * @param sgraph
     *            the sequence graph
     * @return the node that should be placed in the next position
     */
    private LifelineNode calculateNextNode(final SGraph sgraph) {
        int minSF = Integer.MAX_VALUE;
        LifelineNode candidate = null;
        
        for (LifelineNode node : lifelineToNodeMap.values()) {
            if (!node.placed) {
                // Calculate the selection factor as proposed by McAllister
                int sf = node.getWeightedDegree() - 2 * node.tl;
                
                // If the selection factor is smaller than the ones of other nodes, this is the next node
                if (sf < minSF) {
                    minSF = sf;
                    candidate = node;
                    
                } else if (sf == minSF) {
                    // Factor is equal. Choose the node that is more connected to aready placed nodes
                    if (candidate.tl < node.tl) {
                        candidate = node;
                    }
                }
            }
        }
        
        return candidate;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // LifelineNode Class

    /**
     * A list of these nodes is a very basic implementation of a simple graph. Every node has a map with adjacent nodes
     * and the weight of the connecting edge. Every node corresponds to a lifeline in the sequence diagram. The degree
     * of an edge represents the number of messages connecting the corresponding lifelines.
     */
    private static final class LifelineNode {
        
        /**
         * A map that contains every adjacent lifeline node and the number of messages that connect the two.
         */
        private HashMap<LifelineNode, Integer> edges;
        /**
         * For an unplaced node, the weighted sum of edges to nodes that are already placed. At first, this value is 0
         * since there are no nodes placed so far. The name corresponds to the original paper.
         */
        private int tl = 0;
        /** Whether the lifeline has already been placed or not. */
        private boolean placed = false;

        /** Constructor. */
        public LifelineNode() {
            edges = new HashMap<ShortMessageLifelineSorter.LifelineNode, Integer>();
        }

        /**
         * Increment the sum of the edge-weights for neighbored nodes. This is necessary, if this
         * node was placed in the last step.
         */
        public void incrementNeighborsTL() {
            for (LifelineNode node : edges.keySet()) {
                // If a connected node is node placed yet, its TL-value has to be incremented by the
                // connecting edge's weight
                if (!node.placed) {
                    node.tl = node.tl + edges.get(node);
                }
            }
        }

        /**
         * Get the weighted degree of a node. The weighted degree is the sum of all edge-weights of connected edges.
         * 
         * @return the weighted degree for the node
         */
        public int getWeightedDegree() {
            int ret = 0;
            for (int value : edges.values()) {
                ret += value;
            }
            return ret;
        }
    }
    
}
