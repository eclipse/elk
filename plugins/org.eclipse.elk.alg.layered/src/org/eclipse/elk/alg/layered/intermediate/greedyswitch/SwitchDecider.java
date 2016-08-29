/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.greedyswitch;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.p3order.GraphData;
import org.eclipse.elk.alg.layered.p3order.counting.CrossingsCounter;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.Pair;

/**
 * This class decides whether two neighboring nodes should be switched. There are two variants:
 * <ul>
 * <li>OneSided – The traditional risky way: The decider checks if a switch would reduce crossings
 * on the given side of the layer whose nodes are to be switched.
 * <li>TwoSided – The faithless way: The decider checks if a switch would reduce crossings on both
 * sides of the layer whose nodes are to be switched.
 * </ul>
 * 
 * @author alan
 */
public final class SwitchDecider {
    private final LNode[] freeLayer;
    private final CrossingsCounter leftInLayerCounter;
    private final CrossingsCounter rightInLayerCounter;
    private final NorthSouthEdgeNeighbouringNodeCrossingsCounter northSouthCounter;
    private final CrossingMatrixFiller crossingMatrixFiller;
    private GraphData graphData;
    private CrossingsCounter parentRightCrossCounter;
    private CrossingsCounter parentLeftCrossCounter;
    private boolean oneSided;

    /**
     * Creates SwitchDecider.
     * 
     * @param freeLayerIndex
     *            The freeLayer to switch in.
     * @param graph
     *            The graph as LNode[][]
     * @param crossingMatrixFiller
     *            the crossing matrix filler
     * @param assumeCompoundNodeFixedPortOrder
     * @param portPositions
     *            port position array
     * @param parentRightCrossCounter
     *            crossings counter for counting crossings on the right of the child graph.
     * @param parentLeftCrossCounter
     *            crossings counter for counting crossings on the right of the child graph.
     *            TODO-alan consider this constructor. Its so long.
     */
    public SwitchDecider(final int freeLayerIndex, final LNode[][] graph,
            final CrossingMatrixFiller crossingMatrixFiller, final int[] portPositions,
            final GraphData graphData, final boolean oneSided, final CrossingsCounter parentLeftCrossCounter,
            final CrossingsCounter parentRightCrossCounter) {
        
        this.crossingMatrixFiller = crossingMatrixFiller;
        this.graphData = graphData;
        this.oneSided = oneSided;
        // TODO-alan Use the parents and initialize here if needed.
        this.parentLeftCrossCounter = parentLeftCrossCounter;
        this.parentRightCrossCounter = parentRightCrossCounter;
        if (freeLayerIndex >= graph.length) {
            throw new IndexOutOfBoundsException(
                    "Greedy SwitchDecider: Free layer layer not in graph.");
        }
        freeLayer = graph[freeLayerIndex];

        leftInLayerCounter = new CrossingsCounter(portPositions);
        leftInLayerCounter.initPortPositionsForInLayerCrossings(freeLayer, PortSide.WEST);
        rightInLayerCounter = new CrossingsCounter(portPositions);
        rightInLayerCounter.initPortPositionsForInLayerCrossings(freeLayer, PortSide.EAST);
        northSouthCounter = new NorthSouthEdgeNeighbouringNodeCrossingsCounter(freeLayer);
    }

    /**
     * Notifies in-layer counter of node switch for efficiency reasons.
     * 
     * @param upperNode
     *            a node
     * @param lowerNode
     *            a node
     */
    public void notifyOfSwitch(final LNode upperNode, final LNode lowerNode) {
        leftInLayerCounter.switchNodes(upperNode, lowerNode, PortSide.WEST);
        rightInLayerCounter.switchNodes(upperNode, lowerNode, PortSide.EAST);
        // In case of hierarchical layer sweep: Ports are ordered by final order at the end of the sweep.
    }

    /**
     * {@inheritDoc}
     */
    public boolean doesSwitchReduceCrossings(final int upperNodeIndex,
            final int lowerNodeIndex) {
        
        if (constraintsPreventSwitch(upperNodeIndex, lowerNodeIndex)) {
            return false;
        }

        LNode upperNode = freeLayer[upperNodeIndex];
        LNode lowerNode = freeLayer[lowerNodeIndex];
        
        Pair<Integer, Integer> leftInlayer =
                leftInLayerCounter.countInLayerCrossingsBetweenNodesInBothOrders(upperNode, lowerNode, PortSide.WEST);
        Pair<Integer, Integer> rightInlayer =
                rightInLayerCounter.countInLayerCrossingsBetweenNodesInBothOrders(upperNode, lowerNode, PortSide.EAST);
        northSouthCounter.countCrossings(upperNode, lowerNode);
        int upperLowerCrossings =
                crossingMatrixFiller.getCrossingMatrixEntry(upperNode, lowerNode)
                        + leftInlayer.getFirst() + rightInlayer.getFirst()
                        + northSouthCounter.getUpperLowerCrossings();
        int lowerUpperCrossings =
                crossingMatrixFiller.getCrossingMatrixEntry(lowerNode, upperNode)
                        + leftInlayer.getSecond() + rightInlayer.getSecond()
                        + northSouthCounter.getLowerUpperCrossings();

        if (!oneSided && graphData.hasParent() && !graphData.dontSweepInto()
                && upperNode.getType() == NodeType.EXTERNAL_PORT) {
            LPort upperPort = (LPort) upperNode.getProperty(InternalProperties.ORIGIN);
            LPort lowerPort = (LPort) lowerNode.getProperty(InternalProperties.ORIGIN);
            CrossingsCounter crossCounter =
                    upperPort.getSide() == PortSide.EAST ? parentRightCrossCounter : parentLeftCrossCounter;
            Pair<Integer, Integer> crossingNumbers =
                    crossCounter.countCrossingsBetweenPortsInBothOrders(upperPort, lowerPort);
            upperLowerCrossings += crossingNumbers.getFirst();
            lowerUpperCrossings += crossingNumbers.getSecond();
        }

        return upperLowerCrossings > lowerUpperCrossings;
    }


    /**
     * Check if in layer {@link InternalProperties.IN_LAYER_SUCCESSOR_CONSTRAINTS} or
     * {@link InternalProperties.IN_LAYER_LAYOUT_UNIT} constraints prevent a possible switch or if
     * the nodes are a normal node and a north south port dummy.
     * 
     * @param nodeIndex
     *            the index of the upper node, assuming a left-right order.
     * @param lowerNodeIndex
     *            the index of the lower node, assuming a left-right order.
     * @return true if constraints should prevent switching.
     */
    private boolean constraintsPreventSwitch(final int nodeIndex, final int lowerNodeIndex) {
        LNode upperNode = freeLayer[nodeIndex];
        LNode lowerNode = freeLayer[lowerNodeIndex];

        return haveSuccessorConstraints(upperNode, lowerNode)
                || haveLayoutUnitConstraints(upperNode, lowerNode)
                || areNormalAndNorthSouthPortDummy(upperNode, lowerNode);
    }

    private boolean haveSuccessorConstraints(final LNode upperNode, final LNode lowerNode) {
        List<LNode> constraints =
                upperNode.getProperty(InternalProperties.IN_LAYER_SUCCESSOR_CONSTRAINTS);
        boolean hasSuccessorConstraint =
                constraints != null && constraints.size() != 0 && constraints.contains(lowerNode);
        return hasSuccessorConstraint;
    }

    private boolean haveLayoutUnitConstraints(final LNode upperNode, final LNode lowerNode) {
        boolean neitherNodeIsLongEdgeDummy =
                upperNode.getType() != NodeType.LONG_EDGE
                        && lowerNode.getType() != NodeType.LONG_EDGE;

        // If upperNode and lowerNode are part of a layout unit not only containing themselves,
        // then the layout units must be equal for a switch to be allowed.
        LNode upperLayoutUnit = upperNode.getProperty(InternalProperties.IN_LAYER_LAYOUT_UNIT);
        LNode lowerLayoutUnit = lowerNode.getProperty(InternalProperties.IN_LAYER_LAYOUT_UNIT);
        boolean nodesHaveLayoutUnits =
                partOfMultiNodeLayoutUnit(upperNode, upperLayoutUnit)
                        || partOfMultiNodeLayoutUnit(lowerNode, lowerLayoutUnit);
        boolean areInDifferentLayoutUnits = upperLayoutUnit != lowerLayoutUnit;

        boolean upperNodeHasNorthernEdges = hasEdgesOnSide(upperNode, PortSide.NORTH);
        boolean lowerNodeHasSouthernEdges = hasEdgesOnSide(lowerNode, PortSide.SOUTH);

        boolean hasLayoutUnitConstraint =
                nodesHaveLayoutUnits && areInDifferentLayoutUnits || upperNodeHasNorthernEdges
                        || lowerNodeHasSouthernEdges;

        return neitherNodeIsLongEdgeDummy && hasLayoutUnitConstraint;
    }

    private boolean hasEdgesOnSide(final LNode node, final PortSide side) {
        Iterable<LPort> ports = node.getPorts(side);
        for (LPort port : ports) {
            if (port.getProperty(InternalProperties.PORT_DUMMY) != null
                    || port.getConnectedEdges().iterator().hasNext()) {
                return true;
            }
        }
        return false;
    }

    private boolean partOfMultiNodeLayoutUnit(final LNode node, final LNode layoutUnit) {
        return layoutUnit != null && layoutUnit != node;
    }

    private boolean areNormalAndNorthSouthPortDummy(final LNode upperNode, final LNode lowerNode) {
        return isNorthSouthPortNode(upperNode) && isNormalNode(lowerNode)
                || isNorthSouthPortNode(lowerNode) && isNormalNode(upperNode);
    }

    private boolean isNormalNode(final LNode node) {
        return node.getType() == NodeType.NORMAL;
    }

    private boolean isNorthSouthPortNode(final LNode node) {
        return node.getType() == NodeType.NORTH_SOUTH_PORT;
    }


    /**
     * The side on which to count crossings for the one-sided SwitchDecider.
     * 
     * @author alan
     *
     */
    public enum CrossingCountSide {
        /** Consider crossings to the west of the free layer. */
        WEST,
        /** Consider crossings to the east of the free layer. */
        EAST
    }

}