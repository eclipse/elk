/*******************************************************************************
 * Copyright (c) 2024 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.mrtree.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.elk.alg.mrtree.options.MrTreeOptions;
import org.eclipse.elk.alg.test.PlainJavaInitialization;
import org.eclipse.elk.core.LayoutConfigurator;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.UnsupportedGraphException;
import org.eclipse.elk.core.data.LayoutAlgorithmResolver;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.junit.Test;

/**
 * Tests that the graph size calculated by MrTree is correct.
 *
 */
public class MrTreeGraphSizeTest {

    private static double DOUBLE_EQ_EPSILON = 10e-5;
    
    /**
     * Tests that the graph size is correct.
     */
    @Test
    public void GraphSizeCalculationTest() {
        PlainJavaInitialization.initializePlainJavaLayout();
        ElkNode graph = ElkGraphUtil.createGraph();
        graph.setProperty(CoreOptions.ALGORITHM, MrTreeOptions.ALGORITHM_ID);
        graph.setProperty(CoreOptions.PADDING, new ElkPadding(10));
        graph.setProperty(CoreOptions.SPACING_NODE_NODE, 10.0);
        
        ElkNode n1 = ElkGraphUtil.createNode(graph);
        n1.setDimensions(20, 20);
        ElkNode n2 = ElkGraphUtil.createNode(graph);
        n2.setDimensions(20, 20);
        ElkNode n3 = ElkGraphUtil.createNode(graph);
        n3.setDimensions(20, 20);
        
        ElkGraphUtil.createSimpleEdge(n1, n2);
        ElkGraphUtil.createSimpleEdge(n1, n3);
        
        // prepare layout engine
        LayoutConfigurator config = new LayoutConfigurator();
        ElkUtil.applyVisitors(graph, config, new LayoutAlgorithmResolver());
        // call layout with layout engine
        try {
            new RecursiveGraphLayoutEngine().layout(graph, new BasicProgressMonitor());
        } catch (UnsupportedGraphException exception) {
            fail(exception.toString());
        }
        
        assertEquals(10 + 20 + 10 + 20 + 10, graph.getWidth(), DOUBLE_EQ_EPSILON);
        assertEquals(10 + 20 + 10 + 20 + 10, graph.getHeight(), DOUBLE_EQ_EPSILON);
    }
}
