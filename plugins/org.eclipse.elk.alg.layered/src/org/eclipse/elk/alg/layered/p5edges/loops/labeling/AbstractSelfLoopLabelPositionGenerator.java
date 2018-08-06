/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.labeling;

import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopRoutingDirection;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

/**
 * Abstract super class for all {@link ISelfLoopLabelPositionGenerator}s. Provides a number of convenience methods.
 */
public abstract class AbstractSelfLoopLabelPositionGenerator implements ISelfLoopLabelPositionGenerator {
    
    /** TODO Document. Perhaps replace through layout options. */
    protected static final double EDGE_DISTANCE = 10;
    /** TODO Document. Perhaps replace through layout options. */
    protected static final double LABEL_SPACING = 2;
    /** TODO Document. Perhaps replace through layout options. */
    protected static final double ANCHOR_SIZE = 5;
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Centered

    /**
     * Returns the coordinates of a label position centered between the given points at the given node side.
     */
    protected KVector sideCenteredCoordinates(final KVector startPoint, final KVector endPoint,
            final SelfLoopLabel label, final PortSide side) {

        double euclidianDistance = startPoint.distance(endPoint);
        double direction = SplinesMath.portSideToDirection(side);
        KVector dirVector = new KVector(direction);

        double width = label.getWidth();
        double height = label.getHeight();
        double startPointDistance = 0;

        KVector labelPosition = new KVector(startPoint);
        
        switch (side) {
        case NORTH:
            startPointDistance = (euclidianDistance - width) / 2;
            labelPosition.x += startPointDistance;
            labelPosition.add(dirVector.clone().scale(label.getHeight()));
            break;

        case EAST:
            startPointDistance = euclidianDistance / 2 - height / 2;
            labelPosition.y += startPointDistance;
            break;

        case SOUTH:
            startPointDistance = euclidianDistance / 2 - width / 2;
            labelPosition.x += startPointDistance;
            break;

        case WEST:
            startPointDistance = (euclidianDistance - height) / 2;
            labelPosition.y += startPointDistance;
            labelPosition.add(dirVector.clone().scale(label.getWidth()));
            break;
        }
        
        return labelPosition;
    }

    /**
     * Returns a label position, along with the appropriate penalty, that is centered on a short segment.
     */
    protected SelfLoopLabelPosition shortSegmentCenteredPosition(final SelfLoopLabel label, final KVector startPoint,
            final KVector endPoint, final SelfLoopPort closestPort) {
        
        PortSide labelSideCenter = closestPort.getPortSide();
        SelfLoopRoutingDirection direction = closestPort.getDirection();

        KVector startPortPoint = null;
        KVector endPortPoint = null;

        switch (labelSideCenter) {
        case NORTH:
        case EAST:
            startPortPoint = direction == SelfLoopRoutingDirection.LEFT ? endPoint : startPoint;
            endPortPoint = direction == SelfLoopRoutingDirection.LEFT ? startPoint : endPoint;
            break;
            
        case SOUTH:
        case WEST:
            startPortPoint = direction == SelfLoopRoutingDirection.LEFT ? startPoint : endPoint;
            endPortPoint = direction == SelfLoopRoutingDirection.LEFT ? endPoint : startPoint;
            break;
        }

        // Create the centered position
        KVector centerTop = sideCenteredCoordinates(startPortPoint, endPortPoint, label, labelSideCenter);
        SelfLoopLabelPosition centerTopPosition = new SelfLoopLabelPosition(label, centerTop);
        centerTopPosition.setSide(labelSideCenter);

        // Set the penalty
        double penalty = SelfLoopLabelPenalties.getSidePenalty(labelSideCenter)
                + SelfLoopLabelPenalties.CENTERED
                + SelfLoopLabelPenalties.SHORT_SEGMENT;
        centerTopPosition.setPenalty(penalty);

        return centerTopPosition;
    }

    /**
     * Returns a label position, along with the appropriate penalty, that is centered on a long segment.
     */
    protected SelfLoopLabelPosition longSegmentCenteredPosition(final SelfLoopLabel label,
            final KVector pointClosestToPort, final KVector endPoint, final SelfLoopPort closestPort) {
        
        PortSide startSide = closestPort.getPortSide();
        PortSide labelSideCenter = closestPort.getDirection() == SelfLoopRoutingDirection.LEFT
                ? startSide.left()
                : startSide.right();

        KVector startPortPoint = null;
        KVector endPortPoint = null;

        switch (startSide) {
        case NORTH:
        case WEST:
            startPortPoint = pointClosestToPort;
            endPortPoint = endPoint;
            break;
            
        case SOUTH:
        case EAST:
            startPortPoint = endPoint;
            endPortPoint = pointClosestToPort;
            break;
        }

        // Create the centered position
        KVector centerTop = sideCenteredCoordinates(startPortPoint, endPortPoint, label, labelSideCenter);
        SelfLoopLabelPosition centerTopPosition = new SelfLoopLabelPosition(label, centerTop);
        centerTopPosition.setSide(labelSideCenter);

        // Set the penalty
        double penalty = SelfLoopLabelPenalties.getSidePenalty(labelSideCenter)
                + SelfLoopLabelPenalties.CENTERED;
        centerTopPosition.setPenalty(penalty);

        return centerTopPosition;
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Aligned

    /**
     * Returns coordinates that align the label at the top left of the given points.
     */
    protected KVector topLeftAlignedCoordinates(final SelfLoopLabel label, final KVector startPoint,
            final KVector endPoint, final PortSide side) {
        
        double direction = SplinesMath.portSideToDirection(side);
        KVector dirVector = new KVector(direction);

        double width = label.getWidth();
        double height = label.getHeight();
        
        KVector labelPosition = new KVector(startPoint);
        
        switch (side) {
        case NORTH:
            labelPosition.add(dirVector.clone().scale(height));
            break;

        case WEST:
            labelPosition.add(dirVector.clone().scale(width));
            break;
        }
        
        return labelPosition;
    }

    /**
     * Returns coordinates that align the label at the top left of the given points.
     */
    protected KVector bottomRightAlignedCoordinates(final SelfLoopLabel label, final KVector startPoint,
            final KVector endPoint, final PortSide side) {

        double direction = SplinesMath.portSideToDirection(side);
        KVector dirVector = new KVector(direction);

        double directionLeftBottom = SplinesMath.portSideToDirection(side.left());
        KVector dirVectorLeft = new KVector(directionLeftBottom);

        double width = label.getWidth();
        double height = label.getHeight();
        
        KVector labelPosition = new KVector(endPoint);
        
        switch (side) {
        case NORTH:
            labelPosition.add(dirVector.clone().scale(height));
            labelPosition.add(dirVectorLeft.clone().scale(width));
            break;

        case EAST:
            labelPosition.add(dirVectorLeft.clone().scale(height));
            break;

        case SOUTH:
            labelPosition.add(dirVectorLeft.clone().scale(-width));
            break;

        case WEST:
            labelPosition.add(dirVector.clone().scale(width));
            labelPosition.add(dirVectorLeft.clone().scale(-height));
            break;
        }
        
        return labelPosition;
    }

    /**
     * Returns a label position, along with the appropriate penalty, that is aligned with a short segment.
     */
    protected SelfLoopLabelPosition shortSegmentAlignedPosition(final SelfLoopLabel label, final KVector startPoint,
            final KVector endPoint, final SelfLoopPort closestPort, final boolean left) {
        
        PortSide labelSideCenter = closestPort.getPortSide();
        SelfLoopRoutingDirection direction = closestPort.getDirection();

        KVector startPortPoint = null;
        KVector endPortPoint = null;

        switch (labelSideCenter) {
        case NORTH:
        case EAST:
            startPortPoint = direction == SelfLoopRoutingDirection.LEFT ? endPoint : startPoint;
            endPortPoint = direction == SelfLoopRoutingDirection.LEFT ? startPoint : endPoint;
            break;
            
        case SOUTH:
        case WEST:
            startPortPoint = direction == SelfLoopRoutingDirection.LEFT ? startPoint : endPoint;
            endPortPoint = direction == SelfLoopRoutingDirection.LEFT ? endPoint : startPoint;
            break;
        }

        double penalty = SelfLoopLabelPenalties.getSidePenalty(labelSideCenter) + SelfLoopLabelPenalties.SHORT_SEGMENT;

        KVector topAlignRightSide = null;
        if (!left) {
            topAlignRightSide = bottomRightAlignedCoordinates(label, startPortPoint, endPortPoint, labelSideCenter);
            penalty += SelfLoopLabelPenalties.RIGHT_BOTTOM_ALIGNED;
        } else {
            topAlignRightSide = topLeftAlignedCoordinates(label, startPortPoint, endPortPoint, labelSideCenter);
            penalty += SelfLoopLabelPenalties.LEFT_TOP_ALIGNED;
        }

        SelfLoopLabelPosition centerTopPosition = new SelfLoopLabelPosition(label, topAlignRightSide);
        centerTopPosition.setSide(labelSideCenter);
        centerTopPosition.setPenalty(penalty);

        return centerTopPosition;
    }

    /**
     * Returns a label position, along with the appropriate penalty, that is aligned with a long segment.
     */
    protected SelfLoopLabelPosition longSegmentAlignedPosition(final SelfLoopLabel label,
            final KVector pointClosestToPort, final KVector endPoint, final SelfLoopPort closestPort,
            final boolean leftTopAligned) {
        
        PortSide startSide = closestPort.getPortSide();
        PortSide labelSideCenter = closestPort.getDirection() == SelfLoopRoutingDirection.LEFT
                ? startSide.left()
                : startSide.right();

        KVector startPortPoint = null;
        KVector endPortPoint = null;

        switch (startSide) {
        case NORTH:
        case WEST:
            startPortPoint = pointClosestToPort;
            endPortPoint = endPoint;
            break;
            
        case SOUTH:
        case EAST:
            startPortPoint = endPoint;
            endPortPoint = pointClosestToPort;
            break;
        }

        double penalty = SelfLoopLabelPenalties.getSidePenalty(labelSideCenter);

        KVector topAlignRightSide = null;
        if (!leftTopAligned) {
            topAlignRightSide = bottomRightAlignedCoordinates(label, startPortPoint, endPortPoint, labelSideCenter);
            penalty += SelfLoopLabelPenalties.RIGHT_BOTTOM_ALIGNED;
        } else {
            topAlignRightSide = topLeftAlignedCoordinates(label, startPortPoint, endPortPoint, labelSideCenter);
            penalty += SelfLoopLabelPenalties.LEFT_TOP_ALIGNED;
        }

        SelfLoopLabelPosition centerTopPosition = new SelfLoopLabelPosition(label, topAlignRightSide);
        centerTopPosition.setSide(labelSideCenter);
        centerTopPosition.setPenalty(penalty);

        return centerTopPosition;
    }
}
