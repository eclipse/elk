/*******************************************************************************
 * Copyright (c) 2010, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.orthogonal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.p5edges.orthogonal.direction.BaseRoutingDirectionStrategy;

import com.google.common.collect.Lists;

/**
 * Instances of this class represent the "trunk" of a hyper edge. In left-to-right layouts, this will be the vertical
 * segment of a hyperedge. Such a segment has a list of coordinates for each of its two sides that specifies where
 * incoming or outgoing connections enter and leave the segment, respectively (for horizontal layouts, this will be
 * horizontal edge segments that usually connect to ports). The range of coordinates spanned by a hyper edge segment
 * determines its extent, which can be requested by calling {@link #getStartCoordinate()} and
 * {@link #getEndCoordinate()}.
 * 
 * <p>
 * Whether coordinates refer to the x or y axis depends on the edge routing direction: for horizontal layouts, the edge
 * segment is vertical, which means that the coordinates are y coordinates.
 * </p>
 * 
 * <p>
 * Instances of this class are comparable based on the value of {@link #mark}. {@link #hashCode()} and
 * {@link #equals(Object)} are implemented based on that value as well. That means, of course, that all of those methods
 * only start making sense once {@link #mark} has a meaningful value.
 * </p>
 */
public class HyperEdgeSegment implements Comparable<HyperEdgeSegment> {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties

    /** Routing strategy which will ultimately decide how edges will be routed. */
    private final BaseRoutingDirectionStrategy routingStrategy;
    /** ports represented by this hypernode. */
    private final List<LPort> ports = Lists.newArrayList();
    
    /** mark value used for cycle breaking (to be accessed directly). */
    // SUPPRESS CHECKSTYLE NEXT Visibility 
    int mark;
    
    /** the routing slot determines the horizontal distance to the preceding layer. */
    private int routingSlot;
    
    /** start position of this edge segment (in horizontal layouts, this is the topmost y coordinate). */
    private double startPosition = Double.NaN;
    /** end position of this edge segment (in horizontal layouts, this is the bottommost y coordinate). */
    private double endPosition = Double.NaN;
    
    /** sorted list of coordinates where incoming connections enter this segment. */
    private final LinkedList<Double> incomingConnectionCoordinates = Lists.newLinkedList();
    /** sorted list of coordinates where outgoing connections leave this segment. */
    private final LinkedList<Double> outgoingConnectionCoordinates = Lists.newLinkedList();
    
    /** list of outgoing dependencies to other edge segments. */
    private final List<HyperEdgeSegmentDependency> outgoingSegmentDependencies = Lists.newArrayList();
    /** combined weight of outgoing dependencies. */
    private int outDepWeight;
    /** list of incoming dependencies from other edge segments. */
    private final List<HyperEdgeSegmentDependency> incomingSegmentDependencies = Lists.newArrayList();
    /** combined weight of incoming dependencies. */
    private int inDepWeight;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Initialization

    /**
     * Creates a new instance for the given routing strategy.
     * 
     * @param routingStrategy
     *            the routing strategy. Only required if {@link #addPortPositions(LPort, Map)} will be called to set
     *            this thing up.
     */
    public HyperEdgeSegment(final BaseRoutingDirectionStrategy routingStrategy) {
        this.routingStrategy = routingStrategy;
    }

    /**
     * Adds the positions of the given port and all connected ports.
     *
     * @param port
     *            a port.
     * @param hyperEdgeSegmentMap
     *            map of ports to existing hyperedge segments.
     */
    public void addPortPositions(final LPort port, final Map<LPort, HyperEdgeSegment> hyperEdgeSegmentMap) {
        hyperEdgeSegmentMap.put(port, this);
        ports.add(port);
        double portPos = routingStrategy.getPortPositionOnHyperNode(port);

        // set new start position
        if (Double.isNaN(startPosition)) {
            startPosition = portPos;
        } else {
            startPosition = Math.min(startPosition, portPos);
        }

        // set new end position
        if (Double.isNaN(endPosition)) {
            endPosition = portPos;
        } else {
            endPosition = Math.max(endPosition, portPos);
        }

        // add the new port position to the respective list
        if (port.getSide() == routingStrategy.getSourcePortSide()) {
            insertSorted(incomingConnectionCoordinates, portPos);
        } else {
            insertSorted(outgoingConnectionCoordinates, portPos);
        }

        // add connected ports
        for (LPort otherPort : port.getConnectedPorts()) {
            if (!hyperEdgeSegmentMap.containsKey(otherPort)) {
                addPortPositions(otherPort, hyperEdgeSegmentMap);
            }
        }
    }

    private static void insertSorted(final List<Double> list, final double value) {
        ListIterator<Double> listIter = list.listIterator();
        while (listIter.hasNext()) {
            double next = listIter.next().floatValue();
            if (next == value) {
                // an exactly equal value is already present in the list
                return;
            } else if (next > value) {
                listIter.previous();
                break;
            }
        }
        listIter.add(Double.valueOf(value));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Getters and Setters

    /**
     * Returns the ports incident to this segment.
     */
    public List<LPort> getPorts() {
        return ports;
    }

    /**
     * Returns this segment's routing slot.
     */
    public int getRoutingSlot() {
        return routingSlot;
    }

    /**
     * Sets this segment's routing slot.
     */
    public void setRoutingSlot(final int slot) {
        this.routingSlot = slot;
    }

    /**
     * Returns the coordinate where this segment begins.
     */
    public double getStartCoordinate() {
        return startPosition;
    }

    /**
     * Returns the coordinate where this segment ends.
     */
    public double getEndCoordinate() {
        return endPosition;
    }

    /**
     * Returns the (sorted) list of coordinates where incoming connections enter this segment.
     */
    public LinkedList<Double> getIncomingConnectionCoordinates() {
        return incomingConnectionCoordinates;
    }

    /**
     * Returns the (sorted) list of coordinates where outgoing connections leave this segment.
     */
    public LinkedList<Double> getOutgoingConnectionCoordinates() {
        return outgoingConnectionCoordinates;
    }

    /**
     * Return the outgoing dependencies to other hyper edge segments.
     */
    public List<HyperEdgeSegmentDependency> getOutgoingSegmentDependencies() {
        return outgoingSegmentDependencies;
    }

    /**
     * Returns the combined weight of outgoing dependencies.
     */
    public int getOutWeight() {
        return outDepWeight;
    }

    /**
     * Sets the combined weight of outgoing dependencies.
     */
    public void setOutWeight(final int outWeight) {
        this.outDepWeight = outWeight;
    }

    /**
     * Return the incoming dependencies from other hyper edge segments..
     */
    public List<HyperEdgeSegmentDependency> getIncomingSegmentDependencies() {
        return incomingSegmentDependencies;
    }

    /**
     * Returns the weight of incoming dependencies.
     */
    public int getInWeight() {
        return inDepWeight;
    }

    /**
     * Sets the weight of incoming dependencies.
     */
    public void setInWeight(final int inWeight) {
        this.inDepWeight = inWeight;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Comparable and Object Overrides

    @Override
    public int compareTo(final HyperEdgeSegment other) {
        return this.mark - other.mark;
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof HyperEdgeSegment) {
            HyperEdgeSegment other = (HyperEdgeSegment) object;
            return this.mark == other.mark;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mark;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        Iterator<LPort> portIter = ports.iterator();
        while (portIter.hasNext()) {
            LPort port = portIter.next();
            String name = port.getNode().getDesignation();
            if (name == null) {
                name = "n" + port.getNode().getIndex();
            }
            builder.append(name);
            if (portIter.hasNext()) {
                builder.append(',');
            }
        }
        builder.append('}');
        return builder.toString();
    }

}
