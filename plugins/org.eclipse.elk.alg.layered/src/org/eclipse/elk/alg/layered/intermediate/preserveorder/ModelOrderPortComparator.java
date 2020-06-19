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

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;

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
     * The previous layer.
     */
    private final Layer previousLayer;

    /**
     * Creates a comparator to compare {@link LPort}s in the same layer.
     * 
     * @param previousLayer The previous layer
     * @param targetNodeModelOrder The minimal model order connecting to a target node.
     */
    public ModelOrderPortComparator(final Layer previousLayer, final Map<LNode, Integer> targetNodeModelOrder) {
        this.previousLayer = previousLayer;
        this.targetNodeModelOrder = targetNodeModelOrder;
    }

    @Override
    public int compare(final LPort p1, final LPort p2) {
        // Sort incoming edges by sorting their ports by the order of the nodes they connect to.
        if (!p1.getIncomingEdges().isEmpty() && !p2.getIncomingEdges().isEmpty()) {
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
            int p1Order = p1.getOutgoingEdges().get(0).getProperty(InternalProperties.MODEL_ORDER);
            int p2Order = p2.getOutgoingEdges().get(0).getProperty(InternalProperties.MODEL_ORDER);
            
            if (p1TargetNode != null && p1TargetNode.equals(p2TargetNode)) {
                return Integer.compare(p1Order, p2Order);
            }
            
            if (targetNodeModelOrder.containsKey(p1TargetNode)) {
                p1Order = targetNodeModelOrder.get(p1TargetNode);
            }
            if (targetNodeModelOrder.containsKey(p2TargetNode)) {
                p2Order = targetNodeModelOrder.get(p2TargetNode);
            }
            return Integer.compare(p1Order, p2Order);
            
        }
        // Sort outgoing ports before incoming ports.
        if (!p1.getIncomingEdges().isEmpty() && !p2.getOutgoingEdges().isEmpty()) {
            return 1;
        } else {
            return -1;
        }
    }
}
