/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.rectpacking;

/**
 * This algorithm's layout phases. each phase has its own strategy factory.
 */
public enum RectPackingLayoutPhases {
    
    /** Phase 1. */
    P1_WIDTH_APPROXIMATION,
    /** Phase 2. */
    P2_PACKING,
    /** Phase 3. */
    P4_WHITESPACE_ELIMINATION;
    
}
