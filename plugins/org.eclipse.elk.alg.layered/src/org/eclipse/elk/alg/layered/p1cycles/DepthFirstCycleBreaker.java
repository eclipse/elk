/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p1cycles;

import java.util.Arrays;
import java.util.List;

import org.eclipse.elk.alg.layered.LayeredPhases;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Cycle breaker implementation that uses a depth-first traversal of the graph. Described in
 * <ul>
 * <li>Emden R. Gansner, Eleftherios Koutsofios, Stephen C. North, Kiem-Phong Vo, A technique for
 * drawing directed graphs. <i>Software Engineering</i> 19(3), pp. 214-230, 1993.</li>
 * </ul>
 * 
 * <p>This cycle breaker does not support the {@link LayeredOptions#PRIORITY_DIRECTION} option 
 * that can be set on edges. Neither does it support layer constraints out of the box. 
 * If layer constraints should be observed,
 * {@link org.eclipse.elk.alg.layered.intermediate.EdgeAndLayerConstraintEdgeReverser} and
 * {@link org.eclipse.elk.alg.layered.intermediate.LayerConstraintProcessor} should
 * be used.</p>
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>an unlayered graph</dd>
 *   <dt>Postcondition:</dt><dd>the graph has no cycles</dd>
 * </dl>
 * 
 * @see org.eclipse.elk.alg.layered.intermediate.EdgeAndLayerConstraintEdgeReverser
 * @see org.eclipse.elk.alg.layered.intermediate.LayerConstraintProcessor
 */
public class DepthFirstCycleBreaker implements ILayoutPhase<LayeredPhases, LGraph> {

    /** intermediate processing configuration. */
    private static final LayoutProcessorConfiguration<LayeredPhases, LGraph> INTERMEDIATE_PROCESSING_CONFIGURATION =
        LayoutProcessorConfiguration.<LayeredPhases, LGraph>create()
            .addAfter(LayeredPhases.P5_EDGE_ROUTING, IntermediateProcessorStrategy.REVERSED_EDGE_RESTORER);

    /** list of source nodes. */
    private final List<LNode> sources = Lists.newArrayList();

    /** mark for the nodes, inducing an ordering of the nodes. */
    private int[] mark;
    /** node at which the dfs started. */
    private int[] root;
    
    /**
     * {@inheritDoc}
     */
    public LayoutProcessorConfiguration<LayeredPhases, LGraph> getLayoutProcessorConfiguration(final LGraph graph) {
        return INTERMEDIATE_PROCESSING_CONFIGURATION;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final LGraph graph, final IElkProgressMonitor monitor) {
        monitor.begin("Depth-first cycle removal", 1);
        
        List<LNode> nodes = graph.getLayerlessNodes();

        // initialize values for the algorithm 
        int unprocessedNodeCount = nodes.size();
        mark = new int[unprocessedNodeCount];
        Arrays.fill(mark, -1);
        root = new int[unprocessedNodeCount];
        Arrays.fill(root, -1);
        
        int index = 0;
        for (LNode node : nodes) {
            // the node id is used as index for the indeg, outdeg, and mark arrays
            node.id = index;
            if (Iterables.isEmpty(node.getIncomingEdges())) {
                sources.add(node);
            }
            index++;
        }

        // from every source node start a dfs
        for (LNode source : sources) {
            dfs(source, 0, source.id);
        }
        
        // check if every node has been visited, otherwise start another dfs
        //  this can, for instance, happen if the graph is a circle 
        for (int i = 0; i < mark.length; i++) {
            if (mark[i] == -1) {
                // this assumes that the nodes list is an array list and 
                // that node ids are assigned in accordance to the nodes' indexes in this list
                LNode n = nodes.get(i);
                assert n.id == i;
                dfs(n, 0, n.id);
            }
        }
        
        // reverse "back edges"
        for (LNode u : nodes) {
            for (LEdge e : Lists.newArrayList(u.getOutgoingEdges())) {
                if (e.isSelfLoop()) {
                    continue;
                }
                LNode v = e.getOther(u);
                if (root[u.id] == root[v.id] && mark[v.id] < mark[u.id]) {
                    e.reverse(graph, true);
                    graph.setProperty(InternalProperties.CYCLIC, true);
                }
            }
        }
        
        // cleanup
        this.mark = null;
        this.root = null;
        this.sources.clear();
        
        monitor.done();
    }
    
    private void dfs(final LNode n, final int index, final int rootId) {
        if (mark[n.id] != -1) {
            // already visited
            return;
        }
        
        mark[n.id] = index;
        root[n.id] = rootId;
        
        for (LEdge out : n.getOutgoingEdges()) {
            if (out.isSelfLoop()) {
                continue;
            }
            LNode target = out.getTarget().getNode();
            dfs(target, index + 1, rootId);
        }
    }
    
}
