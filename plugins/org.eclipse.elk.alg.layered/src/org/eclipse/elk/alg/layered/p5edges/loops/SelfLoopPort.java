/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.core.options.PortSide;

/**
 * A port involved with self loops. Each self loop port represents an {@link LPort}.
 */
public class SelfLoopPort {

    /** The {@link LPort} represented by this self loop port. */
    private final LPort port;
    /** The side of the node the port is placed on. */
    private PortSide portSide;
    /** The direction edges will leave this port in. */
    private SelfLoopRoutingDirection direction;
    /** The level of the edge segment. */
    private int maximumLevel = 0;
    /** Map about the different level for the different edges connected to this port. */
    private final Map<LEdge, Integer> edgeToLevelMap = new HashMap<>();
    /** The SelfLoopComponent the port belongs to. */
    private SelfLoopComponent component;
    /** Self loops connected to this port. */
    private final List<SelfLoopEdge> connectedEdges = new ArrayList<>();
    /** The port is connected to an edge connecting it to another node. */
    private boolean isNonLoopPort = false;
    /** The original index of the port before using the linear node representation or altering the port order. */
    private int originalIndex;
    /** TODO Document. */
    private double otherEdgeOffset;

    /**
     * Create a new self-Loop port. For each self loop port the corresponding {@link LPort} and the the component it
     * belongs to are required.
     */
    public SelfLoopPort(final LPort port, final SelfLoopComponent selfLoopComponent) {
        this.port = port;
        this.setOriginalIndex(port.getIndex());
        this.portSide = port.getSide();
        this.component = selfLoopComponent;
        this.isNonLoopPort = computeNonLoopPortProperty(port);
    }

    /**
     * Checks whether this self loop port is in fact involved with self loops.
     * 
     * @returns {@code true} if there are incident edges and all of them are regular edges.
     */
    private boolean computeNonLoopPortProperty(final LPort lPort) {
        // If there are no incident edges, there are no regular incident edges either
        if (lPort.getIncomingEdges().isEmpty() && lPort.getOutgoingEdges().isEmpty()) {
            return false;
        }
        
        // Otherwise we check if there are only regular edges
        return StreamSupport.stream(lPort.getConnectedEdges().spliterator(), false)
                .allMatch(edge -> !edge.isSelfLoop());
    }

    /**
     * Return the {@link LPort} represented by this self loop port.
     */
    public LPort getLPort() {
        return port;
    }

    /**
     * Returns the port side this self loop port is on.
     */
    public PortSide getPortSide() {
        return portSide;
    }

    /**
     * Sets the port side this self loop port is on.
     */
    public void setPortSide(final PortSide side) {
        this.portSide = side;
    }

    /**
     * Returns the direction in which edges should leave this port.
     */
    public SelfLoopRoutingDirection getDirection() {
        return direction;
    }

    /**
     * Sets the direction in which edges should leave this port.
     */
    public void setDirection(final SelfLoopRoutingDirection direction) {
        this.direction = direction;
    }

    /**
     * Returns the maximum level occupied by an edge segment.
     */
    public int getMaximumLevel() {
        return maximumLevel;
    }

    /**
     * Sets the maximum level occupied by an edge segment.
     */
    public void setMaximumLevel(final int level) {
        this.maximumLevel = level;
    }

    /**
     * Returns the level the given edge is on.
     */
    public int getEdgeLevel(final LEdge edge) {
        Integer nonHyperEdgeLevel = edgeToLevelMap.get(edge);
        return nonHyperEdgeLevel == null ? maximumLevel : nonHyperEdgeLevel;
    }

    /**
     * Sets the level the given edge is on.
     */
    public void setEdgeLevel(final LEdge edge, final int level) {
        this.edgeToLevelMap.put(edge, level);
    }

    /**
     * Returns the component this port is part of.
     */
    public SelfLoopComponent getComponent() {
        return component;
    }

    /**
     * Sets the component this port is part of.
     */
    public void setComponent(final SelfLoopComponent component) {
        this.component = component;
    }
    
    /**
     * Returns the self loops connected to this port.
     */
    public List<SelfLoopEdge> getConnectedEdges() {
        return connectedEdges;
    }

    /**
     * Returns {@code true} if this port does not actually have any incident self loops.
     */
    public boolean isNonLoopPort() {
        return isNonLoopPort;
    }

    /**
     * Returns the original index of this port in its node's port list.
     */
    public int getOriginalIndex() {
        return originalIndex;
    }

    /**
     * Sets the original index of this port in its node's port list.
     */
    public void setOriginalIndex(final int originalIndex) {
        this.originalIndex = originalIndex;
    }

    /**
     * TODO Document.
     */
    public double getOtherEdgeOffset() {
        return this.otherEdgeOffset;

    }

    /**
     * TODO Document.
     */
    public void setOtherEdgeOffset(final double otherEdgeOffset) {
        this.otherEdgeOffset = otherEdgeOffset;

    }

    @Override
    public String toString() {
        return port.toString() + "(" + portSide + ", " + direction + ")";
    }
}
