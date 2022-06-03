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

import org.eclipse.elk.alg.test.PlainJavaInitialization;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.TopdownNodeTypes;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.junit.Test;

/**
 * Tests for checking that topdown layout works correctly in different configurations.
 */
public class TopdownLayoutTest {
    
    // CHECKSTYLEOFF MagicNumber
    
    /**
     * Tests a topdown layout with two hierarchical levels. All nodes are hierarchical nodes.
     */
    @Test
    public void testTwoLevelLayout() {
        PlainJavaInitialization.initializePlainJavaLayout();
        ElkNode graph = ElkGraphUtil.createGraph();
        graph.setProperty(CoreOptions.TOPDOWN_LAYOUT, true);
        graph.setProperty(CoreOptions.TOPDOWN_NODE_TYPE, TopdownNodeTypes.ROOT_NODE);
        
        ElkNode toplevel = ElkGraphUtil.createNode(graph);
        toplevel.setProperty(CoreOptions.TOPDOWN_LAYOUT, true);
        toplevel.setProperty(CoreOptions.TOPDOWN_NODE_TYPE, TopdownNodeTypes.HIERARCHICAL_NODE);
        toplevel.setProperty(CoreOptions.NODE_SIZE_FIXED_GRAPH_SIZE, true);
        toplevel.setProperty(CoreOptions.ALGORITHM, "org.eclipse.elk.layered");
        toplevel.setProperty(CoreOptions.PADDING, new ElkPadding());
        toplevel.setProperty(CoreOptions.SPACING_NODE_NODE, 0.0);
        
        ElkNode child1 = ElkGraphUtil.createNode(toplevel);
        child1.setProperty(CoreOptions.TOPDOWN_LAYOUT, true);
        child1.setProperty(CoreOptions.TOPDOWN_NODE_TYPE, TopdownNodeTypes.HIERARCHICAL_NODE);
        child1.setProperty(CoreOptions.NODE_SIZE_FIXED_GRAPH_SIZE, true);
        
        ElkNode child2 = ElkGraphUtil.createNode(toplevel);
        child2.setProperty(CoreOptions.TOPDOWN_LAYOUT, true);
        child2.setProperty(CoreOptions.TOPDOWN_NODE_TYPE, TopdownNodeTypes.HIERARCHICAL_NODE);
        child2.setProperty(CoreOptions.NODE_SIZE_FIXED_GRAPH_SIZE, true);
        
        // calle layout with layout engine
        
    }
    

}
