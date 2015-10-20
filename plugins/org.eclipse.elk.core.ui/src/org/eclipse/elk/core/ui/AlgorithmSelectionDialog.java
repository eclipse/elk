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
package org.eclipse.elk.core.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.elk.core.ILayoutMetaData;
import org.eclipse.elk.core.LayoutAlgorithmData;
import org.eclipse.elk.core.LayoutMetaDataService;
import org.eclipse.elk.core.LayoutTypeData;
import org.eclipse.elk.core.util.Maybe;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A dialog to browse and select layout algorithms or layout types.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class AlgorithmSelectionDialog extends Dialog {

    /** the current layouter hint as selected by the user. */
    private String layouterHint;
    /** the label for displaying the name of the current hint. */
    private Label displayNameLabel;
    /** the label for displaying the description of the current hint. */
    private Label descriptionLabel;
    /** the label for displaying the preview image. */
    private Label imageLabel;
    /** the selection provider for layout algorithms and types. */
    private ISelectionProvider selectionProvider;
    /** the cached preview images. */
    private final Map<ILayoutMetaData, Image> imageCache = new HashMap<ILayoutMetaData, Image>();
    /** the selection listeners that are added to the tree viewer when it is created. */
    private List<ISelectionChangedListener> selectionListeners
            = new LinkedList<ISelectionChangedListener>();
    
    /**
     * Creates a layout algorithm dialog.
     * 
     * @param parentShell the parent shell
     * @param currentHint the currently active layouter hint
     */
    public AlgorithmSelectionDialog(final Shell parentShell, final String currentHint) {
        super(parentShell);
        this.layouterHint = currentHint;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell(final Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.getString("kiml.ui.58")); //$NON-NLS-1$
    }
    
    /**
     * Add a selection listener that is notified when an algorithm or type is selected.
     * 
     * @param listener a listener
     */
    public void addAlgorithmSelectionListener(final ISelectionChangedListener listener) {
        if (selectionProvider != null) {
            selectionProvider.addSelectionChangedListener(listener);
        } else {
            selectionListeners.add(listener);
        }
    }
    
    /**
     * Remove a selection listener for algorithm or type changes.
     * 
     * @param listener a listener
     */
    public void removeAlgorithmSelectionListener(final ISelectionChangedListener listener) {
        if (selectionProvider != null) {
            selectionProvider.removeSelectionChangedListener(listener);
        } else {
            selectionListeners.remove(listener);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean close() {
        imageLabel.setImage(null);
        for (Image image : imageCache.values()) {
            image.dispose();
        }
        imageCache.clear();
        return super.close();
    }
    
    /**
     * Update the currently displayed value of the description area according
     * to the tree selection.
     * 
     * @param layoutData the currently selected layout data
     */
    private void updateValue(final ILayoutMetaData layoutData) {
        layouterHint = layoutData.getId();

        String name = layoutData.getName();
        if (name == null || name.length() == 0) {
            name = layoutData instanceof LayoutAlgorithmData
                    ? Messages.getString("kiml.ui.61")
                    : Messages.getString("kiml.ui.8");
        }
        displayNameLabel.setText(name);
        String description = layoutData.getDescription();
        if (description == null || description.length() == 0) {
            description = Messages.getString("kiml.ui.60");
        }
        descriptionLabel.setText(description);
        Image image = imageCache.get(layoutData);
        if (image == null && layoutData instanceof LayoutAlgorithmData) {
            Object descriptor = ((LayoutAlgorithmData) layoutData).getPreviewImage();       
            if (descriptor instanceof ImageDescriptor) {
                image = ((ImageDescriptor) descriptor).createImage(false);
                if (image != null) {
                    imageCache.put(layoutData, image);
                }
            }
        }
        imageLabel.setImage(image);
        imageLabel.getParent().layout();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        ((GridLayout) composite.getLayout()).numColumns = 2;
        createSelectionTree(composite);
        createDescriptionArea(composite);    
        
        // set the initial selection according to the current layouter hint
        if (layouterHint != null) {
            LayoutMetaDataService layoutServices = LayoutMetaDataService.getInstance();
            ILayoutMetaData layoutData = layoutServices.getAlgorithmData(layouterHint);
            if (layoutData == null) {
                layoutData = layoutServices.getTypeData(layouterHint);
            }
            if (layoutData != null) {
                selectionProvider.setSelection(new StructuredSelection(layoutData));
            }
        }
        return composite;
    }
    
    /** minimal width of the selection area. */
    private static final int SELECTION_WIDTH = 220;
    
    /**
     * Create the dialog area that displays the selection tree and filter text.
     * 
     * @param parent the parent composite
     * @return the control for the selection area
     */
    private Control createSelectionTree(final Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        
        // create filter text
        final Text filterText = new Text(composite, SWT.BORDER);
        filterText.setText(Messages.getString("kiml.ui.59"));
        filterText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        filterText.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
        
        // create tree viewer
        final TreeViewer treeViewer = new TreeViewer(composite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        final AlgorithmContentProvider contentProvider = new AlgorithmContentProvider();
        treeViewer.setContentProvider(contentProvider);
        treeViewer.setLabelProvider(new LabelProvider());
        treeViewer.setSorter(new ViewerSorter() {
            public int category(final Object element) {
                if (element instanceof LayoutTypeData) {
                    LayoutTypeData typeData = (LayoutTypeData) element;
                    // the "Other" layout type has empty identifier and is put to the bottom
                    return typeData.getId().length() == 0 ? 1 : 0;
                }
                return super.category(element);
            }
        });
        treeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        treeViewer.setInput(LayoutMetaDataService.getInstance());
        treeViewer.expandAll();
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                okPressed();
            }
        });
        
        // set up a filter on the tree viewer using the filter text
        final Maybe<Boolean> filterChanged = new Maybe<Boolean>(Boolean.FALSE);
        final Maybe<Boolean> filterLeft = new Maybe<Boolean>(Boolean.FALSE);
        filterText.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                if (!filterChanged.get()) {
                    filterChanged.set(Boolean.TRUE);
                    filterText.setForeground(null);
                    int pos = filterText.getCaretPosition();
                    String newText = filterText.getText(pos - 1, pos - 1);
                    filterText.setText(newText);
                    filterText.setSelection(pos);
                } else {
                    contentProvider.updateFilter(filterText.getText());
                    treeViewer.refresh();
                    treeViewer.expandAll();
                    ILayoutMetaData selected = contentProvider.getBestFilterMatch();
                    if (selected != null) {
                        treeViewer.setSelection(new StructuredSelection(selected));
                    }
                }
            }
        });
        filterText.addFocusListener(new FocusListener() {
            public void focusGained(final FocusEvent e) {
                if (filterLeft.get() && !filterChanged.get()) {
                    filterChanged.set(Boolean.TRUE);
                    filterText.setForeground(null);
                    filterText.setText("");
                }
            }
            public void focusLost(final FocusEvent e) {
                filterLeft.set(Boolean.TRUE);
            }
        });
        treeViewer.addFilter(new ViewerFilter() {
            public boolean select(final Viewer viewer, final Object parentElement,
                    final Object element) {
                return contentProvider.applyFilter(element);
            }
        });
        
        // add a selection listener to the tree so that the selected element is displayed
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                Object element = selection.getFirstElement();
                if (element instanceof ILayoutMetaData) {
                    updateValue((ILayoutMetaData) element);
                }
            }
        });
        
        composite.setLayout(new GridLayout());
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.minimumWidth = SELECTION_WIDTH;
        composite.setLayoutData(gridData);
        
        // register all selection listeners that have been stored in a list
        selectionProvider = treeViewer;
        for (ISelectionChangedListener listener : selectionListeners) {
            selectionProvider.addSelectionChangedListener(listener);
        }
        return composite;
    }
    
    /** width of the description area. */
    private static final int DESCRIPTION_WIDTH = 300;
    /** vertical spacing in the description area. */
    private static final int DESCR_SPACING = 12;
    
    /**
     * Create the dialog area that displays the description of a layout algorithm.
     * 
     * @param parent the parent composite
     * @return the control for the description area
     */
    private Control createDescriptionArea(final Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        
        // create label for the display name
        displayNameLabel = new Label(composite, SWT.NONE);
        FontDescriptor fontDescriptor = FontDescriptor.createFrom(parent.getFont());
        fontDescriptor = fontDescriptor.increaseHeight(2).setStyle(SWT.BOLD);
        displayNameLabel.setFont(fontDescriptor.createFont(parent.getDisplay()));
        displayNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
        // create label for the description
        descriptionLabel = new Label(composite, SWT.WRAP);
        GridData descriptionLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        descriptionLayoutData.widthHint = DESCRIPTION_WIDTH;
        descriptionLabel.setLayoutData(descriptionLayoutData);
        
        // create label for the preview image
        imageLabel = new Label(composite, SWT.NONE);
        GridData imageLayoutData = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        imageLabel.setLayoutData(imageLayoutData);
        
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.verticalSpacing = DESCR_SPACING;
        composite.setLayout(compositeLayout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        return composite;
    }
    
    /**
     * The layouter hint that was selected by the user.
     * 
     * @return the selected layouter hint
     */
    public String getSelectedHint() {
        return layouterHint;
    }

}
