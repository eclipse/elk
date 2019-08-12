/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.calculators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNodeSide;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopOpposingSegment;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopPort;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Iterables;

/**
 * Offers a set of methods for calculating the level of ports or opposing segments.
 */
public final class SelfLoopLevelCalculator {

    /**
     * No instantiation.
     */
    private SelfLoopLevelCalculator() {
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Comparators

    private static final Comparator<OldSelfLoopOpposingSegment> OPPOSING_COMPARATOR = (segment1, segment2) -> {
            OldSelfLoopComponent component1 = segment1.getComponent();
            OldSelfLoopPort firstPort1 = component1.getPorts().get(0);
            
            OldSelfLoopComponent component2 = segment2.getComponent();
            OldSelfLoopPort firstPort2 = component2.getPorts().get(0);
            OldSelfLoopPort secondPort2 = component2.getPorts().get(component2.getPorts().size() - 1);

            if (firstPort1.getDirection() == firstPort2.getDirection()) {
                return -1 * Integer.compare(firstPort1.getOriginalIndex(), firstPort2.getOriginalIndex());
            } else {
                return -1 * Integer.compare(firstPort1.getOriginalIndex(), secondPort2.getOriginalIndex());
            }
        };

    private static final Comparator<Entry<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> OPPOSING_NON_HYPER_EDGE_COMPARATOR =
        (entry1, entry2) -> {
            OldSelfLoopOpposingSegment segment1 = entry1.getValue();
            OldSelfLoopOpposingSegment segment2 = entry2.getValue();
            OldSelfLoopComponent component1 = segment1.getComponent();
            OldSelfLoopComponent component2 = segment2.getComponent();

            if (component1 == component2) {
                PortSide segmentSide = segment1.getSide();
                OldSelfLoopEdge edge1 = entry1.getKey();
                OldSelfLoopEdge edge2 = entry2.getKey();
                PortSide nextSide = segmentSide.left();
                Integer order1;
                Integer order2;
                do {
                    order1 = edge1.getEdgeOrders().get(nextSide);
                    order2 = edge2.getEdgeOrders().get(nextSide);

                    nextSide = segmentSide.left();
                } while (order1 == null && order2 == null);

                return Integer.compare(order1, order2);
            }

            OldSelfLoopPort firstPort1 = component1.getPorts().get(0);
            OldSelfLoopPort firstPort2 = component2.getPorts().get(0);
            OldSelfLoopPort secondPort2 = component2.getPorts().get(component2.getPorts().size() - 1);

            if (firstPort1.getDirection() == firstPort2.getDirection()) {
                return -1 * Integer.compare(firstPort1.getOriginalIndex(), firstPort2.getOriginalIndex());
            } else {
                return -1 * Integer.compare(firstPort1.getOriginalIndex(), secondPort2.getOriginalIndex());
            }
        };
            
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Level Calculation

    /**
     * The actual level of each port of a node is calculated by making use of the component's dependencies. In case
     * hyperedges are supported, depth-first search can be used.
     */
    public static void calculatePortLevels(final OldSelfLoopNode slNode) {
        for (OldSelfLoopNodeSide side : slNode.getSides()) {
            ArrayList<OldSelfLoopComponent> sideComponentDependencies = new ArrayList<>(side.getComponentDependencies());
            
            if (!sideComponentDependencies.isEmpty()) {
                int maximumPortLevel = 0;

                if (supportsHyperedges(slNode)) {
                    maximumPortLevel = calculateHyperedgePortLevels(side, sideComponentDependencies);
                } else {
                    maximumPortLevel = calculateNonHyperedgePortMaxLevels(side, sideComponentDependencies);
                }
                side.setMaximumPortLevel(maximumPortLevel);
            }
        }
    }

    /**
     * Calculates the port levels for one side of a node.
     */
    public static int calculateHyperedgePortLevels(final OldSelfLoopNodeSide side,
            final List<OldSelfLoopComponent> components) {
        
        int maximumLevel = 0;
        int componentLevel = 0;

        for (OldSelfLoopComponent component : components) {
            // check whether there are more dependencies
            List<OldSelfLoopComponent> sideDependencies = component.getDependencyComponents().get(side);
            if (sideDependencies == null || sideDependencies.isEmpty()) {
                // if there are no dependencies the level is one
                componentLevel = 1;
            } else {
                // if there are dependencies their maximum port level has to be calculated first
                componentLevel = calculateHyperedgePortLevels(side, sideDependencies) + 1;
            }

            // update the maximum level if necessary
            maximumLevel = Math.max(maximumLevel, componentLevel);

            // assign level to each port of the component
            for (OldSelfLoopPort port : component.getPorts()) {
                if (port.getPortSide() == side.getSide()) {
                    port.setMaximumLevel(componentLevel);
                }
            }
        }

        return maximumLevel;
    }

    /**
     * Calculate the port levels for components for which hyperedges are not supported.
     */
    private static int calculateNonHyperedgePortMaxLevels(final OldSelfLoopNodeSide side,
            final ArrayList<OldSelfLoopComponent> components) {
        int maximumLevel = 0;
        int componentLevel = 0;

        for (OldSelfLoopComponent component : components) {
            // check whether there are more dependencies
            List<OldSelfLoopComponent> sideDependencies = component.getDependencyComponents().get(side);
            if (sideDependencies == null || sideDependencies.isEmpty()) {
                // if there are no dependencies the level is one
                componentLevel = 1;
            } else {
                // if there are dependencies their maximum port level has to be calculated first
                componentLevel = calculateHyperedgePortLevels(side, sideDependencies) + 1;
            }

            // update the maximum level if necessary
            maximumLevel = Math.max(maximumLevel, componentLevel);

            // assign level to each port of the component
            for (OldSelfLoopPort port : component.getPorts()) {
                if (port.getPortSide() == side.getSide()) {
                    int connectedEdges = Iterables.size(port.getConnectedEdges());
                    port.setMaximumLevel(componentLevel + connectedEdges - 1);

                    maximumLevel = Math.max(maximumLevel, componentLevel + connectedEdges - 1);
                }
            }
        }

        return maximumLevel;
    }

    /**
     * Calculate the level for the opposing segments.
     */
    public static void calculateOpposingSegmentLevel(final OldSelfLoopNode slNode) {
        int maximumSegmentLevelOfNode = 0;
        boolean supportHyperedges = supportsHyperedges(slNode);

        for (OldSelfLoopNodeSide side : slNode.getSides()) {
            if (supportHyperedges) {
                maximumSegmentLevelOfNode = SelfLoopLevelCalculator.calculateHyperedgeOpposingSegmentMaxLevel(side,
                        new HashSet<>(side.getOpposingSegments().values()));
            } else {
                maximumSegmentLevelOfNode = SelfLoopLevelCalculator.calculateNonHyperedgeOpposingSegmentMaxLevel(side,
                        side.getOpposingSegments());

            }
            side.setMaximumSegmentLevel(maximumSegmentLevelOfNode);
        }
    }

    /**
     * Calculates the maximum level for the given opposing segments on the given side.
     */
    private static int calculateHyperedgeOpposingSegmentMaxLevel(final OldSelfLoopNodeSide side,
            final Set<OldSelfLoopOpposingSegment> segments) {
        
        List<OldSelfLoopOpposingSegment> sortedSegments = new ArrayList<>(segments);
        sortedSegments.sort(OPPOSING_COMPARATOR);
        int level = side.getMaximumPortLevel();
        
        for (OldSelfLoopOpposingSegment segment : sortedSegments) {
            OldSelfLoopPort levelGivingPort = segment.getLevelGivingPort();
            if (levelGivingPort != null) {
                segment.setLevel(levelGivingPort.getMaximumLevel());
            } else {
                segment.setLevel(++level);
            }
        }
        
        return level;
    }

    /**
     * Calculates the maximum level for the given opposing segments on the given side.
     */
    public static int calculateNonHyperedgeOpposingSegmentMaxLevel(final OldSelfLoopNodeSide side,
            final Map<OldSelfLoopEdge, OldSelfLoopOpposingSegment> opposingSegments) {

        Set<Entry<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> unsortedSegments = opposingSegments.entrySet();
        ArrayList<Entry<OldSelfLoopEdge, OldSelfLoopOpposingSegment>> sortedSegments = new ArrayList<>(unsortedSegments);
        sortedSegments.sort(OPPOSING_NON_HYPER_EDGE_COMPARATOR);

        int level = side.getMaximumPortLevel();
        for (Entry<OldSelfLoopEdge, OldSelfLoopOpposingSegment> entry : sortedSegments) {
            OldSelfLoopOpposingSegment segment = entry.getValue();
            segment.setLevel(++level);
        }
        return level;
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Edge Order

    /**
     * Calculate the edge orders for the components on each side.
     */
    public static void calculateEdgeOrders(final List<OldSelfLoopComponent> components) {
        for (PortSide side : PortSide.values()) {
            if (side.ordinal() != 0) {
                for (OldSelfLoopComponent component : components) {
                    List<OldSelfLoopEdge> edgeDependencies = component.getEdgeDependencies().get(side);
                    calculateEdgeOrder(side, edgeDependencies);
                }
            }
        }
    }

    /**
     * Computes the edge order on the given side.
     */
    private static int calculateEdgeOrder(final PortSide side, final List<OldSelfLoopEdge> edgeDependencies) {
        int maximumLevel = 0;
        int componentLevel = 0;
        for (OldSelfLoopEdge edge : edgeDependencies) {
            List<OldSelfLoopEdge> nextEdgeDependencies = edge.getDependencyEdges().get(side);
            if (nextEdgeDependencies == null || nextEdgeDependencies.isEmpty()) {
                // if there are no dependencies the level is one
                componentLevel = 1;
            } else {
                // if there are dependencies their maximum port level has to be calculated first
                componentLevel = calculateEdgeOrder(side, nextEdgeDependencies) + 1;
            }
            // update the maximum level if necessary
            maximumLevel = Math.max(maximumLevel, componentLevel);
        }

        for (OldSelfLoopEdge edge : edgeDependencies) {
            Map<PortSide, Integer> orderMap = edge.getEdgeOrders();
            orderMap.put(side, maximumLevel);
        }

        return maximumLevel;
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utilities

    /**
     * Checks whether the given node supports hyperedge routing.
     */
    private static boolean supportsHyperedges(final OldSelfLoopNode nodeRep) {
        return nodeRep.getNode().getGraph().getProperty(LayeredOptions.EDGE_ROUTING) != EdgeRouting.SPLINES;
    }
}
