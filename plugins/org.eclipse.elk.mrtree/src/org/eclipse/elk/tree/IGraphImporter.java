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
package org.eclipse.elk.tree;

import org.eclipse.elk.tree.graph.TGraph;

/**
 * Interface for importer classes for graph structure. Graph importers
 * should usually subclass
 * {@link org.eclipse.elk.tree.KGraphImporter KGraphImporter}
 * instead of implementing this interface directly.
 * 
 * <p>Graph importers are encouraged to set the {@link Properties#GRAPH_PROPERTIES}
 * property on imported graphs.</p>
 *
 * @param <T> the type of graph that this importer can transform into a layered graph.
 * @author sor
 * @author sgu
 * @author msp
 */
public interface IGraphImporter<T> {
    
    /**
     * Create a t-graph from the given graph.
     * 
     * @param graph the graph to turn into a t-graph
     * @return a t-graph, or {@code null} if the input was not recognized
     */
    TGraph importGraph(T graph);
    
    /**
     * Apply the computed layout of a t-graph to the original graph.
     * 
     * @param tGraph the graph for which layout is applied
     */
    void applyLayout(TGraph tGraph);

}
