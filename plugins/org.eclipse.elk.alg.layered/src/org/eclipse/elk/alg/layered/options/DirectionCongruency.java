/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.options;

/**
 * Enumeration that allows to specify how drawings of the same graph with different layout directions compare to each
 * other: either a natural reading direction is preserved or the drawings are rotated versions of each other.
 */
public enum DirectionCongruency {

    /**
     * The vertical reading direction of a left-to-right or right-to-left layout (and thus the node, edge, and edge
     * label order) corresponds to the horizontal reading direction of a top-to-bottom or bottom-to-top layout.
     */
    READING_DIRECTION,

    /**
     * The four possible drawings are simply rotated versions of the left-to-right variant. This might be of interest if
     * one wants to preserve a _clockwise orientation_ of feedback loops.
     */
    ROTATION,
}
