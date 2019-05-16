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

import org.eclipse.elk.core.debug.ElkDebugPlugin;
import org.eclipse.jface.action.Action;

/**
 * Action that clears all execution data in the execution view.
 */
public class ClearExecutionsAction extends Action {

    /** identifier string for this action. */
    private static final String ACTION_ID = "org.eclipse.elk.debug.clearExecutions";
    /** relative path to the icon to use for this action. */
    private static final String ICON_PATH = "icons/clear.gif";

    /** the execution view associated with this action. */
    private ExecutionTimeView view;

    /**
     * Creates an image export action for a given layout graph view.
     * 
     * @param theview execution view that created this action
     */
    public ClearExecutionsAction(final ExecutionTimeView theview) {
        this.view = theview;
        setId(ACTION_ID);
        setText("&Clear");
        setToolTipText("Clears all execution times.");
        setImageDescriptor(ElkDebugPlugin.imageDescriptorFromPlugin(ElkDebugPlugin.PLUGIN_ID, ICON_PATH));
    }

    @Override
    public void run() {
        view.clearExecutions();
    }

}
