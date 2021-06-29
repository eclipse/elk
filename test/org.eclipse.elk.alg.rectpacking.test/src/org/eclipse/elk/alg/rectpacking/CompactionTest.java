package org.eclipse.elk.alg.rectpacking;
/*******************************************************************************
 * Copyright (c) 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/


import static org.junit.Assert.assertEquals;

import org.eclipse.elk.alg.rectpacking.options.RectPackingOptions;
import org.eclipse.elk.alg.test.PlainJavaInitialization;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the various compaction movements.
 * This implicitly tests the placement step and spacing too.
 * 
 * For this tests we remove the width approximation step and set a target width to make the different cases easier to
 * understand.
 *
 */
public class CompactionTest {

    @BeforeClass
    public static void init() {
        PlainJavaInitialization.initializePlainJavaLayout();
    }

    /**
     * Test the compaction step for the case that a block, which was in the next row after placement, is placed on top
     * of the current block forming a stack of blocks.
     */
    @Test
    public void testPlaceBlockFromNextRowOnTop() {
        ElkNode parent = ElkGraphUtil.createGraph();
        ElkNode n1 = ElkGraphUtil.createNode(parent);
        ElkNode n2 = ElkGraphUtil.createNode(parent);
        ElkNode n3 = ElkGraphUtil.createNode(parent);
        ElkNode n4 = ElkGraphUtil.createNode(parent);
        ElkNode n5 = ElkGraphUtil.createNode(parent);
        ElkNode n6 = ElkGraphUtil.createNode(parent);
        ElkNode n7 = ElkGraphUtil.createNode(parent);
        parent.setProperty(CoreOptions.ALGORITHM, RectPackingOptions.ALGORITHM_ID);
        parent.setProperty(RectPackingOptions.TARGET_WIDTH, 110.0);
        parent.setProperty(CoreOptions.SPACING_NODE_NODE, 10.0);
        parent.setProperty(CoreOptions.PADDING, new ElkPadding(0.0));
        n1.setDimensions(30, 70);
        n2.setDimensions(30, 10);
        n3.setDimensions(30, 10);
        n4.setDimensions(30, 50);
        n5.setDimensions(30, 50);
        n6.setDimensions(100, 10);
        n7.setDimensions(100, 10);
        
        // ___  ___  ___
        //|   ||___||___|
        //|   | ___  ___
        //|   ||   ||   |
        //|   ||   ||   |
        //|   ||   ||   |
        //|   ||   ||   |
        //|___||___||___|
        // ____________
        //|____________|
        // ____________
        //|____________|
        //
        RectPackingLayoutProvider layoutProvider = new RectPackingLayoutProvider();
        layoutProvider.layout(parent, new BasicProgressMonitor());
        assertEquals("", 110.0, parent.getWidth(), 1);
        assertEquals("", 110.0, parent.getHeight(), 1);
        assertEquals("", 0.0, n1.getX(), 1);
        assertEquals("", 0.0, n1.getY(), 1);
        assertEquals("", 40.0, n2.getX(), 1);
        assertEquals("", 0.0, n2.getY(), 1);
        assertEquals("", 80.0, n3.getX(), 1);
        assertEquals("", 0.0, n3.getY(), 1);
        assertEquals("", 40.0, n4.getX(), 1);
        assertEquals("", 20.0, n4.getY(), 1);
        assertEquals("", 80.0, n5.getX(), 1);
        assertEquals("", 20.0, n5.getY(), 1);
        assertEquals("", 0.0, n6.getX(), 1);
        assertEquals("", 80.0, n6.getY(), 1);        
    }

    /**
     * Test the compaction step for the case that a block, which was in the next row after placement, is tried to be
     * placed on top of the current block but the block (and also not the first node) does not fit.
     */
    @Test
    public void testPlaceBlockFromNextRowOnTopDoesNotWork() {
        ElkNode parent = ElkGraphUtil.createGraph();
        ElkNode n1 = ElkGraphUtil.createNode(parent);
        ElkNode n2 = ElkGraphUtil.createNode(parent);
        ElkNode n3 = ElkGraphUtil.createNode(parent);
        ElkNode n4 = ElkGraphUtil.createNode(parent);
        ElkNode n5 = ElkGraphUtil.createNode(parent);
        ElkNode n6 = ElkGraphUtil.createNode(parent);
        ElkNode n7 = ElkGraphUtil.createNode(parent);
        parent.setProperty(CoreOptions.ALGORITHM, RectPackingOptions.ALGORITHM_ID);
        parent.setProperty(RectPackingOptions.TARGET_WIDTH, 110.0);
        parent.setProperty(CoreOptions.SPACING_NODE_NODE, 10.0);
        parent.setProperty(CoreOptions.PADDING, new ElkPadding(0.0));
        n1.setDimensions(30, 70);
        n2.setDimensions(40, 10);
        n3.setDimensions(30, 10);
        n4.setDimensions(30, 50);
        n5.setDimensions(30, 50);
        n6.setDimensions(100, 10);
        n7.setDimensions(100, 10);
        
        // ___  ____
        //|   ||____|
        //|   | ___ 
        //|   ||___|
        //|   |
        //|   |
        //|   |
        //|___|
        // ___  ___
        //|   ||   |
        //|   ||   |
        //|   ||   |
        //|   ||   |
        //|___||___|
        // _____________
        //|_____________|
        // _____________
        //|_____________|
        //
        RectPackingLayoutProvider layoutProvider = new RectPackingLayoutProvider();
        layoutProvider.layout(parent, new BasicProgressMonitor());
        assertEquals("", 100.0, parent.getWidth(), 1);
        assertEquals("", 170.0, parent.getHeight(), 1);
        assertEquals("", 0.0, n1.getX(), 1);
        assertEquals("", 0.0, n1.getY(), 1);
        assertEquals("", 40.0, n2.getX(), 1);
        assertEquals("", 0.0, n2.getY(), 1);
        assertEquals("", 40.0, n3.getX(), 1);
        assertEquals("", 20.0, n3.getY(), 1);
        assertEquals("", 0.0, n4.getX(), 1);
        assertEquals("", 80.0, n4.getY(), 1);
        assertEquals("", 40.0, n5.getX(), 1);
        assertEquals("", 80.0, n5.getY(), 1);       
    }

    /**
     * Test how a block is absorbed in another block from the next row.
     */
    @Test
    public void testAbsorbBlock() {
        ElkNode parent = ElkGraphUtil.createGraph();
        ElkNode n1 = ElkGraphUtil.createNode(parent);
        ElkNode n2 = ElkGraphUtil.createNode(parent);
        ElkNode n3 = ElkGraphUtil.createNode(parent);
        ElkNode n4 = ElkGraphUtil.createNode(parent);
        ElkNode n5 = ElkGraphUtil.createNode(parent);
        ElkNode n6 = ElkGraphUtil.createNode(parent);
        ElkNode n7 = ElkGraphUtil.createNode(parent);
        parent.setProperty(CoreOptions.ALGORITHM, RectPackingOptions.ALGORITHM_ID);
        parent.setProperty(RectPackingOptions.TARGET_WIDTH, 110.0);
        parent.setProperty(CoreOptions.SPACING_NODE_NODE, 10.0);
        parent.setProperty(CoreOptions.PADDING, new ElkPadding(0.0));
        n1.setDimensions(30, 70);
        n2.setDimensions(30, 30);
        n3.setDimensions(30, 30);
        n4.setDimensions(30, 30);
        n5.setDimensions(30, 30);
        n6.setDimensions(100, 10);
        n7.setDimensions(100, 10);
        
        // ___  ___  ___
        //|   ||   ||   |
        //|   ||   ||   | 
        //|   ||___||___|
        //|   | ___  ___
        //|   ||   ||   |
        //|   ||   ||   |
        //|___||___||___|
        // ____________
        //|____________|
        // ____________
        //|____________|
        //
        RectPackingLayoutProvider layoutProvider = new RectPackingLayoutProvider();
        layoutProvider.layout(parent, new BasicProgressMonitor());
        assertEquals("", 110.0, parent.getWidth(), 1);
        assertEquals("", 110.0, parent.getHeight(), 1);
        assertEquals("", 0.0, n1.getX(), 1);
        assertEquals("", 0.0, n1.getY(), 1);
        assertEquals("", 40.0, n2.getX(), 1);
        assertEquals("", 0.0, n2.getY(), 1);
        assertEquals("", 80.0, n3.getX(), 1);
        assertEquals("", 0.0, n3.getY(), 1);
        assertEquals("", 40.0, n4.getX(), 1);
        assertEquals("", 40.0, n4.getY(), 1);
        assertEquals("", 80.0, n5.getX(), 1);
        assertEquals("", 40.0, n5.getY(), 1);
        assertEquals("", 0.0, n6.getX(), 1);
        assertEquals("", 80.0, n6.getY(), 1);        
    }
    

    /**
     * Test how a block is compacted inside a row to save space.
     */
    @Test
    public void testCompactBlock() {
        ElkNode parent = ElkGraphUtil.createGraph();
        ElkNode n1 = ElkGraphUtil.createNode(parent);
        ElkNode n2 = ElkGraphUtil.createNode(parent);
        ElkNode n3 = ElkGraphUtil.createNode(parent);
        ElkNode n4 = ElkGraphUtil.createNode(parent);
        ElkNode n5 = ElkGraphUtil.createNode(parent);
        parent.setProperty(CoreOptions.ALGORITHM, RectPackingOptions.ALGORITHM_ID);
        parent.setProperty(RectPackingOptions.TARGET_WIDTH, 190.0);
        parent.setProperty(CoreOptions.SPACING_NODE_NODE, 10.0);
        parent.setProperty(CoreOptions.PADDING, new ElkPadding(0.0));
        n1.setDimensions(30, 70);
        n2.setDimensions(30, 30);
        n3.setDimensions(30, 30);
        n4.setDimensions(30, 30);
        n5.setDimensions(30, 30);
        
        // ___  ___  ___
        //|   ||   ||   |
        //|   ||   ||   | 
        //|   ||___||___|
        //|   | ___  ___
        //|   ||   ||   |
        //|   ||   ||   |
        //|___||___||___|
        //
        RectPackingLayoutProvider layoutProvider = new RectPackingLayoutProvider();
        layoutProvider.layout(parent, new BasicProgressMonitor());
        assertEquals("", 110.0, parent.getWidth(), 1);
        assertEquals("", 70.0, parent.getHeight(), 1);
        assertEquals("", 0.0, n1.getX(), 1);
        assertEquals("", 0.0, n1.getY(), 1);
        assertEquals("", 40.0, n2.getX(), 1);
        assertEquals("", 0.0, n2.getY(), 1);
        assertEquals("", 80.0, n3.getX(), 1);
        assertEquals("", 0.0, n3.getY(), 1);
        assertEquals("", 40.0, n4.getX(), 1);
        assertEquals("", 40.0, n4.getY(), 1);
        assertEquals("", 80.0, n5.getX(), 1);
        assertEquals("", 40.0, n5.getY(), 1);      
    }

    /**
     * Test how a block is only partly absorbed by a previous block.
     */
    @Test
    public void testSplitBlock() {
        ElkNode parent = ElkGraphUtil.createGraph();
        ElkNode n1 = ElkGraphUtil.createNode(parent);
        ElkNode n2 = ElkGraphUtil.createNode(parent);
        ElkNode n3 = ElkGraphUtil.createNode(parent);
        ElkNode n4 = ElkGraphUtil.createNode(parent);
        ElkNode n5 = ElkGraphUtil.createNode(parent);
        ElkNode n6 = ElkGraphUtil.createNode(parent);
        ElkNode n7 = ElkGraphUtil.createNode(parent);
        parent.setProperty(CoreOptions.ALGORITHM, RectPackingOptions.ALGORITHM_ID);
        parent.setProperty(RectPackingOptions.TARGET_WIDTH, 110.0);
        parent.setProperty(CoreOptions.SPACING_NODE_NODE, 10.0);
        parent.setProperty(CoreOptions.PADDING, new ElkPadding(0.0));
        n1.setDimensions(30, 70);
        n2.setDimensions(30, 30);
        n3.setDimensions(30, 30);
        n4.setDimensions(30, 30);
        n5.setDimensions(30, 30);
        n6.setDimensions(30, 30);
        n7.setDimensions(100, 10);
        
        // ___  ___  ___
        //|   ||   ||   |
        //|   ||   ||   | 
        //|   ||___||___|
        //|   | ___  ___
        //|   ||   ||   |
        //|   ||   ||   |
        //|___||___||___|
        // ___
        //|   |
        //|   |
        //|___|
        // ___________
        //|___________|
        RectPackingLayoutProvider layoutProvider = new RectPackingLayoutProvider();
        layoutProvider.layout(parent, new BasicProgressMonitor());
        assertEquals("", 110.0, parent.getWidth(), 1);
        assertEquals("", 130.0, parent.getHeight(), 1);
        assertEquals("", 0.0, n1.getX(), 1);
        assertEquals("", 0.0, n1.getY(), 1);
        assertEquals("", 40.0, n2.getX(), 1);
        assertEquals("", 0.0, n2.getY(), 1);
        assertEquals("", 80.0, n3.getX(), 1);
        assertEquals("", 0.0, n3.getY(), 1);
        assertEquals("", 40.0, n4.getX(), 1);
        assertEquals("", 40.0, n4.getY(), 1);
        assertEquals("", 80.0, n5.getX(), 1);
        assertEquals("", 40.0, n5.getY(), 1);
        assertEquals("", 0.0, n6.getX(), 1);
        assertEquals("", 80.0, n6.getY(), 1);
        assertEquals("", 0.0, n7.getX(), 1);
        assertEquals("", 120.0, n7.getY(), 1);           
    }   


    /**
     * Test the compaction step for the case that a block, which was in the next row after placement, is placed to the
     * right of the current block forming a new stack of blocks.
     */
    @Test
    public void testPlaceBlockFromNextRowRight() {
        ElkNode parent = ElkGraphUtil.createGraph();
        ElkNode n1 = ElkGraphUtil.createNode(parent);
        ElkNode n2 = ElkGraphUtil.createNode(parent);
        ElkNode n3 = ElkGraphUtil.createNode(parent);
        ElkNode n4 = ElkGraphUtil.createNode(parent);
        parent.setProperty(CoreOptions.ALGORITHM, RectPackingOptions.ALGORITHM_ID);
        parent.setProperty(RectPackingOptions.TARGET_WIDTH, 110.0);
        parent.setProperty(CoreOptions.SPACING_NODE_NODE, 10.0);
        parent.setProperty(CoreOptions.PADDING, new ElkPadding(0.0));
        n1.setDimensions(30, 70);
        n2.setDimensions(30, 30);
        n3.setDimensions(20, 20);
        n4.setDimensions(30, 70);
        
        // ___  ___  ___
        //|   ||   ||   |
        //|   ||   ||   | 
        //|   ||___||   |
        //|   | ___ |   |
        //|   ||   ||   |
        //|   ||   ||   |
        //|___||___||___|
        RectPackingLayoutProvider layoutProvider = new RectPackingLayoutProvider();
        layoutProvider.layout(parent, new BasicProgressMonitor());
        assertEquals("", 110.0, parent.getWidth(), 1);
        assertEquals("", 70.0, parent.getHeight(), 1);
        assertEquals("", 0.0, n1.getX(), 1);
        assertEquals("", 0.0, n1.getY(), 1);
        assertEquals("", 40.0, n2.getX(), 1);
        assertEquals("", 0.0, n2.getY(), 1);
        assertEquals("", 40.0, n3.getX(), 1);
        assertEquals("", 40.0, n3.getY(), 1);
        assertEquals("", 80.0, n4.getX(), 1);
        assertEquals("", 0.0, n4.getY(), 1);         
    }
    
    /**
     * Test the compaction step for the case that a block, which was in the current row after placement, is placed to
     * the right of the current block forming a new stack of blocks.
     * In this case the previous block is compacted to save horizontal space in the row.
     */
    @Test
    public void testPlaceBlockFromCurrentRowRight() {
        ElkNode parent = ElkGraphUtil.createGraph();
        ElkNode n1 = ElkGraphUtil.createNode(parent);
        ElkNode n2 = ElkGraphUtil.createNode(parent);
        ElkNode n3 = ElkGraphUtil.createNode(parent);
        ElkNode n4 = ElkGraphUtil.createNode(parent);
        parent.setProperty(CoreOptions.ALGORITHM, RectPackingOptions.ALGORITHM_ID);
        parent.setProperty(RectPackingOptions.TARGET_WIDTH, 150.0);
        parent.setProperty(CoreOptions.SPACING_NODE_NODE, 10.0);
        parent.setProperty(CoreOptions.PADDING, new ElkPadding(0.0));
        n1.setDimensions(30, 70);
        n2.setDimensions(30, 30);
        n3.setDimensions(20, 20);
        n4.setDimensions(30, 70);
        
        // ___  ___  ___
        //|   ||   ||   |
        //|   ||   ||   | 
        //|   ||___||   |
        //|   | ___ |   |
        //|   ||   ||   |
        //|   ||   ||   |
        //|___||___||___|
        RectPackingLayoutProvider layoutProvider = new RectPackingLayoutProvider();
        layoutProvider.layout(parent, new BasicProgressMonitor());
        assertEquals("", 110.0, parent.getWidth(), 1);
        assertEquals("", 70.0, parent.getHeight(), 1);
        assertEquals("", 0.0, n1.getX(), 1);
        assertEquals("", 0.0, n1.getY(), 1);
        assertEquals("", 40.0, n2.getX(), 1);
        assertEquals("", 0.0, n2.getY(), 1);
        assertEquals("", 40.0, n3.getX(), 1);
        assertEquals("", 40.0, n3.getY(), 1);
        assertEquals("", 80.0, n4.getX(), 1);
        assertEquals("", 0.0, n4.getY(), 1);         
    }
    
    /**
     * Test the compaction step for the case that a block, which was in the current row after placement, is placed on
     * top of the current block forming a new stack of blocks.
     * In this case the previous block is compacted to save vertically space in the row.
     */
    @Test
    public void testPlaceBlockFromCurrentRowOnTop() {
        ElkNode parent = ElkGraphUtil.createGraph();
        ElkNode n1 = ElkGraphUtil.createNode(parent);
        ElkNode n2 = ElkGraphUtil.createNode(parent);
        ElkNode n3 = ElkGraphUtil.createNode(parent);
        ElkNode n4 = ElkGraphUtil.createNode(parent);
        parent.setProperty(CoreOptions.ALGORITHM, RectPackingOptions.ALGORITHM_ID);
        parent.setProperty(RectPackingOptions.TARGET_WIDTH, 150.0);
        parent.setProperty(CoreOptions.SPACING_NODE_NODE, 10.0);
        parent.setProperty(CoreOptions.PADDING, new ElkPadding(0.0));
        n1.setDimensions(30, 70);
        n2.setDimensions(30, 10);
        n3.setDimensions(30, 10);
        n4.setDimensions(30, 50);
        
        // ___  ___  ___
        //|   ||___||___|
        //|   | ___ 
        //|   ||   |
        //|   ||   |
        //|   ||   |
        //|   ||   |
        //|___||___|
        //
        RectPackingLayoutProvider layoutProvider = new RectPackingLayoutProvider();
        layoutProvider.layout(parent, new BasicProgressMonitor());
        assertEquals("", 110.0, parent.getWidth(), 1);
        assertEquals("", 70.0, parent.getHeight(), 1);
        assertEquals("", 0.0, n1.getX(), 1);
        assertEquals("", 0.0, n1.getY(), 1);
        assertEquals("", 40.0, n2.getX(), 1);
        assertEquals("", 0.0, n2.getY(), 1);
        assertEquals("", 80.0, n3.getX(), 1);
        assertEquals("", 0.0, n3.getY(), 1);
        assertEquals("", 40.0, n4.getX(), 1);
        assertEquals("", 20.0, n4.getY(), 1);      
    }

}
