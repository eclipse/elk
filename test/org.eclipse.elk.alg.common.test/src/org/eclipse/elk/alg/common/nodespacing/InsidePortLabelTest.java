package org.eclipse.elk.alg.common.nodespacing;

import static org.junit.Assert.assertFalse;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.test.framework.LayoutTestRunner;
import org.eclipse.elk.alg.test.framework.annotations.Algorithm;
import org.eclipse.elk.alg.test.framework.annotations.DefaultConfiguration;
import org.eclipse.elk.alg.test.framework.annotations.GraphResourceProvider;
import org.eclipse.elk.alg.test.framework.io.AbstractResourcePath;
import org.eclipse.elk.alg.test.framework.io.ModelResourcePath;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.ElkPort;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;

/**
 * Tests whether node size computation handles inside port labels properly. Since the test graph contains a node
 * without a node label, we need to deactivate the default configuration for nodes.
 */
@RunWith(LayoutTestRunner.class)
@Algorithm(LayeredOptions.ALGORITHM_ID)
@DefaultConfiguration(edges = false, nodes = false, ports = true)
public class InsidePortLabelTest {

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Sources

    @GraphResourceProvider
    public List<AbstractResourcePath> testGraphs() {
        return Lists.newArrayList(new ModelResourcePath("tests/core/node_size/inside_port_labels.elkt"));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Tests

    /**
     * Check for each node that no two labels overlap.
     */
    @Test
    public void testNoLabelOverlaps(final ElkNode graph) {
        graph.getChildren().stream()
            .map(node -> assembleLabelRectangles(node))
            .forEach(labels -> ensureNoOverlaps(labels));
    }
    
    private List<Rectangle2D> assembleLabelRectangles(final ElkNode node) {
        List<Rectangle2D> labelRects = new ArrayList<>();
        
        // Add all node labels
        node.getLabels().stream()
                .map(label -> new Rectangle2D.Double(label.getX(), label.getY(), label.getWidth(), label.getHeight()))
                .forEach(rect -> labelRects.add(rect));
        
        // Add labels of each port (convert to absolute positions)
        for (ElkPort port : node.getPorts()) {
            port.getLabels().stream()
                .map(label-> new Rectangle2D.Double(
                        port.getX() + label.getX(), port.getY() + label.getY(), label.getWidth(), label.getHeight()))
                .forEach(rect -> labelRects.add(rect));
        }
        
        return labelRects;
    }
    
    private void ensureNoOverlaps(final List<Rectangle2D> labels) {
        for (int first = 0; first < labels.size(); first++) {
            Rectangle2D firstRect = labels.get(first);
            
            for (int second = first + 1; second < labels.size(); second++) {
                Rectangle2D secondRect = labels.get(second);
                
                assertFalse("Detected intersection between " + firstRect + " and " + secondRect,
                        firstRect.intersects(secondRect));
            }
        }
    }

}
