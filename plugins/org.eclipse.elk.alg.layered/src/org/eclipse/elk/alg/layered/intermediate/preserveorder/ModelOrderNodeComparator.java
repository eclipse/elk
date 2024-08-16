/*******************************************************************************
 * Copyright (c) 2020, 2023 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.preserveorder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LongEdgeOrderingStrategy;
import org.eclipse.elk.alg.layered.options.OrderingStrategy;

/**
 * Orders {@link LNode}s in the same layer by {@link InternalProperties#MODEL_ORDER}
 * or the order of the nodes they connect to in the previous layer.
 */
public class ModelOrderNodeComparator implements Comparator<LNode> {
    
    /**
     * The previous layer.
     */
    private LNode[] previousLayer;
    
    /**
     * The ordering strategy.
     */
    private final OrderingStrategy orderingStrategy;
    
    /**
     * Each node has an entry of nodes for which it is bigger.
     */
    private HashMap<LNode, HashSet<LNode>> biggerThan = new HashMap<>();
    /**
     * Each node has an entry of nodes for which it is smaller.
     */
    private HashMap<LNode, HashSet<LNode>> smallerThan = new HashMap<>();
    
    /**
     * Dummy node sorting strategy when compared to nodes with no connection to the previous layer.
     */
    private LongEdgeOrderingStrategy longEdgeNodeOrder = LongEdgeOrderingStrategy.EQUAL;
    
    /**
     * Creates a comparator to compare {@link LNode}s in the same layer.
     * 
     * @param thePreviousLayer The previous layer
     * @param orderingStrategy The ordering strategy
     * @param longEdgeOrderingStrategy The strategy to order dummy nodes and nodes with no connection the previous layer
     */
    public ModelOrderNodeComparator(final Layer thePreviousLayer, final OrderingStrategy orderingStrategy,
            final LongEdgeOrderingStrategy longEdgeOrderingStrategy) {
        this(orderingStrategy, longEdgeOrderingStrategy);
        this.previousLayer = new LNode[thePreviousLayer.getNodes().size()];
        thePreviousLayer.getNodes().toArray(this.previousLayer);
    }

    /**
     * Creates a comparator to compare {@link LNode}s in the same layer.
     * 
     * @param previousLayer The previous layer
     * @param orderingStrategy The ordering strategy
     * @param longEdgeOrderingStrategy The strategy to order dummy nodes and nodes with no connection the previous layer
     */
    public ModelOrderNodeComparator(final LNode[] previousLayer, final OrderingStrategy orderingStrategy,
            final LongEdgeOrderingStrategy longEdgeOrderingStrategy) {
        this(orderingStrategy, longEdgeOrderingStrategy);
        this.previousLayer = previousLayer;
    }
    
    private ModelOrderNodeComparator(final OrderingStrategy orderingStrategy,
            final LongEdgeOrderingStrategy longEdgeOrderingStrategy) {
        this.orderingStrategy = orderingStrategy;
        this.longEdgeNodeOrder = longEdgeOrderingStrategy;
    }

    @Override
    public int compare(final LNode n1, final LNode n2) {
        if (!biggerThan.containsKey(n1)) {
            biggerThan.put(n1, new HashSet<>());
        } else if (biggerThan.get(n1).contains(n2)) {
            return 1;
        }
        if (!biggerThan.containsKey(n2)) {
            biggerThan.put(n2, new HashSet<>());
        } else if (biggerThan.get(n2).contains(n1)) {
            return -1;
        }
        if (!smallerThan.containsKey(n1)) {
            smallerThan.put(n1, new HashSet<>());
        } else if (smallerThan.get(n1).contains(n2)) {
            return -1;
        }
        if (!smallerThan.containsKey(n2)) {
            smallerThan.put(n2, new HashSet<>());
        } else if (biggerThan.get(n2).contains(n1)) {
            return 1;
        }
        // If no model order is set, the one node is a dummy node and the nodes should be ordered
        // by the connected edges.
        // This kind of ordering should be preferred, if the order of the edges has priority.
        if (orderingStrategy == OrderingStrategy.PREFER_EDGES || !n1.hasProperty(InternalProperties.MODEL_ORDER)
                || !n2.hasProperty(InternalProperties.MODEL_ORDER)) {
            // In this case the order of the connected nodes in the previous layer should be respected
            LPort p1SourcePort = null;
            for (LPort p : n1.getPorts()) {
                // Get the first port that actually connects to a previous layer.
                if (!p.getIncomingEdges().isEmpty()) {
                    if (p.getIncomingEdges().get(0).getSource().getNode().getLayer() != n1.getLayer()) {
                        p1SourcePort = p.getIncomingEdges().get(0).getSource();
                        break;
                    }
                }
            }
            
            LPort p2SourcePort = null;
            for (LPort p : n2.getPorts()) {
                // Get the first port that actually connects to a previous layer.
                if (!p.getIncomingEdges().isEmpty()) {
                    if (p.getIncomingEdges().get(0).getSource().getNode().getLayer() != n2.getLayer()) {
                        p2SourcePort = p.getIncomingEdges().get(0).getSource();
                        break;
                    }
                }
            }
            
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
                            // Case the port is the one connecting to n1, therefore, n1 has a smaller model order
                            updateBiggerAndSmallerAssociations(n2, n1);
                            return -1;
                        } else if (port.equals(p2SourcePort)) {
                            // Case the port is the one connecting to n2, therefore, n1 has a bigger model order
                            updateBiggerAndSmallerAssociations(n1, n2);
                            return 1;
                        }
                    }
                    assert (false);
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
                        updateBiggerAndSmallerAssociations(n2, n1);
                        return -1;
                    } else if (previousNode.equals(p2Node)) {
                        updateBiggerAndSmallerAssociations(n1, n2);
                        return 1;
                    }
                }
            }
            
            // One node has no source port
            if (p1SourcePort != null && p2SourcePort == null || p1SourcePort == null && p2SourcePort != null) {
                // Check whether one of them is a helper dummy node.
                int comparedWithLongEdgeFeedback = handleHelperDummyNodes(n1, n2);
                if (comparedWithLongEdgeFeedback != 0) {
                    return comparedWithLongEdgeFeedback;
                }
                
                // Otherwise use the model order of the connected edges if one of them is no dummy node.
                if (!n1.hasProperty(InternalProperties.MODEL_ORDER) || !n2.hasProperty(InternalProperties.MODEL_ORDER)) {
                    int n1ModelOrder = getModelOrderFromConnectedEdges(n1);
                    int n2ModelOrder = getModelOrderFromConnectedEdges(n2);
                    if (n1ModelOrder > n2ModelOrder) {
                        updateBiggerAndSmallerAssociations(n1, n2);
                    } else {
                        updateBiggerAndSmallerAssociations(n2, n1);
                    }
                    return Integer.compare(
                            n1ModelOrder,
                            n2ModelOrder);
                }
            }

            // Both nodes are not connected to the previous layer
            if (p1SourcePort == null && p2SourcePort == null) {
                // Check whether one of them is a helper dummy node.
                int comparedWithLongEdgeFeedback = handleHelperDummyNodes(n1, n2);
                if (comparedWithLongEdgeFeedback != 0) {
                    return comparedWithLongEdgeFeedback;
                }
            }
            
            // Fall through case.
            // Both nodes are not connected to the previous layer. Therefore, they must be normal nodes.
            // The model order shall be used to order them.
        }
        // Order nodes by their order in the model.
        // This is also the fallback case if one of the nodes is not connected to the previous layer.
        int n1ModelOrder = n1.getProperty(InternalProperties.MODEL_ORDER);
        int n2ModelOrder = n2.getProperty(InternalProperties.MODEL_ORDER);
        if (n1ModelOrder > n2ModelOrder) {
            updateBiggerAndSmallerAssociations(n1, n2);
        } else {
            updateBiggerAndSmallerAssociations(n2, n1);
        }
        return Integer.compare(
                n1ModelOrder,
                n2ModelOrder);
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
        // Set to -1 to sort dummy nodes under nodes without a connection to the previous layer.
        // Set to MAX_INT to sort dummy nodes over nodes without a connection to the previous layer.
        // Set to 0 if you do not care about their order.
        // One of this has to be chosen, since dummy nodes are not comparable with nodes
        // that do not have a connection to the previous layer.
        return longEdgeNodeOrder.returnValue();
    }
    
    private void updateBiggerAndSmallerAssociations(final LNode bigger, final LNode smaller) {
        HashSet<LNode> biggerNodeBiggerThan = biggerThan.get(bigger);
        HashSet<LNode> smallerNodeBiggerThan = biggerThan.get(smaller);
        HashSet<LNode> biggerNodeSmallerThan = smallerThan.get(bigger);
        HashSet<LNode> smallerNodeSmallerThan = smallerThan.get(smaller);
        biggerNodeBiggerThan.add(smaller);
        smallerNodeSmallerThan.add(bigger);
        for (LNode verySmall : smallerNodeBiggerThan) {
            biggerNodeBiggerThan.add(verySmall);
            smallerThan.get(verySmall).add(bigger);
            smallerThan.get(verySmall).addAll(biggerNodeSmallerThan);
        }
        

        for (LNode veryBig : biggerNodeSmallerThan) {
            smallerNodeSmallerThan.add(veryBig);
            biggerThan.get(veryBig).add(smaller);
            biggerThan.get(veryBig).addAll(smallerNodeBiggerThan);
        }
    }
    
    private int handleHelperDummyNodes(LNode n1, LNode n2) {
        if (n1.getType() == NodeType.LONG_EDGE && n2.getType() == NodeType.NORMAL) {
            // n1 is a long edge node feedback node.
            
            LPort dummyNodeSourcePort = getFirstIncomingSourcePortOfNode(n1);
            LNode dummyNodeSourceNode = dummyNodeSourcePort.getNode();
            // Case the source of the dummy is the same node as n2, than the dummy node is routed below.
            if (dummyNodeSourceNode.equals(n2)) {
                updateBiggerAndSmallerAssociations(n1, n2);
                return 1;
            } else {
                // Calculate whether the dummy node leads to the target node.
                LPort dummyNodeTargetPort = getFirstOutgoingSourcePortOfNode(n1);
                LNode dummyNodeTargetNode = dummyNodeTargetPort.getNode();
                if (dummyNodeTargetNode.equals(n2)) {
                    updateBiggerAndSmallerAssociations(n1, n2);
                    return 1;
                }
                
                // If the two nodes are not the same, order them based on the source model order.
                return this.compare(dummyNodeSourceNode, n2);
            }
        } else if (n1.getType() == NodeType.NORMAL && n2.getType() == NodeType.LONG_EDGE) {
            // n2 is a long edge node feedback node.
            LPort dummyNodeSourcePort = getFirstIncomingSourcePortOfNode(n2);
            LNode dummyNodeSourceNode = dummyNodeSourcePort.getNode();
            // Case the source of the dummy is the same node as n2, than the dummy node is routed below.
            if (dummyNodeSourceNode.equals(n1)) {
                updateBiggerAndSmallerAssociations(n2, n1);
                return -1;
            } else {
                // Calculate whether the dummy node leads to the target node.
                LPort dummyNodeTargetPort = getFirstOutgoingSourcePortOfNode(n2);
                LNode dummyNodeTargetNode = dummyNodeTargetPort.getNode();
                if (dummyNodeTargetNode.equals(n1)) {
                    updateBiggerAndSmallerAssociations(n2, n1);
                    return 1;
                }
                
                // If the two nodes are not the same, order them based on the source model order.
                return this.compare(n1, dummyNodeSourceNode);
            }
        } else if (n1.getType() == NodeType.LONG_EDGE && n2.getType() == NodeType.LONG_EDGE) {
            // both are long edge feedback nodes.
            LPort n1dummyNodeSourcePort = getFirstIncomingSourcePortOfNode(n1);
            LPort n2dummyNodeSourcePort = getFirstIncomingSourcePortOfNode(n2);
            // Case both are on the same node, sort them in reverse order of their ports.
            if (n1dummyNodeSourcePort.getNode().equals(n2dummyNodeSourcePort.getNode())) {
                // Find the first port that occurs on the node. Since it has to be a WEST port (check this) reverse
                // the order.
                for (LPort port : n1dummyNodeSourcePort.getNode().getPorts()) {
                    if (n1dummyNodeSourcePort.equals(port)) {
                        updateBiggerAndSmallerAssociations(n2, n1);
                        return -1;
                    } else if (n2dummyNodeSourcePort.equals(port)) {
                        updateBiggerAndSmallerAssociations(n1, n2);
                        return 1;
                    }
                }
            }
            
            // Case both edges connect to separate nodes.
            // In this case comapare the order of their nodes in their layer.
            LNode n1dummyNodeSourceNode = n1dummyNodeSourcePort.getNode();
            LNode n2dummyNodeSourceNode = n2dummyNodeSourcePort.getNode();
            Layer dummySourceLayer = n1dummyNodeSourceNode.getLayer();
            // Find the first node that occurs in the layer and order the nodes in reverse.
            for (LNode node : dummySourceLayer) {
                if (n1dummyNodeSourceNode.equals(node)) {
                    updateBiggerAndSmallerAssociations(n2, n1);
                    return -1;
                } else if (n2dummyNodeSourceNode.equals(node)) {
                    updateBiggerAndSmallerAssociations(n1, n2);
                    return 1;
                }
            }
            // This cannot occur.
            return 0;
        } else {
            // These nodes are just two normal nodes
            return 0;
        }
    }
    
    private LPort getFirstIncomingPortOfNode(LNode node) {
        return node.getPorts().stream().filter(p -> !p.getIncomingEdges().isEmpty()).findFirst().orElse(null);
    }
    
    private LPort getFirstIncomingSourcePortOfNode(LNode node) {
        return getFirstIncomingPortOfNode(node).getIncomingEdges().get(0).getSource();
    }
    
    private LPort getFirstOutgoingPortOfNode(LNode node) {
        return node.getPorts().stream().filter(p -> !p.getOutgoingEdges().isEmpty()).findFirst().orElse(null);
    }
    
    private LPort getFirstOutgoingSourcePortOfNode(LNode node) {
        return getFirstOutgoingPortOfNode(node).getOutgoingEdges().get(0).getTarget();
    }
}
