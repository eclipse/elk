/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  
 *******************************************************************************/
package org.eclipse.elk.alg.topdownpacking;

import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.ILayoutPhaseFactory;

/**
 * Node placement strategy to use during topdown packing algorithm.
 *
 */
public enum NodePlacementStrategy implements ILayoutPhaseFactory<TopdownPackingPhases, GridElkNode> {
    /**
     * Places nodes from left to right, top to bottom in a grid.
     */
    LEFT_RIGHT_TOP_DOWN_NODE_PLACER;

    /**
     * {@inheritDoc}
     */
    @Override
    public ILayoutPhase<TopdownPackingPhases, GridElkNode> create() {
        switch (this) {
        case LEFT_RIGHT_TOP_DOWN_NODE_PLACER:
            return new LeftRightTopDownNodePlacer();
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the node placer " + this.toString());
        }
    }

}
