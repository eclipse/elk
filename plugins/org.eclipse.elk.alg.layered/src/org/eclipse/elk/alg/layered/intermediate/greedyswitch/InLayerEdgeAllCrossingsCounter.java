/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.greedyswitch;

import java.util.Map;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.BoundType;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

/**
 * In-layer edge crossing counter for all crossings in a layer. The subclass
 * {@link InLayerEdgeTwoNodeCrossingCounter} accesses the {@link #countCrossingsOn(LEdge, LPort)}
 * method with only the nodes and edges relevant to the two nodes in question.
 * 
 * @author alan
 */
public class InLayerEdgeAllCrossingsCounter {
    /** The number of inLayerEdges incident to each node from the east. */
    private final Map<LNode, Integer> eastNodeCardinalities;
    /** The number of inLayerEdges incident to each node from the west. */
    private final Map<LNode, Integer> westNodeCardinalities;
    private final Map<LPort, Integer> portPositions;
    private final LNode[] nodeOrder;
    /** We store port-positions in mutlisets, as nodes without fixed order have the same port ids. */
    private final SortedMultiset<Integer> inLayerPorts;
    private final Set<LEdge> inLayerEdges;

    /**
     * Create InLayerEdgeAllCrossingsCounter.
     * 
     * @param nodeOrder
     *            the current node order.
     */
    public InLayerEdgeAllCrossingsCounter(final LNode[] nodeOrder) {
        eastNodeCardinalities = Maps.newHashMap();
        westNodeCardinalities = Maps.newHashMap();
        portPositions = Maps.newHashMap();
        inLayerEdges = Sets.newHashSet();
        inLayerPorts = TreeMultiset.create();
        this.nodeOrder = nodeOrder;
        initializeLayer(nodeOrder);
    }

    private void initializeLayer(final LNode[] layer) {
        int eastPortId = 0;
        int westPortId = 0;
        for (LNode node : layer) {
            eastPortId =
                    setPortIdsAndNodeCardinality(eastPortId, node, PortSide.EAST,
                            eastNodeCardinalities);
            westPortId =
                    setPortIdsAndNodeCardinality(westPortId, node, PortSide.WEST,
                            westNodeCardinalities);
        }
    }

    private int setPortIdsAndNodeCardinality(final int portId, final LNode node,
            final PortSide side, final Map<LNode, Integer> cardinalities) {
        
        int currentPortId = portId;
        int cardinality = 0;
        boolean hasPorts = false;
        
        Iterable<LPort> ports = PortIterable.inNorthSouthEastWestOrder(node, side);
        for (LPort port : ports) {
            hasPorts = true;
            portPositions.put(port, currentPortId);
            // Ports whose order on the node is not set have the same id.
            if (portOrderIsFixedFor(node) || port.getDegree() > 1) {
                cardinality++;
                currentPortId++;
            }
        }
        
        if (!portOrderIsFixedFor(node) && hasPorts) {
            cardinality++;
            currentPortId++;
        }
        cardinalities.put(node, cardinality);
        return currentPortId;
    }

    private boolean portOrderIsFixedFor(final LNode node) {
        return node.getProperty(LayeredOptions.PORT_CONSTRAINTS).isOrderFixed();
    }

    /**
     * Counts all in-layer crossings.
     * 
     * @return in-layer crossings
     */
    public int countCrossings() {
        int crossings = 0;
        crossings = iterateEdgesTopDownAndCountCrossingsOnSide(PortSide.WEST);
        crossings += iterateEdgesTopDownAndCountCrossingsOnSide(PortSide.EAST);
        return crossings;
    }

    private int iterateEdgesTopDownAndCountCrossingsOnSide(final PortSide portSide) {
        int crossings = 0;
        for (LNode node : nodeOrder) {
            Iterable<LPort> ports = PortIterable.inNorthSouthEastWestOrder(node, portSide);
            for (LPort port : ports) {
                for (LEdge edge : port.getConnectedEdges()) {
                    if (!edge.isSelfLoop()) {
                        crossings += countCrossingsOn(edge, port);
                    }
                }
            }
        }

        return crossings;
    }

    /**
     * This is the main method called for one edge and port both for counting all in-layer crossings
     * and for counting in-layer crossings between two nodes. <br>
     * The algorithm works the following way: We step through each port: If the connected edge is a
     * between-layer edge, we add the position of the port on this layer to the sorted list of
     * ports. Each time we meet an edge which has been visited, we count all port position in
     * between the end and start position of this edge and delete it from the list.<br>
     * Note that on nodes with free port order, all ports have the same port position.
     * 
     * @param edge
     *            The current edge.
     * @param port
     *            connected to this port currently being visited.
     * @return amount of crossings caused.
     */
    protected int countCrossingsOn(final LEdge edge, final LPort port) {
        int crossings = 0;
        if (isInLayer(edge)) {
            if (notVisited(edge)) {
                add(edge);
            } else {
                remove(edge);
                crossings += numberOfPortsInBetweenEndsOf(edge, inLayerPorts);
            }
        } else { // is in-between layer edge
            int portsOnNodeWithFreePortOrder = inLayerPorts.count(positionOf(port));
            crossings += inLayerEdges.size() - portsOnNodeWithFreePortOrder;
        }
        return crossings;
    }

    private boolean notVisited(final LEdge edge) {
        return !inLayerEdges.contains(edge);
    }

    private int numberOfPortsInBetweenEndsOf(final LEdge edge, final SortedMultiset<Integer> set) {
        int lowerBound = Math.min(positionOf(edge.getTarget()), positionOf(edge.getSource()));
        int upperBound = Math.max(positionOf(edge.getTarget()), positionOf(edge.getSource()));
        return set.subMultiset(lowerBound, BoundType.OPEN, upperBound, BoundType.OPEN).size();
    }

    private void remove(final LEdge edge) {
        inLayerPorts.remove(positionOf(edge.getSource()));
        inLayerPorts.remove(positionOf(edge.getTarget()));
        inLayerEdges.remove(edge);
    }

    private void add(final LEdge edge) {
        inLayerEdges.add(edge);
        inLayerPorts.add(positionOf(edge.getSource()));
        inLayerPorts.add(positionOf(edge.getTarget()));
    }

    /**
     * Whether edge is inlayer edge or not.
     * 
     * @param edge
     *            in question
     * @return true when edge is in-layer.
     */
    protected boolean isInLayer(final LEdge edge) {
        Layer sourceLayer = edge.getSource().getNode().getLayer();
        Layer targetLayer = edge.getTarget().getNode().getLayer();
        return sourceLayer == targetLayer;
    }

    /**
     * Returns position of current port.
     * 
     * @param port
     *            in question.
     * @return position value.
     */
    protected int positionOf(final LPort port) {
        return portPositions.get(port);
    }

    /**
     * This method should be used as soon as neighboring nodes have been switched. Use the first
     * parameter to pass which node was the upper node before a switch and the second to pass the
     * former lower node. We assume a left-right layout.
     * 
     * @param wasUpperNode
     *            The node which was the upper node before switching.
     * @param wasLowerNode
     *            The node which was the lower node before switching.
     */
    public void notifyOfSwitch(final LNode wasUpperNode, final LNode wasLowerNode) {
        updatePortIds(wasUpperNode, wasLowerNode, PortSide.EAST, eastNodeCardinalities);
        updatePortIds(wasUpperNode, wasLowerNode, PortSide.WEST, westNodeCardinalities);
    }

    private void updatePortIds(final LNode firstNode, final LNode secondNode, final PortSide side,
            final Map<LNode, Integer> cardinalities) {
        
        Iterable<LPort> ports = PortIterable.inNorthSouthEastWestOrder(firstNode, side);
        for (LPort port : ports) {
            portPositions.put(port, positionOf(port) + cardinalities.get(secondNode));
        }
        
        ports = PortIterable.inNorthSouthEastWestOrder(secondNode, side);
        for (LPort port : ports) {
            portPositions.put(port, positionOf(port) - cardinalities.get(firstNode));
        }
    }
}
