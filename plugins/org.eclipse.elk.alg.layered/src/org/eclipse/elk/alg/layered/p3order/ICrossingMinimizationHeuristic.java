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

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LNode;

/**
 * Determines the node order of a given free layer. Uses heuristic methods to find an ordering that
 * minimizes edge crossings between the given free layer and a neighboring layer with fixed node
 * order. Given constraints are to be respected, possibly by the use of an
 * {@link de.cau.cs.kieler.klay.layered.p3order.constraints.IConstraintResolver IConstraintResolver}.
 *
 * @author cds
 * @author ima
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public interface ICrossingMinimizationHeuristic {

    boolean alwaysImproves();

    /**
     * Minimize the number of crossings for the edges between the given layer and either its
     * predecessor or its successor. Resolve violated constraints.
     *
     * @param layer
     *            the free layer whose nodes are reordered.
     * @param preOrdered
     *            whether the nodes have been ordered in a previous run.
     * @param randomize
     *            {@code true} if this layer's node order should just be randomized. In that case,
     *            {@code preOrdered} is assumed to be {@code false} and the return value is
     *            {@code 0}.
     * @param forward
     *            whether the free layer is after the fixed layer.
     */
    void minimizeCrossings(List<LNode> layer, boolean preOrdered, boolean randomize, boolean forward);

    /**
     * Set the order in the first layer (with regard to the sweep direction) according to how the
     * heuristic works.
     *
     * @param currentNodeOrder
     *            the current order of the nodes.
     * @param isForwardSweep
     *            whether we are sweeping forward or not.
     */
    boolean setFirstLayerOrder(LNode[][] currentNodeOrder, boolean isForwardSweep);

    /**
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

}
