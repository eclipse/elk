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
package org.eclipse.elk.graph.json.test

import org.eclipse.elk.graph.json.ElkGraphJson
import org.junit.Test

import static org.junit.Assert.*

/**
 */
class SectionsTest {
    
    @Test
    def void edgeSectionTest() {
        val graph = '''
        {
          "id": "root",
          "properties": {
            "direction": "DOWN"
          },
          "children": [{"id": "n1"}, {"id": "n2"}],
          "edges": [{"id": "e1", 
            "sources": [ "n1" ], 
            "targets": [ "n2" ],
            "sections": [{
                "id": "s1",
                "startPoint": {x: 1, y: 1},
                "endPoint": {x: 2, y: 2},
                "incomingShape": "n1",
                "outgoingSections": [ 44 ]
            },{
                "id": 44,
                "startPoint": {x: 3, y: 3},
                "endPoint": {x: 4, y: 4},
                "incomingSections": [ "s1" ],
                "outgoingShape": "n2"
            }]
          }]
        }
        '''
        
        val root = ElkGraphJson.forGraph(graph).toElk
        assertTrue(root.children.size === 2)
        val n1 = root.children.findFirst[ identifier == "n1" ]
        val n2 = root.children.findFirst[ identifier == "n2" ]        
        
        assertTrue(root.containedEdges.size === 1)
        val edge = root.containedEdges.head
        assertTrue(edge.sections.size === 2)
        
        val s1 = edge.sections.findFirst[ identifier == "s1" ]
        val s44 = edge.sections.findFirst[ identifier == "44" ]
        
        assertTrue(s1.incomingShape == n1)
        assertTrue(s44.outgoingShape == n2)
        
        assertTrue(s1.outgoingSections.size === 1)
        assertTrue(s1.outgoingSections.head === s44)
        
        assertTrue(s44.incomingSections.size === 1)
        assertTrue(s44.incomingSections.head === s1)
    }
}