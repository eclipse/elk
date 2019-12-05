/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.options;

/**
 * Determines how self loops will be placed in certain {@link SelfLoopDistributionStrategy distribution strategies}.
 */
public enum SelfLoopOrderingStrategy {

    /** Self loops will be stacked or nested high. */
    STACKED,
    /** Self loops will be placed next to each other. */
    SEQUENCED;
    
}
