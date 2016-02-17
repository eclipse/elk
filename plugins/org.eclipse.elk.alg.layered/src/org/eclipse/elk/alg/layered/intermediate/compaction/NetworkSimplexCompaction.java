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

import java.util.List;
import java.util.Map;

import org.eclipse.elk.alg.layered.compaction.oned.CGroup;
import org.eclipse.elk.alg.layered.compaction.oned.CNode;
import org.eclipse.elk.alg.layered.compaction.oned.OneDimensionalCompactor;
import org.eclipse.elk.alg.layered.compaction.oned.algs.ICompactionAlgorithm;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.networksimplex.NEdge;
import org.eclipse.elk.alg.layered.networksimplex.NGraph;
import org.eclipse.elk.alg.layered.networksimplex.NNode;
import org.eclipse.elk.alg.layered.networksimplex.NetworkSimplex;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.BasicProgressMonitor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Compaction strategy for the {@link OneDimensionalCompactor} based on the network simplex
 * algorithm. As a consequence not the width is minimized but the edge length of the unterlying
 * network simplex graph. We model this graph, such that the edge lengths of the original
 * {@link de.cau.cs.kieler.klay.layered.graph.LGraph LGraph} are minimized.
 */
public class NetworkSimplexCompaction implements ICompactionAlgorithm {
    
    private OneDimensionalCompactor compactor; 
    private NGraph networkSimplexGraph;
    
    private NNode[] nNodes;
    private int index;
    
    private static final int SEPARATION_WEIGHT = 1;
    private static final int EDGE_WEIGHT = 100;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void compact(final OneDimensionalCompactor theCompactor) {
        
        this.compactor = theCompactor;
        this.networkSimplexGraph = new NGraph();

        this.nNodes = new NNode[compactor.cGraph.cGroups.size()];
        this.index = 0;

        // create a node in the network simplex graph for each group
        for (CGroup cGroup : compactor.cGraph.cGroups) {

            cGroup.id = index;
            NNode nNode = NNode.of().id(index).origin(cGroup).create(networkSimplexGraph);
            nNodes[index] = nNode;

            index++;
        }

        // #2 transform constraint graph edges
        addSeparationConstraints();

        // #3 add the original graph's edges for edge length minimization
        addEdgeConstraints();
        
        // #4 network simplex does not allow disconnected graphs
        //  so we make it connected!
        addArtificialSourceNode();
        
//        if(nNodes.length > 1)
//        networkSimplexGraph.writeDebugGraph("d:/tmp/cc/foo.kgx");
        
        // #5 execute network simplex
        NetworkSimplex.forGraph(networkSimplexGraph)
                      .execute(new BasicProgressMonitor());
        
        // #6 apply positions
        for (CNode cNode : compactor.cGraph.cNodes) {
            cNode.startPos = nNodes[cNode.cGroup.id].layer + cNode.cGroupOffset.x;
            cNode.applyPosition();
        }  
    }

    /**
     * @return true if both passed nodes originate from the same (set) of {@link LEdge}. 
     */
    private boolean isVerticalSegmentsOfSameEdge(final CNode cNode1, final CNode cNode2) {
        return cNode1.parentNode != null
                && cNode2.parentNode != null
                && (cNode1 instanceof CLEdge && cNode2 instanceof CLEdge)
                // this might seem quite expensive but in most cases the sets 
                // contain only one element
                && !Sets.intersection(((CLEdge) cNode1).originalLEdges,
                        ((CLEdge) cNode2).originalLEdges).isEmpty();
    }
    
    /**
     * Add constraints that guarantee proper spacing and order between all groups.
     */
    private void addSeparationConstraints() {

        for (CNode cNode : compactor.cGraph.cNodes) {
            for (CNode incNode : cNode.constraints) {
                
                // no need to add edges between nodes of the same group
                if (cNode.cGroup == incNode.cGroup) {
                    continue;
                }
                
                double spacing;
                if (compactor.direction.isHorizontal()) {
                    spacing = compactor.spacingsHandler.getHorizontalSpacing(cNode, incNode);
                } else {
                    spacing = compactor.spacingsHandler.getVerticalSpacing(cNode, incNode);
                }
                
                // the constraint edges look as follows: 
                //   cNode <-- incNode
                double delta = cNode.cGroupOffset.x 
                               + cNode.hitbox.width
                               + spacing
                               - incNode.cGroupOffset.x;
                
                // network simplex requires an integer for delta
                delta = Math.ceil(delta);
                
                // negative values can occur with external ports
                //  it should be ok to fix this here
                delta = Math.max(0, delta); 

                if (!isVerticalSegmentsOfSameEdge(cNode, incNode)) {
                    
                    // it looks nicer if the initial segment is close to the node
                    double weight = SEPARATION_WEIGHT;
                    if ((cNode instanceof CLEdge && incNode instanceof CLNode)
                            || (incNode instanceof CLEdge && cNode instanceof CLNode)) {
                        weight = 2;
                    }
                    // add a single edge, i.e. constraint to the network simplex graph
                    NEdge.of()
                        .delta((int) delta)
                        .weight(weight) // small weight here, it only preserves separations
                        .source(nNodes[cNode.cGroup.id])
                        .target(nNodes[incNode.cGroup.id])
                        .create();

                } else {

                    // vertical segments are quite a species ...
                    //  it may be necessary to change the "order" of 
                    //  vertical segments' groups to get a straight 
                    //  edge without violating any other requirements
                    
                    NNode helper = NNode.of().create(networkSimplexGraph);
                    
                    // TODO add an offsetcorrection... just slightly move one of the nodes 
                    int offsetDelta =
                            (int) Math.ceil(incNode.cGroupOffset.x - cNode.cGroupOffset.x);
                    
                    NEdge.of()
                        .delta(Math.max(0, offsetDelta))
                        .weight(SEPARATION_WEIGHT) // small weight here, it only preserves separations
                        .source(helper)
                        .target(nNodes[cNode.cGroup.id])
                        .create();
                    
                    NEdge.of()
                        .delta(Math.max(0, -offsetDelta))
                        .weight(SEPARATION_WEIGHT) // no weight here, it only preserves separations
                        .source(helper)
                        .target(nNodes[incNode.cGroup.id])
                        .create();

                }
            }
        }
    }
    
    /**
     * Add edges to the network simplex graph that represent the original {@link LEdge}s of the
     * graph. Give them a larger weight such that they are kept short.
     * 
     * There are two additional special cases:
     *  - directly connected north + south port
     *    (they are already handled by the {@link #addSeparationConstraints()} method.
     *     Note that this is only possible because the spacings handler that is specified 
     *     in {@link HorizontalGraphCompactor} adds a single pixel to each of the 
     *     vertical segments, thus enforcing a constraint)
     *  - inverted ports
     */
    private void addEdgeConstraints() {

        // collect the original edgess
        Map<LNode, CNode> lNodeMap = Maps.newHashMap();
        Multimap<LEdge, CNode> lEdgeMap = HashMultimap.create();
        for (CNode cNode : compactor.cGraph.cNodes) {
            if (cNode instanceof CLNode) {
                LNode lNode = ((CLNode) cNode).getlNode();
                lNodeMap.put(lNode, cNode);
            } else if (cNode instanceof CLEdge) {
                for (LEdge e : ((CLEdge) cNode).originalLEdges) {
                    lEdgeMap.put(e, cNode);
                }
            }
        }
        
        // add network simplex edges
        for (CNode cNode : compactor.cGraph.cNodes) {
            if (cNode instanceof CLNode) {
                LNode lNode = ((CLNode) cNode).getlNode();
                for (LEdge lEdge : lNode.getOutgoingEdges()) {
                    
                    // ignore self-loops
                    if (lEdge.isSelfLoop()) {
                        continue;
                    }
                    
                    LPort srcPort = lEdge.getSource();
                    LPort tgtPort = lEdge.getTarget();
                    
                    // they may be connected "directly"
                    // if one of them is north or south however, we still want to pull the node close
                    if (PortSide.SIDES_NORTH_SOUTH.contains(lEdge.getSource().getSide())
                          && PortSide.SIDES_NORTH_SOUTH.contains(lEdge.getTarget().getSide())) {
                        continue;
                    }
                    
                    CNode target = lNodeMap.get(lEdge.getTarget().getNode());
                    
                    NEdge.of()
                        .delta(0)
                        .weight(EDGE_WEIGHT)
                        .source(nNodes[cNode.cGroup.id])
                        .target(nNodes[target.cGroup.id])
                        .create();
                    
                    // keep vertical segments close to the node if they are inverted ports
                    if (srcPort.getSide() == PortSide.WEST && LPort.OUTPUT_PREDICATE.apply(srcPort)) {

                        for (CNode n : lEdgeMap.get(lEdge)) {
                            if (n.hitbox.x < cNode.hitbox.x) {
                                NNode src = nNodes[n.cGroup.id];
                                NNode tgt = nNodes[cNode.cGroup.id];
                                if (src == tgt) {
                                    continue;
                                }
                                NEdge.of()
                                    .delta(1)
                                    .weight(EDGE_WEIGHT)
                                    .source(src)
                                    .target(tgt)
                                    .create();
                            }
                        }
                    }

                    if (tgtPort.getSide() == PortSide.EAST && LPort.INPUT_PREDICATE.apply(tgtPort)) {

                        for (CNode n : lEdgeMap.get(lEdge)) {
                            if (n.hitbox.x > cNode.hitbox.x) {
                                NNode src = nNodes[cNode.cGroup.id];
                                NNode tgt = nNodes[n.cGroup.id];
                                if (src == tgt) {
                                    continue;
                                }
                                NEdge.of()
                                    .delta(1)
                                    .weight(EDGE_WEIGHT)
                                    .source(src)
                                    .target(tgt)
                                    .create();
                            }
                        }
                    }
                    
                }
            }
        }
    }
    
    /**
     * Creates an new node in the network simplex graph and adds an edge to every (so far) source
     * node.
     */
    private void addArtificialSourceNode() {
        List<NNode> sources = Lists.newLinkedList();
        for (NNode n : networkSimplexGraph.nodes) {
            if (n.getIncomingEdges().isEmpty()) {
                sources.add(n);
            }
        }
        
        if (sources.size() > 1) {
            NNode dummySource = NNode.of()
                                    .id(index++)
                                    .create(networkSimplexGraph);
            
            for (NNode src : sources) {
                NEdge.of()
                   .delta(1)
                   .weight(0)
                   .source(dummySource)
                   .target(src)
                   .create();
            }
        }
    }
}
