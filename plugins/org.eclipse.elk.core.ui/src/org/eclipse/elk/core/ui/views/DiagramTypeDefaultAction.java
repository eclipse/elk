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

import org.eclipse.elk.core.LayoutConfigService;
import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.service.ExtensionLayoutConfigService;
import org.eclipse.elk.core.ui.ElkUiPlugin;
import org.eclipse.elk.core.ui.util.KimlUiUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetEntry;

/**
 * An action that sets the selected layout option as default for all instances of a
 * diagram type.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class DiagramTypeDefaultAction extends Action {

    /** identifier of the diagram type default action. */
    public static final String ACTION_ID = "elk.diagram.type.default";

    /** the icon used for this action. */
    private static ImageDescriptor icon = ElkUiPlugin.getImageDescriptor(
            "icons/menu16/apply2diagramType.gif");

    /** the layout view that created this action. */
    private LayoutViewPart layoutView;
    /** the current diagram type, injected by the layout view. */
    private String diagramType;
    
    /**
     * Creates a diagram type default action.
     * 
     * @param thelayoutView the layout view that created this action
     * @param text user friendly text
     */
    public DiagramTypeDefaultAction(final LayoutViewPart thelayoutView, final String text) {
        super(text, icon);
        this.layoutView = thelayoutView;
    }
    
    /**
     * Set the diagram type to affect when the action is run.
     * 
     * @param thediagramType the diagram type
     */
    public void setDiagramType(final String thediagramType) {
        this.diagramType = thediagramType;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        Object diagramPart = layoutView.getCurrentDiagramPart();
        if (diagramPart != null && diagramType != null) {
            for (IPropertySheetEntry entry : layoutView.getSelection()) {
                setDefault(entry);
            }
        }
    }
    
    /**
     * Sets the layout option of the given property sheet entry as default for
     * the given diagram type.
     * 
     * @param diagramType a diagram type identifier
     * @param entry a property sheet entry
     */
    private void setDefault(final IPropertySheetEntry entry) {
        LayoutOptionData optionData = KimlUiUtil.getOptionData(
                layoutView.getCurrentLayouterData(), entry.getDisplayName());
        
        if (optionData != null) {
            String valueString = entry.getValueAsString();
            if (optionData.equals(LayoutOptions.ALGORITHM)) {
                valueString = LayoutPropertySource.getLayoutHint(valueString);
            }
            ((ExtensionLayoutConfigService) LayoutConfigService.getInstance()).storeOption(
                    diagramType, optionData, valueString);
        }
    }
    
}
