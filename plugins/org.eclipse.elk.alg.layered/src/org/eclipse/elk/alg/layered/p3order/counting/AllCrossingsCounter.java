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
 * Counts all crossings in a graph.
 *
 * @author alan
 */
public final class AllCrossingsCounter {

    private CrossingsCounter crossingCounter;
    private NorthSouthEdgeAllCrossingsCounter northSouthEdgeCrossingCounter;
    private boolean[] hasHyperEdgesEastOfIndex;
    private HyperedgeCrossingsCounter hyperedgeCrossingsCounter;

    /**
     * Create utility class for counting all different kinds of crossings.
     * 
     * @param inLayerEdgeCount
     *            Number of inlayer edges per layer for {@link HyperedgeCrossingsCounter}.
     * @param hasNorthSouthPorts
     *            Whether graph has north south ports in a given layer for {@link HyperedgeCrossingsCounter}.
     * @param hasHyperEdgesEastOfIndex
     *            Whether there are hyperedges to the right of each layer index.
     * @param portPos
     *            port position array to prevent frequent large array creation.
     */
    public AllCrossingsCounter(final int[] inLayerEdgeCount,
            final boolean[] hasNorthSouthPorts, final boolean[] hasHyperEdgesEastOfIndex, final int[] portPos) {
        this.hasHyperEdgesEastOfIndex = hasHyperEdgesEastOfIndex;
        hyperedgeCrossingsCounter = new HyperedgeCrossingsCounter(inLayerEdgeCount, hasNorthSouthPorts, portPos);
        northSouthEdgeCrossingCounter = new NorthSouthEdgeAllCrossingsCounter(portPos);
        crossingCounter = new CrossingsCounter(portPos);
    }

    /**
     * Count all crossings.
     * 
     * @param currentOrder
     * @return
     */
    public int countAllCrossings(final LNode[][] currentOrder) {
        if (currentOrder.length == 0) {
            return 0;
        }
        int crossings = crossingCounter.countInLayerCrossingsOnSide(currentOrder[0], PortSide.WEST);
        crossings += crossingCounter.countInLayerCrossingsOnSide(currentOrder[currentOrder.length - 1], PortSide.EAST);
        for (int layerIndex = 0; layerIndex < currentOrder.length; layerIndex++) {
            crossings += countCrossingsAt(layerIndex, currentOrder);
        }
        return crossings;
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

        totalCrossings += northSouthEdgeCrossingCounter.countCrossings(leftLayer);
        return totalCrossings;
    }

}
