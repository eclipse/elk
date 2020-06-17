/*******************************************************************************
 * Copyright (c) 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.Collections;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.preserveorder.ModelOrderNodeComparator;
import org.eclipse.elk.alg.layered.intermediate.preserveorder.ModelOrderPortComparator;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Sorts all child nodes and their edges of the given graph by their {@link InternalProperties#MODEL_ORDER},
 * which represents the order in input graph.
 * Outgoing ports are sorted by the order of their edges, incoming ports are sorted by the order of their nodes in the
 * previous layer.
 * <dl><dl>
 *   <dt>Precondition:</dt>
 *     <dd>a layered graph</dd>
 *     <dd>nodes have fixed port sides.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>Nodes and ports (edges) are sorted to respect the order in the input graph</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 3.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>Since this changes the order of ports and nodes, it is not compatible with other processors
 *     that do the same.</dd>
 * </dl>
 */
public class SortByInputModelProcessor implements ILayoutProcessor<LGraph> {

    @Override
    public void process(final LGraph graph, final IElkProgressMonitor progressMonitor) {
        int layerIndex = 0;
        for (Layer layer : graph) {
            final int previousLayerIndex = layerIndex == 0 ? 0 : layerIndex - 1;
            Layer previousLayer = graph.getLayers().get(previousLayerIndex);
            for (LNode node : layer.getNodes()) {
                Collections.sort(node.getPorts(), new ModelOrderPortComparator(previousLayer));
            }
            // Sort nodes.
            Collections.sort(layer.getNodes(), new ModelOrderNodeComparator(previousLayer));
            layerIndex++;
        }
    }
}

