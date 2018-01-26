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
package org.eclipse.elk.alg.sequence.p4sorting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SGraph;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.MessageType;
import org.eclipse.elk.alg.sequence.options.SequenceArea;
import org.eclipse.elk.alg.sequence.options.SequenceDiagramOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.HashBiMap;

/**
 * Lifeline sorting algorithm that tries to minimize the length of message. The algorithm is
 * inspired by the heuristic solution to the linear arrangement problem as proposed by McAllister in
 * <em>A new heuristic algorithm for the Linear Arrangement problem</em>.
 * 
 * @author grh
 */
public final class ShortMessageLifelineSorter implements ILayoutPhase<SequencePhases, LayoutContext> {

    /** Option that indicates, if the starting node is searched by layering attributes. */
    private boolean layerBased = true;
    /** List of nodes that are already placed by the algorithm. */
    private List<EDLSNode> placedNodes;
    /** The map of lifeline <-> node correspondences. */
    private HashBiMap<SLifeline, EDLSNode> correspondences;
    

    @Override
    public LayoutProcessorConfiguration<SequencePhases, LayoutContext> getLayoutProcessorConfiguration(
            final LayoutContext graph) {
        
        return LayoutProcessorConfiguration.create();
    }
    
    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Equal distribution lifeline sorting", 1);

        // Create the simple graph representation that this algorithm works with.
        createEDLSNodes(context);

        // Initialize list of nodes that are already placed. Nodes will be inserted one by one here.
        placedNodes = new LinkedList<EDLSNode>();

        // Calculate the starting node in a first step
        EDLSNode first;
        if (layerBased) {
            first = layerBasedFirstNode(context.sgraph, context.lgraph);
        } else {
            first = degreeBasedFirstNode(context.sgraph, context.lgraph);
        }
        placedNodes.add(first);
        // Update the TL-values for connected nodes
        first.incrementNeighborsTL();

        // Calculate following nodes one after another
        for (int i = 2; i <= context.sgraph.getLifelines().size(); i++) {
            EDLSNode next = calculateNextNode(context.sgraph);
            placedNodes.add(next);
            // Update the TL-value for connected nodes
            next.incrementNeighborsTL();
        }

        // Get the corresponding lifelines
        int i = 0;
        List<SLifeline> lifelines = new LinkedList<SLifeline>();
        for (EDLSNode node : placedNodes) {
            SLifeline lifeline = correspondences.inverse().get(node);
            lifelines.add(lifeline);
            lifeline.setHorizontalSlot(i);
            i++;
        }

        // Return the list of lifelines in the calculated order
        context.lifelineOrder = lifelines;

        // Free memory
        placedNodes = null;
        correspondences = null;
        
        progressMonitor.done();
    }
    

    /**
     * Set the layer-based option.
     * 
     * @param layerBased
     *            the new value for the layer-based option
     */
    public void setLayerBased(final boolean layerBased) {
        this.layerBased = layerBased;
    }

    /**
     * Create the lightweight graph implementation out of the SGraph.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void createEDLSNodes(final LayoutContext context) {
        // List of lifelines
        List<SLifeline> lifelines = context.sgraph.getLifelines();

        // Initialize correspondences list
        correspondences = HashBiMap.create(lifelines.size());

        // Create nodes
        for (SLifeline lifeline : lifelines) {
            EDLSNode node = new EDLSNode();
            correspondences.put(lifeline, node);
        }

        // If the considerAreas option is set, increase the weight of every edge whose message is
        // contained in an area.
        // Prepare this by filling a map of (message <-> number of its areas) pairs.
        HashMap<SMessage, Integer> areaMessages = new HashMap<SMessage, Integer>();
        if (context.groupAreasWhenSorting) {
            List<SequenceArea> areas = context.sgraph.getProperty(SequenceDiagramOptions.AREAS);
            if (areas != null) {
                for (SequenceArea area : areas) {
                    for (Object messageObject : area.getMessages()) {
                        SMessage message = (SMessage) messageObject;
                        Integer messageEntry = areaMessages.get(message);
                        if (messageEntry == null) {
                            areaMessages.put(message, 1);
                        } else {
                            areaMessages.put(message, areaMessages.get(messageEntry) + 1);
                        }
                    }
                }
            }
        }

        // Insert edges
        for (SLifeline lifeline : lifelines) {
            // Get the corresponding node
            EDLSNode node = correspondences.get(lifeline);

            // Update or create entry in the edges map for every message
            for (SMessage message : lifeline.getOutgoingMessages()) {
                int increaseValue = 1;
                if (context.groupAreasWhenSorting) {
                    if (areaMessages.containsKey(message)) {
                        increaseValue += areaMessages.get(message);
                    }
                }

                SLifeline target = message.getTarget();
                EDLSNode oppositeNode = correspondences.get(target);
                if (oppositeNode != null) {
                    if (node.edges.containsKey(oppositeNode)) {
                        // Increment edge-weight by one to represent this message
                        node.edges.put(oppositeNode, node.edges.get(oppositeNode) + increaseValue);
                        // Increment opposite's map too
                        oppositeNode.edges.put(node, oppositeNode.edges.get(node) + increaseValue);
                    } else {
                        // Insert edge
                        node.edges.put(oppositeNode, increaseValue);
                        // Insert edge at opposite node too
                        oppositeNode.edges.put(node, increaseValue);
                    }
                }

                // Give a "penalty" to the TL-value of the node if there are messages leading to the
                // surrounding interaction. This is necessary, because these messages point to the
                // right border of the diagram and are not considered in the normal algorithm.
                MessageType messageType = message.getProperty(SequenceDiagramOptions.MESSAGE_TYPE);
                if (oppositeNode == null && messageType != MessageType.LOST) {
                    node.setTl(node.getTl() - 1);
                }
            }

            // Give an "advantage" to the TL-value of the node if there are messages coming from the
            // surrounding interaction. This is necessary, because these messages come from the left
            // border of the diagram and are not considered in the normal algorithm.
            for (SMessage message : lifeline.getIncomingMessages()) {
                SLifeline source = message.getSource();
                EDLSNode oppositeNode = correspondences.get(source);
                MessageType messageType = message.getProperty(SequenceDiagramOptions.MESSAGE_TYPE);
                if (oppositeNode == null && messageType != MessageType.FOUND) {
                    node.setTl(node.getTl() + 1);
                }
            }
        }
    }

    /**
     * Calculate the first node to be set. Standard way in the linear arrangement problem is to
     * choose the one with the fewest connected edges.
     * 
     * @param sgraph
     *            the sequence graph
     * @param lgraph
     *            the layered graph
     * @return the node that should be placed in first position
     */
    private EDLSNode degreeBasedFirstNode(final SGraph sgraph, final LGraph lgraph) {
        // Search the node with the lowest weighted degree
        int minDegree = Integer.MAX_VALUE;
        EDLSNode candidate = null;
        for (EDLSNode node : correspondences.values()) {
            if (node.getWeightedDegree() < minDegree) {
                minDegree = node.getWeightedDegree();
                candidate = node;
            }
        }
        candidate.setPlaced(true);
        return candidate;
    }

    /**
     * Calculate the first node to be set. Sequence diagram specific algorithm that chooses the
     * lifeline with the highest outgoing message.
     * 
     * @param sgraph
     *            the sequence graph
     * @param lgraph
     *            the layered graph
     * @return the node that should be placed in first location
     */
    private EDLSNode layerBasedFirstNode(final SGraph sgraph, final LGraph lgraph) {
        List<Layer> layers = lgraph.getLayers();
        Layer firstLayer = layers.get(0);
        List<LNode> nodes = firstLayer.getNodes();
        if (nodes.size() > 1) {
            // If there is more than one message in the first layer, return the one with the lowest
            // weighted node degree
            EDLSNode candidate = null;
            int bestDegree = Integer.MAX_VALUE;
            for (LNode node : nodes) {
                SMessage message = (SMessage) node.getProperty(InternalProperties.ORIGIN);
                SLifeline sourceLifeline = message.getSource();
                EDLSNode cand = correspondences.get(sourceLifeline);
                if (cand.getWeightedDegree() < bestDegree) {
                    bestDegree = cand.getWeightedDegree();
                    candidate = cand;
                }
            }
            candidate.setPlaced(true);
            return candidate;
        } else {
            // If there is just one message in the first layer, return the node corresponding to its
            // source lifeline
            SMessage message = (SMessage) nodes.get(0).getProperty(InternalProperties.ORIGIN);
            SLifeline sourceLifeline = message.getSource();
            EDLSNode candidate = correspondences.get(sourceLifeline);
            if (candidate == null) {
                // Found messages have no source lifeline. Therefore their target is the first lifeline 
                candidate = correspondences.get(message.getTarget());
            }
            candidate.setPlaced(true);
            return candidate;
        }
    }

    /**
     * Calculate the next node to be set.
     * 
     * @param sgraph
     *            the sequence graph
     * @return the node that should be placed in the next position
     */
    private EDLSNode calculateNextNode(final SGraph sgraph) {
        int minSF = Integer.MAX_VALUE;
        EDLSNode candidate = null;
        for (EDLSNode node : correspondences.values()) {
            if (!node.isPlaced()) {
                // Calculate the selection factor as proposed by McAllister
                int sf = node.getWeightedDegree() - 2 * node.getTl();
                // If the selection factor is smaller than the ones of other nodes, this is the next
                // node
                if (sf < minSF) {
                    minSF = sf;
                    candidate = node;
                } else if (sf == minSF) {
                    // If the factor is equal, choose the one that is more connected to aready
                    // placed nodes than the other
                    if (candidate.getTl() < node.getTl()) {
                        candidate = node;
                    }
                }
            }
        }
        candidate.setPlaced(true);
        return candidate;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // EDSLNode Class

    /**
     * A list of these nodes is a very basic implementation of a simple graph. Every node has a map
     * with adjacent nodes and the weight of the connecting edge. Every node corresponds to a
     * lifeline in the sequence diagram. The degree of an edge represents the number of messages
     * connecting the corresponding lifelines.
     */
    private static final class EDLSNode {
        /**
         * A map that contains every adjacent node and the weight of the corresponding edge. Edges
         * are stored in both of their connected nodes.
         */
        private HashMap<EDLSNode, Integer> edges;
        /**
         * For an unplaced node, the weighted sum of edges to nodes that are already placed. At
         * first, this value is 0 since there are no nodes placed so far.
         */
        private int tl = 0;
        /** Indicates, if the node was already placed. */
        private boolean placed = false;

        /** Constructor. */
        public EDLSNode() {
            edges = new HashMap<ShortMessageLifelineSorter.EDLSNode, Integer>();
        }

        /**
         * @return the tl value
         */
        public int getTl() {
            return tl;
        }

        /**
         * @param tl
         *            the new tl value
         */
        public void setTl(final int tl) {
            this.tl = tl;
        }

        /**
         * Increment the sum of the edge-weights for neighbored nodes. This is necessary, if this
         * node was placed in the last step.
         */
        public void incrementNeighborsTL() {
            for (EDLSNode node : edges.keySet()) {
                // If a connected node is node placed yet, its TL-value has to be incremented by the
                // connecting edge's weight
                if (!node.isPlaced()) {
                    node.setTl(node.getTl() + edges.get(node));
                }
            }
        }

        /**
         * @return the placed value
         */
        public boolean isPlaced() {
            return placed;
        }

        /**
         * @param placed
         *            the new placed value
         */
        public void setPlaced(final boolean placed) {
            this.placed = placed;
        }

        /**
         * Get the weighted degree of a node. The weighted degree is the sum of all edge-weights of
         * connected edges.
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
