/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.position;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.options.SelfLoopDistributionStrategy;
import org.eclipse.elk.alg.layered.options.SelfLoopOrderingStrategy;

/**
 * Positions ports around nodes that have free or undefined port constraints such that edge crossings are minimized.
 * Depending on the configured {@link SelfLoopDistributionStrategy}, this class delegates to more specialized classes.
 * 
 * @see EquallyDistributedSelfLoopPortPositioner
 * @see NorthSelfLoopPortPositioner
 * @see NorthSouthSelfLoopPortPositioner
 */
public class FreePortsSelfLoopPortPositioner extends AbstractSelfLoopPortPositioner {

    /** The concrete positioner we're using. */
    private ISelfLoopPortPositioner positioner;

    /**
     * Creates a new instance that delegates to a more specialized class depending on the given strategies.
     */
    public FreePortsSelfLoopPortPositioner(final SelfLoopDistributionStrategy distribution,
            final SelfLoopOrderingStrategy ordering) {

        switch (distribution) {
        case NORTH:
            positioner = new NorthSelfLoopPortPositioner(ordering);
        case NORTH_SOUTH:
            positioner = new NorthSouthSelfLoopPortPositioner(ordering);
        case EQUALLY:
            positioner = new EquallyDistributedSelfLoopPortPositioner();
        }
    }

    @Override
    public void position(final LNode node) {
        positioner.position(node);
    }

}
