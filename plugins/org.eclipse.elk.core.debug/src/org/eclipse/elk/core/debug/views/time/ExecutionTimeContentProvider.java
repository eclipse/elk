/*******************************************************************************
 * Copyright (c) 2009, 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.debug.views.time;

import java.util.List;

import org.eclipse.elk.core.debug.LayoutExecutionInfo;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for Executions. The viewer input element is expected to be a list of {@link LayoutExecutionInfo} instances.
 */
final class ExecutionTimeContentProvider implements ITreeContentProvider {

    @Override
    public Object[] getChildren(final Object parentElement) {
        if (parentElement instanceof LayoutExecutionInfo) {
            return ((LayoutExecutionInfo) parentElement).getChildren().toArray();
        }
        return new LayoutExecutionInfo[0];
    }

    @Override
    public Object getParent(final Object element) {
        if (element instanceof LayoutExecutionInfo) {
            return ((LayoutExecutionInfo) element).getParent();
        }
        return null;
    }

    @Override
    public boolean hasChildren(final Object element) {
        return element instanceof LayoutExecutionInfo
                && !((LayoutExecutionInfo) element).getChildren().isEmpty();
    }

    @Override
    public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof List<?>) {
            return ((List<?>) inputElement).toArray();
        }
        return new Object[0];
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    }

}
