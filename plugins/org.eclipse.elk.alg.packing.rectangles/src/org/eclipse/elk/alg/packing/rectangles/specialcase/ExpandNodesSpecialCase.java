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
     * @param maxHeight
     *            The maximal height to be used (not including spacing).
     * @param maxWidth
     *            The maximal width to be used (not including spacing).
     * @param columns
     *            The number of columns.
     * @param rows
     *            The number of rows.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     * @param mode
     *            Packing mode
     */
    protected static void expand(final List<ElkNode> rects, final double maxHeight,
            final double maxWidth, final int columns, final int rows, final double nodeNodeSpacing, final int mode) {
        // Evenly distribute height, width and adjust x,y values.
        int index = 0;
        if (mode == 0) {
            // The order is left to right and columns are evenly filled.
            // Make sure rectangle in the same row has the same height.
            // Evenly distribute width that is unused.
            // Make sure the height defined by maxHeight is filled by the last rectangle.
            for (int row = 0; row < rows; row++) {
                // Calculate elements in row
                int actualColumns = columns;
                if (row == rows - 1 && columns > 1 && rects.size() % columns != 0) {
                    actualColumns = rects.size() % columns;
                }
                double widthOfRow = 0;
                double maxHeightInRow = 0;
                for (int column = 0; column < actualColumns; column++) {
                    ElkNode rect = rects.get(row * columns + column);
                    
                    if (rect.getHeight() > maxHeightInRow) {
                        maxHeightInRow = rect.getHeight();
                    }
                    widthOfRow += rect.getWidth() + nodeNodeSpacing;
                }
                double widthToDistribute = (maxWidth - widthOfRow) / actualColumns;
                for (int column = 0; column < actualColumns; column++) {
                    ElkNode rect = rects.get(row * columns + column);
                    
                    if (row ==  rows - 1) {
                        rect.setHeight(maxHeight - rect.getY());
                    } else {
                        rect.setHeight(maxHeightInRow);
                    }
                    rect.setX(rect.getX() + column * widthToDistribute);
                    rect.setWidth(rect.getWidth() + widthToDistribute);
                }
            }
        } else if (mode == 1) {
            // The order is left to right and rows are evenly filled.
            // Make sure rectangle in the same row has the same height.
            // Evenly distribute unused height to all rows.
            // Evenly distribute width that is unused.
            ElkNode lastNode = rects.get(rects.size() - 1);
            double currentHeight = lastNode.getY() + lastNode.getHeight();
            double heightToDistribute = (maxHeight - currentHeight) / rows;
            for (int row = 0; row < rows; row++) {
                // Calculate elements in row
                int actualColumns = columns;
                if (row >= rects.size() % rows && rows > 1 && rects.size() % rows != 0) {
                    actualColumns = columns - 1;
                }
                double widthOfRow = 0;
                double maxHeightInRow = 0;
                for (int column = 0; column < actualColumns; column++) {
                    ElkNode rect = rects.get(index);
                    
                    if (rect.getHeight() > maxHeightInRow) {
                        maxHeightInRow = rect.getHeight();
                    }
                    widthOfRow += rect.getWidth() + nodeNodeSpacing;
                    index++;
                }
                index -= actualColumns;
                double widthToDistribute = (maxWidth - widthOfRow) / actualColumns;
                for (int column = 0; column < actualColumns; column++) {
                    ElkNode rect = rects.get(index);
                    rect.setY(rect.getY() + row * heightToDistribute);
                    if (row ==  rows - 1) {
                        rect.setHeight(maxHeight - rect.getY());
                    } else {
                        rect.setHeight(maxHeightInRow + heightToDistribute);
                    }
                    rect.setX(rect.getX() + column * widthToDistribute);
                    rect.setWidth(rect.getWidth() + widthToDistribute);
                    index++;
                }
            }
        } else if (mode == 2) {
            // The order is top to bottom and columns are evenly filled.
            // Make sure rectangle in the same column has the same width.
            // Evenly distribute unused height to all rows.
            for (int column = 0; column < columns; column++) {
                int actualRows = column >= rects.size() % columns && rects.size() % columns != 0 ? rows - 1 : rows;
                if (rects.size() <= rows) {
                    actualRows = rects.size();
                }
                double heightOfRow = 0;
                double maxWidthInColumn = 0;
                for (int row = 0; row < actualRows; row++) {
                    ElkNode rect = rects.get(index);
                    
                    if (rect.getWidth() > maxWidthInColumn) {
                        maxWidthInColumn = rect.getWidth();
                    }
                    heightOfRow += rect.getHeight() + nodeNodeSpacing;
                    index++;
                }
                index -= actualRows;
                double heightToDistribute = (maxHeight + nodeNodeSpacing - heightOfRow) / actualRows;
                for (int row = 0; row < actualRows; row++) {
                    ElkNode rect = rects.get(index);
                    rect.setY(rect.getY() + row * heightToDistribute);
                    rect.setHeight(rect.getHeight() + heightToDistribute);
                    rect.setWidth(maxWidthInColumn);
                    index++;
                }
            }
        } else if (mode == 3) {
            // The order is top to bottom and rows are evenly filled.
            // Make sure rectangle in the same column has the same width.
            // Evenly distribute unused height to all rows.
            for (int column = 0; column < columns; column++) {
                int actualRows = rows;
                if (column == columns - 1 && rows > 1 && rects.size() % rows != 0) {
                    actualRows = rects.size() % rows;
                }
                double heightOfRow = 0;
                double maxWidthInColumn = 0;
                for (int row = 0; row < actualRows; row++) {
                    ElkNode rect = rects.get(index);
                    
                    if (rect.getWidth() > maxWidthInColumn) {
                        maxWidthInColumn = rect.getWidth();
                    }
                    heightOfRow += rect.getHeight() + nodeNodeSpacing;
                    index++;
                }
                index -= actualRows;
                double heightToDistribute = (maxHeight + nodeNodeSpacing - heightOfRow) / actualRows;
                for (int row = 0; row < actualRows; row++) {
                    ElkNode rect = rects.get(index);
                    rect.setY(rect.getY() + row * heightToDistribute);
                    rect.setHeight(rect.getHeight() + heightToDistribute);
                    rect.setWidth(maxWidthInColumn);
                    index++;
                }
            }
        }
    }
}
