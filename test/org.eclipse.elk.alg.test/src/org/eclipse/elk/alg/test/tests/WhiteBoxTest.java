/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.tests;

import static org.junit.Assert.fail;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.options.LayeringStrategy;
import org.eclipse.elk.alg.layered.p2layers.NetworkSimplexLayerer;
import org.eclipse.elk.alg.test.framework.LayoutTestRunner;
import org.eclipse.elk.alg.test.framework.annotations.Algorithm;
import org.eclipse.elk.alg.test.framework.annotations.Configurator;
import org.eclipse.elk.alg.test.framework.annotations.FailIfNotExecuted;
import org.eclipse.elk.alg.test.framework.annotations.GraphProvider;
import org.eclipse.elk.alg.test.framework.annotations.OnlyOnRootNode;
import org.eclipse.elk.alg.test.framework.annotations.TestAfterProcessor;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.HierarchyHandling;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.junit.runner.RunWith;

/**
 * white box tests executed on ELK Layered The tested Algorithm is specified in TestTestClasses.
 */
@RunWith(LayoutTestRunner.class)
@Algorithm(LayeredOptions.ALGORITHM_ID)
public class WhiteBoxTest {
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Sources
    
    private static final IProperty<String> DESCRIPTION = new Property<>("wbt.description", "");

    /**
     * Returns a concrete graph to test with.
     */
    @GraphProvider
    public ElkNode compoundGraph() {
        // Create a graph with a compound node in it
        ElkNode topLevelGraph = ElkGraphUtil.createGraph();
        topLevelGraph.setProperty(CoreOptions.HIERARCHY_HANDLING, HierarchyHandling.INCLUDE_CHILDREN);
        topLevelGraph.setProperty(DESCRIPTION, "topLevelGraph");
        
        ElkNode compoundChild = ElkGraphUtil.createNode(topLevelGraph);
        compoundChild.setProperty(DESCRIPTION, "compoundChild");
        
        ElkGraphUtil.createNode(compoundChild);
        
        return topLevelGraph;
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration
    
    @Configurator
    public void configure(final ElkNode graph) {
        graph.setProperty(LayeredOptions.LAYERING_STRATEGY, LayeringStrategy.NETWORK_SIMPLEX);
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Tests
    
    private static boolean testExecuted = false;
    
    @TestAfterProcessor(NetworkSimplexLayerer.class)
    @FailIfNotExecuted
    public void test(final Object graph) {
        if (!testExecuted) {
            testExecuted = true;
            fail();
        }
        
        System.out.println("test() executed on " + ((LGraph) graph).getProperty(DESCRIPTION));
    }
    
    @TestAfterProcessor(NetworkSimplexLayerer.class)
    @OnlyOnRootNode
    @FailIfNotExecuted
    public void testOnlyOnRoot(final Object graph) {
        System.out.println("testOnlyOnRoot() executed on " + ((LGraph) graph).getProperty(DESCRIPTION));
    }
    
//    /**
//     * Checks that there are no layerless nodes left.
//     */
//    @Test
//    @TestAfterProcessor(processor = NetworkSimplexLayerer.class)
//    @TestAfterProcessor(processor = LongestPathLayerer.class)
//    @FailIfNotExecuted()
//    public void testNoLayerlessNodes(final LGraph lGraph) {
//        assertTrue("There are layerless nodes left!", lGraph.getLayerlessNodes().isEmpty());
//    }
//    
//    /**
//     * Checks that the layering is proper.
//     */
//    @Test
//    @TestAfterProcessor(processor = NetworkSimplexLayerer.class)
//    @TestAfterProcessor(processor = LongestPathLayerer.class)
//    @FailIfNotExecuted()
//    public void testProperLayering(final LGraph lGraph) {
//        for (Layer layer : lGraph) {
//            int sourceLayerIndex = layer.getIndex();
//            
//            for (LNode lnode : layer) {
//                for (LEdge ledge : lnode.getOutgoingEdges()) {
//                    int targetLayerIndex = ledge.getTarget().getNode().getLayer().getIndex();
//                    assertTrue("Edge points leftwards!", sourceLayerIndex <= targetLayerIndex);
//                }
//            }
//        }
//    }

}
