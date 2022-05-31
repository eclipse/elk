/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.topdown.test;

import static org.junit.Assert.fail;

import org.eclipse.elk.alg.test.PlainJavaInitialization;
import org.eclipse.elk.alg.topdownpacking.TopdownpackingLayoutProvider;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.junit.Test;

/**
 * Tests the topdown packing algorithm. The node sizes must be externally set and determine the layout results.
 * Unless otherwise specified, the tests expect the default values of properties to be used.
 */
public class TopdownPackingTest {

    // CHECKSTYLEOFF MagicNumber

    /**
     * Test topdown packing on an empty graph.
     */
    @Test
    public void testEmptyGraph() {
        PlainJavaInitialization.initializePlainJavaLayout();
        ElkNode graph = createEmptyGraph();
        TopdownpackingLayoutProvider layoutProvider = new TopdownpackingLayoutProvider();
        layoutProvider.layout(graph, new BasicProgressMonitor());
    }
    
    /**
     * Test topdown packing for a graph with two nodes. In this case the resulting graph
     * size should be vertically compacted.
     */
    @Test
    public void testTwoNodes() {
        fail();
    }
    
    /**
     * Test topdown packing for a graph with three nodes. In this case the third node should
     * be expanded to fill the whitespace of the parent.
     */
    @Test
    public void testThreeNodes() {
        fail();
    }
    
    /**
     * Test topdown packing for a graph with five nodes. In this case there is both vertical
     * compaction and whitespace elimination in the second row.
     */
    @Test
    public void testFiveNodes() {
        fail();
    }
    
    private static ElkNode createEmptyGraph() {
        return ElkGraphUtil.createGraph();
    }
    
    private static ElkNode createGraph(int numberOfNodes) {
        if (numberOfNodes < 0) {
            fail();
        }
        ElkNode graph = ElkGraphUtil.createGraph();
        for (int i = 0; i < numberOfNodes; i++) {
            ElkGraphUtil.createNode(graph);
        }
        return graph;
    }
}
