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
     * One big rectangle with a set of smaller rectangles. The bigger rectangle has to have a greater height than all of
     * the smaller rectangles.
     * The smaller rectangles shave a overall width and height difference as specified by the corresponding jitter.
     * 
     * @param rectangles
     *            rectangles to be packed.
     * @param widthJitter
     *            Jitter in small rectangle width.
     * @param heightJitter
     *            Jitter in small rectangle height.
     * @return true, if special case is met, false otherwise.
     */
    public static boolean confirm(final List<ElkNode> rectangles, double widthJitter, double heightJitter) {
        ElkNode theBiggerRect = null;
        // 
        double beforeMinWidth = Double.MAX_VALUE;
        double beforeMaxWidth = Double.MIN_VALUE;
        double beforeMinHeight = Double.MAX_VALUE;
        double beforeMaxHeight = Double.MIN_VALUE;
        double afterMinWidth = Double.MAX_VALUE;
        double afterMaxWidth = Double.MIN_VALUE;
        double afterMinHeight = Double.MAX_VALUE;
        double afterMaxHeight = Double.MIN_VALUE;
        boolean clearBiggestRect = true;

        // Check if there exists a biggest rectangle (one with a height higher than all other rectangles).
        for (ElkNode rect : rectangles) {
            if (theBiggerRect == null) {
                theBiggerRect = rect;
            } else {
                if (theBiggerRect.getHeight() < rect.getHeight()) {
                    theBiggerRect = rect;
                    clearBiggestRect = true;
                } else if (theBiggerRect.getHeight() == rect.getHeight()){
                    clearBiggestRect = false;
                }
            }
        }
        if (!clearBiggestRect) {
            return false;
        }
        
        // Check difference in height and width of smaller rectangles
        boolean beforeBigRect = true;
        for (ElkNode rect : rectangles) {
            if (rect.equals(theBiggerRect)) {
                beforeBigRect = false;
                continue;
            } else {
                if (beforeBigRect) {
                    if (rect.getWidth() < beforeMinWidth) {
                        beforeMinWidth = rect.getWidth();
                    }
                    if (rect.getWidth() > beforeMaxWidth) {
                        beforeMaxWidth = rect.getWidth();
                    }
                    if (rect.getHeight() < beforeMinHeight) {
                        beforeMinHeight= rect.getHeight();
                    }
                    if (rect.getHeight() > beforeMaxHeight) {
                        beforeMaxHeight = rect.getHeight();
                    }
                } else {
                    if (rect.getWidth() < afterMinWidth) {
                        afterMinWidth = rect.getWidth();
                    }
                    if (rect.getWidth() > afterMaxWidth) {
                        afterMaxWidth = rect.getWidth();
                    }
                    if (rect.getHeight() < afterMinHeight) {
                        afterMinHeight= rect.getHeight();
                    }
                    if (rect.getHeight() > afterMaxHeight) {
                        afterMaxHeight = rect.getHeight();
                    }
                }
            }
        }

        return (beforeMaxWidth - beforeMinWidth <= widthJitter) &&
                (beforeMaxHeight - beforeMinHeight <= heightJitter) &&
                (afterMaxWidth - afterMinWidth <= widthJitter) &&
                (afterMaxHeight - afterMinHeight <= heightJitter);
    }
}
