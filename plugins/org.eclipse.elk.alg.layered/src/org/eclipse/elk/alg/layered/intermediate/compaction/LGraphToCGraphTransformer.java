/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
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
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import org.eclipse.elk.alg.common.compaction.oned.CGraph;
import org.eclipse.elk.alg.common.compaction.oned.CGroup;
import org.eclipse.elk.alg.common.compaction.oned.CNode;
import org.eclipse.elk.alg.common.compaction.oned.CompareFuzzy;
import org.eclipse.elk.alg.common.compaction.oned.Quadruplet;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.Pair;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Manages the transformation of {@link LNode}s and {@link LEdge}s from an {@link LGraph} into
 * compactable {@link CNode}s and {@link CGroup}s.
 */
public final class LGraphToCGraphTransformer {
    
    /** the output CGraph. */
    private CGraph cGraph;
    /** the layered graph. */
    private LGraph layeredGraph;
    /** Current style of edge routing. */
    private EdgeRouting edgeRouting;
    /** remember comment boxes as we neglect them during compaction and offset them afterwards. */
    private Map<LNode, Pair<LNode, KVector>> commentOffsets = Maps.newHashMap();
    
    /* ------------------ Internal Mappings ------------------ */
    private Map<LNode, CNode> nodesMap = Maps.newHashMap();
    private Map<VerticalSegment, CNode> verticalSegmentsMap = Maps.newHashMap();
    private Map<CNode, Quadruplet> lockMap = Maps.newHashMap();
    
    // for debugging
    private static final Function<CNode, String> NODE_TO_STRING_DELEGATE =
            n -> ((LNode) n.origin).getProperty(InternalProperties.ORIGIN).toString();
    private static final Function<CNode, String> VS_TO_STRING_DELEGATE = n -> ((VerticalSegment) n.origin).toString();
    
    /**
     * {@inheritDoc}
     */
    public CGraph transform(final LGraph inputGraph) {
        this.layeredGraph = inputGraph;
        this.edgeRouting = layeredGraph.getProperty(LayeredOptions.EDGE_ROUTING);
        
        init();
        transformNodes();
        transformEdges();

        return cGraph;
    }

    /**
     * @return the lockMap
     */
    public Map<CNode, Quadruplet> getLockMap() {
        return lockMap;
    }
    
    private void init() {
        // checking if the graph has edges and possibly prohibiting vertical compaction
        boolean hasEdges = false;

        int index = 0;
        for (Layer l : layeredGraph) {
            l.id = index++;
            for (LNode n : l) {
                // avoid calling Iterables.isEmpty unnecessarily
                if (!hasEdges && !Iterables.isEmpty(n.getConnectedEdges())) {
                    hasEdges = true;
                }
            }
        }

        EnumSet<Direction> supportedDirections = EnumSet.of(Direction.UNDEFINED, Direction.LEFT, Direction.RIGHT);
        if (!hasEdges) {
            supportedDirections.add(Direction.UP);
            supportedDirections.add(Direction.DOWN);
        }

        // initializing members
        cGraph = new CGraph(supportedDirections);
        nodesMap.clear();
        commentOffsets.clear();
        lockMap.clear();
        verticalSegmentsMap.clear();
    }

    private void transformNodes() {
        for (Layer layer : layeredGraph) {
            for (LNode node : layer) {

                // comment boxes are part of a node's margins
                // hence we can neglect them here without the risk
                // of other nodes overlapping them after compaction
                if (node.getProperty(LayeredOptions.COMMENT_BOX)) {
                    if (!Iterables.isEmpty(node.getConnectedEdges())) {
                        LEdge e = Iterables.get(node.getConnectedEdges(), 0);
                        LNode other = e.getSource().getNode();
                        if (other == node) {
                            other = e.getTarget().getNode();
                        }
                        Pair<LNode, KVector> p = Pair.of(other, node.getPosition().clone().sub(other.getPosition()));
                        commentOffsets.put(node, p);
                        continue;
                    }
                }
                
                ElkRectangle hitbox =
                      new ElkRectangle(
                              node.getPosition().x - node.getMargin().left, 
                              node.getPosition().y - node.getMargin().top, 
                              node.getSize().x + node.getMargin().left + node.getMargin().right, 
                              node.getSize().y + node.getMargin().top + node.getMargin().bottom);

                // create the node in the compaction graph
                CNode cNode = CNode.of()
                                   .origin(node)
                                   .hitbox(hitbox)
                                   .toStringDelegate(NODE_TO_STRING_DELEGATE)
                                   .create(cGraph);
                // the node lives in its own group
                CGroup.of()
                      .nodes(cNode)
                      .master(cNode)
                      .create(cGraph);
                
                Quadruplet nodeLock = new Quadruplet();
                lockMap.put(cNode, nodeLock);

                // locking the node for directions that fewer edges are connected in
                // (only used if LEFT_RIGHT_CONNECTION_LOCKING is used)
                int difference = Iterables.size(node.getIncomingEdges())
                                 - Iterables.size(node.getOutgoingEdges());
                if (difference < 0) {
                    nodeLock.set(true, Direction.LEFT);
                } else if (difference > 0) {
                    nodeLock.set(true, Direction.RIGHT);
                }
                
                // excluding external port dummies
                if (node.getType() == NodeType.EXTERNAL_PORT) {
                    nodeLock.set(false, false, false, false);
                }
                
                nodesMap.put(node, cNode);
            }
        }
    }

    private void transformEdges() {
        EdgeRouting style = layeredGraph.getProperty(LayeredOptions.EDGE_ROUTING);
        List<VerticalSegment> verticalSegments;
        switch (style) {
            case ORTHOGONAL:
                verticalSegments = collectVerticalSegmentsOrthogonal();
                break;
            default:
                throw new IllegalStateException("Compaction not supported for " + style + " edges.");
        }

        // merge them
        mergeVerticalSegments(verticalSegments);
        
        // create precomputed constraints
        verticalSegmentsMap.keySet()
            .forEach(vs -> {
                CNode vsNode = verticalSegmentsMap.get(vs);
                vs.constraints.forEach(other -> {
                   CNode otherNode = verticalSegmentsMap.get(other);
                   cGraph.predefinedHorizontalConstraints.add(Pair.of(vsNode, otherNode));
                });
            });
    }
    
    private List<VerticalSegment> collectVerticalSegmentsOrthogonal() {
        List<VerticalSegment> verticalSegments = Lists.newArrayList();
        
        for (Layer layer : layeredGraph) {
            for (LNode node : layer) {
                final CNode cNode = nodesMap.get(node);
                
                // add vertical edge segments
                for (LEdge edge : node.getOutgoingEdges()) {

                    Iterator<KVector> bends = edge.getBendPoints().iterator();

                    boolean first = true;
                    VerticalSegment lastSegment = null;
                    // infer vertical segments from positions of bendpoints
                    if (bends.hasNext()) {
                        KVector bend1 = bends.next();
                        KVector bend2 = null;

                        // get segment of source n/s port of outgoing segment
                        if (edge.getSource().getSide() == PortSide.NORTH) {
                            VerticalSegment vs =
                                    new VerticalSegment(bend1, new KVector(bend1.x, cNode.hitbox.y), cNode, edge);
                            vs.ignoreSpacing.down = true;
                            vs.aPort = edge.getSource();
                            verticalSegments.add(vs);
                        }
                        if (edge.getSource().getSide() == PortSide.SOUTH) {
                            VerticalSegment vs = new VerticalSegment(bend1, new KVector(bend1.x,
                                    cNode.hitbox.y + cNode.hitbox.height), cNode, edge);
                            vs.ignoreSpacing.up = true;
                            vs.aPort = edge.getSource();
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
                                   
                                   // make sure that "invalid" spacing that sometimes is required 
                                   // to route the edges away from the nodes, does not become an issue
                                   if (bend2.y < cNode.hitbox.y) {
                                       lastSegment.ignoreSpacing.down = true;
                                   } else if (bend2.y > cNode.hitbox.y + cNode.hitbox.height) {
                                       lastSegment.ignoreSpacing.up = true;
                                   } else {
                                       // completely surrounded
                                       lastSegment.ignoreSpacing.up = true;
                                       lastSegment.ignoreSpacing.down = true;
                                   }
                                }
                            }

                            if (bends.hasNext()) {
                                bend1 = bend2;
                            }
                        }
                        
                        // handle last vertical segment
                        if (lastSegment != null) {
                            CNode cTargetNode = nodesMap.get(edge.getTarget().getNode());
                            if (bend1.y < cTargetNode.hitbox.y) {
                                lastSegment.ignoreSpacing.down = true;
                            } else if (bend1.y > cTargetNode.hitbox.y + cTargetNode.hitbox.height) {
                                lastSegment.ignoreSpacing.up = true;
                            } else {
                                // completely surrounded
                                lastSegment.ignoreSpacing.up = true;
                                lastSegment.ignoreSpacing.down = true;
                            }
                        }
                    }
                }

                // same for incoming edges to get north/south segments on target side
                for (LEdge edge : node.getIncomingEdges()) {
                    if (!edge.getBendPoints().isEmpty()) {

                        // get segment of target n/s port
                        KVector bend1 = edge.getBendPoints().getLast();
                        if (edge.getTarget().getSide() == PortSide.NORTH) {
                            VerticalSegment vs = new VerticalSegment(bend1, new KVector(bend1.x,
                                    cNode.hitbox.y), cNode, edge);
                            vs.ignoreSpacing.down = true;
                            vs.aPort = edge.getTarget();
                            verticalSegments.add(vs);
                        }
                        if (edge.getTarget().getSide() == PortSide.SOUTH) {
                            VerticalSegment vs = new VerticalSegment(bend1, new KVector(bend1.x,
                                    cNode.hitbox.y + cNode.hitbox.height), cNode, edge);
                            vs.ignoreSpacing.up = true;
                            vs.aPort = edge.getTarget();
                            verticalSegments.add(vs);
                        }
                        
                    }
                }
            }
        }
        
        return verticalSegments;
    }
    
    
    private void mergeVerticalSegments(final List<VerticalSegment> verticalSegments) {
        if (verticalSegments.isEmpty()) {
            return;
        }
        
        // sort the segments such that segments with the same 
        //  x coordinate are consecutive within the list, 
        //  for equal x coordinates, sort with ascending y coordinate 
        Collections.sort(verticalSegments);

        // merge segments that intersect
        Iterator<VerticalSegment> vsIt = verticalSegments.iterator();
        VerticalSegment survivor = vsIt.next();

        while (vsIt.hasNext()) {
            VerticalSegment next = vsIt.next();
            
            if (survivor.intersects(next)) {
                survivor = survivor.joinWith(next);
            } else {
                verticalSegmentToCNode(survivor);
                // start with a new segment
                survivor = next;
            }
        }
        
        // transform the last segment as well
        verticalSegmentToCNode(survivor);
    }
    
    private void verticalSegmentToCNode(final VerticalSegment verticalSegment) {
        // create CNode representation for the last segment
        CNode cNode = CNode.of()
                           .origin(verticalSegment)
                           .type("vs")
                           .hitbox(new ElkRectangle(verticalSegment.hitbox))
                           .toStringDelegate(VS_TO_STRING_DELEGATE)
                           .create(cGraph);
        
        // group the node if requested, currently this is only the case for north/south segments of orthogonal edges 
        // assumption: during creation of LNode representing CNodes, these CNodes have been fitted with their own group
        if (!verticalSegment.potentialGroupParents.isEmpty()) {
            verticalSegment.potentialGroupParents.get(0).cGroup.addCNode(cNode);
        }
        
        Quadruplet vsLock = new Quadruplet();
        lockMap.put(cNode, vsLock);

        // segments belonging to multiple edges should be locked 
        // in the direction that fewer different ports are connected in
        // (only used if LEFT_RIGHT_CONNECTION_LOCKING is active)
        Set<LPort> inc = Sets.newHashSet();
        Set<LPort> out = Sets.newHashSet();
        for (LEdge e : verticalSegment.representedLEdges) {
            inc.add(e.getSource());
            out.add(e.getTarget());
        }
        int difference = inc.size() - out.size();
        if (difference < 0) {
            vsLock.set(true, Direction.LEFT);
            vsLock.set(false, Direction.RIGHT);
        } else if (difference > 0) {
            vsLock.set(false, Direction.LEFT);
            vsLock.set(true, Direction.RIGHT);
        }
        
        verticalSegment.joined.forEach(other -> verticalSegmentsMap.put(other, cNode));
        verticalSegmentsMap.put(verticalSegment, cNode);
    }

    /**
     * {@inheritDoc}
     */
    public void applyLayout() {
        
        // apply the compacted positions to nodes
        cGraph.cNodes.stream()
            .filter(cNode -> cNode.origin instanceof LNode)
            .forEach(cNode -> {
                LNode lNode = (LNode) cNode.origin;
                lNode.getPosition().x = cNode.hitbox.x + lNode.getMargin().left;
            });
        
        // adjust comment boxes
        applyCommentPositions();

        // apply new positions to vertical segments
        cGraph.cNodes.stream()
            .filter(cNode -> cNode.origin instanceof VerticalSegment)
            .forEach(cNode -> {
                double deltaX = cNode.hitbox.x - cNode.hitboxPreCompaction.x;
                VerticalSegment vs = (VerticalSegment) cNode.origin;
                vs.affectedBends.forEach(b -> b.x += deltaX);
                vs.affectedBoundingBoxes.forEach(bb -> bb.x += deltaX);
                vs.junctionPoints.forEach(jp -> jp.x += deltaX);
            });
        
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
        
        
        // external port dummies may have been moved ... put them back
        applyExternalPortPositions(topLeft, bottomRight);
        
        cleanup();
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
        for (CNode cNode : cGraph.cNodes) {
            LNode lNode = HorizontalGraphCompactor.getLNodeOrNull(cNode);
            if (lNode != null) {
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
    
    private void cleanup() {
        nodesMap.clear();
        commentOffsets.clear();
        verticalSegmentsMap.clear();
        lockMap.clear();
        cGraph.cGroups.clear();
        cGraph.cNodes.clear();
        cGraph = null;
        layeredGraph = null;
    }

}
