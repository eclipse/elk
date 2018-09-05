/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
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
