/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  
 *******************************************************************************/
package org.eclipse.elk.alg.topdownpacking;

/**
 * Topdown packing phases.
 *
 */
public enum TopdownPackingPhases {

    /**
     * Place equally sized nodes where the size is defined by the parent.
     */
    P1_NODE_PLACEMENT,
    
    /**
     * Eliminate white space by increasing the size of some nodes to fully fill the space provided by the parent. 
     */
    P2_WHITESPACE_ELIMINATION
    
}
