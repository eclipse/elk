/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://rtsys.informatik.uni-kiel.de/kieler
 * 
 * Copyright 2022 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 */
package org.eclipse.elk.alg.topdownpacking;

import java.util.List;

import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * @author mka
 *
 */
public class WhitespaceEliminator implements ILayoutPhase<TopdownPackingPhases, GridElkNode> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(GridElkNode layoutGraph, IElkProgressMonitor progressMonitor) {
        // Start progress monitor
        progressMonitor.begin("Whitespace elimination", 1);
        progressMonitor.log("Whitespace elimination began for node " + layoutGraph.getIdentifier());
        
        if (layoutGraph.getWidth() == 0) {
            progressMonitor.log("Parent node has no width, skipping phase");
        }
        
        ElkPadding padding = layoutGraph.getProperty(CoreOptions.PADDING);
        
        // for each row check whether there is white space, if there is expand and shift nodes
        // TODO: optimization chance here, if parent width matches predicted width, we can skip directly to the last row
        for (int i = 0; i < layoutGraph.getRows(); i++) {
            List<ElkNode> row = layoutGraph.getRow(i);
            // check for whitespace next to last node
            ElkNode last = null;
            int lastIndex = row.size();
            while (last == null) {
                last = row.get(--lastIndex);
            }
            double rightBorder = last.getX() + last.getWidth();
            
            if (rightBorder + padding.right < layoutGraph.getWidth()) {
                progressMonitor.log("Eliminate white space in row " + i);
                double extraSpace = layoutGraph.getWidth() - (rightBorder + padding.right);
                double extraSpacePerNode = extraSpace / (lastIndex + 1);
                double accumulatedShift = 0;
                // go through all nodes in row, shift and enlargen them
                for (int j = 0; j <= lastIndex; j++) {
                    ElkNode node = row.get(j);
                    node.setX(node.getX() + accumulatedShift);
                    node.setWidth(node.getWidth() + extraSpacePerNode);
                    accumulatedShift += extraSpacePerNode;
                }
            }
             
        }
        
        progressMonitor.logGraph(layoutGraph, "Graph after whitespace elimination");
        progressMonitor.done();
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutProcessorConfiguration<TopdownPackingPhases, GridElkNode> getLayoutProcessorConfiguration(
            GridElkNode graph) {
        return LayoutProcessorConfiguration.<TopdownPackingPhases, GridElkNode>create();
    }

}
