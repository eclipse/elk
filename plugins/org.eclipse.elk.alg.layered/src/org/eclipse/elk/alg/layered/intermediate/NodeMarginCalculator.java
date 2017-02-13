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
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphAdapters;
import org.eclipse.elk.alg.layered.graph.LMargin;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.nodespacing.NodeDimensionCalculation;

/**
 * Sets the node margins. Node margins are influenced by both port positions and sizes
 * and label positions and sizes. Furthermore, comment boxes that are put directly
 * above or below a node also increase the margin.
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>A layered graph.</dd>
 *     <dd>Ports have fixed port positions.</dd>
 *     <dd>Labels have fixed positions.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>The node margins are properly set to form a bounding box around the node and its ports and
 *         labels.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 4.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link LabelAndNodeSizeProcessor}</dd>
 * </dl>
 *
 * @see LabelAndNodeSizeProcessor
 * @author cds
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating proposed yellow by cds
 */
public final class NodeMarginCalculator implements ILayoutProcessor {

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Node margin calculation", 1);
        
        // calculate the margins using ELK's utility methods
        // Use transparentNorthSouthEdges. This ensures that end labels of edges connected to north/south
        // ports are considered during margin calculation.
        // Setting this to false would consider end labels in the margin calculation of the corresponding
        // dummy node instead of the originally connected port.
        NodeDimensionCalculation.calculateNodeMargins(LGraphAdapters.adapt(layeredGraph, true));

        // Iterate through the layers to additionally handle comments
        double spacing = layeredGraph.getProperty(LayeredOptions.SPACING_NODE_NODE).doubleValue();
        for (Layer layer : layeredGraph) {
            // Iterate through the layer's nodes
            for (LNode node : layer) {
                processComments(node, spacing);
                processSelfLoops(node);                
            }
        }
        
        monitor.done();
    }

    /**
     * Make some extra space for comment boxes that are placed near a node.
     * 
     * @param node a node
     * @param spacing the overall spacing value
     */
    private void processComments(final LNode node, final double spacing) {
        LMargin margin = node.getMargin();

        // Consider comment boxes that are put on top of the node
        List<LNode> topBoxes = node.getProperty(InternalProperties.TOP_COMMENTS);
        double topWidth = 0;
        if (topBoxes != null) {
            double maxHeight = 0;
            for (LNode commentBox : topBoxes) {
                maxHeight = Math.max(maxHeight, commentBox.getSize().y);
                topWidth += commentBox.getSize().x;
            }
            topWidth += spacing / 2 * (topBoxes.size() - 1);
            margin.top += maxHeight + spacing;
        }
        
        // Consider comment boxes that are put in the bottom of the node
        List<LNode> bottomBoxes = node.getProperty(InternalProperties.BOTTOM_COMMENTS);
        double bottomWidth = 0;
        if (bottomBoxes != null) {
            double maxHeight = 0;
            for (LNode commentBox : bottomBoxes) {
                maxHeight = Math.max(maxHeight, commentBox.getSize().y);
                bottomWidth += commentBox.getSize().x;
            }
            bottomWidth += spacing / 2 * (bottomBoxes.size() - 1);
            margin.bottom += maxHeight + spacing;
        }
        
        // Check if the maximum width of the comments is wider than the node itself, which the comments
        // are centered on
        double maxCommentWidth = Math.max(topWidth, bottomWidth);
        if (maxCommentWidth > node.getSize().x) {
            double protrusion = (maxCommentWidth - node.getSize().x) / 2;
            margin.left = Math.max(margin.left, protrusion);
            margin.right = Math.max(margin.right, protrusion);
        }
    }
    
    /**
     * Apply the additional space from spline self loops.
     * 
     * @param node a node
     */
    private void processSelfLoops(final LNode node) {
        LMargin nodeMargin = node.getMargin();
        ElkMargin selfLoopMargin = node.getProperty(InternalProperties.SPLINE_SELF_LOOP_MARGINS);

        nodeMargin.left = Math.max(nodeMargin.left, selfLoopMargin.left);
        nodeMargin.right = Math.max(nodeMargin.right, selfLoopMargin.right);
        nodeMargin.bottom = Math.max(nodeMargin.bottom, selfLoopMargin.bottom);
        nodeMargin.top = Math.max(nodeMargin.top, selfLoopMargin.top);
    }    
    
}
