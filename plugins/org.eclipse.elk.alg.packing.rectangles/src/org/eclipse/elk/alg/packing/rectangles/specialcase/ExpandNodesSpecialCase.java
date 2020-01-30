/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.packing.rectangles.specialcase;

import java.util.List;

import org.eclipse.elk.graph.ElkNode;

/**
 * Offers method to expand nodes in the special case.
 * 
 * @see SpecialCasePlacer
 */
public final class ExpandNodesSpecialCase {

    private ExpandNodesSpecialCase() {
    }

    /**
     * Expands nodes so the bounding box, given by width and height of the bounding box of the packing, is filled. The
     * biggest rectangle does not need to be resized.
     * 
     * @param rects
     *            The rectangles to be expanded.
     * @param height
     *            The height of the bounding box or the biggest rectangle.
     * @param maxColumns
     *            The amount of columns.
     * @param rows
     *            The maximal number of rows.
     * @param width
     *            The width of the widest rectangle.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     */
    protected static void expand(final List<ElkNode> rects, final double height,
            final int maxColumns, final int rows,
            final double width, final double nodeNodeSpacing) {

        // Evenly distribute height and adjust y values.
        int index = 0;
        // Get the node in the last row.
        int minColumns = (rects.size() % (rows - 1));
        // Normal node height.
        double nodeHeight = ((height + nodeNodeSpacing) / rows) - nodeNodeSpacing;
        // Node high for nodes in columns that have one less node.
        double higherNodeHeight = ((height + nodeNodeSpacing) / (rows - 1)) - nodeNodeSpacing;
        for (int row = 0; row < rows; row++) {
            // The last row might have fewer columns.
            int columns = row == rows - 1 ? minColumns : maxColumns;
            for (int column = 0; column < columns; column++) {
                // Compensate in height for nodes missing in the last row.
                double actualHeight = nodeHeight;
                if (column >= minColumns) {
                    actualHeight = higherNodeHeight;
                }
                
                ElkNode rect = rects.get(index);
                rect.setWidth(width);
                rect.setHeight(actualHeight);
                rect.setY(row * (actualHeight + nodeNodeSpacing));
                index++;
            }
        }
    }
}
