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
import java.util.ListIterator;

import org.eclipse.elk.alg.packing.rectangles.util.DrawingData;
import org.eclipse.elk.alg.packing.rectangles.util.DrawingDataDescriptor;
import org.eclipse.elk.alg.packing.rectangles.util.RectRow;
import org.eclipse.elk.alg.packing.rectangles.util.RectStack;
import org.eclipse.elk.graph.ElkNode;

/**
 * Second iteration of the algorithm. Actual placement of the boxes inside the approximated bounding box. Rectangles are
 * placed in rows, which are compacted and then filled with elements from the subsequent row.
 */
public class RowFillingAndCompaction {
    //////////////////////////////////////////////////////////////////
    // Fields
    /** Current drawing width. */
    private double drawingWidth;
    /** Current drawing height. */
    private double drawingHeight;
    /** Indicates that something was optimized while compacting. */
    private boolean compactRepeat;
    /** Indicates that something was optimized while row filling. */
    private boolean rowFillingRepeatI;
    /** Indicates that something was optimized while row filling. */
    private boolean rowFillingRepeatII;
    /** Desired aspect ratio. */
    private double dar;
    /** Indicates whether to expand the nodes in the end. */
    private boolean expandNodes;

    //////////////////////////////////////////////////////////////////
    // Constructor
    /**
     * Creates an {@link RowFillingAndCompaction} object to execute the second iteration on.
     * 
     * @param desiredAr
     *            desired aspect ratio.
     * @param expandNodes
     *            indicates whether to expand the nodes in the end.
     */
    public RowFillingAndCompaction(final double desiredAr, final boolean expandNodes) {
        this.dar = desiredAr;
        this.expandNodes = expandNodes;
        this.compactRepeat = true;
        this.rowFillingRepeatI = true;
        this.rowFillingRepeatII = true;
    }

    //////////////////////////////////////////////////////////////////
    // Starting method.
    /**
     * Placement of the rectangles given by {@link ElkNode} inside the given bounding box.
     * 
     * @param rectangles
     *            given set of rectangles to be placed inside the bounding box.
     * @param boundingBoxWidth
     *            width of the given bounding box.
     * @return Drawing data for a produced drawing.
     */
    public DrawingData start(final List<ElkNode> rectangles, final double boundingBoxWidth) {
        List<RectRow> rows = InitialPlacement.place(rectangles, boundingBoxWidth);

        for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
            while (this.compactRepeat || this.rowFillingRepeatI || this.rowFillingRepeatII) {
                this.compactRepeat = Compaction.compact(rowIdx, rows, boundingBoxWidth);

                if (rowIdx < rows.size() - 1) {
                    this.rowFillingRepeatI = RowFilling.fill(RowFillStrat.WHOLE_STACK, rowIdx, rows, boundingBoxWidth);
                    this.rowFillingRepeatII = RowFilling.fill(RowFillStrat.SINGLE_RECT, rowIdx, rows, boundingBoxWidth);
                } else {
                    this.rowFillingRepeatI = false;
                    this.rowFillingRepeatII = false;
                }
            }
            resetImprovementBooleans();
        }
        deleteEmptyRows(rows);
        calculateDimensions(rows);

        // expand notes if configured.
        if (this.expandNodes) {
            RectangleExpansion.expand(rows, this.drawingWidth);
        }

        return new DrawingData(this.dar, this.drawingWidth, this.drawingHeight, DrawingDataDescriptor.WHOLE_DRAWING);
    }

    //////////////////////////////////////////////////////////////////
    // Helping method.

    /**
     * Calculates the maximum width and height for the given list of {@link RectRow}s.
     */
    private void calculateDimensions(final List<RectRow> rows) {
        // new calculation of drawings dimensions.
        double maxWidth = Double.NEGATIVE_INFINITY;
        double newHeight = 0;
        for (RectRow row : rows) {
            maxWidth = Math.max(maxWidth, row.getWidth());
            newHeight += row.getHeight();
        }

        this.drawingHeight = newHeight;
        this.drawingWidth = maxWidth;
    }

    /**
     * Sets the fields indicating whether improvements have been made to false.
     */
    private void resetImprovementBooleans() {
        this.compactRepeat = true;
        this.rowFillingRepeatI = true;
        this.rowFillingRepeatII = true;
    }

    /**
     * Removes every empty {@link RectStack} from the given {@link RectRow}s.
     */
    private static void deleteEmptyRows(final List<RectRow> rows) {
        ListIterator<RectRow> iter = rows.listIterator();
        while (iter.hasNext()) {
            if (iter.next().hasNoAssignedStacks()) {
                iter.remove();
            }
        }
    }

    //////////////////////////////////////////////////////////////////
    // Helping enumerate.
    /**
     * Enumerate to identify a row-filling strategy.
     */
    protected enum RowFillStrat {
        /** Fill row with whole stacks. */
        WHOLE_STACK,
        /** Fill row with single rectangles. */
        SINGLE_RECT;
    }
}
