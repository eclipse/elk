/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    spoenemann - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core;

import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.util.InstancePool;

/**
 * Each layout algorithm must be wrapped with a meta data object in order to create instances of
 * its corresponding {@link AbstractLayoutProvider}. An implementation of such meta data is provided
 * in the {@code org.eclipse.elk.core.service} plugin.
 */
public interface ILayoutAlgorithmData {
    
    /**
     * Return an instance pool for layout providers. If multiple threads execute the layout
     * algorithm in parallel, each thread should use its own instance of the algorithm.
     *
     * @return a layout provider instance pool
     */
    InstancePool<AbstractLayoutProvider> getInstancePool();
    
    /**
     * Check whether the given graph feature is supported.
     * 
     * @param graphFeature the graph feature to check
     * @return {@code true} if the algorithm supports the given feature
     */
    boolean supportsFeature(GraphFeature graphFeature);

}
