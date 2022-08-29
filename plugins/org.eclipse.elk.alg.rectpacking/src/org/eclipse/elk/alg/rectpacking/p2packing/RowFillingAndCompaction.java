/*******************************************************************************
 * Copyright (c) 2018, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.rectpacking.p2packing;

import java.util.List;

import org.eclipse.elk.alg.rectpacking.options.InternalProperties;
import org.eclipse.elk.alg.rectpacking.util.BlockStack;
import org.eclipse.elk.alg.rectpacking.util.DrawingData;
import org.eclipse.elk.alg.rectpacking.util.DrawingDataDescriptor;
import org.eclipse.elk.alg.rectpacking.util.DrawingUtil;
import org.eclipse.elk.alg.rectpacking.util.RectRow;
import org.eclipse.elk.core.math.ElkPadding;
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
    /** Desired aspect ratio. */
    private double aspectRatio;
    /** Spacing between two nodes. */
    private double nodeNodeSpacing;

    //////////////////////////////////////////////////////////////////
    // Constructors.
    /**
     * Creates an {@link RowFillingAndCompaction} object to execute the second iteration on.
     * 
     * @param aspectRatio The desired aspect ratio.
     * @param nodeNodeSpacing The spacing between two nodes.
     */
    public RowFillingAndCompaction(final double aspectRatio, final double nodeNodeSpacing) {
        this.aspectRatio = aspectRatio;
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
    public DrawingData start(final List<ElkNode> rectangles,
            final IElkProgressMonitor progressMonitor, final ElkNode layoutGraph, final ElkPadding padding) {
        double targetWidth = layoutGraph.getProperty(InternalProperties.TARGET_WIDTH);
        double minWidth = layoutGraph.getProperty(InternalProperties.MIN_WIDTH);
        double minHeight = layoutGraph.getProperty(InternalProperties.MIN_HEIGHT);
        // Reset coordinates potentially set by width approximation.
        DrawingUtil.resetCoordinates(layoutGraph.getChildren());

        // Initial placement for rectangles in blocks in each row.
        List<RectRow> rows = InitialPlacement.place(layoutGraph.getChildren(), targetWidth, nodeNodeSpacing);
        
        // Compaction of blocks.
        for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
            RectRow currentRow = rows.get(rowIdx);
            if (rowIdx != 0) {
                RectRow previousRow = rows.get(rowIdx - 1);
                currentRow.setY(previousRow.getY() + previousRow.getHeight() + nodeNodeSpacing);
            }
            Compaction.compact(rowIdx, rows, targetWidth, nodeNodeSpacing);
            adjustWidthAndHeight(currentRow);
            // Log graph after first row compaction.
            if (progressMonitor.isLoggingEnabled()) {
                progressMonitor.logGraph(layoutGraph, "Compacted row " + rowIdx);
            }
        }
         
        KVector size = DrawingUtil.calculateDimensions(rows, nodeNodeSpacing);
        
        double totalWidth = Math.max(size.x, minWidth - padding.getHorizontal());
        double height = Math.max(size.y, minHeight - padding.getVertical());
        double additionalHeight = height - size.y;
        layoutGraph.setProperty(InternalProperties.ADDITIONAL_HEIGHT, additionalHeight);
        layoutGraph.setProperty(InternalProperties.ROWS, rows);

        return new DrawingData(this.aspectRatio, totalWidth, size.y + additionalHeight, DrawingDataDescriptor.WHOLE_DRAWING);
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
        int index = 0;
        for (BlockStack stack : row.getStacks()) {
            stack.updateDimension();
            maxHeight = Math.max(maxHeight, stack.getHeight());
            maxWidth += stack.getWidth() + (index > 0 ? nodeNodeSpacing : 0);
            index++;
        }
        row.setHeight(maxHeight);
        row.setWidth(maxWidth);
    }
}
