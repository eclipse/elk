/*******************************************************************************
 * Copyright (c) 2019, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import org.eclipse.elk.alg.layered.DebugUtil;
import org.eclipse.elk.alg.layered.JsonDebugUtil;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.LoggedGraph;

/**
 * Adds to each LNode the layerID and positionID that has been computed by ELK Layered.
 */
public final class ConstraintsPostprocessor implements ILayoutProcessor<LGraph> {

    @Override
    public void process(final LGraph graph, final IElkProgressMonitor progressMonitor) {

        progressMonitor.begin("Constraints Postprocessor", 1);
        if (progressMonitor.isLoggingEnabled()) {
            DebugUtil.logDebugGraph(progressMonitor, graph, 0, "ConstraintPostProcessor");
        }
        // Uses for each loops since they should be more efficient because LinkedLists are accessed.
        // It still creates counter variables so that no getter calls on layer and node are needed.
        int layerIndex = 0;

        for (Layer layer : graph.getLayers()) {
            int posIndex = 0;

            for (LNode currentNode : layer.getNodes()) {
                if (currentNode.getType() == NodeType.NORMAL) {
                    currentNode.setProperty(LayeredOptions.LAYERING_LAYER_I_D, layerIndex);
                    currentNode.setProperty(LayeredOptions.CROSSING_MINIMIZATION_POSITION_I_D, posIndex);
                    posIndex++;
                }
            }

            layerIndex++;
        }
        if (progressMonitor.isLoggingEnabled()) {
            DebugUtil.logDebugGraph(progressMonitor, graph, 0, "ConstraintPostProcessor");
        }
        progressMonitor.done();

    }

}
