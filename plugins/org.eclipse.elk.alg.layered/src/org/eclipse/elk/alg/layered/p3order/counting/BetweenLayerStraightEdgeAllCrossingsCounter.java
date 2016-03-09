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
package org.eclipse.elk.alg.layered.p3order.counting;

import java.util.ListIterator;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.BetweenLayerEdgeAllCrossingsCounter;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortSide;

/**
 * A very efficient crossings counter that assumes all edges to be drawn as straight lines. The
 * result is exact for normal edges. Taken from
 * <ul>
 * <li>W. Barth , M. Juenger, P. Mutzel. Simple and efficient bilayer cross counting, In <i>Graph
 * Drawing</i>, volume 2528 of LNCS, pp. 331-360. Springer, 2002.</li>
 * </ul>
 * 
 * @author msp
 */
public final class BetweenLayerStraightEdgeAllCrossingsCounter extends
        BetweenLayerEdgeAllCrossingsCounter {


    /**
     * @param nodeOrder
     *            a node order.
     * @param assumeFixedPortOrder
     * @param numPorts
     */
    private BetweenLayerStraightEdgeAllCrossingsCounter(final boolean assumeFixedPortOrder,
            final int numPorts) {
        super(assumeFixedPortOrder, numPorts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countCrossings(final LNode[] leftLayer, final LNode[] rightLayer) {
        // Assign index values to the ports of the right layer
        int targetCount = 0, edgeCount = 0;
        Layer leftLayerRef = leftLayer[0].getLayer();
        Layer rightLayerRef = rightLayer[0].getLayer();
        for (LNode node : rightLayer) {
            assert node.getLayer() == rightLayerRef;
            if (isPortOrderFixed(node)) {
                // Determine how many input ports there are on the north side
                // (note that the standard port order is north - east - south - west)
                int northInputPorts = 0;
                for (LPort port : node.getPorts()) {
                    if (port.getSide() == PortSide.NORTH) {
                        for (LEdge edge : port.getIncomingEdges()) {
                            if (edge.getSource().getNode().getLayer() == leftLayerRef) {
                                northInputPorts++;
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
                // Assign index values in the order north - west - south - east
                int otherInputPorts = 0;
                ListIterator<LPort> portIter = node.getPorts().listIterator(node.getPorts().size());
                while (portIter.hasPrevious()) {
                    LPort port = portIter.previous();
                    int portEdges = 0;
                    for (LEdge edge : port.getIncomingEdges()) {
                        if (edge.getSource().getNode().getLayer() == leftLayerRef) {
                            portEdges++;
                        }
                    }
                    if (portEdges > 0) {
                        if (port.getSide() == PortSide.NORTH) {
                            getPortPos()[port.id] = targetCount;
                            targetCount++;
                        } else {
                            getPortPos()[port.id] = targetCount + northInputPorts + otherInputPorts;
                            otherInputPorts++;
                        }
                        edgeCount += portEdges;
                    }
                }
                targetCount += otherInputPorts;

            } else {
                // All ports are assigned the same index value, since their order does not matter
                int nodeEdges = 0;
                for (LPort port : node.getPorts()) {
                    for (LEdge edge : port.getIncomingEdges()) {
                        if (edge.getSource().getNode().getLayer() == leftLayerRef) {
                            nodeEdges++;
                        }
                    }
                    getPortPos()[port.id] = targetCount;
                }
                if (nodeEdges > 0) {
                    targetCount++;
                    edgeCount += nodeEdges;
                }
            }
        }

        int[] southSequence =
                sortEdgeTargetsBySourceAndTargetPos(leftLayer, edgeCount,
                        rightLayerRef);

        int crossCount =
                buildAccumulatorTreeAndCountCrossings(targetCount, edgeCount, southSequence);

        return crossCount;
    }

    private int[] sortEdgeTargetsBySourceAndTargetPos(final LNode[] leftLayer, final int edgeCount,
            final Layer rightLayerRef) {
        // Determine the sequence of edge target positions sorted by source and target index
        int[] southSequence = new int[edgeCount];
        int i = 0;
        for (LNode node : leftLayer) {
            if (isPortOrderFixed(node)) {
                // Iterate output ports in their natural order, that is north - east - south - west
                for (LPort port : node.getPorts()) {
                    int start = i;
                    for (LEdge edge : port.getOutgoingEdges()) {
                        LPort target = edge.getTarget();
                        if (target.getNode().getLayer() == rightLayerRef) {
                            assert i < edgeCount;
                            // If the port has multiple output edges, sort them by target port index
                            insert(southSequence, start, i++, getPortPos()[target.id]);
                        }
                    }
                }
            } else {
                // The order of output ports does not matter, so sort only by target port index
                int start = i;
                for (LPort port : node.getPorts()) {
                    for (LEdge edge : port.getOutgoingEdges()) {
                        LPort target = edge.getTarget();
                        if (target.getNode().getLayer() == rightLayerRef) {
                            assert i < edgeCount;
                            insert(southSequence, start, i++, getPortPos()[target.id]);
                        }
                    }
                }
            }
        }
        return southSequence;
    }

    private boolean isPortOrderFixed(final LNode node) {
        return isAssumeFixedPortOrder()
                || node.getProperty(CoreOptions.PORT_CONSTRAINTS).isOrderFixed();
    }

    private int buildAccumulatorTreeAndCountCrossings(final int targetCount, final int edgeCount,
            final int[] southSequence) {
        // Build the accumulator tree
        int firstIndex = 1;
        while (firstIndex < targetCount) {
            firstIndex *= 2;
        }
        int treeSize = 2 * firstIndex - 1;
        firstIndex -= 1;
        int[] tree = new int[treeSize];

        // Count the crossings
        int crossCount = 0;
        for (int k = 0; k < edgeCount; k++) {
            int index = southSequence[k] + firstIndex;
            tree[index]++;
            while (index > 0) {
                if (index % 2 > 0) {
                    crossCount += tree[index + 1];
                }
                index = (index - 1) / 2;
                tree[index]++;
            }
        }
        return crossCount;
    }

    /**
     * Insert a number into a sorted range of an array.
     *
     * @param array
     *            an integer array
     * @param start
     *            the start index of the search range (inclusive)
     * @param end
     *            the end index of the search range (exclusive)
     * @param n
     *            the number to insert
     */
    private static void insert(final int[] array, final int start, final int end, final int n) {
        int insx = binarySearch(array, start, end, n);
        if (insx < 0) {
            insx = -insx - 1;
        }
        for (int j = end - 1; j >= insx; j--) {
            array[j + 1] = array[j];
        }
        array[insx] = n;
    }

    private static int binarySearch(final int[] array, final int start, final int end, final int n) {
        int currentStart = start;
        int currentEnd = end - 1;

        while (currentStart <= currentEnd) {
            int index = (currentStart + currentEnd) / 2;

            if (array[index] == n) {
                return index;
            } else if (array[index] < n) {
                currentStart = index + 1;
            } else {
                currentEnd = index - 1;
            }
        }

        return -currentStart - 1;
    }

    /**
     * Create BetweenLayerStraightEdgeAllCrossingsCounter.
     * 
     * @param numPorts
     *            number of ports in complete graph.
     * @return new BetweenLayerStraightEdgeAllCrossingsCounter object.
     */
    public static BetweenLayerStraightEdgeAllCrossingsCounter create(
            final int numPorts) {
        return new BetweenLayerStraightEdgeAllCrossingsCounter(false, numPorts);
    }

    /**
     * Create BetweenLayerStraightEdgeAllCrossingsCounter assuming all port orders to be fixed.
     * 
     * @param numPorts
     *            number of ports in complete graph.
     * @return new BetweenLayerStraightEdgeAllCrossingsCounter object.
     */
    public static BetweenLayerStraightEdgeAllCrossingsCounter createAssumingPortOrderFixed(
            final int numPorts) {
        return new BetweenLayerStraightEdgeAllCrossingsCounter(true, numPorts);
    }
}
