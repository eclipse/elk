/*******************************************************************************
 * Copyright (c) 2018, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.rectpacking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.elk.alg.common.NodeMicroLayout;
import org.eclipse.elk.alg.rectpacking.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.rectpacking.options.InternalProperties;
import org.eclipse.elk.alg.rectpacking.options.OptimizationGoal;
import org.eclipse.elk.alg.rectpacking.options.RectPackingOptions;
import org.eclipse.elk.alg.rectpacking.p1widthapproximation.AreaApproximation;
import org.eclipse.elk.alg.rectpacking.p3compaction.RowFillingAndCompaction;
import org.eclipse.elk.alg.rectpacking.util.DrawingData;
import org.eclipse.elk.alg.rectpacking.util.DrawingDataDescriptor;
import org.eclipse.elk.alg.rectpacking.util.DrawingUtil;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.alg.AlgorithmAssembler;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * A layout algorithm that does not take edges into account, but treats all nodes as isolated boxes. This is useful for
 * parts of a diagram that consist of objects without connections, such as parallel regions in Statecharts.
 * <p>
 * Nodes are viewed as rectangles and so {@link ElkNode}s are referred to as rectangles in the comments.
 * </p>
 * <p>
 * Depending on the settings, checks for a specified special case, calculates a layout with a approximation algorithm or
 * uses that approximation algorithm for the needed area of the rectangles and places the rectangles nicely aligned on
 * the drawing area according to that approximation.
 * </p>
 */
public class RectPackingLayoutProvider extends AbstractLayoutProvider {
    /** The thing that will assemble the processors that will constitute our algorithm. */
    private final AlgorithmAssembler<RectPackingLayoutPhases, ElkNode> algorithmAssembler =
            AlgorithmAssembler.<RectPackingLayoutPhases, ElkNode>create(RectPackingLayoutPhases.class);
    
    /** Our algorithm. */
    private List<ILayoutProcessor<ElkNode>> algorithm;
    
    /**
     * Calculating and applying layout to the model.
     */
    @Override
    public void layout(final ElkNode layoutGraph, final IElkProgressMonitor progressMonitor) {
        
        progressMonitor.begin("Rectangle Packing", 1);
        
//        if (progressMonitor.isLoggingEnabled()) {
//            progressMonitor.logGraph(layoutGraph, "Input");
//        }
//        // The desired aspect ratio.
//        double aspectRatio = layoutGraph.getProperty(RectPackingOptions.ASPECT_RATIO);
//        // Option to only do the initial width approximation.
//        boolean onlyFirstIteration = layoutGraph.getProperty(RectPackingOptions.ONLY_FIRST_ITERATION);
//        // Option whether the nodes should be expanded to fill the bounding rectangle.
//        boolean expandNodes = layoutGraph.getProperty(RectPackingOptions.EXPAND_NODES);
        // The padding surrounding the drawing.
        ElkPadding padding = layoutGraph.getProperty(RectPackingOptions.PADDING);
//        //  The spacing between two nodes.
//        double nodeNodeSpacing = layoutGraph.getProperty(RectPackingOptions.SPACING_NODE_NODE);
//        // Whether the nodes are compacted after the initial placement.
//        boolean compaction = layoutGraph.getProperty(RectPackingOptions.ROW_COMPACTION);
//        // Whether the nodes should be expanded to fit the aspect ratio during node expansion.
//        // Only effective if nodes are expanded.
//        boolean expandToAspectRatio = layoutGraph.getProperty(RectPackingOptions.EXPAND_TO_ASPECT_RATIO);
//        // Whether interactive layout is activ.
//        boolean interactive = layoutGraph.getProperty(RectPackingOptions.INTERACTIVE);
//        // A target width for the algorithm. If this is set the width approximation step is skipped.
//        double targetWidth = layoutGraph.getProperty(RectPackingOptions.TARGET_WIDTH);
        // Whether the size of the parent node shall not be changed.
        boolean fixedGraphSize = layoutGraph.getProperty(RectPackingOptions.NODE_SIZE_FIXED_GRAPH_SIZE);

        List<ILayoutProcessor<ElkNode>> algorithm = assembleAlgorithm(layoutGraph);
        float monitorProgress = 1.0f / algorithm.size();
        
     // Invoke each layout processor
        int slotIndex = 0;
        for (ILayoutProcessor<ElkNode> processor : algorithm) {
            if (progressMonitor.isCanceled()) {
                return;
            }
            
            // Output debug graph
            // elkjs-exclude-start
            if (progressMonitor.isLoggingEnabled()) {
                progressMonitor.logGraph(layoutGraph, slotIndex + "-Before " + processor.getClass().getSimpleName());
            }
            // elkjs-exclude-end
            
            processor.process(layoutGraph, progressMonitor.subTask(monitorProgress));
            // TODO processor might add new processes to the algorithm.
            slotIndex++;
        }

        // Graph debug output
        // elkjs-exclude-start
        if (progressMonitor.isLoggingEnabled()) {
            progressMonitor.logGraph(layoutGraph, slotIndex + "-Finished");
        }
        // elkjs-exclude-end
        
        List<ElkNode> rectangles = layoutGraph.getChildren();
//        DrawingUtil.resetCoordinates(rectangles);
//        DrawingData drawing;
//        
//        // Get minimum size of parent.
//        KVector minSize = ElkUtil.effectiveMinSizeConstraintFor(layoutGraph);
//        // Remove padding to get the space the algorithm can use.
//        minSize.x -= padding.getHorizontal();
//        minSize.y -= padding.getVertical();
//        double maxWidth = minSize.x;
//        if (targetWidth < 0 || targetWidth < minSize.x) {
//            // Initial width approximation.
//            AreaApproximation firstIt = new AreaApproximation(aspectRatio, goal, lastPlaceShift);
//            drawing = firstIt.approxBoundingBox(rectangles, nodeNodeSpacing, padding);
//            if (progressMonitor.isLoggingEnabled()) {
//                progressMonitor.logGraph(layoutGraph, "After approximation");
//            }
//        } else {
//            drawing = new DrawingData(aspectRatio, targetWidth, 0, DrawingDataDescriptor.WHOLE_DRAWING);
//        }
//        // Readd padding for next steps.
//        minSize.x += padding.getHorizontal();
//        minSize.y += padding.getVertical();
//        
//        // Placement according to approximated width.
//        if (!onlyFirstIteration) {
//            DrawingUtil.resetCoordinates(rectangles);
//            RowFillingAndCompaction secondIt = new RowFillingAndCompaction(aspectRatio, expandNodes, expandToAspectRatio, compaction, nodeNodeSpacing);
//            // Modify the initial approximation if necessary.
//            maxWidth = Math.max(minSize.x, drawing.getDrawingWidth());
//            
//            // Run placement, compaction, and expansion (if enabled).
//            drawing = secondIt.start(rectangles, maxWidth, minSize, progressMonitor, layoutGraph, padding);
//        }

        // Final touch.
        applyPadding(rectangles, padding);
        
        if (!fixedGraphSize) {
            ElkUtil.resizeNode(layoutGraph,
                    layoutGraph.getProperty(InternalProperties.DRAWING_WIDTH) + padding.getHorizontal(),
                    layoutGraph.getProperty(InternalProperties.DRAWING_HEIGHT) + padding.getVertical(), false, true);
        }

        // if requested, compute nodes's dimensions, place node labels, ports, port labels, etc.
        // Since node sizes might change during the algorithm, this has to be done at the end?
        if (!layoutGraph.getProperty(RectPackingOptions.OMIT_NODE_MICRO_LAYOUT)) {
            NodeMicroLayout.forGraph(layoutGraph).execute();
        }
        if (progressMonitor.isLoggingEnabled()) {
            progressMonitor.logGraph(layoutGraph, "Output");
        }
        progressMonitor.done();
    }

    /**
     * Shifts all rectangles to the right and bottom according to the specified padding.
     * 
     * @param rectangles
     *            list of rectangles that have been placed.
     */
    private static void applyPadding(final List<ElkNode> rectangles, ElkPadding padding) {
        for (ElkNode rect : rectangles) {
            rect.setLocation(rect.getX() + padding.getLeft(), rect.getY() + padding.getTop());
        }
    }
    


    /**
     * Configure the layout provider by assembling different layout processors.
     * 
     * @param layoutGraph The graph which shall be layout.
     * @return The list of assembled layout processors.
     */
    private List<ILayoutProcessor<ElkNode>> assembleAlgorithm(final ElkNode layoutGraph) {
        this.algorithmAssembler.reset();
        algorithmAssembler.setPhase(RectPackingLayoutPhases.P1_WIDTH_APPROXIMATION,
                layoutGraph.getProperty(RectPackingOptions.WIDTH_APPROXIMATION_STRATEGY));
        algorithmAssembler.setPhase(RectPackingLayoutPhases.P2_NODE_PLACEMENT,
                layoutGraph.getProperty(RectPackingOptions.PLACEMENT_STRATEGY));
        algorithmAssembler.setPhase(RectPackingLayoutPhases.P3_COMPACTION,
                layoutGraph.getProperty(RectPackingOptions.COMPACTION_STRATEGY));
        algorithmAssembler.setPhase(RectPackingLayoutPhases.P4_WHITESPACE_ELIMINATION,
                layoutGraph.getProperty(RectPackingOptions.WHITE_SPACE_ELIMINATION_STRATEGY));
        
        algorithmAssembler.addProcessorConfiguration(getPhaseIndependentLayoutProcessorConfiguration(layoutGraph));
        List<ILayoutProcessor<ElkNode>> processors = algorithmAssembler.build(layoutGraph);
        return processors;
    }
    

    
    /**
     * Returns an intermediate processing configuration with processors not tied to specific phases.
     * 
     * @param lgraph the layered graph to be processed. The configuration may vary depending on certain
     *               properties of the graph.
     * @return intermediate processing configuration. May be {@code null}.
     */
    private LayoutProcessorConfiguration<RectPackingLayoutPhases, ElkNode> getPhaseIndependentLayoutProcessorConfiguration(
            final ElkNode layoutGraph) {

        // Basic configuration
        LayoutProcessorConfiguration<RectPackingLayoutPhases, ElkNode> configuration =
                LayoutProcessorConfiguration.create();
        
        configuration.addBefore(RectPackingLayoutPhases.P1_WIDTH_APPROXIMATION, IntermediateProcessorStrategy.MIN_SIZE_PROCESSOR);
        
        if (layoutGraph.getProperty(RectPackingOptions.INTERACTIVE)) {
            configuration.addBefore(RectPackingLayoutPhases.P1_WIDTH_APPROXIMATION,
                    IntermediateProcessorStrategy.INTERACTIVE_NODE_REORDERER);
        }
        
        return configuration;
    }
}
