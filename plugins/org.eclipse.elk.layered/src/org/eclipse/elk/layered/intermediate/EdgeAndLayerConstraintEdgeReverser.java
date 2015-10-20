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
package org.eclipse.elk.layered.intermediate;

import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.layered.ILayoutProcessor;
import org.eclipse.elk.layered.graph.LEdge;
import org.eclipse.elk.layered.graph.LGraph;
import org.eclipse.elk.layered.graph.LNode;
import org.eclipse.elk.layered.graph.LPort;
import org.eclipse.elk.layered.properties.EdgeConstraint;
import org.eclipse.elk.layered.properties.InternalProperties;
import org.eclipse.elk.layered.properties.LayerConstraint;
import org.eclipse.elk.layered.properties.PortType;
import org.eclipse.elk.layered.properties.Properties;

/**
 * Makes sure nodes with edge or layer constraints have only incoming or only outgoing edges,
 * as appropriate. This is done even before cycle breaking because the result may
 * already break some cycles. This processor is required for
 * {@link LayerConstraintProcessor} to work correctly. If edge constraints are in conflict
 * with layer constraints, the latter take precedence. Furthermore, this processor handles
 * nodes with fixed port sides for which all ports are reversed, i.e. input ports are on the
 * right and output ports are on the left. All incident edges are reversed in such cases.
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>an unlayered graph.</dd>
 *   <dt>Postcondition:</dt><dd>nodes with layer constraints have only incoming or
 *     only outgoing edges, as appropriate.</dd>
 *   <dt>Slots:</dt><dd>Before phase 1.</dd>
 *   <dt>Same-slot dependencies:</dt><dd>None.</dd>
 * </dl>
 * 
 * @see LayerConstraintProcessor
 * @author cds
 * @author msp
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating proposed yellow by msp
 */
public final class EdgeAndLayerConstraintEdgeReverser implements ILayoutProcessor {

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Edge and layer constraint edge reversal", 1);
        
        // Iterate through the list of nodes
        for (LNode node : layeredGraph.getLayerlessNodes()) {
            // Check if there is a layer constraint
            LayerConstraint layerConstraint = node.getProperty(Properties.LAYER_CONSTRAINT);
            EdgeConstraint edgeConstraint = null;
            
            switch (layerConstraint) {
            case FIRST:
            case FIRST_SEPARATE:
                edgeConstraint = EdgeConstraint.OUTGOING_ONLY;
                break;
            
            case LAST:
            case LAST_SEPARATE:
                edgeConstraint = EdgeConstraint.INCOMING_ONLY;
                break;
            }
            
            if (edgeConstraint != null) {
                // Set the edge constraint on the node
                node.setProperty(InternalProperties.EDGE_CONSTRAINT, EdgeConstraint.OUTGOING_ONLY);
                
                if (edgeConstraint == EdgeConstraint.INCOMING_ONLY) {
                    reverseEdges(layeredGraph, node, layerConstraint, PortType.OUTPUT);
                } else if (edgeConstraint == EdgeConstraint.OUTGOING_ONLY) {
                    reverseEdges(layeredGraph, node, layerConstraint, PortType.INPUT);
                }
            } else {
                // If the port sides are fixed, but all ports are reversed, that probably means that we
                // have a feedback node. Normally, the connected edges would be routed around the node,
                // but that hides the feedback node character. We thus simply reverse all connected
                // edges and thus make KLay Layered think we have a regular node
                //
                // Note that this behavior is only desired if none of the connected nodes have 
                // layer constraints set. Otherwise this processing causes issues with an external 
                // port dummy with FIRST_SEPARATE and an inverted ports on the target node's EAST side.
                if (node.getProperty(LayoutOptions.PORT_CONSTRAINTS).isSideFixed()
                        && !node.getPorts().isEmpty()) {
                    
                    boolean allPortsReversed = true;
                    
                    for (LPort port : node.getPorts()) {
                        if (!(port.getSide() == PortSide.EAST && port.getNetFlow() > 0
                                || port.getSide() == PortSide.WEST && port.getNetFlow() < 0)) {
                            
                            allPortsReversed = false;
                            break;
                        }
                        
                        // no LAST or LAST_SEPARATE allowed for the target of outgoing WEST ports
                        if (port.getSide() == PortSide.WEST) {
                            for (LEdge e : port.getOutgoingEdges()) {
                                LayerConstraint lc = e.getTarget().getNode()
                                                .getProperty(Properties.LAYER_CONSTRAINT);
                                if (lc == LayerConstraint.LAST
                                        || lc == LayerConstraint.LAST_SEPARATE) {
                                    allPortsReversed = false;
                                    break;
                                }
                            }
                        }

                        // no FIRST or FIRST_SEPARATE allowed for the source of incoming EAST ports
                        if (port.getSide() == PortSide.EAST) {
                            for (LEdge e : port.getIncomingEdges()) {
                                LayerConstraint lc = e.getSource().getNode()
                                                .getProperty(Properties.LAYER_CONSTRAINT);
                                if (lc == LayerConstraint.FIRST
                                        || lc == LayerConstraint.FIRST_SEPARATE) {
                                    allPortsReversed = false;
                                    break;
                                }
                            }
                        }
                    }
                    
                    if (allPortsReversed) {
                        reverseEdges(layeredGraph, node, layerConstraint, PortType.UNDEFINED);
                    }
                }
            }
        }
        
        monitor.done();
    }
    
    /**
     * Reverses edges as appropriate.
     * 
     * @param layeredGraph the layered graph.
     * @param node the node to place in the layer.
     * @param nodeLayerConstraint the layer constraint put on the node.
     * @param type type of edges that are reversed.
     */
    private void reverseEdges(final LGraph layeredGraph, final LNode node,
            final LayerConstraint nodeLayerConstraint, final PortType type) {
        
        // Iterate through the node's edges and reverse them, if necessary
        LPort[] ports = node.getPorts().toArray(new LPort[node.getPorts().size()]);
        for (LPort port : ports) {
            // Only incoming edges
            if (type != PortType.INPUT) {
                LEdge[] outgoing = port.getOutgoingEdges().toArray(
                        new LEdge[port.getOutgoingEdges().size()]);
                
                for (LEdge edge : outgoing) {
                    LayerConstraint targetLayerConstraint = edge.getTarget().getNode().getProperty(
                            Properties.LAYER_CONSTRAINT);
                    
                    // We leave an edge untouched if it has already been reversed or if it runs from a
                    // LAST to a LAST_SEPARATE node (such outgoing edges are allowed for LAST nodes)
                    if (!edge.getProperty(InternalProperties.REVERSED)
                            && !(nodeLayerConstraint == LayerConstraint.LAST
                                && targetLayerConstraint == LayerConstraint.LAST_SEPARATE)) {
                        
                        edge.reverse(layeredGraph, true);
                    }
                }
            }
            
            // Only outgoing edges
            if (type != PortType.OUTPUT) {
                LEdge[] incoming = port.getIncomingEdges().toArray(
                        new LEdge[port.getIncomingEdges().size()]);
                
                for (LEdge edge : incoming) {
                    LayerConstraint sourceLayerConstraint = edge.getSource().getNode().getProperty(
                            Properties.LAYER_CONSTRAINT);
                    
                    // We leave an edge untouched if it has already been reversed or if it runs from a
                    // FIRST_SEPARATE to a FIRST node (such incoming edges are allowed for FIRST nodes)
                    if (!edge.getProperty(InternalProperties.REVERSED)
                            && !(nodeLayerConstraint == LayerConstraint.FIRST
                                && sourceLayerConstraint == LayerConstraint.FIRST_SEPARATE)) {
                        
                        edge.reverse(layeredGraph, true);
                    }
                }
            }
        }
    }

}
