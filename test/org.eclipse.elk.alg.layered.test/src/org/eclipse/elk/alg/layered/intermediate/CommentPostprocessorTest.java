/*******************************************************************************
 * Copyright (c) 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import static org.junit.Assert.assertEquals;

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
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;

/**
 * Basic tests for the {@link CommentPostprocessor}.
 */
@RunWith(LayoutTestRunner.class)
@Algorithm(LayeredOptions.ALGORITHM_ID)
@DefaultConfiguration
public class CommentPostprocessorTest {

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Sources

    @GraphResourceProvider
    public List<AbstractResourcePath> testGraphs() {
        return Lists.newArrayList(new ModelResourcePath("tests/layered/comment_boxes/**"));
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Tests
    
    private static final double TOLERANCE = 0.1;
    
    @Test
    public void testSpacings(final ElkNode graph) {
        // Run our test on all non-comment nodes
        for (ElkNode node : graph.getChildren()) {
            // Ignore comment boxes
            if (node.getProperty(LayeredOptions.COMMENT_BOX)) {
                continue;
            }
            
            // Retrieve applicable spacings
            double commentCommentSpacing = graph.getProperty(LayeredOptions.SPACING_COMMENT_COMMENT);
            double commentNodeSpacing = graph.getProperty(LayeredOptions.SPACING_COMMENT_NODE);
            
            // We go through the top and bottom comments from left to right and keep track where we expect the
            // next comment's x coordinate to be (separately for top and bottom comments)
            double nextTopX = Double.NaN;
            double nextBottomX = Double.NaN;
            
            for (ElkNode comment : gatherConnectedCommentsSortedByX(node)) {
                if (comment.getY() < node.getY()) {
                    // Top comment; check y coordinate
                    assertEquals("Comment node spacing " + commentNodeSpacing + " violated by comment " + comment,
                            node.getY() - commentNodeSpacing - comment.getHeight(),
                            comment.getY(),
                            TOLERANCE);
                    
                    // Check x coordinate
                    if (nextTopX != Double.NaN) {
                        assertEquals(
                                "Comment comment spacing " + commentCommentSpacing + " violated by comment " + comment,
                                nextTopX,
                                comment.getX(),
                                TOLERANCE);
                    }
                    
                    nextTopX = comment.getX() + comment.getWidth() + commentCommentSpacing;
                    
                } else {
                    // Bottom comment; check y coordinate
                    assertEquals("Comment node spacing " + commentNodeSpacing + " violated by comment " + comment,
                            node.getY() + node.getHeight() + commentNodeSpacing,
                            comment.getY(),
                            TOLERANCE);
                    
                    // Check x coordinate
                    if (nextBottomX != Double.NaN) {
                        assertEquals(
                                "Comment comment spacing " + commentCommentSpacing + " violated by comment " + comment,
                                nextBottomX,
                                comment.getX(),
                                TOLERANCE);
                    }
                    
                    nextBottomX = comment.getX() + comment.getWidth() + commentCommentSpacing;
                }
            }
        }
    }

    /**
     * Returns the comments connected to the given node, sorted by x coordinate.
     */
    private List<ElkNode> gatherConnectedCommentsSortedByX(final ElkNode node) {
        List<ElkNode> comments = new ArrayList<>();
        
        node.getIncomingEdges().stream()
            .map(inEdge -> inEdge.getSources().get(0))
            .map(shape -> ElkGraphUtil.connectableShapeToNode(shape))
            .filter(comment -> comment.getProperty(LayeredOptions.COMMENT_BOX))
            .forEach(comment -> comments.add(comment));
        
        node.getOutgoingEdges().stream()
            .map(outEdge -> outEdge.getTargets().get(0))
            .map(shape -> ElkGraphUtil.connectableShapeToNode(shape))
            .filter(comment -> comment.getProperty(LayeredOptions.COMMENT_BOX))
            .forEach(comment -> comments.add(comment));
        
        comments.sort((n1, n2) -> Double.compare(n1.getX(), n2.getX()));
        
        return comments;
    }
    
}
