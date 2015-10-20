/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.tree;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.tree.graph.TGraph;

/**
 * A layout processor processes a {@link org.eclipse.elk.tree.graph.TGraph}, performing layout
 * related tasks on it.
 * 
 * @author sor
 * @author sgu
 * @author cds
 */
public interface ILayoutProcessor {

    /**
     * Performs the phase's work on the given graph.
     * 
     * @param tGraph
     *            a tree graph
     * @param progressMonitor
     *            a progress monitor to track algorithm execution
     */
    void process(TGraph tGraph, IElkProgressMonitor progressMonitor);

}
