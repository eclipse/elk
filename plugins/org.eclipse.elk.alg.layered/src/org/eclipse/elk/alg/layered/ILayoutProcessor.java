/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * A layout processor processes a {@link org.eclipse.elk.alg.layered.graph.LGraph},
 * performing layout related tasks on it.
 *
 * @see LayeredLayoutProvider
 * @author cds
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating yellow 2014-11-09 review KI-56 by chsch, als
 */
public interface ILayoutProcessor {
    
    /**
     * Performs the phase's work on the given graph.
     * 
     * @param layeredGraph a layered graph
     * @param progressMonitor a progress monitor to track algorithm execution
     */
    void process(LGraph layeredGraph, IElkProgressMonitor progressMonitor);
    
}
