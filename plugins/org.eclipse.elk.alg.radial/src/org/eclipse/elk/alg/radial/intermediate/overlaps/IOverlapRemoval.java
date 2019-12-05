/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.radial.intermediate.overlaps;

import org.eclipse.elk.graph.ElkNode;

/**
 * Interface for overlap removal strategies.
 *
 */
public interface IOverlapRemoval {

    /**
     * Remove the existing overlaps from the graph.
     * 
     * @param graph
     *            The graph where all nodes are positioned but overlaps may appear.
     */
    void removeOverlaps(ElkNode graph);
}
