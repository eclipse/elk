/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.service;

import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Listener interface for automatic layout done through {@link DiagramLayoutEngine}. Instances can be
 * registered in {@link LayoutConnectorsService}.
 */
public interface ILayoutListener {
    
    /**
     * Called when layout is about to be executed.
     */
    void layoutAboutToStart(LayoutMapping mapping, IElkProgressMonitor progressMonitor);
    
    /**
     * Called when layout has been executed.
     */
    void layoutDone(LayoutMapping mapping, IElkProgressMonitor progressMonitor);

}
