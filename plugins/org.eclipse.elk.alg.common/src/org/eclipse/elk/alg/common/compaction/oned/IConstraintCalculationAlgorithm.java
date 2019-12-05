/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.common.compaction.oned;

/**
 * An algorithm that calculates separation constraints in one dimension that are induced by a
 * set of boxes in the plane.
 */
@FunctionalInterface
public interface IConstraintCalculationAlgorithm {

    /**
     * @param compactor
     *            the instance of the surrounding {@link OneDimensionalCompactor}.
     */
    void calculateConstraints(OneDimensionalCompactor compactor);
    
}