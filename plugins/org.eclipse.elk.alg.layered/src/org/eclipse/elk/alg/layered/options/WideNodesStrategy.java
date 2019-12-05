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
 * Strategy to distribute wide nodes over multiple layers.
 * 
 * @author uru
 */
public enum WideNodesStrategy {

    /** Do not handle wide nodes specially. */
    OFF,
    /**
     * Splits wide nodes prior to the crossing minimization phase. Note that this can lead to
     * edge-node crossings.
     */
    AGGRESSIVE,
    /**
     * Splits wide nodes after the crossing minimization phase, guaranteeing that no edge-node
     * crossings are introduced.
     */
    CAREFUL,

}
