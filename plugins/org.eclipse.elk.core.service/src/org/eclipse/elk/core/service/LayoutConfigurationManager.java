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
package org.eclipse.elk.core.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.core.LayoutConfigurator;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.data.LayoutAlgorithmData;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.properties.IProperty;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Manages layout configurations derived from implementations of {@link ILayoutConfigurationStore}.
 * 
 * <p>Subclasses of this class can be bound in an {@link ILayoutSetup} injector for customization.
 * Note that this class is marked as {@link Singleton}, which means that exactly one instance is
 * created for each injector, i.e. for each registered {@link ILayoutSetup}.</p>
 */
@Singleton
public class LayoutConfigurationManager {
    
    /**
     * A specialized version of the recursive graph layout engine that uses a layout configuration
     * manager to obtain layout algorithm meta data for a given algorithm identifier.
     */
    public static class GraphLayoutEngine extends RecursiveGraphLayoutEngine {
        
        @Inject
        private LayoutConfigurationManager configManager;
        
        /**
         * {@inheritDoc}
         */
        @Override
        protected LayoutAlgorithmData getAlgorithm(final String algorithmId) {
            return configManager.getAlgorithm(algorithmId);
        }
        
    }
    
    /**
     * Suffix appended to layout option identifiers to mark them as applied recursively to all
     * contained elements.
     */
    private static final String RECURSIVE_SUFFIX = "#recursive";
    
    /**
     * Optional provider for layout configuration stores.
     */
    @Inject(optional = true)
    private ILayoutConfigurationStore.Provider configProvider;
    
    /**
     * Return a layout algorithm data instance for the given layout algorithm identifier. The id can be
     * attached to some graph element in order to select an algorithm. If no id is given, a default
     * algorithm is selected.
     * 
     * @return a layout algorithm, or {@code null} if none was found for the given hint
     */
    public LayoutAlgorithmData getAlgorithm(final String algorithmId) {
        final String finalAlgorithmId = algorithmId == null || algorithmId.isEmpty()
                ? "org.eclipse.elk.layered" : algorithmId;
        LayoutMetaDataService layoutDataService = LayoutMetaDataService.getInstance();
        LayoutAlgorithmData result = layoutDataService.getAlgorithmData(finalAlgorithmId);
        if (result != null) {
            return result;
        }
        Collection<LayoutAlgorithmData> allAlgorithmData = layoutDataService.getAlgorithmData();
        if (!allAlgorithmData.isEmpty()) {
            return allAlgorithmData.iterator().next();
        }
        return null;
    }
    
    /**
     * Return a layout algorithm data instance from the given configuration store. If no specific
     * algorithm is configured, a default algorithm is selected.
     * 
     * @return a layout algorithm, or {@code null} if none was found for the given configuration
     */
    public LayoutAlgorithmData getAlgorithm(final ILayoutConfigurationStore config) {
        return getAlgorithm((String) getRawOptionValue(LayoutOptions.ALGORITHM, config));
    }
    
    /**
     * Determine a list of options supported by the layout algorithms from the given configuration store.
     * 
     * @param config a layout configuration store associated to a graph element
     * @return a list of supported options
     */
    public List<LayoutOptionData> getSupportedOptions(final ILayoutConfigurationStore config) {
        LayoutMetaDataService layoutDataService = LayoutMetaDataService.getInstance();
        
        List<LayoutOptionData> optionData = new LinkedList<LayoutOptionData>();
        Set<LayoutOptionData.Target> optionTargets = config.getOptionTargets();
        if (optionTargets.contains(LayoutOptionData.Target.PARENTS)) {
            LayoutAlgorithmData algoData = getAlgorithm(config);
            if (algoData != null) {
                optionData.addAll(layoutDataService.getOptionData(algoData, LayoutOptionData.Target.PARENTS));
            }
        }
        ILayoutConfigurationStore parentConfig = config.getParent();
        if (parentConfig != null) {
            LayoutAlgorithmData algoData = getAlgorithm(parentConfig);
            if (algoData != null) {
                for (LayoutOptionData.Target target : optionTargets) {
                    if (target != LayoutOptionData.Target.PARENTS) {
                        optionData.addAll(layoutDataService.getOptionData(algoData, target));
                    }
                }
            }
        }
        return optionData;
    }
    
    /**
     * Retrieve the currently valid value for the given layout option. If no value is found in the
     * given configuration store, a default value is taken.
     * 
     * @param optionData a layout option descriptor
     * @param config a layout configuration store from which to fetch values
     */
    public Object getOptionValue(final LayoutOptionData optionData, final ILayoutConfigurationStore config) {
        Object result = getRawOptionValue(optionData, config);
        if (optionData.equals(LayoutOptions.ALGORITHM)) {
            LayoutAlgorithmData algoData = getAlgorithm((String) result);
            if (algoData != null) {
                return algoData.getId();
            }
        } else if (result != null) {
            return result;
        }
        
        if (optionData.getTargets().contains(LayoutOptionData.Target.PARENTS)) {
            // Check default value of the content layout algorithm
            LayoutAlgorithmData algoData = getAlgorithm(config);
            if (algoData != null) {
                result = algoData.getDefaultValue(optionData);
                if (result != null) {
                    return result;
                }
            }
        } else {
            // Check default value of the container layout algorithm
            ILayoutConfigurationStore parentConfig = config.getParent();
            if (parentConfig != null) {
                LayoutAlgorithmData algoData = getAlgorithm(parentConfig);
                if (algoData != null) {
                    result = algoData.getDefaultValue(optionData);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        
        // Fall back to default value of the option itself
        result = optionData.getDefault();
        if (result != null) {
            return result;
        }
        
        // Fall back to default-default value
        return optionData.getDefaultDefault();
    }
    
    /**
     * Retrieve the currently valid value for the given layout option. Default values are not considered.
     * 
     * @param option a layout option
     * @param config a layout configuration store from which to fetch values
     */
    protected Object getRawOptionValue(final IProperty<?> option, final ILayoutConfigurationStore config) {
        Object result = config.getOptionValue(option.getId());
        if (result == null) {
            String recursiveOptionId = option.getId() + RECURSIVE_SUFFIX;
            ILayoutConfigurationStore current = config;
            do {
                result = current.getOptionValue(recursiveOptionId);
                if (result == null) {
                    current = current.getParent();
                }
            } while (current != null && result == null);
        }
        if (result instanceof String && option instanceof LayoutOptionData) {
            return ((LayoutOptionData) option).parseValue((String) result);
        } else {
            return result;
        }
    }
    
    /**
     * Determine the parent configuration store that is responsible for the given child configuration.
     * The highest parent for which full-hierarchy layout is enabled is responsible, if it exists,
     * and otherwise it's the direct parent (which may be {@code null}).
     */
    protected ILayoutConfigurationStore getResponsibleParent(final ILayoutConfigurationStore config) {
        ILayoutConfigurationStore result = config.getParent();
        ILayoutConfigurationStore current = result;
        ILayoutConfigurationStore parent;
        while (current != null) {
            parent = current.getParent();
            if (parent != null && isFullHierarchyLayout(parent)) {
                result = parent;
            }
            current = parent;
        }
        return result;
    }
    
    /**
     * Determine whether the given configuration store is set up to do a full-hierarchy layout,
     * i.e. to process all contained hierarchy levels with a single layout algorithm invocation.
     */
    protected boolean isFullHierarchyLayout(final ILayoutConfigurationStore config) {
        Object layoutHierarchyVal = config.getOptionValue(LayoutOptions.LAYOUT_HIERARCHY.getId());
        boolean layoutHierarchy = layoutHierarchyVal instanceof Boolean ? (Boolean) layoutHierarchyVal
                : Boolean.parseBoolean((String) layoutHierarchyVal);
        if (layoutHierarchy) {
            LayoutAlgorithmData algoData = getAlgorithm(config);
            return algoData != null
                    && (algoData.supportsFeature(GraphFeature.COMPOUND)
                    || algoData.supportsFeature(GraphFeature.CLUSTERS));
        }
        return false;
    }
    
    /**
     * Set a default value for the given layout option. The value will be considered for all elements of
     * the containing diagram.
     */
    public void setDefaultValue(final LayoutOptionData optionData, final String value,
            final ILayoutConfigurationStore config) {
        config.setOptionValue(optionData.getId(), null);
        ILayoutConfigurationStore rootConfig = getRoot(config);
        rootConfig.setOptionValue(optionData.getId() + RECURSIVE_SUFFIX, value);
    }
    
    /**
     * Return the root configuration store for the given one by going up the parent hierarchy.
     */
    protected ILayoutConfigurationStore getRoot(final ILayoutConfigurationStore config) {
        ILayoutConfigurationStore current = config;
        ILayoutConfigurationStore parent;
        do {
            parent = current.getParent();
            if (parent == null) {
                return current;
            }
            current = parent;
        } while (current != null);
        return null;
    }
    
    /**
     * Clear all non-recursive layout option values for the given configuration store.
     */
    public void clearOptionValues(final ILayoutConfigurationStore config) {
        for (String optionId : config.getAffectedOptions()) {
            if (!optionId.endsWith(RECURSIVE_SUFFIX)) {
                config.setOptionValue(optionId, null);
            }
        }
    }
    
    /**
     * Clear all default layout option values for the given configuration store.
     */
    public void clearDefaultOptionValues(final ILayoutConfigurationStore config) {
        ILayoutConfigurationStore rootConfig = getRoot(config);
        for (String optionId : rootConfig.getAffectedOptions()) {
            if (optionId.endsWith(RECURSIVE_SUFFIX)) {
                config.setOptionValue(optionId, null);
            }
        }
    }
    
    /**
     * Create a layout configurator and initialize it with the option values from the given layout
     * configuration store provider.
     */
    public <T> LayoutConfigurator createConfigurator(final LayoutMapping layoutMapping) {
        LayoutConfigurator result = new LayoutConfigurator();
        if (configProvider != null) {
            configureElement(layoutMapping.getLayoutGraph(), layoutMapping, result);
            Iterator<KGraphElement> allElements = Iterators.filter(layoutMapping.getLayoutGraph().eAllContents(),
                    KGraphElement.class);
            while (allElements.hasNext()) {
                KGraphElement element = allElements.next();
                configureElement(element, layoutMapping, result);
            }
        }
        return result;
    }
    
    /**
     * Transfer option values from a configuration store of a graph element to a configurator.
     */
    protected void configureElement(final KGraphElement element, final LayoutMapping layoutMapping,
            final LayoutConfigurator configurator) {
        Object diagramPart = layoutMapping.getGraphMap().get(element);
        ILayoutConfigurationStore configurationStore = configProvider.get(layoutMapping.getWorkbenchPart(),
                diagramPart);
        if (configurationStore != null) {
            configureElement(element, configurationStore, configurator, false);
        }
    }
    
    /**
     * Transfer option values from the given configuration store to a configurator using the given graph
     * element as key.
     */
    protected void configureElement(final KGraphElement element,
            final ILayoutConfigurationStore configStore, final LayoutConfigurator configurator,
            final boolean recursiveOnly) {
        ILayoutConfigurationStore parentStore = configStore.getParent();
        if (parentStore != null) {
            configureElement(element, parentStore, configurator, true);
        }
        
        LayoutMetaDataService layoutDataService = LayoutMetaDataService.getInstance();
        for (String optionId : configStore.getAffectedOptions()) {
            boolean isRecursive = optionId.endsWith(RECURSIVE_SUFFIX);
            if (isRecursive || !recursiveOnly) {
                Object value = configStore.getOptionValue(optionId);
                String rawId = isRecursive ? optionId.substring(0, optionId.length() - RECURSIVE_SUFFIX.length())
                        : optionId;
                LayoutOptionData optionData = layoutDataService.getOptionData(rawId);
                if (optionData != null && value != null) {
                    if (value instanceof String) {
                        value = optionData.parseValue((String) value);
                    }
                    configurator.configure(element).setProperty(optionData, value);
                }
            }
        }
    }

}
