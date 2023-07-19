/*******************************************************************************
 * Copyright (c) 2023 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.radial.intermediate.rotation;

import org.eclipse.elk.alg.radial.options.RadialOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * The layout processor for rotation.
 *
 */
public class GeneralRotator implements ILayoutProcessor<ElkNode> {
    
    @Override
    public void process(final ElkNode graph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("General 'Rotator", 1);
        progressMonitor.logGraph(graph, "Before");
        IRadialRotator rotator = graph.getProperty(RadialOptions.ROTATOR).create();
        rotator.rotate(graph);
        progressMonitor.logGraph(graph, "After");
    }

}
