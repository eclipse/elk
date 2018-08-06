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

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNodeSide;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.core.options.PortSide;

/**
 * During self loop routing ports where processed using a {@link SelfLoopNode} object. These information now have to be
 * added back to the original ports and the node, which is what the methods in this class do.
 */
public final class SelfLoopNodePortRestorator {
    
    /**
     * No instantiation.
     */
    private SelfLoopNodePortRestorator() {
    }

    /**
     * Map Node Representation back to node.
     * 
     * @param layeredGraph
     */
    public static void restorePorts(final LNode node) {
        SelfLoopNode nodeRep = node.getProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION);

        if (nodeRep != null) {
            List<LPort> nodePorts = new ArrayList<LPort>();

            for (SelfLoopNodeSide side : nodeRep.getSides()) {
                for (SelfLoopPort port : side.getPorts()) {
                    LPort lPort = port.getLPort();
                    lPort.setSide(port.getPortSide());
                    nodePorts.add(lPort);
                    setSidePositionOfPort(lPort, port.getPortSide());
                }
            }

            List<LPort> realNodePorts = node.getPorts();
            realNodePorts.removeAll(nodePorts);
            realNodePorts.addAll(nodePorts);
        }
    }

    /**
     * Sets the port side of the port as specified. Also calculates the x or y coordinate that can be derived from the
     * portSide.
     * 
     * @param port
     *            The port to work on.
     * @param side
     *            The port side to assign.
     */
    private static void setSidePositionOfPort(final LPort port, final PortSide side) {
        switch (side) {
        case EAST:
            port.setSide(PortSide.EAST);
            // adapt the anchor so edges are attached center right
            port.getAnchor().x = port.getSize().x;
            port.getAnchor().y = port.getSize().y / 2;
            break;
        case WEST:
            port.setSide(PortSide.WEST);
            // adapt the anchor so edges are attached center left
            port.getAnchor().x = 0;
            port.getAnchor().y = port.getSize().y / 2;
            break;
        case NORTH:
            port.setSide(PortSide.NORTH);
            // adapt the anchor so edges are attached top center
            port.getAnchor().x = port.getSize().x / 2;
            port.getAnchor().y = 0;
            break;
        case SOUTH:
            port.setSide(PortSide.SOUTH);
            // adapt the anchor so edges are attached bottom center
            port.getAnchor().x = port.getSize().x / 2;
            port.getAnchor().y = port.getSize().y;
            break;
        }
    }
}
