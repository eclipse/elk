/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.loops.routing;

import org.eclipse.elk.alg.layered.intermediate.loops.SelfLoopEdge;
import org.eclipse.elk.core.math.KVectorChain;

/**
 * Routes self loops with polylines. This is basically the same routing as computed by the
 * {@link OrthogonalSelfLoopRouter}, but with the corners cut a little.
 */
public class PolylineSelfLoopRouter extends OrthogonalSelfLoopRouter {

    /**
     * Turns a vector chain of orthogonal bend points into polyline bend points by cutting the corners.
     */
    @Override
    protected KVectorChain modifyBendPoints(final SelfLoopEdge slEdge, final KVectorChain bendPoints) {
        // TODO Auto-generated method stub
        return super.modifyBendPoints(slEdge, bendPoints);
    }
    
}
