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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.core.debug.model.ExecutionInfo;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
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
public class ExecutionTimeView extends ViewPart {

    /** the view identifier. */
    public static final String VIEW_ID = "org.eclipse.elk.debug.executionView";

    /** the tree viewer used to display content. */
    private TreeViewer viewer;
    /** the list of executions. */
    private List<ExecutionInfo> executions = new ArrayList<>();
    

    /**
     * Creates an execution view.
     */
    public ExecutionTimeView() {
        super();
    }


    /**
     * Tries to find the relevant currently open view.
     */
    private static ExecutionTimeView findView() {
        IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWindow == null) {
            return null;
        }

        IWorkbenchPage activePage = activeWindow.getActivePage();
        if (activePage == null) {
            return null;
        }

        // find execution view
        IViewPart viewPart = activePage.findView(ExecutionTimeView.VIEW_ID);
        if (viewPart instanceof ExecutionTimeView) {
            return (ExecutionTimeView) viewPart;
        }
        
        return null;
    }

    /**
     * Adds an execution and updates the tree viewer of the currently active execution view.
     * 
     * @param progressMonitor progress monitor with execution information
     */
    public static void addExecution(final IElkProgressMonitor progressMonitor) {
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            public void run() {
                ExecutionTimeView activeView = findView();
                if (progressMonitor != null && activeView != null) {
                    activeView.executions.add(ExecutionInfo.fromProgressMonitor(progressMonitor));
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

    @Override
    public void createPartControl(final Composite parent) {
        // create actions in the view toolbar
        getViewSite().getActionBars().getToolBarManager().add(new ClearExecutionsAction(this));

        // create tree viewer
        viewer = new TreeViewer(parent);
        viewer.setContentProvider(new ExecutionTimeContentProvider());
        viewer.setInput(executions);
        
        // setup tree columns
        viewer.getTree().setHeaderVisible(true);
        
        TreeViewerColumn nameColumn = new TreeViewerColumn(viewer, SWT.NONE);
        nameColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
                new ExecutionTimeLabelProvider(ExecutionTimeLabelProvider.DisplayMode.NAME)));
        nameColumn.getColumn().setText("Name");
        nameColumn.getColumn().setWidth(500);
        
        TreeViewerColumn timeColumn = new TreeViewerColumn(viewer, SWT.NONE);
        timeColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
                new ExecutionTimeLabelProvider(ExecutionTimeLabelProvider.DisplayMode.TIME_TOTAL)));
        timeColumn.getColumn().setAlignment(SWT.RIGHT);
        timeColumn.getColumn().setText("Time [ms]");
        timeColumn.getColumn().setWidth(100);
        
        TreeViewerColumn localTimeColumn = new TreeViewerColumn(viewer, SWT.NONE);
        localTimeColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
                new ExecutionTimeLabelProvider(ExecutionTimeLabelProvider.DisplayMode.TIME_LOCAL)));
        localTimeColumn.getColumn().setAlignment(SWT.RIGHT);
        localTimeColumn.getColumn().setText("Local Time [ms]");
        localTimeColumn.getColumn().setWidth(100);
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

}
