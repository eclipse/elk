/*******************************************************************************
 * Copyright (c) 2018 stu124145 and others.
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
 * This class offers a method that filters the given list regarding the area. The candidate or candidates with equal
 * area with the smallest area are returned in the filtered list.
 * </p>
 */
public class AreaFilter implements BestCandidateFilter {

    @Override
    public List<DrawingData> filterList(final List<DrawingData> candidates, final double dar) {
        List<DrawingData> remainingCandidates = new ArrayList<DrawingData>();
        double minArea = Double.POSITIVE_INFINITY;
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
}
