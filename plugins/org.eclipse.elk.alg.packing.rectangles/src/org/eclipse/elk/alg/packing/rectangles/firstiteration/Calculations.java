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

import java.util.List;

import org.eclipse.elk.alg.packing.rectangles.util.DrawingData;
import org.eclipse.elk.alg.packing.rectangles.util.DrawingDataDescriptor;
import org.eclipse.elk.graph.ElkNode;

/**
 * Class that offers methods for small operations in the first iteration.
 * 
 * @see AreaApproximation
 */
public final class Calculations {

    private Calculations() {
    }

    /**
     * Calculates the drawing width for placement options LPR and LPB.
     * 
     * @param drawingWidth
     *            width of drawing before placement of new rectangle.
     * @param xCoord
     *            potential x-coordinate of new rectangle.
     * @param toPlaceWidth
     *            width of new rectangle.
     * @return width of drawing including new rectangle in case LPR and LPB.
     */
    protected static double getWidthLPRorLPB(final double drawingWidth, final double xCoord,
            final double toPlaceWidth) {
        return Math.max(drawingWidth, xCoord + toPlaceWidth);
    }

    /**
     * Calculates the drawing height for placement options LPR and LPB.
     * 
     * @param drawingHeight
     *            height of drawing before placement of new rectangle.
     * @param yCoord
     *            potential y-coordinate of new rectangle.
     * @param toPlaceHeight
     *            height of new rectangle.
     * @return height of drawing including new rectangle in case LPR and LPB.
     */
    protected static double getHeightLPRorLPB(final double drawingHeight, final double yCoord,
            final double toPlaceHeight) {
        return Math.max(drawingHeight, yCoord + toPlaceHeight);
    }

    /**
     * Calculates the y-coordinate after the shift of the rectangle to be placed right of lastPlaced.
     * 
     * @param xCoordRectToPlace
     *            x-value of the rectangle to be placed.
     * @param placedRects
     *            a list of already placed rectangles.
     * @param lastPlaced
     *            rectangle that was placed most recently.
     * @return y-coordinate after the shift of the rectangle to be placed right of lastPlaced
     */
    protected static double calculateYforLPR(final double xCoordRectToPlace, final List<ElkNode> placedRects,
            final ElkNode lastPlaced) {
        ElkNode closestUpperNeighbor = null;
        double closestNeighborBottomBorder = 0;
        // find neighbors that lay between the upper and lower border of the rectangle to be placed.
        for (ElkNode placedRect : placedRects) {
            double placedRectBottomBorder = placedRect.getY() + placedRect.getHeight();
            if (verticalOrderConstraint(placedRect, xCoordRectToPlace)) {
                // is closest neighbor?
                if (closestUpperNeighbor == null) {
                    closestUpperNeighbor = placedRect;
                } else if (lastPlaced.getY() - placedRectBottomBorder < lastPlaced.getY() - closestNeighborBottomBorder) {
                    closestUpperNeighbor = placedRect;
                }
                closestNeighborBottomBorder = closestUpperNeighbor.getY() + closestUpperNeighbor.getHeight();
            }
        }

        // no neighbor yet
        if (closestUpperNeighbor == null) {
            return 0;
        } else {
            // else, choose closest neighbors bottom border
            return closestNeighborBottomBorder;
        }
    }

    /**
     * Calculates the x-coordinate after shift of the rectangle to be placed below lastPlaced.
     * 
     * @param placedRects
     *            a list of already placed rectangles.
     * @param yCoordRectToPlace
     *            y-value of the rectangle to be placed.
     * @param lastPlaced
     *            most recently placed rectangle.
     * @return x-coordinate after shift of the rectangle to be placed below lastPlaced.
     */
    protected static double calculateXforLPB(final double yCoordRectToPlace, final List<ElkNode> placedRects,
            final ElkNode lastPlaced) {
        ElkNode closestLeftNeighbour = null;
        double closestNeighborRightBorder = 0;
        // find neighbors that lay in between the height of the rectangle to be placed.
        for (ElkNode placedRect : placedRects) {
            double placedRectRightBorder = placedRect.getX() + placedRect.getWidth();
            if (horizontalOrderConstraint(placedRect, yCoordRectToPlace)) {
                // is closest neighbor?
                if (closestLeftNeighbour == null) {
                    closestLeftNeighbour = placedRect;
                } else if (lastPlaced.getX() - placedRectRightBorder < lastPlaced.getX() - closestNeighborRightBorder) {
                    closestLeftNeighbour = placedRect;
                }
                closestNeighborRightBorder = closestLeftNeighbour.getX() + closestLeftNeighbour.getWidth();
            }
        }

        // no neighbor yet
        if (closestLeftNeighbour == null) {
            return 0;
        } else {
            // else, choose closest neighbors right border
            return closestNeighborRightBorder;
        }
    }

    /**
     * Calculates the area taken up by the last placed rectangle and the rectangle to place for the
     * {@link DrawingDataDescriptor} last placed right.
     * 
     * @param lastPlaced
     *            the last placed rectangle.
     * @param toPlace
     *            rectangle to be placed.
     * @param lprOpt
     *            drawing data containing the coordinates of the rectangle to be placed.
     * @return area taken up by the two given rectangles.
     */
    protected static double calculateAreaLPR(final ElkNode lastPlaced, final ElkNode toPlace,
            final DrawingData lprOpt) {
        double lastPlacedBottomBorder = lastPlaced.getY() + lastPlaced.getHeight();
        double toPlaceBottomBorder = lprOpt.getNextYcoordinate() + toPlace.getHeight();
        double maxYLPR = Math.max(lastPlacedBottomBorder, toPlaceBottomBorder);

        double heightLPR = maxYLPR - Math.min(lastPlaced.getY(), lprOpt.getNextYcoordinate());
        double widthLPR = lprOpt.getNextXcoordinate() + toPlace.getWidth() - lastPlaced.getX();

        return widthLPR * heightLPR;
    }

    /**
     * Calculates the area taken up by the last placed rectangle and the rectangle to place for the
     * {@link DrawingDataDescriptor} last placed below.
     * 
     * @param lastPlaced
     *            the last placed rectangle.
     * @param toPlace
     *            rectangle to be placed.
     * @param lpbOpt
     *            drawing data containing the coordinates of the rectangle to be placed.
     * @return area taken up by the two given rectangles.
     */
    protected static double calculateAreaLPB(final ElkNode lastPlaced, final ElkNode toPlace,
            final DrawingData lpbOpt) {
        double lastPlacedRightBorder = lastPlaced.getX() + lastPlaced.getWidth();
        double toPlaceRightBorder = lpbOpt.getNextXcoordinate() + toPlace.getWidth();
        double maxXLPB = Math.max(lastPlacedRightBorder, toPlaceRightBorder);

        double widthLPB = maxXLPB - Math.min(lastPlaced.getX(), lpbOpt.getNextXcoordinate());
        double heightLPB = lpbOpt.getNextYcoordinate() + toPlace.getHeight() - lastPlaced.getY();

        return widthLPB * heightLPB;
    }

    /**
     * Checks whether the placedRect produces an vertical order constraint regarding the order. If toPlace is placed
     * left of placedRect, toPlace can at most be placed at the bottom border of placedRect.
     * 
     * @param placedRect
     *            the already placed rectangle.
     * @param xCoordRectToPlace
     *            x-value of the rectangle to be placed.
     * @return true, if the placedRect produces a constraint. False otherwise.
     */
    private static boolean verticalOrderConstraint(final ElkNode placedRect, final double xCoordRectToPlace) {
        return xCoordRectToPlace < placedRect.getX() + placedRect.getWidth();
    }

    /**
     * Checks whether the placedRect produces an horizontal order constraint regarding the order. If toPlace is placed
     * above of placedRect, toPlace can at most be placed at the right border of placedRect.
     * 
     * @param placedRect
     *            the already placed rectangle.
     * @param yCoordRectToPlace
     *            y-value of the rectangle to be placed.
     * @return True, if the placedRect produces a constraint. False otherwise.
     */
    private static boolean horizontalOrderConstraint(final ElkNode placedRect, final double yCoordRectToPlace) {
        return yCoordRectToPlace < placedRect.getY() + placedRect.getHeight();
    }
}
