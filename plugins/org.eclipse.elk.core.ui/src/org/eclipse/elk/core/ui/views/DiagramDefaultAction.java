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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.service.ILayoutConfigurationStore;
import org.eclipse.elk.core.service.LayoutConfigurationManager;
import org.eclipse.elk.core.service.data.LayoutAlgorithmData;
import org.eclipse.elk.core.service.data.LayoutOptionData;
import org.eclipse.elk.core.ui.ElkUiPlugin;
import org.eclipse.elk.core.ui.Messages;
import org.eclipse.elk.core.ui.util.ElkUiUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.views.properties.IPropertySheetEntry;

/**
 * An action that applies the selected layout option as default for the whole diagram.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class DiagramDefaultAction extends Action {
    
    /** identifier of the diagram default action. */
    public static final String ACTION_ID = "elk.diagram.default";
    
    /** the layout view that created this action. */
    private final LayoutViewPart layoutView;
    
    /**
     * Creates an apply option action.
     * 
     * @param thelayoutView the layout view that created this action
     * @param text user friendly text
     */
    public DiagramDefaultAction(final LayoutViewPart thelayoutView, final String text) {
        super(text, ElkUiPlugin.getImageDescriptor("icons/menu16/apply2diagram.gif"));
        this.layoutView = thelayoutView;
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
                for (IPropertySheetEntry entry : layoutView.getSelection()) {
                    applyOption(configs.get(0), entry, propSourceProvider.getConfigurationManager());
                }
            }
        }
    }
    
    /**
     * Sets the layout option of the given property sheet entry as default for the whole diagram.
     * 
     * @param config a layout configuration
     * @param entry a property sheet entry
     */
    private void applyOption(final ILayoutConfigurationStore config, final IPropertySheetEntry entry,
            final LayoutConfigurationManager configManager) {
        final LayoutOptionData optionData = ElkUiUtil.getOptionData(
                getCurrentLayouterData(config, configManager), entry.getDisplayName());
        if (optionData == null) {
            return;
        }

        final String value;
        if (optionData.equals(LayoutOptions.ALGORITHM)) {
            value = LayoutPropertySource.getLayoutHint((String) entry.getValueAsString());
        } else {
            value = entry.getValueAsString();
        }
        if (value != null) {
            Runnable modelChange = new Runnable() {
                public void run() {
                    configManager.setDefaultValue(optionData, value, config);
                }
            };
            ElkUiUtil.runModelChange(modelChange, config.getEditingDomain(), Messages.getString("kiml.ui.13"));
        }
    }

    /**
     * Returns the current layout algorithm data.
     */
    private LayoutAlgorithmData[] getCurrentLayouterData(final ILayoutConfigurationStore config,
            final LayoutConfigurationManager configManager) {
        // SUPPRESS CHECKSTYLE NEXT MagicNumber
        HashSet<LayoutAlgorithmData> data = new HashSet<LayoutAlgorithmData>(4);
        Set<LayoutOptionData.Target> optionTargets = config.getOptionTargets();
        if (optionTargets.contains(LayoutOptionData.Target.PARENTS)) {
            LayoutAlgorithmData algoData = configManager.getAlgorithm(config);
            if (algoData != null) {
                data.add(algoData);
            }
        }
        ILayoutConfigurationStore parentConfig = config.getParent();
        if (parentConfig != null) {
            LayoutAlgorithmData algoData = configManager.getAlgorithm(parentConfig);
            if (algoData != null) {
                data.add(algoData);
            }
        }
        return data.toArray(new LayoutAlgorithmData[data.size()]);
    }

}
