/*******************************************************************************
 * Copyright (c) 2010, 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.options;

import org.eclipse.elk.alg.layered.LayeredPhases;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.p1cycles.DepthFirstCycleBreaker;
import org.eclipse.elk.alg.layered.p1cycles.GreedyCycleBreaker;
import org.eclipse.elk.alg.layered.p1cycles.InteractiveCycleBreaker;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.ILayoutPhaseFactory;
import org.eclipse.elk.graph.properties.AdvancedPropertyValue;

/**
 * Enumeration of and factory for the different available cycle breaking strategies.
 * 
 * @author msp
 * @author cds
 */
public enum CycleBreakingStrategy implements ILayoutPhaseFactory<LayeredPhases, LGraph> {

    /**
     * Applies a greedy heuristic to minimize the number of reversed edges.
     */
    GREEDY,
    /**
     * Applies a depth-first traversal to find and reverse edges to make the graph acyclic.
     */
    DEPTH_FIRST,
    /**
     * Reacts on user interaction by respecting initial node positions. The actual positions
     * as given in the input diagram are considered here. This means that if the user moves
     * a node, that movement is reflected in the decision which edges to reverse.
     */
    @AdvancedPropertyValue
    INTERACTIVE;
    

    @Override
    public ILayoutPhase<LayeredPhases, LGraph> create() {
        switch (this) {
        case GREEDY:
            return new GreedyCycleBreaker();
            
        case DEPTH_FIRST:
            return new DepthFirstCycleBreaker();
            
        case INTERACTIVE:
            return new InteractiveCycleBreaker();
            
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the cycle breaker " + this.toString());
        }
    }

}
