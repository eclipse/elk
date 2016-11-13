/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
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
import org.junit.Assert;

import com.google.common.collect.Iterators;

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

    /**
     * Checks that the coordinates of all nodes in {@code parentNode}'s inclusion tree have x and y coordinates
     * different from 0.
     * 
     * @param parentNode
     *            root node of a graph.
     */
    public static void checkNodeCoordinates(final ElkNode parentNode) {
        Iterators.filter(parentNode.eAllContents(), ElkNode.class).forEachRemaining(node -> {
            Assert.assertTrue(node.getX() > 0);
            Assert.assertTrue(node.getY() > 0);
        });
    }
}
