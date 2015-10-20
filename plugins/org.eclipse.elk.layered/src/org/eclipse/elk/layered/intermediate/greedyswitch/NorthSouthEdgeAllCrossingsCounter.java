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
package org.eclipse.elk.layered.intermediate.greedyswitch;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.layered.graph.LNode;
import org.eclipse.elk.layered.graph.LPort;
import org.eclipse.elk.layered.graph.LNode.NodeType;
import org.eclipse.elk.layered.properties.InternalProperties;

import com.google.common.collect.Maps;

/**
 * Counts all crossings caused by the ordering of north/south ports and between north/south edges
 * and long-edge dummies.
 * 
 * @author alan
 */
public class NorthSouthEdgeAllCrossingsCounter {

    private final Map<LNode, Integer> nodePositions;
    private final Map<LPort, Integer> portPositions;
    private final Map<LNode, Integer> northCardinalities;
    private final Map<LNode, Integer> southCardinalities;
    private final LNode[] layer;
    private LNode currentOriginNode;
    private int numberOfNorthSouthEdges;
    private int numberOfLongEdgeDummies;
    private boolean northOfCurrentOriginNode = true;
    private final boolean usesOrthogonalCounter;

    /**
     * Creates counter.
     * 
     * @param layer
     *            The current order of the nodes.
     */
    public NorthSouthEdgeAllCrossingsCounter(final LNode[] layer) {
        this.layer = layer;
        nodePositions = Maps.newHashMap();
        portPositions = Maps.newHashMap();
        northCardinalities = Maps.newHashMap();
        southCardinalities = Maps.newHashMap();
        usesOrthogonalCounter =
                layer[0].getGraph().getProperty(LayoutOptions.EDGE_ROUTING) == EdgeRouting.ORTHOGONAL;
        initPositionsAndCardinalities();
    }

    private void initPositionsAndCardinalities() {
        int nodeId = 0;
        for (LNode element : layer) {
            LNode node = element;

            if (!isLongEdgeDummy(node)) {
                nodePositions.put(node, nodeId++);
            }

            setPortPositionsAndCardinalitiesFor(node, northCardinalities, PortSide.NORTH);
            setPortPositionsAndCardinalitiesFor(node, southCardinalities, PortSide.SOUTH);
        }
    }

    private void setPortPositionsAndCardinalitiesFor(final LNode node,
            final Map<LNode, Integer> cardinalities, final PortSide side) {
        Iterable<LPort> ports = PortIterable.inNorthSouthEastWestOrder(node, side);
        int portId = 0;
        for (LPort port : ports) {
            portPositions.put(port, portId++);
        }
        cardinalities.put(node, portId);
    }

    /**
     * <p>Counts all crossings caused by the ordering of north/south ports and between north/south
     * edges and long-edge dummies. Assume the following layout:</p>
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
     * <p>This can be viewed as a matrix:</p>
     * <pre>
     *   0 1 2 
     * 0  |  *
     * 1  |*__
     * 2 x
     * </pre>
     * 
     * <p>Thereby node the eastern edge of node x causes crossings with north/south edges of all nodes
     * which are to the right and above it. For western edges this is the same to the left and
     * below.</p>
     * 
     * @return number of crossings
     */
    public int countCrossings() {
        int crossings = 0;
        for (LNode node : layer) {
            crossings += getLongEdgeDummyCrossings(node);
            if (fixedPortOrderOn(node)) {
                if (hasPortOnSide(node, PortSide.NORTH)) {
                    crossings += getCrossingsOnSide(node, PortSide.NORTH);
                }
                if (hasPortOnSide(node, PortSide.SOUTH)) {
                    crossings += getCrossingsOnSide(node, PortSide.SOUTH);
                }
            }
        }
        return crossings;
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
        Iterable<LPort> southPorts = node.getPorts(side);
        for (LPort port : southPorts) {
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
        int factor = usesOrthogonalCounter ? 1 : northSouthDummy.getPorts().get(0).getDegree();
        return factor * Math.min(positionOf(port), nearnessBetween(node, northSouthDummy));
    }

    private int numberOfEasternCrossings(final LNode node, final LPort port,
            final LNode northSouthDummy, final PortSide side) {
        int factor = usesOrthogonalCounter ? 1 : northSouthDummy.getPorts().get(0).getDegree();
        return factor
                * Math.min(cardinalityOnSide(node, side) - 1 - positionOf(port),
                nearnessBetween(node, northSouthDummy));
    }

    private boolean fixedPortOrderOn(final LNode node) {
        return node.getProperty(LayoutOptions.PORT_CONSTRAINTS).isOrderFixed();
    }

    private boolean hasPortOnSide(final LNode node, final PortSide side) {
        return getPortIteratorForSide(node, side).hasNext();
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
            return northCardinalities.get(node);
        case SOUTH:
            return southCardinalities.get(node);
        default:
        }
        assert false : "Cardinality for port side " + side + " has not been collected!";
        return 0;
    }

    private int positionOf(final LNode node) {
        return nodePositions.get(node);
    }

    private int positionOf(final LPort port) {
        return portPositions.get(port);
    }

    private PortSide getSideOf(final LNode northSouthDummy) {
        return originPortOf(northSouthDummy).getSide();
    }

    private LPort originPortOf(final LNode node) {
        LPort port = node.getPorts().get(0);
        LPort origin = (LPort) port.getProperty(InternalProperties.ORIGIN);
        return origin;
    }

    private Iterator<LPort> getPortIteratorForSide(final LNode node, final PortSide side) {
        return node.getPorts(side).iterator();
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
        if (nodePositions.containsKey(nodeOne) && nodePositions.containsKey(nodeTwo)) {
            int formerPositionOfOne = nodePositions.get(nodeOne);
            nodePositions.put(nodeOne, nodePositions.get(nodeTwo));
            nodePositions.put(nodeTwo, formerPositionOfOne);
        }
    }

}
