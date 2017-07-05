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
package org.eclipse.elk.alg.radial.options;

import org.eclipse.elk.alg.radial.intermediate.optimization.CrossingMinimizationPosition;
import org.eclipse.elk.alg.radial.intermediate.optimization.EdgeLengthOptimization;
import org.eclipse.elk.alg.radial.intermediate.optimization.EdgeLengthPositionOptimization;
import org.eclipse.elk.alg.radial.intermediate.optimization.IEvaluation;
import org.eclipse.elk.graph.properties.AdvancedPropertyValue;

/**
 * A list of the possible optimization strategies.
 */
public enum RadialTranslationStrategy {
    /** Do nothing. */
    NONE,
    /** Optimize the edge length. */
    EDGE_LENGTH,
    /** Optimize the edge length by the position specified in CoreOptions.POSITION. */
    @AdvancedPropertyValue
    EDGE_LENGTH_BY_POSITION,
    /** Optimize the number of crossing in the graph. */
    CROSSING_MINIMIZATION_BY_POSITION;

    /**
     * Instantiate the chosen optimization strategy.
     * 
     * @return A wedge compactor.
     */
    public IEvaluation create() {
        switch (this) {
        case EDGE_LENGTH:
            return new EdgeLengthOptimization();
        case EDGE_LENGTH_BY_POSITION:
            return new EdgeLengthPositionOptimization();
        case CROSSING_MINIMIZATION_BY_POSITION:
            return new CrossingMinimizationPosition();
        case NONE:
            return null;
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the layout option " + this.toString());
        }
    }
}