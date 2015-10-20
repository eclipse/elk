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
package org.eclipse.elk.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.elk.core.config.ILayoutConfig;
import org.eclipse.elk.core.config.SemanticLayoutConfig;
import org.eclipse.elk.core.util.IFactory;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.elk.graph.properties.IPropertyValueProxy;
import org.eclipse.elk.graph.properties.MapPropertyHolder;
import org.eclipse.emf.ecore.EClass;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * Service class for layout configuration. This class is used globally to retrieve layout option
 * configuration data, which is given through the {@code layoutConfigs} extension point.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow 2012-07-10 msp
 */
public abstract class LayoutConfigService {
    
    /**
     * Data element for custom layout configurators.
     */
    public static class ConfigData {
        /** the layout configurator implementation. */
        private ILayoutConfig config;
        /** the activation property. */
        private IProperty<Boolean> activation;
        /** the text of the activation menu entry. */
        private String activationText;
        /** the activation action that is executed when activation changes. */
        private Runnable activationAction;
        
        /**
         * Returns the layout configurator implementation.
         * 
         * @return the layout configurator
         */
        public ILayoutConfig getConfig() {
            return config;
        }
        
        /**
         * Sets the layout configurator implementation.
         * 
         * @param config the layout configurator
         */
        public void setConfig(final ILayoutConfig config) {
            this.config = config;
        }
        
        /**
         * Returns the activation property.
         * 
         * @return the activation property
         */
        public IProperty<Boolean> getActivationProperty() {
            return activation;
        }
        
        /**
         * Sets the activation property.
         * 
         * @param prop the activation property
         */
        public void setActivationProperty(final IProperty<Boolean> prop) {
            this.activation = prop;
        }
        
        /**
         * Returns the activation menu entry text.
         * 
         * @return the activation menu entry text
         */
        public String getActivationText() {
            return activationText;
        }
        
        /**
         * Sets the activation menu entry text.
         * 
         * @param text the activation menu entry text
         */
        public void setActivationText(final String text) {
            this.activationText = text;
        }
        
        /**
         * Returns the activation action that is executed when the configurator is enabled or
         * disabled.
         * 
         * @return the activation action
         */
        public Runnable getActivationAction() {
            return activationAction;
        }
        
        /**
         * Sets the activation action that is executed when the configurator is enabled or disabled.
         * 
         * @param action the activation action
         */
        public void setActivationAction(final Runnable action) {
            this.activationAction = action;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            if (config == null) {
                return super.toString();
            }
            return config.getClass().getSimpleName();
        }
    }
    
    
    /** the layout configuration service instance, which is created lazily. */
    private static LayoutConfigService instance;
    /** the factory for creation of service instances. */
    private static IFactory<? extends LayoutConfigService> instanceFactory;

    /**
     * Returns the layout configuration service instance. If no instance is created yet, create one
     * using the configured factory.
     * Note that the instance may change if the instance factory is reset. However, in usual
     * applications that should not happen.
     * 
     * @return the layout configuration service instance
     */
    public static synchronized LayoutConfigService getInstance() {
        if (instance == null) {
            if (instanceFactory == null) {
                try {
                    // Try to load the subclass that loads the content of the layoutConfigs
                    // extension point; the subclass is accessible through the 'buddy policy'
                    // declared in the plugin manifest. By loading the class, the containing
                    // plugin is activated and the instance factory is set.
                    Class.forName("org.eclipse.elk.core.service.ExtensionLayoutConfigService");
                } catch (ClassNotFoundException exception) {
                    throw new IllegalStateException("The layout config service is not initialized yet."
                            + " Load the plugin 'org.eclipse.elk.core.service' in order to initialize"
                            + " this service with Eclipse extensions.");
                }
            }
            instance = instanceFactory.create();
        }
        return instance;
    }
    
    /**
     * Set the factory for creating instances. If an instance is already created, it is cleared
     * so the next call to {@link #getInstance()} uses the new factory.
     * 
     * @param factory an instance factory
     */
    public static void setInstanceFactory(final IFactory<? extends LayoutConfigService> factory) {
        if (factory == null) {
            throw new NullPointerException("The given instance factory is null");
        }
        instanceFactory = factory;
        instance = null;
    }
    
    
    /** mapping of diagram type identifiers to their names. */
    private final Map<String, String> diagramTypeMap = Maps.newLinkedHashMap();
    /** mapping of object identifiers to associated options. */
    private final Map<String, Map<String, Object>> id2OptionsMap = Maps.newHashMap();
    /** mapping of domain class names to semantic layout configurations. */
    private final Multimap<String, SemanticLayoutConfig> semanticConfigMap = HashMultimap.create();
    /** list of general layout configurators. */
    private final List<ConfigData> configData = Lists.newLinkedList();
    /** property map for activation of registered layout configurators. */
    private final MapPropertyHolder configProperties = new MapPropertyHolder();
    
    /**
     * Returns the property holder to activate or deactivate registered layout configurations.
     * 
     * @return the property holder for configuration activation
     */
    public IPropertyHolder getConfigProperties() {
        return configProperties;
    }
    
    /**
     * Registers the given diagram type.
     * 
     * @param id
     *            identifier of the diagram type
     * @param name
     *            user friendly name of the diagram type
     */
    protected final void addDiagramType(final String id, final String name) {
        diagramTypeMap.put(id, name);
    }

    /**
     * Adds the given layout option value as default for an object identifier.
     * 
     * @param id
     *            identifier of the object to register
     * @param optionId
     *            identifier of a layout option
     * @param value
     *            value for the layout option
     */
    public final void addOptionValue(final String id, final String optionId, final Object value) {
        Map<String, Object> optionsMap = id2OptionsMap.get(id);
        if (optionsMap == null) {
            optionsMap = new LinkedHashMap<String, Object>();
            id2OptionsMap.put(id, optionsMap);
        }
        optionsMap.put(optionId, value);
    }

    /**
     * Remove the value of the given layout option.
     * 
     * @param id
     *            identifier of the object for which an option shall be removed
     * @param optionId
     *            identifier of a layout option
     */
    public final void removeOptionValue(final String id, final String optionId) {
        Map<String, Object> optionsMap = id2OptionsMap.get(id);
        if (optionsMap != null) {
            optionsMap.remove(optionId);
        }
    }

    /**
     * Registers the given semantic layout configuration.
     * 
     * @param clazzName
     *            domain model class name for which to register the configuration
     * @param config
     *            a semantic layout configuration
     */
    protected final void addSemanticConfig(final String clazzName, final SemanticLayoutConfig config) {
        semanticConfigMap.put(clazzName, config);
    }

    /**
     * Returns the name of the given diagram type.
     * 
     * @param id
     *            identifier of the diagram type
     * @return user friendly name of the diagram type, or {@code null} if there is no diagram type
     *         with the given identifier
     */
    public final String getDiagramTypeName(final String id) {
        return diagramTypeMap.get(id);
    }

    /**
     * Returns a collection of registered diagram types. The first element of each returned entry is
     * a diagram type identifier, the second element is the corresponding name.
     * 
     * @return the registered diagram types
     */
    public final List<Pair<String, String>> getDiagramTypes() {
        return Pair.fromMap(diagramTypeMap);
    }

    /**
     * Returns a map that contains all layout option values for an object identifier.
     * 
     * @param objectId
     *            an object identifier, such as an edit part class name, a domain model class name,
     *            or a diagram type id
     * @return a map of layout option identifiers to their values
     */
    public final Map<String, Object> getOptionValues(final String objectId) {
        Map<String, Object> optionsMap = id2OptionsMap.get(objectId);
        if (optionsMap != null) {
            LayoutMetaDataService dataService = LayoutMetaDataService.getInstance();
            for (Map.Entry<String, Object> entry : optionsMap.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof IPropertyValueProxy) {
                    value = ((IPropertyValueProxy) value).resolveValue(
                            dataService.getOptionData(entry.getKey()));
                    if (value != null) {
                        entry.setValue(value);
                    }
                }
            }
            return Collections.unmodifiableMap(optionsMap);
        }
        return Collections.emptyMap();
    }

    /**
     * Retrieves a layout option value for an object identifier.
     * 
     * @param objectId
     *            an object identifier, such as an edit part class name, a domain model class name,
     *            or a diagram type id
     * @param optionId
     *            a layout option identifier
     * @return the preconfigured value of the option, or {@code null} if the option is not set for
     *         the given object
     */
    public final Object getOptionValue(final String objectId, final String optionId) {
        Map<String, Object> optionsMap = id2OptionsMap.get(objectId);
        if (optionsMap != null) {
            Object value = optionsMap.get(optionId);
            if (value instanceof IPropertyValueProxy) {
                value = ((IPropertyValueProxy) value).resolveValue(
                        LayoutMetaDataService.getInstance().getOptionData(optionId));
                if (value != null) {
                    optionsMap.put(optionId, value);
                }
            }
            return value;
        }
        return null;
    }

    /**
     * Returns a map that contains all layout option values for a domain model class. This involves
     * options that are set for any superclass of the given one.
     * 
     * @param clazz
     *            a domain model class
     * @return a map of layout option identifiers to their values
     */
    public final Map<String, Object> getOptionValues(final EClass clazz) {
        if (clazz != null) {
            LayoutMetaDataService dataService = LayoutMetaDataService.getInstance();
            HashMap<String, Object> options = new HashMap<String, Object>();
            LinkedList<EClass> classes = new LinkedList<EClass>();
            classes.add(clazz);
            do {
                EClass c = classes.removeFirst();
                Map<String, Object> optionsMap = id2OptionsMap.get(c.getInstanceTypeName());
                if (optionsMap != null) {
                    for (Map.Entry<String, Object> entry : optionsMap.entrySet()) {
                        Object value = entry.getValue();
                        if (value instanceof IPropertyValueProxy) {
                            value = ((IPropertyValueProxy) value).resolveValue(
                                    dataService.getOptionData(entry.getKey()));
                            if (value != null) {
                                entry.setValue(value);
                            }
                        }
                        if (value != null) {
                            options.put(entry.getKey(), value);
                        }
                    }
                }
                classes.addAll(c.getESuperTypes());
            } while (!classes.isEmpty());
            return options;
        }
        return Collections.emptyMap();
    }

    /**
     * Retrieves a layout option value for a domain model class. This involves options that are set
     * for any superclass of the given one.
     * 
     * @param clazz
     *            a domain model class
     * @param optionId
     *            a layout option identifier
     * @return the option value for the class or a superclass, or {@code null} if the option is not
     *         set for the class
     */
    public final Object getOptionValue(final EClass clazz, final String optionId) {
        if (clazz != null) {
            LayoutMetaDataService dataService = LayoutMetaDataService.getInstance();
            LinkedList<EClass> classes = new LinkedList<EClass>();
            classes.add(clazz);
            do {
                EClass c = classes.removeFirst();
                Map<String, Object> optionsMap = id2OptionsMap.get(c.getInstanceTypeName());
                if (optionsMap != null) {
                    Object value = optionsMap.get(optionId);
                    if (value instanceof IPropertyValueProxy) {
                        value = ((IPropertyValueProxy) value).resolveValue(
                                dataService.getOptionData(optionId));
                        if (value != null) {
                            optionsMap.put(optionId, value);
                        }
                    }
                    if (value != null) {
                        return value;
                    }
                }
                classes.addAll(c.getESuperTypes());
            } while (!classes.isEmpty());
        }
        return null;
    }

    /**
     * Return the semantic layout configurators that are associated with the given domain model
     * class. This involves configurators that are set for any superclass of the given one.
     * The superclasses are determined through Java reflection.
     * 
     * @param clazz
     *            a domain model class
     * @return the semantic layout configurators for the class or a superclass
     */
    public final List<ILayoutConfig> getSemanticConfigs(final Class<?> clazz) {
        if (clazz != null) {
            List<ILayoutConfig> configs = new LinkedList<ILayoutConfig>();
            LinkedList<Class<?>> classes = new LinkedList<Class<?>>();
            classes.add(clazz);
            do {
                Class<?> c = classes.removeFirst();
                if (semanticConfigMap.containsKey(c.getName())) {
                    configs.addAll(semanticConfigMap.get(c.getName()));
                }
                if (c.getSuperclass() != null) {
                    classes.add(c.getSuperclass());
                }
                for (Class<?> i : c.getInterfaces()) {
                    classes.add(i);
                }
            } while (!classes.isEmpty());
            return configs;
        }
        return Collections.emptyList();
    }

    /**
     * Return the semantic layout configurators that are associated with the given domain model
     * class. This involves configurators that are set for any superclass of the given one.
     * The superclasses are determined through EMF reflection.
     * 
     * @param clazz
     *            a domain model class
     * @return the semantic layout configurators for the class or a superclass
     */
    public final List<ILayoutConfig> getSemanticConfigs(final EClass clazz) {
        if (clazz != null) {
            List<ILayoutConfig> configs = new LinkedList<ILayoutConfig>();
            LinkedList<EClass> classes = new LinkedList<EClass>();
            classes.add(clazz);
            do {
                EClass c = classes.removeFirst();
                if (semanticConfigMap.containsKey(c.getInstanceTypeName())) {
                    configs.addAll(semanticConfigMap.get(c.getInstanceTypeName()));
                }
                classes.addAll(c.getESuperTypes());
            } while (!classes.isEmpty());
            return configs;
        }
        return Collections.emptyList();
    }

    /**
     * Returns all general layout configurators that are currently active.
     * 
     * @return the active layout configurators
     */
    public final List<ILayoutConfig> getActiveConfigs() {
        LinkedList<ILayoutConfig> configs = new LinkedList<ILayoutConfig>();
        for (ConfigData data : configData) {
            if (data.activation == null || configProperties.getProperty(data.activation)) {
                configs.add(data.config);
            }
        }
        return configs;
    }
    
    /**
     * Returns all custom layout configurator data.
     * 
     * @return the registered custom layout configurators
     */
    public final List<ConfigData> getConfigData() {
        return Collections.unmodifiableList(configData);
    }
    
    /**
     * Registers the given custom configurator.
     * 
     * @param data meta data for a custom configurator
     */
    protected final void addCustomConfig(final ConfigData data) {
        configData.add(data);
    }

}
