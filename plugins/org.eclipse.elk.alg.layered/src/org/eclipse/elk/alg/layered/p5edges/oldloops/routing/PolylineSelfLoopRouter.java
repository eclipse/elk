/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopRoutingDirection;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.PortSide;

/**
 * A self loop router that routes edges in the polyline edge routing style.
 */
public class PolylineSelfLoopRouter extends AbstractSelfLoopRouter {

    private static final double DISTANCE = 10.0;

    @Override
    public void routeSideSelfLoop(final SelfLoopEdge slEdge) {
        LEdge lEdge = slEdge.getEdge();
        SelfLoopPort source = slEdge.getSource();
        SelfLoopPort target = slEdge.getTarget();

        // Calculate the basic bend points
        KVector sourceBendPoint = computeSourceBendPoint(slEdge);
        KVector targetBendPoint = computeTargetBendPoint(slEdge);

        // Cut the corners
        List<KVector> sourceBendPoints = cutCornerBendPoints(sourceBendPoint, source,
                source.getMaximumLevel() * DISTANCE, targetBendPoint.distance(sourceBendPoint));

        List<KVector> targetBendPoints = cutCornerBendPoints(targetBendPoint, target,
                sourceBendPoint.distance(targetBendPoint), target.getMaximumLevel() * DISTANCE);
        Collections.reverse(targetBendPoints);
        
        // Apply bend points
        KVectorChain bendPoints = lEdge.getBendPoints();
        
        bendPoints.addAll(sourceBendPoints);
        bendPoints.addAll(targetBendPoints);
    }

    @Override
    public void routeCornerSelfLoop(final SelfLoopEdge slEdge) {
        LEdge lEdge = slEdge.getEdge();
        final SelfLoopPort source = slEdge.getSource();
        final SelfLoopPort target = slEdge.getTarget();

        // Calculate the basic bend points
        KVector sourceBendPoint = computeSourceBendPoint(slEdge);
        KVector targetBendPoint = computeTargetBendPoint(slEdge);
        
        KVector cornerBendPoint = computeSingleCornerBendPoint(sourceBendPoint, targetBendPoint, target.getPortSide());
        
        // Cut the corners
        List<KVector> sourceBendPoints = cutCornerBendPoints(sourceBendPoint, source,
                source.getMaximumLevel() * DISTANCE, targetBendPoint.distance(sourceBendPoint));

        List<KVector> cornerBendPoints = cutCornerBendPoints(cornerBendPoint, target.getPortSide(),
                source.getDirection(), targetBendPoint.distance(sourceBendPoint),
                targetBendPoint.distance(sourceBendPoint), source.getMaximumLevel());

        List<KVector> targetBendPoints = cutCornerBendPoints(targetBendPoint, target,
                sourceBendPoint.distance(targetBendPoint), target.getMaximumLevel() * DISTANCE);
        Collections.reverse(targetBendPoints);
        
        // Apply bend points
        KVectorChain bendPoints = lEdge.getBendPoints();
        
        bendPoints.addAll(sourceBendPoints);
        bendPoints.addAll(cornerBendPoints);
        bendPoints.addAll(targetBendPoints);
    }

    @Override
    public void routeOpposingSelfLoop(final SelfLoopEdge slEdge) {
        LEdge lEdge = slEdge.getEdge();
        final SelfLoopPort source = slEdge.getSource();
        final SelfLoopPort target = slEdge.getTarget();
        
        SelfLoopNode slNode = lEdge.getSource().getNode().getProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION);
        SelfLoopRoutingDirection routingDirection = computeRoutingDirection(source, target);

        // Calculate the basic bend points
        KVector sourceBendPoint = computeSourceBendPoint(slEdge);
        KVector targetBendPoint = computeTargetBendPoint(slEdge);

        List<KVector> cornerBendPoints = computeCornerBendpoints(slNode, slEdge, sourceBendPoint, targetBendPoint);
        KVector firstCorner = cornerBendPoints.get(0);
        KVector secondCorner = cornerBendPoints.get(1);

        // Cut the corners
        List<KVector> sourceBendPoints = cutCornerBendPoints(sourceBendPoint, source,
                source.getMaximumLevel() * DISTANCE, sourceBendPoint.distance(firstCorner));

        PortSide nextSide = routingDirection == SelfLoopRoutingDirection.LEFT
                ? source.getPortSide().left()
                : source.getPortSide().right();
        List<KVector> firstCornerBendPoints = cutCornerBendPoints(firstCorner, nextSide, routingDirection,
                sourceBendPoint.distance(firstCorner), secondCorner.distance(firstCorner), source.getMaximumLevel());

        nextSide = routingDirection == SelfLoopRoutingDirection.LEFT
                ? nextSide.left()
                : nextSide.right();
        List<KVector> secondCornerBendPoints = cutCornerBendPoints(secondCorner, nextSide, routingDirection,
                secondCorner.distance(firstCorner), targetBendPoint.distance(secondCorner), target.getMaximumLevel());

        List<KVector> targetBendPoints = cutCornerBendPoints(targetBendPoint, target,
                targetBendPoint.distance(secondCorner), target.getMaximumLevel() * DISTANCE);
        Collections.reverse(targetBendPoints);
        
        // Apply bend points
        KVectorChain bendPoints = lEdge.getBendPoints();
        
        bendPoints.addAll(sourceBendPoints);
        bendPoints.addAll(firstCornerBendPoints);
        bendPoints.addAll(secondCornerBendPoints);
        bendPoints.addAll(targetBendPoints);
    }

    @Override
    public void routeThreeCornerSelfLoop(final SelfLoopEdge slEdge) {
        LEdge loop = slEdge.getEdge();
        SelfLoopPort source = slEdge.getSource();
        SelfLoopPort target = slEdge.getTarget();
        
        SelfLoopNode slNode = loop.getSource().getNode().getProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION);
        SelfLoopRoutingDirection routingDirection = computeRoutingDirection(source, target);

        // Calculate the basic bend points
        KVector sourceBendPoint = computeSourceBendPoint(slEdge);
        KVector targetBendPoint = computeTargetBendPoint(slEdge);

        List<KVector> cornerBendPoints = computeCornerBendpoints(slNode, slEdge, sourceBendPoint, targetBendPoint);
        KVector firstCornerBendPoint = cornerBendPoints.get(0);
        KVector secondCornerBendPoint = cornerBendPoints.get(1);
        KVector thirdCornerBendPoint = cornerBendPoints.get(2);

        // Cut the corners
        List<KVector> sourceBendPoints = cutCornerBendPoints(sourceBendPoint, source,
                source.getMaximumLevel() * DISTANCE, sourceBendPoint.distance(firstCornerBendPoint));
        
        PortSide nextSide = routingDirection == SelfLoopRoutingDirection.LEFT
                ? source.getPortSide().left()
                : source.getPortSide().right();
        List<KVector> firstCornerBendPoints = cutCornerBendPoints(firstCornerBendPoint, nextSide, routingDirection,
                sourceBendPoint.distance(firstCornerBendPoint),
                secondCornerBendPoint.distance(firstCornerBendPoint), source.getMaximumLevel());

        nextSide = routingDirection == SelfLoopRoutingDirection.LEFT
                ? nextSide.left()
                : nextSide.right();
        List<KVector> secondCornerBendPoints = cutCornerBendPoints(secondCornerBendPoint, nextSide, routingDirection,
                secondCornerBendPoint.distance(firstCornerBendPoint),
                thirdCornerBendPoint.distance(secondCornerBendPoint), target.getMaximumLevel());

        nextSide = routingDirection == SelfLoopRoutingDirection.LEFT
                ? nextSide.left()
                : nextSide.right();
        List<KVector> thirdCornerBendPoints = cutCornerBendPoints(thirdCornerBendPoint, nextSide, routingDirection,
                secondCornerBendPoint.distance(thirdCornerBendPoint), targetBendPoint.distance(thirdCornerBendPoint),
                target.getMaximumLevel());

        List<KVector> targetBendPoints = cutCornerBendPoints(targetBendPoint, target,
                targetBendPoint.distance(secondCornerBendPoint), target.getMaximumLevel() * DISTANCE);
        Collections.reverse(targetBendPoints);

        // Apply bend points
        KVectorChain bendPoints = loop.getBendPoints();
        
        bendPoints.addAll(sourceBendPoints);
        bendPoints.addAll(firstCornerBendPoints);
        bendPoints.addAll(secondCornerBendPoints);
        bendPoints.addAll(thirdCornerBendPoints);
        bendPoints.addAll(targetBendPoints);
    }

    @Override
    public void routeFourCornerSelfLoop(final SelfLoopEdge slEdge) {
        LEdge lEdge = slEdge.getEdge();
        final SelfLoopPort source = slEdge.getSource();
        final SelfLoopPort target = slEdge.getTarget();
        
        SelfLoopNode slNode = lEdge.getSource().getNode().getProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION);
        SelfLoopRoutingDirection routingDirection = computeRoutingDirection(source, target);

        // Calculate the basic bend points
        KVector sourceBendPoint = computeSourceBendPoint(slEdge);
        KVector targetBendPoint = computeTargetBendPoint(slEdge);
        
        List<KVector> cornerBendPoints = computeCornerBendpoints(slNode, slEdge, sourceBendPoint, targetBendPoint);
        KVector firstCornerBendPoint = cornerBendPoints.get(0);
        KVector secondCornerBendPoint = cornerBendPoints.get(1);
        KVector thirdCornerBendPoint = cornerBendPoints.get(2);
        KVector fourthCornerBendPoint = cornerBendPoints.get(slNode.getSides().size() - 1);

        // Cut the corners
        List<KVector> sourceBendPoints = cutCornerBendPoints(sourceBendPoint, source,
                source.getMaximumLevel() * DISTANCE, sourceBendPoint.distance(firstCornerBendPoint));

        PortSide nextSide = routingDirection == SelfLoopRoutingDirection.LEFT
                ? source.getPortSide().left()
                : source.getPortSide().right();
        List<KVector> firstCornerBendPoints = cutCornerBendPoints(firstCornerBendPoint, nextSide, routingDirection,
                sourceBendPoint.distance(firstCornerBendPoint),
                secondCornerBendPoint.distance(firstCornerBendPoint), source.getMaximumLevel());

        nextSide = routingDirection == SelfLoopRoutingDirection.LEFT
                ? nextSide.left()
                : nextSide.right();
        List<KVector> secondCornerBendPoints = cutCornerBendPoints(secondCornerBendPoint, nextSide, routingDirection,
                secondCornerBendPoint.distance(firstCornerBendPoint),
                thirdCornerBendPoint.distance(secondCornerBendPoint), target.getMaximumLevel());

        nextSide = routingDirection == SelfLoopRoutingDirection.LEFT
                ? nextSide.left()
                : nextSide.right();
        List<KVector> thirdCornerBendPoints = cutCornerBendPoints(thirdCornerBendPoint, nextSide, routingDirection,
                secondCornerBendPoint.distance(thirdCornerBendPoint),
                fourthCornerBendPoint.distance(thirdCornerBendPoint), target.getMaximumLevel());

        nextSide = routingDirection == SelfLoopRoutingDirection.LEFT
                ? nextSide.left()
                : nextSide.right();
        List<KVector> fourthCornerBendPoints = cutCornerBendPoints(fourthCornerBendPoint, nextSide, routingDirection,
                fourthCornerBendPoint.distance(thirdCornerBendPoint), targetBendPoint.distance(fourthCornerBendPoint),
                target.getMaximumLevel());

        List<KVector> targetBendPoints = cutCornerBendPoints(targetBendPoint, target,
                targetBendPoint.distance(secondCornerBendPoint), target.getMaximumLevel() * DISTANCE);
        Collections.reverse(targetBendPoints);

        // Apply bend points
        KVectorChain bendPoints = lEdge.getBendPoints();
        
        bendPoints.addAll(sourceBendPoints);
        bendPoints.addAll(firstCornerBendPoints);
        bendPoints.addAll(secondCornerBendPoints);
        bendPoints.addAll(thirdCornerBendPoints);
        bendPoints.addAll(fourthCornerBendPoints);
        bendPoints.addAll(targetBendPoints);
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utilities
    
    private static final double CORNER_CUT = 2.5;
    private static final double HALF = 0.5;
    private static final double QUARTER = 0.25;
    
    /**
     * Calls {@link #cutCornerBendPoints(KVector, PortSide, SelfLoopRoutingDirection, double, double, int)} by deriving
     * the port side, direction, and maximum level from the properties of the given port.
     */
    private List<KVector> cutCornerBendPoints(final KVector bendPoint, final SelfLoopPort slPort,
            final double distanceToPreviousPoint, final double distanceToNextPoint) {
        
        return cutCornerBendPoints(bendPoint, slPort.getPortSide(), slPort.getDirection(), distanceToPreviousPoint,
                distanceToNextPoint, slPort.getMaximumLevel());
    }

    /**
     * Turns the given bend point into two bend points placed such that they cut the corner.
     */
    private List<KVector> cutCornerBendPoints(final KVector bendPoint, final PortSide side,
            final SelfLoopRoutingDirection routingDirection, final double distanceToPreviousPoint,
            final double distanceToNextPoint, final int level) {
        
        List<KVector> result = new ArrayList<KVector>();
        
        double shortestDistance = Math.min(distanceToPreviousPoint, distanceToNextPoint);
        double distanceValue = CORNER_CUT * level > HALF * shortestDistance
                ? QUARTER * shortestDistance
                : CORNER_CUT * level;
        
        // First cut bend point
        double direction = SplinesMath.portSideToDirection(side);
        KVector directionVector = new KVector(direction).scale(-1);
        KVector firstCutBendpoint = bendPoint.clone().add(directionVector.scale(distanceValue));
        result.add(firstCutBendpoint);

        PortSide nextSide = routingDirection == SelfLoopRoutingDirection.LEFT
                ? side.left()
                : side.right();
        
        // Second cut bend point
        direction = SplinesMath.portSideToDirection(nextSide);
        directionVector = new KVector(direction);
        KVector secondCutBendpoint = bendPoint.clone().add(directionVector.scale(distanceValue));
        result.add(secondCutBendpoint);

        return result;
    }

    /**
     * Returns the source port's routing direction or, if that is {@link SelfLoopRoutingDirection#BOTH}, a routing
     * direction derived from the target port.
     */
    private SelfLoopRoutingDirection computeRoutingDirection(final SelfLoopPort source, final SelfLoopPort target) {
        SelfLoopRoutingDirection result = source.getDirection();
        
        if (result == SelfLoopRoutingDirection.BOTH) {
            result = target.getDirection() == SelfLoopRoutingDirection.LEFT
                    ? SelfLoopRoutingDirection.RIGHT
                    : SelfLoopRoutingDirection.LEFT;
        }
        
        return result;
    }
    
}
