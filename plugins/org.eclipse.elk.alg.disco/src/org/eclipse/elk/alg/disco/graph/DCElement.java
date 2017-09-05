/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.disco.graph;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.disco.ICompactor;
import org.eclipse.elk.alg.disco.transform.IGraphTransformer;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.graph.properties.MapPropertyHolder;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

/**
 * Represents an element of a {@link DCGraph}. Expresses common graph features like nodes, edges, labels, ... simply as
 * polygons, as this abstraction is sufficient for the compaction of different connected components, whereas short
 * hierarchical edges are represented by {@link DCExtension DCExtensions}.
 */
public class DCElement extends MapPropertyHolder {
    /** the serial version UID. */
    private static final long serialVersionUID = 58841189716563145L;

    ///////////////////////////////////////////////////////////////////////////////
    // Variables

    /** Path representing this elements shape based on the positions of polygon vertices . */
    private Path2D.Double shape = new Path2D.Double();
    /** The {@link DCComponent} this element belongs to. */
    private DCComponent cp;
    /** Maintained for drawing purposes in the DisCoGraphRenderer of the DisCoDebugView. */
    private double[] coords;
    /** Maintained for drawing purposes in the DisCoGraphRenderer of the DisCoDebugView. */
    private KVector parentCoords = null;
    /** List holding all short hierarchical edges to or from the parent node (if this DCElement represents a node). */
    private List<DCExtension> extensions = Lists.newArrayList();

    ///////////////////////////////////////////////////////////////////////////////
    // Constructor

    /**
     * Constructs a {@link DCElement} from a list of of points representing the vertices of a closed polygonal path.
     * Typically invoked by a class implementing {@link IGraphTransformer}.
     * 
     * @param polyPath
     *            Ordered list of points expressing the vertices of a Polygon
     */
    public DCElement(final List<Point2D.Double> polyPath) {
        Point2D.Double start = polyPath.get(0);
        shape.moveTo(start.x, start.y);

        for (int i = 1; i < polyPath.size(); i++) {
            Point2D.Double vertice = polyPath.get(i);
            shape.lineTo(vertice.x, vertice.y);
        }
        shape.closePath();
    }

    ///////////////////////////////////////////////////////////////////////////////
    // Public methods

    /**
     * Returns the offset which has to be added to the elements original position to reach its new position after
     * component compactions using a class implementing {@link ICompactor}.
     * 
     * @return Offset
     * @throws NullPointerException
     *             Offset hasn't been set, yet.
     */
    public KVector getOffset() throws NullPointerException {
        return cp.getOffset();
    }

    /**
     * Used for debugging purposes in the DisCoDebugView. Returns the coordinates of the vertices of this element in an
     * easily paintable form.
     *
     * @return coordinates in the form {@code (x_0, y_0, x_1, y_1, ..., x_n, y_n)} for points {@code p_i = (x_i, y_i)}
     *         representing a polygon (a closed path) p_0,p_1,...,p_n,p_0
     */
    public double[] getCoords() {
        if (coords != null) {
            return coords;
        }

        final int six = 6;
        ArrayList<Double> coordList = Lists.newArrayList();
        PathIterator iter = shape.getPathIterator(null);
        double[] returned = new double[six];
        while (!iter.isDone()) {
            for (int i = 0; i < six; i++) {
                returned[i] = Double.NaN;
            }
            iter.currentSegment(returned);
            for (int j = 0; j < six && !Double.isNaN(returned[j]); j++) {
                coordList.add(returned[j]);
            }
            iter.next();
        }
        coords = Doubles.toArray(coordList);
        return coords;
    }

    /**
     * Adds an {@link DCExtension} to this element.
     * 
     * @param extension
     *            {@link DCExtension} to add.
     */
    public void addExtension(final DCExtension extension) {
        extensions.add(extension);
    }

    /**
     * Returns the extensions of this element.
     * 
     * @return list of extensions
     */
    public List<DCExtension> getExtensions() {
        return extensions;
    }

    /**
     * Gets the bounding rectangle of this element.
     * 
     * @return The bounding rectangle of this element
     */
    public Rectangle2D getBounds() {
        return shape.getBounds2D();
    }

    /**
     * Sets the coordinates of the surrounding parent of this element, Needed for the debug view, only.
     * 
     * @param pCoords
     *            Coordinates of the parent of the element
     */
    public void setParentCoords(final KVector pCoords) {
        parentCoords = pCoords;
    }

    /**
     * Returns the coordinates of the parent of this element for debugging purposes.
     * 
     * @return The coordinates of the surrounding parent of the element. {@link <code>null</code>} if not set.
     */
    public KVector getParentcoords() {
        return parentCoords;
    }

    ///////////////////////////////////////////////////////////////////////////////
    // Package private methods

    /**
     * Sets the {@link DCComponent} this element belongs to.
     * 
     * @param comp
     *            The {@link DCComponent} this element belongs to.
     */
    void setComponent(final DCComponent comp) {
        this.cp = comp;
    }

    /**
     * Tests whether this {@link DCElement} intersects with a rectangular area given by the four paramaters of this
     * method.
     * 
     * @param x
     *            X-coordinate of the upper left corner of the rectangle
     * @param y
     *            Y-coordinate of the upper left corner of the rectangle
     * @param width
     *            Width of the rectangle
     * @param height
     *            Height of the rectangle
     * @return true: rectangle intersects with this {@link DCElement}; false: otherwise.
     */
    boolean intersects(final double x, final double y, final double width, final double height) {
        // The implementations of the following Path2D methods enable false positives and negatives in edge cases as a
        // sacrifice for better performance.
        return shape.intersects(x, y, width, height) || shape.contains(x, y, width, height);
    }
}
