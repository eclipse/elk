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

import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.core.config.IMutableLayoutConfig;
import org.eclipse.elk.core.config.LayoutContext;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.service.DiagramLayoutEngine;
import org.eclipse.elk.core.service.EclipseLayoutConfig;
import org.eclipse.elk.core.service.IDiagramLayoutManager;
import org.eclipse.elk.core.service.LayoutManagersService;
import org.eclipse.elk.core.ui.KimlUiPlugin;
import org.eclipse.elk.core.ui.Messages;
import org.eclipse.elk.core.ui.util.KimlUiUtil;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
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
    
    /** the icon used for this action. */
    private static ImageDescriptor icon = KimlUiPlugin.getImageDescriptor(
            "icons/menu16/apply2diagram.gif");

    /** the layout view that created this action. */
    private LayoutViewPart layoutView;

    /**
     * Creates an apply option action.
     * 
     * @param thelayoutView the layout view that created this action
     * @param text user friendly text
     */
    public DiagramDefaultAction(final LayoutViewPart thelayoutView, final String text) {
        super(text, icon);
        this.layoutView = thelayoutView;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        IWorkbenchPart workbenchPart = layoutView.getCurrentWorkbenchPart();
        IDiagramLayoutManager<?> manager = LayoutManagersService.getInstance()
                .getManager(workbenchPart, null);
        if (manager != null) {
            IMutableLayoutConfig layoutConfig = manager.getDiagramConfig();
            if (layoutConfig != null) {
                // build a layout context for setting the option
                LayoutContext context = new LayoutContext();
                context.setProperty(EclipseLayoutConfig.WORKBENCH_PART, workbenchPart);
                
                Object diagramPart = layoutConfig.getContextValue(LayoutContext.DIAGRAM_PART, context);
                if (diagramPart == null) {
                    diagramPart = layoutView.getCurrentDiagramPart();
                }
                if (diagramPart != null) {
                    context.setProperty(LayoutContext.DIAGRAM_PART, diagramPart);
                    context.setProperty(LayoutContext.GLOBAL, true);
                    DiagramLayoutEngine.INSTANCE.getOptionManager().enrich(context, layoutConfig,
                            false);

                    EditingDomain editingDomain = (EditingDomain) layoutConfig.getContextValue(
                            EclipseLayoutConfig.EDITING_DOMAIN, context);
                    for (IPropertySheetEntry entry : layoutView.getSelection()) {
                        applyOption(editingDomain, layoutConfig, context, entry);
                    }
                }
            }
        }
    }
    
    /**
     * Sets the layout option of the given property sheet entry as default for the whole
     * diagram.
     * 
     * @param editingDomain the editing domain, or {@code null}
     * @param config a layout configuration
     * @param context a layout context
     * @param entry a property sheet entry
     */
    private void applyOption(final EditingDomain editingDomain,
            final IMutableLayoutConfig config, final LayoutContext context,
            final IPropertySheetEntry entry) {
        final LayoutOptionData optionData = KimlUiUtil.getOptionData(
                layoutView.getCurrentLayouterData(), entry.getDisplayName());
        if (optionData == null) {
            return;
        }

        final Object value;
        if (optionData.equals(LayoutOptions.ALGORITHM)) {
            value = LayoutPropertySource.getLayoutHint((String) entry.getValueAsString());
        } else {
            value = optionData.parseValue(entry.getValueAsString());
        }
        if (value != null) {
            Runnable modelChange = new Runnable() {
                public void run() {
                    config.setOptionValue(optionData, context, value);
                }
            };
            KimlUiUtil.runModelChange(modelChange, editingDomain, Messages.getString("kiml.ui.13"));
        }
    }

}
