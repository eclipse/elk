/*******************************************************************************
 * Copyright (c) 2018, 2020 Kiel University and others.
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

/**
 * A row of rectangles inside a given bounding box. Tracks the blocks inside the row and the row's width and height.
 * A might have stacks of blocks for ordering them after the compaction step.
 */
public class RectRow {
    //////////////////////////////////////////////////////////////////
    // Fields.
    /** Height of row, given by the highest stack. */
    private double height = 0;
    /** Sum of this row's stack's widths. */
    private double width = 0;
    /** Y-coordinate of this row. */
    private double y;
    /** This row's blocks. */
    private final List<Block> children = new ArrayList<Block>();
    /**
     * This row's stacks of blocks. 
     * Used during the compaction step for better handling and during the stuffing (expansion) process.
     */
    private final List<BlockStack> stacks = new ArrayList<BlockStack>();

    //////////////////////////////////////////////////////////////////
    // Constructors.

    /**
     * Creates a row.
     * 
     * @param y The y-coordinate of the row.
     */
    public RectRow(final double y) {
        this.y = y;
    }

    //////////////////////////////////////////////////////////////////
    // Public methods.

    /**
     * Called by one of its assigned blocks when a change was made to respective block like removing or adding a
     * rectangle. By removing or adding a rectangle, the blocks dimensions change which might affect this rows
     * dimensions. This method recalculates its dimensions.
     */
    protected void notifyAboutNodeChange() {
        double totalStackWidth = 0;
        double newMaxHeight = Double.NEGATIVE_INFINITY;

        for (Block child : this.children) {
            totalStackWidth += child.getWidth();
            newMaxHeight = Math.max(newMaxHeight, child.getHeight());
        }

        this.width = totalStackWidth;
        this.height = newMaxHeight;
    }
    
    public void expand(double width, double additionalHeight) {
        double additionalWidth = width - this.width;
        double additionalWidthPerStack = additionalWidth / stacks.size();
        int index = 0;
        for (BlockStack stack : stacks) {
            double additionalHeightForStack = this.getHeight() - stack.getHeight() + additionalHeight;
            stack.setLocation(stack.getX() + index * additionalWidthPerStack, stack.getY());
            stack.expand(additionalWidthPerStack, additionalHeightForStack);
            index++;
        }
    }

    /**
     * Puts blocks with the same x coordinate in stacks for better expansion calculation.
     */
    public void calculateBlockStacks() {
        double currentX = -1;
        for (Block block : children) {
            if (block.getX() != currentX) {
                stacks.add(new BlockStack(block.getX(), this.y));
                stacks.get(stacks.size() - 1).addBlock(block);
                currentX = block.getX();
            } else {
                stacks.get(stacks.size() - 1).addBlock(block);
            }
        }
        
    }

    /**
     * Gets the first block of this row.
     */
    public Block getFirstBlock() {
        return this.children.get(0);
    }

    /**
     * Gets the last block of this row.
     */
    public Block getLastBlock() {
        return this.children.get(this.children.size() - 1);
    }

    /**
     * Amount of stacks assigned to this row.
     */
    public int getNumberOfAssignedBlocks() {
        return this.children.size();
    }

    //////////////////////////////////////////////////////////////////
    // Add and remove methods.
    /**
     * Assigns stack to the row. Adjusts height and width of the row. Does not adjust the stack's coordinates.
     * 
     * @param stack The stack to add.
     */
    public void addBlock(final Block block) {
        this.children.add(block);

        this.height = Math.max(this.height, block.getHeight());
        this.width += block.getWidth();
    }

    /**
     * Removes specified stack from this row. Adjusts width and height.
     * @param stack The stack to remove.
     */
    public void removeBlock(final Block block) {
        this.children.remove(block);
        this.width -= block.getWidth();

        double newMaxHeight = Double.MIN_VALUE;
        for (Block child : this.children) {
            newMaxHeight = Math.max(newMaxHeight, child.getHeight());
        }
        this.height = newMaxHeight;
    }

    //////////////////////////////////////////////////////////////////
    // Getters and setters.
    /**
     * Gets height of this row.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the height of this row.
     * 
     * @param height
     *            new height of this row.
     */
    public void setHeight(final double height) {
        this.height = height;
    }

    /**
     * Gets width of this row.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets width of this row.
     * 
     * @param width
     *            width of this row.
     */
    public void setWidth(final double width) {
        this.width = width;
    }

    /**
     * Gets y-coordinate of this row.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets y-coordinate of this row.
     * 
     * @param y The new y-coordinate of this row.
     */
    public void setY(final double y) {
        double yChange = y - this.y;
        for (BlockStack stack : stacks) {
            stack.setLocation(stack.getX(), stack.getY() + yChange);
        }
        this.y = y;
    }

    /**
     * Gets stacks assigned to this row.
     */
    public List<Block> getChildren() {
        return children;
    }

    /**
     * @return the stacks
     */
    public List<BlockStack> getStacks() {
        return stacks;
    }
}
