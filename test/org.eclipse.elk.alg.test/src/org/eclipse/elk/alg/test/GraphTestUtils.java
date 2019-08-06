/*******************************************************************************
 * Copyright (c) 2016, 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.test;

import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.ElkPort;
import org.eclipse.elk.graph.util.ElkGraphUtil;

/**
 * Utility class for creating simple test graphs.
 */
public final class GraphTestUtils {

    private GraphTestUtils() { }

    /**
     * Creates a KGraph, represented by a parent node that contains the actual nodes and edges of the graph.
     * 
     * @return a parent node that contains graph elements
     */
    public static ElkNode createSimpleGraph() {
        // create parent node
        ElkNode parentNode = ElkGraphUtil.createGraph();

        // create child nodes with labels
        ElkNode childNode1 = ElkGraphUtil.createNode(parentNode);
        ElkGraphUtil.createLabel("node1", childNode1);
        ElkNode childNode2 = ElkGraphUtil.createNode(parentNode);
        ElkGraphUtil.createLabel("node2", childNode2);

        // create ports (optional)
        ElkPort port1 = ElkGraphUtil.createPort(childNode1);
        ElkPort port2 = ElkGraphUtil.createPort(childNode2);

        // create a simple edge
        ElkGraphUtil.createSimpleEdge(port1, port2);

        return parentNode;
    }

    /**
     * Returns a basic graph with three connected nodes.
     */
    public static ElkNode createThreeNodeGraph() {
        ElkNode layoutGraph = ElkGraphUtil.createGraph();
        ElkNode node1 = ElkGraphUtil.createNode(layoutGraph);
        ElkNode node2 = ElkGraphUtil.createNode(layoutGraph);
        ElkNode node3 = ElkGraphUtil.createNode(layoutGraph);

        ElkGraphUtil.createSimpleEdge(node1, node3);
        ElkGraphUtil.createSimpleEdge(node2, node3);

        // layout options
        for (ElkNode node : layoutGraph.getChildren()) {
            // Suppress the Checkstyle warnings, because the meaning is clear
            // SUPPRESS CHECKSTYLE NEXT 2 MagicNumber
            node.setWidth(50.0);
            node.setHeight(50.0);
        }

        return layoutGraph;
    }

    /**
     * @return a hierarchical graph composed of two {@link #createSimpleGraph()}s.
     */
    public static ElkNode createHierarchicalGraph() {
        // create parent node
        ElkNode parentNode = ElkGraphUtil.createGraph();

        ElkNode child1 = createSimpleGraph();
        child1.setParent(parentNode);
        ElkGraphUtil.createLabel("child1", child1);

        ElkNode child2 = createSimpleGraph();
        child2.setParent(parentNode);
        ElkGraphUtil.createLabel("child2", child2);

        return parentNode;
    }
    
}
