/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.topdownpacking;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.graph.ElkNode;

/**
 * A node placer has to provide a size prediction without computing a layout.
 */
public interface INodePlacer {
    
    /**
     * Computes the predicted required size of the graph and returns it without computing a full layout.
     * @param graph the input graph
     * @return the predicted size
     */
    KVector getPredictedSize(ElkNode graph);

}
