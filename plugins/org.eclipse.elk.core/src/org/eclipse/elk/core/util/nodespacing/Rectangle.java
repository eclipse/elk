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
package org.eclipse.elk.core.util.nodespacing;

/**
 * Class resembles basic functionality of {@link java.awt.geom.Rectangle2D}. This way it is possible
 * to avoid awt dependencies in klay layered's code.
 * 
 * @author msp
 */
public class Rectangle {

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
    public Rectangle() {
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
    public Rectangle(final double x, final double y, final double w, final double h) {
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
     * Unions the receiver and the given <code>Rectangle</code> objects and puts the result into the
     * receiver.
     * 
     * @param other
     *            the <code>Rectangle</code> to be combined with this instance
     */
    public void union(final Rectangle other) {
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

}
