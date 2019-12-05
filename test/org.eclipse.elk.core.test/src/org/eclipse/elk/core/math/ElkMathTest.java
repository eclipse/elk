/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.core.math;

import org.eclipse.elk.core.math.ElkMath;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Various tests for the {@link ElkMath} class.
 */
public class ElkMathTest {

    @Test
    public void testPointContains() {
        
        ElkRectangle rect = new ElkRectangle(23, 14, 20, 20);
        KVector contained = new KVector(26, 19);
        assertTrue(ElkMath.contains(rect, contained));
        
        KVector notContained = new KVector(10, 9); 
        assertTrue(!ElkMath.contains(rect, notContained));        
                
        KVector onBorder = new KVector(23, 20);
        assertTrue(!ElkMath.contains(rect, onBorder));
        
        KVector onCorner = new KVector(23, 14);
        assertTrue(!ElkMath.contains(rect, onCorner));
    }
    
    @Test 
    public void testLineContains() {
        
        ElkRectangle rect = new ElkRectangle(23, 14, 20, 20);
        KVector line11 = new KVector(24, 20);
        KVector line12 = new KVector(40, 32);
        assertTrue(ElkMath.contains(rect, line11, line12));
        
        KVector line21 = new KVector(10, 10); // outside
        KVector line22 = new KVector(40, 32);
        assertTrue(!ElkMath.contains(rect, line21, line22));
    }
    
    @Test
    public void testPathContains() {
        ElkRectangle rect = new ElkRectangle(23, 14, 20, 20);
        
        KVectorChain path = new KVectorChain();
        path.add(24, 15);
        path.add(27, 20);
        path.add(39, 30);
        path.add(29, 19);
        assertTrue(ElkMath.contains(rect, path));
        
        // on border
        KVectorChain path2 = new KVectorChain(path);
        path2.add(23, 14);
        assertTrue(!ElkMath.contains(rect, path2));
        
        // outside
        path.add(10, 10);
        assertTrue(!ElkMath.contains(rect, path));
    }
    
    @Test
    public void testLineLineIntersect() {
        
        KVector l11 = new KVector(10, 10);
        KVector l12 = new KVector(20, 20);
        
        // cross
        KVector l21 = new KVector(11, 21);
        KVector l22 = new KVector(21, 11);
        assertTrue(ElkMath.intersects(l11, l12, l21, l22));
        
        // touch
        l21 = new KVector(10, 10);
        l22 = new KVector(15, 10);
        assertTrue(!ElkMath.intersects(l11, l12, l21, l22));
        
        // no cross
        l21 = new KVector(1, 2);
        l22 = new KVector(2, 1);
        assertTrue(!ElkMath.intersects(l11, l12, l21, l22));
        
        // same line
        assertTrue(!ElkMath.intersects(l11, l12, l11.clone(), l12.clone()));
        assertTrue(!ElkMath.intersects(l11, l12, l12.clone(), l11.clone()));
        
        // parallel
        l21 = new KVector(11, 1);
        l22 = new KVector(21, 21);
        assertTrue(!ElkMath.intersects(l11, l12, l21, l22));
    }
    
    @Test
    public void testPathIntersects() {
        
        ElkRectangle rect = new ElkRectangle(23, 14, 20, 20);
        
        KVectorChain path = new KVectorChain();
        path.add(24, 15);
        path.add(27, 20);
        path.add(39, 30);
        path.add(29, 19);
        assertTrue(!ElkMath.intersects(rect, path));
        
        // on border
        KVectorChain path2 = new KVectorChain(path);
        path2.add(23, 14);
        assertTrue(!ElkMath.intersects(rect, path2));
        
        // cross
        path.add(10, 10);
        assertTrue(ElkMath.intersects(rect, path));
    }
}
