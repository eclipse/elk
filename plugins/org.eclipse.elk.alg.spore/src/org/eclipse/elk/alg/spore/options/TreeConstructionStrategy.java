/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.spore.options;

import org.eclipse.elk.alg.common.structure.ILayoutPhase;
import org.eclipse.elk.alg.common.structure.ILayoutPhaseFactory;
import org.eclipse.elk.alg.spore.SPOrEPhases;
import org.eclipse.elk.alg.spore.graph.Graph;
import org.eclipse.elk.alg.spore.p2processingorder.MaxSTPhase;
import org.eclipse.elk.alg.spore.p2processingorder.MinSTPhase;

/**
 * Definition of the spanning tree construction strategy for the processing order phase.
 */
public enum TreeConstructionStrategy implements ILayoutPhaseFactory<SPOrEPhases, Graph> {
    /** Minimum spanning tree construction. */
    MINIMUM_SPANNING_TREE,
    /** Maximum spanning tree construction. */
    MAXIMUM_SPANNING_TREE;

    /**
     * {@inheritDoc}
     */
    @Override
    public ILayoutPhase<SPOrEPhases, Graph> create() {
        switch (this) {
        case MINIMUM_SPANNING_TREE:
            return new MinSTPhase();

        case MAXIMUM_SPANNING_TREE:
            return new MaxSTPhase();

        default:
            throw new IllegalArgumentException(
                    "No implementation available for " + this.toString());
        }
    }

}
