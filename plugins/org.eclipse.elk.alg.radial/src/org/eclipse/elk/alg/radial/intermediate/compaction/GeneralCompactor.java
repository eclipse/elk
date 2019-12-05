/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.radial.intermediate.compaction;

import org.eclipse.elk.alg.radial.options.RadialOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * The LayoutProcessor for compaction.
 */
public class GeneralCompactor implements ILayoutProcessor<ElkNode> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final ElkNode graph, final IElkProgressMonitor progressMonitor) {
        IRadialCompactor compactor = graph.getProperty(RadialOptions.COMPACTOR).create();
        compactor.compact(graph);
    }
}