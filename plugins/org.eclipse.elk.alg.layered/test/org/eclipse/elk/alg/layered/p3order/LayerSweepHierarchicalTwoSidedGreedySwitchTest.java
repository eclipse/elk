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

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.TestGraphCreator;
import org.eclipse.elk.alg.layered.properties.GreedySwitchType;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.junit.Test;

// CHECKSTYLEOFF javadoc
// CHECKSTYLEOFF MagicNumber
// CHECKSTYLEOFF MethodName
public class LayerSweepHierarchicalTwoSidedGreedySwitchTest extends TestGraphCreator {
    /**
     * <pre>
     * ______
     * |*  *+-*
     * | \/ |
     * | /\ |
     * |*  *+-*
     * |----|
     * </pre>
     */
    @Test
    public void givencrossInFirstLevelCompoundNodeUsingTwoSidedGreedySwitch_LeavesCrossing() {
        // parent graph
        LNode leftOuterNode = addNodeToLayer(makeLayer(getGraph()));
        LNode[] rightNodes = addNodesToLayer(2, makeLayer(getGraph()));
        LPort[] leftOuterPorts = addPortsOnSide(2, leftOuterNode, PortSide.EAST);
        eastWestEdgeFromTo(leftOuterPorts[0], rightNodes[0]);
        eastWestEdgeFromTo(leftOuterPorts[1], rightNodes[1]);

        // child graph
        LGraph leftInnerGraph = nestedGraph(leftOuterNode);
        LNode[] leftInnerNodesleft = addNodesToLayer(2, makeLayer(leftInnerGraph));
        LNode[] leftInnerNodesRight = addNodesToLayer(2, makeLayer(leftInnerGraph));
        LNode[] leftInnerDummyNodes =
                addExternalPortDummiesToLayer(makeLayer(leftInnerGraph), leftOuterPorts);
        eastWestEdgeFromTo(leftInnerNodesRight[0], leftInnerDummyNodes[0]);
        eastWestEdgeFromTo(leftInnerNodesRight[1], leftInnerDummyNodes[1]);
        eastWestEdgeFromTo(leftInnerNodesleft[0], leftInnerNodesRight[1]);
        eastWestEdgeFromTo(leftInnerNodesleft[1], leftInnerNodesRight[0]);
        getGraph().setProperty(LayeredOptions.CROSSING_MINIMIZATION_BOTTOM_UP, true);

        List<LNode> expectedSameOrder = getNodesInLayer(1);

        setUpAndMinimizeCrossings();

        assertThat(getGraph().getLayers().get(1).getNodes(), is(expectedSameOrder));
    }

    /**
     * <pre>
     * ________
     * |___  *|
     * || |\/ |
     * || |/\ |
     * ||_|  *|
     * |------|
     * </pre>
     *
     * Sweep backward first.
     */
    @Test
    public void givenCompoundGraphWhereOrderIsOnlyCorrectedOnForwardSweep_RemovesCrossing() {
        LNode node = addNodeToLayer(makeLayer(getGraph()));
        LGraph innerGraph = nestedGraph(node);
        LNode leftInnerNode = addNodeToLayer(makeLayer(innerGraph));
        setFixedOrderConstraint(leftInnerNode);
        LNode[] rightInnerNodes = addNodesToLayer(2, makeLayer(innerGraph));
        eastWestEdgeFromTo(leftInnerNode, rightInnerNodes[1]);
        eastWestEdgeFromTo(leftInnerNode, rightInnerNodes[0]);

        Layer rightInnerLayer = innerGraph.getLayers().get(1);
        List<LNode> expectedOrderRightInner = switchOrderOfNodesInLayer(0, 1, rightInnerLayer);
        List<LPort> expectedPortOrderLeft = copyPortsInIndexOrder(leftInnerNode, 0, 1);

        getRandom().setNextBoolean(false);
        setUpAndMinimizeCrossings();

        assertThat(leftInnerNode.getPorts(), is(expectedPortOrderLeft));
        assertThat(rightInnerLayer.getNodes(), is(expectedOrderRightInner));
    }

    /**
     * <pre>
     * ______
     * |*  *|
     * | \/ |
     * | /\ |
     * |*  *|
     * |----|
     * </pre>
     */
    @Test
    public void givenSingleHierarchicalNodeWithCross_RemovesCrossing() {
        LNode node = addNodeToLayer(makeLayer(getGraph()));
        LGraph innerGraph = nestedGraph(node);
        LNode[] innerNodesleft = addNodesToLayer(2, makeLayer(innerGraph));
        LNode[] innerNodesRight = addNodesToLayer(2, makeLayer(innerGraph));
        eastWestEdgeFromTo(innerNodesleft[0], innerNodesRight[1]);
        eastWestEdgeFromTo(innerNodesleft[1], innerNodesRight[0]);

        List<LNode> expectedOrderLeft =
                switchOrderOfNodesInLayer(0, 1, innerGraph.getLayers().get(0));

        setUpAndMinimizeCrossings();

        assertThat(innerGraph.getLayers().get(0).getNodes(), is(expectedOrderLeft));
    }

    private void setAllGraphsToGreedySwitchType(final LGraph graph,
            final GreedySwitchType greedyType) {
        graph.setProperty(LayeredOptions.CROSSING_MINIMIZATION_GREEDY_SWITCH, greedyType);
        for (Layer layer : graph) {
            for (LNode node : layer) {
                if (isCompound(node)) {
                    setAllGraphsToGreedySwitchType(
                            node.getProperty(InternalProperties.NESTED_LGRAPH), greedyType);
                }
            }
        }
    }

    private boolean isCompound(final LNode n) {
        return n.getProperty(InternalProperties.NESTED_LGRAPH) != null;
    }

    private void setUpAndMinimizeCrossings() {
        LayerSweepCrossingMinimizer crossMin =
                new LayerSweepCrossingMinimizer(CrossMinType.TWO_SIDED_GREEDY_SWITCH);
        setAllGraphsToGreedySwitchType(getGraph(), GreedySwitchType.TWO_SIDED);
        crossMin.process(getGraph(), new BasicProgressMonitor());
    }
}
