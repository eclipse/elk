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
package org.eclipse.elk.alg.layered.p1cycles;

import org.eclipse.elk.alg.layered.ILayoutPhase;
import org.eclipse.elk.alg.layered.ILayoutPhaseFactory;

/**
 * Enumeration of and factory for the different available cycle breaking strategies.
 * 
 * @author msp
 * @author cds
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating yellow 2012-11-13 review KI-33 by grh, akoc
 */
public enum CycleBreakingStrategy implements ILayoutPhaseFactory {

    /**
     * Applies a greedy heuristic to minimize the number of reversed edges.
     */
    GREEDY,
    /**
     * Reacts on user interaction by respecting initial node positions. The actual positions
     * as given in the input diagram are considered here. This means that if the user moves
     * a node, that movement is reflected in the decision which edges to reverse.
     */
    INTERACTIVE;
    

    /**
     * {@inheritDoc}
     */
    public ILayoutPhase create() {
        switch (this) {
        case GREEDY:
            return new GreedyCycleBreaker();
            
        case INTERACTIVE:
            return new InteractiveCycleBreaker();
            
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the cycle breaker " + this.toString());
        }
    }

}
