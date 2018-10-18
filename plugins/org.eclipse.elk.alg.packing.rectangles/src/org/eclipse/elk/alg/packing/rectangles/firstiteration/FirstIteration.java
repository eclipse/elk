/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.packing.rectangles.firstiteration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.packing.rectangles.util.DrawingData;
import org.eclipse.elk.alg.packing.rectangles.util.PackingStrategy;
import org.eclipse.elk.alg.packing.rectangles.util.DrawingDataDescriptor;
import org.eclipse.elk.graph.ElkNode;

/**
 * Class that handles the first iteration of the algorithm, producing an approximated bounding box for the packing.
 * 
 * @author dalu
 */
public class FirstIteration {
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
    public FirstIteration(final double desiredAr, final PackingStrategy packStrat, final boolean lpShifting) {
        this.dar = desiredAr;
        this.packingStrat = packStrat;
        this.lpShift = lpShifting;
    }

    /**
     * Calculates a drawing for the given rectangles according options set in the object creation of
     * {@link FirstIteration}. This method also sets the coordinates for the rectangles.
     * 
     * @param rectangles
     *            the rectangles to place.
     * @return a drawing calculated by this methods algorithm.
     */
    public DrawingData approxBoundingBox(final List<ElkNode> rectangles) {
        // init
        List<ElkNode> placedRects = new ArrayList<>();
        ElkNode lastPlaced = null;
        ElkNode toPlace = null;
        DrawingData opt1, opt2, opt3, opt4, currentValues, bestOpt;

        // place first box.
        ElkNode firstRect = rectangles.get(0);
        firstRect.setX(0);
        firstRect.setY(0);
        placedRects.add(firstRect);
        lastPlaced = firstRect;
        currentValues = new DrawingData(this.dar, firstRect.getWidth(), firstRect.getHeight(),
                DrawingDataDescriptor.WHOLE_DRAWING);

        // place the other boxes.
        for (int rectangleIdx = 1; rectangleIdx < rectangles.size(); rectangleIdx++) {
            toPlace = rectangles.get(rectangleIdx);

            // determine drawing metrics for different candiate positions/placement options
            DrawingDataDescriptor lpRight = DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_RIGHT;
            opt1 = calcValuesForOpt(lpRight, toPlace, lastPlaced, currentValues, placedRects);

            DrawingDataDescriptor lpBelow = DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_BELOW;
            opt2 = calcValuesForOpt(lpBelow, toPlace, lastPlaced, currentValues, placedRects);

            DrawingDataDescriptor wdRight = DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_RIGHT;
            opt3 = calcValuesForOpt(wdRight, toPlace, lastPlaced, currentValues, placedRects);

            DrawingDataDescriptor wdBelow = DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_BELOW;
            opt4 = calcValuesForOpt(wdBelow, toPlace, lastPlaced, currentValues, placedRects);

            bestOpt = findBestCandidate(opt1, opt2, opt3, opt4, toPlace, lastPlaced);

            toPlace.setY(bestOpt.getNextYcoordinate());
            toPlace.setX(bestOpt.getNextXcoordinate());
            bestOpt.setPlaOpt(DrawingDataDescriptor.WHOLE_DRAWING);
            currentValues = bestOpt;
            lastPlaced = toPlace;
            placedRects.add(rectangleIdx, toPlace);
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
        List<DrawingData> candidates = new ArrayList<>();
        candidates.add(opt1);
        candidates.add(opt2);
        candidates.add(opt3);
        candidates.add(opt4);

        switch (packingStrat) {
        // examines the candidates according to the packing strategy.
        case MAX_SCALE_DRIVEN:
            candidates = BestOptionFinder.findMaxScale(candidates);
            if (candidates.size() > 1) {
                candidates = BestOptionFinder.findMinArea(candidates);
                if (candidates.size() > 1) {
                    candidates = BestOptionFinder.findBestAspectRatio(candidates, dar);
                }
            }
            break;
        case ASPECT_RATIO_DRIVEN:
            candidates = BestOptionFinder.findBestAspectRatio(candidates, dar);
            if (candidates.size() > 1) {
                candidates = BestOptionFinder.findMinArea(candidates);
                if (candidates.size() > 1) {
                    candidates = BestOptionFinder.findMaxScale(candidates);
                }
            }

            break;
        case AREA_DRIVEN:
            candidates = BestOptionFinder.findMinArea(candidates);
            if (candidates.size() > 1) {
                candidates = BestOptionFinder.findMaxScale(candidates);
                if (candidates.size() > 1) {
                    candidates = BestOptionFinder.findBestAspectRatio(candidates, dar);
                }
            }

            break;
        default:
            break;
        }
        // only one candidate remains
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
        DrawingDataDescriptor firstOpt = drawing1.getPlaOpt();
        DrawingDataDescriptor secondOpt = drawing2.getPlaOpt();

        DrawingDataDescriptor lpRight = DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_RIGHT;
        DrawingDataDescriptor lpBelow = DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_BELOW;
        DrawingDataDescriptor wdRight = DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_RIGHT;
        DrawingDataDescriptor wdBelow = DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_BELOW;

        boolean firstOptLPBorCDB = (firstOpt == lpBelow || firstOpt == wdBelow);
        boolean secondOptLPBorCDB = (secondOpt == lpBelow || secondOpt == wdBelow);

        boolean firstOptLPRorCDR = (firstOpt == lpRight || firstOpt == wdRight);
        boolean secondOptLPRorCDR = (secondOpt == lpRight || secondOpt == wdRight);

        boolean firstOptLPRorLPB = (firstOpt == lpRight || firstOpt == lpBelow);
        boolean secondOptLPRorLPB = (secondOpt == lpRight || secondOpt == lpBelow);

        if (firstOptLPBorCDB && secondOptLPBorCDB) {
            // If placing it LPB and WDB produces the same values. Take WDB.
            if (drawing1.getPlaOpt() == wdBelow) {
                return drawing1;
            } else {
                return drawing2;
            }
        } else if (firstOptLPRorCDR && secondOptLPRorCDR) {
            // If placing it LPR and WDR produces the same values. Take WDR.
            if (drawing1.getPlaOpt() == wdRight) {
                return drawing1;
            } else {
                return drawing2;
            }
        } else if (firstOptLPRorLPB && secondOptLPRorLPB) {
            // If LPR AND LPB produce the same values. Take the option producing less area with the last placed
            // rectangle and rectangle to place.
            DrawingData lprOpt;
            DrawingData lpbOpt;
            if (firstOpt == lpRight) {
                lprOpt = drawing1;
                lpbOpt = drawing2;
            } else {
                lprOpt = drawing2;
                lpbOpt = drawing1;
            }

            double areaLPR = Calculations.calculateAreaLPR(lastPlaced, toPlace, lprOpt);
            double areaLPB = Calculations.calculateAreaLPB(lastPlaced, toPlace, lpbOpt);

            if (areaLPR <= areaLPB) {
                if (drawing1.getPlaOpt() == lpRight) {
                    return drawing1;
                } else {
                    return drawing2;
                }
            } else {
                if (drawing1.getPlaOpt() == lpBelow) {
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
            final ElkNode lastPlaced, final DrawingData currDrawing, final List<ElkNode> placedRects) {
        // init variables
        DrawingData newDrawing;
        double width, height;
        double potentialXvalue = 0;
        double potentialYvalue = 0;
        DrawingDataDescriptor placementOpt;

        double currDrawingWidth = currDrawing.getDrawingWidth();
        double currDrawingHeight = currDrawing.getDrawingHeight();
        double heightToPlace = toPlace.getHeight();
        double widthToPlace = toPlace.getWidth();

        switch (option) {
        case CANDIDATE_POSITION_LAST_PLACED_RIGHT:

            potentialXvalue = lastPlaced.getX() + lastPlaced.getWidth();
            if (lpShift) {
                potentialYvalue = Calculations.calculateYforLPR(potentialXvalue, placedRects, lastPlaced);
            } else {
                potentialYvalue = lastPlaced.getY();
            }

            width = Calculations.getWidthLPRorLPB(currDrawingWidth, potentialXvalue, widthToPlace);
            height = Calculations.getHeightLPRorLPB(currDrawingHeight, potentialYvalue, heightToPlace);
            placementOpt = DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_RIGHT;
            break;
        case CANDIDATE_POSITION_LAST_PLACED_BELOW:

            potentialYvalue = lastPlaced.getY() + lastPlaced.getHeight();
            if (lpShift) {
                potentialXvalue = Calculations.calculateXforLPB(potentialYvalue, placedRects, lastPlaced);
            } else {
                potentialXvalue = lastPlaced.getX();
            }

            width = Calculations.getWidthLPRorLPB(currDrawingWidth, potentialXvalue, widthToPlace);
            height = Calculations.getHeightLPRorLPB(currDrawingHeight, potentialYvalue, heightToPlace);
            placementOpt = DrawingDataDescriptor.CANDIDATE_POSITION_LAST_PLACED_BELOW;
            break;
        case CANDIDATE_POSITION_WHOLE_DRAWING_RIGHT:

            potentialXvalue = currDrawingWidth;
            potentialYvalue = 0;

            width = currDrawingWidth + widthToPlace;
            height = Math.max(currDrawingHeight, heightToPlace);
            placementOpt = DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_RIGHT;
            break;
        case CANDIDATE_POSITION_WHOLE_DRAWING_BELOW:

            potentialXvalue = 0;
            potentialYvalue = currDrawingHeight;

            width = Math.max(currDrawingWidth, widthToPlace);
            height = currDrawingHeight + heightToPlace;
            placementOpt = DrawingDataDescriptor.CANDIDATE_POSITION_WHOLE_DRAWING_BELOW;
            break;
        default:
            throw new IllegalArgumentException("IllegalPlacementOption.");
        }

        // LPR and LPB have more values to be set in newDrawing.
        newDrawing = new DrawingData(dar, width, height, placementOpt, potentialXvalue, potentialYvalue);
        return newDrawing;
    }
}
