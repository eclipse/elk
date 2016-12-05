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
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.ElkPort;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import com.google.common.collect.Lists;

/**
 * Implements the graph layout application aspect of {@link ElkGraphTransformer}.
 * 
 * @author cds
 */
class ElkGraphLayoutTransferrer {

    /**
     * Applies the layout information contained in the given LGraph to the KGraph elements it was
     * created from. All source KGraph elements are expected to be accessible through their LGraph
     * counterparts through the {@link InternalProperties#ORIGIN} property.
     * 
     * @param lgraph the LGraph whose layout information to apply.
     */
    public void applyLayout(final LGraph lgraph) {
        Object graphOrigin = lgraph.getProperty(InternalProperties.ORIGIN);
        if (!(graphOrigin instanceof ElkNode)) {
            return;
        }
        
        // The ElkNode that represents this graph in the original ElkGraph
        ElkNode parentElkNode = (ElkNode) graphOrigin;
        
        // The LNode that represents this graph in the upper hierarchy level, if any
        LNode parentLNode = (LNode) lgraph.getProperty(InternalProperties.PARENT_LNODE);
        
        // Get the offset to be added to all coordinates
        KVector offset = new KVector(lgraph.getOffset());
        
        // Adjust offset (and with it the positions), if requested
        LInsets lInsets = lgraph.getInsets();
        
        // We may need to apply increased top/left insets
        final EnumSet<SizeOptions> sizeOptions = parentElkNode.getProperty(LayeredOptions.NODE_SIZE_OPTIONS);
        KVector additionalInsets = new KVector();
        
        // MIGRATE I believe this will become the only case since the ElkGraph doesn't know about insets?
        if (sizeOptions.contains(SizeOptions.APPLY_ADDITIONAL_PADDING)) {
            additionalInsets.x = lInsets.left;
            additionalInsets.y = lInsets.top;
            offset.x += additionalInsets.x;
            offset.y += additionalInsets.y;
        }
        
        // Set node insets, if requested
        if (sizeOptions.contains(SizeOptions.COMPUTE_PADDING)) {
            ElkPadding padding = parentElkNode.getProperty(LayeredOptions.PADDING);
            
            padding.setBottom(lInsets.bottom);
            padding.setTop(lInsets.top);
            padding.setLeft(lInsets.left);
            padding.setRight(lInsets.right);
        }

        // Along the way, we collect the list of edges to be processed later
        List<LEdge> edgeList = Lists.newArrayList();

        // Process the nodes
        for (LNode lnode : lgraph.getLayerlessNodes()) {
            Object origin = lnode.getProperty(InternalProperties.ORIGIN);

            if (origin instanceof ElkNode) {
                applyNodeLayout(lnode, offset);
            } else if (origin instanceof ElkPort && parentLNode == null) {
                // It's an external port. Set its position if it hasn't already been done before
                ElkPort elkport = (ElkPort) origin;
                KVector portPosition = LGraphUtil.getExternalPortPosition(lgraph, lnode,
                        elkport.getWidth(), elkport.getHeight());
                elkport.setLocation(portPosition.x, portPosition.y);
            }

            // Collect edges, except if they go into a nested subgraph (those edges need to be
            // processed during one of the recursive calls so that any additional offsets are applied
            // correctly)
            for (LPort port : lnode.getPorts()) {
                port.getOutgoingEdges().stream()
                    .filter(edge -> !LGraphUtil.isDescendant(edge.getTarget().getNode(), lnode))
                    .forEach(edge -> edgeList.add(edge));
            }
        }

        // Collect edges that go from the current graph's representing LNode down into its descendants
        if (parentLNode != null) {
            for (LPort port : parentLNode.getPorts()) {
                port.getOutgoingEdges().stream()
                        .filter(edge -> LGraphUtil.isDescendant(edge.getTarget().getNode(), parentLNode))
                        .forEach(edge -> edgeList.add(edge));
            }
        }
        
        // Iterate through all edges
        EdgeRouting routing = parentElkNode.getProperty(LayeredOptions.EDGE_ROUTING);
        for (LEdge ledge : edgeList) {
            applyEdgeLayout(ledge, routing, offset, additionalInsets);
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
        final ElkNode elknode = (ElkNode) lnode.getProperty(InternalProperties.ORIGIN);
        
        // Set the node position
        elknode.setX(lnode.getPosition().x + offset.x);
        elknode.setY(lnode.getPosition().y + offset.y);
        
        // Set the node size, if necessary
        if (!elknode.getProperty(LayeredOptions.NODE_SIZE_CONSTRAINTS).isEmpty()
                || lnode.getProperty(InternalProperties.NESTED_LGRAPH) != null) {
            
            elknode.setWidth(lnode.getSize().x);
            elknode.setHeight(lnode.getSize().y);
        }

        // Set port positions
        for (LPort lport : lnode.getPorts()) {
            Object origin = lport.getProperty(InternalProperties.ORIGIN);
            if (origin instanceof ElkPort) {
                ElkPort elkport = (ElkPort) origin;
                elkport.setLocation(lport.getPosition().x, lport.getPosition().y);
                elkport.setProperty(LayeredOptions.PORT_SIDE, lport.getSide());
            }
        }
        
        // Set node label positions, if they were not fixed
        // (that is at least one of the node or the label has a node label placement set)
        final boolean nodeHasLabelPlacement = !lnode.getProperty(LayeredOptions.NODE_LABELS_PLACEMENT).isEmpty();
        
        for (LLabel llabel : lnode.getLabels()) {
            if (nodeHasLabelPlacement || !llabel.getProperty(LayeredOptions.NODE_LABELS_PLACEMENT).isEmpty()) {
                ElkLabel elklabel = (ElkLabel) llabel.getProperty(InternalProperties.ORIGIN);
                elklabel.setLocation(llabel.getPosition().x, llabel.getPosition().y);
            }
        }
        
        // Set port label positions, if they were not fixed
        if (lnode.getProperty(LayeredOptions.PORT_LABELS_PLACEMENT) != PortLabelPlacement.FIXED) {
            for (LPort lport : lnode.getPorts()) {
                for (LLabel llabel : lport.getLabels()) {
                    ElkLabel elklabel = (ElkLabel) llabel.getProperty(InternalProperties.ORIGIN);
                    elklabel.setWidth(llabel.getSize().x);
                    elklabel.setHeight(llabel.getSize().y);
                    elklabel.setLocation(llabel.getPosition().x, llabel.getPosition().y);
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
     * @param additionalInsets
     *            the additional insets that may have to be taken into account for hierarchical that go
     *            into the bowels of their source node. These are already included in the offset, but
     *            are required separately.
     */
    private void applyEdgeLayout(final LEdge ledge, final EdgeRouting routing, final KVector offset,
            final KVector additionalInsets) {

        ElkEdge elkedge = (ElkEdge) ledge.getProperty(InternalProperties.ORIGIN);
        
        // Only the orthogonal edge routing algorithm supports self-loops. Thus, leave self-loops
        // untouched if another routing algorithm is selected.
        if (elkedge == null) {
            return;
        } else if (ledge.isSelfLoop() && routing != EdgeRouting.ORTHOGONAL && routing != EdgeRouting.SPLINES) {
            return;
        }
        
        KVectorChain bendPoints = ledge.getBendPoints();
        KVector edgeOffset = offset;

        // Adapt the offset value and add the source port position to the vector chain
        KVector sourcePoint;
        if (LGraphUtil.isDescendant(ledge.getTarget().getNode(), ledge.getSource().getNode())) {
            // The external port's anchor position, relative to the node's top left corner
            LPort sourcePort = ledge.getSource();
            sourcePoint = KVector.sum(sourcePort.getPosition(), sourcePort.getAnchor());
            
            // The node's insets need to be subtracted since edges going into the node's bowels are
            // relative to the top left corner + insets
            // TODO This line assumes that for a compound node, the insets computed for its LGraph and
            //      for its representing LNode are the same, which doesn't always seem to be the case
            LInsets sourceInsets = sourcePort.getNode().getInsets();
            sourcePoint.add(-sourceInsets.left, -sourceInsets.top);
            
            // The source point will later have the passed offset added to it, which it doesn't actually
            // need, so we subtract it now
            sourcePoint.sub(offset);

            // What it does need, however, is any additional insets that may be present, so we
            // explicitly add them here
            sourcePoint.add(additionalInsets);
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
        
        // Give the edge a proper edge section to store routing information
        ElkEdgeSection elkedgeSection = ElkGraphUtil.firstEdgeSection(elkedge, true, true);
        ElkUtil.applyVectorChain(bendPoints, elkedgeSection);

        // Apply layout to labels
        for (LLabel llabel : ledge.getLabels()) {
            ElkLabel elklabel = (ElkLabel) llabel.getProperty(InternalProperties.ORIGIN);
            elklabel.setWidth(llabel.getSize().x);
            elklabel.setHeight(llabel.getSize().y);
            elklabel.setLocation(llabel.getPosition().x + edgeOffset.x,
                                 llabel.getPosition().y + edgeOffset.y);
        }
        
        // Copy junction points
        KVectorChain junctionPoints = ledge.getProperty(LayeredOptions.JUNCTION_POINTS);
        if (junctionPoints != null) {
            junctionPoints.offset(edgeOffset);
            elkedge.setProperty(LayeredOptions.JUNCTION_POINTS, junctionPoints);
        } else {
            elkedge.setProperty(LayeredOptions.JUNCTION_POINTS, null);
        }

        // Mark the edge with information about its routing
        if (routing == EdgeRouting.SPLINES) {
            // SPLINES means that bend points shall be interpreted as control points for splines
            elkedge.setProperty(LayeredOptions.EDGE_ROUTING, EdgeRouting.SPLINES);
        } else {
            // null means that bend points shall be interpreted as bend points
            elkedge.setProperty(LayeredOptions.EDGE_ROUTING, null);
        }
    }

    /**
     * Applies layout information computed for the given graph.
     * 
     * @param lgraph
     *            the edge that has the layout information.
     */
    private void applyParentNodeLayout(final LGraph lgraph) {
        ElkNode elknode = (ElkNode) lgraph.getProperty(InternalProperties.ORIGIN);
        
        KVector actualGraphSize = lgraph.getActualSize();
        elknode.setProperty(LayeredOptions.NODE_SIZE_CONSTRAINTS, SizeConstraint.fixed());

        if (lgraph.getProperty(InternalProperties.PARENT_LNODE) == null) {
            Set<GraphProperties> graphProps = lgraph.getProperty(InternalProperties.GRAPH_PROPERTIES);
            
            if (graphProps.contains(GraphProperties.EXTERNAL_PORTS)) {
                // Ports have positions assigned, the graph's size is final
                elknode.setProperty(LayeredOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_POS);
                ElkUtil.resizeNode(
                        elknode,
                        actualGraphSize.x,
                        actualGraphSize.y,
                        false,
                        true);
            } else {
                // Ports have not been positioned yet - leave this for next layouter
                ElkUtil.resizeNode(
                        elknode,
                        actualGraphSize.x,
                        actualGraphSize.y,
                        true,
                        true);
            }
        }
    }

}
