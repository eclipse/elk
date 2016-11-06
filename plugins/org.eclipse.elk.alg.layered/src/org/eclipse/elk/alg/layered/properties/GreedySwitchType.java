/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.properties;

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

