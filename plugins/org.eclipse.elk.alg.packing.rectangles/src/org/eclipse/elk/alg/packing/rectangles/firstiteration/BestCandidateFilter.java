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

/**
 * Interface implementing the Strategy interface of the strategy pattern. This interface offers a method that filters a
 * given list of {@link DrawingData} objects and returns a filtered list.
 */
public interface BestCandidateFilter {

    /**
     * Filters the given list of {@link DrawingData} objects and returns it.
     * 
     * @param candidates The list to be filtered.
     * @param aspectRatio The desired aspect ratio.
     * @return A list that is filtered by whatever the implementation filtered for.
     */
    List<DrawingData> filterList(List<DrawingData> candidates, double aspectRatio);
}
