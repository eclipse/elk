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
package org.eclipse.elk.core;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.util.IFactory;

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
public abstract class LayoutMetaDataService {

    /** identifier of the 'general' diagram type, which applies to all diagrams. */
    public static final String DIAGRAM_TYPE_GENERAL = "org.eclipse.elk.diagram.general";

   
    /** the layout data service instance, which is created lazily. */
    private static LayoutMetaDataService instance;
    /** the factory for creation of service instances. */
    private static IFactory<? extends LayoutMetaDataService> instanceFactory;

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
            if (instanceFactory == null) {
                try {
                    // Try to load the subclass that loads the content of the layoutProviders
                    // extension point; the subclass is accessible through the 'buddy policy'
                    // declared in the plugin manifest. By loading the class, the containing
                    // plugin is activated and the instance factory is set.
                    Class.forName("org.eclipse.elk.core.service.ExtensionLayoutMetaDataService");
                } catch (ClassNotFoundException exception) {
                    throw new IllegalStateException(
                            "The layout meta data service is not initialized yet."
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
    public static void setInstanceFactory(final IFactory<? extends LayoutMetaDataService> factory) {
        if (factory == null) {
            throw new NullPointerException("The given instance factory is null");
        }
        instanceFactory = factory;
        instance = null;
    }
    

    /** the instance of the registry class. */
    private final Registry registry = new Registry();
    
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
     * Class used to register the layout services. The access methods are not thread-safe, so use
     * only a single thread to register layout meta-data.
     */
    protected final class Registry {

        /**
         * The default constructor is hidden to prevent others from instantiating this class.
         */
        private Registry() {
        }

        /**
         * Registers the given layout provider. If there is already a registered provider data
         * instance with the same identifier, it is overwritten.
         * 
         * @param providerData
         *            data instance of the layout provider to register
         */
        public void addLayoutProvider(final LayoutAlgorithmData providerData) {
            if (layoutAlgorithmMap.containsKey(providerData.getId())) {
                layoutAlgorithmMap.remove(providerData.getId());
            }
            layoutAlgorithmMap.put(providerData.getId(), providerData);
        }

        /**
         * Registers the given layout option. If there is already a registered option data instance
         * with the same identifier, it is overwritten.
         * 
         * @param optionData
         *            data instance of the layout option to register
         */
        public void addLayoutOption(final LayoutOptionData optionData) {
            if (layoutOptionMap.containsKey(optionData.getId())) {
                layoutOptionMap.remove(optionData.getId());
            }
            layoutOptionMap.put(optionData.getId(), optionData);
        }

        /**
         * Registers the given layout type. If there is already a registered layout type instance
         * with the same identifier, it is overwritten, but its contained layout algorithms are copied.
         * 
         * @param typeData
         *            data instance of the layout type to register
         */
        public void addLayoutType(final LayoutTypeData typeData) {
            LayoutTypeData oldData = layoutTypeMap.get(typeData.getId());
            if (oldData != null) {
                typeData.getLayouters().addAll(oldData.getLayouters());
                layoutTypeMap.remove(typeData.getId());
            }
            layoutTypeMap.put(typeData.getId(), typeData);
        }

        /**
         * Registers the given category. Categories are used to group layout algorithms according
         * to the library they are contained in.
         * 
         * @param id
         *            identifier of the category
         * @param name
         *            user friendly name of the category
         */
        public void addCategory(final String id, final String name) {
            categoryMap.put(id, name);
        }

    }

    /**
     * Returns the instance of the registry class associated with the this layout data service.
     * 
     * @return the registry instance, or {@code null} if the service instance has not been registered
     */
    protected final Registry getRegistry() {
        return registry;
    }

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

}
