/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.radial.intermediate.compaction;

import org.eclipse.elk.graph.ElkNode;

/**
 * An interface for compacting the radial layout.
 */
public interface IRadialCompactor {
    /**
     * Compact the graph.
     * 
     * @param graph
     *            The graph which is already radial.
     */
    void compact(ElkNode graph);
}