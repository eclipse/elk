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
package org.eclipse.elk.alg.layered.intermediate;

import java.util.List;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.LayerConstraint;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.UnsupportedConfigurationException;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Moves nodes with layer constraints to the appropriate layers. To meet the preconditions of
 * this processor, the {@link EdgeAndLayerConstraintEdgeReverser} can be used.
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>a layered graph.</dd>
 *     <dd>nodes to be placed in the first layer have only outgoing edges</dd>
 *     <dd>nodes to be placed in the last layer have only incoming edges</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>nodes with layer constraints have been placed in the appropriate layers.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 3.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link HierarchicalPortConstraintProcessor}</dd>
 * </dl>
 * 
 * @see EdgeAndLayerConstraintEdgeReverser
 * @author cds
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating proposed yellow by msp
 */
public final class LayerConstraintProcessor implements ILayoutProcessor {

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Layer constraint application", 1);
        
        List<Layer> layers = layeredGraph.getLayers();
        if (layers.isEmpty()) {
            monitor.done();
            return;
        }
        
        // Retrieve the current first and last layers
        Layer firstLayer = layers.get(0);
        Layer lastLayer = layers.get(layers.size() - 1);
        
        // Create the new first and last layers, in case they will be needed
        Layer veryFirstLayer = new Layer(layeredGraph);
        Layer veryLastLayer = new Layer(layeredGraph);
        
        // Iterate through the current list of layers
        for (Layer layer : layers) {
            // Iterate through a node array to avoid ConcurrentModificationExceptions
            LNode [] nodes = layer.getNodes().toArray(new LNode[layer.getNodes().size()]);
            
            for (LNode node : nodes) {
                LayerConstraint constraint = node.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT);
                
                // Check if there is a layer constraint
                switch (constraint) {
                case FIRST:
                    node.setLayer(firstLayer);
                    assertNoIncoming(node, false);
                    break;
                
                case FIRST_SEPARATE:
                    node.setLayer(veryFirstLayer);
                    assertNoIncoming(node, true);
                    break;
                
                case LAST:
                    node.setLayer(lastLayer);
                    assertNoOutgoing(node, false);
                    break;
                
                case LAST_SEPARATE:
                    node.setLayer(veryLastLayer);
                    assertNoOutgoing(node, false);
                    break;
                }
            }
        }

        // If there is a second first layer. To be allowed to move all first layer's nodes to the second
        // layer, we need to check 2 things:
        // - if any of the first layer's nodes has outgoing edges to the second layer
        //      (In this case, we obviously can't move the nodes.)
        // - if any of the first layer's nodes has no layer constraint set
        //      (In this case, we are not allowed to move the node by definition.)
        if (layers.size() >= 2) {
            boolean moveAllowed = true;
            Layer sndFirstLayer = layers.get(1);
            for (LNode node : firstLayer) {
                if (node.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT) == LayerConstraint.NONE) {
                    moveAllowed = false;
                    break;
                }
                for (LEdge edge : node.getOutgoingEdges()) {
                    if (edge.getTarget().getNode().getLayer() == sndFirstLayer) {
                        moveAllowed = false;
                        break;
                    }
                }
                if (!moveAllowed) {
                    break;
                }
            }
            if (moveAllowed) {
                // Iterate through a node array to avoid ConcurrentModificationExceptions
                LNode [] nodes = firstLayer.getNodes().toArray(new LNode[firstLayer.getNodes().size()]);
                for (LNode node : nodes) {
                    node.setLayer(sndFirstLayer);
                }
                layers.remove(firstLayer);
                firstLayer = sndFirstLayer;
            }
        }

        // same description as above
        if (layers.size() >= 2) {
            boolean moveAllowed = true;
            Layer sndLastLayer = layers.get(layers.size() - 2);
            for (LNode node : lastLayer) {
                if (node.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT) == LayerConstraint.NONE) {
                    moveAllowed = false;
                    break;
                }
                for (LEdge edge : node.getIncomingEdges()) {
                    if (edge.getSource().getNode().getLayer() == sndLastLayer) {
                        moveAllowed = false;
                        break;
                    }
                }
                if (!moveAllowed) {
                    break;
                }
            }
            if (moveAllowed) {
                // Iterate through a node array to avoid ConcurrentModificationExceptions
                LNode [] nodes = lastLayer.getNodes().toArray(new LNode[lastLayer.getNodes().size()]);
                for (LNode node : nodes) {
                    node.setLayer(sndLastLayer);
                }
                layers.remove(lastLayer);
                lastLayer = sndLastLayer;
            }
        }
        
        if (layers.size() == 1 && layers.get(0).getNodes().isEmpty()) {
            layers.remove(0);
        }
        
        // Add non-empty new first and last layers
        if (!veryFirstLayer.getNodes().isEmpty()) {
            layers.add(0, veryFirstLayer);
        }

        if (!veryLastLayer.getNodes().isEmpty()) {
            layers.add(veryLastLayer);
        }
        
        monitor.done();
    }
    
    /**
     * Check that the node has no incoming edges, and fail if it has any.
     * 
     * @param node a node
     * @param strict {@code false} if incoming connections from {@code FIRST_SEPARATE} nodes are allowed,
     *               {@code true} otherwise.
     */
    private void assertNoIncoming(final LNode node, final boolean strict) {
        for (LPort port : node.getPorts()) {
            if (strict) {
                if (!port.getIncomingEdges().isEmpty()) {
                    throw new UnsupportedConfigurationException("Node '" + node.getDesignation()
                            + "' has its layer constraint set to FIRST or FIRST_SEPARATE, but has "
                            + "at least one incoming edge. Connections between nodes with these "
                            + "layer constraints are not supported.");
                }
            } else {
                for (LEdge incoming : port.getIncomingEdges()) {
                    if (incoming.getSource().getNode().getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT)
                            != LayerConstraint.FIRST_SEPARATE) {
                        
                        throw new UnsupportedConfigurationException("Node '" + node.getDesignation()
                                + "' has its layer constraint set to FIRST or FIRST_SEPARATE, but has "
                                + "at least one incoming edge. Connections between nodes with these "
                                + "layer constraints are not supported.");
                    }
                }
            }
        }
    }
    
    /**
     * Check that the node has no outgoing edges, and fail if it has any.
     * 
     * @param node a node
     * @param strict {@code false} if outgoing connections from {@code LAST_SEPARATE} nodes are allowed,
     *               {@code true} otherwise.
     */
    private void assertNoOutgoing(final LNode node, final boolean strict) {
        for (LPort port : node.getPorts()) {
            if (strict) {
                if (!port.getOutgoingEdges().isEmpty()) {
                    throw new UnsupportedConfigurationException("Node '" + node.getDesignation()
                            + "' has its layer constraint set to LAST or LAST_SEPARATE, but has "
                            + "at least one outgoing edge. Connections between nodes with these "
                            + "layer constraints are not supported.");
                }
            } else {
                for (LEdge outgoing : port.getOutgoingEdges()) {
                    if (outgoing.getTarget().getNode().getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT)
                            != LayerConstraint.LAST_SEPARATE) {

                        throw new UnsupportedConfigurationException("Node '" + node.getDesignation()
                                + "' has its layer constraint set to LAST or LAST_SEPARATE, but has "
                                + "at least one outgoing edge. Connections between nodes with these "
                                + "layer constraints are not supported.");
                    }
                }
            }
        }
    }

}
