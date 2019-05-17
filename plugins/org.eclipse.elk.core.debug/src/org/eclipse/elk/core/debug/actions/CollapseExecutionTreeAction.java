/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.debug.actions;

import org.eclipse.elk.core.debug.AbstractLayoutDebugView;
import org.eclipse.elk.core.debug.ElkDebugPlugin;
import org.eclipse.jface.action.Action;

/**
 * Action for collapsing all elements of the tree view in the debug log view.
 */
public class CollapseExecutionTreeAction extends Action {
    
    /** Identifier string for this action. */
    private static final String ACTION_ID = "org.eclipse.elk.debug.actions.collapseAll";
    /** Relative path to the icon to use for this action. */
    private static final String ICON_PATH = "icons/collapse_all.gif";
    
    /** The debug view associated with this action. */
    private final AbstractLayoutDebugView view;

    public CollapseExecutionTreeAction(final AbstractLayoutDebugView view) {
        setId(ACTION_ID);
        setText("&Collapse All");
        setToolTipText("Collapses every element of the tree viewer.");
        setImageDescriptor(ElkDebugPlugin.imageDescriptorFromPlugin(ElkDebugPlugin.PLUGIN_ID, ICON_PATH));
        
        this.view = view;
    }

    @Override
    public void run() {
        view.collapseAllTreeViewerElements();
    }
}
