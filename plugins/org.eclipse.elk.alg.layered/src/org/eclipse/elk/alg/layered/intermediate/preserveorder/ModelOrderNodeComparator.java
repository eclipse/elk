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

    @Override
    public int compare(final LNode n1, final LNode n2) {
        // If no model order is set, the one node is a dummy node and the nodes should be ordered
        // by the connected edges.
        // This kind of ordering should be preferred, if the order of the edges has priority.
        if (orderingStrategy == OrderingStrategy.PREFER_EDGES || !n1.hasProperty(InternalProperties.MODEL_ORDER)
                || !n2.hasProperty(InternalProperties.MODEL_ORDER)) {
            // In this case the order of the connected nodes in the previous layer should be respected
            LPort p1SourcePort = n1.getPorts().stream().filter(p -> !p.getIncomingEdges().isEmpty())
                    .findFirst().map(p -> p.getIncomingEdges().get(0).getSource()).orElse(null);
            LPort p2SourcePort = n2.getPorts().stream().filter(p -> !p.getIncomingEdges().isEmpty())
                    .findFirst().map(p -> p.getIncomingEdges().get(0).getSource()).orElse(null);
            
            // Case both nodes have connections to the previous layer.
            if (p1SourcePort != null && p2SourcePort != null) {
                LNode p1Node = p1SourcePort.getNode();
                LNode p2Node = p2SourcePort.getNode();
                
                // If both nodes connect to the same node the order of their corresponding ports in the previous
                // layer should be used to order them.
                if (p1Node != null && p1Node.equals(p2Node)) {
                    // We are not allowed to look at the model order of the edges but we have to look at the actual
                    // port ordering.
                    for (LPort port : p1Node.getPorts()) {
                        if (port.equals(p1SourcePort)) {
                            return -1;
                        } else if (port.equals(p2SourcePort)) {
                            return 1;
                        }
                    }
                    // Cannot happen, since both nodes have a connection to the previous layer.
                    return Integer.compare(
                            getModelOrderFromConnectedEdges(n1),
                            getModelOrderFromConnectedEdges(n2));
                }
                
                // Else the nodes are ordered by the nodes they connect to.
                // One can disregard the model order here
                // since the ordering in the previous layer does already reflect it.
                for (LNode previousNode : previousLayer) {
                    if (previousNode.equals(p1Node)) {
                        return -1;
                    } else if (previousNode.equals(p2Node)) {
                        return 1;
                    }
                }
            }
            
            // One node has no source port
            if (!n1.hasProperty(InternalProperties.MODEL_ORDER) || !n2.hasProperty(InternalProperties.MODEL_ORDER)) {
                return Integer.compare(
                        getModelOrderFromConnectedEdges(n1),
                        getModelOrderFromConnectedEdges(n2));
            }
            // Fall through case.
            // Both nodes are not connected to the previous layer. Therefore, they must be normal nodes.
            // The model order shall be used to order them.
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
     * @return The model order of the first incoming edge of the given node.
     * Returns Integer.MAX_VALUE if no such edge exists.
     */
    private int getModelOrderFromConnectedEdges(final LNode n) {
        LPort sourcePort = n.getPorts().stream().filter(p -> !p.getIncomingEdges().isEmpty()).findFirst().orElse(null);
        if (sourcePort != null) {
            LEdge edge = sourcePort.getIncomingEdges().get(0);
            if (edge != null) {
                return edge.getProperty(InternalProperties.MODEL_ORDER);
            }
        }
        // Set to -1 to sort nodes without a connection to the previous layer over dummy nodes.
        // One of this has to be chosen, since dummy nodes are not comparable with nodes
        // that do not have a connection to the previous layer.
        return Integer.MAX_VALUE;
    }
}
