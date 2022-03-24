/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://rtsys.informatik.uni-kiel.de/kieler
 * 
 * Copyright 2022 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 */
package org.eclipse.elk.alg.topdownpacking;

import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * @author mka
 *
 */
public class WhitespaceEliminator implements ILayoutPhase<TopdownPackingPhases, GridElkNode> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(GridElkNode layoutGraph, IElkProgressMonitor progressMonitor) {
        // Start progress monitor
        progressMonitor.begin("Whitespace elimination", 1);
        progressMonitor.log("Whitespace elimination began for node " + layoutGraph.getIdentifier());
        
        progressMonitor.logGraph(layoutGraph, "Graph after whitespace elimination");
        progressMonitor.done();
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutProcessorConfiguration<TopdownPackingPhases, GridElkNode> getLayoutProcessorConfiguration(
            GridElkNode graph) {
        return LayoutProcessorConfiguration.<TopdownPackingPhases, GridElkNode>create();
    }

}