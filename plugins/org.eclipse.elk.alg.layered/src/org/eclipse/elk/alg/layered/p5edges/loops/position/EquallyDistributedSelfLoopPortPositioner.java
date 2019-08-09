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

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Distribute components equally over all sides and corners of the node.
 */
public class EquallyDistributedSelfLoopPortPositioner extends AbstractSelfLoopPortPositioner {

    /**
     * The sides we'll be distributing the ports over.
     */
    private static enum Sides {
        NORTH,
        SOUTH,
        EAST,
        WEST,
        NORTH_WEST_CORNER,
        NORTH_EAST_CORNER,
        SOUTH_WEST_CORNER,
        SOUTH_EAST_CORNER;
    }

    @Override
    public void position(final LNode node) {
        // Receive the node representation and the self loop components
        SelfLoopNode slNode = node.getProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION);
        List<SelfLoopComponent> components = slNode.getSelfLoopComponents();

        // Sort by size
        components.sort((comp1, comp2) -> Integer.compare(comp1.getPorts().size(), comp2.getPorts().size()));
        
        // Distribute non-loop ports
        List<SelfLoopComponent> nonLoopWest = new ArrayList<>();
        List<SelfLoopComponent> nonLoopEast = new ArrayList<>();
        
        distributeNonLoops(node, components, nonLoopWest, nonLoopEast);
        
        // Distribute the components over the different sides and corners
        Multimap<Sides, SelfLoopComponent> sides = ArrayListMultimap.create();
        
        distributeComponentsEqually(components, sides);

        // stack the ports of the sides
        stackComponents(slNode, sides.get(Sides.NORTH), PortSide.NORTH);
        stackComponents(slNode, sides.get(Sides.SOUTH), PortSide.SOUTH);
        stackComponents(slNode, sides.get(Sides.EAST), PortSide.EAST);
        stackComponents(slNode, sides.get(Sides.WEST), PortSide.WEST);

        // place the non loop ports left and right of the side stacks
        placeNonLoopPorts(slNode, PortSide.WEST, nonLoopWest);
        placeNonLoopPorts(slNode, PortSide.EAST, nonLoopEast);

        // place the corner components
        stackCornerComponents(slNode, sides.get(Sides.NORTH_WEST_CORNER), PortSide.WEST, PortSide.NORTH);
        stackCornerComponents(slNode, sides.get(Sides.NORTH_EAST_CORNER), PortSide.NORTH, PortSide.EAST);
        stackCornerComponents(slNode, sides.get(Sides.SOUTH_EAST_CORNER), PortSide.EAST, PortSide.SOUTH);
        stackCornerComponents(slNode, sides.get(Sides.SOUTH_WEST_CORNER), PortSide.SOUTH, PortSide.WEST);
    }

    /**
     * Distribute the components over the different sides and corners.
     */
    private void distributeComponentsEqually(final List<SelfLoopComponent> components,
            final Multimap<Sides, SelfLoopComponent> sides) {

        Sides[] availableSides = Sides.values();
        int currSideIndex = 0;
        
        for (SelfLoopComponent component : components) {
            sides.put(availableSides[currSideIndex], component);
            currSideIndex = (currSideIndex + 1) % availableSides.length;
        }
    }

    /**
     * Distribute the non loop components over the west and east side.
     */
    private void distributeNonLoops(final LNode node, final List<SelfLoopComponent> components,
            final List<SelfLoopComponent> nonLoopWest, final List<SelfLoopComponent> nonLoopEast) {
        
        // filter the non loop components
        List<SelfLoopComponent> nonLoopComponents = components.stream()
                .filter(comp -> comp.getPorts().size() == 1)
                .collect(Collectors.toList());

        Layer currentLayer = node.getLayer();
        int currentLayerIndex = currentLayer.getIndex();

        for (SelfLoopComponent component : nonLoopComponents) {
            SelfLoopPort port = component.getPorts().get(0);
            port.setPortSide(PortSide.UNDEFINED);
            LPort lport = port.getLPort();

            for (LEdge edge : lport.getConnectedEdges()) {
                LPort relevantPort;
                if (edge.getTarget() == lport) {
                    relevantPort = edge.getSource();
                } else {
                    relevantPort = edge.getTarget();
                }

                Layer layer = relevantPort.getNode().getLayer();
                if (currentLayerIndex > layer.getIndex()) {
                    port.setPortSide(PortSide.WEST);
                    nonLoopWest.add(component);
                    break;
                }
            }

            if (port.getPortSide() == PortSide.UNDEFINED) {
                port.setPortSide(PortSide.EAST);
                nonLoopEast.add(component);
            }
        }
        
        components.removeAll(nonLoopComponents);
    }
}
