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

import org.eclipse.elk.alg.spore.SPOrEPhases;
import org.eclipse.elk.alg.spore.graph.Graph;
import org.eclipse.elk.alg.spore.p3execution.GrowTreePhase;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.ILayoutPhaseFactory;

/**
 * Definition of the strategy for the third phase of overlap removal by growing a tree.
 */
public enum OverlapRemovalStrategy implements ILayoutPhaseFactory<SPOrEPhases, Graph> {
    /** Overlap removal by growing a tree. */
    GROW_TREE;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ILayoutPhase<SPOrEPhases, Graph> create() {
        return new GrowTreePhase();
    }
}
