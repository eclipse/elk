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
import org.eclipse.elk.alg.spore.p3execution.ShrinkTreeCompactionPhase;

/**
 * Definition of the compaction strategy for compaction by shrinking a tree.
 */
public enum CompactionStrategy implements ILayoutPhaseFactory<SPOrEPhases, Graph> {
    /** Simple compaction strategy. */
    DEPTH_FIRST;

    /**
     * {@inheritDoc}
     */
    @Override
    public ILayoutPhase<SPOrEPhases, Graph> create() {
        switch (this) {
        case DEPTH_FIRST:
            return new ShrinkTreeCompactionPhase();

        default:
            throw new IllegalArgumentException(
                    "No implementation available for " + this.toString());
        }
    }
}
