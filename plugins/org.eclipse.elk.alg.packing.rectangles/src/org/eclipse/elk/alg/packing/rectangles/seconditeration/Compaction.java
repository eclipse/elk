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
import org.eclipse.elk.alg.packing.rectangles.util.RectStack;
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
        double rowPreviousHeight = row.getHeight();
        double currentX = 0;
        double currentY = row.getY();
        for (int blockId = 0; blockId < row.getNumberOfAssignedBlocks(); blockId++) {
            Block block = blocks.get(blockId);
            if (block.isFixed()) {
                currentX += block.getWidth();
                currentY = row.getY();
                continue;
            }
            if (block.getChildren().isEmpty()) {
                row.removeBlock(block);
                somethingWasChanged = true;
                continue;
            }
            if ((blockId == 0 && block.getX() >= 0) || somethingWasChanged || !block.isPositionFixed()) {
                block.setLocation(currentX, currentY);
            }
            // Optimization 1: Does the next block fit on top of me?
            // This includes removing element from the next block and putting them in a new block.
            // A block can also be taken from the next row.
            Block nextBlock = null;
            boolean wasFromNextRow = false;
            if (blockId < row.getNumberOfAssignedBlocks() - 1) {
                nextBlock = row.getChildren().get(blockId + 1);
            } else if (nextRowIndex < rows.size() - 1 && !rows.get(nextRowIndex).getChildren().isEmpty()) {
                
                nextBlock = rows.get(nextRowIndex).getChildren().get(0);
                wasFromNextRow = true;
            }
            if (nextBlock != null) {
                // Decide whether the block can be merged with the previous block

                // Try to get as much (or one) rect(s) from the next block to fit in there.
                if (!nextBlock.getChildren().isEmpty()) {
                    ElkNode rect = nextBlock.getFirstRectangle();
                    // Check whether this rectangle can fit in the current block.
                    while (InitialPlacement.placeRectInBlock(row, block, rect, boundingWidth, nodeNodeSpacing)) {
                        somethingWasChanged = true;
                        nextBlock.removeChild(rect);
                        if (nextBlock.getChildren().isEmpty()) {
                            break;
                        }
                        rect = nextBlock.getFirstRectangle();
                    }                    
                }
                // Check whether whole block can be added
                
                // Check whether it can be added on top of the last block.
                
                if (nextBlock.getChildren().isEmpty()) {
                    rows.get(nextRowIndex).removeBlock(nextBlock);
                    while (rows.get(nextRowIndex).getChildren().isEmpty()) {
                        rows.remove(rows.get(nextRowIndex));
                        // TODO maybe adjust y of other rows
                    }
                    nextBlock = rows.get(nextRowIndex).getFirstBlock();
                    
                    double targetHeight = nextBlock.getHeightForTargetWidth(block.getWidth());
                    if (block.getY() + block.getHeight() + targetHeight <= row.getHeight()) {
                        block.setFixed(true);
                        nextBlock.setPositionFixed(true);
                        nextBlock.setX(currentX);
                        nextBlock.setY(block.getY() + block.getHeight());
                        nextBlock.placeRectsIn(block.getWidth());
                        nextBlock.setWidth(block.getWidth());
                        nextBlock.setHeight(targetHeight);
                        somethingWasChanged = true;
                        if (wasFromNextRow) {
                            row.addBlock(nextBlock);
                            // TODO removeBlock should handle specific things
                            rows.get(nextRowIndex).removeBlock(nextBlock);
                        }
                        continue;
                    }
                }
                // TODO overthink this
                if (somethingWasChanged) {
                    currentX += block.getWidth();
                    continue;
                }
            }
            
            // Optimization 2: Let blocks use the row width if they can
            if (block.getHeight() < row.getHeight()) {
                double targetWidth = block.getWidthForTargetHeight(row.getHeight());
                if (block.getWidth() > targetWidth) {
                    block.placeRectsIn(targetWidth);
                    block.setWidth(targetWidth);
                    block.setHeight(row.getHeight());
                    currentX += targetWidth;
                    somethingWasChanged = true;
                    block.setFixed(true);
                    continue;
                }
            }
            // Nothing was changed, update currentX/currentY
            currentX += block.getWidth();
            currentY = row.getY();
        }
//        verticalShift(rowPreviousHeight, rows, rowIdx);

        return somethingWasChanged;
    }

    //////////////////////////////////////////////////////////////////
    // Helping methods.

    /**
     * Reassigns a the first rectangle of the yielding stack according to the compaction algorithm and takes care of all
     * shifts.
     * 
     * @param takingStack
     *            new stack of the rectangle.
     * @param yieldingStack
     *            current stack of the rectangle.
     * @param stacks
     *            stacks in the row of the yielding stack.
     * @param stacksToRemove
     *            list of empty stacks that are to be removed later.
     * @param takingStackIdx
     *            index of the taking stack.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     */
    private static void reassignRectangleAndHorizontalShifts(final RectStack takingStack, final RectStack yieldingStack,
            final List<RectStack> stacks, final int takingStackIdx, final double nodeNodeSpacing) {
        double takingStackPreviousWidth = takingStack.getWidth();
        ElkNode movingRect = yieldingStack.getFirstRectangle();

        movingRect.setLocation(takingStack.getX(), takingStack.getY() + takingStack.getHeight());
        takingStack.addChild(movingRect);
        yieldingStack.removeChild(movingRect);

        if (yieldingStack.hasNoRectanglesAssigned()) {
            horizontalShiftYieldingStackEmpty(movingRect, takingStackPreviousWidth, takingStackIdx, stacks, nodeNodeSpacing);
        } else {
            horizontalShiftYieldingStackNotEmpty(movingRect, takingStackPreviousWidth, takingStackIdx, stacks, nodeNodeSpacing);
        }
    }

    /**
     * Shifts the {@link RectStack}s by the calculated amounts. Yielding and followings stacks are affected differently.
     * 
     * @param movingRect
     *            the rectangle that was assigned to a new stack.
     * @param takingStackPreviousWidth
     *            the width of the taking stack before the addition of the moving rectangle.
     * @param takingStackIdx
     *            index of the taking stack.
     * @param stacks
     *            list of all stacks in the row of the compaction.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     */
    private static void horizontalShiftYieldingStackNotEmpty(final ElkNode movingRect,
            final double takingStackPreviousWidth, final int takingStackIdx, final List<RectStack> stacks, final double nodeNodeSpacing) {
        RectStack yieldingStack = stacks.get(takingStackIdx + 1);
        double distanceToShiftFollowingStacks = 0;
        double distanceToShiftYieldingStack = 0;

        boolean movingRectWiderThanYieldingStack = movingRect.getWidth() > yieldingStack.getWidth();
        boolean movingRectWiderThanTakingStack = movingRect.getWidth() > takingStackPreviousWidth;

        // calculation of distances to shift according to different cases.
        if (movingRectWiderThanYieldingStack && movingRectWiderThanTakingStack) {

            distanceToShiftFollowingStacks = (yieldingStack.getWidth() - movingRect.getWidth())
                    + (movingRect.getWidth() - takingStackPreviousWidth);
            distanceToShiftYieldingStack = movingRect.getWidth() - takingStackPreviousWidth;

        } else if (!movingRectWiderThanYieldingStack && movingRectWiderThanTakingStack) {

            distanceToShiftYieldingStack = movingRect.getWidth() - takingStackPreviousWidth;
            distanceToShiftFollowingStacks = distanceToShiftYieldingStack;

        } else if (movingRectWiderThanYieldingStack && !movingRectWiderThanTakingStack) {

            distanceToShiftFollowingStacks = yieldingStack.getWidth() - movingRect.getWidth();
        }

        yieldingStack.adjustXRecursively(yieldingStack.getX() + distanceToShiftYieldingStack);
        // Set new y coordinates for the yielding stack (all nodes are shifted up)
        yieldingStack.decreaseChildrensY(movingRect.getHeight() + nodeNodeSpacing);

        for (int stackToShiftIdx = takingStackIdx + 2; stackToShiftIdx < stacks.size(); stackToShiftIdx++) {
            RectStack stackToShift = stacks.get(stackToShiftIdx);
            stackToShift.adjustXRecursively(stackToShift.getX() + distanceToShiftFollowingStacks);
        }
    }

    /**
     * Shifts all following {@link RectStack}s by the calculated amount. This method is called if the yielding
     * {@link RectStack} does not contain any more elements after the compaction.
     * 
     * @param movingRect
     *            the rectangle that was assigned to a new stack.
     * @param takingStackPreviousWidth
     *            the width of the taking stack before the compaction.
     * @param tackingStackIdx
     *            index of the tacking stack.
     * @param stacks
     *            list of all stacks in the row of the compaction.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     */
    private static void horizontalShiftYieldingStackEmpty(final ElkNode movingRect,
            final double takingStackPreviousWidth, final int tackingStackIdx, final List<RectStack> stacks, final double nodeNodeSpacing) {
        double distanceToShift = 0;
        if (takingStackPreviousWidth >= movingRect.getWidth() + nodeNodeSpacing) {
            distanceToShift = movingRect.getWidth() + nodeNodeSpacing;
        } else {
            distanceToShift = takingStackPreviousWidth;
        }

        for (int stackToShiftIdx = tackingStackIdx + 2; stackToShiftIdx < stacks.size(); stackToShiftIdx++) {
            RectStack stackToShift = stacks.get(stackToShiftIdx);
            stackToShift.adjustXRecursively(stackToShift.getX() - distanceToShift);
        }
    }

    /**
     * Executes the vertical shift of following {@link RectRow}s caused by the change of height of the current
     * {@link RectRow}.
     * 
     * @param rowPreviousHeight
     *            height of the row before a compaction was made.
     * @param rows
     *            contains the rows the shifts are made to.
     * @param compactingRowIdx
     *            index of the row the compaction was made in.
     */
//    private static void verticalShift(final double rowPreviousHeight, final List<RectRow> rows,
//            final int compactingRowIdx) {
//        RectRow compactingRow = rows.get(compactingRowIdx);
//
//        double verticalShift = rowPreviousHeight - compactingRow.getHeight();
//        if (verticalShift != 0) {
//            for (int rowsToShiftIdx = compactingRowIdx + 1; rowsToShiftIdx < rows.size(); rowsToShiftIdx++) {
//                rows.get(rowsToShiftIdx).decreaseYRecursively(verticalShift);
//            }
//        }
//    }
//
//    /**
//     * Removes empty {@link RectStack}s from the given {@link RectRow}.
//     * 
//     * @param row
//     *            the row the stacks are removed from.
//     */
//    private static void removeEmptyStacksFromRow(final RectRow row) {
//        List<RectStack> stacks = row.getChildren();
//        for (RectStack stack : stacks) {
//            if (stack.hasNoRectanglesAssigned()) {
//                row.removeStack(stack);
//            }
//        }
//    }

    /**
     * Checks whether a compaction that adds the first rectangle of the yielding {@link RectStack} to the taking stack
     * exceeds the stack's parent {@link RectRow}'s height. Also checks whether the compaction surpasses the given
     * bounding width.
     * 
     * @param takingStack
     *            stack the rectangle is added to.
     * @param yieldingStack
     *            stack that might yield its first rectangle.
     * @param boundingWidth
     *            width of a bounding box that is not be be exceeded.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     * @return true, if the stack's height added to the first rectangle's height is smaller than the parent row's
     *         height, and false otherwise. Returns false, if the yieldingStack does not contain rectangles.
     */
    private static boolean isCompactionAllowed(final RectStack takingStack, final RectStack yieldingStack,
            final double boundingWidth, final double nodeNodeSpacing) {
        if (yieldingStack.hasNoRectanglesAssigned()) {
            return false;
        }

        RectRow parent = takingStack.getParentRow();
        ElkNode movingRect = yieldingStack.getFirstRectangle();

        if (takingStack.getHeight() + movingRect.getHeight() <= parent.getHeight()) {

            // change of widths due to rectangle reassignment.
            double potentialWidthIncreaseTaking = calcPotIncreaseTakingStackWidth(takingStack, movingRect, nodeNodeSpacing);
            double potentialWidthDecreaseYielding = calcPotDecreaseYieldingStackWidth(yieldingStack);

            if (parent.getWidth() + potentialWidthIncreaseTaking - potentialWidthDecreaseYielding <= boundingWidth) {
                return true;
            }
        }

        return false;
    }

    /**
     * Calculates the potential increase of width of the taking {@link RectStack}, if the given rectangle is added to
     * the stack.
     * 
     * @param takingStack
     *            the stack the rectangle is potentially added to.
     * @param movingRect
     *            the rectangle that is potentially added to the taking stack.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     * @return the potential new stack width minus the current stack width.
     */
    private static double calcPotIncreaseTakingStackWidth(final RectStack takingStack, final ElkNode movingRect, final double nodeNodeSpacing) {
        double newStackWidth = Math.max(takingStack.getWidth(), movingRect.getWidth() + nodeNodeSpacing);
        return newStackWidth - takingStack.getWidth();
    }

    /**
     * Returns the potential decrease of width of the yielding stack, if its first rectangle is removed.
     * The node node spacing is not relevant for this calculation, since it applied to the width of the biggest rectangle
     * and the width of the moving rectangle.
     * 
     * @param yieldingStack
     *            the stack that gets a rectangle removed from.
     * @return if the moving rectangle was the widest rectangle, moving rectangle width minus new width of the stack and
     *         zero otherwise. Returns the width of the moving rectangle, if the stack only contains said moving
     *         rectangle.
     */
    private static double calcPotDecreaseYieldingStackWidth(final RectStack yieldingStack) {
        double movingRectWidth = yieldingStack.getFirstRectangle().getWidth();
        if (yieldingStack.getNumberOfRectangles() > 1) {
            double newWidth = Double.MIN_VALUE;
            List<ElkNode> rectangles = yieldingStack.getChildren();

            // Get maximum width of other rectangles in the yielding stack.
            for (int rectIdx = 1; rectIdx < yieldingStack.getNumberOfRectangles(); rectIdx++) {
                newWidth = Math.max(newWidth, rectangles.get(rectIdx).getWidth());
            }

            if (movingRectWidth > newWidth) {
                return movingRectWidth - newWidth;
            } else {
                return 0;
            }
        } else {
            return movingRectWidth;
        }
    }
}
