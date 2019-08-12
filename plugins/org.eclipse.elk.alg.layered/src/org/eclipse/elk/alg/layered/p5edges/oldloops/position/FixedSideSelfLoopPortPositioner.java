/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.SelfLoopOrderingStrategy;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNodeSide;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopOpposingSegment;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopRoutingDirection;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopType;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Positions ports around nodes that have fixed side port constraints such that edge crossings are minimized. Each
 * instance is configured to place ports according to a {@link SelfLoopOrderingStrategy}.
 */
public class FixedSideSelfLoopPortPositioner extends AbstractSelfLoopPortPositioner {

    private static final int ONE_SIDE_COMP = 1;
    private static final int TWO_SIDE_COMP = 2;
    private static final int THREE_SIDE_COMP = 3;
    private static final int FOUR_SIDE_COMP = 4;

    /** The ordering strategy we're using. */
    private SelfLoopOrderingStrategy ordering;
    /** The node we're placing ports for. */
    private OldSelfLoopNode slNode;
    /** The number of non-loop ports for each side. */
    private Map<PortSide, Integer> nonLoopsPerSide = new HashMap<PortSide, Integer>();

    /**
     * Create a new instance using the given ordering strategy.
     */
    public FixedSideSelfLoopPortPositioner(final SelfLoopOrderingStrategy ordering) {
        this.ordering = ordering;
    }

    @Override
    public void position(final LNode node) {
        slNode = node.getProperty(InternalProperties.SELF_LOOP_NODE_REPRESENTATION);
        List<OldSelfLoopComponent> components = slNode.getSelfLoopComponents();

        // Sort by size
        components.sort((comp1, comp2) -> Integer.compare(comp1.getPorts().size(), comp2.getPorts().size()));
        
        // Find self loops of different types
        Multimap<OldSelfLoopType, OldSelfLoopComponent> loopTypes = ArrayListMultimap.create();
        determineLoopTypes(components, loopTypes);

        // Place them
        placeSideLoops(loopTypes.get(OldSelfLoopType.SIDE));
        placeNonLoops(loopTypes.get(OldSelfLoopType.NON_LOOP));
        placeOpposingLoops(loopTypes.get(OldSelfLoopType.OPPOSING));
        placeThreeSidesLoop(loopTypes.get(OldSelfLoopType.THREE_CORNER));
        placeFourSidesLoop(loopTypes.get(OldSelfLoopType.FOUR_CORNER));
        placeCornerLoops(loopTypes.get(OldSelfLoopType.CORNER));
    }

    /**
     * Goes through all components and adds them to their respective loop type.
     */
    public void determineLoopTypes(final List<OldSelfLoopComponent> components,
            final Multimap<OldSelfLoopType, OldSelfLoopComponent> loopTypes) {

        for (OldSelfLoopComponent component : components) {
            switch (component.getPortSides().size()) {
            case ONE_SIDE_COMP:
                if (component.getPorts().size() == 1) {
                    loopTypes.put(OldSelfLoopType.NON_LOOP, component);
                } else {
                    loopTypes.put(OldSelfLoopType.SIDE, component);
                }
                break;
                
            case TWO_SIDE_COMP:
                if (component.isCornerComponent()) {
                    loopTypes.put(OldSelfLoopType.CORNER, component);
                } else {
                    loopTypes.put(OldSelfLoopType.OPPOSING, component);
                }
                break;
                
            case THREE_SIDE_COMP:
                loopTypes.put(OldSelfLoopType.THREE_CORNER, component);
                break;
                
            case FOUR_SIDE_COMP:
                loopTypes.put(OldSelfLoopType.FOUR_CORNER, component);
                break;
            }
        }

    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Side Loops

    private void placeSideLoops(final Collection<OldSelfLoopComponent> sideComponents) {
        Multimap<PortSide, OldSelfLoopComponent> sideMap = ArrayListMultimap.create();

        // distribute to sides
        for (OldSelfLoopComponent component : sideComponents) {
            sideMap.put(component.getPorts().get(0).getPortSide(), component);
        }

        // Delegate to appropriate superclass methods
        if (ordering == SelfLoopOrderingStrategy.STACKED) {
            stackComponents(slNode, sideMap.get(PortSide.NORTH), PortSide.NORTH);
            stackComponents(slNode, sideMap.get(PortSide.SOUTH), PortSide.SOUTH);
            stackComponents(slNode, sideMap.get(PortSide.EAST), PortSide.EAST);
            stackComponents(slNode, sideMap.get(PortSide.WEST), PortSide.WEST);
            
        } else {
            sequenceComponents(slNode, sideMap.get(PortSide.NORTH), PortSide.NORTH);
            sequenceComponents(slNode, sideMap.get(PortSide.SOUTH), PortSide.SOUTH);
            sequenceComponents(slNode, sideMap.get(PortSide.EAST), PortSide.EAST);
            sequenceComponents(slNode, sideMap.get(PortSide.WEST), PortSide.WEST);
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Corner Loops
    
    private void placeCornerLoops(final Collection<OldSelfLoopComponent> cornerComponents) {
        List<OldSelfLoopComponent> leftTop = new ArrayList<OldSelfLoopComponent>();
        List<OldSelfLoopComponent> rightTop = new ArrayList<OldSelfLoopComponent>();
        List<OldSelfLoopComponent> leftBottom = new ArrayList<OldSelfLoopComponent>();
        List<OldSelfLoopComponent> rightBottom = new ArrayList<OldSelfLoopComponent>();

        // Distribute components to lists
        for (OldSelfLoopComponent component : cornerComponents) {
            // Rotate to get the correct order to allow us to distribute by the first port's side
            List<OldSelfLoopPort> ports = component.getPorts();
            rotatePorts(component);
            PortSide firstPortSide = ports.get(0).getPortSide();

            switch (firstPortSide) {
            case NORTH:
                rightTop.add(component);
                break;
            case EAST:
                rightBottom.add(component);
                break;
            case SOUTH:
                leftBottom.add(component);
                break;
            case WEST:
                leftTop.add(component);
                break;
            }
        }

        stackCornerComponents(slNode, rightTop, PortSide.NORTH, PortSide.EAST);
        stackCornerComponents(slNode, rightBottom, PortSide.EAST, PortSide.SOUTH);
        stackCornerComponents(slNode, leftBottom, PortSide.SOUTH, PortSide.WEST);
        stackCornerComponents(slNode, leftTop, PortSide.WEST, PortSide.NORTH);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Opposing Loops
    
    private void placeOpposingLoops(final Collection<OldSelfLoopComponent> oppossingComponents) {
        List<OldSelfLoopComponent> northSouth = new ArrayList<OldSelfLoopComponent>();
        List<OldSelfLoopComponent> westEast = new ArrayList<OldSelfLoopComponent>();

        // distribute to sides
        for (OldSelfLoopComponent component : oppossingComponents) {
            PortSide side1 = component.getPorts().get(0).getPortSide();
            switch (side1) {
            case NORTH:
                northSouth.add(component);
                break;
            case WEST:
                westEast.add(component);
                break;
            case SOUTH:
                northSouth.add(component);
                break;
            case EAST:
                westEast.add(component);
                break;
            }
        }

        addOpposingComponents(northSouth);
        addOpposingComponents(westEast);
    }

    /**
     * TODO Document.
     */
    private void addOpposingComponents(final Collection<OldSelfLoopComponent> oppossingComponents) {
        // position components
        for (OldSelfLoopComponent component : oppossingComponents) {

            // sort ports such that they point clockwise
            List<OldSelfLoopPort> ports = component.getPorts();
            ports.sort(INCOMING_EDGE_PORT_COMPARATOR);

            // It's it's an opposing component, there must be exactly two port sides its ports occupy
            Set<PortSide> sides = component.getPortSides();
            Iterator<PortSide> sidesIterator = sides.iterator();
            PortSide side1 = sidesIterator.next();
            PortSide side2 = sidesIterator.next();
            
            PortSide routeSide;
            OldSelfLoopNodeSide westNorthSide;
            OldSelfLoopNodeSide eastSouthSide;

            if (side1 == PortSide.NORTH || side2 == PortSide.NORTH) {
                westNorthSide = slNode.getNodeSide(PortSide.WEST);
                eastSouthSide = slNode.getNodeSide(PortSide.EAST);
            } else {
                westNorthSide = slNode.getNodeSide(PortSide.NORTH);
                eastSouthSide = slNode.getNodeSide(PortSide.SOUTH);
            }
            
            // get the number of non loop edges on the opposing side
            int westNorthNonLoops = nonLoopsPerSide.get(westNorthSide.getSide());
            int eastSouthNonLoops = nonLoopsPerSide.get(eastSouthSide.getSide());

            // get the current level of the opposing sides
            int portlevelWestNorth = westNorthSide.getMaximumPortLevel()
                    + new HashSet<>(westNorthSide.getOpposingSegments().values()).size();
            int portlevelEastSouth = eastSouthSide.getMaximumPortLevel()
                    + new HashSet<>(eastSouthSide.getOpposingSegments().values()).size();

            // the opposing segment is placed on the side with the fewest non loops to avoid crossings. In case the
            // amount of crossings is equal the segment is placed on the side with the lowest maximum level
            if (westNorthNonLoops < eastSouthNonLoops) {
                routeSide = westNorthSide.getSide();
            } else if (westNorthNonLoops > eastSouthNonLoops) {
                routeSide = eastSouthSide.getSide();
            } else {
                if (portlevelEastSouth <= portlevelWestNorth) {
                    routeSide = eastSouthSide.getSide();
                } else {
                    routeSide = westNorthSide.getSide();
                }
            }
            
            // order the ports such that are ordered clockwise
            while (ports.get(0).getPortSide() != routeSide.left()) {
                OldSelfLoopPort firstElementPort = ports.remove(0);
                ports.add(firstElementPort);
            }


            List<OldSelfLoopPort> firstSidePorts = new ArrayList<>();
            List<OldSelfLoopPort> secondSidePorts = new ArrayList<>();
            OldSelfLoopRoutingDirection direction = null;
            boolean startPortOnFirstSide = false;
            
            for (int i = 0; i < ports.size(); i++) {
                OldSelfLoopPort port = ports.get(i);
                setDirection(port, i, ports.size());
                PortSide side = port.getLPort().getSide();
                port.setPortSide(side);
                
                if (port.getPortSide() == side1) {
                    firstSidePorts.add(port);
                } else {
                    secondSidePorts.add(port);
                }

                if (i == 0) {
                    direction = port.getDirection();
                    startPortOnFirstSide = port.getPortSide() == side1;
                }
            }
            
            if (direction == OldSelfLoopRoutingDirection.LEFT) {
                if (startPortOnFirstSide) {
                    slNode.prependPorts(firstSidePorts, side1);
                    slNode.appendPorts(secondSidePorts, side2);
                } else {
                    slNode.appendPorts(firstSidePorts, side1);
                    slNode.prependPorts(secondSidePorts, side2);
                }
            } else {
                if (startPortOnFirstSide) {
                    slNode.appendPorts(firstSidePorts, side1);
                    slNode.prependPorts(secondSidePorts, side2);
                } else {
                    slNode.prependPorts(firstSidePorts, side1);
                    slNode.appendPorts(secondSidePorts, side2);
                }
            }
            
            // create opposing segments
            OldSelfLoopNodeSide routeSideRep = slNode.getNodeSide(routeSide);
            Map<PortSide, Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> segment =
                    OldSelfLoopOpposingSegment.create(component, slNode);
            routeSideRep.getOpposingSegments().putAll(segment.get(routeSide));
        }

        List<OldSelfLoopPort> ports = new ArrayList<OldSelfLoopPort>();
        for (OldSelfLoopNodeSide side : slNode.getSides()) {
            ports.addAll(side.getPorts());
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Three Side Loops
    
    private void placeThreeSidesLoop(final Collection<OldSelfLoopComponent> threeSideComponents) {
        for (OldSelfLoopComponent component : threeSideComponents) {
            // find the side which holds no component ports
            Set<PortSide> portSideValues = new HashSet<>(Arrays.asList(PortSide.values()));
            Set<PortSide> componentPortSides = component.getPortSides();
            portSideValues.remove(PortSide.UNDEFINED);
            portSideValues.removeAll(componentPortSides);
            PortSide freePortSide = portSideValues.iterator().next();

            // rotate to get the correct order
            List<OldSelfLoopPort> ports = component.getPorts();
            ports.sort(new Comparator<OldSelfLoopPort>() {

                @Override
                public int compare(final OldSelfLoopPort o1, final OldSelfLoopPort o2) {
                    return Integer.compare(o1.getPortSide().ordinal(), o2.getPortSide().ordinal());
                }
            });
            
            while (ports.get(0).getPortSide() != freePortSide.right()) {
                OldSelfLoopPort firstElementPort = ports.remove(0);
                ports.add(firstElementPort);
            }

            PortSide firstPortSide = ports.get(0).getPortSide();
            PortSide lastPortSide = ports.get(ports.size() - 1).getPortSide();

            PortSide middleSide = firstPortSide.right();
            PortSide middleOpposingSide = middleSide.right().right();

            // get the amount of non loops per side
            int middleSideNonLoops = nonLoopsPerSide.get(middleSide);
            int middleOpposingSideNonLoops = nonLoopsPerSide.get(middleOpposingSide);
            int firstSideNonLoops = nonLoopsPerSide.get(firstPortSide);
            int lastSideNonLoops = nonLoopsPerSide.get(lastPortSide);

            int type2 = lastSideNonLoops + middleOpposingSideNonLoops;
            int type3 = firstSideNonLoops + middleOpposingSideNonLoops;

            int minimum = Math.min(middleSideNonLoops, Math.min(type2, type3));

            if (minimum == middleSideNonLoops) {
                // do nothing but ensure this type is chosen first in case of equal crossings
                updateSideAndDirection(ports);

                Map<PortSide, Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> segment =
                        OldSelfLoopOpposingSegment.create(component, slNode);
                slNode.getNodeSide(middleSide).getOpposingSegments().putAll(segment.get(middleSide));

            } else if (minimum == type2) {
                OldSelfLoopPort firstElementPort = ports.remove(0);
                ports.add(firstElementPort);
                updateSideAndDirection(ports);

                // Create segments
                Map<PortSide, Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> segment =
                        OldSelfLoopOpposingSegment.create(component, slNode);
                slNode.getNodeSide(lastPortSide).getOpposingSegments().putAll(segment.get(lastPortSide));
                slNode.getNodeSide(middleOpposingSide).getOpposingSegments().putAll(segment.get(middleOpposingSide));

            } else {
                // rotate to create constellation
                OldSelfLoopPort lastElementPort = ports.remove(ports.size() - 1);
                ports.add(0, lastElementPort);
                updateSideAndDirection(ports);

                // Create segments
                Map<PortSide, Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> segment =
                        OldSelfLoopOpposingSegment.create(component, slNode);
                slNode.getNodeSide(firstPortSide).getOpposingSegments().putAll(segment.get(firstPortSide));
                slNode.getNodeSide(middleOpposingSide).getOpposingSegments().putAll(segment.get(middleOpposingSide));
            }
        }
    }

    /**
     * TODO Document.
     */
    private void updateSideAndDirection(final List<OldSelfLoopPort> ports) {
        for (int i = 0; i < ports.size(); i++) {
            OldSelfLoopPort port = ports.get(i);
            setDirection(port, i, ports.size());
            PortSide side = port.getLPort().getSide();
            port.setPortSide(side);
            if (port.getDirection() == OldSelfLoopRoutingDirection.LEFT) {
                slNode.prependPort(port, side);
            } else {
                slNode.appendPort(port, side);
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Four Side Loops
    
    private void placeFourSidesLoop(final Collection<OldSelfLoopComponent> fourSideComponents) {
        for (OldSelfLoopComponent component : fourSideComponents) {

            // rotate to get the correct order
            List<OldSelfLoopPort> ports = component.getPorts();
            ports.sort(new Comparator<OldSelfLoopPort>() {

                @Override
                public int compare(final OldSelfLoopPort o1, final OldSelfLoopPort o2) {
                    return Integer.compare(o1.getPortSide().ordinal(), o2.getPortSide().ordinal());
                }
            });

            int northSide = nonLoopsPerSide.get(PortSide.NORTH);
            int eastSide = nonLoopsPerSide.get(PortSide.EAST);
            int southSide = nonLoopsPerSide.get(PortSide.SOUTH);
            int westSide = nonLoopsPerSide.get(PortSide.WEST);

            int northEast = northSide + eastSide;
            int eastSouth = eastSide + southSide;
            int soutWest = southSide + westSide;
            int westNorth = westSide + northSide;

            int maximum = Math.max(northEast, Math.max(eastSouth, Math.max(soutWest, westNorth)));

            PortSide startSide = null;
            if (maximum == northEast) {
                startSide = PortSide.EAST;
            } else if (maximum == eastSouth) {
                startSide = PortSide.SOUTH;

            } else if (maximum == soutWest) {
                startSide = PortSide.WEST;

            } else if (maximum == westNorth) {
                startSide = PortSide.NORTH;
            }

            PortSide firstOpposingSide = startSide.right();
            PortSide secondOpposingSide = startSide.right().right();

            while (ports.get(0).getPortSide() != startSide) {
                OldSelfLoopPort firstElementPort = ports.remove(0);
                ports.add(firstElementPort);
            }

            updateSideAndDirection(ports);

            Map<PortSide, Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> segment =
                    OldSelfLoopOpposingSegment.create(component, slNode);
            slNode.getNodeSide(firstOpposingSide).getOpposingSegments().putAll(segment.get(firstOpposingSide));
            slNode.getNodeSide(secondOpposingSide).getOpposingSegments().putAll(segment.get(secondOpposingSide));
        }

    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Non-Loops

    private void placeNonLoops(final Collection<OldSelfLoopComponent> nonLoopComponents) {
        List<OldSelfLoopComponent> nonLoopNorth = new ArrayList<OldSelfLoopComponent>();
        List<OldSelfLoopComponent> nonLoopSouth = new ArrayList<OldSelfLoopComponent>();
        List<OldSelfLoopComponent> nonLoopWest = new ArrayList<OldSelfLoopComponent>();
        List<OldSelfLoopComponent> nonLoopEast = new ArrayList<OldSelfLoopComponent>();

        // Distribute components to their proper lists
        for (OldSelfLoopComponent component : nonLoopComponents) {
            OldSelfLoopPort port = component.getPorts().get(0);
            switch (port.getPortSide()) {
            case NORTH:
                nonLoopNorth.add(component);
                break;
            case SOUTH:
                nonLoopSouth.add(component);
                break;
            case EAST:
                nonLoopEast.add(component);
                break;
            case WEST:
                nonLoopWest.add(component);
                break;
            default:
                nonLoopEast.add(component);
                break;
            }

        }

        // Remember the number of non-loop ports on each side
        nonLoopsPerSide.put(PortSide.NORTH, nonLoopNorth.size());
        nonLoopsPerSide.put(PortSide.SOUTH, nonLoopSouth.size());
        nonLoopsPerSide.put(PortSide.WEST, nonLoopWest.size());
        nonLoopsPerSide.put(PortSide.EAST, nonLoopEast.size());

        // Place non-loop ports
        placeNonLoopPorts(slNode, PortSide.NORTH, nonLoopNorth);
        placeNonLoopPorts(slNode, PortSide.SOUTH, nonLoopSouth);
        placeNonLoopPorts(slNode, PortSide.WEST, nonLoopWest);
        placeNonLoopPorts(slNode, PortSide.EAST, nonLoopEast);
    }
}
