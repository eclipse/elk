/*******************************************************************************
 * Copyright (c) 2024 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.graph.json.test

import com.google.gson.JsonParser
import org.eclipse.elk.alg.test.PlainJavaInitialization
import org.eclipse.elk.core.RecursiveGraphLayoutEngine
import org.eclipse.elk.core.util.BasicProgressMonitor
import org.eclipse.elk.core.util.Maybe
import org.eclipse.elk.graph.json.ElkGraphJson
import org.eclipse.elk.graph.json.JsonImporter
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.*

/**
 */
class EdgeCoordsTest {
        
    @BeforeClass
    static def void init() {
        PlainJavaInitialization.initializePlainJavaLayout
    }
    val graph = '''
    {
        "id": "root",
        "properties": {
            "algorithm": "layered",
            "org.eclipse.elk.hierarchyHandling": "INCLUDE_CHILDREN"
        },
        "children": [
            { "id": "A",
              "children": [
                  { "id": "x", "width": 50, "height": 90 },
                  { "id": "B",
                    "children": [
                        { "id": "y", "width": 50, "height": 90 },
                        { "id": "z", "width": 50, "height": 90 }
                    ],
                    "edges": [
                        { "id": "e1", "sources": [ "y" ], "targets": [ "z" ] },
                        { "id": "e2", "sources": [ "x" ], "targets": [ "z" ],
                          "labels": [
                              { "text": "Foo", "width": 30, "height": 12 }
                          ]
                        }
                    ]
                  }
              ]
            }
        ]
    }
    '''
    
    val secContainer = '''
    {
     "id": "e2_s0",
     "startPoint": {
      "x": 62,
      "y": 124
     },
     "endPoint": {
      "x": 219,
      "y": 99
     },
     "bendPoints": [
      {
       "x": 209,
       "y": 124
      },
      {
       "x": 209,
       "y": 99
      }
     ],
     "incomingShape": "x",
     "outgoingShape": "z"
    }
    '''
    
    val labelContainer = '''
    {
     "text": "Foo",
     "width": 30,
     "height": 12,
     "x": 82,
     "y": 127
    }
    '''
    
    val secParent = '''
    {
     "id": "e2_s0",
     "startPoint": {
      "x": -75,
      "y": 112
     },
     "endPoint": {
      "x": 82,
      "y": 87
     },
     "bendPoints": [
      {
       "x": 72,
       "y": 112
      },
      {
       "x": 72,
       "y": 87
      }
     ],
     "incomingShape": "x",
     "outgoingShape": "z"
    }
    '''
    
    val labelParent = '''
    {
     "text": "Foo",
     "width": 30,
     "height": 12,
     "x": -55,
     "y": 115
    }
    '''
    
    val secRoot = '''
    {
     "id": "e2_s0",
     "startPoint": {
      "x": 74,
      "y": 136
     },
     "endPoint": {
      "x": 231,
      "y": 111
     },
     "bendPoints": [
      {
       "x": 221,
       "y": 136
      },
      {
       "x": 221,
       "y": 111
      }
     ],
     "incomingShape": "x",
     "outgoingShape": "z"
    }
    '''
    
    val labelRoot = '''
    {
     "text": "Foo",
     "width": 30,
     "height": 12,
     "x": 94,
     "y": 139
    }
    '''
            
    @Test
    def void edgeCoordsTest() {
        
        val cases = #[
            #['CONTAINER', secContainer, labelContainer],
            #['PARENT', secParent, labelParent],
            #['ROOT', secRoot, labelRoot]
        ]
        
        for (p : cases) {
            val mode = p.get(0)
            val expectedSecString = p.get(1)
            val expectedLabelString = p.get(2)
            
            val jsonGraph = JsonParser.parseString(graph).asJsonObject
            
            jsonGraph.get("properties").asJsonObject.addProperty(
                "org.eclipse.elk.json.edgeCoords", mode
            )
            
            val mby = new Maybe<JsonImporter>
            val root = ElkGraphJson.forGraph(jsonGraph)
                                   .rememberImporter(mby)
                                   .toElk
            
            new RecursiveGraphLayoutEngine().layout(root, new BasicProgressMonitor)
            
            mby.get.transferLayout(root)
            
            val nodeB = jsonGraph.get("children").asJsonArray
                                 .get(0).asJsonObject
                                 .get("children").asJsonArray
                                 .get(1).asJsonObject
            val edge2 = nodeB.get("edges").asJsonArray
                             .get(1).asJsonObject
            val computedSec = edge2.get("sections").asJsonArray
                           .get(0).asJsonObject
            val computedLabel = edge2.get("labels").asJsonArray
                           .get(0).asJsonObject
            
            val expectedSec = JsonParser.parseString(expectedSecString).asJsonObject
            val expectedLabel = JsonParser.parseString(expectedLabelString).asJsonObject
            
            assertEquals(expectedSec, computedSec)
            assertEquals(expectedLabel, computedLabel)
        }

    }
    
}
