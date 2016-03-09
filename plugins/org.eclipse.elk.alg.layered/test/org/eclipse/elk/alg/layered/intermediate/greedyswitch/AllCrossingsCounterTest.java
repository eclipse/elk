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

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.core.options.PortSide;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for extracted and modified AllCrossingsCounter.
 *
 * @author alan
 *
 */
public class AllCrossingsCounterTest extends TestGraphCreator {

	private LGraph graph;
	private AllCrossingsCounter counter;

	// CHECKSTYLEOFF Javadoc
	// CHECKSTYLEOFF MagicNumber
	@Before
	public void setUp() {
		graph = new LGraph();
	}

	@Test
	public void countOneCrossing() {
		graph = getCrossFormedGraph();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(1));
	}

	@Test
	public void countInLayerCrossing() {
		graph = getInLayerEdgesGraph();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(1));
	}

	@Test
	public void countInLayerCrossingAndSwitch() {
		graph = getInLayerEdgesGraph();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(1));
	}

	@Test
	public void countNorthSouthCrossing() {
		graph = new NorthSouthEdgeTestGraphCreator().getNorthSouthDownwardCrossingGraph();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(1));
	}

	@Test
	public void countNorthernNorthSouthCrossing() {
		graph = new NorthSouthEdgeTestGraphCreator().getNorthSouthUpwardCrossingGraph();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(1));
	}

	@Test
	public void northSouthDummyEdgeCrossing() {
		graph = new NorthSouthEdgeTestGraphCreator().getSouthernNorthSouthDummyEdgeCrossingGraph();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(1));
	}

	@Test
	public void switchAndCountTwice() {
		graph = getCrossFormedGraph();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(1));
		switchNodesInLayer(0, 1, 1);
		amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(0));
	}

	private LNode[][] switchNodesInLayer(final int upperNodeIndex, final int lowerNodeIndex, final int layerIndex) {
		Layer layer = graph.getLayers().get(layerIndex);
		List<LNode> nodes = layer.getNodes();
		LNode upperNode = nodes.get(upperNodeIndex);
		// counter.notifyOfSwitch(upperNode, nodes.get(lowerNodeIndex),
		// layerIndex);

		nodes.set(upperNodeIndex, nodes.get(lowerNodeIndex));
		nodes.set(lowerNodeIndex, upperNode);
		return getCurrentOrder(graph);
	}

	@Test
	public void tooManyInLayerCrossingsWithTheOldMethod() {
		graph = new InLayerEdgeTestGraphCreator().getInLayerOneLayerNoCrossings();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(0));
	}

	@Test
	public void countCrossingsWithMultipleEdgesBetweenSameNodes() {
		graph = getMultipleEdgesBetweenSameNodesGraph();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(4));
	}

	@Test
	public void countCrossingsInEmptyGraph() {
		graph = getEmptyGraph();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(0));
	}

	@Test
	public void ignoresCrossingsWithoutFixedPortOrder() {
		graph = getGraphNoCrossingsDueToPortOrderNotFixed();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(0));
	}

	@Test
	public void oneNodeIsLongEdgeDummy() {
		graph = new NorthSouthEdgeTestGraphCreator().getSouthernNorthSouthDummyEdgeCrossingGraph();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(1));
	}

	@Test
	public void oneNodeIsLongEdgeDummyNorthern() {
		graph = new NorthSouthEdgeTestGraphCreator().getNorthernNorthSouthDummyEdgeCrossingGraph();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(1));
	}

	@Test
	public void multipleNorthSouthAndLongEdgeDummiesOnBothSides() {
		graph = new NorthSouthEdgeTestGraphCreator().getMultipleNorthSouthAndLongEdgeDummiesOnBothSides();
		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(4));
	}

	/**
	 * <pre>
	*   --*
	*   | ____
	*   |/|  |
	*   /\|  |
	*   | |  |
	*   | |__|
	*   |
	*    \
	*     *
	 * </pre>
	 */
	@Test
	public void inLayerCrossingsOnFarLeft() {
		LNode[] nodes = addNodesToLayer(3, makeLayer(graph));

		setFixedOrderConstraint(nodes[1]);

		addInLayerEdge(nodes[0], nodes[1], PortSide.WEST);
		addInLayerEdge(nodes[1], nodes[2], PortSide.WEST);

		int amountOfCrossings = whenCountingAllCrossings();
		assertThat(amountOfCrossings, is(1));
	}

	private int whenCountingAllCrossings() {
		counter = AllCrossingsCounter.create();
		LNode[][] nodeArray = graph.toNodeArray();
		int portId = 0;
		for (LNode[] lNodes : nodeArray) {
			for (LNode lNode : lNodes) {
				for (LPort port : lNode.getPorts()) {
					port.id = portId;
					portId++;
				}
			}
		}
		boolean[] useHyperEdgeCounterInLayer = AllCrossingsCounter.getHyperedges(nodeArray, PortSide.EAST);
		return counter.countAllCrossingsInGraphWithOrder(nodeArray, useHyperEdgeCounterInLayer, portId); // TODO-alan
	}
}
