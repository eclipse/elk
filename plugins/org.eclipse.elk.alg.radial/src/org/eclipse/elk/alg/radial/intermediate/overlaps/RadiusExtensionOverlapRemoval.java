/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.radial.intermediate.overlaps;

import java.util.List;

import org.eclipse.elk.alg.radial.InternalProperties;
import org.eclipse.elk.alg.radial.RadialUtil;
import org.eclipse.elk.alg.radial.intermediate.compaction.AbstractRadiusExtensionCompaction;
import org.eclipse.elk.alg.radial.options.RadialOptions;
import org.eclipse.elk.alg.radial.sorting.IRadialSorter;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * Remove overlaps after the initial layout by extending layers with overlaps.
 */
public class RadiusExtensionOverlapRemoval extends AbstractRadiusExtensionCompaction
        implements ILayoutProcessor<ElkNode>, IOverlapRemoval {

    private IRadialSorter sorter;

    @Override
    public void removeOverlaps(final ElkNode graph) {
        ElkNode root = graph.getProperty(InternalProperties.ROOT_NODE);
        setRoot(root);
        sorter = graph.getProperty(RadialOptions.SORTER).create();
        Double spacing = graph.getProperty(CoreOptions.SPACING_NODE_NODE);
        setSpacing(spacing);

        List<ElkNode> successors = RadialUtil.getSuccessors(root);
        extend(successors);
    }

    /**
     * Extend the first radius nodes until the nodes are non-overlapping.
     * 
     * @param nodes A list of the same radius.
     */
    public void extend(final List<ElkNode> nodes) {
        if (!nodes.isEmpty()) {

            while (overlapLayer(nodes)) {
                contractLayer(nodes, false);
            }

            List<ElkNode> nextLevelNodes = RadialUtil.getNextLevelNodes(nodes);
            if (sorter != null) {
                sorter.sort(nextLevelNodes);
                extend(nextLevelNodes);
            }
        }
    }

    @Override
    public void process(final ElkNode graph, final IElkProgressMonitor progressMonitor) {
        removeOverlaps(graph);
    }

}