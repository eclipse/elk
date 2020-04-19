/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.graph.json.test

import org.eclipse.elk.core.data.LayoutMetaDataService
import org.eclipse.elk.core.options.CoreOptions
import org.eclipse.elk.core.options.Direction
import org.eclipse.elk.graph.json.ElkGraphJson
import org.eclipse.elk.graph.json.JsonExporter
import org.eclipse.elk.graph.properties.Property
import org.junit.Before
import org.junit.Test

import static org.eclipse.elk.graph.util.ElkGraphUtil.*
import static org.junit.Assert.*

/**
 * Test for {@link JsonExporter}.
 */
class ExportTest {
 
  @Before
  def void setup() {
      LayoutMetaDataService.instance
  }
  
  @Test
  def void testPortLessEdge() {
      val graph = createGraph
      val node1 = createNode(graph)
      val node2 = createNode(graph)
      createSimpleEdge(node1, node2)
      
      ElkGraphJson.forGraph(graph).toJson;
  }  
  
  @Test 
  def void testUniqueIds() {
      val graph = createGraph
      val node1 = createNode(graph)
      node1.identifier = "foo"
      val node2 = createNode(graph)
      node2.identifier = "foo"
      
      val json = ElkGraphJson.forGraph(graph).toJson
      assertTrue(json.contains("foo_g")) // _gxxxxx should be appended to node2 identifier
  }
  
  @Test
  def void testIdsIncreasing() {
      val graph = createGraph
      createNode(graph)
      val node2 = createNode(graph)
      node2.identifier = "foo"
      createNode(graph)
      
      val json = ElkGraphJson.forGraph(graph).toJson
      assertTrue(json.contains("n1"))
      assertTrue(json.contains("foo"))
      assertTrue(json.contains("n2"))
      assertTrue(!json.contains("n3"))
  }
  
  @Test
  def void testOmitUnknownProperties() {
      val graph = createGraph
      graph.setProperty(CoreOptions.DIRECTION, Direction.RIGHT)
      graph.setProperty(new Property<Integer>("foo.bar.dummy"), 0)
      
      val json1 = ElkGraphJson.forGraph(graph)
                              .omitUnknownLayoutOptions(true)
                              .toJson
      assertTrue(json1.contains("direction"))
      assertTrue(!json1.contains("foo.bar.dummy"))
      
      val json2 = ElkGraphJson.forGraph(graph)
                              .omitUnknownLayoutOptions(false)
                              .toJson
      assertTrue(json2.contains("direction"))
      assertTrue(json2.contains("foo.bar.dummy"))
  }
  
  @Test
  def void dontExportEmptyJunctionPoints() {
      val graph = createGraph
      val n1 = createNode(graph)
      val n2 = createNode(graph)
      createSimpleEdge(n1, n2)
      
      val json = ElkGraphJson.forGraph(graph)
                             .omitLayout(false)
                             .toJson

      assertFalse(json.contains("layoutOptions"))
      assertFalse(json.contains("junctionPoints"))
  }
}