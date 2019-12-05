/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.json.test

import org.eclipse.elk.core.options.CoreOptions
import org.eclipse.elk.core.options.Direction
import org.eclipse.elk.graph.json.ElkGraphJson
import org.eclipse.elk.graph.json.JsonImportException
import org.junit.Test

import static org.junit.Assert.*

/**
 */
class GraphTest {
    
    @Test 
    def void graphMustBeObjectTest() {
        ElkGraphJson.forGraph("{id:1}").toElk
    }
    
    @Test(expected = JsonImportException) 
    def void graphMustBeObjectFailArrayTest() {
        ElkGraphJson.forGraph("[]").toElk
    }
    
    @Test
    def void smallGraphTest() {
        val graph = '''
        {
          "id": "root",
          "properties": {
            "direction": "DOWN"
          },
          "children": [{"id": "n1", "width": 40, "height": 40},
                      {"id": "n2", "width": 40, "height": 40}],
          "edges": [{"id": "e1", "source": "n1", "target": "n2"}]
        }
        '''
        
        val root = ElkGraphJson.forGraph(graph).toElk
        
        assertTrue(root.children.size === 2)
        assertTrue(root.containedEdges.size === 1)
        
        assertTrue(root.getProperty(CoreOptions.DIRECTION) == Direction.DOWN)
    }
}