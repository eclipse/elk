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

import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
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
    public static KNode createSimpleGraph() {
        // create parent node
        KNode parentNode = ElkUtil.createInitializedNode();

        // create child nodes
        KNode childNode1 = ElkUtil.createInitializedNode();
        // This automatically adds the child to the list of its parent's
        // children.
        childNode1.setParent(parentNode);
        KLabel nodeLabel1 = ElkUtil.createInitializedLabel(childNode1);
        nodeLabel1.setText("node1");
        KNode childNode2 = ElkUtil.createInitializedNode();
        childNode2.setParent(parentNode);
        KLabel nodeLabel2 = ElkUtil.createInitializedLabel(childNode2);
        nodeLabel2.setText("node2");

        // create ports (optional)
        KPort port1 = ElkUtil.createInitializedPort();
        // This automatically adds the port to the node's list of ports.
        port1.setNode(childNode1);
        KPort port2 = ElkUtil.createInitializedPort();
        port2.setNode(childNode2);

        // create edges
        KEdge edge1 = ElkUtil.createInitializedEdge();
        // This automatically adds the edge to the node's list of outgoing
        // edges.
        edge1.setSource(childNode1);
        // This automatically adds the edge to the node's list of incoming
        // edges.
        edge1.setTarget(childNode2);
        // As our ports do not distinguish between incoming and outgoing edges,
        // the edges must be added manually to their list of edges.
        edge1.setSourcePort(port1);
        port1.getEdges().add(edge1);
        edge1.setTargetPort(port2);
        port2.getEdges().add(edge1);

        return parentNode;
    }

    /**
     * @return a hierarchical graph composed of two {@link #createSimpleGraph()}s.
     */
    public static KNode createHierarchicalGraph() {
        // create parent node
        KNode parentNode = ElkUtil.createInitializedNode();

        KNode child1 = createSimpleGraph();
        child1.setParent(parentNode);
        KLabel nodeLabel1 = ElkUtil.createInitializedLabel(child1);
        nodeLabel1.setText("child1");

        KNode child2 = createSimpleGraph();
        child2.setParent(parentNode);
        KLabel nodeLabel2 = ElkUtil.createInitializedLabel(child2);
        nodeLabel2.setText("child2");

        return parentNode;
    }

    /**
     * Checks that the coordinates of all nodes in {@code parentNode}'s inclusion tree have x and y coordinates
     * different from 0.
     * 
     * @param parentNode
     *            root node of a graph.
     */
    public static void checkNodeCoordinates(final KNode parentNode) {
        Iterators.filter(parentNode.eAllContents(), KNode.class).forEachRemaining(node -> {
            KShapeLayout shapeLayout = node.getData(KShapeLayout.class);
            Assert.assertTrue(shapeLayout.getXpos() > 0);
            Assert.assertTrue(shapeLayout.getYpos() > 0);
        });
    }
}
