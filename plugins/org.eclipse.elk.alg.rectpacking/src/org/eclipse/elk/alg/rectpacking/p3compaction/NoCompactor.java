/*******************************************************************************
 * Copyright (c) 2022 sdo and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.rectpacking.p3compaction;

import java.util.List;

import org.eclipse.elk.alg.rectpacking.RectPackingLayoutPhases;
import org.eclipse.elk.alg.rectpacking.options.InternalProperties;
import org.eclipse.elk.alg.rectpacking.options.RectPackingOptions;
import org.eclipse.elk.alg.rectpacking.util.Block;
import org.eclipse.elk.alg.rectpacking.util.BlockStack;
import org.eclipse.elk.alg.rectpacking.util.DrawingUtil;
import org.eclipse.elk.alg.rectpacking.util.RectRow;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * @author sdo
 *
 */
public class NoCompactor implements ILayoutPhase<RectPackingLayoutPhases, ElkNode> {
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.elk.core.alg.ILayoutProcessor#process(java.lang.Object,
     * org.eclipse.elk.core.util.IElkProgressMonitor)
     */
    @Override
    public void process(ElkNode graph, IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("No Compaction", 1);
        List<RectRow> rows = graph.getProperty(InternalProperties.ROWS);
        double nodeNodeSpacing = graph.getProperty(RectPackingOptions.SPACING_NODE_NODE);
        ElkPadding padding = graph.getProperty(RectPackingOptions.PADDING);
        KVector size;
        List<ElkNode> rectangles = graph.getChildren();
        if (rows == null) {
            size = DrawingUtil.calculateDimensions(rectangles);
        } else {
            // Put every block in its own block stack.
            for (RectRow row : rows) {
                for (Block block : row.getChildren()) {
                    BlockStack stack = new BlockStack(block.getX(), block.getY(),
                            graph.getProperty(RectPackingOptions.SPACING_NODE_NODE));
                    stack.addBlock(block);
                    row.getStacks().add(stack);
                }
            }
            size = DrawingUtil.calculateDimensions(rows, nodeNodeSpacing);
        }
        double width = Math.max(size.x, graph.getProperty(InternalProperties.MIN_WIDTH) - padding.getHorizontal());
        double height = Math.max(size.y, graph.getProperty(InternalProperties.MIN_HEIGHT) - padding.getVertical());
        double additionalHeight = height - size.y;
        graph.setProperty(InternalProperties.ADDITIONAL_HEIGHT, additionalHeight);
        graph.setProperty(InternalProperties.DRAWING_WIDTH, width);
        graph.setProperty(InternalProperties.DRAWING_HEIGHT, height + additionalHeight);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.elk.core.alg.ILayoutPhase#getLayoutProcessorConfiguration(java.lang.Object)
     */
    @Override
    public LayoutProcessorConfiguration<RectPackingLayoutPhases, ElkNode> getLayoutProcessorConfiguration(
            ElkNode graph) {
        // TODO Auto-generated method stub
        return null;
    }
}
