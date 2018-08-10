/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.SelfLoopOrderingStrategy;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.core.options.PortSide;

/**
 * Distributes the ports over the north and south side of their node.
 */
public class NorthSouthSelfLoopPortPositioner extends AbstractSelfLoopPortPositioner {

    /** Determine if the loops will be stacked or sequenced. */
    private final SelfLoopOrderingStrategy ordering;

    /**
     * Create a new instance configured for the given ordering strategy.
     */
    public NorthSouthSelfLoopPortPositioner(final SelfLoopOrderingStrategy ordering) {
        this.ordering = ordering;
    }

    @Override
    public void position(final LNode node) {
        // receive the node representation and the self-loop components
        SelfLoopNode slNode = node.getProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION);
        List<SelfLoopComponent> components = slNode.getSelfLoopComponents();

        // sort by size
        components.sort((comp1, comp2) -> Integer.compare(comp1.getPorts().size(), comp2.getPorts().size()));

        // filter the non loop components
        List<SelfLoopComponent> nonLoopComponents = components.stream()
                .filter(comp -> comp.getPorts().size() == 1)
                .collect(Collectors.toList());
        components.removeAll(nonLoopComponents);

        // distribute the components over the two sides depending on their amount of ports
        List<SelfLoopComponent> componentSide1 = new ArrayList<SelfLoopComponent>();
        List<SelfLoopComponent> componentSide2 = new ArrayList<SelfLoopComponent>();

        int portsSide1 = 0;
        int portsSide2 = 0;

        for (SelfLoopComponent component : components) {
            if (portsSide1 <= portsSide2) {
                componentSide1.add(component);
                for (SelfLoopPort port : component.getPorts()) {
                    port.setPortSide(PortSide.NORTH);
                    portsSide1++;
                }
            } else {
                componentSide2.add(component);
                for (SelfLoopPort port : component.getPorts()) {
                    port.setPortSide(PortSide.SOUTH);
                    portsSide2++;
                }
            }
        }

        // stack or sequence depending on the ordering
        if (ordering == SelfLoopOrderingStrategy.STACKED) {
            stackComponents(slNode, componentSide1, PortSide.NORTH);
            stackComponents(slNode, componentSide2, PortSide.SOUTH);
        } else {
            sequenceComponents(slNode, componentSide1, PortSide.NORTH);
            sequenceComponents(slNode, componentSide2, PortSide.SOUTH);
        }
    }
}
