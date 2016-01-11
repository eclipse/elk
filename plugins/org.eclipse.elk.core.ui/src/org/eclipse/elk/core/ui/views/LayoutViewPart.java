/*******************************************************************************
 * Copyright (c) 2009, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.ui.views;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.core.service.IDiagramLayoutManager;
import org.eclipse.elk.core.service.ILayoutConfigurationStore;
import org.eclipse.elk.core.service.LayoutManagersService;
import org.eclipse.elk.core.ui.ElkUiPlugin;
import org.eclipse.elk.core.ui.Messages;
import org.eclipse.elk.core.util.Maybe;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;

/**
 * A view that displays layout options for selected objects.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2012-10-26 review KI-29 by cmot, sgu
 */
public class LayoutViewPart extends ViewPart {

    /** the view identifier. */
    public static final String VIEW_ID = "org.eclipse.elk.core.views.layout";
    /** preference identifier for enabling categories. */
    public static final String PREF_CATEGORIES = "view.categories";
    /** preference identifier for enabling advanced options. */
    public static final String PREF_ADVANCED = "view.advanced";
    
    /** the form toolkit used to create forms. */
    private FormToolkit toolkit;
    /** the page that is displayed in this view part. */
    private PropertySheetPage page;
    /** the property source provider that keeps track of created property sources. */
    private final LayoutPropertySourceProvider propSourceProvider = new LayoutPropertySourceProvider();
    
    /** the selection listener. */
    private final ISelectionListener selectionListener = new ISelectionListener() {
        public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
            if (part instanceof IEditorPart && propSourceProvider.getWorkbenchPart() instanceof IEditorPart
                    && part != propSourceProvider.getWorkbenchPart()) {
                // If the editor is switched, clear the view content even if the new editor is not supported
                propSourceProvider.setWorkbenchPart(null);
            }
            IDiagramLayoutManager<?> manager = LayoutManagersService.getInstance().getManager(part, null);
            if (manager != null) {
                propSourceProvider.setWorkbenchPart(part);
                page.selectionChanged(part, selection);
            }
        }
    };
    
    /** the part listener for reacting to closed workbench parts. */
    private final IPartListener partListener = new IPartListener() {
        public void partClosed(final IWorkbenchPart part) {
            if (propSourceProvider.getWorkbenchPart() == part) {
                // Reset the workbench part in order to ensure proper garbage collection
                propSourceProvider.setWorkbenchPart(null);
            }
        }
        public void partOpened(final IWorkbenchPart part) { }
        public void partDeactivated(final IWorkbenchPart part) { }
        public void partBroughtToTop(final IWorkbenchPart part) { }
        public void partActivated(final IWorkbenchPart part) { }
    };
    
    /**
     * Finds the active layout view, if it exists.
     * 
     * @return the active layout view, or {@code null} if there is none
     */
    public static LayoutViewPart findView() {
        if (Display.getCurrent() == null) {
            final Maybe<LayoutViewPart> part = new Maybe<LayoutViewPart>();
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    part.set(findViewUI());
                }
            });
            return part.get();
        } else {
            return findViewUI();
        }
    }
    
    /**
     * Finds the active layout view, if it exists. This method works only if called
     * from the user interface thread.
     * 
     * @return the active layout view, or {@code null} if there is none
     */
    private static LayoutViewPart findViewUI() {
        IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWindow != null) {
            IWorkbenchPage activePage = activeWindow.getActivePage();
            if (activePage != null) {
                return (LayoutViewPart) activePage.findView(VIEW_ID);
            }
        }
        return null;
    }
    
    /** margin width for the form layout. */
    private static final int MARGIN_WIDTH = 4;
    /** position for left attachment. */
    private static final int FORM_LEFT = 0;
    /** position for right attachment. */
    private static final int FORM_RIGHT = 100;
    /** position for top attachment. */
    private static final int FORM_TOP = 0;
    /** position for bottom attachment. */
    private static final int FORM_BOTTOM = 100;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent) {
        // GUI code needs magic numbers
        // CHECKSTYLEOFF MagicNumber
        
        toolkit = new FormToolkit(parent.getDisplay());
        Form form = toolkit.createForm(parent);
        
        // Content
        Composite content = form.getBody();
        FormLayout contentLayout = new FormLayout();
        contentLayout.marginWidth = MARGIN_WIDTH;
        content.setLayout(contentLayout);
        
        // Property Sheet Page
        page = new PropertySheetPage();
        page.setRootEntry(new PropertySheetEntry());
        page.createControl(content);
        FormData formData = new FormData();
        formData.left = new FormAttachment(FORM_LEFT, 0);
        formData.right = new FormAttachment(FORM_RIGHT, 0);
        formData.top = new FormAttachment(FORM_TOP, 5);
        formData.bottom = new FormAttachment(FORM_BOTTOM, 0);
        page.getControl().setLayoutData(formData);
        page.setPropertySourceProvider(propSourceProvider);
        IPreferenceStore preferenceStore = ElkUiPlugin.getDefault().getPreferenceStore();
        
        // add actions to the toolbar, view menu, and context menu
        IActionBars actionBars = getViewSite().getActionBars();
        page.setActionBars(actionBars);
        addPopupActions(page.getControl().getMenu());
        IMenuManager menuManager = actionBars.getMenuManager();
        menuManager.add(new RemoveOptionsAction(this, Messages.getString("kiml.ui.30")));
        IToolBarManager toolBarManager = actionBars.getToolBarManager();
        
        // CHECKSTYLEON MagicNumber
        // set the stored value of the categories button
        ActionContributionItem categoriesItem =
                (ActionContributionItem) toolBarManager.find("categories");
        if (categoriesItem != null) {
            categoriesItem.getAction().setChecked(preferenceStore.getBoolean(PREF_CATEGORIES));
            categoriesItem.getAction().run();
        }
        
        // set the stored value of the advanced button
        ActionContributionItem advancedItem =
                (ActionContributionItem) toolBarManager.find("filter");
        if (advancedItem != null) {
            advancedItem.getAction().setChecked(preferenceStore.getBoolean(PREF_ADVANCED));
            advancedItem.getAction().run();
        }
        
        // get the current selection and trigger an update
        IWorkbenchWindow workbenchWindow = getSite().getWorkbenchWindow();
        IWorkbenchPage activePage = workbenchWindow.getActivePage();
        if (activePage != null) {
            final IWorkbenchPart activePart = activePage.getActivePart();
            final ISelection selection = workbenchWindow.getSelectionService().getSelection();
            if (activePart != null && selection != null) {
                workbenchWindow.getWorkbench().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        selectionListener.selectionChanged(activePart, selection);
                    }
                });
            }
        }
        workbenchWindow.getSelectionService().addSelectionListener(selectionListener);
        workbenchWindow.getPartService().addPartListener(partListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        page.setFocus();
    }
    
    /**
     * Returns the control that is handled by this view part.
     * 
     * @return the control
     */
    public Control getControl() {
        return page.getControl();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        // store the current status of the categories button
        ActionContributionItem categoriesItem = (ActionContributionItem) getViewSite()
                .getActionBars().getToolBarManager().find("categories");
        if (categoriesItem != null) {
            ElkUiPlugin.getDefault().getPreferenceStore().setValue(PREF_CATEGORIES,
                    categoriesItem.getAction().isChecked());
        }
        // store the current status of the advanced button
        ActionContributionItem advancedItem = (ActionContributionItem) getViewSite()
                .getActionBars().getToolBarManager().find("filter");
        if (advancedItem != null) {
            ElkUiPlugin.getDefault().getPreferenceStore().setValue(PREF_ADVANCED,
                    advancedItem.getAction().isChecked());
        }
        // dispose the view part
        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
        getSite().getWorkbenchWindow().getPartService().removePartListener(partListener);
        propSourceProvider.setWorkbenchPart(null);
        toolkit.dispose();
        super.dispose();
    }
    
    /**
     * Refreshes the layout view asynchronously.
     */
    public void refresh() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                propSourceProvider.clearCache();
                page.refresh();
            }
        });
    }
    
    /**
     * Returns the current selection of the layout view.
     * 
     * @return the selected property sheet entries
     */
    public List<IPropertySheetEntry> getSelection() {
        List<IPropertySheetEntry> entries = new LinkedList<IPropertySheetEntry>();
        TreeItem[] treeItems = ((Tree) page.getControl()).getSelection();
        for (TreeItem item : treeItems) {
            Object data = item.getData();
            if (data instanceof IPropertySheetEntry) {
                entries.add((IPropertySheetEntry) data);
            } else {
                // A category was selected, apply options for all children
                for (TreeItem childItem : item.getItems()) {
                    data = childItem.getData();
                    if (data instanceof IPropertySheetEntry) {
                        entries.add((IPropertySheetEntry) data);
                    }
                }
            }
        }
        return entries;
    }
    
    /**
     * Adds the specific view actions to the given menu.
     * 
     * @param menu context menu to enrich with actions
     */
    private void addPopupActions(final Menu menu) {
        final DiagramDefaultAction applyOptionAction
                = new DiagramDefaultAction(this, Messages.getString("kiml.ui.10"));
        
        // dirty hack to add actions to an existing menu without having the menu manager
        menu.addMenuListener(new MenuAdapter() {
            public void menuShown(final MenuEvent event) {
                MenuItem diagramDefaultItem = null;
                for (MenuItem item : menu.getItems()) {
                    if (item.getData() instanceof IContributionItem) {
                        String itemId = ((IContributionItem) item.getData()).getId();
                        if (DiagramDefaultAction.ACTION_ID.equals(itemId)) {
                            diagramDefaultItem = item;
                        }
                    }
                }
                
                // Add the "set as default for this diagram" action
                if (propSourceProvider.getConfigurationStore() != null) {
                    if (diagramDefaultItem == null) {
                        ContributionItem contributionItem = new ActionContributionItem(
                                applyOptionAction);
                        contributionItem.setId(DiagramDefaultAction.ACTION_ID);
                        contributionItem.fill(menu, -1);
                    } else {
                        diagramDefaultItem.setEnabled(true);
                    }
                } else if (diagramDefaultItem != null) {
                    diagramDefaultItem.setEnabled(false);
                }
            }
        });
    }
    
    /**
     * Return the currently tracked workbench part, or {@code null} if none is active.
     */
    public IWorkbenchPart getCurrentWorkbenchPart() {
        return propSourceProvider.getWorkbenchPart();
    }
    
    /**
     * Return the currently active configuration store, or {@code null} if none is active.
     */
    public ILayoutConfigurationStore getCurrentConfigurationStore() {
        return propSourceProvider.getConfigurationStore();
    }

}
