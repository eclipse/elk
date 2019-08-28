/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.loops.routing;

import org.eclipse.elk.alg.layered.intermediate.loops.SelfLoopHolder;

/**
 * Abstract base class for self loop routers. 
 */
public abstract class AbstractSelfLoopRouter {
    
    /**
     * Routes the given self loop.
     */
    public abstract void routeSelfLoops(SelfLoopHolder slHolder);

}
