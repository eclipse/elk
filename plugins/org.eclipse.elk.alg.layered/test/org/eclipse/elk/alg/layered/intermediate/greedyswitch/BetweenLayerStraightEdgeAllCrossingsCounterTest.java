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

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.p3order.counting.BarthJuengerMutzelCrossingsCounter;
import org.junit.Test;

/**
 * Tests straight edge all crossings counter.
 *
 * @author alan
 *
 */
public class BetweenLayerStraightEdgeAllCrossingsCounterTest extends TestGraphCreator {
	private BarthJuengerMutzelCrossingsCounter crossingCounter;
	private LNode[][] nodeOrder;
	private LNode[] leftLayer;
	private LNode[] rightLayer;

	// CHECKSTYLEOFF Javadoc
	// CHECKSTYLEOFF MagicNumber
	@Test
	public void twoNodeNoEdges() {
		getTwoNodesNoConnectionGraph();
		nodeOrder = graph.toNodeArray();
		leftLayer = nodeOrder[0];
		rightLayer = nodeOrder[0];
		initCrossingCounter();

		int crossingCount = crossingCounter.countCrossings(leftLayer, rightLayer);

		assertThat(crossingCount, is(0));
	}

	@Test
	public void crossFormed() {
		getCrossFormedGraph();
		setLeftAndRightLayer();
		initCrossingCounter();

		int crossingCount = crossingCounter.countCrossings(leftLayer, rightLayer);

		assertThat(crossingCount, is(1));
	}

	@Test
	public void oneNode() {
		getOneNodeGraph();
		nodeOrder = graph.toNodeArray();
		leftLayer = nodeOrder[0];
		rightLayer = nodeOrder[0];
		initCrossingCounter();

		int crossingCount = crossingCounter.countCrossings(leftLayer, rightLayer);

		assertThat(crossingCount, is(0));
	}

	@Test
	public void crossFormedMultipleEdgesBetweenSameNodes() {
		getMultipleEdgesBetweenSameNodesGraph();
		setLeftAndRightLayer();
		initCrossingCounter();

		int crossingCount = crossingCounter.countCrossings(leftLayer, rightLayer);

		assertThat(crossingCount, is(4));
	}

	@Test
	public void crossWithExtraEdgeInBetween() {
		getCrossWithExtraEdgeInBetweenGraph();
		setLeftAndRightLayer();
		initCrossingCounter();

		int crossingCount = crossingCounter.countCrossings(leftLayer, rightLayer);

		assertThat(crossingCount, is(3));
	}

	@Test
	public void ignoreInLayerEdges() {
		getInLayerEdgesGraph();
		nodeOrder = graph.toNodeArray();
		leftLayer = nodeOrder[0];
		rightLayer = nodeOrder[0];
		initCrossingCounter();

		int crossingCount = crossingCounter.countCrossings(leftLayer, rightLayer);

		assertThat(crossingCount, is(0));
	}

	@Test
	public void ignoreSelfLoops() {
		getCrossWithManySelfLoopsGraph();
		setLeftAndRightLayer();
		initCrossingCounter();

		int crossingCount = crossingCounter.countCrossings(leftLayer, rightLayer);

		assertThat(crossingCount, is(1));
	}

	@Test
	public void moreComplexThreeLayerGraph() {
		getMoreComplexThreeLayerGraph();
		setLeftAndRightLayer();
		initCrossingCounter();

		int crossingCount = crossingCounter.countCrossings(leftLayer, rightLayer);

		assertThat(crossingCount, is(1));

		nodeOrder = graph.toNodeArray();
		leftLayer = nodeOrder[1];
		rightLayer = nodeOrder[2];
		initCrossingCounter();

		crossingCount = crossingCounter.countCrossings(leftLayer, rightLayer);

		assertThat(crossingCount, is(2));
	}

	@Test
	public void fixedPortOrder() {
		getFixedPortOrderGraph();
		setLeftAndRightLayer();
		initCrossingCounter();

		int crossingCount = crossingCounter.countCrossings(leftLayer, rightLayer);

		assertThat(crossingCount, is(1));
	}

	@Test
	public void intoSamePort() {
		twoEdgesIntoSamePort();
		setLeftAndRightLayer();
		initCrossingCounter();

		int crossingCount = crossingCounter.countCrossings(leftLayer, rightLayer);

		assertThat(crossingCount, is(2));
	}

	/**
	 * <pre>
	 *  *  ____
	 *   \/|  |
	 *   /\|  |
	 *  *  |__|
	 *      ^
	 *      |
	 *      port order fixed
	 * </pre>
	 */
	@Test
	public void partlyFixedPartlyUnfixedPortOrder2() {
		LNode[] leftNodes = addNodesToLayer(2, makeLayer(graph));
		LNode rightNode = addNodeToLayer(makeLayer(graph));
		setFixedOrderConstraint(rightNode);
		eastWestEdgeFromTo(leftNodes[0], rightNode);
		eastWestEdgeFromTo(leftNodes[1], rightNode);

		setLeftAndRightLayer();
		initCrossingCounter();

		int crossingCount = crossingCounter.countCrossings(leftLayer, rightLayer);

		assertThat(crossingCount, is(1));
	}

	private int setIds() {
		int portNum = 0;
		for (LNode[] lNodes : nodeOrder) {
			for (LNode lNode : lNodes) {
				for (LPort port : lNode.getPorts()) {
					port.id = portNum++;
				}
			}
		}
		return portNum;
	}

	private void setLeftAndRightLayer() {
		nodeOrder = graph.toNodeArray();
		leftLayer = nodeOrder[0];
		rightLayer = nodeOrder[1];
	}

	private void initCrossingCounter() {
		int portNum = setIds();
		LNode[][] currentNodeOrder = graph.toNodeArray();
		boolean[] hasNorthSouthPorts = new boolean[currentNodeOrder.length];
		int[] inLayerEdgeCount = new int[currentNodeOrder.length];
		for (int i = 0; i < currentNodeOrder.length; i++) {
			LNode[] layerArr = currentNodeOrder[i];
			for (LNode node : layerArr) {
				if (node.getType() == NodeType.NORTH_SOUTH_PORT) {
					hasNorthSouthPorts[i] = true;
				}
				for (LPort port : node.getPorts()) {
					for (LEdge edge : port.getOutgoingEdges()) {
						if (edge.getTarget().getNode().getLayer() == edge.getSource().getNode().getLayer()) {
							inLayerEdgeCount[i]++;
						}
					}
				}
			}
		}
		crossingCounter = new BarthJuengerMutzelCrossingsCounter(inLayerEdgeCount, hasNorthSouthPorts,
				new int[portNum]);
	}

}
