/*******************************************************************************
 * Copyright (c) 2009, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.conn.gmf.layouter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.graph.CompoundDirectedGraphLayout;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KLayoutDataFactory;
import org.eclipse.elk.core.klayoutdata.KPoint;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KNode;

/**
 * Layout provider that uses the layout algorithm shipped with Draw2D. Either the
 * default version or the Compound version of the algorithm can be applied.
 * 
 * TODO implement compound graph layout using CompoundDirectedGraphLayout
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class Draw2DLayoutProvider extends AbstractLayoutProvider {

    /** parameter value for the compound version of the layout algorithm. */
    public static final String PARAM_COMPOUND = "Compound";
    /** default value for minimal spacing. */
    private static final float DEF_MIN_SPACING = 16.0f;
    
    /** indicates whether the compound graph version of the algorithm shall be used. */
    private boolean compoundMode = false;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(final String parameter) {
        compoundMode = PARAM_COMPOUND.equals(parameter);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void layout(final KNode layoutNode, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Draw2D Directed Graph layout", 1);
        
        DirectedGraph graph = buildDraw2dGraph(layoutNode);
        DirectedGraphLayout draw2dLayout = compoundMode ? new CompoundDirectedGraphLayout()
                : new DirectedGraphLayout();
        draw2dLayout.visit(graph);
        applyLayout(layoutNode, graph);
        
        progressMonitor.done();
    }
    
    /**
     * Builds the graph which is used internally by the Draw2D layout algorithm.
     * 
     * @param layoutNode parent layout node
     * @return a graph that contains the children of the given parent
     */
    @SuppressWarnings("unchecked")
    private DirectedGraph buildDraw2dGraph(final KNode layoutNode) {
        DirectedGraph graph = new DirectedGraph();
        
        // set layout options for the graph
        KShapeLayout parentLayout = layoutNode.getData(KShapeLayout.class);
        float minSpacing = parentLayout.getProperty(LayoutOptions.SPACING);
        if (minSpacing < 0) {
            minSpacing = DEF_MIN_SPACING;
        }
        graph.setDefaultPadding(new Insets((int) minSpacing));
        float borderSpacing = parentLayout.getProperty(LayoutOptions.BORDER_SPACING);
        if (borderSpacing < 0) {
            borderSpacing = DEF_MIN_SPACING;
        }
        graph.setMargin(new Insets((int) borderSpacing));
        Direction layoutDirection = parentLayout.getProperty(LayoutOptions.DIRECTION);
        switch (layoutDirection) {
        case UP:
        case DOWN:
            graph.setDirection(PositionConstants.SOUTH);
            break;
        default:
            graph.setDirection(PositionConstants.EAST);
            break;
        }
        
        // add nodes to the graph
        Map<KNode, Node> nodeMap = new HashMap<KNode, Node>();
        for (KNode knode : layoutNode.getChildren()) {
            Node draw2dNode = new Node(knode);
            KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);
            ElkUtil.resizeNode(knode);
            draw2dNode.width = (int) nodeLayout.getWidth();
            draw2dNode.height = (int) nodeLayout.getHeight();
            nodeMap.put(knode, draw2dNode);
            graph.nodes.add(draw2dNode);
        }
        
        // add edges to the graph
        for (KNode source : layoutNode.getChildren()) {
            Node draw2dSource = nodeMap.get(source);
            for (KEdge kedge : source.getOutgoingEdges()) {
                KNode target = kedge.getTarget();
                Node draw2dTarget = nodeMap.get(target);
                if (draw2dTarget != null && draw2dTarget != draw2dSource) {
                    Edge draw2dEdge = new Edge(kedge, draw2dSource, draw2dTarget);
                    graph.edges.add(draw2dEdge);
                }
            }
        }
        
        return graph;
    }
    
    /**
     * Applies the layout determined by Draw2D to the original graph.
     * 
     * @param parentNode the parent layout node
     * @param graph the Draw2D graph
     */
    private void applyLayout(final KNode parentNode, final DirectedGraph graph) {
        // apply node layouts
        for (int i = 0; i < graph.nodes.size(); i++) {
            Node node = graph.nodes.getNode(i);
            if (node.data instanceof KNode) {
                KNode knode = (KNode) node.data;
                KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);
                nodeLayout.setPos(node.x, node.y);
            }
        }
        
        // apply edge layouts
        for (int i = 0; i < graph.edges.size(); i++) {
            Edge edge = graph.edges.getEdge(i);
            if (edge.data instanceof KEdge) {
                KEdge kedge = (KEdge) edge.data;
                KEdgeLayout edgeLayout = kedge.getData(KEdgeLayout.class);
                edgeLayout.getBendPoints().clear();
                PointList pointList = edge.getPoints();
                KPoint sourcekPoint = edgeLayout.getSourcePoint();
                Point sourcePoint = pointList.getFirstPoint();
                sourcekPoint.setPos(sourcePoint.x, sourcePoint.y);
                for (int j = 1; j < pointList.size() - 1; j++) {
                    Point point = pointList.getPoint(j);
                    KPoint kpoint = KLayoutDataFactory.eINSTANCE.createKPoint();
                    kpoint.setPos(point.x, point.y);
                    edgeLayout.getBendPoints().add(kpoint);
                }
                KPoint targetkPoint = edgeLayout.getTargetPoint();
                Point targetPoint = pointList.getLastPoint();
                targetkPoint.setPos(targetPoint.x, targetPoint.y);
            }
        }
        
        // apply parent node layout
        KShapeLayout parentLayout = parentNode.getData(KShapeLayout.class);
        KInsets insets = parentLayout.getInsets();
        Dimension layoutSize = graph.getLayoutSize();
        float width = insets.getLeft() + layoutSize.width + insets.getRight();
        float height = insets.getTop() + layoutSize.height + insets.getBottom();
        ElkUtil.resizeNode(parentNode, width, height, false, true);
    }

}
