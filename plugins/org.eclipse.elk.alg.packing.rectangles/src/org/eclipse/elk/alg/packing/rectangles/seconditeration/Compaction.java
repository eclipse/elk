/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.packing.rectangles.seconditeration;

import java.util.List;

import org.eclipse.elk.alg.packing.rectangles.util.Block;
import org.eclipse.elk.alg.packing.rectangles.util.RectRow;
import org.eclipse.elk.graph.ElkNode;

/**
 * Class that offers methods that help calculating the compaction phase of {@link RowFillingAndCompaction}.
 * 
 * @see RowFillingAndCompaction
 */
public final class Compaction {

    //////////////////////////////////////////////////////////////////
    // Private Constructor.
    private Compaction() {
    }

    //////////////////////////////////////////////////////////////////
    // Main method.
    /**
     * Compacts the given {@link RectRow} by putting its children beneath each other. Calculates the drawing's width and
     * height accordingly and executes needed shifts.
     * 
     * @param rowIdx
     *            index of the row to improve.
     * @param rows
     *            given {@link RectRow}s with placed rectangles.
     * @param boundingWidth
     *            width of a bounding box that is not be be exceeded.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     * @return returns true, if at least one compaction was made and false otherwise.
     */
    protected static boolean compact(final int rowIdx, final List<RectRow> rows, final double boundingWidth, final double nodeNodeSpacing) {
        boolean somethingWasChanged = false;
        int nextRowIndex = rowIdx + 1;
        RectRow row = rows.get(rowIdx);
        List<Block> blocks = row.getChildren();
        
        // Check for each block whether:
        // The next (or part of the next) block can be put on top of it,
        // the next block cannot be put on top of it, but the current block can be drawn higher,
        // or part of the next block can be added to the current block.
        for (int blockId = 0; blockId < row.getNumberOfAssignedBlocks(); blockId++) {
            Block block = blocks.get(blockId);
            if (block.isFixed()) {
                continue;
            }
            if (block.getChildren().isEmpty()) {
                System.out.println("Deleted empty block, this should not happen");
                row.removeBlock(block);
                blockId--;
                somethingWasChanged = true;
                continue;
            }
            
            // Move the block to its new position if something before it was changed and it is moveable.
            if (!block.isPositionFixed()) {
                double currentX = 0;
                if (blockId > 0) {
                    Block previousBlock = row.getChildren().get(blockId - 1);
                    currentX = previousBlock.getX() + previousBlock.getWidth();
                }
                block.setLocation(currentX, row.getY());
                block.setPositionFixed(true);
            }
            
            // Optimization 1: Does the next block fit on top of me?
            // This includes removing element from the next block and putting them in a new block.
            // A block can also be taken from the next row.
            Block nextBlock = null;
            nextBlock = getNextBlock(rows, row, blockId, nextRowIndex);
            boolean wasFromNextRow = false;
            if (nextBlock != null) {
                wasFromNextRow = !nextBlock.getParentRow().equals(row);
            }
            
            if (nextBlock != null) {
                // Decide whether the block can be merged with the previous block

                // Try to move as many rects as possible from the next block in this block.
                // First flatten the current block.
                if (!nextBlock.getChildren().isEmpty()) {
                    useRowWidth(block, boundingWidth);
                    // Absorb all rectangles that fit in the current block form the next block if they fit the row.
                    somethingWasChanged |= absorbBlocks(row, block, nextBlock, boundingWidth, nodeNodeSpacing);
                                       
                } else {
                    System.out.println("The next block is empty? Why?");
                    // Delete empty nextBlock
                    row.removeBlock(nextBlock);
                    break;
                }
                // Check whether whole next block or part of the whole next block can be added to this row.
                // This involves flattening the current block if possible to maintain the left to right reading direction.
                // if the current block is too high to fit the next block on top of it, try placing it next to it.
                // FIXME If the after next block is very high this might be a bag idea.
                
                // From the previous step the next block and the next row might be empty.
                // Delete all empty blocks and rows.
                while (nextBlock.getChildren().isEmpty()) {
                    rows.get(nextRowIndex).removeBlock(nextBlock);
                    while (rows.size() > nextRowIndex && rows.get(nextRowIndex).getChildren().isEmpty()) {
                        rows.remove(rows.get(nextRowIndex));
                        System.out.println("Remvoed rows");
                        // TODO maybe adjust y of other rows
                    }
                    if (rows.size() > nextRowIndex) {
                        System.out.println("New next block");
                        nextBlock = rows.get(nextRowIndex).getFirstBlock();
                    } else {
                        nextBlock = null;
                        break;
                    }
                }
                if (nextBlock == null) {
                    continue;
                }
                // Get height of block if it uses the 
                // Try to use the whole remaining width of the row and try to put the block on top of it.
                // If it does not work (because the block is too high) check the minimum height of the block and check whether
                // it can fit at all.
                // If it could compact the width of the current block and try to fit as much as possible in there

                // Try to fit next block on top of the current block.
                if (placeBelow(rows, row, block, nextBlock, wasFromNextRow, boundingWidth, nextRowIndex)) {
                    somethingWasChanged = true;
                    continue;
                }
                
                if (wasFromNextRow) {
                    // Try to place the next block next to the current one.
                    // Draw the current block as slim as possible.
                    if (placeBeside(rows, row, block, nextBlock, wasFromNextRow, boundingWidth, nextRowIndex)) {
                        somethingWasChanged = true;
                        continue;
                    }
                }
                
                // Case only parts of the next block where added, but no full next block could be added.
                if (somethingWasChanged) {
                    continue;
                }
            } else {
                System.out.println("There is not new block");
            }
            
            // Optimization 2: Let blocks use the row width if they can
            
            if (useRowHeight(row, block)) {
                block.setFixed(true);
                somethingWasChanged = true;
                continue;
            }
        }

        return somethingWasChanged;
    }
    
    /**
     * Returns the next block if any.
     * @return The next block
     */
    private static Block getNextBlock(List<RectRow> rows, RectRow row, int blockId, int nextRowIndex) {
        Block nextBlock = null;
        if (blockId < row.getNumberOfAssignedBlocks() - 1) {
            // Get block from this row.
            nextBlock = row.getChildren().get(blockId + 1);
        } else if (nextRowIndex < rows.size() && !rows.get(nextRowIndex).getChildren().isEmpty()) {
            // Get block from next row.
            nextBlock = rows.get(nextRowIndex).getChildren().get(0);
        }
        return nextBlock;
    }

    /**
     * Check whether the width of the block can be reduced by using the whole height, since the preceding block does not exist 
     * or is too big to be put on top of it.
     * @param row The row the block is in
     * @param block The block to be slimed.
     * @return If changes to the block were made.
     */
    public static boolean useRowHeight(RectRow row, Block block) {
        boolean somethingWasChanged = false;
        double previousWidth = block.getWidth();
        if (block.getHeight() < row.getHeight()) {
            double targetWidth = block.getWidthForTargetHeight(row.getHeight());
            if (block.getWidth() > targetWidth) {
                block.placeRectsIn(targetWidth);
                somethingWasChanged = previousWidth != block.getWidth();
            }
        }
        return somethingWasChanged;
    }
    
    public static void useRowWidth(Block block, double boundingWidth) {
        block.placeRectsIn(boundingWidth - block.getX());
    }
    
    public static boolean absorbBlocks(RectRow row, Block block, Block nextBlock, double boundingWidth, double nodeNodeSpacing) {
        boolean somethingWasChanged = false;
        ElkNode rect = nextBlock.getFirstRectangle();
        while (InitialPlacement.placeRectInBlock(row, block, rect, boundingWidth, nodeNodeSpacing)) {
            // The rect was added to this block.
            somethingWasChanged = true;
            nextBlock.removeChild(rect);
            if (nextBlock.getChildren().isEmpty()) {
                break;
            }
            rect = nextBlock.getFirstRectangle();
        }
        if (nextBlock.getChildren().isEmpty()) {
            nextBlock.getParentRow().removeBlock(nextBlock);
        }
        return somethingWasChanged;
    }
    
    public static boolean placeBelow(List<RectRow> rows, RectRow row, Block block, Block nextBlock, boolean wasFromNextRow,
            double boundingWidth, int nextRowIndex) {
        boolean somethingWasChanged = false;
        double currentBlockMinHeight = block.getHeightForTargetWidth(boundingWidth - block.getX());                
        double nextBlockMinHeight = nextBlock.getHeightForTargetWidth(boundingWidth - block.getX());
        
        if (currentBlockMinHeight + nextBlockMinHeight <= row.getHeight()) {
            block.placeRectsIn(boundingWidth - block.getX());
            block.setFixed(true);
            nextBlock.placeRectsIn(boundingWidth - block.getX());
            nextBlock.setLocation(block.getX(), block.getY() + block.getHeight());
            nextBlock.setPositionFixed(true);
            somethingWasChanged = true;
            if (wasFromNextRow) {
                row.addBlock(nextBlock);
                nextBlock.setParentRow(row);
                if (rows.size() > nextRowIndex) {
                    rows.get(nextRowIndex).removeBlock(nextBlock);
                    if (rows.get(nextRowIndex).getChildren().isEmpty()) {
                        System.out.println("Removed row");
                        rows.remove(nextRowIndex);
                    }
                }
            }
        }
        return somethingWasChanged;
    }
    
    public static boolean placeBeside(List<RectRow> rows, RectRow row, Block block, Block nextBlock, boolean wasFromNextRow,
            double boundingWidth, int nextRowIndex) {
        boolean somethingWasChanged = false;
        double currentBlockMinWidth = block.getWidthForTargetHeight(row.getY() + row.getHeight() - block.getY());
        double targetWidthOfNextBlock = boundingWidth - (block.getX() + currentBlockMinWidth);
        if (targetWidthOfNextBlock < nextBlock.getMinWidth()) {
            return false;
        }
        double nextBlockHeight = nextBlock.getHeightForTargetWidth(targetWidthOfNextBlock);
        if (nextBlockHeight <= row.getHeight()) {
            block.placeRectsIn(currentBlockMinWidth);
            block.setFixed(true);
            nextBlock.placeRectsIn(boundingWidth - (block.getY() + currentBlockMinWidth));
            nextBlock.setLocation(block.getX() + block.getWidth(), row.getY());
            row.addBlock(nextBlock);
            if (rows.size() > nextRowIndex) {
                rows.get(nextRowIndex).removeBlock(nextBlock);
                if (rows.get(nextRowIndex).getChildren().isEmpty()) {
                    System.out.println("Removed 2row");
                    rows.remove(nextRowIndex);
                }
            }
            somethingWasChanged = true;
        }
        return somethingWasChanged;
    }
}
