/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.disco.options;

import org.eclipse.elk.alg.common.compaction.polyomino.PolyominoCompactor;

/**
 * <p>
 * Possible strategies for packing different connected components on a canvas in order to save space and enhance
 * readability of a graph.
 * </p>
 * 
 * <p>
 * POLYOMINO - First naive implementation of Freivalds et al. (2002) realized in {@link PolyominoCompactor}
 * </p>
 */
public enum CompactionStrategy {
    /**
     * There is only one strategy implemented, yet.
     */
    POLYOMINO
}
