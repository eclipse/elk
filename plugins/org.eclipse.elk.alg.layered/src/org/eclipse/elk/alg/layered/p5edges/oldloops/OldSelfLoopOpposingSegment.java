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
public final class OldSelfLoopOpposingSegment {
    
    /** The component this segment belongs to. */
    private OldSelfLoopComponent component;
    /** The port side this segment passes. */
    private final PortSide side;
    /** The level of the segment. It represents the abstract distance to the node. */
    private int level;
    /** The segment's label offset. */
    private double labelOffset;
    /** The port this segment derives its level from. */
    private OldSelfLoopPort levelGivingPort;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Constructors

    /**
     * Private constructor.
     */
    private OldSelfLoopOpposingSegment(final OldSelfLoopComponent component, final PortSide side) {
        this.component = component;
        this.side = side;
    }

    /**
     * Private constructor.
     */
    private OldSelfLoopOpposingSegment(final OldSelfLoopComponent component, final PortSide currentPortSide,
            final OldSelfLoopPort port) {
        
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
    public static Map<PortSide, Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> create(final OldSelfLoopComponent component,
            final OldSelfLoopNode nodeRep) {
        
        Map<PortSide, Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> segments = new HashMap<>();
        
        if (supportsHyperedges(nodeRep)) {
            segments = createHyperEdgeSegments(component);
        } else {
            segments = createNonHyperEdgeSegments(component, nodeRep);
        }
        
        return segments;
    }

    /**
     * Implements {@link #create(OldSelfLoopComponent, OldSelfLoopNode)} if the graph allows for self loops to be merged into
     * hyperedges.
     */
    private static Map<PortSide, Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> createHyperEdgeSegments(
            final OldSelfLoopComponent component) {
        
        // initialize map with all four node sides
        Map<PortSide, Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> segments = initSegmentMap();

        // compute the sides the component is spanning
        Set<PortSide> sides = new HashSet<>(component.getComponentSpanningSides());
        List<OldSelfLoopPort> ports = component.getPorts();
        
        // remove the start and end side of the spanning sides; we are only interested in the sides in between
        sides.remove(ports.get(0).getPortSide());
        sides.remove(ports.get(ports.size() - 1).getPortSide());

        // for each remaining side a segment is created
        for (PortSide side : sides) {
            List<OldSelfLoopPort> portsOfSide = component.getPortsOfSide(side);
            OldSelfLoopOpposingSegment segment;

            // depending on other ports on the side a level giving port is added
            if (portsOfSide != null && !portsOfSide.isEmpty()) {
                segment = new OldSelfLoopOpposingSegment(component, side, portsOfSide.get(0));
            } else {
                segment = new OldSelfLoopOpposingSegment(component, side);
            }

            // add the new segments to their corresponding side
            Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment> sideSegments = segments.get(side);
            for (OldSelfLoopEdge edge : component.getConnectedEdges()) {
                sideSegments.put(edge, segment);
            }
        }
        return segments;
    }

    /**
     * Implements {@link #create(OldSelfLoopComponent, OldSelfLoopNode)} if the graph does not allow for self loops to be
     * merged into hyperedges.
     */
    private static Map<PortSide, Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> createNonHyperEdgeSegments(
            final OldSelfLoopComponent component, final OldSelfLoopNode nodeRep) {

        // initialize map with all four node sides
        Map<PortSide, Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> segments = initSegmentMap();

        rotatePortsToBeRightPointing(component);

        // initialize the data types
        Map<LPort, OldSelfLoopEdge> startedEdges = new HashMap<>();
        List<OldSelfLoopPort> ports = component.getPorts();
        Set<OldSelfLoopEdge> visitedEdges = new HashSet<OldSelfLoopEdge>();

        // only actual self-loops are processed normal edges are ignored
        if (ports.size() > 1) {
            OldSelfLoopPort startPort = ports.get(0);
            PortSide startSide = startPort.getPortSide();
            updateConnectedEdges(startedEdges, startPort, visitedEdges);

            // remaining ports
            List<OldSelfLoopPort> sidePorts = component.getPortsOfSide(startSide);
            OldSelfLoopNodeSide nodeSide = nodeRep.getNodeSide(startSide);
            sidePorts.sort((port1, port2) -> {
                return Integer.compare(nodeSide.getPorts().indexOf(port1), nodeSide.getPorts().indexOf(port2));
            });
            
            int startIndex = sidePorts.indexOf(startPort);
            if (sidePorts.size() > 1 && startIndex < sidePorts.size()) {
                sidePorts = sidePorts.subList(startIndex + 1, sidePorts.size());
            } else {
                sidePorts = new ArrayList<OldSelfLoopPort>();
            }

            for (int i = 0; i < nodeRep.getSides().size(); i++) {
                Map<LPort, OldSelfLoopEdge> startedEdges2 = new HashMap<>();
                for (OldSelfLoopPort port : sidePorts) {
                    startedEdges.remove(port.getLPort());
                    updateConnectedEdges(startedEdges2, port, visitedEdges);
                }

                if (i != 0) {
                    for (OldSelfLoopEdge startedEdge : startedEdges.values()) {
                        OldSelfLoopOpposingSegment segment = new OldSelfLoopOpposingSegment(component, startSide);
                        Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment> sideSegment = segments.get(startSide);
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
    public OldSelfLoopComponent getComponent() {
        return component;
    }

    /**
     * Setss the component this segment belongs to.
     */
    public void setComponent(final OldSelfLoopComponent component) {
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
    public OldSelfLoopPort getLevelGivingPort() {
        return levelGivingPort;
    }

    /**
     * Sets the port this segment derives its level from.
     */
    public void setLevelGivingPort(final OldSelfLoopPort levelGivingPort) {
        this.levelGivingPort = levelGivingPort;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Miscellaneous

    /**
     * Checks whether or not the given node supports hyperedge self loops.
     */
    private static boolean supportsHyperedges(final OldSelfLoopNode nodeRep) {
        return nodeRep.getNode().getGraph().getProperty(LayeredOptions.EDGE_ROUTING) != EdgeRouting.SPLINES;
    }

    /**
     * Returns a map initialized with one hash map for each port side.
     */
    private static Map<PortSide, Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> initSegmentMap() {
        Map<PortSide, Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> segments = new EnumMap<>(PortSide.class);
        
        segments.put(PortSide.NORTH, new HashMap<OldSelfLoopEdge, OldSelfLoopOpposingSegment>());
        segments.put(PortSide.EAST, new HashMap<OldSelfLoopEdge, OldSelfLoopOpposingSegment>());
        segments.put(PortSide.SOUTH, new HashMap<OldSelfLoopEdge, OldSelfLoopOpposingSegment>());
        segments.put(PortSide.WEST, new HashMap<OldSelfLoopEdge, OldSelfLoopOpposingSegment>());
        
        return segments;
    }
    
    /**
     * TODO Document.
     */
    private static void rotatePortsToBeRightPointing(final OldSelfLoopComponent component) {
        List<OldSelfLoopPort> ports = component.getPorts();
        OldSelfLoopPort currPort = ports.get(0);
        
        while (currPort.getDirection() != OldSelfLoopRoutingDirection.RIGHT) {
            OldSelfLoopPort removedPort = ports.remove(0);
            ports.add(removedPort);
            currPort = ports.get(0);
        }
    }

    /**
     * TODO Document.
     */
    private static void updateConnectedEdges(final Map<LPort, OldSelfLoopEdge> startedEdges, final OldSelfLoopPort port,
            final Set<OldSelfLoopEdge> visitedEdges) {
        
        Iterable<OldSelfLoopEdge> connectedEdges = port.getConnectedEdges();
        for (OldSelfLoopEdge edge : connectedEdges) {
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
