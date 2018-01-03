/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.sequence;

import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * A layout processor processes a {@link LayoutContext}, performing layout related tasks on it. This
 * basically models one step in the list of steps required to layout a sequence diagram.
 *
 * @see SequenceDiagramLayoutProvider
 * @author cds
 */
public interface ISequenceLayoutProcessor {
    
    /**
     * Performs the processor's work on the given graph.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @param progressMonitor
     *            a progress monitor to track algorithm execution.
     */
    void process(LayoutContext context, IElkProgressMonitor progressMonitor);
    
}
