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

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.Layer;
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
    private static final double MIN_WIDTH = 40;
    
    
    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Label management", 1);
        
        // This processor should actually not be run in the first place if there is no label manager
        // set on the graph, but let's be sure anyway
        ILabelManager labelManager = layeredGraph.getProperty(LabelManagementOptions.LABEL_MANAGER);
        if (labelManager != null) {
            float labelSpacing = layeredGraph.getProperty(LayoutOptions.LABEL_SPACING);
            
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
        
        double maxWidth = Math.max(MIN_WIDTH, findMaxNonDummyNodeWidth(layer));
        
        // Apply the maximum width to all label dummy nodes
        for (LNode labelDummy : layer) {
            if (labelDummy.getType() == NodeType.LABEL) {
                LEdge edge = labelDummy.getConnectedEdges().iterator().next();
                double edgeThickness = edge.getProperty(LayoutOptions.THICKNESS).doubleValue();
                
                final KVector newDummySize = new KVector(0.0, edgeThickness);
                
                Iterable<LLabel> labels = labelDummy.getProperty(InternalProperties.REPRESENTED_LABELS);
                for (LLabel label : labels) {
                    // If the label has an origin, call the label size modifier
                    Object origin = label.getProperty(InternalProperties.ORIGIN);
                    if (origin != null) {
                        KVector newSize = labelManager.resizeLabelToWidth(origin, maxWidth);
                        
                        if (newSize != null) {
                            label.getSize().x = newSize.x;
                            label.getSize().y = newSize.y;
                        }
                    }
                    
                    newDummySize.x = Math.max(newDummySize.x, label.getSize().x);
                    newDummySize.y += label.getSize().y + labelSpacing;
                }
                
                // Apply new dummy node size (we don't bother with the ports here since they will be
                // meddled with later by the LabelSideSelector anyway)
                labelDummy.getSize().x = newDummySize.x;
                labelDummy.getSize().y = newDummySize.y;
            }
        }
    }

    /**
     * Finds the maximum width of non-dummy nodes in the given layer.
     * 
     * @param layer
     *            the layer to iterate over.
     * @return the maximum width of non-dummy nodes. If there are none, {@code 0.0} is returned.
     */
    private double findMaxNonDummyNodeWidth(final Layer layer) {
        double maxWidth = 0.0;
        
        for (LNode node : layer) {
            if (node.getType() == NodeType.NORMAL) {
                maxWidth = Math.max(maxWidth, node.getSize().x);
            }
        }
        
        return maxWidth;
    }
    
}
