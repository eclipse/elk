/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.spore;

import org.eclipse.elk.alg.spore.graph.Graph;
import org.eclipse.elk.alg.spore.options.SporeCompactionOptions;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * Layout provider to connect the ShrinkTree compaction to ELK.
 */
public class ShrinkTreeLayoutProvider extends AbstractLayoutProvider {

    /** the compaction algorithm. */
    private ShrinkTree shrinktree = new ShrinkTree();

    /**
     * {@inheritDoc}
     */
    @Override
    public void layout(final ElkNode layoutGraph, final IElkProgressMonitor progressMonitor) {
        
        // If desired, apply a layout algorithm
        if (layoutGraph.hasProperty(SporeCompactionOptions.UNDERLYING_LAYOUT_ALGORITHM)) {
            String requestedAlgorithm = layoutGraph.getProperty(SporeCompactionOptions.UNDERLYING_LAYOUT_ALGORITHM);
            LayoutAlgorithmData lad = LayoutMetaDataService.getInstance().getAlgorithmDataBySuffix(requestedAlgorithm);
            if (lad != null) {
                AbstractLayoutProvider layoutProvider = lad.getInstancePool().fetch();
                layoutProvider.layout(layoutGraph, progressMonitor.subTask(1));
            }
        }
        
        IGraphImporter<ElkNode> graphImporter = new ElkGraphImporter();
        Graph graph = graphImporter.importGraph(layoutGraph);
        
        shrinktree.shrink(graph, progressMonitor.subTask(1));
        
        graphImporter.applyPositions(graph);
    }

}
