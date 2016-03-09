package org.eclipse.elk.alg.layered.p3order;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.TestGraphCreator;
import org.eclipse.elk.alg.layered.properties.GreedySwitchType;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class LayerSweepHierarchicalCrossingMinimizerTest extends TestGraphCreator {
    private final CrossMinType crossMinType;
    private LayerSweepHierarchicalCrossingMinimizer crossMin;

    /**
     * Constructor called by Parameterized.
     * 
     * @param gT
     *            greedyType
     */
    public LayerSweepHierarchicalCrossingMinimizerTest(final CrossMinType crossMinType) {
        this.crossMinType = crossMinType;
    }

    /**
     * Sets the Parameters to be tested as all elements of the GreedySwitchType enum.
     * 
     * @return parameters
     */
    @Parameters(name = "{0}")
    public static Iterable<Object[]> greedyTypes() {
        return Arrays.asList(
                new Object[][] { { CrossMinType.BARYCENTER }, { CrossMinType.GREEDY_SWITCH } });
    }

    @Before
    public void setUp() {
        crossMin = new LayerSweepHierarchicalCrossingMinimizer(crossMinType);
    }

    @Test
    public void givenOneNode_ShouldntCrash() {
        getOneNodeGraph();

        setUpAndMinimizeCrossings();
        // should cause no errors
    }

    @Test
    public void givenNoNode_ShouldntCrash() {

        setUpAndMinimizeCrossings();
        // should cause no errors
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
        LNode node = addNodeToLayer(makeLayer(graph));
        LGraph innerGraph = nestedGraph(node);
        LNode leftInnerNode = addNodeToLayer(makeLayer(innerGraph));
        setFixedOrderConstraint(leftInnerNode);
        LNode[] rightInnerNodes = addNodesToLayer(2, makeLayer(innerGraph));
        eastWestEdgeFromTo(leftInnerNode, rightInnerNodes[1]);
        eastWestEdgeFromTo(leftInnerNode, rightInnerNodes[0]);

        Layer rightInnerLayer = innerGraph.getLayers().get(1);
        List<LNode> expectedOrderRightInner = switchOrderOfNodesInLayer(0, 1, rightInnerLayer);
        List<LPort> expectedPortOrderLeft = copyPortsInIndexOrder(leftInnerNode, 0, 1);

        random.setNextBoolean(false);
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
        LNode node = addNodeToLayer(makeLayer(graph));
        LGraph innerGraph = nestedGraph(node);
        LNode[] innerNodesleft = addNodesToLayer(2, makeLayer(innerGraph));
        LNode[] innerNodesRight = addNodesToLayer(2, makeLayer(innerGraph));
        eastWestEdgeFromTo(innerNodesleft[0], innerNodesRight[1]);
        eastWestEdgeFromTo(innerNodesleft[1], innerNodesRight[0]);

        List<LNode> expectedOrderRightBaryCenter =
                switchOrderOfNodesInLayer(0, 1, innerGraph.getLayers().get(1));

        setUpAndMinimizeCrossings();

        assertThat(innerGraph.getLayers().get(1).getNodes(), is(expectedOrderRightBaryCenter));
    }

    /**
     * <pre>
     * ____  ____
     * |*-+  +-*|
     * |  |\/|  |
     * |*-+/\+-*|
     * |--|  |--|
     * </pre>
     */
    @Test
    public void givenSimpleHierarchicalCross_ShouldResultInNoCrossing() {
        LNode leftOuterNode = addNodeToLayer(makeLayer(graph));
        LNode rightOuterNode = addNodeToLayer(makeLayer(graph));
        LPort[] leftOuterPorts = addPortsOnSide(2, leftOuterNode, PortSide.EAST);
        LPort[] rightOuterPorts = addPortsOnSide(2, rightOuterNode, PortSide.WEST);

        addEdgeBetweenPorts(leftOuterPorts[0], rightOuterPorts[0]);
        addEdgeBetweenPorts(leftOuterPorts[1], rightOuterPorts[1]);

        makeNestedTwoNodeGraphWithEasternPorts(leftOuterNode, leftOuterPorts);

        LGraph rightInnerGraph =
                makeNestedTwoNodeGraphWithWesternPorts(rightOuterNode, rightOuterPorts);

        List<LNode> expectedExternalDummyOrderRight =
                switchOrderOfNodesInLayer(0, 1, rightInnerGraph.getLayers().get(0));
        List<LNode> expectedNormalNodeOrderRight =
                switchOrderOfNodesInLayer(0, 1, rightInnerGraph.getLayers().get(1));

        List<LPort> expectedOrderOfPortsRight =
                Lists.newArrayList(rightOuterPorts[1], rightOuterPorts[0]);

        setUpAndMinimizeCrossings();
        List<LNode> actualExternalDummyOrderRight = rightInnerGraph.getLayers().get(0).getNodes();
        assertThat(actualExternalDummyOrderRight, is(expectedExternalDummyOrderRight));

        assertThat(rightOuterNode.getPorts(), is(expectedOrderOfPortsRight));

        List<LNode> actualNormalOrderRight = rightInnerGraph.getLayers().get(1).getNodes();
        assertThat(actualNormalOrderRight, is(expectedNormalNodeOrderRight));
    }

    /**
     * <pre>
     * ____  ____
     * |*-+  +-*|
     * |  |\/|  |
     * |*-+/\+-*|
     * |--|  |--|
     * </pre>
     */
    @Test
    public void givenSimpleHierarchicalCrossSweepingFromRightToLeft_ShouldResultInNoCrossing() {
        LNode leftOuterNode = addNodeToLayer(makeLayer(graph));
        LNode rightOuterNode = addNodeToLayer(makeLayer(graph));
        LPort[] leftOuterPorts = addPortsOnSide(2, leftOuterNode, PortSide.EAST);
        LPort[] rightOuterPorts = addPortsOnSide(2, rightOuterNode, PortSide.WEST);
        addEdgeBetweenPorts(leftOuterPorts[0], rightOuterPorts[0]);
        addEdgeBetweenPorts(leftOuterPorts[1], rightOuterPorts[1]);

        LGraph leftInnerGraph =
                makeNestedTwoNodeGraphWithEasternPorts(leftOuterNode, leftOuterPorts);

        makeNestedTwoNodeGraphWithWesternPorts(rightOuterNode, rightOuterPorts);

        List<LNode> expectedNodeOrderLeft =
                switchOrderOfNodesInLayer(0, 1, leftInnerGraph.getLayers().get(0));

        List<LPort> expectedOrderOfPortsLeft =
                Lists.newArrayList(leftOuterPorts[1], leftOuterPorts[0]);

        random.setNextBoolean(false);
        setUpAndMinimizeCrossings();

        assertThat(leftOuterNode.getPorts(), is(expectedOrderOfPortsLeft));
        assertThat(leftInnerGraph.getLayers().get(0).getNodes(), is(expectedNodeOrderLeft));

    }

    /**
     * <pre>
     * ___        ___
     * | |===*  --| |
     * | |    \/  | |
     * | |    /\  | |
     * |_|===*  --|_|
     *       ___  ___
     *       | |\/| |
     *       |_|/\|_|
     * .           ^
     *           port orders fixed
     * </pre>
     * 
     * First Layer and last layer in fixed order.
     * 
     * @return graph of the form above.
     */
    @Test
    public void althoughBackwardSweepNotTakenStillCorrectsPortOrder() {
        Layer[] layers = makeLayers(3);
        LNode leftNode = addNodeToLayer(layers[0]);
        LNode[] middleNodes = addNodesToLayer(3, layers[1]);
        LNode[] rightNodes = addNodesToLayer(2, layers[2]);

        eastWestEdgeFromTo(middleNodes[0], rightNodes[0]);
        eastWestEdgeFromTo(middleNodes[1], rightNodes[0]);

        eastWestEdgeFromTo(middleNodes[2], rightNodes[1]);
        eastWestEdgeFromTo(middleNodes[2], rightNodes[1]);
        eastWestEdgeFromTo(leftNode, middleNodes[0]);
        eastWestEdgeFromTo(leftNode, middleNodes[0]);
        eastWestEdgeFromTo(leftNode, middleNodes[1]);
        eastWestEdgeFromTo(leftNode, middleNodes[1]);

        setFixedOrderConstraint(rightNodes[1]);
        setFixedOrderConstraint(rightNodes[0]);
        setFixedOrderConstraint(leftNode);

        setUpIds();
        List<LPort> expectedPortOrderBottomMiddleNode = copyPortsInIndexOrder(middleNodes[2], 1, 0);

        setUpAndMinimizeCrossings();

        Layer middleLayer = graph.getLayers().get(1);
        assertTrue(middleLayer.toString(), inOrder(middleNodes[0], middleNodes[1], middleLayer));
        assertThat(middleNodes[2].getPorts(), is(expectedPortOrderBottomMiddleNode));
    }

    /**
     * <pre>
     * ___        ___
     * | |---*  --| |
     * | |    \/  | |
     * | |    /\  | |
     * |_|---*  --|_|
     *       ___  ___
     *       | |\/| |
     *       |_|/\|_|
     *             ^
     *           port orders fixed
     * </pre>
     * 
     * First Layer and last layer in fixed order. Should leave this order, since the fixable port
     * order is already corrected before counting.
     * 
     * @return graph of the form above.
     */
    @Test
    public void doesNotCountRemovablePortCrossing() {
        Layer[] layers = makeLayers(3);
        LNode leftNode = addNodeToLayer(layers[0]);
        LNode[] middleNodes = addNodesToLayer(3, layers[1]);
        LNode[] rightNodes = addNodesToLayer(2, layers[2]);

        eastWestEdgeFromTo(middleNodes[0], rightNodes[0]);
        eastWestEdgeFromTo(middleNodes[1], rightNodes[0]);

        eastWestEdgeFromTo(middleNodes[2], rightNodes[1]);
        eastWestEdgeFromTo(middleNodes[2], rightNodes[1]);
        eastWestEdgeFromTo(leftNode, middleNodes[0]);
        eastWestEdgeFromTo(leftNode, middleNodes[1]);

        setFixedOrderConstraint(rightNodes[1]);
        setFixedOrderConstraint(rightNodes[0]);
        setFixedOrderConstraint(leftNode);

        setUpIds();
        List<LPort> expectedPortOrderBottomMiddleNode = copyPortsInIndexOrder(middleNodes[2], 1, 0);

        setUpAndMinimizeCrossings();

        Layer middleLayer = graph.getLayers().get(1);
        assertTrue(middleLayer.toString(), inOrder(middleNodes[0], middleNodes[1], middleLayer));
        assertThat(middleNodes[2].getPorts(), is(expectedPortOrderBottomMiddleNode));
    }

    /**
     * <pre>
     * *   ___
     *  \  | |==*
     * *-+-|_|--
     *    \    |
     *     *---|
     * </pre>
     */
    @Test
    public void resolvesInNotTakenSweepDirectionInLayerPortOrderCrossingsAfterSwitch() {
        LNode[] leftNodes = addNodesToLayer(2, makeLayer(graph));
        LNode[] middleNodes = addNodesToLayer(2, makeLayer(graph));
        LNode rightNode = addNodeToLayer(makeLayer(graph));

        eastWestEdgeFromTo(middleNodes[0], rightNode);
        eastWestEdgeFromTo(middleNodes[0], rightNode);
        addInLayerEdge(middleNodes[0], middleNodes[1], PortSide.EAST);
        eastWestEdgeFromTo(leftNodes[0], middleNodes[1]);
        eastWestEdgeFromTo(leftNodes[1], middleNodes[0]);
        setUpIds();

        List<LNode> expectedNodeOrderMiddleLayer = switchOrderOfNodesInLayer(0, 1, 1);
        List<LPort> expectedPortOrderMiddleTopNode =
                copyPortsInIndexOrder(middleNodes[0], 2, 0, 1, 3);

        setUpAndMinimizeCrossings();

        assertThat(graph.getLayers().get(1).getNodes(), is(expectedNodeOrderMiddleLayer));
        assertThat(middleNodes[0].getPorts(), is(expectedPortOrderMiddleTopNode));
    }

    /**
     * <pre>
     * *   ___
     *  \  | |
     * *-+-| |
     *   |/|_|
     *   |\  
     *   --*
     * </pre>
     */
    @Test
    public void resolvesInLayerPortOrderCrossingsAfterSwitch() {
        LNode[] leftNodes = addNodesToLayer(2, makeLayer(graph));
        LNode[] middleNodes = addNodesToLayer(2, makeLayer(graph));

        eastWestEdgeFromTo(leftNodes[0], middleNodes[1]);
        addInLayerEdge(middleNodes[1], middleNodes[0], PortSide.WEST);
        eastWestEdgeFromTo(leftNodes[1], middleNodes[0]);
        setUpIds();

        List<LNode> expectedNodeOrderMiddleLayer = switchOrderOfNodesInLayer(0, 1, 1);
        List<LPort> expectedPortOrderMiddleTopNode = copyPortsInIndexOrder(middleNodes[0], 1, 0);

        setUpAndMinimizeCrossings();

        if (crossMinType == CrossMinType.BARYCENTER) {
            assertThat(graph.getLayers().get(1).getNodes(), is(expectedNodeOrderMiddleLayer));
            assertThat(middleNodes[0].getPorts(), is(expectedPortOrderMiddleTopNode));
        }
    }

    /**
     * Assumes both nodes to actually be in layer.
     * 
     * @param firstNode
     * @param secondNode
     * @param layer
     * @return
     */
    private boolean inOrder(final LNode firstNode, final LNode secondNode, final Layer layer) {
        for (LNode node : layer) {
            if (node == firstNode) {
                return true;
            } else if (node == secondNode) {
                return false;
            }
        }
        return false;
    }

    private LGraph makeNestedTwoNodeGraphWithWesternPorts(final LNode rightOuterNode,
            final LPort[] rightOuterPorts) {
        LGraph rightInnerGraph = nestedGraph(rightOuterNode);
        LNode[] rightInnerDummyNodes =
                addExternalPortDummiesToLayer(makeLayer(rightInnerGraph), rightOuterPorts);
        LNode[] rightInnerNodes = addNodesToLayer(2, makeLayer(rightInnerGraph));
        eastWestEdgeFromTo(rightInnerDummyNodes[0], rightInnerNodes[0]);
        eastWestEdgeFromTo(rightInnerDummyNodes[1], rightInnerNodes[1]);
        return rightInnerGraph;
    }

    private LGraph makeNestedTwoNodeGraphWithEasternPorts(final LNode leftOuterNode,
            final LPort[] leftOuterPorts) {
        LGraph leftInnerGraph = nestedGraph(leftOuterNode);
        LNode[] leftInnerNodes = addNodesToLayer(2, makeLayer(leftInnerGraph));
        LNode[] leftInnerDummyNodes =
                addExternalPortDummiesToLayer(makeLayer(leftInnerGraph), leftOuterPorts);
        eastWestEdgeFromTo(leftInnerNodes[0], leftInnerDummyNodes[0]);
        eastWestEdgeFromTo(leftInnerNodes[1], leftInnerDummyNodes[1]);
        return leftInnerGraph;
    }

    /**
     * <pre>
     * ____  ____
     * |*-+  | *|
     * |  |\/|  |
     * |*-+/\| *|
     * |--|  |--|
     * </pre>
     */
    @Test
    public void givenCrossWithNoExternalPortDummiesOnOneNestedGraph_ShouldRemoveCrossing() {
        LNode leftOuterNode = addNodeToLayer(makeLayer(graph));
        LNode rightOuterNode = addNodeToLayer(makeLayer(graph));
        LPort[] leftOuterPorts = addPortsOnSide(2, leftOuterNode, PortSide.EAST);
        LPort[] rightOuterPorts = addPortsOnSide(2, rightOuterNode, PortSide.WEST);
        addEdgeBetweenPorts(leftOuterPorts[0], rightOuterPorts[0]);
        addEdgeBetweenPorts(leftOuterPorts[1], rightOuterPorts[1]);
        makeNestedTwoNodeGraphWithEasternPorts(leftOuterNode, leftOuterPorts);
        LGraph rightInnerGraph = nestedGraph(rightOuterNode);
        addNodesToLayer(2, makeLayer(rightInnerGraph));

        List<LPort> expectedPortOrderRight =
                Lists.newArrayList(rightOuterPorts[1], rightOuterPorts[0]);

        setUpAndMinimizeCrossings();

        assertThat(expectedPortOrderRight, is(rightOuterNode.getPorts()));
    }

    /**
     * <pre>
     * ____  
     * |*-+  *
     * |  |\/
     * |*-+/\
     * |--|  *
     * </pre>
     */
    @Test
    public void givenCrossBetweenCompoundAndNonCompoundNodes_ShouldRemoveCrossing() {
        LNode leftOuterNode = addNodeToLayer(makeLayer(graph));
        LNode[] rightNodes = addNodesToLayer(2, makeLayer(graph));
        LPort[] leftOuterPorts = addPortsOnSide(2, leftOuterNode, PortSide.EAST);
        eastWestEdgeFromTo(leftOuterPorts[0], rightNodes[1]);
        eastWestEdgeFromTo(leftOuterPorts[1], rightNodes[0]);
        makeNestedTwoNodeGraphWithEasternPorts(leftOuterNode, leftOuterPorts);

        List<LNode> expectedOrderRight = switchOrderOfNodesInLayer(0, 1, 1);

        setUpAndMinimizeCrossings();

        assertThat(expectedOrderRight, is(graph.getLayers().get(1).getNodes()));
    }

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
    public void givenCrossInFirstLevelCompoundNode_ShouldRemoveCrossing() {
        // parent graph
        LNode leftOuterNode = addNodeToLayer(makeLayer(graph));
        LNode[] rightNodes = addNodesToLayer(2, makeLayer(graph));
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

        List<LNode> expectedOrderRight = switchOrderOfNodesInLayer(0, 1, 1);

        setUpAndMinimizeCrossings();

        assertThat(graph.getLayers().get(1).getNodes(), is(expectedOrderRight));
    }

    /**
     * <pre>
     *    ___
     * *  | |
     *  \/| |
     *  /\| |
     * *  |_|
     * </pre>
     * 
     * port order fixed.
     */
    @Test
    public void givenGraphWithoutNesting_ShouldImproveOnBackwardSweep() {
        Layer leftLayer = makeLayer(graph);
        Layer rightLayer = makeLayer(graph);
        LNode leftTopNode = addNodeToLayer(leftLayer);
        LNode leftBottomNode = addNodeToLayer(leftLayer);
        LNode rightNode = addNodeToLayer(rightLayer);
        rightNode.setProperty(CoreOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_ORDER);
        eastWestEdgeFromTo(leftTopNode, rightNode);
        eastWestEdgeFromTo(leftBottomNode, rightNode);

        List<LNode> expectedOrderLayerOne = switchOrderOfNodesInLayer(0, 1, 0);

        random.setNextBoolean(false); // sweeps backward
        setUpAndMinimizeCrossings();

        assertThat("Layer one", getNodesInLayer(0), is(expectedOrderLayerOne));
    }

    /**
     * <pre>
     *    *
     *   /
     *  /
     * *  *
     *  \/
     *  /\
     * *  *
     * 
     * </pre>
     */
    @Test
    public void givenSimpleGraph_ShouldNotBeReorderedRandomlyOnBackwardSweep() {
        Layer[] layers = makeLayers(2);
        LNode[] leftNodes = addNodesToLayer(2, layers[0]);
        LNode[] rightNodes = addNodesToLayer(3, layers[1]);
        eastWestEdgeFromTo(leftNodes[0], rightNodes[0]);
        eastWestEdgeFromTo(leftNodes[0], rightNodes[2]);
        eastWestEdgeFromTo(leftNodes[1], rightNodes[1]);

        List<LNode> expectedOrderLayerTwo = switchOrderOfNodesInLayer(1, 2, 1);
        List<LNode> expectedOrderLayerOne = getNodesInLayer(0);

        setUpAndMinimizeCrossings();

        assertThat("Layer two", getNodesInLayer(1), is(expectedOrderLayerTwo));
        assertThat("Layer one", getNodesInLayer(0), is(expectedOrderLayerOne));

    }

    /**
     * <pre>
     * ___     ___
     * | |\ /*-| |
     * | |\x/  | |
     * | |/x\  | |
     * |_|/ \*-|_|
     * </pre>
     * 
     * port order fixed.
     */
    @Test
    public void givenGraphWhichWorsensOnBackwardSweep_ShouldTakeResultOfForwardSweep() {
        Layer[] layers = makeLayers(3);
        LNode leftNode = addNodeToLayer(layers[0]);
        setFixedOrderConstraint(leftNode);
        LNode[] middleNodes = addNodesToLayer(2, layers[1]);
        LNode rightNode = addNodeToLayer(layers[2]);
        setFixedOrderConstraint(rightNode);
        eastWestEdgeFromTo(leftNode, middleNodes[1]);
        eastWestEdgeFromTo(leftNode, middleNodes[1]);
        eastWestEdgeFromTo(leftNode, middleNodes[0]);
        eastWestEdgeFromTo(leftNode, middleNodes[0]);
        eastWestEdgeFromTo(middleNodes[1], rightNode);
        eastWestEdgeFromTo(middleNodes[0], rightNode);

        List<LNode> expectedOrderLayerTwo = switchOrderOfNodesInLayer(0, 1, 1);

        setUpAndMinimizeCrossings();

        assertThat("Layer two", getNodesInLayer(1), is(expectedOrderLayerTwo));
    }

    /**
     * <pre>
     * ____  ____
     * |*-+  +-*|
     * |  |\/|  |
     * |*-+/\+-*|
     * |--|  |--|
     * </pre>
     * 
     * With external origins of port dummies on right node wrong way around.
     */
    @Test
    public void givenNestedGraphWithWronglySortedDummyNodes_ShouldSortAndResolveCrossing() {
        LNode leftOuterNode = addNodeToLayer(makeLayer(graph));
        LNode rightOuterNode = addNodeToLayer(makeLayer(graph));
        LPort[] leftOuterPorts = addPortsOnSide(2, leftOuterNode, PortSide.EAST);
        LPort[] rightOuterPorts = addPortsOnSide(2, rightOuterNode, PortSide.WEST);
        addEdgeBetweenPorts(leftOuterPorts[0], rightOuterPorts[0]);
        addEdgeBetweenPorts(leftOuterPorts[1], rightOuterPorts[1]);

        makeNestedTwoNodeGraphWithEasternPorts(leftOuterNode, leftOuterPorts);

        LGraph rightInnerGraph = nestedGraph(rightOuterNode);
        LPort[] reversedRightPorts = reverse(rightOuterPorts);
        LNode[] rightInnerDummyNodes =
                addExternalPortDummiesToLayer(makeLayer(rightInnerGraph), reversedRightPorts);
        LNode[] rightInnerNodes = addNodesToLayer(2, makeLayer(rightInnerGraph));
        eastWestEdgeFromTo(rightInnerDummyNodes[1], rightInnerNodes[0]);
        eastWestEdgeFromTo(rightInnerDummyNodes[0], rightInnerNodes[1]);

        List<LNode> expectedNormalNodeOrderRight =
                switchOrderOfNodesInLayer(0, 1, rightInnerGraph.getLayers().get(1));

        List<LPort> expectedOrderOfPortsRight =
                Lists.newArrayList(rightOuterPorts[1], rightOuterPorts[0]);

        setUpAndMinimizeCrossings();
        List<LNode> actualExternalDummyOrderRight = rightInnerGraph.getLayers().get(0).getNodes();
        assertThat(actualExternalDummyOrderRight,
                is((List<LNode>) Lists.newArrayList(rightInnerDummyNodes)));

        assertThat(rightOuterNode.getPorts(), is(expectedOrderOfPortsRight));

        List<LNode> actualNormalOrderRight = rightInnerGraph.getLayers().get(1).getNodes();
        assertThat(actualNormalOrderRight, is(expectedNormalNodeOrderRight));
    }

    /**
     * <pre>
     * ____  ____
     * |*-+  +-*|
     * |  |\/|  |
     * |*-+/\+-*|
     * |--|  |--|
     * </pre>
     * 
     * With external origins of port dummies on right node wrong way around.
     */
    @Test
    public void allRecursive_nestedGraphWithWronglySortedDummyNodes_crossingStays() {
        LNode leftOuterNode = addNodeToLayer(makeLayer(graph));
        LNode rightOuterNode = addNodeToLayer(makeLayer(graph));
        LPort[] leftOuterPorts = addPortsOnSide(2, leftOuterNode, PortSide.EAST);
        LPort[] rightOuterPorts = addPortsOnSide(2, rightOuterNode, PortSide.WEST);
        addEdgeBetweenPorts(leftOuterPorts[0], rightOuterPorts[0]);
        addEdgeBetweenPorts(leftOuterPorts[1], rightOuterPorts[1]);

        makeNestedTwoNodeGraphWithEasternPorts(leftOuterNode, leftOuterPorts);

        LGraph rightInnerGraph = nestedGraph(rightOuterNode);
        LNode[] rightInnerDummyNodes =
                addExternalPortDummiesToLayer(makeLayer(rightInnerGraph), rightOuterPorts);
        LNode[] rightInnerNodes = addNodesToLayer(2, makeLayer(rightInnerGraph));
        eastWestEdgeFromTo(rightInnerDummyNodes[0], rightInnerNodes[0]);
        eastWestEdgeFromTo(rightInnerDummyNodes[1], rightInnerNodes[1]);

        graph.setProperty(LayeredOptions.CROSS_MIN_RECURSIVE, true);

        List<LNode> expectedNormalNodeOrderRight = Lists.newArrayList(rightInnerNodes);

        List<LPort> expectedOrderOfPortsRight = Lists.newArrayList(rightOuterPorts);

        setUpAndMinimizeCrossings();

        assertThat(rightOuterNode.getPorts(), is(expectedOrderOfPortsRight));

        List<LNode> actualNormalOrderRight = rightInnerGraph.getLayers().get(1).getNodes();
        assertThat(actualNormalOrderRight, is(expectedNormalNodeOrderRight));
    }

    /**
     * <pre>
     *       /*
     *      / ____
     * *   *==|  |
     * \\ //  |  |
     *  xx    |  |
     * // \\  |  |
     * *   *==|__|
     * </pre>
     * 
     * This test was used in development and can be reused if there is a problem with the
     * thoroughness value. It is deactivated because it depends on a random value and is slow.
     */
    @Ignore
    public void givenGraphWhichCanBeRepairedGivenGoodRandomStart_ShouldFindBestSolution() {
        Layer[] layers = makeLayers(3);
        LNode[] leftNodes = addNodesToLayer(2, layers[0]);
        LNode[] middleNodes = addNodesToLayer(2, layers[1]);
        LNode[] rightNodes = addNodesToLayer(3, layers[2]);
        setFixedOrderConstraint(rightNodes[1]);
        eastWestEdgesFromTo(2, leftNodes[0], middleNodes[1]);
        eastWestEdgesFromTo(2, leftNodes[1], middleNodes[0]);
        eastWestEdgeFromTo(middleNodes[0], rightNodes[0]);
        eastWestEdgesFromTo(2, middleNodes[1], rightNodes[1]);
        eastWestEdgesFromTo(2, middleNodes[0], rightNodes[1]);

        List<LNode> expectedWrongOrder = getNodesInLayer(0);
        List<LNode> expectedCorrectOrder = switchOrderOfNodesInLayer(0, 1, 0);

        setUpAndMinimizeCrossings();

        assertThat(graph.getLayers().get(0).getNodes(), is(expectedWrongOrder));

        graph.setProperty(InternalProperties.RANDOM, new Random()); // AAA! Random in Test!!!
        graph.setProperty(LayeredOptions.THOROUGHNESS, 100);
        for (int i = 0; i < 100; i++) {
            setUpAndMinimizeCrossings();
            assertThat(graph.getLayers().get(0).getNodes(), is(expectedCorrectOrder));
            graph.getLayers().get(0).getNodes().clear();
            graph.getLayers().get(0).getNodes().addAll(expectedWrongOrder);
        }
    }

    /**
     * <pre>
     * ____  
     * |  |\ /-*
     * |  | x
     * |*-+/ \-*
     * |--|
     * </pre>
     */
    @Test
    public void givenGraphWithNormalEdgeAndHierarchicalEdgeCrossing_ShouldRemoveCrossing() {
        Layer[] layers = makeLayers(2);
        LNode leftOuterNode = addNodeToLayer(layers[0]);
        LNode[] rightNodes = addNodesToLayer(2, layers[1]);
        eastWestEdgeFromTo(leftOuterNode, rightNodes[1]);

        Layer[] innerLayers = makeLayers(2, nestedGraph(leftOuterNode));
        LNode innerNormalNode = addNodeToLayer(innerLayers[0]);
        LPort hierarchPort = addPortOnSide(leftOuterNode, PortSide.EAST);
        LNode innerDummyNode = addExternalPortDummyNodeToLayer(innerLayers[1], hierarchPort);
        eastWestEdgeFromTo(innerNormalNode, innerDummyNode);

        LPort rightTopPort = addPortOnSide(rightNodes[0], PortSide.WEST);
        addEdgeBetweenPorts(hierarchPort, rightTopPort);

        List<LNode> expectedCorrectOrder = switchOrderOfNodesInLayer(0, 1, 1);

        setUpAndMinimizeCrossings();

        assertThat(graph.getLayers().get(1).getNodes(), is(expectedCorrectOrder));
    }

    /**
     * <pre>
     * ____  
     * |  |\ /-*
     * |  | x
     * |*-+/ \-*
     * |  p
     * |--|
     * </pre>
     * 
     * p is a port with no edge.
     */
    @Test
    public void givenGraphWithPortWithoutEdge_ShouldRemoveCrossing() {
        Layer[] layers = makeLayers(2);
        LNode leftOuterNode = addNodeToLayer(layers[0]);
        LNode[] rightNodes = addNodesToLayer(2, layers[1]);
        eastWestEdgeFromTo(leftOuterNode, rightNodes[1]);

        Layer[] innerLayers = makeLayers(2, nestedGraph(leftOuterNode));
        LNode innerNormalNode = addNodeToLayer(innerLayers[0]);
        LPort hierarchPort = addPortOnSide(leftOuterNode, PortSide.EAST);
        LNode innerDummyNode = addExternalPortDummyNodeToLayer(innerLayers[1], hierarchPort);
        eastWestEdgeFromTo(innerNormalNode, innerDummyNode);

        addPortOnSide(leftOuterNode, PortSide.EAST);

        LPort rightTopPort = addPortOnSide(rightNodes[0], PortSide.WEST);
        addEdgeBetweenPorts(hierarchPort, rightTopPort);

        List<LNode> expectedCorrectOrder = switchOrderOfNodesInLayer(0, 1, 1);

        setUpAndMinimizeCrossings();
        assertThat(graph.getLayers().get(1).getNodes(), is(expectedCorrectOrder));

    }

    /**
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
    public void showsErrorInCrossingCountingAlgorithmsForFreePortOrder() {
        LNode leftNode = addNodeToLayer(makeLayer(graph));
        LNode[] rightNodes = addNodesToLayer(2, makeLayer(graph));
        setFixedOrderConstraint(rightNodes[0]);
        setFixedOrderConstraint(rightNodes[1]);
        // setFixedOrderConstraint(leftNode);
        LPort leftTopPort = addPortOnSide(leftNode, PortSide.EAST);
        LPort leftLowerPort = addPortOnSide(leftNode, PortSide.EAST);
        LPort rightUpperNodeLowerPort = addPortOnSide(rightNodes[0], PortSide.WEST);
        LPort rightUpperNodeTopPort = addPortOnSide(rightNodes[0], PortSide.WEST);
        LPort rightLowerPort = addPortOnSide(rightNodes[1], PortSide.WEST);
        addEdgeBetweenPorts(leftTopPort, rightUpperNodeTopPort);
        addEdgeBetweenPorts(leftTopPort, rightLowerPort);
        addEdgeBetweenPorts(leftLowerPort, rightUpperNodeLowerPort);

        List<LNode> expectedCorrectOrder = switchOrderOfNodesInLayer(0, 1, 1);

        setUpAndMinimizeCrossings();

        assertThat(graph.getLayers().get(1).getNodes(), is(expectedCorrectOrder));
    }

    /**
     * <pre>
     * ___  ____
     * | |\/+-*|
     * |_|/\|  |
     *      |--|
     * </pre>
     */
    @Test
    public void nonRecursive_givenGraphWithCrossingWithFixedPortOrderToHierarchicalGraph_ShouldRemoveCrossing() {
        LNode leftNode = addNodeToLayer(makeLayer(graph));
        setFixedOrderConstraint(leftNode);
        LNode rightOuterNode = addNodeToLayer(makeLayer(graph));
        eastWestEdgeFromTo(leftNode, rightOuterNode);
        LPort rightOuterPort = addPortOnSide(rightOuterNode, PortSide.WEST);
        LPort leftLowerPort = addPortOnSide(leftNode, PortSide.EAST);
        addEdgeBetweenPorts(leftLowerPort, rightOuterPort);

        LGraph rightInnerGraph = nestedGraph(rightOuterNode);
        LNode rightInnerDummyNode =
                addExternalPortDummyNodeToLayer(makeLayer(rightInnerGraph), rightOuterPort);
        LNode rightInnerNode = addNodeToLayer(makeLayer(rightInnerGraph));
        eastWestEdgeFromTo(rightInnerDummyNode, rightInnerNode);

        List<LPort> expectedPortOrderRight = Lists.newArrayList(rightOuterNode.getPorts().get(1),
                rightOuterNode.getPorts().get(0));

        setUpAndMinimizeCrossings();
        if (crossMinType.isDeterministic()) {
            assertThat(graph.getLayers().get(1).getNodes().get(0).getPorts(),
                    is(expectedPortOrderRight));
        }
    }

    /**
     * <pre>
     * ____  _____
     * |  |  +-*-+-*
     * |  |\/|   |
     * |  |/\+-*-+-*
     * |--|  |---|
     * </pre>
     */
    @Test
    public void givenGraphWhichMustTransportSwitchedPortOrderThroughHierarchy_ShouldContainNoCrossing() {
        LNode leftNode = addNodeToLayer(makeLayer(graph));
        // setFixedOrderConstraint(leftNode);
        LNode middleOuterNode = addNodeToLayer(makeLayer(graph));
        LNode[] rightNodes = addNodesToLayer(2, makeLayer(graph));

        LPort[] middleOuterRightPorts = addPortsOnSide(2, middleOuterNode, PortSide.EAST);
        eastWestEdgeFromTo(middleOuterRightPorts[0], rightNodes[0]);
        eastWestEdgeFromTo(middleOuterRightPorts[1], rightNodes[1]);

        LPort[] leftPorts = addPortsOnSide(2, leftNode, PortSide.EAST);
        LPort[] middleOuterLeftPorts = addPortsOnSide(2, middleOuterNode, PortSide.WEST);
        addEdgeBetweenPorts(leftPorts[0], middleOuterLeftPorts[0]);
        addEdgeBetweenPorts(leftPorts[1], middleOuterLeftPorts[1]);

        LGraph innerGraph = nestedGraph(middleOuterNode);
        LNode[] leftInnerDummyNodes =
                addExternalPortDummiesToLayer(makeLayer(innerGraph), middleOuterLeftPorts);
        LNode[] innerNodes = addNodesToLayer(2, makeLayer(innerGraph));
        LNode[] rightInnerDummyNodes =
                addExternalPortDummiesToLayer(makeLayer(innerGraph), middleOuterRightPorts);

        eastWestEdgeFromTo(innerNodes[0], rightInnerDummyNodes[0]);
        eastWestEdgeFromTo(innerNodes[1], rightInnerDummyNodes[1]);
        eastWestEdgeFromTo(leftInnerDummyNodes[0], innerNodes[0]);
        eastWestEdgeFromTo(leftInnerDummyNodes[1], innerNodes[1]);

        List<LPort> expextedPorts = copyPortsInIndexOrder(middleOuterNode, 1, 0, 3, 2);

        setAllGraphsToGreedySwitchType(graph, GreedySwitchType.ONE_SIDED);

        setUpAndMinimizeCrossings();

        assertThat(graph.getLayers().get(1).getNodes().get(0).getPorts(), is(expextedPorts));
    }

    /**
     * <pre>
     *    _____  ____
     * *--+-*-+  |  |
     *    |   |\/|  |
     * *--+-*-+/\|  |
     *    |---|  |--|
     * </pre>
     */
    @Test
    public void givenGraphTransportsSwitchedPortsThroughHierarchyFromEastToWest_ShouldContainNoCrossing() {
        LNode[] leftNodes = addNodesToLayer(2, makeLayer(graph));
        LNode middleOuterNode = addNodeToLayer(makeLayer(graph));
        LNode rightNode = addNodeToLayer(makeLayer(graph));
        // setFixedOrderConstraint(rightNode);

        LPort[] leftPortsRightNode = addPortsOnSide(2, rightNode, PortSide.WEST);
        LPort[] middleOuterRightPorts = addPortsOnSide(2, middleOuterNode, PortSide.EAST);
        addEdgeBetweenPorts(middleOuterRightPorts[0], leftPortsRightNode[0]);
        addEdgeBetweenPorts(middleOuterRightPorts[1], leftPortsRightNode[1]);

        LPort[] middleOuterLeftPorts = addPortsOnSide(2, middleOuterNode, PortSide.WEST);
        eastWestEdgeFromTo(leftNodes[0], middleOuterLeftPorts[1]);
        eastWestEdgeFromTo(leftNodes[1], middleOuterLeftPorts[0]);

        LGraph innerGraph = nestedGraph(middleOuterNode);
        LNode[] leftInnerDummyNodes =
                addExternalPortDummiesToLayer(makeLayer(innerGraph), middleOuterLeftPorts);
        LNode[] innerNodes = addNodesToLayer(2, makeLayer(innerGraph));
        LNode[] rightInnerDummyNodes =
                addExternalPortDummiesToLayer(makeLayer(innerGraph), middleOuterRightPorts);

        eastWestEdgeFromTo(innerNodes[0], rightInnerDummyNodes[0]);
        eastWestEdgeFromTo(innerNodes[1], rightInnerDummyNodes[1]);
        eastWestEdgeFromTo(leftInnerDummyNodes[0], innerNodes[0]);
        eastWestEdgeFromTo(leftInnerDummyNodes[1], innerNodes[1]);

        List<LPort> expectedPortOrderMiddleNode =
                copyPortsInIndexOrder(middleOuterNode, 1, 0, 3, 2);

        // sweep backward
        random.setNextBoolean(false);
        setUpAndMinimizeCrossings();

        assertThat(graph.getLayers().get(1).getNodes().get(0).getPorts(),
                is(expectedPortOrderMiddleNode));
    }

    /**
     * <pre>
     *  *-------
     * ____  __|_
     * |*-+  +-*|
     * |  |\/|  |
     * |*-+/\+-*|
     * |--|  |--|
     *   |_____*
     * 
     * </pre>
     */
    @Test
    public void givenSimpleHierarchicalCrossWithNorthSouthEdge_ShouldResultInNoCrossing() {
        LNode[] leftNodes = addNodesToLayer(3, makeLayer(graph));
        LNode leftOuterNode = leftNodes[1];
        LNode[] rightNodes = addNodesToLayer(3, makeLayer(graph));
        LNode rightOuterNode = rightNodes[1];

        // adds first port, so must be before other edges.
        addNorthSouthEdge(PortSide.NORTH, rightOuterNode, rightNodes[0], leftNodes[0], true);
        LPort[] leftOuterPorts = addPortsOnSide(2, leftOuterNode, PortSide.EAST);
        LPort[] rightOuterPorts = addPortsOnSide(2, rightOuterNode, PortSide.WEST);
        addEdgeBetweenPorts(leftOuterPorts[0], rightOuterPorts[0]);
        addEdgeBetweenPorts(leftOuterPorts[1], rightOuterPorts[1]);

        makeNestedTwoNodeGraphWithEasternPorts(leftOuterNode, leftOuterPorts);

        addNorthSouthEdge(PortSide.SOUTH, leftOuterNode, leftNodes[2], rightNodes[2], false);

        LGraph rightInnerGraph =
                makeNestedTwoNodeGraphWithWesternPorts(rightOuterNode, rightOuterPorts);

        List<LNode> expectedExternalDummyOrderRight =
                switchOrderOfNodesInLayer(0, 1, rightInnerGraph.getLayers().get(0));
        List<LNode> expectedNormalNodeOrderRight =
                switchOrderOfNodesInLayer(0, 1, rightInnerGraph.getLayers().get(1));

        List<LPort> expectedOrderOfPortsRight = Lists.newArrayList(rightOuterNode.getPorts().get(0),
                rightOuterPorts[1], rightOuterPorts[0]);

        setUpAndMinimizeCrossings();
        List<LNode> actualExternalDummyOrderRight = rightInnerGraph.getLayers().get(0).getNodes();
        assertThat(actualExternalDummyOrderRight, is(expectedExternalDummyOrderRight));

        assertThat(rightOuterNode.getPorts(), is(expectedOrderOfPortsRight));

        List<LNode> actualNormalOrderRight = rightInnerGraph.getLayers().get(1).getNodes();
        assertThat(actualNormalOrderRight, is(expectedNormalNodeOrderRight));
    }

    /**
     * <pre>
     * *\ /*
     *   x
     *  / \
     * *---*
     * </pre>
     * 
     * Node order free left bottom node.
     */
    @Test
    public void givenCrossWhichWouldCauseCrossingIfPortOrderWouldBeFixed_ShouldBeRemoved() {
        LNode[] leftNodes = addNodesToLayer(2, makeLayer(graph));
        LNode[] rightNodes = addNodesToLayer(2, makeLayer(graph));
        eastWestEdgeFromTo(leftNodes[0], rightNodes[1]);
        eastWestEdgeFromTo(leftNodes[1], rightNodes[0]);
        eastWestEdgeFromTo(leftNodes[1], rightNodes[1]);

        List<LNode> expectedOrderRight = switchOrderOfNodesInLayer(0, 1, graph.getLayers().get(1));

        setUpAndMinimizeCrossings();

        List<LNode> actualOrderRight = graph.getLayers().get(1).getNodes();
        assertThat(actualOrderRight, is(expectedOrderRight));

    }

    /**
     * <pre>
     * ____  _____
     * |*-+  +-* |
     * |  |\/|   |
     * |*-+/\+-* |
     * |  |  |*-*|
     * |  |  |*-*|
     * |  |  |*-*|
     * |  |  |*-*|
     * |  |  |*-*|
     * |  |  |*-*|
     * |  |  |*-*|
     * |  |  |*-*|
     * |--|  |---|
     * </pre>
     */
    @Test
    public void givenSimpleHierarchicalNodeWithLowConnectivity_DoesNotUseHierarchySweep() {
        if (!crossMinType.isDeterministic()) {

            LNode leftOuterNode = addNodeToLayer(makeLayer(graph));
            LNode rightOuterNode = addNodeToLayer(makeLayer(graph));
            LPort[] leftOuterPorts = addPortsOnSide(2, leftOuterNode, PortSide.EAST);
            LPort[] rightOuterPorts = addPortsOnSide(2, rightOuterNode, PortSide.WEST);

            addEdgeBetweenPorts(leftOuterPorts[0], rightOuterPorts[0]);
            addEdgeBetweenPorts(leftOuterPorts[1], rightOuterPorts[1]);
            LGraph leftInnerGraph =
                    makeNestedTwoNodeGraphWithEasternPorts(leftOuterNode, leftOuterPorts);
            LGraph rightInnerGraph =
                    makeNestedTwoNodeGraphWithWesternPorts(rightOuterNode, rightOuterPorts);
            Layer leftLayer = rightInnerGraph.getLayers().get(1);
            LNode[] leftNodes = addNodesToLayer(7, leftLayer);
            LNode[] rightNodes = addNodesToLayer(7, makeLayer(rightInnerGraph));
            for (int i = 0; i < 7; i++) {
                eastWestEdgeFromTo(leftNodes[i], rightNodes[i]);
            }

            graph.setProperty(LayeredOptions.RECURSIVE_BOUNDARY, 0.2f);

            List<LNode> expectedExternalDummyOrderRight =
                    Lists.newArrayList(rightInnerGraph.getLayers().get(0));
            List<LPort> expectedOrderOfPortsRight = Lists.newArrayList(rightOuterPorts);
            List<LNode> expectedNodeOrderLeftInner =
                    switchOrderOfNodesInLayer(0, 1, leftInnerGraph.getLayers().get(0));

            setUpAndMinimizeCrossings();
            List<LNode> actualExternalDummyOrderRight =
                    rightInnerGraph.getLayers().get(0).getNodes();
            assertThat(actualExternalDummyOrderRight, is(expectedExternalDummyOrderRight));

            assertThat(rightOuterNode.getPorts(), is(expectedOrderOfPortsRight));

            List<LNode> actualNormalOrderLeft = leftInnerGraph.getLayers().get(0).getNodes();
            assertThat(actualNormalOrderLeft, is(expectedNodeOrderLeftInner));
        }
    }

    /**
     * <pre>
     *  
     * __________________
     * |___        ___  |
     * || |===*  --| |  |    ______
     * || |    \/  | |  |    |*  *|
     * || |    /\  | |  |----| \/ |
     * ||_|===*  --|_|  |    | /\ |
     * |      ___  ___  |    |*  *|
     * |      | |\/| |  |    |____|
     * |      |_|/\|_|  |
     * |____________^___|
     * .            |
     *           port orders fixed
     * </pre>
     * 
     * First Layer and last layer in fixed order.
     * 
     * @return graph of the form above.
     */
    @Test
    public void inRecursiveLayout_althoughBackwardSweepNotTakenStillCorrectsPortOrder() {
        LNode leftOuterNode = addNodeToLayer(makeLayer());
        LNode rightOuterNode = addNodeToLayer(makeLayer());

        eastWestEdgeFromTo(leftOuterNode, rightOuterNode);

        LGraph leftInnerGraph = nestedGraph(leftOuterNode);
        Layer[] layers = makeLayers(3, leftInnerGraph);
        LNode leftNode = addNodeToLayer(layers[0]);
        LNode[] middleNodes = addNodesToLayer(3, layers[1]);
        LNode[] rightNodes = addNodesToLayer(2, layers[2]);

        eastWestEdgeFromTo(middleNodes[0], rightNodes[0]);
        eastWestEdgeFromTo(middleNodes[1], rightNodes[0]);

        eastWestEdgeFromTo(middleNodes[2], rightNodes[1]);
        eastWestEdgeFromTo(middleNodes[2], rightNodes[1]);
        eastWestEdgeFromTo(leftNode, middleNodes[0]);
        eastWestEdgeFromTo(leftNode, middleNodes[0]);
        eastWestEdgeFromTo(leftNode, middleNodes[1]);
        eastWestEdgeFromTo(leftNode, middleNodes[1]);

        setFixedOrderConstraint(rightNodes[1]);
        setFixedOrderConstraint(rightNodes[0]);
        setFixedOrderConstraint(leftNode);

        LGraph rightInnerGraph = nestedGraph(rightOuterNode);
        LNode[] rightGraphLeftNodes = addNodesToLayer(2, makeLayer(rightInnerGraph));
        LNode[] rightGraphRightNodes = addNodesToLayer(2, makeLayer(rightInnerGraph));

        eastWestEdgeFromTo(rightGraphLeftNodes[0], rightGraphRightNodes[1]);
        eastWestEdgeFromTo(rightGraphLeftNodes[1], rightGraphRightNodes[0]);
        setUpIds();

        if (!crossMinType.isDeterministic()) {

            List<LPort> expectedPortOrderBottomMiddleNode =
                    copyPortsInIndexOrder(middleNodes[2], 1, 0);

            setUpAndMinimizeCrossings();

            Layer middleLayer = leftInnerGraph.getLayers().get(1);
            assertTrue(middleLayer.toString(),
                    inOrder(middleNodes[0], middleNodes[1], middleLayer));
            assertThat(middleNodes[2].getPorts(), is(expectedPortOrderBottomMiddleNode));
        }
    }

    /**
     * <pre>
     * _______  
     * |*  *-|--*
     * | \/  |
     * | /\  |
     * |*  *-|--*
     * |*--* |
     * |*--* |
     * |*--* |
     * |*--* |
     * |*--* |
     * |*--* |
     * |-----|
     * </pre>
     */
    @Test
    public void recursiveLayout_givenCrossInFirstLevelCompoundNode_sortsPortsAccordingly() {
        // parent graph
        LNode leftOuterNode = addNodeToLayer(makeLayer(graph));
        LNode[] rightNodes = addNodesToLayer(2, makeLayer(graph));
        LPort[] leftOuterPorts = addPortsOnSide(2, leftOuterNode, PortSide.EAST);
        eastWestEdgeFromTo(leftOuterPorts[0], rightNodes[0]);
        eastWestEdgeFromTo(leftOuterPorts[1], rightNodes[1]);

        // child graph
        LGraph leftInnerGraph = nestedGraph(leftOuterNode);
        LNode[] leftInnerNodesLeft = addNodesToLayer(9, makeLayer(leftInnerGraph));
        LNode[] leftInnerNodesRight = addNodesToLayer(9, makeLayer(leftInnerGraph));
        LNode[] leftInnerDummyNodes =
                addExternalPortDummiesToLayer(makeLayer(leftInnerGraph), leftOuterPorts);
        eastWestEdgeFromTo(leftInnerNodesRight[0], leftInnerDummyNodes[0]);
        eastWestEdgeFromTo(leftInnerNodesRight[1], leftInnerDummyNodes[1]);
        eastWestEdgeFromTo(leftInnerNodesLeft[0], leftInnerNodesRight[1]);
        eastWestEdgeFromTo(leftInnerNodesLeft[1], leftInnerNodesRight[0]);

        for (int i = 2; i < leftInnerNodesLeft.length; i++) {
            eastWestEdgeFromTo(leftInnerNodesLeft[i], leftInnerNodesRight[i]);
        }

        List<LNode> expectedOrderRight = switchOrderOfNodesInLayer(0, 1, 1);
        List<LPort> expectedPortOrderLeft = copyPortsInIndexOrder(leftOuterNode, 1, 0);

        setUpAndMinimizeCrossings();

        if (!crossMinType.isDeterministic()) {
            assertThat(leftOuterNode.getPorts(), is(expectedPortOrderLeft));
            assertThat(graph.getLayers().get(1).getNodes(), is(expectedOrderRight));
        }
    }

    /**
     * <pre>
     * ___
     * | |==|
     * | |  |
     * | |--+--*
     * |_|--|--*
     *  ^
     *fixed port order
     * </pre>
     */
    @Test
    public void doesNotChangeLayoutDueToHyperedgeCounter() {
        LNode leftNode = addNodeToLayer(makeLayer());
        LNode[] rightNodes = addNodesToLayer(2, makeLayer());
        LPort lowerRightNodePort = addPortOnSide(rightNodes[1], PortSide.WEST);
        LPort leftNodePort = addPortOnSide(rightNodes[0], PortSide.EAST);

        addEdgeBetweenPorts(lowerRightNodePort, leftNodePort);
        addEdgeBetweenPorts(lowerRightNodePort, leftNodePort);
        eastWestEdgeFromTo(leftNode, rightNodes[0]);
        eastWestEdgeFromTo(leftNode, lowerRightNodePort);
        setPortOrderFixed(leftNode);

        List<LNode> expectedOrderRight = Lists.newArrayList(rightNodes);

        random.setNextBoolean(false);
        setUpAndMinimizeCrossings();

        if (crossMinType == CrossMinType.BARYCENTER) {
            assertThat(graph.getLayers().get(1).getNodes(), is(expectedOrderRight));
        }
    }

    /**
     * <pre>
     *   --*
     *   |    ____
     *   //*--|  |
     * //|    |  |
     *|| --*--|__|
     * \\ 
     *  \\
     *   \\
     *     *
     * </pre>
     */
    @Test
    public void inLayerCrossingsInLeftMostLayer() {
        LNode[] nodes = addNodesToLayer(4, makeLayer());
        LNode rightNode = addNodeToLayer(makeLayer());
        eastWestEdgeFromTo(nodes[2], rightNode);
        eastWestEdgeFromTo(nodes[1], rightNode);
        addInLayerEdge(nodes[0], nodes[2], PortSide.WEST);
        final PortSide portSide = PortSide.WEST;
        LPort portOne = addPortOnSide(nodes[1], portSide);
        LPort portTwo = addPortOnSide(nodes[3], portSide);
        addEdgeBetweenPorts(portOne, portTwo);
        addEdgeBetweenPorts(portOne, portTwo);
        setFixedOrderConstraint(rightNode);
        List<LNode> actualOrder = graph.getLayers().get(0).getNodes();

        List<LNode> expectedOrderBarycenter = getListCopyInIndexOrder(actualOrder, 1, 0, 2, 3);
        List<LNode> expectedOrderGreedySwitch = getListCopyInIndexOrder(actualOrder, 1, 0, 2, 3);

        setUpAndMinimizeCrossings();
        if (crossMinType.equals(CrossMinType.BARYCENTER)) {
            assertThat(actualOrder, is(expectedOrderBarycenter));
        } else {
            assertThat(actualOrder, is(expectedOrderGreedySwitch));
        }
    }

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
    public void allRecursive_crossInFirstLevelCompoundNode_ShouldRemoveCrossing() {
        // parent graph
        LNode leftOuterNode = addNodeToLayer(makeLayer(graph));
        LNode[] rightNodes = addNodesToLayer(2, makeLayer(graph));
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
        graph.setProperty(LayeredOptions.CROSS_MIN_RECURSIVE, true);
        List<LNode> expectedOrderRight = switchOrderOfNodesInLayer(0, 1, 1);

        setUpAndMinimizeCrossings();

        assertThat(graph.getLayers().get(1).getNodes(), is(expectedOrderRight));
    }

    // TODO-alan change design to be able to test thouroughness value with dummy heuristic.

    private LPort[] reverse(final LPort[] rightOuterPorts) {
        LPort[] res = new LPort[rightOuterPorts.length];
        for (int i = 0; i < rightOuterPorts.length; i++) {
            LPort lPort = rightOuterPorts[i];
            res[rightOuterPorts.length - 1 - i] = lPort;
        }
        return res;
    }

    private void setUpAndMinimizeCrossings() {
        setAllGraphsToGreedySwitchType(graph, GreedySwitchType.ONE_SIDED);
        if (crossMinType == CrossMinType.BARYCENTER) {
            graph.setProperty(LayeredOptions.THOROUGHNESS, 1);
        }
        crossMin.process(graph, new BasicProgressMonitor());
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
}
