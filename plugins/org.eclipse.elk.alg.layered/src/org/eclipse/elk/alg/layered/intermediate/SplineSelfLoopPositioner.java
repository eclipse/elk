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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.p5edges.splines.ConnectedSelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.splines.LoopSide;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.Properties;
import org.eclipse.elk.alg.layered.properties.SelfLoopPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


/**
 * Unhides ports that were hidden by the {@link SplineSelfLoopPreProcessor} before. A sorting of the
 * components is done, in respect to the size of text labels of the edges. The ports are re-added to
 * the node in a crossing minimizing order that depends on the
 * {@link Properties#SPLINE_SELF_LOOP_PLACEMENT} property of the graph.
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>A layered graph.</dd>
 *     <dd>Fixed port order.</dd>
 *     <dd>All {@code ConnectedSelfLoop}s have a loop side set.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>All ports removed from the nodes before are added again. They are added in a way to minimize
 *         crossings of edges.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Between phase 3 and 4.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>None.</dd>
 * </dl>
 * 
 * @author tit
 * @see SplineSelfLoopPreProcessor
 */
public final class SplineSelfLoopPositioner implements ILayoutProcessor {
    
    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Spline SelfLoop positioning", 1);
        
        /** Stores which loop placement strategy to choose. */
        final SelfLoopPlacement loopPlacement = 
                layeredGraph.getProperty(Properties.SELF_LOOP_PLACEMENT);
        
        ////////////////////////////////////////////////////////
        // There are two main jobs to be done:
        // 1) Find a loop-side for each component.
        // 2) Position ports in the correct order around the node.
        ////////////////////////////////////////////////////////
        
        // Process all nodes on all layers.
        for (final Layer layer : layeredGraph) {
            for (final LNode node : layer) {
                
                // Read self-loops components. 
                final List<ConnectedSelfLoopComponent> components = 
                        node.getProperty(InternalProperties.SPLINE_SELFLOOP_COMPONENTS);

                // Components to be distributed by the placement strategy.
                final List<ConnectedSelfLoopComponent> componentsToBePlaced = Lists.newArrayList();

                for (final ConnectedSelfLoopComponent component : components) {
                    // Re-Add all hidden edges to their ports.
                    component.unhideEdges();
                    
                    if (component.getConstrainedPorts().isEmpty()) {
                        // If there is no constraint on any port, we have to process this component later
                        componentsToBePlaced.add(component);
                    } else {
                        // If there is at least one port with a constraint to it's port-side,
                        // we will set the port- and loop-sides by this constraint. (Job 1)
                        setPortSideByConstraint(component);
                        if (!component.getNonLoopPorts().isEmpty()) {
                            // Position and re-add all ports to the node, that are part of a component 
                            // with at least one non-loop edge. (Job 2)
                            addComponentWithNonLoopEdges(component);
                        }
                    }
                }
                
                // Now we have to find a loop-side (job 1) for the remaining components. They are all 
                // stored in componentsToBePlaced. All these components don't have a port with a 
                // constraint on it's portSide, so we can arrange them accordingly to the cosen strategy.
                switch (loopPlacement) {
                case EQUALLY_DISTRIBUTED:
                    setPortSideSpreadEqually(componentsToBePlaced, node);
                    break;
                case NORTH_SEQUENCE:
                    for (final ConnectedSelfLoopComponent component : componentsToBePlaced) {
                        component.setLoopSide(LoopSide.N, true);
                    }
                    break;
                case NORTH_STACKED:
                    for (final ConnectedSelfLoopComponent component : componentsToBePlaced) {
                        component.setLoopSide(LoopSide.N, true);
                    }
                    break;
                default:
                    // Unknown strategy chosen.
                    assert false;
                    break;
                }
                
                // Position and re-add all ports to the node.
                // This is job 2)
                switch (loopPlacement) {
                case EQUALLY_DISTRIBUTED:
                case NORTH_STACKED:
                    portStackedPositioning(components);
                    break;
                case NORTH_SEQUENCE:
                    portLinedPositioning(components);
                    break;
                default:
                    break;
                }
            }
        }
        monitor.done();
    }
    
    /**
     * (Re-)Adds the ports to the node of a component, having at least one port with a non-loop edge.
     * The ports are added as the direct neighbor of a port they are connected to via an edge.
     * 
     * @param component The component whose ports have to get re-added.
     */
    public void addComponentWithNonLoopEdges(final ConnectedSelfLoopComponent component) {
        // the node we are working on
        final LNode node = component.getNode();
        // Set of all ports of the components that are hidden. Initially all ports that CAN be hidden.
        final Set<LPort> hiddenPorts = Sets.newHashSet(component.getHidablePorts());
        // Iterator holding all ports that already have a portSide specified. Initially these are 
        // all ports with an non-loop edge connected to them. While processing the elements, we will 
        // add new ports to this Iterator. So we need a ListIterator.
        final ListIterator<LPort> portsWithSideIter = 
                Lists.newLinkedList(component.getNonLoopPorts()).listIterator(); 

        while (portsWithSideIter.hasNext()) {
            final LPort portWithSide = portsWithSideIter.next();
            if (portWithSide.getOutgoingEdges().isEmpty()) {
                // inbound port
                for (final LEdge edgeWithHiddenPort : portWithSide.getIncomingEdges()) {
                    final LPort hiddenPort = edgeWithHiddenPort.getSource();
                    if (hiddenPorts.contains(hiddenPort)) {
                        final ListIterator<LPort> nodePortIter = node.getPorts().listIterator();
                        LPort portOnNode = nodePortIter.next();
                        // find the port with side ...
                        while (!portOnNode.equals(portWithSide)) {
                            portOnNode = nodePortIter.next();
                        }
                        // ... and add the hidden port on it's right side
                        nodePortIter.add(hiddenPort);
                        portsWithSideIter.add(hiddenPort);
                        setSideOfPort(hiddenPort, portWithSide.getSide());
                        // ensure the next element will be our new added element
                        portsWithSideIter.previous();
                        portsWithSideIter.previous();
                        hiddenPorts.remove(hiddenPort);
                    }
                }
            } else {
                // outbound port
                for (final LEdge edgeWithHiddenPort : portWithSide.getOutgoingEdges()) {
                    final LPort hiddenPort = edgeWithHiddenPort.getTarget();
                    if (hiddenPorts.contains(hiddenPort)) {
                        final ListIterator<LPort> nodePortIter = node.getPorts().listIterator();
                        LPort portOnNode = nodePortIter.next();
                        // find the port with side ...
                        while (!portOnNode.equals(portWithSide)) {
                            portOnNode = nodePortIter.next();
                        }
                        // ... and add the hidden port on it's left side
                        nodePortIter.previous();
                        nodePortIter.add(hiddenPort);
                        portsWithSideIter.add(hiddenPort);
                        setSideOfPort(hiddenPort, portWithSide.getSide());
                        // ensure the next element will be our new added element
                        portsWithSideIter.previous();
                        portsWithSideIter.previous();
                        hiddenPorts.remove(hiddenPort);
                    }
                }
            }
        }
    }

    // The ports will get re-added to the node by an instance of the PortReAdder.
    // As the ports of a node are listed clockwise, starting in the left corner of the north side of the
    // node, we will add the ports exactly in this order. 
    // The order on each side of the node is as follows:
    //  - Corner loop components
    //  - Across loop components
    //  - Ports already laying on the node (i.e. ports with edges to other nodes)
    //    Every time in the following method there is a "find transition from..." comment, the iterator
    //    is moved behind such ports of the current nodeSide.
    //  - Straight side loop components 
    //  - Across loop components
    //  - Corner loop components
    /**
     * Positions the ports of the given components around their node in a crossing minimizing order.
     * The components on straight sides are places next to each other. 
     * All components in the given list must lay on the same node.
     * 
     * @param components The components those ports needs to be placed.
     */
    private void portLinedPositioning(final List<ConnectedSelfLoopComponent> components) {
        // Important to avoid nullPointer exceptions when reading the node.
        if (components.isEmpty()) {
            return;
        }
        // The node we are working on.
        final LNode node = components.get(0).getNode();
        // The PortReAdder we will use to re-add the ports.
        final PortReadder compHolder = new PortReadder(components);
        
        // An iterator over the portList of our node
        final ListIterator<LPort> itr = node.getPorts().listIterator();

        /////////////////////////////////////
        // Now we will re-add the ports to the portList of the node.
        
        // north-west, 1st part
        compHolder.addSourcePortsReversed(LoopSide.NW, itr);
        // south-north via west, 2nd part
        compHolder.addTargetPorts(LoopSide.SWN, itr);
        
        /////////////////////////////////////
        // find transition from north to east
        findNextSide(PortSide.NORTH, itr);
        // north
        compHolder.addInlineTargetsFirst(LoopSide.N, itr);
        // south-north via east, 2nd part
        compHolder.addTargetPorts(LoopSide.SEN, itr);
        // east-north
        compHolder.addAllPorts(LoopSide.EN, itr, false);
        // east-west via north, 1st part
        compHolder.addSourcePortsReversed(LoopSide.ENW, itr);
        
        /////////////////////////////////////
        // find transition from east to south
        findNextSide(PortSide.EAST, itr);
        // east
        compHolder.addInlineTargetsFirst(LoopSide.E, itr);
        // west-east via south, 1st part
        compHolder.addSourcePortsReversed(LoopSide.ESW, itr);
        // south-east
        compHolder.addAllPorts(LoopSide.SE, itr, false);
        // south-north via east, 1st part
        compHolder.addSourcePortsReversed(LoopSide.SEN, itr);

        /////////////////////////////////////
        // find transition from south to west
        findNextSide(PortSide.SOUTH, itr);
        // south
        compHolder.addInlineTargetsFirst(LoopSide.S, itr);
        // south-north via west, 1st part
        compHolder.addSourcePortsReversed(LoopSide.SWN, itr);
        // west-south
        compHolder.addAllPorts(LoopSide.WS, itr, false);
        // east-west via south, 2nd part
        compHolder.addTargetPorts(LoopSide.ESW, itr);

        /////////////////////////////////////
        // find transition from west to north (the very end of the list)
        while (itr.hasNext()) {
            itr.next();
        }
        // west
        compHolder.addInlineTargetsFirst(LoopSide.W, itr);
        // east-west via north, 2nd part
        compHolder.addTargetPorts(LoopSide.ENW, itr);
        // north-west, 2nd part
        compHolder.addTargetPorts(LoopSide.NW, itr);        
    }
    
    // For details on algorithm, check the pre-javaDoc comment on portLinedPositioning.
    // Only difference is the way of adding the straight LoopSide components. 
    /**
     * Positions the ports of the given components around their node in a crossing minimizing order.
     * The components on straight sides are places stacked. 
     * All components in the given list must lay on the same node.
     * 
     * @param components The components those ports needs to be placed.
     */
    private void portStackedPositioning(final List<ConnectedSelfLoopComponent> components) {
        if (components.isEmpty()) {
            return;
        }
        final LNode node = components.get(0).getNode();
        // The componentHolder, holding our components.
        final PortReadder compHolder = new PortReadder(components);
        
        // An iterator over the portList of our node
        final ListIterator<LPort> itr = node.getPorts().listIterator();

        /////////////////////////////////////
        // Now we will re-add the ports to the portList of the node, again.
        
        // north-west, 1st part
        compHolder.addSourcePortsReversed(LoopSide.NW, itr);

        // south-north via west, 2nd part
        compHolder.addTargetPorts(LoopSide.SWN, itr);
        
        /////////////////////////////////////
        // find transition from north to east
        findNextSide(PortSide.NORTH, itr);
        // north
        compHolder.addAllPorts(LoopSide.N, itr, false);
        // south-north via east, 2nd part
        compHolder.addTargetPorts(LoopSide.SEN, itr);
        // east-north
        compHolder.addAllPorts(LoopSide.EN, itr, false);
        // east-west via north, 1st part
        compHolder.addSourcePortsReversed(LoopSide.ENW, itr);
        
        /////////////////////////////////////
        // find transition from east to south
        findNextSide(PortSide.EAST, itr);
        // east
        compHolder.addAllPorts(LoopSide.E, itr, false);
        // west-east via south, 1st part
        compHolder.addSourcePortsReversed(LoopSide.ESW, itr);
        // south-east
        compHolder.addAllPorts(LoopSide.SE, itr, false);
        // south-north via east, 1st part
        compHolder.addSourcePortsReversed(LoopSide.SEN, itr);

        /////////////////////////////////////
        // find transition from south to west
        findNextSide(PortSide.SOUTH, itr);
        // south
        compHolder.addAllPorts(LoopSide.S, itr, false);
        // south-north via west, 1st part
        compHolder.addSourcePortsReversed(LoopSide.SWN, itr);
        // west-south
        compHolder.addAllPorts(LoopSide.WS, itr, false);
        // east-west via south, 2nd part
        compHolder.addTargetPorts(LoopSide.ESW, itr);

        /////////////////////////////////////
        // find transition from west to north (the very end of the list)
        while (itr.hasNext()) {
            itr.next();
        }
        // west
        compHolder.addAllPorts(LoopSide.W, itr, false);
        // east-west via north, 2nd part
        compHolder.addTargetPorts(LoopSide.ENW, itr);
        // north-west, 2nd part
        compHolder.addTargetPorts(LoopSide.NW, itr);
    }
    

    /**
     * Advances the iterator such that the next call to next() returns the next element not on the given 
     * PortSide.
     * 
     * @param startSide The {@link PortSide} we want to move over. 
     * @param itr The iterator to move.
     */
    private void findNextSide(final PortSide startSide, final ListIterator<LPort> itr) {
        PortSide currentSide = startSide;
        while (itr.hasNext() && currentSide.equals(startSide)) {
            currentSide = itr.next().getSide();
        }
        if (!currentSide.equals(startSide)) {
            itr.previous();
        }
    }

    /**
     * Sets the loopSide of all self-loops of the component individually,
     * depending on the side(s) already defined for at least one port in the component.
     * At least one portSide must be defined, otherwise this method will run infinitely.
     * 
     * @param component The connected component to process.
     * @return The LoopSide that is equal to the assigned portSides. Undefined, if there is not a
     *         common side for all loops.
     */
    private LoopSide setPortSideByConstraint(final ConnectedSelfLoopComponent component) {
        // Iterator over the edges of the component. 
        // The iterator is cycling, as there is no guarantee, that (at least) one port of the current
        // edge has a portSide defined. If both portSides of the current edge are UNDEFINED, we will
        // return to it later.
        final Iterator<LEdge> iter = Iterators.cycle(Lists.newArrayList(component.getEdges()));
        // LoopSides in this component will be collected here
        final EnumSet<LoopSide> loopSidesInComponent =  EnumSet.noneOf(LoopSide.class);
        
        while (iter.hasNext()) {
            final LEdge edge = iter.next();
            final PortSide sourceSide = edge.getSource().getSide();
            final PortSide targetSide = edge.getTarget().getSide();
            if (sourceSide == PortSide.UNDEFINED) {
                // source side is undefined
                if (targetSide != PortSide.UNDEFINED) {
                    // only targetSide is defined. Edge becomes a sideLoop on the side of target.
                    final LoopSide side = LoopSide.fromPortSides(targetSide);
                    edge.setProperty(InternalProperties.SPLINE_LOOPSIDE, side);
                    edge.getSource().setSide(targetSide);
                    loopSidesInComponent.add(side);
                    iter.remove();
                }
            } else {
                // source side is defined
                if (targetSide == PortSide.UNDEFINED) {
                    // only sourceSide is defined. Edge becomes a sideLoop on the side of source.
                    final LoopSide side = LoopSide.fromPortSides(sourceSide);
                    edge.setProperty(InternalProperties.SPLINE_LOOPSIDE, side);
                    edge.getTarget().setSide(sourceSide);
                    loopSidesInComponent.add(side);
                    iter.remove();
                } else {
                    // both sides are defined, set edge side resulting from the combination
                    final LoopSide side = LoopSide.fromPortSide(sourceSide, targetSide);
                    edge.setProperty(InternalProperties.SPLINE_LOOPSIDE, side);
                    loopSidesInComponent.add(side);
                    iter.remove();
                }
            }
        }
        
        // Now all edges have a LoopSide assigned.
        // Check if we can find a LoopSide for the whole component.
        LoopSide side;
        if (loopSidesInComponent.size() == 1) {
            side = loopSidesInComponent.iterator().next();
        } else {
            side = LoopSide.UNDEFINED;
        }
        
        component.setLoopSide(side, false);
        return side;
    }
    
    /**
     * Sets the portSide of the port as specified. Also calculates the x or y coordinate that can
     * be derived from the portSide.
     * 
     * @param port The port to work on.
     * @param side The portSide to assign.
     * @return The portSide that was assigned.
     */
    private static void setSideOfPort(final LPort port, final PortSide side) {
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
    
    /**
     * Sets the side of all given ports the the given portSide.
     * 
     * @param ports The ports those sides to be set.
     * @param side The portSide for all of the ports.
     */
    private static void setSideOfPorts(final Iterable<LPort> ports, final PortSide side) {
        for (final LPort port : ports) {
            setSideOfPort(port, side);
        }
    }
    
    
    /** 
     * Sets the port-side of all elements in the components list. The connectedComponents 
     * are spread around the node in a pattern resulting from the number of available port-sides and 
     * the number of loop-ports.
     * 
     * The node is includes as a parameter to point out, that all components must be on the same node.
     * The parameter could be dropped, as the information is included in each connected component.
     * 
     * @param components The connectedComponents to spread equally around the node.
     * @param node The node that the components will be places on.
     */
    private static void setPortSideSpreadEqually(final List<ConnectedSelfLoopComponent> components,
            final LNode node) {

        // The loopSideCalculator we will distribute the ports with.
        final DistributedLoopSidesCalculator loopSideCalculator = 
                new DistributedLoopSidesCalculator(node);
        
        // We don't want loops on sides with a non-loop edge.
        loopSideCalculator.removeOccupiedSides();

        // Distribute the components.
        loopSideCalculator.calculate(components);
        
    }

    /**
     * A {@link Comparator} for {@link ConnectedSelfLoopComponent}s based on the width of the center 
     * edge texts. The result is in reversed order, so the {@link ConnectedSelfLoopComponent} with the 
     * longest is assumed to be smaller.
     * 
     * @author tit
     * 
     */
    private static final class TextWidthComparator implements Comparator<ConnectedSelfLoopComponent> {

        /**
         * {@inheritDoc}
         */
        public int compare(
                final ConnectedSelfLoopComponent loop0, final ConnectedSelfLoopComponent loop1) {
            return Double.compare(loop1.getTextWidth(), loop0.getTextWidth());
        }
    }
    
    /**
     * A PortReAdder provides methods to re-add hidden ports of components to their node.
     * This is done in a sorted way, depening on the chosen method.
     *  
     * @author tit
     */
    private static final class PortReadder {
        /** A comparator to compare the text widths of the self-loops. */
        private final Comparator<? super ConnectedSelfLoopComponent> loopSorter = 
                new TextWidthComparator();

        // We have to re-add our ports from the connected components to the port list.
        // The position in the list is relevant!
        private static final Map<LoopSide, List<ConnectedSelfLoopComponent>> LISTS_OF_COMPONENTS = 
                new EnumMap<LoopSide, List<ConnectedSelfLoopComponent>>(LoopSide.class);
        static {
            for (LoopSide side : LoopSide.values()) {
                LISTS_OF_COMPONENTS.put(side, null);
            }
        }

        /** List of all connected components containing non self-loops. */
        private final List<ConnectedSelfLoopComponent> withNonSelfLoop = Lists.newArrayList();
        /** List of all hidden ports of the current connected component. */
        private final List<LPort> allHiddenPorts = Lists.newArrayList();
        
        /**
         * Constructs a new {@code PortReAdder} holding the given {@link ConnectedSelfLoopComponent}s 
         * given.
         * 
         * @param components The {@link ConnectedSelfLoopComponent}s to be part of this PortReAdder.
         */
        PortReadder(final List<ConnectedSelfLoopComponent> components) {
            // Initialize the mapping. As the mapping is static, this needs to be done on every new run.
            for (LoopSide side : LoopSide.values()) {
                LISTS_OF_COMPONENTS.put(side, new ArrayList<ConnectedSelfLoopComponent>());
            }

            // First: group the components according to their loopSide
            for (final ConnectedSelfLoopComponent component : components) {
                allHiddenPorts.addAll(component.getHidablePorts());
                
                if (component.getNonLoopPorts().isEmpty()) {
                    LISTS_OF_COMPONENTS.get(component.getLoopSide()).add(component);
                } else {
                    withNonSelfLoop.add(component);
                }
            }
            
            // Second: sort the collections of components 
            // (in revered order, biggest first)
            for (List<ConnectedSelfLoopComponent> list : LISTS_OF_COMPONENTS.values()) {
                Collections.sort(list, loopSorter);
            }
            // ports on the NW-border need to be reversed, again.
            Collections.reverse(LISTS_OF_COMPONENTS.get(LoopSide.NW));            
        }
        
        /**
         * Adds all target ports and than all source ports of the components of given {@link LoopSide} 
         * of this component-holder to the current position of the {@link ListIterator}.
         * The source ports will be added in reversed order.
         * 
         * @param loopSide The components of this {@link LoopSide} will be added. 
         * @param itr The {@link ListIterator} to add the ports into.
         */
        public void addAllPorts(final LoopSide loopSide, final ListIterator<LPort> itr, 
                final boolean sourceFirst) {

            final List<LPort> secondPart = Lists.newArrayList();
            PortSide secondPartSide = null;
            
            if (sourceFirst) {
                for (final ConnectedSelfLoopComponent component : LISTS_OF_COMPONENTS.get(loopSide)) {
                    for (final LPort port : component.getSourceLoopPorts()) {
                        itr.add(port);
                        setSideOfPort(port, loopSide.getSourceSide());
                    }
                    secondPart.addAll(component.getTargetLoopPorts());
                    secondPartSide = loopSide.getTargetSide();
                }
            } else {
                for (final ConnectedSelfLoopComponent component : LISTS_OF_COMPONENTS.get(loopSide)) {
                    for (final LPort port : component.getTargetLoopPorts()) {
                        itr.add(port);
                        setSideOfPort(port, loopSide.getTargetSide());
                    }
                    secondPart.addAll(component.getSourceLoopPorts());
                    secondPartSide = loopSide.getSourceSide();
                }
            }
                
            Collections.reverse(secondPart);
            setSideOfPorts(secondPart, secondPartSide);
            for (final LPort port : secondPart) {
                itr.add(port);
            }
        }

        /**
         * Adds all source ports of the components of given {@link LoopSide} of this component-holder
         * to the current position of the {@link ListIterator} in reversed order.
         * 
         * @param loopSide The components of this {@link LoopSide} will be added. 
         * @param itr The {@link ListIterator} to add the ports into.
         */
        public void addSourcePortsReversed(final LoopSide loopSide, final ListIterator<LPort> itr) {
            final List<LPort> sourcePorts = Lists.newArrayList();

            for (final ConnectedSelfLoopComponent component : LISTS_OF_COMPONENTS.get(loopSide)) {
                sourcePorts.addAll(component.getSourceLoopPorts());
            }

            Collections.reverse(sourcePorts);
            setSideOfPorts(sourcePorts, loopSide.getSourceSide());

            for (final LPort port : sourcePorts) {
                itr.add(port);
            }
        }
        
        /**
         * Adds all target ports of the components of given {@link LoopSide} of this component-holder
         * to the current position of the {@link ListIterator}.
         * 
         * @param loopSide The components of this {@link LoopSide} will be added. 
         * @param itr The {@link ListIterator} to add the ports into.
         */
        public void addTargetPorts(final LoopSide loopSide, final ListIterator<LPort> itr) {
            final PortSide portSide = loopSide.getTargetSide();
            for (final ConnectedSelfLoopComponent component : LISTS_OF_COMPONENTS.get(loopSide)) {
                for (final LPort port : component.getTargetLoopPorts()) {
                    itr.add(port);
                    setSideOfPort(port, portSide);
                }
            }
        }
        
        /**
         * Adds all ports of given {@link LoopSide} of this component-holder to the current 
         * position of the {@link ListIterator}. The ports are placed, so that source and target ports 
         * of an edge are placed next to each other. The source port of an edge will be places before
         * the target port of an edge.
         * 
         * FIXME: This method doesn't seem to be used anywhere. Remove?
         * 
         * @param loopSide The components of this {@link LoopSide} will be added. 
         * @param itr The {@link ListIterator} to add the ports into.
         */
        public void addInlineSourcesFirst(final LoopSide loopSide, final ListIterator<LPort> itr) {
            final List<LPort> firstPart = Lists.newArrayList();
            final List<LPort> secondPart = Lists.newArrayList();

            final Iterator<ConnectedSelfLoopComponent> compItr = 
                    LISTS_OF_COMPONENTS.get(loopSide).iterator();
            
            while (compItr.hasNext()) {
                ConnectedSelfLoopComponent component = compItr.next();
                firstPart.addAll(0, component.getTargetLoopPorts());
                firstPart.addAll(0, component.getSourceLoopPortsReversed());
                
                if (compItr.hasNext()) {
                    component = compItr.next();
                    secondPart.addAll(component.getSourceLoopPortsReversed());
                    secondPart.addAll(component.getTargetLoopPorts());
                }
            }
            
            setSideOfPorts(firstPart, loopSide.getSourceSide());
            setSideOfPorts(secondPart, loopSide.getTargetSide());
            
            for (final LPort port : firstPart) {
                itr.add(port);
            }
            for (final LPort port : secondPart) {
                itr.add(port);
            }
        }

        /**
         * Adds all ports of given {@link LoopSide} of this component-holder to the current 
         * position of the {@link ListIterator}. The ports are placed, so that source and target ports 
         * of an edge are placed next to each other. The target port of an edge will be places before
         * the source port of an edge. 
         * 
         * @param loopSide The components of this {@link LoopSide} will be added. 
         * @param itr The {@link ListIterator} to add the ports into.
         */
        public void addInlineTargetsFirst(final LoopSide loopSide, final ListIterator<LPort> itr) {
            final List<LPort> firstPart = Lists.newArrayList();
            final List<LPort> secondPart = Lists.newArrayList();

            final Iterator<ConnectedSelfLoopComponent> compItr = 
                    LISTS_OF_COMPONENTS.get(loopSide).iterator();
            
            while (compItr.hasNext()) {
                ConnectedSelfLoopComponent component = compItr.next();
                firstPart.addAll(0, component.getSourceLoopPorts());
                firstPart.addAll(0, component.getTargetLoopPortsReversed());
                
                if (compItr.hasNext()) {
                    component = compItr.next();
                    secondPart.addAll(component.getTargetLoopPortsReversed());
                    secondPart.addAll(component.getSourceLoopPorts());
                }
            }
            
            setSideOfPorts(firstPart, loopSide.getTargetSide());
            setSideOfPorts(secondPart, loopSide.getSourceSide());

            for (final LPort port : firstPart) {
                itr.add(port);
            }
            for (final LPort port : secondPart) {
                itr.add(port);
            }
        }
    }
    
    /**
     * This class represents a calculator for loop-sides. If you don't want to have loops on sides where
     * already a non-loop edge is connected to, you should call the {@code removeOccupiedSides} method
     * first. After that you can let the DistributedLoopSidesCalculator assign port sides for a set of 
     * ConnectedSelfLoop with the {@code calculate} method. 
     * 
     * @author tit
     */
    private static class DistributedLoopSidesCalculator {
        /** Components with a text length bigger than this value will be placed as across loops. */
        private static final int MAX_TEXT_LENGTH = 50;
        
        /** We need a SortedLoopSides to sort the loopSides. */
        private final SortedLoopSides loopSides; 
        /** The node that this calculator will work on. */
        private final LNode node;
        
        /**
         * Creates a new LoopSideCalculator and initializes all sizes with 0.
         * 
         * @param node The node this LoopSideCalculator is created for.
         */
        public DistributedLoopSidesCalculator(final LNode node) {
            loopSides  = new SortedLoopSides();
            this.node = node;
        }
        
        /**
         * Removes all sides from the list of available sides, that already have an port places on it.
         * Use to make sure no loops will be places on sides with ports containing non-loop edges. 
         */
        public void removeOccupiedSides() {
            for (final LPort port : node.getPorts()) {
                loopSides.removeSide(LoopSide.fromPortSides(port.getSide()));
            }
        }

        /**  
         * Distributes the self-loops equally around the node.
         * 
         * @param components
         */
        public void calculate(final List<ConnectedSelfLoopComponent> components) {
            final List<ConnectedSelfLoopComponent> withLongText = Lists.newArrayList();
            final List<ConnectedSelfLoopComponent> withShortText = Lists.newArrayList();
            final List<ConnectedSelfLoopComponent> withoutText = Lists.newArrayList();
            
            // first we are going to check for the size of possible edge labels
            for (final ConnectedSelfLoopComponent component : components) {
                if (component.getTextWidth() > MAX_TEXT_LENGTH) {
                    withLongText.add(component);
                } else if (component.getTextWidth() > 0.0) {
                    withShortText.add(component);
                } else {
                    withoutText.add(component);
                }
            }
            
            // if there is only one loop with text, handle this loop as a long text loop
            if (withShortText.size() == 1 && withLongText.isEmpty()) {
                withLongText.addAll(withShortText);
                withShortText.clear();
            }

            // try to put the components with long text to one of the horizontal across loop sides.
            // if there are no such sides available, handle long text loops as short text loops.
            if (!withLongText.isEmpty() 
                    && loopSides.availableSides().contains(LoopSide.N) 
                    && loopSides.availableSides().contains(LoopSide.S)) {
                assignAcrossSide(withLongText);
            } else {
                withShortText.addAll(withLongText);
            }
            
            // put the components with short text to one of the corner loop sides
            if (!withShortText.isEmpty()) {
                assignCornerSide(withShortText);
            }

            // Last but not least: loops without text get spread equally.
            if (!withoutText.isEmpty()) {
                // ////////////////////////////////////////////////////////////
                // First of all we try to put those ConnectedSelfLoops who have more than one edge,
                // to one of the available straight sides.
                // ////////////////////////////////////////////////////////////

                final Set<LoopSide> availableStraights = loopSides.availableStraightSides();
                if (!availableStraights.isEmpty()) {
                    final Iterator<ConnectedSelfLoopComponent> itrComponent = withoutText.iterator();
                    final Iterator<LoopSide> itrAvailable =
                            Iterables.cycle(availableStraights).iterator();

                    while (itrComponent.hasNext()) {

                        // look for a component with more than one edge
                        ConnectedSelfLoopComponent component = itrComponent.next();
                        while (itrComponent.hasNext() && component.getEdges().size() < 2) {
                            component = itrComponent.next();
                        }

                        // If we have found a component with more than one edge, assign the next
                        // straight side to the component.
                        if (component.getEdges().size() > 1) {
                            final LoopSide side = itrAvailable.next();
                            component.setLoopSide(side, true);
                            itrComponent.remove();
                            loopSides.removeSide(side);
                        }
                    }
                }

                // ////////////////////////////////////////////////////////////
                // Now we can distribute the remaining components around the node.
                // ////////////////////////////////////////////////////////////

                // number of required sides
                final int number = withoutText.size();

                // Center of the assigned pattern.
                final LoopSide center = findCenter();

                // The list of portSides we assign to the set of connected components.
                final List<LoopSide> portSides = Lists.newArrayList();

                // How many times must a "full set" of LoopSide-elements be constructed?
                final int fullSets = number / loopSides.availableNotAcrossSides().size();

                // Construct the full sets.
                for (int i = 0; i < fullSets; i++) {
                    portSides.addAll(loopSides.availableNotAcrossSides());
                }

                // How many elements are remaining to be constructed after the full sets have been
                // constructed?
                int remainer = number % loopSides.availableNotAcrossSides().size();

                // If more than three elements are remaining, the pattern includes all four corners
                // and the sides that would be included in the pattern for (n-4) elements.
                if (remainer > 3) {
                    portSides.addAll(LoopSide.getAllCornerSides());
                    remainer -= 4;
                }

                // Create the pattern for 1-3 elements.
                switch (remainer) {
                case 3:
                    // opposed side (from center) and the the same sides as for two elements
                    portSides.add(center.opposite());
                case 2:
                    // the first AVAILABLE neighbors of the neighbors of the opposed side of the
                    // center
                    LoopSide tmpSide = center.opposite().left();
                    do {
                        tmpSide = tmpSide.left();
                    } while (!loopSides.availableSides().contains(tmpSide));
                    portSides.add(tmpSide);

                    tmpSide = center.opposite().right();
                    do {
                        tmpSide = tmpSide.right();
                    } while (!loopSides.availableSides().contains(tmpSide));
                    portSides.add(tmpSide);
                    break;
                case 1:
                    // opposed side (from center)
                    portSides.add(center.opposite());
                    break;
                default:
                    break;
                }

                // Now assign the remaining sides to the set of portSides we just have constructed.
                final Iterator<LoopSide> itrSides = portSides.iterator();
                final Iterator<ConnectedSelfLoopComponent> itrComponent = withoutText.iterator();
                while (itrSides.hasNext() && itrComponent.hasNext()) {
                    itrComponent.next().setLoopSide(itrSides.next(), true);
                }
            }
        }
            

        /**
         * Assignes the least crowded across loopSide to the given components.
         * The crowding will be recalculated after each added edge.
         *  
         * @param components The components to assign a loopSide for.
         */
        private void assignAcrossSide(final List<ConnectedSelfLoopComponent> components) {
            for (final ConnectedSelfLoopComponent component : components) {
                final LoopSide side = loopSides.getLeastCrowdedHorizontalCrossing();
                loopSides.addSize(side, component.getTextWidth(), component.getTextHeight());
                component.setLoopSide(side, true);
            }
        }
        
        /**
         * Assignes the least crowded corner loopSide to the given components.
         * The crowding will be recalculated after each added edge.
         *  
         * @param components The components to assign a loopSide for.
         */
        private void assignCornerSide(final List<ConnectedSelfLoopComponent> components) {
            for (final ConnectedSelfLoopComponent component : components) {
                final LoopSide side = loopSides.getLeastCrowdedCorner();
                loopSides.addSize(side, component.getTextWidth(), component.getTextHeight());
                component.setLoopSide(side, true);
            }
        }

        /**
         * Finds the "center" of the setup of currently available sides.
         * 
         * @return The side considered as "center" for current setup of available port-sides.
         */
        private LoopSide findCenter() {
            final EnumSet<LoopSide> retVal;
            Iterator<LoopSide> itr;
            switch (loopSides.availableStraightSides().size()) {
            case 4:
                // all sides available: south is the center.
                return LoopSide.S;
            case 3:
                // only one side not available: the side not available is the center.
                return loopSides.allRemovedStraightSides().iterator().next();
            case 2:
                // Two sides were removed, two sides remain available. The center side depends on
                // the pattern of the removed/available sides:
                retVal = loopSides.availableStraightSides();
                itr = retVal.iterator();
                LoopSide first = itr.next();
                LoopSide second = itr.next();

                if (first.opposite() == second) {
                    // the removed sides are opposed to each other.
                    if (retVal.contains(LoopSide.S)) {
                        // n-s sides were removed. Center is east.
                        return LoopSide.E;
                    } else {
                        // w-e sides were removed. center is south.
                        return LoopSide.S;
                    }
                } else {
                    // the removed sides were neighbors. The center is between them.
                    if (first.left().left() == second) {
                        // first element is right of second element.
                        return first.left();
                    } else {
                        // first element is left of second element
                        return first.right();
                    }
                }
            case 1:
                // only one straight side is remaining. The opposite of this side is the center.
                retVal = loopSides.availableStraightSides();
                return retVal.iterator().next().opposite();
            case 0:
                // all straight sides were removed. The center is the s-e corner.
                return LoopSide.SE;
            default:
                return null;
            }
        }
        
        /**
         * This class holds a set of {@link LoopSide}s and a height and width for each of them.
         * There are methods to increase the height and width, to remove a {@link LoopSide} form the 
         * list and to return {@link LoopSide}s best fitting for specific constraints.
         * 
         * @author tit
         */
        private static final class SortedLoopSides {
            /** Differences below this value are treated as zero. */
            private static final double EPSILON = 0.00000001;
            /** Mapping from the LoopSides to the current occupancy of that LoopSide of current node.
             * Pair: firstVal is width, secondVal is height. */ 
            private final Map<LoopSide, SizeOfSide> sizeMap = Maps.newLinkedHashMap();
            /** True if a LoopSide was removed or changed (SizeOfSide) since last call of 
             * updateAvailableSides. */
            private boolean sideRemovedOrChanged = true;
            // Three lists to improve the performance.
            /** List of all LoopSides that are available (i.e. not blocked by a non-loop edge). */
            private final EnumSet<LoopSide> availableSides = EnumSet.noneOf(LoopSide.class);
            /** List of all LoopSides that are available (i.e. not blocked by a non-loop edge) and that
             * are straight LoopSides. */
            private final EnumSet<LoopSide> availableStraightSides = EnumSet.noneOf(LoopSide.class);
            /** List of all LoopSides that are available (i.e. not blocked by a non-loop edge) and that
             * are not across LoopSides (i.e. straight or corner). */
            private final EnumSet<LoopSide> availableNotAcrossSides = EnumSet.noneOf(LoopSide.class);
            
            public SortedLoopSides() {
                for (final LoopSide side : LoopSide.getAllDefinedSides()) {
                    sizeMap.put(side, new SizeOfSide(0.0, 0.0));
                }
            }
            
            /**
             * Updates the height and width of a loopSide.
             *  
             * @param side The loopSide to update.
             * @param addedWidth The width to add.
             * @param addedHeight The height to add.
             */
            private void addSize(final LoopSide side, 
                    final double addedWidth, final double addedHeight) {
                final SizeOfSide size = sizeMap.get(side);
                size.addWidth(addedWidth);
                size.addHeight(addedHeight);
                sizeMap.put(side, size);
                sideRemovedOrChanged = true;
            }
            
            /**
             * Returns the least crowded corner loopSide available or {@code null}.
             * A loopSide is assumed to be the least crowded, if it's height is the smallest of all 
             * loopSides. If two loopSides have the same height, the less wider one is assumed to be less
             * crowded.
             * 
             * @return The least crowded corner loopSide or {@code null}.
             */
            private LoopSide getLeastCrowdedCorner() {
                double minHeight = Double.MAX_VALUE;
                double minWidth = Double.MAX_VALUE;
                LoopSide minSide = null;

                for (final Entry<LoopSide, SizeOfSide> entry : sizeMap.entrySet()) {
                    if (entry.getKey().isCorner()) {
                        double height = entry.getValue().getHeight();
                        double width =  entry.getValue().getWidth();
                        boolean isLessHigh = minHeight - height > EPSILON;
                        boolean isNotHigherAndLessWide = 
                                height - minHeight < EPSILON && minWidth - width > EPSILON;
                        
                        if (isLessHigh || isNotHigherAndLessWide) {
                            minWidth = entry.getValue().getWidth();
                            minHeight = entry.getValue().getHeight();
                            minSide = entry.getKey();
                            // values cannot get smaller
                            if (minWidth == 0.0 && minHeight == 0.0) {
                                return minSide;
                            }
                        }
                    }
                }
                return minSide;
            }

            /**
             * Returns the least crowded horizontal across loopSide available or {@code null}.
             * A loopSide is assumed to be the least crowded, if it's height is the smallest of all 
             * loopSides. If two loopSides have the same height, the less wider one is assumed to be less
             * crowded.
             * 
             * @return The least crowded horizontal across loopSide or {@code null}.
             */
            private LoopSide getLeastCrowdedHorizontalCrossing() {
                double minHeight = Double.MAX_VALUE;
                double minWidth = Double.MAX_VALUE;
                LoopSide minSide = null;

                for (final Entry<LoopSide, SizeOfSide> entry : sizeMap.entrySet()) {
                    if (entry.getKey() == LoopSide.ENW || entry.getKey() == LoopSide.ESW) {
                        double height = entry.getValue().getHeight();
                        double width = entry.getValue().getWidth();
                        boolean isLessHigh = minHeight - height > EPSILON;
                        boolean isNotHigherAndLessWide =
                                height - minHeight < EPSILON && minWidth - width > EPSILON;

                        if (isLessHigh || isNotHigherAndLessWide) {
                            minWidth = entry.getValue().getWidth();
                            minHeight = entry.getValue().getHeight();
                            minSide = entry.getKey();
                            // values cannot get smaller
                            if (minWidth == 0.0 && minHeight == 0.0) {
                                return minSide;
                            }
                        }
                    }
                }
                return minSide;
            }
            
            /**
             * Removes the side from the list of available loopSides. Only straight sides may be removed.
             * 
             * @param side The loopSide to remove.
             */
            private void removeSide(final LoopSide side) {
                final SizeOfSide wasRemoved = sizeMap.remove(side);
                if (wasRemoved != null) {
                    sideRemovedOrChanged = true;
                }
            }

            /**
             * @return A collection of currently available side loopSides.
             */
            private EnumSet<LoopSide> availableStraightSides() {
                if (sideRemovedOrChanged) {
                    updateAvailableSides();
                }
                return availableStraightSides;
            }

            /**
             * @return A collection of currently available straight loopSides.
             */
            private EnumSet<LoopSide> availableSides() {
                if (sideRemovedOrChanged) {
                    updateAvailableSides();
                }
                return availableSides;
            }
            
            /**
             *  
             * @return A collection of all available side and corner loopSides.
             */
            private EnumSet<LoopSide> availableNotAcrossSides() {
                if (sideRemovedOrChanged) {
                    updateAvailableSides();
                }
                return availableNotAcrossSides;
            }
            
            /**
             * Updates the performance lists.
             */
            private void updateAvailableSides() {
                availableStraightSides.clear();
                availableSides.clear();
                availableNotAcrossSides.clear();
                
                for (final LoopSide side : sizeMap.keySet()) {
                    if (!side.isAcross()) {
                        availableNotAcrossSides.add(side);
                        if (side.isStraight()) {
                            availableStraightSides.add(side);
                        }
                    }
                    availableSides.add(side);
                }
                sideRemovedOrChanged = false;
            }
            
            /**
             * @return A {@link List} of all removed side loopSides.
             */
            private Set<LoopSide> allRemovedStraightSides() {
                final Set<LoopSide> retVal = LoopSide.getAllStraightSides();
                retVal.removeAll(availableStraightSides());
                return retVal;
            }
            
            //TODO: kommentar
            /**
             * @author tit
             */
            private static class SizeOfSide {
                private double width;
                private double height;
                
                SizeOfSide(final double width, final double height) {
                    this.width = width;
                    this.height = height;
                }
                
                void addWidth(final double w) {
                    this.width += w;
                }

                double getWidth() {
                    return width;
                }

                void addHeight(final double h) {
                    this.height += h;
                }

                double getHeight() {
                    return height;
                }
            }
        }
    }
}
