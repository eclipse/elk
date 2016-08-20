package org.eclipse.elk.alg.layered.p3order;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.TestGraphCreator;
import org.eclipse.elk.core.options.PortSide;
import org.junit.Test;

public class LayerSweepTypeDeciderTest extends TestGraphCreator {

    /**
     * <pre>
     *   __________
     *   | d--*   |
     *   | |  |   |
     *   | *  d-* |
     * *-+-*--*-*-+-*
     *   |________|
     * </pre>
     */
    @Test
    public void northSouthPortsChangeDecision() {
        Layer[] layers = makeLayers(3);
        LNode leftOuter = addNodeToLayer(layers[0]);
        LNode middleOuter = addNodeToLayer(layers[1]);
        LNode rightOuter = addNodeToLayer(layers[2]);
        LPort leftPortMiddleNode = addPortOnSide(middleOuter, PortSide.EAST);
        LPort rightPortMiddleNode = addPortOnSide(middleOuter, PortSide.WEST);
        eastWestEdgeFromTo(leftOuter, leftPortMiddleNode);
        eastWestEdgeFromTo(rightPortMiddleNode, rightOuter);
        LGraph nestedGraph = nestedGraph(middleOuter);
        Layer[] innerLayers = makeLayers(5, nestedGraph);
        LNode leftDummy = addExternalPortDummyNodeToLayer(innerLayers[0], leftPortMiddleNode);
        LNode[] firstLayer = addNodesToLayer(3, innerLayers[1]);
        LNode[] secondLayer = addNodesToLayer(3, innerLayers[2]);
        LNode[] thirdLayer = addNodesToLayer(2, innerLayers[3]);
        LNode rightDummy = addExternalPortDummyNodeToLayer(innerLayers[4], rightPortMiddleNode);
        eastWestEdgeFromTo(firstLayer[2], secondLayer[2]);
        eastWestEdgeFromTo(leftDummy, firstLayer[2]);
        eastWestEdgeFromTo(secondLayer[2], thirdLayer[1]);
        eastWestEdgeFromTo(leftDummy, firstLayer[2]);
    }
}
