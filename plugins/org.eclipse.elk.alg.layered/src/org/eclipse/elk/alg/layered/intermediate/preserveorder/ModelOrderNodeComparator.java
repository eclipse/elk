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
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.OrderingStrategy;

/**
 * Orders {@link LNode}s in the same layer by {@link InternalProperties#MODEL_ORDER}
 * or the order of the nodes they connect to in the previous layer.
 */
public class ModelOrderNodeComparator implements Comparator<LNode> {
    
    /**
     * The previous layer.
     */
    private final Layer previousLayer;
    
    /**
     * The ordering strategy.
     */
    private final OrderingStrategy orderingStrategy;
    
    /**
     * Creates a comparator to compare {@link LNode}s in the same layer.
     * 
     * @param previousLayer The previous layer
     * @param orderingStrategy The ordering strategy
     */
    public ModelOrderNodeComparator(final Layer previousLayer, final OrderingStrategy orderingStrategy) {
        this.previousLayer = previousLayer;
        this.orderingStrategy = orderingStrategy;
    }

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(final LNode n1, final LNode n2) {
        // If no model order is set, the one node is a dummy node and the nodes should be ordered
        // by the connected edges.
        if (orderingStrategy == OrderingStrategy.PREFER_EDGES || !n1.hasProperty(InternalProperties.MODEL_ORDER)
                || !n2.hasProperty(InternalProperties.MODEL_ORDER)) {
            // In this case the order of the connected nodes in the previous layer should be respected
            LNode p1Node = n1.getPorts().stream().filter(p -> !p.getIncomingEdges().isEmpty())
                    .findFirst().map(p -> p.getIncomingEdges().get(0).getSource().getNode())
                    .orElse(null);
            LNode p2Node = n2.getPorts().stream().filter(p -> !p.getIncomingEdges().isEmpty())
                    .findFirst().map(p -> p.getIncomingEdges().get(0).getSource().getNode())
                    .orElse(null);
            // If the nodes are equal the incoming edges can be used to order the nodes.
            // We assume one of the nodes p1Node and p2Node is not null.
            // Otherwise both of them would have a model order set,
            // since dummy nodes have at least one incoming edge.
            // This means that dummy nodes are always ordered above nodes with no incoming edges.
            if (p1Node != null && p1Node.equals(p2Node)) {
                return Integer.compare(
                      getModelOrderFromConnectedEdges(n1),
                      getModelOrderFromConnectedEdges(n2));
            }
            
            // Else they are ordered by the nodes they connect to.
            // One can disregard the model order here,
            // since the ordering in the previous layer does already reflect it.
            for (LNode previousNode : previousLayer) {
                if (previousNode.equals(p1Node)) {
                    return -1;
                } else if (previousNode.equals(p2Node)) {
                    return 1;
                }
            }
            // Fall through case.
            // Both nodes are not connected to the previous layer. They henceforth must be normal nodes.
            // Therefore the model order shall be used to order them.
        }
        // Order nodes by their order in the model.
        return Integer.compare(
                n1.getProperty(InternalProperties.MODEL_ORDER),
                n2.getProperty(InternalProperties.MODEL_ORDER));
    }
    
    /**
     * The {@link InternalProperties#MODEL_ORDER} of the first incoming edge of a node.
     * 
     * @param n The node
     * @return The model order of the first incoming edge of the given node. Returns -1 if no such edge exists.
     */
    private int getModelOrderFromConnectedEdges(final LNode n) {
        List<LPort> ports = n.getPorts();
        if (ports.size() > 0) {
            LEdge edge = null;
            for (LPort lPort : ports) {
                if (lPort.getIncomingEdges().size() > 0) {
                    edge = lPort.getIncomingEdges().get(0);
                    break;
                }
            }
            if (edge == null) {
                return -1;
            }
            return edge.getProperty(InternalProperties.MODEL_ORDER);
        }
        return -1;
    }
}
