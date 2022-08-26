/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
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
import org.eclipse.elk.alg.rectpacking.util.DrawingData;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * @author sdo
 *
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
