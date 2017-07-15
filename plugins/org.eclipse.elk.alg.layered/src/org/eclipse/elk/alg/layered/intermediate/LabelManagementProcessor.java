/*******************************************************************************
 * Copyright (c) 2010, 2017 Kiel University and others.
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
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.splines.ConnectedSelfLoopComponent;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.labels.ILabelManager;
import org.eclipse.elk.core.labels.LabelManagementOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Invokes a potentially registered label manager on center edge label dummy nodes.
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>The graph is layered.</dd>
 *     <dd>Label dummy nodes are placed in their final layers.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>Possible lable text modifications.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 3.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link LabelDummySwitcher}</dd>
 * </dl>
 */
public final class LabelManagementProcessor implements ILayoutProcessor<LGraph> {

    /** Minimum width for shortened edge labels. */
    private static final double MIN_WIDTH_EDGE_LABELS = 60;
    /** Minimum width for shortened port labels. */
    private static final double MIN_WIDTH_PORT_LABELS = 20;

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Label management", 1);

        // If there is no label manager configured for this graph, this processor should not be part of the algorithm.
        // However, better be sure.
        ILabelManager labelManager = layeredGraph.getProperty(LabelManagementOptions.LABEL_MANAGER);
        if (labelManager != null) {
            double edgeLabelSpacing = layeredGraph.getProperty(LayeredOptions.SPACING_EDGE_LABEL).doubleValue();
            double labelLabelSpacing = layeredGraph.getProperty(LayeredOptions.SPACING_LABEL_LABEL).doubleValue();

            // Iterate over all layers and call our nifty code
            for (Layer layer : layeredGraph) {
                manageLabels(layer, labelManager, edgeLabelSpacing, labelLabelSpacing);
            }
        }

        monitor.done();
    }

    /**
     * Iterates over the nodes labels and invokes the label manager on label dummy nodes. The target width for labels is
     * the width of the widest non-dummy node.
     * 
     * @param layer
     *            the layer whose labels to manage.
     * @param labelManager
     *            the label manager to be used.
     * @param edgeLabelSpacing
     *            spacing between labels and edges.
     * @param labelLabelSpacing
     *            spacing between labels.
     */
    private void manageLabels(final Layer layer, final ILabelManager labelManager,
            final double edgeLabelSpacing, final double labelLabelSpacing) {

        assert labelManager != null : "labelManager is null";

        boolean verticalLayout = layer.getGraph().getProperty(LayeredOptions.DIRECTION).isVertical();
        double maxWidth = Math.max(MIN_WIDTH_EDGE_LABELS, LGraphUtil.findMaxNonDummyNodeWidth(layer, false));

        // Apply the maximum width to all label dummy nodes
        for (LNode layerNode : layer) {
            switch (layerNode.getType()) {
            case NORMAL:
                // Handle ports
                List<LPort> ports = layerNode.getPorts();
                for (LPort port : ports) {
                    doManageLabels(labelManager, port.getLabels(), MIN_WIDTH_PORT_LABELS, 0, verticalLayout);
                }

                // Self-loop splines
                final List<ConnectedSelfLoopComponent> components =
                        layerNode.getProperty(InternalProperties.SPLINE_SELFLOOP_COMPONENTS);
                
                for (ConnectedSelfLoopComponent component : components) {
                    Set<LEdge> edges = component.getEdges();
                    for (LEdge edge : edges) {
                        doManageLabels(labelManager, edge.getLabels(), maxWidth, edgeLabelSpacing, verticalLayout);
                    }
                }
                
                break;

            case LABEL:
                LEdge edge = layerNode.getConnectedEdges().iterator().next();
                double edgeThickness = edge.getProperty(LayeredOptions.EDGE_THICKNESS).doubleValue();

                // The list of labels should never be empty (otherwise the label dummy wouldn't have been created in
                // the first place)
                Iterable<LLabel> labels = layerNode.getProperty(InternalProperties.REPRESENTED_LABELS);
                KVector spaceRequiredForLabels = doManageLabels(
                        labelManager, labels, maxWidth, labelLabelSpacing, verticalLayout);
                
                // Apply the space required for labels to the dummy node (we don't bother with the ports here since
                // they will be meddled with later by the LabelSideSelector anyway)
                layerNode.getSize().x = spaceRequiredForLabels.x;
                layerNode.getSize().y = spaceRequiredForLabels.y + edgeThickness + edgeLabelSpacing;
                
                break;

            }
            
            // Edges can have edge and tail labels that need to be managed as well (note that at this
            // point, only head and tail labels remain)
            for (LEdge edge : layerNode.getOutgoingEdges()) {
                doManageLabels(labelManager, edge.getLabels(), MIN_WIDTH_EDGE_LABELS, 0, verticalLayout);
            }
        }
        
    }

    /**
     * Manage all the labels according to the labelManager and change the labels size.
     * 
     * @param labelManager
     *            the label manager used to manage labels.
     * @param labels
     *            the labels to be passed to the label manager.
     * @param targetWidth
     *            the target width.
     * @param labelLabelSpacing
     *            spacing between two adjacent labels
     * @param verticalLayout
     *            {@code true} if the graph is laid out downwards or upwards. In these cases, the new label dimensions
     *            returned by the label manager need to be turned 90 degrees.
     * @return size required to place the labels. Does not include the edge-label space yet.
     */
    private KVector doManageLabels(final ILabelManager labelManager, final Iterable<LLabel> labels,
            final double targetWidth, final double labelLabelSpacing, final boolean verticalLayout) {
        
        KVector requiredLabelSpace = new KVector();
        
        // Manage all of them labels, if there are any
        if (labels.iterator().hasNext()) {
            for (LLabel label : labels) {
                KVector labelSize = label.getSize();
                
                // If the label has an origin, call the label size modifier
                Object origin = label.getProperty(InternalProperties.ORIGIN);
                if (origin != null) {
                    KVector newSize = labelManager.manageLabelSize(origin, targetWidth);
                    
                    if (newSize != null) {
                        if (verticalLayout) {
                            // Our labels are turned 90 degrees
                            labelSize.x = newSize.y;
                            labelSize.y = newSize.x;
                        } else {
                            labelSize.x = newSize.x;
                            labelSize.y = newSize.y;
                        }
                    }
                }
                
                // Update required label space
                if (verticalLayout) {
                    requiredLabelSpace.x += labelLabelSpacing + label.getSize().x;
                    requiredLabelSpace.y = Math.max(requiredLabelSpace.y, labelSize.y);
                } else {
                    requiredLabelSpace.x = Math.max(requiredLabelSpace.x, labelSize.x);
                    requiredLabelSpace.y += labelLabelSpacing + label.getSize().y;
                }
            }
            
            // There is one label-label spacing too much
            if (verticalLayout) {
                requiredLabelSpace.x -= labelLabelSpacing;
            } else {
                requiredLabelSpace.y -= labelLabelSpacing;
            }
        }
        
        return requiredLabelSpace;
    }

}
