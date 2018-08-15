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
import org.eclipse.elk.alg.spore.p1structure.DelaunayTriangulationPhase;

/**
 * Definition of the structure extraction strategy for SPOrE.
 */
public enum StructureExtractionStrategy implements ILayoutPhaseFactory<SPOrEPhases, Graph> {
    /** A Delaunay triangulation. */
    DELAUNAY_TRIANGULATION;

    /**
     * {@inheritDoc}
     */
    @Override
    public ILayoutPhase<SPOrEPhases, Graph> create() {
        switch (this) {
        case DELAUNAY_TRIANGULATION:
            return new DelaunayTriangulationPhase();

        default:
            throw new IllegalArgumentException(
                    "No implementation available for " + this.toString());
        }
    }

}
