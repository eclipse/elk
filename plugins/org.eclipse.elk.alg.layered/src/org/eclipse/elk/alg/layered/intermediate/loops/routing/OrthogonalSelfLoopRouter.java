/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.loops.routing;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LMargin;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfHyperLoop;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfHyperLoopLabels;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfLoopEdge;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfLoopHolder;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.PortSide;

/**
 * Routes self loops orthogonally. The routing functionality is exposed to subclasses. If a subclass wants to base its
 * routing on the orthogonal routing, it can simply override {@link #modifyBendPoints(SelfLoopEdge, KVectorChain)} to
 * turn an orthogonal routing into some kind of a strange, exotic, exciting new routing style!
 */
public class OrthogonalSelfLoopRouter extends AbstractSelfLoopRouter {
    
    // TODO Replace by spacing options.
    private static final double DISTANCE = 10.0;
    private static final double LABEL_EDGE_DISTANCE = 5.0;

    @Override
    public void routeSelfLoops(final SelfLoopHolder slHolder) {
        KVector nodeSize = slHolder.getLNode().getSize();
        LMargin nodeMargins = slHolder.getLNode().getMargin();
        
        LMargin newNodeMargins = new LMargin();
        newNodeMargins.set(nodeMargins);
        
        // Compute how far away from the node each routing slot on each side is
        double[][] routingSlotPositions = computeRoutingSlotPositions(slHolder);
        
        for (SelfHyperLoop slLoop : slHolder.getSLHyperLoops()) {
            for (SelfLoopEdge slEdge : slLoop.getSLEdges()) {
                LEdge lEdge = slEdge.getLEdge();
                
                KVectorChain bendPoints = computeOrthogonalBendPoints(slEdge, routingSlotPositions);
                bendPoints = modifyBendPoints(slEdge, bendPoints);
                
                lEdge.getBendPoints().clear();
                lEdge.getBendPoints().addAll(bendPoints);
                
                bendPoints.stream().forEach(bp -> updateNewNodeMargins(nodeSize, newNodeMargins, bp));
            }
            
            // Place the self loop's labels
            SelfHyperLoopLabels slLabels = slLoop.getSLLabels();
            if (slLabels != null) {
                placeLabels(slLoop, slLabels, routingSlotPositions);
                updateNewNodeMargins(nodeSize, newNodeMargins, slLabels);
            }
            
        }
        
        // Update the node's margins to include the space required for self loops
        nodeMargins.set(newNodeMargins);
    }

    /**
     * Places any labels of the given self loop.
     */
    private void placeLabels(final SelfHyperLoop slLoop, final SelfHyperLoopLabels slLabels,
            final double[][] routingSlotPositions) {
        
        // Find the baseline of the routing slot (we need to offset this by the spacing to be left between label and
        // edge)
        PortSide labelSide = slLabels.getSide();
        double labelPosition = routingSlotPositions[labelSide.ordinal()][slLoop.getRoutingSlot(labelSide)];
        
        switch (labelSide) {
        case NORTH:
            labelPosition -= LABEL_EDGE_DISTANCE - slLabels.getSize().y;
            slLabels.getPosition().y = labelPosition;
            break;
            
        case SOUTH:
            labelPosition += LABEL_EDGE_DISTANCE;
            slLabels.getPosition().y = labelPosition;
            break;
            
        case WEST:
            labelPosition -= LABEL_EDGE_DISTANCE - slLabels.getSize().x;
            slLabels.getPosition().x = labelPosition;
            break;
            
        case EAST:
            labelPosition += LABEL_EDGE_DISTANCE;
            slLabels.getPosition().x = labelPosition;
            break;
        
        default:
            assert false;
        }
    }

    /**
     * Extends the node margins to include the given bend point.
     */
    private void updateNewNodeMargins(final KVector nodeSize, final LMargin newNodeMargins, final KVector bendPoint) {
        newNodeMargins.left = Math.max(newNodeMargins.left, -bendPoint.x);
        newNodeMargins.right = Math.max(newNodeMargins.right, bendPoint.x - nodeSize.x);
        
        newNodeMargins.top = Math.max(newNodeMargins.top, -bendPoint.y);
        newNodeMargins.bottom = Math.max(newNodeMargins.bottom, bendPoint.y - nodeSize.y);
    }
    
    /**
     * Ensures that self loop labels are fully covered by the node margins.
     */
    private void updateNewNodeMargins(final KVector nodeSize, final LMargin newNodeMargins,
            final SelfHyperLoopLabels slLabels) {
        
        KVector pos = new KVector(slLabels.getPosition());
        updateNewNodeMargins(nodeSize, newNodeMargins, pos);
        
        pos.add(slLabels.getSize());
        updateNewNodeMargins(nodeSize, newNodeMargins, pos);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Routing Slot Positions
    
    /**
     * Computes the position of each routing slot on each side, taking care to leave enough space for labels between
     * adjacent routing slots on the north and south sides.
     */
    private double[][] computeRoutingSlotPositions(final SelfLoopHolder slHolder) {
        // Initialize array
        double[][] positions = new double[PortSide.values().length][];
        for (PortSide portSide : PortSide.values()) {
            positions[portSide.ordinal()] = new double[slHolder.getRoutingSlotCount()[portSide.ordinal()]];
        }
        
        // To know how much space we need to leave between adjacent routing slots, we have to find the size of labels
        // first (for north and south sides, that is)
        initializeWithMaxLabelHeight(positions, slHolder, PortSide.NORTH);
        initializeWithMaxLabelHeight(positions, slHolder, PortSide.SOUTH);
        
        // Compute the positions for each side
        computePositions(positions, slHolder, PortSide.NORTH);
        computePositions(positions, slHolder, PortSide.EAST);
        computePositions(positions, slHolder, PortSide.SOUTH);
        computePositions(positions, slHolder, PortSide.WEST);
        
        return positions;
    }

    /**
     * Initializes the entry for each routing slot on the north or south sides with the maximum height of labels it
     * houses.
     */
    private void initializeWithMaxLabelHeight(final double[][] positions, final SelfLoopHolder slHolder,
            final PortSide portSide) {
        
        assert portSide == PortSide.NORTH || portSide == PortSide.SOUTH;
        
        double[] sidePositions = positions[portSide.ordinal()];
        
        for (SelfHyperLoop slLoop : slHolder.getSLHyperLoops()) {
            SelfHyperLoopLabels slLabels = slLoop.getSLLabels();
            if (slLabels != null && slLabels.getSide() == portSide) {
                int routingSlot = slLoop.getRoutingSlot(portSide);
                sidePositions[routingSlot] = Math.max(sidePositions[routingSlot], slLabels.getSize().y);
            }
        }
    }
    
    /**
     * Computes the positions for each routing slot, taking labels into account.
     */
    private void computePositions(final double[][] positions, final SelfLoopHolder slHolder, final PortSide portSide) {
        double currPos = computeBaselinePosition(slHolder, portSide);
        
        // For northern and western coordinates, we have to subtract from the current position
        double factor = portSide == PortSide.NORTH || portSide == PortSide.WEST ? -1 : 1;
        
        double[] sidePositions = positions[portSide.ordinal()];
        for (int slot = 0; slot < sidePositions.length; slot++) {
            // The slot entry currently contains the height or width of the largest label in that slot
            double largestLabelSize = sidePositions[slot];
            if (largestLabelSize > 0) {
                // Account for label spacing
                largestLabelSize += LABEL_EDGE_DISTANCE;
            }
            
            // Place the slot at the current position and advance the position
            sidePositions[slot] = currPos;
            currPos += factor * (largestLabelSize + DISTANCE);
        }
    }

    /**
     * Based on the margin area, this computes the offset from the node origin to add to escape the area occupied by
     * ports.
     */
    private double computeBaselinePosition(final SelfLoopHolder slHolder, final PortSide portSide) {
        LNode lNode = slHolder.getLNode();
        LMargin lMargins = lNode.getMargin();
        
        switch (portSide) {
        case NORTH:
            return -lMargins.top - DISTANCE;
        case EAST:
            return lNode.getSize().x + lMargins.right + DISTANCE;
        case SOUTH:
            return lNode.getSize().y + lMargins.bottom + DISTANCE;
        case WEST:
            return -lMargins.left - DISTANCE;
        default:
            assert false;
            return -1;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // High-Level Bend Point Computation

    /**
     * Computes the bend points necessary to route the given self loop edge orthogonally.
     */
    protected KVectorChain computeOrthogonalBendPoints(final SelfLoopEdge slEdge,
            final double[][] routingSlotPositions) {
        
        KVectorChain bendPoints = new KVectorChain();
        
        addOuterBendPoint(slEdge, slEdge.getSLSource(), routingSlotPositions, bendPoints);
        addCornerBendPoints(slEdge, routingSlotPositions, bendPoints);
        addOuterBendPoint(slEdge, slEdge.getSLTarget(), routingSlotPositions, bendPoints);
        
        return bendPoints;
    }
    
    /**
     * Allows subclasses to turn the given list of bend points into a new list of bend points.
     */
    protected KVectorChain modifyBendPoints(final SelfLoopEdge slEdge, final KVectorChain bendPoints) {
        return bendPoints;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actual Bend Point Computation

    private void addOuterBendPoint(final SelfLoopEdge slEdge, final SelfLoopPort slPort,
            final double[][] routingSlotPositions, final KVectorChain bendPoints) {
        
        SelfHyperLoop slLoop = slEdge.getSLHyperLoop();
        LPort lPort = slPort.getLPort();
        PortSide portSide = lPort.getSide();
        
        // We'll start by computing the coordinate of the level we're on
        KVector result = getBaseVector(portSide, slLoop.getRoutingSlot(portSide), routingSlotPositions);
//        KVector result = new KVector(SplinesMath.portSideToDirection(portSide))
//                .scale(routingSlotPositions[portSide.ordinal()][slLoop.getRoutingSlot(portSide)]);
        
        // Now take care of the port anchor
        KVector anchor = lPort.getPosition().clone().add(lPort.getAnchor());
        switch (lPort.getSide()) {
        case NORTH:
        case SOUTH:
            result.x += anchor.x;
            break;
            
        case EAST:
        case WEST:
            result.y += anchor.y;
            break;
            
        default:
            assert false;
        }
        
        bendPoints.add(result);
    }

    private void addCornerBendPoints(final SelfLoopEdge slEdge, final double[][] routingSlotPositions,
            final KVectorChain bendPoints) {
        
        // Check if we even need corner bend points
        LPort lSourcePort = slEdge.getSLSource().getLPort();
        LPort lTargetPort = slEdge.getSLTarget().getLPort();
        
        if (lSourcePort.getSide() == lTargetPort.getSide()) {
            return;
        }
        
        // Check whether we need to route clockwise or anticlockwise
        SelfHyperLoop slLoop = slEdge.getSLHyperLoop();
        
        PortSide currPortSide = lSourcePort.getSide();
        boolean clockwise = slLoop.getOccupiedPortSides().contains(currPortSide.right());
        
        // Compute corner points
        PortSide nextPortSide = null;
        
        while (currPortSide != lTargetPort.getSide()) {
            // Next port side depends on the direction we're going
            nextPortSide = clockwise ? currPortSide.right() : currPortSide.left();
            
            // Compute the coordinates contributes by the current and next port sides
//            KVector currPortSideComponent = new KVector(SplinesMath.portSideToDirection(currPortSide))
//                    .scale(routingSlotPositions[currPortSide.ordinal()][slLoop.getRoutingSlot(currPortSide)]);
//            KVector nextPortSideComponent = new KVector(SplinesMath.portSideToDirection(nextPortSide))
//                    .scale(routingSlotPositions[nextPortSide.ordinal()][slLoop.getRoutingSlot(nextPortSide)]);
            KVector currPortSideComponent = getBaseVector(currPortSide, slLoop.getRoutingSlot(currPortSide), routingSlotPositions);
            KVector nextPortSideComponent = getBaseVector(nextPortSide, slLoop.getRoutingSlot(nextPortSide), routingSlotPositions);
            
            // One has its x coordinate set, the other has its y coordinate set -- their sum is our final bend point
            bendPoints.add(currPortSideComponent.add(nextPortSideComponent));
            
            // Advance to next port side
            currPortSide = nextPortSide;
        }
    }
    
    private KVector getBaseVector(final PortSide portSide, final int routingSlot,
            final double[][] routingSlotPositions) {
        
        double position = routingSlotPositions[portSide.ordinal()][routingSlot];
        
        switch (portSide) {
        case NORTH:
        case SOUTH:
            return new KVector(0, position);
            
        case EAST:
        case WEST:
            return new KVector(position, 0);
            
        default:
            assert false;
            return null;
        }
    }

}
