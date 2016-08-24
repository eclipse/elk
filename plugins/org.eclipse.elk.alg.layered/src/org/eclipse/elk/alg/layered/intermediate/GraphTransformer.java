/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.LShape;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.properties.EdgeLabelSideSelection;
import org.eclipse.elk.alg.layered.properties.InLayerConstraint;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayerConstraint;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.Alignment;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Lists;

/**
 * A layout processor that is able to perform transformations on the coordinates of a graph.
 *
 * @author msp
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating proposed yellow by msp
 */
public final class GraphTransformer implements ILayoutProcessor {

    /** definition of transformation modes. */
    public enum Mode {
        /** mirror the coordinates of the graph. */
        MIRROR_X,
        /** transpose by swapping x and y coordinates. */
        TRANSPOSE,
        /** mirror and then transpose the graph. */
        MIRROR_AND_TRANSPOSE;
    }
    
    
    /** the configured mode of the graph transformer. */
    private final Mode mode;
    
    
    /**
     * Creates a graph transformer with the given mode.
     * 
     * @param themode the transformation mode
     */
    public GraphTransformer(final Mode themode) {
        this.mode = themode;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Graph transformation (" + mode + ")", 1);
        
        // We need to add all layerless nodes as well as all nodes in layers since this processor
        // is run twice -- once before layering, and once afterwards
        List<LNode> nodes = Lists.newArrayList(layeredGraph.getLayerlessNodes());
        for (Layer layer : layeredGraph.getLayers()) {
            nodes.addAll(layer.getNodes());
        }
        
        switch (mode) {
        case MIRROR_X:
            mirrorX(nodes, layeredGraph);
            break;
        case TRANSPOSE:
            transpose(nodes);
            transposeEdgeLabelPlacement(layeredGraph);
            transpose(layeredGraph.getOffset());
            transpose(layeredGraph.getSize());
            break;
        case MIRROR_AND_TRANSPOSE:
            mirrorX(nodes, layeredGraph);
            mirrorY(nodes, layeredGraph);
            transpose(nodes);
            transposeEdgeLabelPlacement(layeredGraph);
            transpose(layeredGraph.getOffset());
            transpose(layeredGraph.getSize());
            break;
        }
        monitor.done();
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Mirror Horizontally
    
    /**
     * Mirror the x coordinates of the given graph.
     * 
     * @param nodes the nodes of the graph to transpose
     * @param graph the graph the nodes are part of
     */
    private void mirrorX(final List<LNode> nodes, final LGraph graph) {
        /* Assuming that no nodes extend into negative x coordinates, mirroring a node means that the
         * space left to its left border equals the space right to its right border when mirrored. In
         * mathematical terms:
         *     oldPosition.x = graphWidth - newPosition.x - nodeWidth
         * We use the offset variable to store graphWidth, since that's the constant offset against which
         * we calculate the new node positions.
         * This, however, stops to work once nodes are allowed to extend into negative coordinates. Then,
         * we have to subtract from the graphWidth the amount of space the graph extends into negative
         * coordinates. This amount is saved in the graph's graphOffset. Thus, our offset here becomes:
         *     offset = graphWidth - graphOffset.x 
         */
        double offset = 0;
        
        // If the graph already had its size calculated, use that; if not, find its width by iterating
        // over its nodes
        if (graph.getSize().x == 0) {
            for (LNode node : nodes) {
                offset = Math.max(
                        offset,
                        node.getPosition().x + node.getSize().x + node.getMargin().right);
            }
        } else {
            offset = graph.getSize().x - graph.getOffset().x;
        }
        offset -= graph.getOffset().x;
        
        // mirror all nodes, ports, edges, and labels
        for (LNode node : nodes) {
            mirrorX(node.getPosition(), offset - node.getSize().x);
            mirrorNodeLabelPlacementX(node);
            // mirror the alignment
            switch (node.getProperty(LayeredOptions.ALIGNMENT)) {
            case LEFT:
                node.setProperty(LayeredOptions.ALIGNMENT, Alignment.RIGHT);
                break;
            case RIGHT:
                node.setProperty(LayeredOptions.ALIGNMENT, Alignment.LEFT);
                break;
            }
            
            KVector nodeSize = node.getSize();
            for (LPort port : node.getPorts()) {
                mirrorX(port.getPosition(), nodeSize.x - port.getSize().x);
                mirrorX(port.getAnchor(), port.getSize().x);
                mirrorPortSideX(port);
                reverseIndex(port);
                for (LEdge edge : port.getOutgoingEdges()) {
                    // Mirror bend points
                    for (KVector bendPoint : edge.getBendPoints()) {
                        mirrorX(bendPoint, offset);
                    }
                    
                    // Mirror junction points
                    KVectorChain junctionPoints = edge.getProperty(LayeredOptions.JUNCTION_POINTS);
                    if (junctionPoints != null) {
                        for (KVector jp : junctionPoints) {
                            mirrorX(jp, offset);
                        }
                    }
                    
                    // Mirror edge label positions
                    for (LLabel label : edge.getLabels()) {
                        mirrorX(label.getPosition(), offset - label.getSize().x);
                    }
                }
                
                // Mirror port label positions
                for (LLabel label : port.getLabels()) {
                    mirrorX(label.getPosition(), -label.getSize().x);
                }
            }
            
            // External port dummy?
            if (node.getType() == NodeType.EXTERNAL_PORT) {
                mirrorExternalPortSideX(node);
                mirrorLayerConstraintX(node);
            }

            // Mirror node labels
            for (LLabel label : node.getLabels()) {
                mirrorNodeLabelPlacementX(label);
                mirrorX(label.getPosition(), nodeSize.x - label.getSize().x);
            }
        }
    }

    /**
     * Mirror the x coordinate of the given vector and add an offset.
     * 
     * @param v a vector
     * @param offset offset for the x coordinate
     */
    private void mirrorX(final KVector v, final double offset) {
        v.x = offset - v.x;
    }
    
    /**
     * Horrizontally mirrors the node label placement options, if any are set.
     * 
     * @param shape the node or label whose placement should be transposed.
     */
    private void mirrorNodeLabelPlacementX(final LShape shape) {
        Set<NodeLabelPlacement> oldPlacement = shape.getProperty(LayeredOptions.NODE_LABELS_PLACEMENT);
        if (oldPlacement.isEmpty()) {
            return;
        }
        
        if (oldPlacement.contains(NodeLabelPlacement.H_LEFT)) {
            oldPlacement.remove(NodeLabelPlacement.H_LEFT);
            oldPlacement.add(NodeLabelPlacement.H_RIGHT);
        } else if (oldPlacement.contains(NodeLabelPlacement.H_RIGHT)) {
            oldPlacement.remove(NodeLabelPlacement.H_RIGHT);
            oldPlacement.add(NodeLabelPlacement.H_LEFT);
        }
    }
    
    /**
     * Mirror the side of the given port. Undefined port sides are left untouched.
     * 
     * @param port the port.
     */
    private void mirrorPortSideX(final LPort port) {
        port.setSide(getMirroredPortSideX(port.getSide()));
    }
    
    /**
     * Mirror the side of the external port represented by the given external port dummy.
     * 
     * @param node external port dummy node.
     */
    private void mirrorExternalPortSideX(final LNode node) {
        node.setProperty(InternalProperties.EXT_PORT_SIDE,
                getMirroredPortSideX(node.getProperty(InternalProperties.EXT_PORT_SIDE)));
    }
    
    /**
     * Returns the port side that is horizontally mirrored from the given side.
     * 
     * @param side the side whose horizontal opposite to return.
     * @return horizontal opposite of the given side.
     */
    private PortSide getMirroredPortSideX(final PortSide side) {
        switch (side) {
        case EAST:
            return PortSide.WEST;
            
        case WEST:
            return PortSide.EAST;
            
        default:
            return side;
        }
    }
    
    /**
     * Horizontally mirrors the layer constraint set on a node. This is only meant for handling external
     * port dummy nodes.
     * 
     * @param node the node whose layer constraint to mirror.
     */
    private void mirrorLayerConstraintX(final LNode node) {
        switch (node.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT)) {
        case FIRST:
            node.setProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT, LayerConstraint.LAST);
            break;
            
        case FIRST_SEPARATE:
            node.setProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT, LayerConstraint.LAST_SEPARATE);
            break;

        case LAST:
            node.setProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT, LayerConstraint.FIRST);
            break;
            
        case LAST_SEPARATE:
            node.setProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT, LayerConstraint.FIRST_SEPARATE);
            break;
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Mirror Vertically
    
    /**
     * Mirror the y coordinates of the given graph.
     * 
     * @param nodes the nodes of the graph to transpose
     * @param graph the graph the nodes are part of
     */
    private void mirrorY(final List<LNode> nodes, final LGraph graph) {
        // See mirrorX for an explanation of how the offset is calculated
        double offset = 0;
        if (graph.getSize().y == 0) {
            for (LNode node : nodes) {
                offset = Math.max(
                        offset,
                        node.getPosition().y + node.getSize().y + node.getMargin().bottom);
            }
        } else {
            offset = graph.getSize().y - graph.getOffset().y;
        }
        offset -= graph.getOffset().y;
        
        // mirror all nodes, ports, edges, and labels
        for (LNode node : nodes) {
            mirrorY(node.getPosition(), offset - node.getSize().y);
            mirrorNodeLabelPlacementY(node);
            // mirror the alignment
            switch (node.getProperty(LayeredOptions.ALIGNMENT)) {
            case TOP:
                node.setProperty(LayeredOptions.ALIGNMENT, Alignment.BOTTOM);
                break;
            case BOTTOM:
                node.setProperty(LayeredOptions.ALIGNMENT, Alignment.TOP);
                break;
            }
            
            KVector nodeSize = node.getSize();
            for (LPort port : node.getPorts()) {
                mirrorY(port.getPosition(), nodeSize.y - port.getSize().y);
                mirrorY(port.getAnchor(), port.getSize().y);
                mirrorPortSideY(port);
                reverseIndex(port);
                for (LEdge edge : port.getOutgoingEdges()) {
                    // Mirror bend points
                    for (KVector bendPoint : edge.getBendPoints()) {
                        mirrorY(bendPoint, offset);
                    }
                    
                    // Mirror junction points
                    KVectorChain junctionPoints = edge.getProperty(LayeredOptions.JUNCTION_POINTS);
                    if (junctionPoints != null) {
                        for (KVector jp : junctionPoints) {
                            mirrorY(jp, offset);
                        }
                    }
                    
                    // Mirror edge label positions
                    for (LLabel label : edge.getLabels()) {
                        mirrorY(label.getPosition(), offset - label.getSize().y);
                    }
                }
                
                // Mirror port label positions
                for (LLabel label : port.getLabels()) {
                    mirrorY(label.getPosition(), -label.getSize().y);
                }
            }
            
            // External port dummy?
            if (node.getType() == NodeType.EXTERNAL_PORT) {
                mirrorExternalPortSideY(node);
                mirrorInLayerConstraintY(node);
            }
            
            // Mirror node labels
            for (LLabel label : node.getLabels()) {
                mirrorNodeLabelPlacementY(label);
                mirrorY(label.getPosition(), nodeSize.y - label.getSize().y);
            }
        }
    }
    
    /**
     * Mirror the y coordinate of the given vector and add an offset.
     * 
     * @param v a vector
     * @param offset offset for the x coordinate
     */
    private void mirrorY(final KVector v, final double offset) {
        v.y = offset - v.y;
    }
    
    /**
     * Vertically mirrors the node label placement options, if any are set.
     * 
     * @param shape the node or label whose placement should be mirrored.
     */
    private void mirrorNodeLabelPlacementY(final LShape shape) {
        Set<NodeLabelPlacement> oldPlacement = shape.getProperty(LayeredOptions.NODE_LABELS_PLACEMENT);
        if (oldPlacement.isEmpty()) {
            return;
        }
        
        if (oldPlacement.contains(NodeLabelPlacement.V_TOP)) {
            oldPlacement.remove(NodeLabelPlacement.V_TOP);
            oldPlacement.add(NodeLabelPlacement.V_BOTTOM);
        } else if (oldPlacement.contains(NodeLabelPlacement.V_BOTTOM)) {
            oldPlacement.remove(NodeLabelPlacement.V_BOTTOM);
            oldPlacement.add(NodeLabelPlacement.V_TOP);
        }
    }
    
    /**
     * Mirror the side of the given port. Undefined port sides are left untouched.
     * 
     * @param port the port.
     */
    private void mirrorPortSideY(final LPort port) {
        port.setSide(getMirroredPortSideY(port.getSide()));
    }
    
    /**
     * Mirror the side of the external port represented by the given external port dummy.
     * 
     * @param node external port dummy node.
     */
    private void mirrorExternalPortSideY(final LNode node) {
        node.setProperty(InternalProperties.EXT_PORT_SIDE,
                getMirroredPortSideY(node.getProperty(InternalProperties.EXT_PORT_SIDE)));
    }
    
    /**
     * Returns the port side that is vertically mirrored from the given side.
     * 
     * @param side the side whose vertical opposite to return.
     * @return vertical opposite of the given side.
     */
    private PortSide getMirroredPortSideY(final PortSide side) {
        switch (side) {
        case NORTH:
            return PortSide.SOUTH;
            
        case SOUTH:
            return PortSide.NORTH;
            
        default:
            return side;
        }
    }
    
    /**
     * Vertically mirrors the in-layer constraint set on a node. This is only meant for handling external
     * port dummy nodes.
     * 
     * @param node the node whose in-layer constraint to mirror.
     */
    private void mirrorInLayerConstraintY(final LNode node) {
        switch (node.getProperty(InternalProperties.IN_LAYER_CONSTRAINT)) {
        case TOP:
            node.setProperty(InternalProperties.IN_LAYER_CONSTRAINT, InLayerConstraint.BOTTOM);
            break;

        case BOTTOM:
            node.setProperty(InternalProperties.IN_LAYER_CONSTRAINT, InLayerConstraint.TOP);
            break;
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Transpose
    
    /**
     * Transpose the x and y coordinates of the given graph.
     * 
     * @param nodes the nodes of the graph to transpose
     */
    private void transpose(final List<LNode> nodes) {
        // Transpose nodes
        for (LNode node : nodes) {
            transpose(node.getPosition());
            transpose(node.getSize());
            transposeNodeLabelPlacement(node);
            transposeProperties(node);
            
            // Transpose ports
            for (LPort port : node.getPorts()) {
                transpose(port.getPosition());
                transpose(port.getAnchor());
                transpose(port.getSize());
                transposePortSide(port);
                reverseIndex(port);
                
                // Transpose edges
                for (LEdge edge : port.getOutgoingEdges()) {
                    // Transpose bend points
                    for (KVector bendPoint : edge.getBendPoints()) {
                        transpose(bendPoint);
                    }
                    
                    // Transpose junction points
                    KVectorChain junctionPoints = edge.getProperty(LayeredOptions.JUNCTION_POINTS);
                    if (junctionPoints != null) {
                        for (KVector jp : junctionPoints) {
                            transpose(jp);
                        }
                    }
                    
                    // Transpose edge labels
                    for (LLabel label : edge.getLabels()) {
                        transpose(label.getPosition());
                        transpose(label.getSize());
                    }
                }
                
                // Transpose port labels
                for (LLabel label : port.getLabels()) {
                    transpose(label.getPosition());
                    transpose(label.getSize());
                }
            }
            
            // External port dummy?
            if (node.getType() == NodeType.EXTERNAL_PORT) {
                transposeExternalPortSide(node);
                transposeLayerConstraint(node);
            }

            // Transpose node labels
            for (LLabel label : node.getLabels()) {
                transposeNodeLabelPlacement(label);
                transpose(label.getSize());
                transpose(label.getPosition());
            }
        }
    }
    
    /**
     * Transpose the x and y coordinate of the given vector.
     * 
     * @param v a vector
     */
    private void transpose(final KVector v) {
        double temp = v.x;
        v.x = v.y;
        v.y = temp;
    }
    
    /**
     * Transposes the node label placement options, if any are set.
     * 
     * @param shape the node or label whose placement should be transposed.
     */
    private void transposeNodeLabelPlacement(final LShape shape) {
        Set<NodeLabelPlacement> oldPlacement = shape.getProperty(LayeredOptions.NODE_LABELS_PLACEMENT);
        if (oldPlacement.isEmpty()) {
            return;
        }
        
        // Build up a new node label placement enumeration
        EnumSet<NodeLabelPlacement> newPlacement = EnumSet.noneOf(NodeLabelPlacement.class);
        
        // Inside or outside
        if (oldPlacement.contains(NodeLabelPlacement.INSIDE)) {
            newPlacement.add(NodeLabelPlacement.INSIDE);
        } else {
            newPlacement.add(NodeLabelPlacement.OUTSIDE);
        }
        
        // Horizontal priority
        if (!oldPlacement.contains(NodeLabelPlacement.H_PRIORITY)) {
            newPlacement.add(NodeLabelPlacement.H_PRIORITY);
        }
        
        // Horizontal alignment
        if (oldPlacement.contains(NodeLabelPlacement.H_LEFT)) {
            newPlacement.add(NodeLabelPlacement.V_TOP);
        } else if (oldPlacement.contains(NodeLabelPlacement.H_CENTER)) {
            newPlacement.add(NodeLabelPlacement.V_CENTER);
        } else if (oldPlacement.contains(NodeLabelPlacement.H_RIGHT)) {
            newPlacement.add(NodeLabelPlacement.V_BOTTOM);
        }
        
        // Vertical alignment
        if (oldPlacement.contains(NodeLabelPlacement.V_TOP)) {
            newPlacement.add(NodeLabelPlacement.H_LEFT);
        } else if (oldPlacement.contains(NodeLabelPlacement.V_CENTER)) {
            newPlacement.add(NodeLabelPlacement.H_CENTER);
        } else if (oldPlacement.contains(NodeLabelPlacement.V_BOTTOM)) {
            newPlacement.add(NodeLabelPlacement.H_RIGHT);
        }
        
        // Apply new placement
        shape.setProperty(LayeredOptions.NODE_LABELS_PLACEMENT, newPlacement);
    }
    
    /**
     * Transpose the side of the given port. Undefined port sides are left untouched.
     * 
     * @param p the port.
     */
    private void transposePortSide(final LPort p) {
        p.setSide(transposePortSide(p.getSide()));
    }
    
    /**
     * Returns the transposed side of the given port side.
     * 
     * @param side the side to transpose.
     * @return transposed port side.
     */
    private PortSide transposePortSide(final PortSide side) {
        switch (side) {
        case NORTH:
            return PortSide.WEST;
        
        case WEST:
            return PortSide.NORTH;
        
        case SOUTH:
            return PortSide.EAST;
        
        case EAST:
            return PortSide.SOUTH;
            
        default:
            return PortSide.UNDEFINED;    
        }
    }
    
    /**
     * Transpose the placement of edge labels in the graph.
     *
     * @param graph the complete graph
     */
    private void transposeEdgeLabelPlacement(final LGraph graph) {
        EdgeLabelSideSelection oldSide = graph.getProperty(LayeredOptions.EDGE_LABEL_SIDE_SELECTION);
        if (oldSide != null) {
            graph.setProperty(LayeredOptions.EDGE_LABEL_SIDE_SELECTION, oldSide.transpose());
        }

    }

    /**
     * Transpose the side of the external port represented by the given external port dummy.
     * 
     * @param node external port dummy node.
     */
    private void transposeExternalPortSide(final LNode node) {
        node.setProperty(InternalProperties.EXT_PORT_SIDE,
                transposePortSide(node.getProperty(InternalProperties.EXT_PORT_SIDE)));
    }
    
    /**
     * The layer constraint and in-layer constraint set on a node. A node with layer constraint
     * {@link LayerConstraint#FIRST_SEPARATE} will end up with an in-layer constraint
     * {@link InLayerConstraint#TOP}. This is only meant for external port dummy nodes and only
     * supports the cases that can occur with them.
     * 
     * @param node the node whose layer constraint to mirror.
     */
    private void transposeLayerConstraint(final LNode node) {
        LayerConstraint layerConstraint = node.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT);
        InLayerConstraint inLayerConstraint = node.getProperty(InternalProperties.IN_LAYER_CONSTRAINT);
        
        if (layerConstraint == LayerConstraint.FIRST_SEPARATE) {
            node.setProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT, LayerConstraint.NONE);
            node.setProperty(InternalProperties.IN_LAYER_CONSTRAINT, InLayerConstraint.TOP);
        } else if (layerConstraint == LayerConstraint.LAST_SEPARATE) {
            node.setProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT, LayerConstraint.NONE);
            node.setProperty(InternalProperties.IN_LAYER_CONSTRAINT, InLayerConstraint.BOTTOM);
        } else if (inLayerConstraint == InLayerConstraint.TOP) {
            node.setProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT, LayerConstraint.FIRST_SEPARATE);
            node.setProperty(InternalProperties.IN_LAYER_CONSTRAINT, InLayerConstraint.NONE);
        } else if (inLayerConstraint == InLayerConstraint.BOTTOM) {
            node.setProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT, LayerConstraint.LAST_SEPARATE);
            node.setProperty(InternalProperties.IN_LAYER_CONSTRAINT, InLayerConstraint.NONE);
        }
    }
    
    /**
     * Checks a node's properties for ones that need to be transposed. Currently, the following
     * properties are transposed:
     * <ul>
     *   <li>{@link LayeredOptions#MIN_HEIGHT} and {@link LayeredOptions#MIN_WIDTH}.</li>
     * </ul>
     * 
     * @param node the node whose properties are to be transposed.
     */
    private void transposeProperties(final LNode node) {
        // Transpose MIN_HEIGHT and MIN_WIDTH
        KVector minSize = node.getProperty(LayeredOptions.NODE_SIZE_MINIMUM);
        if (minSize == null) {
            minSize = new KVector(
                    node.getProperty(LayeredOptions.NODE_SIZE_MIN_WIDTH).doubleValue(),
                    node.getProperty(LayeredOptions.NODE_SIZE_MIN_HEIGHT).doubleValue());
        }
        node.setProperty(LayeredOptions.NODE_SIZE_MINIMUM, new KVector(minSize.y, minSize.x));
        
        // Transpose ALIGNMENT
        switch (node.getProperty(LayeredOptions.ALIGNMENT)) {
        case LEFT:
            node.setProperty(LayeredOptions.ALIGNMENT, Alignment.TOP);
            break;
        case RIGHT:
            node.setProperty(LayeredOptions.ALIGNMENT, Alignment.BOTTOM);
            break;
        case TOP:
            node.setProperty(LayeredOptions.ALIGNMENT, Alignment.LEFT);
            break;
        case BOTTOM:
            node.setProperty(LayeredOptions.ALIGNMENT, Alignment.RIGHT);
            break;
        }
    }
    
    /**
     * Reverse the port index.
     * 
     * @param port a port
     */
    private void reverseIndex(final LPort port) {
        Integer index = port.getProperty(LayeredOptions.PORT_INDEX);
        if (index != null) {
            port.setProperty(LayeredOptions.PORT_INDEX, -index);
        }
    }

}
