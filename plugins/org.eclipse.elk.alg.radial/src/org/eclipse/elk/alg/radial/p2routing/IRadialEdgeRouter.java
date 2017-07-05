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

package org.eclipse.elk.alg.radial.p2routing;

import org.eclipse.elk.graph.ElkNode;

/**
 * An interface for routing edges in a radial layout.
 */
public interface IRadialEdgeRouter {
    /**
     * Route edges of a graph.
     * 
     * @param root
     *            The root node of the graph.
     */
    void routeEdges(ElkNode root);

}
