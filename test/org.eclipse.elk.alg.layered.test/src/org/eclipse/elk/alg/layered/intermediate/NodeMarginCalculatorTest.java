/*******************************************************************************
 * Copyright (c) 2013, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.test.framework.LayoutTestRunner;
import org.eclipse.elk.alg.test.framework.annotations.Algorithm;
import org.eclipse.elk.alg.test.framework.annotations.DefaultConfiguration;
import org.eclipse.elk.alg.test.framework.annotations.GraphResourceProvider;
import org.eclipse.elk.alg.test.framework.annotations.TestAfterProcessor;
import org.eclipse.elk.alg.test.framework.io.AbstractResourcePath;
import org.eclipse.elk.alg.test.framework.io.FileExtensionFilter;
import org.eclipse.elk.alg.test.framework.io.ModelResourcePath;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;


/**
 * Tests node margin calculation, which is spread out over several processors.
 */
@RunWith(LayoutTestRunner.class)
@Algorithm(LayeredOptions.ALGORITHM_ID)
@DefaultConfiguration()
public class NodeMarginCalculatorTest {

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Sources

    @GraphResourceProvider
    public List<AbstractResourcePath> testGraphs() {
        return Lists.newArrayList(
                new ModelResourcePath("realworld/ptolemy/flattened/**/").withFilter(new FileExtensionFilter("elkg")));
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Tests
    
    /**
     * This method could be split into several, one for each processor. We could then check whether the margins are
     * correct.
     */
    @TestAfterProcessor(InnermostNodeMarginCalculator.class)
    @TestAfterProcessor(SelfLoopRouter.class)
    @TestAfterProcessor(CommentNodeMarginCalculator.class)
    @TestAfterProcessor(EndLabelPreprocessor.class)
    public void testNodeMargins(final Object graph) {
        LGraph lGraph = (LGraph) graph;
        
        for (Layer layer : lGraph) {
            for (LNode node : layer) {
                assertTrue(node.getMargin() != null);
                assertTrue(node.getMargin().top >= 0);
                assertTrue(node.getMargin().right >= 0);
                assertTrue(node.getMargin().bottom >= 0);
                assertTrue(node.getMargin().left >= 0);
            }
        }
    }

}
