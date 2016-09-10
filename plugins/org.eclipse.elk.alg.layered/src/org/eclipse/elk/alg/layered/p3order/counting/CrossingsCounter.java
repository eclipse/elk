/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order.counting;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.Pair;

import com.google.common.collect.Lists;

/**
 * Counts in-layer and between layer crossings. Does not count North-South crossings.
 *
 * @author alan
 */
public final class CrossingsCounter {
    private final int[] portPositions;

    private BinaryIndexedTree indexTree;
    private final Deque<Integer> ends;

    private Map<LNode, Integer> nodeCardinalities;

    /**
     * Create crossings counter.
     * 
     * @param portPositions
     *            port position array passed to prevent frequent large array construction.
     */
    public CrossingsCounter(final int[] portPositions) {
        this.portPositions = portPositions;
        ends = new ArrayDeque<>();
        nodeCardinalities = new HashMap<>();
    }

    /*
     * Between-layer crossings become in-layer crossings if we fold and rotate the right layer downward and
     * pretend that we are in a single layer. For example:
     * <pre>
     * 0  3
     *  \/
     *  /\
     * 1  2
     * becomes:
     * 0--
     * 1-+-|
     *   | |
     * 2-- |
     * 3----
     * Ta daaa!
     * </pre>
    */
    /**
     * Count in-layer and between-layer crossings between the two given layers.
     *
     * @param leftLayerNodes
     *            left layer
     * @param rightLayerNodes
     *            right layer
     * @return number of crossings.
     */
    public int countCrossingsBetweenLayers(final LNode[] leftLayerNodes,
            final LNode[] rightLayerNodes) {
        List<LPort> ports = setPositionsCounterClockwise(leftLayerNodes, rightLayerNodes);
        indexTree = new BinaryIndexedTree(ports.size());
        return countCrossingsOnPorts(ports);
    }

    /*
     * The aim was to count in-layer edges in O(n log n). We step through each edge and add the
     * position of the end of the edge to a sorted list. Each time we meet the same edge again,
     * we delete it from the list again. Each time we add an edge end position, the number of crossings
     * is the index of the this position in the sorted list. The implementation of this list
     * guarantees that adding, deleting and finding indices is log n.
     * @formatter:off
     *           List
     * 0--       [2]
     * 1-+-|     [2,3]
     *   | |
     * 2-- |     [3]
     * 3----     []
     * @formatter:on
     */
    /**
     * Only count in-layer crossings on the given side.
     *
     * @param nodes
     *            order of nodes in layer in question.
     * @param side
     *            the side
     * @return number of crossings.
     */
    public int countInLayerCrossingsOnSide(final LNode[] nodes, final PortSide side) {
        List<LPort> ports = initPortPositionsForInLayerCrossings(nodes, side);

        return countInLayerCrossingsOnPorts(ports);
    }

    /**
     * Count crossings caused between edges incident to upperPort and lowerPort and when the order of these two is
     * switched.
     * 
     * @param upperPort
     *            the upper port
     * @param lowerPort
     *            the lower port
     * @return {@link Pair} of integers where {@link Pair#getFirst()} returns the crossings in the unswitched and
     *         {@link Pair#getSecond()} in the switched order.
     */
    public Pair<Integer, Integer> countCrossingsBetweenPortsInBothOrders(final LPort upperPort,
            final LPort lowerPort) {
        List<LPort> ports = connectedPortsSortedByPosition(upperPort, lowerPort);
        int upperLowerCrossings = countCrossingsOnPorts(ports);
        // Since we might add endpositions of ports which are not in the ports list, we need to explicitly clear
        // the index tree.
        indexTree.clear();
        switchPorts(upperPort, lowerPort);
        Collections.sort(ports, (a, b) -> Integer.compare(positionOf(a), positionOf(b)));
        int lowerUpperCrossings = countCrossingsOnPorts(ports);
        indexTree.clear();
        switchPorts(lowerPort, upperPort);
        return Pair.of(upperLowerCrossings, lowerUpperCrossings);
    }

    /**
     * Count crossings caused between edges incident to upperNode and lowerNode and when the order of these two is
     * switched.
     * 
     * @param upperNode
     *            the upper node
     * @param lowerNode
     *            the lower node
     * @param side
     *            the side on which to count
     * @return {@link Pair} of integers where {@link Pair#getFirst()} returns the crossings in the unswitched and
     *         {@link Pair#getSecond()} in the switched order.
     */
    public Pair<Integer, Integer> countInLayerCrossingsBetweenNodesInBothOrders(final LNode upperNode,
            final LNode lowerNode, final PortSide side) {
        List<LPort> ports = connectedInLayerPortsSortedByPosition(upperNode, lowerNode, side);
        int upperLowerCrossings = countInLayerCrossingsOnPorts(ports);
        switchNodes(upperNode, lowerNode, side);
        // Since we might add endpositions of ports which are not in the ports list, we need to explicitly clear
        // the index tree.
        indexTree.clear();
        Collections.sort(ports, (a, b) -> Integer.compare(positionOf(a), positionOf(b)));
        int lowerUpperCrossings = countInLayerCrossingsOnPorts(ports);
        switchNodes(lowerNode, upperNode, side);
        indexTree.clear();
        return Pair.of(upperLowerCrossings, lowerUpperCrossings);
    }

    /**
     * Initializes the counter for counting crosses on a specific side of two layers.
     *
     * @param leftLayerNodes
     *            Nodes in western layer.
     * @param rightLayerNodes
     *            Nodes in eastern layer.
     */
    public void initForCountingBetweenOnSide(final LNode[] leftLayerNodes, final LNode[] rightLayerNodes) {
        List<LPort> ports = setPositionsCounterClockwise(leftLayerNodes, rightLayerNodes);
        indexTree = new BinaryIndexedTree(ports.size());
    }

    /**
     * Initializes the counter for counting in-layer crossings on a specific side of a single layer.
     * 
     * @param nodes
     *            The order of the nodes in the layer
     * @param side
     *            The side to initialize
     * @return the ports on which to count crossings on
     */
    public List<LPort> initPortPositionsForInLayerCrossings(final LNode[] nodes, final PortSide side) {
        List<LPort> ports = new ArrayList<>();
        initPositions(nodes, ports, side, true, true);
        indexTree = new BinaryIndexedTree(ports.size());
        return ports;
    }

    /**
     * Notify counter of port switch.
     *
     * @param topPort
     *            The port previously further north.
     * @param bottomPort
     *            The port previously further south.
     */
    public void switchPorts(final LPort topPort, final LPort bottomPort) {
        int topPortPos = portPositions[topPort.id];
        portPositions[topPort.id] = portPositions[bottomPort.id];
        portPositions[bottomPort.id] = topPortPos;
    }

    /**
     * This method should be used as soon as neighboring nodes have been switched. Use the first parameter to pass which
     * node was the upper node before a switch and the second to pass the former lower node. We assume a left-right
     * layout.
     * 
     * @param wasUpperNode
     *            The node which was the upper node before switching.
     * @param wasLowerNode
     *            The node which was the lower node before switching.
     * @param side
     *            The side on which the crossings are currently being counted.
     */
    public void switchNodes(final LNode wasUpperNode, final LNode wasLowerNode, final PortSide side) {
        Iterable<LPort> ports = CrossMinUtil.inNorthSouthEastWestOrder(wasUpperNode, side);
        for (LPort port : ports) {
            portPositions[port.id] = positionOf(port) + nodeCardinalities.get(wasLowerNode);
        }
        
        ports = CrossMinUtil.inNorthSouthEastWestOrder(wasLowerNode, side);
        for (LPort port : ports) {
            portPositions[port.id] =  positionOf(port) - nodeCardinalities.get(wasUpperNode);
        }
    }

    private List<LPort> connectedInLayerPortsSortedByPosition(final LNode upperNode, final LNode lowerNode,
            final PortSide side) {
        Set<LPort> ports = new TreeSet<>((a, b) -> Integer.compare(positionOf(a), positionOf(b)));
        for (LNode node : Arrays.asList(upperNode, lowerNode)) {
            for (LPort port : CrossMinUtil.inNorthSouthEastWestOrder(node, side)) {
                for (LEdge edge : port.getConnectedEdges()) {
                    if (!edge.isSelfLoop()) {
                        ports.add(port);
                        if (isInLayer(edge)) {
                            ports.add(otherEndOf(edge, port));
                        }
                    }
                }
            }
        }
        return Lists.newArrayList(ports);
    }

    private List<LPort> connectedPortsSortedByPosition(final LPort upperPort, final LPort lowerPort) {
        Set<LPort> ports = new TreeSet<>((a, b) -> Integer.compare(positionOf(a), positionOf(b)));
        for (LPort port : new LPort[] { upperPort, lowerPort }) {
            ports.add(port);
            for (LEdge edge : port.getConnectedEdges()) {
                if (!isPortSelfLoop(edge)) {
                    ports.add(otherEndOf(edge, port));
                }
            }
        }
        return Lists.newArrayList(ports);
    }

    private boolean isPortSelfLoop(final LEdge edge) {
        return edge.getSource() == edge.getTarget();
    }

    private int countCrossingsOnPorts(final Iterable<LPort> ports) {
        int crossings = 0;
        for (LPort port : ports) {
            indexTree.removeAll(positionOf(port));
            // First get crossings for all edges.
            for (LEdge edge : port.getConnectedEdges()) {
                int endPosition = positionOf(otherEndOf(edge, port));
                if (endPosition > positionOf(port)) {
                    crossings += indexTree.rank(endPosition);
                    ends.push(endPosition);
                }
            }
            // Then add end points.
            while (!ends.isEmpty()) {
                indexTree.add(ends.pop());
            }
        }

        return crossings;
    }

    private int countInLayerCrossingsOnPorts(final Iterable<LPort> ports) {
        int crossings = 0;
        for (LPort port : ports) {
            indexTree.removeAll(positionOf(port));
            int numBetweenLayerEdges = 0;
            int size = indexTree.size();
            // First get crossings for all edges.
            for (LEdge edge : port.getConnectedEdges()) {
                if (isInLayer(edge)) {
                    int endPosition = positionOf(otherEndOf(edge, port));
                    if (endPosition > positionOf(port)) {
                        crossings += indexTree.rank(endPosition);
                        ends.push(endPosition);
                    }
                } else {
                    numBetweenLayerEdges++;
                }
            }
            crossings += size * numBetweenLayerEdges;
            // Then add end points.
            while (!ends.isEmpty()) {
                indexTree.add(ends.pop());
            }
        }
        return crossings;
    }
   
    private boolean isInLayer(final LEdge edge) {
        Layer sourceLayer = edge.getSource().getNode().getLayer();
        Layer targetLayer = edge.getTarget().getNode().getLayer();
        return sourceLayer == targetLayer;
    }

    private int positionOf(final LPort port) {
        return portPositions[port.id];
    }

    private LPort otherEndOf(final LEdge edge, final LPort fromPort) {
        return fromPort == edge.getSource() ? edge.getTarget() : edge.getSource();
    }

    // remove unneeded options
    private void initPositions(final LNode[] nodes, final List<LPort> ports,
            final PortSide side, final boolean topDown, final boolean getCardinalities) {
        int numPorts = ports.size();
        for (int i = start(nodes, topDown); end(i, topDown, nodes); i += step(topDown)) {
            LNode node = nodes[i];
            List<LPort> nodePorts = getPorts(node, side, topDown);
            if (getCardinalities) {
                nodeCardinalities.put(node, nodePorts.size());
            }
            for (LPort port : nodePorts) {
                portPositions[port.id] = numPorts++;
            }
            ports.addAll(nodePorts);
        }
    }

    private List<LPort> getPorts(final LNode node, final PortSide side, final boolean topDown) {
        if (side == PortSide.EAST) {
            if (topDown) {
                return node.getPortSideView(side);
            } else {
                return Lists.reverse(node.getPortSideView(side));
            }
        } else if (topDown) {
            return Lists.reverse(node.getPortSideView(side));
        } else {
            return node.getPortSideView(side);
        }
    }

    private int start(final LNode[] nodes, final boolean topDown) {
        return topDown ? 0 : nodes.length - 1;
    }
    private boolean end(final int i, final boolean topDown, final LNode[] nodes) {
        return topDown ? i < nodes.length : i >= 0;
    }
    
    private int step(final boolean topDown) {
        return topDown ? 1 : -1;
    }

    private List<LPort> setPositionsCounterClockwise(final LNode[] leftLayerNodes,
            final LNode[] rightLayerNodes) {
        List<LPort> ports = new ArrayList<>();
        initPositions(leftLayerNodes, ports, PortSide.EAST, true, false);
        initPositions(rightLayerNodes, ports, PortSide.WEST, false, false);
        return ports;
    }

    /**
     * Return portPositions of this counter.
     * 
     * @return portPositions
     */
    public int[] getPortPositions() {
        return portPositions;
    }
}
