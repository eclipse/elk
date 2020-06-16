/*******************************************************************************
 * Copyright (c) 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.Collections;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Sorts all child nodes and their edges of the given graph by their {@link InternalProperties#MODEL_ORDER},
 * which represents the order in input graph.
 * Outgoing ports are sorted by the order of their edges, incoming ports are sorted by the order of their nodes in the
 * previous layer.
 * <dl><dl>
 *   <dt>Precondition:</dt>
 *     <dd>a layered graph</dd>
 *     <dd>nodes have fixed port sides.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>Nodes and ports (edges) are sorted to respect the order in the input graph</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 3.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>Since this changes the order of ports and nodes, it is not compatible with other processors
 *     that do the same.</dd>
 * </dl>
 */
public class SortByInputModelProcessor implements ILayoutProcessor<LGraph> {

    @Override
    public void process(final LGraph graph, final IElkProgressMonitor progressMonitor) {
        int layerIndex = 0;
        for (Layer layer : graph) {
            final int previousLayerIndex = layerIndex == 0 ? 0 : layerIndex - 1;
            Layer previousLayer = graph.getLayers().get(previousLayerIndex);
            for (LNode node : layer.getNodes()) {
                Collections.sort(node.getPorts(), (p1, p2) -> {
                    // Sort incoming edges by sorting their ports by the order of the nodes they connect to.
                    if (p1.getOutgoingEdges().isEmpty() && p2.getOutgoingEdges().isEmpty()) {
                        LNode p1Node = p1.getIncomingEdges().get(0).getSource().getNode();
                        LNode p2Node = p2.getIncomingEdges().get(0).getSource().getNode();
                        if (p1Node.equals(p2Node)) {
                            // In this case both incoming edges must have a model order set. Check it.
                            return Integer.compare(
                                    p1.getIncomingEdges().get(0).getProperty(InternalProperties.MODEL_ORDER),
                                    p2.getIncomingEdges().get(0).getProperty(InternalProperties.MODEL_ORDER));
                        }
                        for (LNode previousNode : previousLayer) {
                            if (previousNode.equals(p1Node)) {
                                return 1;
                            }
                            if (previousNode.equals(p2Node)) {
                                return -1;
                            }
                        }
                    }

                    // Sort outgoing edges by sorting their ports.
                    if (p1.getIncomingEdges().isEmpty() && p2.getIncomingEdges().isEmpty()) {
                        return Integer.compare(
                                p1.getOutgoingEdges().get(0).getProperty(InternalProperties.MODEL_ORDER),
                                p2.getOutgoingEdges().get(0).getProperty(InternalProperties.MODEL_ORDER));
                    }
                    // Sort outgoing ports before incoming ports.
                    if (p1.getOutgoingEdges().isEmpty() && p2.getIncomingEdges().isEmpty()) {
                        return 1;
                    } else {
                        return -1;
                    }
                });
            }
            // Sort nodes.
            Collections.sort(layer.getNodes(), (n1, n2) -> {
                // If no model order is set, the one node is a dummy node and the nodes should be ordered
                // by the connected edges.
                if (!n1.hasProperty(InternalProperties.MODEL_ORDER)
                        || !n2.hasProperty(InternalProperties.MODEL_ORDER)) {
                    // In this case the order of the connected nodes in the previous layer should be respected
           
                    LPort p1 = null;
                    LPort p2 = null;
                    // Get first incoming port
                    for (LPort port : n1.getPorts()) {
                        if (!port.getIncomingEdges().isEmpty()) {
                            p1 = port;
                            break;
                        }
                    }
                    for (LPort port : n2.getPorts()) {
                        if (!port.getIncomingEdges().isEmpty()) {
                            p2 = port;
                            break;
                        }
                    }
                    
                    // Get node in previous layer
                    LNode p1Node = p1.getIncomingEdges().get(0).getSource().getNode();
                    LNode p2Node = p2.getIncomingEdges().get(0).getSource().getNode();
                    
                    // If the nodes are equal the incoming edges can be used to order the nodes.
                    if (p1Node.equals(p2Node)) {
                        return Integer.compare(
                              getModelOrderFromConnectedEdges(n1),
                              getModelOrderFromConnectedEdges(n2));
                    }
                    
                    // Else they are ordered by the nodes they connect to.
                    // One can disregard the model order here, since it the orderings does already reflect it.
                    for (LNode previousNode : previousLayer) {
                        if (previousNode.equals(p1Node)) {
                            return -1;
                        } else if (previousNode.equals(p2Node)) {
                            return 1;
                        }
                    }
                }
                // Order nodes by their order in the model.
                return Integer.compare(
                        n1.getProperty(InternalProperties.MODEL_ORDER),
                        n2.getProperty(InternalProperties.MODEL_ORDER));
            });
            layerIndex++;
        }
    }
    
    /**
     * The {@link InternalProperties#MODEL_ORDER} of the first incoming edge of a node.
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
