/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 *
 * Copyright 2013 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 *
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package org.eclipse.elk.alg.layered.intermediate.greedyswitch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.core.options.PortSide;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests counting crosses of edges in between layers for neighbouring nodes.
 *
 * @author alan
 *
 */
public class BetweenLayerEdgeTwoNodeCrossingsCounterTest extends TestGraphCreator {
	private BetweenLayerEdgeTwoNodeCrossingsCounter crossingCounter;
	private LNode upperNode;
	private LNode lowerNode;
	private Layer layerToCountIn;
	private LNode[][] nodeOrder;

	// CHECKSTYLEOFF javadoc
	// CHECKSTYLEOFF MagicNumber

	@Test
	public void twoNodeNoEdges() {
		getTwoNodesNoConnectionGraph();

		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(0);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 0);
		setUpperNode(0);
		setLowerNode(1);

		assertBothSideUpperLowerCrossingsIs(0);
		assertBothSideLowerUpperCrossingsIs(0);
		assertWesternSideUpperLowerCrossingsIs(0);
		assertWesternSideLowerUpperCrossingsIs(0);
		assertEasternSideUpperLowerCrossingsIs(0);
		assertEasternSideLowerUpperCrossingsIs(0);
	}

	@Test
	public void crossFormed() {
		getCrossFormedGraph();

		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(1);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 1);
		setUpperNode(0);
		setLowerNode(1);

		assertBothSideUpperLowerCrossingsIs(1);
		assertBothSideLowerUpperCrossingsIs(0);
		assertWesternSideUpperLowerCrossingsIs(1);
		assertWesternSideLowerUpperCrossingsIs(0);
		assertEasternSideUpperLowerCrossingsIs(0);
		assertEasternSideLowerUpperCrossingsIs(0);
	}

	@Test
	public void oneNode() {
		getOneNodeGraph();

		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(0);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 0);
		setUpperNode(0);
		setLowerNode(0);

		assertBothSideUpperLowerCrossingsIs(0);
		assertBothSideLowerUpperCrossingsIs(0);
		assertWesternSideUpperLowerCrossingsIs(0);
		assertWesternSideLowerUpperCrossingsIs(0);
		assertEasternSideUpperLowerCrossingsIs(0);
		assertEasternSideLowerUpperCrossingsIs(0);
	}

	@Test
	public void crossFormedMultipleEdgesBetweenSameNodes() {
		getMultipleEdgesBetweenSameNodesGraph();

		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(1);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 1);
		setUpperNode(0);
		setLowerNode(1);

		assertBothSideUpperLowerCrossingsIs(4);
		assertBothSideLowerUpperCrossingsIs(0);
		assertWesternSideUpperLowerCrossingsIs(4);
		assertWesternSideLowerUpperCrossingsIs(0);
		assertEasternSideUpperLowerCrossingsIs(0);
		assertEasternSideLowerUpperCrossingsIs(0);
	}

	@Test
	public void crossWithExtraEdgeInBetween() {
		getCrossWithExtraEdgeInBetweenGraph();

		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(1);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 1);
		setUpperNode(0);
		setLowerNode(2);

		assertBothSideUpperLowerCrossingsIs(1);
		assertBothSideLowerUpperCrossingsIs(0);
		assertWesternSideUpperLowerCrossingsIs(1);
		assertWesternSideLowerUpperCrossingsIs(0);
		assertEasternSideUpperLowerCrossingsIs(0);
		assertEasternSideLowerUpperCrossingsIs(0);
	}

	@Test
	public void ignoreInLayerEdges() {
		getInLayerEdgesGraph();

		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(1);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 1);
		setUpperNode(0);
		setLowerNode(2);

		assertBothSideUpperLowerCrossingsIs(0);
		assertBothSideLowerUpperCrossingsIs(0);
		assertWesternSideUpperLowerCrossingsIs(0);
		assertWesternSideLowerUpperCrossingsIs(0);
		assertEasternSideUpperLowerCrossingsIs(0);
		assertEasternSideLowerUpperCrossingsIs(0);
	}

	@Test
	public void ignoreSelfLoops() {
		getCrossWithManySelfLoopsGraph();

		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(1);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 1);
		setUpperNode(0);
		setLowerNode(1);

		assertBothSideUpperLowerCrossingsIs(1);
		assertBothSideLowerUpperCrossingsIs(0);
		assertWesternSideUpperLowerCrossingsIs(1);
		assertWesternSideLowerUpperCrossingsIs(0);
		assertEasternSideUpperLowerCrossingsIs(0);
		assertEasternSideLowerUpperCrossingsIs(0);
	}

	// @formatter:off
	/**
	 * *\ --* \/ / *-*===* + / * * --*
	 *
	 * @return
	 */
	// @formatter:on
	@Test
	public void moreComplexThreeLayerGraph() {
		getMoreComplexThreeLayerGraph();

		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(1);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 1);
		setUpperNode(0);
		setLowerNode(1);

		assertWesternSideUpperLowerCrossingsIs(1);
		assertWesternSideLowerUpperCrossingsIs(1);
		assertEasternSideUpperLowerCrossingsIs(2);
		assertEasternSideLowerUpperCrossingsIs(3);
		assertBothSideUpperLowerCrossingsIs(3);
		assertBothSideLowerUpperCrossingsIs(4);
	}

	@Test
	public void fixedPortOrder() {
		getFixedPortOrderGraph();

		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(1);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 1);
		setUpperNode(0);
		setLowerNode(1);

		assertEasternSideUpperLowerCrossingsIs(0);
		assertEasternSideLowerUpperCrossingsIs(0);
		assertWesternSideUpperLowerCrossingsIs(1);
		assertWesternSideLowerUpperCrossingsIs(0);
		assertBothSideUpperLowerCrossingsIs(1);
		assertBothSideLowerUpperCrossingsIs(0);
	}

	@Test
	public void switchThreeTimes() {
		LNode[] leftNodes = addNodesToLayer(2, makeLayer(graph));
		LNode[] rightNodes = addNodesToLayer(4, makeLayer(graph));

		eastWestEdgeFromTo(leftNodes[1], rightNodes[0]);
		eastWestEdgeFromTo(leftNodes[1], rightNodes[2]);

		eastWestEdgeFromTo(leftNodes[0], rightNodes[0]);
		eastWestEdgeFromTo(leftNodes[0], rightNodes[1]);
		eastWestEdgeFromTo(leftNodes[0], rightNodes[3]);

		setUpIds();
		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(0);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 0);
		setUpperNode(0);
		setLowerNode(1);

		assertEasternSideUpperLowerCrossingsIs(3);
		assertEasternSideLowerUpperCrossingsIs(2);
		assertWesternSideUpperLowerCrossingsIs(0);
		assertWesternSideLowerUpperCrossingsIs(0);
		assertBothSideUpperLowerCrossingsIs(3);
		assertBothSideLowerUpperCrossingsIs(2);
	}

	@Test
	public void intoSamePort() {
		twoEdgesIntoSamePort();
		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(1);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 1);
		setUpperNode(0);
		setLowerNode(1);

		assertEasternSideUpperLowerCrossingsIs(0);
		assertEasternSideLowerUpperCrossingsIs(0);
		assertWesternSideUpperLowerCrossingsIs(2);
		assertWesternSideLowerUpperCrossingsIs(0);
		assertBothSideUpperLowerCrossingsIs(2);
		assertBothSideLowerUpperCrossingsIs(0);
	}

	@Test
	public void intoSamePortCausesCrossingsOnSwitch() {
		twoEdgesIntoSamePortCrossesWhenSwitched();
		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(0);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 0);
		setUpperNode(0);
		setLowerNode(1);

		assertEasternSideUpperLowerCrossingsIs(0);
		assertEasternSideLowerUpperCrossingsIs(1);
		assertWesternSideUpperLowerCrossingsIs(0);
		assertWesternSideLowerUpperCrossingsIs(0);
		assertBothSideUpperLowerCrossingsIs(0);
		assertBothSideLowerUpperCrossingsIs(1);
	}

	@Test
	public void intoSamePortReducesCrossingsOnSwitch() {
		twoEdgesIntoSamePortResolvesCrossingWhenSwitched();
		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(0);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 0);
		setUpperNode(0);
		setLowerNode(1);

		assertEasternSideUpperLowerCrossingsIs(1);
		assertEasternSideLowerUpperCrossingsIs(0);
		assertWesternSideUpperLowerCrossingsIs(0);
		assertWesternSideLowerUpperCrossingsIs(0);
		assertBothSideUpperLowerCrossingsIs(1);
		assertBothSideLowerUpperCrossingsIs(0);
	}

	@Test
	public void intoSamePortFromEastSwitchWithFixedPortOrder() {
		twoEdgesIntoSamePortFromEastWithFixedPortOrder();
		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(0);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 0);
		setUpperNode(0);
		setLowerNode(1);

		assertEasternSideUpperLowerCrossingsIs(0);
		assertEasternSideLowerUpperCrossingsIs(1);
		assertWesternSideUpperLowerCrossingsIs(0);
		assertWesternSideLowerUpperCrossingsIs(0);
		assertBothSideUpperLowerCrossingsIs(0);
		assertBothSideLowerUpperCrossingsIs(1);
	}

	@Test
	public void multipleEdgesAndSingleEdgeShouldAlwaysReturnSameValue() {
		multipleEdgesAndSingleEdge();
		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(0);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 0);
		setUpperNode(0);
		setLowerNode(1);

		assertEasternSideUpperLowerCrossingsIs(0);
		assertEasternSideUpperLowerCrossingsIs(0);
	}

	/**
	 * This behavior is not supported by the
	 * BetweenLayerEdgeTwoNodeCrossingsCounter.
	 *
	 * <pre>
	 * ___   ____
	 * | o---o  |
	 * | |\  |  |
	 * |_o-+-o  |
	 *     | |__|
	 *      \
	 *       *
	 * </pre>
	 *
	 * Right layer has fixed port order, left has free.
	 */
	@Ignore
	public void partlyFixedPartlyUnfixedPortOrder() {
		LNode leftNode = addNodeToLayer(makeLayer(graph));
		LNode[] rightNodes = addNodesToLayer(2, makeLayer(graph));
		setFixedOrderConstraint(rightNodes[0]);
		setFixedOrderConstraint(rightNodes[1]);
		LPort leftTopPort = addPortOnSide(leftNode, PortSide.EAST);
		LPort leftLowerPort = addPortOnSide(leftNode, PortSide.EAST);
		LPort rightUpperNodeLowerPort = addPortOnSide(rightNodes[0], PortSide.WEST);
		LPort rightUpperNodeTopPort = addPortOnSide(rightNodes[0], PortSide.WEST);
		LPort rightLowerPort = addPortOnSide(rightNodes[1], PortSide.WEST);
		addEdgeBetweenPorts(leftTopPort, rightUpperNodeTopPort);
		addEdgeBetweenPorts(leftTopPort, rightLowerPort);
		addEdgeBetweenPorts(leftLowerPort, rightUpperNodeLowerPort);

		nodeOrder = graph.toNodeArray();
		layerToCountIn = graph.getLayers().get(1);
		crossingCounter = BetweenLayerEdgeTwoNodeCrossingsCounter.create(nodeOrder, 1);
		setUpperNode(0);
		setLowerNode(1);

		assertBothSideUpperLowerCrossingsIs(1);
	}

	private void assertEasternSideLowerUpperCrossingsIs(final int expectedCrossings) {
		crossingCounter.countEasternEdgeCrossings(upperNode, lowerNode);
		int crossings = crossingCounter.getLowerUpperCrossings();
		assertThat(failMessage(" east, lower upper "), crossings, is(expectedCrossings));
	}

	private void assertEasternSideUpperLowerCrossingsIs(final int expectedCrossings) {
		crossingCounter.countEasternEdgeCrossings(upperNode, lowerNode);
		int crossings = crossingCounter.getUpperLowerCrossings();
		assertThat(failMessage(" east, upper lower "), crossings, is(expectedCrossings));
	}

	private void assertWesternSideLowerUpperCrossingsIs(final int expectedCrossings) {
		crossingCounter.countWesternEdgeCrossings(upperNode, lowerNode);
		int crossings = crossingCounter.getLowerUpperCrossings();
		assertThat(failMessage(" west, lower upper "), crossings, is(expectedCrossings));
	}

	private void assertWesternSideUpperLowerCrossingsIs(final int expectedCrossings) {
		crossingCounter.countWesternEdgeCrossings(upperNode, lowerNode);
		int crossings = crossingCounter.getUpperLowerCrossings();
		assertThat(failMessage(" west, upper lower "), crossings, is(expectedCrossings));
	}

	private void assertBothSideLowerUpperCrossingsIs(final int expectedCrossings) {
		crossingCounter.countBothSideCrossings(upperNode, lowerNode);
		int crossings = crossingCounter.getLowerUpperCrossings();
		assertThat(failMessage(" both, lower upper "), crossings, is(expectedCrossings));
	}

	private void assertBothSideUpperLowerCrossingsIs(final int expectedCrossings) {
		crossingCounter.countBothSideCrossings(upperNode, lowerNode);
		int crossings = crossingCounter.getUpperLowerCrossings();
		assertThat(failMessage(" both, upper lower "), crossings, is(expectedCrossings));
	}

	private void setUpperNode(final int nodeIndex) {
		List<LNode> nodes = layerToCountIn.getNodes();
		upperNode = nodes.get(nodeIndex);
	}

	private void setLowerNode(final int nodeIndex) {
		List<LNode> nodes = layerToCountIn.getNodes();
		lowerNode = nodes.get(nodeIndex);
	}

	private String failMessage(final String configuration) {
		return "With configuration: " + configuration + " in Layer " + layerToCountIn
				+ " between the nodes: Upper node " + upperNode.getIndex() + " and lower Node " + lowerNode.getIndex();
	}
}
