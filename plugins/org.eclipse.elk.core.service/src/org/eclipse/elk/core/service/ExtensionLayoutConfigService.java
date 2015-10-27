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
package org.eclipse.elk.core.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.elk.core.LayoutConfigService;
import org.eclipse.elk.core.LayoutMetaDataService;
import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.core.config.ILayoutConfig;
import org.eclipse.elk.core.config.LayoutContext;
import org.eclipse.elk.core.config.SemanticLayoutConfig;
import org.eclipse.elk.core.util.LayoutOptionProxy;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * A layout configuration service that reads its content from the Eclipse extension registry.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow 2012-07-10 msp
 */
public class ExtensionLayoutConfigService extends LayoutConfigService {

    /** preference identifier for the list of registered diagram elements. */
    public static final String PREF_REG_ELEMENTS = "elk.reg.elements";

    /** identifier of the extension point for layout configuration. */
    protected static final String EXTP_ID_LAYOUT_CONFIGS = "org.eclipse.elk.core.layoutConfigs";
    /** name of the 'diagram type' element in the 'layout configs' extension point. */
    protected static final String ELEMENT_DIAGRAM_TYPE = "diagramType";
    /** name of the 'static config' element in the 'layout configs' extension point. */
    protected static final String ELEMENT_STATIC_CONFIG = "staticConfig";
    /** name of the 'semantic config' element in the 'layout configs' extension point. */
    protected static final String ELEMENT_SEMANTIC_CONFIG = "semanticConfig";
    /** name of the 'custom config' element in the 'layout configs' extension point. */
    protected static final String ELEMENT_CUSTOM_CONFIG = "customConfig";
    /** name of the 'global static config' element in the 'layout configs' extension point. */
    protected static final String ELEMENT_GLOBAL_STATIC_CONFIG = "globalStaticConfig";
    /** name of the 'activation' attribute in the extension points. */
    protected static final String ATTRIBUTE_ACTIVATION = "activation";
    /** name of the 'activationAction' attribute in the extension points. */
    protected static final String ATTRIBUTE_ACTIVATION_ACTION = "activationAction";
    /** name of the 'activationText' attribute in the extension points. */
    protected static final String ATTRIBUTE_ACTIVATION_TEXT = "activationText";
    /** name of the 'class' attribute in the extension points. */
    protected static final String ATTRIBUTE_CLASS = "class";
    /** name of the 'config' attribute in the extension points. */
    protected static final String ATTRIBUTE_CONFIG = "config";
    /** name of the 'default' attribute in the extension points. */
    protected static final String ATTRIBUTE_DEFAULT = "default";
    /** name of the 'id' attribute in the extension points. */
    protected static final String ATTRIBUTE_ID = "id";
    /** name of the 'name' attribute in the extension points. */
    protected static final String ATTRIBUTE_NAME = "name";
    /** name of the 'option' attribute in the extension points. */
    protected static final String ATTRIBUTE_OPTION = "option";
    /** name of the 'value' attribute in the extension points. */
    protected static final String ATTRIBUTE_VALUE = "value";
    
    /** id for option values set in case of a global static layout config. */
    protected static final String GLOBAL_OPTION_VALUE_ID = "org.eclipse.elk.layout.globalConfig";
    
    /** set of registered diagram elements. */
    private final Set<String> registeredElements = new HashSet<String>();

    /**
     * Load all registered extensions for the layout configuration extension point.
     */
    public ExtensionLayoutConfigService() {
        loadLayoutConfigsExtensions();
        loadPreferences();
    }

    /**
     * Report an error that occurred while reading extensions. May be overridden by subclasses in
     * order to report errors in a different way.
     * 
     * @param extensionPoint
     *            the identifier of the extension point
     * @param element
     *            the configuration element
     * @param attribute
     *            the attribute that contains an invalid entry
     * @param exception
     *            an optional exception that was caused by the invalid entry
     */
    protected void reportError(final String extensionPoint, final IConfigurationElement element,
            final String attribute, final Throwable exception) {
        String message;
        if (element != null && attribute != null) {
            message = "Extension point " + extensionPoint + ": Invalid entry in attribute '"
                            + attribute + "' of element " + element.getName() + ", contributed by "
                            + element.getContributor().getName();
        } else {
            message = "Extension point " + extensionPoint
                            + ": An error occured while loading extensions.";
        }
        IStatus status =
                new Status(IStatus.WARNING, ElkServicePlugin.PLUGIN_ID, 0, message, exception);
        StatusManager.getManager().handle(status);
    }

    /**
     * Report an error that occurred while reading extensions. May be overridden by subclasses in
     * order to report errors in a different way.
     * 
     * @param exception
     *            a core exception holding a status with further information
     */
    protected void reportError(final CoreException exception) {
        StatusManager.getManager().handle(exception, ElkServicePlugin.PLUGIN_ID);
    }

    /**
     * Loads and registers all layout info extensions from the extension point.
     */
    private void loadLayoutConfigsExtensions() {
        IConfigurationElement[] extensions =
                Platform.getExtensionRegistry().getConfigurationElementsFor(EXTP_ID_LAYOUT_CONFIGS);
        LayoutMetaDataService layoutDataService = LayoutMetaDataService.getInstance();
        assert layoutDataService != null;

        for (IConfigurationElement element : extensions) {
            if (ELEMENT_DIAGRAM_TYPE.equals(element.getName())) {
                // register a diagram type from the extension
                String id = element.getAttribute(ATTRIBUTE_ID);
                String name = element.getAttribute(ATTRIBUTE_NAME);
                if (id == null || id.length() == 0) {
                    reportError(EXTP_ID_LAYOUT_CONFIGS, element, ATTRIBUTE_ID, null);
                } else if (name == null) {
                    reportError(EXTP_ID_LAYOUT_CONFIGS, element, ATTRIBUTE_NAME, null);
                } else {
                    addDiagramType(id, name);
                }
            } else if (ELEMENT_STATIC_CONFIG.equals(element.getName())) {
                // register a layout option from the extension
                String classId = element.getAttribute(ATTRIBUTE_CLASS);
                String optionId = element.getAttribute(ATTRIBUTE_OPTION);
                String valueString = element.getAttribute(ATTRIBUTE_VALUE);
                if (classId == null || classId.length() == 0) {
                    reportError(EXTP_ID_LAYOUT_CONFIGS, element, ATTRIBUTE_CLASS, null);
                } else if (optionId == null || optionId.length() == 0) {
                    reportError(EXTP_ID_LAYOUT_CONFIGS, element, ATTRIBUTE_OPTION, null);
                } else {
                    LayoutOptionData optionData = layoutDataService.getOptionData(optionId);
                    if (optionData != null) {
                        try {
                            Object value = optionData.parseValue(valueString);
                            if (value != null) {
                                addOptionValue(classId, optionId, value);
                            }
                        } catch (IllegalStateException exception) {
                            reportError(EXTP_ID_LAYOUT_CONFIGS, element, ATTRIBUTE_VALUE, exception);
                        }
                    } else if (valueString != null) {
                        // the layout option could not be resolved, so create a proxy
                        addOptionValue(classId, optionId, new LayoutOptionProxy(valueString));
                    }

                }
            } else if (ELEMENT_SEMANTIC_CONFIG.equals(element.getName())) {
                // register a semantic layout configuration from the extension
                try {
                    SemanticLayoutConfig config =
                            (SemanticLayoutConfig) element
                                    .createExecutableExtension(ATTRIBUTE_CONFIG);
                    String clazz = element.getAttribute(ATTRIBUTE_CLASS);
                    if (clazz == null || clazz.length() == 0) {
                        reportError(EXTP_ID_LAYOUT_CONFIGS, element, ATTRIBUTE_CLASS, null);
                    } else {
                        addSemanticConfig(clazz, config);
                    }
                } catch (CoreException exception) {
                    reportError(exception);
                }
            } else if (ELEMENT_CUSTOM_CONFIG.equals(element.getName())) {
                // register a general layout configuration from the extension
                try {
                    ConfigData data = new ConfigData();
                    data.setConfig((ILayoutConfig) element
                            .createExecutableExtension(ATTRIBUTE_CLASS));
                    String activationId = element.getAttribute(ATTRIBUTE_ACTIVATION);
                    if (activationId != null) {
                        String def = element.getAttribute(ATTRIBUTE_DEFAULT);
                        Boolean defaultActivation =
                                def == null ? Boolean.FALSE : Boolean.valueOf(def);
                        data.setActivationProperty(new Property<Boolean>(activationId,
                                defaultActivation));
                    }
                    String text = element.getAttribute(ATTRIBUTE_ACTIVATION_TEXT);
                    data.setActivationText(text);
                    if (element.getAttribute(ATTRIBUTE_ACTIVATION_ACTION) != null) {
                        Runnable action =
                                (Runnable) element
                                        .createExecutableExtension(ATTRIBUTE_ACTIVATION_ACTION);
                        data.setActivationAction(action);
                    }
                    addCustomConfig(data);
                } catch (CoreException exception) {
                    reportError(exception);
                }
            } else if (ELEMENT_GLOBAL_STATIC_CONFIG.equals(element.getName())) {
                String optionId = element.getAttribute(ATTRIBUTE_OPTION);
                String valueString = element.getAttribute(ATTRIBUTE_VALUE);
                if (optionId == null || optionId.length() == 0) {
                    reportError(EXTP_ID_LAYOUT_CONFIGS, element, ATTRIBUTE_OPTION, null);
                } else {
                    LayoutOptionData optionData = layoutDataService.getOptionData(optionId);
                    if (optionData != null) {
                        try {
                            Object value = optionData.parseValue(valueString);
                            if (value != null) {
                                addOptionValue(GLOBAL_OPTION_VALUE_ID, optionId,
                                        value);
                            }
                        } catch (IllegalStateException exception) {
                            reportError(EXTP_ID_LAYOUT_CONFIGS, element, ATTRIBUTE_VALUE, exception);
                        }
                    } else if (valueString != null) {
                        // the layout option could not be resolved, so create a proxy
                        addOptionValue(GLOBAL_OPTION_VALUE_ID, optionId,
                                new LayoutOptionProxy(valueString));
                    }
                }

            }
        }
    }

    /**
     * Returns the preference name associated with the two identifiers.
     * 
     * @param id1
     *            first identifier
     * @param id2
     *            second identifier
     * @return a preference name for the combination of both identifiers
     */
    public static String getPreferenceName(final String id1, final String id2) {
        return id1 + "-" + id2; //$NON-NLS-1$
    }

    /**
     * Stores the layout option with given value for the diagram type.
     * 
     * @param diagramType
     *            a diagram type identifier
     * @param optionData
     *            a layout option data
     * @param valueString
     *            the value to store for the diagram type and option
     */
    public void storeOption(final String diagramType, final LayoutOptionData optionData,
            final String valueString) {
        Object value = optionData.parseValue(valueString);
        if (value != null) {
            addOptionValue(diagramType, optionData.getId(), value);
            IPreferenceStore preferenceStore = ElkServicePlugin.getDefault().getPreferenceStore();
            preferenceStore.setValue(getPreferenceName(diagramType, optionData.getId()),
                    valueString);
        }
    }

    /**
     * Stores the layout option with given value for the diagram part.
     * 
     * @param diagramPart
     *            a diagram part
     * @param optionData
     *            a layout option data
     * @param valueString
     *            the value to store for the edit part and option
     * @param storeDomainModel
     *            if true, the option is stored for the domain model element associated with the
     *            edit part, else for the edit part itself
     */
    public void storeOption(final Object diagramPart, final LayoutOptionData optionData,
            final String valueString, final boolean storeDomainModel) {
        Object value = optionData.parseValue(valueString);
        if (value != null) {
            Object object;
            if (storeDomainModel) {
                object = LayoutManagersService.getInstance().getContextValue(
                                LayoutContext.DOMAIN_MODEL, null, diagramPart);
            } else {
                object = LayoutManagersService.getInstance().getContextValue(
                                LayoutContext.DIAGRAM_PART, null, diagramPart);
                if (object == null) {
                    object = diagramPart;
                }
            }
            if (object != null) {
                String clazzName;
                if (object instanceof EObject) {
                    clazzName = ((EObject) object).eClass().getInstanceTypeName();
                } else {
                    clazzName = object.getClass().getName();
                }
                addOptionValue(clazzName, optionData.getId(), value);
                registeredElements.add(clazzName);
                IPreferenceStore preferenceStore =
                        ElkServicePlugin.getDefault().getPreferenceStore();
                preferenceStore.setValue(getPreferenceName(clazzName, optionData.getId()),
                        valueString);
            }
        }
    }

    /**
     * Loads preferences for KIML.
     */
    private void loadPreferences() {
        IPreferenceStore preferenceStore = ElkServicePlugin.getDefault().getPreferenceStore();
        LayoutMetaDataService layoutDataService = LayoutMetaDataService.getInstance();

        // load default options for diagram types
        List<Pair<String, String>> diagramTypes = getDiagramTypes();
        Collection<LayoutOptionData> layoutOptionData = layoutDataService.getOptionData();
        for (Pair<String, String> diagramType : diagramTypes) {
            for (LayoutOptionData data : layoutOptionData) {
                String preference = getPreferenceName(diagramType.getFirst(), data.getId());
                if (preferenceStore.contains(preference)) {
                    Object value = data.parseValue(preferenceStore.getString(preference));
                    if (value != null) {
                        addOptionValue(diagramType.getFirst(), data.getId(), value);
                    }
                }
            }
        }

        // load default options for diagram elements
        StringTokenizer editPartsTokenizer =
                new StringTokenizer(preferenceStore.getString(PREF_REG_ELEMENTS), ";");
        while (editPartsTokenizer.hasMoreTokens()) {
            registeredElements.add(editPartsTokenizer.nextToken());
        }
        for (String elementName : registeredElements) {
            for (LayoutOptionData data : layoutOptionData) {
                String preference = getPreferenceName(elementName, data.getId());
                if (preferenceStore.contains(preference)) {
                    Object value = data.parseValue(preferenceStore.getString(preference));
                    if (value != null) {
                        addOptionValue(elementName, data.getId(), value);
                    }
                }
            }
        }
    }

    /**
     * Stores preferences for KIML.
     */
    protected void storePreferences() {
        IPreferenceStore preferenceStore = ElkServicePlugin.getDefault().getPreferenceStore();

        // store set of registered diagram elements
        StringBuilder elementsString = new StringBuilder();
        for (String elementName : registeredElements) {
            elementsString.append(elementName + ";");
        }
        preferenceStore.setValue(PREF_REG_ELEMENTS, elementsString.toString());
    }

    /**
     * Returns the set of registered diagram elements. Modification of the returned set also affects
     * the internally stored set of elements.
     * 
     * @return the set of registered diagram elements
     */
    public Set<String> getRegisteredElements() {
        return registeredElements;
    }

    /**
     * Fill the given menu manager with contribution items for custom layout configurators.
     * 
     * @param menuManager
     *            a menu manager
     */
    public static void fillConfigMenu(final IMenuManager menuManager) {
        final LayoutConfigService service = getInstance();
        for (ConfigData data : service.getConfigData()) {
            if (data.getActivationText() != null && data.getActivationText().length() > 0
                    && data.getActivationProperty() != null) {
                final String text = data.getActivationText();
                final IProperty<Boolean> activation = data.getActivationProperty();
                final Runnable activationAction = data.getActivationAction();
                menuManager.add(new ContributionItem() {
                    public void fill(final Menu parent, final int index) {
                        final MenuItem menuItem = new MenuItem(parent, SWT.CHECK, index);
                        menuItem.setText(text);
                        menuItem.setSelection(service.getConfigProperties().getProperty(activation));
                        menuItem.addSelectionListener(new SelectionListener() {
                            public void widgetSelected(final SelectionEvent e) {
                                service.getConfigProperties().setProperty(activation,
                                        menuItem.getSelection());
                                if (activationAction != null) {
                                    activationAction.run();
                                }
                            }

                            public void widgetDefaultSelected(final SelectionEvent e) {
                            }
                        });
                        // execute the activation action initially
                        if (activationAction != null) {
                            activationAction.run();
                        }
                    }
                });
            }
        }
    }

}
