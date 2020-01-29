/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.packing.rectangles.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.elk.graph.ElkNode;

/**
 * An object that abstracts a stack of rectangles (for example inside a row). This abstraction makes it easier to handle
 * the rectangles as one as well as expanding the nodes to its border.
 * <p>
 * Do not confuse this with the data type {@link java.util.Stack}. Here, Stack means vertically arranged rectangles.
 * </p>
 * 
 * @see RectRow
 */
public class RectStack {
    //////////////////////////////////////////////////////////////////
    // Fields
    /** Stack width, given by the widest child rectangle. */
    private double width;
    /** Stack height, given by the sum of the children's height. */
    private double height;
    /** Rectangles contained in this stack. */
    private final List<ElkNode> children = new ArrayList<ElkNode>();
    /** X coordinate of this stack. */
    private double x;
    /** Y coordinate of this stack. */
    private double y;
    /** The row this stack is assigned to. */
    private RectRow parentRow;
    /** Spacing between two nodes */
    private double nodeNodeSpacing;

    //////////////////////////////////////////////////////////////////
    // Constructor
    /**
     * Creates a vertical rectangle stack with the given element being the first element. Adjusts height and width of
     * this stack accordingly. Sets parent row. Does not add itself as part of the parent row.
     * 
     * @param first
     *            first rectangle of the stack.
     * @param xCoord
     *            x-coordinate of the stack.
     * @param yCoord
     *            y-coordinate of the stack.
     * @param parentRow
     *            row this stack is assigned to.
     */
    public RectStack(final ElkNode first, final double xCoord, final double yCoord, final RectRow parentRow, final double nodeNodeSpacing) {
        this.nodeNodeSpacing = nodeNodeSpacing;
        this.parentRow = parentRow;
        this.children.add(first);
        this.x = xCoord;
        this.y = yCoord;

        this.width = findMaxWidth();
        this.height = findTotalHeight();
    }

    //////////////////////////////////////////////////////////////////
    // Special methods.
    /**
     * Adds given rectangle to this stack. Adjusts height and width of the stack accordingly. Does not set the
     * coordinates of the ElkNode. Informs parent about size change.
     * 
     * @param rect
     *            rectangle that is added to this stack.
     */
    public void addChild(final ElkNode rect) {
        this.children.add(rect);
        adjustSizeAdd(rect);
    }

    /**
     * Removes element from bottom of the abstract stack. Sets height and width accordingly. Informs parent about size
     * change.
     * 
     * @param rect
     *            rectangle that is removed from this stack.
     */
    public void removeChild(final ElkNode rect) {
        this.children.remove(rect);
        adjustSizeRem();
    }

    /**
     * Gets the first (top) rectangle of this stack.
     */
    public ElkNode getFirstRectangle() {
        return this.children.get(0);
    }

    /**
     * Gets the last (bottom) rectangle of this stack.
     */
    public ElkNode getLastRectangle() {
        return this.children.get(this.children.size() - 1);
    }

    /**
     * Returns true, if this stack has no children assigned to it, and false otherwise.
     */
    public boolean hasNoRectanglesAssigned() {
        return this.getNumberOfRectangles() == 0;
    }

    /**
     * Returns the number of rectangles assigned to this stack, given by the amount of ElkNodes in this object's
     * elements list.
     */
    public int getNumberOfRectangles() {
        return this.children.size();
    }

    /**
     * Adjusts this stack's x-value as well as its children's x-values to the new given x-coordinate.
     * 
     * @param newXCoordinate
     *            The new adjusted x-coordinate.
     */
    public void adjustXRecursively(final double newXCoordinate) {
        this.x = newXCoordinate;
        for (ElkNode rect : this.children) {
            rect.setX(this.x);
        }
    }

    /**
     * Shifts this stack's children vertically up by the given amount. Calculated by {@code i.Y = i.Y - dist}.
     * 
     * @param dist
     *            Amount that the rectangles are shifted up by.
     */
    public void decreaseChildrensY(final double dist) {
        for (ElkNode rect : this.children) {
            rect.setY(rect.getY() - dist);
        }
    }

    /**
     * Shifts this stack's children vertically down by the given amount. Calculated by {@code i.Y = i.Y + dist}.
     * 
     * @param dist
     *            Difference that the rectangles are shifted up by.
     */
    public void increaseChildrensY(final double dist) {
        for (ElkNode rect : this.children) {
            rect.setY(rect.getY() + dist);
        }
    }

    /**
     * Relocates this stack by adjusting its x- and y-coordinate. Passes this change to this stack's children.
     * 
     * @param xCoord
     *            new x-coordinate of this stack.
     * @param yCoord
     *            new y-coordinate of this stack.
     */
    public void relocateStack(final double xCoord, final double yCoord) {
        adjustChildrensXandY(xCoord - this.x, yCoord - this.y);
        this.x = xCoord;
        this.y = yCoord;
    }

    //////////////////////////////////////////////////////////////////
    // Getters and setters.
    /**
     * Gets width of the stack.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets width of the stack.
     * 
     * @param width
     *            new width of this stack.
     */
    public void setWidth(final double width) {
        this.width = width;
    }

    /**
     * Gets height of the stack.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets height of the stack.
     * 
     * @param height
     *            new height of this stack.
     */
    public void setHeight(final double height) {
        this.height = height;
    }

    /**
     * Gets this stack's children.
     */
    public List<ElkNode> getChildren() {
        return children;
    }

    /**
     * Gets this stack's x-coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets this stack's x-coordinate.
     * 
     * @param xCoordinate
     *            new x-coordinate.
     */
    public void setX(final double xCoordinate) {
        this.x = xCoordinate;
    }

    /**
     * Gets this stack's y/coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets this stack's y-coordinate.
     * 
     * @param yCoordinate
     *            new y-coordinate.
     */
    public void setY(final double yCoordinate) {
        this.y = yCoordinate;
    }

    /**
     * Gets the row this stack is assigned to.
     */
    public RectRow getParentRow() {
        return parentRow;
    }

    /**
     * Sets the row this stack belongs to.
     * 
     * @param parentRow
     *            row this stack is assigned to.
     */
    public void setParentRow(final RectRow parentRow) {
        this.parentRow = parentRow;
    }

    //////////////////////////////////////////////////////////////////
    // Additional useful methods.

    /**
     * Shifts this stack's children vertically and horizontally according to this stack's coordinates. Needed when
     * rectangles and stack are out of sync (e.g. when a rectangle changes stacks.)
     */
    private void adjustChildrensXandY(double xDiff, double yDiff) {
        for (ElkNode rect : this.children) {
            rect.setLocation(rect.getX() + xDiff, rect.getY() + yDiff);
        }
    }

    /**
     * Adjusts size of stack after the addition of a rectangle. Notifies parent.
     * 
     * @param rect
     *            the rectangle that was assigned to this stack.
     */
    private void adjustSizeAdd(final ElkNode rect) {
        this.width = Math.max(this.width, rect.getWidth() + nodeNodeSpacing);
        this.height += rect.getHeight() + nodeNodeSpacing;
        this.parentRow.notifyAboutNodeChange();
    }

    /**
     * Adjusts size of stack after the removal of a rectangle. Notifies parent.
     */
    private void adjustSizeRem() {
        this.width = findMaxWidth();
        this.height = findTotalHeight();
        this.parentRow.notifyAboutNodeChange();
    }

    /**
     * Finds maximum width of this object's children.
     */
    private double findMaxWidth() {
        double maxStackWidth = Double.MIN_VALUE;
        for (ElkNode element : this.children) {
            maxStackWidth = Math.max(maxStackWidth, element.getWidth() + nodeNodeSpacing);
        }
        return maxStackWidth;
    }

    /**
     * Finds total height of this object's children.
     */
    private double findTotalHeight() {
        double totalHeight = 0;
        for (ElkNode element : this.children) {
            totalHeight += element.getHeight() + nodeNodeSpacing;
        }
        return totalHeight;
    }
}
