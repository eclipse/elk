/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.core.options.PortSide;

/**
 * Represents one node of the graph. Instead of one list of ports for one node it provides access to one
 * {@link SelfLoopNodeSide} object for each side of the node. It also keeps a list of self loop components.
 */
public class SelfLoopNode {

    /** The original node this class represents. */
    private final LNode node;
    /** A map from all four port sides to their respective node side objects. */
    private final EnumMap<PortSide, SelfLoopNodeSide> nodeSides = new EnumMap<>(PortSide.class);
    /** List of self loop components this node has. */
    private final List<SelfLoopComponent> selfLoopComponents = new ArrayList<>();

    
    /**
     * Create a new instance to represent the given node.
     */
    public SelfLoopNode(final LNode node) {
        this.node = node;
        
        nodeSides.put(PortSide.NORTH, new SelfLoopNodeSide(PortSide.NORTH));
        nodeSides.put(PortSide.EAST, new SelfLoopNodeSide(PortSide.EAST));
        nodeSides.put(PortSide.SOUTH, new SelfLoopNodeSide(PortSide.SOUTH));
        nodeSides.put(PortSide.WEST, new SelfLoopNodeSide(PortSide.WEST));
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties

    /**
     * Returns the {@link LNode} this self loop node represents.
     */
    public LNode getNode() {
        return node;
    }
    
    /**
     * Returns the {@link SelfLoopNodeSide} that represents this node's given port side. 
     */
    public SelfLoopNodeSide getNodeSide(final PortSide side) {
        return nodeSides.get(side);
    }

    /**
     * Returns all sides of the node representation.
     */
    public Collection<SelfLoopNodeSide> getSides() {
        return nodeSides.values();
    }


    /**
     * Count the total number of ports of all sides.
     * 
     * @return the number of ports of all sides
     */
    public int getNumberOfPorts() {
        return getSides().stream()
            .mapToInt(side -> side.getPorts().size())
            .sum();
    }
    
    /**
     * Returns the list of self loop components this node has.
     */
    public List<SelfLoopComponent> getSelfLoopComponents() {
        return selfLoopComponents;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Port Management

    /**
     * Add a port to the beginning of the given port side.
     * 
     * @param port
     *            the port to add.
     * @param side
     *            the side the port will be added to.
     */
    public void prependPort(final SelfLoopPort port, final PortSide side) {
        nodeSides.get(side).getPorts().add(0, port);
    }

    /**
     * Add all ports to the beginning of the given port side.
     * 
     * @param ports
     *            the ports to add.
     * @param side
     *            the side the ports will be added to.
     */
    public void prependPorts(final List<SelfLoopPort> ports, final PortSide side) {
        // TODO This will effectively reverse the order of the ports, is that okay?
        for (SelfLoopPort port : ports) {
            prependPort(port, side);
        }
    }

    /**
     * Add a port to the end of the given port side.
     * 
     * @param port
     *            the port to add.
     * @param side
     *            the side the port will be added to.
     */
    public void appendPort(final SelfLoopPort port, final PortSide side) {
        nodeSides.get(side).getPorts().add(port);
    }

    /**
     * Add all ports to the end of the given port side.
     * 
     * @param ports
     *            the ports to add.
     * @param side
     *            the side the ports will be added to.
     */
    public void appendPorts(final List<SelfLoopPort> ports, final PortSide side) {
        nodeSides.get(side).getPorts().addAll(ports);
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Miscellaneous

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        sideSpecificString(builder, PortSide.NORTH);
        sideSpecificString(builder, PortSide.EAST);
        sideSpecificString(builder, PortSide.SOUTH);
        sideSpecificString(builder, PortSide.WEST);
        
        return builder.toString();
    }
    
    private void sideSpecificString(final StringBuilder builder, final PortSide portSide) {
        builder.append(portSide.toString() +  ": [");
        
        SelfLoopNodeSide nodeSide = nodeSides.get(portSide);
        for (SelfLoopPort port : nodeSide.getPorts()) {
            builder.append(port);

            if (nodeSide.getPorts().indexOf(port) != nodeSide.getPorts().size()) {
                builder.append(", ");
            }
        }
        
        builder.append("] ");
    }
}
