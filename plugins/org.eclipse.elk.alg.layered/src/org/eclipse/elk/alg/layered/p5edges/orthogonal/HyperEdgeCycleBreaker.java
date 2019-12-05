/*******************************************************************************
 * Copyright (c) 2010, 2019 Kiel University and others.
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
 * Breaks cycles in the conflict graph of {@link HyperEdgeSegment}s and {@link SegmentDependency}s by removing
 * dependencies. The hope is to remove the minimum-weight set of dependencies such that the graph becomes acyclic.
 */
public final class HyperEdgeCycleBreaker {
    
    /**
     * No instance required.
     */
    private HyperEdgeCycleBreaker() {
        assert false;
    }

    /**
     * Breaks all cycles in the given hypernode structure by reversing or removing some dependencies. This
     * implementation assumes that the dependencies of zero weight are exactly the two-cycles of the hypernode
     * structure.
     *
     * @param nodes
     *            list of hypernodes
     * @param random
     *            random number generator
     */
    public static void breakCycles(final List<HyperEdgeSegment> nodes, final Random random) {
        LinkedList<HyperEdgeSegment> sources = Lists.newLinkedList();
        LinkedList<HyperEdgeSegment> sinks = Lists.newLinkedList();

        // initialize values for the algorithm
        int nextMark = -1;
        for (HyperEdgeSegment node : nodes) {
            node.setMark(nextMark--);
            int inweight = 0, outweight = 0;

            for (SegmentDependency dependency : node.getOutgoingDependencies()) {
                outweight += dependency.getWeight();
            }

            for (SegmentDependency dependency : node.getIncomingDependencies()) {
                inweight += dependency.getWeight();
            }

            node.setInWeight(inweight);
            node.setOutWeight(outweight);

            if (outweight == 0) {
                sinks.add(node);
            } else if (inweight == 0) {
                sources.add(node);
            }
        }

        // assign marks to all nodes, ignore dependencies of weight zero
        Set<HyperEdgeSegment> unprocessed = Sets.newTreeSet(nodes);
        int markBase = nodes.size();
        int nextRight = markBase - 1, nextLeft = markBase + 1;
        List<HyperEdgeSegment> maxNodes = new ArrayList<HyperEdgeSegment>();

        while (!unprocessed.isEmpty()) {
            while (!sinks.isEmpty()) {
                HyperEdgeSegment sink = sinks.removeFirst();
                unprocessed.remove(sink);
                sink.setMark(nextRight--);
                updateNeighbors(sink, sources, sinks);
            }

            while (!sources.isEmpty()) {
                HyperEdgeSegment source = sources.removeFirst();
                unprocessed.remove(source);
                source.setMark(nextLeft++);
                updateNeighbors(source, sources, sinks);
            }

            int maxOutflow = Integer.MIN_VALUE;
            for (HyperEdgeSegment node : unprocessed) {
                int outflow = node.getOutWeight() - node.getInWeight();
                if (outflow >= maxOutflow) {
                    if (outflow > maxOutflow) {
                        maxNodes.clear();
                        maxOutflow = outflow;
                    }
                    maxNodes.add(node);
                }
            }

            if (!maxNodes.isEmpty()) {
                // if there are multiple hypernodes with maximal outflow, select one randomly
                HyperEdgeSegment maxNode = maxNodes.get(random.nextInt(maxNodes.size()));
                unprocessed.remove(maxNode);
                maxNode.setMark(nextLeft++);
                updateNeighbors(maxNode, sources, sinks);
                maxNodes.clear();
            }
        }

        // shift ranks that are left of the mark base
        int shiftBase = nodes.size() + 1;
        for (HyperEdgeSegment node : nodes) {
            if (node.getMark() < markBase) {
                node.setMark(node.getMark() + shiftBase);
            }
        }

        // process edges that point left: remove those of zero weight, reverse the others
        for (HyperEdgeSegment source : nodes) {
            ListIterator<SegmentDependency> depIter = source.getOutgoingDependencies().listIterator();
            while (depIter.hasNext()) {
                SegmentDependency dependency = depIter.next();
                HyperEdgeSegment target = dependency.getTarget();

                if (source.getMark() > target.getMark()) {
                    depIter.remove();
                    target.getIncomingDependencies().remove(dependency);

                    if (dependency.getWeight() > 0) {
                        dependency.setSource(target);
                        target.getOutgoingDependencies().add(dependency);
                        dependency.setTarget(source);
                        source.getIncomingDependencies().add(dependency);
                    }
                }
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
        for (SegmentDependency dep : node.getOutgoingDependencies()) {
            HyperEdgeSegment target = dep.getTarget();
            if (target.getMark() < 0 && dep.getWeight() > 0) {
                target.setInWeight(target.getInWeight() - dep.getWeight());
                if (target.getInWeight() <= 0 && target.getOutWeight() > 0) {
                    sources.add(target);
                }
            }
        }

        // process preceding nodes
        for (SegmentDependency dep : node.getIncomingDependencies()) {
            HyperEdgeSegment source = dep.getSource();
            if (source.getMark() < 0 && dep.getWeight() > 0) {
                source.setOutWeight(source.getOutWeight() - dep.getWeight());
                if (source.getOutWeight() <= 0 && source.getInWeight() > 0) {
                    sinks.add(source);
                }
            }
        }
    }
    
}
