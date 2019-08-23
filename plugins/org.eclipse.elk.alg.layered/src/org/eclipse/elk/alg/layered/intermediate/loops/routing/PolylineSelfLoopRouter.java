/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.loops.routing;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfHyperLoop;
import org.eclipse.elk.alg.layered.intermediate.loops.SelfLoopEdge;
import org.eclipse.elk.core.math.KVectorChain;

/**
 * Routes self loops with polylines. This is basically the same routing as computed by the
 * {@link OrthogonalSelfLoopRouter}, but with the corners cut a little.
 */
public class PolylineSelfLoopRouter extends OrthogonalSelfLoopRouter {

    @Override
    public void routeSelfLoop(final SelfHyperLoop slLoop) {
        for (SelfLoopEdge slEdge : slLoop.getSLEdges()) {
            LEdge lEdge = slEdge.getLEdge();
            
            KVectorChain orthogonalBendPoints = computeOrthogonalBendPoints(slEdge);
            KVectorChain polylineBendPoints = computePolylineBendPoints(orthogonalBendPoints);
            
            lEdge.getBendPoints().clear();
            lEdge.getBendPoints().addAll(polylineBendPoints);
        }
    }

    /**
     * Turns a vector chain of orthogonal bend points into polyline bend points by cutting the corners.
     */
    private KVectorChain computePolylineBendPoints(final KVectorChain orthogonalBendPoints) {
        // TODO Implement
        return orthogonalBendPoints;
    }
    
    
}
