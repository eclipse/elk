/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.position;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.SelfLoopOrderingStrategy;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNode;
import org.eclipse.elk.core.options.PortSide;

/**
 * Places all ports on the north side of the node.
 */
public class NorthSelfLoopPortPositioner extends AbstractSelfLoopPortPositioner {
    
    /** Determines if the loops will be stacked or sequenced. */
    private SelfLoopOrderingStrategy ordering;

    /**
     * Create a new instance configured for the given ordering strategy.
     */
    public NorthSelfLoopPortPositioner(final SelfLoopOrderingStrategy ordering) {
        this.ordering = ordering;
    }

    @Override
    public void position(final LNode node) {
        // receive the node representation and the self-loop components
        OldSelfLoopNode slNode = node.getProperty(InternalProperties.SELF_LOOP_NODE_REPRESENTATION);
        List<OldSelfLoopComponent> components = slNode.getSelfLoopComponents();

        // sort by size
        components.sort((comp1, comp2) -> Integer.compare(comp1.getPorts().size(), comp2.getPorts().size()));

        // filter the non loop components
        List<OldSelfLoopComponent> nonLoopComponents = components.stream()
                .filter(comp -> comp.getPorts().size() == 1)
                .collect(Collectors.toList());
        components.removeAll(nonLoopComponents);

        // stack or sequence depending on the ordering
        if (ordering == SelfLoopOrderingStrategy.STACKED) {
            stackComponents(slNode, components, PortSide.NORTH);
        } else {
            sequenceComponents(slNode, components, PortSide.NORTH);
        }
    }

}
