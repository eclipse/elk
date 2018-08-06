/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.routing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNodeSide;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopOpposingSegment;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopRoutingDirection;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopType;
import org.eclipse.elk.alg.layered.p5edges.loops.util.SelfLoopBendpointCalculationUtil;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Iterables;

/**
 * Abstract base class for self loop routers. Subclasses only need to implement the abstract {@code routeXSelfLoop}
 * methods.
 */
public abstract class AbstractSelfLoopRouter implements ISelfLoopRouter {

    private static final double DISTANCE = 10.0;
    private static final double ANCHOR_HEIGHT = 5.0;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Self Loop Routing
    
    @Override
    public void routeSelfLoop(final SelfLoopEdge slEdge, final SelfLoopNode slNode) {
        // Establish the self loop type and delegate to one of our specialized abstract methods
        SelfLoopType type = SelfLoopType.getEdgeType(slEdge, slNode);
        
        switch (type) {
        case SIDE:
            routeSideSelfLoop(slEdge);
            break;
        case CORNER:
            routeCornerSelfLoop(slEdge);
            break;
        case OPPOSING:
            routeOpposingSelfLoop(slEdge);
            break;
        case THREE_CORNER:
            routeThreeCornerSelfLoop(slEdge);
            break;
        case FOUR_CORNER:
            routeFourCornerSelfLoop(slEdge);
            break;
        }
    }

    /**
     * Routes a side self loop.
     */
    protected abstract void routeSideSelfLoop(SelfLoopEdge slEdge);

    /**
     * Routes a corner self loop.
     */
    protected abstract void routeCornerSelfLoop(SelfLoopEdge slEdge);

    /**
     * Routes an opposing-sides self loop.
     */
    protected abstract void routeOpposingSelfLoop(SelfLoopEdge slEdge);

    /**
     * Routes a three-corner self loop.
     */
    protected abstract void routeThreeCornerSelfLoop(SelfLoopEdge slEdge);

    /**
     * Routes a four-corner self loop.
     */
    protected abstract void routeFourCornerSelfLoop(SelfLoopEdge slEdge);
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Bend Point Calculation Helper Methods

    /**
     * Calculate a bend point for the given edge's source. This will be the first bend point on the path the edge
     * will take from its source to its target.
     */
    protected KVector computeSourceBendPoint(final SelfLoopEdge slEdge) {
        return computeSourceBendPoint(slEdge, true);
    }

    /**
     * Calculate a bend point for the given edge's source. This will be the first bend point on the path the edge
     * will take from its source to its target.
     */
    protected KVector computeSourceBendPoint(final SelfLoopEdge slEdge, final boolean supportsHyperEdges) {
        LEdge lEdge = slEdge.getEdge();
        final PortSide routingSide = lEdge.getSource().getSide();
        final double direction = SplinesMath.portSideToDirection(routingSide);

        final LPort sourceLPort = lEdge.getSource();
        final KVector sourcePos = sourceLPort.getPosition().clone().add(lEdge.getSource().getAnchor());

        SelfLoopPort sourcePort = slEdge.getSource();
        int sourceLevel = sourcePort.getEdgeLevel(lEdge);
        
        if (!supportsHyperEdges) {
            int order = slEdge.getEdgeOrders().get(routingSide);
            int connectedEdges = Iterables.size(sourcePort.getConnectedEdges());
            sourceLevel += -connectedEdges + order;
        }

        final double otherEdgeOffset = slEdge.getSource().getOtherEdgeOffset();

        // calculate the actual bend point
        KVector bendpoint = sourcePos.clone().add(new KVector(direction).scale(sourceLevel * DISTANCE));
        bendpoint.add(new KVector(direction).scale(otherEdgeOffset));
        return bendpoint;
    }

    /**
     * Calculate a bend point for the given edge's target. This will be the last bend point on the path the edge
     * will take from its source to its target.
     */
    protected KVector computeTargetBendPoint(final SelfLoopEdge slEdge) {
        return computeTargetBendPoint(slEdge, true);
    }

    /**
     * Calculate a bend point for the given edge's target. This will be the last bend point on the path the edge
     * will take from its source to its target.
     */
    protected KVector computeTargetBendPoint(final SelfLoopEdge slEdge, final boolean supportsHyperEdges) {
        LEdge lEdge = slEdge.getEdge();
        PortSide routingSide = lEdge.getTarget().getSide();
        final double direction = SplinesMath.portSideToDirection(routingSide);

        final LPort targetLPort = lEdge.getTarget();
        final KVector targetPos = targetLPort.getPosition().clone().add(lEdge.getTarget().getAnchor());

        SelfLoopPort targetPort = slEdge.getTarget();
        int targetLevel = targetPort.getEdgeLevel(lEdge);
        
        if (!supportsHyperEdges) {
            int order = slEdge.getEdgeOrders().get(routingSide);
            int connectedEdges = Iterables.size(targetPort.getConnectedEdges());
            targetLevel += -connectedEdges + order;
        }

        final double otherEdgeOffset = slEdge.getTarget().getOtherEdgeOffset();

        // calculate the actual bend point
        KVector bendpoint = targetPos.clone().add(new KVector(direction).scale(targetLevel * DISTANCE));
        bendpoint.add(new KVector(direction).scale(otherEdgeOffset));
        return bendpoint;
    }

    /**
     * Calculate the corner bend point between two points.
     * 
     * @param sourceBendPoint
     *            the bend point near the source port.
     * @param targetBendPoint
     *            the bend point near the target port.
     * @param targetSide
     *            the side of the node the second bend point is on.
     */
    protected static KVector computeSingleCornerBendPoint(final KVector sourceBendPoint, final KVector targetBendPoint,
            final PortSide targetSide) {
        
        final KVector cornerBendPoint = new KVector();

        switch (targetSide) {
        case NORTH:
        case SOUTH:
            cornerBendPoint.x = sourceBendPoint.x;
            cornerBendPoint.y = targetBendPoint.y;
            break;
            
        case EAST:
        case WEST:
            cornerBendPoint.x = targetBendPoint.x;
            cornerBendPoint.y = sourceBendPoint.y;
            break;
        }
        
        return cornerBendPoint;
    }

    /**
     * Generates all necessary bend points between the first and last as necessary to route the self loop around its
     * node. The first and last bend points have already been computed.
     * 
     * @param slNode
     *            the self loop node we're routing around.
     * @param slEdge
     *            the actual self loop we're routing.
     * @param sourceBendPoint
     *            the bend point right after leaving the source.
     * @param targetBendPoint
     *            the bend point right before entering the target.
     */
    protected List<KVector> computeCornerBendpoints(final SelfLoopNode slNode, final SelfLoopEdge slEdge,
            final KVector sourceBendPoint, final KVector targetBendPoint) {
        
        List<KVector> cornerBendpoints = new ArrayList<KVector>();
        KVector previousBendPoint = sourceBendPoint.clone();
        
        SelfLoopPort source = slEdge.getSource();
        PortSide sourceSide = source.getPortSide();
        
        SelfLoopPort target = slEdge.getTarget();
        PortSide targetSide = target.getPortSide();

        SelfLoopRoutingDirection routingDirection = source.getDirection();
        if (source.getDirection() == SelfLoopRoutingDirection.BOTH) {
            routingDirection = target.getDirection() == SelfLoopRoutingDirection.LEFT ? SelfLoopRoutingDirection.RIGHT
                    : SelfLoopRoutingDirection.LEFT;
        }
        PortSide nextside = routingDirection == SelfLoopRoutingDirection.LEFT ? sourceSide.left() : sourceSide.right();
        SelfLoopNodeSide nodeSide;

        // in case of a four corner loop the corner loop calculation should be done at least once
        do {
            nodeSide = slNode.getNodeSide(nextside);
            SelfLoopOpposingSegment segment = nodeSide.getOpposingSegments().get(slEdge);
            // if (segment != null) {
            double middlePadding = (DISTANCE * segment.getLevel()) + segment.getLabelOffset() + ANCHOR_HEIGHT;
            // calculate second and third control points
            LNode node = source.getLPort().getNode();
            KVector secondCP = SelfLoopBendpointCalculationUtil.calculateOpposingCornerBendPoint(previousBendPoint,
                    nextside, node.getSize(), middlePadding);

            cornerBendpoints.add(secondCP);
            previousBendPoint = secondCP;
            nextside = routingDirection == SelfLoopRoutingDirection.LEFT ? nextside.left() : nextside.right();
        } while (nextside != targetSide);

        final KVector secondCP = computeSingleCornerBendPoint(previousBendPoint, targetBendPoint, target.getPortSide());
        cornerBendpoints.add(secondCP);
        return cornerBendpoints;
    }
}
