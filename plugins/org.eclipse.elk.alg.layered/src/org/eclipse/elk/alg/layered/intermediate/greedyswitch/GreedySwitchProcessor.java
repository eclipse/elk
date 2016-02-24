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

import java.util.ListIterator;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.SwitchDecider.CrossingCountSide;
import org.eclipse.elk.alg.layered.properties.GreedySwitchType;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;

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
 * <li>useHperedgeCounter: uses the hyperedge crossing approximation algorithm to count
 * between-layer edges.
 * <li>useBestOfUpOrDown: tries sweeping from top to bottom in a layer and other way around and
 * takes the better solution.
 * </ul>
 * 
 * @author alan
 *
 */
public class GreedySwitchProcessor implements ILayoutProcessor {
    /** The original LGraph. */
    private LGraph layeredGraph;
    /**
     * Depending on the Settings in the GreedySwitchType enum, the SwitchDecider is set and the
     * sweepDownwardInLayer flag used.
     */
    private GreedySwitchType greedySwitchType;
    private SwitchDecider switchDecider;
    /** The order of the nodes in the layers. This is not needed for the normal two-sided method. */
    private LNode[][] originalNodeOrder;
    private LNode[][] currentNodeOrder;
    private LNode[][] bestNodeOrder;
    /** Counts all crossings in a graph. */
    private AllCrossingsCounter crossingCounter;
    /** The current crossings are calculated and saved when using the one-sided method. */
    private int currentCrossings;
    /** Used for the best of up or down setting. */
    private boolean sweepDownwardInLayer = true;

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph graph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Greedy switch crossing reduction", 1);

        greedySwitchType = graph.getProperty(LayeredOptions.GREEDY_SWITCH);

        int layerCount = graph.getLayers().size();
        if (layerCount < 2 || greedySwitchType == GreedySwitchType.OFF) {
            progressMonitor.done();
            return;
        }

        initialize(graph);

        if (greedySwitchType.useBestOfUpOrDown()) {
            compareSweepingUpwardOrDownward();
        } else {
            sweepOneSidedOrTwoSided();
        }

        setAsGraph(bestNodeOrder);

        progressMonitor.done();
    }

    private void compareSweepingUpwardOrDownward() {
        sweepOneSidedOrTwoSided();

        LNode[][] downwardSweepOrder = copyNodeOrder();
        int downwardSweepCrossings = getCrossingCount();

        // try other direction
        sweepDownwardInLayer = !sweepDownwardInLayer;
        currentNodeOrder = originalNodeOrder;
        sweepOneSidedOrTwoSided();
        int upwardSweepCrossings = getCrossingCount();

        if (downwardSweepCrossings <= upwardSweepCrossings) {
            setAsBestNodeOrder(downwardSweepOrder);
        }
    }

    private void sweepOneSidedOrTwoSided() {
        if (greedySwitchType.isOneSided()) {
            oneSidedLayerSweep();
        } else {
            twoSidedlayerSweep();
        }
    }

    private LNode[][] copyNodeOrder() {
        LNode[][] order = new LNode[bestNodeOrder.length][];
        for (int i = 0; i < order.length; i++) {
            // gwt 2.6 does not support Arrays.copyOf, thus we do it
            // the old school way
            int length = bestNodeOrder[i].length;
            LNode[] copy = new LNode[length];
            System.arraycopy(bestNodeOrder[i], 0, copy, 0, length);
            order[i] = copy;
        }
        return order;
    }

    private int getCrossingCount() {
        return greedySwitchType.isOneSided() ? currentCrossings : countCurrentNumberOfCrossings();
    }

    /**
     * This version of the greedy switch processor sweeps forward and backward over the graph.
     * Assuming a forward sweep, for each pair of layers it holds the nodes of the western layer in
     * place and changes the order of the eastern layer. A node is switched if it causes more
     * crossings between these two layers in the current order than in the switched order. Since the
     * layer following the free layer is not considered, this switch can cause more crossings in the
     * whole graph. In order to prevent this, the number of crossings are counted at the beginning
     * and after each forward and backward sweep. The result with the fewest crossings (possibly the
     * original order) is taken.
     */
    private void oneSidedLayerSweep() {
        int crossingsInGraph = countCurrentNumberOfCrossings();
        int oldNumberOfCrossings = Integer.MAX_VALUE;
        while (oldNumberOfCrossings > crossingsInGraph) {
            setAsBestNodeOrder(currentNodeOrder);

            if (crossingsInGraph == 0) {
                oldNumberOfCrossings = crossingsInGraph;
                break;
            }

            sweepForwardReducingCrossings();
            sweepBackwardReducingCrossings();

            oldNumberOfCrossings = crossingsInGraph;
            crossingsInGraph = countCurrentNumberOfCrossings();
        }
        currentCrossings = oldNumberOfCrossings;
    }

    /**
     * This version of the greedy switch processor repeatedly sweeps only forward across the graph.
     * For each layer it holds the neighboring layers in place and switches the node order iff it
     * would reduce the number of crossings on both sides of the middle layer. In this manner, the
     * number of crossings can not be increased and we do not need to calculate the total number of
     * crossings.
     * 
     */
    private void twoSidedlayerSweep() {
        boolean forward = true;
        boolean improved;
        do {
            if (forward) {
                improved = sweepForwardReducingCrossings();
            } else {
                improved = sweepBackwardReducingCrossings();
            }
            forward = !forward;
        } while (improved);
        setAsBestNodeOrder(currentNodeOrder);
    }

    private boolean sweepForwardReducingCrossings() {
        boolean improved = false;
        for (int freeLayerIndex = 0; freeLayerIndex < currentNodeOrder.length; freeLayerIndex++) {
            // For each free layer the SwitchDecider instantiates the necessary crossing counters
            // upon creation, which is why we need create a new switch decider for each visited free
            // layer
            switchDecider = getNewSwitchDecider(freeLayerIndex, CrossingCountSide.WEST);
            improved |= continueSwitchingUntilNoImprovementInLayer(freeLayerIndex);
        }
        return improved;
    }

    private SwitchDecider getNewSwitchDecider(final int freeLayerIndex, final CrossingCountSide side) {
        CrossingMatrixFiller crossingMatrixFiller =
                new CrossingMatrixFiller(greedySwitchType, currentNodeOrder, freeLayerIndex, side);
        return new SwitchDecider(freeLayerIndex, currentNodeOrder, crossingMatrixFiller);
    }

    private boolean sweepBackwardReducingCrossings() {
        boolean improved = false;
        for (int freeLayerIndex = currentNodeOrder.length - 1; freeLayerIndex >= 0; freeLayerIndex--) {
            // For each free layer the SwitchDecider instantiates the necessary crossing counters
            // upon creation, which is why we need create a new switch decider for each visited free
            // layer
            switchDecider = getNewSwitchDecider(freeLayerIndex, CrossingCountSide.EAST);
            improved |= continueSwitchingUntilNoImprovementInLayer(freeLayerIndex);
        }
        return improved;
    }

    private boolean continueSwitchingUntilNoImprovementInLayer(final int freeLayerIndex) {
        boolean improved = false;
        boolean continueSwitching;
        do {
            if (sweepDownwardInLayer) {
                continueSwitching = sweepDownwardInLayer(freeLayerIndex);
            } else {
                continueSwitching = sweepUpwardInLayer(freeLayerIndex);
            }

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

    private boolean sweepUpwardInLayer(final int layerIndex) {
        boolean continueSwitching = false;
        int lengthOfFreeLayer = currentNodeOrder[layerIndex].length;
        for (int lowerNodeIndex = lengthOfFreeLayer - 1; lowerNodeIndex > 0; lowerNodeIndex--) {
            int upperNodeIndex = lowerNodeIndex - 1;

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

    private int countCurrentNumberOfCrossings() {
        return crossingCounter.countAllCrossingsInGraphWithOrder(currentNodeOrder);
    }

    private void setAsBestNodeOrder(final LNode[][] nodeOrder) {
        for (int i = 0; i < bestNodeOrder.length; i++) {
            for (int j = 0; j < bestNodeOrder[i].length; j++) {
                bestNodeOrder[i][j] = nodeOrder[i][j];
            }
        }
    }

    private void exchangeNodes(final int indexOne, final int indexTwo, final int layerIndex) {
        switchDecider.notifyOfSwitch(currentNodeOrder[layerIndex][indexOne],
                currentNodeOrder[layerIndex][indexTwo]);
        LNode[] layer = currentNodeOrder[layerIndex];
        LNode temp = layer[indexTwo];
        layer[indexTwo] = layer[indexOne];
        layer[indexOne] = temp;
    }

    private void setAsGraph(final LNode[][] nodeOrder) {
        ListIterator<Layer> layerIter = layeredGraph.getLayers().listIterator();
        while (layerIter.hasNext()) {
            Layer layer = layerIter.next();
            LNode[] nodes = nodeOrder[layerIter.previousIndex()];
            ListIterator<LNode> nodeIter = layer.getNodes().listIterator();
            while (nodeIter.hasNext()) {
                nodeIter.next();
                nodeIter.set(nodes[nodeIter.previousIndex()]);
            }
        }
    }

    private void initialize(final LGraph graph) {
        layeredGraph = graph;
        int layerCount = graph.getLayers().size();
        bestNodeOrder = new LNode[layerCount][];
        currentNodeOrder = new LNode[layerCount][];
        originalNodeOrder = new LNode[layerCount][];

        ListIterator<Layer> layerIter = graph.getLayers().listIterator();
        while (layerIter.hasNext()) {
            Layer layer = layerIter.next();

            int layerNodeCount = layer.getNodes().size();
            assert layerNodeCount > 0;

            int layerIndex = layerIter.previousIndex();
            bestNodeOrder[layerIndex] = new LNode[layerNodeCount];
            currentNodeOrder[layerIndex] = new LNode[layerNodeCount];
            originalNodeOrder[layerIndex] = new LNode[layerNodeCount];

            ListIterator<LNode> nodeIter = layer.getNodes().listIterator();
            int id = 0;
            while (nodeIter.hasNext()) {
                LNode node = nodeIter.next();
                node.id = id++;

                currentNodeOrder[layerIndex][nodeIter.previousIndex()] = node;
                bestNodeOrder[layerIndex][nodeIter.previousIndex()] = node;
                originalNodeOrder[layerIndex][nodeIter.previousIndex()] = node;
            }
        }
        crossingCounter = new AllCrossingsCounter(currentNodeOrder);
        if (greedySwitchType.useHyperedgeCounter()) {
            crossingCounter.useHyperedgeCounter();
        }
    }
}
