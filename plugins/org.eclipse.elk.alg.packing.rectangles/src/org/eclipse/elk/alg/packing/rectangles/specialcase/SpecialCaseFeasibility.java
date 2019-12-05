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
 * Class that offers a method for detecting the special case where one big rectangle and other smaller rectangles
 * exists.
 */
public final class SpecialCaseFeasibility {

    //////////////////////////////////////////////////////////////////
    // Constructor.
    private SpecialCaseFeasibility() {
    }

    //////////////////////////////////////////////////////////////////
    // Feasibility method.
    /**
     * Checks whether the given rectangles meet the criteria for being packed with the special case placement algorithm:
     * One big rectangle with a set of smaller rectangles. The bigger rectangle has to have a greater width AND height
     * than all of the smaller rectangles. The smaller rectangles share the same height but differ slightly in width.
     * 
     * @param rectangles
     *            rectangles to be packed.
     * @return true, if special case is met, false otherwise.
     */
    public static boolean confirm(final List<ElkNode> rectangles) {
        ElkNode aSmallerRect = null;
        ElkNode theBiggerRect = null;
        ElkNode widestSmallerRect = null;
        int smallerRectangleCount = 0;

        for (ElkNode rect : rectangles) {
            // first iteration.
            if (aSmallerRect == null && theBiggerRect == null) {
                aSmallerRect = rect;
                widestSmallerRect = rect;
                smallerRectangleCount++;
                continue;
            }

            // biggerRect is assigned as soon as a second height is found that is different from the first found height.
            if (theBiggerRect == null) {
                if (rect.getHeight() > aSmallerRect.getHeight()) {
                    theBiggerRect = rect;
                    if (theBiggerRect.getWidth() < widestSmallerRect.getWidth()) {
                        return false;
                    }
                } else if (rect.getHeight() < aSmallerRect.getHeight()) {
                    if (smallerRectangleCount > 1) {
                        return false;
                    }

                    theBiggerRect = aSmallerRect;
                    aSmallerRect = rect;

                    widestSmallerRect = aSmallerRect;
                } else {
                    if (rect.getWidth() > widestSmallerRect.getWidth()) {
                        widestSmallerRect = rect;
                    }
                    smallerRectangleCount++;
                }
                continue;
            }

            // two different heights have been found.
            if (rect.getHeight() != aSmallerRect.getHeight()) {
                return false;
            }
        }

        // Only same height.
        if (theBiggerRect == null) {
            return false;
        }

        return true;
    }
}
