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
package org.eclipse.elk.core.ui.views;

import java.util.List;

import org.eclipse.elk.core.service.ILayoutConfigurationStore;
import org.eclipse.elk.core.service.LayoutConfigurationManager;
import org.eclipse.elk.core.ui.Messages;
import org.eclipse.elk.core.ui.util.ElkUiUtil;
import org.eclipse.jface.action.Action;

/**
 * An action that removes all layout options from the current diagram.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class RestoreDefaultsAction extends Action {
    
    /** The layout view that created this action. */
    private final LayoutViewPart layoutView;
    /** Whether to apply the action to the diagram defaults. */
    private final boolean diagramDefaults;

    /**
     * Creates an apply option action.
     * 
     * @param thelayoutView the layout view that created this action
     * @param text user friendly text
     * @param thediagramDefaults whether to apply the action to the diagram defaults
     */
    public RestoreDefaultsAction(final LayoutViewPart thelayoutView, final String text,
            final boolean thediagramDefaults) {
        super(text);
        this.layoutView = thelayoutView;
        this.diagramDefaults = thediagramDefaults;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        LayoutPropertySourceProvider propSourceProvider = layoutView.getPropertySourceProvider();
        if (propSourceProvider != null) {
            List<ILayoutConfigurationStore> configs = propSourceProvider.getConfigurationStores();
            if (!configs.isEmpty()) {
                restoreDefaults(configs, propSourceProvider.getConfigurationManager());
            }
        }
    }
    
    /**
     * Remove all layout options from the given context.
     * 
     * @param diagramName name of the open diagram
     * @param configs a non-empty list of layout configurations for options removal
     */
    private void restoreDefaults(final List<ILayoutConfigurationStore> configs,
            final LayoutConfigurationManager configManager) {
        Runnable runnable = new Runnable() {
            public void run() {
                for (ILayoutConfigurationStore config : configs) {
                    if (diagramDefaults) {
                        configManager.clearDefaultOptionValues(config);
                    } else {
                        configManager.clearOptionValues(config);
                    }
                }
            }
        };
        ElkUiUtil.runModelChange(runnable, configs.get(0).getEditingDomain(), Messages.getString("kiml.ui.30"));
        
        // refresh the layout view after these changes
        LayoutViewPart viewPart = LayoutViewPart.findView();
        if (viewPart != null) {
            viewPart.refresh();
        }
    }
    
}
