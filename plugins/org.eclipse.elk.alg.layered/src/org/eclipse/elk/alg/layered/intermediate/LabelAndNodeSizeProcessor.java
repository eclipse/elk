/*******************************************************************************
 * Copyright (c) 2010, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import org.eclipse.elk.alg.common.nodespacing.NodeDimensionCalculation;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphAdapters;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.GraphProperties;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Calculates node sizes, places ports, and places node and port labels.
 * 
 * <p><i>Note:</i> Regarding port placement, this processor now does what the old
 * {@code PortPositionProcessor} did and thus replaces it.</p>
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>The graph is layered.</dd>
 *     <dd>Crossing minimization is finished.</dd>
 *     <dd>Port constraints are at least at {@code FIXED_ORDER}.</dd>
 *     <dd>Port lists are properly sorted going clockwise, starting at the leftmost northern port.</dd>
 *     <dd>External port dummies have the label-port and label-label spacing set that should apply to them.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>Port positions are fixed.</dd>
 *     <dd>Port labels are placed.</dd>
 *     <dd>Node labels are placed.</dd>
 *     <dd>Node sizes are set.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 4.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link LabelSideSelector}</dd>
 * </dl>
 * 
 * @see LabelSideSelector
 */
public final class LabelAndNodeSizeProcessor implements ILayoutProcessor<LGraph> {
    
    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Node and Port Label Placement and Node Sizing", 1);
        
        NodeDimensionCalculation.calculateLabelAndNodeSizes(LGraphAdapters.adapt(
                layeredGraph,
                true,
                true,
                node -> node.getType() == NodeType.NORMAL || node.getType() == NodeType.BIG_NODE));
        
        // If the graph has external ports, we need to treat labels of external port dummies a bit differently,
        // which is the reason why we haven't handed them to the label and node size processing code
        if (layeredGraph.getProperty(InternalProperties.GRAPH_PROPERTIES).contains(GraphProperties.EXTERNAL_PORTS)) {
            PortLabelPlacement portLabelPlacement = layeredGraph.getProperty(LayeredOptions.PORT_LABELS_PLACEMENT);
            boolean placeNextToPort = layeredGraph.getProperty(LayeredOptions.PORT_LABELS_NEXT_TO_PORT_IF_POSSIBLE);
            boolean treatAsGroup = layeredGraph.getProperty(LayeredOptions.PORT_LABELS_TREAT_AS_GROUP);
            
            for (Layer layer : layeredGraph.getLayers()) {
                layer.getNodes().stream()
                        .filter(node -> node.getType() == NodeType.EXTERNAL_PORT)
                        .forEach(dummy -> placeExternalPortDummyLabels(
                                dummy, portLabelPlacement, placeNextToPort, treatAsGroup));
            }
        }
        
        monitor.done();
    }
    
    /**
     * Places the labels of the given external port dummy such that it results in correct node margins later on that
     * will reserve enough space for the labels to be placed once label and node placement is called on the graph.
     */
    private void placeExternalPortDummyLabels(final LNode dummy, final PortLabelPlacement graphPortLabelPlacement,
            final boolean placeNextToPortIfPossible, final boolean treatAsGroup) {
        
        double labelPortSpacing = dummy.getProperty(LayeredOptions.SPACING_LABEL_PORT);
        double labelLabelSpacing = dummy.getProperty(LayeredOptions.SPACING_LABEL_LABEL);
        
        KVector dummySize = dummy.getSize();
        
        // External port dummies have exactly one port (see ElkGraphImporter)
        LPort dummyPort = dummy.getPorts().get(0);
        KVector dummyPortPos = dummyPort.getPosition();
        
        ElkRectangle portLabelBox = computePortLabelBox(dummyPort, labelLabelSpacing);
        if (portLabelBox == null) {
            return;
        }
        
        // Determine the position of the box
        // TODO We could handle FIXED here as well
        if (graphPortLabelPlacement == PortLabelPlacement.INSIDE) {
            // (port label placement has to support this case first, though)
            switch (dummy.getProperty(InternalProperties.EXT_PORT_SIDE)) {
            case NORTH:
                portLabelBox.x = (dummySize.x - portLabelBox.width) / 2 - dummyPortPos.x;
                portLabelBox.y = labelPortSpacing;
                break;
                
            case SOUTH:
                portLabelBox.x = (dummySize.x - portLabelBox.width) / 2 - dummyPortPos.x;
                portLabelBox.y = -labelPortSpacing - portLabelBox.height;
                break;
                
            case EAST:
                if (labelNextToPort(dummyPort, placeNextToPortIfPossible)) {
                    double labelHeight = treatAsGroup
                            ? portLabelBox.height
                            : dummyPort.getLabels().get(0).getSize().y;
                    portLabelBox.y = (dummySize.y - labelHeight) / 2 - dummyPortPos.y;
                } else {
                    portLabelBox.y = dummySize.y + labelPortSpacing - dummyPortPos.y;
                }
                portLabelBox.x = -labelPortSpacing - portLabelBox.width;
                break;
                
            case WEST:
                if (labelNextToPort(dummyPort, placeNextToPortIfPossible)) {
                    double labelHeight = treatAsGroup
                            ? portLabelBox.height
                            : dummyPort.getLabels().get(0).getSize().y;
                    portLabelBox.y = (dummySize.y - labelHeight) / 2 - dummyPortPos.y;
                } else {
                    portLabelBox.y = dummySize.y + labelPortSpacing - dummyPortPos.y;
                }
                portLabelBox.x = labelPortSpacing;
                break;
            }
        } else if (graphPortLabelPlacement == PortLabelPlacement.OUTSIDE) {
            switch (dummy.getProperty(InternalProperties.EXT_PORT_SIDE)) {
            case NORTH:
            case SOUTH:
                portLabelBox.x = dummyPortPos.x + labelPortSpacing;
                break;
                
            case EAST:
            case WEST:
                // TODO Fix this
                // We would have to respect the next-to-port option here as well, but for that we would have to find
                // out if the port has any connections to the outside.
                portLabelBox.y = dummyPortPos.y + labelPortSpacing;
                break;
            }
        }
        
        // Place the labels
        double currentY = portLabelBox.y;
        for (LLabel label : dummyPort.getLabels()) {
            KVector labelPos = label.getPosition();
            
            labelPos.x = portLabelBox.x;
            labelPos.y = currentY;
            
            currentY += label.getSize().y + labelLabelSpacing;
        }
    }

    /**
     * Returns the amount of space required to place the labels later, or {@code null} if there are no labels.
     */
    private ElkRectangle computePortLabelBox(final LPort dummyPort, final double labelLabelSpacing) {
        if (dummyPort.getLabels().isEmpty()) {
            return null;
        } else {
            ElkRectangle result = new ElkRectangle();
            
            for (LLabel label : dummyPort.getLabels()) {
                KVector labelSize = label.getSize();
                
                result.width = Math.max(result.width, labelSize.x);
                result.height += labelSize.y;
            }
            
            result.height += (dummyPort.getLabels().size() - 1) * labelLabelSpacing;
            
            return result;
        }
    }
    
    /**
     * Checks whether the labels of the given port should be placed next to the port or below it. The former is the
     * case if the user requested port labels to be placed next to the port, if possible, and if the port has no
     * connections.
     */
    private boolean labelNextToPort(final LPort dummyPort, final boolean placeNextToPortIfPossible) {
        return placeNextToPortIfPossible
                && dummyPort.getIncomingEdges().isEmpty()
                && dummyPort.getOutgoingEdges().isEmpty();
    }
    
}
