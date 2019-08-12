/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.routing;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.splines.NubsSelfLoop;
import org.eclipse.elk.core.math.KVector;

import com.google.common.collect.Iterables;

/**
 * A self loop router that routes edges in the spline edge routing style.
 */
public class SplineSelfLoopRouter extends AbstractSelfLoopRouter {

    private static final double DISTANCE = 10.0;

    @Override
    public void routeSideSelfLoop(final OldSelfLoopEdge slEdge) {
        LEdge lEdge = slEdge.getEdge();
        OldSelfLoopPort source = slEdge.getSource();
        LPort sourceLPort = lEdge.getSource();
        LPort targetLPort = lEdge.getTarget();

        // Calculate bend points
        int order = slEdge.getEdgeOrders().get(source.getPortSide());
        int connectedEdges = Iterables.size(source.getConnectedEdges());
        int splineLevel = source.getEdgeLevel(lEdge) - connectedEdges + order;

        NubsSelfLoop nubs = NubsSelfLoop.createSideSelfLoop(sourceLPort, targetLPort,
                DISTANCE * splineLevel + source.getOtherEdgeOffset());

        // Apply bend points
        slEdge.getEdge().getBendPoints().addAll(nubs.getBezierCP());
    }

    @Override
    public void routeCornerSelfLoop(final OldSelfLoopEdge slEdge) {
        LEdge lEdge = slEdge.getEdge();
        OldSelfLoopPort source = slEdge.getSource();
        OldSelfLoopPort target = slEdge.getTarget();
        LPort sourceLPort = lEdge.getSource();
        LPort targetLPort = lEdge.getTarget();

        // Calculate bend points
        int orderSource = slEdge.getEdgeOrders().get(source.getPortSide());
        int connectedEdgesSource = Iterables.size(source.getConnectedEdges());
        int splineLevelSource = source.getEdgeLevel(lEdge) - connectedEdgesSource + orderSource;

        int orderTarget = slEdge.getEdgeOrders().get(target.getPortSide());
        int connectedEdgesTarget = Iterables.size(target.getConnectedEdges());
        int splineLevelTarget = target.getEdgeLevel(lEdge) - connectedEdgesTarget + orderTarget;

        NubsSelfLoop nubs = NubsSelfLoop.createCornerSelfLoop(sourceLPort, targetLPort,
                DISTANCE * splineLevelSource + source.getOtherEdgeOffset(),
                DISTANCE * splineLevelTarget + target.getOtherEdgeOffset());

        // Apply bend points
        slEdge.getEdge().getBendPoints().addAll(nubs.getBezierCP());
    }

    @Override
    public void routeOpposingSelfLoop(final OldSelfLoopEdge slEdge) {
        LEdge lEdge = slEdge.getEdge();
        final LPort sourceLPort = lEdge.getSource();
        final LPort targetLPort = lEdge.getTarget();
        OldSelfLoopNode slNode = sourceLPort.getNode().getProperty(InternalProperties.SELF_LOOP_NODE_REPRESENTATION);

        // Calculate bend points
        KVector sourceBendPoint = computeSourceBendPoint(slEdge, false);
        KVector targetBendPoint = computeTargetBendPoint(slEdge, false);

        List<KVector> cornerBendPoints = computeCornerBendpoints(slNode, slEdge, sourceBendPoint, targetBendPoint);

        // Turn bend points into spline
        NubsSelfLoop nubs = NubsSelfLoop.createAcrossSelfLoop(sourceLPort, targetLPort, sourceBendPoint,
                cornerBendPoints, targetBendPoint);

        // Apply bend points
        lEdge.getBendPoints().addAll(nubs.getBezierCP());
    }

    @Override
    public void routeThreeCornerSelfLoop(final OldSelfLoopEdge slEdge) {
        LEdge lEdge = slEdge.getEdge();
        LPort sourceLPort = lEdge.getSource();
        LPort targetLPort = lEdge.getTarget();
        OldSelfLoopNode slNode = sourceLPort.getNode().getProperty(InternalProperties.SELF_LOOP_NODE_REPRESENTATION);

        // Calculate bend points
        KVector sourceBendPoint = computeSourceBendPoint(slEdge, false);
        KVector targetBendPoint = computeTargetBendPoint(slEdge, false);

        List<KVector> cornerBendPoints = computeCornerBendpoints(slNode, slEdge, sourceBendPoint, targetBendPoint);

        // Turn bend points into spline
        NubsSelfLoop nubs = NubsSelfLoop.createThreeSideSelfLoop(sourceLPort, targetLPort, sourceBendPoint,
                cornerBendPoints, targetBendPoint);

        // Apply bend points
        lEdge.getBendPoints().addAll(nubs.getBezierCP());
    }

    @Override
    public void routeFourCornerSelfLoop(final OldSelfLoopEdge slEdge) {
        LEdge lEdge = slEdge.getEdge();
        LPort sourceLPort = lEdge.getSource();
        LPort targetLPort = lEdge.getTarget();
        OldSelfLoopNode slNode = sourceLPort.getNode().getProperty(InternalProperties.SELF_LOOP_NODE_REPRESENTATION);

        // Calculate bend points
        KVector sourceBendPoint = computeSourceBendPoint(slEdge, false);
        KVector targetBendPoint = computeTargetBendPoint(slEdge, false);

        List<KVector> cornerBendPoints = computeCornerBendpoints(slNode, slEdge, sourceBendPoint, targetBendPoint);

        // Turn bend points into spline
        NubsSelfLoop nubs = NubsSelfLoop.createFourSideSelfLoop(sourceLPort, targetLPort, sourceBendPoint,
                cornerBendPoints, targetBendPoint);

        // Apply bend points
        lEdge.getBendPoints().addAll(nubs.getBezierCP());
    }
}
