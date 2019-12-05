/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.radial.intermediate;

import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * Calculate the size of the graph and shift nodes into positive coordinates if necessary.
 *
 */
public class CalculateGraphSize implements ILayoutProcessor<ElkNode> {

    /** Shift the nodes such that each nodes has x and y coordinates bigger 0. */
    public void process(final ElkNode graph, final IElkProgressMonitor progressMonitor) {

        // calculate the offset from border spacing and node distribution
        double minXPos = Double.MAX_VALUE;
        double minYPos = Double.MAX_VALUE;
        double maxXPos = Double.MIN_VALUE;
        double maxYPos = Double.MIN_VALUE;

        for (ElkNode node : graph.getChildren()) {
            double posX = node.getX();
            double posY = node.getY();
            double width = node.getWidth();
            double height = node.getHeight();
            ElkMargin margins = node.getProperty(CoreOptions.MARGINS);

            minXPos = Math.min(minXPos, posX - margins.left);
            minYPos = Math.min(minYPos, posY - margins.top);
            maxXPos = Math.max(maxXPos, posX + width + margins.right);
            maxYPos = Math.max(maxYPos, posY + height + margins.bottom);
        }

        ElkPadding padding = graph.getProperty(CoreOptions.PADDING);
        KVector offset = new KVector(minXPos - padding.getLeft(), minYPos - padding.getTop());

        // process the nodes
        for (ElkNode node : graph.getChildren()) {
            // set the node position
            node.setX(node.getX() - offset.x);
            node.setY(node.getY() - offset.y);
        }

        // set up the graph
        double width = maxXPos - minXPos + padding.getHorizontal();
        double height = maxYPos - minYPos + padding.getVertical();
        graph.setWidth(width);
        graph.setHeight(height);
    }
}
