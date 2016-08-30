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
package org.eclipse.elk.alg.layered.p3order.counting;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.PortSide;

/**
 * Counts all crossings caused by the ordering of north/south ports and between north/south edges
 * and long-edge dummies.
 * 
 * @author alan
 */
public class NorthSouthEdgeAllCrossingsCounter {

    private int[] nodePositions;
    private final int[] portPositions;
    private int[] northCardinalities;
    private int[] southCardinalities;
    private LNode[] layer;
    private LNode currentOriginNode;
    private int numberOfNorthSouthEdges;
    private int numberOfLongEdgeDummies;
    private boolean northOfCurrentOriginNode = true;
    private boolean edgesRoutedOrthogonally;

    /**
     * Creates counter.
     * 
     * @param portPositions
     *            An array the length of all ports in the graph.
     */
    public NorthSouthEdgeAllCrossingsCounter(final int[] portPositions) {
        this.portPositions = portPositions;
    }

    private void initPositionsAndCardinalities() {
        int nodeId = 0;
        for (LNode element : layer) {
            LNode node = element;

            if (!isLongEdgeDummy(node)) {
                nodePositions[node.id] = nodeId++;
            }

            setPortPositionsAndCardinalitiesFor(node, northCardinalities, PortSide.NORTH);
            setPortPositionsAndCardinalitiesFor(node, southCardinalities, PortSide.SOUTH);
        }
    }

    private void setPortPositionsAndCardinalitiesFor(final LNode node, final int[] cardinalities,
            final PortSide side) {
        Iterable<LPort> ports = CrossMinUtil.inNorthSouthEastWestOrder(node, side);
        int portId = 0;
        for (LPort port : ports) {
            portPositions[port.id] = portId++;
        }
        cardinalities[node.id] = portId;
    }

    /**
     * <p>
     * Counts all crossings caused by the ordering of north/south ports and between north/south
     * edges and long-edge dummies. Assume the following layout:
     * </p>
     * 
     * <pre>
     *       *---*
     *       |
     *     *-+--*
     *     | |
     *   x-+-+--*
     *  _|_|_|_
     *  |_____|
     * </pre>
     * 
     * <p>
     * This can be viewed as a matrix:
     * </p>
     * 
     * <pre>
     *   0 1 2 
     * 0  |  *
     * 1  |*__
     * 2 x
     * </pre>
     * 
     * <p>
     * Thereby the eastern edge of node x causes crossings with north/south edges of all nodes which
     * are to the right and above it. For western edges this is the same to the left and below.
     * </p>
     * 
     * @param nodes
     *            The current order of the nodes.
     * @return number of crossings
     */
    public int countCrossings(final LNode[] nodes) {
        initialize(nodes);

        int crossings = 0;
        for (LNode node : layer) {
            crossings += getLongEdgeDummyCrossings(node);
            if (hasPortOnSide(node, PortSide.NORTH)) {
                crossings += getCrossingsOnSide(node, PortSide.NORTH);
            }
            if (hasPortOnSide(node, PortSide.SOUTH)) {
                crossings += getCrossingsOnSide(node, PortSide.SOUTH);
            }
        }
        return crossings;
    }

    private void initialize(final LNode[] nodes) {
        layer = nodes;
        nodePositions = new int[layer.length];
        northCardinalities = new int[layer.length];
        southCardinalities = new int[layer.length];
        edgesRoutedOrthogonally = layer[0].getGraph()
                .getProperty(LayeredOptions.EDGE_ROUTING) == EdgeRouting.ORTHOGONAL;
        initPositionsAndCardinalities();
    }

    /**
     * Each time we are on the north side of the origin node of north/south dummies, we collect the
     * current number of north/south dummies which already have been visited and each time we meet a
     * long edge dummy we add to the crossing count the current number of north/south dummies. On
     * the southern side, we count the number of long edge dummies we meet and each time we meet a
     * north/south dummy, we add to the crossing count the current number of long edge dummies. <br>
     * Note, that an origin node is the normal node connected to a north/south dummy.
     * 
     * @param node
     * @return
     */
    private int getLongEdgeDummyCrossings(final LNode node) {
        int crossings = 0;
        if (isNorthSouth(node)) {
            if (originIsNotCurrentOrigin(node)) {
                resetDummyCountAndSetCurrentOriginNodeTo(originPortOf(node).getNode());
                // since we always iterate from north to south in a layer:
                northOfCurrentOriginNode = true;
            }
            if (northOfCurrentOriginNode) {
                numberOfNorthSouthEdges++;
            } else {
                crossings += numberOfLongEdgeDummies;
            }
        } else if (isLongEdgeDummy(node)) {
            if (northOfCurrentOriginNode) {
                crossings += numberOfNorthSouthEdges;
            } else {
                numberOfLongEdgeDummies++;
            }
        } else if (isNormal(node)) {
            resetDummyCountAndSetCurrentOriginNodeTo(node);
            // since we always iterate from north to south in a layer:
            northOfCurrentOriginNode = false;
        }
        return crossings;
    }

    private boolean originIsNotCurrentOrigin(final LNode node) {
        return !originPortOf(node).getNode().equals(currentOriginNode);
    }

    private void resetDummyCountAndSetCurrentOriginNodeTo(final LNode node) {
        currentOriginNode = node;
        numberOfNorthSouthEdges = 0;
        numberOfLongEdgeDummies = 0;
    }

    private boolean isNormal(final LNode node) {
        return node.getType() == NodeType.NORMAL;
    }

    private boolean isLongEdgeDummy(final LNode node) {
        return node.getType() == NodeType.LONG_EDGE;
    }

    private boolean isNorthSouth(final LNode node) {
        return node.getType() == NodeType.NORTH_SOUTH_PORT;
    }

    private int getCrossingsOnSide(final LNode node, final PortSide side) {
        int crossings = 0;
        Iterable<LPort> ports = node.getPorts(side);
        for (LPort port : ports) {
            if (hasConnectedEdge(port)) {
                LNode northSouthDummy = getConnectedNorthSouthDummy(port);
                if (hasPortOnSide(northSouthDummy, PortSide.EAST)) {
                    crossings += numberOfEasternCrossings(node, port, northSouthDummy, side);
                }
                if (hasPortOnSide(northSouthDummy, PortSide.WEST)) {
                    crossings += numberOfWesternCrossings(node, port, northSouthDummy, side);
                }
            }
        }
        return crossings;
    }

    private boolean hasConnectedEdge(final LPort port) {
        return getConnectedNorthSouthDummy(port) != null;
    }

    private int numberOfWesternCrossings(final LNode node, final LPort port,
            final LNode northSouthDummy, final PortSide side) {
        int factor = edgesRoutedOrthogonally ? 1 : northSouthDummy.getPorts().get(0).getDegree();
        return factor * Math.min(positionOf(port), nearnessBetween(node, northSouthDummy));
    }

    private int numberOfEasternCrossings(final LNode node, final LPort port,
            final LNode northSouthDummy, final PortSide side) {
        int factor = edgesRoutedOrthogonally ? 1 : northSouthDummy.getPorts().get(0).getDegree();
        return factor
                * Math.min(cardinalityOnSide(node, side) - 1 - positionOf(port),
                nearnessBetween(node, northSouthDummy));
    }

    private boolean hasPortOnSide(final LNode node, final PortSide side) {
        return node.getPorts(side).iterator().hasNext();
    }

    private LNode getConnectedNorthSouthDummy(final LPort port) {
        return port.getProperty(InternalProperties.PORT_DUMMY);
    }

    private int nearnessBetween(final LNode node, final LNode northSouthDummy) {
        PortSide dummySide = getSideOf(northSouthDummy);
        int cardinality = cardinalityOnSide(node, dummySide);
        return cardinality - Math.abs(positionOf(node) - positionOf(northSouthDummy));
    }

    private Integer cardinalityOnSide(final LNode node, final PortSide side) {
        switch (side) {
        case NORTH:
            return northCardinalities[node.id];
        case SOUTH:
            return southCardinalities[node.id];
        default:
        }
        assert false : "Cardinality for port side " + side + " has not been collected!";
        return 0;
    }

    private int positionOf(final LNode node) {
        return nodePositions[node.id];
    }

    private int positionOf(final LPort port) {
        return portPositions[port.id];
    }

    private PortSide getSideOf(final LNode northSouthDummy) {
        return originPortOf(northSouthDummy).getSide();
    }

    private LPort originPortOf(final LNode node) {
        LPort port = node.getPorts().get(0);
        LPort origin = (LPort) port.getProperty(InternalProperties.ORIGIN);
        // port.getProperty(InternalProperties.)
        return origin;
    }

    /**
     * Whenever the order of two nodes are switched this counter needs to be notified.
     * 
     * @param nodeOne
     *            first node.
     * @param nodeTwo
     *            second node.
     */
    public void notifyNodeSwitch(final LNode nodeOne, final LNode nodeTwo) {
        int formerPositionOfOne = nodePositions[nodeOne.id];
        nodePositions[nodeOne.id] = nodePositions[nodeTwo.id];
        nodePositions[nodeTwo.id] = formerPositionOfOne;
    }

}

