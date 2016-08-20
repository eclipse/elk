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
import java.util.Deque;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Iterables;
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

    /**
     * Create crossings counter.
     * 
     * @param portPositions
     *            port position array passed to prevent frequent large array construction.
     */
    public CrossingsCounter(final int[] portPositions) {
        this.portPositions = portPositions;
        ends = new ArrayDeque<>();
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
        List<LPort> ports = oneSidePorts(nodes, side);

        indexTree = new BinaryIndexedTree(ports.size());

        return countCrossingsOnPorts(ports);
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
        List<LPort> ports = getCounterClockwisePortsAndSetPositions(leftLayerNodes, rightLayerNodes);
        indexTree = new BinaryIndexedTree(ports.size());
        return countCrossingsOnPorts(ports);
    }

    /**
     * Count crossings between two ports. Before using this method, the layers must be initialized beforehand.
     * @param portOne
     * @param portTwo
     * @return
     */
    public int countCrossingsBetweenPorts(final LPort portOne, final LPort portTwo) {
        assert portOne.getSide() == portTwo.getSide();
        return countCrossingsOnPorts(Lists.newArrayList(portOne, portTwo));
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
     * Count crossings only between two nodes.
     *
     * @param upperNode
     * @param lowerNode
     * @param side
     * @return
     */
    public int countCrossingsBetweenNodesOnSide(final LNode upperNode, final LNode lowerNode, final PortSide side) {
        Iterable<LPort> ports =
                side == PortSide.EAST ? Iterables.concat(upperNode.getPorts(side), lowerNode.getPorts(side))
                        : Iterables.concat(Lists.reverse(upperNode.getPorts(side)),
                                Lists.reverse(lowerNode.getPorts(side)));
        return countCrossingsOnPorts(ports);
    }

    /**
     * Initializes the counter for counting crosses on a specific side of two layers.
     *
     * @param leftLayerNodes
     *            Nodes in western layer.
     * @param rightLayerNodes
     *            Nodes in eastern layer.
     * @param sideToCountOn
     *            Side on which the crossings are counted.
     */
    public void initForCountingBetweenOnSide(final LNode[] leftLayerNodes, final LNode[] rightLayerNodes,
            final PortSide sideToCountOn) {
        List<LPort> ports = sideToCountOn == PortSide.EAST
                ? getCounterClockwisePortsAndSetPositions(leftLayerNodes, rightLayerNodes)
                : getClockwisePortsAndSetPositions(leftLayerNodes, rightLayerNodes);
        indexTree = new BinaryIndexedTree(ports.size());
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

    private int positionOf(final LPort port) {
        return portPositions[port.id];
    }

    private LPort otherEndOf(final LEdge edge, final LPort fromPort) {
        return fromPort == edge.getSource() ? edge.getTarget() : edge.getSource();
    }

    private List<LPort> getClockwisePortsAndSetPositions(final LNode[] leftLayerNodes, final LNode[] rightLayerNodes) {
        List<LPort> ports = new ArrayList<>();
        int numPorts = 0;
        for (int i = 0; i < rightLayerNodes.length; i++) {
            LNode node = rightLayerNodes[i];
            List<LPort> reverseWestPorts = Lists.reverse(node.getPorts(PortSide.WEST));
            for (LPort port : reverseWestPorts) {
                portPositions[port.id] = numPorts++;
            }
            ports.addAll(reverseWestPorts);
        }
        for (int i = leftLayerNodes.length - 1; i >= 0; i--) {
            LNode node = leftLayerNodes[i];
            List<LPort> reverseEastPorts = Lists.reverse(node.getPorts(PortSide.EAST));
            for (LPort port : reverseEastPorts) {
                portPositions[port.id] = numPorts++;
            }
            ports.addAll(reverseEastPorts);
        }
        return ports;
    }

    private List<LPort> getCounterClockwisePortsAndSetPositions(final LNode[] leftLayerNodes,
            final LNode[] rightLayerNodes) {
        List<LPort> ports = new ArrayList<>((leftLayerNodes.length + rightLayerNodes.length) * 2);
        int numPorts = setEasternPorts(leftLayerNodes, ports, 0);
        setWesternPorts(rightLayerNodes, ports, numPorts);
        return ports;
    }

    private List<LPort> oneSidePorts(final LNode[] nodes, final PortSide side) {
        List<LPort> ports = new ArrayList<>(nodes.length * 2);
        if (side == PortSide.EAST) {
            setEasternPorts(nodes, ports, 0);
        } else {
            setWesternPorts(nodes, ports, 0);
        }
        return ports;
    }

    private void setWesternPorts(final LNode[] rightLayerNodes, final List<LPort> ports, final int numPorts) {
        int nP = numPorts;
        for (int i = rightLayerNodes.length - 1; i >= 0; i--) {
            LNode node = rightLayerNodes[i];
            List<LPort> westPorts = node.getPorts(PortSide.WEST);
            for (LPort port : westPorts) {
                portPositions[port.id] = nP++;
            }
            ports.addAll(westPorts);
        }
    }

    private int setEasternPorts(final LNode[] leftLayerNodes, final List<LPort> ports, final int numPorts) {
        int nP = numPorts;
        for (LNode node : leftLayerNodes) {
            List<LPort> eastPorts = node.getPorts(PortSide.EAST);
            for (LPort port : eastPorts) {
                portPositions[port.id] = nP++;
            }
            ports.addAll(eastPorts);
        }
        return nP;
    }

}
