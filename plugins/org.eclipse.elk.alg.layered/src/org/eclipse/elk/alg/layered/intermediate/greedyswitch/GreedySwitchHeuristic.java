/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
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
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.SwitchDecider.CrossingCountSide;
import org.eclipse.elk.alg.layered.p3order.ICrossingMinimizationHeuristic;
import org.eclipse.elk.alg.layered.properties.GreedySwitchType;

/**
 * <p>
 * Implements the greedy switch heuristic: For two neighboring nodes, check to see if by exchanging
 * their positions ("switching" them) the number of crossings is reduced. If it is, switch them, if
 * it is not, don't. This principle is continued throughout the graph for all nodes in each layer.
 * </p>
 * <p>
 * Configuration depends on the {@link GreedySwitchType} set on the graph:
 * </p>
 * <ul>
 * <li>isOneSided: fixes the order of one layer and changes the order in a neighboring layer using
 * the number of crossings to this neighboring layer. To prevent increasing the number of crosses,
 * after each forward and backward sweep the number of crossings in the graph are recounted. If
 * isOneSided is not false, it sets a layer as free and counts crossings to both neighboring layers.
 *
 * </ul>
 *
 * @author alan
 *
 */
public class GreedySwitchHeuristic implements ICrossingMinimizationHeuristic {

    private final GreedySwitchType greedySwitchType;
    private LNode[][] currentNodeOrder;
    private SwitchDecider switchDecider;
    private int[] portPositions;

    /**
     * Create GreedySwitchHeuristic.
     *
     * @param greedyType
     *            The greedy switch type.
     * @param nPorts
     */
    public GreedySwitchHeuristic(final GreedySwitchType greedyType, final int nPorts) {
        portPositions = new int[nPorts];
        greedySwitchType = greedyType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean minimizeCrossings(final LNode[][] order, final int freeLayerIndex,
            final boolean forwardSweep, final boolean isFirstSweep) {
        setUp(order, freeLayerIndex, forwardSweep);
        return continueSwitchingUntilNoImprovementInLayer(freeLayerIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setFirstLayerOrder(final LNode[][] currentOrder, final boolean isForwardSweep) {
        int startIndex = startIndex(isForwardSweep, currentOrder.length);
        setUp(currentOrder, startIndex, isForwardSweep);
        return sweepDownwardInLayer(startIndex);
    }

    private void setUp(final LNode[][] order, final int freeLayerIndex,
            final boolean forwardSweep) {
        currentNodeOrder = order;
        CrossingCountSide side = forwardSweep ? CrossingCountSide.WEST : CrossingCountSide.EAST;
        switchDecider = getNewSwitchDecider(freeLayerIndex, side);
    }

    private SwitchDecider getNewSwitchDecider(final int freeLayerIndex, final CrossingCountSide side) {
        CrossingMatrixFiller crossingMatrixFiller =
                CrossingMatrixFiller.createAssumingFixedPortOrder(greedySwitchType,
                        currentNodeOrder, freeLayerIndex, side);
        return new SwitchDecider(freeLayerIndex, currentNodeOrder, crossingMatrixFiller, portPositions);
    }

    private boolean continueSwitchingUntilNoImprovementInLayer(final int freeLayerIndex) {
        boolean improved = false;
        boolean continueSwitching;
        do {
            continueSwitching = sweepDownwardInLayer(freeLayerIndex);
            improved |= continueSwitching;
        } while (continueSwitching);
        return improved;
    }

    private boolean sweepDownwardInLayer(final int layerIndex) {
        boolean continueSwitching = false;
        int lengthOfFreeLayer = currentNodeOrder[layerIndex].length;
        for (int upperNodeIndex = 0; upperNodeIndex < lengthOfFreeLayer - 1; upperNodeIndex++) {
            int lowerNodeIndex = upperNodeIndex + 1;

            continueSwitching |= switchIfImproves(layerIndex, upperNodeIndex, lowerNodeIndex);
        }
        return continueSwitching;
    }

    private boolean switchIfImproves(final int layerIndex, final int upperNodeIndex,
            final int lowerNodeIndex) {
        boolean continueSwitching = false;

        if (switchDecider.doesSwitchReduceCrossings(upperNodeIndex, lowerNodeIndex)) {
            exchangeNodes(upperNodeIndex, lowerNodeIndex, layerIndex);

            continueSwitching = true;
        }
        return continueSwitching;
    }

    private void exchangeNodes(final int indexOne, final int indexTwo, final int layerIndex) {
        switchDecider.notifyOfSwitch(currentNodeOrder[layerIndex][indexOne],
                currentNodeOrder[layerIndex][indexTwo]);
        LNode[] layer = currentNodeOrder[layerIndex];
        LNode temp = layer[indexTwo];
        layer[indexTwo] = layer[indexOne];
        layer[indexOne] = temp;
    }

    private int startIndex(final boolean isForwardSweep, final int length) {
        return isForwardSweep ? 0 : length - 1;
    }

    @Override
    public boolean alwaysImproves() {
        return !greedySwitchType.isOneSided();
    }

    @Override
    public boolean isDeterministic() {
        return true;
    }

}
