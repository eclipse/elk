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
package org.eclipse.elk.layered.p2layers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.layered.ILayoutPhase;
import org.eclipse.elk.layered.IntermediateProcessingConfiguration;
import org.eclipse.elk.layered.graph.LEdge;
import org.eclipse.elk.layered.graph.LGraph;
import org.eclipse.elk.layered.graph.LNode;
import org.eclipse.elk.layered.graph.LPort;
import org.eclipse.elk.layered.graph.Layer;
import org.eclipse.elk.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.layered.properties.Properties;
import org.eclipse.elk.layered.properties.WideNodesStrategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The main class of the network simplex layerer component. It offers an algorithm to determine an
 * optimal layering of all nodes in the graph concerning a minimal length of all edges using the
 * network simplex algorithm described in
 * <ul>
 * <li>Emden R. Gansner, Eleftherios Koutsofios, Stephen C. North, Kiem-Phong Vo, A technique for
 * drawing directed graphs. <i>Software Engineering</i> 19(3), pp. 214-230, 1993.</li>
 * </ul>
 * 
 * <dl>
 * <dt>Precondition:</dt>
 * <dd>the graph has no cycles</dd>
 * <dt>Postcondition:</dt>
 * <dd>all nodes have been assigned a layer such that edges connect only nodes from layers with
 * increasing indices</dd>
 * </dl>
 * 
 * @author pdo
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating proposed yellow by msp
 */
public final class NetworkSimplexLayerer implements ILayoutPhase {

    /** intermediate processing configuration. */
    private static final IntermediateProcessingConfiguration BASELINE_PROCESSING_CONFIGURATION =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase1(IntermediateProcessorStrategy.EDGE_AND_LAYER_CONSTRAINT_EDGE_REVERSER)
            .addBeforePhase3(IntermediateProcessorStrategy.LAYER_CONSTRAINT_PROCESSOR);

    /** additional processor dependencies for handling big nodes. */
    private static final IntermediateProcessingConfiguration BIG_NODES_PROCESSING_ADDITIONS_AGGRESSIVE =
            IntermediateProcessingConfiguration.createEmpty()
                    .addBeforePhase2(IntermediateProcessorStrategy.BIG_NODES_PREPROCESSOR)
                    .addBeforePhase3(IntermediateProcessorStrategy.BIG_NODES_INTERMEDIATEPROCESSOR)
                    .addAfterPhase5(IntermediateProcessorStrategy.BIG_NODES_POSTPROCESSOR);

    /** additional processor dependencies for handling big nodes after cross min. */
    private static final IntermediateProcessingConfiguration BIG_NODES_PROCESSING_ADDITIONS_CAREFUL =
            IntermediateProcessingConfiguration.createEmpty()
                    .addBeforePhase4(IntermediateProcessorStrategy.BIG_NODES_SPLITTER)
                    .addAfterPhase5(IntermediateProcessorStrategy.BIG_NODES_POSTPROCESSOR);


    // ================================== Attributes ==============================================

    /** The layered graph all methods in this class operate on. */
    private LGraph layeredGraph;

    /** A {@code Collection} containing all nodes in the graph to layer. */
    private List<LNode> nodes;

    /** A {@code LinkedList} containing all edges in the graph. */
    private List<LEdge> edges;

    /**
     * A {@code LinkedList} containing all nodes of the currently identified connected component by
     * {@code connectedComponents()}.
     * 
     * @see #connectedComponents(List)
     */
    private List<LNode> componentNodes;

    /**
     * A {@code LinkedList} containing all source nodes of the graph, i.e. all nodes that have no
     * incident incoming edges.
     */
    private List<LNode> sources;

    /**
     * A {@code LinkedList} containing all sink nodes of the graph, i.e. all nodes that have no
     * incident outgoing edges.
     */
    private List<LNode> sinks;

    /** The number of incoming edges incident to each node. */
    private int[] inDegree;

    /** The number of outgoing edges incident to each node. */
    private int[] outDegree;

    /**
     * The layer each node is currently assigned to. Note that during layerer execution, the lowest
     * layer is not necessary the zeroth layer. To fulfill this characteristic, a final call of
     * {@code normalize()} has to be performed.
     */
    private int[] layer;

    /**
     * The layer each node is assigned to in the reverse traversal of the graph in
     * {@code layeringDFS()}. Note that this determined layering is only necessary to compute the
     * minimal span of each edge in the layering. It will not be reused later.
     */
    private int[] revLayer;

    /** The minimal span (i.e. shortest possible length) of each edge in the layered graph. */
    private int[] minSpan;

    /**
     * A flag indicating whether a specified node is part of the spanning tree determined by
     * {@code tightTree()}.
     * 
     * @see #tightTreeDFS()
     */
    private boolean[] treeNode;

    /**
     * A flag indicating whether a specified edge is part of the spanning tree determined by
     * {@code tightTree()}.
     * 
     * @see #tightTreeDFS(LNode)
     */
    private boolean[] treeEdge;

    /**
     * A flag indicating whether a specified edge has been visited during DFS-traversal. This array
     * has to be filled with {@code false} each time, before a DFS-based method is invoked.
     */
    private boolean[] edgeVisited;

    /**
     * A flag indicating whether a specified node has been visited during DFS-traversal. This array
     * has to be filled with {@code false} each time, before a DFS-based method is invoked.
     */
    private boolean[] nodeVisited;

    /**
     * The current postorder traversal number used by {@code postorderTraversal()} to assign an
     * unique traversal ID to each node.
     * 
     * @see #postorderTraversal(LNode)
     */
    private int postOrder;

    /**
     * The postorder traversal ID of each node determined by {@code postorderTraversal()}.
     * 
     * @see #postorderTraversal(LNode)
     */
    private int[] poID;

    /**
     * The lowest postorder traversal ID of each nodes reachable through a node lower in the
     * traversal tree determined by {@code postorderTraversal}.
     * 
     * @see #postorderTraversal(LNode)
     */
    private int[] lowestPoID;

    /**
     * The cut value of every edge defined as follows: If the edge is deleted, the spanning tree
     * breaks into two connected components, the head component containing the target node of the
     * edge and the tail component containing the source node of the edge. The cut value is the sum
     * of the weight (here {@code 1}) of all edges going from the tail to the head component,
     * including the tree edge, minus the sum of the weights of all edges from the head to the tail
     * component.
     * 
     * @see #cutvalues()
     */
    private int[] cutvalue;

    /**
     * A map storing self-loops that were removed prior to executing the actual algorithm. The map
     * stores the edges' source and target ports so they can be reinserted later.
     */
    private Map<LEdge, Pair<LPort, LPort>> removedSelfLoops;

    /** User-configured strategy to handle wide nodes. */
    private WideNodesStrategy wideNodesStrategy = WideNodesStrategy.OFF;
    
    // =============================== Initialization Methods =====================================

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    public IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(
            final LGraph graph) {

        // Basic strategy
        IntermediateProcessingConfiguration strategy =
                IntermediateProcessingConfiguration.fromExisting(BASELINE_PROCESSING_CONFIGURATION);

        // Additional dependencies
        if (graph.getProperty(Properties.DISTRIBUTE_NODES)
                || graph.getProperty(Properties.WIDE_NODES_ON_MULTIPLE_LAYERS) 
                        == WideNodesStrategy.AGGRESSIVE) {
            strategy.addAll(BIG_NODES_PROCESSING_ADDITIONS_AGGRESSIVE);
            wideNodesStrategy = WideNodesStrategy.AGGRESSIVE;
            
        } else if (graph.getProperty(Properties.WIDE_NODES_ON_MULTIPLE_LAYERS) 
                        == WideNodesStrategy.CAREFUL) {
            strategy.addAll(BIG_NODES_PROCESSING_ADDITIONS_CAREFUL);
            wideNodesStrategy = WideNodesStrategy.CAREFUL;
        }

        return strategy;
    }

    /**
     * Helper method for the network simplex layerer. It determines all connected components of the
     * graph given by a {@code Collection} containing all nodes of the graph.
     * 
     * @param theNodes
     *            a {@code Collection} containing all nodes of the graph to determine the connected
     *            components
     * @return a {@code LinkedList} of {@code LinkedLists} containing all nodes of every connected
     *         component
     * 
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#connectedComponentsDFS(LNode)
     *      connectedComponentsDFS()
     */
    private List<List<LNode>> connectedComponents(final List<LNode> theNodes) {
        // initialize required attributes
        if (nodeVisited == null || nodeVisited.length < theNodes.size()) {
            nodeVisited = new boolean[theNodes.size()];
        } else {
            Arrays.fill(nodeVisited, false);
        }
        componentNodes = Lists.newArrayList();

        // re-index nodes
        int counter = 0;
        for (LNode node : theNodes) {
            node.id = counter++;
        }
        // determine connected components
        LinkedList<List<LNode>> components = Lists.newLinkedList();
        for (LNode node : theNodes) {
            if (!nodeVisited[node.id]) {
                connectedComponentsDFS(node);
                // connected component with the most nodes should be layered first to guarantee
                // reusability of attribute instances
                if (components.isEmpty() || components.getFirst().size() < componentNodes.size()) {
                    components.addFirst(componentNodes);
                } else {
                    components.addLast(componentNodes);
                }
                componentNodes = Lists.newArrayList();
            }
        }
        return components;
    }

    /**
     * Helper method for the connected components determination. It determines all nodes, that are
     * connected with the input node (i.e. all nodes of that connected component the input node is
     * part of) and adds them to {@code componentNodes}.
     * 
     * @param node
     *            the root of the DFS-subtree
     * @return a {@code LinkedList} containing all nodes reachable through a path beginning at the
     *         input node (i.e. all nodes connected to the input node)
     * 
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#connectedComponents(List)
     *      connectedComponents()
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#componentNodes
     *      componentNodes
     */
    private void connectedComponentsDFS(final LNode node) {
        nodeVisited[node.id] = true;
        
        // node is part of the current connected component
        componentNodes.add(node);
        LNode opposite;
        
        // continue with next nodes, if not already visited
        for (LPort port : node.getPorts()) {
            for (LEdge edge : port.getConnectedEdges()) {
                opposite = getOpposite(port, edge).getNode();
                if (!nodeVisited[opposite.id]) {
                    connectedComponentsDFS(opposite);
                }
            }
        }
    }

    /**
     * Helper method for the network simplex layerer. It instantiates all necessary attributes for
     * the execution of the network simplex layerer and initializes them with their default values.
     * All edges in the connected component given by the input argument will be determined, as well
     * as the number of incoming and outgoing edges of each node ( {@code inDegree}, respectively
     * {@code outDegree}). All sinks and source nodes in the connected component identified in this
     * step will be added to {@code sinks}, respectively {@code sources}.
     * 
     * @param theNodes
     *            a {@code Collection} containing all nodes of the graph
     */
    private void initialize(final List<LNode> theNodes) {
        // initialize node attributes
        int numNodes = theNodes.size();
        inDegree = new int[numNodes];
        outDegree = new int[numNodes];
        layer = new int[numNodes];
        revLayer = new int[numNodes];
        treeNode = new boolean[numNodes];
        poID = new int[numNodes];
        lowestPoID = new int[numNodes];
        Arrays.fill(revLayer, numNodes);

        sources = Lists.newArrayList();
        sinks = Lists.newArrayList();
        nodes = theNodes;

        // determine edges and re-index nodes
        int index = 0;
        List<LEdge> theEdges = Lists.newArrayList();
        for (LNode node : theNodes) {
            node.id = index++;
            for (LPort port : node.getPorts()) {
                for (LEdge edge : port.getOutgoingEdges()) {
                    if (edge.getSource().getNode() == edge.getTarget().getNode()) {
                        // Self loops are stored in a map and removed later
                        removedSelfLoops.put(edge,
                                new Pair<LPort, LPort>(edge.getSource(), edge.getTarget()));
                    } else {
                        theEdges.add(edge);
                        outDegree[node.id]++;
                    }
                }

                for (LEdge edge : port.getIncomingEdges()) {
                    if (edge.getSource().getNode() == edge.getTarget().getNode()) {
                        // Self loops are stored in a map and removed later
                        removedSelfLoops.put(edge,
                                new Pair<LPort, LPort>(edge.getSource(), edge.getTarget()));
                    } else {
                        inDegree[node.id]++;
                    }
                }
            }
            // add node to sinks, resp. sources
            if (outDegree[node.id] == 0) {
                sinks.add(node);
            }
            if (inDegree[node.id] == 0) {
                sources.add(node);
            }
        }
        // re-index edges
        int counter = 0;
        for (LEdge edge : theEdges) {
            edge.id = counter++;
        }
        // initialize edge attributes
        int numEdges = theEdges.size();
        if (cutvalue == null || cutvalue.length < numEdges) {
            cutvalue = new int[numEdges];
            minSpan = new int[numEdges];
            treeEdge = new boolean[numEdges];
            edgeVisited = new boolean[numEdges];
        } else {
            Arrays.fill(treeEdge, false);
            Arrays.fill(edgeVisited, false);
        }
        edges = theEdges;
        postOrder = 1;

        // remove self loops
        for (LEdge edge : removedSelfLoops.keySet()) {
            edge.setSource(null);
            edge.setTarget(null);
        }
    }

    /**
     * Restores the self loops removed prior to the actual algorithm's execution.
     */
    private void restoreSelfLoops() {
        for (LEdge edge : removedSelfLoops.keySet()) {
            Pair<LPort, LPort> endpoints = removedSelfLoops.get(edge);

            edge.setSource(endpoints.getFirst());
            edge.setTarget(endpoints.getSecond());
        }
    }

    /**
     * Release all created resources so the GC can reap them.
     */
    private void dispose() {
        this.componentNodes = null;
        this.cutvalue = null;
        this.edges = null;
        this.edgeVisited = null;
        this.inDegree = null;
        this.layer = null;
        this.layeredGraph = null;
        this.lowestPoID = null;
        this.minSpan = null;
        this.nodes = null;
        this.nodeVisited = null;
        this.outDegree = null;
        this.poID = null;
        this.revLayer = null;
        this.sinks = null;
        this.sources = null;
        this.treeEdge = null;
        this.treeNode = null;
        this.removedSelfLoops = null;
    }

    // ============================== Network-Simplex Algorithm ===================================

    /** factor by which the maximal number of iterations is multiplied. */
    private static final int ITER_LIMIT_FACTOR = 4;

/**
     * The main method of the network simplex layerer. It determines an optimal layering of all
     * nodes in the graph concerning a minimal length of all edges by using the network simplex
     * algorithm described in {@literal Emden R. Gansner, Eleftherios Koutsofios, Stephen
     * C. North, Kiem-Phong Vo: "A Technique for Drawing Directed Graphs", AT&T Bell Laboratories.
     * Note that the execution time of this implemented algorithm has not been proven quadratic yet.
     * 
     * @param theLayeredGraph
     *            a layered graph which initially only contains layerless nodes and is
     *            then filled with layers
     * @param monitor
     *            the progress monitor
     *            
     * @see org.eclipse.elk.layered.p2layers.ILayerer ILayerer
     */
    public void process(final LGraph theLayeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Network simplex layering", 1);

        layeredGraph = theLayeredGraph;
        removedSelfLoops = Maps.newLinkedHashMap();
        int thoroughness = theLayeredGraph.getProperty(Properties.THOROUGHNESS) * ITER_LIMIT_FACTOR;

        List<LNode> theNodes = layeredGraph.getLayerlessNodes();
        if (theNodes.size() < 1) {
            monitor.done();
            return;
        }

        // layer graph, each connected component separately
        for (List<LNode> connComp : connectedComponents(theNodes)) {
            // determine a limit on the number of iterations
            int iterLimit = thoroughness * (int) Math.sqrt(connComp.size());

            initialize(connComp);
            // determine an initial feasible layering
            feasibleTree();
            // improve the initial layering until it is optimal
            LEdge e = leaveEdge();
            int iter = 0;
            while (e != null && iter < iterLimit) {
                // current layering is not optimal
                exchange(e, enterEdge(e));
                e = leaveEdge();
                iter++;
            }

            if (wideNodesStrategy == WideNodesStrategy.AGGRESSIVE) {
                normalize();
            } else {
                balance(normalize());
            }
            // put nodes into their assigned layers
            for (LNode node : nodes) {
                putNode(node);
            }
        }

        // restore the self loops
        restoreSelfLoops();

        // empty the list of unlayered nodes
        theNodes.clear();

        // release the created resources
        dispose();
        monitor.done();
    }

    /**
     * Helper method for the network simplex layerer. It determines an initial feasible spanning
     * tree of the graph. This graph will be tight by construction. For determination, an initial
     * feasible tree is being computed. If all tree edges contained are tight (i.e. their minimal
     * length corresponds with their actual length), a tight tree has already been found. If not,
     * this method iteratively determines a non-tree edge incident to the tree with a minimal amount
     * of slack (i.e. the edge with the lowest difference between its current and minimal length)
     * and shifts all tree edges accordingly to shorten the edge to its minimal size. The edge has
     * become tight and will be added to the spanning tree together with all tight edges leading to
     * non-tree nodes as well. If all nodes of the graph are contained in the spanning tree, a tight
     * tree has been found. A concluding computation of each edge's initial cut value takes place.
     * 
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#tightTreeDFS(LNode)
     *      tightTreeDFS()
     */
    private void feasibleTree() {
        initLayering();
        if (edges.size() > 0) {
            Arrays.fill(edgeVisited, false);
            while (tightTreeDFS(nodes.iterator().next()) < nodes.size()) {
                // some nodes are still not part of the tree
                LEdge e = minimalSlack();
                int slack = layer[e.getTarget().getNode().id] - layer[e.getSource().getNode().id]
                                - minSpan[e.id];
                if (treeNode[e.getTarget().getNode().id]) {
                    slack = -slack;
                }

                // update tree
                for (LNode node : nodes) {
                    if (treeNode[node.id]) {
                        layer[node.id] += slack;
                    }
                }
                Arrays.fill(edgeVisited, false);
            }
            // update tree-related attributes
            Arrays.fill(edgeVisited, false);
            postorderTraversal(nodes.iterator().next());
            cutvalues();
        }
    }

    /**
     * Helper method for the network simplex layerer. It determines an initial layering of all nodes
     * in the graph. The graph will be traversed by a depth-first-search assigning all nodes to
     * layers equivalent to its height in the thereby defined DFS-tree. Furthermore, the minimal
     * length of each node in the graph will be determined and, as a first optimization, all leafs
     * of the graph (i.e. sink and source nodes) will be assigned to a layer as close to their
     * adjacent nodes as possible.
     * 
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#layer layer
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#minSpan minSpan
     */
    private void initLayering() {
        // determine initial layering
        layeringTopologicalNumbering(sources, false);
        // determine second (reverse) layering
        layeringTopologicalNumbering(sinks, true);
        
        // normalize revLayer
        int min = Integer.MAX_VALUE;
        for (LNode node : sources) {
            if (revLayer[node.id] < min) {
                min = revLayer[node.id];
            }
        }
        for (LNode node : nodes) {
            revLayer[node.id] -= min;
        }
        
        // determine minimal length of each edge
        for (LEdge edge : edges) {
            if (layer[edge.getTarget().getNode().id] <= revLayer[edge.getSource().getNode().id]) {
                minSpan[edge.id] = 1;
            } else {
                minSpan[edge.id] = Math.min(layer[edge.getTarget().getNode().id]
                                - layer[edge.getSource().getNode().id], Math.min(revLayer[edge
                                .getTarget().getNode().id]
                                - revLayer[edge.getSource().getNode().id], layer[edge.getTarget()
                                .getNode().id] - revLayer[edge.getSource().getNode().id]));
            }
        }
    }

    /**
     * Helper method for the network simplex layerer. It determines an (initial) feasible layering
     * for the graph by traversing it by a minimal topological numbering. Dependently of
     * the chosen mode indicated by {@code reverse}, this method traverses incoming edges (if
     * {@code reverse = true}), or outgoing edges, if {@code reverse = false}, only. Therefore, this
     * method should only be called with source nodes as argument in the first-mentioned case and
     * only with sink nodes in the latter case.
     * 
     * @param initialRootNodes
     *            the roots of the topological numbering (sources or sinks, depending on the direction)
     * @param reverse
     *            the traversal direction of the topological numbering. If {@code reverse = true}),
     *            this method only traverses incoming edges. Otherwise, if {@code reverse = false},
     *            only outgoing edges will be traversed
     * 
     * @see #layer
     * @see #revLayer
     */
    private void layeringTopologicalNumbering(final List<LNode> initialRootNodes,
            final boolean reverse) {
        
        // initialize the number of incident edges for each node
        int[] incident = new int[nodes.size()];
        for (LNode node : nodes) {
            if (reverse) {
                for (LPort port : node.getPorts()) {
                    incident[node.id] += port.getOutgoingEdges().size();
                }
            } else {
                for (LPort port : node.getPorts()) {
                    incident[node.id] += port.getIncomingEdges().size();
                }
            }
        }

        LinkedList<LNode> roots = Lists.newLinkedList(initialRootNodes);
        
        while (!roots.isEmpty()) {
            LNode node = roots.removeFirst();
            
            if (reverse) {
                for (LPort port : node.getPorts()) {
                    for (LEdge edge : port.getIncomingEdges()) {
                        LNode source = edge.getSource().getNode();
                        revLayer[source.id] = Math.min(revLayer[source.id], revLayer[node.id] - 1);
                        incident[source.id]--;
                        if (incident[source.id] == 0) {
                            roots.addLast(source);
                        }
                    }
                }
            } else {
                for (LPort port : node.getPorts()) {
                    for (LEdge edge : port.getOutgoingEdges()) {
                        LNode target = edge.getTarget().getNode();
                        layer[target.id] = Math.max(layer[target.id], layer[node.id] + 1);
                        incident[target.id]--;
                        if (incident[target.id] == 0) {
                            roots.addLast(target);
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper method for the network simplex layerer. It determines the length of the currently
     * shortest incoming or outgoing edge of the input node.
     * 
     * @param node
     *            the node to determine the length of its shortest incoming or outgoing edge
     * @return a pair containing the length of the shortest incoming (first element) and outgoing
     *         edge (second element) incident to the input node or {@code -1} as the length, if no
     *         such edge is incident
     * 
     * @see org.eclipse.elk.core.util.Pair Pair
     */
    private Pair<Integer, Integer> minimalSpan(final LNode node) {
        int minSpanOut = Integer.MAX_VALUE;
        int minSpanIn = Integer.MAX_VALUE;
        int currentSpan;

        for (LPort port : node.getPorts()) {
            for (LEdge edge : port.getConnectedEdges()) {
                currentSpan =
                        layer[edge.getTarget().getNode().id] - layer[edge.getSource().getNode().id];
                if (edge.getTarget() == port && currentSpan < minSpanIn) {
                    minSpanIn = currentSpan;
                } else if (currentSpan < minSpanOut) {
                    minSpanOut = currentSpan;
                }
            }
        }
        if (minSpanIn == Integer.MAX_VALUE) {
            minSpanIn = -1;
        }
        if (minSpanOut == Integer.MAX_VALUE) {
            minSpanOut = -1;
        }

        return new Pair<Integer, Integer>(minSpanIn, minSpanOut);
    }

    /**
     * Helper method for the network simplex layerer. It determines a DFS-subtree of the graph by
     * traversing tight edges only (i.e. edges whose current length matches their minimal length in
     * the layering) and returns the number of nodes in this. If this number is equal to the total
     * number of nodes in the graph, a tight spanning tree has been determined.
     * 
     * @param node
     *            the root of the DFS-subtree
     * @return the number of nodes in the determined tight DFS-tree
     * 
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#treeEdge treeEdge
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#treeNode treeNode
     */
    private int tightTreeDFS(final LNode node) {
        int nodeCount = 1;
        treeNode[node.id] = true;
        LNode opposite = null;
        for (LPort port : node.getPorts()) {
            for (LEdge edge : port.getConnectedEdges()) {
                if (!edgeVisited[edge.id]) {
                    edgeVisited[edge.id] = true;
                    opposite = getOpposite(port, edge).getNode();
                    if (treeEdge[edge.id]) {
                        // edge is a tree edge already: follow this path
                        nodeCount += tightTreeDFS(opposite);
                    } else if (!treeNode[opposite.id]
                            && minSpan[edge.id] == layer[edge.getTarget().getNode().id]
                                    - layer[edge.getSource().getNode().id]) {
                        // edge is a tight non-tree edge
                        treeEdge[edge.id] = true;
                        nodeCount += tightTreeDFS(opposite);
                    }
                }
            }
        }
        return nodeCount;
    }

    /**
     * Helper method for the network simplex layerer. It returns the non-tree edge incident on the
     * tree and incident to a non-tree node with a minimal amount of slack (i.e. an edge with the
     * lowest difference between its current and minimal length) or {@code null}, if no such edge
     * exists. Note, that the returned edge's slack is never {@code 0}, since otherwise, the edge
     * would be a tree-edge.
     * 
     * @return a non-tree edge incident on the tree with a minimal amount of slack or {@code null},
     *         if no such edge exists
     */
    private LEdge minimalSlack() {
        int minSlack = Integer.MAX_VALUE;
        LEdge minSlackEdge = null;
        int curSlack;
        for (LEdge edge : edges) {
            if (treeNode[edge.getSource().getNode().id] ^ treeNode[edge.getTarget().getNode().id]) {
                // edge is non-tree edge and incident on the tree
                curSlack =
                        layer[edge.getTarget().getNode().id] - layer[edge.getSource().getNode().id]
                                - minSpan[edge.id];
                if (curSlack < minSlack) {
                    minSlack = curSlack;
                    minSlackEdge = edge;
                }
            }
        }
        return minSlackEdge;
    }

    /**
     * Helper method for the network simplex layerer. It performs a postorder DFS-traversal of the
     * graph beginning with the input node. Each node will be assigned a unique traversal ID, which
     * will be stored in {@code poID}. Furthermore, the lowest postorder traversal ID of any node in
     * a descending path relative to the input node will be computed and stored in
     * {@code lowestPoID}, which is also the return value of this method.
     * 
     * @param node
     *            the root of the DFS-subtree
     * @return the lowest post-order ID of any descending edge in the depth-first-search
     * 
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#poID poID
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#lowestPoID lowestPoID
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#postOrder postOrder
     */
    private int postorderTraversal(final LNode node) {
        int lowest = Integer.MAX_VALUE;
        for (LPort port : node.getPorts()) {
            for (LEdge edge : port.getConnectedEdges()) {
                if (treeEdge[edge.id] && !edgeVisited[edge.id]) {
                    edgeVisited[edge.id] = true;
                    lowest =
                            Math.min(lowest, postorderTraversal(getOpposite(port, edge).getNode()));
                }
            }
        }
        poID[node.id] = postOrder;
        lowestPoID[node.id] = Math.min(lowest, postOrder++);
        return lowestPoID[node.id];
    }

    /**
     * Helper method for the the network simplex layerer. It determines, whether an node is part of
     * the head component of the given edge defined as follows: If the input edge is deleted, the
     * spanning tree breaks into to connected components. The head component is that component,
     * which contains the edge's target node, and the tail component is the component, which
     * contains the edge's source node. Note that a node either belongs to the head or tail
     * component. Therefore, if the node is not part of the head component, it must be part of the
     * tail component and vice versa.
     * 
     * @param node
     *            the node to determine, whether it belongs to the edges head (or tail) component
     * @param edge
     *            the edge to determine, whether the node is in the head (or tail) component
     * @return {@code true}, if node is in the head component or {@code false}, if the node is in
     *         the tail component of the edge
     */
    private boolean isInHead(final LNode node, final LEdge edge) {
        LNode source = edge.getSource().getNode();
        LNode target = edge.getTarget().getNode();

        if (lowestPoID[source.id] <= poID[node.id] && poID[node.id] <= poID[source.id]
                && lowestPoID[target.id] <= poID[node.id] && poID[node.id] <= poID[target.id]) {
            // node is in a descending path in the DFS-Tree
            if (poID[source.id] < poID[target.id]) {
                // root is in the head component
                return false;
            }
            return true;
        }
        if (poID[source.id] < poID[target.id]) {
            // root is in the head component
            return true;
        }
        return false;
    }

    /**
     * Helper method for the network simplex layerer. It determines the cut value of each tree edge,
     * which is defined as follows: If the edge is deleted, the spanning tree breaks into two
     * connected components, the head component containing the target node of the edge and the tail
     * component containing the source node of the edge. The cut value is the sum of the weights of
     * all edges going from the tail to the head component, including the tree edge itself, minus
     * the sum of the weights of all edges from the head to the tail component.
     * 
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#cutvalue cutvalue
     */
    private void cutvalues() {
        // determine incident tree edges for each node
        List<LNode> leafs = Lists.newArrayList();
        int treeEdgeCount;
        List<Set<LEdge>> unknownCutvalues = Lists.newArrayListWithCapacity(nodes.size());
        for (LNode node : nodes) {
            treeEdgeCount = 0;
            unknownCutvalues.add(new HashSet<LEdge>());
            for (LPort port : node.getPorts()) {
                for (LEdge edge : port.getConnectedEdges()) {
                    if (treeEdge[edge.id]) {
                        unknownCutvalues.get(node.id).add(edge);
                        treeEdgeCount++;
                    }
                }
            }
            if (treeEdgeCount == 1) {
                leafs.add(node);
            }
        }
        // determine cut values
        LEdge toDetermine;
        LNode source, target;
        for (LNode node : leafs) {
            while (unknownCutvalues.get(node.id).size() == 1) {
                // one tree edge with undetermined cut value is incident
                toDetermine = unknownCutvalues.get(node.id).iterator().next();
                cutvalue[toDetermine.id] = 1;
                source = toDetermine.getSource().getNode();
                target = toDetermine.getTarget().getNode();
                for (LPort port : node.getPorts()) {
                    for (LEdge edge : port.getConnectedEdges()) {
                        if (!edge.equals(toDetermine)) {
                            if (treeEdge[edge.id]) {
                                // edge is tree edge
                                if (source.equals(edge.getSource().getNode())
                                        || target.equals(edge.getTarget().getNode())) {
                                    // edge has not the same direction as toDetermine
                                    cutvalue[toDetermine.id] -= cutvalue[edge.id] - 1;
                                } else {
                                    cutvalue[toDetermine.id] += cutvalue[edge.id] - 1;
                                }
                            } else {
                                // edge is non-tree edge
                                if (node.equals(source)) {
                                    if (edge.getSource().getNode().equals(node)) {
                                        cutvalue[toDetermine.id]++;
                                    } else {
                                        cutvalue[toDetermine.id]--;
                                    }
                                } else {
                                    if (edge.getSource().getNode().equals(node)) {
                                        cutvalue[toDetermine.id]--;
                                    } else {
                                        cutvalue[toDetermine.id]++;
                                    }
                                }
                            }
                        }
                    }
                }
                // remove edge from 'unknownCutvalues'
                unknownCutvalues.get(source.id).remove(toDetermine);
                unknownCutvalues.get(target.id).remove(toDetermine);
                // proceed with next node
                if (source.equals(node)) {
                    node = toDetermine.getTarget().getNode();
                } else {
                    node = toDetermine.getSource().getNode();
                }
            }
        }
    }

    /**
     * Helper method for the network simplex layerer. It returns a tree edge with a negative cut
     * value or {@code null}, if no such edge exists, meaning that the current layer assignment of
     * all nodes is optimal. Note, that this method returns any edge with a negative cut value. A
     * special preference to an edge with lowest value will not be given.
     * 
     * @return a tree edge with negative cut value or {@code null}, if no such edge exists
     */
    private LEdge leaveEdge() {
        for (LEdge edge : edges) {
            if (treeEdge[edge.id] && cutvalue[edge.id] < 0) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Helper method for the network simplex layerer. It determines an non-tree edge to replace the
     * given tree edge in the spanning tree. All edges going from the head component to the tail
     * component of the edge will be considered. The edge with a minimal amount of slack (i.e. the
     * lowest difference between its current to its minimal length) will be returned.
     * 
     * @param leave
     *            the tree edge to determine a non-tree edge to be replaced with
     * @return a non-tree edge with a minimal amount of slack to replace the given edge
     * @throws IllegalArgumentException
     *             if the input edge is not a tree edge
     */
    private LEdge enterEdge(final LEdge leave) {
        if (!treeEdge[leave.id]) {
            throw new IllegalArgumentException("The input edge is not a tree edge.");
        }

        LEdge replace = null;
        int repSlack = Integer.MAX_VALUE;
        int slack;
        LNode source, target;
        for (LEdge edge : edges) {
            source = edge.getSource().getNode();
            target = edge.getTarget().getNode();
            if (isInHead(source, leave) && !isInHead(target, leave)) {
                // edge is to consider
                slack = layer[target.id] - layer[source.id] - minSpan[edge.id];
                if (slack < repSlack) {
                    repSlack = slack;
                    replace = edge;
                }
            }
        }
        return replace;
    }

    /**
     * Helper method for the network simplex layerer. It exchanges the tree-edge {@code leave} by
     * the non-tree edge {@code enter} and updates all values based on the tree (i.e. performs a new
     * postorder DFS-traversal and updates the cut values).
     * 
     * @param leave
     *            the tree-edge to be replaced
     * @param enter
     *            the non-tree edge to replace the tree edge
     * @throws IllegalArgumentException
     *             if either {@code leave} is no tree edge or {@code enter} is a tree edge already
     * 
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#enterEdge(LEdge)
     *      enterEdge()
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#leaveEdge() leaveEdge()
     */
    private void exchange(final LEdge leave, final LEdge enter) {
        if (!treeEdge[leave.id]) {
            throw new IllegalArgumentException("Given leave edge is no tree edge.");
        }
        if (treeEdge[enter.id]) {
            throw new IllegalArgumentException("Given enter edge is a tree edge already.");
        }

        // update tree
        treeEdge[leave.id] = false;
        treeEdge[enter.id] = true;
        int delta =
                layer[enter.getTarget().getNode().id] - layer[enter.getSource().getNode().id]
                        - minSpan[enter.id];
        if (!isInHead(enter.getTarget().getNode(), leave)) {
            delta = -delta;
        }
        for (LNode node : nodes) {
            if (!isInHead(node, leave)) {
                layer[node.id] += delta;
            }
        }
        // update tree-based values
        postOrder = 1;
        Arrays.fill(edgeVisited, false);
        postorderTraversal(nodes.iterator().next());
        cutvalues();
    }

    /**
     * Helper method for the network simplex layerer. It normalizes the layering, i.e. determines
     * the lowest layer assigned to a node and shifts all nodes up or down in the layers
     * accordingly. After termination, the lowest layer assigned to a node will be zeroth (and
     * therefore first) layer. This method returns an integer array indicating how many nodes are
     * assigned to which layer. Note that the total number of layers necessary to layer the graph is
     * indicated thereby, which is the size if the array.
     * 
     * @return an integer array indicating how many nodes are assigned to which layer
     */
    private int[] normalize() {
        // determine lowest assigned layer and layer count
        int highest = Integer.MIN_VALUE;
        int lowest = Integer.MAX_VALUE;
        for (LNode node : sources) {
            if (layer[node.id] < lowest) {
                lowest = layer[node.id];
            }
        }
        for (LNode node : sinks) {
            if (layer[node.id] > highest) {
                highest = layer[node.id];
            }
        }
        // normalize and determine layer filling
        int layerID = 0;
        int[] filling = new int[highest - lowest + 1];
        for (LNode node : nodes) {
            layer[node.id] -= lowest;
            filling[layer[node.id]]++;
        }
        // also consider nodes of already layered connected components
        for (Layer eLayer : layeredGraph) {
            filling[layerID++] += eLayer.getNodes().size();
            if (filling.length == layerID) {
                break;
            }
        }
        return filling;
    }

    /**
     * Helper method for the network simplex layerer. It balances the layering concerning its width,
     * i.e. the number of nodes in each layer. If the graph allows multiple optimal layerings
     * regarding a minimal edge length, this method moves separate nodes to a layer with a minimal
     * amount of currently contained nodes with respect to the retention of feasibility and
     * optimality of the given layering.
     * 
     * @param filling
     *            an integer array indicating how many nodes are currently assigned to each layer
     */
    private void balance(final int[] filling) {
        // determine possible layers
        int newLayer;
        Pair<Integer, Integer> range = null;
        for (LNode node : nodes) {
            if (inDegree[node.id] == outDegree[node.id]) {
                // node might get shifted
                newLayer = layer[node.id];
                range = minimalSpan(node);
                for (int i = layer[node.id] - range.getFirst() + 1; i < layer[node.id]
                        + range.getSecond(); i++) {
                    if (filling[i] < filling[newLayer]) {
                        newLayer = i;
                    }
                }
                // assign new layer
                if (filling[newLayer] < filling[layer[node.id]]) {
                    filling[layer[node.id]]--;
                    filling[newLayer]++;
                    layer[node.id] = newLayer;
                }
            }
        }
    }

    /**
     * Helper method for the network simplex layerer. It puts the specified node into its assigned
     * layer indicated by {@code layer} in the layered graph. If the layered graph does not contain
     * the specified layer (i.e. the number of layers in {@code layeredGraph} is lesser than the
     * supposed height in the layering), additional layers will be added to match the required
     * amount.
     * 
     * @param node
     *            the node to put into its assigned layer in the layered graph
     * 
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#layeredGraph layeredGraph
     * @see org.eclipse.elk.layered.p2layers.NetworkSimplexLayerer#layer layer
     */
    private void putNode(final LNode node) {
        List<Layer> layers = layeredGraph.getLayers();
        // add additional layers to match required amount
        while (layers.size() <= layer[node.id]) {
            layers.add(layers.size(), new Layer(layeredGraph));
        }
        node.setLayer(layers.get(layer[node.id]));
    }

    /**
     * Helper method for the network simplex layerer. It returns the port that is connected to the
     * opposite side of the specified edge from the viewpoint of the input port.
     * 
     * @param port
     *            the port to get the opposite port from
     * @param edge
     *            the edge to consider when determining the opposite port
     * @return the opposite port from the viewpoint of the given port
     * 
     * @throws IllegalArgumentException
     *             if the input edge is not connected to the input port
     */
    private LPort getOpposite(final LPort port, final LEdge edge) {
        if (edge.getSource().equals(port)) {
            return edge.getTarget();
        } else if (edge.getTarget().equals(port)) {
            return edge.getSource();
        }
        throw new IllegalArgumentException("Input edge is not connected to the input port.");
    }

}
