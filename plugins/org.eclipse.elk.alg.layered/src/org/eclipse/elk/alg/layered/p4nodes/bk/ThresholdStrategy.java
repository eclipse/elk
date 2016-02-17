/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p4nodes.bk;

import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.p4nodes.bk.BKAlignedLayout.HDirection;
import org.eclipse.elk.alg.layered.p4nodes.bk.BKAlignedLayout.VDirection;
import org.eclipse.elk.core.util.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.math.DoubleMath;

/**
 * 
 */
public abstract class ThresholdStrategy {

    // TODO make this an option?!
    private static final double THRESHOLD = Double.MAX_VALUE;
    
    private static final double EPSILON = 0.0001d; 
    
    // SUPPRESS CHECKSTYLE NEXT 24 VisibilityModifier
    /**
     * The currently processed layout with its iteration directions.
     */
    protected BKAlignedLayout bal;
    
    /**
     * The precalculated neighborhood information.
     */
    protected NeighborhoodInformation ni;
    
    /**
     * We keep track of which blocks have been completely finished.
     */
    protected Set<LNode> blockFinished = Sets.newHashSet();

    /**
     * A queue with blocks that are postponed during compaction.
     */
    protected Queue<Postprocessable> postProcessablesQueue = Lists.newLinkedList();
    
    /** 
     * A stack that is used to treat postponed nodes in reversed order.
     */
    protected Stack<Postprocessable> postProcessablesStack = new Stack<>();
    
    /**
     * Resets the internal state.
     * 
     * @param theBal
     *            The currently processed layout with its iteration directions.
     * @param theNi
     *            The precalculated neighborhood information of the graph.
     */
    public void init(final BKAlignedLayout theBal, final NeighborhoodInformation theNi) {
        this.bal = theBal;
        this.ni = theNi;
        blockFinished.clear();
        postProcessablesQueue.clear();
        postProcessablesStack.clear();
    }
    
    /**
     * Marks the block of which {@code n} is the root to be completely placed.
     * 
     * @param n
     *            the root of a block.
     */
    public void finishBlock(final LNode n) {
        blockFinished.add(n);
    }
    
    
    /* ------------------------------------------------
     *  Methods to be implemented by deriving classes.
     * ------------------------------------------------
     */
    
    /**
     * @param oldThresh
     *            an old, previously calculated threshold value
     * @param blockRoot
     *            the root node of the current block being placed
     * @param currentNode
     *            the currently processed node of a block. This can be equal to
     *            {@code blockRoot}.
     * @return a threshold value representing a bound that would allow an additional edge to be
     *         drawn straight.
     */
    public abstract double calculateThreshold(final double oldThresh,
            final LNode blockRoot, final LNode currentNode);
    
    
    /**
     * Handle nodes that have been marked as having potential to 
     * lead to further straight edges after all blocks were initially placed.
     */
    public abstract void postProcess();
    
    // SUPPRESS CHECKSTYLE NEXT 20 VisibilityModifier
    /**
     * @param edge
     *            the edge for which the node is requested.
     * @param n
     *            a node edge is connected to.
     * @return for an edge {@code (o,n)}, return {@code o}.
     */
    protected LNode getOther(final LEdge edge, final LNode n) {
        if (edge.getSource().getNode() == n) {
            return edge.getTarget().getNode();
        } else if (edge.getTarget().getNode() == n) {
            return edge.getSource().getNode();
        } else {
            throw new IllegalArgumentException("Node " + n
                    + " is neither source nor target of edge " + edge);
        }
    }

    
    /**
     * {@link ThresholdStrategy} for the classic compaction phase of the original bk algorithm.
     * 
     * It calculates a threshold value such that it has no effect.
     */
    public static class NullThresholdStrategy extends ThresholdStrategy {
        /**
         * {@inheritDoc}
         */
        @Override
        public double calculateThreshold(final double oldThresh, final LNode blockRoot,
                final LNode currentNode) {
            if (bal.vdir == VDirection.UP) {
                // new value calculated using min(a,thresh) --> thresh = +infty has no effect
                return Double.POSITIVE_INFINITY;
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void postProcess() {
        }
    }

    /**
     * <ul>
     *  <li> Only calculates threshold for the first and last node of a block.</li>
     *  <li> Picks the first edge it encounters that is valid.</li>
     * </ul>
     */
    public static class SimpleThresholdStrategy extends ThresholdStrategy {
        
        /**
         * {@inheritDoc}
         */
        public double calculateThreshold(final double oldThresh,
                final LNode blockRoot, final LNode currentNode) {
            
            // just the root or last node of a block
            
            // Remember that for blocks with a single node both flags can be true
            boolean isRoot = blockRoot.equals(currentNode);
            boolean isLast = bal.align[currentNode.id].equals(blockRoot);
            
            if (!(isRoot || isLast)) {
                return oldThresh;
            }
            
            // Remember two things:
            //  1) it is not guaranteed that adjacent nodes are already placed
            //  2) blocks can consist of a single node implying that the current
            //     node is both the root and the last node
    
            double t = oldThresh;
            if (bal.hdir == HDirection.RIGHT) {
                
                if (isRoot) {
                    t = getBound(blockRoot, true);
                }
                if (Double.isInfinite(t) && isLast) {
                    t = getBound(currentNode, false);
                }
                
            } else { // LEFT
                
                if (isRoot) {
                    t = getBound(blockRoot, true);
                } 
                if (Double.isInfinite(t) && isLast) {
                    t = getBound(currentNode, false);
                }
            }
            
            return t;
        }
        
        
        /**
         * Only regards for root and last nodes of a block.
         * 
         * @param bal
         * @param currentNode
         *            a node of the block
         * @param isRoot
         *            whether {@code currentNode} is considered to be the root node of the current
         *            block. For a block that consists of a single node it is important to be able
         *            to regard it as root as well as as last node of a block.
         * 
         * @return a pair with an {@link LEdge} and a {@link Boolean}. If no valid edge was picked,
         *         the pair's first element is {@code null} and the second element indicates if
         *         there are possible candidate edges that might become valid at a later stage.
         */
        private Postprocessable pickEdge(final Postprocessable pp) {
        
            Iterable<LEdge> edges;
            if (pp.isRoot) {
                edges = bal.hdir == HDirection.RIGHT
                            ? pp.free.getIncomingEdges() : pp.free.getOutgoingEdges();
            } else {
                edges = bal.hdir == HDirection.LEFT 
                            ? pp.free.getIncomingEdges() : pp.free.getOutgoingEdges();
            }
            
            boolean hasEdges = false;
            for (LEdge e : edges) {
                
                // ignore in-layer edges
                if (ni.layerIndex[e.getSource().getNode().getLayer().id] 
                        == ni.layerIndex[e.getTarget().getNode().getLayer().id]) {
                    continue;
                }
                
                hasEdges = true;
                
                // if the other node does not have a position yet, ignore this edge
                if (blockFinished.contains(bal.root[getOther(e, pp.free).id])) {
                    pp.hasEdges = true;
                    pp.edge = e;
                    return pp;
                }
            }
            
            // no edge picked
            pp.hasEdges = hasEdges;
            pp.edge = null;
            return pp;
        }
        
    
        /**
         * 
         */
        private double getBound(final LNode blockNode,
                final boolean isRoot) {
    
            double invalid = bal.vdir == VDirection.UP 
                    ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    
            final Postprocessable pick = pickEdge(new Postprocessable(blockNode, isRoot));
            
            // if edges exist but we couldn't find a good one
            if (pick.edge == null && pick.hasEdges) {
                 postProcessablesQueue.add(pick);
                 return invalid;
            } else if (pick.edge != null) {
    
                double threshold;
                LPort left = pick.edge.getSource();
                LPort right = pick.edge.getTarget();
    
                if (isRoot) {
                    // We handle the root (first) node of a block here
                    LPort rootPort = bal.hdir == HDirection.RIGHT ? right : left;
                    LPort otherPort = bal.hdir == HDirection.RIGHT ? left : right;
        
                    LNode otherRoot = bal.root[otherPort.getNode().id];
                    threshold = bal.y[otherRoot.id] 
                                      + bal.innerShift[otherPort.getNode().id]
                                      + otherPort.getPosition().y 
                                      + otherPort.getAnchor().y
                                      // root node
                                      - bal.innerShift[rootPort.getNode().id] 
                                      - rootPort.getPosition().y
                                      - rootPort.getAnchor().y;
                } else {
                    
                    // ... and the last node of a block here 
                    LPort rootPort = bal.hdir == HDirection.LEFT ? right : left;
                    LPort otherPort = bal.hdir == HDirection.LEFT ? left : right;
    
                    threshold = bal.y[bal.root[otherPort.getNode().id].id]
                            + bal.innerShift[otherPort.getNode().id]
                            + otherPort.getPosition().y
                            + otherPort.getAnchor().y
                            // root node
                            - bal.innerShift[rootPort.getNode().id]
                            - rootPort.getPosition().y
                            - rootPort.getAnchor().y;
                }
                return threshold;
            }
            return invalid;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void postProcess() {
            
            // try original iteration order
            while (!postProcessablesQueue.isEmpty()) {
                
                // first is the node, second whether it is regarded as root
                Postprocessable pp = postProcessablesQueue.poll();
                Postprocessable pick = pickEdge(pp);
                
                if (pick.edge == null) {
                    continue;
                }
                
                LEdge edge = pick.edge;
                
                // ignore in-layer edges
                if (ni.layerIndex[edge.getSource().getNode().getLayer().id] 
                        == ni.layerIndex[edge.getTarget().getNode().getLayer().id]) {
                    continue;
                }

                // try to straighten the edge ...
                boolean moved = process(pp);
                // if it wasn't possible try again later in the opposite iteration direction
                if (!moved) {
                    postProcessablesStack.push(pp);
                }
                
            }

            // reversed iteration order
            while (!postProcessablesStack.isEmpty()) {
                process(postProcessablesStack.pop());
            }
        }
        
        private boolean process(final Postprocessable pp) {
            assert pp.edge != null;
            
            LEdge edge = pp.edge;
            LPort fix;
            if (edge.getSource().getNode() == pp.free) {
                fix = edge.getTarget();
            } else {
                fix = edge.getSource();
            }
            LPort block; 
            if (edge.getSource().getNode() == pp.free) {
                block = edge.getSource();
            } else {
                block = edge.getTarget();
            }

            // t has to be the root node of a different block
            double delta = bal.calculateDelta(fix, block);
            
            if (delta > 0 && delta < THRESHOLD) {
                // target y larger than source y --> shift upwards?
                double availableSpace = bal.checkSpaceAbove(block.getNode(), delta);
                assert DoubleMath.fuzzyEquals(availableSpace, 0, EPSILON) || availableSpace >= 0;
                bal.shiftBlock(block.getNode(), -availableSpace);
                
                return availableSpace > 0;
            } else if (delta < 0 && -delta < THRESHOLD) {
                
                // direction is up, we possibly shifted some blocks too far upward 
                // for an edge to be straight, so check if we can shift down again
                double availableSpace = bal.checkSpaceBelow(block.getNode(), -delta);
                assert DoubleMath.fuzzyEquals(availableSpace, 0, EPSILON) || availableSpace >= 0;
                bal.shiftBlock(block.getNode(), availableSpace);
                
                return availableSpace > 0;
            }
            
            return false;
        }
    }
    
    /** 
     * Represents a unit to be post-processed. 
     */
    private static class Postprocessable {
        // SUPPRESS CHECKSTYLE NEXT 8 VisibilityModifier
        /** the node whose block can potentially be moved. */
        LNode free;
        /** whether {@code free} is the root node of its block. */
        boolean isRoot;
        /** whether {@code free} has edges. */
        boolean hasEdges;
        /** the edge that was selected to be straightened. */
        LEdge edge;
        
        public Postprocessable(final LNode free, final boolean isRoot) {
            this.free = free;
            this.isRoot = isRoot;
        }
    }
    
    /**
     * The following code does not work in its current form. It contains
     * a more sophisticated idea of the {@link #pickEdge(LNode, boolean)} 
     * method that might be subject to future work and 
     * re-activated in the future.
     * 
     * @experimental
     */
    public static class SophisticatedThresholdStrategy extends ThresholdStrategy {

        /**
         * {@inheritDoc}
         */
        @Override
        public double calculateThreshold(final double oldThresh, final LNode blockRoot,
                final LNode currentNode) {
            calculateThreshold(bal, oldThresh, 0, blockRoot, currentNode);
            return 0;
        }

        private double calculateThreshold(final BKAlignedLayout bal, final double currentThreshold,
                final double suggestion, final LNode root, final LNode currentNode) {

            double t = currentThreshold;

            // Remember that for blocks with a single node both flags can be true
            boolean isRoot = root.equals(currentNode);
            boolean isLast = bal.align[currentNode.id].equals(root);

            if (!(isRoot || isLast)) {
                return t;
            }

            if (bal.hdir == HDirection.RIGHT) {

                // Note that it is not guaranteed that adjacent nodes are already placed!

                if (isRoot) {
                    t = getBound(bal, root, true, suggestion);
                }
                if (Double.isInfinite(t) && isLast) {
                    t = getBound(bal, currentNode, false, suggestion);
                }

            } else { // LEFT

                if (isRoot) {
                    t = getBound(bal, root, true, suggestion);
                }
                if (Double.isInfinite(t) && isLast) {
                    t = getBound(bal, currentNode, false, suggestion);
                }
            }

            return t;
        }

        /**
         * Picks an edge that we consider the best choice when seeking for additional alignments
         * between nodes of distinct blocks.
         */
        private Pair<LEdge, Boolean> pickEdge(final BKAlignedLayout bal, final LNode root,
                final boolean isRoot, final double suggestion, final boolean inverted) {

            Iterable<LEdge> edges;
            if (isRoot) {
                edges =
                        bal.hdir == HDirection.RIGHT ? root.getIncomingEdges() : root
                                .getOutgoingEdges();
            } else {
                edges =
                        bal.hdir == HDirection.LEFT ? root.getIncomingEdges() : root
                                .getOutgoingEdges();
            }

            // pick the edge to check, we want to use the edge that
            // connects to the closest node in iteration direction
            LEdge pick = null;
            double distance = Double.MAX_VALUE;
            boolean hasEdges = false;
            for (LEdge e : edges) {
                hasEdges = true;
                LPort left = e.getSource();
                LPort right = e.getTarget();
                LPort rootPort, otherPort;
                if (isRoot) {
                    rootPort = bal.hdir == HDirection.RIGHT ? right : left;
                    otherPort = bal.hdir == HDirection.RIGHT ? left : right;
                } else {
                    rootPort = bal.hdir == HDirection.LEFT ? right : left;
                    otherPort = bal.hdir == HDirection.LEFT ? left : right;
                }

                // if the other node does not have a position yet, ignore this edge
                if (!blockFinished.contains(bal.root[otherPort.getNode().id])) {
                    continue;
                }

                LNode otherRoot = bal.root[otherPort.getNode().id];

                double otherPos =
                        bal.y[otherRoot.id] + bal.innerShift[otherPort.getNode().id]
                                + otherPort.getPosition().y + otherPort.getAnchor().y;

                double rootPos =
                        suggestion + bal.innerShift[rootPort.getNode().id]
                                + rootPort.getPosition().y + rootPort.getAnchor().y;

                double curDistance = Math.abs(otherPos - rootPos);
                if (!inverted) {
                    if (bal.vdir == VDirection.DOWN) {
                        if (otherPos > rootPos && curDistance < distance) {
                            pick = e;
                            distance = curDistance;
                        }
                    } else {
                        if (otherPos < rootPos && curDistance < distance) {
                            pick = e;
                            distance = curDistance;
                        }
                    }
                } else {
                    if (bal.vdir == VDirection.DOWN) {
                        if (otherPos < rootPos && curDistance < distance) {
                            pick = e;
                            distance = curDistance;
                        }
                    } else {
                        if (otherPos > rootPos && curDistance < distance) {
                            pick = e;
                            distance = curDistance;
                        }
                    }
                }
            }

            return Pair.of(pick, hasEdges);
        }

        /**
         * Get threshold value for the <b>root</b> node of the block to be aligned! (As opposed to
         * the last node in the block.
         */
        private double getBound(final BKAlignedLayout bal, final LNode blockNode,
                final boolean isRoot, final double suggestion) {

            double invalid =
                    bal.vdir == VDirection.UP ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;

            final Pair<LEdge, Boolean> pick = pickEdge(bal, blockNode, isRoot, suggestion, false);

            // if edges exist but we couldn't find a good one
            if (pick.getFirst() == null && pick.getSecond()) {
                postProcessablesQueue.add(new Postprocessable(blockNode, isRoot));
                return invalid;
            } else if (pick.getFirst() != null) {

                double threshold;
                LPort left = pick.getFirst().getSource();
                LPort right = pick.getFirst().getTarget();

                if (isRoot) {
                    // We handle the root (first) node of a block here
                    LPort rootPort = bal.hdir == HDirection.RIGHT ? right : left;
                    LPort otherPort = bal.hdir == HDirection.RIGHT ? left : right;

                    LNode otherRoot = bal.root[otherPort.getNode().id];
                    threshold =
                            bal.y[otherRoot.id] + bal.innerShift[otherPort.getNode().id]
                                    + otherPort.getPosition().y + otherPort.getAnchor().y
                                    // root node
                                    - bal.innerShift[rootPort.getNode().id]
                                    - rootPort.getPosition().y - rootPort.getAnchor().y;
                } else {

                    // ... and the last node of a block here
                    LPort rootPort = bal.hdir == HDirection.LEFT ? right : left;
                    LPort otherPort = bal.hdir == HDirection.LEFT ? left : right;

                    threshold =
                            bal.y[bal.root[otherPort.getNode().id].id]
                                    + bal.innerShift[otherPort.getNode().id]
                                    + otherPort.getPosition().y + otherPort.getAnchor().y
                                    // root node
                                    - bal.innerShift[rootPort.getNode().id]
                                    - rootPort.getPosition().y - rootPort.getAnchor().y;
                }
                return threshold;
            }
            return invalid;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void postProcess() {

            while (!postProcessablesQueue.isEmpty()) {
                Postprocessable pair = postProcessablesQueue.poll();
                System.out.println("PostProcesS: " + pair);

                // TODO !!! it is quite important to check both directions here!
                // why ... elaborate
                Pair<LEdge, Boolean> pick =
                        pickEdge(bal, pair.free, pair.isRoot,
                                bal.y[pair.free.id], true);

                if (pick.getFirst() == null) {
                    pick =
                            pickEdge(bal, pair.free, pair.isRoot,
                                    bal.y[pair.free.id], false);
                }

                if (!pick.getSecond()) {
                    continue;
                }

                if (pick.getFirst() == null) {
                    continue;
                }

                LEdge edge = pick.getFirst();
                LPort left = edge.getSource();
                LPort right = edge.getTarget();
                LPort block = bal.hdir == HDirection.LEFT ? right : left;
                LPort fix = bal.hdir == HDirection.LEFT ? left : right;

                // t has to be the root node of a different block
                double delta = bal.calculateDelta(fix, block);

                if (delta > 0 && delta < THRESHOLD) {

                    // target y larger than source y --> shift upwards?
                    if (bal.checkSpaceAbove(block.getNode(), delta) == delta) {
                        bal.shiftBlock(block.getNode(), -delta);
                    }
                } else if (delta < 0 && -delta < THRESHOLD) {

                    // direction is up, we possibly shifted some blocks too far upward
                    // for an edge to be straight, so check if we can shift down again

                    // target y smaller than source y --> shift down?
                    if (bal.checkSpaceBelow(block.getNode(), -delta) == delta) {
                        bal.shiftBlock(block.getNode(), -delta);
                    }
                }

            }
        }
    }
    
}
