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
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopRoutingDirection;
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
    protected static final Comparator<SelfLoopPort> ORIGINAL_INDEX_PORT_COMPARATOR = new Comparator<SelfLoopPort>() {
        
        /** Double comparison precision. */
        private static final double EPSILON = 1E-10;

        @Override
        public int compare(final SelfLoopPort port1, final SelfLoopPort port2) {
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
    protected static final Comparator<SelfLoopPort> INCOMING_EDGE_PORT_COMPARATOR = new Comparator<SelfLoopPort>() {
        @Override
        public int compare(final SelfLoopPort port1, final SelfLoopPort port2) {
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
    public void setDirection(final SelfLoopPort port, final int index, final int portListSize) {
        SelfLoopRoutingDirection direction = SelfLoopRoutingDirection.BOTH;
        
        if (index == 0) {
            direction = SelfLoopRoutingDirection.RIGHT;
            
        } else if (index == portListSize - 1) {
            direction = SelfLoopRoutingDirection.LEFT;
        }
        
        // Apply direction
        port.setDirection(direction);
    }
    
    /**
     * TODO Document.
     */
    public void rotatePorts(final SelfLoopComponent component) {
        // Rotate to get the correct order
        List<SelfLoopPort> ports = component.getPorts();
        PortSide firstPortSide = ports.get(0).getPortSide();
        PortSide lastPortSide = ports.get(ports.size() - 1).getPortSide();
        
        while (firstPortSide != lastPortSide.left()) {
            List<SelfLoopPort> rotatedPorts = new ArrayList<SelfLoopPort>(ports.subList(1, ports.size()));
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
    protected void stackComponents(final SelfLoopNode slNode, final Collection<SelfLoopComponent> components,
            final PortSide side) {
        
        // Position components
        for (SelfLoopComponent component : components) {
            List<SelfLoopPort> ports = component.getPorts();
            ports.sort(INCOMING_EDGE_PORT_COMPARATOR);

            for (int i = 0; i < ports.size(); i++) {
                SelfLoopPort port = ports.get(i);
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
    protected void stackCornerComponents(final SelfLoopNode slNode,
            final Collection<SelfLoopComponent> cornerComponents, final PortSide leftSide, final PortSide rightSide) {
        
        // Position components
        for (SelfLoopComponent component : cornerComponents) {
            List<SelfLoopPort> ports = component.getPorts();
            ports.sort(INCOMING_EDGE_PORT_COMPARATOR);

            for (int i = 0; i < ports.size() / 2; i++) {
                SelfLoopPort port = ports.get(i);
                slNode.appendPort(port, leftSide);
                port.setPortSide(leftSide);
                setDirection(port, ports.indexOf(port), ports.size());
            }
            
            for (int i = ports.size() / 2; i < ports.size(); i++) {
                SelfLoopPort port = ports.get(i);
                slNode.prependPort(port, rightSide);
                port.setPortSide(rightSide);
                setDirection(port, ports.indexOf(port), ports.size());
            }
        }
    }

    /**
     * TODO Document.
     */
    protected void sequenceComponents(final SelfLoopNode slNode, final Collection<SelfLoopComponent> components,
            final PortSide side) {
        
        for (SelfLoopComponent component : components) {
            List<SelfLoopPort> ports = component.getPorts();
            ports.sort(INCOMING_EDGE_PORT_COMPARATOR);

            slNode.appendPorts(ports, PortSide.NORTH);

            for (SelfLoopPort port : ports) {
                port.setPortSide(PortSide.NORTH);
                setDirection(port, ports.indexOf(port), ports.size());
            }
        }
    }

    /**
     * Places ports the are not involved with self loops.
     */
    protected void placeNonLoopPorts(final SelfLoopNode slNode, final PortSide side,
            final List<SelfLoopComponent> components) {
        
        if (side == PortSide.WEST) {
            Collections.reverse(components);
        }
        
        int median = (int) Math.ceil(components.size() / 2.0);
        List<SelfLoopComponent> firstHalf = components.subList(0, median);
        List<SelfLoopComponent> secondHalf = components.subList(median, components.size());
        
        for (SelfLoopComponent component : secondHalf) {
            slNode.prependPorts(component.getPorts(), side);
        }
        
        for (SelfLoopComponent component : firstHalf) {
            slNode.appendPorts(component.getPorts(), side);
        }

    }

}
