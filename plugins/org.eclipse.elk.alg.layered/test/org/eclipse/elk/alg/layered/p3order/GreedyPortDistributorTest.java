/*******************************************************************************
 * Copyright (c) 2016 2014, 2015 alan and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    alan - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.TestGraphCreator;
import org.eclipse.elk.core.options.PortSide;
import org.junit.Before;
import org.junit.Test;

/**
 * @author alan
 *
 */
public class GreedyPortDistributorTest extends TestGraphCreator {
    private GreedyPortDistributor portDist;

    // CHECKSTYLEOFF javadoc
    // CHECKSTYLEOFF MagicNumber
    // CHECKSTYLEOFF MethodName

    @Before
    public void setUp() {
        portDist = new GreedyPortDistributor();
    }
    /**
     * <pre>
     * *  ___
     *  \/| |
     *  /\| |
     * *  |_|
     * </pre>
     */
    @Test
    public void distributePorts_GivenCrossOnWesternSide_RemoveCrossing() {
        LNode[] leftNodes = addNodesToLayer(2, makeLayer(getGraph()));
        LNode rightNode = addNodeToLayer(makeLayer(getGraph()));
        eastWestEdgeFromTo(leftNodes[0], rightNode);
        eastWestEdgeFromTo(leftNodes[1], rightNode);
        cachePortSides();

        List<LPort> expectedPortOrderRightNode = portsOrderedAs(rightNode, 1, 0);

        PortSide side = PortSide.WEST;
        portDist.initForLayers(leftNodes, getGraph().toNodeArray()[1], side, new int[4]);
        portDist.distributePorts(rightNode, side);

        assertThat(rightNode.getPorts(), is(expectedPortOrderRightNode));
    }
    private void cachePortSides() {
        for (Layer l : getGraph()) {
            for (LNode node : l) {
                node.cachePortSides();
            }
        }
    }

    /**
     * <pre>
     * *    ___
     *  \/--| |
     *  /\ /| |
     * *  x | |
     * *-/ \|_|
     * </pre>
     */
    @Test
    public void distributePorts_GivenMultipleCrossingsOnWesternSide_RemoveCrossing() {
        LNode[] leftNodes = addNodesToLayer(3, makeLayer(getGraph()));
        LNode rightNode = addNodeToLayer(makeLayer(getGraph()));
        eastWestEdgeFromTo(leftNodes[0], rightNode);
        eastWestEdgeFromTo(leftNodes[2], rightNode);
        eastWestEdgeFromTo(leftNodes[1], rightNode);

        List<LPort> expectedPortOrderRightNode = portsOrderedAs(rightNode, 1, 2, 0);

        PortSide side = PortSide.WEST;
        portDist.initForLayers(leftNodes, getGraph().toNodeArray()[1], side, new int[6]);
        portDist.distributePorts(rightNode, side);

        assertThat(rightNode.getPorts(), is(expectedPortOrderRightNode));
    }

    /**
     * <pre>
     * ___
     * | |\/*
     * |_|/\*
     * </pre>
     */
    @Test
    public void distributePorts_GivenCrossingsOnEasternSide_RemoveThem() throws Exception {
        LNode[] leftNodes = addNodesToLayer(1, makeLayer());
        LNode[] rightNodes = addNodesToLayer(2, makeLayer());
        eastWestEdgeFromTo(leftNodes[0], rightNodes[1]);
        eastWestEdgeFromTo(leftNodes[0], rightNodes[0]);

        List<LPort> expectedPortOrderLeftNode = portsOrderedAs(leftNodes[0], 1, 0);

        PortSide side = PortSide.EAST;
        portDist.initForLayers(leftNodes, rightNodes, side, new int[4]);
        portDist.distributePorts(leftNodes[0], side);

        assertThat(leftNodes[0].getPorts(), is(expectedPortOrderLeftNode));
    }

    /**
     * <pre>
     * ____
     * |*-+   *
     * |  |\\/
     * |*-+/\\
     * |--|   *
     * </pre>
     */
    @Test
    public void givenDoubleCrossBetweenCompoundAndNonCompoundNodes_SwitchesPorts() {
        LNode leftOuterNode = addNodeToLayer(makeLayer(getGraph()));
        LNode[] rightNodes = addNodesToLayer(2, makeLayer(getGraph()));
        LPort[] leftOuterPorts = addPortsOnSide(2, leftOuterNode, PortSide.EAST);
        eastWestEdgeFromTo(leftOuterPorts[0], rightNodes[1]);
        eastWestEdgeFromTo(leftOuterPorts[0], rightNodes[1]);
        eastWestEdgeFromTo(leftOuterPorts[1], rightNodes[0]);
        LGraph leftInnerGraph = nestedGraph(leftOuterNode);
        LNode[] leftInnerNodes = addNodesToLayer(2, makeLayer(leftInnerGraph));
        LNode[] leftInnerDummyNodes = addExternalPortDummiesToLayer(makeLayer(leftInnerGraph), leftOuterPorts);
        eastWestEdgeFromTo(leftInnerNodes[0], leftInnerDummyNodes[0]);
        eastWestEdgeFromTo(leftInnerNodes[1], leftInnerDummyNodes[1]);

        List<LPort> expectedPortOrderLeftNode = portsOrderedAs(leftOuterNode, 1, 0);

        PortSide side = PortSide.EAST;
        portDist.initForLayers(getGraph().toNodeArray()[0], rightNodes, side, new int[5]);
        portDist.distributePorts(leftOuterNode, side);

        assertThat(leftOuterNode.getPorts(), is(expectedPortOrderLeftNode));
    }

    private List<LPort> portsOrderedAs(final LNode rightNode, final int... indices) {
        List<LPort> ordered = new ArrayList<>();
        for (int i : indices) {
            ordered.add(rightNode.getPorts().get(i));
        }
        return ordered;
    }
}
