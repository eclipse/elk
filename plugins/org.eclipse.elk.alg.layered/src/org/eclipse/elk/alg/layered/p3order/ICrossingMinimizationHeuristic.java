/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.p3order.counting.IInitializable;

/**
 * Determines the node order of a given free layer. Uses heuristic methods to find an ordering that minimizes edge
 * crossings between the given free layer and a neighboring layer with fixed node order. Given constraints are to be
 * respected, possibly by the use of an {@link org.eclipse.elk.alg.layered.p3order.constraints.IConstraintResolver}.
 * <p>
 * Must be initialized using {@link IInitializable#init(java.util.List)}!
 * </p>
 * 
 * @author cds
 * @author ima
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public interface ICrossingMinimizationHeuristic extends IInitializable {

    /** Determines whether the heuristic always improves. */
    boolean alwaysImproves();

    /**
     * Set the order in the first layer (with regard to the sweep direction) according to how the heuristic works.
     *
     * @param order
     *            the current order of the nodes.
     * @param forwardSweep
     *            whether we are sweeping forward or not.
     */
    boolean setFirstLayerOrder(LNode[][] order, boolean forwardSweep);

    /**
     * Minimize crossings in the layer as indicated by freeLayerIndex.
     * 
     * @param order
     *            the current order of the nodes.
     * @param freeLayerIndex
     *            the index of the layer being reorderd
     * @param forwardSweep
     *            whether we are sweeping forward or not.
     * @param isFirstSweep
     *            whether this is the first sweep or not.
     */
    boolean minimizeCrossings(LNode[][] order, int freeLayerIndex, boolean forwardSweep,
            boolean isFirstSweep);

    /** Determines whether the heuristic is deterministic or has random decisions. */
    boolean isDeterministic();

}
