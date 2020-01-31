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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.packing.rectangles.util.DrawingData;
import org.eclipse.elk.alg.packing.rectangles.util.DrawingDataDescriptor;
import org.eclipse.elk.graph.ElkNode;

/**
 * Class that offers a method for placing rectangles in the special case where one big rectangle and other smaller
 * rectangles exists.
 */
public final class SpecialCasePlacer {

    //////////////////////////////////////////////////////////////////
    // Constructor.
    private SpecialCasePlacer() {
    }

    //////////////////////////////////////////////////////////////////
    // Main method.
    /**
     * Distributes the rectangles evenly along the big rectangle.
     * 
     * @param rectangles
     *            rectangles in order they are supposed to be placed.
     * @param dar
     *            desired Aspect Ratio.
     * @param expandNodes
     *            indicates whether the nodes should be expanded to fill the bounding box.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     * @param mode
     *            Packing mode.
     * @return values of the drawing (width, height, scale measure, area.
     */
    public static DrawingData place(final List<ElkNode> rectangles, final double dar, final boolean expandNodes,
            final double nodeNodeSpacing, final int mode) {
        ElkNode biggestRect = findBiggestRectangle(rectangles);

        List<ElkNode> beforeBiggest = new ArrayList<ElkNode>();
        List<ElkNode> afterBiggest = new ArrayList<ElkNode>();
        boolean beforeAfterSwitch = true;

        for (ElkNode rect : rectangles) {
            if (rect.equals(biggestRect)) {
                beforeAfterSwitch = false;
            } else {
                if (beforeAfterSwitch) {
                    beforeBiggest.add(rect);
                } else {
                    afterBiggest.add(rect);
                }
            }
        }
        ElkNode widestRectBefore = null;
        ElkNode highestRectBefore = null;
        ElkNode widestRectAfter = null;
        ElkNode highestRectAfter = null;
        int maxRowsBefore = 0;
        int maxRowsAfter = 0;
        int columnsBefore = 0;
        int rowsBefore = 0;
        int columnsAfter = 0;
        int rowsAfter = 0;
        if (!beforeBiggest.isEmpty()) {
            widestRectBefore = findWidestRectangle(beforeBiggest);
            highestRectBefore = findHighestRectangle(beforeBiggest);
            maxRowsBefore = (int) ((biggestRect.getHeight() + nodeNodeSpacing) / (highestRectBefore.getHeight() + nodeNodeSpacing));
            columnsBefore = (int) Math.ceil(1.0 * beforeBiggest.size() / maxRowsBefore);
            rowsBefore = (int) Math.ceil(((double) beforeBiggest.size()) / columnsBefore);
            
        }
        if (!afterBiggest.isEmpty()) {
            widestRectAfter = findWidestRectangle(afterBiggest);
            highestRectAfter = findHighestRectangle(afterBiggest);
            maxRowsAfter = (int) ((biggestRect.getHeight() + nodeNodeSpacing) / (highestRectAfter.getHeight() + nodeNodeSpacing));
            columnsAfter = (int) Math.ceil(1.0 * afterBiggest.size() / maxRowsAfter);
            rowsAfter = (int) Math.ceil(((double) afterBiggest.size()) / columnsAfter);
        }
        // This number is correct for all rows despite the last one.
        // The last one has rowsBefore - (beforeBiggest.size() % howManyColumnsBefore) rows
        double currWidth = 0;

        // Place before.
        if (!beforeBiggest.isEmpty()) {
            currWidth = placeRects(currWidth, beforeBiggest, widestRectBefore.getWidth(), highestRectBefore.getHeight(),
                    columnsBefore, rowsBefore, nodeNodeSpacing, mode);
        }

        // place BiggestRect.
        biggestRect.setLocation(currWidth, 0);
        currWidth += biggestRect.getWidth() + nodeNodeSpacing;

        // Place after.
        if (!afterBiggest.isEmpty()) {
            currWidth = placeRects(currWidth, afterBiggest, widestRectAfter.getWidth(), highestRectAfter.getHeight(),
                columnsAfter, rowsAfter, nodeNodeSpacing, mode);
        }

        // expand nodes.
        if (expandNodes) {
            if (!beforeBiggest.isEmpty()) {
                ExpandNodesSpecialCase.expand(beforeBiggest,
                        biggestRect.getHeight(),
                        biggestRect.getX(),
                        columnsBefore,
                        rowsBefore,
                        nodeNodeSpacing,
                        mode);
            }
            if (!afterBiggest.isEmpty()) {
                ExpandNodesSpecialCase.expand(afterBiggest,
                        biggestRect.getHeight(),
                        currWidth - biggestRect.getX() - biggestRect.getWidth() - nodeNodeSpacing,
                        columnsAfter,
                        rowsAfter,
                        nodeNodeSpacing,
                        mode);
            }
        }

        // prepare return object.
        DrawingData returner =
                new DrawingData(dar, currWidth, biggestRect.getHeight(), DrawingDataDescriptor.WHOLE_DRAWING);
        return returner;
    }

    //////////////////////////////////////////////////////////////////
    // Helping methods.
    /**
     * Places rectangles in list according to starting x position in the given amount of columns and rows. The columns
     * are separated by the given width parameter.
     * 
     * @param startX
     *            x-coordinate, where the first rectangle is placed.
     * @param rects
     *            list of rectangles to be placed.
     * @param width
     *            width of the widest rectangle in the list. Width between columns.
     * @param height
     *            height of the rectangles to be placed.
     * @param columns
     *            number of columns to place the rectangles in.
     * @param rows
     *            number of rows to place the rectangles in.
     * @param nodeNodeSpacing
     *            The spacing between two nodes.
     * @param mode
     *            Packing mode.
     * @return
     *            Current width including the node node spacing      
     */
    private static double placeRects(final double startX, final List<ElkNode> rects, final double width,
            final double height, final int columns, final int rows, final double nodeNodeSpacing, final int mode) {
        double currentX = startX;
        double currentY = 0;
        double blockWidth = 0;
        int index = 0;
        
        if (mode == 0) {
            // Left to right, balance columns
            for (int row = 0; row < rows; row++) {
                int actualColumns = columns;
                if (row == rows - 1 && columns > 1 && rects.size() % columns != 0) {
                    actualColumns = rects.size() % columns;
                }
                double maxHeight = Double.MIN_VALUE;
                for (int column = 0; column < actualColumns; column++) {
                    ElkNode rect = rects.get(index);
                    rect.setX(currentX);
                    rect.setY(currentY);
                    if (maxHeight < rect.getHeight()) {
                        maxHeight = rect.getHeight();
                    }
                    currentX += rect.getWidth() + nodeNodeSpacing;
                    if (blockWidth < currentX) {
                        blockWidth = currentX;
                    }
                    index++;
                }
                currentX = startX;
                currentY += maxHeight + nodeNodeSpacing;
            }
        } else if (mode == 1) {
            // Left to right, balance rows
            for (int row = 0; row < rows; row++) {
                int actualColumns = columns;
                if (row >= rects.size() % rows && rows > 1 && rects.size() % rows != 0) {
                    actualColumns = columns - 1;
                }
                double maxHeight = Double.MIN_VALUE;
                for (int column = 0; column < actualColumns; column++) {
                    ElkNode rect = rects.get(index);
                    rect.setX(currentX);
                    rect.setY(currentY);
                    if (maxHeight < rect.getHeight()) {
                        maxHeight = rect.getHeight();
                    }
                    currentX += rect.getWidth() + nodeNodeSpacing;
                    if (blockWidth < currentX) {
                        blockWidth = currentX;
                    }
                    index++;
                }
                currentX = startX;
                currentY += maxHeight + nodeNodeSpacing;
            }
        } else if (mode == 2) {
            // Top to bottom, balance columns
            for (int column = 0; column < columns; column++) {
                double maxWidth = Double.MIN_VALUE;
                int actualRows = rows;
                if (column >= rects.size() % columns && columns > 1 && rects.size() % columns != 0) {
                    actualRows = rows - 1;
                }
                for (int row = 0; row < actualRows && index < rects.size(); row++) {
                    if (index < rects.size()) {
                        ElkNode rect = rects.get(index);
                        rect.setX(currentX);
                        rect.setY(currentY);
                        if (maxWidth < rect.getWidth()) {
                            maxWidth = rect.getWidth();
                        }
                        currentY += rect.getHeight() + nodeNodeSpacing;
                        index++;
                    }
                }
                currentY = 0;
                currentX += maxWidth + nodeNodeSpacing;
                if (blockWidth < currentX) {
                    blockWidth = currentX;
                }
            }
        } else if (mode == 3) {
            // Top to bottom, balance rows
            for (int column = 0; column < columns; column++) {
                int actualRows = rows;
                if (column == columns - 1 && rows > 1 && rects.size() % rows != 0) {
                    actualRows = rects.size() % rows;
                }
                double maxWidth = Double.MIN_VALUE;
                for (int row = 0; row < actualRows; row++) {
                    ElkNode rect = rects.get(index);
                    rect.setX(currentX);
                    rect.setY(currentY);
                    if (maxWidth < rect.getWidth()) {
                        maxWidth = rect.getWidth();
                    }
                    currentY += rect.getHeight() + nodeNodeSpacing;
                    index++;
                }
                currentY = 0;
                currentX += maxWidth + nodeNodeSpacing;
                if (blockWidth < currentX) {
                    blockWidth = currentX;
                }
            }
        }
        return blockWidth;
    }

    /**
     * Finds and returns the biggest rectangle in the list.
     */
    private static ElkNode findBiggestRectangle(final List<ElkNode> rectangles) {
        ElkNode biggest = rectangles.get(0);

        for (ElkNode rect : rectangles) {
            if (rect.getHeight() > biggest.getHeight()) {
                biggest = rect;
            }
        }
        return biggest;
    }

    /**
     * Finds and returns the widest rectangle of the given list.
     */
    private static ElkNode findWidestRectangle(final List<ElkNode> rectangles) {
        ElkNode widest = null;

        for (ElkNode rect : rectangles) {
            if (widest == null || rect.getWidth() > widest.getWidth()) {
                widest = rect;
            }
        }
        return widest;
    }

    /**
     * Finds and returns the widest rectangle of the given list.
     */
    private static ElkNode findHighestRectangle(final List<ElkNode> rectangles) {
        ElkNode highest = null;

        for (ElkNode rect : rectangles) {
            if (highest == null || rect.getHeight() > highest.getHeight()) {
                highest = rect;
            }
        }
        return highest;
    }
}
