/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.radial.intermediate;

import org.eclipse.elk.alg.radial.intermediate.compaction.GeneralCompactor;
import org.eclipse.elk.alg.radial.intermediate.overlaps.RadiusExtensionOverlapRemoval;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.alg.ILayoutProcessorFactory;
import org.eclipse.elk.graph.ElkNode;

/**
 * The list of intermediate processors.
 *
 */
public enum IntermediateProcessorStrategy implements ILayoutProcessorFactory<ElkNode> {
    // After phase 1
    /** Remove the overlaps from the graph. */
    OVERLAP_REMOVAL,
    /** Compact the current layout. */
    COMPACTION,

    // Before phase 2
    /** Calculate the graph size to the new values. */
    GRAPH_SIZE_CALCULATION;

    /**
     * {@inheritDoc}
     */
    public ILayoutProcessor<ElkNode> create() {
        switch (this) {
        case OVERLAP_REMOVAL:
            return new RadiusExtensionOverlapRemoval();
        case COMPACTION:
            return new GeneralCompactor();
        case GRAPH_SIZE_CALCULATION:
            return new CalculateGraphSize();
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the layout processor " + this.toString());
        }
    }

}