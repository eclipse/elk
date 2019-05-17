/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.debug.actions;

import org.eclipse.elk.core.debug.ElkDebugPlugin;
import org.eclipse.jface.action.Action;

/**
 * Action that clears all execution infos. The debug views are automatically updated since they listen for model
 * changes.
 */
public class ClearExecutionsAction extends Action {

    /** Identifier string for this action. */
    private static final String ACTION_ID = "org.eclipse.elk.debug.actions.clearExecutions";
    /** Relative path to the icon to use for this action. */
    private static final String ICON_PATH = "icons/clear.gif";
    
    public ClearExecutionsAction() {
        setId(ACTION_ID);
        setText("&Clear");
        setToolTipText("Clears all layout executions.");
        setImageDescriptor(ElkDebugPlugin.imageDescriptorFromPlugin(ElkDebugPlugin.PLUGIN_ID, ICON_PATH));
    }

    @Override
    public void run() {
        ElkDebugPlugin.getDefault().getModel().removeAllExecutionInfos();
    }

}
