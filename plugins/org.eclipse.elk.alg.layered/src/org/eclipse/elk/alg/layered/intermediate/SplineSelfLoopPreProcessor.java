/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.p5edges.splines.ConnectedSelfLoopComponent;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * Finds connected components of self loops and adds them to the SPLINE_SELFLOOP_COMPONENTS property
 * of the node. The text's width and height is also calculated.
 * 
 * See header of class ConnectedSelfLoop for an explanation of connected components of self loops.  
 *
 * Sets the port side to UNDEFIEND for all ports on a node those PORT_CONSTRAINTS is set to < FixedSide.
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>An unlayered graph.</dd>
 *   <dt>Postcondition:</dt><dd>All self-loops are grouped into connected components. 
 *             The components are added to the SPLINE_SELFLOOP_COMPONENTS property of the node, the
 *             self-loops are laying on.
 *             The port side of ports of nodes with port constraints < fixed_side are set to UNDEFINED.
 *             Some ports are removed from the node: All ports only having self loops connected to them,
 *             but only if their port side is undefined.
 *             </dd>
 *   <dt>Slots:</dt><dd>Before phase 1.</dd>
 *   <dt>Same-slot dependencies:</dt><dd>None.</dd>
 * </dl>
 *
 * @author tit
 */
public final class SplineSelfLoopPreProcessor implements ILayoutProcessor {

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Spline SelfLoop pre-processing.", 1);
       
        // A set of all loop edges of the currently processed node.
        final Set<LEdge> loopEdges = Sets.newLinkedHashSet();

        // process all nodes
        for (final LNode node : layeredGraph.getLayerlessNodes()) {
            correctPortSideConstraint(node);
            loopEdges.clear();

            // find all self-loops of the node. Don't use node.connectedEdges, as it returns all
            // loops twice.
            for (final LEdge edge : node.getOutgoingEdges()) {
                if (edge.isSelfLoop()) {
                    loopEdges.add(edge);
                }
            }

            /* First we will turn some loops, so that they go into the directions:
             * North->South / West->East / Counterclockwise  
             * This way we have to care about fewer cases.
             * We will reverse following edges:
             *  1. North -> East or South
             *  2. East -> South
             *  3. South -> West
             *  4. West -> North or East
             */
            for (final LEdge edge : loopEdges) {
                final PortSide sourcePortSide = edge.getSource().getSide();
                final PortSide targetPortSide = edge.getTarget().getSide();
                
                if ((sourcePortSide == PortSide.NORTH
                     && (targetPortSide == PortSide.EAST || targetPortSide == PortSide.SOUTH))
                  || (sourcePortSide == PortSide.EAST && targetPortSide == PortSide.SOUTH)
                  || (sourcePortSide == PortSide.SOUTH && targetPortSide == PortSide.WEST)
                  || (sourcePortSide == PortSide.WEST
                     && (targetPortSide == PortSide.NORTH || targetPortSide == PortSide.EAST))) {
                    
                    edge.reverse(layeredGraph, false);
                }
            }
            
            // Construct the set of connected components...
            final List<ConnectedSelfLoopComponent> allComponents = 
                    findAllConnectedComponents(loopEdges, node);

            // ... and add the set to the node's properties.
            node.setProperty(InternalProperties.SPLINE_SELFLOOP_COMPONENTS, allComponents);
            
            // Now we will hide self-loop ports from the node.
            // We don't hide the ports of nodes with portConstraint "fixedOrder",
            // or if the port has a non-loop edge connected.
            if (!node.getProperty(LayeredOptions.PORT_CONSTRAINTS).isOrderFixed()) {
                final Set<LPort> portsToHide = Sets.newHashSet();
                for (final ConnectedSelfLoopComponent component : allComponents) {
                    portsToHide.addAll(component.getHidablePorts());
                    portsToHide.addAll(component.getPortsWithPortSide());
                }
                
                // Hide the ports by removing them from the node. 
                // The reference of the port to the node is kept, 
                // as we will re-add the port to the same node later. (in the SplieSelfLoopPositioner) 
                final Iterator<LPort> itr = node.getPorts().listIterator();
                while (itr.hasNext()) {
                    final LPort port = itr.next();
                    if (portsToHide.contains(port)) {
                        itr.remove();
                    }
                }
            }
        }
        monitor.done();
    }
    
    /**
     * Sets the portSide of all ports of the given node to {@code undefined}, if port sides are 
     * not fixed for the given node.
     * 
     * @param node The node to process.
     */
    private void correctPortSideConstraint(final LNode node) {
        if (!node.getProperty(LayeredOptions.PORT_CONSTRAINTS).isSideFixed()) {
            for (final LPort port : node.getPorts()) {
                port.setSide(PortSide.UNDEFINED);
            }
        }
    }
    
    /**
     * Creates a set of all connected components. If there are ports with no edge, they will form a
     * component of size 1. If there are edges connecting ports not in the list of ports, this will
     * result in unpredictable behavior.
     * 
     * @param loopEdges The edges connecting the ports. They may not connect any ports not in the list 
     *            of ports!
     * @param node The node we are currently working on.         
     * @return A set of sets. Every single set represents a connected component.
     */
    private static List<ConnectedSelfLoopComponent> findAllConnectedComponents(
            final Set<LEdge> loopEdges, final LNode node) {
        
        final List<ConnectedSelfLoopComponent> components = Lists.newArrayList();
        final Multimap<LPort, LEdge> portToEdge = LinkedListMultimap.create();
        
        for (final LEdge edge : loopEdges) {
            portToEdge.put(edge.getSource(), edge);
            portToEdge.put(edge.getTarget(), edge);
        }
        
        while (!portToEdge.isEmpty()) {
            components.add(findAConnectedComponent(
                    portToEdge, 
                    node, 
                    node.getProperty(LayeredOptions.PORT_CONSTRAINTS).isOrderFixed()));
        }
        return components;
    }

    /**
     * Finds a set of connected edges. Two edges are connected if they share a port, or if there
     * is a path between them, only containing self-loops.
     * 
     * @param portsToEdges A Multimap holding all connections between the ports. 
     *          ATTENTION: THIS PARAMETER GETS ALTERED: 
     *          All edges included in the return value are removed from the Multimap.
     * @param node The node we are currently working on. 
     * @param isFixedOrder True, if the port-order of the node is at least {@code fixedOrder}. 
     * @return A set of connected edges. 
     */
    private static ConnectedSelfLoopComponent findAConnectedComponent(
            final Multimap<LPort, LEdge> portsToEdges, final LNode node, final boolean isFixedOrder) {

        final Multimap<LEdge, LPort> edgeToPort = ArrayListMultimap.create();
        Multimaps.invertFrom(portsToEdges, edgeToPort);
        
        // The connected components element we are constructing.
        final ConnectedSelfLoopComponent connectedComponent = new ConnectedSelfLoopComponent(node);

        // Create a list of ports we have to check for connected ports. Initially add an arbitrary port.
        final List<LPort> portsToProcess = Lists.newArrayList();
        portsToProcess.add(portsToEdges.keys().iterator().next());

        final List<LPort> portsProcessed = Lists.newArrayList();
        // Check ports for connection to current connected component till no port to check is left.
        while (!portsToProcess.isEmpty()) {
            final LPort currentPort = portsToProcess.iterator().next();
            portsProcessed.add(currentPort);
            final Collection<LEdge> allEdgesOnCurrentPort = portsToEdges.removeAll(currentPort);
            
            for (final LEdge currentEdge : allEdgesOnCurrentPort) {
                if (connectedComponent.tryAddEdge(currentEdge, isFixedOrder)) {
                    final Collection<LPort> portsOfCurrentEdge = edgeToPort.removeAll(currentEdge);
                    for (final LPort port : portsOfCurrentEdge) {
                        if (!portsProcessed.contains(port)) {
                            portsToProcess.add(port);
                        }
                    }
                }
            }
            portsToProcess.remove(currentPort);
        }
        return connectedComponent;
    }
 }
