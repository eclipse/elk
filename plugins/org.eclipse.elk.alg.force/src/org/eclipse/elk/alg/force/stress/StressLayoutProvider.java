/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.force.stress;

import java.util.List;

import org.eclipse.elk.alg.force.ComponentsProcessor;
import org.eclipse.elk.alg.force.ElkGraphImporter;
import org.eclipse.elk.alg.force.ForceLayoutProvider;
import org.eclipse.elk.alg.force.IGraphImporter;
import org.eclipse.elk.alg.force.graph.FGraph;
import org.eclipse.elk.alg.force.options.StressOptions;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * Layout provider for stress-minimizing layouts. 
 * 
 * @author uru
 */
public class StressLayoutProvider extends AbstractLayoutProvider {

    /** connected components processor. */
    private ComponentsProcessor componentsProcessor = new ComponentsProcessor();
    /** implementation of stress majorization. */
    private StressMajorization stressMajorization = new StressMajorization();

    /**
     * {@inheritDoc}
     */
    @Override
    public void layout(final ElkNode layoutGraph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("ELK Stress", 1);

        // calculate initial coordinates
        if (!layoutGraph.getProperty(StressOptions.INTERACTIVE)) {
            new ForceLayoutProvider().layout(layoutGraph, progressMonitor.subTask(1));
        }
        
        // transform the input graph
        IGraphImporter<ElkNode> graphImporter = new ElkGraphImporter();
        FGraph fgraph = graphImporter.importGraph(layoutGraph);

        // split the input graph into components
        List<FGraph> components = componentsProcessor.split(fgraph);

        // perform the actual layout
        for (FGraph subGraph : components) {
            stressMajorization.initialize(subGraph);
            stressMajorization.execute();
        }

        // pack the components back into one graph
        fgraph = componentsProcessor.recombine(components);

        // apply the layout results to the original graph
        graphImporter.applyLayout(fgraph);

        progressMonitor.done();
    }

}
