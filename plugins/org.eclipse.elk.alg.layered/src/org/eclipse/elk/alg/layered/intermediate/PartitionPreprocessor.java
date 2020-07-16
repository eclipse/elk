/*******************************************************************************
 * Copyright (c) 2016, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.stream.Collectors;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Streams;

/**
 * Reverses edges that run contrary to layout partitions. More specifically, an edge (a, b) is reversed if a is
 * configured for a higher layout partition than b. If all nodes have a partition set, the result is a graph that can
 * only contain cycles among nodes in the same partition, and no edge reversed by this processor can be part of a cycle.
 * Thus, the cycle breaker should not restore any such edge.
 * 
 * <p>
 * If there are nodes that do not have a partition configured, that's another story. Since there can be arbitrarily
 * many such nodes connected in arbitrary ways, any edge we reverse here could be part of a cycle and thus be restored
 * again during cycle breaking. We try to avoid this by imposing a high direction priority on them, but we cannot
 * guarantee success.
 * </p>
 *
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>an unlayered graph.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>edges that contradict layout partitions are reversed.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 1.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>None.</dd>
 * </dl>
 * 
 * @see PartitionMidprocessor
 * @see PartitionPostprocessor
 */
public class PartitionPreprocessor implements ILayoutProcessor<LGraph> {

    /** The priority to set on added constraint edges. */
    private static final int PARTITION_CONSTRAINT_EDGE_PRIORITY = 50;

    @Override
    public void process(final LGraph lGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Partition preprocessing", 1);
        
        // Find all edges that must be reversed, and then reverse them (this needs to be a two-step process to avoid
        // ConcurrentModificationExceptions)
        lGraph.getLayerlessNodes().stream()
            .filter(lNode -> lNode.hasProperty(LayeredOptions.PARTITIONING_PARTITION))
            .flatMap(lNode -> Streams.stream(lNode.getOutgoingEdges()))
            .filter(lEdge -> mustBeReversed(lEdge))
            .collect(Collectors.toList())
            
            .stream()
            .forEach(lEdge -> reverse(lEdge, lGraph));
        
        monitor.done();
    }

    private boolean mustBeReversed(final LEdge lEdge) {
        assert lEdge.getSource().getNode().hasProperty(LayeredOptions.PARTITIONING_PARTITION);
        
        if (lEdge.getTarget().getNode().hasProperty(LayeredOptions.PARTITIONING_PARTITION)) {
            Integer sourcePartition = lEdge.getSource().getNode().getProperty(LayeredOptions.PARTITIONING_PARTITION);
            Integer targetPartition = lEdge.getTarget().getNode().getProperty(LayeredOptions.PARTITIONING_PARTITION);
            
            // We need to reverse an edge if sourcePartition > targetPartition
            return sourcePartition.compareTo(targetPartition) > 0;
            
        } else {
            return false;
        }
    }

    private void reverse(final LEdge lEdge, final LGraph lGraph) {
        lEdge.reverse(lGraph, true);
        lEdge.setProperty(LayeredOptions.PRIORITY_DIRECTION, PARTITION_CONSTRAINT_EDGE_PRIORITY);
    }

}
