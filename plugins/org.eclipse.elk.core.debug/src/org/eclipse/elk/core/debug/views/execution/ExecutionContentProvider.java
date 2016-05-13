/*******************************************************************************
 * Copyright (c) 2009, 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.debug.views.execution;

import java.util.List;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for Elk progress monitors.
 */
public class ExecutionContentProvider implements ITreeContentProvider {

    /**
     * {@inheritDoc}
     */
    public Object[] getChildren(final Object parentElement) {
        if (parentElement instanceof IElkProgressMonitor) {
            return ((IElkProgressMonitor) parentElement).getSubMonitors().toArray();
        }
        return new IElkProgressMonitor[0];
    }

    /**
     * {@inheritDoc}
     */
    public Object getParent(final Object element) {
        if (element instanceof IElkProgressMonitor) {
            return ((IElkProgressMonitor) element).getParentMonitor();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasChildren(final Object element) {
        return element instanceof IElkProgressMonitor
                && !((IElkProgressMonitor) element).getSubMonitors().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof List<?>) {
            return ((List<?>) inputElement).toArray();
        }
        return new Object[0];
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
    }

    /**
     * {@inheritDoc}
     */
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    }

}
