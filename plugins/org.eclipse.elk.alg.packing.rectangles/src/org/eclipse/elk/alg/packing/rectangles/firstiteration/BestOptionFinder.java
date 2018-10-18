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

/**
 * Compares placement options, i.e. different drawings and offers methods to find the best options out of the given
 * ones.
 * 
 * @author dalu
 */
public final class BestOptionFinder {

    private BestOptionFinder() {
    }

    /**
     * Filters the given list for the element with the aspect ratio that is closest to the given desired aspect ratio.
     * Returns a list with the elements that have smallest deviation to the desired aspect ratio as multiple options may
     * have the same aspect ratio.
     * 
     * @param candidates
     *            list of still considerable options.
     * @param dar
     *            desired aspect ratio.
     * @return list of elements with aspect ratio being as close to the desired aspect ratio as possible.
     */
    protected static List<DrawingData> findBestAspectRatio(final List<DrawingData> candidates, final double dar) {
        List<DrawingData> remainingCandidates = new ArrayList<DrawingData>();
        double smallestDeviation = Double.MAX_VALUE;
        for (DrawingData opt : candidates) {
            smallestDeviation = Math.min(smallestDeviation, Math.abs(opt.getAspectRatio() - dar));
        }
        for (DrawingData candidate : candidates) {
            if (Math.abs(candidate.getAspectRatio() - dar) == smallestDeviation) {
                remainingCandidates.add(candidate);
            }
        }
        return remainingCandidates;
    }

    /**
     * Filters the given list for the element with the minimal area. Returns a list with the elements that have the
     * smallest area as multiple options may have the same area.
     * 
     * @param candidates
     *            list of still considerable options.
     * @return list of elements with the smallest area.
     */
    protected static List<DrawingData> findMinArea(final List<DrawingData> candidates) {
        List<DrawingData> remainingCandidates = new ArrayList<DrawingData>();
        double minArea = Double.MAX_VALUE;
        for (DrawingData opt : candidates) {
            minArea = Math.min(minArea, opt.getArea());
        }
        for (DrawingData candidate : candidates) {
            if (candidate.getArea() == minArea) {
                remainingCandidates.add(candidate);
            }
        }
        return remainingCandidates;
    }

    /**
     * Filters the given list for the element with the maximum scale measure. Returns a list with the elements that have
     * the biggest scale measure as multiple options may have the same scale measure.
     * 
     * @param candidates
     *            list of still considerable options.
     * @return list of elements with the highest scale measure.
     */
    protected static List<DrawingData> findMaxScale(final List<DrawingData> candidates) {
        List<DrawingData> remainingCandidates = new ArrayList<DrawingData>();
        double maxScale = Double.MIN_VALUE;
        for (DrawingData opt : candidates) {
            maxScale = Math.max(maxScale, opt.getScaleMeasure());
        }
        for (DrawingData candidate : candidates) {
            if (candidate.getScaleMeasure() == maxScale) {
                remainingCandidates.add(candidate);
            }
        }
        return remainingCandidates;
    }
}
