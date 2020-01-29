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
     * @return returns true, if at least one compaction was made and false otherwise.
     */
    protected static boolean compact(final int rowIdx, final List<RectRow> rows, final double boundingWidth, final double nodeNodeSpacing) {
        boolean somethingWasChanged = false;
        RectRow currentRow = rows.get(rowIdx);
        List<RectStack> stacksCurrentRow = currentRow.getChildren();
        double rowPreviousHeight = currentRow.getHeight();

        for (int stackIdx = 0; stackIdx < currentRow.getNumberOfAssignedStacks() - 1; stackIdx++) {
            RectStack takingStack = stacksCurrentRow.get(stackIdx);
            RectStack yieldingStack = stacksCurrentRow.get(stackIdx + 1);

            while (isCompactionAllowed(takingStack, yieldingStack, boundingWidth)) {
                somethingWasChanged = true;
                reassignRectangleAndHorizontalShifts(takingStack, yieldingStack, stacksCurrentRow, stackIdx, nodeNodeSpacing);
            }

            if (yieldingStack.hasNoRectanglesAssigned()) {
                currentRow.removeStack(yieldingStack);
            }
        }

        removeEmptyStacksFromRow(currentRow);
        verticalShift(rowPreviousHeight, rows, rowIdx);

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
            horizontalShiftYieldingStackNotEmpty(movingRect, takingStackPreviousWidth, takingStackIdx, stacks);
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
     */
    private static void horizontalShiftYieldingStackNotEmpty(final ElkNode movingRect,
            final double takingStackPreviousWidth, final int takingStackIdx, final List<RectStack> stacks) {
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
        yieldingStack.decreaseChildrensY(movingRect.getHeight());

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
     */
    private static void horizontalShiftYieldingStackEmpty(final ElkNode movingRect,
            final double takingStackPreviousWidth, final int tackingStackIdx, final List<RectStack> stacks, final double nodeNodeSpacing) {
        double distanceToShift = 0;
        if (takingStackPreviousWidth >= movingRect.getWidth()) {
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
    private static void verticalShift(final double rowPreviousHeight, final List<RectRow> rows,
            final int compactingRowIdx) {
        RectRow compactingRow = rows.get(compactingRowIdx);

        double verticalShift = rowPreviousHeight - compactingRow.getHeight();
        if (verticalShift != 0) {
            for (int rowsToShiftIdx = compactingRowIdx + 1; rowsToShiftIdx < rows.size(); rowsToShiftIdx++) {
                rows.get(rowsToShiftIdx).decreaseYRecursively(verticalShift);
            }
        }
    }

    /**
     * Removes empty {@link RectStack}s from the given {@link RectRow}.
     * 
     * @param row
     *            the row the stacks are removed from.
     */
    private static void removeEmptyStacksFromRow(final RectRow row) {
        List<RectStack> stacks = row.getChildren();
        for (RectStack stack : stacks) {
            if (stack.hasNoRectanglesAssigned()) {
                row.removeStack(stack);
            }
        }
    }

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
     * @return true, if the stack's height added to the first rectangle's height is smaller than the parent row's
     *         height, and false otherwise. Returns false, if the yieldingStack does not contain rectangles.
     */
    private static boolean isCompactionAllowed(final RectStack takingStack, final RectStack yieldingStack,
            final double boundingWidth) {
        if (yieldingStack.hasNoRectanglesAssigned()) {
            return false;
        }

        RectRow parent = takingStack.getParentRow();
        ElkNode movingRect = yieldingStack.getFirstRectangle();

        if (takingStack.getHeight() + movingRect.getHeight() <= parent.getHeight()) {

            // change of widths due to rectangle reassignment.
            double potentialWidthIncreaseTaking = calcPotIncreaseTakingStackWidth(takingStack, movingRect);
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
     * @return the potential new stack width minus the current stack width.
     */
    private static double calcPotIncreaseTakingStackWidth(final RectStack takingStack, final ElkNode movingRect) {
        double newStackWidth = Math.max(takingStack.getWidth(), movingRect.getWidth());
        return newStackWidth - takingStack.getWidth();
    }

    /**
     * Returns the potential decrease of width of the yielding stack, if its first rectangle is removed.
     * 
     * @param yieldingStack
     *            the stack that gets a rectangle removed from.
     * @return if the moving rectangle was the widest rectangle, moving rectangle width minus new width of the stack and
     *         zero otherwise. Returns the width of the moving rectangle, if the stack only containts said moving
     *         rectangle.
     */
    private static double calcPotDecreaseYieldingStackWidth(final RectStack yieldingStack) {
        double movingRectWidth = yieldingStack.getFirstRectangle().getWidth();
        if (yieldingStack.getNumberOfRectangles() > 1) {
            double newWidth = Double.MIN_VALUE;
            List<ElkNode> rectangles = yieldingStack.getChildren();

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
