/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.routing;

import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNode;

/**
 * Provide an interface for the different self-loop routers. Each router has to provide a method for routing each
 * self-loop type. A default method, {@link #routeSelfLoop(OldSelfLoopEdge, OldSelfLoopNode)}, delegates to
 * the appropriate implementation.
 */
public interface ISelfLoopRouter {
    
    /**
     * Routes the given self loop.
     */
    void routeSelfLoop(OldSelfLoopEdge slEdge, OldSelfLoopNode slNode);
    
}
