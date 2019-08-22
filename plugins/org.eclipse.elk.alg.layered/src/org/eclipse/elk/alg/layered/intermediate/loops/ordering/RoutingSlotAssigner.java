/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.loops.ordering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfHyperLoop;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfLoopHolder;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.p5edges.orthogonal.HyperEdgeCycleBreaker;
import org.eclipse.elk.alg.layered.p5edges.orthogonal.HyperEdgeSegment;
import org.eclipse.elk.alg.layered.p5edges.orthogonal.OrthogonalRoutingGenerator;
import org.eclipse.elk.alg.layered.p5edges.orthogonal.SegmentDependency;
import org.eclipse.elk.core.options.PortSide;

/**
 * Assigns routing slots for each {@link SelfHyperLoop} for each of the port sides it spans. This is done such that it
 * tries to minimize the number of crossings between self loops that ensue. The crossing minimization is based on what
 * the {@link OrthogonalRoutingGenerator} does to order vertical segments of hyperedges.
 */
public class RoutingSlotAssigner {
    
    /** Our segments that constitute our crossing graph. */
    private List<HyperEdgeSegment> hyperEdgeSegments;
    /** Map of segments created for the loops. */
    private Map<SelfHyperLoop, HyperEdgeSegment> slLoopToSegmentMap;
    /** Maps each loop to an array indexed by port IDs and specifying whether the loops runs along the port. */
    private Map<SelfHyperLoop, boolean[]> slLoopActivityOverPorts;

    /**
     * Assigns routing slots to all self loops per port side they span.
     */
    public void assignRoutingSlots(final SelfLoopHolder slHolder) {
        // We're using the orthogonal edge router's cycle breaker for this, so create the crossing graph for our loops
        createCrossingGraph(slHolder);
        HyperEdgeCycleBreaker.breakCycles(hyperEdgeSegments,
                slHolder.getLNode().getGraph().getProperty(InternalProperties.RANDOM));
        
        // Assign routing slots based on the graph
        doAssignRoutingSlots(slHolder);
        
        // Cleanup
        hyperEdgeSegments = null;
        slLoopToSegmentMap = null;
        slLoopActivityOverPorts = null;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Crossing Graph

    /**
     * Create the crossing graph by creating one {@link HyperEdgeSegment} for each {@link SelfHyperLoop} and computing
     * the proper dependencies between them.
     */
    private void createCrossingGraph(final SelfLoopHolder slHolder) {
        List<SelfHyperLoop> slLoops = slHolder.getSLHyperLoops();
        
        // Create an edge segment for each loop
        hyperEdgeSegments = new ArrayList<>(slLoops.size());
        slLoopToSegmentMap = new HashMap<>();
        
        for (SelfHyperLoop slLoop : slLoops) {
            HyperEdgeSegment segment = new HyperEdgeSegment(null);
            hyperEdgeSegments.add(segment);
            slLoopToSegmentMap.put(slLoop, segment);
        }
        
        // To be able to quickly count crossings later, we remember for each loop whether it's active at a given port
        // ID or not
        computeLoopActivity(slHolder);
        
        // For each pair of hyper loops, determine the crossings for one segment routed above the other and vice versa
        for (int firstIdx = 0; firstIdx < slLoops.size() - 1; firstIdx++) {
            SelfHyperLoop slLoop1 = slHolder.getSLHyperLoops().get(firstIdx);
            for (int secondIdx = firstIdx + 1; secondIdx < slLoops.size(); secondIdx++) {
                createDependencies(slLoop1, slHolder.getSLHyperLoops().get(secondIdx));
            }
        }
    }

    /**
     * Initializes {@link #slLoopActivityOverPorts}. Each loop is mapped to an array indexed by port indices indicating
     * whether the loop runs along the given port or not.
     */
    private void computeLoopActivity(final SelfLoopHolder slHolder) {
        List<SelfHyperLoop> slLoops = slHolder.getSLHyperLoops();
        List<LPort> lPorts = slHolder.getLNode().getPorts();
        
        for (SelfHyperLoop slLoop : slLoops) {
            boolean[] loopActivity = new boolean[lPorts.size()];
            slLoopActivityOverPorts.put(slLoop, loopActivity);
            
            // Run from the loop's start port to the end port, possibly wrapping around, and set everything to true
            // along the way
            int lPortIdx = slLoop.getLeftmostPort().getLPort().id;
            int lPortTargetIdx = slLoop.getRightmostPort().getLPort().id;
            do {
                loopActivity[lPortIdx] = true;
                lPortIdx = (lPortIdx + 1) % lPorts.size();
            } while (lPortIdx != lPortTargetIdx);
        }
    }

    /**
     * Creates the necessary dependencies between the {@link SelfHyperLoop}s with the given indices. The lists must have
     * a one-to-one correspondence between loops and segments, that is, the i-th loop must be represented by the i-th
     * segment. The array says at which ports each loop is active.
     */
    private void createDependencies(final SelfHyperLoop slLoop1, final SelfHyperLoop slLoop2) {
        // Count the numbers of crossings that would ensue if placing to first segment above the second segment and
        // vice versa
        int firstAboveSecondCrossings = countCrossings(slLoop1, slLoop2);
        int secondAboveFirstCrossings = countCrossings(slLoop2, slLoop1);
        
        // Create dependencies
        HyperEdgeSegment segment1 = slLoopToSegmentMap.get(slLoop1);
        HyperEdgeSegment segment2 = slLoopToSegmentMap.get(slLoop2);
        
        if (firstAboveSecondCrossings < secondAboveFirstCrossings) {
            // The first loop should be above the second loop
            new SegmentDependency(segment1, segment2, secondAboveFirstCrossings - firstAboveSecondCrossings);
            
        } else if (secondAboveFirstCrossings < firstAboveSecondCrossings) {
            // The second loop should be above the first loop
            new SegmentDependency(segment2, segment1, firstAboveSecondCrossings - secondAboveFirstCrossings);
            
        } else {
            // Doesn't matter, let the cycle breaker decide
            new SegmentDependency(segment1, segment2, 0);
            new SegmentDependency(segment2, segment1, 0);
        }
    }

    /**
     * Counts the number of crossings that result between a loop routed through a higher and a loop routed through a
     * lower routing slot. The crossings will happen where the upper loop connects to ports within the routing area
     * of the lower loop. That is, for each of the upper loop's ports, if the lower loop is active there, this will
     * cause a crossing.
     */
    private int countCrossings(final SelfHyperLoop slUpperLoop, final SelfHyperLoop slLowerLoop) {
        boolean[] lowerLoopActivity = slLoopActivityOverPorts.get(slLowerLoop);
        int crossings = 0;
        
        for (SelfLoopPort slPort : slUpperLoop.getSLPorts()) {
            if (lowerLoopActivity[slPort.getLPort().id]) {
                crossings++;
            }
        }
        
        return crossings;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Slot Assignment

    /**
     * Based on the acyclic crossing graph, assign routing slots to all combinations of self loops and port sides.
     */
    private void doAssignRoutingSlots(final SelfLoopHolder slHolder) {
        // We first compute raw slots
        assignRawRoutingSlotsToSegments();
        
        // Simply assigning those raw slots to the loop has the effect of loops being routed through the same slots on
        // all sides, without paying regard to the fact that on some sides, some loops might occupy lower slots since
        // they are free there. What we're going to do, therefore, is to first apply the raw routing slots to all self
        // loops and then shift them towards the node on each side, if possible
        assignRawRoutingSlotsToLoops(slHolder);
        shiftTowardsNode(slHolder);
    }

    /**
     * This will assign routing slots to the segments as if the four port sides were completely dependent. However,
     * they are not. If the northern port side has 6 routing slots, that doesn't mean the eastern side will need 6 as
     * well.
     */
    private void assignRawRoutingSlotsToSegments() {
        Queue<HyperEdgeSegment> sinks = new LinkedList<>();
        
        // Fill our queue of sinks, which will ultimately end up being routed nearest to the node. While we go through
        // these, we also reset the in- and out weights to the number of incoming and outgoing dependencies
        for (HyperEdgeSegment segment : hyperEdgeSegments) {
            segment.setInWeight(segment.getIncomingDependencies().size());
            segment.setOutWeight(segment.getOutgoingDependencies().size());
            
            if (segment.getOutWeight() == 0) {
                segment.setRoutingSlot(0);
                sinks.add(segment);
            }
        }
        
        // Assign raw routing slots!
        while (!sinks.isEmpty()) {
            HyperEdgeSegment segment = sinks.poll();
            int nextRoutingSlot = segment.getRoutingSlot() + 1;
            
            for (SegmentDependency inDependency : segment.getIncomingDependencies()) {
                HyperEdgeSegment sourceSegment = inDependency.getSource();
                sourceSegment.setRoutingSlot(Math.max(sourceSegment.getRoutingSlot(), nextRoutingSlot));
                
                sourceSegment.setOutWeight(sourceSegment.getOutWeight() - 1);
                if (sourceSegment.getOutWeight() == 0) {
                    sinks.add(sourceSegment);
                }
            }
        }
    }

    /**
     * Assigns the computed raw routing slots to each loop on each side the loop occupies.
     */
    private void assignRawRoutingSlotsToLoops(final SelfLoopHolder slHolder) {
        for (SelfHyperLoop slLoop : slHolder.getSLHyperLoops()) {
            int slot = slLoopToSegmentMap.get(slLoop).getRoutingSlot();
            for (PortSide portSide : slLoop.getOccupiedPortSides()) {
                slLoop.setRoutingSlot(portSide, slot);
            }
        }
    }

    /**
     * Moves the self loops towards the node on each of the node's sides to avoid empty routing slots.
     */
    private void shiftTowardsNode(final SelfLoopHolder slHolder) {
        // For each port, this array specifies the next routing slot we can assign a self loop on that side to
        int[] nextFreeRoutingSlotAtPort = new int[slHolder.getLNode().getPorts().size()];
        
        shiftTowardsNodeOnSide(slHolder, PortSide.NORTH, nextFreeRoutingSlotAtPort);
        shiftTowardsNodeOnSide(slHolder, PortSide.EAST, nextFreeRoutingSlotAtPort);
        shiftTowardsNodeOnSide(slHolder, PortSide.SOUTH, nextFreeRoutingSlotAtPort);
        shiftTowardsNodeOnSide(slHolder, PortSide.WEST, nextFreeRoutingSlotAtPort);
    }

    private void shiftTowardsNodeOnSide(final SelfLoopHolder slHolder, final PortSide side,
            final int[] nextFreeRoutingSlotAtPort) {
        
        // We will iterate over the self loops that occupy that port side, sorted ascendingly by routing slot
        List<SelfHyperLoop> slLoops = slHolder.getSLHyperLoops().stream()
                .filter(slLoop -> slLoop.getOccupiedPortSides().contains(side))
                .sorted((sl1, sl2) -> Integer.compare(sl1.getRoutingSlot(side), sl2.getRoutingSlot(side)))
                .collect(Collectors.toList());
        
        // Find the indices of the first and last regular port on the port side
        int minLPortIndex = Integer.MAX_VALUE;
        int maxLPortIndex = Integer.MIN_VALUE;
        for (LPort lPort : slHolder.getLNode().getPorts()) {
            if (lPort.getSide() == side) {
                minLPortIndex = Math.min(minLPortIndex, lPort.id);
                maxLPortIndex = Math.max(maxLPortIndex, lPort.id);
            }
        }
        
        if (minLPortIndex == Integer.MAX_VALUE) {
            // There are no ports on this side, so we simply assign the loops to consecutive slots starting with 0
            for (int i = 0; i < slLoops.size(); i++) {
                slLoops.get(i).setRoutingSlot(side, i);
            }
        
        } else {
            // There are ports on this side. Find the lowest free slot across all slots spanned by each loop over all of
            // the ports spanned by the loop there
            for (SelfHyperLoop slLoop : slLoops) {
                boolean[] activeAtPort = slLoopActivityOverPorts.get(slLoop);
                int lowestAvailableSlot = 0;
                
                for (int portIndex = minLPortIndex; portIndex <= maxLPortIndex; portIndex++) {
                    if (activeAtPort[portIndex]) {
                        lowestAvailableSlot = Math.max(lowestAvailableSlot, nextFreeRoutingSlotAtPort[portIndex]);
                    }
                }
                
                // Assign the loop to that routing slot and update out routing slot array
                slLoop.setRoutingSlot(side, lowestAvailableSlot);
                for (int portIndex = minLPortIndex; portIndex <= maxLPortIndex; portIndex++) {
                    if (activeAtPort[portIndex]) {
                        nextFreeRoutingSlotAtPort[portIndex] = lowestAvailableSlot + 1;
                    }
                }
            }
        }
    }

}
