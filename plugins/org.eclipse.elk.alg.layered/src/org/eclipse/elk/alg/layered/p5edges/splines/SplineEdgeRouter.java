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
package org.eclipse.elk.alg.layered.p5edges.splines;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.elk.alg.layered.ILayoutPhase;
import org.eclipse.elk.alg.layered.IntermediateProcessingConfiguration;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.layered.options.GraphProperties;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.p5edges.PolylineEdgeRouter;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.Pair;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implements a way of routing the edges with splines. Uses the dummy nodes as reference points for
 * a spline calculation, but the dummy nodes do not lay on the edge. They are only approximated.
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>the graph has a proper layering with assigned node and port positions</dd>
 *     <dd>the size of each layer is correctly set</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>each node is assigned a horizontal coordinate</dd>
 *     <dd>the bend points of each edge are set such that they can be interpreted as Bezier splines</dd>
 *     <dd>the width of the whole graph is set</dd>
 * </dl>
 * 
 * @author tit
 */
public final class SplineEdgeRouter implements ILayoutPhase {
    
    // /////////////////////////////////////////////////////////////////////////////
    // Constants and Variables
    
    /** An edge is drawn as a straight line if the y-difference of source/target is lower than this. */
    private static final double MAX_VERTICAL_DIFF_FOR_STRAIGHT = 0.2;
    /** X-difference between two vertical segments. May be overwritten. */
    private double hEdgeSpacing = SplinesMath.THREE;
    /** Avoiding magic number problems. */
    private static final double ONE_HALF = 0.5;
    /** Default dimension of an edge-spline. */
    private static final int DIMENSION = 3;
    /** Defines the gap between a node and the first vertical segment of an edge. */
    private static final double NODE_TO_VERTICAL_SEGMENT_GAP = 10;
    /**
     * Defines the gap between the source/target anchor of an edge and the control point that is
     * inserted to straighten the start/end of the edge-spline.
     */
    private static final double NODE_TO_STRAIGHTENING_CP_GAP = 5;

    //////////////////////////////////////////////////
    // Hyper-Edge Constants
    
    /**
     * Defines the fraction of the outer y position of a hyper-edge for defining the "point of overlap"
     * of two hyper-edges. 1.0 means the point lays on the outer border of the hyper-edge.
     */
    private static final double RELEVANT_POS_OUTER_RATE = 0.9;
    /** See RELEVANT_POS_OUTER_RATE! */
    private static final double RELEVANT_POS_MID_RATE = 1 - RELEVANT_POS_OUTER_RATE;
    
    //////////////////////////////////////////////////
    // Intermediate processing configurations
    
    /** additional processor dependencies for graphs with self-loops. */
    private static final IntermediateProcessingConfiguration SELF_LOOP_PROCESSING_ADDITIONS =
            IntermediateProcessingConfiguration.createEmpty()
                    .addBeforePhase1(IntermediateProcessorStrategy.SPLINE_SELF_LOOP_PREPROCESSOR)
                    .addBeforePhase4(IntermediateProcessorStrategy.SPLINE_SELF_LOOP_POSITIONER)
                    .addBeforePhase4(IntermediateProcessorStrategy.SPLINE_SELF_LOOP_ROUTER);

    /** additional processor dependencies for graphs with center edge labels. */
    private static final IntermediateProcessingConfiguration CENTER_EDGE_LABEL_PROCESSING_ADDITIONS =
            IntermediateProcessingConfiguration.createEmpty()
                    .addBeforePhase2(IntermediateProcessorStrategy.LABEL_DUMMY_INSERTER)
                    .addBeforePhase3(IntermediateProcessorStrategy.LABEL_DUMMY_SWITCHER)
                    .addBeforePhase4(IntermediateProcessorStrategy.LABEL_SIDE_SELECTOR)
                    .addAfterPhase5(IntermediateProcessorStrategy.LABEL_DUMMY_REMOVER);

    /** additional processor dependencies for graphs with possible inverted ports. */
    private static final IntermediateProcessingConfiguration INVERTED_PORT_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase3(IntermediateProcessorStrategy.INVERTED_PORT_PROCESSOR);
    
    /** additional processor dependencies for graphs with northern / southern non-free ports. */
    private static final IntermediateProcessingConfiguration NORTH_SOUTH_PORT_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase3(IntermediateProcessorStrategy.NORTH_SOUTH_PORT_PREPROCESSOR)
            .addAfterPhase5(IntermediateProcessorStrategy.NORTH_SOUTH_PORT_POSTPROCESSOR);

    /** additional processor dependencies for graphs with head or tail edge labels. */
    private static final IntermediateProcessingConfiguration END_EDGE_LABEL_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase4(IntermediateProcessorStrategy.LABEL_SIDE_SELECTOR)
            .addAfterPhase5(IntermediateProcessorStrategy.END_LABEL_PROCESSOR);
    
    //////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Spline edge routing", 1);
        // Retrieve some generic values
        final double hNodeSpacing = layeredGraph.getProperty(LayeredOptions.SPACING_NODE_NODE_BETWEEN_LAYERS);
        hEdgeSpacing = layeredGraph.getProperty(LayeredOptions.SPACING_EDGE_EDGE_BETWEEN_LAYERS);
        
        // Find out if splines should be routed thoroughly or sloppy
        final boolean sloppyRouting = layeredGraph.getProperty(LayeredOptions.EDGE_ROUTING_SLOPPY_SPLINE_ROUTING);
        final double sloppyLayerSpacingFactor = layeredGraph.getProperty(
                LayeredOptions.EDGE_ROUTING_SLOPPY_SPLINE_LAYER_SPACING);

        double xpos = 0.0;

        final Iterator<Layer> layerIterator = layeredGraph.iterator();
        Layer leftLayer = null;
        Layer rightLayer;

        // A mapping pointing from an edge to it's succeeding edge, together with their connected 
        // friends, they form a long-edge.
        final Map<LEdge, LEdge> successingEdge = Maps.newHashMap();
        
        // a collection of all edges that have a normal node as their source
        final List<LEdge> startEdges = Lists.newArrayList();

        boolean externalLeftLayer = true;
        boolean externalRightLayer = true;
        
        do {
            rightLayer = layerIterator.hasNext() ? layerIterator.next() : null;

            /////////////////////////////////////
            // Creation of the SplineHyperEdges//
            // some variables we need
            final List<SplineHyperEdge> hyperEdges = Lists.newArrayList();
            final List<LEdge> edgesRemaining = Lists.newArrayList();
            final Set<LPort> leftPorts = Sets.newLinkedHashSet();
            final Set<LPort> rightPorts = Sets.newLinkedHashSet();
            final Set<LEdge> selfLoops = Sets.newLinkedHashSet();
            
            // fill edgesRemaining and PortToEdgesPairs
            fillMappings(Pair.of(leftLayer, rightLayer), 
                        Pair.of(leftPorts, rightPorts), 
                        edgesRemaining, successingEdge, startEdges, selfLoops);
            
            // create the hyperEdges having their start port on the left side.
            createHyperEdges(leftPorts, rightPorts, SideToProcess.LEFT, 
                    true, edgesRemaining, hyperEdges);
            createHyperEdges(leftPorts, rightPorts, SideToProcess.LEFT, 
                    false, edgesRemaining, hyperEdges);

            // create the hyperEdges having their start port on the right side.
            createHyperEdges(leftPorts, rightPorts, SideToProcess.RIGHT, 
                    true, edgesRemaining, hyperEdges);
            createHyperEdges(leftPorts, rightPorts, SideToProcess.RIGHT, 
                    false, edgesRemaining, hyperEdges);

            // remaining edges are single edges that cannot be combined with others to a hyper-edge
            createHyperEdges(edgesRemaining, leftPorts, rightPorts, hyperEdges);
            
            ////////////////////////////////////
            // Creation of the dependencies of the SplineHyperEdges
            final ListIterator<SplineHyperEdge> sourceIter = hyperEdges.listIterator();
            while (sourceIter.hasNext()) {
                final SplineHyperEdge hyperEdge1 = sourceIter.next();
                final ListIterator<SplineHyperEdge> targetIter = 
                        hyperEdges.listIterator(sourceIter.nextIndex());
                while (targetIter.hasNext()) {
                    final SplineHyperEdge hyperEdge2 = targetIter.next();
                    createDependency(hyperEdge1, hyperEdge2);
                }
            }

            ////////////////////////////////////
            // Apply the topological numbering//
            // break cycles
            breakCycles(hyperEdges, layeredGraph.getProperty(InternalProperties.RANDOM));
            
            // assign ranks to the hyper-nodes
            topologicalNumbering(hyperEdges);

            ////////////////////////////////////
            // Place right layers's nodes. This needs to be done before calculating the bendPoints.
            double rightLayersPosition = xpos;
            if (rightLayer != null) {
                externalRightLayer =
                        Iterables.all(rightLayer.getNodes(), PolylineEdgeRouter.PRED_EXTERNAL_WEST_OR_EAST_PORT);

                if (sloppyRouting) {
                    // For sloppy routing we limit the angle of vertical segments similar to the polyline
                    // edge router.
                    // While routing edges, we remember the maximum vertical span of any edge between this and
                    // the next layer to insert enough space between the layers to keep the edge slopes from
                    // becoming too steep
                    double maxVertDiff = 0.0;

                    // Iterate over the layer's nodes
                    for (LNode node : rightLayer) {
                        // Calculate the maximal vertical span of output edges.
                        double maxCurrInputYDiff = 0.0;
                        for (LEdge incomingEdge : node.getIncomingEdges()) {
                            double sourcePos = incomingEdge.getSource().getAbsoluteAnchor().y;
                            double targetPos = incomingEdge.getTarget().getAbsoluteAnchor().y;

                            maxCurrInputYDiff = Math.max(maxCurrInputYDiff, Math.abs(targetPos - sourcePos));
                        }
                        maxVertDiff = Math.max(maxVertDiff, maxCurrInputYDiff);
                    }

                    // Determine where next layer should start based on the maximal vertical span of edges
                    // between the two layers
                    double layerSpacing = sloppyLayerSpacingFactor * (hEdgeSpacing / hNodeSpacing) * maxVertDiff;

                    // At least have minimal node spacing if all edges are straight
                    if (layerSpacing < hNodeSpacing && !externalLeftLayer && !externalRightLayer) {
                        layerSpacing = hNodeSpacing;
                    }

                    rightLayersPosition += layerSpacing;
                } else {
                    // For thorough edge routing we reserve horizontal space similar to orthogonal routing.
                    rightLayersPosition += NODE_TO_VERTICAL_SEGMENT_GAP;
                    int maxRank = -1;
                    for (final SplineHyperEdge edge : hyperEdges) {
                        maxRank = Math.max(maxRank, edge.rank);
                    }
                    maxRank++;

                    if (maxRank > 0) {
                        // The space between each pair of edge segments, and between nodes and edges
                        double increment = (maxRank + 1) * hEdgeSpacing;

                        // If we are between two layers, make sure their minimal spacing is preserved
                        if (increment < hNodeSpacing && !externalLeftLayer && !externalRightLayer) {
                            increment = hNodeSpacing;
                        }
                        rightLayersPosition += increment;
                    } else if (!(externalLeftLayer || externalRightLayer || layerOnlyContainsDummies(leftLayer)
                            || layerOnlyContainsDummies(rightLayer))) {
                        // If all edges are straight, use the usual spacing
                        // (except when we are between two layers where both only contains dummy nodes)
                        rightLayersPosition += hNodeSpacing;
                    }
                }
                
                LGraphUtil.placeNodesHorizontally(rightLayer, rightLayersPosition);
            }
            
            ////////////////////////////////////
            // Self loops are already calculated. All we have to do is add the node offset
            // to the bend points and the edge labels.
            for (final LEdge selfLoop : selfLoops) {
                final KVector offset = selfLoop.getSource().getNode().getPosition();

                selfLoop.getBendPoints().offset(offset);
                
                for (final LLabel label : selfLoop.getLabels()) {
                    label.getPosition().add(offset);
                }
            }

            ////////////////////////////////////
            // Calculate the NubSpline control points.
            for (final SplineHyperEdge edge : hyperEdges) {
                // SplineHyperEdges that are just straight lines don't need any control-points
                if (edge.isStraight) {
                    calculateNUBSBendPointStraight(edge, xpos);
                } else {
                    calculateNUBSBendPoints(edge, xpos, rightLayersPosition, sloppyRouting);
                }
            }

            ////////////////////////////////////
            // proceed to next layer
            if (rightLayer != null) {
                xpos = rightLayersPosition + rightLayer.getSize().x;
                if (!sloppyRouting) {
                    xpos += NODE_TO_VERTICAL_SEGMENT_GAP;
                }
            } else {
                // When handling the last layer
                // add spacing for the last routing area after the node
                // Calculation taken from inter layer spacing calculation
                int maxRank = -1;
                for (final SplineHyperEdge edge : hyperEdges) {
                    maxRank = Math.max(maxRank, edge.rank);
                }
                if (maxRank >= 0) {
                    xpos += (maxRank + 2) * hEdgeSpacing;
                }
            }
            leftLayer = rightLayer;
            externalLeftLayer = externalRightLayer;

        } while (rightLayer != null);
        
        ////////////////////////////////////
        // all layers are processed, now we can calculate the bezier bend-points for all edges
        for (final LEdge edge : startEdges) {
            calculateBezierBendPoints(edge, successingEdge, sloppyRouting);
        }
        
        layeredGraph.getSize().x = xpos;
        monitor.done();
    }

    /**
     * {@inheritDoc}
     */
    public IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(
            final LGraph graph) {
        // Basic configuration
        final IntermediateProcessingConfiguration configuration =
                IntermediateProcessingConfiguration.createEmpty();

        final Set<GraphProperties> graphProperties =
                graph.getProperty(InternalProperties.GRAPH_PROPERTIES);

        if (graphProperties.contains(GraphProperties.SELF_LOOPS)) {
            configuration.addAll(SELF_LOOP_PROCESSING_ADDITIONS);
        }

        if (graphProperties.contains(GraphProperties.CENTER_LABELS)) {
            configuration.addAll(CENTER_EDGE_LABEL_PROCESSING_ADDITIONS);
        }

        if (graphProperties.contains(GraphProperties.NON_FREE_PORTS)
                || graph.getProperty(LayeredOptions.FEEDBACK_EDGES)) {
            
            configuration.addAll(INVERTED_PORT_PROCESSING_ADDITIONS);

            if (graphProperties.contains(GraphProperties.NORTH_SOUTH_PORTS)) {
                configuration.addAll(NORTH_SOUTH_PORT_PROCESSING_ADDITIONS);
            }
        }

        if (graphProperties.contains(GraphProperties.END_LABELS)) {
            configuration.addAll(END_EDGE_LABEL_PROCESSING_ADDITIONS);
        }
        return configuration;
    }

    /**
     * Checks if the two Y coordinates are resulting in a straight edge.
     * @param firstY First Y coordinate.
     * @param secondY Second Y coordinate.
     * @return True, if the two Y coordinates result in a straight edge. 
     */
    private static boolean isStraight(final double firstY, final double secondY) {
        return Math.abs(firstY - secondY) < MAX_VERTICAL_DIFF_FOR_STRAIGHT;
    }
    
    /**
     * Initially fills the mappings, collection and sets: allEdges, preceedingEdge, endingEdges
     * and selfLoops. leftRightLayer and leftRightPorts are pairs to prevent a "more than seven
     * parameters" message.
     * 
     * @param leftRightLayer A pair of the current left and right Layer.
     * @param leftRightPorts A pair of current ports on the left and right layer involved in current
     *          iteration.
     * @param allEdges A list that will hold all edges.
     * @param succeedingEdge A mapping from each edge to its successor edge.
     * @param startingEdges A list of all edges that are not successor of another edge.
     * @param selfLoops A set of all selfLoops starting in one of the given ports.
     */
    private void fillMappings(
            final Pair<Layer, Layer> leftRightLayer,
            final Pair<Set<LPort>, Set<LPort>> leftRightPorts,
            final List<LEdge> allEdges, 
            final Map<LEdge, LEdge> succeedingEdge, 
            final List<LEdge> startingEdges,
            final Set<LEdge> selfLoops) {
        
        final Layer leftLayer = leftRightLayer.getFirst();
        final Layer rightLayer = leftRightLayer.getSecond();
        final Set<LPort> leftPorts = leftRightPorts.getFirst();
        final Set<LPort> rightPorts = leftRightPorts.getSecond();

        // iterate over all outgoing edges on the left layer.
        if (leftLayer != null) {
            for (final LNode node : leftLayer.getNodes()) { 
                for (final LPort sourcePort : node.getPorts(PortSide.EAST)) {
                    leftPorts.add(sourcePort);
                    
                    for (final LEdge edge : sourcePort.getOutgoingEdges()) {
                        // Self-loops are handled in the right-layer section below.
                        if (edge.isSelfLoop()) {
                            continue;
                        }
                        
                        // Add edge to set of all edges and find it's successor
                        allEdges.add(edge);
                        findAndAddSuccessor(edge, succeedingEdge);

                        // Check if edge is a startingEdge
                        if (isQualifiedAsStartingNode(edge.getSource().getNode())) {
                            startingEdges.add(edge);
                        }
                        
                        // Check port-side of target port
                        final LPort targetPort = edge.getTarget();
                        final Layer targetLayer = targetPort.getNode().getLayer();
                        if (targetLayer.equals(rightLayer)) {
                            rightPorts.add(targetPort);
                        } else if (targetLayer.equals(leftLayer)) {
                            leftPorts.add(targetPort);
                        } else {
                            // Unhandled situation. Probably there are incoming and outgoing edges on
                            // the same port. This is not supported.
                            allEdges.remove(edge);
                        }
                    }
                }
            }
        }
        
        if (rightLayer != null) {
            for (final LNode node : rightLayer.getNodes()) {
                // handle all self-loops, no matter on witch port-side they are.
                for (final LPort port : node.getPorts()) {
                    for (final LEdge edge : port.getOutgoingEdges()) {
                        if (edge.isSelfLoop()) {
                            selfLoops.add(edge);
                        }
                    }
                }

                // iterate over all outgoing edges on the right layer
                for (final LPort sourcePort : node.getPorts(PortSide.WEST)) {
                    rightPorts.add(sourcePort);
                    
                    for (final LEdge edge : sourcePort.getOutgoingEdges()) {
                        // self-loops have been handled before
                        if (edge.isSelfLoop()) {
                            continue;
                        }
                        
                        // Add edge to set of all edges and find it's successor
                        allEdges.add(edge);
                        findAndAddSuccessor(edge, succeedingEdge);

                        // Check if edge is a startingEdge
                        if (isQualifiedAsStartingNode(edge.getSource().getNode())) {
                            startingEdges.add(edge);
                        }
                        
                        // Check port-side of target port
                        final LPort targetPort = edge.getTarget();
                        final Layer targetLayer = targetPort.getNode().getLayer();
                        if (targetLayer.equals(rightLayer)) {
                            rightPorts.add(targetPort);
                        } else if (targetLayer.equals(leftLayer)) {
                            leftPorts.add(targetPort);
                        } else {
                            // Unhandled situation. Probably there are incoming and outgoing edges on
                            // the same port. This is not supported.
                            allEdges.remove(edge);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Finds the predecessor {@link LEdge} of given edge. It is assumed that the source node of an
     * edge with a predecessor only has one incoming edge. Otherwise the first incoming edge of the
     * source node is added as the predecessor.  
     * 
     * @param edge The {@link LEdge} those predecessor to find.
     * @param succeedingEdge A mapping to add the link to.
     */
    private void findAndAddSuccessor(final LEdge edge, final Map<LEdge, LEdge> succeedingEdge) {
        final LNode targetNode = edge.getTarget().getNode();
        
        // if target node is a normal node there is no successor
        if (isNormalNode(targetNode)) {
            return;
        }
        
        // otherwise take the first outgoing edge of target node
        final Iterator<LEdge> iter = targetNode.getOutgoingEdges().iterator();
        if (iter.hasNext()) {
            succeedingEdge.put(edge, iter.next());
        }
    }
    
    /**
     * Creates a "one-edge" hyper-edge for each edges in the collection. The hyper-edges are added to 
     * the hyperEdges collection.
     * 
     * @param edges The edges to process.
     * @param leftPorts The left ports of the current situation.
     * @param rightPorts The right ports of the current situation.
     * @param hyperEdges The new hyper-edges will be added to this collection.
     */
    private void createHyperEdges(
            final List<LEdge> edges,
            final Set<LPort> leftPorts,
            final Set<LPort> rightPorts,
            final List<SplineHyperEdge> hyperEdges) {
        
        for (final LEdge edge : edges) {
            final LPort sourcePort = edge.getSource();
            SideToProcess sourceSide;
            
            if (leftPorts.contains(sourcePort)) {
                sourceSide = SideToProcess.LEFT;
            } else if (rightPorts.contains(sourcePort)) {
                sourceSide = SideToProcess.RIGHT;
            } else {
                throw new IllegalArgumentException("Source port must be in one of the port sets.");
            }

            SideToProcess targetSide;
            final LPort targetPort = edge.getTarget();

            if (leftPorts.contains(targetPort)) {
                targetSide = SideToProcess.LEFT;
            } else if (rightPorts.contains(targetPort)) {
                targetSide = SideToProcess.RIGHT;
            } else {
                throw new IllegalArgumentException("Target port must be in one of the port sets.");
            }

            hyperEdges.add(new SplineHyperEdge(edge, sourceSide, targetSide));
        }
    }

    /**
     * Creates hyperEdges. The created hyperEdges all have one port on their source side (if reversed
     * is {@code false}) or on their target side (if reversed is {@code true}). Also only hyperEdges 
     * starting in one of the leftPorts (if sideToProcess is {@code LEFT}) or one of the right ports (if 
     * SideToProcess is {@code RIGHT}) will be created.
     * 
     * @param leftPorts The ports on the left side of current between-layer segment.
     * @param rightPorts The ports on the right side of current between-layer segment.
     * @param sideToProcess Either {@code LEFT} or {@code RIGHT}. 
     * @param reversed {@code true}, if hyperEdges for reversed edges shall be created.
     * @param edgesRemaining Only hyperEdges pointing to a set of ports in this collection will be 
     *          created. 
     * @param hyperEdges The collection of hyperEdges that the created edges will be added to.
     */
    private void createHyperEdges(
            final Set<LPort> leftPorts, 
            final Set<LPort> rightPorts, 
            final SideToProcess sideToProcess,
            final boolean reversed,
            final List<LEdge> edgesRemaining,
            final List<SplineHyperEdge> hyperEdges) {

        Set<LPort> portsToProcess = null;
        if (sideToProcess == SideToProcess.LEFT) {
            portsToProcess = leftPorts;  
        } else if (sideToProcess == SideToProcess.RIGHT) {
            portsToProcess = rightPorts;
        } else {
            assert false : "sideToProcess must be either LEFT or RIGHT.";
        }
        
        // Iterate through all ports on the side to process.
        for (final LPort singlePort : portsToProcess) {
            final double singlePortPosition = singlePort.getAbsoluteAnchor().y;
            final Set<Pair<SideToProcess, LEdge>> upEdges = Sets.newHashSet();
            final Set<Pair<SideToProcess, LEdge>> downEdges = Sets.newHashSet();
            
            // Find edges we could construct a hyper-edge from. If the edge is in the 
            // edgesRemaining set, there is no hyper-edge that represents this edge. 
            for (final LEdge edge : singlePort.getConnectedEdges()) {
                if (edge.getProperty(InternalProperties.REVERSED) != reversed) {
                    continue;
                }
                if (edgesRemaining.contains(edge)) {
                    // find the target port
                    LPort targetPort;
                    if (edge.getTarget() == singlePort) {
                        targetPort = edge.getSource();
                    } else {
                        targetPort = edge.getTarget();
                    }
                    
                    // check if this edge should get drawn as a straight edge
                    final double targetPortPosition = targetPort.getAbsoluteAnchor().y;
                    if (isStraight(targetPortPosition, singlePortPosition)) {
                        continue;
                    }

                    // add the edge to the correct set of up/down-edges 
                    if (targetPortPosition < singlePortPosition) {
                        if (leftPorts.contains(targetPort)) {
                            upEdges.add(Pair.of(SideToProcess.LEFT, edge));
                        } else {
                            upEdges.add(Pair.of(SideToProcess.RIGHT, edge));
                        }
                    } else {
                        if (leftPorts.contains(targetPort)) {
                            downEdges.add(Pair.of(SideToProcess.LEFT, edge));
                        } else {
                            downEdges.add(Pair.of(SideToProcess.RIGHT, edge));
                        }
                    }
                }
            }

            // Create some hyper edges. 
            // We are creating only hyper-edges that have more than one real edge.  
            if (upEdges.size() > 1) {
                hyperEdges.add(new SplineHyperEdge(singlePort, upEdges, sideToProcess));
                for (final Pair<SideToProcess, LEdge> pair : upEdges) {
                    edgesRemaining.remove(pair.getSecond());
                }
            }
            if (downEdges.size() > 1) {
                hyperEdges.add(new SplineHyperEdge(singlePort, downEdges, sideToProcess));
                for (final Pair<SideToProcess, LEdge> pair : downEdges) {
                    edgesRemaining.remove(pair.getSecond());
                }
            }
        }
    }
    
    /**
     * Calculate the "must lay left of" dependency for two SplineHyperEdges.
     * @param edge0 First hyper-edge to compare.
     * @param edge1 Second hyper-edge to compare.
     */
    private void createDependency(final SplineHyperEdge edge0, final SplineHyperEdge edge1) {
        if (edge0.topYPos > edge1.bottomYPos 
                || edge1.topYPos > edge0.bottomYPos) {
            // the two hyper-edges do not share a vertical segment
            return;
        }
        int edge0Counter = 0;
        int edge1Counter = 0;
        
        for (final LPort port : edge0.rightPorts) {
            if (SplinesMath.isBetween(port.getAbsoluteAnchor().y, 
                    edge1.topYPos, edge1.bottomYPos)) {
                edge0Counter++;
            }
        }
        for (final LPort port : edge0.leftPorts) {
            if (SplinesMath.isBetween(port.getAbsoluteAnchor().y, 
                    edge1.topYPos, edge1.bottomYPos)) {
                edge0Counter--;
            }
        }
        for (final LPort port : edge1.rightPorts) {
            if (SplinesMath.isBetween(port.getAbsoluteAnchor().y, 
                    edge0.topYPos, edge0.bottomYPos)) {
                edge1Counter++;
            }
        }
        for (final LPort port : edge1.leftPorts) {
            if (SplinesMath.isBetween(port.getAbsoluteAnchor().y, 
                    edge0.topYPos, edge0.bottomYPos)) {
                edge1Counter--;
            }
        }
        
        if (edge0Counter < edge1Counter) {
            // edge0 should lay left of edge1
            new Dependency(edge0, edge1, edge1Counter - edge0Counter);
        } else if (edge1Counter < edge0Counter) {
            // edge0 should lay right of edge1
            new Dependency(edge1, edge0, edge0Counter - edge1Counter);
        } else {
            // in either ordering there would be the same number of crossings
            new Dependency(edge1, edge0, 0);
            new Dependency(edge0, edge1, 0);
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////
    // Cycle Breaking
    
    /**
     * Breaks all cycles in the given hypernode structure by reversing or removing
     * some dependencies. This implementation assumes that the dependencies of zero
     * weight are exactly the two-cycles of the hypernode structure.
     * 
     * @param edges list of hypernodes
     * @param random random number generator
     */
    private static void breakCycles(final List<SplineHyperEdge> edges, final Random random) {
        final LinkedList<SplineHyperEdge> sources = Lists.newLinkedList();
        final LinkedList<SplineHyperEdge> sinks = Lists.newLinkedList();
        
        // initialize values for the algorithm
        int nextMark = -1;
        for (final SplineHyperEdge edge : edges) {
            edge.mark = nextMark--;
            int inweight = 0;
            int outweight = 0;
            
            for (final Dependency dependency : edge.outgoing) {
                outweight += dependency.weight;
            }
            
            for (final Dependency dependency : edge.incoming) {
                inweight += dependency.weight;
            }
            
            edge.inweight = inweight;
            edge.outweight = outweight;
            
            if (outweight == 0) {
                sinks.add(edge);
            } else if (inweight == 0) {
                sources.add(edge);
            }
        }
    
        // assign marks to all nodes, ignore dependencies of weight zero
        final Set<SplineHyperEdge> unprocessed = Sets.newLinkedHashSet(edges);
        final int markBase = edges.size();
        int nextLeft = markBase + 1;
        int nextRight = markBase - 1;
        final List<SplineHyperEdge> maxEdges = Lists.newArrayList();

        while (!unprocessed.isEmpty()) {
            while (!sinks.isEmpty()) {
                final SplineHyperEdge sink = sinks.removeFirst();
                unprocessed.remove(sink);
                sink.mark = nextRight--;
                updateNeighbors(sink, sources, sinks);
            }
            
            while (!sources.isEmpty()) {
                final SplineHyperEdge source = sources.removeFirst();
                unprocessed.remove(source);
                source.mark = nextLeft++;
                updateNeighbors(source, sources, sinks);
            }
            
            int maxOutflow = Integer.MIN_VALUE;
            for (final SplineHyperEdge edge : unprocessed) {
                final int outflow = edge.outweight - edge.inweight;
                if (outflow >= maxOutflow) {
                    if (outflow > maxOutflow) {
                        maxEdges.clear();
                        maxOutflow = outflow;
                    }
                    maxEdges.add(edge);
                }
            }
            
            if (!maxEdges.isEmpty()) {
                // if there are multiple SplineHyperEdges with maximal outflow, select one randomly
                final SplineHyperEdge maxEdge = maxEdges.get(random.nextInt(maxEdges.size()));
                unprocessed.remove(maxEdge);
                maxEdge.mark = nextLeft++;
                updateNeighbors(maxEdge, sources, sinks);
                maxEdges.clear();
            }
        }
    
        // shift ranks that are left of the mark base
        final int shiftBase = edges.size() + 1;
        for (final SplineHyperEdge edge : edges) {
            if (edge.mark < markBase) {
                edge.mark += shiftBase;
            }
        }
    
        // process edges that point left: remove those of zero weight, reverse the others
        for (final SplineHyperEdge source : edges) {
            final ListIterator<Dependency> depIter = source.outgoing.listIterator();
            while (depIter.hasNext()) {
                final Dependency dependency = depIter.next();
                final SplineHyperEdge target = dependency.target;
                
                if (source.mark > target.mark) {
                    depIter.remove();
                    target.incoming.remove(dependency);
                    
                    if (dependency.weight > 0) {
                        dependency.source = target;
                        target.outgoing.add(dependency);
                        dependency.target = source;
                        source.incoming.add(dependency);
                    }
                }
            }
        }
    }
    
    /**
     * Updates in-weight and out-weight values of the neighbors of the given node,
     * simulating its removal from the graph. The sources and sinks lists are
     * also updated.
     * 
     * @param edge node for which neighbors are updated
     * @param sources list of sources
     * @param sinks list of sinks
     */
    private static void updateNeighbors(final SplineHyperEdge edge, 
            final List<SplineHyperEdge> sources,
            final List<SplineHyperEdge> sinks) {
        // process following edges
        for (final Dependency dep : edge.outgoing) {
            if (dep.target.mark < 0 && dep.weight > 0) {
                dep.target.inweight -= dep.weight;
                if (dep.target.inweight <= 0 && dep.target.outweight > 0) {
                    sources.add(dep.target);
                }
            }
        }
        
        // process preceding edges
        for (final Dependency dep : edge.incoming) {
            if (dep.source.mark < 0 && dep.weight > 0) {
                dep.source.outweight -= dep.weight;
                if (dep.source.outweight <= 0 && dep.source.inweight > 0) {
                    sinks.add(dep.source);
                }
            }
        }
    }
        
    ///////////////////////////////////////////////////////////////////////////////
    // Topological Ordering
    
    /**
     * Perform a topological numbering of the given SplineHyperEdges.
     * 
     * @param edges list of SplineHyperEdge
     */
    private static void topologicalNumbering(final List<SplineHyperEdge> edges) {
        // determine sources, targets, incoming count and outgoing count; targets are only
        // added to the list if they only connect westward ports (that is, if all their
        // horizontal segments point to the right)
        final List<SplineHyperEdge> sources = Lists.newLinkedList();
        final List<SplineHyperEdge> rightwardTargets = Lists.newLinkedList();
        for (final SplineHyperEdge edge : edges) {
            edge.inweight = edge.incoming.size();
            edge.outweight = edge.outgoing.size();
            
            if (edge.inweight == 0) {
                sources.add(edge);
            }
            
            if (edge.outweight == 0 && edge.leftPorts.isEmpty()) {
                rightwardTargets.add(edge);
            }
        }
        
        int maxRank = -1;
        
        // assign ranks using topological numbering
        while (!sources.isEmpty()) {
            final SplineHyperEdge edge = sources.remove(0);
            for (final Dependency dep : edge.outgoing) {
                SplineHyperEdge target = dep.target;
                target.rank = Math.max(target.rank, edge.rank + 1);
                maxRank = Math.max(maxRank, target.rank);
                
                target.inweight--;
                if (target.inweight == 0) {
                    sources.add(target);
                }
            }
        }
        
        /* If we stopped here, hyper nodes that don't have any horizontal segments pointing
         * leftward would be ranked just like every other hyper node. This would move back
         * edges too far away from their target node. To remedy that, we move all hyper nodes
         * with horizontal segments only pointing rightwards as far right as possible.
         */
        if (maxRank > -1) {
            // assign all target nodes with horzizontal segments pointing to the right the
            // rightmost rank
            for (final SplineHyperEdge edge : rightwardTargets) {
                edge.rank = maxRank;
            }
            
            // let all other segments with horizontal segments pointing rightwards move as
            // far right as possible
            while (!rightwardTargets.isEmpty()) {
                final SplineHyperEdge edge = rightwardTargets.remove(0);
                
                // The node only has connections to western ports
                for (final Dependency dep : edge.incoming) {
                    SplineHyperEdge source = dep.source;
                    if (!source.leftPorts.isEmpty()) {
                        continue;
                    }
                    
                    source.rank = Math.min(source.rank, edge.rank - 1);
                    
                    source.outweight--;
                    if (source.outweight == 0) {
                        rightwardTargets.add(source);
                    }
                }
            }
        }
    }

    /**
     * Collects all bend-points defined for any edge in a chain of edges and converts them to bezier
     * bend-points.
     *   
     * @param edge The last edge of the chain of edges we want the bezier CPs to be calculated for. 
     * @param succeedingEdge A mapping pointing from an edge to it's successor.
     */
    private void calculateBezierBendPoints(final LEdge edge, final Map<LEdge, LEdge> succeedingEdge,
            final boolean sloppyRouting) {
        // in this chain we will put all NURBS control points.
        final KVectorChain allCP = new KVectorChain();
        // We will temporarily store north- or south-bendpoints here.
        KVector northSouthBendPoint = null;
        
        ///////////////////////////////////////
        // Process the source end of the edge-chain. 
        LPort sourcePort = edge.getSource();
        final NodeType sourceNodeType = sourcePort.getNode().getType();
        
        // edge must be the first edge of a chain of edges
        if (!isQualifiedAsStartingNode(sourcePort.getNode())) {
            throw new IllegalArgumentException(
                    "The target node of the edge must be a normal node " + "or a northSouthPort.");
        }
        
        // Calculate the NubSpline bend-point for a north or south port and reroute the edge.
        if (sourceNodeType == NodeType.NORTH_SOUTH_PORT) {
            final LPort originPort = (LPort) sourcePort.getProperty(InternalProperties.ORIGIN);
            northSouthBendPoint = new KVector(
                    originPort.getAbsoluteAnchor().x, 
                    sourcePort.getAbsoluteAnchor().y);
            sourcePort = originPort;
        }

        // add the source as the very first CP.
        allCP.addLast(sourcePort.getAbsoluteAnchor());

        double gap;
        KVector offsetOfStraightening;
        if (!sloppyRouting) {
            // Add a control-point for a straight segment at the very start of an edge-chain to prevent
            // the edge from colliding with self-loops or the like inside the margin of the node. This also
            // ensures the correct initial direction of the edge.
            // This is only performed if the splines should be thoroughly routed.
            gap = Math.max(NODE_TO_STRAIGHTENING_CP_GAP, 
                    SplinesMath.getMarginOnPortSide(sourcePort.getNode(), sourcePort.getSide()));
            offsetOfStraightening = 
                    new KVector(SplinesMath.portSideToDirection(sourcePort.getSide()));
            offsetOfStraightening.scale(gap);
            allCP.add(offsetOfStraightening.add(sourcePort.getAbsoluteAnchor()));
        }
        // Add the calculated north/south port bend-point, if there is one.
        if (northSouthBendPoint != null) {
            allCP.addLast(northSouthBendPoint);
            northSouthBendPoint = null;
        }
        
        ///////////////////////////////////////
        // Process the inner segments.
        LEdge currentEdge = edge;
        LEdge lastEdge = edge;
        KVector lastCP = null;
        boolean addMidPoint = false;
        
        while (currentEdge != null) {
            // read the stored bend-points for vertical segments, calculated by calculateNUBSBendPoint.
            final KVectorChain currentBendPoints = currentEdge.getBendPoints();

            if (!currentBendPoints.isEmpty()) {
                // add a CP in the middle of the straight segment between two vertical segments to
                // get a more straight horizontal segment
                if (addMidPoint) {
                    allCP.add(lastCP.add(currentBendPoints.getFirst()).scale(ONE_HALF));
                    addMidPoint = false;
                } else {
                    addMidPoint = true;
                }
                lastCP = currentBendPoints.getLast().clone();
                allCP.addAll(currentBendPoints);
                currentBendPoints.clear();
            }
            lastEdge = currentEdge;
            currentEdge = succeedingEdge.get(currentEdge);
        }
        

        ///////////////////////////////////////
        // Process the end of the chain of edges.
        LPort targetPort = lastEdge.getTarget();
        
        // Calculate and add a NubSpline bend-point for a north or south port and reroute the edge.
        if (targetPort.getNode().getType() == NodeType.NORTH_SOUTH_PORT) {
            final LPort originPort = (LPort) targetPort.getProperty(InternalProperties.ORIGIN);
            allCP.add(new KVector(
                    originPort.getAbsoluteAnchor().x, 
                    targetPort.getAbsoluteAnchor().y));
            targetPort = originPort;
        }
        
        if (!sloppyRouting) {
            // Add a control-point for a straight segment at the very start of an edge-chain to prevent
            // the edge from colliding with self-loops or the like inside the margin of the node. This also
            // ensures the correct final direction of the edge.
            // This is only performed if the splines should be thoroughly routed.
            gap = Math.max(NODE_TO_STRAIGHTENING_CP_GAP,
                    SplinesMath.getMarginOnPortSide(targetPort.getNode(), targetPort.getSide()));
            offsetOfStraightening = new KVector(SplinesMath.portSideToDirection(targetPort.getSide()));
            offsetOfStraightening.scale(gap);
            allCP.add(offsetOfStraightening.add(targetPort.getAbsoluteAnchor()));
        }
        
        // Add the targetPort as a NubSpline bend-point.
        allCP.addLast(targetPort.getAbsoluteAnchor());

        ///////////////////////////////////////
        // convert list of control points to bezier bend points.
        
        // create the NubSpline for the control-points
        final NubSpline nubSpline = new NubSpline(true, DIMENSION, allCP);
        
        // Calculate the bezier CP and set them as the bend-points (without source and target vector). 
        edge.getBendPoints().addAll(nubSpline.getBezierCP());
    }
        
    /**
     * Adds a single bendPoint to a straight edge.
     * 
     * @param hyperEdge The hyper edge, those edges shall be processed.
     * @param startPos The start x position of current between layer gap.
     */
    private void calculateNUBSBendPointStraight(final SplineHyperEdge hyperEdge, final double startPos) {
        final Set<LEdge> edges = hyperEdge.edges;
        if (edges.size() > 1) {
            throw new IllegalArgumentException("In straight hyperEdges there may be only one edge.");
        }

        edges.iterator().next().getBendPoints().add(new KVector(startPos, hyperEdge.centerYPos));
    }
    
    /**
     * Calculates the bend-points of the edges of given {@link SplineHyperEdge}.
     * If the target node of an {@link LEdge} that is part of the hyperEdge is NOT 
     * {@link NodeType#NORMAL}, NubSpline control-points are added to the edge, 
     * as it still has successors. 
     * If finally the end node is reached, the mapping preceedingEdge is used to find the whole edge 
     * through the graph. All NubSpline control-points are taken from the edges and the final bezier 
     * bend-points are calculated and added the the last edge part.
     * 
     * @param hyperEdge
     *            The hyper edge, those edges shall be processed.
     * @param startXPos
     *            The start x position of current between layer gap.
     * @param endXPos
     *            The end x position of current between layer gap.
     */
    private void calculateNUBSBendPoints(final SplineHyperEdge hyperEdge, final double startXPos,
            final double endXPos, final boolean sloppyRouting) {
        // the center position is the same for all edges but depends on sloppiness of the routing
        final double centerXPos = sloppyRouting ? (startXPos + endXPos) / 2
                : startXPos + (hyperEdge.rank + 1) * hEdgeSpacing;
       
        for (final LEdge edge : hyperEdge.edges) {
            final KVector targetAnchor = edge.getTarget().getAbsoluteAnchor();
            final KVector sourceAnchor = edge.getSource().getAbsoluteAnchor();
            
            final KVector sourceVerticalCP = 
                    new KVector(centerXPos, sourceAnchor.y);
            final KVector targetVerticalCP = 
                    new KVector(centerXPos, targetAnchor.y);

            // Calculate bend points to draw inner layer segments straight
            // to prevent intersections with big nodes
            final KVector sourceStraightCP =
                    new KVector(startXPos - NODE_TO_VERTICAL_SEGMENT_GAP, sourceAnchor.y);
            final KVector targetStraightCP = new KVector(endXPos, targetAnchor.y);

            boolean invertedSource = false;
            boolean invertedTarget = false;
            // Modify straight CPs to handle inverted edges.
            if (targetAnchor.x >= endXPos && sourceAnchor.x >= endXPos) {
                // Inner layer connection on the right layer
                invertedSource = true;
                sourceStraightCP.x = endXPos;
            }
            if (targetAnchor.x <= startXPos && sourceAnchor.x <= startXPos) {
                // Inner layer connection on the right layer
                invertedTarget = true;
                targetStraightCP.x = startXPos - NODE_TO_VERTICAL_SEGMENT_GAP;
            }

            // If using sloppy routing, only some bendpoints are needed at the start and end
            // First determine if this part of an edge is connected to a normal node
            final boolean normalSource = isNormalNode(edge.getSource().getNode());
            final boolean normalTarget = isNormalNode(edge.getTarget().getNode());
            
            if (!sloppyRouting || !(normalSource || normalTarget) || invertedTarget || invertedSource) {
                // add the NubSpline control points to the edge, but in revered order!
                if (hyperEdge.edges.size() == 1) {
                    // Special handling of single edges. They don't need a center CP.
                    edge.getBendPoints().addAll(sourceStraightCP, sourceVerticalCP, targetVerticalCP, 
                            targetStraightCP);
                } else {
                    final KVector center = new KVector(centerXPos, hyperEdge.centerYPos);                    
                    edge.getBendPoints().addAll(sourceStraightCP, sourceVerticalCP, center, 
                            targetVerticalCP, targetStraightCP);
                }
            } else {
                if (normalSource && normalTarget) {
                    // If using sloppy routing we want to use a central y point which is just halfway
                    // between the nodes. We add a small offset to create a curved line between the nodes.
                    // The side of the offset is calculated depending on the difference of 
                    // target indegree and source outdegree.
                    int indegree = 0;
                    int outdegree = 0;
                    // Count all the incoming and outgoing edges
                    for (LPort port : edge.getTarget().getNode().getPorts()) {
                        indegree += port.getIncomingEdges().size();
                    }
                    for (LPort port : edge.getSource().getNode().getPorts()) {
                        outdegree += port.getOutgoingEdges().size();
                    }
                    final int degreeDiff = (int) Math.signum(outdegree - indegree);
                    final double centerYPos = ((targetAnchor.y + sourceAnchor.y) / 2)
                            + (targetAnchor.y - sourceAnchor.y) * (0.4 * degreeDiff);
                    
                    edge.getBendPoints().addAll(new KVector(centerXPos, centerYPos));
                } else if (normalSource) {
                    // If leaving a normal source, add the straight part of the target dummy.
                    // This mostly prevents intersections with big nodes in the same layer as the target 
                    edge.getBendPoints().addAll(targetVerticalCP, targetStraightCP);
                } else if (normalTarget) {
                    // If entering a normal source, keep the straight part of the source dummy.
                    edge.getBendPoints().addAll(sourceStraightCP, sourceVerticalCP);
                }
            }
        }
    }

    /**
     * Check whether for every node {@code n} of {@code layer} {@code !isNormalNode(n)} holds.
     * 
     * @param layer The layer to check.
     * @return {@code true}, if the layer only contains dummy nodes.
     */
    private boolean layerOnlyContainsDummies(final Layer layer) {
        for (final LNode n : layer.getNodes()) {
            if (isNormalNode(n)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Additionally to the {@link NodeType#NORMAL} nodes, we deem {@link NodeType#BIG_NODE} 
     * and {@link NodeType#BREAKING_POINT} to be normal nodes.
     * 
     * @param node the node to test
     * @return true if we consider {@code node} to be normal.
     */
    private boolean isNormalNode(final LNode node) {
        NodeType nt = node.getType();
        return nt == NodeType.NORMAL 
            || nt == NodeType.BIG_NODE
            || nt == NodeType.BREAKING_POINT;
    }

    /**
     * A node {@code n} qualifies as 'starting node' for a 'starting edge' if it is not a long edge dummy or label
     * dummy. That is, any edge connected to {@code n} can potentially be the first segment of a chain of edges (i.e.
     * long edge).
     * 
     * @param node
     *            the node to test
     * @return true if {@code node} qualifies as starting node.
     */
    private boolean isQualifiedAsStartingNode(final LNode node) {
        NodeType nt = node.getType();
        return nt == NodeType.NORMAL 
            || nt == NodeType.NORTH_SOUTH_PORT 
            || nt == NodeType.EXTERNAL_PORT 
            || nt == NodeType.BIG_NODE
            || nt == NodeType.BREAKING_POINT;
    }
    
    /**
     * Splines only support 1:n directed hyperEdges. Also all ports on the n-side must lay on the same 
     * SideToProcess, looking from the single port. So in a left to right routing, it is not possible to
     * create a hyperEdge from an edge going upwards and an edge going downwards.
     * 
     * Furthermore only hyperEdges are considered for vertical alignment of edges. All other edges
     * are assumed to go straight to the target port.
     * 
     * @author tit
     * 
     */
    private static final class SplineHyperEdge implements Comparable<SplineHyperEdge> {
        /** A sets of ports, determining the ports left of the hyper-edge. */  
        private final Set<LPort> leftPorts = Sets.newHashSet();
        /** A sets of ports, determining the ports right of the hyper-edge. */  
        private final Set<LPort> rightPorts = Sets.newHashSet();
        /** A set of all LEdges that are combined in this hyper-edge. */
        private final Set<LEdge> edges = Sets.newHashSet();
        /** If true, the hyper-edge has no vertical segment, connecting two ports by a vertical edge. */
        private boolean isStraight;
        /** This positions represent the upper position of the vertical segment. */
        private double topYPos;
        /** The center of the hyper edge. */
        private double centerYPos;
        /** This positions represent the lower position of the vertical segment. */
        private double bottomYPos;
        /** Outgoing dependencies are pointing to hyper-edges that must lay right of this hyper edge. */
        private final List<Dependency> outgoing = Lists.newArrayList();
        /** Incoming dependencies are pointing to hyper-edges that must lay left of this hyper edge. */
        private final List<Dependency> incoming = Lists.newArrayList();
        /** Used to mark nodes in the cycle breaker. */
        private int mark;
        /** Determines how many elements are depending on this. */
        private int inweight;
        /** Determines how many elements this element is depending on. */
        private int outweight;
        /** the rank determines the horizontal distance to the preceding layer. */
        private int rank;
        
        /**
        * Constructor for a 1:n hyper-edge.
         * @param singlePort The one and only source port.
         * @param edges All edges that shall be part of this hyper-edge paired with the side 
         * (LEFT or RIGHT) they are laying on.
         * @param sourceSide The side of the source.
         */
        SplineHyperEdge(final LPort singlePort, final Set<Pair<SideToProcess, LEdge>> edges,
                final SideToProcess sourceSide) {
            
            if (sourceSide == SideToProcess.LEFT) {
                leftPorts.add(singlePort);
            } else {
                rightPorts.add(singlePort);
            }

            double yMinPosOfTarget = Double.POSITIVE_INFINITY;
            double yMaxPosOfTarget = Double.NEGATIVE_INFINITY;
            
            for (final Pair<SideToProcess, LEdge> pair : edges) {
                final SideToProcess side = pair.getFirst();
                final LEdge edge = pair.getSecond();
                
                LPort targetPort = edge.getSource();
                if (targetPort.equals(singlePort)) {
                    targetPort = edge.getTarget();
                }
                
                if (side == SideToProcess.LEFT) {
                    leftPorts.add(targetPort);
                } else {
                    rightPorts.add(targetPort);
                }
                
                final double yPosOfTarget = targetPort.getAbsoluteAnchor().y;
                yMinPosOfTarget = Math.min(yMinPosOfTarget, yPosOfTarget);
                yMaxPosOfTarget = Math.max(yMaxPosOfTarget, yPosOfTarget);
            }
                
            final double yPosOfSingleSide = singlePort.getAbsoluteAnchor().y;

            // set the relevant positions 
            setRelevantPositions(yPosOfSingleSide, yMinPosOfTarget, yMaxPosOfTarget);
            
            for (final Pair<SideToProcess, LEdge> pair : edges) {
                this.edges.add(pair.getSecond());
            }
            isStraight = false;
        }

        /**
         * Constructor for a hyper-edges consisting of a single edge.
         * This MAY be a straight edge.
         * 
         * @param edge The straight edge that shall be the one and only element of this hyper-edge.
         * @param sourceSide On witch Layer (LEFT or RIGHT) lays the source port.
         * @param targetSide On witch Layer (LEFT or RIGHT) lays the target port.
         */
        SplineHyperEdge(final LEdge edge, final SideToProcess sourceSide, final SideToProcess targetSide) {
            // adding left and right ports
            if (sourceSide == SideToProcess.LEFT) {
                leftPorts.add(edge.getSource());
            } else {
                rightPorts.add(edge.getSource());
            } 
            if (targetSide == SideToProcess.LEFT) {
                leftPorts.add(edge.getTarget());
            } else {
                rightPorts.add(edge.getTarget());
            }
            
            // adding the edges
            edges.add(edge);
            
            // setting relevant positions
            final double sourceY = edge.getSource().getAbsoluteAnchor().y;
            final double targetY = edge.getTarget().getAbsoluteAnchor().y;
            setRelevantPositions(sourceY, targetY, targetY);

            
            isStraight = isStraight(edge.getSource().getAbsoluteAnchor().y, 
                                    edge.getTarget().getAbsoluteAnchor().y);
        }

        /**
         * {@inheritDoc}
         */
        public int compareTo(final SplineHyperEdge other) {
            return this.mark - other.mark;
        }
        
        /**
         * Sets the {@code centerYPos} to a appropriate value.
         * 
         * @param sourceY The y-value of the source point.
         * @param targetYMin The y-value of lowest target point.
         * @param targetYMax The y-value of the highest target point.
         */
        private void setRelevantPositions(final double sourceY, 
                final double targetYMin, final double targetYMax) {
            if (sourceY < targetYMin) {
                // source lays below all target ports
                centerYPos = ONE_HALF * (sourceY + targetYMin);
                topYPos = 
                        RELEVANT_POS_MID_RATE * centerYPos 
                        + RELEVANT_POS_OUTER_RATE * sourceY;
                bottomYPos =
                        RELEVANT_POS_MID_RATE * centerYPos 
                        + RELEVANT_POS_OUTER_RATE * targetYMin;
            } else {
                // source lays above all target ports
                centerYPos = ONE_HALF * (sourceY + targetYMax);
                topYPos =
                        RELEVANT_POS_MID_RATE * centerYPos 
                        + RELEVANT_POS_OUTER_RATE * targetYMax;
                bottomYPos =
                        RELEVANT_POS_MID_RATE * centerYPos 
                        + RELEVANT_POS_OUTER_RATE * sourceY;
            }
            
        }
    }

    
    /**
     * A dependency between two SplineHyperEdges.
     * A dependency pointing from Edge A to edge B means that edge A must lay left of edge B to
     * minimize the number of edge crossings.
     * A dependency with the weight of 0 means that the number of edge crossings does not vary, if the
     * position of the two hyper-edges is swapped. BUT the two hyper-edges share a vertical segment,
     * so they may not lay on the same x-coordinate as this would result in an overlapping segment. 
     * 
     * @author tit
     */
    private final class Dependency {
        /** The source of the dependency. (Should lay left) */
        private SplineHyperEdge source;
        /** The target of the dependency. (Should lay right) */
        private SplineHyperEdge target;
        /** The weight of the dependency. */
        private final int weight;
        
        /**
         * Creates a dependency from the given source to the given target.
         * 
         * @param source the dependency source
         * @param target the dependency target
         * @param weight weight of the dependency
         */
        Dependency(final SplineHyperEdge source, final SplineHyperEdge target, final int weight) {
            this.source = source;
            this.target = target;
            this.weight = weight;
            source.outgoing.add(this);
            target.incoming.add(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return source + " ->(" + weight + ") " + target;
        }
    }
    
    /**
     * Pretty small enumeration used to define witch side to process.
     * @author tit
     */
    private enum SideToProcess {
        /** Process the left side. */
        LEFT,
        /** Process the right side. */
        RIGHT
    }
}