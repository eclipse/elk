/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.graph.json.test;

import org.eclipse.elk.graph.json.ElkGraphJson;
import org.eclipse.elk.graph.json.JsonImportException;
import org.junit.Test;

/**
 * Tests for the json format.
 */
public class IdTest {
    
    @Test(expected = JsonImportException.class)
    public void testNoId() {
        ElkGraphJson.forGraph("{}").toElk();
    }
    
    @Test(expected = JsonImportException.class)
    public void testWrongIdTypeNumber() {
        ElkGraphJson.forGraph("{ id: 1.2 }").toElk();
    }

    @Test(expected = JsonImportException.class)
    public void testWrongIdTypeObject() {
        ElkGraphJson.forGraph("{ id: {} }").toElk();
    }

    @Test(expected = JsonImportException.class)
    public void testWrongIdTypeArray() {
        ElkGraphJson.forGraph("{ id: [] }").toElk();
    }
    
    @Test(expected = JsonImportException.class)
    public void testWrongIdTypeBoolean() {
        ElkGraphJson.forGraph("{ id: true }").toElk();
    }
    
    @Test
    public void testGoodIdString() {
        ElkGraphJson.forGraph("{ id: 'foo' }").toElk();
    }
    
    @Test
    public void testGoodIdInt() {
        ElkGraphJson.forGraph("{ id: 3 }").toElk();
    }
    
}
