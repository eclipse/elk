/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2010 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package org.eclipse.elk.alg.layered.p3order;

/**
 * The type of the crossing minimizer.
 * 
 * @author alan
 *
 */
public enum CrossMinType {
    /** Use BarycenterHeuristic. */
    BARYCENTER, /** Use one-sided GreedySwitchHeuristic. */
    GREEDY_SWITCH;

    /**
     * Determines whether the given heuristic is deterministic, if it is, it for example does
     * not need to use the thoroughness value.
     * 
     * @return whether or not the heuristic is deterministic.
     */
    public boolean isDeterministic() {
        return this == GREEDY_SWITCH;
    }
}