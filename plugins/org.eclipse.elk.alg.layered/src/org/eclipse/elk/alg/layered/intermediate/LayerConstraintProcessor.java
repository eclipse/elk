/*******************************************************************************
 * Copyright (c) 2010, 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.options.LayerConstraint;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.UnsupportedConfigurationException;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Moves nodes with layer constraints to the appropriate layers. To meet the preconditions of
 * this processor, the {@link EdgeAndLayerConstraintEdgeReverser} can be used.
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>a layered graph.</dd>
 *     <dd>nodes with {@code FIRST_SEPARATE} layer constraint have only outgoing edges.</dd>
 *     <dd>nodes with {@code FIRST} layer constraint have only outgoing edges, except for edges incoming from
 *         {@code FIRST_SEPARATE} nodes or label dummy nodes</dd>
 *     <dd>nodes with {@code LAST_SEPARATE} layer constraint have only incoming edges.</dd>
 *     <dd>nodes with {@code LAST} layer constraint have only incoming edges, except for edges outgoing to
 *         {@code LAST_SEPARATE} nodes or label dummy nodes</dd>
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
 */
public final class LayerConstraintProcessor implements ILayoutProcessor<LGraph> {

    @Override
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
        
        // We may also need label dummy layers between the very first / last layers and the first / last layers
        Layer firstLabelLayer = new Layer(layeredGraph);
        Layer lastLabelLayer = new Layer(layeredGraph);
        
        // Iterate through the current list of layers
        for (Layer layer : layers) {
            // Iterate through a node array to avoid ConcurrentModificationExceptions
            LNode [] nodes = LGraphUtil.toNodeArray(layer.getNodes());
            
            for (LNode node : nodes) {
                LayerConstraint constraint = node.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT);
                
                // Check if there is a layer constraint
                switch (constraint) {
                case FIRST:
                    node.setLayer(firstLayer);
                    throwUpUnlessNoIncomingEdges(node, true);
                    moveLabelsToLabelLayer(node, true, firstLabelLayer);
                    break;
                
                case FIRST_SEPARATE:
                    node.setLayer(veryFirstLayer);
                    throwUpUnlessNoIncomingEdges(node, false);
                    break;
                
                case LAST:
                    node.setLayer(lastLayer);
                    throwUpUnlessNoOutgoingEdges(node, true);
                    moveLabelsToLabelLayer(node, false, lastLabelLayer);
                    break;
                
                case LAST_SEPARATE:
                    node.setLayer(veryLastLayer);
                    throwUpUnlessNoOutgoingEdges(node, false);
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
                if (node.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT) != LayerConstraint.NONE) {
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
                LNode [] nodes = LGraphUtil.toNodeArray(firstLayer.getNodes());
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
                if (node.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT) != LayerConstraint.NONE) {
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
                LNode [] nodes = LGraphUtil.toNodeArray(lastLayer.getNodes());
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
        
        // Add non-empty new first and last (label) layers
        if (!firstLabelLayer.getNodes().isEmpty()) {
            layers.add(0, firstLabelLayer);
        }
        
        if (!veryFirstLayer.getNodes().isEmpty()) {
            layers.add(0, veryFirstLayer);
        }
        
        if (!lastLabelLayer.getNodes().isEmpty()) {
            layers.add(lastLabelLayer);
        }

        if (!veryLastLayer.getNodes().isEmpty()) {
            layers.add(veryLastLayer);
        }
        
        monitor.done();
    }
    
    
    /**
     * Moves the label dummies coming in to the given node or going out from the given node to the given label dummy
     * layer.
     * 
     * @param node
     *            the node whose adjacent label dummies to move.
     * @param incoming
     *            {@code true} if label dummies on incoming edges should be moved, {@code false} if label dummies on
     *            outgoing edges should be moved.
     * @param labelLayer
     *            the layer to move the label dummies to.
     */
    private void moveLabelsToLabelLayer(final LNode node, final boolean incoming, final Layer labelLayer) {
        for (LEdge edge : incoming ? node.getIncomingEdges() : node.getOutgoingEdges()) {
            LNode possibleLableDummy = incoming
                    ? edge.getSource().getNode()
                    : edge.getTarget().getNode();
            
            if (possibleLableDummy.getType() == NodeType.LABEL) {
                possibleLableDummy.setLayer(labelLayer);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // FIRST(_SEPARATE) Node Checks
    
    /**
     * Check that the node has no incoming edges, and fail if it has any. Edges that connect two hierarchical port
     * dummies are always allowed.
     * 
     * @param node
     *            a node.
     * @param allowFromFirstSeparate
     *            {@code true} if incoming connections from {@code FIRST_SEPARATE} nodes are allowed.
     */
    private void throwUpUnlessNoIncomingEdges(final LNode node, final boolean allowFromFirstSeparate) {
        for (LPort port : node.getPorts()) {
            for (LEdge incoming : port.getIncomingEdges()) {
                if (!isAcceptableIncomingEdge(incoming)) {
                    if (allowFromFirstSeparate) {
                        throw new UnsupportedConfigurationException("Node '" + node.getDesignation()
                                + "' has its layer constraint set to FIRST, but has at least one incoming edge that "
                                + " does not come from a FIRST_SEPARATE node. That must not happen.");
                    } else {
                        throw new UnsupportedConfigurationException("Node '" + node.getDesignation()
                                + "' has its layer constraint set to FIRST_SEPARATE, but has at least one incoming "
                                + "edge. FIRST_SEPARATE nodes must not have incoming edges.");
                    }
                }
            }
        }
    }
    
    /**
     * Checks whether or not the given edge incoming to a {@code FIRST} layer node is allowed to do so.
     */
    private boolean isAcceptableIncomingEdge(final LEdge edge) {
        LNode sourceNode = edge.getSource().getNode();
        LNode targetNode = edge.getTarget().getNode();
        
        // If both nodes are external port dummies, that's fine
        if (sourceNode.getType() == NodeType.EXTERNAL_PORT && targetNode.getType() == NodeType.EXTERNAL_PORT) {
            return true;
        }
        
        // Otherwise, the target node is expected to be in the FIRST layer
        assert targetNode.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT) == LayerConstraint.FIRST;
        
        // If the source node is in the very first layer, that's okay
        if (sourceNode.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT) == LayerConstraint.FIRST_SEPARATE) {
            return true;
        }
        
        // If the source node is a label dummy, that's okay too
        return sourceNode.getType() == NodeType.LABEL;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // LAST(_SEPARATE) Node Checks
    
    /**
     * Check that the node has no outgoing edges, and fail if it has any.
     * 
     * @param node
     *            a node
     * @param allowToLastSeparate
     *            {@code true} if outgoing connections to {@code LAST_SEPARATE} nodes are allowed.
     */
    private void throwUpUnlessNoOutgoingEdges(final LNode node, final boolean allowToLastSeparate) {
        for (LPort port : node.getPorts()) {
            for (LEdge outgoing : port.getOutgoingEdges()) {
                if (!isAcceptableOutgoingEdge(outgoing)) {
                    if (allowToLastSeparate) {
                        throw new UnsupportedConfigurationException("Node '" + node.getDesignation()
                                + "' has its layer constraint set to LAST, but has at least one outgoing edge that "
                                + " does not go to a LAST_SEPARATE node. That must not happen.");
                    } else {
                        throw new UnsupportedConfigurationException("Node '" + node.getDesignation()
                                + "' has its layer constraint set to LAST_SEPARATE, but has at least one outgoing "
                                + "edge. LAST_SEPARATE nodes must not have outgoing edges.");
                    }
                }
            }
        }
    }
    
    /**
     * Checks whether or not the given edge leacing a {@code LAST} layer node is allowed to do so.
     */
    private boolean isAcceptableOutgoingEdge(final LEdge edge) {
        LNode sourceNode = edge.getSource().getNode();
        LNode targetNode = edge.getTarget().getNode();
        
        // If both nodes are external port dummies, that's fine
        if (sourceNode.getType() == NodeType.EXTERNAL_PORT && targetNode.getType() == NodeType.EXTERNAL_PORT) {
            return true;
        }
        
        // Otherwise, the source node is expected to be in the LAST layer
        assert sourceNode.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT) == LayerConstraint.LAST;
        
        // If the target node is in the very last layer, that's okay
        if (targetNode.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT) == LayerConstraint.LAST_SEPARATE) {
            return true;
        }
        
        // If the target node is a label dummy, that's okay too
        return targetNode.getType() == NodeType.LABEL;
    }

}
