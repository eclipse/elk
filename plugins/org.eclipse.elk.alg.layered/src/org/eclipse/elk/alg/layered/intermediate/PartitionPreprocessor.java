/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
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
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Lists;

/**
 * Add constraint edges between partitions. </br>
 * In partitioned graphs, nodes may have an assigned partition. For every such node holds, that every other node
 * with a smaller partition is placed left to it. This is accomplished by adding partition constraint
 * edges. For each node, we add an edge to every node in the next greater partition. These edges get a
 * high priority assigned such that during cycle breaking, none of these constraint edges gets reversed.
 * During layering, the constraint edges assure the aforementioned condition.
 * The constraint edges are removed later by the {@link PartitionPostprocessor}.
 *
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>an unlayered graph.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>unlayered graph with partition constraint edges.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 1.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>None.</dd>
 * </dl>
 *
 * @see PartitionPostprocessor
 */
public class PartitionPreprocessor implements ILayoutProcessor<LGraph> {

    /** The priority to set on added constraint edges. */
    private static final int PARTITION_CONSTRAINT_EDGE_PRIORITY = 20;

    /** The nodes of the currently processed graph, sorted into partitions. */
    private List<List<LNode>> partitions;

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final LGraph lGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Adding partition constraint edges", 1);
        partitions = Lists.newArrayList();
        // Sort nodes into partitions.
        for (LNode node : lGraph.getLayerlessNodes()) {
            if (node.hasProperty(LayeredOptions.PARTITIONING_PARTITION)) {
                Integer index = node.getProperty(LayeredOptions.PARTITIONING_PARTITION);
                retrievePartition(index).add(node);
            }
        }

        // Add edges from each node to every node in the next greater partition.
        for (int i = 0; i < partitions.size() - 1; i++) {
            for (LNode node : partitions.get(i)) {
                LPort sourcePort = new LPort();
                sourcePort.setNode(node);
                sourcePort.setSide(PortSide.EAST);
                sourcePort.setProperty(InternalProperties.PARTITION_DUMMY, true);
                
                for (LNode otherNode : partitions.get(i + 1)) {
                    LPort targetPort = new LPort();
                    targetPort.setNode(otherNode);
                    targetPort.setSide(PortSide.WEST);
                    targetPort.setProperty(InternalProperties.PARTITION_DUMMY, true);
                    
                    LEdge edge = new LEdge();
                    edge.setProperty(InternalProperties.PARTITION_DUMMY, true);
                    edge.setProperty(LayeredOptions.PRIORITY_DIRECTION, PARTITION_CONSTRAINT_EDGE_PRIORITY);
                    edge.setSource(sourcePort);
                    edge.setTarget(targetPort);
                }
            }
        }
        partitions = null;
        monitor.done();
    }
    
    /**
     * Retrieve the partition with the given index. If doesn't exist yet, the list of partitions is
     * extended sufficiently.
     * 
     * @param index
     *            the index of the partition to retrieve.
     * @return the existing or newly created partition.
     */
    private List<LNode> retrievePartition(final int index) {
        while (index >= partitions.size()) {
            partitions.add(new ArrayList<LNode>());
        }
        return partitions.get(index);
    }

}
