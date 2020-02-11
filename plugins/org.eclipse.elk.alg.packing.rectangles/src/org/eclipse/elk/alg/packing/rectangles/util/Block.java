/*******************************************************************************
 * Copyright (c) 2020 sdo and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  *******************************************************************************/
package org.eclipse.elk.alg.packing.rectangles.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.graph.ElkNode;

/**
 * A block is a ordered set of rectangles that shall be layouted left to right. The height of these rectangles is "not that different".
 * "Not that different" means that the smallest rectangle is bigger than half the height of the highest rectangle.
 * The minimum/maximum width and height are defined by their rectangles and is kept up to date on add and remove of rectangles.
 * 
 * Depending on the width and height these rectangles can be layouted.
 * For a given height/width one can calculate the corresponding minimum height/width (if possible).
 * 
 * @author sdo
 *
 */
public class Block {
    //////////////////////////////////////////////////////////////////
    // Fields
    /** Minimal width + spacing. All rectangles are in one column. */
    private double minWidth;
    /** Maximum width + spacing. All rectangles are in one row. */
    private double maxWidth;
    /** Current width + spacing. This is corresponds to the maxWidth if no other value is set by the algorithm. */
    private double width;
    /** Maximum width in the last row + spacing. */
    private double widthInRow;
    /** Minimal height + spacing. All rectangles are in one row. */
    private double minHeight;
    /** Smallest rect height + spacing */
    private double smallestRectHeight = Double.MAX_VALUE;
    /** Average block height */
    private double averageHeight;
    /** Maximum height + spacing. All rectangles are in one column. */
    private double maxHeight;
    /** Current height + spacing. This is corresponds to the minHeight if no other value is set by the algorithm. */
    private double height;
    /** Maximum in the last row height + spacing. */
    private double maxHeightInRow;
    /** Rectangles contained in this block. */
    private final List<ElkNode> children = new ArrayList<ElkNode>();
    /** X coordinate of this block. */
    private double x;
    /** Y coordinate of this block. */
    private double y;
    /** X coordinate of the current row of this block. */
    private double rowX;
    /** Y coordinate of the current row of this block. */
    private double rowY;
    /** The row this block is assigned to. */
    private RectRow parentRow;
    /** Spacing between two nodes */
    private double nodeNodeSpacing;
    
    private boolean fixed;
    
    private boolean positionFixed;

    //////////////////////////////////////////////////////////////////
    // Constructor
    
    /**
     * Creates a new block.
     * 
     * @param first
     *            first rectangle of the stack.
     * @param xCoord
     *            x-coordinate of the stack.
     * @param yCoord
     *            y-coordinate of the stack.
     * @param parentRow
     *            row this stack is assigned to.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     */
    public Block(final double xCoord, final double yCoord, final RectRow parentRow,
            final double nodeNodeSpacing) {
        this.nodeNodeSpacing = nodeNodeSpacing;
        this.parentRow = parentRow;
        this.x = xCoord;
        this.y = yCoord;
        this.rowX = x;
        this.rowY = y;

        this.width = 0;
        this.height = 0;
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
        rect.setX(rowX);
        this.rowX += rect.getWidth() + nodeNodeSpacing;
        rect.setY(rowY);
        adjustSizeAdd(rect);
    }
    
    public void addChildInNewRow(final ElkNode rect) {
        this.children.add(rect);
        rect.setX(x);
        this.rowX = x + rect.getWidth() + nodeNodeSpacing;
        this.rowY += maxHeightInRow;
        rect.setY(rowY);
        this.maxHeightInRow = 0;
        this.widthInRow = 0;
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
        adjustSizeAfterRemove();
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
     * Relocates this block by adjusting its x- and y-coordinate. Passes this change to this block's children.
     * 
     * @param xCoord
     *            new x-coordinate of this block.
     * @param yCoord
     *            new y-coordinate of this block.
     */
    public void setLocation(final double xCoord, final double yCoord) {
        adjustChildrensXandY(xCoord - this.x, yCoord - this.y);
        this.rowX += xCoord - this.x;
        this.rowY += yCoord - this.y;
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

    /**
     * @return the minWidth
     */
    public double getMinWidth() {
        return minWidth;
    }

    /**
     * @return the maxWidth
     */
    public double getMaxWidth() {
        return maxWidth;
    }

    /**
     * @return the minHeight
     */
    public double getMinHeight() {
        return minHeight;
    }

    /**
     * @return the maxHeight
     */
    public double getMaxHeight() {
        return maxHeight;
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
        this.widthInRow += rect.getWidth() + nodeNodeSpacing;
        this.maxWidth = Math.max(maxWidth, widthInRow);
        this.minWidth = Math.max(minWidth, rect.getWidth() + nodeNodeSpacing);
        this.width = maxWidth;

        this.smallestRectHeight = Math.min(smallestRectHeight, rect.getHeight() + nodeNodeSpacing);
        this.maxHeightInRow = Math.max(maxHeightInRow, rect.getHeight() + nodeNodeSpacing);
        this.maxHeight += rect.getHeight() + nodeNodeSpacing;
        this.minHeight = Math.max(minHeight, rect.getHeight() + nodeNodeSpacing);
        this.height = maxHeightInRow + rowY - y;
        this.averageHeight = maxHeight / this.children.size();
        this.parentRow.notifyAboutNodeChange();
    }
    

    /**
     * @param height
     * @return width of the rectangle block with specified height
     */
    public double getWidthForTargetHeight(double height) {
        if (this.maxHeight <= height) {
            return this.minWidth;
        }
        if (placeRectsIn(this.minWidth, height)) {
            return this.minWidth;
        }
        int columns = 2;
        while (!placeRectsIn(columns * this.minWidth, height) && columns * this.minWidth < this.width) {
            columns++;
        }
        return columns * this.minWidth;
    }
    
    public double getHeightForTargetWidth(double width) {
        ElkRectangle bounds = placeRectsIn(width, false);
        return bounds.height;
    }
    
    private ElkRectangle placeRectsIn(double width, boolean placeRects) {
        double currentX = 0;
        double currentY = 0;
        double currentWidth = 0;
        double currentHeight = 0;
        double maxHeightInRow = 0;
        double widthInRow = 0;
        for (ElkNode rect : children) {
            if (currentX + rect.getWidth() + nodeNodeSpacing > width) {
                currentX = 0;
                currentY += maxHeightInRow + nodeNodeSpacing;
                currentWidth = Math.max(currentWidth, widthInRow);
                currentHeight += maxHeightInRow;
                maxHeightInRow = 0;
                widthInRow = 0;
            }
            widthInRow += rect.getWidth() + nodeNodeSpacing;
            maxHeightInRow = Math.max(maxHeightInRow, rect.getHeight());
            if (placeRects) {
                rect.setX(currentX + x);
                rect.setY(currentY + y);
            }
            currentX += rect.getWidth() + nodeNodeSpacing;
        }
        currentWidth = Math.max(currentWidth, widthInRow);
        currentHeight += maxHeightInRow;
        return new ElkRectangle(x, y, currentWidth, currentHeight);
    }
    
    private boolean placeRectsIn(double width, double height, boolean placeRects) {
        ElkRectangle bounds = placeRectsIn(width, placeRects);
        return bounds.width < width && bounds.height < height;
    }
    
    /**
     * @param width
     * @param height
     * @return false if it is not possible
     */
    public boolean placeRectsIn(double width, double height) {
        return placeRectsIn(width, height, true);
    }
    
    public void placeRectsIn(double width) {
        placeRectsIn(width, true);
    }

    /**
     * Adjusts size of stack after the removal of a rectangle. Notifies parent.
     */
    private void adjustSizeAfterRemove() {
        // TODO this is not correct and everything is missing here.
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

    /**
     * @return the rowY
     */
    public double getRowY() {
        return rowY;
    }

    /**
     * @return the widthInRow
     */
    public double getWidthInRow() {
        return widthInRow;
    }

    /**
     * @return the rowX
     */
    public double getRowX() {
        return rowX;
    }

    /**
     * @return the smallestRectHeight
     */
    public double getSmallestRectHeight() {
        return smallestRectHeight;
    }

    /**
     * @return the averageHeight
     */
    public double getAverageHeight() {
        return averageHeight;
    }

    /**
     * @return the fixed
     */
    public boolean isFixed() {
        return fixed;
    }

    /**
     * @param fixed the fixed to set
     */
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    /**
     * @return the positionFixed
     */
    public boolean isPositionFixed() {
        return positionFixed;
    }

    /**
     * @param positionFixed the positionFixed to set
     */
    public void setPositionFixed(boolean positionFixed) {
        this.positionFixed = positionFixed;
    }
}
