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

import org.eclipse.elk.alg.packing.rectangles.seconditeration.RowFillingAndCompaction.RowFillStrat;
import org.eclipse.elk.alg.packing.rectangles.util.RectRow;
import org.eclipse.elk.alg.packing.rectangles.util.RectStack;
import org.eclipse.elk.graph.ElkNode;

/**
 * Class that offers methods that help calculating the row-filling phase of {@link RowFillingAndCompaction}.
 * 
 * @see RowFillingAndCompaction
 */
public final class RowFilling {

    //////////////////////////////////////////////////////////////////
    // Private Constructor.
    private RowFilling() {
    }

    //////////////////////////////////////////////////////////////////
    // Main methods.
    /**
     * This method sequentially fills the given {@link RectRow} as much as the bounding box width allows. It takes
     * {@link RectStack}s from the next {@link RectRow} or single {@link ElkNode} representing rectangles as long as the
     * {@link RectRow}s width is smaller than the bounding box width. According shifts are made.
     * 
     * @param strategy
     *            strategy that determines with what elements (stacks or rectangles) the taking row should be filled.
     * @param takingRowIdx
     *            index of the row to improve (taking row).
     * @param rows
     *            rows containing stacks and rectangles.
     * @param boundingWidth
     *            width of the bounding box.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     * @return true, if an improvement was made and false otherwise.
     */
    protected static boolean fill(final RowFillStrat strategy, final int takingRowIdx, final List<RectRow> rows,
            final double boundingWidth, final double nodeNodeSpacing) {
        boolean somethingWasChanged = false;

        int yieldingRowIdx = findNextNonEmptyRow(takingRowIdx + 1, rows);
        RectRow takingRow = rows.get(takingRowIdx);
        RectRow yieldingRow = rows.get(yieldingRowIdx);

        while (isRowFillingAllowed(strategy, takingRow, yieldingRow, boundingWidth, nodeNodeSpacing)) {
            somethingWasChanged = true;
            reassignElementAndShift(strategy, rows, takingRowIdx, yieldingRowIdx, nodeNodeSpacing);

            yieldingRowIdx = findNextNonEmptyRow(yieldingRowIdx, rows);
            yieldingRow = rows.get(yieldingRowIdx);
        }

        return somethingWasChanged;
    }

    //////////////////////////////////////////////////////////////////
    // Strategy determination methods.
    /**
     * Calls methods that determine, whether filling the taking row with the given strategy from the yielding row is
     * allowed, which is bounded by the given bounding width.
     * 
     * @param strategy
     *            strategy that determines with what elements the taking row is filled.
     * @param takingRow
     *            the row that takes elements from the yielding row.
     * @param yieldingRow
     *            the row that yields elements for the taking row.
     * @param boundingWidth
     *            width of the bounding box.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     * @return true, if filling the taking row is allowed for the given strategy and false otherwise.
     */
    private static boolean isRowFillingAllowed(final RowFillStrat strategy, final RectRow takingRow,
            final RectRow yieldingRow, final double boundingWidth, final double nodeNodeSpacing) {
        switch (strategy) {
        case WHOLE_STACK:
            return isTakingWholeStackAllowed(takingRow, yieldingRow, boundingWidth);
        case SINGLE_RECT:
            return isFillingWithRectsAllowed(takingRow, yieldingRow, boundingWidth, nodeNodeSpacing);
        default:
            return false;
        }
    }

    /**
     * Calls reassignment and shift methods according to the given filling strategy.
     * 
     * @param strategy
     *            strategy that determines with what elements the taking row is filled.
     * @param rows
     *            rows containing taking row and yielding rows.
     * @param takingRowIdx
     *            index of the taking row.
     * @param yieldingRowIdx
     *            index of the yielding row.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     */
    private static void reassignElementAndShift(final RowFillStrat strategy, final List<RectRow> rows,
            final int takingRowIdx, final int yieldingRowIdx, final double nodeNodeSpacing) {
        switch (strategy) {
        case WHOLE_STACK:
            reassignStackAndShift(rows, takingRowIdx, yieldingRowIdx);
            break;
        case SINGLE_RECT:
            reassignRectAndShift(rows, takingRowIdx, yieldingRowIdx, nodeNodeSpacing);
            break;
        default:
            break;

        }
    }

    //////////////////////////////////////////////////////////////////
    // Reassignment and shift methods.
    /**
     * Reassigns first {@link RectStack} of yielding {@link RectRow} to the taking {@link RectRow} and relocates the
     * {@link RectStack}'s coordinates accordingly. The corresponding shifts are also made.
     * 
     * @param takingRow
     *            row that the moving stack is assigned to.
     * @param yieldingRow
     *            row that yields the moving stack.
     * @param rows
     *            rows to be shifted.
     * @param takingRowIdx
     *            index of the taking row.
     * @param yieldingRowIdx
     *            index of the yielding row.
     */
    private static void reassignStackAndShift(final List<RectRow> rows, final int takingRowIdx,
            final int yieldingRowIdx) {
        RectRow takingRow = rows.get(takingRowIdx);
        RectRow yieldingRow = rows.get(yieldingRowIdx);

        double takingRowPreviousWidth = takingRow.getWidth();
        double takingRowPreviousHeight = takingRow.getHeight();
        RectStack movingStack = yieldingRow.getFirstStack();

        takingRow.assignStack(movingStack);
        movingStack.setParentRow(takingRow);
        yieldingRow.removeStack(movingStack);
        movingStack.relocateStack(takingRowPreviousWidth, takingRow.getY());

        double downwardShift = 0;
        if (movingStack.getHeight() > takingRowPreviousHeight) {
            downwardShift = movingStack.getHeight() - takingRowPreviousHeight;
        }

        double upwardShift = 0;
        if (movingStack.getHeight() > yieldingRow.getHeight()) {
            upwardShift = movingStack.getHeight() - yieldingRow.getHeight();
        }

        if (downwardShift > 0 || upwardShift > 0) {
            for (int rowToShiftIdx = takingRowIdx + 1; rowToShiftIdx < yieldingRowIdx; rowToShiftIdx++) {
                RectRow adjustingRow = rows.get(rowToShiftIdx);
                adjustingRow.increaseYRecursively(downwardShift);
            }
            yieldingRow.increaseYRecursively(downwardShift);

            for (int rowToShiftIdx = yieldingRowIdx + 1; rowToShiftIdx < rows.size(); rowToShiftIdx++) {
                RectRow adjustingRow = rows.get(rowToShiftIdx);
                adjustingRow.increaseYRecursively(downwardShift - upwardShift);
            }
        }

        double horizontalShiftYieldingRow = movingStack.getWidth();
        yieldingRow.decreaseXRecursively(horizontalShiftYieldingRow);
    }

    /**
     * Actually assigns a rectangle from the yielding {@link RectRow} to the current {@link RectRow}. Necessary shifts
     * are made.
     * 
     * @param rows
     *            all rows of the drawing. Needed for the shifts.
     * @param takingRow
     *            row that a rectangle is being assigned to.
     * @param yieldingRow
     *            row that is yielding the rectangle to be reassigned.
     * @param takingRowIdx
     *            index of the taking row.
     * @param yieldingRowIdx
     *            index of the yielding row.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     */
    private static void reassignRectAndShift(final List<RectRow> rows, final int takingRowIdx,
            final int yieldingRowIdx, final double nodeNodeSpacing) {
        RectRow takingRow = rows.get(takingRowIdx);
        RectRow yieldingRow = rows.get(yieldingRowIdx);

        RectStack yieldingStack = yieldingRow.getFirstStack();
        RectStack takingStack = takingRow.getLastStack();
        ElkNode movingRect = yieldingStack.getFirstRectangle();

        double takingStackBottomBorder = takingStack.getY() + takingStack.getHeight();
        movingRect.setLocation(takingStack.getX(), takingStackBottomBorder);
        yieldingStack.removeChild(movingRect);
        takingStack.addChild(movingRect);

        yieldingStack.decreaseChildrensY(movingRect.getHeight() + nodeNodeSpacing);

        // Potentially shift rows vertically if the yielding stack defined the height of its row.
        double verticalShift = movingRect.getHeight() + nodeNodeSpacing + yieldingStack.getHeight() - yieldingRow.getHeight();
        if (verticalShift > 0) {
            for (int rowShiftIdx = yieldingRowIdx + 1; rowShiftIdx < rows.size(); rowShiftIdx++) {
                RectRow rowToShift = rows.get(rowShiftIdx);
                rowToShift.decreaseYRecursively(verticalShift);
            }
        }

        // Potentially shift stacks horizontally if moved node (+ spacing) was wider than the whole stack it was removed from.
        double horizontalShift = movingRect.getWidth() + nodeNodeSpacing - yieldingStack.getWidth();
        if (horizontalShift > 0) {
            List<RectStack> yieldingRowStacks = yieldingRow.getChildren();
            for (int stackIdx = 1; stackIdx < yieldingRowStacks.size(); stackIdx++) {
                RectStack stackToShift = yieldingRowStacks.get(stackIdx);
                stackToShift.adjustXRecursively(stackToShift.getX() - horizontalShift);
            }
        }

        if (yieldingStack.hasNoRectanglesAssigned()) {
            yieldingRow.removeStack(yieldingStack);
        }
    }

    //////////////////////////////////////////////////////////////////
    // Methods checking whether row-filling is allowed.
    /**
     * Determines whether taking a whole stack from the yielding {@link RectRow} and adding it to the taking
     * {@link RectRow} exceeds the bounding width.
     * 
     * @param takingRow
     *            the row that takes a stack from the yielding row.
     * @param yieldingRow
     *            the row that yields a stack.
     * @param boundingWidth
     *            the width of the bounding box that is not to be surpassed.
     * @return returns true, if bounding box is not exceeded, and false otherwise.
     */
    private static boolean isTakingWholeStackAllowed(final RectRow takingRow, final RectRow yieldingRow,
            final double boundingWidth) {
        if (yieldingRow.hasNoAssignedStacks()) {
            return false;
        }

        double potentialRowWidth = takingRow.getWidth() + yieldingRow.getFirstStackWidth();
        return potentialRowWidth <= boundingWidth;
    }

    /**
     * Checks whether it is allowed to fill the taking {@link RectRow} with a single rectangle from the yielding
     * {@link RectRow}. Output on the taking row's height, bounding box width, and availability of {@link RectStack}s
     * and rectangles in the yielding {@link RectRow}.
     * 
     * @param takingRow
     *            row that takes the rectangle.
     * @param yieldingRow
     *            row that yields the rectangle.
     * @param boundingWidth
     *            width of the bounding box.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     * @return true, if reassigning the rectangle to the taking row is allowed, false otherwise.
     */
    private static boolean isFillingWithRectsAllowed(final RectRow takingRow, final RectRow yieldingRow,
            final double boundingWidth, final double nodeNodeSpacing) {
        if (yieldingRow.hasNoAssignedStacks() || yieldingRow.getFirstStack().hasNoRectanglesAssigned()) {
            return false;
        }

        double potentialWidth = calculatePotentialRowWidth(takingRow, yieldingRow, nodeNodeSpacing);
        double potentialHeightLastStack = takingRow.getLastStackHeight() + yieldingRow.getFirstRectFirstStackHeight() + nodeNodeSpacing;

        return potentialWidth <= boundingWidth && potentialHeightLastStack <= takingRow.getHeight();
    }

    //////////////////////////////////////////////////////////////////
    // Other helping methods.
    /**
     * Calculates the potential width of the taking {@link RectRow} after adding the first rectangle of the yielding
     * {@link RectRow} to the last {@link RectStack} of the taking {@link RectRow}.
     * 
     * @param takingRow
     *            the taking row.
     * @param yieldingRow
     *            the yielding row.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     * @return returns the width of the taking row, if the moving rectangle is less wide than the taking row's last
     *         stack. Returns the width of the taking row plus the increase of the width of the last stack, if the
     *         moving rectangle is wider than the last stack.
     */
    private static double calculatePotentialRowWidth(final RectRow takingRow, final RectRow yieldingRow, final double nodeNodeSpacing) {
        double potentialRowWidth = takingRow.getWidth();
        double widthDiffTakingAndYieldingStack =
                yieldingRow.getFirstRectFirstStackWidth() + nodeNodeSpacing - takingRow.getLastStackWidth();

        if (widthDiffTakingAndYieldingStack > 0) {
            potentialRowWidth += widthDiffTakingAndYieldingStack;
        }

        return potentialRowWidth;
    }

    /**
     * Finds and returns the index of the next non empty row including or after the given index.
     * 
     * @param currentIdx
     *            the current index.
     * @param rows
     *            list of rows to be checked.
     * @return the next index of a non empty row, including the current index. If all rows after or including the
     *         current index are empty, this method returns the last index of the rows.
     */
    private static int findNextNonEmptyRow(final int currentIdx, final List<RectRow> rows) {
        int yieldingRowIdx = currentIdx;
        while (rows.get(yieldingRowIdx).hasNoAssignedStacks() && yieldingRowIdx < rows.size() - 1) {
            yieldingRowIdx++;
        }
        return yieldingRowIdx;
    }
}
