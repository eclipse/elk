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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Lists;

/**
 * Nodes that exceed a certain width are considered <emph>Big Nodes</emph>. Currently either a
 * minimal width of 50, or, if wider, the smallest node are used as criterion. Big nodes are then
 * replaced by <code>n</code> dummy nodes, where n equals the width of a specific node divided by
 * the previously calculated minimal width. Each dummy node gets assigned the height of the original
 * node, as well, as the {@link PortConstraints} value and is connected to its predecessor by
 * exactly one edge. EAST ports of the original node are moved to the lastly created dummy node.
 * 
 * <h2>Remarks</h2>
 * <ul>
 *   <li>During minimal width calculation, any type of dummy node is ignored.</li>
 *   <li>Only handle nodes with {@link PortConstraints} <= {@link PortConstraints#FIXED_ORDER}, or for
 *       greater port constraints we demand that the node has no NORTH and SOUTH ports.</li>
 * </ul>
 * 
 * <h2>Labels</h2>
 * Labels require special treatment, otherwise labels with a larger width than the node's width
 * would introduce possibly unnecessary spacing between the big node dummies. 
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>an acyclic graph, where every port has an assigned side.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>big nodes are split into a series of dummy nodes.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 2.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link PortSideProcessor}</dd>
 * </dl>
 * 
 * @see BigNodesIntermediateProcessor
 * @see BigNodesPostProcessor
 * 
 * @author uru
 */
public class BigNodesPreProcessor implements ILayoutProcessor<LGraph> {

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
    
    /**
     * {@inheritDoc}
     */
    public void process(final LGraph theLayeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Big nodes pre-processing", 1);

        this.layeredGraph = theLayeredGraph;
        List<LNode> nodes = layeredGraph.getLayerlessNodes();
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
            if ((node.getType() == NodeType.NORMAL) && (node.getSize().x < minWidth)) {
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
                bigNodes.add(new BigNode(node, parts, chunkWidth));
            }
        }

        // handle each big node
        for (BigNode node : bigNodes) {
            // is this big node ok?
            if (isProcessorApplicable(node.node)) {
                node.process();
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
    private boolean isProcessorApplicable(final LNode node) {

        if (node.getProperty(LayeredOptions.PORT_CONSTRAINTS) == PortConstraints.FIXED_RATIO
                || node.getProperty(LayeredOptions.PORT_CONSTRAINTS) == PortConstraints.FIXED_POS) {
            for (LPort port : node.getPorts()) {
                if (port.getSide() == PortSide.NORTH || port.getSide() == PortSide.SOUTH) {
                    return false;
                }
            }
        }
        
        // we reject nodes with incoming edges on east ports 
        // if port constraints are at least fixed side
        // Reason: incoming edges on EAST ports introduce dummy nodes within the same layer.
        // This would introduce trouble as the "last part" of a big node is a dummy itself.
        if (node.getProperty(LayeredOptions.PORT_CONSTRAINTS).isSideFixed()) {
            for (LPort p : node.getPorts(PortSide.EAST)) {
                if (!p.getIncomingEdges().isEmpty()) {
                    return false;
                }
            }
        }

        // we don't support self-loops 
        for (LEdge edge : node.getOutgoingEdges()) {
            if (edge.getSource().getNode().equals(edge.getTarget().getNode())) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Internal representation of a big node.
     * 
     * @author uru
     */
    private class BigNode {

        private LNode node;
        private int chunks;
        private double minWidth;

        /** The dummy nodes created for this big node (include the node itself at index 0). */
        private ArrayList<LNode> dummies = Lists.newArrayList();

        /**
         * Creates a new big node.
         */
        BigNode(final LNode node, final int chunks, final double minWidth) {
            this.node = node;
            this.chunks = chunks;
            this.minWidth = minWidth;
        }

        /**
         * Main entry point for big node processing.
         * 
         *  - splits the big node into consecutive dummy nodes
         *  - handles labels
         * 
         */
        public void process() {

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

            // remember original width for later step
            double originalWidth = node.getSize().x;

            // shrink the big node and mark it
            node.setProperty(InternalProperties.BIG_NODE_ORIGINAL_SIZE, (float) node.getSize().x);
            node.getSize().x = minWidth;
            node.setProperty(InternalProperties.BIG_NODE_INITIAL, true);

            // we consider the first node as dummy as well, even though we do not mark it
            dummies.add(node);

            // introduce dummy nodes
            LNode start = node;
            // the original node represents 1*minWidth
            originalWidth -= minWidth;

            int tmpChunks = chunks;
            while (tmpChunks > 1) {
                // create it and add dummy to the graph
                double dummyWidth = Math.min(originalWidth, minWidth);
                start = introduceDummyNode(start, dummyWidth);
                layeredGraph.getLayerlessNodes().add(start);

                tmpChunks--;
                // each chunk implicitly covers one spacing as well
                originalWidth -= minWidth + spacing;
            }
            
            
            // handle labels
            BigNodesLabelHandler.handle(node, dummies, minWidth);

            // add the east ports to the final dummy
            for (LPort port : eastPorts) {
                node.getPorts().remove(port);
                port.setNode(start);
            }
        }

        /**
         * Creates a new dummy node of the specified width.
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
            //  overallWidth - (n-1) * minWidth
            dummy.getSize().x = width;

            // add ports to connect it with the previous node
            LPort outPort = new LPort();
            outPort.setSide(PortSide.EAST);
            outPort.setNode(src);
            // assign reasonable positions to the port in case of FIXES_POS
            outPort.getPosition().x = dummy.getSize().x;
            outPort.getPosition().y = dummy.getSize().y / 2;

            LPort inPort = new LPort();
            inPort.setSide(PortSide.WEST);
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
