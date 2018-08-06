/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.labeling;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopRoutingDirection;
import org.eclipse.elk.alg.layered.p5edges.loops.util.SelfLoopBendpointCalculationUtil;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Iterables;

/**
 * Generates self loop label positions for four-corner self loops.
 */
public class FourCornerLoopLabelPositionGenerator extends AbstractSelfLoopLabelPositionGenerator {

    private SelfLoopNode slNode;

    /**
     * Creates a new generator for the given self loop node.
     */
    public FourCornerLoopLabelPositionGenerator(final SelfLoopNode nodeRep) {
        this.slNode = nodeRep;
    }

    @Override
    public List<SelfLoopLabelPosition> generatePositions(final SelfLoopComponent component) {
        List<SelfLoopLabelPosition> positions = new ArrayList<SelfLoopLabelPosition>();

        List<SelfLoopPort> ports = component.getPorts();
        SelfLoopPort startPort = ports.get(0);
        SelfLoopPort endPort = ports.get(ports.size() - 1);
        
        positions = generatePositions(component, startPort, endPort);
        
        return positions;
    }

    private List<SelfLoopLabelPosition> generatePositions(final SelfLoopComponent component,
            final SelfLoopPort startPort, final SelfLoopPort endPort) {
        
        List<SelfLoopLabelPosition> positions = new ArrayList<SelfLoopLabelPosition>();
        KVector startPosition = startPort.getLPort().getPosition().clone().add(startPort.getLPort().getAnchor());
        KVector endPosition = endPort.getLPort().getPosition().clone().add(endPort.getLPort().getAnchor());

        double directionStart = SplinesMath.portSideToDirection(startPort.getPortSide());
        KVector dirVectorStart = new KVector(directionStart);
        KVector firstBendpoint = startPosition.clone()
                .add(dirVectorStart.clone().scale((startPort.getMaximumLevel() * EDGE_DISTANCE) + LABEL_SPACING));

        double directionEnd = SplinesMath.portSideToDirection(endPort.getPortSide());
        KVector dirVectorEnd = new KVector(directionEnd);
        KVector secondBendpoint = endPosition.clone()
                .add(dirVectorEnd.clone().scale((endPort.getMaximumLevel() * EDGE_DISTANCE) + LABEL_SPACING));

        SelfLoopEdge edge = Iterables.get(component.getConnectedEdges(), 0);
        List<KVector> cornerPoints = SelfLoopBendpointCalculationUtil.generateCornerBendpoints(slNode, startPort,
                endPort, firstBendpoint, secondBendpoint, edge);

        // Centered Positions
        List<SelfLoopLabelPosition> centeredPositions = createCenteredPositions(startPort, endPort, firstBendpoint,
                secondBendpoint, cornerPoints, component.getLabel());
        positions.addAll(centeredPositions);

        // Side aligned positions
        List<SelfLoopLabelPosition> alignedPositions = createSideAlignedPositions(startPort, endPort, firstBendpoint,
                secondBendpoint, cornerPoints, component.getLabel());
        positions.addAll(alignedPositions);

        return positions;
    }

    /**
     * Creates all centered candidate positions across all the segments.
     */
    private List<SelfLoopLabelPosition> createCenteredPositions(final SelfLoopPort startPort,
            final SelfLoopPort endPort, final KVector firstBendpoint, final KVector secondBendpoint,
            final List<KVector> cornerBendPoints, final SelfLoopLabel label) {
        
        // SUPPRESS CHECKSTYLE NEXT 20 MagicNumber
        List<SelfLoopLabelPosition> positions = new ArrayList<>();

        SelfLoopLabelPosition firstShortSegment = shortSegmentCenteredPosition(
                label, firstBendpoint, cornerBendPoints.get(0), startPort);
        positions.add(firstShortSegment);

        SelfLoopLabelPosition secondShortSegment = shortSegmentCenteredPosition(
                label, secondBendpoint, cornerBendPoints.get(3), endPort);
        positions.add(secondShortSegment);

        SelfLoopLabelPosition firstLongSegment = longSegmentCenteredPosition(
                label, cornerBendPoints.get(0), cornerBendPoints.get(1), startPort);
        positions.add(firstLongSegment);

        SelfLoopLabelPosition secondLongSegment = longSegmentCenteredPosition(
                label, cornerBendPoints.get(3), cornerBendPoints.get(2), endPort);
        positions.add(secondLongSegment);

        SelfLoopLabelPosition middleLongSegment = middleSegmentCentered(
                label, cornerBendPoints.get(1), cornerBendPoints.get(2), startPort);
        positions.add(middleLongSegment);
        
        return positions;

    }

    /**
     * Creates all side-agligned candidate positions across all the segments.
     */
    private List<SelfLoopLabelPosition> createSideAlignedPositions(final SelfLoopPort startPort,
            final SelfLoopPort endPort, final KVector firstBendpoint, final KVector secondBendpoint,
            final List<KVector> cornerBendPoints, final SelfLoopLabel label) {

        // SUPPRESS CHECKSTYLE NEXT 40 MagicNumber
        List<SelfLoopLabelPosition> positions = new ArrayList<SelfLoopLabelPosition>();

        // Right-aligned
        SelfLoopLabelPosition firstShortSegmentRightBottomAligned = shortSegmentAlignedPosition(
                label, firstBendpoint, cornerBendPoints.get(0), startPort, false);
        positions.add(firstShortSegmentRightBottomAligned);

        SelfLoopLabelPosition secondShortSegmentRightBottomAligned = shortSegmentAlignedPosition(
                label, secondBendpoint, cornerBendPoints.get(3), endPort, false);
        positions.add(secondShortSegmentRightBottomAligned);

        SelfLoopLabelPosition firstLongSegmentRightBottomAligned = longSegmentAlignedPosition(
                label, cornerBendPoints.get(0), cornerBendPoints.get(1), startPort, false);
        positions.add(firstLongSegmentRightBottomAligned);

        SelfLoopLabelPosition secondSegmentRightBottomAligned = longSegmentAlignedPosition(
                label, cornerBendPoints.get(3), cornerBendPoints.get(2), endPort, false);
        positions.add(secondSegmentRightBottomAligned);

        SelfLoopLabelPosition middleSegmentRightBottomAligned = middleSideAligned(
                label, cornerBendPoints.get(1), cornerBendPoints.get(2), startPort, false);
        positions.add(middleSegmentRightBottomAligned);

        // Left-aligned
        SelfLoopLabelPosition firstShortSegmentLeftTopAligned = shortSegmentAlignedPosition(
                label, firstBendpoint, cornerBendPoints.get(0), startPort, true);
        positions.add(firstShortSegmentLeftTopAligned);

        SelfLoopLabelPosition secondShortSegmentLeftTopAligned = shortSegmentAlignedPosition(
                label, secondBendpoint, cornerBendPoints.get(3), endPort, true);
        positions.add(secondShortSegmentLeftTopAligned);

        SelfLoopLabelPosition firstLongSegmentLeftTopAligned = longSegmentAlignedPosition(
                label, cornerBendPoints.get(0), cornerBendPoints.get(1), startPort, true);
        positions.add(firstLongSegmentLeftTopAligned);

        SelfLoopLabelPosition secondSegmentLeftTopAligned = longSegmentAlignedPosition(
                label, cornerBendPoints.get(3), cornerBendPoints.get(2), endPort, true);
        positions.add(secondSegmentLeftTopAligned);

        SelfLoopLabelPosition middleSegmentLeftTopAligned = middleSideAligned(
                label, cornerBendPoints.get(1), cornerBendPoints.get(2), startPort, true);
        positions.add(middleSegmentLeftTopAligned);

        return positions;
    }

    /**
     * Returns a label position, along with the appropriate penalty, that is centered on a middle segment.
     */
    private SelfLoopLabelPosition middleSegmentCentered(final SelfLoopLabel label,
            final KVector pointClosestToPort, final KVector endPoint, final SelfLoopPort closestPort) {
        
        PortSide portSide = closestPort.getPortSide();
        SelfLoopRoutingDirection direction = closestPort.getDirection();
        PortSide segmentSide = portSide.right().right();

        KVector startPortPoint = null;
        KVector endPortPoint = null;

        switch (portSide) {
        case NORTH:
        case EAST:
            startPortPoint = direction == SelfLoopRoutingDirection.RIGHT ? endPoint : pointClosestToPort;
            endPortPoint = direction == SelfLoopRoutingDirection.RIGHT ? pointClosestToPort : endPoint;
            break;
        case SOUTH:
        case WEST:
            startPortPoint = direction == SelfLoopRoutingDirection.RIGHT ? pointClosestToPort : endPoint;
            endPortPoint = direction == SelfLoopRoutingDirection.RIGHT ? endPoint : pointClosestToPort;
            break;
        }
        KVector topAlignRightSide = sideCenteredCoordinates(startPortPoint, endPortPoint, label, segmentSide);

        SelfLoopLabelPosition centerTopPosition = new SelfLoopLabelPosition(label, topAlignRightSide);
        centerTopPosition.setSide(segmentSide);
        centerTopPosition.setPenalty(SelfLoopLabelPenalties.getSidePenalty(segmentSide)
                + SelfLoopLabelPenalties.CENTERED);

        return centerTopPosition;
    }

    /**
     * 
     * Returns a label position, along with the appropriate penalty, that is side-aligned on a middle segment.
     */
    private SelfLoopLabelPosition middleSideAligned(final SelfLoopLabel label, final KVector pointClosestToPort,
            final KVector endPoint, final SelfLoopPort closestPort, final boolean left) {
        
        PortSide portSide = closestPort.getPortSide();
        SelfLoopRoutingDirection direction = closestPort.getDirection();
        PortSide segmentSide = portSide.right().right();

        KVector startPortPoint = null;
        KVector endPortPoint = null;

        switch (portSide) {
        case NORTH:
        case EAST:
            startPortPoint = direction == SelfLoopRoutingDirection.RIGHT ? endPoint : pointClosestToPort;
            endPortPoint = direction == SelfLoopRoutingDirection.RIGHT ? pointClosestToPort : endPoint;
            break;
        case SOUTH:
        case WEST:
            startPortPoint = direction == SelfLoopRoutingDirection.RIGHT ? pointClosestToPort : endPoint;
            endPortPoint = direction == SelfLoopRoutingDirection.RIGHT ? endPoint : pointClosestToPort;
            break;
        }

        double penalty = SelfLoopLabelPenalties.getSidePenalty(segmentSide);

        KVector topAlignRightSide = null;
        if (!left) {
            topAlignRightSide = bottomRightAlignedCoordinates(label, startPortPoint, endPortPoint, segmentSide);
            penalty = SelfLoopLabelPenalties.RIGHT_BOTTOM_ALIGNED;
        } else {
            topAlignRightSide = topLeftAlignedCoordinates(label, startPortPoint, endPortPoint, segmentSide);
            penalty = SelfLoopLabelPenalties.LEFT_TOP_ALIGNED;
        }

        SelfLoopLabelPosition centerTopPosition = new SelfLoopLabelPosition(label, topAlignRightSide);
        centerTopPosition.setSide(segmentSide);
        centerTopPosition.setPenalty(penalty);

        return centerTopPosition;
    }
}
