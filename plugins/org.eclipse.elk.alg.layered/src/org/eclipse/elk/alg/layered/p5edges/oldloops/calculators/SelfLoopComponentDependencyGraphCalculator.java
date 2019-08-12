/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.calculators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNodeSide;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopRoutingDirection;
import org.eclipse.elk.core.options.PortSide;

/**
 *
 */
public final class SelfLoopComponentDependencyGraphCalculator {

    /**
     * No instantiation.
     */
    private SelfLoopComponentDependencyGraphCalculator() {
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Component Dependencies

    /**
     * Calculate all dependencies for the given node and set each node site's component dependencies accordingly.
     */
    public static void calculateComponentDependecies(final OldSelfLoopNode nodeRep) {
        for (OldSelfLoopNodeSide nodeSide : nodeRep.getSides()) {
            List<OldSelfLoopPort> sidePorts = nodeSide.getPorts();

            // Filter ports which have no connected edges
            sidePorts = sidePorts.stream()
                    .filter(port -> (!port.getLPort().getOutgoingEdges().isEmpty()
                            || !port.getLPort().getIncomingEdges().isEmpty())
                            || port.getComponent().getPorts().size() != 1)
                    .collect(Collectors.toList());
            
            // Calculate the dependencies
            Set<OldSelfLoopComponent> dependencies =
                    getDependencyComponents(nodeSide, sidePorts, sidePorts, new HashSet<OldSelfLoopPort>());
            
            nodeSide.getComponentDependencies().clear();
            nodeSide.getComponentDependencies().addAll(dependencies);
        }
    }

    /**
     * Calculate dependencies between the given ports on the given side.
     * 
     * @param side
     *            the side to calculate dependencies for.
     * @param sidePorts
     *            ports on that side.
     * @param relevantPorts
     *            ports that may still want to look at (this list shrinks as the method calls itself recursively)
     * @param visitedPorts
     *            ports that have already been visited (pass an empty set on the initial call)
     */
    private static Set<OldSelfLoopComponent> getDependencyComponents(final OldSelfLoopNodeSide side,
            final List<OldSelfLoopPort> sidePorts, final List<OldSelfLoopPort> relevantPorts,
            final Set<OldSelfLoopPort> visitedPorts) {

        Set<OldSelfLoopComponent> dependencyComponents = new HashSet<OldSelfLoopComponent>();

        // iterate all ports
        for (int i = 0; i < relevantPorts.size(); i++) {
            // get the port, the component and the current dependencies
            OldSelfLoopPort port = relevantPorts.get(i);
            OldSelfLoopComponent component = port.getComponent();
            Map<OldSelfLoopNodeSide, List<OldSelfLoopComponent>> dependenciesOfSide = component.getDependencyComponents();

            // only process port if not already visited
            if (!visitedPorts.contains(port)) {
                OldSelfLoopRoutingDirection direction = port.getDirection();
                OldSelfLoopPort lastPortOfComponent = getLastPortOnSide(side, component);
                int lastPortIndex = i;

                if (direction == null) {
                    continue;
                }
                
                // look up what kind of port is given
                switch (direction) {
                case LEFT:
                    // has to be left open, otherwise it was already visited
                    // therefore all earlier dependencies belong under the new component
                    dependenciesOfSide.put(side, new ArrayList<OldSelfLoopComponent>(dependencyComponents));
                    dependencyComponents.removeAll(dependencyComponents);
                    dependencyComponents.add(component);
                    break;

                case RIGHT:
                    // set components ports to be visited
                    List<OldSelfLoopPort> portsOfSide = component.getPortsOfSide(side.getSide());
                    visitedPorts.addAll(portsOfSide);

                    // has to be highest component otherwise it would have been visited earlier
                    dependencyComponents.add(component);
                    // all components wrapped are dependencies.

                    if (lastPortOfComponent != null && lastPortOfComponent != port) {
                        lastPortIndex = sidePorts.indexOf(lastPortOfComponent);
                    } else if (lastPortOfComponent == port) {
                        lastPortIndex = sidePorts.size();
                    }
                    
                    List<OldSelfLoopPort> wrappedPorts = sidePorts.subList(i + 1, lastPortIndex);
                    Set<OldSelfLoopComponent> rightSideOpenDependencies =
                            getDependencyComponents(side, sidePorts, wrappedPorts, visitedPorts);
                    dependenciesOfSide.put(side, new ArrayList<OldSelfLoopComponent>(rightSideOpenDependencies));
                    break;
                    
                case BOTH:
                    // ignore the port if another port of this component is following on this side
                    if (lastPortOfComponent != port) {
                        visitedPorts.add(port);
                        continue;
                    }

                    // only the case where this port is the only one on this side can occur
                    ArrayList<OldSelfLoopComponent> dependencies = new ArrayList<OldSelfLoopComponent>(dependencyComponents);
                    dependencyComponents.removeAll(dependencyComponents);
                    dependencyComponents.add(component);

                    if (lastPortOfComponent != null && lastPortOfComponent != port) {
                        lastPortIndex = sidePorts.indexOf(lastPortOfComponent);
                    } else if (lastPortOfComponent == port) {
                        lastPortIndex = sidePorts.size();
                    }
                    
                    Set<OldSelfLoopComponent> rightSideOpenDependencies2 = getDependencyComponents(side, sidePorts,
                            sidePorts.subList(i + 1, lastPortIndex), visitedPorts);
                    dependencies.addAll(new ArrayList<OldSelfLoopComponent>(rightSideOpenDependencies2));
                    dependenciesOfSide.put(side, dependencies);
                    break;
                }
                
                // set components ports to be visited
                List<OldSelfLoopPort> portsOfSide = component.getPortsOfSide(side.getSide());
                visitedPorts.addAll(portsOfSide);
            }
        }
        
        return dependencyComponents;
    }

    /**
     * Get the last of the given component's ports placed on the given side.
     */
    private static OldSelfLoopPort getLastPortOnSide(final OldSelfLoopNodeSide side, final OldSelfLoopComponent component) {
        OldSelfLoopPort lastPort = null;
        List<OldSelfLoopPort> ports = side.getPorts();
        
        for (int i = ports.size() - 1; i >= 0; i--) {
            OldSelfLoopPort currentPort = ports.get(i);
            if (currentPort.getComponent() == component) {
                lastPort = currentPort;
                break;
            }
        }
        
        return lastPort;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Edge Dependencies

    /**
     * Calculates edge dependencies for all components in the list and stores them in the components.
     */
    public static void calculateEdgeDependecies(final List<OldSelfLoopComponent> components) {
        for (OldSelfLoopComponent component : components) {
            // iterate the sides
            int numberOfPortSides = PortSide.values().length - 1;
            List<OldSelfLoopPort> ports = component.getPorts();
            PortSide currentSide = ports.get(0).getPortSide();

            for (int i = 0; i < numberOfPortSides; i++) {
                List<OldSelfLoopPort> sidePorts = component.getPortsOfSide(currentSide);
                
                // filter non-loop components
                if (ports.size() > 1) {
                    List<OldSelfLoopEdge> dependencies = calculateEdgeOrder(
                            ports, sidePorts, new HashSet<OldSelfLoopEdge>(), currentSide, false);
                    component.getEdgeDependencies().put(currentSide, dependencies);
                } else {
                    component.getEdgeDependencies().put(currentSide, new ArrayList<OldSelfLoopEdge>());
                }
                
                // update the side
                currentSide = currentSide.right();
            }
        }
    }

    /**
     * TODO: Document.
     */
    private static List<OldSelfLoopEdge> calculateEdgeOrder(final List<OldSelfLoopPort> allPorts,
            final List<OldSelfLoopPort> portsToVisit, final Set<OldSelfLoopEdge> visitedEdges, final PortSide side,
            final boolean ignoreLeftPointing) {

        List<OldSelfLoopEdge> dependencyEdges = new ArrayList<OldSelfLoopEdge>();
        List<OldSelfLoopEdge> previousEdges = new ArrayList<OldSelfLoopEdge>();

        for (int i = 0; i < portsToVisit.size(); i++) {
            // keep track of visited edges, to process each one only once
            OldSelfLoopPort port = portsToVisit.get(i);
            List<OldSelfLoopEdge> portEdges = new ArrayList<>(port.getConnectedEdges());
            portEdges.removeAll(visitedEdges);

            if (portEdges.isEmpty()) { // if no edges are left, there is nothing to do
                continue;
                
            } else if (portEdges.size() == 1) {
                OldSelfLoopEdge onlyPortEdge = portEdges.get(0);
                visitedEdges.add(onlyPortEdge);

                OldSelfLoopPort targetPort = oppositePort(port, onlyPortEdge);
                int targetIndexAllPorts = allPorts.indexOf(targetPort);
                int targetIndexSidePorts = portsToVisit.indexOf(targetPort);
                int portIndex = allPorts.indexOf(port);
                List<OldSelfLoopPort> nextPortsToVisit = new ArrayList<OldSelfLoopPort>();

                if (targetPort.getPortSide() != side && targetIndexAllPorts < portIndex) {
                    // left-pointing
                    if (!ignoreLeftPointing) {
                        addEdgeDependencyToEdge(onlyPortEdge, new ArrayList<OldSelfLoopEdge>(dependencyEdges), side);
                        dependencyEdges.clear();
                        dependencyEdges.add(onlyPortEdge);
                        i--;
                    }
                } else if (targetPort.getPortSide() != side) {
                    nextPortsToVisit = portsToVisit.subList(i, portsToVisit.size() - 1);
                    previousEdges = calculateEdgeOrder(allPorts, nextPortsToVisit, visitedEdges, side, true);
                    addEdgeDependencyToEdge(onlyPortEdge, previousEdges, side);
                    dependencyEdges.add(onlyPortEdge);
                } else if (targetIndexAllPorts == portIndex + 1) {
                    dependencyEdges.add(onlyPortEdge);
                } else {
                    nextPortsToVisit = portsToVisit.subList(i, targetIndexSidePorts);
                    previousEdges = calculateEdgeOrder(allPorts, nextPortsToVisit, visitedEdges, side, true);
                    addEdgeDependencyToEdge(onlyPortEdge, previousEdges, side);
                    dependencyEdges.add(onlyPortEdge);
                }
                
            } else {
                // there are multiple edges connected with one port
                List<OldSelfLoopEdge> leftPointingEdges = getLeftwardPointingEdges(port, visitedEdges, allPorts);

                if (leftPointingEdges.isEmpty()) {
                    // the edges are all right pointing

                    // sort connected ports by
                    portEdges.sort((OldSelfLoopEdge edge1, OldSelfLoopEdge edge2) -> {
                        OldSelfLoopPort targetPort1 = oppositePort(port, edge1);
                        OldSelfLoopPort targetPort2 = oppositePort(port, edge2);
                        int index1 = allPorts.indexOf(targetPort1);
                        int index2 = allPorts.indexOf(targetPort2);
                        return -1 * Integer.compare(index1, index2);
                    });
                    
                    // outermost edge is marked as visited
                    OldSelfLoopEdge outermostEdge = portEdges.get(0);
                    visitedEdges.add(outermostEdge);
                    dependencyEdges.add(outermostEdge);

                    OldSelfLoopPort otherPort = oppositePort(port, outermostEdge);

                    List<OldSelfLoopPort> nextPortsToVisit = new ArrayList<OldSelfLoopPort>();
                    if (otherPort.getPortSide() == side) {
                        // the next port is on the same side
                        List<OldSelfLoopEdge> hasOtherPortsConnected = getLeftwardPointingEdges(
                                otherPort, visitedEdges, allPorts);

                        // the list remains untouched if other edges might need the targetPort of the outermost edge
                        int otherEdgeIndex = portsToVisit.indexOf(otherPort);
                        nextPortsToVisit = hasOtherPortsConnected.isEmpty()
                                ? portsToVisit.subList(0, otherEdgeIndex)
                                : portsToVisit.subList(0, otherEdgeIndex + 1);
                    } else {
                        nextPortsToVisit = portsToVisit;
                    }

                    previousEdges = calculateEdgeOrder(allPorts, nextPortsToVisit, visitedEdges, side, false);
                    addEdgeDependencyToEdge(outermostEdge, previousEdges, side);
                
                } else {
                    if (!ignoreLeftPointing) {

                        portEdges.sort((OldSelfLoopEdge edge1, OldSelfLoopEdge edge2) -> {
                            OldSelfLoopPort targetPort1 = oppositePort(port, edge1);
                            OldSelfLoopPort targetPort2 = oppositePort(port, edge2);
                            int index1 = allPorts.indexOf(targetPort1);
                            int index2 = allPorts.indexOf(targetPort2);
                            return Integer.compare(index1, index2);
                        });
                        OldSelfLoopEdge innermostLeftPointingEdge = portEdges.get(0);
                        visitedEdges.add(innermostLeftPointingEdge);
                        addEdgeDependencyToEdge(innermostLeftPointingEdge, new ArrayList<>(dependencyEdges), side);
                        dependencyEdges.clear();
                        dependencyEdges.add(innermostLeftPointingEdge);
                        i--;
                    }
                }
            }
        }
        
        return dependencyEdges;
    }

    /**
     * Returns all edges incident ot the given port that connect it to ports to its left.
     */
    private static List<OldSelfLoopEdge> getLeftwardPointingEdges(final OldSelfLoopPort port,
            final Set<OldSelfLoopEdge> visitedEdges, final List<OldSelfLoopPort> allPorts) {
        
        List<OldSelfLoopEdge> connectedEdges = port.getConnectedEdges();
        connectedEdges.removeAll(visitedEdges);
        int portIndex = allPorts.indexOf(port);
        Set<OldSelfLoopPort> perviousPorts = new HashSet<>(allPorts.subList(0, portIndex));

        List<OldSelfLoopEdge> leftwardEdges = new ArrayList<OldSelfLoopEdge>();

        for (OldSelfLoopEdge edge : connectedEdges) {
            OldSelfLoopPort otherEndPort = oppositePort(port, edge);
            if (perviousPorts.contains(otherEndPort)) {
                leftwardEdges.add(edge);
            }
        }
        return leftwardEdges;
    }

    /**
     * Stores the given dependencies on the given side in the given edge.
     */
    private static void addEdgeDependencyToEdge(final OldSelfLoopEdge edge, final List<OldSelfLoopEdge> newDependencies,
            final PortSide side) {
        
        Map<PortSide, List<OldSelfLoopEdge>> dependencyMap = edge.getDependencyEdges();
        List<OldSelfLoopEdge> sideDependencies = dependencyMap.get(side);
        if (sideDependencies == null) {
            dependencyMap.put(side, newDependencies);
        } else {
            sideDependencies.addAll(newDependencies);
        }

    }

    /**
     * Returns the port the given edge connects the given port to.
     */
    private static OldSelfLoopPort oppositePort(final OldSelfLoopPort port, final OldSelfLoopEdge edge) {
        assert edge.getSource() == port || edge.getTarget() == port;
        
        if (port == edge.getSource()) {
            return edge.getTarget();
        } else {
            return edge.getSource();
        }
    }
}
