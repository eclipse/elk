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
import java.util.Arrays;
import java.util.Deque;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Counts in-layer and between layer crossings. Does not count North-South crossings.
 *
 * @author alan TODO-alan generalize init and count by passing iterable.
 */
public final class CrossingsCounter {
    private final int[] portPositions;
    private BinaryPrefixTree indexTree;
    private final Deque<Integer> ends;

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
        Iterable<LNode> ns = Arrays.asList(nodes);
        Iterable<LPort> ports = side == PortSide.EAST ? joinPorts(ns, side) : joinReversePorts(ns, side);
        int numPorts = 0;
        for (LPort port : ports) {
            portPositions[port.id] = numPorts++;
        }

        indexTree = new BinaryPrefixTree(numPorts);

        return countCrossingsOnPorts(ports);
    }

    /*
     * Between-layer crossings become in-layer crossings if we fold the right layer downward and
     * pretend that we are in a single layer. For example:
     * @formatter:off
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
     * @formatter:on
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
        Iterable<LNode> leftLayer = Arrays.asList(leftLayerNodes);
        Iterable<LNode> rightLayerReverse = Lists.reverse(Arrays.asList(rightLayerNodes));

        setPortPositions(PortSide.EAST, leftLayer, rightLayerReverse);

        return countCrossings(PortSide.EAST, leftLayer, rightLayerReverse);
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
        Iterable<LPort> ports = sideToCountOn == PortSide.EAST
                ? counterClockWisePorts(leftLayerNodes, rightLayerNodes)
                : clockWisePorts(leftLayerNodes, rightLayerNodes);
    
        int numPorts = 0;
        for (LPort port : ports) {
            portPositions[port.id] = numPorts++;
        }
        indexTree = new BinaryPrefixTree(numPorts);
    }

    @SafeVarargs
    private final void setPortPositions(final PortSide side, final Iterable<LNode>... layers) {
        int currentPortPos = 0;
        PortSide s = side;
        for (Iterable<LNode> layer : layers) {
            for (LNode node : layer) {
                for (LPort port : node.getPorts(s)) {
                    portPositions[port.id] = currentPortPos++;
                }
            }
            s = s.opposed();
        }
        indexTree = new BinaryPrefixTree(currentPortPos);
    }

    @SafeVarargs
    private final int countCrossings(final PortSide firstSide, final Iterable<LNode>... layers) {
        int crossings = 0;
        PortSide s = firstSide;
        for (Iterable<LNode> nodes : layers) {
            for (LNode node : nodes) {
                crossings += countCrossingsOnPorts(node.getPorts(s));
            }
            s = s.opposed();
        }
        return crossings;
    }

    private int countCrossingsOnPorts(final Iterable<LPort> ports) {
        int crossings = 0;
        for (LPort port : ports) {
            indexTree.removeAll(positionOf(port));
            // First get crossings for all edges.
            for (LEdge edge : port.getConnectedEdges()) {
                if (isSelfLoop(edge)) {
                    continue;
                }
                if (pointsDownward(edge, port)) {
                    int endPosition = positionOf(otherEndOf(edge, port));
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

    private boolean pointsDownward(final LEdge edge, final LPort port) {
        return positionOf(otherEndOf(edge, port)) > positionOf(port);
    }

    private boolean isSelfLoop(final LEdge edge) {
        return edge.getSource().getNode() == edge.getTarget().getNode();
    }

    private int positionOf(final LPort port) {
        return portPositions[port.id];
    }

    private LPort otherEndOf(final LEdge edge, final LPort fromPort) {
        return fromPort == edge.getSource() ? edge.getTarget() : edge.getSource();
    }

    private Iterable<LPort> clockWisePorts(final LNode[] leftLayerNodes, final LNode[] rightLayerNodes) {
        Iterable<LPort> rightPorts = joinReversePorts(Arrays.asList(rightLayerNodes), PortSide.WEST);
        Iterable<LPort> leftPorts = joinReversePorts(Lists.reverse(Arrays.asList(leftLayerNodes)), PortSide.EAST);
        return Iterables.concat(rightPorts, leftPorts);
    }

    private Iterable<LPort> joinReversePorts(final Iterable<LNode> nodes, final PortSide side) {
        return Iterables.concat(Iterables.transform(nodes, n -> Lists.reverse(n.getPorts(side))));
    }

    private Iterable<LPort> counterClockWisePorts(final LNode[] leftLayerNodes, final LNode[] rightLayerNodes) {
        Iterable<LPort> leftPorts = joinPorts(Arrays.asList(leftLayerNodes), PortSide.EAST);
        Iterable<LPort> rightPorts = joinPorts(Lists.reverse(Arrays.asList(rightLayerNodes)), PortSide.WEST);
        return Iterables.concat(leftPorts, rightPorts);
    }

    private Iterable<LPort> joinPorts(final Iterable<LNode> nodes, final PortSide side) {
        return Iterables.concat(Iterables.transform(nodes, n -> n.getPorts(side)));
    }

}
