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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * View that can be used to display execution results of an algorithm. Use {@link #addExecution(IElkProgressMonitor)}
 * and {@link #clearExecutions()} to manipulate the view.
 */
public class ExecutionView extends ViewPart {

    /** the view identifier. */
    public static final String VIEW_ID = "org.eclipse.elk.debug.executionView";

    /** the tree viewer used to display content. */
    private TreeViewer viewer;
    /** the list of executions. */
    private List<Execution> executions = new ArrayList<>();
    

    /**
     * Creates an execution view.
     */
    public ExecutionView() {
        super();
    }
    

    /**
     * Adds an execution and updates the tree viewer of the currently active execution view.
     * 
     * @param progressMonitor progress monitor with execution information
     */
    public static void addExecution(final IElkProgressMonitor progressMonitor) {
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            public void run() {
                ExecutionView activeView = findView();
                if (progressMonitor != null && activeView != null) {
                    activeView.executions.add(Execution.fromProgressMonitor(progressMonitor));
                    activeView.viewer.refresh();
                }
            }
        });
    }

    /**
     * Clears all executions and updates the tree viewer.
     */
    public void clearExecutions() {
        executions.clear();
        viewer.refresh();
    }

    /**
     * Tries to find the relevant currently open view.
     */
    private static ExecutionView findView() {
        IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWindow == null) {
            return null;
        }

        IWorkbenchPage activePage = activeWindow.getActivePage();
        if (activePage == null) {
            return null;
        }

        // find execution view
        IViewPart viewPart = activePage.findView(ExecutionView.VIEW_ID);
        if (viewPart instanceof ExecutionView) {
            return (ExecutionView) viewPart;
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent) {
        // create actions in the view toolbar
        getViewSite().getActionBars().getToolBarManager().add(new ClearExecutionsAction(this));

        // create tree viewer
        viewer = new TreeViewer(parent);
        viewer.setContentProvider(new ExecutionContentProvider());
        viewer.setLabelProvider(new ExecutionLabelProvider());
        viewer.setInput(executions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

}
