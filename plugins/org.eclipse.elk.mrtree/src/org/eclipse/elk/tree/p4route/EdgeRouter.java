/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.tree.p4route;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.tree.ILayoutPhase;
import org.eclipse.elk.tree.IntermediateProcessingConfiguration;
import org.eclipse.elk.tree.graph.TEdge;
import org.eclipse.elk.tree.graph.TGraph;
import org.eclipse.elk.tree.graph.TNode;

/**
 * TODO: implement smart edge routing
 * 
 * This class implements a dull edge routing by setting just source and target of a edge.
 * 
 * @author sor
 * @author sgu
 * 
 */
public class EdgeRouter implements ILayoutPhase {

    /** intermediate processing configuration. */
    private static final IntermediateProcessingConfiguration INTERMEDIATE_PROCESSING_CONFIGURATION = 
            new IntermediateProcessingConfiguration();

    /**
     * {@inheritDoc}
     */
    public IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(
            final TGraph tGraph) {
        return INTERMEDIATE_PROCESSING_CONFIGURATION;
    }

    /**
     * {@inheritDoc}
     */
    public void process(final TGraph tGraph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Dull edge routing", 1);

        for (TNode tnode : tGraph.getNodes()) {
            for (TEdge tedge : tnode.getOutgoingEdges()) {
                tedge.getBendPoints().clear();
            }
        }
    }

}
