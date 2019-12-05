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
     * @return values of the drawing (width, height, scale measure, area.
     */
    public static DrawingData place(final List<ElkNode> rectangles, final double dar, final boolean expandNodes) {
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

        ElkNode widestRectBefore = findWidestRectangle(beforeBiggest);
        ElkNode widestRectAfter = findWidestRectangle(afterBiggest);

        int howManyRows = (int) (biggestRect.getHeight() / widestRectBefore.getHeight());
        int howManyColumnsBefore = (int) Math.ceil(1.0 * beforeBiggest.size() / howManyRows);
        int howManyColumnsAfter = (int) Math.ceil(1.0 * afterBiggest.size() / howManyRows);
        double currWidth = 0;

        // Place before.
        if (beforeBiggest.size() > 0) {
            placeRects(currWidth, beforeBiggest, widestRectBefore.getWidth(), widestRectBefore.getHeight(),
                    howManyColumnsBefore, howManyRows);

            // calculate width of rectangles placed before biggest rectangle and biggest rectangles' x coordinate.
            currWidth = howManyColumnsBefore * widestRectBefore.getWidth();
        }

        // place BiggestRect.
        biggestRect.setLocation(currWidth, 0);
        currWidth += biggestRect.getWidth();

        // Place after.
        if (afterBiggest.size() > 0) {
            placeRects(currWidth, afterBiggest, widestRectAfter.getWidth(), widestRectAfter.getHeight(),
                    howManyColumnsAfter, howManyRows);
            currWidth += howManyColumnsAfter * widestRectAfter.getWidth();
        }

        // expand nodes.
        if (expandNodes) {
            ExpandNodesSpecialCase.expand(beforeBiggest, afterBiggest, biggestRect.getHeight(), howManyColumnsBefore,
                    howManyColumnsAfter, widestRectBefore.getWidth(), widestRectAfter.getWidth());
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
     * @param rectList
     *            list of rectangles to be placed.
     * @param width
     *            width of the widest rectangle in the list. Width between columns.
     * @param height
     *            height of the rectangles to be placed.
     * @param columns
     *            number of columns to place the rectangles in.
     * @param rows
     *            number of rows to place the rectangles in.
     */
    private static void placeRects(final double startX, final List<ElkNode> rectList, final double width,
            final double height, final int columns, final int rows) {
        double currentX = startX;
        double currentY = 0;
        int columnCounter = 1;
        int rowCounter = 1;

        // place rectangles sequentially according to given rows and columns.
        for (ElkNode rect : rectList) {
            rect.setLocation(currentX, currentY);
            columnCounter++;
            currentX += width;
            if (columnCounter > columns) {
                columnCounter = 1;
                rowCounter++;
                if (rowCounter > rows) {
                    break;
                }
                currentX = startX;
                currentY += height;
            }
        }
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
}
