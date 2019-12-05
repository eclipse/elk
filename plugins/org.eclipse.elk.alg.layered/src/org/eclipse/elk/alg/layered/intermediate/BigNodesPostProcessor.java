/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * This class merges the series of big node dummy nodes introduced by either the
 * {@link BigNodesPreProcessor} or the {@link BigNodesSplitter} back into the original node. 
 * I.e., the original width is assigned to the first node of the series, all other dummies 
 * are dropped. Furthermore, the EAST ports that were moved to the last dummy node, are moved 
 * back to the original node. Here, the x coordinate of the moved ports have to be adapted properly.
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>a graph with routed edges.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>all big node dummy nodes are removed from the graph.</dd>
 *   <dt>Slots:</dt>
 *     <dd>After phase 5.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>Before {@link LongEdgeJoiner}</dd>
 * </dl>
 * 
 * @see BigNodesPreProcessor
 * @see BigNodesIntermediateProcessor
 * 
 * @author uru
 */
public class BigNodesPostProcessor implements ILayoutProcessor<LGraph> {

    private LGraph layeredGraph;

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph theLayeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Big nodes post-processing", 1);

        this.layeredGraph = theLayeredGraph;

        for (Layer layer : layeredGraph) {

            // collect all starting big nodes
            Iterable<LNode> bigNodes = Iterables.filter(layer.getNodes(), new Predicate<LNode>() {
                public boolean apply(final LNode node) {
                    return isInitialBigNode(node);
                }
            });

            for (LNode node : bigNodes) {
                // set the original size
                Float originalSize = node.getProperty(InternalProperties.BIG_NODE_ORIGINAL_SIZE);

                // remove the dummy nodes
                LNode lastDummy = removeBigNodeChain(node);

                // move the east ports
                List<LPort> toMove = Lists.newArrayList();
                for (LPort p : lastDummy.getPorts(PortSide.EAST)) {
                    toMove.add(p);

                    // adjust position of ports
                    // ports might not be placed exactly at a node's boundary,
                    // hence we apply the relative position of the port 
                    // at the dummy node to the original node
                    double offset = p.getPosition().x - lastDummy.getSize().x;
                    p.getPosition().x = originalSize + offset;
                }
                
                node.getSize().x = originalSize.doubleValue();

                for (LPort p : toMove) {
                    p.setNode(node);
                }

                // layers that contain only dummy nodes are considered to be of width 0. Hence it is
                // possible that a merged big node might exceed the calculated width of the layered
                // graph (during edge routing). In such a case we expand the bounding box here.
                if (layeredGraph.getSize().x < node.getPosition().x + node.getSize().x) {
                    layeredGraph.getSize().x = node.getPosition().x + node.getSize().x;
                }

                // reassign labels
                List<LLabel> labels = node.getProperty(InternalProperties.BIGNODES_ORIG_LABELS);
                node.getLabels().addAll(labels);
                
                Function<Void, Void> f = node.getProperty(InternalProperties.BIGNODES_POST_PROCESS);
                if (f != null) {
                    f.apply(null);
                }
            }

        }

        monitor.done();
    }

    /**
     * Remove the big node dummy nodes from the graph and return the last node from the chain.
     * 
     * @param start
     * @return
     */
    private LNode removeBigNodeChain(final LNode start) {

        List<LEdge> outs = Lists.newLinkedList(start.getOutgoingEdges());
        for (LEdge edge : outs) {
            LNode target = edge.getTarget().getNode();

            // only walk through intermediate big nodes
            if ((target.getType() == NodeType.BIG_NODE) && !isInitialBigNode(target)) {
                // remove the current dummy and its incoming edge
                target.getLayer().getNodes().remove(target);

                // remove the ports
                edge.getSource().setNode(null);
                edge.getTarget().setNode(null);

                // call recursively
                return removeBigNodeChain(target);
            } else {
                // this was the last node
                return start;
            }
        }

        // no final outgoing edge
        return start;
    }

    /**
     * @return true, if the {@link InternalProperties#BIG_NODE_INITIAL} property is set and an
     *         {@link InternalProperties#ORIGIN} is set.
     */
    private boolean isInitialBigNode(final LNode node) {
        return (node.getProperty(InternalProperties.BIG_NODE_INITIAL))
                && (node.getProperty(InternalProperties.ORIGIN) != null);
    }
}
