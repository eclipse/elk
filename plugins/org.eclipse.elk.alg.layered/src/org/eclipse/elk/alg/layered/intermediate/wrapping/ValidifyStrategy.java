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
 * The cuts calculated by a {@link ICutIndexCalculator} are not necessarily valid (when using
 * {@link WrappingStrategy#PATH_LIKE}). The specified 'validify' takes care of transforming forbidden cuts into valid
 * ones.
 */
public enum ValidifyStrategy {
    
    /** Do not touch my cuts!. */
    NO,
    /** Just increase forbidden cuts until they are valid. */
    GREEDY,
    /** Be a bit smarter and check if the lastly valid cut is closer than the next valid cut. */
    LOOK_BACK,
    
}
