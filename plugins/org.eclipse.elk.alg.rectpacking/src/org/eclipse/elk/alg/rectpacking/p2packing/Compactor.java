/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.rectpacking.p2packing;

import java.util.List;

import org.eclipse.elk.alg.rectpacking.RectPackingLayoutPhases;
import org.eclipse.elk.alg.rectpacking.options.InternalProperties;
import org.eclipse.elk.alg.rectpacking.options.RectPackingOptions;
import org.eclipse.elk.alg.rectpacking.util.DrawingData;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * Places and compact the given rectangles by forming rows of stacks of blocks of subrows to maintain a common
 * reading direction inside a target width.
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>{@link InternalProperties#TARGET_WIDTH} is set.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>All child nodes have coordinates such that:</dd>
 *     <dd>the next node is either in the same subrows/row,</dd>
 *     <dd>a new subrows/row,</dd>
 *     <dd>or in a new stack in the same row.</dd>
 *     <dd>{@link InternalProperties#DRAWING_WIDTH},</dd>
 *     <dd>{@link InternalProperties#DRAWING_HEIGHT},</dd>
 *     <dd>{@link InternalProperties#ROWS},</dd>
 *     <dd>and {@link InternalProperties#ADDITIONAL_HEIGHT} are set.</dd>
 * </dl>
 */
public class Compactor implements ILayoutPhase<RectPackingLayoutPhases, ElkNode> {

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutProcessor#process(java.lang.Object, org.eclipse.elk.core.util.IElkProgressMonitor)
     */
    @Override
    public void process(ElkNode graph, IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Compaction", 1);
        List<ElkNode> rectangles = graph.getChildren();
        // The desired aspect ratio.
        double aspectRatio = graph.getProperty(RectPackingOptions.ASPECT_RATIO);
        //  The spacing between two nodes.
        double nodeNodeSpacing = graph.getProperty(RectPackingOptions.SPACING_NODE_NODE);
        // The padding surrounding the drawing.
        ElkPadding padding = graph.getProperty(RectPackingOptions.PADDING);
        // Reset coordinates after width approximation step.
        RowFillingAndCompaction secondIt = new RowFillingAndCompaction(aspectRatio, nodeNodeSpacing);
        
        // Run placement, compaction, and expansion (if enabled).
        DrawingData drawing = secondIt.start(rectangles, progressMonitor, graph, padding);
        graph.setProperty(InternalProperties.DRAWING_HEIGHT, drawing.getDrawingHeight());
        graph.setProperty(InternalProperties.DRAWING_WIDTH, drawing.getDrawingWidth());
    }

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutPhase#getLayoutProcessorConfiguration(java.lang.Object)
     */
    @Override
    public LayoutProcessorConfiguration<RectPackingLayoutPhases, ElkNode> getLayoutProcessorConfiguration(
            ElkNode graph) {
        return null;
    }

}
