/*******************************************************************************
 * Copyright (c) 2012, 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.options;

/**
 * Layout option for the choice of candidates in the Brandes & Köpf node placement.
 *
 * @author jjc
 */
public enum FixedAlignment {
    
    /** Chooses the smallest layout from the four possible candidates. */
    NONE,
    /** Chooses the left-up candidate from the four possible candidates. */
    LEFTUP,
    /** Chooses the right-up candidate from the four possible candidates. */
    RIGHTUP,
    /** Chooses the left-down candidate from the four possible candidates. */
    LEFTDOWN,
    /** Chooses the right-down candidate from the four possible candidates. */
    RIGHTDOWN,
    /** Creates a balanced layout from the four possible candidates. */
    BALANCED;

}
