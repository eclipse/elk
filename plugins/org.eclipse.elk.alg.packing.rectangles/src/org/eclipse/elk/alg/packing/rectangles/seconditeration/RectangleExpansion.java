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
 * Offer method to expand rectangles after the calculations of the second iteration.
 * 
 * @see RowFillingAndCompaction
 */
public final class RectangleExpansion {

    private RectangleExpansion() {
    }

    /**
     * Expands rectangles after the calculations of the second iteration, so that the bounding box is filled without
     * whitespace. First, every {@link RectRow} is enlarged to fit the drawings dimensions. Then, {@link RectStack} are
     * enlarged to fit their respective {@link RectRow}. Lastly, {@link ElkNode}s are enlarged to fill their respective
     * {@link RectStack}.
     * 
     * @param rows
     *            rows containing the rectangles given by {@link RectStack}s.
     * @param drawingWidth
     *            width of the drawing (given by the widest {@link RectRow}).
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     */
    protected static void expand(final List<RectRow> rows, final double drawingWidth, final double nodeNodeSpacing) {
        for (RectRow expandingRow : rows) {
            expandingRow.setWidth(drawingWidth);
            // Relocate stacks to be evenly distributed in the row (this includes the rects).
            expandStacks(expandingRow, nodeNodeSpacing);
        }
    }

    /**
     * Expands every {@link RectStack} of the given {@link RectRow} according to its size. The width a row is enlarged
     * is distributed on the stacks of a row. Calls a method to expand the children of each {@link RectStack.}
     * 
     * @param row
     *            the {@link RectRow}, whose children are to be expanded.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     */
    private static void expandStacks(final RectRow row, final double nodeNodeSpacing) {
        RectStack lastStackOfRow = row.getLastStack();

        // No node spacing is required, since it is already included in the width of a stack.
        double lastStackRightBorder = lastStackOfRow.getX() + lastStackOfRow.getWidth();
        double widthToDistribute = row.getWidth() - lastStackRightBorder;
        // Calculate how much each stack is moved to the right to have evenly distributed space.
        double xShiftForStack = widthToDistribute / row.getChildren().size();

        for (int i = 0; i < row.getChildren().size(); i++) {
            RectStack stack = row.getChildren().get(i);
            stack.setHeight(row.getHeight());
            if (i > 0) {
                stack.adjustXRecursively(stack.getX() + xShiftForStack * i);
                RectStack previousStack = row.getChildren().get(i - 1);
                previousStack.setWidth(stack.getX() - previousStack.getX());
            }
            // Width of last stack is the width of the row
            if (i == row.getChildren().size() - 1) {
                stack.setWidth(row.getWidth() - stack.getX());
            }
        }
        // After the stack width is calculated expand nodes appropriately
        for (RectStack stack : row.getChildren()) {
            expandRectangles(stack, nodeNodeSpacing);
        }
    }

    /**
     * Expands the children of the given {@link RectStack} according to the size of said {@link RectStack}.
     * 
     * @param stack
     *            the {@link RectStack}, whose children are to be expanded.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     */
    private static void expandRectangles(final RectStack stack, final double nodeNodeSpacing) {
        ElkNode lastRect = stack.getLastRectangle();

        // The currently used height of the stack.
        double heightOfRectangles = lastRect.getY() + lastRect.getHeight() - stack.getY() + nodeNodeSpacing;
        double heightToDistribute = stack.getHeight() - heightOfRectangles;
        // Calculate how much each node is moved down to evenly distribute free space.
        double yShiftForRects = heightToDistribute / stack.getChildren().size();

        for (int i = 0; i < stack.getChildren().size(); i++) {
            ElkNode rect = stack.getChildren().get(i);
            if (i > 0) {
                rect.setY(rect.getY() + yShiftForRects * i);
                ElkNode previousRect = stack.getChildren().get(i - 1);
                previousRect.setHeight(rect.getY() - previousRect.getY() - nodeNodeSpacing);
            }
            rect.setWidth(stack.getWidth() - nodeNodeSpacing);
            // Width of last stack is the width of the row
            if (i == stack.getChildren().size() - 1) {
                rect.setHeight(stack.getY() + stack.getHeight() - rect.getY() - nodeNodeSpacing);
            }
        }
    }
}
