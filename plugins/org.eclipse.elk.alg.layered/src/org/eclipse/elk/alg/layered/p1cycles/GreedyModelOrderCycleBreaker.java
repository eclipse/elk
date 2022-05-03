/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p1cycles;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.eclipse.elk.alg.layered.LayeredPhases;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Lists;

/**
 * Greedy Cycle Breaker that behaves the same as {@link GreedyCycleBreaker} but does not randomly choose an edge to
 * reverse if multiple candidates exist but does so by model order.
 */
public final class GreedyModelOrderCycleBreaker implements ILayoutPhase<LayeredPhases, LGraph> {
    
    /** intermediate processing configuration. */
    private static final LayoutProcessorConfiguration<LayeredPhases, LGraph> INTERMEDIATE_PROCESSING_CONFIGURATION =
        LayoutProcessorConfiguration.<LayeredPhases, LGraph>create()
            .addAfter(LayeredPhases.P5_EDGE_ROUTING, IntermediateProcessorStrategy.REVERSED_EDGE_RESTORER);

    /** indegree values for the nodes. */
    private int[] indeg;
    /** outdegree values for the nodes. */
    private int[] outdeg;
    /** mark for the nodes, inducing an ordering of the nodes. */
    private int[] mark;
    /** list of source nodes. */
    private final LinkedList<LNode> sources = Lists.newLinkedList();
    /** list of sink nodes. */
    private final LinkedList<LNode> sinks = Lists.newLinkedList();
    
    private Random random;
    
    @Override
    public LayoutProcessorConfiguration<LayeredPhases, LGraph> getLayoutProcessorConfiguration(final LGraph graph) {
        return INTERMEDIATE_PROCESSING_CONFIGURATION;
    }

    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Greedy cycle removal", 1);
        
        List<LNode> nodes = layeredGraph.getLayerlessNodes();

        // initialize values for the algorithm (sum of priorities of incoming edges and outgoing
        // edges per node, and the ordering calculated for each node)
        int unprocessedNodeCount = nodes.size();
        indeg = new int[unprocessedNodeCount];
        outdeg = new int[unprocessedNodeCount];
        mark = new int[unprocessedNodeCount];
        
        int index = 0;
        for (LNode node : nodes) {
            // the node id is used as index for the indeg, outdeg, and mark arrays
            node.id = index;
            
            for (LPort port : node.getPorts()) {
                // calculate the sum of edge priorities
                for (LEdge edge : port.getIncomingEdges()) {
                    // ignore self-loops
                    if (edge.getSource().getNode() == node) {
                        continue;
                    }
                    
                    int priority = edge.getProperty(LayeredOptions.PRIORITY_DIRECTION);
                    indeg[index] += priority > 0 ? priority + 1 : 1;
                }
                
                for (LEdge edge : port.getOutgoingEdges()) {
                    // ignore self-loops
                    if (edge.getTarget().getNode() == node) {
                        continue;
                    }
                    
                    int priority = edge.getProperty(LayeredOptions.PRIORITY_DIRECTION);
                    outdeg[index] += priority > 0 ? priority + 1 : 1;
                }
            }
            
            // collect sources and sinks
            if (outdeg[index] == 0) {
                sinks.add(node);
            } else if (indeg[index] == 0) {
                sources.add(node);
            }
            index++;
        }
        
        // next rank values used for sinks and sources (from right and from left)
        int nextRight = -1, nextLeft = 1;

        // assign marks to all nodes
        List<LNode> maxNodes = Lists.newArrayList();
        random = layeredGraph.getProperty(InternalProperties.RANDOM);
        
        while (unprocessedNodeCount > 0) {
            // sinks are put to the right --> assign negative rank, which is later shifted to positive
            while (!sinks.isEmpty()) {
                LNode sink = sinks.removeFirst();
                mark[sink.id] = nextRight--;
                updateNeighbors(sink);
                unprocessedNodeCount--;
            }
            
            // sources are put to the left --> assign positive rank
            while (!sources.isEmpty()) {
                LNode source = sources.removeFirst();
                mark[source.id] = nextLeft++;
                updateNeighbors(source);
                unprocessedNodeCount--;
            }
            
            // while there are unprocessed nodes left that are neither sinks nor sources...
            if (unprocessedNodeCount > 0) {
                int maxOutflow = Integer.MIN_VALUE;
                
                // find the set of unprocessed node (=> mark == 0), with the largest out flow
                for (LNode node : nodes) {
                    if (mark[node.id] == 0) {
                        int outflow = outdeg[node.id] - indeg[node.id];
                        if (outflow >= maxOutflow) {
                            if (outflow > maxOutflow) {
                                maxNodes.clear();
                                maxOutflow = outflow;
                            }
                            maxNodes.add(node);
                        }
                    }
                }
                assert maxOutflow > Integer.MIN_VALUE;
                
                // randomly select a node from the ones with maximal outflow and put it left
                LNode maxNode = chooseNodeWithMaxOutflow(maxNodes);
                mark[maxNode.id] = nextLeft++;
                updateNeighbors(maxNode);
                unprocessedNodeCount--;
            }
        }

        // shift negative ranks to positive; this applies to sinks of the graph
        int shiftBase = nodes.size() + 1;
        for (index = 0; index < nodes.size(); index++) {
            if (mark[index] < 0) {
                mark[index] += shiftBase;
            }
        }

        // reverse edges that point left
        for (LNode node : nodes) {
            LPort[] ports = LGraphUtil.toPortArray(node.getPorts());
            for (LPort port : ports) {
                LEdge[] outgoingEdges = LGraphUtil.toEdgeArray(port.getOutgoingEdges());
                
                // look at the node's outgoing edges
                for (LEdge edge : outgoingEdges) {
                    int targetIx = edge.getTarget().getNode().id;
                    if (mark[node.id] > mark[targetIx]) {
                        edge.reverse(layeredGraph, true);
                        layeredGraph.setProperty(InternalProperties.CYCLIC, true);
                    }
                }                
            }
        }

        dispose();
        monitor.done();
    }

    /**
     * Updates indegree and outdegree values of the neighbors of the given node,
     * simulating its removal from the graph. the sources and sinks lists are
     * also updated.
     * 
     * @param node node for which neighbors are updated
     */
    private void updateNeighbors(final LNode node) {
        for (LPort port : node.getPorts()) {
            for (LEdge edge : port.getConnectedEdges()) {
                LPort connectedPort = edge.getSource() == port ? edge.getTarget() : edge.getSource();
                LNode endpoint = connectedPort.getNode();
                
                // exclude self-loops
                if (node == endpoint) {
                    continue;
                }
                
                int priority = edge.getProperty(LayeredOptions.PRIORITY_DIRECTION);
                if (priority < 0) {
                    priority = 0;
                }
                int index = endpoint.id;
                if (mark[index] == 0) {
                    if (edge.getTarget() == connectedPort) {
                        indeg[index] -= priority + 1;
                        if (indeg[index] <= 0 && outdeg[index] > 0) {
                            sources.add(endpoint);
                        }
                    } else {
                        outdeg[index] -= priority + 1;
                        if (outdeg[index] <= 0 && indeg[index] > 0) {
                            sinks.add(endpoint);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Choose the node with the minimum model order.
     */
    protected LNode chooseNodeWithMaxOutflow(final List<LNode> nodes) {
        LNode returnNode = null;
        int minimumModelOrder = Integer.MAX_VALUE;
        for (LNode node : nodes) {
            // In this step nodes without a model order are disregarded.
            // One could of course think of a different strategy regarding this aspect.
            // FUTURE WORK: If multiple model order groups exist, one has to chose based on the priority of the groups.
            if (node.hasProperty(InternalProperties.MODEL_ORDER)
                    && node.getProperty(InternalProperties.MODEL_ORDER) < minimumModelOrder) {
                minimumModelOrder = node.getProperty(InternalProperties.MODEL_ORDER);
                returnNode = node;
            }
        }
        if (returnNode == null) {
            return chooseRandomNode(nodes);
        }
        return returnNode;
    }
    
    private LNode chooseRandomNode(final List<LNode> nodes) {
        return nodes.get(random.nextInt(nodes.size()));
    }
    
    /**
     * Release all created resources so the GC can reap them.
     */
    private void dispose() {
        this.indeg = null;
        this.outdeg = null;
        this.mark = null;
        sources.clear();
        sinks.clear();
    }

}
