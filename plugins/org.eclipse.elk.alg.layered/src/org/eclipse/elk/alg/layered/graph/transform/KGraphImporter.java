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
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphElement;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LInsets;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.p3order.CrossingMinimizationStrategy;
import org.eclipse.elk.alg.layered.p4nodes.NodePlacementStrategy;
import org.eclipse.elk.alg.layered.properties.GraphProperties;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.PortType;
import org.eclipse.elk.alg.layered.properties.Properties;
import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KPoint;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.adapters.KGraphAdapters;
import org.eclipse.elk.core.util.labelspacing.LabelSpaceCalculation;
import org.eclipse.elk.core.util.nodespacing.Spacing.Insets;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Implements the graph import aspect of {@link KGraphTransformer}.
 * 
 * @author cds
 */
class KGraphImporter {
    
    /** map between KGraph nodes / ports and the LGraph nodes / ports created for them. */
    private final Map<KGraphElement, LGraphElement> nodeAndPortMap = Maps.newHashMap();
    

    /////////////////////////////////////////////////////////////
    // Import Entry Points

    /**
     * Imports the given graph.
     * 
     * @param kgraph
     *            the graph to import.
     * @return the transformed graph.
     */
    public LGraph importGraph(final KNode kgraph) {
        // Create the layered graph
        final LGraph topLevelGraph = createLGraph(kgraph);
        
        // Transform the external ports, if any
        Set<GraphProperties> graphProperties = topLevelGraph.getProperty(
                InternalProperties.GRAPH_PROPERTIES);
        checkExternalPorts(kgraph, graphProperties);
        if (graphProperties.contains(GraphProperties.EXTERNAL_PORTS)) {
            for (KPort kport : kgraph.getPorts()) {
                transformExternalPort(kgraph, topLevelGraph, kport);
            }
        }
        
        // Import the graph either with or without all nested levels of hierarchy
        if (kgraph.getData(KShapeLayout.class).getProperty(LayoutOptions.LAYOUT_HIERARCHY)) {
            importHierarchicalGraph(kgraph, topLevelGraph);
        } else {
            importFlatGraph(kgraph, topLevelGraph);
        }
        
        return topLevelGraph;
    }

    /**
     * Imports the direct children of the given graph.
     * 
     * @param kgraph
     *            graph to import.
     * @param lgraph
     *            graph to add the imported elements to.
     */
    private void importFlatGraph(final KNode kgraph, final LGraph lgraph) {
        // Transform the node's children, unless we're told not to
        for (KNode child : kgraph.getChildren()) {
            if (!child.getData(KShapeLayout.class).getProperty(LayoutOptions.NO_LAYOUT)) {
                transformNode(child, lgraph);
            }
        }
        
        // Transform the outgoing edges of children (this is not part of the previous loop since all
        // children must have already been transformed)
        for (KNode child : kgraph.getChildren()) {
            // Is inside self loop processing enabled for this node?
            KShapeLayout childLayout = child.getData(KShapeLayout.class);
            boolean enableInsideSelfLoops = childLayout.getProperty(LayoutOptions.SELF_LOOP_INSIDE);
            
            for (KEdge kedge : child.getOutgoingEdges()) {
                // Find out basic information about the edge
                KEdgeLayout kedgeLayout = kedge.getData(KEdgeLayout.class);
                boolean isToBeLaidOut = !kedgeLayout.getProperty(LayoutOptions.NO_LAYOUT);
                boolean isInsideSelfLoop = enableInsideSelfLoops && kedge.getTarget() == child
                        && kedgeLayout.getProperty(LayoutOptions.SELF_LOOP_INSIDE);
                boolean connectsToGraph = kedge.getTarget() == kgraph;
                boolean connectsToSibling = kedge.getTarget().getParent() == kgraph;
                
                // Only transform the edge if we are to layout the edge and if it stays in the current
                // level of hierarchy (which implies that we don't transform inside self loops)
                if (isToBeLaidOut && !isInsideSelfLoop && (connectsToGraph || connectsToSibling)) {
                    transformEdge(kedge, lgraph);
                }
            }
        }
        
        // Transform the outgoing edges of the graph itself (either inside self loops or edges connected
        // to its children)
        KShapeLayout shapeLayout = kgraph.getData(KShapeLayout.class);
        boolean enableInsideSelfLoops = shapeLayout.getProperty(LayoutOptions.SELF_LOOP_INSIDE);
        
        for (KEdge kedge : kgraph.getOutgoingEdges()) {
            KEdgeLayout kedgeLayout = kedge.getData(KEdgeLayout.class);
            boolean isToBeLaidOut = !kedgeLayout.getProperty(LayoutOptions.NO_LAYOUT);
            boolean isInsideSelfLoop = enableInsideSelfLoops && kedge.getTarget() == kgraph
                    && kedgeLayout.getProperty(LayoutOptions.SELF_LOOP_INSIDE);
            boolean connectsToChild = kedge.getTarget().getParent() == kgraph;
            
            if (isToBeLaidOut && (connectsToChild || isInsideSelfLoop)) {
                transformEdge(kedge, lgraph);
            }
        }
    }

    /**
     * Imports the graph hierarchy rooted at the given graph.
     * 
     * @param kgraph
     *            graph to import.
     * @param lgraph
     *            graph to add the direct children of the current hierarchy level to.
     */
    private void importHierarchicalGraph(final KNode kgraph, final LGraph lgraph) {
        final Queue<KNode> knodeQueue = Lists.newLinkedList();

        // Transform the node's children
        knodeQueue.addAll(kgraph.getChildren());
        while (!knodeQueue.isEmpty()) {
            KNode knode = knodeQueue.poll();
            KShapeLayout knodeLayout = knode.getData(KShapeLayout.class);
            
            // Check if the current node is to be laid out in the first place
            boolean isNodeToBeLaidOut = !knodeLayout.getProperty(LayoutOptions.NO_LAYOUT);
            if (isNodeToBeLaidOut) {
                // Transform da node!!!
                LGraph parentLGraph = lgraph;
                LNode parentLNode = (LNode) nodeAndPortMap.get(knode.getParent());
                if (parentLNode != null) {
                    parentLGraph = parentLNode.getProperty(InternalProperties.NESTED_LGRAPH);
                }
                LNode lnode = transformNode(knode, parentLGraph);
                
                // Check if there has to be an LGraph for this node (which is the case if it has
                // children or inside self-loops)
                boolean hasChildren = !knode.getChildren().isEmpty();
                boolean hasInsideSelfLoops = hasInsideSelfLoops(knode);
                
                if (hasChildren || hasInsideSelfLoops) {
                    LGraph nestedGraph = createLGraph(knode);
                    lnode.setProperty(InternalProperties.NESTED_LGRAPH, nestedGraph);
                    nestedGraph.setProperty(InternalProperties.PARENT_LNODE, lnode);
                    knodeQueue.addAll(knode.getChildren());
                }
            }
        }

        // Transform the edges
        knodeQueue.add(kgraph);
        while (!knodeQueue.isEmpty()) {
            KNode knode = knodeQueue.poll();
            KShapeLayout knodeLayout = knode.getData(KShapeLayout.class);
            
            boolean enableInsideSelfLoops = knodeLayout.getProperty(LayoutOptions.SELF_LOOP_INSIDE);

            // Check if the current node is to be laid out in the first place
            boolean isNodeToBeLaidOut = !knodeLayout.getProperty(LayoutOptions.NO_LAYOUT);
            if (isNodeToBeLaidOut) {
                for (KEdge kedge : knode.getOutgoingEdges()) {
                    KEdgeLayout kedgeLayout = kedge.getData(KEdgeLayout.class);
                    
                    // Check if the current edge is to be laid out
                    boolean isEdgeToBeLaidOut = !kedgeLayout.getProperty(LayoutOptions.NO_LAYOUT);
                    if (isEdgeToBeLaidOut) {
                        // Check if this edge is an inside self-loop
                        boolean isInsideSelfLoop = enableInsideSelfLoops
                                && kedge.getSource() == kedge.getTarget()
                                && kedgeLayout.getProperty(LayoutOptions.SELF_LOOP_INSIDE);
                        
                        // Find the graph the edge will be placed in. Basically, if the edge is an inside
                        // self loop or connects to a descendant of this node, the edge will be placed in
                        // the graph that represents the node's insides. Otherwise, it will be placed in
                        // the graph that represents the node's parent.
                        KNode parentKGraph = knode;
                        if (!ElkUtil.isDescendant(kedge.getTarget(), knode) && !isInsideSelfLoop) {
                            parentKGraph = knode.getParent();
                        }
                        
                        LGraph parentLGraph = lgraph;
                        LNode parentLNode = (LNode) nodeAndPortMap.get(parentKGraph);
                        if (parentLNode != null) {
                            parentLGraph = parentLNode.getProperty(InternalProperties.NESTED_LGRAPH);
                        }
                        
                        // Transform the edge, finally...
                        transformEdge(kedge, parentLGraph);
                    }
                }
                
                knodeQueue.addAll(knode.getChildren());
            }
        }
    }
    
    /**
     * Checks if the given node has any inside self loops.
     * 
     * @param knode the node to check for inside self loops.
     * @return {@code true} if the node has inside self loops, {@code false} otherwise.
     */
    private boolean hasInsideSelfLoops(final KNode knode) {
        KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);
        
        if (nodeLayout.getProperty(LayoutOptions.SELF_LOOP_INSIDE)) {
            for (KEdge edge : knode.getOutgoingEdges()) {
                if (edge.getTarget() == knode) {
                    final KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
                    if (edgeLayout.getProperty(LayoutOptions.SELF_LOOP_INSIDE)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    

    /////////////////////////////////////////////////////////////
    // Graph Transformation

    /**
     * Create an LGraph from the given KNode.
     * 
     * @param kgraph
     *            the parent KNode from which to create the LGraph
     * @return a new LGraph instance
     */
    private LGraph createLGraph(final KNode kgraph) {
        LGraph lgraph = new LGraph();
        
        // Copy the properties of the KGraph to the layered graph
        KShapeLayout parentLayout = kgraph.getData(KShapeLayout.class);
        lgraph.copyProperties(parentLayout);
        if (lgraph.getProperty(LayoutOptions.DIRECTION) == Direction.UNDEFINED) {
            lgraph.setProperty(LayoutOptions.DIRECTION, LGraphUtil.getDirection(lgraph));
        }
        
        // Remember the KGraph parent the LGraph was created from
        lgraph.setProperty(InternalProperties.ORIGIN, kgraph);

        // Initialize the graph properties discovered during the transformations
        lgraph.setProperty(InternalProperties.GRAPH_PROPERTIES,
                EnumSet.noneOf(GraphProperties.class));
        
        // Adjust the insets to respect inside labels.
        float labelSpacing = lgraph.getProperty(LayoutOptions.LABEL_SPACING);
        Insets insets = LabelSpaceCalculation.calculateRequiredNodeLabelSpace(
                KGraphAdapters.adaptSingleNode(kgraph), labelSpacing);
        
        // Copy the insets to the layered graph
        LInsets linsets = lgraph.getInsets();
        linsets.left = insets.left;
        linsets.right = insets.right;
        linsets.top = insets.top;
        linsets.bottom = insets.bottom;
        
        return lgraph;
    }
    
    
    /////////////////////////////////////////////////////////////
    // External Port Transformation
    
    /**
     * Checks if external ports processing should be active. This is the case if the parent node has
     * ports and at least one of the following conditions is true:
     * <ul>
     *   <li>
     *     Port label placement is set to {@code INSIDE} and at least one of the ports has a label.
     *   </li>
     *   <li>
     *     At least one of the ports has an edge that connects to the insides of the parent node.
     *   </li>
     *   <li>
     *     There is a self-loop that should be routed inside the node.
     *   </li>
     * </ul>
     * 
     * @param kgraph
     *            a KGraph we want to check for external ports.
     * @param graphProperties
     *            the set of graph properties to store our results in.
     */
    private void checkExternalPorts(final KNode kgraph, final Set<GraphProperties> graphProperties) {
        final KShapeLayout shapeLayout = kgraph.getData(KShapeLayout.class);
        final boolean enableSelfLoops = shapeLayout.getProperty(LayoutOptions.SELF_LOOP_INSIDE);
        final PortLabelPlacement portLabelPlacement = shapeLayout.getProperty(
                LayoutOptions.PORT_LABEL_PLACEMENT);
        
        // We're iterating over the ports until we've determined that we have both external ports and
        // hyperedges, or if there are no more ports left
        boolean hasExternalPorts = false;
        boolean hasHyperedges = false;
        
        final Iterator<KPort> portIterator = kgraph.getPorts().iterator();
        while (portIterator.hasNext() && (!hasExternalPorts || !hasHyperedges)) {
            final KPort kport = portIterator.next();
            
            // Find out if there are edges connected to external ports of the graph (this is the case
            // for inside self loops as well as for edges connected to children)
            int externalPortEdges = 0;
            for (KEdge kedge : kport.getEdges()) {
                boolean isInsideSelfLoop = enableSelfLoops && kedge.getSource() == kedge.getTarget()
                        && kedge.getData(KEdgeLayout.class).getProperty(LayoutOptions.SELF_LOOP_INSIDE);
                boolean connectsToChild = kgraph == kedge.getSource().getParent()
                        || kgraph == kedge.getTarget().getParent();
                
                if (isInsideSelfLoop || connectsToChild) {
                    externalPortEdges++;
                    if (externalPortEdges > 1) {
                        break;
                    }
                }
            }
            
            // External ports?
            if (externalPortEdges > 0) {
                hasExternalPorts = true;
            } else if (portLabelPlacement == PortLabelPlacement.INSIDE && kport.getLabels().size() > 0) {
                hasExternalPorts = true;
            }
            
            // Hyperedges, even?
            if (externalPortEdges > 1) {
                hasHyperedges = true;
            }
        }
        
        // Update graph properties
        if (hasExternalPorts) {
            graphProperties.add(GraphProperties.EXTERNAL_PORTS);
        }
        
        if (hasHyperedges) {
            graphProperties.add(GraphProperties.HYPEREDGES);
        }
    }

    /**
     * Transforms the given external port into a dummy node.
     * 
     * @param kgraph
     *            the original KGraph
     * @param lgraph
     *            the corresponding layered graph
     * @param kport
     *            the port to be transformed
     */
    private void transformExternalPort(final KNode kgraph, final LGraph lgraph, final KPort kport) {
        KShapeLayout kgraphLayout = kgraph.getData(KShapeLayout.class);
        KShapeLayout kportLayout = kport.getData(KShapeLayout.class);

        // We need some information about the port
        KVector kportPosition = new KVector(kportLayout.getXpos() + kportLayout.getWidth() / 2.0,
                kportLayout.getYpos() + kportLayout.getHeight() / 2.0);
        int netFlow = calculateNetFlow(kport);
        PortSide portSide = kportLayout.getProperty(LayoutOptions.PORT_SIDE);
        PortConstraints portConstraints = kgraphLayout.getProperty(LayoutOptions.PORT_CONSTRAINTS);
        
        // If we don't have a proper port side, calculate one
        Direction layoutDirection = lgraph.getProperty(LayoutOptions.DIRECTION);
        if (portSide == PortSide.UNDEFINED) {
            portSide = ElkUtil.calcPortSide(kport, layoutDirection);
            kportLayout.setProperty(LayoutOptions.PORT_SIDE, portSide);
        }
        
        // If we don't have a port offset, infer one
        Float portOffset = kportLayout.getProperty(LayoutOptions.PORT_OFFSET);
        if (portOffset == null) {
            // if port coordinates are (0,0), we default to port offset 0 to make the common case
            // frustration-free
            if (kportLayout.getXpos() == 0.0f && kportLayout.getYpos() == 0.0f) {
                portOffset = 0.0f;
            } else {
                portOffset = ElkUtil.calcPortOffset(kport, portSide);
            }
            kportLayout.setProperty(LayoutOptions.PORT_OFFSET, portOffset);
        }
        
        // Create the external port dummy node
        KVector graphSize = new KVector(kgraphLayout.getWidth(), kgraphLayout.getHeight());
        LNode dummy = LGraphUtil.createExternalPortDummy(
                kportLayout, portConstraints, portSide, netFlow, graphSize,
                kportPosition, new KVector(kportLayout.getWidth(), kportLayout.getHeight()),
                layoutDirection, lgraph);
        dummy.setProperty(InternalProperties.ORIGIN, kport);
        
        // If the compound node wants to have its port labels placed on the inside, we need to leave
        // enough space for them by creating an LLabel for the KLabels
        if (kgraphLayout.getProperty(LayoutOptions.PORT_LABEL_PLACEMENT) == PortLabelPlacement.INSIDE) {
            // The dummy only has one port
            LPort dummyPort = dummy.getPorts().get(0);
            dummy.setProperty(LayoutOptions.PORT_LABEL_PLACEMENT, PortLabelPlacement.OUTSIDE);
            
            // Transform all of the port's labels
            for (KLabel klabel : kport.getLabels()) {
                KShapeLayout labelLayout = klabel.getData(KShapeLayout.class);
                if (!labelLayout.getProperty(LayoutOptions.NO_LAYOUT)) {
                    dummyPort.getLabels().add(transformLabel(klabel));
                }
            }
        }
        
        // Put the external port dummy into our graph and associate it with the original KPort
        lgraph.getLayerlessNodes().add(dummy);
        nodeAndPortMap.put(kport, dummy);
    }
    
    /**
     * Count how many edges want the port to be an output port of the parent and how many want it to
     * be an input port. An edge coming into the port from the inside votes for the port to be an
     * output port of the parent, as does an edge leaving the port for the outside. The result returned
     * by this method is the so-called net flow as fed into {@code createExternalPort(..)}.
     * 
     * @param kport
     *            the port to look at.
     * @return the port's net flow.
     */
    private int calculateNetFlow(final KPort kport) {
        final KNode kgraph = kport.getNode();
        
        int outputPortVote = 0, inputPortVote = 0;
        for (KEdge edge : kport.getEdges()) {
            if (edge.getSourcePort() == kport) {
                if (edge.getTarget().getParent() == kgraph || edge.getTarget() == kgraph) {
                    inputPortVote++;
                } else {
                    outputPortVote++;
                }
            } else {
                if (edge.getSource().getParent() == kgraph || edge.getSource() == kgraph) {
                    outputPortVote++;
                } else {
                    inputPortVote++;
                }
            }
        }
        
        return outputPortVote - inputPortVote;
    }
    
    
    /////////////////////////////////////////////////////////////
    // Node Transformation

    /**
     * Transforms the given node and its contained ports.
     * 
     * @param knode
     *            the node to transform
     * @param lgraph
     *            the layered graph into which the transformed node is put
     * @return the transformed node
     */
    private LNode transformNode(final KNode knode, final LGraph lgraph) {
        KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);

        // add a new node to the layered graph, copying its position
        LNode lnode = new LNode(lgraph);
        lnode.copyProperties(nodeLayout);
        lnode.setProperty(InternalProperties.ORIGIN, knode);
        
        lnode.getSize().x = nodeLayout.getWidth();
        lnode.getSize().y = nodeLayout.getHeight();
        lnode.getPosition().x = nodeLayout.getXpos();
        lnode.getPosition().y = nodeLayout.getYpos();
        
        lgraph.getLayerlessNodes().add(lnode);
        nodeAndPortMap.put(knode, lnode);
        
        // check if the node is a compound node in the original graph
        if (!knode.getChildren().isEmpty()) {
            lnode.setProperty(InternalProperties.COMPOUND_NODE, true);
        }

        // port constraints and sides cannot be undefined
        Set<GraphProperties> graphProperties = lgraph.getProperty(
                InternalProperties.GRAPH_PROPERTIES);
        
        PortConstraints portConstraints = lnode.getProperty(LayoutOptions.PORT_CONSTRAINTS);
        if (portConstraints == PortConstraints.UNDEFINED) {
            lnode.setProperty(LayoutOptions.PORT_CONSTRAINTS, PortConstraints.FREE);
        } else if (portConstraints != PortConstraints.FREE) {
            // if the port constraints are not free, set the appropriate graph property
            graphProperties.add(GraphProperties.NON_FREE_PORTS);
        }

        // transform the ports
        Direction direction = lgraph.getProperty(LayoutOptions.DIRECTION);
        for (KPort kport : knode.getPorts()) {
            KShapeLayout kportLayout = kport.getData(KShapeLayout.class);
            
            if (!kportLayout.getProperty(LayoutOptions.NO_LAYOUT)) {
                transformPort(kport, lnode, graphProperties, direction, portConstraints);
            }
        }

        // add the node's labels
        for (KLabel klabel : knode.getLabels()) {
            KShapeLayout labelLayout = klabel.getData(KShapeLayout.class);
            if (!labelLayout.getProperty(LayoutOptions.NO_LAYOUT)) {
                lnode.getLabels().add(transformLabel(klabel));
            }
        }

        if (lnode.getProperty(LayoutOptions.COMMENT_BOX)) {
            graphProperties.add(GraphProperties.COMMENTS);
        }

        // if we have a hypernode without ports, create a default input and output port
        if (lnode.getProperty(LayoutOptions.HYPERNODE)) {
            graphProperties.add(GraphProperties.HYPERNODES);
            graphProperties.add(GraphProperties.HYPEREDGES);
            lnode.setProperty(LayoutOptions.PORT_CONSTRAINTS, PortConstraints.FREE);
        }
        
        return lnode;
    }
    
    
    /////////////////////////////////////////////////////////////
    // Port Transformation
    
    /**
     * Transforms the given port. The new port will be added to the given node and will be
     * registered with the {@code transformMap}.
     * 
     * @param kport
     *            the port to transform.
     * @param parentLNode
     *            the node the port should be added to.
     * @param graphProperties
     *            the graph properties of the graph the transformed port will be part of. The graph
     *            properties are modified depending on the port's properties.
     * @param layoutDirection
     *            the layout direction in the graph the port will be part of.
     * @param portConstraints
     *            the port constraints of the port's node.
     * @return the transformed port.
     */
    private LPort transformPort(final KPort kport, final LNode parentLNode,
            final Set<GraphProperties> graphProperties, final Direction layoutDirection,
            final PortConstraints portConstraints) {
        
        final KShapeLayout kportLayout = kport.getData(KShapeLayout.class);

        // create layered port, copying its position
        LPort lport = new LPort();
        lport.copyProperties(kportLayout);
        lport.setSide(kportLayout.getProperty(LayoutOptions.PORT_SIDE));
        lport.setProperty(InternalProperties.ORIGIN, kport);
        lport.setNode(parentLNode);
        
        KVector portSize = lport.getSize();
        portSize.x = kportLayout.getWidth();
        portSize.y = kportLayout.getHeight();
        
        KVector portPos = lport.getPosition();
        portPos.x = kportLayout.getXpos();
        portPos.y = kportLayout.getYpos();
        
        nodeAndPortMap.put(kport, lport);
        
        // check if the original port has any connections to descendants of its node
        for (KEdge edge : kport.getEdges()) {
            if (edge.getSource() == kport.getNode()) {
                // check if the edge's target is a descendant of its source node
                if (ElkUtil.isDescendant(edge.getTarget(), kport.getNode())) {
                    lport.setProperty(InternalProperties.INSIDE_CONNECTIONS, true);
                    break;
                }
            } else {
                // check if the edge's source is a descendant of its source node
                if (ElkUtil.isDescendant(edge.getSource(), kport.getNode())) {
                    lport.setProperty(InternalProperties.INSIDE_CONNECTIONS, true);
                    break;
                }
            }
        }

        // initialize the port's side, offset, and anchor point
        LGraphUtil.initializePort(lport, portConstraints, layoutDirection,
                kportLayout.getProperty(LayoutOptions.PORT_ANCHOR));

        // create the port's labels
        for (KLabel klabel : kport.getLabels()) {
            KShapeLayout labelLayout = klabel.getData(KShapeLayout.class);
            if (!labelLayout.getProperty(LayoutOptions.NO_LAYOUT)) {
                lport.getLabels().add(transformLabel(klabel));
            }
        }

        // Check if we need to add anything to the graph properties
        if (kport.getEdges().size() > 1) {
            graphProperties.add(GraphProperties.HYPEREDGES);
        }
        
        switch (layoutDirection) {
        case LEFT:
        case RIGHT:
            if (lport.getSide() == PortSide.NORTH || lport.getSide() == PortSide.SOUTH) {
                graphProperties.add(GraphProperties.NORTH_SOUTH_PORTS);
            }
            break;
        case UP:
        case DOWN:
            if (lport.getSide() == PortSide.EAST || lport.getSide() == PortSide.WEST) {
                graphProperties.add(GraphProperties.NORTH_SOUTH_PORTS);
            }
            break;
        }
        
        return lport;
    }
    
    
    /////////////////////////////////////////////////////////////
    // Edge Transformation

    /**
     * Transforms the given edge.
     * 
     * @param kedge the edge to transform
     * @param lgraph the layered graph
     * @return the transformed edge, or {@code null} if it cannot be transformed
     */
    private LEdge transformEdge(final KEdge kedge, final LGraph lgraph) {
        // Find the transformed source and target nodes
        LNode sourceLNode = (LNode) nodeAndPortMap.get(kedge.getSource());
        LNode targetLNode = (LNode) nodeAndPortMap.get(kedge.getTarget());
        LPort sourceLPort = null;
        LPort targetLPort = null;

        // Find the transformed source port, if any
        if (kedge.getSourcePort() != null) {
            assert kedge.getSource() == kedge.getSourcePort().getNode();
            
            // If the KPort is a regular port, it will map to an LPort; if it's an external port, it
            // will map to an LNode
            LGraphElement sourceElem = nodeAndPortMap.get(kedge.getSourcePort());
            if (sourceElem instanceof LPort) {
                sourceLPort = (LPort) sourceElem;
            } else if (sourceElem instanceof LNode) {
                sourceLNode = (LNode) sourceElem;
                sourceLPort = sourceLNode.getPorts().get(0);
            }
        }

        // Find the transformed target port, if any
        if (kedge.getTargetPort() != null) {
            assert kedge.getTarget() == kedge.getTargetPort().getNode();

            // If the KPort is a regular port, it will map to an LPort; if it's an external port, it
            // will map to an LNode
            LGraphElement targetElem = nodeAndPortMap.get(kedge.getTargetPort());
            if (targetElem instanceof LPort) {
                targetLPort = (LPort) targetElem;
            } else if (targetElem instanceof LNode) {
                targetLNode = (LNode) targetElem;
                targetLPort = targetLNode.getPorts().get(0);
            }
        }
        
        // If either the source or the target of the edge wasn't properly transformed for whatever
        // reason, we back out
        if (sourceLNode == null || targetLNode == null) {
            return null;
        }
        
        KEdgeLayout kedgeLayout = kedge.getData(KEdgeLayout.class);
        
        // Create a layered edge
        LEdge ledge = new LEdge();
        ledge.copyProperties(kedgeLayout);
        ledge.setProperty(InternalProperties.ORIGIN, kedge);
        
        // Clear junction points, since they are recomputed from scratch
        ledge.setProperty(LayoutOptions.JUNCTION_POINTS, null);
        
        // If we have a self-loop, set the appropriate graph property
        Set<GraphProperties> graphProperties = lgraph.getProperty(
                InternalProperties.GRAPH_PROPERTIES);
        if (sourceLNode == targetLNode) {
            graphProperties.add(GraphProperties.SELF_LOOPS);
        }

        // Create source and target ports if they do not exist yet
        if (sourceLPort == null) {
            PortType portType = PortType.OUTPUT;
            KVector sourcePoint = null;
            if (sourceLNode.getProperty(LayoutOptions.PORT_CONSTRAINTS).isSideFixed()) {
                sourcePoint = kedgeLayout.getSourcePoint().createVector();
                if (ElkUtil.isDescendant(kedge.getTarget(), kedge.getSource())) {
                    // External source port: put it on the west side
                    portType = PortType.INPUT;
                    sourcePoint.add(sourceLNode.getPosition());
                }
            }
            sourceLPort = LGraphUtil.createPort(sourceLNode, sourcePoint, portType, lgraph);
        }
        
        if (targetLPort == null) {
            PortType portType = PortType.INPUT;
            KVector targetPoint = null;
            if (targetLNode.getProperty(LayoutOptions.PORT_CONSTRAINTS).isSideFixed()) {
                targetPoint = kedgeLayout.getTargetPoint().createVector();
                if (kedge.getSource().getParent() != kedge.getTarget().getParent()) {
                    // Cross-hierarchy edge: correct the target position
                    KNode referenceNode = kedge.getSource();
                    if (!ElkUtil.isDescendant(kedge.getTarget(), kedge.getSource())) {
                        referenceNode = referenceNode.getParent();
                        if (ElkUtil.isDescendant(kedge.getSource(), kedge.getTarget())) {
                            portType = PortType.OUTPUT;
                        }
                    }
                    ElkUtil.toAbsolute(targetPoint, referenceNode);
                    ElkUtil.toRelative(targetPoint, kedge.getTarget().getParent());
                }
            }
            targetLPort = LGraphUtil.createPort(
                    targetLNode, targetPoint, portType, targetLNode.getGraph());
        }
        
        // Finally set the source and target of the edge
        ledge.setSource(sourceLPort);
        ledge.setTarget(targetLPort);

        // Transform the edge's labels
        for (KLabel klabel : kedge.getLabels()) {
            KShapeLayout labelLayout = klabel.getData(KShapeLayout.class);
            if (!labelLayout.getProperty(LayoutOptions.NO_LAYOUT)) {
                LLabel llabel = transformLabel(klabel);
                ledge.getLabels().add(llabel);
                
                // Depending on the label placement, we want to set graph properties and make sure the
                // edge label placement is actually properly defined
                switch (llabel.getProperty(LayoutOptions.EDGE_LABEL_PLACEMENT)) {
                case HEAD:
                case TAIL:
                    graphProperties.add(GraphProperties.END_LABELS);
                    break;
                    
                case CENTER:
                case UNDEFINED:
                    graphProperties.add(GraphProperties.CENTER_LABELS);
                    llabel.setProperty(LayoutOptions.EDGE_LABEL_PLACEMENT,
                            EdgeLabelPlacement.CENTER);
                }
            }
        }
        
        // Copy the original bend points of the edge in case they are required
        boolean bendPointsRequired =
                lgraph.getProperty(Properties.CROSS_MIN) == CrossingMinimizationStrategy.INTERACTIVE
                || lgraph.getProperty(Properties.NODE_PLACEMENT) == NodePlacementStrategy.INTERACTIVE;
        
        if (!kedgeLayout.getBendPoints().isEmpty() && bendPointsRequired) {
            KVectorChain bendpoints = new KVectorChain();
            for (KPoint point : kedgeLayout.getBendPoints()) {
                bendpoints.add(point.createVector());
            }
            ledge.setProperty(InternalProperties.ORIGINAL_BENDPOINTS, bendpoints);
        }
        
        return ledge;
    }
    
    
    /////////////////////////////////////////////////////////////
    // Label Transformation

    /**
     * Transform the given {@code KLabel} into an {@code LLabel}.
     * 
     * @param klabel the label to transform.
     * @return the created {@code LLabel}.
     */
    private LLabel transformLabel(final KLabel klabel) {
        KShapeLayout klabelLayout = klabel.getData(KShapeLayout.class);
        
        LLabel newLabel = new LLabel(klabel.getText());
        newLabel.copyProperties(klabelLayout);
        newLabel.setProperty(InternalProperties.ORIGIN, klabel);
        newLabel.getSize().x = klabelLayout.getWidth();
        newLabel.getSize().y = klabelLayout.getHeight();
        newLabel.getPosition().x = klabelLayout.getXpos();
        newLabel.getPosition().y = klabelLayout.getYpos();
        
        return newLabel;
    }

}
