/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.layered.p5edges.splines;

import java.util.List;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.layered.graph.LPort;

/**
 * An extension of {@link NubSpline} for self-loops.
 * Extends the parent-class by methods for creation of self-loop splines.
 * 
 * @author ttoe
 *
 */
public final class NubsSelfLoop extends NubSpline {
    /** Accuracy of the min/max calculation. This is the value of the first derivate that is assumed
     * to be equal to zero. */
    private static final double ACCURACY = 0.5;
    /** How many recursions shall be processed by the min/max calculation at maximum.  */
    private static final int MAX_RECURSIONS = 50;
    /** Fraction of the second/fourth control-point compared to the desired high. A bigger value will 
     * result in a "higher" second/fourth control-point and a lower middle control-point. */
    private static final double FRACTION = 1.3;
    /** To avoid magic number warning. */
    private static final double HALF = 0.5;
    
    /** The position of the first self-loop label. */ 
    private KVector firstLabelPosition = new KVector();

    /**
     * A copy constructor.
     * 
     * @param nubs The NubsSelfLoop to copy.
     */
    public NubsSelfLoop(final NubsSelfLoop nubs) {
        super(nubs);
        firstLabelPosition = nubs.firstLabelPosition;
    }

    /**
     * Creates a new uniform and clamped NubsSelfLoop with the specified control points.
     * 
     * @param dimension The dimension if this NubsSelfLoop.
     * @param kVectors The control points of this NubsSelfLoop.
     */
    private NubsSelfLoop(final int dim, final KVector... kVectors) {
        super(true, dim, kVectors);
    }

    /**
     * Creates a new uniform and clamped NubsSelfLoop with the specified control points. Dimension must
     * be > 0.
     * 
     * @param dimension The dimension if this NubsSelfLoop.
     * @param kVectors The control points of this NubsSelfLoop.
     */
    public NubsSelfLoop(final int dimension, final List<KVector> kVectors) {
        super(true, dimension, kVectors);
    }

    /**
     * Creates a new uniform NubsSelfLoop with the specified control points.  Dimension must be > 0.
     * 
     * @param clamped True, if the NubsSelfLoop shall be clamped.
     * @param dimension The dimension if this NubsSelfLoop.
     * @param kVectors The control points of this NubsSelfLoop.
     */
    public NubsSelfLoop(final boolean clamped, final int dimension, final List<KVector> kVectors) {
        super(clamped, dimension, kVectors);
    }

    /**
     * Generates a new NUBS representing a self loop at one of the straight sides of a node.
     * 
     * @param source Source port this spline shall start at.
     * @param target Target port this spline shall end at.
     * @param length The estimated distance the spline shall go away from the side of the node.
     * @return A new NUBS representing the loop spline.
     */
    public static NubsSelfLoop createSideSelfLoop(final LPort source, final LPort target,
            final double length) {

        final double direction = SplinesMath.portSideToDirection(source.getSide());
        final KVector sourcePos = source.getPosition().clone().add(source.getAnchor());
        final KVector targetPos = target.getPosition().clone().add(target.getAnchor());

        // calculate the three control points (next to source and target point)
        final KVector firstCP = new KVector(sourcePos).add(new KVector(direction).scale(length));
        final KVector thirdCP = new KVector(targetPos).add(new KVector(direction).scale(length));
        final KVector mid = new KVector(firstCP).sub(thirdCP).scale(1.0 / 2.0);
        final KVector secondCP =
                new KVector(thirdCP).add(mid).add(new KVector(direction).scale(mid.length()));

        // calculate the spline
        final NubsSelfLoop nubs = 
                new NubsSelfLoop(DIM, sourcePos, firstCP, secondCP, thirdCP, targetPos);

        // calculate bounding Box and label position
        // we are symmetric, so the top position of the spline is in it's middle
        final KVector labelPos = nubs.getPointOnCurve(HALF);
        nubs.setFirstLabelPosition(labelPos);
        nubs.setOuterBox(new Rectangle(labelPos, sourcePos, targetPos));

        return nubs;
    }

    /**
     * Generates a new NUBS representing a self loop at the corner of a node.
     * 
     * @param source The source port this spline shall start at.
     * @param target The target port this spline shall end at.
     * @param sourceHeight The estimated distance the spline shall go away from the source side.
     * @param targetHeight The estimated distance the spline shall go away from the target side.
     * @param textLength The (maximum) length of any label on this spline. Of cause you can put 
     *            longer labels at the spline, but it may look bad.
     * @return A new NubSpline representing the loop spline.
     */
    public static NubsSelfLoop createCornerSelfLoop(final LPort source, final LPort target,
            final double sourceHeight, final double targetHeight, final double textLength) {
        // Radian direction of source and target.
        final double sourceDir = SplinesMath.portSideToDirection(source.getSide());
        final double targetDir = SplinesMath.portSideToDirection(target.getSide());
        // Position of source and target (port).
        final KVector sourcePos = source.getPosition().clone().add(source.getAnchor());
        final KVector targetPos = target.getPosition().clone().add(target.getAnchor());

        // first and last CP are simply located in a specified distance (sourceHeight/targetHeight)
        // and a specified direction (sourceDirection/targetDirection) from source/target.
        final KVector firstCP = sourcePos.clone()
                .add(new KVector(sourceDir).scale(FRACTION * sourceHeight));
        final KVector thirdCP = targetPos.clone()
                .add(new KVector(targetDir).scale(FRACTION * targetHeight));

        // the vertical distance between first and last CP shall be at least as big as the
        // textLength
        final double dist = Math.abs(firstCP.x - thirdCP.x);
        if (dist < textLength) {
            // move the x coordinate of the CP, that is located at the west or east side of the node 
            // away from the CP that is located at the north or south side of the node.
            if (source.getSide() == PortSide.WEST || source.getSide() == PortSide.EAST) {
                if (firstCP.x < thirdCP.x) {
                    firstCP.x = thirdCP.x - textLength;
                } else {
                    firstCP.x = thirdCP.x + textLength;
                }
            } else {
                if (firstCP.x < thirdCP.x) {
                    thirdCP.x = firstCP.x + textLength;
                } else {
                    thirdCP.x = firstCP.x - textLength;
                }
            }
        }
        
        // second CP is located at a defined scale of the vector from corner to cpCorner
        double cornerX = 0.0;
        double cornerY = 0.0;

        switch (source.getSide()) {
        case WEST:
            cornerX = 2 * (sourcePos.x - sourceHeight) - HALF * (firstCP.x + thirdCP.x);
            break;
        case EAST:
            cornerX = 2 * (sourcePos.x + sourceHeight) - HALF * (firstCP.x + thirdCP.x);
            break;
        case NORTH:
            cornerY = 2 * (sourcePos.y - sourceHeight) - HALF * (firstCP.y + thirdCP.y);
            break;
        case SOUTH:
            cornerY = 2 * (sourcePos.y + sourceHeight) - HALF * (firstCP.y + thirdCP.y);
            break;
        default:
            break;
        }
        switch (target.getSide()) {
        case WEST:
            cornerX = 2 * (targetPos.x - targetHeight) - HALF * (thirdCP.x + firstCP.x);
            break;
        case EAST:
            cornerX = 2 * (targetPos.x + targetHeight) - HALF * (thirdCP.x + firstCP.x);
            break;
        case NORTH:
            cornerY = 2 * (targetPos.y - targetHeight) - HALF * (thirdCP.y + firstCP.y);
            break;
        case SOUTH:
            cornerY = 2 * (targetPos.y + targetHeight) - HALF * (thirdCP.y + firstCP.y);
            break;
        default:
            break;
        }

        final KVector secondCP = new KVector(cornerX, cornerY);

        // Calculate the spline
        final NubsSelfLoop nubs = 
                new NubsSelfLoop(DIM, sourcePos, firstCP, secondCP, thirdCP, targetPos);

        // calculate bounding Box and label position
        final KVector horizontal = 
                getFirstHorizontalPoint(nubs, ACCURACY, MAX_RECURSIONS);
        final KVector vertical = 
                getFirstVerticalPoint(nubs, ACCURACY, MAX_RECURSIONS);
        nubs.setFirstLabelPosition(horizontal);
        nubs.setOuterBox(new Rectangle(horizontal, vertical, sourcePos, targetPos));
        return nubs;
    }
    
    /**
     * Generates a KVectorChain representing the control points of a bezier spline forming a self
     * loop edge going from one side of a node to it's opposed.
     * 
     * @param source Start point of spline.
     * @param target End point of spline.
     * @param intermediateSide PortSide this self loop shall bend around.
     * @param sourceHeight Distance from source in sourceDirection to keep free.
     * @param middleHeight FreeSpace between node at loopDirection side and the spline.
     * @param targetHeight Distance from target in targetDirection to keep free.
     * @param textLength The (maximum) length of any label on this spline. Of cause you can put 
     *            longer labels at the spline, but it may look bad.
     * @return A KVectorChain of the control points of the constructed bezier spline.
     */
    public static NubsSelfLoop createAcrossSelfLoop(
            final LPort source, final double sourceHeight, 
            final LPort target, final double targetHeight, 
            final PortSide intermediateSide, final double middleHeight,
            final double textLength) {

        final double sourceDir = SplinesMath.portSideToDirection(source.getSide());
        final double targetDir = SplinesMath.portSideToDirection(target.getSide());
        final KVector sourcePos = source.getPosition().clone().add(source.getAnchor());
        final KVector targetPos = target.getPosition().clone().add(target.getAnchor());

        // calculate first and last control point
        final KVector firstCP =
                new KVector(sourcePos).add(new KVector(sourceDir).scale(sourceHeight));
        final KVector fourthCP =
                new KVector(targetPos).add(new KVector(targetDir).scale(targetHeight));

        // calculate second and third control points
        double loopHeight = SplinesMath.distPortToNodeEdge(source, intermediateSide);
        if (intermediateSide == PortSide.SOUTH || intermediateSide == PortSide.EAST) {
            loopHeight += middleHeight;
        } else {
            loopHeight -= middleHeight;
        }
        
        final KVector secondCP = new KVector();
        final KVector thirdCP = new KVector();

        switch (intermediateSide) {
        case NORTH:
        case SOUTH:
            secondCP.x = firstCP.x;
            secondCP.y = sourcePos.y + loopHeight;
            thirdCP.x = fourthCP.x;
            thirdCP.y = secondCP.y;
            break;
        case EAST:
        case WEST:
            secondCP.x = sourcePos.x + loopHeight;
            secondCP.y = firstCP.y;
            thirdCP.x = secondCP.x;
            thirdCP.y = fourthCP.y;
            break;
        default:
            return null;
        }
        
        // calculate middle (between second and third) control point
        final KVector middleCP = secondCP.clone().add(thirdCP).scale(0.5);

        // calculate the spline
        final NubsSelfLoop nubs = new NubsSelfLoop(DIM, sourcePos, firstCP, secondCP, 
                middleCP, thirdCP, fourthCP, targetPos);

        // calculate bounding Box and label position
        final KVector horizontal = 
                getFirstHorizontalPoint(nubs, ACCURACY, MAX_RECURSIONS);
        final KVector vertical = 
                getFirstVerticalPoint(nubs, ACCURACY, MAX_RECURSIONS);
        KVector thirdExtremum;
        
        switch (intermediateSide) {
        case NORTH:
        case SOUTH:
            nubs.setFirstLabelPosition(horizontal);
            thirdExtremum = getLastVerticalPoint(nubs, ACCURACY, MAX_RECURSIONS);
            break;
        case EAST:
        case WEST:
            nubs.setFirstLabelPosition(vertical);
            thirdExtremum = getLastHorizontalPoint(nubs, ACCURACY, MAX_RECURSIONS);
            break;
        default:
            return null;
        }

        nubs.setOuterBox(new Rectangle(horizontal, vertical, thirdExtremum, sourcePos, targetPos));

        return nubs;
    }

    /**
     * Sets the label position.
     * 
     * @param position The new position for the first label.
     */
    public void setFirstLabelPosition(final KVector position) {
        firstLabelPosition = position;
    }

    /**
     * @return The position for the first label. May be modified.
     */
    public KVector getFirstLabelPosition() {
        return firstLabelPosition;
    }
}
