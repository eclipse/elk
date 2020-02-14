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
 * A row of rectangles inside a given bounding box. Tracks the rectangles inside the row and the row's width and height.
 * 
 * @see RectStack
 */
public class RectRow {
    //////////////////////////////////////////////////////////////////
    // Fields
    /** Height of row, given by the highest stack. */
    private double height = 0;
    /** Sum of this row's stack's widths. */
    private double width = 0;
    /** Y-coordinate of this row. */
    private double y;
    /** This row's stacks. */
    private final List<Block> children = new ArrayList<Block>();

    //////////////////////////////////////////////////////////////////
    // Constructors.

    /**
     * Creates a row.
     * 
     * @param yCoord
     *            y-coordinate of the row.
     */
    public RectRow(final double yCoord) {
        this.y = yCoord;
    }

    //////////////////////////////////////////////////////////////////
    // Special public methods.

    /**
     * Called by one of its assigned stacks when a change was made to respective stack like removing or adding a
     * rectangle. By removing or adding a rectangle, the stacks dimensions change which might affect this rows
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

    /**
     * Decreases the y-coordinates of this row and its children (stacks and their respective rectangles).
     * 
     * @param verticalDecrease
     *            amount by which this row's children's y-coordinates should be decreased.
     */
//    public void decreaseYRecursively(final double verticalDecrease) {
//        this.y -= verticalDecrease;
//        for (Block child : this.children) {
//            child.setY(child.getY() - verticalDecrease);
//            child.decreaseChildrensY(verticalDecrease);
//        }
//    }

    /**
     * Increases the y-coordinates of this row and its children (stacks and their respective rectangles).
     * 
     * @param verticalIncrease
     *            amount by which this row's children's y-coordinates should be increased.
     */
//    public void increaseYRecursively(final double verticalIncrease) {
//        this.y += verticalIncrease;
//        for (Block stack : this.children) {
//            stack.setY(stack.getY() + verticalIncrease);
//            stack.increaseChildrensY(verticalIncrease);
//        }
//    }

    /**
     * Decreases the x-coordinates of all children (stacks and their respective rectangles) by the given amount.
     * 
     * @param horizontalDecrease
     *            amount by which this row's children's x-coordinates should be decreased.
     */
//    public void decreaseXRecursively(final double horizontalDecrease) {
//        for (Block stack : this.children) {
//            stack.adjustXRecursively(stack.getX() - horizontalDecrease);
//        }
//    }

    /**
     * Gets the first stack of this row.
     */
    public Block getFirstBlock() {
        return this.children.get(0);
    }

    /**
     * Returns the width of the first stack of this row.
     */
    public double getFirstStackWidth() {
        return this.getFirstBlock().getWidth();
    }

    /**
     * Gets the width of the first rectangle of the first stack in the row.
     */
    public double getFirstRectFirstStackWidth() {
        return this.getFirstRectFirstStack().getWidth();
    }

    /**
     * Gets the height of the first rectangle of the first stack in the row.
     */
    public double getFirstRectFirstStackHeight() {
        return this.getFirstRectFirstStack().getHeight();
    }

    /**
     * Gets the last block of this row.
     */
    public Block getLastBlock() {
        return this.children.get(this.children.size() - 1);
    }

    /**
     * Gets the width of the last block of this row.
     */
    public double getLastBlockWidth() {
        return this.getLastBlock().getWidth();
    }

    /**
     * Gets the height of the last stack of this row.
     */
    public double getLastBlockHeight() {
        return this.getLastBlock().getHeight();
    }

    /**
     * Amount of stacks assigned to this row.
     */
    public int getNumberOfAssignedBlocks() {
        return this.children.size();
    }

    /**
     * Returns true, if row has stacks assigned to it, and false otherwise.
     */
    public boolean hasAssignedStacks() {
        return this.getNumberOfAssignedBlocks() > 0;
    }

    /**
     * Returns true, if row has no stacks assigned to it, and false otherwise.
     */
    public boolean hasNoAssignedBlocks() {
        return this.getNumberOfAssignedBlocks() == 0;
    }

    //////////////////////////////////////////////////////////////////
    // Add and remove methods.
    /**
     * Assigns stack to the row. Adjusts height and width of the row. Does not adjust the stack's coordinates.
     * 
     * @param stack
     *            stack to add.
     */
    public void addBlock(final Block block) {
        this.children.add(block);

        this.height = Math.max(this.height, block.getHeight());
        this.width += block.getWidth();
    }

    /**
     * Removes specified stack from this row. Adjusts width and height.
     * TODO this is not correct
     * @param stack
     *            stack to remove.
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
    // Special private methods
    /**
     * Gets the first rectangle of the first stack in the row.
     */
    private ElkNode getFirstRectFirstStack() {
        return this.children.get(0).getFirstRectangle();
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
     * @param yCoord
     *            new y-coordinate of this row.
     */
    public void setY(final double yCoord) {
        double yChange = yCoord - this.y;
        for (Block block : children) {
            block.setLocation(block.getX(), block.getY() + yChange);
        }
        this.y = yCoord;
    }

    /**
     * Gets stacks assigned to this row.
     */
    public List<Block> getChildren() {
        return children;
    }
}
