/*******************************************************************************
 * Copyright (c) 2010, 2019 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LMargin;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Computes and sets the node margins required to place comment boxes.
 * 
 * <dl>
 *   <dt>Preconditions:</dt>
 *     <dd>A layered graph.</dd>
 *     <dd>Node margins include space for ports, port labels, and self loops.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>Node margins are extended to include space for comment boxes.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 4.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link SelfLoopRouter}</dd>
 * </dl>
 * 
 * @see InnermostNodeMarginCalculator
 */
public final class CommentNodeMarginCalculator implements ILayoutProcessor<LGraph> {

    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Node margin calculation", 1);

        // Iterate through the layers to additionally handle comments
        double nodeNodeSpacing = layeredGraph.getProperty(LayeredOptions.SPACING_NODE_NODE).doubleValue();
        layeredGraph.getLayers().stream()
            .flatMap(layer -> layer.getNodes().stream())
            .forEach(lnode -> processComments(lnode, nodeNodeSpacing));

        monitor.done();
    }

    /**
     * Make some extra space for comment boxes that are placed near a node.
     * 
     * @param node
     *            a node
     * @param spacing
     *            the overall spacing value
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

}
