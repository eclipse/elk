/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.loops.routing;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfHyperLoop;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfLoopEdge;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.PortSide;

/**
 * Routes self loops orthogonally. The routing functionality is exposed to subclasses for
 */
public class OrthogonalSelfLoopRouter extends AbstractSelfLoopRouter {
    
    private static final double DISTANCE = 10.0;

    @Override
    public void routeSelfLoop(final SelfHyperLoop slLoop) {
        for (SelfLoopEdge slEdge : slLoop.getSLEdges()) {
            LEdge lEdge = slEdge.getLEdge();
            
            lEdge.getBendPoints().clear();
            lEdge.getBendPoints().addAll(computeOrthogonalBendPoints(slEdge));
        }
    }
    
    /**
     * Computes the bend points necessary to route the given self loop edge orthogonally.
     */
    protected KVectorChain computeOrthogonalBendPoints(final SelfLoopEdge slEdge) {
        KVectorChain bendPoints = new KVectorChain();
        
        ElkMargin margins = computeMargins(slEdge.getSLHyperLoop().getSLHolder().getLNode());
        
        addOuterBendPoint(slEdge, slEdge.getSLSource(), margins, bendPoints);
        addCornerBendPoints(slEdge, margins, bendPoints);
        addOuterBendPoint(slEdge, slEdge.getSLTarget(), margins, bendPoints);
        
        return bendPoints;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actual Bend Point Computation

    private void addOuterBendPoint(final SelfLoopEdge slEdge, final SelfLoopPort slPort, final ElkMargin nodeMargins,
            final KVectorChain bendPoints) {
        
        SelfHyperLoop slLoop = slEdge.getSLHyperLoop();
        LPort lPort = slPort.getLPort();
        LNode lNode = lPort.getNode();
        
        PortSide portSide = lPort.getSide();
        
        // We'll start by computing the coordinate of the level we're on
        KVector result = new KVector(SplinesMath.portSideToDirection(portSide))
                .scale(slLoop.getRoutingSlot(portSide) * DISTANCE)
                .add(computeBaseline(portSide, lNode, nodeMargins));
        
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

    private void addCornerBendPoints(final SelfLoopEdge slEdge, final ElkMargin nodeMargins,
            final KVectorChain bendPoints) {
        
        // Check if we even need corner bend points
        LPort lSourcePort = slEdge.getSLSource().getLPort();
        LPort lTargetPort = slEdge.getSLTarget().getLPort();
        
        if (lSourcePort.getSide() == lTargetPort.getSide()) {
            return;
        }
        
        LNode lNode = lSourcePort.getNode();
        
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
            KVector currPortSideComponent = new KVector(SplinesMath.portSideToDirection(currPortSide))
                    .scale(slLoop.getRoutingSlot(currPortSide) * DISTANCE)
                    .add(computeBaseline(currPortSide, lNode, nodeMargins));
            KVector nextPortSideComponent = new KVector(SplinesMath.portSideToDirection(nextPortSide))
                    .scale(slLoop.getRoutingSlot(nextPortSide) * DISTANCE)
                    .add(computeBaseline(nextPortSide, lNode, nodeMargins));
            
            // One has its x coordinate set, the other has its y coordinate set -- their sum is our final bend point
            bendPoints.add(currPortSideComponent.add(nextPortSideComponent));
            
            // Advance to next port side
            currPortSide = nextPortSide;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Proper Level Computation
    
    /**
     * Computes the margin area around the node blocked by ports. This then becomes the baseline for computing the self
     * loop levels.
     */
    private ElkMargin computeMargins(final LNode lNode) {
        ElkMargin margin = new ElkMargin();
        
        for (LPort lPort : lNode.getPorts()) {
            switch (lPort.getSide()) {
            case NORTH:
                margin.top = Math.max(margin.top, -lPort.getPosition().y);
                break;
                
            case EAST:
                margin.right = Math.max(margin.right, lPort.getPosition().x - lNode.getSize().x);
                break;
                
            case SOUTH:
                margin.bottom = Math.max(margin.bottom, lPort.getPosition().y - lNode.getSize().y);
                break;
                
            case WEST:
                margin.left = Math.max(margin.left, -lPort.getPosition().x);
                break;
                
            default:
                assert false;
            }
        }
        
        return margin;
    }

    /**
     * Based on the margin area, this computes the offset from the node origin to add to escape the area occupied by
     * ports.
     */
    private KVector computeBaseline(final PortSide portSide, final LNode lNode, final ElkMargin nodeMargins) {
        switch (portSide) {
        case NORTH:
            return new KVector(0, -nodeMargins.top);
        case EAST:
            return new KVector(lNode.getSize().x + nodeMargins.right, 0);
        case SOUTH:
            return new KVector(0, lNode.getSize().y + nodeMargins.bottom);
        case WEST:
            return new KVector(-nodeMargins.left, 0);
        default:
            assert false;
            return null;
        }
    }

}
