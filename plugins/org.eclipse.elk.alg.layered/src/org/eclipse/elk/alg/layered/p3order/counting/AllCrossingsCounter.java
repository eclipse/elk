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

import org.eclipse.elk.alg.layered.graph.LNode;
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
 * TODO-alan this class should be removed some day. AND ITS A MESS!
 *
 * @author alan
 */
public final class AllCrossingsCounter {

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
    }

    public int countAllCrossings(final LNode[][] currentOrder) {
        int crossings = crossingCounter.countInLayerCrossingsOnSide(currentOrder[0], PortSide.WEST);
        crossings += crossingCounter.countInLayerCrossingsOnSide(currentOrder[currentOrder.length - 1], PortSide.EAST);
        for (int layerIndex = 0; layerIndex < currentOrder.length; layerIndex++) {
            crossings += countCrossingsAt(layerIndex, currentOrder);
        }
        return crossings;
    }

    private AllCrossingsCounter(final boolean fixedOrder, final int[] inLayerEdgeCount,
            final boolean[] hasNorthSouthPorts, final int[] portPos,
            final boolean[] hasHyperEdgesEastOfIndex) {
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


    private int countCrossingsAt(final int layerIndex, final LNode[][] currentOrder) {
        int totalCrossings = 0;
        LNode[] leftLayer = currentOrder[layerIndex];
        if (layerIndex < currentOrder.length - 1) {
            LNode[] rightLayer = currentOrder[layerIndex + 1];
            if (hasHyperEdgesEastOfIndex[layerIndex]) {
                totalCrossings = hyperedgeCrossingsCounter.countCrossings(leftLayer, rightLayer);
            } else {
                totalCrossings = crossingCounter.countCrossingsBetweenLayers(leftLayer, rightLayer);
            }

        }
        final LNode[] layer = leftLayer;
        totalCrossings += northSouthEdgeCrossingCounter.countCrossings(layer);
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
