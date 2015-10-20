/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.ui.views;

import org.eclipse.elk.core.LayoutAlgorithmData;
import org.eclipse.elk.core.LayoutMetaDataService;
import org.eclipse.elk.core.LayoutTypeData;
import org.eclipse.elk.core.ui.AlgorithmSelectionDialog;
import org.eclipse.elk.core.ui.Messages;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A cell editor that opens a dialog to select a layout algorithm or type.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2012-10-26 review KI-29 by cmot, sgu
 */
public class AlgorithmCellEditor extends DialogCellEditor {
    
    /**
     * Creates a layout algorithm cell editor.
     * 
     * @param parent the parent composite
     */
    public AlgorithmCellEditor(final Composite parent) {
        super(parent);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireApplyEditorValue() {
        super.fireApplyEditorValue();
        // Applying the editor value will cause the layout view to refresh,
        // which will in turn cause the active cell editor to apply its value again;
        // this can be avoided by firing a cancel event on the cell editor
        fireCancelEditor();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createContents(final Composite cell) {
        Control label = super.createContents(cell);
        label.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(final MouseEvent e) {
                Object newValue = openDialogBox(cell);
                if (newValue != null) {
                    markDirty();
                    doSetValue(newValue);
                    fireApplyEditorValue();
                }
                // Set focus on the layout view in order to be able to respond to key bindings
                LayoutViewPart layoutView = LayoutViewPart.findView();
                if (layoutView != null) {
                    layoutView.setFocus();
                }
            }
        });
        return label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateContents(final Object value) {
        if (value instanceof String) {
            String newText;
            LayoutMetaDataService layoutServices = LayoutMetaDataService.getInstance();
            LayoutTypeData layoutType = layoutServices.getTypeData((String) value);
            if (layoutType != null) {
                newText = layoutType.toString();
            } else {
                LayoutAlgorithmData layouterData = layoutServices.getAlgorithmData((String) value);
                if (layouterData != null) {
                    newText = layouterData.toString();
                } else {
                    newText = Messages.getString("kiml.ui.8");
                }
            }
            super.updateContents(newText);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object openDialogBox(final Control cellEditorWindow) {
        AlgorithmSelectionDialog dialog = new AlgorithmSelectionDialog(cellEditorWindow.getShell(),
                (String) getValue());
        if (dialog.open() == AlgorithmSelectionDialog.OK) {
            return dialog.getSelectedHint();
        }
        return null;
    }

}
