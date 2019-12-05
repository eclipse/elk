/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.packing.rectangles;

import java.util.List;

import org.eclipse.elk.alg.packing.rectangles.firstiteration.AreaApproximation;
import org.eclipse.elk.alg.packing.rectangles.options.RectPackingOptions;
import org.eclipse.elk.alg.packing.rectangles.seconditeration.RowFillingAndCompaction;
import org.eclipse.elk.alg.packing.rectangles.specialcase.SpecialCaseFeasibility;
import org.eclipse.elk.alg.packing.rectangles.specialcase.SpecialCasePlacer;
import org.eclipse.elk.alg.packing.rectangles.util.DrawingData;
import org.eclipse.elk.alg.packing.rectangles.util.DrawingUtil;
import org.eclipse.elk.alg.packing.rectangles.util.PackingStrategy;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * A layout algorithm that does not take edges into account, but treats all nodes as isolated boxes. This is useful for
 * parts of a diagram that consist of objects without connections, such as parallel regions in Statecharts.
 * <p>
 * Nodes are viewed as rectangles and so {@link ElkNode}s are referred to as rectangles in the comments.
 * </p>
 * <p>
 * Depending on the settings, checks for a specified special case, calculates a layout with a approximation algorithm or
 * uses that approximation algorithm for the needed area of the rectangles and places the rectangles nicely aligned on
 * the drawing area according to that approximation.
 * </p>
 */
public class RectPackingLayoutProvider extends AbstractLayoutProvider {
    /** Desired aspect ratio. */
    private double dar;
    /** Packing Strategy. */
    private PackingStrategy packingStrat;
    /** Shift when placing behind or below the last placed rectangle. */
    private boolean lastPlaceShift;
    /** Only first iteration. */
    private boolean onlyFirstIteration;
    /** Check for special case. */
    private boolean checkSpecialCase;
    /** Expand nodes to fill the bounding box. */
    private boolean expandNodes;
    /** Padding surrounding the drawing. */
    private ElkPadding padding;

    /**
     * Calculating and applaying layout to the model.
     */
    @Override
    public void layout(final ElkNode layoutGraph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Rectangle Packing", 1);
        dar = layoutGraph.getProperty(RectPackingOptions.ASPECT_RATIO);
        packingStrat = layoutGraph.getProperty(RectPackingOptions.STRATEGY);
        lastPlaceShift = layoutGraph.getProperty(RectPackingOptions.LAST_PLACE_SHIFT);
        onlyFirstIteration = layoutGraph.getProperty(RectPackingOptions.ONLY_FIRST_ITERATION);
        checkSpecialCase = layoutGraph.getProperty(RectPackingOptions.CHECK_FOR_SPECIAL_CASE);
        expandNodes = layoutGraph.getProperty(RectPackingOptions.EXPAND_NODES);
        padding = layoutGraph.getProperty(RectPackingOptions.PADDING);

        List<ElkNode> rectangles = layoutGraph.getChildren();
        DrawingUtil.resetCoordinates(rectangles);
        DrawingData drawing;

        // PLACEMENT ACCORDING TO SETTINGS
        if (checkSpecialCase && SpecialCaseFeasibility.confirm(rectangles)) {
            drawing = SpecialCasePlacer.place(rectangles, dar, expandNodes);
        } else {
            AreaApproximation firstIt = new AreaApproximation(dar, packingStrat, lastPlaceShift);
            drawing = firstIt.approxBoundingBox(rectangles);
            if (!onlyFirstIteration) {
                DrawingUtil.resetCoordinates(rectangles);
                RowFillingAndCompaction secondIt = new RowFillingAndCompaction(dar, expandNodes);
                drawing = secondIt.start(rectangles, drawing.getDrawingWidth());
            }
        }

        // FINAL TOUCH
        applyPadding(rectangles);
        layoutGraph.setDimensions(drawing.getDrawingWidth() + padding.getHorizontal(),
                drawing.getDrawingHeight() + padding.getVertical());

        progressMonitor.done();
    }

    /**
     * Shifts all rectangles to the right and bottom according to the specified padding.
     * 
     * @param rectangles
     *            list of rectangles that have been placed.
     */
    private void applyPadding(final List<ElkNode> rectangles) {
        for (ElkNode rect : rectangles) {
            rect.setLocation(rect.getX() + padding.getLeft(), rect.getY() + padding.getTop());
        }
    }
}
