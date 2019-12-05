/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.mrtree.p2order;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.elk.alg.mrtree.TreeLayoutPhases;
import org.eclipse.elk.alg.mrtree.graph.TEdge;
import org.eclipse.elk.alg.mrtree.graph.TGraph;
import org.eclipse.elk.alg.mrtree.graph.TNode;
import org.eclipse.elk.alg.mrtree.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.mrtree.options.InternalProperties;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.properties.PropertyHolderComparator;

/**
 * This phase orders the nodes of each level by separating the nodes into leaves and inner nodes.
 * It then fills whitespaces in the levels with corresponding leaves.
 * 
 * @author sor
 * @author sgu
 */
public class NodeOrderer implements ILayoutPhase<TreeLayoutPhases, TGraph> {

    /** intermediate processing configuration. */
    private static final LayoutProcessorConfiguration<TreeLayoutPhases, TGraph> INTERMEDIATE_PROCESSING_CONFIG =
            LayoutProcessorConfiguration.<TreeLayoutPhases, TGraph>create()
                    .before(TreeLayoutPhases.P2_NODE_ORDERING)
                        .add(IntermediateProcessorStrategy.ROOT_PROC)
                        .add(IntermediateProcessorStrategy.FAN_PROC);

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutProcessorConfiguration<TreeLayoutPhases, TGraph> getLayoutProcessorConfiguration(final TGraph graph) {
        return INTERMEDIATE_PROCESSING_CONFIG;
    }

    /**
     * {@inheritDoc}
     */
    public void process(final TGraph tGraph, final IElkProgressMonitor progressMonitor) {

        progressMonitor.begin("Processor arrange node", 1);

        // find the root of the component
        // expected only one root exists
        TNode root = null;
        LinkedList<TNode> roots = new LinkedList<TNode>();
        Iterator<TNode> it = tGraph.getNodes().iterator();
        while (root == null && it.hasNext()) {
            TNode tNode = it.next();
            if (tNode.getProperty(InternalProperties.ROOT)) {
                root = tNode;
            }
        }
        // order each level
        roots.add(root);
        orderLevel(roots, progressMonitor.subTask(1.0f));

        progressMonitor.done();

    }

    /**
     * Order each level by separating the nodes into leaves and inner nodes. And then fill gaps with
     * corresponding leaves.
     * 
     * @param currentLevel
     * @param progressMonitor
     */
    private void orderLevel(final LinkedList<TNode> currentLevel,
            final IElkProgressMonitor progressMonitor) {

        progressMonitor.begin("Processor arrange level", 1);

        int pos = 0;

        // sort all nodes in this level by their fan out
        // so the leaves are at the end of the list
        Collections.sort(currentLevel, PropertyHolderComparator.with(InternalProperties.FAN));

        // find the first occurence of a leave in the list
        int firstOcc = currentLevel.size();
        ListIterator<TNode> it = currentLevel.listIterator(currentLevel.size());
        boolean notNull = true;
        while (notNull && it.hasPrevious()) {
            TNode tNode = (TNode) it.previous();
            if ((tNode.getProperty(InternalProperties.FAN) == 0)) {
                firstOcc--;
            } else {
                notNull = false;
            }
        }

        // seperate the level into leaves and inner nodes
        List<TNode> tmp = currentLevel.subList(0, firstOcc);
        LinkedList<TNode> inners = new LinkedList<TNode>(tmp);
        tmp = currentLevel.subList(firstOcc, currentLevel.size());
        LinkedList<TNode> leaves = new LinkedList<TNode>(tmp);

        // check if their are inner nodes left
        if (inners.isEmpty()) {
            // leave the leaves in their order
            for (TNode tENode : leaves) {
                tENode.setProperty(InternalProperties.POSITION, pos++);
            }
        } else {

            // order each level of descendants of the inner nodes
            int size = inners.size();
            for (TNode tPNode : inners) {
                tPNode.setProperty(InternalProperties.POSITION, pos++);

                // set the position of the children and set them in order
                LinkedList<TNode> children = tPNode.getChildrenCopy();
                orderLevel(children, progressMonitor.subTask(1 / size));

                // order the children by their reverse position
                Collections.sort(children,
                        Collections.reverseOrder(PropertyHolderComparator.with(InternalProperties.POSITION)));

                // reset the list of children with the new order
                List<TEdge> sortedOutEdges = new LinkedList<TEdge>();

                for (TNode tNode : children) {
                    for (TEdge tEdge : tPNode.getOutgoingEdges()) {
                        if (tEdge.getTarget() == tNode) {
                            sortedOutEdges.add(tEdge);
                        }
                    }
                }
                tPNode.getOutgoingEdges().clear();
                tPNode.getOutgoingEdges().addAll(sortedOutEdges);

                // fill gaps with leafs
                it = leaves.listIterator(leaves.size());
                int fillGap = tPNode.getOutgoingEdges().size();
                notNull = true;
                while ((0 < fillGap) && notNull && it.hasPrevious()) {
                    TNode tNode = (TNode) it.previous();
                    if ((tNode.getProperty(InternalProperties.FAN) == 0)) {
                        tNode.setProperty(InternalProperties.POSITION, pos++);
                        fillGap--;
                        it.remove();
                    } else {
                        notNull = false;
                    }
                }
            }
        }
        progressMonitor.done();
    }
}
