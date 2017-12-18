/*******************************************************************************
 * Copyright (c) 2012, 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.CenterEdgeLabelPlacementStrategy;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.options.PortType;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.options.Alignment;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.nodespacing.NodeLabelAndSizeCalculator;

import com.google.common.collect.Lists;


/**
 * Processor that tries to move label dummy nodes into an "optimal" layer their long edges crosses. This
 * is done by switching the order of long edge dummies and label dummies. There are
 * {@link CenterEdgeLabelPlacementStrategy different strategies} available to determine the target layer.
 * 
 * <p>
 * If this was the only thing we did we could end up in situations where multiple edges forming a
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
 *     <dd>Before phase 4.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link NodeLabelAndSizeCalculator}</dd>
 *     <dd>{@link NodeMarginCalculator}</dd>
 *     <dd>{@link EndLabelPreprocessor}</dd>
 * </dl>
 * 
 * @see CenterEdgeLabelPlacementStrategy
 */
public final class LabelDummySwitcher implements ILayoutProcessor<LGraph> {
    
    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Label dummy switching", 1);
        
        // The basic placement strategy to be assumed if no more specific strategy is
        CenterEdgeLabelPlacementStrategy placementStrategy =
                layeredGraph.getProperty(LayeredOptions.EDGE_LABELS_CENTER_LABEL_PLACEMENT_STRATEGY);
        
        // Gather all label dummies in the graph and calculate layer width info, if required
        List<LabelDummyInfo> labelDummyInfos = gatherLabelDummyInfos(layeredGraph, placementStrategy);
        double[] layerWidths = placementStrategy.usesLabelSizeInformation()
                ? calculateLayerWidthsAndAssignIDsToLayers(layeredGraph)
                : null;
        
        for (LabelDummyInfo labelDummyInfo : labelDummyInfos) {
            // Find the swap candidate, depending on the strategy to be used
            LNode swapCandidate = null;
            
            switch (labelDummyInfo.placementStrategy) {
            case CENTER_LAYER:
                swapCandidate = findCenterLayerSwapCandidate(labelDummyInfo, layerWidths);
                break;
            
            case MEDIAN_LAYER:
                swapCandidate = findMedianLayerSwapCandidate(labelDummyInfo);
                break;
                
            case WIDEST_LAYER:
                swapCandidate = findWidestLayerSwapCandidate(labelDummyInfo, layerWidths);
                break;
            
            case HEAD_LAYER:
                setEndLayerNodeAlignment(labelDummyInfo);
                swapCandidate = findEndLayerSwapCandidate(labelDummyInfo, true);
                break;
                
            case TAIL_LAYER:
                setEndLayerNodeAlignment(labelDummyInfo);
                swapCandidate = findEndLayerSwapCandidate(labelDummyInfo, false);
                break;
            }
            
            // If we have a swap candidate, swap the two
            if (swapCandidate != null) {
                swapNodes(labelDummyInfo.labelDummy, swapCandidate);
            }
            
            updateLongEdgeSourceLabelDummyInfo(labelDummyInfo);
        }
        
        monitor.done();
    }

    /**
     * Returns an array containing the width of all layers, indexed by layer ID which is assigned to the layers by this
     * method as well.
     */
    private double[] calculateLayerWidthsAndAssignIDsToLayers(final LGraph layeredGraph) {
        final List<Layer> layers = layeredGraph.getLayers();
        double[] layerWidths = new double[layers.size()];
        
        int layerIndex = 0;
        for (Layer layer : layers) {
            layerWidths[layerIndex] = LGraphUtil.findMaxNonDummyNodeWidth(layer, false);
            layer.id = layerIndex;
            layerIndex++;
        }
        
        return layerWidths;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Center Layer

    /**
     * Find a long edge dummy to swap the label dummy with. This method bases its decision on which layer it thinks
     * will be closest to the edge's physical center later.
     * 
     * @param labelDummyInfo
     *            information object of the label dummy to find a swap candidate for.
     * @param layerWidths
     *            widths of layers, indexed by layer IDs.
     * @return long edge dummy to swap the label dummy with or {@code null}.
     */
    private LNode findCenterLayerSwapCandidate(final LabelDummyInfo labelDummyInfo, final double[] layerWidths) {
        // Sum up the widths of all the layers this thing spans
        double[] layerWidthSums = computeLayerWidthSums(labelDummyInfo, layerWidths);
        
        // Find the first layer that exceeds half the width
        double threshold = layerWidthSums[layerWidthSums.length - 1] / 2;
        
        for (int i = 0; i < layerWidthSums.length; i++) {
            if (layerWidthSums[i] >= threshold) {
                return labelDummyInfo.ithLongEdgeDummyOrNull(i);
            }
        }
        
        // This should actually not happen
        assert false;
        return null;
    }
    
    /**
     * Computes an array in which entry i refers to the combined width of layers [0, i]. The combined width is
     * estimated based on the currently estimated layer width and a wild guess as to the layer spacing.
     */
    private double[] computeLayerWidthSums(final LabelDummyInfo labelDummyInfo, final double[] layerWidths) {
        // The minimum space that we think will be left between 
        LGraph lgraph = labelDummyInfo.labelDummy.getGraph();
        double edgeNodeSpacing = lgraph.getProperty(LayeredOptions.SPACING_EDGE_NODE_BETWEEN_LAYERS) * 2;
        double nodeNodeSpacing = lgraph.getProperty(LayeredOptions.SPACING_NODE_NODE_BETWEEN_LAYERS);
        double minSpaceBetweenLayers = Math.max(edgeNodeSpacing, nodeNodeSpacing);
        
        // The array that will hold the accumulated widths
        int layerCount = labelDummyInfo.leftLongEdgeDummies.size() + labelDummyInfo.rightLongEdgeDummies.size() + 1;
        double[] layerWidthSums = new double[layerCount];
        
        double currentWidthSum = -minSpaceBetweenLayers;
        int currentIndex = 0;
        
        for (LNode leftDummy : labelDummyInfo.leftLongEdgeDummies) {
            currentWidthSum += layerWidths[leftDummy.getLayer().id] + minSpaceBetweenLayers;
            layerWidthSums[currentIndex++] = currentWidthSum;
        }
        
        currentWidthSum += layerWidths[labelDummyInfo.labelDummy.getLayer().id] + minSpaceBetweenLayers;
        layerWidthSums[currentIndex++] = currentWidthSum;
        
        for (LNode rightDummy : labelDummyInfo.rightLongEdgeDummies) {
            currentWidthSum += layerWidths[rightDummy.getLayer().id] + minSpaceBetweenLayers;
            layerWidthSums[currentIndex++] = currentWidthSum;
        }
        
        return layerWidthSums;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Median Layer

    /**
     * Find a long edge dummy to swap the label dummy with. This method bases its decision on which long edge dummy is
     * in the median layer of the layers the long edge spans.
     * 
     * @param labelDummyInfo
     *            information object of the label dummy to find a swap candidate for.
     * @return long edge dummy to swap the label dummy with or {@code null}.
     */
    private LNode findMedianLayerSwapCandidate(final LabelDummyInfo labelDummyInfo) {
        // Find the median of the layers spanned by the long edge this label dummy is part of
        int layers = labelDummyInfo.leftLongEdgeDummies.size() + labelDummyInfo.rightLongEdgeDummies.size() + 1;
        int lowerMedian = (layers - 1) / 2;
        
        return labelDummyInfo.ithLongEdgeDummyOrNull(lowerMedian);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Widest Layer

    /**
     * Find a long edge dummy to swap the label dummy with. This method bases its decision on which long edge dummy is
     * in the widest layer.
     * 
     * @param labelDummyInfo
     *            information object of the label dummy to find a swap candidate for.
     * @param layerWidths
     *            widths of layers, indexed by layer IDs.
     * @return long edge dummy to swap the label dummy with or {@code null}.
     */
    private LNode findWidestLayerSwapCandidate(final LabelDummyInfo labelDummyInfo, final double[] layerWidths) {
        // Find the widest layer among those the long edge dummies are placed in
        Optional<LNode> widestLayerDummy = Stream
                .concat(labelDummyInfo.leftLongEdgeDummies.stream(), labelDummyInfo.rightLongEdgeDummies.stream())
                .max((node1, node2) -> Double.compare(
                        layerWidths[node1.getLayer().id],
                        layerWidths[node2.getLayer().id]));
        
        if (widestLayerDummy.isPresent()) {
            // We need to check with the width of the label dummy's current layer as well
            double labelDummyLayerWidth = layerWidths[labelDummyInfo.labelDummy.getLayer().id];
            double widestLayerWidth = layerWidths[widestLayerDummy.get().getLayer().id];
            
            if (widestLayerWidth > labelDummyLayerWidth) {
                return widestLayerDummy.get();
            }
        }
        
        // We don't have a swap candidate
        return null;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // End Layer

    /**
     * Find a long edge dummy to swap the label dummy with. This method simply returns the dummy node closest to the
     * original edge's head or tail. It does take into account whether the edge is reversed or not.
     * 
     * @param labelDummyInfo
     *            information object of the label dummy to find a swap candidate for.
     * @param headLayer
     *            {@code true} if the dummy node closest to the edge's head should be returned, {@code false} if the
     *            dummy node closes to the edge's tail should be returned.
     * @return long edge dummy to swap the label dummy with or {@code null}.
     */
    private LNode findEndLayerSwapCandidate(final LabelDummyInfo labelDummyInfo, final boolean headLayer) {
        boolean reversed = isPartOfReversedEdge(labelDummyInfo);
        
        if ((headLayer && !reversed) || (!headLayer && reversed)) {
            // If we already are in the head layer, don't bother
            if (labelDummyInfo.rightLongEdgeDummies.isEmpty()) {
                return null;
            } else {
                return labelDummyInfo.rightLongEdgeDummies.get(labelDummyInfo.rightLongEdgeDummies.size() - 1);
            }
        } else {
            // If we already are in the tail layer, don't bother
            if (labelDummyInfo.leftLongEdgeDummies.isEmpty()) {
                return null;
            } else {
                return labelDummyInfo.leftLongEdgeDummies.get(0);
            }
        }
    }
    
    /**
     * Sets the alignment property for the given label dummy. Its placement strategy is assumed to be one of the end
     * strategies.
     */
    private void setEndLayerNodeAlignment(final LabelDummyInfo labelDummyInfo) {
        assert labelDummyInfo.placementStrategy == CenterEdgeLabelPlacementStrategy.HEAD_LAYER
                || labelDummyInfo.placementStrategy == CenterEdgeLabelPlacementStrategy.TAIL_LAYER;
        
        boolean isHeadLabel = labelDummyInfo.placementStrategy == CenterEdgeLabelPlacementStrategy.HEAD_LAYER;
        boolean isPartOfReversedEdge = isPartOfReversedEdge(labelDummyInfo);
        
        if ((isHeadLabel && !isPartOfReversedEdge) || (!isHeadLabel && isPartOfReversedEdge)) {
            labelDummyInfo.labelDummy.setProperty(LayeredOptions.ALIGNMENT, Alignment.RIGHT);
        } else {
            labelDummyInfo.labelDummy.setProperty(LayeredOptions.ALIGNMENT, Alignment.LEFT);
        }
    }
    
    /**
     * Checks if the given label dummy node is part of a reversed edge.
     */
    private boolean isPartOfReversedEdge(final LabelDummyInfo labelDummyInfo) {
        assert labelDummyInfo.labelDummy.getType() == NodeType.LABEL;
        assert labelDummyInfo.labelDummy.getIncomingEdges().iterator().hasNext();
        assert labelDummyInfo.labelDummy.getOutgoingEdges().iterator().hasNext();
        
        // Find incoming and outgoing edge
        LEdge incoming = labelDummyInfo.labelDummy.getIncomingEdges().iterator().next();
        LEdge outgoing = labelDummyInfo.labelDummy.getOutgoingEdges().iterator().next();
        
        return incoming.getProperty(InternalProperties.REVERSED) || outgoing.getProperty(InternalProperties.REVERSED);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Swapping Utilities

    /**
     * Swaps the two given dummy nodes. The nodes are assumed to be part of the same long edge.
     */
    private void swapNodes(final LNode dummy1, final LNode dummy2) {
        // Find the layers and the positions inside the layers of the dummy nodes. We need the positions later since
        // we run after crossing minimization and have to keep the order of nodes the same. An alternative for this
        // method would be not to change the layers and connections of the two nodes but to switch all of their
        // properties instead, but we reckon that might be more work
        Layer layer1 = dummy1.getLayer();
        Layer layer2 = dummy2.getLayer();
        
        int dummy1LayerPosition = layer1.getNodes().indexOf(dummy1);
        int dummy2LayerPosition = layer2.getNodes().indexOf(dummy2);
        
        // Detect incoming and outgoing ports of the nodes (this of course assumes that there's just one of each kind,
        // which should be true for long edge and label dummy nodes)
        LPort inputPort1 = dummy1.getPorts(PortType.INPUT).iterator().next();
        LPort outputPort1 = dummy1.getPorts(PortType.OUTPUT).iterator().next();
        LPort inputPort2 = dummy2.getPorts(PortType.INPUT).iterator().next();
        LPort outputPort2 = dummy2.getPorts(PortType.OUTPUT).iterator().next();
        
        // Store incoming and outgoing edges
        LEdge[] incomingEdges1 = inputPort1.getIncomingEdges().toArray(new LEdge[1]);
        LEdge[] outgoingEdges1 = outputPort1.getOutgoingEdges().toArray(new LEdge[1]);
        LEdge[] incomingEdges2 = inputPort2.getIncomingEdges().toArray(new LEdge[1]);
        LEdge[] outgoingEdges2 = outputPort2.getOutgoingEdges().toArray(new LEdge[1]);

        // Put first dummy into second dummy's layer and reroute second dummy's edges to first dummy
        dummy1.setLayer(dummy2LayerPosition, layer2);
        for (LEdge edge : incomingEdges2) {
            edge.setTarget(inputPort1);
        }
        for (LEdge edge : outgoingEdges2) {
            edge.setSource(outputPort1);
        }

        // Put second dummy into first dummy's layer and reroute first dummy's edges to second dummy
        dummy2.setLayer(dummy1LayerPosition, layer1);
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
     */
    private void updateLongEdgeSourceLabelDummyInfo(final LabelDummyInfo labelDummyInfo) {
        // Predecessors
        doUpdateLongEdgeLabelDummyInfo(
                labelDummyInfo.labelDummy,
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
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Dummy Gathering Utilities
    
    /**
     * Turns the graph's label dummies into a list of {@link LabelDummyInfo} objects.
     */
    private List<LabelDummyInfo> gatherLabelDummyInfos(final LGraph layeredGraph,
            final CenterEdgeLabelPlacementStrategy defaultPlacementStrategy) {
        
        // From the graph's nodes, filter out the label dummies and return them in the form of a list
        return layeredGraph.getLayers().stream()
                .flatMap((layer) -> layer.getNodes().stream())
                .filter(node -> node.getType() == NodeType.LABEL)
                .map(labelDummy -> new LabelDummyInfo(labelDummy, defaultPlacementStrategy))
                .collect(Collectors.toList());
    }
    
    /**
     * Encapsulates information about a label dummy along with the long edge dummies to its left and to its right.
     */
    private static final class LabelDummyInfo {
        
        /** The label dummy node. */
        private LNode labelDummy;
        /** The label placement strategy to be used with this label. */
        private CenterEdgeLabelPlacementStrategy placementStrategy = null;
        /** The long edge dummies to the left of the label dummy. May well be empty. */
        private List<LNode> leftLongEdgeDummies = new ArrayList<>();
        /** The long edge dummies to the right of the label dummy. May well be empty. */
        private List<LNode> rightLongEdgeDummies = new ArrayList<>();
        
        /**
         * Create a new instance to represent the given label dummy. Automatically collects long edge dummies to its
         * left and to its right.
         * 
         * @param labelDummy
         *            the label dummy.
         */
        private LabelDummyInfo(final LNode labelDummy,
                final CenterEdgeLabelPlacementStrategy defaultPlacementStrategy) {
            
            this.labelDummy = labelDummy;
            this.placementStrategy = defaultPlacementStrategy;
            
            gatherLeftLongEdgeDummies();
            gatherRightLongEdgeDummies();

            // Check if the label wants to deviate from the default placement strategy
            for (LLabel label : labelDummy.getProperty(InternalProperties.REPRESENTED_LABELS)) {
                // Take the first override we can find
                if (label.hasProperty(LayeredOptions.EDGE_LABELS_CENTER_LABEL_PLACEMENT_STRATEGY)) {
                    placementStrategy = label.getProperty(LayeredOptions.EDGE_LABELS_CENTER_LABEL_PLACEMENT_STRATEGY);
                    break;
                }
            }
        }
        
        /**
         * Gathers the long edge dummies left of the label dummy which are part of its long edge. The order they are
         * in reflects the order of layers, that is, the label dummy is the successor of the last long edge dummy in
         * the list.
         */
        private void gatherLeftLongEdgeDummies() {
            LNode source = labelDummy;
            do {
                source = source.getIncomingEdges().iterator().next().getSource().getNode();
                if (source.getType() == NodeType.LONG_EDGE) {
                    leftLongEdgeDummies.add(source);
                }
            } while (source.getType() == NodeType.LONG_EDGE);
            
            // The list is currently not in the order we would expect, so produce a reversed version
            leftLongEdgeDummies = Lists.reverse(leftLongEdgeDummies);
        }
        
        /**
         * Gathers the long edge dummies right of the label dummy which are part of its long edge. The order they are
         * in reflects the order of layers, that is, the label dummy is the predecessor of the first long edge dummy in
         * the list.
         */
        private void gatherRightLongEdgeDummies() {
            LNode target = labelDummy;
            do {
                target = target.getOutgoingEdges().iterator().next().getTarget().getNode();
                if (target.getType() == NodeType.LONG_EDGE) {
                    rightLongEdgeDummies.add(target);
                }
            } while (target.getType() == NodeType.LONG_EDGE);
        }
        
        /**
         * Returns the i-th dummy node from the edge through the long edge dummies and the label dummy. If the label
         * dummy itself would be returned, this method instead returns {@code null} since that usually indicates that
         * no swapping needs to be done.
         */
        private LNode ithLongEdgeDummyOrNull(final int i) {
            if (i < leftLongEdgeDummies.size()) {
                // The label dummy is not in the desired layer, but one of the left long edge dummies is
                return leftLongEdgeDummies.get(i);
            } else if (i > leftLongEdgeDummies.size()) {
                // The label dummy is not in the desired layer, but one of the right long edge dummies is
                return rightLongEdgeDummies.get(i - leftLongEdgeDummies.size() - 1);
            } else {
                return null;
            }
        }
    }
    
}
