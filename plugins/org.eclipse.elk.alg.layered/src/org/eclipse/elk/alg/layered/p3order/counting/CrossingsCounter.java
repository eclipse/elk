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

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.core.options.CoreOptions;
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
    private final boolean assumeFixedPortOrder;
    private final boolean assumeCompoundNodePortOrderFixed;
    private boolean countBothInAndBetweenLayerCrossings;
    private int numPorts;

    private CrossingsCounter(final int[] portPositions,
            final boolean assumeFixedPortOrder, final boolean assumeCompoundNodePortOrderFixed) {
        this.assumeFixedPortOrder = assumeFixedPortOrder;
        this.assumeCompoundNodePortOrderFixed = assumeCompoundNodePortOrderFixed;
        this.portPositions = portPositions;

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
        countBothInAndBetweenLayerCrossings = false;
        numPorts = setPortPositions(side, nodesIter, 0);
        indexTree = new FenwickTree(numPorts);
        return addEdgesAndCountCrossingsOn(side, nodesIter);
    }

    /**
     * Only count in-layer crossings on both sides of a layer.
     *
     * @param nodes
     *            order of nodes in layer in question.
     * @return number of crossings.
     */
    public int countInLayerCrossingsOnBothSides(final LNode[] nodes) {
        return countInLayerCrossingsOnSide(nodes, PortSide.EAST)
                + countInLayerCrossingsOnSide(nodes, PortSide.WEST);
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
        countBothInAndBetweenLayerCrossings = true;

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
        countBothInAndBetweenLayerCrossings = true;

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
        return countWithFixedPortOrder(Lists.newArrayList(portOne, portTwo));
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
        return countWithFixedPortOrder(ports);
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

            boolean hasPorts = false;

            Iterable<LPort> ports = countBothInAndBetweenLayerCrossings ? node.getPorts(side)
                    : PortIterable.inNorthSouthEastWestOrder(node, side);

            for (LPort port : ports) {
                hasPorts = true;
                portPositions[port.id] = currentPortPos;
                // Ports whose order on the node is not set have the same id.
                if (portOrderIsFixedFor(node)) {
                    currentPortPos++;
                }
            }

            if (!portOrderIsFixedFor(node) && hasPorts) {
                currentPortPos++;
            }
        }
        return currentPortPos;
    }

    private int addEdgesAndCountCrossingsOn(final PortSide side, final Iterable<LNode> layer) {
        int crossings = 0;

        for (LNode node : layer) {
            Iterable<LPort> ports =
                    countBothInAndBetweenLayerCrossings ? PortIterable.inClockwiseOrder(node, side)
                    : PortIterable.inNorthSouthEastWestOrder(node, side);

            if (portOrderIsFixedFor(node)) {
                crossings += countWithFixedPortOrder(ports);
            } else {
                crossings += countWithFreePortOrder(ports);
            }
        }
        return crossings;
    }

    /**
     * Assumes that crossings caused by port order on nodes with free port order can always be
     * removed by port sorting and therefore don't need to be counted. Note that this is not always
     * true!
     *
     * @param ports
     *            the ports
     * @return crossings
     */
    private int countWithFreePortOrder(final Iterable<LPort> ports) {
        int crossings = 0;
        // First get all crossings
        // TODO-alan explain why in two steps.
        for (LPort port : ports) {
            indexTree.removeAll(positionOf(port));
            crossings += getCrossingsOfEdges(port);
        }

        // Must get this before adding edges
        int size = indexTree.size();

        // Then add edges
        int numBetweenLayerEdges = 0;
        for (LPort port : ports) {
            numBetweenLayerEdges += addTargetsCountingBetweenLayerEdges(port);
        }

        crossings += numBetweenLayerEdges * size;
        return crossings;
    }

    private int countWithFixedPortOrder(final Iterable<LPort> ports) {
        int crossings = 0;
        for (LPort port : ports) {
            // First get crossings for all edges
            indexTree.removeAll(positionOf(port));
            crossings += getCrossingsOfEdges(port);

            // Then add edges
            int sizeBeforeHand = indexTree.size();
            int numBetweenLayerEdges = addTargetsCountingBetweenLayerEdges(port);
            crossings += numBetweenLayerEdges * sizeBeforeHand;
        }
        return crossings;
    }

    private int getCrossingsOfEdges(final LPort port) {
        int crossings = 0;
        for (LEdge edge : port.getConnectedEdges()) {
            if (isSelfLoop(edge)) {
                continue;
            }
            if ((countBothInAndBetweenLayerCrossings || isInLayer(edge)) && pointsDownward(edge, port)) {
                crossings += indexTree.sumBefore(positionOf(otherEndOf(edge, port)));
            }
        }
        return crossings;
    }

    private int addTargetsCountingBetweenLayerEdges(final LPort port) {
        int numBetweenLayerEdges = 0;
        for (LEdge edge : port.getConnectedEdges()) {
            if (isSelfLoop(edge)) {
                continue;
            }
            if (countBothInAndBetweenLayerCrossings || isInLayer(edge)) {
                if (pointsDownward(edge, port)) {
                    indexTree.add(positionOf(otherEndOf(edge, port)));
                }
            } else {
                numBetweenLayerEdges++;
            }
        }
        return numBetweenLayerEdges;
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

    private boolean isInLayer(final LEdge edge) {
        Layer sourceLayer = edge.getSource().getNode().getLayer();
        Layer targetLayer = edge.getTarget().getNode().getLayer();
        return sourceLayer == targetLayer;
    }

    private LPort otherEndOf(final LEdge edge, final LPort fromPort) {
        return fromPort == edge.getSource() ? edge.getTarget() : edge.getSource();
    }

    private boolean portOrderIsFixedFor(final LNode node) {
        return assumeFixedPortOrder || assumeCompoundNodePortOrderFixed && hasNestedGraph(node)
                || node.getProperty(CoreOptions.PORT_CONSTRAINTS).isOrderFixed();
    }

    private boolean hasNestedGraph(final LNode node) {
        return node.getProperty(InternalProperties.NESTED_LGRAPH) != null;
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

    /**
     * Does not assume fixed port order. Crossings between edges connected to node with free port
     * order are assumed to be non-existent. Note that this is not always true.
     *
     * @param portPositions
     *            array the length of the number of ports in the graph.
     *
     * @return the counter
     */
    public static CrossingsCounter create(final int[] portPositions) {
        return new CrossingsCounter(portPositions, false, false);

    }

    /**
     * Assumes fixed port order.
     *
     * @param portPositions
     *            array the length of the number of ports in the graph.
     *
     * @return the counter
     */
    public static CrossingsCounter createAssumingPortOrderFixed(
            final int[] portPositions) {
        return new CrossingsCounter(portPositions, true, false);
    }

}
