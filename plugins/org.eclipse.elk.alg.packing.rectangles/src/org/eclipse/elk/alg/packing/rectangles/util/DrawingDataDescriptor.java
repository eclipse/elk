/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.packing.rectangles.util;

/**
 * Enumerate that specifies if a {@link DrawingData} object represents the values of a candidate position or a set
 * drawing of a rectangle.
 */
public enum DrawingDataDescriptor {
    /** Placing rectangle to the right of the most recently placed rectangle. */
    CANDIDATE_POSITION_LAST_PLACED_RIGHT,
    /** Placing rectangle to below the most recently placed rectangle. */
    CANDIDATE_POSITION_LAST_PLACED_BELOW,
    /** Placing rectangle to the right of the whole drawing. */
    CANDIDATE_POSITION_WHOLE_DRAWING_RIGHT,
    /** Placing rectangle to below of the whole drawing. */
    CANDIDATE_POSITION_WHOLE_DRAWING_BELOW,
    /** Indicating that a {@link DrawingData} object refers to a actual drawing and not a placement option. */
    WHOLE_DRAWING
}
