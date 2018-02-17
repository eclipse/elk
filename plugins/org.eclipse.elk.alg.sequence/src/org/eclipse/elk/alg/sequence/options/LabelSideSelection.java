/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.options;

/**
 * Possible ways to determine whether edge labels should be placed above or below their edge.
 */
public enum LabelSideSelection {
    
    /** Labels are always placed above their edge. */
    ALWAYS_UP,
    /** Labels are always placed below their edge. */
    ALWAYS_DOWN,
    /** Labels are always placed above their edge if the edge points rightwards, or below if it doesn't. */
    DIRECTION_UP,
    /** Labels are always placed below their edge if the edge points leftwards, or above if it doesn't. */
    DIRECTION_DOWN;

}
