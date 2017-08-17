/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.comments;

import java.awt.geom.Rectangle2D;

import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkNode;

/**
 * A matcher based on the distance between comments and attachment targets. Use the methods named
 * {@code withXXX} to configure the matcher.
 * 
 * @see IBoundsProvider
 */
public final class DistanceMatcher extends AbstractNormalizedMatcher {
    
    /** The bounds provider to use. */
    private IBoundsProvider boundsProvider = new ElkGraphBoundsProvider();
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration
    
    /**
     * Configures the matcher to consider comments up to the given distance to be attachable.
     * 
     * @param distance
     *            the maximum possible distance.
     * @return this object for method chaining.
     */
    public DistanceMatcher withMaximumAttachmentDistance(final double distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("Maximum attachment distance must be >= 0.");
        }
        
        super.withBounds(distance, 0);
        return this;
    }
    
    /**
     * Configures the matcher to use the given bounds provider to determine the bounds of
     * comments.
     * 
     * <p>
     * If this method is not called, the {@link ElkGraphBoundsProvider} is used by default.
     * </p>
     * 
     * @param provider
     *            the bounds provider to use.
     * @return this object for method chaining.
     */
    public DistanceMatcher withBoundsProvider(final IBoundsProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Bounds provider must not be null.");
        }
        
        this.boundsProvider = provider;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistanceMatcher withNormalizationFunction(
            final NormalizationFunction normalizationFunction) {
        
        super.withNormalizationFunction(normalizationFunction);
        return this;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // AbstractNormalizedHeuristic

    /**
     * {@inheritDoc}
     */
    @Override
    public double raw(final ElkNode comment, final ElkGraphElement element) {
        if (element instanceof ElkNode) {
            ElkNode node = (ElkNode) element;
            
            Rectangle2D.Double commentBounds = boundsProvider.boundsFor(comment);
            Rectangle2D.Double nodeBounds = boundsProvider.boundsFor(node);
            
            double distance = distance(commentBounds, nodeBounds);
            return distance == -1 ? getWorstRawValue() : distance;
        } else {
            return getWorstRawValue();
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Distance Calculation
    
    // Constants to easily check where points lie with respect to a rectangle
    private static final int TOP = Rectangle2D.OUT_TOP;
    private static final int LEFT = Rectangle2D.OUT_LEFT;
    private static final int RIGHT = Rectangle2D.OUT_RIGHT;
    private static final int BOTTOM = Rectangle2D.OUT_BOTTOM;
    private static final int TOP_LEFT = Rectangle2D.OUT_TOP | Rectangle2D.OUT_LEFT;
    private static final int BOTTOM_LEFT = Rectangle2D.OUT_BOTTOM | Rectangle2D.OUT_LEFT;
    private static final int TOP_RIGHT = Rectangle2D.OUT_TOP | Rectangle2D.OUT_RIGHT;
    private static final int BOTTOM_RIGHT = Rectangle2D.OUT_BOTTOM | Rectangle2D.OUT_RIGHT;
    
    /**
     * Compute the squared distance between the two shapes defined by the given bounds. If the two
     * shapes intersect, a distance of zero is returned.
     *
     * @param bounds1
     *            the first shape.
     * @param bounds2
     *            the second shape.
     * @return the squared distance between the two shapes, or {@code -1} if the distance could not
     *         be determined.
     */
    public static double distance(final Rectangle2D.Double bounds1, final Rectangle2D.Double bounds2) {
        // Check if the bounds intersect
        if (bounds1.intersects(bounds2)) {
            return 0;
        }
        
        // Check where the top left and bottom right corners of shape 1 lie with respect to shape 2
        int topLeftOutcode = bounds2.outcode(bounds1.x, bounds1.y);
        int bottomRightOutcode = bounds2.outcode(bounds1.x + bounds1.width, bounds1.y + bounds1.height);
        
        // How exactly we compute the distance depends on where the two corners of bounds1 lie with
        // respect to bounds2
        if ((topLeftOutcode & TOP_LEFT) == TOP_LEFT
                && (bottomRightOutcode & TOP_LEFT) == TOP_LEFT) {
            
            // Return distance between shape1.bottomRight and shape2.topLeft
            return distance(
                    bounds1.x + bounds1.width, bounds1.y + bounds1.height,
                    bounds2.x, bounds2.y);
            
        } else if ((topLeftOutcode & BOTTOM_LEFT) == BOTTOM_LEFT
                && (bottomRightOutcode & BOTTOM_LEFT) == BOTTOM_LEFT) {
            
            // Return distance between shape1.topRight and shape2.bottomLeft
            return distance(
                bounds1.x + bounds1.width, bounds1.y,
                bounds2.x, bounds2.y + bounds2.height);
                
        } else if ((topLeftOutcode & TOP_RIGHT) == TOP_RIGHT
                && (bottomRightOutcode & TOP_RIGHT) == TOP_RIGHT) {

            // Return distance between shape1.bottomLeft and shape2.topRight
            return distance(
                bounds1.x, bounds1.y + bounds1.height,
                bounds2.x + bounds2.width, bounds2.y);
                
        } else if ((topLeftOutcode & BOTTOM_RIGHT) == BOTTOM_RIGHT
                && (bottomRightOutcode & BOTTOM_RIGHT) == BOTTOM_RIGHT) {

            // Return distance between shape1.topLeft and shape2.bottomRight
            return distance(
                bounds1.x, bounds1.y,
                bounds2.x + bounds2.width, bounds2.y + bounds2.height);
                
        } else if ((topLeftOutcode & LEFT) != 0
                && (bottomRightOutcode & LEFT) != 0) {

            // Return distance between shape1.right and shape2.left
            double distance = bounds2.x - bounds1.x + bounds1.width;
            return distance;
            
        } else if ((topLeftOutcode & RIGHT) != 0
                && (bottomRightOutcode & RIGHT) != 0) {

            // Return distance between shape1.left and shape2.right
            double distance = bounds1.x - bounds2.x - bounds2.width;
            return distance;
            
        } else if ((topLeftOutcode & TOP) != 0
                && (bottomRightOutcode & TOP) != 0) {

            // Return distance between shape1.bottom and shape2.top
            double distance = bounds2.y - bounds1.y + bounds1.height;
            return distance;
            
        } else if ((topLeftOutcode & BOTTOM) != 0
                && (bottomRightOutcode & BOTTOM) != 0) {

            // Return distance between shape1.top and shape2.bottom
            double distance = bounds1.y - bounds2.y - bounds2.height;
            return distance;
        }
        
        // At this point we know that the outcodes are not what we expect and that the two bounds don't
        // intersect. They may be touching...
        if (bounds2.x <= bounds1.x + bounds1.width && bounds2.x + bounds2.width >= bounds1.x) {
            // They overlap horizontally, check if their borders touch
            if (bounds2.y + bounds2.height == bounds1.y || bounds2.y == bounds1.y + bounds1.height) {
                return 0;
            }
        } else if (bounds2.y <= bounds1.y + bounds1.height && bounds2.y + bounds2.height >= bounds1.y) {
            // They overlap vertically, check if their borders touch
            if (bounds2.x + bounds2.width == bounds1.x || bounds2.x == bounds1.x + bounds1.width) {
                return 0;
            }
        }
        
        // At this point, all hope is lost...
        return -1;
    }
    
    /**
     * Returns the distance between the two points defined by the given coordinates.
     *
     * @param x1
     *            x coordinate of the first point.
     * @param y1
     *            y coordinate of the first point.
     * @param x2
     *            x coordinate of the second point.
     * @param y2
     *            y coordinate of the second point.
     * @return the distance between the two points.
     */
    private static double distance(
            final double x1, final double y1, final double x2, final double y2) {
        
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
    
}
