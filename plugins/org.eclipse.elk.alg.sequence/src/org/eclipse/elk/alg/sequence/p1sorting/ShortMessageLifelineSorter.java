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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

/**
 * Lifeline sorting algorithm that tries to minimize the length of message. The algorithm is
 * inspired by the heuristic solution to the linear arrangement problem as proposed by McAllister in
 * <em>A new heuristic algorithm for the Linear Arrangement problem</em>.
 */
public final class ShortMessageLifelineSorter implements ILayoutPhase<SequencePhases, LayoutContext> {
    
    /** The number of lifelines in the graph. */
    private int lifelineCount = 0;
    /** The number of messages in the graph. */
    private int messageCount = 0;
    /** Map of lifelines to lifeline nodes in the auxiliary graph. */
    private Map<SLifeline, LifelineNode> lifelineNodeMap;
    

    @Override
    public LayoutProcessorConfiguration<SequencePhases, LayoutContext> getLayoutProcessorConfiguration(
            final LayoutContext graph) {
        
        return LayoutProcessorConfiguration.create();
    }
    
    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Short Message Lifeline Sorting", 1);

        initialize(context);
        
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
        for (LifelineNode lifelineNode : placedNodes) {
            SLifeline lifeline = lifelineNode.lifeline;
            lifelines.add(lifeline);
            lifeline.setHorizontalSlot(slot++);
        }
        
        // Clean up
        lifelineNodeMap = null;
        
        progressMonitor.done();
    }
    
    /**
     * Assigns IDs to the lifelines and messages and counts them in the process. IDs are unique in their respective
     * sets.
     */
    private void initialize(final LayoutContext context) {
        lifelineCount = 0;
        messageCount = 0;
        
        for (SLifeline lifeline : context.sgraph.getLifelines()) {
            lifeline.id = lifelineCount++;
            
            for (SMessage message : lifeline.getOutgoingMessages()) {
                message.id = messageCount++;
            }
        }
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
        
        lifelineNodeMap = new HashMap<>();
        for (SLifeline lifeline : lifelines) {
            LifelineNode lifelineNode = new LifelineNode(lifeline);
            lifelineNodeMap.put(lifeline, lifelineNode);
        }
        
        // If the considerAreas option is set, increase the weight of every edge whose message is contained in an area.
        // Prepare this by filling a map of (message <-> number of its areas) pairs.
        int[] messageWeightIncrement = new int[messageCount];
        if (context.groupAreasWhenSorting) {
            for (SArea area : context.sgraph.getAreas()) {
                for (SMessage message : area.getMessages()) {
                    messageWeightIncrement[message.id]++;
                }
            }
        }

        // Insert edges
        for (LifelineNode node : lifelineNodeMap.values()) {
            // Update or create entry in the edges map for every message
            for (SMessage message : node.lifeline.getOutgoingMessages()) {
                int increaseValue = 1 + messageWeightIncrement[message.id];

                SLifeline target = message.getTarget();
                LifelineNode targetNode = lifelineNodeMap.get(target);
                if (targetNode != null) {
                    node.addConnectionTo(targetNode, increaseValue);
                    targetNode.addConnectionTo(node, increaseValue);
                }

                // Give a "penalty" to the TL-value of the node if there are messages leading to the surrounding
                // interaction. This is necessary, because these messages point to the right border of the diagram and
                // are not considered in the normal algorithm.
                MessageType messageType = message.getProperty(SequenceDiagramOptions.TYPE_MESSAGE);
                if (targetNode == null && messageType != MessageType.LOST) {
                    node.tl--;
                }
            }

            // Give an "advantage" to the TL-value of the node if there are messages coming from the surrounding
            // interaction. This is necessary, because these messages come from the left border of the diagram and are
            // not considered in the normal algorithm.
            for (SMessage message : node.lifeline.getIncomingMessages()) {
                SLifeline source = message.getSource();
                LifelineNode oppositeNode = lifelineNodeMap.get(source);
                MessageType messageType = message.getProperty(SequenceDiagramOptions.TYPE_MESSAGE);
                if (oppositeNode == null && messageType != MessageType.FOUND) {
                    node.tl++;
                }
            }
        }
        
        // Calculate the weighted degrees of all lifeline nodes
        lifelineNodeMap.values().stream()
            .forEach(ln -> ln.calculateWeightedDegree());
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
        return lifelineNodeMap.values().stream()
            .min(ShortMessageLifelineSorter::compareLifelineNodesByWeightedDegree)
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
                    .map(ll -> lifelineNodeMap.get(ll))
                    .min(ShortMessageLifelineSorter::compareLifelineNodesByWeightedDegree)
                    .get();
            
        } else {
            // If there is just one message in the first layer, return the node corresponding to its source lifeline
            SMessage message = (SMessage) candidates.get(0).getProperty(InternalSequenceProperties.ORIGIN);
            SLifeline sourceLifeline = message.getSource();
            
            LifelineNode lifelineNode = lifelineNodeMap.get(sourceLifeline);
            if (lifelineNode == null) {
                // Found messages have no source lifeline. Therefore their target is the first lifeline 
                lifelineNode = lifelineNodeMap.get(message.getTarget());
            }
            
            return lifelineNode;
        }
    }
    
    private static int compareLifelineNodesByWeightedDegree(final LifelineNode node1, final LifelineNode node2) {
        return node1.weightedDegree - node2.weightedDegree;
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
        while (placedNodes.size() < context.sgraph.getLifelines().size()) {
            LifelineNode next = calculateNextNode(context.sgraph);
            placedNodes.add(next);
            next.placed = true;
            
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
        
        for (LifelineNode node : lifelineNodeMap.values()) {
            if (!node.placed) {
                // Calculate the selection factor as proposed by McAllister
                int sf = node.weightedDegree - 2 * node.tl;
                
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
    private final class LifelineNode {
        
        /** The lifeline this node represents. */
        private SLifeline lifeline;
        /** Whether the lifeline has already been placed or not. */
        private boolean placed = false;
        /** The set of this node's neighbors. */
        private Set<LifelineNode> neighbors = new HashSet<>();
        /** Weight of the edges that connect this node to other nodes. */
        private int[] edgeWeights = new int[lifelineCount];
        /** The weighted degree is the sum of all edge-weights of connected edges. */
        private int weightedDegree = 0;
        /**
         * For an unplaced node, the weighted sum of edges to nodes that are already placed. At first, this value is 0
         * since there are no nodes placed so far. The name corresponds to the original paper.
         */
        private int tl = 0;

        /**
         * Creates a new instance that represents the given lifeline.
         */
        public LifelineNode(final SLifeline lifeline) {
            this.lifeline = lifeline;
        }
        
        /**
         * Adds weight to the connection to the other lifeline node.
         */
        public void addConnectionTo(final LifelineNode other, final int weight) {
            neighbors.add(other);
            edgeWeights[other.lifeline.id] += weight;
        }

        /**
         * Increment the sum of the edge-weights for neighbored nodes. This is necessary, if this
         * node was placed in the last step.
         */
        public void incrementNeighborsTL() {
            for (LifelineNode node : neighbors) {
                // If a connected node is node placed yet, its TL-value has to be incremented by the
                // connecting edge's weight
                if (!node.placed) {
                    node.tl = node.tl + edgeWeights[node.lifeline.id];
                }
            }
        }

        /**
         * Get the weighted degree of a node. The weighted degree is the sum of all edge-weights of connected edges.
         */
        public void calculateWeightedDegree() {
            weightedDegree = neighbors.stream()
                .mapToInt(ln -> edgeWeights[ln.lifeline.id])
                .sum();
        }
    }
    
}
