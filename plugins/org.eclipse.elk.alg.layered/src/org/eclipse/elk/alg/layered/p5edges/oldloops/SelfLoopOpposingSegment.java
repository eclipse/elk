/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.PortSide;

/**
 * An opposing segment for a self loop describes the part of a self loop hich passes a side of the node without being
 * connected to a port there.
 */
public final class SelfLoopOpposingSegment {
    
    /** The component this segment belongs to. */
    private SelfLoopComponent component;
    /** The port side this segment passes. */
    private final PortSide side;
    /** The level of the segment. It represents the abstract distance to the node. */
    private int level;
    /** The segment's label offset. */
    private double labelOffset;
    /** The port this segment derives its level from. */
    private SelfLoopPort levelGivingPort;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Constructors

    /**
     * Private constructor.
     */
    private SelfLoopOpposingSegment(final SelfLoopComponent component, final PortSide side) {
        this.component = component;
        this.side = side;
    }

    /**
     * Private constructor.
     */
    private SelfLoopOpposingSegment(final SelfLoopComponent component, final PortSide currentPortSide,
            final SelfLoopPort port) {
        
        this.component = component;
        this.side = currentPortSide;
        this.levelGivingPort = port;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Methods

    /**
     * Create segments for all edges that belong to the given self loop component. We return a map that maps each
     * edge to its corresponding segment, specific to each possible port side.
     */
    public static Map<PortSide, Map<SelfLoopEdge, SelfLoopOpposingSegment>> create(final SelfLoopComponent component,
            final SelfLoopNode nodeRep) {
        
        Map<PortSide, Map<SelfLoopEdge, SelfLoopOpposingSegment>> segments = new HashMap<>();
        
        if (supportsHyperedges(nodeRep)) {
            segments = createHyperEdgeSegments(component);
        } else {
            segments = createNonHyperEdgeSegments(component, nodeRep);
        }
        
        return segments;
    }

    /**
     * Implements {@link #create(SelfLoopComponent, SelfLoopNode)} if the graph allows for self loops to be merged into
     * hyperedges.
     */
    private static Map<PortSide, Map<SelfLoopEdge, SelfLoopOpposingSegment>> createHyperEdgeSegments(
            final SelfLoopComponent component) {
        
        // initialize map with all four node sides
        Map<PortSide, Map<SelfLoopEdge, SelfLoopOpposingSegment>> segments = initSegmentMap();

        // compute the sides the component is spanning
        Set<PortSide> sides = new HashSet<>(component.getComponentSpanningSides());
        List<SelfLoopPort> ports = component.getPorts();
        
        // remove the start and end side of the spanning sides; we are only interested in the sides in between
        sides.remove(ports.get(0).getPortSide());
        sides.remove(ports.get(ports.size() - 1).getPortSide());

        // for each remaining side a segment is created
        for (PortSide side : sides) {
            List<SelfLoopPort> portsOfSide = component.getPortsOfSide(side);
            SelfLoopOpposingSegment segment;

            // depending on other ports on the side a level giving port is added
            if (portsOfSide != null && !portsOfSide.isEmpty()) {
                segment = new SelfLoopOpposingSegment(component, side, portsOfSide.get(0));
            } else {
                segment = new SelfLoopOpposingSegment(component, side);
            }

            // add the new segments to their corresponding side
            Map<SelfLoopEdge, SelfLoopOpposingSegment> sideSegments = segments.get(side);
            for (SelfLoopEdge edge : component.getConnectedEdges()) {
                sideSegments.put(edge, segment);
            }
        }
        return segments;
    }

    /**
     * Implements {@link #create(SelfLoopComponent, SelfLoopNode)} if the graph does not allow for self loops to be
     * merged into hyperedges.
     */
    private static Map<PortSide, Map<SelfLoopEdge, SelfLoopOpposingSegment>> createNonHyperEdgeSegments(
            final SelfLoopComponent component, final SelfLoopNode nodeRep) {

        // initialize map with all four node sides
        Map<PortSide, Map<SelfLoopEdge, SelfLoopOpposingSegment>> segments = initSegmentMap();

        rotatePortsToBeRightPointing(component);

        // initialize the data types
        Map<LPort, SelfLoopEdge> startedEdges = new HashMap<>();
        List<SelfLoopPort> ports = component.getPorts();
        Set<SelfLoopEdge> visitedEdges = new HashSet<SelfLoopEdge>();

        // only actual self-loops are processed normal edges are ignored
        if (ports.size() > 1) {
            SelfLoopPort startPort = ports.get(0);
            PortSide startSide = startPort.getPortSide();
            updateConnectedEdges(startedEdges, startPort, visitedEdges);

            // remaining ports
            List<SelfLoopPort> sidePorts = component.getPortsOfSide(startSide);
            SelfLoopNodeSide nodeSide = nodeRep.getNodeSide(startSide);
            sidePorts.sort((port1, port2) -> {
                return Integer.compare(nodeSide.getPorts().indexOf(port1), nodeSide.getPorts().indexOf(port2));
            });
            
            int startIndex = sidePorts.indexOf(startPort);
            if (sidePorts.size() > 1 && startIndex < sidePorts.size()) {
                sidePorts = sidePorts.subList(startIndex + 1, sidePorts.size());
            } else {
                sidePorts = new ArrayList<SelfLoopPort>();
            }

            for (int i = 0; i < nodeRep.getSides().size(); i++) {
                Map<LPort, SelfLoopEdge> startedEdges2 = new HashMap<>();
                for (SelfLoopPort port : sidePorts) {
                    startedEdges.remove(port.getLPort());
                    updateConnectedEdges(startedEdges2, port, visitedEdges);
                }

                if (i != 0) {
                    for (SelfLoopEdge startedEdge : startedEdges.values()) {
                        SelfLoopOpposingSegment segment = new SelfLoopOpposingSegment(component, startSide);
                        Map<SelfLoopEdge, SelfLoopOpposingSegment> sideSegment = segments.get(startSide);
                        sideSegment.put(startedEdge, segment);
                    }
                }

                startedEdges.putAll(startedEdges2);
                startSide = startSide.right();
                sidePorts = component.getPortsOfSide(startSide);
            }
        }
        
        return segments;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties

    /**
     * Returns the component this segment belongs to.
     */
    public SelfLoopComponent getComponent() {
        return component;
    }

    /**
     * Setss the component this segment belongs to.
     */
    public void setComponent(final SelfLoopComponent component) {
        this.component = component;
    }

    /**
     * Returns the side this segment is on.
     */
    public PortSide getSide() {
        return side;
    }

    /**
     * Returns the routing level this segment is assigned to.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the routing level this segment is assigned to.
     */
    public void setLevel(final int level) {
        this.level = level;
    }

    /**
     * Returns the segment's label offset.
     */
    public double getLabelOffset() {
        return labelOffset;
    }

    /**
     * Sets the segment's label offset.
     */
    public void setLabelOffset(final double labelOffset) {
        this.labelOffset = labelOffset;
    }

    /**
     * Returns the port this segment derives its level from.
     */
    public SelfLoopPort getLevelGivingPort() {
        return levelGivingPort;
    }

    /**
     * Sets the port this segment derives its level from.
     */
    public void setLevelGivingPort(final SelfLoopPort levelGivingPort) {
        this.levelGivingPort = levelGivingPort;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Miscellaneous

    /**
     * Checks whether or not the given node supports hyperedge self loops.
     */
    private static boolean supportsHyperedges(final SelfLoopNode nodeRep) {
        return nodeRep.getNode().getGraph().getProperty(LayeredOptions.EDGE_ROUTING) != EdgeRouting.SPLINES;
    }

    /**
     * Returns a map initialized with one hash map for each port side.
     */
    private static Map<PortSide, Map<SelfLoopEdge, SelfLoopOpposingSegment>> initSegmentMap() {
        Map<PortSide, Map<SelfLoopEdge, SelfLoopOpposingSegment>> segments = new EnumMap<>(PortSide.class);
        
        segments.put(PortSide.NORTH, new HashMap<SelfLoopEdge, SelfLoopOpposingSegment>());
        segments.put(PortSide.EAST, new HashMap<SelfLoopEdge, SelfLoopOpposingSegment>());
        segments.put(PortSide.SOUTH, new HashMap<SelfLoopEdge, SelfLoopOpposingSegment>());
        segments.put(PortSide.WEST, new HashMap<SelfLoopEdge, SelfLoopOpposingSegment>());
        
        return segments;
    }
    
    /**
     * TODO Document.
     */
    private static void rotatePortsToBeRightPointing(final SelfLoopComponent component) {
        List<SelfLoopPort> ports = component.getPorts();
        SelfLoopPort currPort = ports.get(0);
        
        while (currPort.getDirection() != SelfLoopRoutingDirection.RIGHT) {
            SelfLoopPort removedPort = ports.remove(0);
            ports.add(removedPort);
            currPort = ports.get(0);
        }
    }

    /**
     * TODO Document.
     */
    private static void updateConnectedEdges(final Map<LPort, SelfLoopEdge> startedEdges, final SelfLoopPort port,
            final Set<SelfLoopEdge> visitedEdges) {
        
        Iterable<SelfLoopEdge> connectedEdges = port.getConnectedEdges();
        for (SelfLoopEdge edge : connectedEdges) {
            if (!visitedEdges.contains(edge)) {
                visitedEdges.add(edge);
                if (edge.getTarget() == port) {
                    startedEdges.put(edge.getSource().getLPort(), edge);
                } else {
                    startedEdges.put(edge.getTarget().getLPort(), edge);
                }
            }
        }
    }

    @Override
    public String toString() {
        return side.toString();
    }

}
