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

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.p3order.counting.CrossingsCounter;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.junit.Test;

// CHECKSTYLEOFF MagicNumber
// CHECKSTYLEOFF javadoc
// CHECKSTYLEOFF MethodName
public class CrossingsCounterTest extends InLayerEdgeTestGraphCreator {
    private CrossingsCounter counter;
    private LNode[][] order;

    @Test
    public void countInLayerCrossingsOnBothSides_ignoresInBetweenLayerEdges() {
        getCrossFormedGraph();
        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(0));
    }

    /**
     * <pre>
     *   --*
     *   |
     * *-+-*-*
     *   |
     *   --*
     * .
     * </pre>
     *
     * @return graph of the form above
     */
    @Test
    public void countInLayerCrossingsOnBothSides_countInLayerEdgeWithNormalEdgeCrossing() {
        LNode leftNode = addNodeToLayer(makeLayer(getGraph()));
        LNode[] middleNodes = addNodesToLayer(3, makeLayer(getGraph()));
        LNode rightNode = addNodeToLayer(makeLayer(getGraph()));
        eastWestEdgeFromTo(middleNodes[1], rightNode);
        eastWestEdgeFromTo(leftNode, middleNodes[1]);
        addInLayerEdge(middleNodes[0], middleNodes[2], PortSide.WEST);
        setUpIds();
        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(1));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_crossingsWhenSwitched() {
        getInLayerEdgesGraphWhichResultsInCrossingsWhenSwitched();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        order = order();
        assertThat(counter.countInLayerCrossingsOnBothSides(order[1]), is(0));

        switchOrder(1, 2, 1);

        assertThat(counter.countInLayerCrossingsOnBothSides(order[1]), is(1));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_fixedPortOrderNodeToNode() {
        getInLayerEdgesGraphWithCrossingsToBetweenLayerEdgeWithFixedPortOrder();
        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(1));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_fixedPortOrderNodeToNodeLowerNode() {
        getInLayerEdgesGraphWithCrossingsToBetweenLayerEdgeWithFixedPortOrder();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(1));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_fixedPortOrderCrossingsAndNormalEdgeCrossings() {
        getInLayerEdgesWithFixedPortOrderAndNormalEdgeCrossings();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        order = order();
        assertThat(counter.countInLayerCrossingsOnBothSides(order[1]), is(2));

        switchOrder(0, 1, 1);

        assertThat(counter.countInLayerCrossingsOnBothSides(order[1]), is(1));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_ignoresSelfLoops() {
        getCrossWithManySelfLoopsGraph();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(0));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_ignoresCrossingsWhenPortOrderNotSet() {
        getInLayerEdgesCrossingsButNoFixedOrder();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(0));

    }

    @Test
    public void countInLayerCrossingsOnBothSides_ignoresCrossingsWhenPortOrderNotSetNoEdgeBetweenUpperAndLower() {
        getInLayerEdgesCrossingsNoFixedOrderNoEdgeBetweenUpperAndLower();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(2));

    }

    @Test
    public void countInLayerCrossingsOnBothSides_ignoreCrossingsWhenPortOrderNotSet() {
        getInLayerEdgesCrossingsNoFixedOrderNoEdgeBetweenUpperAndLowerUpsideDown();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(2));

    }

    @Test
    public void countInLayerCrossingsOnBothSides_crossingsOnBothSides() {
        getInLayerCrossingsOnBothSides();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(2));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_fixedPortOrderInLayerNoCrossings() {
        getFixedPortOrderInLayerEdgesDontCrossEachOther();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[0]), is(0));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_fixedPortOrderInLayerWithCrossings() {
        getFixedPortOrderInLayerEdgesWithCrossings();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[0]), is(1));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_oneLayerInLayerSwitchWouldReduceCrossings() {
        getOneLayerWithInLayerCrossings();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[0]), is(1));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_oneLayerInLayerNowCrossingReduction() {
        getInLayerOneLayerNoCrossings();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[0]), is(0));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_oneNode() {
        getOneNodeGraph();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[0]), is(0));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_moreComplex() {
        getMoreComplexInLayerGraph();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(6));

        // set port order as not fixed for layer in question.
        LGraph graph = getMoreComplexInLayerGraph();
        Layer secondLayer = graph.getLayers().get(1);
        for (LNode lNode : secondLayer) {
            lNode.setProperty(CoreOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_SIDE);
        }
        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(graph.toNodeArray()[1]), is(2));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_downwardInLayerEdgesOnLowerNode() {
        getInLayerEdgesFixedPortOrderInLayerAndInBetweenLayerCrossing();
        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(2));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_moreThanOneEdgeIntoAPort() {
        getInLayerEdgesMultipleEdgesIntoSinglePort();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(2));
    }

    /**
     * <pre>
     *  --* Two edges in wrong order from the same port
     *  |/
     *  ||
     *  |\
     *  | *
     *  \
     *   *
     * .
     * </pre>
     *
     * @return Graph of the form above.
     */
    @Test
    public void countInLayerCrossingsOnBothSides_moreThanOneEdgeOutOfAPort() {
        LNode[] nodes = addNodesToLayer(3, makeLayer(getGraph()));

        LPort port = addPortOnSide(nodes[0], PortSide.WEST);

        addInLayerEdge(port, nodes[1]);
        addInLayerEdge(port, nodes[2]);

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[0]), is(0));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_givenMultipleInBetweenLayerIntoNodeNoFixedPortOrder_NoCrossings() {
        multipleInBetweenLayerEdgesIntoNodeWithNoFixedPortOrder();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(0));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_givenMultipleInBetweenLayerIntoNodeNoFixedPortOrder_Crossings() {
        multipleInBetweenLayerEdgesIntoNodeWithNoFixedPortOrderCauseCrossings();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(2));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_oneLayerShouldNotCauseCrossings() {
        getInLayerOneLayerNoCrossings();
        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[0]), is(0));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_noPortOrderConstraintShouldResolveCrossing() {
        getInLayerEdgesDownwardGraphNoFixedOrder();
        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(0));
    }

    @Test
    public void countInLayerCrossingsOnBothSides_ignoreSelfLoops() {
        getCrossWithManySelfLoopsGraph();
        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countInLayerCrossingsOnBothSides(order()[1]), is(0));
    }

    /**
     * <pre>
     * ___  ___
     * | |\/| |
     * |_|/\|_|
     * </pre>
     */
    @Test
    public void countCrossingsBetweenLayers_fixedPortOrderCrossingOnTwoNodes() {
        LNode left = addNodeToLayer(makeLayer(getGraph()));
        LNode right = addNodeToLayer(makeLayer(getGraph()));
        eastWestEdgeFromTo(left, right);
        eastWestEdgeFromTo(left, right);
        setFixedOrderConstraint(right);
        setFixedOrderConstraint(left);

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countCrossingsBetweenLayers(order()[0], order()[1]), is(1));

    }

    @Test
    public void countCrossingsBetweenLayers_crossFormed() {
        getCrossFormedGraph();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countCrossingsBetweenLayers(order()[0], order()[1]), is(1));
    }

    private LNode[][] order() {
        return getGraph().toNodeArray();
    }

    @Test
    public void countCrossingsBetweenLayers_crossFormedMultipleEdgesBetweenSameNodes() {
        getMultipleEdgesBetweenSameNodesGraph();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countCrossingsBetweenLayers(order()[0], order()[1]), is(4));
    }

    @Test
    public void countCrossingsBetweenLayers_crossWithExtraEdgeInBetween() {
        getCrossWithExtraEdgeInBetweenGraph();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countCrossingsBetweenLayers(order()[0], order()[1]), is(3));
    }

    @Test
    public void countCrossingsBetweenLayers_ignoreSelfLoops() {
        getCrossWithManySelfLoopsGraph();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countCrossingsBetweenLayers(order()[0], order()[1]), is(1));
    }

    @Test
    public void countCrossingsBetweenLayers_moreComplexThreeLayerGraph() {
        getMoreComplexThreeLayerGraph();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countCrossingsBetweenLayers(order()[0], order()[1]), is(1));

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countCrossingsBetweenLayers(order()[1], order()[2]), is(2));
    }

    @Test
    public void countCrossingsBetweenLayers_fixedPortOrder() {
        getFixedPortOrderGraph();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countCrossingsBetweenLayers(order()[0], order()[1]), is(1));
    }

    @Test
    public void countCrossingsBetweenLayers_intoSamePort() {
        twoEdgesIntoSamePort();

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countCrossingsBetweenLayers(order()[0], order()[1]), is(2));
    }

    /**
     * <pre>
     * *  ____
     *  \/|  |
     *  /\|  |
     * *  |__|
     * </pre>
     */
    @Test
    public void givenCounterWhichAssumesFixedPortOrderCrossings_CountsCross() {
        LNode[] leftNodes = addNodesToLayer(2, makeLayer(getGraph()));
        LNode rightNode = addNodeToLayer(makeLayer(getGraph()));
        eastWestEdgeFromTo(leftNodes[0], rightNode);
        eastWestEdgeFromTo(leftNodes[1], rightNode);

        counter = CrossingsCounter.create(new int[getNumPorts(order())]);

        assertThat(counter.countCrossingsBetweenLayers(order()[0], order()[1]), is(0));

        counter = CrossingsCounter.createAssumingPortOrderFixed(new int[getNumPorts(order())]);

        assertThat(counter.countCrossingsBetweenLayers(order()[0], order()[1]), is(1));
    }

    /**
     * <pre>
     * *   /*
     * |  /
     * \ /____
     *  x/|  |
     * |/\|  |
     * *  |__|
     * </pre>
     */
    @Test
    public void givenCounterWhichAssumesFixedPortOrderCrossings_CountsPortCrossingsOnTwoPorts() {
        LNode[] leftNodes = addNodesToLayer(2, makeLayer(getGraph()));
        LNode[] rightNodes = addNodesToLayer(2, makeLayer(getGraph()));
        eastWestEdgeFromTo(leftNodes[0], rightNodes[1]);
        eastWestEdgeFromTo(leftNodes[1], rightNodes[1]);
        eastWestEdgeFromTo(leftNodes[1], rightNodes[0]);

        counter = CrossingsCounter.createAssumingPortOrderFixed(new int[getNumPorts(order())]);
        counter.initForCountingBetweenOnSide(leftNodes, rightNodes, PortSide.WEST);
        assertThat(counter.countCrossingsBetweenPorts(rightNodes[1].getPorts().get(1), rightNodes[1].getPorts().get(0)),
                is(1));
    }

    private int getNumPorts(final LNode[][] currentOrder) {
        int numPorts = 0;
        for (LNode[] lNodes : currentOrder) {
            for (LNode node : lNodes) {
                numPorts += node.getPorts().size();
            }
        }
        return numPorts;
    }

    private void switchOrder(final int indexOne, final int indexTwo, final int layerIndex) {
        LNode one = order[layerIndex][indexOne];
        order[layerIndex][indexOne] = order[layerIndex][indexTwo];
        order[layerIndex][indexTwo] = one;
    }
}
