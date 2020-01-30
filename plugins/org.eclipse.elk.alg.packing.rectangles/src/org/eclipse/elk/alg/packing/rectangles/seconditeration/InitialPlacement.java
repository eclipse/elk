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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.packing.rectangles.util.RectRow;
import org.eclipse.elk.alg.packing.rectangles.util.RectStack;
import org.eclipse.elk.graph.ElkNode;

/**
 * Class that offers methods that calculate the initial placement of rectangles in {@link RowFillingAndCompaction}.
 * 
 * @see RowFillingAndCompaction
 */
public final class InitialPlacement {

    //////////////////////////////////////////////////////////////////
    // Private Constructor.
    private InitialPlacement() {
    }

    //////////////////////////////////////////////////////////////////
    // Main method.
    /**
     * Simply places the rectangles as {@link RectRow}s onto the drawing area, bounded by the calculated bounding box
     * width.
     * 
     * @param rectangles
     *            rectangles to be placed.
     * @param boundingWidth
     *            bounding box width.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     * @return returns the rows in which the rectangles were placed.
     */
    protected static List<RectRow> place(final List<ElkNode> rectangles, final double boundingWidth, final double nodeNodeSpacing) {
        List<RectRow> rows = new ArrayList<RectRow>();
        RectRow currRow = new RectRow(0);
        double currDrawingHeight = 0;

        for (ElkNode currRect : rectangles) {
            double potentialRowWidth = currRow.getWidth() + currRect.getWidth();
            if (potentialRowWidth > boundingWidth) {
                currDrawingHeight += currRow.getHeight();
                rows.add(currRow);
                currRow = new RectRow(currDrawingHeight);
            }
            createNewStackAndAddToRow(currRect, currRow, nodeNodeSpacing);
        }
        rows.add(currRow);
        return rows;
    }

    //////////////////////////////////////////////////////////////////
    // Helping methods.

    /**
     * Method that creates a new {@link RectStack}, adds the given {@link ElkNode} to the {@link RectStack}, and adds
     * the created {@link RectStack} to the given {@link RectRow}. The new {@link RectStack} and given {@link ElkNode}
     * will be at the end of the {@link RectRow}.
     * 
     * @param rect
     *            Rectangle to add to a {@link RectStack}.
     * @param row
     *            The {@link ElkNode} the new {@link RectStack} is added to.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     * @return returns the newly created {@link RectStack}.
     */
    private static RectStack createNewStackAndAddToRow(final ElkNode rect, final RectRow row, final double nodeNodeSpacing) {
        rect.setLocation(row.getWidth(), row.getY());
        RectStack newStack = new RectStack(rect, rect.getX(), rect.getY(), row, nodeNodeSpacing);
        row.assignStack(newStack);
        return newStack;
    }
}
