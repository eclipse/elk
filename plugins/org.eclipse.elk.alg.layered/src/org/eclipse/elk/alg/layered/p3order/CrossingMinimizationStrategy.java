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

import org.eclipse.elk.alg.layered.ILayoutPhase;
import org.eclipse.elk.alg.layered.ILayoutPhaseFactory;
import org.eclipse.elk.alg.layered.p3order.LayerSweepCrossingMinimizer.CrossMinType;

/**
 * Enumeration of and factory for the different available crossing minimization strategies.
 * 
 * @author msp
 * @author cds
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating proposed yellow by msp
 */
public enum CrossingMinimizationStrategy implements ILayoutPhaseFactory {

    /**
     * This heuristic sweeps through the layers, trying to minimize the crossings locally. When
     * {@link org.eclipse.elk.alg.layered.properties.LayeredOptions.HIERARCHY_HANDLING} is set to
     * {@link org.eclipse.elk.core.options.HierarchyHandling.INCLUDE_CHILDREN}, it sweeps into hierarchical graphs
     * during the sweep.
     */
    LAYER_SWEEP,

    /**
     * Allow user interaction by considering the previous node positioning. The actual positions
     * as given in the input diagram are considered here. This means that if the user moves
     * a node, that movement is reflected in the ordering of nodes.
     */
    INTERACTIVE;
    

    /**
     * {@inheritDoc}
     */
    public ILayoutPhase create() {
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
