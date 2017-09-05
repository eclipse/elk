/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.common.compaction.polyomino.structures;

/**
 * Represents the four cardinal directions.
 */
public enum Direction {
    /**
     * Here they are, the four cardinal directions.
     */
    NORTH, EAST, SOUTH, WEST;

    private boolean horizontal;

    static {
        NORTH.horizontal = false;
        EAST.horizontal = true;
        SOUTH.horizontal = false;
        WEST.horizontal = true;
    }

    /**
     * Returns whether the direction is horizontal or not. {@code EAST} and {@code WEST} are considered horizontal,
     * {@code NORTH} and {@code SOUTH} vertical.
     * 
     * @return true, if direction is horizontal, false, otherwise.
     */
    public boolean isHorizontal() {
        return horizontal;
    }
}
