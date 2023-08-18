/*******************************************************************************
 * Copyright (c) 2011, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.yconstree;
/**
 * This algorithm's layout phases. each phase has its own strategy factory.
 */
public enum YconstreeLayoutPhases {
    
    /** Phase 1. */
    //P1_TREECHECKING,
    /** Phase 2. */
    P2_NODE_Y_PLACEMENT,
    /** Phase 2. */
    P3_NODE_RELATIV_PLACEMENT,
    /** Phase 2. */
    P4_NODE_ABSOLUTE_PLACEMENT;
    
}