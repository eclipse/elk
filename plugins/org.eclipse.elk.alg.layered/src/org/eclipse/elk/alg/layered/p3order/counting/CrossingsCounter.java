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
import java.util.Iterator;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * Counts in-layer and between layer crossings. Does not count North-South crossings.
 *
 * @author alan TODO-alan specialize to only fixed and always both in and between layer case.
 */
public final class CrossingsCounter {
    private final int[] portPositions;
    private FenwickTree indexTree;
    private int numPorts;
    final Deque<Integer> ends;

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
        Iterable<LNode> nodesIter = () -> Iterators.forArray(nodes);
        numPorts = setPortPositions(side, nodesIter, 0);
        indexTree = new FenwickTree(numPorts);
        return addEdgesAndCountCrossingsOn(side, nodesIter);
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

        Iterable<LNode> leftLayerIter = () -> Iterators.forArray(leftLayerNodes);
        Iterable<LNode> rightLayerReverseIter = () -> descendingIterator(rightLayerNodes);

        numPorts = setPortPositions(PortSide.EAST, leftLayerIter, 0);
        numPorts = setPortPositions(PortSide.WEST, rightLayerReverseIter, numPorts);
        indexTree = new FenwickTree(numPorts);

        int crossings = addEdgesAndCountCrossingsOn(PortSide.EAST, leftLayerIter);
        crossings += addEdgesAndCountCrossingsOn(PortSide.WEST, rightLayerReverseIter);

        return crossings;
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

        numPorts = 0;
        for (LPort port : ports) {
            portPositions[port.id] = numPorts++;
        }

        indexTree = new FenwickTree(numPorts);
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

    /**
     * Count crossings between two ports. Before using this method, the layers must be initialized beforehand.
     * @param portOne
     * @param portTwo
     * @return
     */
    public int countCrossingsBetweenPorts(final LPort portOne, final LPort portTwo) {
        assert portOne.getSide() == portTwo.getSide();
        indexTree = new FenwickTree(numPorts);
        return countCrossingsOnPorts(Lists.newArrayList(portOne, portTwo));
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
        Iterable<LPort> ports = Iterables.concat(PortIterable.inCounterClockwiseOrder(upperNode, side),
                PortIterable.inCounterClockwiseOrder(lowerNode, side));
        indexTree = new FenwickTree(numPorts);
        return countCrossingsOnPorts(ports);
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


    private int setPortPositions(final PortSide side, final Iterable<LNode> layer, final int startPos) {
        int currentPortPos = startPos;
        for (LNode node : layer) {
            Iterable<LPort> ports = node.getPorts(side);
            for (LPort port : ports) {
                portPositions[port.id] = currentPortPos++;
            }
        }
        return currentPortPos;
    }

    private int addEdgesAndCountCrossingsOn(final PortSide side, final Iterable<LNode> nodes) {
        int crossings = 0;
        for (LNode node : nodes) {
            crossings += countCrossingsOnPorts(node.getPorts(side));
        }
        return crossings;
    }

    private int countCrossingsOnPorts(final Iterable<LPort> ports) {
        int crossings = 0;
        for (LPort port : ports) {
            // First get crossings for all edges
            for (LEdge edge : port.getConnectedEdges()) {
                if (isSelfLoop(edge)) {
                    continue;
                }
                if (pointsDownward(edge, port)) {
                    int endPosition = positionOf(otherEndOf(edge, port));
                    crossings += indexTree.sumBefore(endPosition);
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

    private Iterator<LNode> descendingIterator(final LNode[] rightLayerOrd) {
        return new Iterator<LNode>() {
            private final LNode[] ord = rightLayerOrd;
            private int i = ord.length - 1;

            @Override
            public boolean hasNext() {
                return i >= 0;
            }

            @Override
            public LNode next() {
                return ord[i--];
            }
        };
    }
}
