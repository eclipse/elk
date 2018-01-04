/*******************************************************************************
 * Copyright (c) 2010, 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.graph.transform;

import org.eclipse.elk.alg.sequence.graph.LayoutContext;

/**
 * Interface for importer classes for the layout context structure.
 *
 * @param <T> the type of graph that this importer can transform into a layout context.
 */
public interface IGraphTransformer<T> {
    
    /**
     * Create a layout context from the given graph.
     * 
     * @param graph the graph to turn into a layout context.
     * @return a layout context, or {@code null} if the input was not recognized
     */
    LayoutContext importGraph(T graph);
    
    /**
     * Apply the computed layout of the given layout context to the original input graph.
     * 
     * @param layoutContext the context for which layout is to be applied
     */
    void applyLayout(LayoutContext layoutContext);

}
