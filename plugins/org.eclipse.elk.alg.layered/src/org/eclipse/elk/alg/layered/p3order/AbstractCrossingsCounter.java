/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

import java.util.Map;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.Pair;

import com.google.common.collect.Maps;

/**
 * Base class for edge crossings counting strategies. This abstract class contains the code for
 * counting in-layer edge crossings and north/south port crossings.
 * 
 * @author msp
 * @author cds
 */
public abstract class AbstractCrossingsCounter {
    
    /**
     * The number of in-layer edges for each layer, including virtual connections to
     * north/south dummies.
     */
    private final int[] inLayerEdgeCount;
    /**
     * Whether the layers contain north / south port dummies or not.
     */
    private final boolean[] hasNorthSouthPorts;
    
    /**
     * Create a crossing counter with given information about the layers.
     * 
     * @param inLayerEdgeCount
     *          The number of in-layer edges for each layer, including virtual connections to
     *          north/south dummies
     * @param hasNorthSouthPorts
     *          Whether the layers contain north / south port dummies or not
     */
    public AbstractCrossingsCounter(final int[] inLayerEdgeCount, final boolean[] hasNorthSouthPorts) {
        this.inLayerEdgeCount = inLayerEdgeCount;
        this.hasNorthSouthPorts = hasNorthSouthPorts;
    }
    
    /**
     * Count the inter-layer crossings between the two given layers. The actual counting strategy
     * must be implemented by subclasses. The computed value may be an estimation.
     * 
     * @param leftLayer the left layer
     * @param rightLayer the right layer
     * @return the number of crossings between edges going from the left layer to the right layer
     */
    public abstract int countCrossings(final NodeGroup[] leftLayer, final NodeGroup[] rightLayer);
    
    /**
     * Count the in-layer crossings of the given layer. This includes crossings of in-layer edges
     * as well as edges connected to north/south side ports.
     * 
     * @param layer a layer
     * @param index the index of the layer inside the layered graph
     * @return the number of in-layer crossings of the given layer
     */
    public final int countCrossings(final NodeGroup[] layer, final int index) {
        int c = 0;
        if (inLayerEdgeCount[index] > 0) {
            c += countInLayerEdgeCrossings(layer);
        }
        
        if (hasNorthSouthPorts[index]) {
            c += countNorthSouthPortCrossings(layer);
        }
        return c;
    }

    /*
     * The algorithm used to count crossings within a layer implemented in the following method has
     * two parts:
     * 
     * Part 1 A normal node cannot be connected to another normal node in the same layer due to how
     * layering is performed. It remains that at least one of the two nodes must be a dummy node.
     * Currently, that can only happen due to odd port side handling. Because of the way dummies are
     * created, there are only two cases:
     * 
     * - An eastern port can be connected to another eastern port.
     * - A western port can be connected to another western port.
     * 
     * The algorithm now works by assigning numbers to eastern ports top-down, and to western ports
     * bottom-up, all the time dependent on their number of incident edges. (the link direction is
     * not important) Then we traverse the ports. If we find an eastern port connected to another
     * eastern port, the difference of their indices tells us how many other ports with incident
     * edges lie between them and can cause crossings.
     * 
     * Part 2 Additional crossings can happen due to nodes being placed between a node and its north
     * / south dummies. The idea here is to use the first node sweep from part 1 to count the number
     * of northern and southern North/South dummies for each node. Each north dummy is assigned a
     * position in the list of northern dummies for its node. South dummies are treated accordingly.
     * 
     * In a second sweep, for each non-north/south dummy, the most recently encountered north/south
     * dummy or normal node is retrieved. Its index is subtracted from the number of northern or
     * southern dummies of its node. The result gives the number of crossings caused by the node
     * being placed between a node and its north/south dummies.
     * 
     * Note that part two relies on information about layer layout units. If we find that they have
     * not been set, we can skip this part.
     */

    /**
     * Calculates the worst case for the number of crossings caused by in-layer edges in the given
     * layer and by north/south port dummies that are later connected to their corresponding regular
     * nodes. The actual number of crossings may be lower.
     * 
     * @param layer
     *            the layer whose in-layer crossings and north/south dummy crossings to estimate.
     * @return the worst possible number of crossings
     */
    private int countInLayerEdgeCrossings(final NodeGroup[] layer) {
        int eastWestCrossings = 0;
        int northSouthCrossings = 0;

        // Number of north/south dummies and indices
        Map<LNode, Pair<Integer, Integer>> northSouthCrossingHints  = Maps.newHashMap();
        Map<LNode, Integer> dummyIndices = Maps.newHashMap();

        // Assign numbers to the layer's eastern and western ports
        Map<LPort, Integer> easternPortNumbers = Maps.newHashMap();
        Map<LPort, Integer> westernPortNumbers = Maps.newHashMap();

        numberEastWestPorts(layer, easternPortNumbers, westernPortNumbers);

        // Iterate through the nodes
        LNode currentNormalNode = null;
        int northMaxCrossingHint = 0;
        int southMaxCrossingHint = 0;
        boolean northernSide = true;
        boolean layerLayoutUnitsSet = true;

        for (NodeGroup nodeGroup : layer) {
            LNode node = nodeGroup.getNode();
            
            // Part 1 of the crossing counting algorithm
            for (LPort port : node.getPorts()) {
                switch (port.getSide()) {
                case EAST:
                    eastWestCrossings += countInLayerCrossings(port, easternPortNumbers);
                    break;

                case WEST:
                    eastWestCrossings += countInLayerCrossings(port, westernPortNumbers);
                    break;
                }
            }

            // First sweep of part 2 of the crossing counting algorithm
            NodeType nodeType = node.getType();
            if (layerLayoutUnitsSet
                    && (nodeType == NodeType.NORMAL || nodeType == NodeType.NORTH_SOUTH_PORT)) {

                LNode newNormalNode = node.getProperty(InternalProperties.IN_LAYER_LAYOUT_UNIT);
                if (newNormalNode == null) {
                    // Layer layout units don't seem to have been set
                    layerLayoutUnitsSet = false;
                    continue;
                }

                // Check if this node belongs to a new normal node
                if (currentNormalNode != newNormalNode) {
                    // Save the old normal node's values
                    if (currentNormalNode != null) {
                        northSouthCrossingHints.put(currentNormalNode, new Pair<Integer, Integer>(
                                northMaxCrossingHint, southMaxCrossingHint));
                    }

                    // Reset the counters
                    currentNormalNode = newNormalNode;
                    northMaxCrossingHint = 0;
                    southMaxCrossingHint = 0;
                    northernSide = true;
                }

                // If the node is the normal node, we're entering its south side
                if (node == currentNormalNode) {
                    northernSide = false;
                }

                // Update and save crossing hints
                if (northernSide) {
                    northMaxCrossingHint += node.getProperty(InternalProperties.CROSSING_HINT);
                    dummyIndices.put(node, northMaxCrossingHint);
                } else {
                    southMaxCrossingHint += node.getProperty(InternalProperties.CROSSING_HINT);
                    dummyIndices.put(node, southMaxCrossingHint);
                }
            }
        }

        // Remember to save the values for the last normal node
        if (currentNormalNode != null) {
            northSouthCrossingHints.put(currentNormalNode, new Pair<Integer, Integer>(
                    northMaxCrossingHint, southMaxCrossingHint));
        }

        // Second sweep of Part 2 of the algorithm
        if (layerLayoutUnitsSet) {
            LNode lastDummyNormalNode = null;
            int lastDummyIndex = 0;
            int dummyCount = 0;
            northernSide = true;

            for (NodeGroup nodeGroup : layer) {
                LNode node = nodeGroup.getNode();
                NodeType nodeType = node.getType();

                switch (nodeType) {
                case NORMAL:
                    lastDummyIndex = dummyIndices.get(node);

                    dummyCount = northSouthCrossingHints.get(node).getSecond();
                    lastDummyNormalNode = node;
                    northernSide = false;
                    
                    break;

                case NORTH_SOUTH_PORT:
                    lastDummyIndex = dummyIndices.get(node);

                    LNode newNormalNode = node.getProperty(InternalProperties.IN_LAYER_LAYOUT_UNIT);
                    if (newNormalNode != lastDummyNormalNode) {
                        dummyCount = northSouthCrossingHints.get(newNormalNode).getFirst();
                        lastDummyNormalNode = newNormalNode;
                        northernSide = true;
                    }
                    break;

                default:
                    northSouthCrossings += northernSide ? lastDummyIndex : dummyCount - lastDummyIndex;
                }
            }
        }
        
        return eastWestCrossings + northSouthCrossings;
    }

    /**
     * Assigns numbers to the eastern ports of a layer, and to the western ports of a layer. A
     * number is assigned to a port if it has incident edges. Eastern ports are numbered top-down,
     * while western ports are numbered bottom-up.
     * 
     * @param layer
     *            the layer whose ports to index.
     * @param easternMap
     *            map to put the eastern ports' indices in.
     * @param westernMap
     *            map to put the western ports' indices in.
     */
    private void numberEastWestPorts(final NodeGroup[] layer, final Map<LPort, Integer> easternMap,
            final Map<LPort, Integer> westernMap) {

        int currentEasternNumber = 0;
        int currentWesternNumber = 0;

        // Assign numbers to eastern ports, top-down
        for (int nodeIndex = 0; nodeIndex < layer.length; nodeIndex++) {
            LNode node = layer[nodeIndex].getNode();

            if (node.getProperty(LayoutOptions.PORT_CONSTRAINTS).isOrderFixed()) {
                for (LPort easternPort : node.getPorts(PortSide.EAST)) {
                    if (easternPort.getDegree() > 0) {
                        currentEasternNumber += easternPort.getDegree();
                        easternMap.put(easternPort, currentEasternNumber);
                    }
                }
            } else {
                // Find the number of edges incident to eastern ports
                for (LPort easternPort : node.getPorts(PortSide.EAST)) {
                    currentEasternNumber += easternPort.getDegree();
                }

                // Assign the eastern number to all eastern ports
                for (LPort easternPort : node.getPorts(PortSide.EAST)) {
                    if (easternPort.getDegree() > 0) {
                        easternMap.put(easternPort, currentEasternNumber);
                    }
                }
            }
        }

        // Assign indices to western ports, bottom-up
        for (int nodeIndex = layer.length - 1; nodeIndex >= 0; nodeIndex--) {
            LNode node = layer[nodeIndex].getNode();

            if (node.getProperty(LayoutOptions.PORT_CONSTRAINTS).isOrderFixed()) {
                for (LPort westernPort : node.getPorts(PortSide.WEST)) {
                    if (westernPort.getDegree() > 0) {
                        currentWesternNumber += westernPort.getDegree();
                        westernMap.put(westernPort, currentWesternNumber);
                    }
                }
            } else {
                // Find the number of edges incident to western ports
                for (LPort westernPort : node.getPorts(PortSide.WEST)) {
                    currentWesternNumber += westernPort.getDegree();
                }

                // Assign the western number to all western ports
                for (LPort westernPort : node.getPorts(PortSide.WEST)) {
                    if (westernPort.getDegree() > 0) {
                        westernMap.put(westernPort, currentWesternNumber);
                    }
                }
            }
        }
    }

    /**
     * Counts the crossings caused by in-layer edges connected to the given port. An edge is only
     * counted once.
     * 
     * @param port
     *            the port whose edge crossings to count.
     * @param portIndices
     *            map of ports to their respective indices as calculated by
     *            {@link #numberEastWestPorts(LNode[], Map, Map)}.
     * @return the maximum number of crossings for this port.
     */
    private int countInLayerCrossings(final LPort port, final Map<LPort, Integer> portIndices) {
        int maxCrossings = 0;

        // Find this port's index
        Integer portIndex = portIndices.get(port);
        if (portIndex == null) {
            return 0;
        }

        // Find the maximum distance between two connected ports
        Integer connectedPortIndex = null;
        for (LEdge edge : port.getConnectedEdges()) {
            if (edge.getSource() == port) {
                connectedPortIndex = portIndices.get(edge.getTarget());
            } else {
                connectedPortIndex = portIndices.get(edge.getSource());
            }
            
            // Check if the edge is connected to another port in the same layer
            if (connectedPortIndex != null) {
                // Only count the edge once
                if (portIndex.intValue() > connectedPortIndex.intValue()) {
                    maxCrossings = Math.max(maxCrossings,
                            portIndex.intValue() - connectedPortIndex.intValue() - 1);
                }
            }
        }

        return maxCrossings;
    }
    
    /**
     * Counts the number of edge crossings caused by the way north / south port dummies are ordered.
     * 
     * @param layer the layer whose north / south port related crossings to count.
     * @return the number of crossings caused by edges connected to northern or southern ports.
     */
    private int countNorthSouthPortCrossings(final NodeGroup[] layer) {
        int crossings = 0;
        boolean northernSide = true;
        LNode recentNormalNode = null;
        
        // Iterate through the layer's nodes
        for (int i = 0; i < layer.length; i++) {
            LNode node = layer[i].getNode();
            NodeType nodeType = node.getType();
            
            if (nodeType == NodeType.NORMAL) {
                // We possibly have a new recentNormalNode; we definitely change the side to the normal
                // node's south
                recentNormalNode = node;
                northernSide = false;
            } else if (nodeType == NodeType.NORTH_SOUTH_PORT) {
                // If we have a dummy that represents a self-loop, continue with the next one
                // (self-loops have no influence on the number of crossings anyway, regardless of where
                // they are placed)
                if (node.getProperty(InternalProperties.ORIGIN) instanceof LEdge) {
                    continue;
                }
                
                // Check if the dummy node belongs to a new normal node
                LNode currentNormalNode = (LNode) node.getProperty(InternalProperties.ORIGIN);
                if (recentNormalNode != currentNormalNode) {
                    // A have a new normal node and are on its northern side
                    recentNormalNode = currentNormalNode;
                    northernSide = true;
                }
                
                // Check if the current normal node has a fixed port order; if not, we can ignore it
                if (!currentNormalNode.getProperty(LayoutOptions.PORT_CONSTRAINTS).isOrderFixed()) {
                    continue;
                }
                
                // Find the up to two ports represented by this dummy node
                LPort nodeInputPort = null;
                LPort nodeOutputPort = null;
                for (LPort port : node.getPorts()) {
                    // We assume here that a port of a north / south dummy has either incoming or
                    // outgoing edges, but not both. So far, that's the case.
                    assert port.getIncomingEdges().isEmpty() ^ port.getOutgoingEdges().isEmpty()
                        : port.getIncomingEdges().size() + " incoming edges, "
                        + port.getOutgoingEdges().size() + " outgoing edges";
                    
                    if (!port.getIncomingEdges().isEmpty()) {
                        nodeInputPort = (LPort) port.getProperty(InternalProperties.ORIGIN);
                    } else if (!port.getOutgoingEdges().isEmpty()) {
                        nodeOutputPort = (LPort) port.getProperty(InternalProperties.ORIGIN);
                    }
                }
                
                // Iterate over the next nodes until we find a north / south port dummy belonging to a
                // new normal node or until we find our current normal node
                for (int j = i + 1; j < layer.length; j++) {
                    LNode node2 = layer[j].getNode();
                    NodeType node2Type = node2.getType();
                    
                    if (node2Type == NodeType.NORMAL) {
                        // We can stop
                        break;
                    } else if (node2Type == NodeType.NORTH_SOUTH_PORT) {
                        // Check if the north / south port dummy still belongs to the same normal node
                        if (node2.getProperty(InternalProperties.ORIGIN) != currentNormalNode) {
                            // New normal node, we can stop
                            break;
                        }
                        
                        // Find the up to two ports represented by this dummy node
                        LPort node2InputPort = null;
                        LPort node2OutputPort = null;
                        for (LPort port2 : node2.getPorts()) {
                            // We assume here that a port of a north / south dummy has either incoming or
                            // outgoing edges, but not both. So far, that's the case.
                            if (!port2.getIncomingEdges().isEmpty()) {
                                node2InputPort = (LPort) port2.getProperty(InternalProperties.ORIGIN);
                            } else if (!port2.getOutgoingEdges().isEmpty()) {
                                node2OutputPort = (LPort) port2.getProperty(InternalProperties.ORIGIN);
                            }
                        }
                        
                        // How crossings are determined depends on which side of the normal node we're on
                        if (northernSide) {
                            boolean nodeInputPortCollision = false;
                            boolean nodeOutputPortCollision = false;
                            
                            if (nodeOutputPort != null && node2InputPort != null
                                    && nodeOutputPort.id < node2InputPort.id) {
                                crossings++;
                                nodeOutputPortCollision = true;
                            }
                            if (nodeInputPort != null && node2OutputPort != null
                                    && nodeInputPort.id > node2OutputPort.id) {
                                crossings++;
                                nodeInputPortCollision = true;
                            }
                            if (nodeOutputPort != null && node2OutputPort != null
                                    && nodeOutputPort.id > node2OutputPort.id) {
                                crossings++;
                                nodeOutputPortCollision = true;
                            }
                            if (nodeInputPort != null && node2InputPort != null
                                    && nodeInputPort.id < node2InputPort.id) {
                                crossings++;
                                nodeInputPortCollision = true;
                            }
                            
                            if (nodeInputPortCollision && nodeOutputPortCollision
                                    && nodeInputPort == nodeOutputPort) {
                                crossings--;
                            }
                        } else {
                            boolean node2InputPortCollision = false;
                            boolean node2OutputPortCollision = false;
                            
                            if (nodeInputPort != null && node2OutputPort != null
                                    && nodeInputPort.id < node2OutputPort.id) {
                                crossings++;
                                node2OutputPortCollision = true;
                            }
                            if (nodeOutputPort != null && node2InputPort != null
                                    && nodeOutputPort.id > node2InputPort.id) {
                                crossings++;
                                node2InputPortCollision = true;
                            }
                            if (nodeInputPort != null && node2InputPort != null
                                    && nodeInputPort.id < node2InputPort.id) {
                                crossings++;
                                node2InputPortCollision = true;
                            }
                            if (nodeOutputPort != null && node2OutputPort != null
                                    && nodeOutputPort.id > node2OutputPort.id) {
                                crossings++;
                                node2OutputPortCollision = true;
                            }
                            
                            if (node2InputPortCollision && node2OutputPortCollision
                                    && node2InputPort == node2OutputPort) {
                                crossings--;
                            }
                        }
                    }
                }
            }
        }
        
        return crossings;
    }

}
