/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.disco.graph;

/**
 * Represents the four cardinal directions.
 */
public enum DCDirection {
    /**
     * The four cardinal directions.
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
     * Returns whether the given direction is horizontal.
     * 
     * @return true, if {@code EAST} or {@code WEST}; false, otherwise.
     */
    public boolean isHorizontal() {
        return horizontal;
    }
}
