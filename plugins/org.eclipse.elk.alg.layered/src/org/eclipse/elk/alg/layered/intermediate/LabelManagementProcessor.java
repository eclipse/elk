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
import java.util.Set;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.p5edges.splines.ConnectedSelfLoopComponent;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.core.labels.ILabelManager;
import org.eclipse.elk.core.labels.LabelManagementOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.LayoutOptions;
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
 * 
 * @author cds
 */
public final class LabelManagementProcessor implements ILayoutProcessor {

    /** Minimum width for shortened edge labels. */
    private static final double MIN_WIDTH_EDGE_LABELS = 60;
    /** Minimum width for shortened port labels. */
    private static final double MIN_WIDTH_PORT_LABELS = 20;

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Label management", 1);

        // This processor should actually not be run in the first place if there is no label manager
        // set on the graph, but let's be sure anyway
        ILabelManager labelManager = layeredGraph.getProperty(LabelManagementOptions.LABEL_MANAGER);
        if (labelManager != null) {
            double labelSpacing = layeredGraph.getProperty(LayoutOptions.LABEL_SPACING).doubleValue();

            // Iterate over all layers and call our nifty code
            for (Layer layer : layeredGraph) {
                manageLabels(layer, labelManager, labelSpacing);
            }
        }

        monitor.done();
    }

    /**
     * Iterates over the nodes labels and invokes the label manager on label dummy nodes. The target
     * width for labels is the width of the widest non-dummy node.
     * 
     * @param layer
     *            the layer whose labels to manage.
     * @param labelManager
     *            the label manager used.
     * @param labelSpacing
     *            spacing between labels and other objects.
     */
    private void manageLabels(final Layer layer, final ILabelManager labelManager,
            final double labelSpacing) {

        assert labelManager != null : "labelManager is null";

        boolean verticalLayout = layer.getGraph().getProperty(LayoutOptions.DIRECTION).isVertical();
        double maxWidth =
                Math.max(MIN_WIDTH_EDGE_LABELS, LGraphUtil.findMaxNonDummyNodeWidth(layer, false));

        // Apply the maximum width to all label dummy nodes
        for (LNode layerNode : layer) {
            switch (layerNode.getType()) {
            case NORMAL:
                // Handle ports
                List<LPort> ports = layerNode.getPorts();
                for (LPort port : ports) {
                    doManageLabels(labelManager, port.getLabels(), MIN_WIDTH_PORT_LABELS,
                            null, 0, verticalLayout);
                }

                // Selfloop splines
                final List<ConnectedSelfLoopComponent> components =
                        layerNode.getProperty(InternalProperties.SPLINE_SELFLOOP_COMPONENTS);
                
                for (ConnectedSelfLoopComponent component : components) {
                    Set<LEdge> edges = component.getEdges();
                    for (LEdge edge : edges) {
                        doManageLabels(labelManager, edge.getLabels(), maxWidth,
                                null, labelSpacing, verticalLayout);

                    }
                }
                
                break;

            case LABEL:
                LEdge edge = layerNode.getConnectedEdges().iterator().next();
                double edgeThickness = edge.getProperty(LayoutOptions.THICKNESS).doubleValue();

                KVector newDummySize = new KVector(0.0, edgeThickness);

                Iterable<LLabel> labels = layerNode.getProperty(InternalProperties.REPRESENTED_LABELS);

                newDummySize = doManageLabels(labelManager, labels, maxWidth,
                        newDummySize, labelSpacing, verticalLayout);

                // Apply new dummy node size (we don't bother with the ports here since they will be
                // meddled with later by the LabelSideSelector anyway)
                layerNode.getSize().x = newDummySize.x;
                layerNode.getSize().y = newDummySize.y;
                
                break;

            }
            
            // Edges can have edge and tail labels that need to be managed as well (note that at this
            // point, only head and tail labels remain)
            for (LEdge edge : layerNode.getOutgoingEdges()) {
                doManageLabels(labelManager, edge.getLabels(), MIN_WIDTH_EDGE_LABELS,
                        null, 0, verticalLayout);
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
     * @param newDummySize
     *            the new bounding box for the shortened labels. This will normally be {@code null}
     *            except for when this method is called multiple times for the same bounding box.
     * @param labelSpacing
     *            the label spacing.
     * @param verticalLayout
     *            {@code true} if the graph is laid out downwards or upwards. In these cases, the
     *            new label dimensions returned by the label manager need to be turned 90 degrees.
     * @return the bounding box passed as {@code newDummySize} or a new size if that was
     *         {@code null}.
     */
    private KVector doManageLabels(final ILabelManager labelManager, final Iterable<LLabel> labels,
            final double targetWidth, final KVector newDummySize, final double labelSpacing,
            final boolean verticalLayout) {
        
        for (LLabel label : labels) {
            // If the label has an origin, call the label size modifier
            Object origin = label.getProperty(InternalProperties.ORIGIN);
            if (origin != null) {
                KVector newSize = labelManager.manageLabelSize(origin, targetWidth);

                if (newSize != null) {
                    if (verticalLayout) {
                        // Our labels are turned 90 degrees
                        label.getSize().x = newSize.y;
                        label.getSize().y = newSize.x;
                    } else {
                        label.getSize().x = newSize.x;
                        label.getSize().y = newSize.y;
                    }
                }
            }
            
            if (newDummySize != null) {
                newDummySize.x = Math.max(newDummySize.x, label.getSize().x);
                newDummySize.y += label.getSize().y + labelSpacing;
            }
        }
        
        return newDummySize;
    }

}
