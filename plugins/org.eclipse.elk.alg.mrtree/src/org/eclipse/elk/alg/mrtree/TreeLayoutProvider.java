/*******************************************************************************
 * Copyright (c) 2013, 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.mrtree;

import java.util.List;

import org.eclipse.elk.alg.common.nodespacing.NodeMicroLayout;
import org.eclipse.elk.alg.mrtree.graph.TGraph;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.adapters.ElkGraphAdapters;
import org.eclipse.elk.graph.ElkNode;

/**
 * Layout provider to connect the tree layouter to the Eclipse based layout services and orchestrate
 * the pre layout processing.
 */
public class TreeLayoutProvider extends AbstractLayoutProvider {

    // /////////////////////////////////////////////////////////////////////////////
    // Variables

    /** the layout algorithm used for this layout. */
    private MrTree mrTree = new MrTree();
    /** connected components processor. */
    private ComponentsProcessor componentsProcessor = new ComponentsProcessor();

    // /////////////////////////////////////////////////////////////////////////////
    // Regular Layout

    /**
     * {@inheritDoc}
     */
    @Override
    public void layout(final ElkNode graph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("ELK MrTree", 1);
        
        NodeMicroLayout.executeAll(ElkGraphAdapters.adapt(graph));
        
        // build tGraph
        IGraphImporter<ElkNode> graphImporter = new ElkGraphImporter();
        TGraph tGraph = graphImporter.importGraph(graph);

        // split the input graph into components
        List<TGraph> components = componentsProcessor.split(tGraph);

        // perform the actual layout on the components
        for (TGraph comp : components) {
            mrTree.doLayout(comp, progressMonitor.subTask(1.0f / components.size()));
        }

        // pack the components back into one graph
        tGraph = componentsProcessor.pack(components);

        // apply the layout results to the original graph
        graphImporter.applyLayout(tGraph);
        
        progressMonitor.done();
    }

}
