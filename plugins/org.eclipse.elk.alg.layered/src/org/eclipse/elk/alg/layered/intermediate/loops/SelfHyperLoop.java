/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.loops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.intermediate.loops.ordering.PortRestorer;
import org.eclipse.elk.alg.layered.intermediate.loops.ordering.RoutingDirector;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * A self loop hyperedge consisting of at least one self loop edge.
 * 
 * <p>Since a self hyper loop may connect more than two ports, it is a relevant question how exactly the involved edges
 * are routed to connect them. The edges will later appear as one <em>trunk</em> which runs around the node, with little
 * <em>branches</em> establishing the actual connections to the ports involved. Thus, it is only required to remember
 * the outermost ports that limit the trunk. We remember the left and right boundary of the trunk: the trunk will leave
 * the leftmost port rightwards and approach the rightmost port from the left. The leftmost and rightmost ports are
 * computed after the port order has been established.</p>
 */
public class SelfHyperLoop {
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties

    // Structural properties
    
    /** The {@link SelfLoopHolder} which owns this instance. */
    private final SelfLoopHolder slHolder;
    /** List of ports that belong to this hyper loop. */
    private final List<SelfLoopPort> slPorts = new ArrayList<>();
    /** Set of edges that belong to this instance. */
    private final Set<SelfLoopEdge> slEdges = new HashSet<>();
    /** This hyper loop's labels. */
    private SelfHyperLoopLabels slLabels = null;
    
    // Routing properties (will be filled with values as self loop placement progresses)
    
    /** This self loop's loop type. Determined once port sides have been assigned. */
    private SelfLoopType selfLoopType;
    /** List of ports per port side. */
    private Multimap<PortSide, SelfLoopPort> slPortsBySide;
    /** The hyper loop trunk's leftmost port. Computed after initialization. */
    private SelfLoopPort leftmostPort = null;
    /** The hyper loop trunk's rightmost port. Computed after initialization. */
    private SelfLoopPort rightmostPort = null;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Creation

    /**
     * Creates a new instance owned by the given {@link SelfLoopHolder}.
     */
    SelfHyperLoop(final SelfLoopHolder slHolder) {
        this.slHolder = slHolder;
    }
    
    /**
     * Fills our {@link #slPortsBySide} multimap and determines this self loop's type. This method is called by
     * {@link PortRestorer} right before it gets to work since it has to know which ports are on which side.
     */
    public void computePortsPerSide() {
        assert slPortsBySide == null;
        
        // Remember ports for each side
        slPortsBySide = HashMultimap.create(PortSide.values().length, slPorts.size());
        for (SelfLoopPort slPort : slPorts) {
            PortSide portSide = slPort.getLPort().getSide();
            assert portSide != PortSide.UNDEFINED;
            
            slPortsBySide.put(portSide, slPort);
        }
        
        // Determine this self loop's loop type
        selfLoopType = SelfLoopType.fromPortSides(slPortsBySide.keySet());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors

    /**
     * Returns the {@link SelfLoopHolder} which owns this instance.
     */
    public SelfLoopHolder getSLHolder() {
        return slHolder;
    }

    /**
     * Adds the given edge to this instance and sets everything up accordingly, unless the edge was already part of this
     * instance.
     */
    public void addSelfLoopEdge(final SelfLoopEdge slEdge) {
        if (slEdges.add(slEdge)) {
            slEdge.setSLHyperLoop(this);
            
            SelfLoopPort slSource = slEdge.getSLSource();
            if (!slPorts.contains(slSource)) {
                slPorts.add(slSource);
            }
            
            SelfLoopPort slTarget = slEdge.getSLTarget();
            if (!slPorts.contains(slTarget)) {
                slPorts.add(slTarget);
            }
            
            // Check if we need to take care of any edge labels
            List<LLabel> lLabels = slEdge.getLEdge().getLabels();
            if (!lLabels.isEmpty()) {
                if (slLabels == null) {
                    slLabels = new SelfHyperLoopLabels(this);
                }
                
                slLabels.getLLabels().addAll(lLabels);
            }
        }
    }

    /**
     * Returns the list of edges that belong to this instance.
     */
    public Set<SelfLoopEdge> getSLEdges() {
        return slEdges;
    }
    
    /**
     * Returns the list of ports connected by this hyper loop. Once the {@link RoutingDirector} has run, this list will
     * be sorted according to the {@link LNode}'s list of {@link LPort}s.
     */
    public List<SelfLoopPort> getSLPorts() {
        return slPorts;
    }
    
    /**
     * Returns the loop's self loop type.
     */
    public SelfLoopType getSelfLoopType() {
        return selfLoopType;
    }
    
    /**
     * Returns a map of port sides mapped to ports on that side.
     */
    public Multimap<PortSide, SelfLoopPort> getSLPortsBySide() {
        return slPortsBySide;
    }
    
    /**
     * Returns the loop's ports on the given side.
     */
    public Collection<SelfLoopPort> getSLPortsBySide(final PortSide portSide) {
        return slPortsBySide.get(portSide);
    }
    
    /**
     * Returns whether the loop has ports on the given side.
     */
    public boolean hasSLPortsOnSide(final PortSide portSide) {
        return slPortsBySide.containsKey(portSide);
    }
    
    /**
     * Returns the hyper loop's leftmost port. See the class description for more details.
     */
    public SelfLoopPort getLeftmostPort() {
        return leftmostPort;
    }
    
    /**
     * Sets the hyper loop's leftmost port. See the class description for more details.
     */
    public void setLeftmostPort(final SelfLoopPort leftmostPort) {
        this.leftmostPort = leftmostPort;
    }
    
    /**
     * Returns the hyper loop's rightmost port. See the class description for more details.
     */
    public SelfLoopPort getRightmostPort() {
        return rightmostPort;
    }
    
    /**
     * Sets the hyper loop's rightmost port. See the class description for more details.
     */
    public void setRightmostPort(final SelfLoopPort rightmostPort) {
        this.rightmostPort = rightmostPort;
    }
    
}
