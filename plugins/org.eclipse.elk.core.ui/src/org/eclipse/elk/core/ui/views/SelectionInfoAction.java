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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.elk.core.LayoutAlgorithmData;
import org.eclipse.elk.core.LayoutMetaDataService;
import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.core.config.LayoutContext;
import org.eclipse.elk.core.service.LayoutManagersService;
import org.eclipse.elk.core.ui.ElkUiPlugin;
import org.eclipse.elk.core.ui.Messages;
import org.eclipse.elk.core.ui.util.KimlUiUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.views.properties.IPropertySheetEntry;

/**
 * An action that displays useful information about the current selection.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class SelectionInfoAction extends Action {

    /** the icon used for this action. */
    private static ImageDescriptor icon = ElkUiPlugin.getImageDescriptor("icons/menu16/info.gif");
    /** the dialog's default width. */
    private static final int DEFAULT_WIDTH = 580;
    /** the dialog's default height. */
    private static final int DEFAULT_HEIGHT = 600;
    
    /** the layout view that created this action. */
    private LayoutViewPart layoutView;

    /**
     * A dialog class that displays HTML content.
     */
    private static class SelectionInfoDialog extends Dialog {
        
        /** the HTML text to display in the dialog. */
        private String htmlText;
        
        /**
         * Create a selection info dialog.
         * @param parentShell the parent shell
         */
        protected SelectionInfoDialog(final Shell parentShell, final String text) {
            super(parentShell);
            this.htmlText = text;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void configureShell(final Shell shell) {
            super.configureShell(shell);
            shell.setText(Messages.getString("kiml.ui.38"));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Point getInitialSize() {
            return new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean isResizable() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Control createDialogArea(final Composite parent) {
            Composite composite = (Composite) super.createDialogArea(parent);
            try {
                Browser browser = new Browser(composite, SWT.BORDER);
                GridData gridData = new GridData(GridData.FILL_BOTH);
                browser.setLayoutData(gridData);
                browser.setText(htmlText);
            } catch (SWTError exception) {
                IStatus status = new Status(IStatus.ERROR, ElkUiPlugin.PLUGIN_ID,
                        "Could not instantiate Browser.", exception);
                StatusManager.getManager().handle(status);
            }
            return composite;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void createButtonsForButtonBar(final Composite parent) {
            createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        }
        
    };
    
    /**
     * Creates a selection info action.
     * 
     * @param thelayoutView the layout view that created this action
     * @param text user friendly text
     */
    public SelectionInfoAction(final LayoutViewPart thelayoutView, final String text) {
        super(text, icon);
        this.layoutView = thelayoutView;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        Dialog dialog = new SelectionInfoDialog(layoutView.getViewSite().getShell(), createInfo()); 
        dialog.open();
    }
    
    /**
     * Create an information string to display to the user.
     * 
     * @return an info string
     */
    private String createInfo() {
        LayoutMetaDataService layoutServices = LayoutMetaDataService.getInstance();
        boolean infoAvailable = false;
        
        StringBuilder html = new StringBuilder("<HTML><HEAD>")
            .append(generateCSS())
            .append("</HEAD><BODY>");
        
        // display editor part
        IWorkbenchPart workbenchPart = layoutView.getCurrentWorkbenchPart();
        if (workbenchPart != null) {
            infoAvailable = true;
            html.append("<b>Workbench part class</b><ul><li>"
                    + workbenchPart.getClass().getName() + "</li></ul>");
        }
        
        // display edit part and domain model class
        Object diagramPart = layoutView.getCurrentDiagramPart();
        if (diagramPart != null) {
            infoAvailable = true;
            html.append("<b>Diagram part class</b><ul><li>"
                    + diagramPart.getClass().getName() + "</li></ul>");
            
            Object model = LayoutManagersService.getInstance().getContextValue(
                    LayoutContext.DOMAIN_MODEL, null, diagramPart);
            if (model != null) {
                if (model instanceof EObject) {
                    html.append("<b>Domain model class</b><ul><li>"
                            + ((EObject) model).eClass().getInstanceTypeName() + "</li></ul>");
                } else {
                    html.append("<b>Domain model class</b><ul><li>"
                            + model.getClass().getName() + "</li></ul>");
                }
            }
        }
        
        // display layout algorithms
        LayoutAlgorithmData[] layouterData = layoutView.getCurrentLayouterData();
        if (layouterData != null && layouterData.length > 0) {
            infoAvailable = true;
            html.append("<b>Involved layout algorithms</b><ul>");
            
            for (LayoutAlgorithmData data : layouterData) {
                if (data != null) {
                    html.append("<li>" + data.getName());
                    String category = layoutServices.getCategoryName(data.getCategory());
                    if (category != null) {
                        html.append(" (" + category + ")");
                    }
                    html.append(" - " + data.getId() + "</li>");
                }
            }
            html.append("</ul>");
        }
        
        // display layout options
        List<IPropertySheetEntry> selectedOptions = layoutView.getSelection();
        if (!selectedOptions.isEmpty()) {
            infoAvailable = true;
            html.append("<b>Selected options</b><ul>");
            
            for (IPropertySheetEntry entry : selectedOptions) {
                final LayoutOptionData optionData = KimlUiUtil.getOptionData(layouterData,
                        entry.getDisplayName());
                if (optionData != null) {
                    html.append("<li>" + optionData.getName() + " (" + optionData.getType().literal()
                            + ") - " + optionData.getId() + "</li>");
                }
            }
            html.append("</ul>");
        }
        
        if (!infoAvailable) {
            html.append("No information available.");
        }
        
        html.append("</BODY></HTML>");
        
        return html.toString();
    }
    
    /**
     * Generates the CSS code necessary for the HTML output to look good.
     * 
     * @return CSS code.
     */
    private static String generateCSS() {
        // Get the standard dialog font and the font used for headings
        FontData headerFont = JFaceResources.getHeaderFont().getFontData()[0];
        boolean boldHeaderFont = (headerFont.getStyle() & SWT.BOLD) != 0;
        FontData dialogFont = JFaceResources.getDialogFont().getFontData()[0];
        
        StringBuilder css = new StringBuilder("<style TYPE='text/css'><!--")
            .append("body, table { font-family: \"" + dialogFont.getName() + "\";")
            .append("              font-size: " + dialogFont.getHeight() + "pt; }")
            .append("h1 { font-family: \"" + headerFont.getName() + "\";")
            .append("     font-size: " + headerFont.getHeight() + "pt;")
            .append("     font-weight: " + (boldHeaderFont ? "bold" : "normal") + "; }")
            .append("table { border-bottom: 1pt solid #aaaaaa;")
            .append("        margin-left: 20pt; margin-bottom: 20pt; }")
            .append("th { background: #fafafa;")
            .append("     border-bottom: 2pt solid #aaaaaa; border-top: 2pt solid #aaaaaa;")
            .append("     font-weight: bold; text-align: left; }")
            .append("td { border-bottom: 1pt solid #aaaaaa; }")
            .append(".analysisName { font-weight: normal; }")
            .append(".even { background: white; }")
            .append(".odd { background: white; }")
            .append("--></style>");
        
        return css.toString();
    }

}
