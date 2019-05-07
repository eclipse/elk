/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.debug.views.graph;

import org.eclipse.elk.core.debug.ElkDebugPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * An action for reloading a KGraph and performing layout on it.
 */
public class ReloadGraphAction extends LoadGraphAction {

    /** identifier string for this action. */
    private static final String ACTION_ID = "org.eclipse.elk.debug.reloadGraph";
    /** relative path to the icon to use for this action. */
    private static final String ICON_PATH = "icons/refresh.gif";

    /**
     * Creates a load graph action.
     */
    public ReloadGraphAction() {
        setId(ACTION_ID);
        setText("Reload Graph");
        setToolTipText("ReLoad, layout, and display a saved ELK graph.");
        setImageDescriptor(ElkDebugPlugin.imageDescriptorFromPlugin(ElkDebugPlugin.PLUGIN_ID, ICON_PATH));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        IPreferenceStore preferenceStore = ElkDebugPlugin.getDefault().getPreferenceStore();

        String fileName = preferenceStore.getString(LAST_FILE_NAME_PREF);

        if (fileName != null) {
            loadAndLayout(fileName);
        }
    }
}
