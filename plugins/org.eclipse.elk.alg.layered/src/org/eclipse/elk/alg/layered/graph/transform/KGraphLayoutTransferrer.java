/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.graph.transform;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LInsets;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.properties.GraphProperties;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.Properties;
import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KInsets;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;

import com.google.common.collect.Lists;

/**
 * Implements the graph layout application aspect of {@link KGraphTransformer}.
 * 
 * @author cds
 */
class KGraphLayoutTransferrer {

    /**
     * Applies the layout information contained in the given LGraph to the KGraph elements it was
     * created from. All source KGraph elements are expected to be accessible through their LGraph
     * counterparts through the {@link InternalProperties#ORIGIN} property.
     * 
     * @param lgraph the LGraph whose layout information to apply.
     */
    public void applyLayout(final LGraph lgraph) {
        Object graphOrigin = lgraph.getProperty(InternalProperties.ORIGIN);
        if (!(graphOrigin instanceof KNode)) {
            return;
        }
        KNode parentNode = (KNode) graphOrigin;
        
        // Get the offset to be added to all coordinates
        KVector offset = new KVector(lgraph.getOffset());
        
        // Adjust offset (and with it the positions), if requested
        KShapeLayout parentLayout = parentNode.getData(KShapeLayout.class);
        LInsets lInsets = lgraph.getInsets();
        KInsets kInsets = parentLayout.getInsets();
        final EnumSet<SizeOptions> sizeOptions = parentLayout.getProperty(LayoutOptions.SIZE_OPTIONS);
        if (sizeOptions.contains(SizeOptions.APPLY_ADDITIONAL_INSETS)) {
            offset.x += lInsets.left - kInsets.getLeft();
            offset.y += lInsets.top - kInsets.getTop();
        }
        
        // Set node insets, if requested
        if (sizeOptions.contains(SizeOptions.COMPUTE_INSETS)) {
            kInsets.setBottom((float) lInsets.bottom);
            kInsets.setTop((float) lInsets.top);
            kInsets.setLeft((float) lInsets.left);
            kInsets.setRight((float) lInsets.right);
        }

        // Along the way, we collect the list of edges to be processed later
        List<LEdge> edgeList = Lists.newArrayList();

        // Process the nodes
        for (LNode lnode : lgraph.getLayerlessNodes()) {
            Object origin = lnode.getProperty(InternalProperties.ORIGIN);

            if (origin instanceof KNode) {
                applyNodeLayout(lnode, offset);
            } else if (origin instanceof KPort
                    && lgraph.getProperty(InternalProperties.PARENT_LNODE) == null) {
                
                // It's an external port. Set its position if it hasn't already been done before
                KPort kport = (KPort) origin;
                KShapeLayout portLayout = kport.getData(KShapeLayout.class);
                KVector portPosition = LGraphUtil.getExternalPortPosition(lgraph, lnode,
                        portLayout.getWidth(), portLayout.getHeight());
                portLayout.applyVector(portPosition);
            }

            // Collect edges
            for (LPort port : lnode.getPorts()) {
                edgeList.addAll(port.getOutgoingEdges());
            }
        }

        EdgeRouting routing = parentLayout.getProperty(Properties.EDGE_ROUTING);
        
        // Iterate through all edges
        for (LEdge ledge : edgeList) {
            applyEdgeLayout(ledge, routing, offset);
        }

        // Setup the parent node
        applyParentNodeLayout(lgraph);
        
        // Process nested subgraphs
        for (LNode lnode : lgraph.getLayerlessNodes()) {
            LGraph nestedGraph = lnode.getProperty(InternalProperties.NESTED_LGRAPH);
            if (nestedGraph != null) {
                applyLayout(nestedGraph);
            }
        }
    }

    /**
     * Applies layout information computed for the given node.
     * 
     * @param lnode
     *            the node that has the layout information.
     * @param offset
     *            offset to add to coordinates.
     */
    private void applyNodeLayout(final LNode lnode, final KVector offset) {
        final KNode knode = (KNode) lnode.getProperty(InternalProperties.ORIGIN);
        
        // Set the node position
        KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);
        nodeLayout.setXpos((float) (lnode.getPosition().x + offset.x));
        nodeLayout.setYpos((float) (lnode.getPosition().y + offset.y));
        
        // Set the node size, if necessary
        if (!nodeLayout.getProperty(LayoutOptions.SIZE_CONSTRAINT).isEmpty()
                || lnode.getProperty(InternalProperties.NESTED_LGRAPH) != null) {
            nodeLayout.setWidth((float) lnode.getSize().x);
            nodeLayout.setHeight((float) lnode.getSize().y);
        }

        // Set port positions
        for (LPort lport : lnode.getPorts()) {
            Object origin = lport.getProperty(InternalProperties.ORIGIN);
            if (origin instanceof KPort) {
                KPort kport = (KPort) origin;
                KShapeLayout portLayout = kport.getData(KShapeLayout.class);
                portLayout.applyVector(lport.getPosition());
                portLayout.setProperty(LayoutOptions.PORT_SIDE, lport.getSide());
            }
        }
        
        // Set node label positions, if they were not fixed
        // (that is at least one of the node or the label has a node label placement set)
        final boolean nodeHasLabelPlacement =
                !lnode.getProperty(LayoutOptions.NODE_LABEL_PLACEMENT).isEmpty();
        
        for (LLabel llabel : lnode.getLabels()) {
            if (nodeHasLabelPlacement
                    || !llabel.getProperty(LayoutOptions.NODE_LABEL_PLACEMENT).isEmpty()) {
                KLabel klabel = (KLabel) llabel.getProperty(InternalProperties.ORIGIN);
                KShapeLayout klabelLayout = klabel.getData(KShapeLayout.class);
                klabelLayout.applyVector(llabel.getPosition());
            }
        }
        
        // Set port label positions, if they were not fixed
        if (lnode.getProperty(LayoutOptions.PORT_LABEL_PLACEMENT) != PortLabelPlacement.FIXED) {
            for (LPort lport : lnode.getPorts()) {
                for (LLabel label : lport.getLabels()) {
                    KLabel klabel = (KLabel) label.getProperty(InternalProperties.ORIGIN);
                    KShapeLayout klabelLayout = klabel.getData(KShapeLayout.class);
                    klabelLayout.applyVector(label.getPosition());
                }
            }
        }
    }
    
    /**
     * Applies layout information computed for the given edge.
     * 
     * @param ledge
     *            the edge that has the layout information.
     * @param routing
     *            the kind of routing applied to edges.
     * @param offset
     *            offset to add to coordinates.
     */
    private void applyEdgeLayout(final LEdge ledge, final EdgeRouting routing, final KVector offset) {
        KEdge kedge = (KEdge) ledge.getProperty(InternalProperties.ORIGIN);
        
        // Only the orthogonal edge routing algorithm supports self-loops. Thus, leave self-loops
        // untouched if another routing algorithm is selected.
        if (kedge == null
                || (ledge.isSelfLoop() 
                        && routing != EdgeRouting.ORTHOGONAL 
                        && routing != EdgeRouting.SPLINES)) {
            return;
        }
        
        KEdgeLayout edgeLayout = kedge.getData(KEdgeLayout.class);
        KVectorChain bendPoints = ledge.getBendPoints();
        KVector edgeOffset = offset;

        // Adapt the offset value and add the source port position to the vector chain
        KVector sourcePoint;
        if (LGraphUtil.isDescendant(ledge.getTarget().getNode(), ledge.getSource().getNode())) {
            LPort sourcePort = ledge.getSource();
            sourcePoint = KVector.sum(sourcePort.getPosition(), sourcePort.getAnchor());
            LInsets sourceInsets = sourcePort.getNode().getInsets();
            sourcePoint.add(-sourceInsets.left, -sourceInsets.top);
            LGraph nestedGraph = sourcePort.getNode().getProperty(InternalProperties.NESTED_LGRAPH);
            if (nestedGraph != null) {
                edgeOffset = nestedGraph.getOffset();
            }
            sourcePoint.sub(edgeOffset);
        } else {
            sourcePoint = ledge.getSource().getAbsoluteAnchor();
        }
        bendPoints.addFirst(sourcePoint);
        
        // Add the target port position to the vector chain, including additional offset
        KVector targetPoint = ledge.getTarget().getAbsoluteAnchor();
        if (ledge.getProperty(InternalProperties.TARGET_OFFSET) != null) {
            targetPoint.add(ledge.getProperty(InternalProperties.TARGET_OFFSET));
        }
        bendPoints.addLast(targetPoint);

        // Translate the bend points by the offset and apply the bend points
        bendPoints.offset(edgeOffset);
        edgeLayout.applyVectorChain(bendPoints);

        // Apply layout to labels
        for (LLabel label : ledge.getLabels()) {
            KLabel klabel = (KLabel) label.getProperty(InternalProperties.ORIGIN);
            KShapeLayout klabelLayout = klabel.getData(KShapeLayout.class);
            klabelLayout.setWidth((float) label.getSize().x);
            klabelLayout.setHeight((float) label.getSize().y);
            klabelLayout.applyVector(label.getPosition().add(edgeOffset));
        }
        
        // Copy junction points
        KVectorChain junctionPoints = ledge.getProperty(LayoutOptions.JUNCTION_POINTS);
        if (junctionPoints != null) {
            junctionPoints.offset(edgeOffset);
            edgeLayout.setProperty(LayoutOptions.JUNCTION_POINTS, junctionPoints);
        } else {
            edgeLayout.setProperty(LayoutOptions.JUNCTION_POINTS, null);
        }

        // Mark the edge with information about its routing
        if (routing == EdgeRouting.SPLINES) {
            // SPLINES means that bend points shall be interpreted as control points for splines
            edgeLayout.setProperty(Properties.EDGE_ROUTING, EdgeRouting.SPLINES);
        } else {
            // null means that bend points shall be interpreted as bend points
            edgeLayout.setProperty(Properties.EDGE_ROUTING, null);
        }
    }

    /**
     * Applies layout information computed for the given graph.
     * 
     * @param lgraph
     *            the edge that has the layout information.
     */
    private void applyParentNodeLayout(final LGraph lgraph) {
        KNode knode = (KNode) lgraph.getProperty(InternalProperties.ORIGIN);
        KShapeLayout knodeLayout = knode.getData(KShapeLayout.class);
        
        KVector actualGraphSize = lgraph.getActualSize();
        knodeLayout.setProperty(LayoutOptions.SIZE_CONSTRAINT, SizeConstraint.fixed());

        if (lgraph.getProperty(InternalProperties.PARENT_LNODE) == null) {
            Set<GraphProperties> graphProps = lgraph.getProperty(InternalProperties.GRAPH_PROPERTIES);
            
            if (graphProps.contains(GraphProperties.EXTERNAL_PORTS)) {
                // Ports have positions assigned, the graph's size is final
                knodeLayout.setProperty(LayoutOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_POS);
                ElkUtil.resizeNode(
                        knode,
                        (float) actualGraphSize.x,
                        (float) actualGraphSize.y,
                        false,
                        true);
            } else {
                // Ports have not been positioned yet - leave this for next layouter
                ElkUtil.resizeNode(
                        knode,
                        (float) actualGraphSize.x,
                        (float) actualGraphSize.y,
                        true,
                        true);
            }
        }
    }

}
