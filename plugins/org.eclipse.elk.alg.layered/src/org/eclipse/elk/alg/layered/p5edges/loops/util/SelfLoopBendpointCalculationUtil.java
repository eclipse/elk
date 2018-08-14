/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNodeSide;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopOpposingSegment;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopRoutingDirection;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

/**
 * This utility class provides basics methods for the self-loop bend point calculation.
 */
public final class SelfLoopBendpointCalculationUtil {
    
    /**
     * Not intended for instantiation.
     */
    private SelfLoopBendpointCalculationUtil() {
    }

    /** The distance between adjacent self loop segment levels. */
    private static final double DISTANCE = 10.0;
    private static final double ANCHOR_HEIGHT = 5.0;
    private static final double CORNER_CUT = 2.5;

    /**
     * Generates a list of bend points for where a self loop goes around a corner.
     */
    public static List<KVector> generateCornerBendpoints(final SelfLoopNode nodeRep,
            final SelfLoopPort source, final SelfLoopPort target, final KVector firstBendpoint,
            final KVector secondBendpoint, final SelfLoopEdge edge) {

        List<KVector> cornerBendpoints = new ArrayList<KVector>();
        KVector previousBendPoint = firstBendpoint.clone();
        PortSide sourceSide = source.getPortSide();
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
            nodeSide = nodeRep.getNodeSide(nextside);
            SelfLoopOpposingSegment segment = nodeSide.getOpposingSegments().get(edge);
            double middlePadding = (DISTANCE * segment.getLevel()) + ANCHOR_HEIGHT;
            // calculate second and third control points
            LNode node = source.getLPort().getNode();
            KVector secondCP = SelfLoopBendpointCalculationUtil.calculateOpposingCornerBendPoint(previousBendPoint,
                    nextside, node.getSize(), middlePadding);

            cornerBendpoints.add(secondCP);
            previousBendPoint = secondCP;
            nextside = routingDirection == SelfLoopRoutingDirection.LEFT ? nextside.left() : nextside.right();
        } while (nextside != targetSide);

        final KVector secondCP = calculateCorner(previousBendPoint, secondBendpoint, target.getPortSide());
        cornerBendpoints.add(secondCP);

        return cornerBendpoints;
    }

    /**
     * Calculate the corner between two points.
     */
    private static KVector calculateCorner(final KVector firstBendPoint, final KVector secondBendPoint,
            final PortSide targetSide) {
        
        final KVector cornerBendPoint = new KVector();

        switch (targetSide) {
        case NORTH:
        case SOUTH:
            cornerBendPoint.x = firstBendPoint.x;
            cornerBendPoint.y = secondBendPoint.y;
            break;
        case EAST:
        case WEST:
            cornerBendPoint.x = secondBendPoint.x;
            cornerBendPoint.y = firstBendPoint.y;
            break;
        }
        return cornerBendPoint;
    }

    /**
     * Calculates a bend point at the corner between the two given (adjacent) port sides and the two bend points placed
     * on those sides. The result will be the bend point that needs to be inserted to connect the two existing bend
     * points orthogonally.
     */
    public static KVector calculateCornerBendPoint(final KVector firstBendpoint, final PortSide firstSide,
            final KVector secondBendpoint, final PortSide secondSide) {
        
        double cornerX = 0.0;
        double cornerY = 0.0;

        switch (firstSide) {
        case WEST:
            cornerX = firstBendpoint.x;
            break;
        case EAST:
            cornerX = firstBendpoint.x;
            break;
        case NORTH:
            cornerX = secondBendpoint.x;
            break;
        case SOUTH:
            cornerX = secondBendpoint.x;
            break;
        default:
            break;
        }
        
        switch (secondSide) {
        case WEST:
            cornerY = firstBendpoint.y;
            break;
        case EAST:
            cornerY = firstBendpoint.y;
            break;
        case NORTH:
            cornerY = secondBendpoint.y;
            break;
        case SOUTH:
            cornerY = secondBendpoint.y;
            break;
        default:
            break;
        }

        final KVector secondCP = new KVector(cornerX, cornerY);
        return secondCP;
    }

    public static KVector calculateOpposingCornerBendPoint(final KVector previousBendPoint, final PortSide opposingSide,
            final KVector nodeSize, final double opposingSideDistance) {
        
        final KVector secondCP = new KVector();
        switch (opposingSide) {
        case NORTH:
            secondCP.x = previousBendPoint.x;
            secondCP.y = -opposingSideDistance;
            break;
        case EAST:
            secondCP.x = opposingSideDistance + nodeSize.x;
            secondCP.y = previousBendPoint.y;
            break;
        case SOUTH:
            secondCP.x = previousBendPoint.x;
            secondCP.y = opposingSideDistance + nodeSize.y;
            break;
        case WEST:
            secondCP.x = -opposingSideDistance;
            secondCP.y = previousBendPoint.y;
            break;
        }
        return secondCP;

    }

    public static List<KVector> cutCornerBendPoints(final KVector firstBendpoint, final PortSide side,
            final SelfLoopRoutingDirection routingDirection) {
        
        List<KVector> cutCornerBendPoints = new ArrayList<KVector>();
        double direction = SplinesMath.portSideToDirection(side);
        KVector directionVector = new KVector(direction).scale(-1);
        KVector firstCutBendpoint = firstBendpoint.clone().add(directionVector.scale(CORNER_CUT));
        cutCornerBendPoints.add(firstCutBendpoint);

        PortSide nextSide = routingDirection == SelfLoopRoutingDirection.LEFT ? side.left() : side.right();
        double directionSecondCut = SplinesMath.portSideToDirection(nextSide);
        KVector directionVectorSecondCut = new KVector(directionSecondCut);
        KVector secondCutBendpoint = firstBendpoint.clone().add(directionVectorSecondCut.scale(CORNER_CUT));
        cutCornerBendPoints.add(secondCutBendpoint);

        return cutCornerBendPoints;
    }

}
