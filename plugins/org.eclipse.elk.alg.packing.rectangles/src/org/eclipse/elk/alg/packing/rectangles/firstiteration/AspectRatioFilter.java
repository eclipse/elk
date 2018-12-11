/*******************************************************************************
 * Copyright (c) 2018 stu124145 and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.packing.rectangles.firstiteration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.packing.rectangles.util.DrawingData;

/**
 * This class implements a concrete strategy in the strategy pattern given by {@link BestCandidateFilter}.
 * <p>
 * This class offers a method that filters the given list regarding the aspect ratio. The candidate or candidates with
 * equal aspect ratio that are closest to the desired aspect ratio are returned in the filtered list.
 * </p>
 */
public class AspectRatioFilter implements BestCandidateFilter {

    @Override
    public List<DrawingData> filterList(final List<DrawingData> candidates, final double dar) {
        List<DrawingData> remainingCandidates = new ArrayList<DrawingData>();
        double smallestDeviation = Double.POSITIVE_INFINITY;
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
}
