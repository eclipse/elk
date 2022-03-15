package org.eclipse.elk.alg.topdownpacking;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.topdownpacking.options.TopdownpackingOptions;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * A simple box packing algorithm that places nodes as evenly sized rectangles. This algorithm uses fixed sizes and 
 * therefore requires the option 'Topdown Layout to be set to true to result in a correct layout.
 * 
 * New more specific usage idea: for sccharts in topdown layout instead of rectpacking. parent state should already
 * be sized to fit this layout. Size can be predicted. This layout should NOT be scaled down, instead only its child layout,
 * which in the case of SCCharts is a layered layout.
 */
public class TopdownpackingLayoutProvider extends AbstractLayoutProvider {

    @Override
    public void layout(ElkNode layoutGraph, IElkProgressMonitor progressMonitor) {
        // Start progress monitor
        progressMonitor.begin("Topdownpacking", 1);
        progressMonitor.log("Algorithm began for node " + layoutGraph.getIdentifier());
                
        ElkPadding padding = layoutGraph.getProperty(TopdownpackingOptions.PADDING);
        double nodeNodeSpacing = layoutGraph.getProperty(TopdownpackingOptions.SPACING_NODE_NODE);
        
        progressMonitor.log("Graph Width: " + layoutGraph.getWidth());
        progressMonitor.log("Graph Height: " + layoutGraph.getHeight());    
        
        // Get the list of nodes to lay out
        List<ElkNode> nodes = new ArrayList<>(layoutGraph.getChildren());
        
        IElkProgressMonitor nodeArrangementMonitor = progressMonitor.subTask(1);
        nodeArrangementMonitor.begin("Node Arrangement", 1);
        // Compute number of rows and columns to use to arrange nodes to maintain the aspect ratio
        // TODO: 0 nodes are forbidden, need to error check this
        int cols = (int) Math.ceil(Math.sqrt(nodes.size()));
        
        nodeArrangementMonitor.log(layoutGraph.getIdentifier() + "\nPlacing " + nodes.size() + " nodes in " + cols + " columns.");
        nodeArrangementMonitor.done();
        progressMonitor.log("Node Arrangement done!");
        
        // Create a sub monitor for node placement
        IElkProgressMonitor nodePlacingMonitor = progressMonitor.subTask(1);
        nodePlacingMonitor.begin("Node Spacing", nodes.size());
        
        // Place the nodes
        double currX = padding.left;
        double currY = padding.top;
        int currentCol = 0;
        int currentRow = 0;
        
        // for keeping track of child area, (omit padding)
        double totalWidth = padding.left;
        double totalHeight = padding.top;
        
        for (ElkNode node : nodes) {
            // Set the node's size
            double desiredNodeWidth = node.getProperty(CoreOptions.TOPDOWN_REGION_WIDTH);
            double aspectRatio = node.getProperty(CoreOptions.TOPDOWN_REGION_ASPECT_RATIO);
            node.setDimensions(desiredNodeWidth, desiredNodeWidth/aspectRatio);
            System.out.println("During TDP: " + node.getIdentifier() + " " + node.getWidth() + " " + node.getHeight());
            // Set the node's coordinates
            node.setX(currX);
            node.setY(currY);
            nodePlacingMonitor.log("currX: " + currX);
            nodePlacingMonitor.log("currY: " + currY);
            
            if (currY + desiredNodeWidth/aspectRatio > totalHeight) {
                totalHeight = currY + desiredNodeWidth/aspectRatio;
            }
            
            nodePlacingMonitor.logGraph(layoutGraph, node.getIdentifier() + " placed in (" + currentCol + "|" + currentRow + ")");
            
            // Advance the coordinates
            currX += node.getWidth() + nodeNodeSpacing;
            if (currX > totalWidth) {
                totalWidth = currX;
            }
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
        
        // TODO: later this stuff should be covered by white space elimination anyway so can be removed here
        /** REMOVE THIS, IT'S A LITTLE MORE COMPLICATED NOW, BECAUSE SCALING IS DONE EXTERNALLY
        // if the last row is not full, do some adjustments to the nodes
        // currentCol is at this point the position after the last placed node
        if (currentCol < cols && currentCol > 0) {
            // determine center of placed nodes
            double occupiedWidth = 0;
            for (int i = nodes.size() - currentCol; i < nodes.size(); i++ ) {
                occupiedWidth += nodes.get(i).getWidth() + nodeNodeSpacing;
            }
            // remove last spacing
            occupiedWidth -= nodeNodeSpacing;
            double midPoint = padding.left + (occupiedWidth/2);
            
            // move nodes so that center matches graph center
            double xShift = (layoutGraph.getWidth()/2) - midPoint;
            nodePlacingMonitor.log("occupiedWidth: " + occupiedWidth);
            nodePlacingMonitor.log("midPoint: " + midPoint);
            nodePlacingMonitor.log("Shifting last row by: " + xShift);
            for (int i = nodes.size() - currentCol; i < nodes.size(); i++ ) {
                nodes.get(i).setX(nodes.get(i).getX() + xShift);
            }
        }
        */
        
        
        // store child area as property, remove final superfluous nodeNodeSpacing
        if (totalWidth > padding.left) {
            totalWidth -= nodeNodeSpacing;
        }
        totalWidth += padding.right;
        
        totalHeight += padding.bottom;
        // TODO: not sure whether this is actually necessary, because the region layout area is never scaled, so we don't care about its size
        layoutGraph.setProperty(CoreOptions.CHILD_AREA_WIDTH, totalWidth);
        layoutGraph.setProperty(CoreOptions.CHILD_AREA_HEIGHT, totalHeight);
        
        
        // Close the sub monitor
        nodePlacingMonitor.done();
        progressMonitor.log("Node Placing done!");
        
        // NO SUPPORT FOR EDGES
        
        // End the progress monitor
        progressMonitor.log("Algorithm executed");
        progressMonitor.logGraph(layoutGraph, "Final graph");
        progressMonitor.done();
    }
}
