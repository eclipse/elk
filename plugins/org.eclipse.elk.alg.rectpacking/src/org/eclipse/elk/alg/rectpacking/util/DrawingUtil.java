/*******************************************************************************
 * Copyright (c) 2018, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.rectpacking.util;

import java.util.List;

import org.eclipse.elk.graph.ElkNode;

/**
 * Class that offers different methods related to coordinates.
 */
public final class DrawingUtil {

    // private constructor
    private DrawingUtil() {
    }

    /**
     * Computes the altered scale measure for a given height and width according to the desired aspect ratio.
     * 
     * @param width The given width.
     * @param height The given height.
     * @param dar The desired aspect ratio.
     * @return altered scale measure calculated the given parameters.
     */
    public static double computeScaleMeasure(final double width, final double height, final double dar) {
        return Math.min(1d / width, (1d / dar) / height);
    }

    /**
     * Resets coordinates of every element to (0,0).
     * 
     * @param graph
     *            given graph to reset the coordinates from.
     */
    public static void resetCoordinates(final List<ElkNode> graph) {
        for (ElkNode node : graph) {
            node.setLocation(0, 0);
        }
    }
}
