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

import java.util.Collections;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.p3order.counting.CrossMinUtil;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Lists;

/**
 * Counts crossings between in-layer edges incident to two nodes. In the case where there is free
 * port order and two edges go into one port, this crossing counter can in some cases count to few
 * crossings. See the ignored test in the test class.
 * 
 * @author alan
 */
public final class InLayerEdgeTwoNodeCrossingCounter extends InLayerEdgeAllCrossingsCounter {

    private final List<ComparableEdgeAndPort> relevantEdgesAndPorts;
    private int upperLowerCrossings;
    private int lowerUpperCrossings;
    private LNode upperNode;
    private LNode lowerNode;

    /**
     * Counts crossings between in-layer edges incident to two nodes.
     * 
     * @param nodeOrder
     *            the current order of the layer to be counted in.
     * @param assumeCompoundNodePortOrderFixed
     */
    private InLayerEdgeTwoNodeCrossingCounter(final LNode[] nodeOrder,
            final boolean assumeCompoundNodePortOrderFixed) {
        super(nodeOrder, false, assumeCompoundNodePortOrderFixed);
        relevantEdgesAndPorts = Lists.newArrayList();
    }

    /**
     * Counts crossings between in-layer edges incident to the given nodes. Use
     * {@link #getUpperLowerCrossings()} and {@link #getLowerUpperCrossings()} to access the
     * calculated values.
     * 
     * @param upper
     *            the upper node
     * @param lower
     *            the lower node
     */
    public void countCrossingsBetweenNodes(final LNode upper, final LNode lower) {
        upperNode = upper;
        lowerNode = lower;

        upperLowerCrossings = countCrossingsOnSide(PortSide.EAST);
        upperLowerCrossings += countCrossingsOnSide(PortSide.WEST);

        notifyOfSwitch(upper, lower);

        lowerUpperCrossings = countCrossingsOnSide(PortSide.EAST);
        lowerUpperCrossings += countCrossingsOnSide(PortSide.WEST);

        notifyOfSwitch(lower, upper);
    }

    /**
     * This class simply collects all edges and ports connected to the two nodes in questions, sorts
     * them by port position and uses the superclass method countCrossingsOn(LEdge edge, LPort
     * port).
     * 
     */
    private int countCrossingsOnSide(final PortSide side) {
        relevantEdgesAndPorts.clear();

        addEdgesAndPortsConnectedToNodesAndSort(side);

        return iterateThroughRelevantEdgesAndPortsAndCountCrossings();
    }

    private void addEdgesAndPortsConnectedToNodesAndSort(final PortSide side) {
        iterateThroughEdgesAndCollectThem(upperNode, side);
        iterateThroughEdgesAndCollectThem(lowerNode, side);
        Collections.sort(relevantEdgesAndPorts);
    }

    private void iterateThroughEdgesAndCollectThem(final LNode node, final PortSide side) {
        Iterable<LPort> ports = CrossMinUtil.inNorthSouthEastWestOrder(node, side);
        for (LPort port : ports) {
            for (LEdge edge : port.getConnectedEdges()) {
                if (!edge.isSelfLoop()) {
                    addThisEndOrBothEndsOfEdge(node, port, edge);
                }
            }
        }
    }

    private int iterateThroughRelevantEdgesAndPortsAndCountCrossings() {
        int crossings = 0;
        for (ComparableEdgeAndPort eP : relevantEdgesAndPorts) {
            crossings += super.countCrossingsOn(eP.edge, eP.port);
        }
        return crossings;
    }

    private void addThisEndOrBothEndsOfEdge(final LNode node, final LPort port, final LEdge edge) {
        relevantEdgesAndPorts.add(new ComparableEdgeAndPort(port, edge, positionOf(port)));

        if (isInLayer(edge) && notConnectedToOtherNode(edge, node)) {
            LPort otherEnd = otherEndOf(edge, port);
            relevantEdgesAndPorts
                    .add(new ComparableEdgeAndPort(otherEnd, edge, positionOf(otherEnd)));
        }
    }

    private boolean notConnectedToOtherNode(final LEdge edge, final LNode node) {
        if (node.equals(upperNode)) {
            return !edge.getTarget().getNode().equals(lowerNode)
                    && !edge.getSource().getNode().equals(lowerNode);
        } else {
            return !edge.getTarget().getNode().equals(upperNode)
                    && !edge.getSource().getNode().equals(upperNode);
        }
    }

    /**
     * This private class collects a port and a connected edge and can be sorted by portPosition.
     * 
     * @author alan
     *
     */
    private class ComparableEdgeAndPort implements Comparable<ComparableEdgeAndPort> {
        /** The port. */
        private final LPort port;
        /** The edge connected to it. */
        private final LEdge edge;
        /** The position of the port. */
        private final int portPosition;

        ComparableEdgeAndPort(final LPort port, final LEdge edge, final int portPosition) {
            this.port = port;
            this.edge = edge;
            this.portPosition = portPosition;
        }

        @Override
        public int compareTo(final ComparableEdgeAndPort o) {
            boolean thisPortBeforeOther = portPosition < o.portPosition;
            boolean thisPortAtSamePositionAsOther = portPosition == o.portPosition;
            boolean bothEdgesAreInLayerAndThisEdgeEndAfterOther = isInLayer(edge)
                    && isInLayer(o.edge)
                    && positionOf(otherEndOf(edge, port)) > positionOf(otherEndOf(o.edge, o.port));
            boolean edgesPointToSamePositions = otherEndOf(edge, port) == otherEndOf(edge, port);

            if (thisPortBeforeOther || thisPortAtSamePositionAsOther
                    && bothEdgesAreInLayerAndThisEdgeEndAfterOther) {
                return -1;
            } else if (thisPortAtSamePositionAsOther && edgesPointToSamePositions) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public String toString() {
            return "ComparableEdgeAndPort [port=" + port + ", edge=" + edge + ", portPosition="
                    + portPosition + "]";
        }

    }

    /**
     * @return number of upper lower crossings.
     */
    public int getUpperLowerCrossings() {
        return upperLowerCrossings;
    }

    /**
     * 
     * @return number of lower upper crossings.
     */
    public int getLowerUpperCrossings() {
        return lowerUpperCrossings;
    }

    private LPort otherEndOf(final LEdge edge, final LPort fromPort) {
        return fromPort == edge.getSource() ? edge.getTarget() : edge.getSource();
    }

    /**
     * Does not assume fixed port order. Crossings between edges connected to node with free port
     * order are assumed to be non-existent. Note that this is not always true.
     * 
     * @param nodeOrder
     *            the current order of nodes in layer.
     * 
     * @return the counter
     */
    public static InLayerEdgeTwoNodeCrossingCounter create(final LNode[] nodeOrder) {
        return new InLayerEdgeTwoNodeCrossingCounter(nodeOrder, false);
    }

    /**
     * Does not assume fixed port order. Crossings between edges connected to node with free port
     * order are assumed to be non-existent. Note that this is not always true.
     * 
     * @param nodeOrder
     *            the current order of nodes in layer.
     * 
     * @return the counter
     */
    public static InLayerEdgeTwoNodeCrossingCounter createAssumingHierarchicalNodePortOrderFixed(
            final LNode[] nodeOrder) {
        return new InLayerEdgeTwoNodeCrossingCounter(nodeOrder, true);
    }

}

