/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.graph.transform;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.graph.ElkNode;

/**
 * Manages the transformation of ELK Graphs to LayeredGraphs. Sets the
 * {@link org.eclipse.elk.alg.layered.options.LayeredOptions#GRAPH_PROPERTIES GRAPH_PROPERTIES}
 * property on imported graphs.
 * 
 * @author msp
 * @author cds
 * @see ElkGraphImporter
 * @see ElkGraphLayoutTransferrer
 */
public class ElkGraphTransformer implements IGraphTransformer<ElkNode> {
    
    /**
     * {@inheritDoc}
     */
    public LGraph importGraph(final ElkNode graph) {
        return new ElkGraphImporter().importGraph(graph);
    }
    
    /**
     * {@inheritDoc}
     */
    public void applyLayout(final LGraph layeredGraph) {
        new ElkGraphLayoutTransferrer().applyLayout(layeredGraph);
    }
    
}
