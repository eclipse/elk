/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.packing.rectangles.firstiteration;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.packing.rectangles.util.DrawingData;
import org.eclipse.elk.alg.packing.rectangles.util.DrawingDataDescriptor;
import org.eclipse.elk.alg.packing.rectangles.util.PackingStrategy;
import org.eclipse.elk.graph.ElkNode;

/**
 * Class that handles the first iteration of the algorithm, producing an approximated bounding box for the packing.
 */
public class AreaApproximation {
    /** Desired aspect ratio. */
    private double dar;
    /** Packing Strategy. */
    private PackingStrategy packingStrat;
    /** Shift when placing behind or below the last placed rectangle. */
    private boolean lpShift;

    /**
     * Constructs an object that the first iteration can be executed on.
     * 
     * @param desiredAr
     *            desired aspect ratio.
     * @param packStrat
     *            given packing strategy.
     * @param lpShifting
     *            indicating whether a shift should happen after placing near the last placed node.
     */
    public AreaApproximation(final double desiredAr, final PackingStrategy packStrat, final boolean lpShifting) {
        this.dar = desiredAr;
        this.packingStrat = packStrat;
        this.lpShift = lpShifting;
    }

    /**
     * Calculates a drawing for the given rectangles according options set in the object creation of
     * {@link AreaApproximation}. This method also sets the coordinates for the rectangles.
     * 
     * @param rectangles
     *            the rectangles to place.
     * @return a drawing calculated by this methods algorithm.
     */
    public DrawingData approxBoundingBox(final List<ElkNode> rectangles, double nodeNodeSpacing) {
        // place first box.
        ElkNode firstRect = rectangles.get(0);
        firstRect.setX(0);
        firstRect.setY(0);
        List<ElkNode> placedRects = new ArrayList<>();
        placedRects.add(firstRect);
        ElkNode lastPlaced = firstRect;
        DrawingData currentValues = new DrawingData(this.dar, firstRect.getWidth(), firstRect.getHeight(),
                DrawingDataDescriptor.WHOLE_DRAWING);

        // place the other boxes.
        for (int rectangleIdx = 1; rectangleIdx < rectangles.size(); rectangleIdx++) {
            ElkNode toPlace = rectangles.get(rectangleIdx);

            // determine drawing metrics for different candidate positions/placement options
            DrawingData opt1 = calcValuesForOpt(DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_RIGHT, toPlace,
                    lastPlaced, currentValues, placedRects, nodeNodeSpacing);

            DrawingData opt2 = calcValuesForOpt(DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_BELOW, toPlace,
                    lastPlaced, currentValues, placedRects, nodeNodeSpacing);

            DrawingData opt3 = calcValuesForOpt(DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_RIGHT, toPlace,
                    lastPlaced, currentValues, placedRects, nodeNodeSpacing);

            DrawingData opt4 = calcValuesForOpt(DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_BELOW, toPlace,
                    lastPlaced, currentValues, placedRects, nodeNodeSpacing);

            DrawingData bestOpt = findBestCandidate(opt1, opt2, opt3, opt4, toPlace, lastPlaced);

            toPlace.setX(bestOpt.getNextXcoordinate() + nodeNodeSpacing);
            toPlace.setY(bestOpt.getNextYcoordinate() + nodeNodeSpacing);
            bestOpt.setPlacementOption(DrawingDataDescriptor.WHOLE_DRAWING);
            currentValues = bestOpt;
            lastPlaced = toPlace;
            placedRects.add(toPlace);
        }

        return currentValues;
    }

    // HELPING METHODS

    /**
     * Determines according to the selected packing strategy, which option out of the given ones is the best.
     * 
     * @param opt1
     *            option lastPlaced right.
     * @param opt2
     *            option lastPlaced bottom.
     * @param opt3
     *            option complete drawing right.
     * @param opt4
     *            option complete drawing below.
     * @param toPlace
     *            rectangle to be placed on drawing area.
     * @param lastPlaced
     *            rectangle that was placed last on the drawing area.
     * @return returns the best option out of the given ones or null, if there is no best option found.
     */
    private DrawingData findBestCandidate(final DrawingData opt1, final DrawingData opt2, final DrawingData opt3,
            final DrawingData opt4, final ElkNode toPlace, final ElkNode lastPlaced) {
        List<DrawingData> candidates = Lists.newArrayList(opt1, opt2, opt3, opt4);
        List<BestCandidateFilter> filters = null;

        switch (packingStrat) {
        // sets the order of the filters according to the given strategy.
        case MAX_SCALE_DRIVEN:
            filters = Lists.newArrayList(new ScaleMeasureFilter(), new AreaFilter(), new AspectRatioFilter());
            break;
        case ASPECT_RATIO_DRIVEN:
            filters = Lists.newArrayList(new AspectRatioFilter(), new AreaFilter(), new ScaleMeasureFilter());
            break;
        case AREA_DRIVEN:
            filters = Lists.newArrayList(new AreaFilter(), new ScaleMeasureFilter(), new AspectRatioFilter());
            break;
        default:
        }

        // filter the candidates according to the order of the filters using the strategy pattern.
        for (BestCandidateFilter filter : filters) {
            if (candidates.size() > 1) {
                candidates = filter.filterList(candidates, dar);
            }
        }

        // only one candidate remains.
        if (candidates.size() == 1) {
            return candidates.get(candidates.size() - 1);
        }
        // Multiple options have the same value for every benchmark. These special cases are caught in the following.
        if (candidates.size() == 2) {
            return checkSpecialCases(candidates.get(0), candidates.get(1), lastPlaced, toPlace);
        }
        return null;
    }

    /**
     * Checks the two options and their possible combinations after being sorted out be the packing strategies.
     * Determines the best option depending on the options given.
     * 
     * @param drawing1
     *            a drawing to compare.
     * @param drawing2
     *            a drawing to compare.
     * @param lastPlaced
     *            the rectangle that was last placed before the two drawings were calculated.
     * @param toPlace
     *            the rectangle that was newly placed in the two drawings.
     * @return the better drawing out of the two.
     */
    private DrawingData checkSpecialCases(final DrawingData drawing1, final DrawingData drawing2,
            final ElkNode lastPlaced, final ElkNode toPlace) {
        DrawingDataDescriptor firstOpt = drawing1.getPlacementOption();
        DrawingDataDescriptor secondOpt = drawing2.getPlacementOption();

        boolean firstOptLPBorCDB = (firstOpt == DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_BELOW
                || firstOpt == DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_BELOW);
        boolean secondOptLPBorCDB = (secondOpt == DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_BELOW
                || secondOpt == DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_BELOW);

        boolean firstOptLPRorCDR = (firstOpt == DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_RIGHT
                || firstOpt == DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_RIGHT);
        boolean secondOptLPRorCDR = (secondOpt == DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_RIGHT
                || secondOpt == DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_RIGHT);

        boolean firstOptLPRorLPB = (firstOpt == DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_RIGHT
                || firstOpt == DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_BELOW);
        boolean secondOptLPRorLPB = (secondOpt == DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_RIGHT
                || secondOpt == DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_BELOW);

        if (firstOptLPBorCDB && secondOptLPBorCDB) {
            // If placing it LPB and WDB produces the same values. Take WDB.
            if (drawing1.getPlacementOption() == DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_BELOW) {
                return drawing1;
            } else {
                return drawing2;
            }
        } else if (firstOptLPRorCDR && secondOptLPRorCDR) {
            // If placing it LPR and WDR produces the same values. Take WDR.
            if (drawing1.getPlacementOption() == DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_RIGHT) {
                return drawing1;
            } else {
                return drawing2;
            }
        } else if (firstOptLPRorLPB && secondOptLPRorLPB) {
            // If LPR AND LPB produce the same values. Take the option producing less area with the last placed
            // rectangle and rectangle to place.
            DrawingData lprOpt;
            DrawingData lpbOpt;
            if (firstOpt == DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_RIGHT) {
                lprOpt = drawing1;
                lpbOpt = drawing2;
            } else {
                lprOpt = drawing2;
                lpbOpt = drawing1;
            }

            double areaLPR = Calculations.calculateAreaLPR(lastPlaced, toPlace, lprOpt);
            double areaLPB = Calculations.calculateAreaLPB(lastPlaced, toPlace, lpbOpt);

            if (areaLPR <= areaLPB) {
                if (drawing1.getPlacementOption() == DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_RIGHT) {
                    return drawing1;
                } else {
                    return drawing2;
                }
            } else {
                if (drawing1.getPlacementOption() == DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_BELOW) {
                    return drawing1;
                } else {
                    return drawing2;
                }
            }
        }

        return drawing1;
    }

    /**
     * Calculates drawing data for the given parameters including x and y coordinate for the rectangle toPlace.
     * 
     * @param option
     *            the placement option the values are calculated for.
     * @param toPlace
     *            the rectangle to be placed.
     * @param lastPlaced
     *            the rectangle that was placed last.
     * @param currDrawing
     *            current drawing containing width and height, besides others.
     * @param placedRects
     *            already placed rectangles.
     * @return an {@link DrawingData} object containing the values after the rectangle would be placed.
     */
    private DrawingData calcValuesForOpt(final DrawingDataDescriptor option, final ElkNode toPlace,
            final ElkNode lastPlaced, final DrawingData currDrawing, final List<ElkNode> placedRects, final double nodeNodeSpacing) {

        double potentialXvalue = nodeNodeSpacing;
        double potentialYvalue = nodeNodeSpacing;
        double currDrawingWidth = currDrawing.getDrawingWidth();
        double currDrawingHeight = currDrawing.getDrawingHeight();
        double heightToPlace = toPlace.getHeight();
        double widthToPlace = toPlace.getWidth();

        double width, height;
        switch (option) {
        case CANDIDATE_POSITION_LAST_PLACED_RIGHT:
            potentialXvalue = lastPlaced.getX() + lastPlaced.getWidth() + nodeNodeSpacing;
            if (lpShift) {
                potentialYvalue = Calculations.calculateYforLPR(potentialXvalue, placedRects, lastPlaced) + nodeNodeSpacing;
            } else {
                potentialYvalue = lastPlaced.getY() + nodeNodeSpacing;
            }

            width = Calculations.getWidthLPRorLPB(currDrawingWidth, potentialXvalue, widthToPlace);
            height = Calculations.getHeightLPRorLPB(currDrawingHeight, potentialYvalue, heightToPlace);
            break;
            
        case CANDIDATE_POSITION_LAST_PLACED_BELOW:
            potentialYvalue = lastPlaced.getY() + lastPlaced.getHeight() + nodeNodeSpacing;
            if (lpShift) {
                potentialXvalue = Calculations.calculateXforLPB(potentialYvalue, placedRects, lastPlaced) + nodeNodeSpacing;
            } else {
                potentialXvalue = lastPlaced.getX() + nodeNodeSpacing;
            }

            width = Calculations.getWidthLPRorLPB(currDrawingWidth, potentialXvalue, widthToPlace);
            height = Calculations.getHeightLPRorLPB(currDrawingHeight, potentialYvalue, heightToPlace);
            break;
            
        case CANDIDATE_POSITION_WHOLE_DRAWING_RIGHT:
            potentialXvalue = currDrawingWidth + nodeNodeSpacing;
            potentialYvalue = 0;

            width = currDrawingWidth + widthToPlace;
            height = Math.max(currDrawingHeight, heightToPlace);
            break;
            
        case CANDIDATE_POSITION_WHOLE_DRAWING_BELOW:
            potentialXvalue = 0;
            potentialYvalue = currDrawingHeight + nodeNodeSpacing;

            width = Math.max(currDrawingWidth, widthToPlace);
            height = currDrawingHeight + heightToPlace;
            break;
        default:
            throw new IllegalArgumentException("IllegalPlacementOption.");
        }

        // LPR and LPB have more values to be set in newDrawing.
        DrawingData newDrawing = new DrawingData(dar, width, height, option, potentialXvalue, potentialYvalue);
        return newDrawing;
    }
}
