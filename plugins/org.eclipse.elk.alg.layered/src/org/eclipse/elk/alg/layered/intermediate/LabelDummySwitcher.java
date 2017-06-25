/*******************************************************************************
 * Copyright (c) 2012, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.List;
import java.util.function.Function;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.CenterEdgeLabelPlacementStrategy;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.options.PortType;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.Pair;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * Processor that tries to move label dummy nodes into an "optimal" layer their long edges crosses. This
 * is done by switching the order of long edge dummies and label dummies. There are
 * {@link CenterEdgeLabelPlacementStrategy different strategies} available to determine the "optimal" layer.
 * 
 * <p>
 * If this is the only thing we did we could end up in situations where multiple edges forming a
 * hyperedge are merged such that it's not clear anymore which edge label belongs to which edge:
 * </p>
 * <pre>
 *       An edge label
 *    -------------------+-----------+-------------------------- - - -
 *                       |           |
 *    -------------------+           +-------------------------- - - -
 *                                       Another edge label
 * </pre>
 * <p>
 * We solve this by making sure that the long edge dummies preceding a label dummy node have their
 * {@link InternalProperties#LONG_EDGE_TARGET} property set to {@code null} (we more or less view the
 * label dummy as the new target of those long edge dummies). The same is true for the
 * {@link InternalProperties#LONG_EDGE_SOURCE} property of long edge dummies succeeding a label dummy.
 * </p>
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>a properly layered graph</dd>
 *     <dd>center edge labels are represented by center label dummy nodes</dd>
 *     <dd>each label dummy and long edge dummy has exactly one incoming and one outgoing edge</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>center label dummy nodes are the centermost dummies of a long edge</dd>
 *     <dd>the {@link InternalProperties#LONG_EDGE_TARGET} property of long edge dummies preceding center
 *         edge label dummies are set to {@code null} to prevent incorrect hyperedge dummy merging; the
 *         same is true for the {@link InternalProperties#LONG_EDGE_SOURCE} property of long edge dummies
 *         succeeding center edge label dummies.
 *   <dt>Slots:</dt>
 *     <dd>Before phase 3.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link LongEdgeSplitter}</dd>
 * </dl>
 * 
 * @see CenterEdgeLabelPlacementStrategy
 * @author jjc
 * @author cds
 * @kieler.rating yellow proposed cds
 */
public final class LabelDummySwitcher implements ILayoutProcessor<LGraph> {
    
    /** Width of all layers. */
    private double[] layerWidths;

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Label dummy switching", 1);

        // List of label dummies we encounter
        List<LNode> labelDummies = Lists.newArrayList();
        // List of label dummies and long edge dummies that need to be swapped
        List<Pair<LNode, LNode>> nodesToSwap = Lists.newArrayList();

        // Iterate over the graph and gather all label dummies that need to be swapped
        List<LNode> leftLongEdgeDummies = Lists.newArrayList();
        List<LNode> rightLongEdgeDummies = Lists.newArrayList();

        CenterEdgeLabelPlacementStrategy strategy =
                layeredGraph.getProperty(LayeredOptions.EDGE_LABELS_CENTER_LABEL_PLACEMENT_STRATEGY);
        if (strategy == CenterEdgeLabelPlacementStrategy.WIDEST_LAYER) {
            // Gather all layer widths and setup layer IDs for array indexing
            final List<Layer> layers = layeredGraph.getLayers();
            layerWidths = new double[layers.size()];
            
            int layerIndex = 0;
            for (Layer layer : layers) {
                layerWidths[layerIndex] = LGraphUtil.findMaxNonDummyNodeWidth(layer, false);
                layer.id = layerIndex;
            }
        }

        for (Layer layer : layeredGraph) {
            for (LNode node : layer.getNodes()) {
                if (node.getType() == NodeType.LABEL) {
                    labelDummies.add(node);
                    leftLongEdgeDummies.clear();
                    rightLongEdgeDummies.clear();

                    // Gather long edge dummies left of the label dummy
                    LNode source = node;
                    do {
                        source = source.getIncomingEdges().iterator().next().getSource().getNode();
                        if (source.getType() == NodeType.LONG_EDGE) {
                            leftLongEdgeDummies.add(source);
                        }
                    } while (source.getType() == NodeType.LONG_EDGE);

                    // Gather long edge dummies right of the label dummy
                    LNode target = node;
                    do {
                        target = target.getOutgoingEdges().iterator().next().getTarget().getNode();
                        if (target.getType() == NodeType.LONG_EDGE) {
                            rightLongEdgeDummies.add(target);
                        }
                    } while (target.getType() == NodeType.LONG_EDGE);

                    // Determine the nodes to swap according to the given strategy
                    switch (strategy) {
                    case WIDEST_LAYER:
                        findSwapCandidateForWidestLayer(node, leftLongEdgeDummies,
                                rightLongEdgeDummies, nodesToSwap);
                        break;
                    case MEDIAN_LAYER:
                    default:
                        findSwapCandidateCenter(node, leftLongEdgeDummies, rightLongEdgeDummies,
                                nodesToSwap);
                        break;
                    }
                }
            }
        }

        // Execute the swapping and reset long edge source / target information
        for (Pair<LNode, LNode> swapPair : nodesToSwap) {
            swapNodes(swapPair.getFirst(), swapPair.getSecond());
        }
        for (LNode labelDummy : labelDummies) {
            updateLongEdgeSourceLabelDummyInfo(labelDummy);
        }

        layerWidths = null;
        monitor.done();
    }

    /**
     * Find a long edge dummy and add it to the list of nodes to swap to move the given node in the
     * widest layer the edge crosses.
     * 
     * @param node
     *            the dummy node to move.
     * @param leftLongEdgeDummies
     *            long edge dummies to the left.
     * @param rightLongEdgeDummies
     *            long edge dummies to the right.
     * @param nodesToSwap
     *            list of nodes to swap.
     */
    private void findSwapCandidateForWidestLayer(final LNode node,
            final List<LNode> leftLongEdgeDummies, final List<LNode> rightLongEdgeDummies,
            final List<Pair<LNode, LNode>> nodesToSwap) {

        // Find the widest layer and the corresponding swap candidate
        double maxWidth = 0.0;
        LNode swapCandidate = null;
        for (LNode dummy : Iterables.concat(rightLongEdgeDummies, leftLongEdgeDummies)) {
            double width = layerWidths[dummy.getLayer().id];
            if (width > maxWidth) {
                maxWidth = width;
                swapCandidate = dummy;
            }
        }

        if (swapCandidate != null) {
            nodesToSwap.add(new Pair<LNode, LNode>(node, swapCandidate));
        }
    }

    /**
     * Find a long edge dummy and add it to the list of nodes to swap to move the given node in the
     * center layer the edge crosses.
     * 
     * @param node
     *            the dummy node to move.
     * @param leftLongEdgeDummies
     *            long edge dummies to the left.
     * @param rightLongEdgeDummies
     *            long edge dummies to the right.
     * @param nodesToSwap
     *            list of nodes to swap.
     */
    private void findSwapCandidateCenter(final LNode node,
            final List<LNode> leftLongEdgeDummies, final List<LNode> rightLongEdgeDummies,
            final List<Pair<LNode, LNode>> nodesToSwap) {

        // Check whether the label dummy should be switched
        int leftSize = leftLongEdgeDummies.size();
        int rightSize = rightLongEdgeDummies.size();

        if (leftSize > rightSize + 1) {
            int pos = (leftSize + rightSize) / 2;
            nodesToSwap.add(new Pair<LNode, LNode>(node, leftLongEdgeDummies.get(pos)));
        } else if (rightSize > leftSize + 1) {
            int pos = (rightSize - leftSize) / 2 - 1;
            nodesToSwap.add(new Pair<LNode, LNode>(node, rightLongEdgeDummies.get(pos)));
        }
    }

    /**
     * Swaps the two given dummy nodes.
     * 
     * @param dummy1 the first dummy node.
     * @param dummy2 the second dummy node.
     */
    private void swapNodes(final LNode dummy1, final LNode dummy2) {
        Layer layer1 = dummy1.getLayer();
        Layer layer2 = dummy2.getLayer();
        
        // Detect incoming and outgoing ports of the nodes
        LPort inputPort1 = dummy1.getPorts(PortType.INPUT).iterator().next();
        LPort outputPort1 = dummy1.getPorts(PortType.OUTPUT).iterator().next();
        LPort inputPort2 = dummy2.getPorts(PortType.INPUT).iterator().next();
        LPort outputPort2 = dummy2.getPorts(PortType.OUTPUT).iterator().next();
        
        // Store incoming and outgoing edges
        LEdge[] incomingEdges1 = inputPort1.getIncomingEdges().toArray(new LEdge[1]);
        LEdge[] outgoingEdges1 = outputPort1.getOutgoingEdges().toArray(new LEdge[1]);
        LEdge[] incomingEdges2 = inputPort2.getIncomingEdges().toArray(new LEdge[1]);
        LEdge[] outgoingEdges2 = outputPort2.getOutgoingEdges().toArray(new LEdge[1]);

        // Set values of first node to values from second node
        dummy1.setLayer(layer2);
        for (LEdge edge : incomingEdges2) {
            edge.setTarget(inputPort1);
        }
        for (LEdge edge : outgoingEdges2) {
            edge.setSource(outputPort1);
        }

        // Set values of first node to values from second node
        dummy2.setLayer(layer1);
        for (LEdge edge : incomingEdges1) {
            edge.setTarget(inputPort2);
        }
        for (LEdge edge : outgoingEdges1) {
            edge.setSource(outputPort2);
        }
    }
    
    /**
     * Updates the {@link InternalProperties#LONG_EDGE_BEFORE_LABEL_DUMMY} property of long edge dummy
     * nodes preceding and succeeding the given label dummy node.
     * 
     * @param labelDummy the label dummy node.
     */
    private void updateLongEdgeSourceLabelDummyInfo(final LNode labelDummy) {
        // Predecessors
        doUpdateLongEdgeLabelDummyInfo(
                labelDummy,
                node -> node.getIncomingEdges().iterator().next().getSource().getNode(),
                true);
        
        // We may want to do things to the successors as well at some point
    }
    
    /**
     * Does the actual work of setting the long edge source or target of all nodes before or after
     * the given label dummy node to {@code null}.
     * 
     * @param labelDummy
     *            the label dummy node to start from.
     * @param nextElement
     *            a function that, given a node, returns the node to process next. Use this to
     *            decide whether to target all successors or all predecessors of the label dummy
     *            node.
     * @param value
     *            the new property value.
     */
    private void doUpdateLongEdgeLabelDummyInfo(final LNode labelDummy,
            final Function<LNode, LNode> nextElement, final boolean value) {
        
        LNode longEdgeDummy = nextElement.apply(labelDummy);
        while (longEdgeDummy.getType() == NodeType.LONG_EDGE) {
            longEdgeDummy.setProperty(InternalProperties.LONG_EDGE_BEFORE_LABEL_DUMMY, value);
            longEdgeDummy = nextElement.apply(longEdgeDummy);
        }
    }
    
}

