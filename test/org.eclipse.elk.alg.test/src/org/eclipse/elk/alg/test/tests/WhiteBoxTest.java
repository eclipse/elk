/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.tests;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p2layers.LongestPathLayerer;
import org.eclipse.elk.alg.layered.p2layers.NetworkSimplexLayerer;
import org.eclipse.elk.alg.test.framework.LayoutTestRunner;
import org.eclipse.elk.alg.test.framework.annotations.Algorithm;
import org.eclipse.elk.alg.test.framework.annotations.FailIfNotExecuted;
import org.eclipse.elk.alg.test.framework.annotations.Graph;
import org.eclipse.elk.alg.test.framework.annotations.ImportGraphs;
import org.eclipse.elk.alg.test.framework.annotations.RunAfterProcessor;
import org.eclipse.elk.alg.test.framework.io.AbstractResourcePath;
import org.eclipse.elk.alg.test.framework.io.ModelResourcePath;
import org.eclipse.elk.alg.test.framework.util.TestUtil;
import org.eclipse.elk.graph.ElkNode;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;

/**
 * white box tests executed on ELK Layered The tested Algorithm is specified in TestTestClasses.
 */
@RunWith(LayoutTestRunner.class)
@Algorithm(LayeredOptions.ALGORITHM_ID)
@RunAfterProcessor(processor = NetworkSimplexLayerer.class)
@RunAfterProcessor(processor = LongestPathLayerer.class)
public class WhiteBoxTest {
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Sources

    /**
     * Specifies a list of paths to test models. These will usually be paths to models in ELK's models repository.
     */
    @ImportGraphs
    public List<AbstractResourcePath> importGraphs() {
        return Lists.newArrayList(
                new ModelResourcePath("realworld/ptolemy/hierarchical/continuous_cartracking_CarTracking.elkt"));
    }
    
    /**
     * Returns a concrete graph to test with.
     */
    @Graph
    public ElkNode basicGraph() {
        return TestUtil.buildBasicGraph();
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration
    
    

    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Tests
    
    /**
     * Checks that there are no layerless nodes left.
     */
    @Test
    @FailIfNotExecuted(false)
    public void testNoLayerlessNodes(final LGraph lGraph) {
        assertTrue("There are layerless nodes left!", lGraph.getLayerlessNodes().isEmpty());
    }
    
    /**
     * Checks that the layering is proper.
     */
    @Test
    @FailIfNotExecuted(false)
    public void testProperLayering(final LGraph lGraph) {
        for (Layer layer : lGraph) {
            int sourceLayerIndex = layer.getIndex();
            
            for (LNode lnode : layer) {
                for (LEdge ledge : lnode.getOutgoingEdges()) {
                    int targetLayerIndex = ledge.getTarget().getNode().getLayer().getIndex();
                    assertTrue("Edge points leftwards!", sourceLayerIndex <= targetLayerIndex);
                }
            }
        }
    }

}
