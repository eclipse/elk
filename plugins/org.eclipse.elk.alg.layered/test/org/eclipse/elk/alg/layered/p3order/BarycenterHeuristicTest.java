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
package org.eclipse.elk.alg.layered.p3order;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.TestGraphCreator;
import org.eclipse.elk.alg.layered.p3order.BarycenterHeuristic.BarycenterState;
import org.eclipse.elk.alg.layered.p3order.constraints.ForsterConstraintResolver;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.PortType;
import org.eclipse.elk.core.options.PortSide;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

// CHECKSTYLEOFF javadoc
// CHECKSTYLEOFF MagicNumber
// CHECKSTYLEOFF MethodName
public class BarycenterHeuristicTest extends TestGraphCreator {
    private MockRandom random;
    @Before
    public void setUp() {
        random = new MockRandom();
    }

    /**
     * <pre>
     * *  *
     *  \/
     *  /\
     * *  *
     * .
     * </pre>
     *
     * @return Graph of the form above.
     */
    @Test
    public void minimizeCrossings_removesCrossingInSimpleCross() {
        LNode[] leftNodes = addNodesToLayer(2, makeLayer());
        LNode[] rightNodes = addNodesToLayer(2, makeLayer());
        eastWestEdgeFromTo(leftNodes[0], rightNodes[1]);
        eastWestEdgeFromTo(leftNodes[1], rightNodes[0]);
        setUpIds();
        LNode[][] nodes = getGraph().toNodeArray();

        float[] portRanks = new float[4];
        NodeRelativePortDistributor portDist = new NodeRelativePortDistributor(portRanks, initNodePositions());
        portDist.calculatePortRanks(nodes[0], PortType.OUTPUT);
        BarycenterState[][] barycenters = getBarycenterArr();
        BarycenterHeuristic crossMin =
                new BarycenterHeuristic(barycenters, new MockConstraintResolver(), random, portRanks);

        LNode[] expectedOrder = switchOrderInArray(0, 1, nodes[1]);

        minimizeCrossings(crossMin, nodes[1], false, false, true);

        assertThat(expectedOrder, is(nodes[1]));
    }

    /**
     * <pre>
     * *  *
     *  \/
     *  /\
     * *  *
     * .
     * </pre>
     *
     * @return Graph of the form above.
     */
    @Test
    public void mockRandomizeFirstLayer() {
        LNode[] leftNodes = addNodesToLayer(2, makeLayer());
        LNode[] rightNodes = addNodesToLayer(2, makeLayer());
        eastWestEdgeFromTo(leftNodes[0], rightNodes[1]);
        eastWestEdgeFromTo(leftNodes[1], rightNodes[0]);
        setUpIds();

        LNode[][] nodes = getGraph().toNodeArray();
        float[] portRanks = new float[4];
        BarycenterState[][] barycenters = getBarycenterArr();
        BarycenterHeuristic crossMin =
                new BarycenterHeuristic(barycenters, new MockConstraintResolver(), random, portRanks);

        LNode[] expectedOrder = Arrays.copyOf(nodes[0], nodes[0].length);
        LNode[] expectedSwitchedOrder = switchOrderInArray(0, 1, nodes[0]);

        minimizeCrossings(crossMin, nodes[0], false, true, true);
        assertThat(expectedOrder, is(nodes[0]));

        random.setChangeBy(-0.01);
        minimizeCrossings(crossMin, nodes[0], false, true, true);
        assertThat(nodes[0], is(expectedSwitchedOrder));
    }

    private BarycenterState[][] getBarycenterArr() {
        BarycenterState[][] barycenters = new BarycenterState[getGraph().getLayers().size()][];
        int i = 0;
        for (Layer l : getGraph().getLayers()) {
            barycenters[i] = new BarycenterState[l.getNodes().size()];
            l.id = i;

            int j = 0;
            for (LNode node : l) {
                node.id = j;
                barycenters[i][j] = new BarycenterState(node);
                j++;
            }

            i++;
        }
        return barycenters;
    }

    /**
     * <pre>
     *   *  *
     *    \/
     *    /\
     * *-*  *
     * .
     * </pre>
     *
     * @return Graph of the form above.
     */
    @Test
    public void fillingInUnknownBarycenters() {
        LNode leftNode = addNodeToLayer(makeLayer());
        LNode[] middleNodes = addNodesToLayer(2, makeLayer());
        LNode[] rightNodes = addNodesToLayer(2, makeLayer());
        eastWestEdgeFromTo(middleNodes[0], rightNodes[1]);
        eastWestEdgeFromTo(middleNodes[1], rightNodes[0]);
        eastWestEdgeFromTo(leftNode, middleNodes[1]);
        setUpIds();

        LNode[][] nodes = getGraph().toNodeArray();
        float[] portRanks = new float[6];
        BarycenterState[][] barycenters = getBarycenterArr();
        BarycenterHeuristic crossMin =
                new BarycenterHeuristic(barycenters, new MockConstraintResolver(), random, portRanks);

        LNode[] expectedSwitchedOrder = switchOrderInArray(0, 1, nodes[2]);
        LNode[] expectedOrderSecondLayer = Arrays.copyOf(nodes[1], nodes[1].length);

        NodeRelativePortDistributor portDist = new NodeRelativePortDistributor(portRanks, initNodePositions());
        portDist.calculatePortRanks(nodes[0], PortType.OUTPUT);
        minimizeCrossings(crossMin, nodes[0], false, true, true);

        portDist.calculatePortRanks(nodes[1], PortType.OUTPUT);
        minimizeCrossings(crossMin, nodes[1], false, false, true);
        assertThat(nodes[1], is(expectedOrderSecondLayer));

        portDist.calculatePortRanks(nodes[2], PortType.OUTPUT);
        minimizeCrossings(crossMin, nodes[2], false, false, true);

        assertThat(nodes[2], is(expectedSwitchedOrder));
    }


    /**
     * @return
     */
    private int[][] initNodePositions() {
        int lp = 0;
        int[][] pos = new int[graph.getLayers().size()][];
        for (Layer layer : graph) {
            layer.id = lp++;
            pos[layer.id] = new int[layer.getNodes().size()];
            List<LNode> nodes = layer.getNodes();
            for (int i = 0; i < nodes.size(); i++) {
                LNode node = nodes.get(i);
                node.id = i;
                pos[layer.id][node.id] = i;
            }
        }
        return pos;
    }

    /**
     * <pre>
     * ____  *
     * |  |\/
     * |__|/\
     *       *
     * .
     * </pre>
     *
     * @return Graph of the form above.
     */
    @Test
    public void assumingFixedPortOrder_givenSimplePortOrderCross_removesCrossingIndependentOfRandom() {
        Layer leftLayer = makeLayer(getGraph());
        Layer rightLayer = makeLayer(getGraph());

        LNode leftNode = addNodeToLayer(leftLayer);

        LNode rightTopNode = addNodeToLayer(rightLayer);
        LNode rightBottomNode = addNodeToLayer(rightLayer);

        eastWestEdgeFromTo(leftNode, rightBottomNode);
        eastWestEdgeFromTo(leftNode, rightTopNode);

        setFixedOrderConstraint(leftNode);

        setUpIds();

        LNode[][] nodes = getGraph().toNodeArray();

        float[] portRanks = new float[4];
        NodeRelativePortDistributor portDist = new NodeRelativePortDistributor(portRanks, initNodePositions());
        portDist.calculatePortRanks(nodes[0], PortType.OUTPUT);
        BarycenterState[][] barycenters = getBarycenterArr();
        BarycenterHeuristic crossMin =
                new BarycenterHeuristic(barycenters, new MockConstraintResolver(), random, portRanks);

        LNode[] expectedOrder = switchOrderInArray(0, 1, nodes[1]);

        minimizeCrossings(crossMin, nodes[1], false, false, true);
        assertThat(nodes[1], is(expectedOrder));

        random.setChangeBy(-0.1);
        random.setNextBoolean(false);
        minimizeCrossings(crossMin, nodes[1], false, false, true);

        assertThat(nodes[1], is(expectedOrder));
    }

    /**
     * <pre>
     *
     * *  ___
     *  \/| |
     *  /\|_|
     * *
     * </pre>
     *
     */
    @Test
    public void assumingFixedPortOrder_givenSimplePortOrderCross_removesCrossingBackwards() {
        LNode[] leftNodes = addNodesToLayer(2, makeLayer(getGraph()));
        LNode rightNode = addNodeToLayer(makeLayer(getGraph()));
        eastWestEdgeFromTo(leftNodes[0], rightNode);
        eastWestEdgeFromTo(leftNodes[1], rightNode);
        setFixedOrderConstraint(rightNode);
        setUpIds();

        LNode[][] nodes = getGraph().toNodeArray();

        float[] portRanks = new float[4];
        NodeRelativePortDistributor portDist = new NodeRelativePortDistributor(portRanks, initNodePositions());
        portDist.calculatePortRanks(nodes[1], PortType.INPUT);
        BarycenterState[][] barycenters = getBarycenterArr();
        BarycenterHeuristic crossMin =
                new BarycenterHeuristic(barycenters, new MockConstraintResolver(), random, portRanks);

        LNode[] expectedOrder = switchOrderInArray(0, 1, nodes[0]);

        minimizeCrossings(crossMin, nodes[0], false, false, false);

        assertThat(expectedOrder, is(nodes[0]));
    }

    /**
     * <pre>
     *       ___
     *    ---| |
     *    |  | |  <- switch this
     * ---+--|_|
     * |  |
     * *--|--*  <- with this
     *    |
     *    ---*
     * .
     * </pre>
     *
     * With fixed Port PortOrder.
     *
     * @return Graph of the form above.
     */
    @Test
    public void inLayerEdges() {
        LNode leftNode = addNodeToLayer(makeLayer());
        LNode[] rightNodes = addNodesToLayer(3, makeLayer());
        setFixedOrderConstraint(rightNodes[0]);
        eastWestEdgeFromTo(leftNode, rightNodes[0]);
        addInLayerEdge(rightNodes[0], rightNodes[2], PortSide.WEST);
        eastWestEdgeFromTo(leftNode, rightNodes[1]);
        setUpIds();
        LNode[][] nodes = getGraph().toNodeArray();

        float[] portRanks = new float[5];
        NodeRelativePortDistributor portDist = new NodeRelativePortDistributor(portRanks, initNodePositions());
        portDist.calculatePortRanks(nodes[0], PortType.INPUT);
        BarycenterState[][] barycenters = getBarycenterArr();
        BarycenterHeuristic crossMin =
                new BarycenterHeuristic(barycenters, new MockConstraintResolver(), random, portRanks);

        LNode[] expectedOrder = getArrayInIndexOrder(nodes[1], 2, 0, 1);

        minimizeCrossings(crossMin, nodes[1], false, false, true);

        assertThat(nodes[1], is(expectedOrder));
    }

    /**
     * <pre>
     *   ----*
     *   |---*
     *   ||
     * *-++--*
     *   ||
     *  ----
     *  |__|
     * </pre>
     * @return Graph of the form above.
     */
    @Test
    public void northSouthEdges() {
        LNode[] leftNodes = addNodesToLayer(1, makeLayer());
        LNode[] middleNodes = addNodesToLayer(4, makeLayer());
        LNode[] rightNodes = addNodesToLayer(3, makeLayer());
        eastWestEdgeFromTo(leftNodes[0], middleNodes[2]);
        eastWestEdgeFromTo(middleNodes[2], rightNodes[2]);
        setAsLongEdgeDummy(middleNodes[2]);
        addNorthSouthEdge(PortSide.NORTH, middleNodes[3], middleNodes[0], rightNodes[0], false);
        addNorthSouthEdge(PortSide.NORTH, middleNodes[3], middleNodes[1], rightNodes[1], false);
        setUpIds();

        Multimap<LNode, LNode> layoutUnits = LinkedListMultimap.create();
        LNode[][] nodes = getGraph().toNodeArray();
        for (LNode[] list : nodes) {
            for (LNode node : list) {
                LNode layoutUnit = node.getProperty(InternalProperties.IN_LAYER_LAYOUT_UNIT);
                if (layoutUnit != null) {
                    layoutUnits.put(layoutUnit, node);
                }
            }
        }

        float[] portRanks = new float[5];
        NodeRelativePortDistributor portDist = new NodeRelativePortDistributor(portRanks, initNodePositions());
        portDist.calculatePortRanks(nodes[0], PortType.INPUT);

        BarycenterState[][] barycenters = getBarycenterArr();
        ForsterConstraintResolver constraintResolver =
                new ForsterConstraintResolver(barycenters, layoutUnits);
        BarycenterHeuristic crossMin =
                new BarycenterHeuristic(barycenters, constraintResolver, random, portRanks);
        LNode[] expectedOrder = getArrayInIndexOrder(nodes[1], 1, 0, 3, 2);

        random.setChangeBy(-0.01);
        minimizeCrossings(crossMin, nodes[1], false, false, true);

        assertThat(nodes[1], is(expectedOrder));

    }

    /**
     * Helper method that wraps the nodes array in a list and applies the order back to the array.
     */
    private void minimizeCrossings(final BarycenterHeuristic crossMin, final LNode[] nodes,
            final boolean preOrdered, final boolean randomized, final boolean forward) {
        List<LNode> nodeList = Lists.newArrayList(nodes);
        crossMin.minimizeCrossings(nodeList, preOrdered, randomized, forward);
        int i = 0;
        for (LNode node : nodeList) {
            nodes[i++] = node;
        }
    }
}
