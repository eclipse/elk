/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.greedyswitch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortSide;

/**
 * Counts the crossings caused by the order of north south port dummies when their respective normal
 * node in the same layer has a fixed port order. Also counts crossings between north south edges
 * and long edge dummies.
 * 
 * @author alan
 */
public class NorthSouthEdgeNeighbouringNodeCrossingsCounter {

    private int upperLowerCrossings;
    private int lowerUpperCrossings;
    private final Map<LPort, Integer> portPositions;
    private final LNode[] layer;
    private final boolean usesOrthogonalLayout;

    /**
     * Creates a counter for north south port crossings.
     * 
     * @param nodes
     *            the order of nodes in the layer in question.
     */
    public NorthSouthEdgeNeighbouringNodeCrossingsCounter(final LNode[] nodes) {
        usesOrthogonalLayout =
                nodes[0].getGraph().getProperty(LayoutOptions.EDGE_ROUTING) == EdgeRouting.ORTHOGONAL;
        layer = nodes;
        portPositions = new HashMap<LPort, Integer>();
        initializePortPositions();
    }

    /**
     * Since accessing the index of a port is linear, the positions are saved. To prevent dependency
     * problems, they are saved in a field of this Object. Ports are numbered as they are in the
     * list returned by getPorts().
     */
    private void initializePortPositions() {
        for (LNode node : layer) {
            setPortIdsOn(node, PortSide.SOUTH);
            setPortIdsOn(node, PortSide.NORTH);
        }
    }

    private void setPortIdsOn(final LNode node, final PortSide side) {
        Iterable<LPort> ports = PortIterable.inNorthSouthEastWestOrder(node, side);
        int portId = 0;
        for (LPort port : ports) {
            portPositions.put(port, portId++);
        }
    }

    /**
     * Counts north south port crossings and crossings between north south ports and dummy nodes,
     * for uppperNode and lowerNode.
     * 
     * @param upperNode
     *            The first node.
     * @param lowerNode
     *            The second node.
     */
    public void countCrossings(final LNode upperNode, final LNode lowerNode) {
        upperLowerCrossings = 0;
        lowerUpperCrossings = 0;

        processIfTwoNorthSouthNodes(upperNode, lowerNode);

        processIfNorthSouthLongEdgeDummyCrossing(upperNode, lowerNode);

        processIfNormalNodeWithNSPortsAndLongEdgeDummy(upperNode, lowerNode);
    }

    private void processIfTwoNorthSouthNodes(final LNode upperNode, final LNode lowerNode) {
        if (isNorthSouth(upperNode) && isNorthSouth(lowerNode)) {
            if (noFixedPortOrderOn(originOf(upperNode))
                    || haveDifferentOrigins(upperNode, lowerNode)) {
                return;
            }
            PortSide upperNodePortSide = getPortDirectionFromNorthSouthNode(upperNode);
            PortSide lowerNodePortSide = getPortDirectionFromNorthSouthNode(lowerNode);
            if (isNorthOfNormalNode(upperNode)) {
                countCrossingsOfTwoNorthSouthDummies(upperNode, lowerNode, upperNodePortSide,
                        lowerNodePortSide);
            } else {
                countCrossingsOfTwoNorthSouthDummies(lowerNode, upperNode, lowerNodePortSide,
                        upperNodePortSide);
            }
        }
    }

    private void countCrossingsOfTwoNorthSouthDummies(final LNode furtherFromNormalNode,
            final LNode closerToNormalNode, final PortSide furtherNodePortSide,
            final PortSide closerNodePortSide) {
        
        if (furtherNodePortSide == PortSide.EAST && closerNodePortSide == PortSide.EAST) {
            if (originPortPositionOf(furtherFromNormalNode) > originPortPositionOf(closerToNormalNode)) {
                upperLowerCrossings = numberOfEdgesConnectTo(closerToNormalNode);
            } else {
                lowerUpperCrossings = numberOfEdgesConnectTo(furtherFromNormalNode);
            }
        } else if (furtherNodePortSide == PortSide.WEST && closerNodePortSide == PortSide.WEST) {
            if (originPortPositionOf(furtherFromNormalNode) < originPortPositionOf(closerToNormalNode)) {
                upperLowerCrossings = numberOfEdgesConnectTo(closerToNormalNode);
            } else {
                lowerUpperCrossings = numberOfEdgesConnectTo(furtherFromNormalNode);
            }
        } else if (furtherNodePortSide == PortSide.WEST && closerNodePortSide == PortSide.EAST) {
            if (originPortPositionOf(furtherFromNormalNode) > originPortPositionOf(closerToNormalNode)) {
                upperLowerCrossings = numberOfEdgesConnectTo(closerToNormalNode);
                lowerUpperCrossings = numberOfEdgesConnectTo(furtherFromNormalNode);
            }
        } else {
            if (originPortPositionOf(furtherFromNormalNode) < originPortPositionOf(closerToNormalNode)) {
                upperLowerCrossings = numberOfEdgesConnectTo(closerToNormalNode);
                lowerUpperCrossings = numberOfEdgesConnectTo(furtherFromNormalNode);
            }
        }
    }

    private void processIfNorthSouthLongEdgeDummyCrossing(final LNode upperNode,
            final LNode lowerNode) {
        
        if (isNorthSouth(upperNode) && isLongEdgeDummy(lowerNode)) {
            if (isNorthOfNormalNode(upperNode)) {
                upperLowerCrossings = 1;
            } else {
                lowerUpperCrossings = 1;
            }
        } else if (isNorthSouth(lowerNode) && isLongEdgeDummy(upperNode)) {
            if (isNorthOfNormalNode(lowerNode)) {
                lowerUpperCrossings = 1;
            } else {
                upperLowerCrossings = 1;
            }
        }
    }

    private void processIfNormalNodeWithNSPortsAndLongEdgeDummy(final LNode upperNode,
            final LNode lowerNode) {
        
        if (isNormal(upperNode) && isLongEdgeDummy(lowerNode)) {
            upperLowerCrossings = numberOfNorthSouthEdges(upperNode, PortSide.SOUTH);
            lowerUpperCrossings = numberOfNorthSouthEdges(upperNode, PortSide.NORTH);
        }
        if (isNormal(lowerNode) && isLongEdgeDummy(upperNode)) {
            upperLowerCrossings = numberOfNorthSouthEdges(lowerNode, PortSide.NORTH);
            lowerUpperCrossings = numberOfNorthSouthEdges(lowerNode, PortSide.SOUTH);
        }
    }

    private int numberOfNorthSouthEdges(final LNode node, final PortSide side) {
        int numberOfEdges = 0;
        for (LPort port : node.getPorts(side)) {
            numberOfEdges += hasConnectedNorthSouthEdge(port) ? 1 : 0;
        }
        return numberOfEdges;
    }

    private boolean hasConnectedNorthSouthEdge(final LPort port) {
        return port.getProperty(InternalProperties.PORT_DUMMY) != null;
    }

    private boolean haveDifferentOrigins(final LNode upperNode, final LNode lowerNode) {
        return originOf(upperNode) != originOf(lowerNode);
    }

    private PortSide getPortDirectionFromNorthSouthNode(final LNode node) {
        assert isNorthSouth(node);
        boolean northSouthNodeOnlyHasOneInBetweenLayerEdge = node.getPorts().size() == 1;
        assert northSouthNodeOnlyHasOneInBetweenLayerEdge;
        return node.getPorts().get(0).getSide();
    }

    private int originPortPositionOf(final LNode node) {
        LPort origin = originPortOf(node);
        final LPort port = origin;
        return portPositions.get(port);
    }

    private LPort originPortOf(final LNode node) {
        LPort port = node.getPorts().get(0);
        LPort origin = (LPort) port.getProperty(InternalProperties.ORIGIN);
        return origin;
    }

    private boolean isNorthOfNormalNode(final LNode upperNode) {
        return originPortOf(upperNode).getSide() == PortSide.NORTH;
    }

    private LNode originOf(final LNode node) {
        return (LNode) node.getProperty(InternalProperties.ORIGIN);
    }

    private boolean noFixedPortOrderOn(final LNode node) {
        return !node.getProperty(LayoutOptions.PORT_CONSTRAINTS).isOrderFixed();
    }

    private boolean isLongEdgeDummy(final LNode node) {
        return node.getType() == NodeType.LONG_EDGE;
    }

    private boolean isNorthSouth(final LNode node) {
        return node.getType() == NodeType.NORTH_SOUTH_PORT;
    }

    private int numberOfEdgesConnectTo(final LNode node) {
        if (usesOrthogonalLayout) {
            return 1;
        }
        int n = 0;
        for (LPort port : node.getPorts()) {
            n += port.getDegree();
        }
        return n;
    }

    private boolean isNormal(final LNode node) {
        return node.getType() == NodeType.NORMAL;
    }

    /**
     * Get crossing count.
     * 
     * @return the crossings between when ordered upper - lower.
     */
    public int getUpperLowerCrossings() {
        return upperLowerCrossings;
    }

    /**
     * 
     * Get crossing count.
     * 
     * @return the crossings between when ordered lower - upper.
     */
    public int getLowerUpperCrossings() {
        return lowerUpperCrossings;
    }

}
