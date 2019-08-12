/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.core.options.PortSide;

/**
 * Represents the ports along one {@link PortSide side} of a {@link OldSelfLoopNode}.
 */
public class OldSelfLoopNodeSide {

    /** The actual side information. */
    private final PortSide side;
    /** The ports which are placed on this node side. */
    private final List<OldSelfLoopPort> ports = new ArrayList<>();
    /** The maximum level a port on this side has. */
    private int maximumPortLevel;
    /** The maximum level a segment on this side has. */
    private int maximumSegmentLevel;
    /** The maximum offset which has to be considered when calculating the margin of the node. */
    private double maximumLabelOffset;
    /** The segments which belong to an opposing self loop (space has to be reserved for them). */
    private final Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment> opposingSegments = new HashMap<>();
    /** The roots of the component dependency graph. They hold the outermost components of the node side. */
    private final Set<OldSelfLoopComponent> componentDependencies = new HashSet<>();

    
    /**
     * Creates a new instance that represents the given port side.
     */
    public OldSelfLoopNodeSide(final PortSide side) {
        this.side = side;
    }
    

    /**
     * Returns the port side represented by this object.
     */
    public PortSide getSide() {
        return side;
    }

    /**
     * Returns the ports on this node side.
     */
    public List<OldSelfLoopPort> getPorts() {
        return ports;
    }

    /**
     * Returns the maximum port level.
     */
    public int getMaximumPortLevel() {
        return maximumPortLevel;
    }

    /**
     * Sets the maximum port level.
     */
    public void setMaximumPortLevel(final int maximumPortLevel) {
        this.maximumPortLevel = maximumPortLevel;
    }

    /**
     * Returns the maximum segment level.
     */
    public int getMaximumSegmentLevel() {
        return maximumSegmentLevel;
    }

    /**
     * Sets the maximum segment level.
     */
    public void setMaximumSegmentLevel(final int maximumSegmentLevel) {
        this.maximumSegmentLevel = maximumSegmentLevel;
    }

    /**
     * Returns the maximum offset which has to be considered when calculating the margin of the node.
     */
    public double getMaximumLabelOffset() {
        return maximumLabelOffset;
    }

    /**
     * Sets the maximum label offset.
     */
    public void setMaximumLabelOffset(final double maximumLabelOffset) {
        this.maximumLabelOffset = maximumLabelOffset;
    }

    /**
     * Returns the self loops that belong to an opposing segment.
     */
    public Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment> getOpposingSegments() {
        return opposingSegments;
    }

    /**
     * Returns component dependencies.
     */
    public Set<OldSelfLoopComponent> getComponentDependencies() {
        return componentDependencies;
    }

    @Override
    public String toString() {
        return side.toString();
    }

}
