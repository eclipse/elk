/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.compaction;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.elk.alg.layered.compaction.oned.CGraph;
import org.eclipse.elk.alg.layered.compaction.oned.CGroup;
import org.eclipse.elk.alg.layered.compaction.oned.CNode;
import org.eclipse.elk.alg.layered.compaction.oned.CompareFuzzy;
import org.eclipse.elk.alg.layered.compaction.oned.ICGraphTransformer;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.Pair;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Manages the transformation of {@link LNode}s and {@link LEdge}s from an {@link LGraph} into
 * compactable {@link CNode}s and {@link CGroup}s.
 */
public final class LGraphToCGraphTransformer implements ICGraphTransformer<LGraph> {
    
    /** the output CGraph. */
    private CGraph cGraph;
    /** the layered graph. */
    private LGraph layeredGraph;
    /** flags the input graph if it has edges. */
    private boolean hasEdges;
    /** remember comment boxes as we neglect them during compaction and offset them afterwards. */
    private Map<LNode, Pair<LNode, KVector>> commentOffsets = Maps.newHashMap();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CGraph transform(final LGraph inputGraph) {
    
        layeredGraph = inputGraph;
        commentOffsets.clear();
        
        // checking if the graph has edges and possibly prohibiting vertical compaction
        hasEdges = false;
        outer: for (Layer l : layeredGraph) {
            for (LNode n : l) {
                if (!Iterables.isEmpty(n.getConnectedEdges())) {
                    hasEdges = true;
                    break outer;
                }
            }
        }
        EnumSet<Direction> supportedDirections =
                EnumSet.of(Direction.UNDEFINED, Direction.LEFT, Direction.RIGHT);
        if (!hasEdges) {
            supportedDirections.add(Direction.UP);
            supportedDirections.add(Direction.DOWN);
        }
        
        // initializing fields
        cGraph = new CGraph(supportedDirections);
        
        // importing LGraphElements into CNodes
        readNodes();

        return cGraph;
    }

    /**
     * Collects the positions and dimensions of {@link LNode}s and vertical segments in the layered
     * graph and writes them to the {@link CNode}s.
     */
    private void readNodes() {
        List<VerticalSegment> verticalSegments = Lists.newArrayList();
        // resetting to avoid problems if this is called repeatedly
        cGraph.cNodes.clear();

        // 1. collecting positions of graph elements
        Map<LNode, CNode> nodeMap = Maps.newHashMap();
        for (Layer layer : layeredGraph) {
            for (LNode node : layer) {
                
                // comment boxes are part of a node's margins 
                //  hence we can neglect them here without the risk 
                // of other nodes overlapping them after compaction
                if (node.getProperty(LayeredOptions.COMMENT_BOX)) {
                    if (!Iterables.isEmpty(node.getConnectedEdges())) {
                        LEdge e = Iterables.get(node.getConnectedEdges(), 0);
                        LNode other = e.getSource().getNode();
                        if (other == node) {
                            other = e.getTarget().getNode();
                        }
                        Pair<LNode, KVector> p =
                                Pair.of(other, node.getPosition().clone().sub(other.getPosition()));
                        commentOffsets.put(node, p);
                        continue;
                    }
                }
                
                // add all nodes
                CLNode cNode = new CLNode(node, layeredGraph);
                cGraph.cNodes.add(cNode);
                nodeMap.put(node, cNode);
            }
        }

        for (Layer layer : layeredGraph) {
            for (LNode node : layer) {
                final CNode cNode = nodeMap.get(node);
                
                // add vertical edge segments
                for (LEdge edge : node.getOutgoingEdges()) {

                    Iterator<KVector> bends = edge.getBendPoints().iterator();

                    boolean first = true;
                    VerticalSegment lastSegment = null;
                    // infer vertical segments from positions of bendpoints
                    if (bends.hasNext()) {
                        KVector bend1 = bends.next();
                        KVector bend2 = null;

                        // get segment of source n/s port
                        if (edge.getSource().getSide() == PortSide.NORTH) {
                            VerticalSegment vs = new VerticalSegment(bend1, new KVector(bend1.x,
                                    cNode.hitbox.y), cNode, edge);
                            vs.blockBottomSpacing = true;
                            verticalSegments.add(vs);
                        }
                        
                        if (edge.getSource().getSide() == PortSide.SOUTH) {
                            VerticalSegment vs = new VerticalSegment(bend1, new KVector(bend1.x,
                                    cNode.hitbox.y + cNode.hitbox.height), cNode, edge);
                            vs.blockTopSpacing = true;
                            verticalSegments.add(vs);
                        }

                        // get regular segments
                        while (bends.hasNext()) {
                            bend2 = bends.next();
                            if (!CompareFuzzy.eq(bend1.y, bend2.y)) {
                                lastSegment = new VerticalSegment(bend1, bend2, null, edge);
                                verticalSegments.add(lastSegment);

                                // the first vertical segment of an outgoing edge
                                if (first) {
                                   first = false;
                                   
                                   if (bend2.y < cNode.hitbox.y) {
                                       lastSegment.blockBottomSpacing = true;
                                   } else if (bend2.y > cNode.hitbox.y + cNode.hitbox.height) {
                                       lastSegment.blockTopSpacing = true;
                                   } else {
                                       // completely surrounded
                                       lastSegment.blockTopSpacing = true;
                                       lastSegment.blockBottomSpacing = true;
                                   }
                                }
                            }

                            if (bends.hasNext()) {
                                bend1 = bend2;
                            }
                        }
                        
                        // handle last vertical segment
                        if (lastSegment != null) {
                            CNode cTargetNode = nodeMap.get(edge.getTarget().getNode());
                            if (bend1.y < cTargetNode.hitbox.y) {
                                lastSegment.blockBottomSpacing = true;
                            } else if (bend1.y > cTargetNode.hitbox.y + cTargetNode.hitbox.height) {
                                lastSegment.blockTopSpacing = true;
                            } else {
                                // completely surrounded
                                lastSegment.blockTopSpacing = true;
                                lastSegment.blockBottomSpacing = true;
                            }
                        }
                    }
                }

                // same for incoming edges to get NSSegments on target side
                for (LEdge edge : node.getIncomingEdges()) {
                    if (!edge.getBendPoints().isEmpty()) {

                        // get segment of target n/s port
                        KVector bend1 = edge.getBendPoints().getLast();
                        if (edge.getTarget().getSide() == PortSide.NORTH) {
                            VerticalSegment vs = new VerticalSegment(bend1, new KVector(bend1.x,
                                    cNode.hitbox.y), cNode, edge);
                            vs.blockBottomSpacing = true;
                            verticalSegments.add(vs);
                        }
                        
                        if (edge.getTarget().getSide() == PortSide.SOUTH) {
                            VerticalSegment vs = new VerticalSegment(bend1, new KVector(bend1.x,
                                    cNode.hitbox.y + cNode.hitbox.height), cNode, edge);
                            vs.blockTopSpacing = true;
                            verticalSegments.add(vs);
                        }
                        
                    }
                }
            }
        }
        
        // 2. combining intersecting segments in CLEdges to process them as one
        if (!verticalSegments.isEmpty()) {
            // sorting the segments by position in ascending order
            Collections.sort(verticalSegments);

            // merging intersecting segments in the same CLEdge
            VerticalSegment last = verticalSegments.get(0);
            CLEdge c = new CLEdge(last, layeredGraph);

            for (int i = 1; i < verticalSegments.size(); i++) {

                VerticalSegment verticalSegment = verticalSegments.get(i);

                if (c.intersects(verticalSegment)) {
                    c.addSegment(verticalSegment);
                } else {
                    cGraph.cNodes.add(c);
                    c = new CLEdge(verticalSegment, layeredGraph);
                }

                last = verticalSegment;
            }
            cGraph.cNodes.add(c);
        }

        verticalSegments.clear();

        // 3. grouping nodes with their connected north/south segments
        groupCNodes();
    }

    /**
     * Groups nodes with their connected north/south segments to keep them at the correct position
     * relative to each other.
     */
    private void groupCNodes() {
        // resetting groups from previous compaction
        cGraph.cGroups.clear();
        // necessary because of the exception in CGroup.addCNode
        for (CNode cNode : cGraph.cNodes) {
            cNode.cGroup = null;
        }

        // creating groups for independent CNodes
        for (CNode cNode : cGraph.cNodes) {
            if (cNode.parentNode == null) {
                cGraph.cGroups.add(new CGroup(cNode));
            }
        }

        // adding CNodes of north/south segments to the same group as their parent nodes
        for (CNode cNode : cGraph.cNodes) {
            if (cNode.parentNode != null) {
                cNode.parentNode.cGroup.addCNode(cNode);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyLayout() {
        // applying the compacted positions
        applyNodePositions();
        
        // adjust comment boxes
        applyCommentPositions();
        
        // calculating new graph size and offset
        KVector topLeft = new KVector(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        KVector bottomRight = new KVector(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        for (CNode cNode : cGraph.cNodes) {
            topLeft.x = Math.min(topLeft.x, cNode.hitbox.x);
            topLeft.y = Math.min(topLeft.y, cNode.hitbox.y);
            bottomRight.x = Math.max(bottomRight.x, cNode.hitbox.x + cNode.hitbox.width);
            bottomRight.y = Math.max(bottomRight.y, cNode.hitbox.y + cNode.hitbox.height);
        }
        layeredGraph.getOffset().reset().add(topLeft.clone().negate());
        layeredGraph.getSize().reset().add(bottomRight.clone().sub(topLeft));
        
        applyExternalPortPositions(topLeft, bottomRight);
        
        // resetting lists
        cGraph.cGroups.clear();
        cGraph.cNodes.clear();
    }
    
    /**
     * Applies the compacted positions to the
     * {@link org.eclipse.elk.alg.layered.graph.LGraphElement LGraphElement}s represented by
     * {@link CNode}s.
     */
    private void applyNodePositions() {
        for (CNode cNode : cGraph.cNodes) {
            cNode.applyElementPosition();
        }
    }

    private void applyCommentPositions() {
        for (Entry<LNode, Pair<LNode, KVector>> e : commentOffsets.entrySet()) {
            LNode comment = e.getKey();
            LNode other = e.getValue().getFirst();
            KVector offset = e.getValue().getSecond();
            comment.getPosition().reset().add(other.getPosition().clone().add(offset));
        }
    }
    
    private void applyExternalPortPositions(final KVector topLeft, final KVector bottomRight) {
        
        // FIXME apply insets here?
        
        for (CNode cNode : cGraph.cNodes) {
            if (cNode instanceof CLNode) {
                LNode lNode = ((CLNode) cNode).getlNode();
                if (lNode.getType() == NodeType.EXTERNAL_PORT) {
                    switch (lNode.getProperty(InternalProperties.EXT_PORT_SIDE)) {
                    case WEST:
                        lNode.getPosition().x = topLeft.x;
                        break;
                    case EAST:
                        lNode.getPosition().x = bottomRight.x
                                - (lNode.getSize().x + lNode.getMargin().right); 
                        break;
                    case NORTH: 
                        lNode.getPosition().y = topLeft.y;
                        break;
                    case SOUTH:
                        lNode.getPosition().y = bottomRight.y
                                - (lNode.getSize().y + lNode.getMargin().bottom);
                        break;
                    }
                }
            }
        }
    }

}
