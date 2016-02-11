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
package org.eclipse.elk.alg.mrtree;

import java.util.List;

import org.eclipse.elk.alg.mrtree.graph.TGraph;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.KNode;

/**
 * Layout provider to connect the tree layouter to the Eclipse based layout services and orchestrate
 * the pre layout processing.
 * 
 * @author sor
 * @author sgu
 */
public class TreeLayoutProvider extends AbstractLayoutProvider {

    /** the layout provider id. */
    public static final String ID = "org.eclipse.elk.alg.mrtree";

    // /////////////////////////////////////////////////////////////////////////////
    // Variables

    /** the layout algorithm used for this layout. */
    private KlayTree klayTree = new KlayTree();
    /** connected components processor. */
    private ComponentsProcessor componentsProcessor = new ComponentsProcessor();

    // /////////////////////////////////////////////////////////////////////////////
    // Regular Layout

    /**
     * {@inheritDoc}
     */
    @Override
    public void layout(final KNode kgraph, final IElkProgressMonitor progressMonitor) {
        // build tGraph
        IGraphImporter<KNode> graphImporter = new KGraphImporter();
        TGraph tGraph = graphImporter.importGraph(kgraph);

        // split the input graph into components
        List<TGraph> components = componentsProcessor.split(tGraph);

        // perform the actual layout on the components
        for (TGraph comp : components) {
            klayTree.doLayout(comp, progressMonitor.subTask(1.0f / components.size()));
        }

        // pack the components back into one graph
        tGraph = componentsProcessor.pack(components);

        // apply the layout results to the original graph
        graphImporter.applyLayout(tGraph);
    }

}
