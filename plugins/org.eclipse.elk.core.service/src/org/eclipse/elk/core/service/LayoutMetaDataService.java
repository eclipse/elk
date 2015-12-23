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
package org.eclipse.elk.core.service;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.service.data.LayoutAlgorithmData;
import org.eclipse.elk.core.service.data.LayoutOptionData;
import org.eclipse.elk.core.service.data.LayoutTypeData;
import org.eclipse.elk.core.util.IFactory;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.core.util.WrappedException;
import org.osgi.framework.Bundle;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * Singleton class for access to the KIML layout meta data. This class is used globally to retrieve
 * meta data for automatic layout through KIML, which is given through the {@code layoutProviders}
 * extension point.
 * The meta data are provided by a subclass through the nested registry instance.
 * 
 * @kieler.design 2011-03-14 reviewed by cmot, cds
 * @kieler.rating yellow 2012-10-09 review KI-25 by chsch, bdu
 * @author msp
 */
public final class LayoutMetaDataService {

    /** the layout data service instance, which is created lazily. */
    private static LayoutMetaDataService instance;
    /** the factory for creation of registry instances. */
    private static IFactory<? extends LayoutMetaDataService.Registry> registryFactory;

    /**
     * Returns the layout data service instance. If no instance is created yet, create one
     * using the configured factory.
     * Note that the instance may change if the instance factory is reset. However, in usual
     * applications that should not happen.
     * 
     * @return the layout data service instance
     */
    public static synchronized LayoutMetaDataService getInstance() {
        if (instance == null) {
            if (registryFactory == null) {
                throw new IllegalStateException("The layout meta data service is not initialized yet.");
            }
            instance = new LayoutMetaDataService();
            Registry registry = registryFactory.create();
            registry.load(instance);
        }
        return instance;
    }
    
    /**
     * Set the factory for creating registry instances. If an instance is already created, it is cleared
     * so the next call to {@link #getInstance()} uses the new factory.
     * 
     * @param factory an instance factory
     */
    public static void setRegistryFactory(final IFactory<? extends LayoutMetaDataService.Registry> factory) {
        if (factory == null) {
            throw new NullPointerException("The given instance factory is null");
        }
        registryFactory = factory;
        instance = null;
    }
    

    /** mapping of layout provider identifiers to their data instances. */
    private final Map<String, LayoutAlgorithmData> layoutAlgorithmMap = Maps.newLinkedHashMap();
    /** mapping of layout option identifiers to their data instances. */
    private final Map<String, LayoutOptionData> layoutOptionMap = Maps.newLinkedHashMap();
    /** mapping of layout type identifiers to their data instances. */
    private final Map<String, LayoutTypeData> layoutTypeMap = Maps.newLinkedHashMap();
    /** mapping of category identifiers to their names. */
    private final Map<String, String> categoryMap = Maps.newHashMap();
    
    /** additional map of layout algorithm suffixes to data instances. */
    private final Map<String, LayoutAlgorithmData> algorithmSuffixMap = Maps.newHashMap();
    /** additional map of layout option suffixes to data instances. */
    private final Map<String, LayoutOptionData> optionSuffixMap = Maps.newHashMap();
    /** additional map of layout type suffixes to data instances. */
    private final Map<String, LayoutTypeData> typeSuffixMap = Maps.newHashMap();

    /**
     * Returns the layout algorithm data associated with the given identifier.
     * 
     * @param id
     *            layout algorithm identifier
     * @return the corresponding layout algorithm data, or {@code null} if there is no algorithm
     *         with the given identifier
     */
    public final LayoutAlgorithmData getAlgorithmData(final String id) {
        return layoutAlgorithmMap.get(id);
    }

    /**
     * Returns a data collection for all registered layout algorithms. The collection is
     * unmodifiable.
     * 
     * @return collection of registered layout algorithms
     */
    public final Collection<LayoutAlgorithmData> getAlgorithmData() {
        return Collections.unmodifiableCollection(layoutAlgorithmMap.values());
    }
    
    /**
     * Returns a layout algorithm data that has the given suffix in its identifier.
     * 
     * @param suffix
     *            a layout algorithm identifier suffix
     * @return the first layout algorithm data that has the given suffix, or {@code null} if
     *          no algorithm has that suffix
     */
    public final LayoutAlgorithmData getAlgorithmDataBySuffix(final String suffix) {
        LayoutAlgorithmData data = layoutAlgorithmMap.get(suffix);
        if (data == null) {
            data = algorithmSuffixMap.get(suffix);
            if (data == null) {
                for (LayoutAlgorithmData d : layoutAlgorithmMap.values()) {
                    String id = d.getId();
                    if (id.endsWith(suffix) && (suffix.length() == id.length()
                            || id.charAt(id.length() - suffix.length() - 1) == '.')) {
                        algorithmSuffixMap.put(suffix, d);
                        return d;
                    }
                }
            }
        }
        return data;
    }

    /**
     * Returns the layout option data associated with the given identifier.
     * 
     * @param id
     *            layout option identifier
     * @return the corresponding layout option data, or {@code null} if there is no option with the
     *         given identifier
     */
    public final LayoutOptionData getOptionData(final String id) {
        return layoutOptionMap.get(id);
    }

    /**
     * Returns a data collection for all registered layout options. The collection is unmodifiable.
     * 
     * @return collection of registered layout options
     */
    public final Collection<LayoutOptionData> getOptionData() {
        return Collections.unmodifiableCollection(layoutOptionMap.values());
    }
    
    /**
     * Returns a layout option data that has the given suffix in its identifier.
     * 
     * @param suffix
     *            a layout option identifier suffix
     * @return the first layout option data that has the given suffix, or {@code null} if
     *          no option has that suffix
     */
    public final LayoutOptionData getOptionDataBySuffix(final String suffix) {
        LayoutOptionData data = layoutOptionMap.get(suffix);
        if (data == null) {
            data = optionSuffixMap.get(suffix);
            if (data == null) {
                for (LayoutOptionData d : layoutOptionMap.values()) {
                    String id = d.getId();
                    if (id.endsWith(suffix) && (suffix.length() == id.length()
                            || id.charAt(id.length() - suffix.length() - 1) == '.')) {
                        optionSuffixMap.put(suffix, d);
                        return d;
                    }
                }
            }
        }
        return data;
    }

    /**
     * Returns a list of layout options that are suitable for the given layout algorithm and layout
     * option target. The layout algorithm must know the layout options and at the target must be
     * active for each option.
     * 
     * @param algorithmData
     *            layout algorithm data
     * @param targetType
     *            type of layout option target
     * @return list of suitable layout options
     */
    public final List<LayoutOptionData> getOptionData(final LayoutAlgorithmData algorithmData,
            final LayoutOptionData.Target targetType) {
        List<LayoutOptionData> optionDataList = new LinkedList<LayoutOptionData>();
        for (LayoutOptionData optionData : layoutOptionMap.values()) {
            if (algorithmData.knowsOption(optionData)
                    || LayoutOptions.ALGORITHM.equals(optionData)) {
                if (optionData.getTargets().contains(targetType)) {
                    optionDataList.add(optionData);
                }
            }
        }
        return optionDataList;
    }

    /**
     * Returns the data instance of the layout type with given identifier.
     * 
     * @param id
     *            identifier of the type
     * @return layout type data instance with given identifier, or {@code null} if the layout type
     *         is not registered
     */
    public final LayoutTypeData getTypeData(final String id) {
        return layoutTypeMap.get(id);
    }

    /**
     * Returns a list of layout type identifiers and names. The first string in each entry is the
     * identifier, and the second string is the name.
     * 
     * @return a list of all layout types
     */
    public final Collection<LayoutTypeData> getTypeData() {
        return Collections.unmodifiableCollection(layoutTypeMap.values());
    }
    
    /**
     * Returns a layout type data that has the given suffix in its identifier.
     * 
     * @param suffix
     *            a layout type identifier suffix
     * @return the first layout type data that has the given suffix, or {@code null} if
     *          no layout type has that suffix
     */
    public final LayoutTypeData getTypeDataBySuffix(final String suffix) {
        LayoutTypeData data = layoutTypeMap.get(suffix);
        if (data == null) {
            data = typeSuffixMap.get(suffix);
            if (data == null) {
                for (LayoutTypeData d : layoutTypeMap.values()) {
                    String id = d.getId();
                    if (id.endsWith(suffix) && (suffix.length() == id.length()
                            || id.charAt(id.length() - suffix.length() - 1) == '.')) {
                        typeSuffixMap.put(suffix, d);
                        return d;
                    }
                }
            }
        }
        return data;
    }

    /**
     * Returns the name of the given category.
     * 
     * @param id
     *            identifier of the category
     * @return user friendly name of the category, or {@code null} if there is no category with the
     *         given identifier
     */
    public final String getCategoryName(final String id) {
        return categoryMap.get(id);
    }
    
    /**
     * Class used to register the layout services. The access methods are not thread-safe, so use
     * only a single thread to register layout meta-data.
     */
    public abstract static class Registry {
        
        /** identifier of the extension point for layout providers. */
        protected static final String EXTP_ID_LAYOUT_PROVIDERS = "org.eclipse.elk.core.service.layoutProviders";
        /** name of the 'layout algorithm' element in the 'layout providers' extension point. */
        protected static final String ELEMENT_LAYOUT_ALGORITHM = "layoutAlgorithm";
        /** name of the 'layout type' element in the 'layout providers' extension point. */
        protected static final String ELEMENT_LAYOUT_TYPE = "layoutType";
        /** name of the 'category' element in the 'layout providers' extension point. */
        protected static final String ELEMENT_CATEGORY = "category";
        /** name of the 'dependency' element in the 'layout providers' extension point. */
        protected static final String ELEMENT_DEPENDENCY = "dependency";
        /** name of the 'known option' element in the 'layout providers' extension point. */
        protected static final String ELEMENT_KNOWN_OPTION = "knownOption";
        /** name of the 'layout  option' element in the 'layout providers' extension point. */
        protected static final String ELEMENT_LAYOUT_OPTION = "layoutOption";
        /** name of the 'supported diagram' element in the 'layout providers' extension point. */
        protected static final String ELEMENT_SUPPORTED_DIAGRAM = "supportedDiagram";
        /** name of the 'supported feature' element in the 'layout providers' extension point. */
        protected static final String ELEMENT_SUPPORTED_FEATURE = "supportedFeature";
        /** name of the 'advanced' attribute in the extension points. */
        protected static final String ATTRIBUTE_ADVANCED = "advanced";
        /** name of the 'appliesTo' attribute in the extension points. */
        protected static final String ATTRIBUTE_APPLIESTO = "appliesTo";
        /** name of the 'category' attribute in the extension points. */
        protected static final String ATTRIBUTE_CATEGORY = "category";
        /** name of the 'class' attribute in the extension points. */
        protected static final String ATTRIBUTE_CLASS = "class";
        /** name of the 'default' attribute in the extension points. */
        protected static final String ATTRIBUTE_DEFAULT = "default";
        /** name of the 'description' attribute in the extension points. */
        protected static final String ATTRIBUTE_DESCRIPTION = "description";
        /** name of the 'enumValues' attribute used in doing remote layout. */
        protected static final String ATTRIBUTE_ENUMVALUES = "enumValues";
        /** name of the 'feature' attribute in the extension points. */
        protected static final String ATTRIBUTE_FEATURE = "feature";
        /** name of the 'id' attribute in the extension points. */
        protected static final String ATTRIBUTE_ID = "id";
        /** name of the 'implementation' attribute of a layout option of type 'remoteenum'. */
        protected static final String ATTRIBUTE_IMPLEMENTATION = "implementation";
        /** name of the 'name' attribute in the extension points. */
        protected static final String ATTRIBUTE_NAME = "name";
        /** name of the 'option' attribute in the extension points. */
        protected static final String ATTRIBUTE_OPTION = "option";
        /** name of the 'parameter' attribute in the extension points. */
        protected static final String ATTRIBUTE_PARAMETER = "parameter";
        /** name of the 'preview' attribute in the extension points. */
        protected static final String ATTRIBUTE_PREVIEW = "preview";
        /** name of the 'priority' attribute in the extension points. */
        protected static final String ATTRIBUTE_PRIORITY = "priority";
        /** name of the 'type' attribute in the extension points. */
        protected static final String ATTRIBUTE_TYPE = "type";
        /** name of the 'value' attribute in the extension points. */
        protected static final String ATTRIBUTE_VALUE = "value";
        
        public void load(final LayoutMetaDataService service) {
            loadLayoutProviderExtensions(service);
        }
        
        /**
         * Report an error that occurred while reading extensions.
         * 
         * @param extensionPoint the identifier of the extension point
         * @param element the configuration element
         * @param attribute the attribute that contains an invalid entry
         * @param exception an optional exception that was caused by the invalid entry
         */
        protected abstract void reportError(String extensionPoint, IConfigurationElement element,
                String attribute, Throwable exception);

        /**
         * Report an error that occurred while reading extensions.
         * 
         * @param exception a core exception holding a status with further information
         */
        protected abstract void reportError(CoreException exception);

        /**
         * Registers the given layout type. If there is already a registered layout type instance
         * with the same identifier, it is overwritten, but its contained layout algorithms are copied.
         */
        protected void addLayoutType(final LayoutTypeData typeData, final LayoutMetaDataService service) {
            LayoutTypeData oldData = service.layoutTypeMap.get(typeData.getId());
            if (oldData != null) {
                typeData.getLayouters().addAll(oldData.getLayouters());
                service.layoutTypeMap.remove(typeData.getId());
            }
            service.layoutTypeMap.put(typeData.getId(), typeData);
        }

        /**
         * Returns the extensions responsible for providing layout meta data. This method
         * can be overridden by subclasses in order to get extensions from a different source
         * than the Eclipse platform.
         * 
         * @return the extensions responsible for providing layout meta data
         */
        protected IConfigurationElement[] getProviderExtensions() {
            IConfigurationElement[] result = null;
            IExtensionRegistry extensionRegistry = null;
            try {
                extensionRegistry = RegistryFactory.getRegistry();
            } catch (Exception e) {
                // Ignore since an exception here means that this instance
                // is not being run in an eclipse environment
            }
            if (extensionRegistry != null) {
                result = extensionRegistry.getConfigurationElementsFor(EXTP_ID_LAYOUT_PROVIDERS);
            }
            return result;
        }
        
        /**
         * Loads and registers all layout provider extensions from the extension point.
         */
        protected void loadLayoutProviderExtensions(final LayoutMetaDataService service) {
            List<String[]> knownOptions = new LinkedList<String[]>();
            List<String[]> dependencies = new LinkedList<String[]>();
            
            IConfigurationElement[] extensions = getProviderExtensions();
            if (extensions == null || extensions.length == 0) {
                return;
            }
            for (IConfigurationElement element : extensions) {
                if (ELEMENT_LAYOUT_ALGORITHM.equals(element.getName())) {
                    // register a layout algorithm from the extension
                    loadLayoutAlgorithm(element, knownOptions, service);
                } else if (ELEMENT_LAYOUT_OPTION.equals(element.getName())) {
                    // register a layout option from the extension
                    loadLayoutOption(element, dependencies, service);
                } else if (ELEMENT_LAYOUT_TYPE.equals(element.getName())) {
                    // register a layout type from the extension
                    String id = element.getAttribute(ATTRIBUTE_ID);
                    if (id == null || id.length() == 0) {
                        reportError(EXTP_ID_LAYOUT_PROVIDERS, element, ATTRIBUTE_ID, null);
                    } else {
                        LayoutTypeData typeData = new LayoutTypeData();
                        typeData.setId(id);
                        typeData.setName(element.getAttribute(ATTRIBUTE_NAME));
                        typeData.setDescription(element.getAttribute(ATTRIBUTE_DESCRIPTION));
                        addLayoutType(typeData, service);
                    }
                } else if (ELEMENT_CATEGORY.equals(element.getName())) {
                    // register a category from the extension
                    String id = element.getAttribute(ATTRIBUTE_ID);
                    String name = element.getAttribute(ATTRIBUTE_NAME);
                    if (id == null || id.length() == 0) {
                        reportError(EXTP_ID_LAYOUT_PROVIDERS, element, ATTRIBUTE_ID, null);
                    } else if (name == null) {
                        reportError(EXTP_ID_LAYOUT_PROVIDERS, element, ATTRIBUTE_NAME, null);
                    } else {
                        service.categoryMap.put(id, name);
                    }
                }
            }
            
            // load layout algorithm options
            for (String[] entry : knownOptions) {
                LayoutAlgorithmData algoData = service.getAlgorithmData(entry[0]);
                LayoutOptionData optionData = service.getOptionData(entry[1]);
                if (algoData != null && optionData != null) {
                    try {
                        Object defaultValue = optionData.parseValue(entry[2]);
                        algoData.setOption(optionData, defaultValue);
                    } catch (IllegalStateException exception) {
                        reportError(EXTP_ID_LAYOUT_PROVIDERS, null, null, exception);
                    }
                }
            }
            
            // load layout option dependencies
            for (String[] entry : dependencies) {
                LayoutOptionData sourceOption = service.getOptionData(entry[0]);
                LayoutOptionData targetOption = service.getOptionData(entry[1]);
                if (sourceOption != null && targetOption != null) {
                    try {
                        Object value = targetOption.parseValue(entry[2]);
                        sourceOption.getDependencies().add(new Pair<LayoutOptionData, Object>(
                                targetOption, value));
                    } catch (IllegalStateException exception) {
                        reportError(EXTP_ID_LAYOUT_PROVIDERS, null, null, exception);
                    }
                }
            }
        }
        
        /**
         * Create a layout algorithm data instance and configure it with platform-specific extensions.
         * 
         * @param element a configuration element to use for configuration
         * @return a new layout algorithm data instance
         */
        protected abstract LayoutAlgorithmData createLayoutAlgorithmData(
                IConfigurationElement element);
        
        /**
         * Create a layout provider factory from a configuration element.
         * 
         * @param element a configuration element from an extension
         * @return a factory for layout provider instances
         */
        private IFactory<AbstractLayoutProvider> getLayoutProviderFactory(
                final IConfigurationElement element) {
            return new IFactory<AbstractLayoutProvider>() {
                public AbstractLayoutProvider create() {
                    try {
                        AbstractLayoutProvider provider = (AbstractLayoutProvider)
                                element.createExecutableExtension(ATTRIBUTE_CLASS);
                        provider.initialize(element.getAttribute(ATTRIBUTE_PARAMETER));
                        return provider;
                    } catch (CoreException e) {
                        throw new WrappedException(e);
                    }
                }
                public void destroy(final AbstractLayoutProvider provider) {
                    provider.dispose();
                }
            };
        }
        
        /**
         * Load a class from a configuration element.
         * 
         * @param element a configuration element from an extension
         * @return a class, or {@code null} if none could be loaded
         */
        private Class<?> loadClass(final IConfigurationElement element) {
            String className = element.getAttribute(ATTRIBUTE_CLASS);
            if (className != null && className.length() > 0) {
                Bundle contributor = Platform.getBundle(element.getContributor().getName());
                if (contributor != null) {
                    try {
                        Class<?> clazz = contributor.loadClass(className);
                        return clazz;
                    } catch (ClassNotFoundException exception) {
                        reportError(EXTP_ID_LAYOUT_PROVIDERS, element, ATTRIBUTE_CLASS, exception);
                    }
                }
            }
            return null;
        }
        
        /**
         * Load a layout algorithm from a configuration element.
         * 
         * @param element a configuration element from an extension
         * @param knownOptions the list of known layout options
         * @param service the meta data service
         */
        private void loadLayoutAlgorithm(final IConfigurationElement element,
                final List<String[]> knownOptions, final LayoutMetaDataService service) {
            try {
                IFactory<AbstractLayoutProvider> layoutProviderFactory = getLayoutProviderFactory(element);
                LayoutAlgorithmData algoData = createLayoutAlgorithmData(element);
                if (layoutProviderFactory != null) {
                    algoData.createPool(layoutProviderFactory);
                }
                String layouterId = element.getAttribute(ATTRIBUTE_ID);
                if (layouterId == null || layouterId.length() == 0) {
                    reportError(EXTP_ID_LAYOUT_PROVIDERS, element, ATTRIBUTE_ID, null);
                    return;
                }
                algoData.setId(layouterId);
                algoData.setName(element.getAttribute(ATTRIBUTE_NAME));
                algoData.setDescription(element.getAttribute(ATTRIBUTE_DESCRIPTION));
                algoData.setCategory(element.getAttribute(ATTRIBUTE_CATEGORY));
                
                // process the layout type
                String layoutType = element.getAttribute(ATTRIBUTE_TYPE);
                if (layoutType == null) {
                    layoutType = "";
                }
                LayoutTypeData typeData = service.getTypeData(layoutType);
                if (typeData == null) {
                    typeData = new LayoutTypeData();
                    typeData.setId(layoutType);
                    addLayoutType(typeData, service);
                }
                algoData.setType(layoutType);
                typeData.getLayouters().add(algoData);
                
                // process child elements (known options and supported diagrams and features)
                for (IConfigurationElement child : element.getChildren()) {
                    if (ELEMENT_KNOWN_OPTION.equals(child.getName())) {
                        String option = child.getAttribute(ATTRIBUTE_OPTION);
                        if (!Strings.isNullOrEmpty(option)) {
                            String defaultValue = child.getAttribute(ATTRIBUTE_DEFAULT);
                            knownOptions.add(new String[] { layouterId, option, defaultValue });
                        } else {
                            reportError(EXTP_ID_LAYOUT_PROVIDERS, child, ATTRIBUTE_OPTION, null);
                        }
                    } else if (ELEMENT_SUPPORTED_DIAGRAM.equals(child.getName())) {
                        String type = child.getAttribute(ATTRIBUTE_TYPE);
                        if (Strings.isNullOrEmpty(type)) {
                            reportError(EXTP_ID_LAYOUT_PROVIDERS, child, ATTRIBUTE_TYPE, null);
                        } else {
                            String priority = child.getAttribute(ATTRIBUTE_PRIORITY);
                            if (Strings.isNullOrEmpty(priority)) {
                                algoData.setDiagramSupport(type, 0);
                            }
                            try {
                                algoData.setDiagramSupport(type, Integer.parseInt(priority));
                            } catch (NumberFormatException exception) {
                                reportError(EXTP_ID_LAYOUT_PROVIDERS, child, ATTRIBUTE_PRIORITY,
                                        exception);
                            }
                        }
                    } else if (ELEMENT_SUPPORTED_FEATURE.equals(child.getName())) {
                        String featureString = child.getAttribute(ATTRIBUTE_FEATURE);
                        if (Strings.isNullOrEmpty(featureString)) {
                            reportError(EXTP_ID_LAYOUT_PROVIDERS, child, ATTRIBUTE_FEATURE, null);
                        } else {
                            String priority = child.getAttribute(ATTRIBUTE_PRIORITY);
                            String description = child.getAttribute(ATTRIBUTE_DESCRIPTION);
                            try {
                                GraphFeature feature = GraphFeature.valueOf(featureString.toUpperCase());
                                if (Strings.isNullOrEmpty(priority)) {
                                    algoData.setFeatureSupport(feature, 0);
                                } else {
                                    algoData.setFeatureSupport(feature, Integer.parseInt(priority));
                                }
                                if (!Strings.isNullOrEmpty(description)) {
                                    algoData.setSupportedFeatureDescription(feature, description);
                                }
                            } catch (IllegalArgumentException exception) {
                                reportError(EXTP_ID_LAYOUT_PROVIDERS, child, ATTRIBUTE_FEATURE, exception);
                            }
                        }
                    }
                }
                service.layoutAlgorithmMap.put(algoData.getId(), algoData);
            } catch (Throwable throwable) {
                reportError(EXTP_ID_LAYOUT_PROVIDERS, element, ATTRIBUTE_CLASS, throwable);
            }
        }
        
        /**
         * Load a layout option from a configuration element.
         * 
         * @param element a configuration element from an extension
         * @param dependencies the list of option dependencies
         * @param service the meta data service
         */
        private void loadLayoutOption(final IConfigurationElement element,
                final List<String[]> dependencies, final LayoutMetaDataService service) {
            LayoutOptionData optionData = new LayoutOptionData();
            // get option identifier
            String optionId = element.getAttribute(ATTRIBUTE_ID);
            if (Strings.isNullOrEmpty(optionId)) {
                reportError(EXTP_ID_LAYOUT_PROVIDERS, element, ATTRIBUTE_ID, null);
                return;
            }
            optionData.setId(optionId);
            // get option type
            String optionType = element.getAttribute(ATTRIBUTE_TYPE);        
            try {
                optionData.setType(optionType);
                optionData.setOptionClass(loadClass(element));
            } catch (IllegalArgumentException exception) {
                reportError(EXTP_ID_LAYOUT_PROVIDERS, element, ATTRIBUTE_TYPE, exception);
                return;
            }
            // get default value, lower bound, and upper bound
            try {
                Object defaultValue = optionData.parseValue(element.getAttribute(ATTRIBUTE_DEFAULT));
                optionData.setDefault(defaultValue);
            } catch (IllegalStateException exception) {
                reportError(EXTP_ID_LAYOUT_PROVIDERS, element, ATTRIBUTE_CLASS, exception);
            }
            // get name and description
            optionData.setName(element.getAttribute(ATTRIBUTE_NAME));
            optionData.setDescription(element.getAttribute(ATTRIBUTE_DESCRIPTION));
            // get option targets (which graph elements it applies to)
            optionData.setTargets(element.getAttribute(ATTRIBUTE_APPLIESTO));
            // whether the option should only be shown in advanced mode
            String advanced = element.getAttribute(ATTRIBUTE_ADVANCED);
            optionData.setAdvanced(advanced != null && advanced.equals("true"));
            
            // process child elements (dependencies)
            for (IConfigurationElement childElement : element.getChildren()) {
                if (ELEMENT_DEPENDENCY.equals(childElement.getName())) {
                    String depId = childElement.getAttribute(ATTRIBUTE_OPTION);
                    if (Strings.isNullOrEmpty(depId)) {
                        reportError(EXTP_ID_LAYOUT_PROVIDERS, childElement, ATTRIBUTE_OPTION, null);
                    } else {
                        dependencies.add(new String[] { optionId, depId,
                                childElement.getAttribute(ATTRIBUTE_VALUE) });
                    }
                }
            }
            
            service.layoutOptionMap.put(optionData.getId(), optionData);
        }

    }

}
