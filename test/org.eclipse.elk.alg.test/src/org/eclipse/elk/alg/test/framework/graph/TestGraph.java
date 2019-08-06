/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.graph;

import org.eclipse.elk.graph.ElkNode;

/**
 * Base class of strategies that know how to load graphs.
 */
public abstract class TestGraph {

    // Force sub classes to provide proper descriptions
    @Override
    public abstract String toString();

    /**
     * Applies the strategy to load a graph.
     * 
     * @param test
     *            instance of the test class that runs test methods.
     * @return the test graph.
     * @throws Throwable
     *             if anything goes wrong while trying to provide the graph.
     */
    public abstract ElkNode provideGraph(Object test) throws Throwable;

}
