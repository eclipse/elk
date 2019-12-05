/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.options;

/**
 * Sets the variant of the greedy switch heuristic.
 * 
 * @author alan
 */
public enum GreedySwitchType {
    /** Only consider crossings to one side of the free layer. Calculate crossing matrix on demand. */
    ONE_SIDED,
    /** Consider crossings to both sides of the free layer. Calculate crossing matrix on demand. */
    TWO_SIDED,
    /** Do not use greedy switch. */
    OFF;
}

