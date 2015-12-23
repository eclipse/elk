/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.tree;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.tree.graph.TEdge;
import org.eclipse.elk.tree.graph.TGraph;
import org.eclipse.elk.tree.graph.TNode;
import org.eclipse.elk.tree.properties.Properties;

/**
 * Manages the transformation of KGraphs to TGraphs.
 * 
 * @author sor
 * @author sgu
 */
public class KGraphImporter implements IGraphImporter<KNode> {

    // /////////////////////////////////////////////////////////////////////////////
    // Transformation KGraph -> TGraph

    /**
     * {@inheritDoc}
     */
    public TGraph importGraph(final KNode kgraph) {
        TGraph tGraph = new TGraph();

        // copy the properties of the KGraph to the t-graph
        KShapeLayout sourceShapeLayout = kgraph.getData(KShapeLayout.class);
        tGraph.copyProperties(sourceShapeLayout);
        tGraph.checkProperties(Properties.SPACING, Properties.ASPECT_RATIO);
        tGraph.setProperty(Properties.ORIGIN, kgraph);

        // keep a list of created nodes in the t-graph
        Map<KNode, TNode> elemMap = new HashMap<KNode, TNode>();

        // transform nodes and edges
        transformNodes(kgraph, tGraph, elemMap);
        transformEdges(kgraph, tGraph, elemMap);

        return tGraph;
    }

    /**
     * Transforms the nodes and ports defined by the given layout node.
     * 
     * @param parentNode
     *            the layout node whose edges to transform.
     * @param tGraph
     *            the t-graph.
     * @param elemMap
     *            the element map that maps the original {@code KGraph} elements to the transformed
     *            {@code TGraph} elements.
     */
    private void transformNodes(final KNode parentNode, final TGraph tGraph,
            final Map<KNode, TNode> elemMap) {
        int index = 0;

        for (KNode knode : parentNode.getChildren()) {
            KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);

            // copy label
            String label = "";
            if (!knode.getLabels().isEmpty()) {
                label = knode.getLabels().get(0).getText();
            }

            // create new tNode
            TNode newNode = new TNode(index++, tGraph, label);
            newNode.copyProperties(nodeLayout);
            newNode.setProperty(Properties.ORIGIN, knode);
            
            newNode.getPosition().y = nodeLayout.getYpos() + nodeLayout.getHeight() / 2;
            newNode.getSize().x = Math.max(nodeLayout.getWidth(), 1);
            newNode.getPosition().x = nodeLayout.getXpos() + nodeLayout.getWidth() / 2;
            newNode.getSize().y = Math.max(nodeLayout.getHeight(), 1);
            
            tGraph.getNodes().add(newNode);

            // keep the corresponding tNode of each kNode for edge transformation
            elemMap.put(knode, newNode);
        }
    }

    /**
     * Transforms the edges defined by the given layout node.
     * 
     * @param parentNode
     *            the layout node whose edges to transform.
     * @param tGraph
     *            the t-graph.
     * @param elemMap
     *            the element map that maps the original {@code KGraph} elements to the transformed
     *            {@code TGraph} elements.
     */
    private void transformEdges(final KNode parentNode, final TGraph tGraph,
            final Map<KNode, TNode> elemMap) {
        for (KNode knode : parentNode.getChildren()) {
            for (KEdge kedge : knode.getOutgoingEdges()) {
                // exclude edges that pass hierarchy bounds as well as self-loops
                if (kedge.getTarget().getParent() == parentNode && knode != kedge.getTarget()) {
                    KEdgeLayout edgeLayout = kedge.getData(KEdgeLayout.class);

                    // find the corresponding source and target tNode of edge
                    TNode source = elemMap.get(knode);
                    TNode target = elemMap.get(kedge.getTarget());

                    if (source != null && target != null) {
                        // create a edge and add edge to tGraph
                        TEdge newEdge = new TEdge(source, target);
                        newEdge.setProperty(Properties.ORIGIN, kedge);

                        // TODO transform the edge's labels

                        // set properties of the new edge
                        newEdge.copyProperties(edgeLayout);

                        // update tNode accordingly
                        source.getOutgoingEdges().add(newEdge);
                        target.getIncomingEdges().add(newEdge);

                        tGraph.getEdges().add(newEdge);
                    }
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Apply Layout Results

    /**
     * {@inheritDoc}
     */
    public void applyLayout(final TGraph tGraph) {
        // get the corresponding kGraph
        KNode kgraph = (KNode) tGraph.getProperty(Properties.ORIGIN);

        // determine the border spacing, which influences the offset
        KShapeLayout graphLayout = kgraph.getData(KShapeLayout.class);

        // check border spacing and update if necessary
        float borderSpacing = tGraph.getProperty(LayoutOptions.BORDER_SPACING);
        if (borderSpacing < 0) {
            borderSpacing = Properties.SPACING.getDefault();
            tGraph.setProperty(LayoutOptions.BORDER_SPACING, borderSpacing);
        }

        // calculate the offset from border spacing and node distribution
        double minXPos = Integer.MAX_VALUE, minYPos = Integer.MAX_VALUE;
        double maxXPos = Integer.MIN_VALUE, maxYPos = Integer.MIN_VALUE;
        for (TNode tNode : tGraph.getNodes()) {
            KVector pos = tNode.getPosition();
            KVector size = tNode.getSize();
            minXPos = Math.min(minXPos, pos.x - size.x / 2);
            minYPos = Math.min(minYPos, pos.y - size.y / 2);
            maxXPos = Math.max(maxXPos, pos.x + size.x / 2);
            maxYPos = Math.max(maxYPos, pos.y + size.y / 2);
        }
        KVector offset = new KVector(borderSpacing - minXPos, borderSpacing - minYPos);

        // process the nodes
        for (TNode tNode : tGraph.getNodes()) {
            Object object = tNode.getProperty(Properties.ORIGIN);
            if (object instanceof KNode) {
                // set the node position
                KNode knode = (KNode) object;
                KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);
                KVector nodePos = tNode.getPosition().add(offset);
                nodeLayout.setXpos((float) nodePos.x - nodeLayout.getWidth() / 2);
                nodeLayout.setYpos((float) nodePos.y - nodeLayout.getHeight() / 2);
            }
        }

        // process the edges
        for (TEdge tEdge : tGraph.getEdges()) {
            KEdge kedge = (KEdge) tEdge.getProperty(Properties.ORIGIN);
            if (kedge != null) {

                KEdgeLayout edgeLayout = kedge.getData(KEdgeLayout.class);
                KVectorChain bendPoints = tEdge.getBendPoints();

                // add the source port and target points to the vector chain
                KVector sourcePoint = new KVector(tEdge.getSource().getPosition());
                bendPoints.addFirst(sourcePoint);
                KVector targetPoint = new KVector(tEdge.getTarget().getPosition());
                bendPoints.addLast(targetPoint);
                
                // correct the source and target points
                toNodeBorder(sourcePoint, bendPoints.get(1),
                        tEdge.getSource().getSize());
                toNodeBorder(targetPoint, bendPoints.get(bendPoints.size() - 2),
                        tEdge.getTarget().getSize());

                edgeLayout.getBendPoints().clear();
                edgeLayout.applyVectorChain(bendPoints);
            }
        }

        // set up the graph
        KInsets insets = graphLayout.getInsets();
        float width = (float) (maxXPos - minXPos) + 2 * borderSpacing + insets.getLeft()
                + insets.getRight();
        float height = (float) (maxYPos - minYPos) + 2 * borderSpacing + insets.getTop()
                + insets.getBottom();
        ElkUtil.resizeNode(kgraph, width, height, false, false);
    }
    
    
    /**
     * Modify the given center position to the border of the node.
     * 
     * @param center the node center position (modified by this method)
     * @param next the next point of the edge vector chain
     * @param size the node size
     */
    private static void toNodeBorder(final KVector center, final KVector next, final KVector size) {
        double wh = size.x / 2, hh = size.y / 2;
        double absx = Math.abs(next.x - center.x), absy = Math.abs(next.y - center.y);
        double xscale = 1, yscale = 1;
        if (absx > wh) {
            xscale = wh / absx;
        }
        if (absy > hh) {
            yscale = hh / absy;
        }
        double scale = Math.min(xscale, yscale);
        center.x += scale * (next.x - center.x);
        center.y += scale * (next.y - center.y);
    }

}
