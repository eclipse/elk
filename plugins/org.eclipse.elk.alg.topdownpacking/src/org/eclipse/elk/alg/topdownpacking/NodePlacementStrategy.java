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
 * @author mka
 *
 */
public enum NodePlacementStrategy implements ILayoutPhaseFactory<TopdownPackingPhases, GridElkNode> {
    NODE_PLACER;

    /**
     * {@inheritDoc}
     */
    @Override
    public ILayoutPhase<TopdownPackingPhases, GridElkNode> create() {
        switch (this) {
        case NODE_PLACER:
            return new NodePlacer();
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the node placer " + this.toString());
        }
    }

}
