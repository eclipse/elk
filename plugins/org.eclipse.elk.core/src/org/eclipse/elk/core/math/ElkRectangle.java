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
package org.eclipse.elk.core.math;

import java.util.Objects;

/**
 * Class resembles basic functionality of {@link java.awt.geom.Rectangle2D}. This way it is possible
 * to avoid awt dependencies in ELK Layered's code.
 * 
 * @author msp
 */
public class ElkRectangle {

    // CHECKSTYLEOFF VisibilityModifier

    /** The X coordinate of this <code>Rectangle</code>. */
    public double x;

    /** The Y coordinate of this <code>Rectangle</code>. */
    public double y;

    /** The width of this <code>Rectangle</code>. */
    public double width;

    /** The height of this <code>Rectangle</code>. */
    public double height;

    /**
     * Constructs a new <code>Rectangle</code>, initialized to location (0,&nbsp;0) and size
     * (0,&nbsp;0).
     */
    public ElkRectangle() {
        this(0, 0, 0, 0);
    }

    /**
     * Constructs and initializes a <code>Rectangle</code> from the specified <code>double</code>
     * coordinates.
     * 
     * @param x
     *            the x coordinate of the upper-left corner of the newly constructed
     *            <code>Rectangle</code>
     * @param y
     *            the y coordinate of the upper-left corner of the newly constructed
     *            <code>Rectangle</code>
     * @param w
     *            the width of the newly constructed <code>Rectangle</code>
     * @param h
     *            the height of the newly constructed <code>Rectangle</code>
     */
    public ElkRectangle(final double x, final double y, final double w, final double h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    /**
     * Sets the location and size of this <code>Rectangle</code> to the specified
     * <code>double</code> values.
     * 
     * @param nx
     *            the X coordinate of the upper-left corner of this <code>Rectangle</code>
     * @param ny
     *            the Y coordinate of the upper-left corner of this <code>Rectangle</code>
     * @param nw
     *            the width of this <code>Rectangle</code>
     * @param nh
     *            the height of this <code>Rectangle</code>
     */
    public void setRect(final double nx, final double ny, final double nw, final double nh) {
        this.x = nx;
        this.y = ny;
        this.width = nw;
        this.height = nh;
    }

    /**
     * @return the (x,y) position of this rectangle.
     */
    public KVector getPosition() {
        return new KVector(x, y);
    }
    
    /**
     * @return the top left coordinate (x,y).
     */
    public KVector getTopLeft() {
        return getPosition();
    }

    /**
     * @return the top right coordinate (x+w,y).
     */
    public KVector getTopRight() {
        return new KVector(x + width, y);
    }

    /**
     * @return the bottom left coordinate (x,y+h).
     */
    public KVector getBottomLeft() {
        return new KVector(x, y + height);
    }

    /**
     * @return the bottom right coordinate (x+w,y+h).
     */
    public KVector getBottomRight() {
        return new KVector(x + width, y + height);
    }
    
    /**
     * Unions the receiver and the given <code>Rectangle</code> objects and puts the result into the
     * receiver.
     * 
     * @param other
     *            the <code>Rectangle</code> to be combined with this instance
     */
    public void union(final ElkRectangle other) {
        double x1 = Math.min(this.x, other.x);
        double y1 = Math.min(this.y, other.y);
        double x2 = Math.max(this.x + this.width, other.x + other.width);
        double y2 = Math.max(this.y + this.height, other.y + other.height);
        if (x2 < x1) {
            double t = x1;
            x1 = x2;
            x2 = t;
        }
        if (y2 < y1) {
            double t = y1;
            y1 = y2;
            y2 = t;
        }
        setRect(x1, y1, x2 - x1, y2 - y1);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Rect[x=" + x + ",y=" + y + ",w=" + width + ",h=" + height + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof ElkRectangle)) {
            return false;
        }
        ElkRectangle other = (ElkRectangle) obj;
        return Objects.equals(x, other.x) && Objects.equals(y, other.y)
                && Objects.equals(width, other.width) && Objects.equals(height, other.height);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

}
