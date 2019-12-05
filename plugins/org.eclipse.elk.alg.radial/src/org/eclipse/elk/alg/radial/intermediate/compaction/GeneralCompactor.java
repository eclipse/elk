/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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