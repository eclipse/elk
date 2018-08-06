/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.calculators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNodeSide;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopOpposingSegment;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
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

    private static final Comparator<SelfLoopOpposingSegment> OPPOSING_COMPARATOR = (segment1, segment2) -> {
            SelfLoopComponent component1 = segment1.getComponent();
            SelfLoopPort firstPort1 = component1.getPorts().get(0);
            
            SelfLoopComponent component2 = segment2.getComponent();
            SelfLoopPort firstPort2 = component2.getPorts().get(0);
            SelfLoopPort secondPort2 = component2.getPorts().get(component2.getPorts().size() - 1);

            if (firstPort1.getDirection() == firstPort2.getDirection()) {
                return -1 * Integer.compare(firstPort1.getOriginalIndex(), firstPort2.getOriginalIndex());
            } else {
                return -1 * Integer.compare(firstPort1.getOriginalIndex(), secondPort2.getOriginalIndex());
            }
        };

    private static final Comparator<Entry<SelfLoopEdge, SelfLoopOpposingSegment>> OPPOSING_NON_HYPER_EDGE_COMPARATOR =
        (entry1, entry2) -> {
            SelfLoopOpposingSegment segment1 = entry1.getValue();
            SelfLoopOpposingSegment segment2 = entry2.getValue();
            SelfLoopComponent component1 = segment1.getComponent();
            SelfLoopComponent component2 = segment2.getComponent();

            if (component1 == component2) {
                PortSide segmentSide = segment1.getSide();
                SelfLoopEdge edge1 = entry1.getKey();
                SelfLoopEdge edge2 = entry2.getKey();
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

            SelfLoopPort firstPort1 = component1.getPorts().get(0);
            SelfLoopPort firstPort2 = component2.getPorts().get(0);
            SelfLoopPort secondPort2 = component2.getPorts().get(component2.getPorts().size() - 1);

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
    public static void calculatePortLevels(final SelfLoopNode slNode) {
        for (SelfLoopNodeSide side : slNode.getSides()) {
            ArrayList<SelfLoopComponent> sideComponentDependencies = new ArrayList<>(side.getComponentDependencies());
            
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
    public static int calculateHyperedgePortLevels(final SelfLoopNodeSide side,
            final List<SelfLoopComponent> components) {
        
        int maximumLevel = 0;
        int componentLevel = 0;

        for (SelfLoopComponent component : components) {
            // check whether there are more dependencies
            List<SelfLoopComponent> sideDependencies = component.getDependencyComponents().get(side);
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
            for (SelfLoopPort port : component.getPorts()) {
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
    private static int calculateNonHyperedgePortMaxLevels(final SelfLoopNodeSide side,
            final ArrayList<SelfLoopComponent> components) {
        int maximumLevel = 0;
        int componentLevel = 0;

        for (SelfLoopComponent component : components) {
            // check whether there are more dependencies
            List<SelfLoopComponent> sideDependencies = component.getDependencyComponents().get(side);
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
            for (SelfLoopPort port : component.getPorts()) {
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
    public static void calculateOpposingSegmentLevel(final SelfLoopNode slNode) {
        int maximumSegmentLevelOfNode = 0;
        boolean supportHyperedges = supportsHyperedges(slNode);

        for (SelfLoopNodeSide side : slNode.getSides()) {
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
    private static int calculateHyperedgeOpposingSegmentMaxLevel(final SelfLoopNodeSide side,
            final Set<SelfLoopOpposingSegment> segments) {
        
        List<SelfLoopOpposingSegment> sortedSegments = new ArrayList<>(segments);
        sortedSegments.sort(OPPOSING_COMPARATOR);
        int level = side.getMaximumPortLevel();
        
        for (SelfLoopOpposingSegment segment : sortedSegments) {
            SelfLoopPort levelGivingPort = segment.getLevelGivingPort();
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
    public static int calculateNonHyperedgeOpposingSegmentMaxLevel(final SelfLoopNodeSide side,
            final Map<SelfLoopEdge, SelfLoopOpposingSegment> opposingSegments) {

        Set<Entry<SelfLoopEdge, SelfLoopOpposingSegment>> unsortedSegments = opposingSegments.entrySet();
        ArrayList<Entry<SelfLoopEdge, SelfLoopOpposingSegment>> sortedSegments = new ArrayList<>(unsortedSegments);
        sortedSegments.sort(OPPOSING_NON_HYPER_EDGE_COMPARATOR);

        int level = side.getMaximumPortLevel();
        for (Entry<SelfLoopEdge, SelfLoopOpposingSegment> entry : sortedSegments) {
            SelfLoopOpposingSegment segment = entry.getValue();
            segment.setLevel(++level);
        }
        return level;
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Edge Order

    /**
     * Calculate the edge orders for the components on each side.
     */
    public static void calculateEdgeOrders(final List<SelfLoopComponent> components) {
        for (PortSide side : PortSide.values()) {
            if (side.ordinal() != 0) {
                for (SelfLoopComponent component : components) {
                    List<SelfLoopEdge> edgeDependencies = component.getEdgeDependencies().get(side);
                    calculateEdgeOrder(side, edgeDependencies);
                }
            }
        }
    }

    /**
     * Computes the edge order on the given side.
     */
    private static int calculateEdgeOrder(final PortSide side, final List<SelfLoopEdge> edgeDependencies) {
        int maximumLevel = 0;
        int componentLevel = 0;
        for (SelfLoopEdge edge : edgeDependencies) {
            List<SelfLoopEdge> nextEdgeDependencies = edge.getDependencyEdges().get(side);
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

        for (SelfLoopEdge edge : edgeDependencies) {
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
    private static boolean supportsHyperedges(final SelfLoopNode nodeRep) {
        return nodeRep.getNode().getGraph().getProperty(LayeredOptions.EDGE_ROUTING) != EdgeRouting.SPLINES;
    }
}
