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
package org.eclipse.elk.alg.layered.intermediate.greedyswitch;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Lists;

/**
 * Counts in-layer and between layer crossings. Does not count North-South crossings.
 *
 * @author alan
 */
public final class InLayerEdgeTwoNodeCrossingCounter {
    private final List<Integer> portPositions;
    private final BinaryIndexedTree indexes;
    private final Deque<Integer> ends;
    private final PortSide side;
    private int upperLowerCrossings;
    private int lowerUpperCrossings;
    private final Map<LNode, Integer> nodeCardinalities;
    /**
     * Create crossings counter.
     * 
     * @param side
     * 
     * @param portPositions
     *            port position array passed to prevent frequent large array construction.
     */
    public InLayerEdgeTwoNodeCrossingCounter(final int freeLayerIndex, final LNode[][] order, final PortSide side) {
        this.side = side;
        this.portPositions = new ArrayList<>();
        ends = new ArrayDeque<>();
        nodeCardinalities = new HashMap<>();
        List<LPort> ports = new ArrayList<>();
        int pos = 0;
        int id = 0;
        for (LNode node : order[freeLayerIndex]) {
            int cardinality = 0;
            boolean hasPorts = false;
            for (LPort port : PortIterable.inNorthSouthEastWestOrder(node, side)) {
                hasPorts = true;
                port.id = id++;
                portPositions.add(pos);
                ports.add(port);
                if (node.getProperty(LayeredOptions.PORT_CONSTRAINTS).isOrderFixed()) {
                    cardinality++;
                    pos++;
                }
            }
            if (hasPorts && !node.getProperty(LayeredOptions.PORT_CONSTRAINTS).isOrderFixed()) {
                cardinality++;
                pos++;
            }
            nodeCardinalities.put(node, cardinality);
        }
        indexes = new BinaryIndexedTree(ports.size());
    }

    /**
     * Count crossings only between two nodes.
     *
     * @param upperNode
     * @param lowerNode
     * @param side
     * @return
     */
    public void countCrossingsBetweenNodes(final LNode upperNode, final LNode lowerNode) {
        List<LPort> ports = addEdgesAndPortsConnectedToNodesAndSort(upperNode, lowerNode);
        upperLowerCrossings = count(ports.iterator());
        
        notifyOfSwitch(upperNode, lowerNode);
        Collections.sort(ports, (a, b) -> Integer.compare(positionOf(a), positionOf(b)));
        lowerUpperCrossings = count(ports.iterator());
        notifyOfSwitch(lowerNode, upperNode);
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
     */
    public void notifyOfSwitch(final LNode wasUpperNode, final LNode wasLowerNode) {
        Iterable<LPort> ports = PortIterable.inNorthSouthEastWestOrder(wasUpperNode, side);
        for (LPort port : ports) {
            portPositions.set(port.id, positionOf(port) + nodeCardinalities.get(wasLowerNode));
        }
        
        ports = PortIterable.inNorthSouthEastWestOrder(wasLowerNode, side);
        for (LPort port : ports) {
            portPositions.set(port.id, positionOf(port) - nodeCardinalities.get(wasUpperNode));
        }
    }

    private List<LPort> addEdgesAndPortsConnectedToNodesAndSort(final LNode upperNode, final LNode lowerNode) {
        Set<LPort> ports = new HashSet<>();
        for (LNode node : Arrays.asList(upperNode, lowerNode)) {
            for (LPort port : PortIterable.inNorthSouthEastWestOrder(node, side)) {
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
        List<LPort> relevantPorts = Lists.newArrayList(ports);
        Collections.sort(relevantPorts, (a, b) -> Integer.compare(positionOf(a), positionOf(b)));
        return relevantPorts;
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
    private int count(final Iterator<LPort> ports) {
        if (!ports.hasNext()) {
            return 0;
        }
        indexes.clear();
        int crossings = 0;
        for (LPort port = ports.next(); ports.hasNext();) {
            indexes.removeAll(positionOf(port));
            int currPos = positionOf(port);
            int numBetweenLayerEdges = 0;
            int size = indexes.size();
            // First get crossings for all edges with same source port position.
            do {
                for (LEdge edge : port.getConnectedEdges()) {
                    if (isInLayer(edge)) {
                        int endPosition = positionOf(otherEndOf(edge, port));
                        if (endPosition > positionOf(port)) {
                            crossings += indexes.rank(endPosition);
                            ends.push(endPosition);
                        }
                    } else {
                        numBetweenLayerEdges++;
                    }
                }
                if (ports.hasNext()) {
                    port = ports.next();
                }
            } while (ports.hasNext() && positionOf(port) == currPos);
            crossings += size * numBetweenLayerEdges;
            // Then add end points to bit.
            while (!ends.isEmpty()) {
                indexes.add(ends.pop());
            }
        }  
        return crossings;
    }

    private int positionOf(final LPort port) {
        return portPositions.get(port.id);
    }

    private boolean isInLayer(final LEdge edge) {
        Layer sourceLayer = edge.getSource().getNode().getLayer();
        Layer targetLayer = edge.getTarget().getNode().getLayer();
        return sourceLayer == targetLayer;
    }

    private LPort otherEndOf(final LEdge edge, final LPort fromPort) {
        return fromPort == edge.getSource() ? edge.getTarget() : edge.getSource();
    }

    /**
     * @return
     */
    public int getUpperLowerCrossings() {
        return upperLowerCrossings;
    }

    /**
     * @return
     */
    public int getLowerUpperCrossings() {
        return lowerUpperCrossings;
    }

    /**
     * Binary Indexed/Fenwick Tree for adding, deleting and finding prefix sums (here: indices) in log n.
     * 
     * @author alan
     *
     */
    private static class BinaryIndexedTree {
        private int[] binarySums;
        private int[] numsPerIndex;
        private int size;
        private int maxNum;

        /**
         * Construct tree given maximum number of elements.
         *
         * @param maxNum
         *            maximum number elements.
         */
        BinaryIndexedTree(final int maxNum) {
            this.maxNum = maxNum;
            binarySums = new int[maxNum + 1];
            numsPerIndex = new int[maxNum];
            size = 0;
        }

        /**
         * Increment given index.
         *
         * @param index
         *            The index to increment.
         */
        public void add(final int index) {
            size++;
            numsPerIndex[index]++;
            int i = index + 1;
            while (i < binarySums.length) {
                binarySums[i]++;
                i += i & -i;
            }
        }

        /**
         * Sum all entries before given index, i.e. index - 1.
         *
         * @param index
         *            Not included end index.
         * @return sum.
         */
        public int rank(final int index) {
            int i = index;
            int sum = 0;
            while (i > 0) {
                sum += binarySums[i];
                i -= i & -i;
            }
            return sum;
        }

        /**
         * Return size of tree.
         *
         * @return size
         */
        public int size() {
            return size;
        }

        /**
         * Remove all entries for one index.
         *
         * @param index
         *            the index
         */
        public void removeAll(final int index) {
            int numEntries = numsPerIndex[index];
            if (numEntries == 0) {
                return;
            }
            numsPerIndex[index] = 0;
            size -= numEntries;
            int i = index + 1;
            while (i < binarySums.length) {
                binarySums[i] -= numEntries;
                i += i & -i;
            }
        }

        /**
         * 
         */
        public void clear() {
            binarySums = new int[maxNum + 1];
            numsPerIndex = new int[maxNum];
            size = 0;
        }
    }

}
