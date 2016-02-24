/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.force;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.elk.alg.force.graph.FEdge;
import org.eclipse.elk.alg.force.graph.FGraph;
import org.eclipse.elk.alg.force.graph.FLabel;
import org.eclipse.elk.alg.force.graph.FNode;
import org.eclipse.elk.alg.force.properties.InternalProperties;
import org.eclipse.elk.alg.force.properties.ForceOptions;
import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;

/**
 * Manages the transformation of KGraphs to FGraphs.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class KGraphImporter implements IGraphImporter<KNode> {
    
    ///////////////////////////////////////////////////////////////////////////////
    // Transformation KGraph -> FGraph

    /**
     * {@inheritDoc}
     */
    public FGraph importGraph(final KNode kgraph) {
        FGraph fgraph = new FGraph();
        
        // copy the properties of the KGraph to the force graph
        KShapeLayout sourceShapeLayout = kgraph.getData(KShapeLayout.class);
        fgraph.copyProperties(sourceShapeLayout);
        // TODO Find another way to do this kind of bounds checking
//        fgraph.checkProperties(Properties.SPACING, Properties.ASPECT_RATIO, Properties.TEMPERATURE,
//                Properties.ITERATIONS, Properties.REPULSION);
        fgraph.setProperty(InternalProperties.ORIGIN, kgraph);
                
        // keep a list of created nodes in the force graph
        Map<KNode, FNode> elemMap = new HashMap<KNode, FNode>();
        
        // transform everything
        transformNodes(kgraph, fgraph, elemMap);
        transformEdges(kgraph, fgraph, elemMap);
                
        return fgraph;
    }
    
    /**
     * Transforms the nodes and ports defined by the given layout node.
     * 
     * @param parentNode the layout node whose edges to transform.
     * @param fgraph the force graph.
     * @param elemMap the element map that maps the original {@code KGraph} elements to the
     *                transformed {@code FGraph} elements.
     */
    private void transformNodes(final KNode parentNode, final FGraph fgraph,
            final Map<KNode, FNode> elemMap) {
        int index = 0;
        for (KNode knode : parentNode.getChildren()) {
            // add a new node to the force graph, copying its size
            KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);
            
            String label = "";
            if (!knode.getLabels().isEmpty()) {
                label = knode.getLabels().get(0).getText();
            }
            FNode newNode = new FNode(label);
            newNode.copyProperties(nodeLayout);
            newNode.setProperty(InternalProperties.ORIGIN, knode);
            
            newNode.id = index++;
            newNode.getPosition().x = nodeLayout.getXpos() + nodeLayout.getWidth() / 2;
            newNode.getPosition().y = nodeLayout.getYpos() + nodeLayout.getHeight() / 2;
            newNode.getSize().x = Math.max(nodeLayout.getWidth(), 1);
            newNode.getSize().y = Math.max(nodeLayout.getHeight(), 1);
            
            fgraph.getNodes().add(newNode);
            
            elemMap.put(knode, newNode);
            
            // port constraints cannot be undefined
            PortConstraints portConstraints = nodeLayout.getProperty(CoreOptions.PORT_CONSTRAINTS);
            if (portConstraints == PortConstraints.UNDEFINED) {
                portConstraints = PortConstraints.FREE;
            }
            
            // TODO consider ports
        }
    }
    
    /**
     * Transforms the edges defined by the given layout node.
     * 
     * @param parentNode the layout node whose edges to transform.
     * @param fgraph the force graph.
     * @param elemMap the element map that maps the original {@code KGraph} elements to the
     *                transformed {@code FGraph} elements.
     */
    private void transformEdges(final KNode parentNode, final FGraph fgraph,
            final Map<KNode, FNode> elemMap) {
        for (KNode knode : parentNode.getChildren()) {
            for (KEdge kedge : knode.getOutgoingEdges()) {
                // exclude edges that pass hierarchy bounds as well as self-loops
                if (kedge.getTarget().getParent() == parentNode && knode != kedge.getTarget()) {
                    KEdgeLayout edgeLayout = kedge.getData(KEdgeLayout.class);
                    
                    // create a force edge
                    FEdge newEdge = new FEdge();
                    newEdge.copyProperties(edgeLayout);
                    // TODO
//                    newEdge.checkProperties(Properties.LABEL_SPACING, Properties.REPULSIVE_POWER);
                    newEdge.setProperty(InternalProperties.ORIGIN, kedge);
                    
                    newEdge.setSource(elemMap.get(knode));
                    newEdge.setTarget(elemMap.get(kedge.getTarget()));
                    
                    fgraph.getEdges().add(newEdge);
                    
                    // transform the edge's labels
                    for (KLabel klabel : kedge.getLabels()) {
                        KShapeLayout labelLayout = klabel.getData(KShapeLayout.class);
                        
                        FLabel newLabel = new FLabel(newEdge, klabel.getText());
                        newLabel.setProperty(InternalProperties.ORIGIN, klabel);
                        
                        newLabel.getSize().x = Math.max(labelLayout.getWidth(), 1);
                        newLabel.getSize().y = Math.max(labelLayout.getHeight(), 1);
                        newLabel.refreshPosition();
                        
                        fgraph.getLabels().add(newLabel);
                    }
                }
            }
        }
    }    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Apply Layout Results

    /**
     * {@inheritDoc}
     */
    public void applyLayout(final FGraph fgraph) {
        KNode kgraph = (KNode) fgraph.getProperty(InternalProperties.ORIGIN);
        
        // determine the border spacing, which influences the offset
        KShapeLayout parentLayout = kgraph.getData(KShapeLayout.class);
        float borderSpacing = fgraph.getProperty(ForceOptions.SPACING_BORDER);
        if (borderSpacing < 0) {
            borderSpacing = ForceOptions.SPACING_BORDER.getDefault();
            fgraph.setProperty(ForceOptions.SPACING_BORDER, borderSpacing);
        }
        
        // calculate the offset from border spacing and node distribution
        double minXPos = Integer.MAX_VALUE, minYPos = Integer.MAX_VALUE,
                maxXPos = Integer.MIN_VALUE, maxYPos = Integer.MIN_VALUE;
        for (FNode node : fgraph.getNodes()) {
            KVector pos = node.getPosition();
            KVector size = node.getSize();
            minXPos = Math.min(minXPos, pos.x - size.x / 2);
            minYPos = Math.min(minYPos, pos.y - size.y / 2);
            maxXPos = Math.max(maxXPos, pos.x + size.x / 2);
            maxYPos = Math.max(maxYPos, pos.y + size.y / 2);
        }
        KVector offset = new KVector(borderSpacing - minXPos, borderSpacing - minYPos);
        
        // process the nodes
        for (FNode fnode : fgraph.getNodes()) {
            Object object = fnode.getProperty(InternalProperties.ORIGIN);
            
            if (object instanceof KNode) {
                // set the node position
                KNode knode = (KNode) object;
                KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);
                KVector nodePos = fnode.getPosition().add(offset);
                nodeLayout.setXpos((float) nodePos.x - nodeLayout.getWidth() / 2);
                nodeLayout.setYpos((float) nodePos.y - nodeLayout.getHeight() / 2);
            }
        }
        
        // process the edges
        for (FEdge fedge : fgraph.getEdges()) {
            KEdge kedge = (KEdge) fedge.getProperty(InternalProperties.ORIGIN);
            KEdgeLayout edgeLayout = kedge.getData(KEdgeLayout.class);
            edgeLayout.getBendPoints().clear();
            edgeLayout.getSourcePoint().applyVector(fedge.getSourcePoint());
            edgeLayout.getTargetPoint().applyVector(fedge.getTargetPoint());
        }
        
        // process the labels
        for (FLabel flabel : fgraph.getLabels()) {
            KLabel klabel = (KLabel) flabel.getProperty(InternalProperties.ORIGIN);
            KShapeLayout klabelLayout = klabel.getData(KShapeLayout.class);
            KVector labelPos = flabel.getPosition().add(offset);
            klabelLayout.applyVector(labelPos);
        }
        
        // set up the parent node
        KInsets insets = parentLayout.getInsets();
        float width = (float) (maxXPos - minXPos) + 2 * borderSpacing
                + insets.getLeft() + insets.getRight();
        float height = (float) (maxYPos - minYPos) + 2 * borderSpacing
                + insets.getTop() + insets.getBottom();
        ElkUtil.resizeNode(kgraph, width, height, false, true);
    }
    
}
