/*******************************************************************************
 * Copyright (c) 2008, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.ui.preferences;

import org.eclipse.elk.core.service.DiagramLayoutEngine;
import org.eclipse.elk.core.service.ElkServicePlugin;
import org.eclipse.elk.core.ui.ElkUiPlugin;
import org.eclipse.elk.core.ui.LayoutHandler;
import org.eclipse.elk.core.ui.Messages;
import org.eclipse.elk.core.ui.views.LayoutViewPart;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for general KIML preferences.
 * 
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class LayoutPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    /** checkbox for animation. */
    private Button animationCheckBox;
    /** checkbox for zoom-to-fit. */
    private Button zoomCheckBox;
    /** checkbox for progress dialog. */
    private Button progressCheckBox;
    /** checkbox for debug graph output. */
    private Button debugCheckBox;
    /** checkbox for execution time measurement. */
    private Button execTimeCheckBox;

    /**
     * Creates the layout preference page.
     */
    public LayoutPreferencePage() {
        super();
    }
    
    
    /** vertical spacing between out group boxes. */
    private static final int VERTICAL_LAYOUT_SPACING = 10;

    /**
     * {@inheritDoc}
     */
    protected Control createContents(final Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout(1, false);
        compositeLayout.verticalSpacing = VERTICAL_LAYOUT_SPACING;
        composite.setLayout(compositeLayout);
        
        Group generalGroup = createGeneralOptionsGroup(composite);
        generalGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
        Group devOptionsGroup = createDeveloperOptionsGroup(composite);
        devOptionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
        return composite;
    }


    /** margin width for layouts. */
    private static final int MARGIN_WIDTH = 10;
    /** margin height for layouts. */
    private static final int MARGIN_HEIGHT = 5;
    
    /**
     * Creates the group for general options.
     * 
     * @param parent the parent control
     * @return a group with general options
     */
    private Group createGeneralOptionsGroup(final Composite parent) {
        IPreferenceStore mainPrefStore = getPreferenceStore();
        Group generalGroup = new Group(parent, SWT.NONE);
        generalGroup.setText(Messages.getString("kiml.ui.35")); //$NON-NLS-1$
        
        // add checkbox for animation
        animationCheckBox = new Button(generalGroup, SWT.CHECK | SWT.LEFT);
        animationCheckBox.setText(Messages.getString("kiml.ui.64")); //$NON-NLS-1$
        animationCheckBox.setToolTipText(Messages.getString("kiml.ui.67")); //$NON-NLS-1$
        animationCheckBox.setSelection(mainPrefStore.getBoolean(LayoutHandler.PREF_ANIMATION));
        
        // add checkbox for zoom-to-fit
        zoomCheckBox = new Button(generalGroup, SWT.CHECK | SWT.LEFT);
        zoomCheckBox.setText(Messages.getString("kiml.ui.65")); //$NON-NLS-1$
        zoomCheckBox.setToolTipText(Messages.getString("kiml.ui.68")); //$NON-NLS-1$
        zoomCheckBox.setSelection(mainPrefStore.getBoolean(LayoutHandler.PREF_ZOOM));
        
        // add checkbox for progress dialog
        progressCheckBox = new Button(generalGroup, SWT.CHECK | SWT.LEFT);
        progressCheckBox.setText(Messages.getString("kiml.ui.66")); //$NON-NLS-1$
        progressCheckBox.setToolTipText(Messages.getString("kiml.ui.69")); //$NON-NLS-1$
        progressCheckBox.setSelection(mainPrefStore.getBoolean(LayoutHandler.PREF_PROGRESS));
        
        FillLayout layout = new FillLayout(SWT.VERTICAL);
        layout.marginWidth = MARGIN_WIDTH;
        layout.marginHeight = MARGIN_HEIGHT;
        layout.spacing = LayoutConstants.getSpacing().y;
        generalGroup.setLayout(layout);
        return generalGroup;
    }

    /**
     * Creates the group for developer options.
     * 
     * @param parent the parent control
     * @return a group with developer options
     */
    private Group createDeveloperOptionsGroup(final Composite parent) {
        IPreferenceStore servicePrefStore = ElkServicePlugin.getInstance().getPreferenceStore();
        Group developerGroup = new Group(parent, SWT.NONE);
        developerGroup.setText(Messages.getString("kiml.ui.81")); //$NON-NLS-1$
        
        // add checkbox for execution time measurements
        execTimeCheckBox = new Button(developerGroup, SWT.CHECK | SWT.LEFT);
        execTimeCheckBox.setText(Messages.getString("kiml.ui.79")); //$NON-NLS-1$
        execTimeCheckBox.setToolTipText(Messages.getString("kiml.ui.80")); //$NON-NLS-1$
        execTimeCheckBox.setSelection(servicePrefStore.getBoolean(
                DiagramLayoutEngine.PREF_EXEC_TIME_MEASUREMENT));
        
        // add checkbox for debug graph output
        debugCheckBox = new Button(developerGroup, SWT.CHECK | SWT.LEFT);
        debugCheckBox.setText(Messages.getString("kiml.ui.71")); //$NON-NLS-1$
        debugCheckBox.setToolTipText(Messages.getString("kiml.ui.72")); //$NON-NLS-1$
        debugCheckBox.setSelection(servicePrefStore.getBoolean(
                DiagramLayoutEngine.PREF_DEBUG_OUTPUT));
        
        FillLayout layout = new FillLayout(SWT.VERTICAL);
        layout.marginWidth = MARGIN_WIDTH;
        layout.marginHeight = MARGIN_HEIGHT;
        layout.spacing = LayoutConstants.getSpacing().y;
        developerGroup.setLayout(layout);
        return developerGroup;
    }
    
    /**
     * {@inheritDoc}
     */
    public void init(final IWorkbench workbench) {
        setPreferenceStore(ElkUiPlugin.getInstance().getPreferenceStore());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performDefaults() {
        super.performDefaults();
        IPreferenceStore mainPrefStore = getPreferenceStore();
        IPreferenceStore servicePrefStore = ElkServicePlugin.getInstance().getPreferenceStore();
        
        // set default values for the general options
        animationCheckBox.setSelection(mainPrefStore.getDefaultBoolean(LayoutHandler.PREF_ANIMATION));
        zoomCheckBox.setSelection(mainPrefStore.getDefaultBoolean(LayoutHandler.PREF_ZOOM));
        progressCheckBox.setSelection(mainPrefStore.getDefaultBoolean(LayoutHandler.PREF_PROGRESS));
        debugCheckBox.setSelection(servicePrefStore.getDefaultBoolean(
                DiagramLayoutEngine.PREF_DEBUG_OUTPUT));
        execTimeCheckBox.setSelection(servicePrefStore.getDefaultBoolean(
                DiagramLayoutEngine.PREF_EXEC_TIME_MEASUREMENT));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performOk() {
        IPreferenceStore mainPrefStore = getPreferenceStore();
        IPreferenceStore servicePrefStore = ElkServicePlugin.getInstance().getPreferenceStore();
        
        // set new values for the general options
        mainPrefStore.setValue(LayoutHandler.PREF_ANIMATION, animationCheckBox.getSelection());
        mainPrefStore.setValue(LayoutHandler.PREF_ZOOM, zoomCheckBox.getSelection());
        mainPrefStore.setValue(LayoutHandler.PREF_PROGRESS, progressCheckBox.getSelection());
        servicePrefStore.setValue(DiagramLayoutEngine.PREF_DEBUG_OUTPUT, debugCheckBox.getSelection());
        servicePrefStore.setValue(DiagramLayoutEngine.PREF_EXEC_TIME_MEASUREMENT,
                execTimeCheckBox.getSelection());
        
        LayoutViewPart layoutView = LayoutViewPart.findView();
        if (layoutView != null) {
            layoutView.refresh();
        }
        return true;
    }

}
