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
package org.eclipse.elk.alg.layered.p2layers;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.elk.alg.layered.ILayoutPhase;
import org.eclipse.elk.alg.layered.IntermediateProcessingConfiguration;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.layered.networksimplex.NEdge;
import org.eclipse.elk.alg.layered.networksimplex.NGraph;
import org.eclipse.elk.alg.layered.networksimplex.NNode;
import org.eclipse.elk.alg.layered.networksimplex.NetworkSimplex;
import org.eclipse.elk.alg.layered.options.WideNodesStrategy;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;

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

    /**
     * A {@code LinkedList} containing all nodes of the currently identified connected component by
     * {@code connectedComponents()}.
     * 
     * @see #connectedComponents(List)
     */
    private List<LNode> componentNodes;

    /**
     * A flag indicating whether a specified node has been visited during DFS-traversal. This array
     * has to be filled with {@code false} each time, before a DFS-based method is invoked.
     */
    private boolean[] nodeVisited;

    /** User-configured strategy to handle wide nodes. */
    private WideNodesStrategy wideNodesStrategy = WideNodesStrategy.OFF;
    
    
    // =============================== Initialization Methods =====================================

    /**
     * {@inheritDoc}
     */
    public IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(final LGraph graph) {
        // Basic strategy
        IntermediateProcessingConfiguration strategy =
                IntermediateProcessingConfiguration.fromExisting(BASELINE_PROCESSING_CONFIGURATION);

        // Additional dependencies
        if (graph.getProperty(LayeredOptions.LAYERING_WIDE_NODES_ON_MULTIPLE_LAYERS) == WideNodesStrategy.AGGRESSIVE) {
            strategy.addAll(BIG_NODES_PROCESSING_ADDITIONS_AGGRESSIVE);
            wideNodesStrategy = WideNodesStrategy.AGGRESSIVE;
            
        } else if (graph.getProperty(LayeredOptions.LAYERING_WIDE_NODES_ON_MULTIPLE_LAYERS) 
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
     * @see de.cau.cs.kieler.klay.layered.p2layers.NetworkSimplexLayerer#connectedComponentsDFS(LNode)
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
     * @see de.cau.cs.kieler.klay.layered.p2layers.NetworkSimplexLayerer#connectedComponents(List)
     *      connectedComponents()
     * @see de.cau.cs.kieler.klay.layered.p2layers.NetworkSimplexLayerer#componentNodes
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
    private NGraph initialize(final List<LNode> theNodes) {

        final Map<LNode, NNode> nodeMap = Maps.newHashMap();
        
        // transform nodes
        int counter = 0;
        NGraph graph = new NGraph();
        for (LNode lNode : theNodes) {
            NNode nNode = NNode.of()
                               .id(counter++)
                               .origin(lNode)
                               .create(graph);
            nodeMap.put(lNode, nNode);
        }
        
        // transform edges
        for (LNode lNode : theNodes) {
            for (LEdge lEdge : lNode.getOutgoingEdges()) {
                
                // ignore self-loops
                if (lEdge.isSelfLoop()) {
                    continue;
                }
                
                NEdge.of(lEdge)
                     .weight(1 * Math.max(1, lEdge.getProperty(LayeredOptions.PRIORITY_SHORTNESS)))
                     .delta(1)
                     .source(nodeMap.get(lEdge.getSource().getNode()))
                     .target(nodeMap.get(lEdge.getTarget().getNode()))
                     .create();
            }
        }
        
        return graph;
    }


    /**
     * Release all created resources so the GC can reap them.
     */
    private void dispose() {
        this.componentNodes = null;
        this.layeredGraph = null;
        this.nodeVisited = null;
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
     * @see de.cau.cs.kieler.klay.layered.p2layers.ILayerer ILayerer
     */
    public void process(final LGraph theLayeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Network simplex layering", 1);

        layeredGraph = theLayeredGraph;
        int thoroughness = theLayeredGraph.getProperty(LayeredOptions.THOROUGHNESS) * ITER_LIMIT_FACTOR;

        List<LNode> theNodes = layeredGraph.getLayerlessNodes();
        if (theNodes.size() < 1) {
            monitor.done();
            return;
        }

        // layer graph, each connected component separately
        for (List<LNode> connComp : connectedComponents(theNodes)) {
            
            // determine a limit on the number of iterations
            int iterLimit = thoroughness * (int) Math.sqrt(connComp.size());

            NGraph graph = initialize(connComp);

            // execute the network simplex algorithm on the (sub-)graph
            NetworkSimplex.forGraph(graph).withIterationLimit(iterLimit)
                    .withPreviousLayering(layeredGraph)
                    .withBalancing(wideNodesStrategy == WideNodesStrategy.OFF)
                    .execute(monitor.subTask(1));

            // the layers are store in the NNode's layer field.
            List<Layer> layers = layeredGraph.getLayers();
            for (NNode nNode : graph.nodes) {
                // add additional layers to match required number
                while (layers.size() <= nNode.layer) {
                    layers.add(layers.size(), new Layer(layeredGraph));
                }
                LNode lNode = (LNode) nNode.origin;
                lNode.setLayer(layers.get(nNode.layer));
            }
        }

        // empty the list of unlayered nodes
        theNodes.clear();

        // release the created resources
        dispose();
        monitor.done();
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
