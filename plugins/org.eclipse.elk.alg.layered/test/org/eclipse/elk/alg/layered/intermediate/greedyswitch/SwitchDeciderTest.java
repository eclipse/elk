/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.greedyswitch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.SwitchDecider.CrossingCountSide;
import org.eclipse.elk.alg.layered.p3order.GraphData;
import org.eclipse.elk.alg.layered.p3order.LayerSweepCrossingMinimizer.CrossMinType;
import org.eclipse.elk.alg.layered.properties.GreedySwitchType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Checks all variants of SwitchDeciders. These are given as parameters with
 * Parameterized.class to create six different test cases.
 *
 * @author alan
 *
 */
@RunWith(Parameterized.class)
public class SwitchDeciderTest {

    private final GreedySwitchType greedyType;

    /**
     * This method is needed by Parameterized.class. The parameters are all
     * elements of the GreedySwitchType enum.
     *
     * @return parameters
     */
    @Parameters(name = "{0}")
    public static Iterable<Object[]> greedyTypes() {
        return Arrays.asList(new Object[][] { { GreedySwitchType.ONE_SIDED }, { GreedySwitchType.TWO_SIDED } });
    }

    /**
     * Constructor is called and greedyType set by Parameterized.
     *
     * @param gT
     *            the greedy type
     */
    public SwitchDeciderTest(final GreedySwitchType gT) {
        greedyType = gT;
    }

    private final TestGraphCreator creator = new TestGraphCreator();
    private LGraph graph;
    private SwitchDecider decider;
    private LNode[][] currentNodeOrder;
    private int freeLayerIndex;

    // CHECKSTYLEOFF javadoc
    // CHECKSTYLEOFF MagicNumber
    @Test
    public void crossFormed() {
        graph = creator.getCrossFormedGraph();
        setUpIds();

        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));

        decider = givenDeciderForFreeLayer(0, CrossingCountSide.EAST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));
    }

    private void setUpIds() {
        for (Layer l : graph) {
            int i = 0;
            for (LNode n : l) {
                n.id = i++;
            }
        }
    }

    @Test
    public void oneNode() {
        graph = creator.getOneNodeGraph();

        decider = givenDeciderForFreeLayer(0, CrossingCountSide.WEST);
        assertThat(decider.doesSwitchReduceCrossings(0, 0), is(false));

        decider = givenDeciderForFreeLayer(0, CrossingCountSide.EAST);
        assertThat(decider.doesSwitchReduceCrossings(0, 0), is(false));
    }

    /**
     * <pre>
     *   --*
     *   |
     * *-+-*-*
     *   |
     *   --*
     * </pre>
     *
     * @ * *
     */
    @Test
    public void inLayerSwitchable() {
        graph = creator.getInLayerEdgesGraph();

        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));

        decider = givenDeciderForFreeLayer(1, CrossingCountSide.EAST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));
    }

    @Test
    public void multipleEdgesBetweenSameNodes() {
        graph = creator.getMultipleEdgesBetweenSameNodesGraph();

        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));

        decider = givenDeciderForFreeLayer(0, CrossingCountSide.EAST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));
    }

    @Test
    public void selfLoops() {
        graph = creator.getCrossWithManySelfLoopsGraph();

        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));

        decider = givenDeciderForFreeLayer(0, CrossingCountSide.EAST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));
    }

    /**
     * Graph:
     *
     * <pre>
     *     ______
     * *---|____|
     *      |  |  ____
     *      *--+--|  |
     *         |  |  |
     *         *--|__|
     * </pre>
     *
     * The one-side decider should switch north-south port crossings in the
     * center layer.
     *
     *
     */
    @Test
    public void northSouthPortCrossing() {
        graph = new NorthSouthEdgeTestGraphCreator().getThreeLayerNorthSouthCrossingGraph();

        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);
        if (greedyType.isOneSided()) {
            assertThat(decider.doesSwitchReduceCrossings(1, 2), is(true));
        } else {
            assertThat(decider.doesSwitchReduceCrossings(1, 2), is(false));
        }
    }

    @Test
    public void moreComplex() {
        graph = creator.getMoreComplexThreeLayerGraph();

        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(false));

        decider = givenDeciderForFreeLayer(2, CrossingCountSide.WEST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));

        assertThat(decider.doesSwitchReduceCrossings(1, 2), is(false));

        decider = givenDeciderForFreeLayer(1, CrossingCountSide.EAST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(false));

        decider = givenDeciderForFreeLayer(0, CrossingCountSide.EAST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(false));

        assertThat(decider.doesSwitchReduceCrossings(1, 2), is(true));
    }

    @Test
    public void switchOnlyTrueForOneSided() {
        graph = creator.getSwitchOnlyOneSided();

        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);
        if (greedyType.isOneSided()) {
            assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));
        } else {
            assertThat(decider.doesSwitchReduceCrossings(0, 1), is(false));
        }
    }

    @Test
    public void switchOnlyTrueForOneSidedEasternSide() {
        graph = creator.getSwitchOnlyEastOneSided();

        decider = givenDeciderForFreeLayer(1, CrossingCountSide.EAST);
        if (greedyType.isOneSided()) {
            assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));
        } else {
            assertThat(decider.doesSwitchReduceCrossings(0, 1), is(false));
        }
    }

    @Test
    public void constraintsPreventSwitch() {
        graph = creator.getCrossFormedGraphWithConstraintsInSecondLayer();

        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(false));
    }

    @Test
    public void inLayerUnitConstraintsPreventSwitch() {
        graph = new NorthSouthEdgeTestGraphCreator().getGraphWhereLayoutUnitPreventsSwitch();
        decider = givenDeciderForFreeLayer(0, CrossingCountSide.WEST);
        assertThat(decider.doesSwitchReduceCrossings(1, 2), is(false));
    }

    @Test
    public void switchAndRecount() {
        graph = creator.getCrossFormedGraph();

        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));

        decider = givenDeciderForFreeLayer(0, CrossingCountSide.EAST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));

        switchNodes(0, 1);
        decider.notifyOfSwitch(getNodesInLayer(0).get(0), getNodesInLayer(0).get(1));
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(false));

    }

    @Test
    public void switchAndRecountCounterBug() {
        LNode[] leftNodes = creator.addNodesToLayer(2, creator.makeLayer(creator.getGraph()));
        LNode[] rightNodes = creator.addNodesToLayer(4, creator.makeLayer(creator.getGraph()));

        creator.eastWestEdgeFromTo(leftNodes[1], rightNodes[0]);
        creator.eastWestEdgeFromTo(leftNodes[1], rightNodes[2]);

        creator.eastWestEdgeFromTo(leftNodes[0], rightNodes[0]);
        creator.eastWestEdgeFromTo(leftNodes[0], rightNodes[1]);
        creator.eastWestEdgeFromTo(leftNodes[0], rightNodes[3]);

        creator.setUpIds();
        graph = creator.getGraph();
        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));
        assertThat(decider.doesSwitchReduceCrossings(1, 2), is(false));
        assertThat(decider.doesSwitchReduceCrossings(2, 3), is(true));

        decider.notifyOfSwitch(currentNodeOrder[freeLayerIndex][0], currentNodeOrder[freeLayerIndex][1]);
        switchNodes(0, 1);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(false));
        assertThat(decider.doesSwitchReduceCrossings(1, 2), is(false));
        assertThat(decider.doesSwitchReduceCrossings(2, 3), is(true));

        decider.notifyOfSwitch(currentNodeOrder[freeLayerIndex][2], currentNodeOrder[freeLayerIndex][3]);
        switchNodes(2, 3);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(false));
        assertThat(decider.doesSwitchReduceCrossings(1, 2), is(true));
        assertThat(decider.doesSwitchReduceCrossings(2, 3), is(false));

        decider.notifyOfSwitch(currentNodeOrder[freeLayerIndex][1], currentNodeOrder[freeLayerIndex][2]);
        switchNodes(1, 2);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(false));
        assertThat(decider.doesSwitchReduceCrossings(1, 2), is(false));
        assertThat(decider.doesSwitchReduceCrossings(2, 3), is(false));

    }

    @Test
    public void switchAndRecountReducedCounterBug() {
        graph = creator.getSwitchedProblemGraph();
        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);
        for (int i = 0; i < getNodesInLayer(1).size() - 1; i++) {
            assertThat("attempted switch " + i + " with " + (i + 1), decider.doesSwitchReduceCrossings(i, i + 1),
                    is(false));
        }
    }

    @Test
    public void ignoresNoFixedOrder() {
        graph = creator.getGraphNoCrossingsDueToPortOrderNotFixed();
        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);
        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(false));
    }

    @Test
    public void shouldSwitchWithLongEdgeDummies() {
        graph = new NorthSouthEdgeTestGraphCreator().getNorthernNorthSouthDummyEdgeCrossingGraph();
        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);

        assertThat(decider.doesSwitchReduceCrossings(1, 2), is(true));

        if (greedyType.isOneSided()) {
            assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));
        }

        graph = new NorthSouthEdgeTestGraphCreator().getSouthernNorthSouthDummyEdgeCrossingGraph();
        decider = givenDeciderForFreeLayer(1, CrossingCountSide.WEST);

        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));

        if (greedyType.isOneSided()) {
            assertThat(decider.doesSwitchReduceCrossings(1, 2), is(true));
        }
    }

    @Test
    public void layoutUnitConstraintPreventsSwitchWithNodeWithNorthernPorts() {
        graph = new NorthSouthEdgeTestGraphCreator()
                .getGraphLayoutUnitPreventsSwitchWithNodeWithNodeWithNorthernEdges();

        decider = givenDeciderForFreeLayer(0, CrossingCountSide.EAST);

        assertThat(decider.doesSwitchReduceCrossings(1, 2), is(false));
    }

    @Test
    public void layoutUnitConstraintPreventsSwitchWithNodeWithSouthernPorts() {
        graph = new NorthSouthEdgeTestGraphCreator()
                .getGraphLayoutUnitPreventsSwitchWithNodeWithNodeWithSouthernEdges();

        decider = givenDeciderForFreeLayer(0, CrossingCountSide.EAST);

        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(false));
    }

    @Test
    public void layoutUnitConstraintDoesNotPreventSwitchWithWhenOtherNodeIsLongEdgeDummy() {
        graph = new NorthSouthEdgeTestGraphCreator().getGraphLayoutUnitDoesNotPreventSwitchWithLongEdgeDummy();

        decider = givenDeciderForFreeLayer(1, CrossingCountSide.EAST);

        assertThat(decider.doesSwitchReduceCrossings(0, 1), is(true));
    }

    private void switchNodes(final int upperNodeIndex, final int lowerNodeIndex) {
        LNode upperNode = currentNodeOrder[freeLayerIndex][upperNodeIndex];
        currentNodeOrder[freeLayerIndex][upperNodeIndex] = currentNodeOrder[freeLayerIndex][lowerNodeIndex];
        currentNodeOrder[freeLayerIndex][lowerNodeIndex] = upperNode;
    }

    private List<LNode> getNodesInLayer(final int layerIndex) {
        return graph.getLayers().get(layerIndex).getNodes();
    }

    private SwitchDecider givenDeciderForFreeLayer(final int layerIndex, final CrossingCountSide direction) {
        freeLayerIndex = layerIndex;
        currentNodeOrder = getCurrentNodeOrder();
        CrossingMatrixFiller crossingMatrixFiller = CrossingMatrixFiller.create(greedyType, currentNodeOrder, layerIndex, direction);
        return new SwitchDecider(layerIndex, currentNodeOrder,
                crossingMatrixFiller,
                new int[getNPorts(currentNodeOrder)],
                new GraphData(graph, CrossMinType.GREEDY_SWITCH, new ArrayList<>()), greedyType.isOneSided());
    }

    private int getNPorts(final LNode[][] currentOrder) {
        int nPorts = 0;
        for (LNode[] lNodes : currentOrder) {
            for (LNode n : lNodes) {
                nPorts += n.getPorts().size();
            }
        }
        return nPorts;
    }

    private LNode[][] getCurrentNodeOrder() {
        LNode[][] nodeOrder = new LNode[graph.getLayers().size()][];
        List<Layer> layers = graph.getLayers();
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            List<LNode> nodes = layer.getNodes();
            nodeOrder[i] = new LNode[nodes.size()];
            for (int j = 0; j < nodes.size(); j++) {
                nodeOrder[i][j] = nodes.get(j);
            }
        }
        return nodeOrder;
    }
}
