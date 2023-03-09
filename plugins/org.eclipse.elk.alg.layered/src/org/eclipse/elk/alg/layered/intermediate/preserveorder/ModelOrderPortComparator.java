/*******************************************************************************
 * Copyright (c) 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.preserveorder;

import java.util.Comparator;
import java.util.Map;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.OrderingStrategy;

/**
 * Orders {@link LPort}s in the same layer by their edges {@link InternalProperties#MODEL_ORDER}
 * or the order of the nodes they connect to in the previous layer.
 * This takes into account that ports that connect to the same (long edge) target should be ordered next to each other.
 * Outgoing ports are ordered before incoming ports.
 * Incoming ports choose a position that does not create conflicts with the previous layer.
 */
public class ModelOrderPortComparator implements Comparator<LPort> {

    /**
     * The model order associated to a target node of this node.
     */
    private final Map<LNode, Integer> targetNodeModelOrder;
    
    /**
     * Whether ports should have a model order.
     */
    private boolean portModelOrder;

    /**
     * The previous layer.
     */
    private LNode[] previousLayer;
    
    private OrderingStrategy strategy;

    /**
     * Creates a comparator to compare {@link LPort}s in the same layer.
     * 
     * @param previousLayer The previous layer
     * @param targetNodeModelOrder The minimal model order connecting to a target node.
     */
    public ModelOrderPortComparator(final Layer previousLayer, final OrderingStrategy strategy,
            final Map<LNode, Integer> targetNodeModelOrder, final boolean portModelOrder) {
        this.previousLayer = new LNode[previousLayer.getNodes().size()];
        this.strategy = strategy;
        previousLayer.getNodes().toArray(this.previousLayer);
        this.targetNodeModelOrder = targetNodeModelOrder;
        this.portModelOrder = portModelOrder;
    }

    /**
     * Creates a comparator to compare {@link LPort}s in the same layer.
     * 
     * @param previousLayer The previous layer
     * @param targetNodeModelOrder The minimal model order connecting to a target node.
     */
    public ModelOrderPortComparator(final LNode[] previousLayer, final OrderingStrategy strategy,
            final Map<LNode, Integer> targetNodeModelOrder, final boolean portModelOrder) {
        this.previousLayer = previousLayer;
        this.strategy = strategy;
        this.targetNodeModelOrder = targetNodeModelOrder;
        this.portModelOrder = portModelOrder;
    }

    @Override
    public int compare(final LPort p1, final LPort p2) {
        // Sort incoming edges by sorting their ports by the order of the nodes they connect to.
        if (!p1.getIncomingEdges().isEmpty() && !p2.getIncomingEdges().isEmpty()) {
            // If port order is used instead of edge order, consult it to make decisions.
            // If not both ports have a model order fall back to the edge order.
            if (portModelOrder) {
                int result =  checkPortModelOrder(p2, p1);
                if (result != 0) {
                    return result;
                }
            }
            LNode p1Node = p1.getIncomingEdges().get(0).getSource().getNode();
            LNode p2Node = p2.getIncomingEdges().get(0).getSource().getNode();
            if (p1Node.equals(p2Node)) {
                // In this case both incoming edges must have a model order set. Check it.
                return Integer.compare(p1.getIncomingEdges().get(0).getProperty(InternalProperties.MODEL_ORDER),
                        p2.getIncomingEdges().get(0).getProperty(InternalProperties.MODEL_ORDER));
            }
            for (LNode previousNode : previousLayer) {
                if (previousNode.equals(p1Node)) {
                    return 1;
                } else if (previousNode.equals(p2Node)) {
                    return -1;
                }
            }
        }

        // Sort outgoing edges by sorting their ports based on the model order of their edges.
        if (!p1.getOutgoingEdges().isEmpty() && !p2.getOutgoingEdges().isEmpty()) {
            LNode p1TargetNode = p1.getProperty(InternalProperties.LONG_EDGE_TARGET_NODE);
            LNode p2TargetNode = p2.getProperty(InternalProperties.LONG_EDGE_TARGET_NODE);

            // If node order is preferred and both edges have a target node use the model order of the long edge target.
            if (this.strategy == OrderingStrategy.PREFER_NODES && p1TargetNode != null && p2TargetNode != null
                    && p1TargetNode.hasProperty(InternalProperties.MODEL_ORDER)
                    && p2TargetNode.hasProperty(InternalProperties.MODEL_ORDER)) {
                return Integer.compare(p1TargetNode.getProperty(InternalProperties.MODEL_ORDER),
                        p2TargetNode.getProperty(InternalProperties.MODEL_ORDER));
            }

            // If port order is used instead of edge order, consult it to make decisions.
            // If not both ports have a model order fall back to the edge order.
            if (portModelOrder) {
                int result =  checkPortModelOrder(p1, p2);
                if (result != 0) {
                    return result;
                }
            }
            
            int p1Order = 0;
            int p2Order = 0;
            if (p1.getOutgoingEdges().get(0).hasProperty(InternalProperties.MODEL_ORDER)) {
                p1Order = p1.getOutgoingEdges().get(0).getProperty(InternalProperties.MODEL_ORDER);
            }
            if (p2.getOutgoingEdges().get(0).hasProperty(InternalProperties.MODEL_ORDER)) {
                p2Order = p1.getOutgoingEdges().get(0).getProperty(InternalProperties.MODEL_ORDER);
            }
            
            // Same target node
            if (p1TargetNode != null && p1TargetNode.equals(p2TargetNode)) {
                // Backward edges below
                if (p1.getOutgoingEdges().get(0).getProperty(InternalProperties.REVERSED)
                        && !p2.getOutgoingEdges().get(0).getProperty(InternalProperties.REVERSED)) {
                    return 1;
                } else if (!p1.getOutgoingEdges().get(0).getProperty(InternalProperties.REVERSED)
                        && p2.getOutgoingEdges().get(0).getProperty(InternalProperties.REVERSED)) {
                    return -1;
                }
                return Integer.compare(p1Order, p2Order);
            }
            if (targetNodeModelOrder != null) {
                if (targetNodeModelOrder.containsKey(p1TargetNode)) {
                    p1Order = targetNodeModelOrder.get(p1TargetNode);
                }
                if (targetNodeModelOrder.containsKey(p2TargetNode)) {
                    p2Order = targetNodeModelOrder.get(p2TargetNode);
                }
            }
            return Integer.compare(p1Order, p2Order);
            
        }
        // Sort outgoing ports before incoming ports.
        if (!p1.getIncomingEdges().isEmpty() && !p2.getOutgoingEdges().isEmpty()) {
            return 1;
        } else if (!p1.getOutgoingEdges().isEmpty() && !p2.getIncomingEdges().isEmpty()) {
            return -1;
        } else if (p1.hasProperty(InternalProperties.MODEL_ORDER) && p2.hasProperty(InternalProperties.MODEL_ORDER)) {
            // The ports have no edges.
            // Use the port model order to compare them.
            return Integer.compare(p1.getProperty(InternalProperties.MODEL_ORDER),
                    p2.getProperty(InternalProperties.MODEL_ORDER));
        } else {
            return -1;
        }
    }
    
    /**
     * Compares two ports by considering the model order of them.
     * 
     * @param p1 The first port.
     * @param p2 The second port.
     * @return a negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the
     *         second.
     */
    public int checkPortModelOrder(final LPort p1, final LPort p2) {
        if (p1.hasProperty(InternalProperties.MODEL_ORDER) && p2.hasProperty(InternalProperties.MODEL_ORDER)) {
            return Integer.compare(p1.getProperty(InternalProperties.MODEL_ORDER),
                    p2.getProperty(InternalProperties.MODEL_ORDER));
        }
        return 0;
    }
}
