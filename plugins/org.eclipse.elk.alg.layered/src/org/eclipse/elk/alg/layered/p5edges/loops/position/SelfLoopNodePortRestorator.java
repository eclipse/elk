/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNodeSide;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.core.math.KVector;

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
     * Copy the ports gathered while preprocessing back to the original node.
     */
    public static void restorePorts(final LNode node) {
        SelfLoopNode nodeRep = node.getProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION);
        if (nodeRep == null) {
            return;
        }
        
        List<LPort> nodePorts = new ArrayList<LPort>();

        for (SelfLoopNodeSide side : nodeRep.getSides()) {
            for (SelfLoopPort port : side.getPorts()) {
                LPort lPort = port.getLPort();
                lPort.setSide(port.getPortSide());
                nodePorts.add(lPort);
                setPortAnchor(lPort);
            }
        }

        List<LPort> realNodePorts = node.getPorts();
        realNodePorts.removeAll(nodePorts);
        realNodePorts.addAll(nodePorts);
    }
    
    /**
     * Copy the ports gathered while preprocessing back to the original node, and add
     * position information to any port that was previously removed from the graph.
     */
    public static void restoreAndPlacePorts(final LNode node) {
        SelfLoopNode nodeRep = node.getProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION);
        if (nodeRep == null) {
            return;
        }
        
        List<LPort> nodePorts = new ArrayList<LPort>();
        List<LPort> realNodePorts = node.getPorts();
        Set<LPort> alreadyArranged = new HashSet<>(realNodePorts);

        for (SelfLoopNodeSide side : nodeRep.getSides()) {
            List<SelfLoopPort> sidePorts = side.getPorts();
            double lastPosition = getInitialPosition(side, node);
            Double spacing = null;
            for (int i = 0; i < sidePorts.size(); i++) {
                SelfLoopPort port = sidePorts.get(i);
                LPort lPort = port.getLPort();
                if (alreadyArranged.contains(lPort)) {
                    spacing = null;
                } else {
                    lPort.setSide(port.getPortSide());
                    if (spacing == null) {
                        spacing = computeSpacing(side, i + 1, lastPosition, node, alreadyArranged);
                    }
                    setPosition(lPort, lastPosition + spacing, node);
                }
                nodePorts.add(lPort);
                setPortAnchor(lPort);
                lastPosition = getPosition(lPort);
            }
        }

        realNodePorts.removeAll(nodePorts);
        realNodePorts.addAll(nodePorts);
    }

    /**
     * Adapt the anchor of the port.
     */
    private static void setPortAnchor(final LPort port) {
        switch (port.getSide()) {
        case EAST:
            // adapt the anchor so edges are attached center right
            port.getAnchor().x = port.getSize().x;
            port.getAnchor().y = port.getSize().y / 2;
            break;
        case WEST:
            // adapt the anchor so edges are attached center left
            port.getAnchor().x = 0;
            port.getAnchor().y = port.getSize().y / 2;
            break;
        case NORTH:
            // adapt the anchor so edges are attached top center
            port.getAnchor().x = port.getSize().x / 2;
            port.getAnchor().y = 0;
            break;
        case SOUTH:
            // adapt the anchor so edges are attached bottom center
            port.getAnchor().x = port.getSize().x / 2;
            port.getAnchor().y = port.getSize().y;
            break;
        }
    }
    
    /**
     * Return the initial position for the given node side.
     */
    private static double getInitialPosition(final SelfLoopNodeSide side, final LNode node) {
        switch (side.getSide()) {
        case SOUTH:
            return node.getSize().x;
        case WEST:
            return node.getSize().y;
        default:
            return 0;
        }
    }
    
    /**
     * Compute the spacing between ports to be positioned.
     */
    private static double computeSpacing(final SelfLoopNodeSide side, final int startIndex,
            final double lastPosition, final LNode node, final Set<LPort> alreadyArranged) {
        List<SelfLoopPort> sidePorts = side.getPorts();
        for (int i = startIndex; i < sidePorts.size(); i++) {
            SelfLoopPort port = sidePorts.get(i);
            if (alreadyArranged.contains(port.getLPort())) {
                KVector portPosition = port.getLPort().getPosition();
                int divisor = i - startIndex + 2;
                switch (side.getSide()) {
                case NORTH:
                case SOUTH:
                    return (portPosition.x - lastPosition) / divisor;
                case EAST:
                case WEST:
                    return (portPosition.y - lastPosition) / divisor;
                }
            }
        }
        KVector nodeSize = node.getSize();
        int divisor = sidePorts.size() - startIndex + 2;
        switch (side.getSide()) {
        case NORTH:
            return (nodeSize.x - lastPosition) / divisor;
        case EAST:
            return (nodeSize.y - lastPosition) / divisor;
        case SOUTH:
        case WEST:
            return -lastPosition / divisor;
        default:
            return Double.NaN;
        }
    }
    
    /**
     * Retrieve the position of a port.
     */
    private static double getPosition(final LPort port) {
        switch (port.getSide()) {
        case EAST:
        case WEST:
            return port.getPosition().y;
        case NORTH:
        case SOUTH:
            return port.getPosition().x;
        default:
            return Double.NaN;
        }
    }
    
    /**
     * Update the position of a port.
     */
    private static void setPosition(final LPort port, final double position, final LNode node) {
        double offset = port.getProperty(LayeredOptions.PORT_BORDER_OFFSET);
        switch (port.getSide()) {
        case EAST:
            port.getPosition().x = node.getSize().x + offset;
            port.getPosition().y = position;
            break;
        case WEST:
            port.getPosition().x = 0 - port.getSize().x - offset;
            port.getPosition().y = position;
            break;
        case NORTH:
            port.getPosition().x = position;
            port.getPosition().y = 0 - port.getSize().y - offset;
            break;
        case SOUTH:
            port.getPosition().x = position;
            port.getPosition().y = node.getSize().y + offset;
            break;
        }
    }
    
}
