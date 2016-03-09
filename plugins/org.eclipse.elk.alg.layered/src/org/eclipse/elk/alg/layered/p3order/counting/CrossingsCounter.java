/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 *
 * Copyright 2014 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 *
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package org.eclipse.elk.alg.layered.p3order.counting;

import java.util.Iterator;
import java.util.function.Consumer;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.PortIterable;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Iterators;

/**
 * Counts in-layer and between layer crossings. Does not count North-South crossings.
 * 
 * @author alan
 *
 */
public final class CrossingsCounter {
    private final int[] portPositions;
    private SortedIntList indexTree;
    private final boolean assumeFixedPortOrder;
    private final boolean assumeCompoundNodePortOrderFixed;
    private boolean countBothInAndBetweenLayerCrossings;

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
        int numPorts = setPortPositions(side, nodesIter, 0);
        indexTree = new SortedIntList(numPorts);
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

        int numPorts = setPortPositions(PortSide.EAST, leftLayerIter, 0);
        numPorts = setPortPositions(PortSide.WEST, rightLayerReverseIter, numPorts);

        indexTree = new SortedIntList(numPorts);

        int crossings = addEdgesAndCountCrossingsOn(PortSide.EAST, leftLayerIter);
        crossings += addEdgesAndCountCrossingsOn(PortSide.WEST, rightLayerReverseIter);

        return crossings;
    }

    private int setPortPositions(final PortSide side, final Iterable<LNode> leftLayer,
            final int startPos) {
        int currentPortPos = startPos;
        for (LNode node : leftLayer) {

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
                crossings += indexTree.indexOf(positionOf(otherEndOf(edge, port)));
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

    /**
     * Sorted list of integers storing values from 0 up to the maxNumber passed on creation. Adding,
     * removing and indexOf (and addAndIndexOf) is in O(log maxNumber).
     * <p/>
     * Implemented as a binary tree where each leaf stores the number of integers at the leaf index
     * and each node stores the number of values in the left branch of the node.
     * 
     * @author alan
     *
     */
    private static class SortedIntList {
        private final int[] t;
        private int size;
        private int totalTreeCapacity;

        /**
         * Sorted list of integers storing values in range 0 to the maxNumber passed on creation.
         * 
         * @param maxNumber
         *            the maximum value which can be stored in this list.
         */
        public SortedIntList(final int maxNumber) {
            totalTreeCapacity = 1;
            while (totalTreeCapacity < maxNumber) {
                totalTreeCapacity *= 2;
            }
            t = new int[2 * totalTreeCapacity - 1];
            size = 0;
        }

        public void add(final int e) {
            t[leafIndex(e)]++;
            size++;
            traverseNodes(e, i -> t[i]++);
        }

        public void removeAll(final int e) {
            int elemIndex = leafIndex(e);
            int numInstances = t[elemIndex];
            if (numInstances == 0) {
                return;
            }
            t[elemIndex] = 0;
            size -= numInstances;
            traverseNodes(e, i -> t[i] -= numInstances);
        }

        public int indexOf(final int e) {
            // i-> {} == NOOP
            return traverseNodes(e, i -> {
            });
        }

        public int size() {
            return size;
        }

        private int traverseNodes(final int e, final Consumer<Integer> changeNodeValue) {
            int elem = e;
            int i = 0;
            int boundary = totalTreeCapacity / 2;
            int indexOfElem = 0;
            while (i < t.length / 2) {
                if (elem < boundary) {
                    // take left branch
                    changeNodeValue.accept(i);
                    i = 2 * i + 1;
                } else {
                    // take right branch
                    // each node contains the number of elements in the left tree, so we add node
                    // value when taking the right branch.
                    indexOfElem += t[i];
                    i = 2 * i + 2;
                    elem -= boundary;
                }
                boundary /= 2;
            }
            return indexOfElem;
        }

        private int leafIndex(final int id) {
            return totalTreeCapacity - 1 + id;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            int halfList = t.length / 2;
            for (int j = halfList; j < t.length; j++) {
                int i = t[j];
                if (i != 0) {
                    sb.append(i + " x " + (j - halfList) + ", ");
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }
}
