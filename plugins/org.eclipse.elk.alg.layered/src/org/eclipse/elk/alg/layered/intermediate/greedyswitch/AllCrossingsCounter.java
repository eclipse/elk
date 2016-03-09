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

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.p3order.counting.BetweenLayerStraightEdgeAllCrossingsCounter;
import org.eclipse.elk.alg.layered.p3order.counting.CrossingsCounter;
import org.eclipse.elk.alg.layered.p3order.counting.HyperedgeCrossingsCounter;
import org.eclipse.elk.alg.layered.p3order.counting.NorthSouthEdgeAllCrossingsCounter;
import org.eclipse.elk.core.options.PortSide;

/**
 * Counts the number of crossings between two given layers or all layers in a graph. <br/>
 * When multiple edges leave or enter a port, it is assumed, that this is drawn as a hyperedge and
 * the hyperedge crossings counter is used. This counter can only approximate the resulting
 * crossings. <br/>
 * The AllCrossingsCounter can be created to assume all port order constraints to be fixed or to see
 * the actual port order constraints. <br/>
 * When it does not assume all port orders fixed: Should the following port sorting phase be able to
 * remove crossings, these crossings are not counted.
 * 
 * TODO-alan this class should be removed some day.
 * 
 * @author alan
 */
public final class AllCrossingsCounter {

    private boolean alwaysUseHyperedgeCounter;
    private CrossingsCounter inLayerEdgeCrossingsCounter;
    private BetweenLayerStraightEdgeAllCrossingsCounter inbetweenLayerStraightEdgeCounter;
    private BetweenLayerHyperedgeAllCrossingsCounter inBetweenLayerHyperedgeAllCrossingsCounter;
    private NorthSouthEdgeAllCrossingsCounter northSouthPortCrossingCounter;
    private final boolean assumeFixedPortOrder;
    private boolean[] hasHyperedgesInLayer;
    private CrossingsCounter betweenAndInLayerCounter;
    private final boolean useNewCounter = true;

    private CrossingsCounter crossingCounter;
    private NorthSouthEdgeAllCrossingsCounter northSouthEdgeCrossingCounter;
    private boolean[] hasHyperEdgesEastOfIndex;
    private HyperedgeCrossingsCounter hyperedgeCrossingsCounter;

    /**
     * Constructs and initializes a cross counter.
     * 
     * @param layeredGraph
     *            The layered graph
     */
    private AllCrossingsCounter(final boolean assumeFixedPortOrder) {
        this.assumeFixedPortOrder = assumeFixedPortOrder;
        alwaysUseHyperedgeCounter = false;
    }

    /**
     * Counts all crossings in a graph in the currentOrder. When multiple edges leave or enter a
     * port, it is assumed, that this is drawn as a hyperedge and the hyperedge crossings counter is
     * used. This counter can only approximate the resulting crossings.
     *
     * @param currentOrder
     *            The current order of the nodes.
     * @param hasHyperEdgesInLayer
     *            array showing which layer has hyperedges. TODO-alan this makes no sense but this
     *            class will die some day anyway.
     * @param numPorts
     *            the number of ports in the complete graph.
     * @return the number of crossings
     */
    public int countAllCrossingsInGraphWithOrder(final LNode[][] currentOrder,
            final boolean[] hasHyperEdgesInLayer, final int numPorts) {
        if (currentOrder.length == 0) {
            return 0;
        }
        hasHyperedgesInLayer = hasHyperEdgesInLayer;
        inBetweenLayerHyperedgeAllCrossingsCounter = assumeFixedPortOrder
                ? BetweenLayerHyperedgeAllCrossingsCounter.createAssumingPortOrderFixed(numPorts)
                : BetweenLayerHyperedgeAllCrossingsCounter.create(numPorts);

        int[] portPositions = new int[numPorts];
        northSouthPortCrossingCounter = new NorthSouthEdgeAllCrossingsCounter(portPositions);
        if (useNewCounter) {
            betweenAndInLayerCounter = assumeFixedPortOrder
                    ? CrossingsCounter.createAssumingPortOrderFixed(portPositions)
                    : CrossingsCounter.create(portPositions);
        } else {
            inbetweenLayerStraightEdgeCounter = assumeFixedPortOrder
                    ? BetweenLayerStraightEdgeAllCrossingsCounter
                            .createAssumingPortOrderFixed(numPorts)
                    : BetweenLayerStraightEdgeAllCrossingsCounter.create(numPorts);
            inLayerEdgeCrossingsCounter =
                assumeFixedPortOrder ? CrossingsCounter.createAssumingPortOrderFixed(portPositions)
                        : CrossingsCounter.create(portPositions);
        }
        int totalCrossings = useNewCounter
                ? betweenAndInLayerCounter.countInLayerCrossingsOnSide(currentOrder[0],
                        PortSide.WEST)
                : inLayerEdgeCrossingsCounter.countInLayerCrossingsOnBothSides(currentOrder[0]);
        for (int layerIndex = 0; layerIndex < currentOrder.length; layerIndex++) {
            totalCrossings += countCrossingsAt(layerIndex, currentOrder, portPositions);
        }
        return totalCrossings;
    }

    private AllCrossingsCounter(final boolean fixedOrder, final int[] inLayerEdgeCount,
            final boolean[] hasNorthSouthPorts, final int[] portPos,
            final boolean[] hasHyperEdgesEastOfIndex) {
        assumeFixedPortOrder = fixedOrder;
        this.hasHyperEdgesEastOfIndex = hasHyperEdgesEastOfIndex;
        hyperedgeCrossingsCounter = HyperedgeCrossingsCounter
                .createAssumingPortOrderFixed(inLayerEdgeCount, hasNorthSouthPorts, portPos);
        northSouthEdgeCrossingCounter = new NorthSouthEdgeAllCrossingsCounter(portPos);
        crossingCounter = CrossingsCounter.createAssumingPortOrderFixed(portPos);
    }

    /**
     * Count in layer crossings not between any layers and north south port crossings in first
     * layer.
     * 
     * @param forward
     *            Whether we are sweeping forward or not (= backward).
     * @param nodes
     *            the current order of the nodes.
     * @return number of crossings.
     */
    public int countCrossingsInFirstLayer(final boolean forward, final LNode[][] nodes) {
        int length = nodes.length;
        // Count crossings on front side of first layer.
        LNode[] firstLayer = nodes[firstLayerInd(forward, length)];
        int crossings = crossingCounter.countInLayerCrossingsOnSide(firstLayer,
                sideOpposedSweepDirection(forward));
        crossings += northSouthEdgeCrossingCounter.countCrossings(firstLayer);
        return crossings;
    }

    private PortSide sideOpposedSweepDirection(final boolean isForwardSweep) {
        return isForwardSweep ? PortSide.WEST : PortSide.EAST;
    }

    private int firstLayerInd(final boolean isForwardSweep, final int length) {
        return isForwardSweep ? 0 : length - 1;
    }

    /**
     * Counts in-layer and between layer crossings between the free and fixed layer of a sweep.
     * 
     * @param forward
     *            Whether we are sweeping forward or not.
     * @param freeLayerIndex
     *            The current free layer.
     * @param nodeOrder
     *            The current order of the nodes.
     * @return number of crossings between free and fixed layer.
     */
    public int countCrossingsOnNeighboringLayers(final boolean forward, final int freeLayerIndex,
            final LNode[][] nodeOrder) {
        int leftLayerIndex = forward ? freeLayerIndex - 1 : freeLayerIndex;
        int rightLayerIndex = forward ? freeLayerIndex : freeLayerIndex + 1;
        LNode[] leftLayer = nodeOrder[leftLayerIndex];
        LNode[] rightLayer = nodeOrder[rightLayerIndex];
        int crossings = 0;

        if (hasHyperEdgesEastOfIndex[leftLayerIndex]) {
            crossings +=
                    crossingCounter.countInLayerCrossingsOnBothSides(nodeOrder[freeLayerIndex]);
            crossings += hyperedgeCrossingsCounter.countCrossings(leftLayer, rightLayer);
        } else {
            crossings += crossingCounter.countCrossingsBetweenLayers(leftLayer, rightLayer);
        }
        crossings += northSouthEdgeCrossingCounter.countCrossings(nodeOrder[freeLayerIndex]);

        return crossings;
    }

    /**
     * Between-layer edges are all counted using the hyperedge crossing approximization algorithm.
     */
    public void alwaysUseHyperedgeCounter() {
        alwaysUseHyperedgeCounter = true;
    }

    private int countBetweenLayerCrossingsInOrder(final LNode[] easternLayer,
            final LNode[] westernLayer, final LNode[][] nodeOrder,
            final boolean useHyperEdgeCounter) {
        if (isALayerEmpty(easternLayer, westernLayer)) {
            return 0;
        }

        return (useHyperEdgeCounter ? inBetweenLayerHyperedgeAllCrossingsCounter
                : inbetweenLayerStraightEdgeCounter).countCrossings(easternLayer, westernLayer);
    }

    /**
     * Getting hyperedges.
     * 
     * @param nodeOrder
     *            the order of nodes in the complete graph.
     * @param side
     *            the side.
     * @return returns where the graph has hyperedges
     */
    public static boolean[] getHyperedges(final LNode[][] nodeOrder, final PortSide side) {
        boolean[] hasHyperedges = new boolean[nodeOrder.length];
        for (int layerIndex = 0; layerIndex < nodeOrder.length; layerIndex++) {
            LNode[] easternLayer = nodeOrder[layerIndex];
            hasHyperedges[layerIndex] |= checkForHyperedges(easternLayer, side);
        }
        return hasHyperedges;
    }

    private static boolean checkForHyperedges(final LNode[] layer, final PortSide side) {
        for (LNode node : layer) {
            for (LPort port : node.getPorts()) {
                if (port.getSide() == side) {
                    if (port.getOutgoingEdges().size() + port.getIncomingEdges().size() > 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isALayerEmpty(final LNode[] easternLayer, final LNode[] westernLayer) {
        return easternLayer.length == 0 || westernLayer.length == 0;
    }

    private int countCrossingsAt(final int layerIndex, final LNode[][] currentOrder,
            final int[] portPositions) {
        int totalCrossings = 0;
        LNode[] leftLayer = currentOrder[layerIndex];
        if (layerIndex < currentOrder.length - 1) {
            LNode[] rightLayer = currentOrder[layerIndex + 1];
            boolean useHyperEdgeCounter =
                    alwaysUseHyperedgeCounter || hasHyperedgesInLayer[layerIndex];
            totalCrossings += useNewCounter
                    ? betweenAndInLayerCounter.countCrossingsBetweenLayers(leftLayer, rightLayer)
                    : countBetweenLayerCrossingsInOrder(leftLayer, rightLayer, currentOrder,
                    useHyperEdgeCounter);
        }
        final LNode[] layer = leftLayer;
        totalCrossings += northSouthPortCrossingCounter.countCrossings(layer);
        if (!useNewCounter) {
            totalCrossings += inLayerEdgeCrossingsCounter.countInLayerCrossingsOnBothSides(leftLayer);
        }
        return totalCrossings;
    }

    /**
     * Create Counter for counting all crossings in a given node order.
     * 
     * @return new AllCrossingsCounter object.
     */
    public static AllCrossingsCounter create() {
        return new AllCrossingsCounter(false);
    }

    /**
     * Create Counter for counting all crossings in a given node order assuming all port order
     * constraints to be fixed independent of the orders actually set.
     * 
     * @return new AllCrossingsCounter object.
     */
    public static AllCrossingsCounter createAssumingFixedPortOrder() {
        return new AllCrossingsCounter(true);
    }


    /**
     * Create Counter for counting all crossings in a given node order assuming all port order
     * constraints to be fixed independent of the constraints actually set. Use this method to pass
     * information from outside saving initialization.
     * 
     * @param inLayerEdgeCount
     *            Number of in layer edges.
     * @param hasNorthSouthPorts
     *            Number of north south ports
     * @param portPos
     *            An array the length of the number of ports in the graph.
     * @param hasHyperEdgesEastOfIndex
     *            whether a layer east of the given index has hyperedges.
     * @return the counter.
     */
    public static AllCrossingsCounter createAssumingFixedPortOrder(final int[] inLayerEdgeCount,
            final boolean[] hasNorthSouthPorts, final int[] portPos,
            final boolean[] hasHyperEdgesEastOfIndex) {
        return new AllCrossingsCounter(true, inLayerEdgeCount, hasNorthSouthPorts, portPos,
                hasHyperEdgesEastOfIndex);
    }

}
