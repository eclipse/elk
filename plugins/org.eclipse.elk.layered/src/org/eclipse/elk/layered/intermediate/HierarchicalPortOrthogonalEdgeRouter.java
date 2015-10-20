/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.layered.intermediate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.layered.ILayoutProcessor;
import org.eclipse.elk.layered.graph.LEdge;
import org.eclipse.elk.layered.graph.LGraph;
import org.eclipse.elk.layered.graph.LInsets;
import org.eclipse.elk.layered.graph.LNode;
import org.eclipse.elk.layered.graph.LPort;
import org.eclipse.elk.layered.graph.Layer;
import org.eclipse.elk.layered.graph.LNode.NodeType;
import org.eclipse.elk.layered.p5edges.OrthogonalRoutingGenerator;
import org.eclipse.elk.layered.properties.InternalProperties;
import org.eclipse.elk.layered.properties.Properties;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * This processor does the job of routing edges connected to hierarchical ports.
 * 
 * <p>Without going too much into the details, it does so in six distinct steps:</p>
 * <ol>
 *   <li>Dummy nodes created for northern and southern hierarchical ports are restored and
 *     connected to the nodes formerly created in their stead.</li>
 *   <li>The coordinates of these dummy nodes - and thus of the hierarchical ports they
 *     represent - are calculated and applied.</li>
 *   <li>The edges connected to northern and southern hierarchical ports are routed.</li>
 *   <li>The temporary dummy nodes are removed, thereby restoring the original bijective
 *     relationship between external ports and the dummy nodes created to represent them.</li>
 *   <li>Due to the necessity of having to route these edges, additional height may be
 *     required, which in turn may invalidate the y coordinates of eastern and western
 *     hierarchical port dummy nodes. Those are corrected in this step.</li>
 *   </li>That may in turn have produced slanted edge segments, which are corrected in the
 *     final step.</li>
 * </ol>
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>A layered graph, with edge routing finished for edges not incident
 *     to external ports; long edge dummies are not yet joined.</dd>
 *   <dt>Postcondition:</dt><dd>All external port dummy nodes left map onto an actual external port;
 *     the coordinates of external port dummy nodes specify the coordinates of their respective
 *     external port; all external port dummy nodes have a size of (0, 0); edges connected to
 *     external ports have their bend points set.</dd>
 *   <dt>Slots:</dt><dd>After phase 5.</dd>
 *   <dt>Same-slot dependencies:</dt><dd>None.</dd>
 * </dl>
 * 
 * @see HierarchicalPortConstraintProcessor
 * @see HierarchicalPortDummySizeProcessor
 * @see HierarchicalPortPositionProcessor
 * @see OrthogonalRoutingGenerator
 * @author cds
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating proposed yellow by msp
 */
public final class HierarchicalPortOrthogonalEdgeRouter implements ILayoutProcessor {
    
    // VARIABLES
    
    /**
     * The amount of space necessary to accomodate northern external port edge routing.
     */
    private double northernExtPortEdgeRoutingHeight;

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Orthogonally routing hierarchical port edges", 1);
        northernExtPortEdgeRoutingHeight = 0.0;
        
        /* Step 1
         * Restore any north / south port dummies removed by the HierarchicalPortConstraintProcessor
         * and connect them to the dummies created in their stead.
         */
        Set<LNode> northSouthDummies = restoreNorthSouthDummies(layeredGraph);
        
        /* Step 2
         * Calculate coordinates for the north / south port dummies. Coordinates for the
         * east / west port dummies have already been calculated prior to this processor's
         * execution. The coordinates are relative to the node's content area, just like
         * normal node coordinates. (the content area is the node size minus insets minus
         * border spacing minus offset)
         */
        setNorthSouthDummyCoordinates(layeredGraph, northSouthDummies);
        
        /* Step 3
         * Orthogonal edge routing.
         */
        routeEdges(layeredGraph, northSouthDummies);
        
        /* Step 4
         * Removal of the temporarily created north / south port dummies.
         */
        removeTemporaryNorthSouthDummies(layeredGraph);
        
        /* Step 5
         * Finally, the coordinates of east / west hierarchical port dummies have to be corrected
         * and set. The x coordinate must be set, and if north / south port routing resulted
         * in a change of offset or graph size, the y coordinates have to be adjusted if port
         * constraints are at FIXED_RATIO or FIXED_POS. The graph's width may also have to be
         * adjusted.
         */
        fixCoordinates(layeredGraph);
        
        /* Step 6
         * Fixing the dummy coordinates can easily lead to slanted edge segments, which needs
         * to be corrected.
         */
        correctSlantedEdgeSegments(layeredGraph);
        
        monitor.done();
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // STEP 1: RESTORE NORTH / SOUTH DUMMIES
    
    /**
     * Iterates through all layers, restoring hierarchical port dummy nodes along the way. The
     * restored nodes are connected to the temporary dummy nodes created for them (or, as Carsten
     * calls them, the "dummy dummies"). The restored nodes are added to the last layer. (which layer
     * they are added to doesn't make any difference)
     * 
     * @param layeredGraph the layered graph.
     * @return the list of restored external port dummies.
     */
    private Set<LNode> restoreNorthSouthDummies(final LGraph layeredGraph) {
        Set<LNode> restoredDummies = Sets.newLinkedHashSet();
        Layer lastLayer = null;
        
        // Iterate through all nodes, looking for hierarchical port dummies that replace
        // another hierarchical port dummy
        for (Layer layer : layeredGraph) {
            for (LNode node : layer) {
                if (node.getType() != NodeType.EXTERNAL_PORT) {
                    // Not a hierarchical port dummy - we're not interested. Move along,
                    // please, there's nothing to see here.
                    continue;
                }
                
                LNode replacedDummy =
                        (LNode) node.getProperty(InternalProperties.EXT_PORT_REPLACED_DUMMY);
                if (replacedDummy != null) {
                    assert replacedDummy.getType() == NodeType.EXTERNAL_PORT;
                    
                    // Restore the origin and connect the node to it
                    restoreDummy(replacedDummy, restoredDummies);
                    connectNodeToDummy(layeredGraph, node, replacedDummy);
                }
            }
            
            lastLayer = layer;
        }
        
        // Assign the restored dummies to the graph's last layer (this has nothing to do anymore
        // with where they'll be placed; we just need some layer to assign them to, and we simply
        // choose the last one)
        for (LNode dummy : restoredDummies) {
            dummy.setLayer(lastLayer);
        }
        
        return restoredDummies;
    }
    
    /**
     * Restores the given dummy, if it's not already restored. If the dummy has already been
     * restored, it will be contained in the given set. If not, it is restored by setting its
     * port's side and putting it in the set.
     * 
     * @param dummy the dummy node to restore.
     * @param restoredDummies set of dummy nodes already restored.
     */
    private void restoreDummy(final LNode dummy, final Set<LNode> restoredDummies) {
        if (restoredDummies.contains(dummy)) {
            return;
        } else {
            // Depending on the hierarchical port's side, we set the port side of the dummy's ports
            // to be able to route properly (northern dummies must have a southern port)
            PortSide portSide = dummy.getProperty(InternalProperties.EXT_PORT_SIDE);
            LPort dummyPort = dummy.getPorts().get(0);
            
            if (portSide == PortSide.NORTH) {
                dummyPort.setSide(PortSide.SOUTH);
            } else if (portSide == PortSide.SOUTH) {
                dummyPort.setSide(PortSide.NORTH);
            }
            
            restoredDummies.add(dummy);
        }
    }
    
    /**
     * Adds a port to the given node and connects that to the given dummy node.
     * 
     * @param node the node to connect to the dummy.
     * @param dummy the external port dummy to connect the node to.
     */
    private void connectNodeToDummy(final LGraph layeredGraph, final LNode node, final LNode dummy) {
        // First, add a port to the node. The port side depends on the node's hierarchical port side
        LPort outPort = new LPort();
        outPort.setNode(node);
        
        PortSide extPortSide = node.getProperty(InternalProperties.EXT_PORT_SIDE);
        outPort.setSide(extPortSide);
        
        // Find the dummy node's port
        LPort inPort = dummy.getPorts().get(0);
        
        // Connect the two nodes
        LEdge edge = new LEdge();
        edge.setSource(outPort);
        edge.setTarget(inPort);
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // STEP 2: SET NORTH / SOUTH DUMMY COORDINATES
    
    /**
     * Set coordinates for northern and southern external port dummy nodes.
     * 
     * @param layeredGraph the layered graph.
     * @param northSouthDummies set of dummy nodes whose position to set.
     */
    private void setNorthSouthDummyCoordinates(final LGraph layeredGraph,
            final Set<LNode> northSouthDummies) {
        
        PortConstraints constraints = layeredGraph.getProperty(LayoutOptions.PORT_CONSTRAINTS);
        KVector graphSize = layeredGraph.getSize();
        LInsets graphInsets = layeredGraph.getInsets();
        float borderSpacing = layeredGraph.getProperty(InternalProperties.BORDER_SPACING);
        double graphWidth = graphSize.x + graphInsets.left + graphInsets.right + 2 * borderSpacing;
        double northY = 0 - graphInsets.top - borderSpacing - layeredGraph.getOffset().y;
        double southY = graphSize.y + graphInsets.top + graphInsets.bottom + 2 * borderSpacing
                - layeredGraph.getOffset().y;
        
        // Lists of northern and southern external port dummies
        List<LNode> northernDummies = Lists.newArrayList();
        List<LNode> southernDummies = Lists.newArrayList();
        
        for (LNode dummy : northSouthDummies) {
            // Set x coordinate
            switch (constraints) {
            case FREE:
            case FIXED_SIDE:
            case FIXED_ORDER:
                calculateNorthSouthDummyPositions(dummy);
                break;
                
            case FIXED_RATIO:
                applyNorthSouthDummyRatio(dummy, graphWidth);
                dummy.borderToContentAreaCoordinates(true, false);
                break;
            
            case FIXED_POS:
                applyNorthSouthDummyPosition(dummy);
                dummy.borderToContentAreaCoordinates(true, false);
                
                // Ensure that the graph is wide enough to hold the port
                graphSize.x = Math.max(
                        graphSize.x,
                        dummy.getPosition().x + dummy.getSize().x / 2.0);
                break;
            }
            
            // Set y coordinates and add the dummy to its respective list
            switch (dummy.getProperty(InternalProperties.EXT_PORT_SIDE)) {
            case NORTH:
                dummy.getPosition().y = northY;
                northernDummies.add((dummy));
                break;
            
            case SOUTH:
                dummy.getPosition().y = southY;
                southernDummies.add(dummy);
                break;
            }
        }
        
        // Check for correct ordering of nodes in the FIXED_ORDER case and for dummy nodes that
        // have been put on top of one another
        switch (constraints) {
        case FREE:
        case FIXED_SIDE:
            ensureUniquePositions(northernDummies, layeredGraph);
            ensureUniquePositions(southernDummies, layeredGraph);
            break;
            
        case FIXED_ORDER:
            restoreProperOrder(northernDummies, layeredGraph);
            restoreProperOrder(southernDummies, layeredGraph);
            break;
        }
    }

    /**
     * Calculates the positions of northern and southern dummy nodes. The position is based
     * on the nodes the dummy nodes are connected to, which is taken as the position of the
     * port's center.
     * 
     * @param dummy the northern or southern external port dummy node to calculate the position for. 
     */
    private void calculateNorthSouthDummyPositions(final LNode dummy) {
        // We use a simple algorithm that simply adds the horizontal positions of all
        // connected ports and divides the sum by the number of connected ports
        
        // First, get the dummy's port (it has only one)
        LPort dummyInPort = dummy.getPorts().get(0);
        
        // Now, iterate over all connected ports, adding their horizontal position
        double posSum = 0.0;
        
        for (LPort connectedPort : dummyInPort.getConnectedPorts()) {
            posSum += connectedPort.getNode().getPosition().x + connectedPort.getPosition().x
                    + connectedPort.getAnchor().x;
        }
        
        // Assign the dummy's x coordinate
        KVector anchor = dummy.getProperty(LayoutOptions.PORT_ANCHOR);
        double offset = anchor == null ? 0 : anchor.x;
        dummy.getPosition().x = posSum / dummyInPort.getDegree() - offset;
    }
    
    /**
     * Sets the dummy's x coordinate to respect the ratio defined for its original port.
     * 
     * @param dummy the dummy.
     * @param width the graph width.
     */
    private void applyNorthSouthDummyRatio(final LNode dummy, final double width) {
        KVector anchor = dummy.getProperty(LayoutOptions.PORT_ANCHOR);
        double offset = anchor == null ? 0 : anchor.x;
        dummy.getPosition().x = width * dummy.getProperty(InternalProperties.PORT_RATIO_OR_POSITION)
                - offset;
    }
    
    /**
     * Sets the dummy's x coordinate to its original port's x coordinate.
     * 
     * @param dummy the dummy.
     */
    private void applyNorthSouthDummyPosition(final LNode dummy) {
        KVector anchor = dummy.getProperty(LayoutOptions.PORT_ANCHOR);
        double offset = anchor == null ? 0 : anchor.x;
        dummy.getPosition().x = dummy.getProperty(InternalProperties.PORT_RATIO_OR_POSITION) - offset;
    }
    
    /**
     * Ensures that no two dummy nodes in the given list are assigned the same x coordinate. This
     * method must only be called if port constraints are set to {@code FREE} or {@code FIXED_SIDE}
     * as it may not preserve the original order of dummy nodes.
     * 
     * @param dummies list of dummy nodes.
     * @param graph the layered graph.
     */
    private void ensureUniquePositions(final List<LNode> dummies, final LGraph graph) {
        if (dummies.isEmpty()) {
            return;
        }
        
        // Turn the list into an array of dummy nodes and sort that by their x coordinate
        LNode[] dummyArray = dummies.toArray(new LNode[dummies.size()]);
        
        Arrays.sort(dummyArray, new Comparator<LNode>() {
            public int compare(final LNode a, final LNode b) {
                return Double.compare(a.getPosition().x, b.getPosition().x);
            }
        });
        
        assignAscendingCoordinates(dummyArray, graph);
    }

    /**
     * Checks if the automatically calculated node coordinates violate their fixed order and fixes
     * the coordinates. Calling this method only makes sense if port constraints are set to
     * {@code FIXED_ORDER}.
     * 
     * @param dummies list of dummy nodes.
     * @param graph the layered graph.
     */
    private void restoreProperOrder(final List<LNode> dummies, final LGraph graph) {
        if (dummies.isEmpty()) {
            return;
        }
        
        // Turn the list into an array of dummy nodes and sort that by their original x coordinate
        LNode[] dummyArray = dummies.toArray(new LNode[dummies.size()]);
        
        Arrays.sort(dummyArray, new Comparator<LNode>() {
            public int compare(final LNode a, final LNode b) {
                return Double.compare(a.getProperty(InternalProperties.PORT_RATIO_OR_POSITION),
                        b.getProperty(InternalProperties.PORT_RATIO_OR_POSITION));
            }
        });
        
        assignAscendingCoordinates(dummyArray, graph);
    }
    
    /**
     * Iterates over the given array of dummy nodes, making sure that their x coordinates
     * are strictly ascending. Dummy nodes whose coordinates are in violation of this rule
     * are moved to the right. Once this method is finished, the coordinates of the dummy
     * nodes reflect their order in the array.
     * 
     * @param dummies array of dummy nodes.
     * @param graph the layered graph.
     */
    private void assignAscendingCoordinates(final LNode[] dummies, final LGraph graph) {
        // Find the edge distance
        float edgeSpacing = graph.getProperty(InternalProperties.SPACING)
                * graph.getProperty(Properties.EDGE_SPACING_FACTOR);
        
        // Now, iterate over the array, remembering the last assigned position. If we find a
        // position that is less than or equal to the last position, assign a new position of
        // "lastPosition + edgeSpacing"
        double lastCoordinate = dummies[0].getPosition().x + dummies[0].getSize().x;
        for (int index = 1; index < dummies.length; index++) {
            KVector currentPosition = dummies[index].getPosition();
            KVector currentSize = dummies[index].getSize();
            
            if (currentPosition.x <= lastCoordinate + edgeSpacing) {
                currentPosition.x = lastCoordinate + edgeSpacing;
            }
            
            lastCoordinate = currentPosition.x + currentSize.x;
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // STEP 3: EDGE ROUTING
    
    /**
     * Routes nothern and southern hierarchical port edges and ajusts the graph's height and
     * offsets accordingly.
     * 
     * @param layeredGraph the layered graph.
     * @param northSouthDummies the collection of restored northern and southern port dummies.
     */
    private void routeEdges(final LGraph layeredGraph, final Iterable<LNode> northSouthDummies) {
        // Prepare south and target layers for northern and southern routing
        Set<LNode> northernSourceLayer = Sets.newLinkedHashSet();
        Set<LNode> northernTargetLayer = Sets.newLinkedHashSet();
        Set<LNode> southernSourceLayer = Sets.newLinkedHashSet();
        Set<LNode> southernTargetLayer = Sets.newLinkedHashSet();
        
        // Find some routing parameters
        double nodeSpacing = layeredGraph.getProperty(InternalProperties.SPACING).doubleValue();
        double edgeSpacing = nodeSpacing * layeredGraph.getProperty(Properties.EDGE_SPACING_FACTOR);
        boolean debug = layeredGraph.getProperty(LayoutOptions.DEBUG_MODE);
        
        // Assemble the northern and southern hierarchical port dummies and the nodes they are
        // connected to
        for (LNode hierarchicalPortDummy : northSouthDummies) {
            PortSide portSide = hierarchicalPortDummy.getProperty(InternalProperties.EXT_PORT_SIDE);
            
            if (portSide == PortSide.NORTH) {
                northernTargetLayer.add(hierarchicalPortDummy);
                
                for (LEdge edge : hierarchicalPortDummy.getIncomingEdges()) {
                    northernSourceLayer.add(edge.getSource().getNode());
                }
            } else if (portSide == PortSide.SOUTH) {
                southernTargetLayer.add(hierarchicalPortDummy);
                
                for (LEdge edge : hierarchicalPortDummy.getIncomingEdges()) {
                    southernSourceLayer.add(edge.getSource().getNode());
                }
            }
        }
        
        // Northern routing
        if (!northernSourceLayer.isEmpty()) {
            // Route the edges using a south-to-north orthogonal edge router
            OrthogonalRoutingGenerator routingGenerator = new OrthogonalRoutingGenerator(
                    OrthogonalRoutingGenerator.RoutingDirection.SOUTH_TO_NORTH,
                    edgeSpacing, debug ? "extnorth" : null);
            
            int slots = routingGenerator.routeEdges(
                    layeredGraph,
                    northernSourceLayer,
                    0,
                    northernTargetLayer,
                    -nodeSpacing - layeredGraph.getOffset().y);
            
            // If anything was routed, adjust the graph's offset and height
            if (slots > 0) {
                northernExtPortEdgeRoutingHeight = nodeSpacing + (slots - 1) * edgeSpacing;

                layeredGraph.getOffset().y += northernExtPortEdgeRoutingHeight;
                layeredGraph.getSize().y += northernExtPortEdgeRoutingHeight;
            }
        }
        
        // Southern routing
        if (!southernSourceLayer.isEmpty()) {
            // Route the edges using a north-to-south orthogonal edge router
            OrthogonalRoutingGenerator routingGenerator = new OrthogonalRoutingGenerator(
                    OrthogonalRoutingGenerator.RoutingDirection.NORTH_TO_SOUTH,
                    edgeSpacing, debug ? "extsouth" : null);
            
            int slots = routingGenerator.routeEdges(
                    layeredGraph,
                    southernSourceLayer,
                    0,
                    southernTargetLayer,
                    layeredGraph.getSize().y + nodeSpacing - layeredGraph.getOffset().y);
            
            // Adjust graph height.
            if (slots > 0) {
                layeredGraph.getSize().y += nodeSpacing + (slots - 1) * edgeSpacing;
            }
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // STEP 4: REMOVE TEMPORARY DUMMIES
    
    /**
     * Removes the temporary hierarchical port dummies, reconnecting their incoming and outgoing
     * edges to the original dummies and setting the appropriate bend points.
     * 
     * @param layeredGraph the layered graph.
     */
    private void removeTemporaryNorthSouthDummies(final LGraph layeredGraph) {
        List<LNode> nodesToRemove = Lists.newArrayList();
        
        // Iterate through all layers
        for (Layer layer : layeredGraph) {
            for (LNode node : layer) {
                if (node.getType() != NodeType.EXTERNAL_PORT) {
                    // We're only looking for hierarchical port dummies
                    continue;
                }
                
                if (node.getProperty(InternalProperties.EXT_PORT_REPLACED_DUMMY) == null) {
                    // We're only looking for temporary north / south dummies
                    continue;
                }
                
                // There must be a port where all edges come in, another port where edges go out, and
                // a port with an edge connecting node and origin (that one was added previously by
                // this processor)
                LPort nodeInPort = null;
                LPort nodeOutPort = null;
                LPort nodeOriginPort = null;
                
                for (LPort port : node.getPorts()) {
                    switch (port.getSide()) {
                    case WEST:
                        nodeInPort = port;
                        break;
                    
                    case EAST:
                        nodeOutPort = port;
                        break;
                        
                    default:
                        nodeOriginPort = port;
                    }
                }
                
                // Find the edge connecting this dummy to the original external port dummy that we
                // restored just a while ago
                LEdge nodeToOriginEdge = nodeOriginPort.getOutgoingEdges().get(0);
                
                // Compute bend points for incoming edges
                KVectorChain incomingEdgeBendPoints = new KVectorChain(nodeToOriginEdge.getBendPoints());
                
                KVector firstBendPoint = new KVector(nodeOriginPort.getPosition());
                firstBendPoint.add(node.getPosition());
                incomingEdgeBendPoints.add(0, firstBendPoint);
                
                // Compute bend points for outgoing edges
                KVectorChain outgoingEdgeBendPoints = KVectorChain.reverse(
                        nodeToOriginEdge.getBendPoints());
                
                KVector lastBendPoint = new KVector(nodeOriginPort.getPosition());
                lastBendPoint.add(node.getPosition());
                outgoingEdgeBendPoints.add(lastBendPoint);
                
                // Retrieve the original hierarchical port dummy
                LNode replacedDummy = (LNode) node.getProperty(
                        InternalProperties.EXT_PORT_REPLACED_DUMMY);
                LPort replacedDummyPort = replacedDummy.getPorts().get(0);
                
                // Reroute all the input port's edges
                LEdge[] edges = nodeInPort.getIncomingEdges().toArray(new LEdge[0]);
                
                for (LEdge edge : edges) {
                    edge.setTarget(replacedDummyPort);
                    edge.getBendPoints().addAllAsCopies(
                            edge.getBendPoints().size(), incomingEdgeBendPoints);
                }
                
                // Reroute all the output port's edges
                edges = nodeOutPort.getOutgoingEdges().toArray(
                        new LEdge[nodeOutPort.getOutgoingEdges().size()]);
                
                for (LEdge edge : edges) {
                    edge.setSource(replacedDummyPort);
                    edge.getBendPoints().addAllAsCopies(0, outgoingEdgeBendPoints);
                }
                
                // Remove connection between node and original hierarchical port dummy
                nodeToOriginEdge.setSource(null);
                nodeToOriginEdge.setTarget(null);
                
                // Remember the temporary node for removal
                nodesToRemove.add(node);
            }
        }
        
        // Remove nodes
        for (LNode node : nodesToRemove) {
            node.setLayer(null);
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // STEP 5: FIX DUMMY COORDINATES
    
    /**
     * Fixes all hierarchical port dummy coordinates. For east / west external port dummies, this
     * means setting the x coordinate appropriately, and, in case of {@code FIXED_RATIO},
     * checking that the ratio is respected. For north / south hierarchical port dummies, this
     * means setting the y coordinate appropriately.
     * 
     * @param layeredGraph the layered graph.
     */
    private void fixCoordinates(final LGraph layeredGraph) {
        PortConstraints constraints = layeredGraph.getProperty(LayoutOptions.PORT_CONSTRAINTS);
        
        // East port dummies are in the first layer; all other dummies are in the last layer
        List<Layer> layers = layeredGraph.getLayers();
        fixCoordinates(
                layers.get(0),
                constraints,
                layeredGraph);
        fixCoordinates(
                layers.get(layers.size() - 1),
                constraints,
                layeredGraph);
    }
    
    /**
     * Fixes the coordinates of the nodes in the given layer.
     * 
     * @param layer the layer.
     * @param constraints external port constraints.
     * @param graph the graph.
     */
    private void fixCoordinates(final Layer layer, final PortConstraints constraints,
            final LGraph graph) {
        
        // Get some geometric values from the graph
        LInsets insets = graph.getInsets();
        float borderSpacing = graph.getProperty(InternalProperties.BORDER_SPACING);
        KVector offset = graph.getOffset();
        KVector graphActualSize = graph.getActualSize();
        
        double newActualGraphHeight = graphActualSize.y;
        
        // During the first iteration, EAST and WEST dummy nodes are fixed. This may change the height
        // of the graph, so we're setting y coordinates of NORTH and SOUTH dummies in a second iteration
        for (LNode node : layer) {
            if (node.getType() != NodeType.EXTERNAL_PORT) {
                // We're only looking for hierarchical port dummies
                continue;
            }
            
            PortSide extPortSide = node.getProperty(InternalProperties.EXT_PORT_SIDE);
            KVector extPortSize = node.getProperty(InternalProperties.EXT_PORT_SIZE);
            KVector nodePosition = node.getPosition();
            
            // Set x coordinate
            switch (extPortSide) {
            case EAST:
                nodePosition.x = graph.getSize().x + borderSpacing + insets.right - offset.x;
                break;
            
            case WEST:
                nodePosition.x = -offset.x - borderSpacing - insets.left;
                break;
            }
            
            // Set y coordinate
            double requiredActualGraphHeight = 0.0;
            
            switch (extPortSide) {
            case EAST:
            case WEST:
                if (constraints == PortConstraints.FIXED_RATIO) {
                    double ratio = node.getProperty(InternalProperties.PORT_RATIO_OR_POSITION);
                    nodePosition.y = graphActualSize.y * ratio
                            - node.getProperty(LayoutOptions.PORT_ANCHOR).y;
                    requiredActualGraphHeight = nodePosition.y + extPortSize.y;
                    node.borderToContentAreaCoordinates(false, true);
                } else if (constraints == PortConstraints.FIXED_POS) {
                    nodePosition.y = node.getProperty(InternalProperties.PORT_RATIO_OR_POSITION)
                            - node.getProperty(LayoutOptions.PORT_ANCHOR).y;
                    requiredActualGraphHeight = nodePosition.y + extPortSize.y;
                    node.borderToContentAreaCoordinates(false, true);
                }
                break;
            }
            
            newActualGraphHeight = Math.max(newActualGraphHeight, requiredActualGraphHeight);
        }
        
        // Make the graph larger, if necessary
        graph.getSize().y += newActualGraphHeight - graphActualSize.y;
        
        // Iterate over NORTH and SOUTH dummies now that the graph's height is fixed
        for (LNode node : layer) {
            if (node.getType() != NodeType.EXTERNAL_PORT) {
                // We're only looking for hierarchical port dummies
                continue;
            }
            
            PortSide extPortSide = node.getProperty(InternalProperties.EXT_PORT_SIDE);
            KVector nodePosition = node.getPosition();
            
            // Set y coordinate
            switch (extPortSide) {
            case NORTH:
                nodePosition.y = -offset.y - borderSpacing - insets.top;
                break;
            
            case SOUTH:
                nodePosition.y = graph.getSize().y + borderSpacing + insets.bottom - offset.y;
                break;
            }
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // STEP 6: SLANTED EDGE SEGMENT CORRECTION
    
    /**
     * Goes over the eastern and western hierarchical dummy nodes and checks whether their
     * incident edges have slanted segments.
     * 
     * @param layeredGraph the layered graph.
     */
    private void correctSlantedEdgeSegments(final LGraph layeredGraph) {
        // East port dummies are in the first layer; all other dummies are in the last layer
        List<Layer> layers = layeredGraph.getLayers();
        correctSlantedEdgeSegments(layers.get(0));
        correctSlantedEdgeSegments(layers.get(layers.size() - 1));
    }
    
    /**
     * Goes over the eastern and western hierarchical dummy nodes in the given layer and checks
     * whether their incident edges have slanted segments. Note that only the first and last
     * segment needs to be checked.
     * 
     * @param layer the layer.
     */
    private void correctSlantedEdgeSegments(final Layer layer) {
        for (LNode node : layer) {
            if (node.getType() != NodeType.EXTERNAL_PORT) {
                // We're only looking for hierarchical port dummies
                continue;
            }
            
            PortSide extPortSide = node.getProperty(InternalProperties.EXT_PORT_SIDE);
            
            if (extPortSide == PortSide.EAST || extPortSide == PortSide.WEST) {
                for (LEdge edge : node.getConnectedEdges()) {
                    KVectorChain bendPoints = edge.getBendPoints();
                    
                    if (bendPoints.isEmpty()) {
                        // TODO: The edge has no bend points yet, but may still be slanted. Handle that!
                        continue;
                    }
                    
                    // Correct a slanted segment connected to the source port if it belongs to our node
                    LPort sourcePort = edge.getSource();
                    
                    if (sourcePort.getNode() == node) {
                        KVector firstBendPoint = bendPoints.getFirst();
                        firstBendPoint.y = sourcePort.getAbsoluteAnchor().y;
                    }
                    
                    // Correct a slanted segment connected to the target port if it belongs to our node
                    LPort targetPort = edge.getTarget();
                    
                    if (targetPort.getNode() == node) {
                        KVector lastBendPoint = bendPoints.getLast();
                        lastBendPoint.y = targetPort.getAbsoluteAnchor().y;
                    }
                }
            }
        }
    }
    
}
