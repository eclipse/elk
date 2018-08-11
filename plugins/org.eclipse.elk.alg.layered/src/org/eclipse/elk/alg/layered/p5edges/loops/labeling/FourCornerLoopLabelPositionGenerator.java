/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.labeling;

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

    /**
     * Creates a new generator for the given self loop node.
     */
    public FourCornerLoopLabelPositionGenerator(final SelfLoopNode slNode) {
        super(slNode);
    }

    @Override
    public void generatePositions(final SelfLoopComponent component) {
        List<SelfLoopPort> ports = component.getPorts();
        SelfLoopPort startPort = ports.get(0);
        SelfLoopPort endPort = ports.get(ports.size() - 1);
        
        // Retrieve the spacings active for this node
        double edgeEdgeSpacing = getEdgeEdgeSpacing();
        double edgeLabelSpacing = getEdgeLabelSpacing();
        
        KVector startPosition = startPort.getLPort().getPosition().clone().add(startPort.getLPort().getAnchor());
        KVector endPosition = endPort.getLPort().getPosition().clone().add(endPort.getLPort().getAnchor());

        double directionStart = SplinesMath.portSideToDirection(startPort.getPortSide());
        KVector dirVectorStart = new KVector(directionStart);
        KVector firstBend = startPosition.clone().add(dirVectorStart.clone().scale(
                (startPort.getMaximumLevel() * edgeEdgeSpacing) + edgeLabelSpacing));

        double directionEnd = SplinesMath.portSideToDirection(endPort.getPortSide());
        KVector dirVectorEnd = new KVector(directionEnd);
        KVector secondBend = endPosition.clone().add(dirVectorEnd.clone().scale(
                (endPort.getMaximumLevel() * edgeEdgeSpacing) + edgeLabelSpacing));

        SelfLoopEdge edge = Iterables.get(component.getConnectedEdges(), 0);
        List<KVector> cornerBends = SelfLoopBendpointCalculationUtil.generateCornerBendpoints(getSelfLoopNode(),
                startPort, endPort, firstBend, secondBend, edge);
        
        SelfLoopLabel label = component.getSelfLoopLabel();
        List<SelfLoopLabelPosition> positions = label.getCandidatePositions();
        
        // SUPPRESS CHECKSTYLE NEXT 20 MagicNumber

        // Centered positions
        positions.add(shortSegmentCenteredPosition(label, firstBend, cornerBends.get(0), startPort));
        positions.add(shortSegmentCenteredPosition(label, secondBend, cornerBends.get(3), endPort));
        positions.add(longSegmentCenteredPosition(label, cornerBends.get(0), cornerBends.get(1), startPort));
        positions.add(longSegmentCenteredPosition(label, cornerBends.get(3), cornerBends.get(2), endPort));
        positions.add(middleSegmentCentered(label, cornerBends.get(1), cornerBends.get(2), startPort));

        // Right-aligned
        positions.add(shortSegmentAlignedPosition(label, firstBend, cornerBends.get(0), startPort, false));
        positions.add(shortSegmentAlignedPosition(label, secondBend, cornerBends.get(3), endPort, false));
        positions.add(longSegmentAlignedPosition(label, cornerBends.get(0), cornerBends.get(1), startPort, false));
        positions.add(longSegmentAlignedPosition(label, cornerBends.get(3), cornerBends.get(2), endPort, false));
        positions.add(middleSideAligned(label, cornerBends.get(1), cornerBends.get(2), startPort, false));

        // Left-aligned
        positions.add(shortSegmentAlignedPosition(label, firstBend, cornerBends.get(0), startPort, true));
        positions.add(shortSegmentAlignedPosition(label, secondBend, cornerBends.get(3), endPort, true));
        positions.add(longSegmentAlignedPosition(label, cornerBends.get(0), cornerBends.get(1), startPort, true));
        positions.add(longSegmentAlignedPosition(label, cornerBends.get(3), cornerBends.get(2), endPort, true));
        positions.add(middleSideAligned(label, cornerBends.get(1), cornerBends.get(2), startPort, true));
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
