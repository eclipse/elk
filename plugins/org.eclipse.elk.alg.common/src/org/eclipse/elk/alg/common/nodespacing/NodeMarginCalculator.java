/*******************************************************************************
 * Copyright (c) 2010, 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.common.nodespacing;

import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.util.IndividualSpacings;
import org.eclipse.elk.core.util.adapters.GraphAdapters.EdgeAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.GraphAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.LabelAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.NodeAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.PortAdapter;

/**
 * Sets the node margins. Node margins are influenced by both port positions and sizes
 * and label positions and sizes.
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>Ports have fixed port positions.</dd>
 *     <dd>Labels have fixed positions.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>The node margins are properly set to form a bounding box around the node and its ports and
 *         labels.</dd>
 * </dl>
 *
 * @see LabelAndNodeSizeProcessor
 * @author cds
 * @author uru
 */
public final class NodeMarginCalculator  {

    private boolean includeLabels = true;
    private boolean includePorts = true;
    private boolean includePortLabels = true;
    private boolean includeEdgeHeadTailLabels = true;

    private GraphAdapter<?> adapter;

    /**
     * Creates a new calculator for the passed adapter.
     * 
     * @param adapter
     *            for the underlying graph type.
     */
    protected NodeMarginCalculator(final GraphAdapter<?> adapter) {
        this.adapter = adapter;
    }

    // ------------
    //  configure
    // ------------

    /**
     * Do not consider labels during margin calculation.
     * 
     * @return this
     */
    public NodeMarginCalculator excludeLabels() {
        this.includeLabels = false;
        return this;
    }
    
    /**
     * Do not consider ports during margin calculation.
     * 
     * @return this
     */
    public NodeMarginCalculator excludePorts() {
        this.includePorts = false;
        return this;
    }
    
    /**
     * Do not consider port labels during margin calculation.
     * 
     * @return this
     */
    public NodeMarginCalculator excludePortLabels() {
        this.includePortLabels = false;
        return this;
    }
    
    /**
     * Do not consider an edge's head and tail labels during margin calculation.
     * 
     * @return this
     */
    public NodeMarginCalculator excludeEdgeHeadTailLabels() {
        this.includeEdgeHeadTailLabels = false;
        return this;
    }
    
    // ------------
    //   process
    // ------------
    
    /**
     * Calculates and assigns margins to all nodes.
     */
    public void process() {
        // Iterate through all nodes
        for (NodeAdapter<?> node : adapter.getNodes()) {
            processNode(node);
        }
    }

    /**
     * Calculates and assigns margins to the given node. The node is expected to be part of the graph the calculator was
     * created for.
     */
    public void processNode(final NodeAdapter<?> node) {

        ElkMargin desiredNodeMargin = IndividualSpacings.getIndividualOrInherited(node, CoreOptions.NODE_LABELS_MARGIN);

        // This will be our bounding box. We'll start with one that's the same size
        // as our node, and at the same position.
        ElkRectangle boundingBox = new ElkRectangle(
                node.getPosition().x,
                node.getPosition().y,
                node.getSize().x,
                node.getSize().y);
        
        // We'll reuse this rectangle as our box for elements to add to the bounding box
        ElkRectangle elementBox = new ElkRectangle();
        
        // Put the node's labels into the bounding box
        if (includeLabels) {
            for (LabelAdapter<?> label : node.getLabels()) {
                elementBox.x = label.getPosition().x + node.getPosition().x;
                elementBox.y = label.getPosition().y + node.getPosition().y;
                elementBox.width = label.getSize().x;
                elementBox.height = label.getSize().y;
                
                boundingBox.union(elementBox);
            }
        }
        
        // Do the same for ports and their labels
        for (PortAdapter<?> port : node.getPorts()) {
            // Calculate the port's upper left corner's x and y coordinate
            double portX = port.getPosition().x + node.getPosition().x;
            double portY = port.getPosition().y + node.getPosition().y;
            
            // The port itself
            if (includePorts) {
                elementBox.x = portX;
                elementBox.y = portY;
                elementBox.width = port.getSize().x;
                elementBox.height = port.getSize().y;
                
                boundingBox.union(elementBox);
            }
            
            // The port's labels
            if (includePortLabels) {
                for (LabelAdapter<?> label : port.getLabels()) {
                    elementBox.x = label.getPosition().x + portX;
                    elementBox.y = label.getPosition().y + portY;
                    elementBox.width = label.getSize().x;
                    elementBox.height = label.getSize().y;
                    
                    boundingBox.union(elementBox);
                }
            }
            
            // End labels of edges connected to the port
            // TODO cds: layered calls #excludeEdgeHeadTailLabels, is this ever active?
            if (includeEdgeHeadTailLabels) {
                KVector requiredPortLabelSpace = new KVector(-desiredNodeMargin.left, -desiredNodeMargin.top);

                // TODO cds: it's not margin but spacing between pairs of labels, right?
                double labelLabelSpacing =
                        IndividualSpacings.getIndividualOrInherited(node, CoreOptions.SPACING_LABEL_LABEL);
                // TODO: maybe leave space for manually placed ports
                if (node.getProperty(CoreOptions.PORT_LABELS_PLACEMENT) == PortLabelPlacement.OUTSIDE) {
                    for (LabelAdapter<?> label : port.getLabels()) {
                        // TODO cds: why spacing in both directions? Doesn't it depend on the stacking direction?
                        requiredPortLabelSpace.x += label.getSize().x + labelLabelSpacing;
                        requiredPortLabelSpace.y += label.getSize().y + labelLabelSpacing;
                    }
                }
                
                requiredPortLabelSpace.x = Math.max(requiredPortLabelSpace.x, 0.0);
                requiredPortLabelSpace.y = Math.max(requiredPortLabelSpace.y, 0.0);
                
                processEdgeHeadTailLabels(boundingBox, port.getOutgoingEdges(), port.getIncomingEdges(), node, port,
                        requiredPortLabelSpace);
            }
        }
        
        // Process end labels of edges directly connected to the node
        if (includeEdgeHeadTailLabels) {
            processEdgeHeadTailLabels(boundingBox, node.getOutgoingEdges(), node.getIncomingEdges(), node, null, null);
        }
        
        // Reset the margin
        ElkMargin margin = new ElkMargin(node.getMargin());
        margin.top = node.getPosition().y - boundingBox.y;
        margin.bottom = boundingBox.y + boundingBox.height - (node.getPosition().y + node.getSize().y);
        margin.left = node.getPosition().x - boundingBox.x;
        margin.right = boundingBox.x + boundingBox.width - (node.getPosition().x + node.getSize().x);
        node.setMargin(margin);
    }
    
    /**
     * Adds the bounding boxes of the head or tail labels of the given sets of outgoing and incoming
     * edges to the given bounding box.
     * 
     * @param boundingBox the bounding box that should be enlarged to include head and tail labels.
     * @param outgoingEdges set of outgoing edges whose tail labels should be included.
     * @param incomingEdges set of incoming edges whose head labels should be included.
     * @param node the node we're processing labels for.
     * @param port the port if the edges are connected to one.
     * @param portLabelSpace if the edges are connected to a port, this is the space required to
     *                               place the port's labels.
     */
    private void processEdgeHeadTailLabels(final ElkRectangle boundingBox,
            final Iterable<EdgeAdapter<?>> outgoingEdges, final Iterable<EdgeAdapter<?>> incomingEdges,
            final NodeAdapter<?> node, final PortAdapter<?> port, final KVector portLabelSpace) {
        
        ElkRectangle labelBox = new ElkRectangle();
        
        // For each edge, the tail labels of outgoing edges ...
        for (EdgeAdapter<?> edge : outgoingEdges) {
            for (LabelAdapter<?> label : edge.getLabels()) {
                if (label.getProperty(CoreOptions.EDGE_LABELS_PLACEMENT) == EdgeLabelPlacement.TAIL) {
                    computeLabelBox(labelBox, label, false, node, port, portLabelSpace);
                    boundingBox.union(labelBox);
                }
            }
        }
   
        // ... and the head label of incoming edges shall be considered
        for (EdgeAdapter<?> edge : incomingEdges) {
            for (LabelAdapter<?> label : edge.getLabels()) {
                if (label.getProperty(CoreOptions.EDGE_LABELS_PLACEMENT) == EdgeLabelPlacement.HEAD) {
                    computeLabelBox(labelBox, label, true, node, port, portLabelSpace);
                    boundingBox.union(labelBox);
                }
            }
        }
    }
    
    /**
     * Computes the given edge label's bounding box. The position of the box is just a rough estimate
     * and might not match what the label positioning algorithm used ends up computing.
     * 
     * @param labelBox result of the computation.
     * @param label the label whose bounding box to compute.
     * @param incomingEdge {@code true} if the edge the label belongs to is an incoming edge to the node.
     * @param node the node the label's edge is incident to.
     * @param port the port the edge might be connected to. {@code null} if the edge is connected
     *             directly to the node.
     * @param portLabelSpace if the edges are connected to a port, this is the space required to
     *                       place the port's labels.
     */
    private void computeLabelBox(final ElkRectangle labelBox, final LabelAdapter<?> label,
            final boolean incomingEdge, final NodeAdapter<?> node, final PortAdapter<?> port,
            final KVector portLabelSpace) {
        
        labelBox.x = node.getPosition().x;
        labelBox.y = node.getPosition().y;
        if (port != null) {
            labelBox.x += port.getPosition().x;
            labelBox.y += port.getPosition().y;
        }
        
        labelBox.width = label.getSize().x;
        labelBox.height = label.getSize().y;
        
        ElkMargin desiredNodeMargin = IndividualSpacings.getIndividualOrInherited(node, CoreOptions.NODE_LABELS_MARGIN);

        if (port == null) {
            // The edge is connected directly to the node
            if (incomingEdge) {
                // Assume the edge enters the node at its western side
                labelBox.x -= desiredNodeMargin.left + label.getSize().x;
            } else {
                // Assume the edge leaves the node at its eastern side
                labelBox.x += node.getSize().x + desiredNodeMargin.right;
            }
        } else {
            switch (port.getSide()) {
            case UNDEFINED:
            case EAST:
                labelBox.x += port.getSize().x
                           + desiredNodeMargin.left
                           + portLabelSpace.x
                           + desiredNodeMargin.right; // TODO cds: I feel this is wrong (same below)
                break;
                
            case WEST:
                labelBox.x -= desiredNodeMargin.left
                           + portLabelSpace.x
                           + desiredNodeMargin.right
                           + label.getSize().x;
                break;
                
            case NORTH:
                labelBox.x += port.getSize().x
                           + desiredNodeMargin.left;
                labelBox.y -= desiredNodeMargin.top
                           + portLabelSpace.y
                           + desiredNodeMargin.bottom
                           + label.getSize().y;
                break;
                
            case SOUTH:
                labelBox.x += port.getSize().x
                           + desiredNodeMargin.left;
                labelBox.y += port.getSize().y
                           + desiredNodeMargin.top
                           + portLabelSpace.y
                           + desiredNodeMargin.bottom;
                break;
            }
        }
    }
}
