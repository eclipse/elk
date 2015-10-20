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
import org.eclipse.elk.core.ui.KimlUiPlugin;
import org.eclipse.elk.core.ui.util.KimlUiUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetEntry;

/**
 * An action that sets the selected layout option as default for all instances of a diagram part.
 * Diagram parts are objects that represent a specific graph element. In GEF these representatives
 * are usually the edit parts.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class DiagramPartDefaultAction extends Action {

    /** identifier of the part default action without the domain model option. */
    public static final String EDIT_PART_ACTION_ID = "elk.edit.part.default";
    /** identifier of the part default action with the domain model option. */
    public static final String MODEL_ACTION_ID = "elk.model.default";

    /** the icon used for this action. */
    private static ImageDescriptor icon = KimlUiPlugin.getImageDescriptor(
            "icons/menu16/apply2editPart.gif");

    /** the layout view that created this action. */
    private LayoutViewPart layoutView;
    /** indicates whether options are set for the domain element class. */
    private boolean forDomainModel;
    
    /**
     * Creates an edit part default action.
     * 
     * @param thelayoutView the layout view that created this action
     * @param text user friendly text
     * @param isforDomainModel if true, the action sets layout options for the domain
     *     element class, else for the edit part class
     */
    public DiagramPartDefaultAction(final LayoutViewPart thelayoutView, final String text,
            final boolean isforDomainModel) {
        super(text, icon);
        this.layoutView = thelayoutView;
        this.forDomainModel = isforDomainModel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        Object diagramPart = layoutView.getCurrentDiagramPart();
        if (diagramPart != null) {
            for (IPropertySheetEntry entry : layoutView.getSelection()) {
                setDefault(diagramPart, entry);
            }
        }
    }
    
    /**
     * Sets the layout option of the given property sheet entry as default for
     * the given edit part.
     * 
     * @param diagramPart a diagram part
     * @param entry a property sheet entry
     */
    private void setDefault(final Object diagramPart, final IPropertySheetEntry entry) {
        LayoutOptionData optionData = KimlUiUtil.getOptionData(
                layoutView.getCurrentLayouterData(), entry.getDisplayName());

        if (optionData != null) {
            String valueString = entry.getValueAsString();
            if (optionData.equals(LayoutOptions.ALGORITHM)) {
                valueString = LayoutPropertySource.getLayoutHint(valueString);
            }
            ((ExtensionLayoutConfigService) LayoutConfigService.getInstance()).storeOption(
                    diagramPart, optionData, valueString, forDomainModel);
        }
    }
    
}
