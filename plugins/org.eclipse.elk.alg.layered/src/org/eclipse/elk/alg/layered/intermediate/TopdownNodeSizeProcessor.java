/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.topdownpacking.options.TopdownpackingOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * @author mka
 * 
 * When performing topdown layout the nodes need to be assigned sizes before attempting to place them in phase 4 of
 * the layered algorithm.
 * TODO: add proper documentation
 * TODO: determine if this really is necessary and if yes what needs to be done
 *
 */
public class TopdownNodeSizeProcessor implements ILayoutProcessor<LGraph> {

    @Override
    public void process(LGraph graph, IElkProgressMonitor progressMonitor) {
        // TODO: only do this if topdown layout is being used, need core option set on graph for this
        // TODO: execution not even reaching here, is this really necessary?
        boolean topdownlayout = true;
        if (!topdownlayout) {
            return;
        }
        for (Layer layer : graph) { 
            for (LNode node : layer.getNodes()) {
                node.getSize().x = node.getProperty(TopdownpackingOptions.DESIRED_WIDTH);
                node.getSize().y = 
                        node.getProperty(TopdownpackingOptions.DESIRED_WIDTH)
                        / node.getProperty(TopdownpackingOptions.DESIRED_ASPECT_RATIO);
            }
        }
        
    }

}
