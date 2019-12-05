/*******************************************************************************
 * Copyright (c) 2010, 2019 Kiel University and others.
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

import com.google.common.collect.Lists;

/**
 * Instances of this class represent the "trunk" of a hyper edge. In left-to-right layouts, this will be the vertical
 * segment of a hyperedge between each pair of adjacent ports. Such a segment has a list of port positions for each of
 * its two sides. The range of coordinates spanned by it determines its extent, which can be requested by calling
 * {@link #getStartPos()} and {@link #getEndPos()}.
 */
public class HyperEdgeSegment implements Comparable<HyperEdgeSegment> {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties

    /** Routing strategy which will ultimately decide how edges will be routed. */
    private final AbstractRoutingDirectionStrategy routingStrategy;
    /** ports represented by this hypernode. */
    private final List<LPort> ports = Lists.newArrayList();
    /** mark value used for cycle breaking. */
    private int mark;
    /** the rank determines the horizontal distance to the preceding layer. */
    private int routingSlot;
    /** vertical starting position of this hypernode. */
    private double start = Double.NaN;
    /** vertical ending position of this hypernode. */
    private double end = Double.NaN;
    /** positions of line segments going to the preceding layer. */
    private final LinkedList<Double> sourcePosis = Lists.newLinkedList();
    /** positions of line segments going to the next layer. */
    private final LinkedList<Double> targetPosis = Lists.newLinkedList();
    /** list of outgoing dependencies. */
    private final List<SegmentDependency> outgoingDependencies = Lists.newArrayList();
    /** sum of the weights of outgoing dependencies. */
    private int outWeight;
    /** list of incoming dependencies. */
    private final List<SegmentDependency> incomingDependencies = Lists.newArrayList();
    /** sum of the weights of incoming depencencies. */
    private int inWeight;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Initialization

    /**
     * Creates a new instance for the given routing strategy.
     * 
     * @param routingStrategy
     *            the routing strategy. Only required if {@link #addPortPositions(LPort, Map)} will be called to set
     *            this thing up.
     */
    public HyperEdgeSegment(final AbstractRoutingDirectionStrategy routingStrategy) {
        this.routingStrategy = routingStrategy;
    }

    /**
     * Adds the positions of the given port and all connected ports.
     *
     * @param port
     *            a port
     * @param hyperNodeMap
     *            map of ports to existing hypernodes
     */
    public void addPortPositions(final LPort port, final Map<LPort, HyperEdgeSegment> hyperNodeMap) {
        hyperNodeMap.put(port, this);
        ports.add(port);
        double pos = routingStrategy.getPortPositionOnHyperNode(port);

        // set new start position
        if (Double.isNaN(start)) {
            start = pos;
        } else {
            start = Math.min(start, pos);
        }

        // set new end position
        if (Double.isNaN(end)) {
            end = pos;
        } else {
            end = Math.max(end, pos);
        }

        // add the new port position to the respective list
        if (port.getSide() == routingStrategy.getSourcePortSide()) {
            insertSorted(sourcePosis, pos);
        } else {
            insertSorted(targetPosis, pos);
        }

        // add connected ports
        for (LPort otherPort : port.getConnectedPorts()) {
            if (!hyperNodeMap.containsKey(otherPort)) {
                addPortPositions(otherPort, hyperNodeMap);
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
     * Returns the segment's mark. Used by cycle breaking algorithms.
     */
    int getMark() {
        return mark;
    }

    /**
     * Sets the segment's mark to the given value. Used by cycle breaking algorithms.
     */
    void setMark(final int mark) {
        this.mark = mark;
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
     * Returns the coordinate where this segment begins. Whether this is an x or a y coordinate depends on the routing
     * strategy.
     */
    public double getStartPos() {
        return start;
    }

    /**
     * Returns the coordinate where this segment ends. Whether this is an x or a y coordinate depends on the routing
     * strategy.
     */
    public double getEndPos() {
        return end;
    }

    /**
     * @return the sourcePosis
     */
    public LinkedList<Double> getSourcePosis() {
        return sourcePosis;
    }

    /**
     * @return the targetPosis
     */
    public LinkedList<Double> getTargetPosis() {
        return targetPosis;
    }

    /**
     * Return the outgoing dependencies.
     */
    public List<SegmentDependency> getOutgoingDependencies() {
        return outgoingDependencies;
    }

    /**
     * Returns the weight of outgoing dependencies.
     */
    public int getOutWeight() {
        return outWeight;
    }

    /**
     * Sets the weight of outgoing dependencies.
     */
    public void setOutWeight(final int outWeight) {
        this.outWeight = outWeight;
    }

    /**
     * Return the incoming dependencies.
     */
    public List<SegmentDependency> getIncomingDependencies() {
        return incomingDependencies;
    }

    /**
     * Returns the weight of incoming dependencies.
     */
    public int getInWeight() {
        return inWeight;
    }

    /**
     * Sets the weight of incoming dependencies.
     */
    public void setInWeight(final int inWeight) {
        this.inWeight = inWeight;
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
