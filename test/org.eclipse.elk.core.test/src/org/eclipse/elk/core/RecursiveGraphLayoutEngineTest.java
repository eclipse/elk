/*******************************************************************************
 * Copyright (c) 2018 TypeFox GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core;

import static org.junit.Assert.*;

import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.UnsupportedConfigurationException;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.junit.Test;

/**
 * Tests for {@link RecursiveGraphLayoutEngine}.
 */
public class RecursiveGraphLayoutEngineTest {
    
    @Test
    public void testResolvedGraph() {
        Graph graph = new Graph();
        LayoutAlgorithmData algorithmData = LayoutMetaDataService.getInstance().getAlgorithmData("org.eclipse.elk.box");
        graph.root.setProperty(CoreOptions.RESOLVED_ALGORITHM, algorithmData);
        RecursiveGraphLayoutEngine engine = new RecursiveGraphLayoutEngine();
        engine.layout(graph.root, new BasicProgressMonitor());
        
        assertTrue(graph.root.getWidth() > 0);
        assertTrue(graph.root.getHeight() > 0);
    }
    
    @Test
    public void testUnresolvedGraph() {
        Graph graph = new Graph();
        graph.root.setProperty(CoreOptions.ALGORITHM, "org.eclipse.elk.box");
        RecursiveGraphLayoutEngine engine = new RecursiveGraphLayoutEngine();
        engine.layout(graph.root, new BasicProgressMonitor());
        
        assertTrue(graph.root.getWidth() > 0);
        assertTrue(graph.root.getHeight() > 0);
    }
    
    @Test(expected = UnsupportedConfigurationException.class)
    public void testUnknownAlgorithm() {
        Graph graph = new Graph();
        graph.root.setProperty(CoreOptions.ALGORITHM, "foo.Bar");
        RecursiveGraphLayoutEngine engine = new RecursiveGraphLayoutEngine();
        engine.layout(graph.root, new BasicProgressMonitor());
    }
    
    private class Graph {
        ElkNode root;
        private ElkNode n1;
        private ElkNode n2;
        public Graph() {
            root = ElkGraphUtil.createGraph();
            n1 = ElkGraphUtil.createNode(root);
            n1.setDimensions(10, 10);
            n2 = ElkGraphUtil.createNode(root);
            n2.setDimensions(10, 10);
        }
    }
    
}
