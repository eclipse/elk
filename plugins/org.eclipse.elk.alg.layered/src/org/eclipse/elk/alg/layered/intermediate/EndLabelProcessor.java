/*******************************************************************************
 * Copyright (c) 2012, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.Map;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LInsets;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.nodespacing.LabelSide;

import com.google.common.collect.Maps;

/**
 * <p>This intermediate processor does the necessary calculations for an absolute positioning
 * of all end and port labels. It uses the port sides and the side choice made before to find
 * this positioning.</p>
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>a layered graph</dd>
 *     <dd>no dummy nodes</dd>
 *     <dd>labels are marked with a placement side</dd>
 *     <dd>nodes have port sides</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>edge and port labels have absolute coordinates.</dd>
 *   <dt>Slots:</dt>
 *     <dd>After phase 5.</dd>
 *   <dt>Same-slot dependencies:</dt><dd>{@link LongEdgeJoiner}</dd>
 *                                   <dd>{@link NorthSouthPortPostProcessor}</dd>
 *                                   <dd>{@link LabelDummyRemover}</dd>
 *                                   <dd>{@link ReverseEdgeRestorer}</dd>
 * </dl>
 * 
 * @author jjc
 * @kieler.rating proposed yellow cds
 */
public final class EndLabelProcessor implements ILayoutProcessor {

    /**
     * In case of northern ports, labels have to be stacked to avoid overlaps.
     * The necessary offset is stored here.
     */
    private Map<LNode, Double> northOffset; 
    
    /** The stacking offset for southern labels is stored here. */
    private Map<LNode, Double> southOffset;
    
    /**
     * Port labels have to be stacked on northern or southern ports as well if
     * placed outside. This offset is memorized here.
     */
    private Map<LPort, Double> portLabelOffsetHint;
    
    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("End label placement", 1);
        
        double labelSpacing = layeredGraph.getProperty(LayoutOptions.LABEL_SPACING).doubleValue();
        
        // Initialize the offset maps
        northOffset = Maps.newHashMap();
        southOffset = Maps.newHashMap();
        portLabelOffsetHint = Maps.newHashMap();
        
        for (Layer layer : layeredGraph.getLayers()) {
            for (LNode node : layer.getNodes()) {
                for (LEdge edge : node.getOutgoingEdges()) {
                    for (LLabel label : edge.getLabels()) {
                        // Only consider end labels
                        if (label.getProperty(LayoutOptions.EDGE_LABEL_PLACEMENT)
                                == EdgeLabelPlacement.TAIL
                                ||
                            label.getProperty(LayoutOptions.EDGE_LABEL_PLACEMENT)
                                == EdgeLabelPlacement.HEAD) {
                            
                            placeEndLabel(node, edge, label, labelSpacing);
                        }
                    }
                }
            }
        }
        
        monitor.done();
    }

    /**
     * Places the given end label of the given edge starting at the given node.
     * 
     * @param node source node of the edge.
     * @param edge the edge whose end label to place.
     * @param label the end label to place.
     * @param labelSpacing space between objects and labels.
     */
    private void placeEndLabel(final LNode node, final LEdge edge, final LLabel label,
            final double labelSpacing) {
        
        // Get the nearest port (source port for tail labels, target port for head labels)
        LPort port = null;
        
        if (label.getProperty(LayoutOptions.EDGE_LABEL_PLACEMENT) == EdgeLabelPlacement.TAIL) {
            port = edge.getSource();
        } else if (label.getProperty(LayoutOptions.EDGE_LABEL_PLACEMENT) == EdgeLabelPlacement.HEAD) {
            port = edge.getTarget();
        }
        
        // Initialize offset with zero if no offset was present
        if (!northOffset.containsKey(port.getNode())) {
            northOffset.put(port.getNode(), 0.0);
        }
        if (!southOffset.containsKey(port.getNode())) {
            southOffset.put(port.getNode(), 0.0);
        }
        if (!portLabelOffsetHint.containsKey(port)) {
            portLabelOffsetHint.put(port, 0.0);
        }
        
        // Calculate end label position based on side choice
        // Port side undefined can be left out, because there would be no reasonable
        // way of handling them
        if (label.getProperty(InternalProperties.LABEL_SIDE) == LabelSide.ABOVE) {
            placeEndLabelUpwards(node, label, port, labelSpacing);
        } else {
            placeEndLabelDownwards(node, label, port, labelSpacing);
        }
    }

    /**
     * Places the given end label above the edge.
     * 
     * @param node source node of the edge the label belongs to.
     * @param label the label to place.
     * @param port the end port of the edge the label is nearest to.
     * @param labelSpacing space between objects and labels.
     */
    private void placeEndLabelDownwards(final LNode node, final LLabel label, final LPort port,
            final double labelSpacing) {
        
        // Remember some stuff
        KVector labelPosition = label.getPosition();
        KVector absolutePortPosition = KVector.sum(port.getPosition(), port.getNode().getPosition());
        KVector absolutePortAnchor = port.getAbsoluteAnchor();
        LInsets portMargin = port.getMargin();
        
        // Actually calculate the coordinates
        switch (port.getSide()) {
        case WEST:
            labelPosition.x = Math.min(absolutePortPosition.x, absolutePortAnchor.x)
                              - portMargin.left
                              - label.getSize().x
                              - labelSpacing;
            labelPosition.y = port.getAbsoluteAnchor().y
                              + labelSpacing;
            break;
            
        case EAST:
            labelPosition.x = Math.max(absolutePortPosition.x + port.getSize().x, absolutePortAnchor.x)
                              + portMargin.right
                              + labelSpacing;
            labelPosition.y = port.getAbsoluteAnchor().y
                              + labelSpacing;
            break;
            
        case NORTH:
            labelPosition.x = port.getAbsoluteAnchor().x
                              + labelSpacing;
            labelPosition.y = Math.min(absolutePortPosition.y, absolutePortAnchor.y)
                              - portMargin.top
                              - label.getSize().y
                              - labelSpacing;
            break;
            
        case SOUTH:
            labelPosition.x = port.getAbsoluteAnchor().x
                              + labelSpacing;
            labelPosition.y = Math.max(absolutePortPosition.y + port.getSize().y, absolutePortAnchor.y)
                              + portMargin.bottom
                              + labelSpacing;
            break;
        }
    }

    /**
     * Places the given end label below the edge.
     * 
     * @param node source node of the edge the label belongs to.
     * @param label the label to place.
     * @param port the end port of the edge the label is nearest to.
     * @param labelSpacing space between objects and labels.
     */
    private void placeEndLabelUpwards(final LNode node, final LLabel label, final LPort port,
            final double labelSpacing) {
        
        // Remember some stuff
        KVector labelPosition = label.getPosition();
        KVector absolutePortPosition = KVector.sum(port.getPosition(), port.getNode().getPosition());
        KVector absolutePortAnchor = port.getAbsoluteAnchor();
        LInsets portMargin = port.getMargin();
        
        // Actually calculate the coordinates
        switch (port.getSide()) {
        case WEST:
            labelPosition.x = Math.min(absolutePortPosition.x, absolutePortAnchor.x)
                              - portMargin.left
                              - label.getSize().x
                              - labelSpacing;
            labelPosition.y = port.getAbsoluteAnchor().y
                              - label.getSize().y
                              - labelSpacing;
            break;
            
        case EAST:
            labelPosition.x = Math.max(absolutePortPosition.x + port.getSize().x, absolutePortAnchor.x)
                              + portMargin.right
                              + labelSpacing;
            labelPosition.y = port.getAbsoluteAnchor().y
                              - label.getSize().y
                              - labelSpacing;
            break;
            
        case NORTH:
            labelPosition.x = port.getAbsoluteAnchor().x
                              + labelSpacing;
            labelPosition.y = Math.min(absolutePortPosition.y, absolutePortAnchor.y)
                              - portMargin.top
                              - label.getSize().y
                              - labelSpacing;
            break;
            
        case SOUTH:
            labelPosition.x = port.getAbsoluteAnchor().x
                              + labelSpacing;
            labelPosition.y = Math.max(absolutePortPosition.y + port.getSize().y, absolutePortAnchor.y)
                              + portMargin.bottom
                              + labelSpacing;
            break;
        }
    }

}
