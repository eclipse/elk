/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.common.compaction.options;

/**
 * Possible traversal orders (with an implicit cost function) for placing polyominoes on an infinite square planar grid.
 */
public enum CostStrategy {
    // TODO finalize choices and comment on each one
    SPIRAL,
    LINE_BY_LINE,
    MANHATTAN,
    JITTER,
    QUADRANTS_LINE_BY_LINE,
    QUADRANTS_MANHATTAN,
    QUADRANTS_JITTER,
    COMBINE_LINE_BY_LINE_MANHATTAN,
    COMBINE_JITTER_MANHATTAN
}
