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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.topdownpacking.options.TopdownpackingOptions;
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
public class NodePlacer implements ILayoutPhase<TopdownPackingPhases, GridElkNode> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(GridElkNode layoutGraph, IElkProgressMonitor progressMonitor) {
        
        // Start progress monitor
        progressMonitor.begin("Node placement", 1);
        progressMonitor.log("Node placement began for node " + layoutGraph.getIdentifier());
        
        ElkPadding padding = layoutGraph.getProperty(TopdownpackingOptions.PADDING);
        double nodeNodeSpacing = layoutGraph.getProperty(TopdownpackingOptions.SPACING_NODE_NODE);
        
        progressMonitor.log("Graph Width: " + layoutGraph.getWidth());
        progressMonitor.log("Graph Height: " + layoutGraph.getHeight());    
        
        // Get the list of nodes to lay out
        List<ElkNode> nodes = new ArrayList<>(layoutGraph.getChildren());
        
        // Compute number of rows and columns to use to arrange nodes to maintain the aspect ratio
        // TODO: 0 nodes are forbidden, need to error check this or don't I because isn't called for 0 nodes anyway?
        int cols = (int) Math.ceil(Math.sqrt(nodes.size()));
        int rows;
        if (nodes.size() > cols * cols - cols) {
            rows = cols;
        } else { // N <= W^2 - W
            rows = cols - 1;
        }
        // set size of grid
        layoutGraph.setGridSize(cols, rows);
        
        progressMonitor.log(layoutGraph.getIdentifier() + "\nPlacing " + nodes.size() + " nodes in " + cols + " columns.");
        progressMonitor.done();
        progressMonitor.log("Node Arrangement done!");
        
        
        // Place the nodes
        double currX = padding.left;
        double currY = padding.top;
        int currentCol = 0;
        int currentRow = 0;
        
        // get hierarchical node sizes from parent for this layout
        double desiredNodeWidth = layoutGraph.getProperty(CoreOptions.TOPDOWN_HIERARCHICAL_NODE_WIDTH);
        double aspectRatio = layoutGraph.getProperty(CoreOptions.TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO);
        
        for (ElkNode node : nodes) {
            // Set the node's size            
            node.setDimensions(desiredNodeWidth, desiredNodeWidth/aspectRatio);
            // Set the node's coordinates
            node.setX(currX);
            node.setY(currY);
            progressMonitor.log("currX: " + currX);
            progressMonitor.log("currY: " + currY);
            // Store node's grid position
            layoutGraph.put(currentCol, currentRow, node);
            
            progressMonitor.logGraph(layoutGraph, node.getIdentifier() + " placed in (" + currentCol + "|" + currentRow + ")");
            
            // Advance the coordinates
            currX += node.getWidth() + nodeNodeSpacing;
            currentCol += 1;
            
            // go to next row if no space left
            // sizes are pre-computed so that everything fits nicely
            if (currentCol >= cols) {
                currX = padding.left;
                currY += desiredNodeWidth/aspectRatio + nodeNodeSpacing;
                currentCol = 0;
                currentRow += 1;
            }
        }
        
        progressMonitor.log("Node Placing done!");
        
        // End the progress monitor
        progressMonitor.logGraph(layoutGraph, "Graph after node placement");
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
