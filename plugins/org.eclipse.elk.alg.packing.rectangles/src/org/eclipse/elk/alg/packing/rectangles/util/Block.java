/*******************************************************************************
 * Copyright (c) 2020 Kiel University.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  *******************************************************************************/
package org.eclipse.elk.alg.packing.rectangles.util;

import java.util.ArrayList;
import java.util.LinkedList;
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
 */
public class Block {
    //////////////////////////////////////////////////////////////////
    // Fields
    private double smallestRectWidth = Double.POSITIVE_INFINITY;
    /** Minimal width + spacing. All rectangles are in one column. */
    private double minWidth;
    /** Current width + spacing. This is corresponds to the maxWidth if no other value is set by the algorithm. */
    private double width;
    /** Minimal height + spacing. All rectangles are in one row. */
    private double minHeight;
    /** Smallest rect height + spacing */
    private double smallestRectHeight = Double.POSITIVE_INFINITY;
    /** Average block height */
    private double averageHeight;
    /** Maximum height + spacing. All rectangles are in one column. */
    private double maxHeight;
    /** Current height + spacing. This is corresponds to the minHeight if no other value is set by the algorithm. */
    private double height;
    /** Rectangles contained in this block. */
    private final List<ElkNode> children = new ArrayList<ElkNode>();
    /** The rectangles in children assigned to rows. */
    private final List<BlockRow> rows = new ArrayList<BlockRow>();
    /** X coordinate of this block. */
    private double x;
    /** Y coordinate of this block. */
    private double y;
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
        if (rows.isEmpty()) {
            rows.add(new BlockRow(this.x, this.y, nodeNodeSpacing));
        }
        this.children.add(rect);
        this.rows.get(rows.size() - 1).addRectangle(rect);
        adjustSizeAdd(rect);
    }
    
    public void addChildInNewRow(final ElkNode rect) {
        this.children.add(rect);
        BlockRow lastRow = getLastRow();
        rows.add(new BlockRow(this.x, lastRow.getY() + lastRow.getHeight(), nodeNodeSpacing));
        getLastRow().addRectangle(rect);
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
        for (BlockRow row : rows) {
            if (row.getNodes().contains(rect)) {
                row.removeRectangle(rect, true);
                if (row.getNodes().isEmpty()) {
                    rows.remove(row);
                }
                break;
            }
        }
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
        for (BlockRow row : rows) {
            row.setX(row.getX() + xCoord - this.x);
            row.setY(row.getY() + yCoord - this.y);
        }
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
     * Gets this stack's y/coordinate.
     */
    public double getY() {
        return y;
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
     * Adjusts size of block after the addition of a rectangle. Notifies parent.
     * 
     * @param rect
     *            the rectangle that was assigned to this stack.
     */
    private void adjustSizeAdd(final ElkNode rect) {
        double widthOflastRow = getLastRow().getWidth();
        this.smallestRectWidth = Math.min(smallestRectWidth, rect.getWidth() + nodeNodeSpacing);
        this.width = Math.max(width, widthOflastRow);
        this.minWidth = Math.max(minWidth, rect.getWidth() + nodeNodeSpacing);
        
        this.smallestRectHeight = Math.min(smallestRectHeight, rect.getHeight() + nodeNodeSpacing);
        this.maxHeight += rect.getHeight() + nodeNodeSpacing;
        this.minHeight = Math.max(minHeight, rect.getHeight() + nodeNodeSpacing);
        double totalHeight = 0;
        for (BlockRow row : rows) {
            totalHeight += row.getHeight();
        }
        this.height = totalHeight;
        this.averageHeight = maxHeight / this.children.size();
        this.parentRow.notifyAboutNodeChange();
    }
    

    /**
     * @param height
     * @return width of the rectangle block with specified height
     */
    public double getWidthForTargetHeight(double height) {
        // Check whether the block would just fit if all rectangles are drawn below each other.
        if (this.maxHeight <= height) {
            return this.minWidth;
        }
        // Check if the minimal width of the block is enough. This can be the case if some rows have more than one rectangle.
        if (placeRectsIn(this.minWidth, height, false)) {
            return this.minWidth;
        }
        
        // Try out multiples of the widest rectangle as width.
        // TODO check break criterium, the width might not be the maximum width possible.
        int columns = 2;
        while (!placeRectsIn(columns * this.minWidth, height, false) && columns * this.minWidth < this.width) {
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
        double currentY = this.y;
        double currentWidth = 0;
        double currentHeight = 0;
        double maxHeightInRow = 0;
        double widthInRow = 0;
        int row = 0;
        if (placeRects) {
            rows.clear();
            rows.add(new BlockRow(this.x, this.y, nodeNodeSpacing));
        }
        for (ElkNode rect : children) {
            if (currentX + rect.getWidth() + nodeNodeSpacing > width) {
                currentX = 0;
                currentY += maxHeightInRow;
                currentWidth = Math.max(currentWidth, widthInRow);
                currentHeight += maxHeightInRow;
                maxHeightInRow = 0;
                widthInRow = 0;
                if (placeRects) {
                    row++;
                    rows.add(new BlockRow(this.x, currentY, nodeNodeSpacing));
                }
            }
            widthInRow += rect.getWidth() + nodeNodeSpacing;
            maxHeightInRow = Math.max(maxHeightInRow, rect.getHeight() + nodeNodeSpacing);
            if (placeRects) {
                rows.get(row).addRectangle(rect);
            }
            currentX += rect.getWidth() + nodeNodeSpacing;
        }
        currentWidth = Math.max(currentWidth, widthInRow);
        currentHeight += maxHeightInRow;
        if (placeRects) {
            this.width = currentWidth;
            this.height = currentHeight;
            this.parentRow.notifyAboutNodeChange();
        }
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
        // Calculate new width and height
        double newWidth = 0;
        double newHeight = 0;
        List<BlockRow> rowsToDelete = new LinkedList<>();
        for (BlockRow row : this.rows) {
            if (row.getNodes().isEmpty()) {
                rowsToDelete.add(row);
            } else {
                newWidth = Math.max(newWidth, row.getWidth());
                newHeight += row.getHeight();
            }
        }
        rows.removeAll(rowsToDelete);
        this.height = newHeight;
        this.width = newWidth;
        
        // Calculate new smallest/biggest rectangle.
        this.minWidth = 0;
        this.minHeight = 0;
        this.maxHeight = 0;
        this.smallestRectHeight = Double.POSITIVE_INFINITY;
        this.smallestRectWidth = Double.POSITIVE_INFINITY;
        for (ElkNode rect : children) {
            this.smallestRectWidth = Math.min(smallestRectWidth, rect.getWidth() + nodeNodeSpacing);
            this.minWidth = Math.max(minWidth, rect.getWidth() + nodeNodeSpacing);
            this.minHeight = Math.max(minHeight, rect.getHeight() + nodeNodeSpacing);
            this.smallestRectHeight = Math.min(smallestRectHeight, rect.getHeight() + nodeNodeSpacing);
            this.maxHeight += rect.getHeight() + nodeNodeSpacing;
        }
        this.averageHeight = maxHeight / this.children.size();
        
        this.parentRow.notifyAboutNodeChange();
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

    /**
     * @return the rows
     */
    public List<BlockRow> getRows() {
        return rows;
    }

    /**
     * @return The x position at which the next rect would be added to the last row.
     */
    public double getLastRowNewX() {
        BlockRow lastRow = getLastRow();
        return lastRow.getX() + lastRow.getWidth();
    }

    /**
     * @return The y position at which the rectangles of the last row are placed.
     */
    public double getLastRowY() {
        BlockRow lastRow = getLastRow();
        return lastRow.getY();
    }
    
    public BlockRow getLastRow() {
        return this.rows.get(rows.size() - 1);
    }
}
