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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.packing.rectangles.util.DrawingData;

/**
 * This class implements a concrete strategy in the strategy pattern given by {@link BestCandidateFilter}.
 * <p>
 * This class offers a method that filters the given list regarding the scale measure. The candidate or candidates with
 * equal scale measure with the biggest scale measure are returned in the filtered list.
 * </p>
 */
public class ScaleMeasureFilter implements BestCandidateFilter {

    @Override
    public List<DrawingData> filterList(final List<DrawingData> candidates, final double aspectRatio) {
        List<DrawingData> remainingCandidates = new ArrayList<DrawingData>();
        double maxScale = Double.NEGATIVE_INFINITY;
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
