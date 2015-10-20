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
package org.eclipse.elk.layered.graph.transform;

import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.layered.graph.LGraph;

/**
 * Manages the transformation of KGraphs to LayeredGraphs. Sets the
 * {@link org.eclipse.elk.layered.properties.Properties#GRAPH_PROPERTIES GRAPH_PROPERTIES}
 * property on imported graphs.
 * 
 * @author msp
 * @author cds
 * @see KGraphImporter
 * @see KGraphLayoutTransferrer
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating proposed yellow by msp
 */
public class KGraphTransformer implements IGraphTransformer<KNode> {
    
    /**
     * {@inheritDoc}
     */
    public LGraph importGraph(final KNode graph) {
        return new KGraphImporter().importGraph(graph);
    }
    
    /**
     * {@inheritDoc}
     */
    public void applyLayout(final LGraph layeredGraph) {
        new KGraphLayoutTransferrer().applyLayout(layeredGraph);
    }
    
}
