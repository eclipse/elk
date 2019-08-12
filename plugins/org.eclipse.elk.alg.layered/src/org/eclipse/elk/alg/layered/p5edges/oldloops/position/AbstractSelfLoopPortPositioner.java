/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopRoutingDirection;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.math.DoubleMath;

/**
 * Abstract base class for self loop port positioners. 
 */
public abstract class AbstractSelfLoopPortPositioner implements ISelfLoopPortPositioner {
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Port Comparators
    
    /**
     * Compares ports according to their original index.
     */
    protected static final Comparator<OldSelfLoopPort> ORIGINAL_INDEX_PORT_COMPARATOR = new Comparator<OldSelfLoopPort>() {
        
        /** Double comparison precision. */
        private static final double EPSILON = 1E-10;

        @Override
        public int compare(final OldSelfLoopPort port1, final OldSelfLoopPort port2) {
            if (port1.getPortSide() == port2.getPortSide()) {
                PortConstraints portConstraint = port1.getLPort().getNode().getProperty(
                        LayeredOptions.PORT_CONSTRAINTS);
                
                if (portConstraint == PortConstraints.FIXED_POS) {
                    KVector position1 = port1.getLPort().getPosition();
                    KVector position2 = port2.getLPort().getPosition();
                    
                    switch (port1.getPortSide()) {
                    case NORTH:
                        return DoubleMath.fuzzyCompare(position1.x, position2.x, EPSILON);
                    case EAST:
                        return -1 * DoubleMath.fuzzyCompare(position1.y, position2.y, EPSILON);
                    case SOUTH:
                        return -1 * DoubleMath.fuzzyCompare(position1.x, position2.x, EPSILON);
                    case WEST:
                        return DoubleMath.fuzzyCompare(position1.y, position2.y, EPSILON);
                    default:
                        return 0;
                    }
                } else {
                    return Integer.compare(port1.getOriginalIndex(), port2.getOriginalIndex());
                }
            } else {
                return Integer.compare(port1.getPortSide().ordinal(), port2.getPortSide().ordinal());
            }
        }
    };
    
    /**
     * Compares ports by their number of incoming edges.
     */
    protected static final Comparator<OldSelfLoopPort> INCOMING_EDGE_PORT_COMPARATOR = new Comparator<OldSelfLoopPort>() {
        @Override
        public int compare(final OldSelfLoopPort port1, final OldSelfLoopPort port2) {
            int incomingEdgesPort1 = port1.getLPort().getIncomingEdges().size();
            int incomingEdgesPort2 = port2.getLPort().getIncomingEdges().size();

            return Integer.compare(incomingEdgesPort1, incomingEdgesPort2);
        }
    };

    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    
    /**
     * Determines and sets the given port's direction, given its index in the list of ports it appears in.
     */
    public void setDirection(final OldSelfLoopPort port, final int index, final int portListSize) {
        OldSelfLoopRoutingDirection direction = OldSelfLoopRoutingDirection.BOTH;
        
        if (index == 0) {
            direction = OldSelfLoopRoutingDirection.RIGHT;
            
        } else if (index == portListSize - 1) {
            direction = OldSelfLoopRoutingDirection.LEFT;
        }
        
        // Apply direction
        port.setDirection(direction);
    }
    
    /**
     * TODO Document.
     */
    public void rotatePorts(final OldSelfLoopComponent component) {
        // Rotate to get the correct order
        List<OldSelfLoopPort> ports = component.getPorts();
        PortSide firstPortSide = ports.get(0).getPortSide();
        PortSide lastPortSide = ports.get(ports.size() - 1).getPortSide();
        
        while (firstPortSide != lastPortSide.left()) {
            List<OldSelfLoopPort> rotatedPorts = new ArrayList<OldSelfLoopPort>(ports.subList(1, ports.size()));
            rotatedPorts.add(ports.get(0));
            component.getPorts().clear();
            component.getPorts().addAll(rotatedPorts);

            firstPortSide = ports.get(0).getPortSide();
            lastPortSide = ports.get(ports.size() - 1).getPortSide();
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utilities for Subclasses

    /**
     * TODO Document.
     */
    protected void stackComponents(final OldSelfLoopNode slNode, final Collection<OldSelfLoopComponent> components,
            final PortSide side) {
        
        // Position components
        for (OldSelfLoopComponent component : components) {
            List<OldSelfLoopPort> ports = component.getPorts();
            ports.sort(INCOMING_EDGE_PORT_COMPARATOR);

            for (int i = 0; i < ports.size(); i++) {
                OldSelfLoopPort port = ports.get(i);
                setDirection(port, i, ports.size());
                port.setPortSide(side);

                if (port.getLPort().getIncomingEdges().isEmpty()) {
                    slNode.prependPort(port, side);
                } else {
                    slNode.appendPort(port, side);
                }
            }
        }
    }

    /**
     * TODO Document.
     */
    protected void stackCornerComponents(final OldSelfLoopNode slNode,
            final Collection<OldSelfLoopComponent> cornerComponents, final PortSide leftSide, final PortSide rightSide) {
        
        // Position components
        for (OldSelfLoopComponent component : cornerComponents) {
            List<OldSelfLoopPort> ports = component.getPorts();
            ports.sort(INCOMING_EDGE_PORT_COMPARATOR);

            for (int i = 0; i < ports.size() / 2; i++) {
                OldSelfLoopPort port = ports.get(i);
                slNode.appendPort(port, leftSide);
                port.setPortSide(leftSide);
                setDirection(port, ports.indexOf(port), ports.size());
            }
            
            for (int i = ports.size() / 2; i < ports.size(); i++) {
                OldSelfLoopPort port = ports.get(i);
                slNode.prependPort(port, rightSide);
                port.setPortSide(rightSide);
                setDirection(port, ports.indexOf(port), ports.size());
            }
        }
    }

    /**
     * TODO Document.
     */
    protected void sequenceComponents(final OldSelfLoopNode slNode, final Collection<OldSelfLoopComponent> components,
            final PortSide side) {
        
        for (OldSelfLoopComponent component : components) {
            List<OldSelfLoopPort> ports = component.getPorts();
            ports.sort(INCOMING_EDGE_PORT_COMPARATOR);

            slNode.appendPorts(ports, PortSide.NORTH);

            for (OldSelfLoopPort port : ports) {
                port.setPortSide(PortSide.NORTH);
                setDirection(port, ports.indexOf(port), ports.size());
            }
        }
    }

    /**
     * Places ports the are not involved with self loops.
     */
    protected void placeNonLoopPorts(final OldSelfLoopNode slNode, final PortSide side,
            final List<OldSelfLoopComponent> components) {
        
        if (side == PortSide.WEST) {
            Collections.reverse(components);
        }
        
        int median = (int) Math.ceil(components.size() / 2.0);
        List<OldSelfLoopComponent> firstHalf = components.subList(0, median);
        List<OldSelfLoopComponent> secondHalf = components.subList(median, components.size());
        
        for (OldSelfLoopComponent component : secondHalf) {
            slNode.prependPorts(component.getPorts(), side);
        }
        
        for (OldSelfLoopComponent component : firstHalf) {
            slNode.appendPorts(component.getPorts(), side);
        }

    }

}
