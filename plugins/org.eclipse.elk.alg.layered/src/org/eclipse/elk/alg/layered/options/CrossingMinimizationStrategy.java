/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
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
import org.eclipse.elk.alg.layered.p3order.InteractiveCrossingMinimizer;
import org.eclipse.elk.alg.layered.p3order.LayerSweepCrossingMinimizer;
import org.eclipse.elk.alg.layered.p3order.LayerSweepCrossingMinimizer.CrossMinType;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.ILayoutPhaseFactory;
import org.eclipse.elk.graph.properties.AdvancedPropertyValue;

/**
 * Enumeration of and factory for the different available crossing minimization strategies.
 * 
 * @author msp
 * @author cds
 */
public enum CrossingMinimizationStrategy implements ILayoutPhaseFactory<LayeredPhases, LGraph> {

    /**
     * This heuristic sweeps through the layers, trying to minimize the crossings locally. When
     * {@link org.eclipse.elk.alg.layered.options.LayeredOptions.HIERARCHY_HANDLING} is set to
     * {@link org.eclipse.elk.core.options.HierarchyHandling.INCLUDE_CHILDREN}, it sweeps into hierarchical graphs
     * during the sweep.
     */
    LAYER_SWEEP,

    /**
     * Allow user interaction by considering the previous node positioning. The actual positions
     * as given in the input diagram are considered here. This means that if the user moves
     * a node, that movement is reflected in the ordering of nodes.
     */
    @AdvancedPropertyValue
    INTERACTIVE;
    

    @Override
    public ILayoutPhase<LayeredPhases, LGraph> create() {
        switch (this) {
        case LAYER_SWEEP:
            return new LayerSweepCrossingMinimizer(CrossMinType.BARYCENTER);
            
        case INTERACTIVE:
            return new InteractiveCrossingMinimizer();
            
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the crossing minimizer " + this.toString());
        }
    }

}
