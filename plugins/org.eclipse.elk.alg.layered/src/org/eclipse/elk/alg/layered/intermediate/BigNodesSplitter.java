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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.Pair;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * This processor splits bignode, i.e. nodes that are wider than most of the diagram's nodes. The
 * processor is executed after crossing minimization restricting its power to a small number of
 * scenarios, as described below. However, since the crossing minimization is finished it can
 * guarantee not to introduce any node-edge crossings. This would not be possible otherwise.
 * 
 * <h1>Long Edge Dummies</h1>
 * 
 * <h2>BigNode With Incoming Long Edge</h2>
 * <p>
 *      We replace as many long edge dummies in front of the big node as possible. The long edge dummies
 *      are only allowed to have exactly one incoming and one outgoing edge, otherwise we leave them as
 *      they are. Depending on the number of dummies we could replace we distribute the original size of
 *      the bignode among the newly created bignode dummies. Special care has to be taken with relation
 *      to the port's positions.
 * </p>
 * 
 * 
 * <h2>BigNode With Outgoing Long Edge</h2>
 * <p>
 *      We replace as many long edge dummies following the big node as possible. All dummies are 
 *      only allowed to have exactly on incoming and outgoing edge. Depending on the number of 
 *      replaced dummies we adapt the width of each replaced dummy to be a fraction of the
 *      original bignode's width. 
 * </p>
 * 
 * <h1>Without In-/Outgoing Edges</h1>
 * 
 * <h2>BigNode Without Outgoing Edge</h2>
 * <p>
 *      We add new bignode dummies in the consecutive layers of the original bignode. While doing this,
 *      we assure that the dummies are placed at a position within a consecutive layer such that
 *      no node-edge crossing is introduced. If this is not possible, we retain the original bignode 
 *      without splitting it. The original number of layers is not increased, i.e. a bignode in the
 *      right-most layer will never be split. 
 * </p>
 * 
 * <h2>BigNode Without Incoming Edge</h2>
 * <p>
 *      Symmetric case as above for bignodes that have no incoming edges. Big nodes in the left-most 
 *      layer are not split any further. 
 * </p>
 * 
 * <h3>Remarks</h3>
 * <p>
 *      Care has to be taken with nodes of the type {@link NodeType#NORTH_SOUTH_PORT}. These are not
 *      connected by edges but are kept edge crossing free by constraints. Hence we are not allowed to
 *      place bignode dummies between such a node and its corresponding normal node.
 * </p>
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>The crossing minimization phase is finished.</dd>
 *     <dd>LongEdge dummies are merged where possible.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>Bignodes are split where possible.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 4.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>After {@link HyperedgeDummyMerger}</dd>
 *     <dd>Before {@link LabelAndNodeSizeProcessor}</dd>
 *     <dd>Care about the results of the {@link InLayerConstraintProcessor}.</dd>
 * </dl>
 * 
 * 
 * @see BigNodesPostProcessor
 * 
 * @author uru
 */
public class BigNodesSplitter implements ILayoutProcessor<LGraph> {

    /**
     * Describes the type of a certain bignode. 
     */
    private enum BigNodeType {
        NO_INCOMING, NO_OUTGOING, INC_LONG_EDGE, OUT_LONG_EDGE, INVALID
    }

    /**
     * Minimal width into which nodes are split, the smaller this value, the more big node dummy
     * nodes are possibly introduced.
     */
    private static final double MIN_WIDTH = 50;
    /** The current graph. */
    private LGraph layeredGraph;
    /** Used to assign ids to newly created dummy nodes. */
    private int dummyID = 0;
    /** Currently used node spacing. */
    private double spacing = 0;
    /** Current layout direction. */
    private Direction direction = Direction.UNDEFINED;

    @Override
    public void process(final LGraph theLayeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Big nodes pre-processing", 1);
        
        this.layeredGraph = theLayeredGraph;
        List<LNode> nodes = Lists.newArrayList();
        for (Layer l : theLayeredGraph.getLayers()) {
            nodes.addAll(l.getNodes());
        }

        // re-index nodes
        int counter = 0;
        for (LNode node : nodes) {
            node.id = counter++;
        }

        // the object spacing in the drawn graph
        spacing = layeredGraph.getProperty(LayeredOptions.SPACING_NODE_NODE).doubleValue();
        direction = layeredGraph.getProperty(LayeredOptions.DIRECTION);
        // the ID for the most recently created dummy node
        dummyID = nodes.size();

        // Compute width of narrowest node
        double minWidth = Float.MAX_VALUE;
        for (LNode node : nodes) {
            // ignore all dummy nodes
            if ((node.getType() == NodeType.NORMAL)
                    && (node.getSize().x < minWidth)) {
                minWidth = node.getSize().x;
            }
        }

        // assure a capped minimal width
        minWidth = Math.max(MIN_WIDTH, minWidth);

        // collect all nodes that are considered "big"
        List<BigNode> bigNodes = Lists.newArrayList();
        double threshold = (minWidth + spacing);
        for (LNode node : nodes) {
            if ((node.getType() == NodeType.NORMAL) && (node.getSize().x > threshold)) {
                // when splitting, consider that we can use the spacing area
                // we try to find a node width that considers the spacing
                // for every dummy node to be created despite the last one
                int parts = 1;
                double chunkWidth = node.getSize().x;
                while (chunkWidth > minWidth) {
                    parts++;
                    chunkWidth = (node.getSize().x - (parts - 1) * spacing) / (double) parts;
                }

                // new
                bigNodes.add(new BigNode(node, parts));
            }
        }

        // handle each big node
        for (BigNode node : bigNodes) {
            // is this big node ok?
            if (isProcessorApplicable(node)) {
                node.process(monitor);
            }
        }

        monitor.done();
    }

    /**
     * Only handle nodes with {@link PortConstraints} <= {@link PortConstraints#FIXED_ORDER}, or for
     * greater port constraints we demand that the node has no NORTH and SOUTH ports.
     * 
     * Also, we do not support self-loops at the moment.
     * 
     * @param node
     * @return true if we can apply big nodes processing
     */
    private boolean isProcessorApplicable(final BigNode node) {

        if (node.node.getProperty(LayeredOptions.PORT_CONSTRAINTS) == PortConstraints.FIXED_RATIO
                || node.node.getProperty(LayeredOptions.PORT_CONSTRAINTS) == PortConstraints.FIXED_POS) {
            for (LPort port : node.node.getPorts()) {
                if (port.getSide() == PortSide.NORTH || port.getSide() == PortSide.SOUTH) {
                    return false;
                }
            }
        }
        
        // we don't support self-loops
        for (LEdge edge : node.node.getOutgoingEdges()) {
            if (edge.getSource().getNode().equals(edge.getTarget().getNode())) {
                return false;
            }
        }

        // edges that are connected to the left side of the node
        Iterable<LEdge> westwardEdges;
        // likewise edges connected to the right side of the node
        Iterable<LEdge> eastwardEdges;

        if (node.node.getProperty(LayeredOptions.PORT_CONSTRAINTS).isSideFixed()) {
            List<Iterable<LEdge>> tmp = Lists.newArrayList();
            for (LPort p : node.node.getPorts(PortSide.WEST)) {
                tmp.add(p.getConnectedEdges());
            }
            westwardEdges = Iterables.concat(tmp);

            tmp = Lists.newArrayList();
            for (LPort p : node.node.getPorts(PortSide.EAST)) {
                tmp.add(p.getConnectedEdges());
            }
            eastwardEdges = Iterables.concat(tmp);
        } else {
            // ports are free, thus ports are moved to appropriate sides
            westwardEdges = node.node.getIncomingEdges();
            eastwardEdges = node.node.getOutgoingEdges();
        }
        
        boolean hasOutgoing = !Iterables.isEmpty(node.node.getOutgoingEdges());
        boolean hasIncoming = !Iterables.isEmpty(node.node.getIncomingEdges());
        // the node has to be connected
        if (!hasOutgoing && !hasIncoming) {
            return false;
        }

        // or no outgoing edge
        if (!hasOutgoing) {
            node.type = BigNodeType.NO_OUTGOING;
            return true;
        }
        
        // or no outgoing edge
        if (!hasIncoming) {
            node.type = BigNodeType.NO_INCOMING;
            return true;
        }

        // either exactly one incoming edge that originates from a long edge dummy
        if (Iterables.size(westwardEdges) == 1) {
            LNode source = Iterables.get(westwardEdges, 0).getSource().getNode();
            if (source.getType() == NodeType.LONG_EDGE
            // and the long edge dummy does not represent a self loop
                    && !source.getProperty(InternalProperties.LONG_EDGE_SOURCE).getNode()
                            .equals(node.node)) {
                node.type = BigNodeType.INC_LONG_EDGE;
                return true;
            }
        }

        // on outgoing edge analog
        if (Iterables.size(eastwardEdges) == 1) {
            LNode target = Iterables.get(eastwardEdges, 0).getTarget().getNode();
            if (target.getType() == NodeType.LONG_EDGE
                    && !target.getProperty(InternalProperties.LONG_EDGE_TARGET).getNode()
                            .equals(node.node)) {
                node.type = BigNodeType.OUT_LONG_EDGE;
                return true;
            }
        }

        return false;
    }

    /** The direction within the layering into which to proceed in helper methods. */ 
    private static enum Dir { Left, Right };
    
    /**
     * Internal representation of a big node.
     * 
     * @author uru
     */
    private class BigNode {

        private LNode node;
        private int chunks;
        public BigNodeType type = BigNodeType.INVALID; // SUPPRESS CHECKSTYLE VisibilityModifier

        /** The dummy nodes created for this big node (include the node itself at index 0). */
        private ArrayList<LNode> dummies = Lists.newArrayList();

        /**
         * Creates a new big node.
         */
        BigNode(final LNode node, final int chunks) {
            this.node = node;
            this.chunks = chunks;
        }

        /**
         * Main entry point for big node processing.
         * 
         * - splits the big node into consecutive dummy nodes - handles labels
         * 
         */
        public void process(final IElkProgressMonitor monitor) {

            // remember east ports
            List<LPort> eastPorts = Lists.newArrayList();
            for (LPort port : node.getPorts()) {
                if (port.getSide() == PortSide.EAST) {
                    eastPorts.add(port);
                }
            }

            // if ports are free to be moved on the sides, we have to move all outgoing edges as
            // well as these will be assigned to the east side later
            if (direction == Direction.RIGHT
                    && !node.getProperty(LayeredOptions.PORT_CONSTRAINTS).isSideFixed()) {
                for (LEdge e : node.getOutgoingEdges()) {
                    eastPorts.add(e.getSource());
                }
            }

            // remember original size to restore it later
            node.setProperty(InternalProperties.BIG_NODE_ORIGINAL_SIZE, (float) node.getSize().x);
            node.setProperty(InternalProperties.BIG_NODE_INITIAL, true);

            // we consider the first node as dummy as well, even though we do not mark it
            dummies.add(node);

            /*
             * Handle depending on the big node type
             */
            Pair<Integer, Double> created = null;
            if (type == BigNodeType.NO_OUTGOING) {
                created = processNoOutgoingEdge(node, node.getLayer().getIndex(), node.getSize().x, monitor);
                
            } else if (type == BigNodeType.NO_INCOMING) {
                created = processNoIncomingEdge(node, node.getLayer().getIndex(), node.getSize().x, monitor);

            } else if (type == BigNodeType.OUT_LONG_EDGE) {
                created = processOutLongEdge(node, node.getSize().x);
                
            } else if (type == BigNodeType.INC_LONG_EDGE) {
                created = processIncLongEdge(node, node.getSize().x);
                
            }

            if (created != null) {
                // handle labels
                BigNodesLabelHandler.handle(node, dummies, created.getSecond());
            }
            
        }

        /*------------------------------------------------------------------------------------------
         *                      Big Node with an incoming Long Edge. 
         *------------------------------------------------------------------------------------------
         */

        private Pair<Integer, Double> processIncLongEdge(final LNode bignode,
                final double originalWidth) {

            // remember all nodes we create to adapt the size later on
            List<LNode> chainOfNodes = Lists.newArrayList();
            chainOfNodes.add(bignode);

            // create the dummies
            LNode start = bignode;
            int replacedDummies = 0;
            do {
                start = swapIncLongEdgeDummy(start);
                if (start != null) {
                    chainOfNodes.add(start);
                }
                replacedDummies++;
            } while (start != null);

            // assign a width to the nodes of the big node chain, care about spacing
            double newWidth =
                    (originalWidth - (chainOfNodes.size() - 1) * spacing)
                            / (double) chainOfNodes.size();
            for (LNode d : chainOfNodes) {
                d.getSize().x = newWidth;
            }
            return Pair.of(replacedDummies, newWidth);
        }
        
        private LNode swapIncLongEdgeDummy(final LNode start) {
            
            // we require exactly one incoming edge
            if (Iterables.size(start.getIncomingEdges()) != 1
                    || Iterables.get(start.getIncomingEdges(), 0).getSource().getNode()
                            .getType() != NodeType.LONG_EDGE) {
                return null;
            }
            
            // get the dummy
            LEdge incEdge = Iterables.get(start.getIncomingEdges(), 0);
            LNode longEdgeDummy = incEdge.getSource().getNode();
            longEdgeDummy.setType(NodeType.NORMAL);
            
            // the longedge dummy becomes the new initial bignode
            longEdgeDummy.setProperty(InternalProperties.LONG_EDGE_SOURCE, null);
            longEdgeDummy.setProperty(InternalProperties.LONG_EDGE_TARGET, null);

            longEdgeDummy.setProperty(InternalProperties.BIG_NODE_ORIGINAL_SIZE,
                    start.getProperty(InternalProperties.BIG_NODE_ORIGINAL_SIZE));
            longEdgeDummy.setProperty(InternalProperties.BIG_NODE_INITIAL, true);
            longEdgeDummy.setProperty(InternalProperties.ORIGIN,
                    start.getProperty(InternalProperties.ORIGIN));

            // adapt height
            longEdgeDummy.getSize().y = start.getSize().y;

            // the EAST ports can stay where they are, we have to adjust WEST ports here

            // adapt the origin
            // Remark: we allow the big node to have an arbitrary amount of
            // ports on the WEST side, however, only one of them is allowed
            // to have an incoming edge
            Object origin = incEdge.getTarget().getProperty(InternalProperties.ORIGIN);
            LPort outPort = null;
            for (LPort p : longEdgeDummy.getPorts(PortSide.WEST)) {
                if (!p.getIncomingEdges().isEmpty()) {
                    p.setProperty(InternalProperties.ORIGIN, origin);

                    LPort tgt = incEdge.getTarget();
                    p.getSize().x = tgt.getSize().x;
                    p.getSize().y = tgt.getSize().y;
                    p.getAnchor().x = tgt.getAnchor().x;
                    p.getAnchor().y = tgt.getAnchor().y;
                    
                    // copy the labels of the port
                    p.getLabels().addAll(tgt.getLabels());
                    tgt.getLabels().clear();

                    outPort = p;
                    break;
                }
            }
            incEdge.getTarget().setProperty(InternalProperties.ORIGIN, null);

            // if the big node has multiple EAST ports, reassemble this on the former long edge
            // dummy
            if (Iterables.size(start.getPorts(PortSide.WEST)) > 1) {
                // the port list is sorted here!
                for (LPort p : Lists.newLinkedList(start.getPorts(PortSide.WEST))) {
                    if (p.getIncomingEdges().isEmpty()) {

                        LPort newPort = new LPort();
                        newPort.setSide(PortSide.WEST);
                        newPort.getSize().x = p.getSize().x;
                        newPort.getSize().y = p.getSize().y;
                        newPort.setNode(longEdgeDummy);
                        newPort.setProperty(InternalProperties.ORIGIN,
                                p.getProperty(InternalProperties.ORIGIN));

                        p.setNode(null);
                    } else {
                        // re-add the original port to retain order
                        outPort.setNode(longEdgeDummy);
                    }
                }
            }
            
            // the original big node becomes a dummy
            start.setProperty(InternalProperties.ORIGIN, null);
            start.setProperty(InternalProperties.BIG_NODE_INITIAL, false);
            start.setType(NodeType.BIG_NODE);
            
            longEdgeDummy.setProperty(LayeredOptions.PORT_CONSTRAINTS,
                    start.getProperty(LayeredOptions.PORT_CONSTRAINTS));
            longEdgeDummy.setProperty(LayeredOptions.NODE_LABELS_PLACEMENT,
                    start.getProperty(LayeredOptions.NODE_LABELS_PLACEMENT));
            
            dummies.add(0, longEdgeDummy);
            
            return longEdgeDummy;
        }
        
        /*------------------------------------------------------------------------------------------
         *                      Big Node with an outgoing Long Edge. 
         *------------------------------------------------------------------------------------------
         */

        private Pair<Integer, Double> processOutLongEdge(final LNode bignode,
                final double originalWidth) {

            // remember all nodes we create to adapt the size later on
            List<LNode> chainOfNodes = Lists.newArrayList();
            chainOfNodes.add(bignode);

            // create dummies
            LNode start = bignode;
            int replacedDummies = 0;
            do {
                start = replaceOutLongEdgeDummy(start);
                if (start != null) {
                    chainOfNodes.add(start);
                }
                replacedDummies++;
            } while (start != null);

            // assign a width to the nodes of the big node chain, care about spacing
            double newWidth =
                    (originalWidth - (chainOfNodes.size() - 1) * spacing)
                            / (double) chainOfNodes.size();
            for (LNode d : chainOfNodes) {
                d.getSize().x = newWidth;
            }

            return Pair.of(replacedDummies, newWidth);
        }

        private LNode replaceOutLongEdgeDummy(final LNode start) {

            if (Iterables.size(start.getOutgoingEdges()) != 1
                    || Iterables.get(start.getOutgoingEdges(), 0).getTarget().getNode()
                            .getType() != NodeType.LONG_EDGE) {
                return null;
            }

            // get the dummy
            LEdge outEdge = Iterables.get(start.getOutgoingEdges(), 0);
            LNode longEdgeDummy = outEdge.getTarget().getNode();
            longEdgeDummy.setType(NodeType.BIG_NODE);

            // tell it to be a big node dummy now
            longEdgeDummy.setProperty(InternalProperties.LONG_EDGE_SOURCE, null);
            longEdgeDummy.setProperty(InternalProperties.LONG_EDGE_TARGET, null);

            // copy some properties
            longEdgeDummy.setProperty(LayeredOptions.PORT_CONSTRAINTS,
                    start.getProperty(LayeredOptions.PORT_CONSTRAINTS));
            longEdgeDummy.setProperty(LayeredOptions.NODE_LABELS_PLACEMENT,
                    start.getProperty(LayeredOptions.NODE_LABELS_PLACEMENT));

            // adapt the origin
            // Remark: we allow the big node to have an arbitrary amount of
            // ports on the EAST side, however, only one of them is allowed
            // to have an outgoing edge
            Object origin = outEdge.getSource().getProperty(InternalProperties.ORIGIN);
            LPort outPort = null;
            for (LPort p : longEdgeDummy.getPorts(PortSide.EAST)) {
                if (!p.getOutgoingEdges().isEmpty()) {
                    p.setProperty(InternalProperties.ORIGIN, origin);

                    LPort src = outEdge.getSource();
                    p.getSize().x = src.getSize().x;
                    p.getSize().y = src.getSize().y;
                    p.getAnchor().x = src.getAnchor().x;
                    p.getAnchor().y = src.getAnchor().y;
                    
                    // copy the labels of the port
                    p.getLabels().addAll(src.getLabels());
                    src.getLabels().clear();

                    outPort = p;
                    break;
                }
            }
            outEdge.getSource().setProperty(InternalProperties.ORIGIN, null);

            // if the big node has multiple EAST ports, reassemble this on the former long edge
            // dummy
            if (!Iterables.isEmpty(start.getPorts(PortSide.EAST))) {
                // the port list is sorted here!
                for (LPort p : Lists.newArrayList(start.getPorts(PortSide.EAST))) {
                    if (p.getOutgoingEdges().isEmpty()) {

                        LPort newPort = new LPort();
                        newPort.setSide(PortSide.EAST);
                        newPort.getSize().x = p.getSize().x;
                        newPort.getSize().y = p.getSize().y;
                        newPort.setNode(longEdgeDummy);
                        newPort.setProperty(InternalProperties.ORIGIN,
                                p.getProperty(InternalProperties.ORIGIN));

                        p.setNode(null);
                    } else {
                        // re-add the original port to retain order
                        outPort.setNode(longEdgeDummy);
                    }
                }
            }

            // adjust height
            longEdgeDummy.getSize().y = start.getSize().y;

            dummies.add(longEdgeDummy);

            return longEdgeDummy;
        }
        
        /*------------------------------------------------------------------------------------------
         *                      Big Node without outgoing edges. 
         *------------------------------------------------------------------------------------------
         */

        private Pair<Integer, Double> processNoOutgoingEdge(final LNode bignode,
                final int startLayerIndex, final double originalWidth, final IElkProgressMonitor monitor) {

            int maxLayer = layeredGraph.getLayers().size();
            if (startLayerIndex >= maxLayer - 1) {
                // there are no more layers, we cannot create dummies
                return null;
            }

            // remember all nodes we create to adapt the size later on
            List<LNode> chainOfNodes = Lists.newArrayList();
            chainOfNodes.add(bignode);

            // copy variables to make them mutable
            LNode start = bignode;          
            int currentLayer = startLayerIndex;
            
            // check if we can split the big node without introducing node edge overlaps
            int inLayerPos = -1;
            Layer currentLayerLayer = layeredGraph.getLayers().get(currentLayer);
            for (int i = 0; i <  currentLayerLayer.getNodes().size(); ++i) {
                LNode n = currentLayerLayer.getNodes().get(i);
                if (n.equals(start)) {
                    inLayerPos = i;
                    break;
                }
            }

            List<Integer> inLayerPositions = findPossibleDummyPositions(
                    Dir.Right, inLayerPos, currentLayer, maxLayer, chunks, true, monitor);
            if (inLayerPositions == null) {
                // no valid positioning could be found
                return null;
            }

            // create at most 'chunks' nodes
            int tmpChunks = chunks;
            int i = 0;
            int createdChunks = 0;
            while (start != null && tmpChunks > 1 && currentLayer < maxLayer - 1) {
                // create the dummy
                LNode dummy = introduceDummyNode(start, 0);

                // get layer
                Layer dummyLayer = layeredGraph.getLayers().get(currentLayer + 1);

                int upperStrokeMax = inLayerPositions.get(i++);
                int newInLayerPos = Math.min(upperStrokeMax, dummyLayer.getNodes().size());
                dummy.setLayer(newInLayerPos, dummyLayer);

                if (start != null) {
                    chainOfNodes.add(start);
                }

                start = dummy;
                tmpChunks--;
                createdChunks++;
                // each chunk implicitly covers one spacing as well
                currentLayer++;
            }

            // assign a width to the nodes of the big node chain, care about spacing
            double newWidth =
                    (originalWidth - (chainOfNodes.size() - 1) * spacing)
                            / (double) chainOfNodes.size();
            for (LNode d : chainOfNodes) {
                d.getSize().x = newWidth;
            }
            
            return Pair.of(createdChunks, newWidth);
        }
        
        
        /*------------------------------------------------------------------------------------------
         *                      Big Node without incoming edges. 
         *------------------------------------------------------------------------------------------
         */

        private Pair<Integer, Double> processNoIncomingEdge(final LNode bignode,
                final int startLayerIndex, final double originalWidth, final IElkProgressMonitor monitor) {

            if (startLayerIndex <= 0) {
                // there are no more layers, we cannot create dummies
                return null;
            }

            // remember all nodes we create to adapt the size later on
            List<LNode> chainOfNodes = Lists.newArrayList();
            chainOfNodes.add(bignode);

            // copy variables to make them mutable
            LNode start = bignode;          
            int currentLayer = startLayerIndex;
            
            // check if we can split the big node without introducing node edge overlaps
            int inLayerPos = -1;
            Layer currentLayerLayer = layeredGraph.getLayers().get(currentLayer);
            for (int i = 0; i <  currentLayerLayer.getNodes().size(); ++i) {
                LNode n = currentLayerLayer.getNodes().get(i);
                if (n.equals(start)) {
                    inLayerPos = i;
                    break;
                }
            }

            List<Integer> inLayerPositions = findPossibleDummyPositions(
                    Dir.Left, inLayerPos, currentLayer, layeredGraph.getLayers().size(), chunks, true, monitor);
            if (inLayerPositions == null) {
                // no valid positioning could be found
                return null;
            }
            
            // create at most 'chunks' nodes
            int tmpChunks = chunks;
            int i = 0;
            int createdChunks = 0;
            int prevInLayerPosition = inLayerPos;
            while (start != null && tmpChunks > 1 && currentLayer > 1) {
                // create the dummy
                LNode dummy = introduceDummyNode(start, 0);

                // get layers
                currentLayerLayer = layeredGraph.getLayers().get(currentLayer);
                Layer dummyLayer = layeredGraph.getLayers().get(currentLayer - 1);

                int upperStrokeMax = inLayerPositions.get(i++);
                int newInLayerPos = Math.min(upperStrokeMax, dummyLayer.getNodes().size());
                
                // move the big node to the new layer
                start.setLayer(newInLayerPos, dummyLayer);
                
                // fill the big node's old spot with the dummy
                dummy.setLayer(prevInLayerPosition, currentLayerLayer);
                prevInLayerPosition = newInLayerPos;
                
                if (start != null) {
                    chainOfNodes.add(start);
                }

                start = dummy;
                tmpChunks--;
                createdChunks++;
                // each chunk implicitly covers one spacing as well
                currentLayer--;
            }

            // assign a width to the nodes of the big node chain, care about spacing
            double newWidth =
                    (originalWidth - (chainOfNodes.size() - 1) * spacing)
                            / (double) chainOfNodes.size();
            for (LNode d : chainOfNodes) {
                d.getSize().x = newWidth;
            }
            
            return Pair.of(createdChunks, newWidth);
        }

        /*------------------------------------------------------------------------------------------
         *                   Common helper methods for the no in/out edges case. 
         *------------------------------------------------------------------------------------------
         */
        
        /**
         * Checks if we can create enough valid dummy nodes without introducing node edge crossings.
         * 
         * @param dir
         *            the direction into which we are extending our search
         * @param inLayerPos
         *            the vertical position of the bignode or the lastly created dummy
         * @param layerIndex
         *            the layer in which either the bignode or the lastly created dummy in situated
         * @param maxLayer
         *            last layer of the diagram
         * @param remainingChunks
         *            the number of dummy nodes we still have to create
         * @param initial
         *            is this the first layer we check
         * @return either a list containing in layer positions for the dummy nodes to be created or
         *         null if no valid positioning could be found. The list is sorted according to the
         *         order of the dummy nodes how they should be created in {@code dir} direction.
         */
        private List<Integer> findPossibleDummyPositions(
                final Dir dir, 
                final int inLayerPos, 
                final int layerIndex,
                final int maxLayer, 
                final int remainingChunks, 
                final boolean initial,
                final IElkProgressMonitor monitor) {

            // current layer
            Layer currentLayer = layeredGraph.getLayers().get(layerIndex);

            // populate upper and lower set 
            Set<LNode> upper = Sets.newHashSet();
            Set<LNode> lower = Sets.newHashSet();
            for (int i = 0; i < currentLayer.getNodes().size(); ++i) {
                LNode n = currentLayer.getNodes().get(i);
                
                if (i < inLayerPos) {
                    upper.add(n);
                } else if (i > inLayerPos) {
                    lower.add(n);
                }
                // ignore the big node and created dummies
            }

            // populate the upper and lower sets for the next layer in the specified direction
            Set<LNode> upperPrime = Sets.newHashSet();
            Set<LNode> lowerPrime = Sets.newHashSet();
            for (LNode n : upper) {
                Iterable<LEdge> connectedEdges =
                        (dir == Dir.Right) ? n.getOutgoingEdges() : n.getIncomingEdges();
                for (LEdge e : connectedEdges) {
                    // no inlayer edges
                    if (n.getLayer().getIndex() != e.getTarget().getNode().getLayer().getIndex()) {
                        upperPrime.add(e.getTarget().getNode());
                    }
                }
            }
            for (LNode n : lower) {
                Iterable<LEdge> connectedEdges =
                        (dir == Dir.Right) ? n.getOutgoingEdges() : n.getIncomingEdges();
                for (LEdge e : connectedEdges) {
                    // no inlayer edge
                    if (n.getLayer().getIndex() != e.getTarget().getNode().getLayer().getIndex()) {
                        lowerPrime.add(e.getTarget().getNode());
                    }
                }
            }

            if (monitor.isLoggingEnabled()) {
                String logMessage = node
                        + "\n    Dir: " + dir
                        + "\n    Upper: " + upper
                        + "\n    Lower: " + lower
                        + "\n    UpperStroke: " + upperPrime
                        + "\n    LowerStroke: " + lowerPrime;
                monitor.log(logMessage);
            }
            
            // find min and max
            Layer nextLayer = layeredGraph.getLayers().get(layerIndex + (dir == Dir.Right ? 1 : -1));
            int maxUprime = Integer.MIN_VALUE;
            int minLprime = Integer.MAX_VALUE;
            for (int i = 0; i < nextLayer.getNodes().size(); i++) {
                LNode n = nextLayer.getNodes().get(i);
                if (upperPrime.contains(n)) {
                   maxUprime = Math.max(maxUprime, i); 
                } else if (lowerPrime.contains(n)) {
                    minLprime = Math.min(minLprime, i);
                }
            }

            // we can find a valid position if max(U') < min(L')
            if (maxUprime < minLprime) {

                // we also require
                // no inlayer edges spanning from upper to lower
                for (LNode n : upperPrime) {
                    for (LEdge e : n.getOutgoingEdges()) {
                        if (n.getLayer().getIndex() == e.getTarget().getNode().getLayer()
                                .getIndex()) {
                            return null;
                        }
                    }
                    for (LEdge e : n.getIncomingEdges()) {
                        if (n.getLayer().getIndex() == e.getSource().getNode().getLayer()
                                .getIndex()) {
                            return null;
                        }
                    }
                }

                // or lower to upper
                for (LNode n : lowerPrime) {
                    for (LEdge e : n.getOutgoingEdges()) {
                        if (n.getLayer().getIndex() == e.getTarget().getNode().getLayer()
                                .getIndex()) {
                            return null;
                        }
                    }
                    for (LEdge e : n.getIncomingEdges()) {
                        if (n.getLayer().getIndex() == e.getSource().getNode().getLayer()
                                .getIndex()) {
                            return null;
                        }
                    }
                }

                // get layer
                int upperStrokeMax = -1; // -1, to differ between '0 pos' and 'not found'
                if (upper.isEmpty()) {
                    upperStrokeMax = 0;
                } else if (lower.isEmpty()) {
                    // if we only have an upper set, place it at the bottom of the dummy layer
                    upperStrokeMax = nextLayer.getNodes().size();
                } else {
                    // find the proper position
                    upperStrokeMax = maxUprime + 1;
                }
                
                // we also require
                // that no node groups are broken ...
                // FIXME this is too restrictive!
                // It would be better if an interleaving exists.
                for (LNode n : currentLayer.getNodes()) {
                    if (n.getType() == NodeType.NORTH_SOUTH_PORT) {
                        return null;
                    }
                }
                
                // return our current knowledge
                if (remainingChunks == 1) {
                    // we created all chunks we need
                    return Lists.newArrayList(upperStrokeMax);
                } else if ((dir == Dir.Right && layerIndex == maxLayer - 2)
                        || (dir == Dir.Left && layerIndex == 1)) {
                    // we reached the end of the diagram
                    return Lists.newArrayList(upperStrokeMax);
                } else {
                    List<Integer> rec = findPossibleDummyPositions(dir, upperStrokeMax, 
                            layerIndex + (dir == Dir.Right ? 1 : -1), maxLayer, 
                            remainingChunks - 1, false, monitor);

                    if (rec != null) {
                        if (dir == Dir.Right) {
                            rec.add(0, upperStrokeMax);
                        }
                    }
                    return rec;
                }

            }

            return null; 
        }
        
        /**
         * Creates a new dummy node of the specified width and connectes 
         * the {@code src} node with an edge to the created dummy node. Thus,
         * {@code src} will be the source of the edge and the dummy the 
         * target of the edge.
         * 
         * @return the created dummy.
         */
        private LNode introduceDummyNode(final LNode src, final double width) {
            // create new dummy node
            LNode dummy = new LNode(layeredGraph);
            dummy.setType(NodeType.BIG_NODE);

            // copy some properties
            dummy.setProperty(LayeredOptions.PORT_CONSTRAINTS,
                    src.getProperty(LayeredOptions.PORT_CONSTRAINTS));
            dummy.setProperty(LayeredOptions.NODE_LABELS_PLACEMENT,
                    src.getProperty(LayeredOptions.NODE_LABELS_PLACEMENT));

            dummy.id = dummyID++;

            // remember
            dummies.add(dummy);

            // set same height as original
            dummy.getSize().y = src.getSize().y;
            // the first n-1 nodes (initial+dummies) are assigned a width of 'minWidth'
            // while the last node (right most) is assigned the remaining
            // width of the bignode, i.e.
            // overallWidth - (n-1) * minWidth
            dummy.getSize().x = width;
            
            
            // move either WEST or EAST ports of the current src node
            PortSide portSide = PortSide.EAST;
            List<LPort> movePorts = Lists.newArrayList(src.getPorts(portSide));
            for (LPort p : movePorts) {
                p.setNode(dummy);
            }

            // add ports to connect it with the previous node
            LPort outPort = new LPort();
            outPort.setSide(portSide);
            outPort.setNode(src);
            // assign reasonable positions to the port in case of FIXES_POS
            outPort.getPosition().x = dummy.getSize().x;
            outPort.getPosition().y = dummy.getSize().y / 2;

            LPort inPort = new LPort();
            inPort.setSide(portSide.opposed());
            inPort.setNode(dummy);
            inPort.getPosition().y = dummy.getSize().y / 2;
            inPort.getPosition().x = -inPort.getSize().x;

            // add edge to connect it with the previous node
            LEdge edge = new LEdge();
            edge.setSource(outPort);
            edge.setTarget(inPort);

            return dummy;
        }
    }
}
