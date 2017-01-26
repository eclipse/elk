/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.wrapping;

/**
 * Specifies the desired strategy to wrap graphs in order to improve their presentation within a drawing area of 
 * a certain aspect ratio. I.e. the graph is split into several chunks to better utilize the given area.
 */
public enum WrappingStrategy {

    /** Off. */
    OFF,
    /**
     * Path-like graphs are easy. The allow to forbid certain indexes, and to have a single edge wrapping backwards at
     * each wrapping point.
     */
    PATH_LIKE,
    /**
     * For general graphs it is allowed that multiple edges wrap backwards at a wrapping point. An additional 
     * objective is thus to keep the number of edges wrapping backwards small.
     */
    GENERAL,
    
}
