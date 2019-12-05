/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
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
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayerConstraint;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Processes the series of big nodes prior to the {@link LongEdgeSplitter}. The
 * {@link LongEdgeSplitter} introduces dummy nodes for edges that span layers. In order to avoid the
 * introduction of long edge dummy nodes in between big node dummy nodes, every series of big node
 * dummy nodes is condensed to one side. I.e., for most series, if two adjacent big node dummy nodes
 * are located in layer i and layer j (j > i+1), the dummy in layer j is moved to layer i+1.
 * 
 * An exception are big nodes with a {@link LayerConstraint} LAST, or LAST_SEPARATE. For these
 * nodes, the series of dummy nodes is condensed to the right side. Furthermore, the
 * {@link LayerConstraint} is moved to the last dummy in the series.
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>a layered graph.</dd>
 *   <dt>Postcondition:</dt><dd>no connected pair of big edge dummy nodes is located in 
 *       non-adjacent layers.</dd>
 *   <dt>Slots:</dt><dd>Before phase 3.</dd>
 *   <dt>Same-slot dependencies:</dt><dd>Before {@link LongEdgeSplitter}</dd>
 * </dl>
 * 
 * @see BigNodesPreProcessor
 * @see BigNodesPostProcessor
 * 
 * @author uru
 */
public class BigNodesIntermediateProcessor implements ILayoutProcessor<LGraph> {

    private LGraph layeredGraph;

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph theLayeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Big nodes intermediate-processing", 1);

        this.layeredGraph = theLayeredGraph;

        for (Layer layer : layeredGraph) {

            Iterable<LNode> nodes = Lists.newLinkedList(layer.getNodes());

            // collect all starting big nodes
            Iterable<LNode> bigNodes = Iterables.filter(nodes, new Predicate<LNode>() {
                public boolean apply(final LNode node) {
                    return isInitialBigNode(node);
                }
            });

            for (LNode node : bigNodes) {

                // handle LAST and LAST_SEPARATE differently
                // condense the nodes to the right side and move the layer constraint
                if ((node.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT) == LayerConstraint.LAST)
                        || (node.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT) 
                                == LayerConstraint.LAST_SEPARATE)) {

                    // condense
                    LNode last = condenseBigNodesChain(node, false);
                    // move the layer constraint property
                    last.setProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT,
                            node.getProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT));
                    node.setProperty(LayeredOptions.LAYERING_LAYER_CONSTRAINT, LayerConstraint.NONE);
                } else {
                    // only condense
                    condenseBigNodesChain(node, true);
                }

            }

        }

        monitor.done();
    }

    private LNode condenseBigNodesChain(final LNode start, final boolean left) {

        List<LEdge> outs = Lists.newLinkedList(start.getOutgoingEdges());
        for (LEdge edge : outs) {
            LNode target = edge.getTarget().getNode();

            // only walk through intermediate big nodes
            // also ignore originally incoming WEST ports that are reverted
            // and outgoing edges targeting WEST ports (have to be part of another big node)
            if (!isInitialBigNode(target)
                    && (target.getType() == NodeType.BIG_NODE)
                    && !edge.getProperty(InternalProperties.REVERSED)
                    && edge.getTarget().getSide() == PortSide.WEST
                    ) {

                // pull left
                int gap = target.getLayer().getIndex() - start.getLayer().getIndex();
                if (gap > 1) {

                    int newIndex = 0;
                    if (left) {
                        newIndex = start.getLayer().getIndex() + 1;
                    } else {
                        newIndex = target.getLayer().getIndex() - 1;
                    }

                    assert newIndex >= 0;
                    assert newIndex < layeredGraph.getLayers().size();

                    Layer newLayer = layeredGraph.getLayers().get(newIndex);
                    target.setLayer(newLayer);
                }

                // call recursively
                condenseBigNodesChain(target, left);
            }
        }

        return start;
    }

    /**
     * @return true, if the {@link LayeredOptions#BIG_NODE_INITIAL} property is set and an
     *         {@link LayeredOptions#ORIGIN} is set.
     */
    private boolean isInitialBigNode(final LNode node) {
        return (node.getProperty(InternalProperties.BIG_NODE_INITIAL))
                && (node.getProperty(InternalProperties.ORIGIN) != null);
    }
}
