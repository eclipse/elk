/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

/**
 * The type of the crossing minimizer.
 *
 * @author alan
 *
 */
public enum CrossMinType {
    /** Use BarycenterHeuristic. */
    BARYCENTER,
    /** Use one-sided GreedySwitchHeuristic. */
    ONE_SIDED_GREEDY_SWITCH,
    /** Use two-sided GreedySwitchHeuristic. */
    TWO_SIDED_GREEDY_SWITCH;

    /**
     * Determines whether the given heuristic is deterministic, if it is, it for example does not need to use the
     * thoroughness value.
     *
     * @return whether or not the heuristic is deterministic.
     */
    public boolean isDeterministic() {
        return this == ONE_SIDED_GREEDY_SWITCH || this == TWO_SIDED_GREEDY_SWITCH;
    }

    public boolean alwaysImproves() {
        return this == TWO_SIDED_GREEDY_SWITCH;
    }
}