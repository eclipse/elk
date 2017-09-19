/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.json.test

import org.junit.Test

import static org.eclipse.elk.graph.util.ElkGraphUtil.*
import org.eclipse.elk.graph.json.ElkGraphJson
import org.junit.Before
import org.eclipse.elk.core.data.LayoutMetaDataService

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
}