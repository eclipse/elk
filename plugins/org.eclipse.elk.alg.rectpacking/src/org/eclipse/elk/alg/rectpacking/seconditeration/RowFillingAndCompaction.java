/*******************************************************************************
 * Copyright (c) 2018, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.rectpacking.seconditeration;

import java.util.List;

import org.eclipse.elk.alg.rectpacking.util.Block;
import org.eclipse.elk.alg.rectpacking.util.BlockStack;
import org.eclipse.elk.alg.rectpacking.util.DrawingData;
import org.eclipse.elk.alg.rectpacking.util.DrawingDataDescriptor;
import org.eclipse.elk.alg.rectpacking.util.RectRow;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * Second iteration of the algorithm. Actual placement of the boxes inside the approximated bounding box. Rectangles are
 * placed in rows, which are compacted and then filled with elements from the subsequent row.
 */
public class RowFillingAndCompaction {
    //////////////////////////////////////////////////////////////////
    // Fields.
    /** Current drawing width. */
    private double drawingWidth;
    /** Current drawing height. */
    private double drawingHeight;
    /** Desired aspect ratio. */
    private double aspectRatio;
    /** Indicates whether to expand the nodes in the end. */
    private boolean expandNodes;
    /** Indicates whether the drawing should be expanded to fit the aspect ratio. */
    private boolean expandToAspectRatio;
    /** Indicates whether the rows of the drawing should be compacted. */
    private boolean compaction;
    /** Spacing between two nodes. */
    private double nodeNodeSpacing;

    //////////////////////////////////////////////////////////////////
    // Constructors.
    /**
     * Creates an {@link RowFillingAndCompaction} object to execute the second iteration on.
     * 
     * @param aspectRatio The desired aspect ratio.
     * @param expandNodes Whether to expand the nodes in the end.
     */
    public RowFillingAndCompaction(final double aspectRatio, final boolean expandNodes, final boolean expandToAspectRatio,
            final boolean compaction, final double nodeNodeSpacing) {
        this.aspectRatio = aspectRatio;
        this.expandNodes = expandNodes;
        this.expandToAspectRatio = expandToAspectRatio;
        this.compaction = compaction;
        this.nodeNodeSpacing = nodeNodeSpacing;
    }

    //////////////////////////////////////////////////////////////////
    // Starting method.
    /**
     * Placement of the rectangles given by {@link ElkNode} inside the given bounding box.
     * @param rectangles The set of rectangles to be placed inside the bounding box.
     * @param minParentSize The minimal size of the parent.
     * @param nodeNodeSpacing The spacing between two nodes.
     * @return Drawing data for a produced drawing.
     */
    public DrawingData start(final List<ElkNode> rectangles, final double maxWidth, final KVector minParentSize,
            final IElkProgressMonitor progressMonitor, final ElkNode layoutGraph) {
        // Initial placement for rectangles in blocks in each row.
        List<RectRow> rows = InitialPlacement.place(rectangles, maxWidth, nodeNodeSpacing);
        
        if (progressMonitor.isLoggingEnabled()) {
            progressMonitor.logGraph(layoutGraph, "After placement");
        }
        
        // Compaction of blocks.
        if (compaction) {
            for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
                RectRow currentRow = rows.get(rowIdx);
                if (rowIdx != 0) {
                    RectRow previousRow = rows.get(rowIdx - 1);
                    currentRow.setY(previousRow.getY() + previousRow.getHeight());
                }
                Compaction.compact(rowIdx, rows, maxWidth, nodeNodeSpacing);
                adjustWidthAndHeight(currentRow);
            }
        } else {
            // Put every block in its own block stack.
            for (RectRow row : rows) {
                for (Block block : row.getChildren()) {
                    BlockStack stack = new BlockStack(block.getX(), block.getY());
                    stack.addBlock(block);
                    row.getStacks().add(stack);
                }
            }
        }
         
        calculateDimensions(rows);

        if (progressMonitor.isLoggingEnabled()) {
            progressMonitor.logGraph(layoutGraph, "After compaction");
        }
        
        double totalWidth = Math.max(this.drawingWidth, minParentSize.x);
        double minHeight = Math.max(this.drawingHeight, minParentSize.y);
        double additionalHeight = minHeight - this.drawingHeight;
        if (expandNodes && expandToAspectRatio) {
            double aspectRatio = totalWidth / minHeight;
            if (aspectRatio < this.aspectRatio) {
                totalWidth = minHeight * this.aspectRatio;
            } else {
                additionalHeight += (totalWidth / this.aspectRatio) - minHeight;
            }
        }
        
        if (this.expandNodes) {
            RectangleExpansion.expand(rows, totalWidth + nodeNodeSpacing, additionalHeight, nodeNodeSpacing);
        }

        if (progressMonitor.isLoggingEnabled()) {
            progressMonitor.logGraph(layoutGraph, "After expansion");
        }

        return new DrawingData(this.aspectRatio, totalWidth, this.drawingHeight + additionalHeight, DrawingDataDescriptor.WHOLE_DRAWING);
    }

    //////////////////////////////////////////////////////////////////
    // Helper method.

    /**
     * Recalculates the width and height of the row.
     * @param row The row.
     */
    private void adjustWidthAndHeight(final RectRow row) {
        double maxHeight = 0;
        double maxWidth = 0;
        for (BlockStack stack : row.getStacks()) {
            stack.updateDimension();
            maxHeight = Math.max(maxHeight, stack.getHeight());
            maxWidth += stack.getWidth();
        }
        row.setHeight(maxHeight);
        row.setWidth(maxWidth);
    }

    /**
     * Calculates the maximum width and height for the given list of {@link RectRow}s.
     * Since the node node spacing is included in the width of a row, it has to be subtracted.
     * @param rows The rows.
     * @param nodeNodeSpacing The spacing between two nodes.
     */
    private void calculateDimensions(final List<RectRow> rows) {
        double maxWidth = 0;
        double newHeight = 0;
        for (RectRow row : rows) {
            maxWidth = Math.max(maxWidth, row.getWidth());
            newHeight += row.getHeight();
        }

        this.drawingHeight = newHeight - nodeNodeSpacing;
        this.drawingWidth = maxWidth - nodeNodeSpacing;
    }
}
