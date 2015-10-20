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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.core.LayoutAlgorithmData;
import org.eclipse.elk.core.LayoutConfigService;
import org.eclipse.elk.core.LayoutMetaDataService;
import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.core.config.DefaultLayoutConfig;
import org.eclipse.elk.core.config.ILayoutConfig;
import org.eclipse.elk.core.config.LayoutContext;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.service.DiagramLayoutEngine;
import org.eclipse.elk.core.service.ExtensionLayoutConfigService;
import org.eclipse.elk.core.service.IDiagramLayoutManager;
import org.eclipse.elk.core.service.LayoutManagersService;
import org.eclipse.elk.core.ui.KimlUiPlugin;
import org.eclipse.elk.core.ui.Messages;
import org.eclipse.elk.core.util.Maybe;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.graphics.FontData;
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
public class LayoutViewPart extends ViewPart implements ISelectionListener {

    /** the view identifier. */
    public static final String VIEW_ID = "org.eclipse.elk.core.views.layout";
    /** preference identifier for enabling categories. */
    public static final String PREF_CATEGORIES = "view.categories";
    /** preference identifier for enabling advanced options. */
    public static final String PREF_ADVANCED = "view.advanced";
    /** preference identifier for the title font. */
    private static final String TITLE_FONT = "org.eclipse.elk.core.ui.views.LayoutViewPart.TITLE_FONT";
    
    /** the form toolkit used to create forms. */
    private FormToolkit toolkit;
    /** the form container for the property sheet page. */
    private Form form;
    /** the page that is displayed in this view part. */
    private PropertySheetPage page;
    /** the property source provider that keeps track of created property sources. */
    private final LayoutPropertySourceProvider propSourceProvider = new LayoutPropertySourceProvider();
    /** the part listener for reacting to closed workbench parts. */
    private final IPartListener partListener = new IPartListener() {

        public void partClosed(final IWorkbenchPart part) {
            if (propSourceProvider.getWorkbenchPart() == part) {
                // reset esp. the workbenchPart field in order to ensure proper
                //  garbage collection of that workbench part instance
                propSourceProvider.resetContext(null);
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
        form = toolkit.createForm(parent);
        
        // Set the form's heading
        // (see org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyTitle)
        toolkit.decorateFormHeading(form);
        form.setText("");

        if (!JFaceResources.getFontRegistry().hasValueFor(TITLE_FONT)) {
            FontData[] fontData = JFaceResources.getFontRegistry().getBold(
                    JFaceResources.DEFAULT_FONT).getFontData();
            /* title font is 2pt larger than that used in the tabs. */  
            fontData[0].setHeight(fontData[0].getHeight() + 2);
            JFaceResources.getFontRegistry().put(TITLE_FONT, fontData);
        }
        form.setFont(JFaceResources.getFont(TITLE_FONT));
        
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
        IPreferenceStore preferenceStore = KimlUiPlugin.getDefault().getPreferenceStore();
        
        // add actions to the toolbar, view menu, and context menu
        IActionBars actionBars = getViewSite().getActionBars();
        page.setActionBars(actionBars);
        addPopupActions(page.getControl().getMenu());
        IMenuManager menuManager = actionBars.getMenuManager();
        menuManager.add(new RemoveOptionsAction(this, Messages.getString("kiml.ui.30")));
        ExtensionLayoutConfigService.fillConfigMenu(menuManager);
        IToolBarManager toolBarManager = actionBars.getToolBarManager();
        toolBarManager.add(new SelectionInfoAction(this, Messages.getString("kiml.ui.37")));
        
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
                        selectionChanged(activePart, selection);
                    }
                });
            }
        }
        workbenchWindow.getSelectionService().addSelectionListener(this);
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
            KimlUiPlugin.getDefault().getPreferenceStore().setValue(PREF_CATEGORIES,
                    categoriesItem.getAction().isChecked());
        }
        // store the current status of the advanced button
        ActionContributionItem advancedItem = (ActionContributionItem) getViewSite()
                .getActionBars().getToolBarManager().find("filter");
        if (advancedItem != null) {
            KimlUiPlugin.getDefault().getPreferenceStore().setValue(PREF_ADVANCED,
                    advancedItem.getAction().isChecked());
        }
        // dispose the view part
        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
        getSite().getWorkbenchWindow().getPartService().removePartListener(partListener);
        propSourceProvider.resetContext(null);
        toolkit.dispose();
        super.dispose();
    }
    
    /**
     * Refreshes the layout view asynchronously.
     */
    public void refresh() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                propSourceProvider.resetContext();
                page.refresh();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
        IDiagramLayoutManager<?> manager = LayoutManagersService.getInstance().getManager(part, null);
        if (manager != null) {
            propSourceProvider.resetContext(part);
            page.selectionChanged(part, selection);
            setPartText();
        }
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
                // a category was selected, apply options for all children
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
        final DiagramPartDefaultAction diagramPartDefaultAction
                = new DiagramPartDefaultAction(this, "", false);
        final DiagramPartDefaultAction modelDefaultAction
                = new DiagramPartDefaultAction(this, "", true);
        final DiagramTypeDefaultAction diagramTypeDefaultAction
                = new DiagramTypeDefaultAction(this, "");
        
        // dirty hack to add actions to an existing menu without having the menu manager
        menu.addMenuListener(new MenuAdapter() {
            public void menuShown(final MenuEvent event) {
                MenuItem diagramDefaultItem = null, diagramTypeDefaultItem = null,
                        diagramPartDefaultItem = null, modelDefaultItem = null;
                for (MenuItem item : menu.getItems()) {
                    if (item.getData() instanceof IContributionItem) {
                        String itemId = ((IContributionItem) item.getData()).getId();
                        if (DiagramDefaultAction.ACTION_ID.equals(itemId)) {
                            diagramDefaultItem = item;
                        } else if (DiagramPartDefaultAction.EDIT_PART_ACTION_ID.equals(itemId)) {
                            diagramPartDefaultItem = item;
                        } else if (DiagramPartDefaultAction.MODEL_ACTION_ID.equals(itemId)) {
                            modelDefaultItem = item;
                        } else if (DiagramTypeDefaultAction.ACTION_ID.equals(itemId)) {
                            diagramTypeDefaultItem = item;
                        }
                    }
                }
                
                // add the "set as default for this diagram" action
                if (propSourceProvider.hasContent()) {
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
                
                // add the "set as default for ... in this context" action
                String diagramPartName = getReadableName(false, true);
                if (diagramPartName != null && propSourceProvider.hasContent()) {
                    if (diagramPartDefaultItem == null) {
                        diagramPartDefaultAction.setText(Messages.getString("kiml.ui.16")
                                + " " + diagramPartName);
                        ContributionItem contributionItem = new ActionContributionItem(
                                diagramPartDefaultAction);
                        contributionItem.setId(DiagramPartDefaultAction.EDIT_PART_ACTION_ID);
                        contributionItem.fill(menu, -1);
                    } else {
                        diagramPartDefaultItem.setEnabled(true);
                        diagramPartDefaultItem.setText(Messages.getString("kiml.ui.16")
                                + " " + diagramPartName);
                    }
                } else if (diagramPartDefaultItem != null) {
                    diagramPartDefaultItem.setEnabled(false);
                }
                    
                // add the "set as default for all ..." action
                String modelName = getReadableName(true, true);
                if (modelName != null && propSourceProvider.hasContent()) {
                    if (modelDefaultItem == null) {
                        modelDefaultAction.setText(Messages.getString("kiml.ui.34")
                                + " " + modelName);
                        ContributionItem contributionItem = new ActionContributionItem(
                                modelDefaultAction);
                        contributionItem.setId(DiagramPartDefaultAction.MODEL_ACTION_ID);
                        contributionItem.fill(menu, -1);
                    } else {
                        modelDefaultItem.setEnabled(true);
                        modelDefaultItem.setText(Messages.getString("kiml.ui.34")
                                + " " + modelName);
                    }
                } else if (modelDefaultItem != null) {
                    modelDefaultItem.setEnabled(false);
                }
                
                // add the "set as default for diagram type" action
                LayoutOptionData diagramTypeOption = LayoutMetaDataService.getInstance().getOptionData(
                        LayoutOptions.DIAGRAM_TYPE.getId());
                LayoutContext context = propSourceProvider.getContext();
                ILayoutConfig config = DiagramLayoutEngine.INSTANCE.getOptionManager().createConfig(
                        context.getProperty(LayoutContext.DOMAIN_MODEL));
                String diagramType = (String) config.getOptionValue(diagramTypeOption, context);
                String diagramTypeName = LayoutConfigService.getInstance()
                        .getDiagramTypeName(diagramType);
                if (diagramTypeName != null) {
                    // make the diagram type name plural, if it does not already end with "s"
                    String dtdText;
                    if (diagramTypeName.endsWith("s")) {
                        dtdText = Messages.getString("kiml.ui.34") + " " + diagramTypeName;
                    } else {
                        dtdText = Messages.getString("kiml.ui.34") + " " + diagramTypeName + "s";
                    }
                    if (diagramTypeDefaultItem == null) {
                        diagramTypeDefaultAction.setText(dtdText);
                        ContributionItem contributionItem = new ActionContributionItem(
                                diagramTypeDefaultAction);
                        contributionItem.setId(DiagramTypeDefaultAction.ACTION_ID);
                        contributionItem.fill(menu, -1);
                    } else {
                        diagramTypeDefaultItem.setEnabled(true);
                        diagramTypeDefaultItem.setText(dtdText);
                    }
                    diagramTypeDefaultAction.setDiagramType(diagramType);
                } else if (diagramTypeDefaultItem != null) {
                    diagramTypeDefaultItem.setEnabled(false);
                }
            }
        });
    }
    
    /**
     * Builds a readable name for the current focus object.
     * 
     * @param plural if true, the plural form is created
     * @return a readable name for the edit part, or {@code null} if the focus object
     *     cannot be handled in this context
     */
    private String getReadableName(final boolean forDomainModel, final boolean plural) {
        if (!propSourceProvider.hasContent()) {
            // the property source provider has no cached content, so we cannot get any context info
            return "";
        }
        
        LayoutContext context = propSourceProvider.getContext();
        Object model = context.getProperty(LayoutContext.DOMAIN_MODEL);
        Object diagramPart = context.getProperty(LayoutContext.DIAGRAM_PART);
        String clazzName = null;
        if (model instanceof EObject) {
            clazzName = ((EObject) model).eClass().getInstanceTypeName();
        } else if (model != null) {
            clazzName = model.getClass().getName();
        }
        
        if (clazzName == null) {
            if (plural || diagramPart == null) {
                return null;
            }
            clazzName = diagramPart.getClass().getName();
            // omit the suffix "EditPart" if found
            if (clazzName.endsWith("EditPart")) {
                clazzName = clazzName.substring(0, clazzName.length() - "EditPart".length());
            }
        }
        int lastDotIndex = clazzName.lastIndexOf('.');
        if (lastDotIndex >= 0) {
            clazzName = clazzName.substring(lastDotIndex + 1);
        }
        StringBuilder stringBuilder = new StringBuilder();
        int length = clazzName.length();
        if (clazzName.endsWith("Impl")) {
            length -= "Impl".length();
        }
        for (int i = 0; i < length; i++) {
            char c = clazzName.charAt(i);
            if (i > 0 && Character.isUpperCase(c)
                    && !Character.isUpperCase(clazzName.charAt(i - 1))) {
                stringBuilder.append(' ');
            }
            if (!Character.isDigit(c)) {
                stringBuilder.append(c);
            }
        }
        if (plural && !clazzName.endsWith("s")) {
            stringBuilder.append('s');
        }
        if (!forDomainModel) {
            stringBuilder.append(" " + Messages.getString("kiml.ui.33"));
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the first diagram part in the current selection for which options are shown.
     * 
     * @return the selected diagram part, or {@code null} if there is none
     */
    public Object getCurrentDiagramPart() {
        if (propSourceProvider.hasContent()) {
            return propSourceProvider.getContext().getProperty(LayoutContext.DIAGRAM_PART);
        }
        return null;
    }

    /**
     * Returns the currently active workbench part that is tracked by the layout view.
     * 
     * @return the current workbench part, or {@code null} if there is none
     */
    public IWorkbenchPart getCurrentWorkbenchPart() {
        return propSourceProvider.getWorkbenchPart();
    }

    /**
     * Returns the current layout algorithm data.
     * 
     * @return the current layout algorithm data
     */
    public LayoutAlgorithmData[] getCurrentLayouterData() {
        // SUPPRESS CHECKSTYLE NEXT MagicNumber
        HashSet<LayoutAlgorithmData> data = new HashSet<LayoutAlgorithmData>(4);
        LayoutContext context = propSourceProvider.getContext();
        LayoutAlgorithmData lad = context.getProperty(DefaultLayoutConfig.CONTENT_ALGO);
        if (lad != null) {
            data.add(lad);
        }
        lad = context.getProperty(DefaultLayoutConfig.CONTAINER_ALGO);
        if (lad != null) {
            data.add(lad);
        }
        return data.toArray(new LayoutAlgorithmData[data.size()]);
    }

    /**
     * Sets a text line for the view part.
     */
    private void setPartText() {
        if (propSourceProvider.hasContent()) {
            StringBuilder textBuffer = new StringBuilder();
            String name = getReadableName(true, false);
            if (name != null) {
                textBuffer.append(name);
            }
            Object model = propSourceProvider.getContext().getProperty(LayoutContext.DOMAIN_MODEL);
            if (model != null) {
                String modelName = getProperty(model, "Name");
                if (modelName == null) {
                    modelName = getProperty(model, "Label");
                }
                if (modelName == null) {
                    modelName = getProperty(model, "Id");
                }
                if (modelName != null) {
                    textBuffer.append(" '" + modelName + "'");
                }
            }
            form.setText(textBuffer.toString());
        } else {
            form.setText("");
        }
    }
    
    /**
     * Gets a property of the given object by invoking its getter method.
     * 
     * @param object the object from which the property shall be fetched
     * @param property the name of a property, starting with a capital
     * @return the named property, or {@code null} if there is no such property
     */
    private static String getProperty(final Object object, final String property) {
        try {
            return (String) object.getClass().getMethod("get" + property).invoke(object);
        } catch (Exception exception) {
            return null;
        }
    }

}
