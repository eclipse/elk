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
package org.eclipse.elk.alg.common;

/**
 * Interface for a cost function used in the processing order phase.
 */
@FunctionalInterface
public interface ICostFunction {
    /** Cost of an edge.
     *
     * @param edge
     * @return cost associated with an edge created in the structure phase
     */
    double cost(TEdge edge);
}
