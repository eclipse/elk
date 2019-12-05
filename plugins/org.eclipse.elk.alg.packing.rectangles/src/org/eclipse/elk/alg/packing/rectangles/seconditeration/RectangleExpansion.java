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
     */
    protected static void expand(final List<RectRow> rows, final double drawingWidth) {
        for (RectRow expandingRow : rows) {
            expandingRow.setWidth(drawingWidth);
            expandStacks(expandingRow);
        }
    }

    /**
     * Expands every {@link RectStack} of the given {@link RectRow} according to its size. The width a row is enlarged
     * is distributed on the stacks of a row. Calls a method to expand the children of each {@link RectStack.}
     * 
     * @param row
     *            the {@link RectRow}, whose children are to be expanded.
     */
    private static void expandStacks(final RectRow row) {
        RectStack lastStackOfRow = row.getLastStack();

        double lastStackRightBorder = lastStackOfRow.getX() + lastStackOfRow.getWidth();
        double broadenFactor = row.getWidth() / lastStackRightBorder;

        double totalShift = 0;

        for (RectStack stack : row.getChildren()) {
            stack.setHeight(row.getHeight());

            if (broadenFactor > 1) {
                double oldStackWidth = stack.getWidth();

                stack.setWidth(oldStackWidth * broadenFactor);
                stack.adjustXRecursively(stack.getX() + totalShift);

                totalShift += stack.getWidth() - oldStackWidth;
            }
            expandRectangles(stack);
        }
    }

    /**
     * Expands the children of the given {@link RectStack} according to the size of said {@link RectStack}.
     * 
     * @param stack
     *            the {@link RectStack}, whose children are to be expanded.
     */
    private static void expandRectangles(final RectStack stack) {
        ElkNode lastRect = stack.getLastRectangle();

        double heightOfRectangles = lastRect.getY() + lastRect.getHeight() - stack.getY();
        double raisingFactor = stack.getHeight() / heightOfRectangles;

        double totalShift = 0;

        for (ElkNode rect : stack.getChildren()) {
            rect.setWidth(stack.getWidth());

            if (raisingFactor > 0) {
                double oldRectHeight = rect.getHeight();

                rect.setHeight(rect.getHeight() * raisingFactor);
                rect.setY(rect.getY() + totalShift);

                totalShift += rect.getHeight() - oldRectHeight;
            }

        }
    }
}
