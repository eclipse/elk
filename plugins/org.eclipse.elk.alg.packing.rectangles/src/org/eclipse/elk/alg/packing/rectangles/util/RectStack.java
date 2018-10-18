/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/

package org.eclipse.elk.alg.packing.rectangles.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.elk.graph.ElkNode;

/**
 * An object that abstracts a stack of rectangles (for example inside a row). This abstraction makes it easier to handle
 * the rectangles as one as well as expanding the nodes to its border.
 * <p>
 * Do not confuse this with the data type {@link Stack}. Here, Stack means vertically arranged rectangles.
 * </p>
 * 
 * @see RectRow
 * 
 * @author dalu
 */
public class RectStack {
    //////////////////////////////////////////////////////////////////
    // Fields
    /** Stack width, given by the widest child rectangle. */
    private double stackWidth;
    /** Stack height, given by the sum of the children's height. */
    private double stackHeight;
    /** Rectangles contained in this stack. */
    private final List<ElkNode> children = new ArrayList<ElkNode>();
    /** X coordinate of this stack. */
    private double xCoord;
    /** Y coordinate of this stack. */
    private double yCoord;
    /** The row this stack is assigned to. */
    private RectRow parentRow;

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
    public RectStack(final ElkNode first, final double xCoord, final double yCoord, final RectRow parentRow) {
        this.parentRow = parentRow;
        this.children.add(first);
        this.xCoord = xCoord;
        this.yCoord = yCoord;

        this.stackWidth = findMaxWidth();
        this.stackHeight = findTotalHeight();
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
        this.xCoord = newXCoordinate;
        for (ElkNode rect : this.children) {
            rect.setX(this.xCoord);
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
     * @param xCoordinate
     *            new x-coordinate of this stack.
     * @param yCoordinate
     *            new y-coordinate of this stack.
     */
    public void relocateStack(final double xCoordinate, final double yCoordinate) {
        this.xCoord = xCoordinate;
        this.yCoord = yCoordinate;
        adjustChildrensXandY();
    }

    //////////////////////////////////////////////////////////////////
    // Getters and setters.
    /**
     * Gets width of the stack.
     */
    public double getWidth() {
        return stackWidth;
    }

    /**
     * Sets width of the stack.
     * 
     * @param width
     *            new width of this stack.
     */
    public void setWidth(final double width) {
        this.stackWidth = width;
    }

    /**
     * Gets height of the stack.
     */
    public double getHeight() {
        return stackHeight;
    }

    /**
     * Sets height of the stack.
     * 
     * @param height
     *            new height of this stack.
     */
    public void setHeight(final double height) {
        this.stackHeight = height;
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
        return xCoord;
    }

    /**
     * Sets this stack's x-coordinate.
     * 
     * @param xCoordinate
     *            new x-coordinate.
     */
    public void setX(final double xCoordinate) {
        this.xCoord = xCoordinate;
    }

    /**
     * Gets this stack's y/coordinate.
     */
    public double getY() {
        return yCoord;
    }

    /**
     * Sets this stack's y-coordinate.
     * 
     * @param yCoordinate
     *            new y-coordinate.
     */
    public void setY(final double yCoordinate) {
        this.yCoord = yCoordinate;
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
    private void adjustChildrensXandY() {
        double currentYcoord = this.getY();
        for (ElkNode rect : this.children) {
            rect.setLocation(this.xCoord, currentYcoord);
            currentYcoord += rect.getHeight();
        }
    }

    /**
     * Adjusts size of stack after the addition of a rectangle. Notifies parent.
     * 
     * @param rect
     *            the rectangle that was assigned to this stack.
     */
    private void adjustSizeAdd(final ElkNode rect) {
        this.stackWidth = Math.max(this.stackWidth, rect.getWidth());
        this.stackHeight += rect.getHeight();
        this.parentRow.notifyAboutNodeChange();
    }

    /**
     * Adjusts size of stack after the removal of a rectangle. Notifies parent.
     */
    private void adjustSizeRem() {
        this.stackWidth = findMaxWidth();
        this.stackHeight = findTotalHeight();
        this.parentRow.notifyAboutNodeChange();
    }

    /**
     * Finds maximum width of this object's children.
     */
    private double findMaxWidth() {
        double maxStackWidth = Double.MIN_VALUE;
        for (ElkNode element : this.children) {
            maxStackWidth = Math.max(maxStackWidth, element.getWidth());
        }
        return maxStackWidth;
    }

    /**
     * Finds total height of this object's children.
     */
    private double findTotalHeight() {
        double height = 0;
        for (ElkNode element : this.children) {
            height += element.getHeight();
        }
        return height;
    }
}
