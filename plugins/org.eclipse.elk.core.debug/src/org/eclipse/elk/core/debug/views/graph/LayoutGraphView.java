/*******************************************************************************
 * Copyright (c) 2008, 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.debug.views.graph;

import org.eclipse.elk.core.ui.rendering.GraphRenderingCanvas;
import org.eclipse.elk.graph.KNode;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * A viewer for layout graphs.
 */
public class LayoutGraphView extends ViewPart {

    /** the view identifier. */
    public static final String VIEW_ID = "org.eclipse.elk.debug.graphView"; //$NON-NLS-1$

    /** the scrolled composite that contains the graph canvas. */
    private ScrolledComposite scrolledComposite;
    /** the canvas used to draw layout graphs. */
    private GraphRenderingCanvas graphCanvas;
    

    /**
     * Creates a layout graph view.
     */
    public LayoutGraphView() {
        super();
    }
    
    /**
     * Updates the view by painting the given layout graph.
     * 
     * @param graph the graph to be drawn.
     */
    public static void updateWithGraph(final KNode graph) {
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            public void run() {
                LayoutGraphView activeView = findView();
                if (graph != null && activeView != null) {
                    KNode nodeCopy = EcoreUtil.copy(graph);
                    activeView.getCanvas().setLayoutGraph(nodeCopy);
                }
            }
        });
    }

    /**
     * Tries to find the relevant currently open view.
     */
    private static LayoutGraphView findView() {
        IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWindow == null) {
            return null;
        }

        IWorkbenchPage activePage = activeWindow.getActivePage();
        if (activePage == null) {
            return null;
        }

        // find layout graph view
        IViewPart viewPart = activePage.findView(LayoutGraphView.VIEW_ID);
        if (viewPart instanceof LayoutGraphView) {
            return (LayoutGraphView) viewPart;
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void createPartControl(final Composite parent) {
        // create actions in the view toolbar
        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(new LoadGraphAction());
        toolBarManager.add(new ImageExportAction(this));

        // create canvas for layout graphs
        scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        graphCanvas = new GraphRenderingCanvas(scrolledComposite);
        scrolledComposite.setContent(graphCanvas);
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        scrolledComposite.setFocus();
    }

    /**
     * Returns the layout graph canvas.
     * 
     * @return the layout graph canvas
     */
    public GraphRenderingCanvas getCanvas() {
        return graphCanvas;
    }

}
