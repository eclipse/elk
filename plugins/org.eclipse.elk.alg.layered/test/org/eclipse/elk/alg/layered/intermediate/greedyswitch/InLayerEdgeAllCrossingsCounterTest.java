/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 *
 * Copyright 2013 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 *
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package org.eclipse.elk.alg.layered.intermediate.greedyswitch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.p3order.counting.InLayerEdgeAllCrossingsCounter;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.junit.Test;

/**
 * Tests if crossing counter counts all in-layer crossings in layer correctly.
 *
 * @author alan
 *
 */
public class InLayerEdgeAllCrossingsCounterTest extends InLayerEdgeTestGraphCreator {

	private LNode[] nodeOrder;
	private InLayerEdgeAllCrossingsCounter counter;

	// CHECKSTYLEOFF MagicNumber
	// CHECKSTYLEOFF javadoc

	@Test
	public void ignoresInBetweenLayerEdges() {
		getCrossFormedGraph();
		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(0));
	}

	@Test
	public void countInLayerEdgeWithNormalEdgeCrossing() {
		getInLayerEdgesGraph();
		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(1));
	}

	@Test
	public void crossingsWhenSwitched() {
		getInLayerEdgesGraphWhichResultsInCrossingsWhenSwitched();

		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(0));

		switchOrder(1, 2);

		assertThat(counter.countCrossings(), is(1));
	}

	@Test
	public void fixedPortOrderNodeToNode() {
		getInLayerEdgesGraphWithCrossingsToBetweenLayerEdgeWithFixedPortOrder();
		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(1));
	}

	@Test
	public void fixedPortOrderNodeToNodeLowerNode() {
		getInLayerEdgesGraphWithCrossingsToBetweenLayerEdgeWithFixedPortOrder();

		initCrossingCounterForLayerIndex(1);
		switchOrder(0, 1);

		assertThat(counter.countCrossings(), is(2));
	}

	@Test
	public void fixedPortOrderCrossingsAndNormalEdgeCrossings() {
		getInLayerEdgesWithFixedPortOrderAndNormalEdgeCrossings();

		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(2));

		switchOrder(0, 1);

		assertThat(counter.countCrossings(), is(1));
	}

	@Test
	public void ignoresSelfLoops() {
		getCrossWithManySelfLoopsGraph();

		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(0));
	}

	@Test
	public void ignoresCrossingsWhenPortOrderNotSet() {
		getInLayerEdgesCrossingsButNoFixedOrder();

		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(0));

	}

	@Test
	public void ignoresCrossingsWhenPortOrderNotSetNoEdgeBetweenUpperAndLower() {
		getInLayerEdgesCrossingsNoFixedOrderNoEdgeBetweenUpperAndLower();

		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(2));

	}

	@Test
	public void ignoresCrossingsWhenPortOrderNotSetNoEdgeBetweenUpperAndLowerLowerUpsideDown() {
		getInLayerEdgesCrossingsNoFixedOrderNoEdgeBetweenUpperAndLowerUpsideDown();

		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(2));

	}

	@Test
	public void crossingsOnBothSides() {
		getInLayerCrossingsOnBothSides();

		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(2));
	}

	@Test
	public void fixedPortOrderInLayerNoCrossings() {
		getFixedPortOrderInLayerEdgesDontCrossEachOther();

		initCrossingCounterForLayerIndex(0);

		assertThat(counter.countCrossings(), is(0));
	}

	@Test
	public void fixedPortOrderInLayerWithCrossings() {
		getFixedPortOrderInLayerEdgesWithCrossings();

		initCrossingCounterForLayerIndex(0);

		assertThat(counter.countCrossings(), is(1));
	}

	@Test
	public void oneLayerInLayerSwitchWouldReduceCrossings() {
		getOneLayerWithInLayerCrossings();

		initCrossingCounterForLayerIndex(0);

		assertThat(counter.countCrossings(), is(1));
	}

	@Test
	public void oneLayerInLayerNowCrossingReduction() {
		getInLayerOneLayerNoCrossings();

		initCrossingCounterForLayerIndex(0);

		assertThat(counter.countCrossings(), is(0));
	}

	@Test
	public void oneNode() {
		getOneNodeGraph();

		initCrossingCounterForLayerIndex(0);

		assertThat(counter.countCrossings(), is(0));
	}

	@Test
	public void moreComplex() {
		getMoreComplexInLayerGraph();

		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(6));

		// set port order as not fixed for layer in question.
		LGraph graph = getMoreComplexInLayerGraph();
		Layer secondLayer = graph.getLayers().get(1);
		for (LNode lNode : secondLayer) {
			lNode.setProperty(CoreOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_SIDE);
		}
		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(2));
	}

	@Test
	public void downwardInLayerEdgesOnLowerNode() {
		getInLayerEdgesFixedPortOrderInLayerAndInBetweenLayerCrossing();
		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(2));
	}

	@Test
	public void moreThanOneEdgeIntoAPort() {
		getInLayerEdgesMultipleEdgesIntoSinglePort();

		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(2));
	}

	@Test
	public void givenMultipleInBetweenLayerEdgesIntoNodeWithNoFixedPortOrder_CountsNoCrossings() {
		multipleInBetweenLayerEdgesIntoNodeWithNoFixedPortOrder();

		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(0));
	}

	@Test
	public void givenMultipleInBetweenLayerEdgesIntoNodeWithNoFixedPortOrder_CountsCrossings() {
		multipleInBetweenLayerEdgesIntoNodeWithNoFixedPortOrderCauseCrossings();

		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(2));
	}

	@Test
	public void oneLayerShouldNotCauseCrossings() {
		getInLayerOneLayerNoCrossings();
		initCrossingCounterForLayerIndex(0);

		assertThat(counter.countCrossings(), is(0));
	}

	@Test
	public void noPortOrderConstraintShouldResolveCrossing() {
		getInLayerEdgesDownwardGraphNoFixedOrder();
		initCrossingCounterForLayerIndex(1);

		assertThat(counter.countCrossings(), is(0));
	}

	/**
	 * Initializes Crossing counter, sets nodeOrder with the nodes from the
	 * layer being considered and numbers its in ascending form as required by
	 * the counter.
	 *
	 * @param layerIndex
	 */
	private void initCrossingCounterForLayerIndex(final int layerIndex) {
		LNode[][] currentOrder = graph.toNodeArray();
		nodeOrder = currentOrder[layerIndex];
		counter = InLayerEdgeAllCrossingsCounter.create(nodeOrder);
	}

	private void switchOrder(final int indexOne, final int indexTwo) {
		LNode one = nodeOrder[indexOne];
		nodeOrder[indexOne] = nodeOrder[indexTwo];
		nodeOrder[indexTwo] = one;
	}
}
