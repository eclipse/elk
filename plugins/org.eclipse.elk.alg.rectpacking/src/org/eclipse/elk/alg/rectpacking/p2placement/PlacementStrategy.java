/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.rectpacking.p2placement;

import org.eclipse.elk.alg.rectpacking.RectPackingLayoutPhases;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.ILayoutPhaseFactory;
import org.eclipse.elk.graph.ElkNode;

/**
 * @author sdo
 *
 */
public enum PlacementStrategy implements ILayoutPhaseFactory<RectPackingLayoutPhases, ElkNode> {
    SIMPLE,
    NONE;

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutPhaseFactory#create()
     */
    @Override
    public ILayoutPhase<RectPackingLayoutPhases, ElkNode> create() {
        switch (this) {
        case SIMPLE:
            return new SimpleNodePlacer();
        case NONE:
            return new NoNodePlacer();
        default:
            return null;
        }
    }

}
