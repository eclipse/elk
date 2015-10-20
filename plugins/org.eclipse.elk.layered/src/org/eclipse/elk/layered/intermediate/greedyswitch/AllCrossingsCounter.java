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
package org.eclipse.elk.layered.intermediate.greedyswitch;

import org.eclipse.elk.layered.graph.LNode;

/**
 * Counts the number of crossings between two given layers or all layers in a graph.
 *
 * @author alan
 */
public final class AllCrossingsCounter {
    private final LNode[][] layeredGraph;
    private boolean useHyperedgeCounter;
    private InLayerEdgeAllCrossingsCounter inLayerEdgeCrossingsCounter;
    private BetweenLayerEdgeAllCrossingsCounter inbetweenLayerCounter;
    private NorthSouthEdgeAllCrossingsCounter northSouthPortCrossingCounter;

    /**
     * Constructs and initializes a cross counter.
     * 
     * @param layeredGraph
     *            The layered graph
     */
    public AllCrossingsCounter(final LNode[][] layeredGraph) {
        this.layeredGraph = layeredGraph;
        useHyperedgeCounter = false;
    }

    /**
     * Counts all crossings in a graph.
     * 
     * @return the number of crossings for the node order passed to the constructor.
     */
    public int countAllCrossingsInGraph() {
        return countAllCrossingsInGraphWithOrder(layeredGraph);
    }

    /**
     * Counts all crossings in a graph in the currentOrder.
     *
     * @param currentOrder
     *            The current order of the nodes.
     * @return the number of crossings
     */
    public int countAllCrossingsInGraphWithOrder(final LNode[][] currentOrder) {
        int totalCrossings = 0;
        for (int layerIndex = 0; layerIndex < currentOrder.length; layerIndex++) {
            LNode[] easternLayer = currentOrder[layerIndex];
            if (layerIndex < currentOrder.length - 1) {
                LNode[] westernLayer = currentOrder[layerIndex + 1];
                totalCrossings += countBetweenLayerCrossingsInOrder(easternLayer, westernLayer);
            }
            totalCrossings += countNorthSouthPortCrossings(easternLayer);
            totalCrossings += countInLayerEdgeCrossingsWithOrder(easternLayer);
        }
        return totalCrossings;
    }

    /**
     * Between-layer edges are counted using the hyperedge crossing approximization algorithm.
     */
    public void useHyperedgeCounter() {
        useHyperedgeCounter = true;
    }

    private int countBetweenLayerCrossingsInOrder(final LNode[] easternLayer,
            final LNode[] westernLayer) {
        
        if (isALayerEmpty(easternLayer, westernLayer)) {
            return 0;
        }
        if (useHyperedgeCounter) {
            inbetweenLayerCounter = new BetweenLayerHyperedgeAllCrossingsCounter(layeredGraph);
        } else {
            inbetweenLayerCounter = new BetweenLayerStraightEdgeAllCrossingsCounter(layeredGraph);
        }
        return inbetweenLayerCounter.countCrossings(easternLayer, westernLayer);
    }

    private boolean isALayerEmpty(final LNode[] easternLayer, final LNode[] westernLayer) {
        return easternLayer.length == 0 || westernLayer.length == 0;
    }

    private int countInLayerEdgeCrossingsWithOrder(final LNode[] layer) {
        inLayerEdgeCrossingsCounter = new InLayerEdgeAllCrossingsCounter(layer);
        return inLayerEdgeCrossingsCounter.countCrossings();
    }

    private int countNorthSouthPortCrossings(final LNode[] layer) {
        northSouthPortCrossingCounter = new NorthSouthEdgeAllCrossingsCounter(layer);
        return northSouthPortCrossingCounter.countCrossings();
    }
}
