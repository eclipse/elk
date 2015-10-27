/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.elk.core.service.DiagramLayoutEngine;
import org.eclipse.elk.core.service.ElkServicePlugin;
import org.eclipse.elk.core.ui.KimlUiPlugin;
import org.eclipse.elk.core.ui.LayoutHandler;
import org.eclipse.elk.core.ui.views.LayoutViewPart;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Preference initializer for the KIML plugins.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore servicePrefStore = ElkServicePlugin.getDefault().getPreferenceStore();
        IPreferenceStore uiPrefStore = KimlUiPlugin.getDefault().getPreferenceStore();
        
        uiPrefStore.setDefault(LayoutHandler.PREF_ANIMATION, true);
        uiPrefStore.setDefault(LayoutHandler.PREF_ZOOM, false);
        uiPrefStore.setDefault(LayoutHandler.PREF_PROGRESS, false);
        uiPrefStore.setDefault(LayoutViewPart.PREF_CATEGORIES, true);
        uiPrefStore.setDefault(LayoutViewPart.PREF_ADVANCED, false);
        servicePrefStore.setDefault(DiagramLayoutEngine.PREF_DEBUG_OUTPUT, false);
        servicePrefStore.setDefault(DiagramLayoutEngine.PREF_EXEC_TIME_MEASUREMENT, false);
    }

}
