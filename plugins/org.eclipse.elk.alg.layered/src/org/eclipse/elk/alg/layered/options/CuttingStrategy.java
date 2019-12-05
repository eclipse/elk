/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.options;

/**
 * Specifies the strategy employed to calculate cut indexes during graph wrapping.
 * 
 * @see WrappingStrategy
 */
public enum CuttingStrategy {

    /** Aspect ratio-driven cut calculation heuristic. */
    ARD,
    /** Max scale-driven cut calculation heuristic. */
    MSD,
    /** Cuts are manually specified by a user via {@link LayeredOptions#WRAPPING_CUTTING_CUTS}. */
    MANUAL,
    
}
