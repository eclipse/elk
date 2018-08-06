/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.labeling;

import org.eclipse.elk.core.options.PortSide;

/**
 * This class holds the penalties for label position evaluation.
 */
public final class SelfLoopLabelPenalties {


    /** Penalty value for the north side. */
    public static final double NORTH = 0;
    /** Penalty value for the south side. */
    public static final double SOUTH = 0.01;
    /** Penalty value for the east side. */
    public static final double EAST = 0.02;
    /** Penalty value for the west side. */
    public static final double WEST = 0.03;

    /** Penalty value for the centered label positions. */
    public static final double CENTERED = 0;
    /** Penalty value for the left or top label positions. */
    public static final double LEFT_TOP_ALIGNED = 0.1;
    /** Penalty value for the right or bottom label positions. */
    public static final double RIGHT_BOTTOM_ALIGNED = 0.2;
    /** Penalty value for short segments. */
    public static final double SHORT_SEGMENT = 0.3;

    
    /**
     * No instantiation.
     */
    private SelfLoopLabelPenalties() {
    }
    
    
    /**
     * Convenience method to find the penalty for a given side.
     */
    public static double getSidePenalty(final PortSide side) {
        switch (side) {
        case NORTH:
            return NORTH;
        case SOUTH:
            return SOUTH;
        case EAST:
            return EAST;
        case WEST:
            return WEST;
        }
        return 0;
    }

}
