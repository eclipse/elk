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
import java.util.List;

import org.eclipse.elk.graph.ElkNode;

/**
 * This class corresponds to a row in a block of rectangles.
 * It introduces one abstraction layer for better handling of rows in a block.
 *
 */
public class BlockRow {
    
    /**
     * y coordinate of the row.
     */
    private double y;
    /**
     * Width of the row + nodeNodeSpacing
     */
    private double width = 0;
    /**
     * Height of the row.
     * Corresponds to the height of the biggest node + nodeNodeSpacing
     */
    private double height = 0;
    /**
     * Maximum width of the row.
     * No new rectangle can be added if the maximum width is exceeded.
     */
    private double maxWidth;
    /**
     * Spacing between two nodes.
     */
    private final double nodeNodeSpacing;
    private List<ElkNode> rects = new ArrayList<>();
    
    /**
     * Create a new BlockRow.
     * @param y y coordinate of the row
     * @param maxWidth maximum width, same as the block.
     * @param nodeNodeSpacing Spacing between two rectangles.
     */
    public BlockRow(double y, double maxWidth, double nodeNodeSpacing) {
        this.y = y;
        this.maxWidth = maxWidth;
        this.nodeNodeSpacing = nodeNodeSpacing;
    }
    
    /**
     * Removes all rectangles from the row and resets width and height.
     */
    public void clear() {
        width = 0;
        height = 0;
        rects = new ArrayList<>();
    }

    /**
     * Adds a rectangle to the row.
     * @param rect The rectangle
     * @return true if the rectangle fits in this row.
     */
    public boolean addRectangle(ElkNode rect) {
        if (!canAddRectangle(rect)) {
            return false;
        }
        rect.setX(width);
        rect.setY(y);
        this.height = Math.max(this.height, rect.getHeight() + nodeNodeSpacing);
        this.width += rect.getWidth() + nodeNodeSpacing;
        return true;
    }
    
    /**
     * Checks whether the given rectangle fits in this row
     * @param rect The rectangle.
     * @return true, if the rectangle fits in the row without exceeding the maximum width.
     */
    public boolean canAddRectangle(ElkNode rect) {
        return this.width + nodeNodeSpacing + rect.getWidth() <= maxWidth;
    }
    
    /**
     * Checks whether the given rectangle fits in this row
     * @param rect The rectangle.
     * @return true, if the rectangle fits in the row without exceeding the maximum width.
     */
    public boolean canAddRectangles(List<ElkNode> rects) {
        double widthToAdd = rects.size() * nodeNodeSpacing;
        for (ElkNode rect : rects) {
            widthToAdd += rect.getWidth();
        }
        return this.width + widthToAdd <= maxWidth;
    }
    
    /**
     * Removes a rectangle from the row.
     * Updates the height and width of the row.
     * @param rect The rectangle.
     * @param update If enabled the x and y coordinate of the containing rectangles
     *  and the width and height of the row are updated.
     */
    public void removeRectangle(ElkNode rect, boolean update) {
        rects.remove(rect);
        if (update) {
            updateRow();
        }
    }
    
    /**
     * Updates the coordinates of all rectangles and the height and width of the row.
     */
    public void updateRow() {
        double width = 0;
        double height = 0;
        for (ElkNode rect : rects) {
            rect.setX(width);
            rect.setY(this.y);
            width += rect.getWidth() + nodeNodeSpacing;
            height = Math.max(height, rect.getHeight() + nodeNodeSpacing);
        }
        this.width = width;
        this.height = height;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @return the maxWidth
     */
    public double getMaxWidth() {
        return maxWidth;
    }

    /**
     * @param maxWidth the maxWidth to set
     */
    public void setMaxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
    }

    /**
     * @return the nodes
     */
    public List<ElkNode> getNodes() {
        return rects;
    }
}
