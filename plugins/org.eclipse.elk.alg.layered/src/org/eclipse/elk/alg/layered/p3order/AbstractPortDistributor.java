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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.alg.layered.properties.PortType;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Lists;

/**
 * Calculates port ranks and distributes ports.
 * The <em>rank</em> of a port is a floating point number that represents its position
 * inside the containing layer. This depends on the node order of that layer and on the
 * port constraints of the nodes. Port ranks are used by {@link ICrossingMinimizationHeuristic}s
 * for calculating barycenter or median values for nodes. Furthermore, they are used in this
 * class for distributing the ports of nodes where the order of ports is not fixed,
 * which has to be done as the last step of each crossing minimization processor.
 * There are different ways to determine port ranks, therefore that is done in concrete subclasses.
 * 
 * @author cds
 * @author ima
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public abstract class AbstractPortDistributor {

    /** port ranks array in which the results of ranks calculation are stored. */
    private float[] portRanks;

    /**
     * Constructs a port distributor for the given array of port ranks.
     * All ports are required to be assigned ids in the range of the given array.
     * 
     * @param portRanks
     *            The array of port ranks
     */
    public AbstractPortDistributor(final float[] portRanks) {
        this.portRanks = portRanks;
    }
    
    /**
     * Returns the array of port ranks.
     * 
     * @return the array of port ranks
     */
    public float[] getPortRanks() {
        return portRanks;
    }

    
    // /////////////////////////////////////////////////////////////////////////////
    // Port Rank Assignment
    
    /**
     * Assign port ranks for the input or output ports of the given node. If the node's port
     * constraints imply a fixed order, the ports are assumed to be pre-ordered in the usual way,
     * i.e. in clockwise order north - east - south - west.
     * The ranks are written to the {@link #getPortRanks()} array.
     * 
     * @param node
     *            a node
     * @param rankSum
     *            the sum of ranks of preceding nodes in the same layer
     * @param type
     *            the port type to consider
     * @return the rank consumed by the given node; the following node's ranks start at
     *            {@code rankSum + consumedRank}
     * @see org.eclipse.elk.alg.layered.intermediate.PortListSorter
     */
    protected abstract float calculatePortRanks(final LNode node, final float rankSum,
            final PortType type);
    
    /**
     * Determine ranks for all ports of specific type in the given layer.
     * The ranks are written to the {@link #getPortRanks()} array.
     * 
     * @param layer
     *            a layer as node array
     * @param portType
     *            the port type to consider
     */
    public final void calculatePortRanks(final LNode[] layer, final PortType portType) {
        float consumedRank = 0;
        for (int nodeIx = 0; nodeIx < layer.length; nodeIx++) {
            consumedRank += calculatePortRanks(layer[nodeIx], consumedRank, portType);
        }
    }
    
    // /////////////////////////////////////////////////////////////////////////////
    // Port Distribution

    /**
     * Distribute the ports of each node in the layered graph depending on the port constraints.
     * 
     * @param layeredGraph
     *            a layered graph as node array
     */
    public final void distributePorts(final LNode[][] layeredGraph) {
        for (int l = 0; l < layeredGraph.length; l++) {
            if (l + 1 < layeredGraph.length) {
                // update the input port ranks of the next layer
                calculatePortRanks(layeredGraph[l + 1], PortType.INPUT);
            }
            LNode[] layer = layeredGraph[l];
            float consumedRank = 0;
            for (int i = 0; i < layer.length; i++) {
                // reorder the ports of the current node
                distributePorts(layer[i]);
                // update the output port ranks after reordering
                consumedRank += calculatePortRanks(layer[i], consumedRank, PortType.OUTPUT);
            }
        }
    }
    
    /**
     * Distribute the ports of the given node by their sides, connected ports, and input or output
     * type.
     * 
     * @param node node whose ports shall be sorted
     */
    private void distributePorts(final LNode node) {
        if (!node.getProperty(LayeredOptions.PORT_CONSTRAINTS).isOrderFixed()) {
            // the order of ports on each side is variable, so distribute the ports
            if (node.getPorts().size() > 1) {
                // array of port barycenter values calculated from ranks of connected ports
                Float[] portBarycenter = new Float[portRanks.length];
                
                // list that keeps track of ports connected to other ports in the same layer; these are
                // treated specially when calculating barycenters
                List<LPort> inLayerPorts = Lists.newArrayListWithCapacity(portRanks.length);
                
                // the minimum and maximum barycenter values assigned for ports of this node
                float minBarycenter = 0.0f;
                float maxBarycenter = 0.0f;
                
                // a float value large enough to ensure that barycenters of south ports work fine
                float absurdlyLargeFloat = 2 * node.getLayer().getNodes().size() + 1;
                
                // calculate barycenter values for the ports of the node
                PortIteration:
                for (LPort port : node.getPorts()) {
                    boolean northSouthPort =
                            port.getSide() == PortSide.NORTH || port.getSide() == PortSide.SOUTH;
                    float sum = 0;
                    
                    if (northSouthPort) {
                        // Find the dummy node created for the port
                        LNode portDummy = port.getProperty(InternalProperties.PORT_DUMMY);
                        if (portDummy == null) {
                            continue;
                        }
                        
                        // Find out if it's an input port, an output port, or both
                        boolean input = false;
                        boolean output = false;
                        for (LPort portDummyPort : portDummy.getPorts()) {
                            if (portDummyPort.getProperty(InternalProperties.ORIGIN) == port) {
                                if (!portDummyPort.getOutgoingEdges().isEmpty()) {
                                    output = true;
                                } else if (!portDummyPort.getIncomingEdges().isEmpty()) {
                                    input = true;
                                }
                            }
                        }
                        
                        if (input && (input ^ output)) {
                            // It's an input port; the index of its dummy node is its inverted sort key
                            // (for southern input ports, the key must be larger than the ones assigned
                            // to output ports or input&&output ports)
                            sum = port.getSide() == PortSide.NORTH
                                    ? -portDummy.getIndex()
                                    : absurdlyLargeFloat - portDummy.getIndex();
                        } else if (output && (input ^ output)) {
                            // It's an output port; the index of its dummy node is its sort key
                            // (for northern output ports, the key must be larger than the ones assigned
                            // to input ports or input&&output ports, which are negative and 0,
                            // respectively)
                            sum = portDummy.getIndex() + 1.0f;
                        } else if (input && output) {
                            // It's both, an input and an output port; it must sit between input and
                            // output ports
                            // North: input ports < 0.0, output ports > 0.0
                            // South: input ports > FLOAT_MAX / 2, output ports near zero
                            sum = port.getSide() == PortSide.NORTH
                                    ? 0.0f
                                    : absurdlyLargeFloat / 2f;
                        }
                    } else {
                        // add up all ranks of connected ports
                        for (LEdge outgoingEdge : port.getOutgoingEdges()) {
                            LPort connectedPort = outgoingEdge.getTarget();
                            if (connectedPort.getNode().getLayer() == node.getLayer()) {
                                inLayerPorts.add(port);
                                continue PortIteration;
                            } else {
                                // outgoing edges go to the subsequent layer and are seen clockwise
                                sum += portRanks[connectedPort.id];
                            }
                        }
                        for (LEdge incomingEdge : port.getIncomingEdges()) {
                            LPort connectedPort = incomingEdge.getSource();
                            if (connectedPort.getNode().getLayer() == node.getLayer()) {
                                inLayerPorts.add(port);
                                continue PortIteration;
                            } else {
                                // incoming edges go to the preceding layer and are seen
                                // counter-clockwise
                                sum -= portRanks[connectedPort.id];
                            }
                        }
                    }
                    
                    if (port.getDegree() > 0) {
                        portBarycenter[port.id] = sum / port.getDegree();
                        minBarycenter = Math.min(minBarycenter, portBarycenter[port.id]);
                        maxBarycenter = Math.max(maxBarycenter, portBarycenter[port.id]);
                    } else if (northSouthPort) {
                        // For northern and southern ports, the sum directly corresponds to the
                        // barycenter value to be used.
                        portBarycenter[port.id] = sum;
                    }
                }
                
                // go through the list of in-layer ports and calculate their barycenter values
                int nodeIndexInLayer = node.getIndex() + 1;
                int layerSize = node.getLayer().getNodes().size() + 1;
                for (LPort inLayerPort : inLayerPorts) {
                    // add the indices of all connected in-layer ports
                    int sum = 0;
                    int inLayerConnections = 0;
                    
                    for (LPort connectedPort : inLayerPort.getConnectedPorts()) {
                        if (connectedPort.getNode().getLayer() == node.getLayer()) {
                            sum += connectedPort.getNode().getIndex() + 1;
                            inLayerConnections++;
                        }
                    }
                    
                    // The port's barycenter value is the mean index of connected nodes. If that value
                    // is lower than the node's index, most in-layer edges point upwards, so we want
                    // the port to be placed at the top of the side. If the value is higher than the
                    // nodes's index, most in-layer edges point downwards, so we want the port to be
                    // placed at the bottom of the side.
                    float barycenter = ((float) sum) / inLayerConnections;
                    
                    PortSide portSide = inLayerPort.getSide();
                    
                    if (portSide == PortSide.EAST) {
                        if (barycenter < nodeIndexInLayer) {
                            // take a low value in order to have the port above
                            portBarycenter[inLayerPort.id] = minBarycenter - barycenter;
                        } else {
                            // take a high value in order to have the port below
                            portBarycenter[inLayerPort.id] = maxBarycenter + (layerSize - barycenter);
                        }
                    } else if (portSide == PortSide.WEST) {
                        if (barycenter < nodeIndexInLayer) {
                            // take a high value in order to have the port above
                            portBarycenter[inLayerPort.id] = maxBarycenter + barycenter;
                        } else {
                            // take a low value in order to have the port below
                            portBarycenter[inLayerPort.id] = minBarycenter - (layerSize - barycenter);
                        }
                    }
                }
                
                // sort the ports by considering the side, type, and barycenter values
                sortPorts(node, portBarycenter);
            }
            node.setProperty(LayeredOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_ORDER);
        }
    }

    /**
     * Sort the ports of a node using the given relative position values. These values are
     * interpreted as a hint for the clockwise order of ports.
     * 
     * @param node
     *            a node
     * @param position
     *            an array of position values; the identifier of each port of this node must be
     *            inside the range of the {@code position} array, since it is used as index
     */
    private static void sortPorts(final LNode node, final Float[] position) {
        Collections.sort(node.getPorts(), new Comparator<LPort>() {
            public int compare(final LPort port1, final LPort port2) {
                PortSide side1 = port1.getSide();
                PortSide side2 = port2.getSide();

                if (side1 != side2) {
                    // sort according to the node side
                    return side1.ordinal() - side2.ordinal();
                } else {
                    Float pos1 = position[port1.id];
                    Float pos2 = position[port2.id];
                    
                    if (pos1 == null && pos2 == null) {
                        // the ports are not connected to anything -- leave their order untouched
                        return 0;
                    } else if (pos1 == null) {
                        return -1;
                    } else if (pos2 == null) {
                        return 1;
                    } else {
                        // sort according to the position value
                        return Float.compare(pos1, pos2);
                    }
                }
            }
        });
    }
    
}
