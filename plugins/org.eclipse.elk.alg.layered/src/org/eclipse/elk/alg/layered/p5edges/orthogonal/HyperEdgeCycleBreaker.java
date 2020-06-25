/*******************************************************************************
 * Copyright (c) 2010, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.orthogonal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Breaks cycles in the conflict graph of {@link HyperEdgeSegment}s and {@link HyperEdgeSegmentDependency}s by removing
 * dependencies. The hope is to remove the minimum-weight set of dependencies such that the graph becomes acyclic.
 * Inspired by:
 * <ul>
 *   <li>Eades, Lin, Smyth. A fast and effective heuristic for the feedback arc set problem. In <i>Information
 *     Processing Letters</i>, 1993.</li>
 * </ul>
 */
public final class HyperEdgeCycleBreaker {
    
    /**
     * No instance required.
     */
    private HyperEdgeCycleBreaker() {
        assert false;
    }

    /**
     * Breaks all cycles in the given hyper edge segment structure by reversing or removing some dependencies. This
     * implementation assumes that the dependencies of zero weight are exactly the two-cycles of the hyper edge segment
     * structure.
     *
     * @param segments
     *            list of hyper edge segments
     * @param random
     *            random number generator
     */
    public static void breakCycles(final List<HyperEdgeSegment> segments, final Random random) {
        LinkedList<HyperEdgeSegment> sources = Lists.newLinkedList();
        LinkedList<HyperEdgeSegment> sinks = Lists.newLinkedList();

        // initialize values for the algorithm
        initialize(segments, sources, sinks);

        // assign marks to all nodes
        computeLinearOrderingMarks(segments, sources, sinks, random);

        // process edges that point left: remove those of zero weight, reverse the others
        for (HyperEdgeSegment source : segments) {
            ListIterator<HyperEdgeSegmentDependency> depIter = source.getOutgoingSegmentDependencies().listIterator();
            while (depIter.hasNext()) {
                HyperEdgeSegmentDependency dependency = depIter.next();
                HyperEdgeSegment target = dependency.getTarget();

                if (source.mark > target.mark) {
                    depIter.remove();
                    target.getIncomingSegmentDependencies().remove(dependency);

                    if (dependency.getWeight() > 0) {
                        dependency.setSource(target);
                        target.getOutgoingSegmentDependencies().add(dependency);
                        dependency.setTarget(source);
                        source.getIncomingSegmentDependencies().add(dependency);
                    }
                }
            }
        }
    }

    /**
     * Initializes the mark, in weight and out weight of each hyper edge segment. Also adds all sources (segments
     * without incoming weight 0) and sinks (segments with outgoing weight 0) to their respective lists. Once this is
     * complete, all segments are marked from {@code -1} to {@code -segments.size()}.
     */
    private static void initialize(final List<HyperEdgeSegment> segments, final List<HyperEdgeSegment> sources,
            final List<HyperEdgeSegment> sinks) {
        
        int nextMark = -1;
        for (HyperEdgeSegment segment : segments) {
            segment.mark = nextMark--;
            int inWeight = 0;
            int outWeight = 0;

            for (HyperEdgeSegmentDependency dependency : segment.getOutgoingSegmentDependencies()) {
                outWeight += dependency.getWeight();
            }

            for (HyperEdgeSegmentDependency dependency : segment.getIncomingSegmentDependencies()) {
                inWeight += dependency.getWeight();
            }

            segment.setInWeight(inWeight);
            segment.setOutWeight(outWeight);

            if (outWeight == 0) {
                sinks.add(segment);
            } else if (inWeight == 0) {
                sources.add(segment);
            }
        }
    }

    /**
     * Computes marks for all segments based on a linear ordering or the segments. Marks will be mutually different, and
     * sinks will have higher marks than sources.
     */
    private static void computeLinearOrderingMarks(final List<HyperEdgeSegment> segments,
            final LinkedList<HyperEdgeSegment> sources, final LinkedList<HyperEdgeSegment> sinks, final Random random) {
        
        Set<HyperEdgeSegment> unprocessed = Sets.newTreeSet(segments);
        List<HyperEdgeSegment> maxSegments = new ArrayList<HyperEdgeSegment>();
        
        // We'll mark sinks with marks < markBase and sources with marks > markBase.
        int markBase = segments.size();
        int nextRight = markBase - 1;
        int nextLeft = markBase + 1;

        while (!unprocessed.isEmpty()) {
            while (!sinks.isEmpty()) {
                HyperEdgeSegment sink = sinks.removeFirst();
                unprocessed.remove(sink);
                sink.mark = nextRight--;
                updateNeighbors(sink, sources, sinks);
            }

            while (!sources.isEmpty()) {
                HyperEdgeSegment source = sources.removeFirst();
                unprocessed.remove(source);
                source.mark = nextLeft++;
                updateNeighbors(source, sources, sinks);
            }

            // If any segments are still unprocessed, they are neither source nor sink. Assemble the list of segments
            // with the highest out flow (out weight - in weight).
            int maxOutflow = Integer.MIN_VALUE;
            for (HyperEdgeSegment segment : unprocessed) {
                int outflow = segment.getOutWeight() - segment.getInWeight();
                if (outflow >= maxOutflow) {
                    if (outflow > maxOutflow) {
                        maxSegments.clear();
                        maxOutflow = outflow;
                    }
                    maxSegments.add(segment);
                }
            }

            // If there are segments with maximal out flow, select one randomly; this might yield new sources and sinks
            if (!maxSegments.isEmpty()) {
                HyperEdgeSegment maxNode = maxSegments.get(random.nextInt(maxSegments.size()));
                unprocessed.remove(maxNode);
                maxNode.mark = nextLeft++;
                updateNeighbors(maxNode, sources, sinks);
                maxSegments.clear();
            }
        }

        // shift ranks that are left of the mark base so that sinks now have higher marks than sources
        int shiftBase = segments.size() + 1;
        for (HyperEdgeSegment node : segments) {
            if (node.mark < markBase) {
                node.mark = node.mark + shiftBase;
            }
        }
    }

    /**
     * Updates in-weight and out-weight values of the neighbors of the given node, simulating its removal from the
     * graph. The sources and sinks lists are also updated.
     *
     * @param node
     *            node for which neighbors are updated
     * @param sources
     *            list of sources
     * @param sinks
     *            list of sinks
     */
    private static void updateNeighbors(final HyperEdgeSegment node, final List<HyperEdgeSegment> sources,
            final List<HyperEdgeSegment> sinks) {

        // process following nodes
        for (HyperEdgeSegmentDependency dep : node.getOutgoingSegmentDependencies()) {
            HyperEdgeSegment target = dep.getTarget();
            if (target.mark < 0 && dep.getWeight() > 0) {
                target.setInWeight(target.getInWeight() - dep.getWeight());
                if (target.getInWeight() <= 0 && target.getOutWeight() > 0) {
                    sources.add(target);
                }
            }
        }

        // process preceding nodes
        for (HyperEdgeSegmentDependency dep : node.getIncomingSegmentDependencies()) {
            HyperEdgeSegment source = dep.getSource();
            if (source.mark < 0 && dep.getWeight() > 0) {
                source.setOutWeight(source.getOutWeight() - dep.getWeight());
                if (source.getOutWeight() <= 0 && source.getInWeight() > 0) {
                    sinks.add(source);
                }
            }
        }
    }
    
}
