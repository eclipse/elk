/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
