/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.labeling;

import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopLabelPosition;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopRoutingDirection;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopLabelPosition.LabelAlignment;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

/**
 * Abstract super class for all {@link ISelfLoopLabelPositionGenerator}s. It provides basically two sets of convenience
 * methods: coordinate and position calculation methods.
 * 
 * <p>
 * Coordinate calculation methods (named {@code xxxCoordinates(...)} take an edge segment defined by a start and an end
 * point and return coordinates that need to be assigned to a label to align it with the segment in different ways. To
 * keep a label from being placed directly on the segment, we always need to know which direction to push it in to
 * prevent that from happening. Coordinate calculation methods are usually used by position calculation methods.
 * </p>
 * 
 * <p>
 * Position calculation methods (named {@code xxxPosition(...)} calculate not just coordinates, but complete
 * {@link SelfLoopLabelPosition} instances for different kinds of edge segments.
 * </p>
 * 
 * <p>
 * Regarding terminology, this class distinguishes different types of edge segments, that is, the straight lines between
 * adjacent bend points:
 * </p>
 * <ul>
 *   <li><em>Outer segments</em> are the two segments an edge ends in. They connect to the ports.</li>
 *   <li><em>Short segments</em> are segments that connect to an outer segment. A short segment does not cover the
 *       whole of a node's side.</li>
 *   <li><em>Long segments</em> are segments that cover a whole side of a node since the edge does not end in any port
 *       there.</li>
 * </ul>
 */
public abstract class AbstractSelfLoopLabelPositionGenerator implements ISelfLoopLabelPositionGenerator {
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Classes
    
    /**
     * An enumeration of the possible label position alignments along a side one might ask for.
     */
    public static enum Alignment {
        /** Left-aligned on horizontal edge segment or top-aligned on vertical edge segment. */
        LEFT_OR_TOP,
        /** Centered on its edge segment. */
        CENTERED,
        /** Right-aligned on horizontal edge segment or bottom-aligned on vertical edge segment. */
        RIGHT_OR_BOTTOM;
        
        private LabelAlignment toLabelAlignment() {
            switch (this) {
            case LEFT_OR_TOP:
                return LabelAlignment.LEFT;
            case CENTERED:
                return LabelAlignment.CENTERED;
            case RIGHT_OR_BOTTOM:
                return LabelAlignment.RIGHT;
            default:
                assert false;
                return null;
            }
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fields
    
    /** TODO Document. Perhaps replace through layout options. */
    protected static final double ANCHOR_SIZE = 5;
    
    /** The self loop node we're generating label positions for. */
    private final SelfLoopNode slNode;
    /** Spacing between edges. */
    private final double edgeEdgeSpacing;
    /** Spacing between edges and their labels. */
    private final double edgeLabelSpacing;
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction
    
    /**
     * Creates a new instance for the given node.
     */
    public AbstractSelfLoopLabelPositionGenerator(final SelfLoopNode slNode) {
        this.slNode = slNode;
        
        edgeEdgeSpacing = LGraphUtil.getIndividualOrInherited(slNode.getNode(), LayeredOptions.SPACING_EDGE_EDGE);
        edgeLabelSpacing = LGraphUtil.getIndividualOrInherited(slNode.getNode(), LayeredOptions.SPACING_EDGE_LABEL);
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Getters
    
    /**
     * Returns the self loop node this position generator was created for.
     */
    protected SelfLoopNode getSelfLoopNode() {
        return slNode;
    }
    
    /**
     * Returns the edge-edge spacing for the node we've been created for.
     */
    protected double getEdgeEdgeSpacing() {
        return edgeEdgeSpacing;
    }
    
    /**
     * Returns the edge-label spacing for the node we've been created for.
     */
    protected double getEdgeLabelSpacing() {
        return edgeLabelSpacing;
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Coordinate Calculation

    /**
     * Calculates the coordinates required to center the label on the line described by the two end points. The start
     * point is expected to be the left (horizontal line) or top (vertical line) point. The label is not placed such
     * that it crosses the line. Instead, it is offset enough in the given direction to not do so.
     */
    protected KVector centeredCoordinates(final SelfLoopLabel label, final PortSide labelOffsetDirection,
            final KVector startPoint, final KVector endPoint) {
        
        // Distance between the points
        double pointDistance = startPoint.distance(endPoint);

        // A unit vector that points in the label offset direction
        KVector offsetDirectionVector = new KVector(SplinesMath.portSideToDirection(labelOffsetDirection));

        // Calculate the label's coordinates
        KVector labelCoordinates = new KVector(startPoint);
        
        switch (labelOffsetDirection) {
        case NORTH:
            // Center on x axis, move by label height along the direction vector
            labelCoordinates.x += (pointDistance - label.getSize().x) / 2;
            labelCoordinates.add(offsetDirectionVector.clone().scale(label.getSize().y));
            break;

        case EAST:
            labelCoordinates.y += pointDistance / 2 - label.getSize().y / 2;
            break;

        case SOUTH:
            // Center on x axis
            labelCoordinates.x += pointDistance / 2 - label.getSize().x / 2;
            break;

        case WEST:
            labelCoordinates.y += (pointDistance - label.getSize().y) / 2;
            labelCoordinates.add(offsetDirectionVector.clone().scale(label.getSize().x));
            break;
        }
        
        return labelCoordinates;
    }

    /**
     * Calculates the coordinates required to place the label along the line described by the two end points, at the
     * left (horizontal line) or top (vertical line) end of the line. The start point is expected to be the left
     * (horizontal line) or top (vertical line) point. The label is not placed such that it crosses the line. Instead,
     * it is offset enough in the given direction to not do so.
     */
    protected KVector topOrLeftAlignedCoordinates(final SelfLoopLabel label, final PortSide labelOffsetDirection,
            final KVector startPoint, final KVector endPoint) {

        // A unit vector that points in the label offset direction
        KVector offsetDirectionVector = new KVector(SplinesMath.portSideToDirection(labelOffsetDirection));

        // Calculate the label's coordinates. We can basically just use the start point and offset the label if its
        // offset direction is NORTH or WEST.
        KVector labelCoordinates = new KVector(startPoint);
        
        switch (labelOffsetDirection) {
        case NORTH:
            labelCoordinates.add(offsetDirectionVector.clone().scale(label.getSize().y));
            break;

        case WEST:
            labelCoordinates.add(offsetDirectionVector.clone().scale(label.getSize().x));
            break;
        }
        
        return labelCoordinates;
    }

    /**
     * Calculates the coordinates required to place the label along the line described by the two end points, at the
     * right (horizontal line) or bottom (vertical line) end of the line. The start point is expected to be the left
     * (horizontal line) or top (vertical line) point. The label is not placed such that it crosses the line. Instead,
     * it is offset enough in the given direction to not do so.
     */
    protected KVector bottomOrRightAlignedCoordinates(final SelfLoopLabel label, final PortSide labelOffsetDirection,
            final KVector startPoint, final KVector endPoint) {

        // A unit vector that points in the label offset direction
        KVector offsetDirectionVector = new KVector(SplinesMath.portSideToDirection(labelOffsetDirection));

        // A unit vector that points in the direction from the end point towards the start point (we'll use that below
        // to ensure that the label is always within the line's bounds)
        KVector reversedLineDirVector = new KVector(endPoint, startPoint).normalize();

        // Calculate the label's coordinates. We start with the end point and move the label two ways: first, in the
        // NORTH and WEST cases we need to move the label in the offset direction. Second, starting at the end point
        // makes the label extend beyond the line either in x or y direction, so we need to reign it back in.
        KVector labelCoordinates = new KVector(endPoint);
        
        switch (labelOffsetDirection) {
        case NORTH:
            labelCoordinates.add(offsetDirectionVector.clone().scale(label.getSize().y));
            labelCoordinates.add(reversedLineDirVector.clone().scale(label.getSize().x));
            break;

        case EAST:
            labelCoordinates.add(reversedLineDirVector.clone().scale(label.getSize().y));
            break;

        case SOUTH:
            labelCoordinates.add(reversedLineDirVector.clone().scale(label.getSize().x));
            break;

        case WEST:
            labelCoordinates.add(offsetDirectionVector.clone().scale(label.getSize().x));
            labelCoordinates.add(reversedLineDirVector.clone().scale(label.getSize().y));
            break;
        }
        
        return labelCoordinates;
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Position Calculation
    
    /* All of the position generation methods for the different kinds of segments below take different kinds of
     * information, depending on what makes sense for the respective segment. They then basically devise a sensible
     * order for the two vectors that define the edge segment and delegate to doCreateLabelPosition(...) to do the
     * remaining computations, which are the same for all methods.
     */

    /**
     * Calculates a label position (including base penalty) for a self-loop's outer segment.
     * 
     * @param label
     *            the label we're computing a position for.
     * @param portSide
     *            the side the self loop is on.
     * @param penaltySide
     *            the side used to compute the position's penalty. This is for same-side self loops, who want their
     *            short segment to always be preferred.
     * @param portPoint
     *            the point where the self loop's outer segment connects to a port.
     * @param bendPoint
     *            the point where the self loop's outer segment bends.
     * @param leftFromCenterSegment
     *            {@code true} if, viewed from the node's center towards the self loop's center segment, we're
     *            calculating positions for the outer segment to the left of the center segment.
     * @param alignment
     *            how the self loop label position should be aligned on its segment.
     */
    protected SelfLoopLabelPosition outerSegmentPosition(final SelfLoopLabel label, final PortSide portSide,
            final PortSide penaltySide, final KVector portPoint, final KVector bendPoint,
            final boolean leftFromCenterSegment, final Alignment alignment) {
        
        PortSide labelSide = leftFromCenterSegment ? portSide.left() : portSide.right();

        // The start and end points are chosen such that the line they describe runs downwards or rightwards
        KVector startPoint = null;
        KVector endPoint = null;

        switch (portSide) {
        case NORTH:
        case WEST:
            startPoint = bendPoint;
            endPoint = portPoint;
            break;
        case SOUTH:
        case EAST:
            startPoint = portPoint;
            endPoint = bendPoint;
            break;
        }
        
        SelfLoopLabelPosition position = doCreateLabelPosition(
                label, portSide, labelSide, penaltySide, startPoint, endPoint, alignment);
        position.setBasePenalty(position.getBasePenalty() + SelfLoopLabelPenalties.SHORT_SEGMENT);
        
        return position;
    }

    /**
     * Calculates a label position (including base penalty) for a self-loop's short segment. This method always calls
     * {@link #shortSegmentPosition(SelfLoopLabel, SelfLoopPort, PortSide, KVector, KVector, Alignment, boolean)}
     * with the closest port's port side as the penalty side.
     * 
     * @param label
     *            the label we're computing a position for.
     * @param closestPort
     *            the port the short segment connects to through an outer segment. The port will be on the same side of
     *            the node as the segment itself.
     * @param portPoint
     *            the bend point between the short segment and the outer segment that connects to the closest port.
     *            This is basically the bend point closer to that port than the other bend point.
     * @param bendPoint
     *            the other bend point.
     * @param alignment
     *            how the self loop label position should be aligned on the segment.
     * @param addShortSegmentPenalty
     *            {@code true} if the penalty set on the position should include the short segment penalty. This will
     *            usually be the case, but self-loops that do not have a long segment will have no use for this penalty.
     */
    protected SelfLoopLabelPosition shortSegmentPosition(final SelfLoopLabel label, final SelfLoopPort closestPort,
            final KVector portPoint, final KVector bendPoint, final Alignment alignment,
            final boolean addShortSegmentPenalty) {
        
        return shortSegmentPosition(
                label, closestPort, closestPort.getPortSide(), portPoint, bendPoint, alignment, addShortSegmentPenalty);
    }

    /**
     * Calculates a label position (including base penalty) for a self-loop's short segment.
     * 
     * @param label
     *            the label we're computing a position for.
     * @param closestPort
     *            the port the short segment connects to through an outer segment. The port will be on the same side of
     *            the node as the segment itself.
     * @param penaltySide
     *            the side used to compute the position's penalty. This is for same-side self loops, who want their
     *            short segment to always be preferred.
     * @param portPoint
     *            the bend point between the short segment and the outer segment that connects to the closest port.
     *            This is basically the bend point closer to that port than the other bend point.
     * @param bendPoint
     *            the other bend point.
     * @param alignment
     *            how the self loop label position should be aligned on the segment.
     * @param addShortSegmentPenalty
     *            {@code true} if the penalty set on the position should include the short segment penalty. This will
     *            usually be the case, but self-loops that do not have a long segment will have no use for this penalty.
     */
    protected SelfLoopLabelPosition shortSegmentPosition(final SelfLoopLabel label, final SelfLoopPort closestPort,
            final PortSide penaltySide, final KVector portPoint, final KVector bendPoint, final Alignment alignment,
            final boolean addShortSegmentPenalty) {
        
        // The side we're on and the direction the edge is heading into
        PortSide portSide = closestPort.getPortSide();
        SelfLoopRoutingDirection routingDirection = closestPort.getDirection();
        
        // Start point needs to be to the left / top of the end point
        KVector startPoint = null;
        KVector endPoint = null;

        switch (portSide) {
        case NORTH:
        case EAST:
            startPoint = routingDirection == SelfLoopRoutingDirection.RIGHT ? portPoint : bendPoint;
            endPoint = routingDirection == SelfLoopRoutingDirection.RIGHT ? bendPoint : portPoint;
            break;
            
        case SOUTH:
        case WEST:
            startPoint = routingDirection == SelfLoopRoutingDirection.LEFT ? portPoint : bendPoint;
            endPoint = routingDirection == SelfLoopRoutingDirection.LEFT ? bendPoint : portPoint;
            break;
        }
        
        SelfLoopLabelPosition position = doCreateLabelPosition(
                label, portSide, portSide, penaltySide, startPoint, endPoint, alignment);
        
        if (addShortSegmentPenalty) {
            position.setBasePenalty(position.getBasePenalty() + SelfLoopLabelPenalties.SHORT_SEGMENT);
        }
        
        return position;
    }

    /**
     * Calculates a label position (including base penalty) for a self-loop's long segment.
     * 
     * @param label
     *            the label we're computing a position for.
     * @param segmentSide
     *            the side of the node the long segment covers.
     * @param firstBend
     *            one of the two bend points that define the edge segment.
     * @param secondBend
     *            the other of the two bend points.
     * @param alignment
     *            how the self loop label position should be aligned on the segment.
     */
    protected SelfLoopLabelPosition longSegmentPosition(final SelfLoopLabel label, final PortSide segmentSide,
            final KVector firstBend, final KVector secondBend, final Alignment alignment) {
        
        // Start point needs to be to the left / top of the end point
        KVector startPoint = firstBend;
        KVector endPoint = secondBend;
        
        switch (segmentSide) {
        case NORTH:
        case SOUTH:
            if (firstBend.x > secondBend.x) {
                startPoint = secondBend;
                endPoint = firstBend;
            }
            break;
            
        case EAST:
        case WEST:
            if (firstBend.y > secondBend.y) {
                startPoint = secondBend;
                endPoint = firstBend;
            }
            break;
        }
        
        return doCreateLabelPosition(label, segmentSide, startPoint, endPoint, alignment);
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utility Methods
    
    /**
     * Called by the other position methods to actually compute coordinates and create a label position.
     * 
     * @param label
     *            the label we're creating the side for.
     * @param side
     *            the side of the node the segment will be on.
     * @param startPoint
     *            the segment's start point. Expected to be above / to the left of the end point.
     * @param endPoint
     *            the segment's end point. Expected to be below / to the right of the end point.
     * @param alignment
     *            the label's alignment with respect to the segment.
     */
    private SelfLoopLabelPosition doCreateLabelPosition(final SelfLoopLabel label, final PortSide side,
            final KVector startPoint, final KVector endPoint, final Alignment alignment) {
        
        return doCreateLabelPosition(label, side, side, side, startPoint, endPoint, alignment);
    }
    
    /**
     * Called by the other position methods to actually compute coordinates and create a label position.
     * 
     * @param label
     *            the label we're creating the side for.
     * @param segmentSide
     *            the side of the node the loop's segment is on. This will be the self loop position's side property.
     * @param labelSide
     *            the side of the segment the label will be on.
     * @param penaltySide
     *            the side used to compute the base penalty. This can be different from the others since same-side
     *            loops want their short segment to always be preferred, regardless of which side those are on.
     * @param startPoint
     *            the segment's start point. Expected to be above / to the left of the end point.
     * @param endPoint
     *            the segment's end point. Expected to be below / to the right of the end point.
     * @param alignment
     *            the label's alignment with respect to the segment.
     */
    private SelfLoopLabelPosition doCreateLabelPosition(final SelfLoopLabel label, final PortSide segmentSide,
            final PortSide labelSide, final PortSide penaltySide, final KVector startPoint, final KVector endPoint,
            final Alignment alignment) {
        
        // Compute the coordinates for the position
        KVector coordinates = null;
        
        switch (alignment) {
        case CENTERED:
            coordinates = centeredCoordinates(label, labelSide, startPoint, endPoint);
            break;
            
        case LEFT_OR_TOP:
            coordinates = topOrLeftAlignedCoordinates(label, labelSide, startPoint, endPoint);
            break;
            
        case RIGHT_OR_BOTTOM:
            coordinates = bottomOrRightAlignedCoordinates(label, labelSide, startPoint, endPoint);
            break;
        }
        
        // Compute the label alignment for multiple labels stacked above each other
        LabelAlignment labelAlignment = null;
        switch (labelSide) {
        case EAST:
            labelAlignment = LabelAlignment.LEFT;
            break;
            
        case WEST:
            labelAlignment = LabelAlignment.RIGHT;
            break;
            
        case NORTH:
        case SOUTH:
            // In these cases, the label alignment depends on our own alignment
            labelAlignment = alignment.toLabelAlignment();
        }
        
        // Setup the label position
        SelfLoopLabelPosition position = new SelfLoopLabelPosition(label, coordinates);
        position.setSide(segmentSide);
        position.setLabelAlignment(labelAlignment);
        position.setBasePenalty(
                SelfLoopLabelPenalties.getSidePenalty(penaltySide)
                + SelfLoopLabelPenalties.getAlignmentPenalty(alignment));

        return position;
    }
    
}
