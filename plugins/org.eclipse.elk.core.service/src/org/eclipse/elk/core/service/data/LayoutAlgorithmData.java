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
package org.eclipse.elk.core.service.data;

import java.util.Map;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.ILayoutAlgorithmData;
import org.eclipse.elk.core.options.GraphFeature;
import org.eclipse.elk.core.service.LayoutMetaDataService;
import org.eclipse.elk.core.util.IFactory;
import org.eclipse.elk.core.util.InstancePool;

import com.google.common.collect.Maps;

/**
 * Data type used to store information for a layout algorithm. Instances are created using
 * data from the {@code layoutProviders} extension point and are managed by
 * {@link LayoutMetaDataService}.
 * 
 * @kieler.design 2011-02-01 reviewed by cmot, soh
 * @kieler.rating yellow 2012-10-09 review KI-25 by chsch, bdu
 * @author msp
 */
public final class LayoutAlgorithmData implements ILayoutAlgorithmData, ILayoutMetaData {

    /**
     * The minimal allowed priority value. Priorities less or equal to this value are treated
     * as 'not supported'. The value is {@code Integer.MIN_VALUE >> 2}, a number 'close to
     * negative infinity' that leaves some space to the least representable number in order
     * to avoid underflows in computations.
     */
    public static final int MIN_PRIORITY = Integer.MIN_VALUE >> 2;
    /** default name for layout algorithms for which no name is given. */
    public static final String DEFAULT_LAYOUTER_NAME = "<Unnamed Layout Algorithm>";
    
    /** identifier of the layout provider. */
    private String id = "";
    /** user friendly name of the layout algorithm. */
    private String name = DEFAULT_LAYOUTER_NAME;
    /** runtime instance of the layout algorithm. */
    private InstancePool<AbstractLayoutProvider> providerPool;
    /** layout type identifier. */
    private String type = "";
    /** category identifier. */
    private String category = "";
    /** detail description. */
    private String description = "";
    /** an object holding preview image data. */
    private Object imageData;
    
    /** Map of known layout options. Keys are option data, values are the default values. */
    private final Map<LayoutOptionData, Object> knownOptions = Maps.newHashMap();
    /** map of supported diagrams. */
    private final Map<String, Integer> supportedDiagrams = Maps.newHashMap();
    /** map of supported graph features. */
    private final Map<GraphFeature, Integer> supportedFeatures = Maps.newEnumMap(GraphFeature.class);
    /** map of descriptions of supported graph features. */
    private final Map<GraphFeature, String> supportedFeaturesDescriptions =
            Maps.newEnumMap(GraphFeature.class);
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object obj) {
        if (obj instanceof LayoutAlgorithmData) {
            return this.id.equals(((LayoutAlgorithmData) obj).id);
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Layout Algorithm: " + id;
    }
    
    /**
     * Sets the knowledge status of the given layout option.
     * 
     * @param optionData layout option data
     * @param defaultValue the default value, or {@code null} if none is specified
     */
    public void setOption(final LayoutOptionData optionData, final Object defaultValue) {
        if (optionData != null) {
            knownOptions.put(optionData, defaultValue);
        }
    }
    
    /**
     * Determines whether the layout algorithm knows the given layout option.
     * 
     * @param optionData layout option data
     * @return true if the associated layout algorithm knows the option
     */
    public boolean knowsOption(final LayoutOptionData optionData) {
        return knownOptions.containsKey(optionData);
    }
    
    /**
     * Returns the layout algorithm's default value for the given option.
     * 
     * @param optionData layout option data
     * @return the associated default value, or {@code null} if there is none
     */
    public Object getDefaultValue(final LayoutOptionData optionData) {
        return knownOptions.get(optionData);
    }
    
    /**
     * Sets support for the given diagram type. If the priority is less or equal to
     * {@link #MIN_PRIORITY}, the type is treated as not supported.
     * 
     * @param diagramType identifier of diagram type
     * @param priority priority value, or {@link #MIN_PRIORITY} if the diagram type is not supported
     */
    public void setDiagramSupport(final String diagramType, final int priority) {
        if (priority > MIN_PRIORITY) {
            supportedDiagrams.put(diagramType, priority);
        } else {
            supportedDiagrams.remove(diagramType);
        }
    }
    
    /**
     * Returns a support value for the given diagram type. If the type is not supported,
     * {@link #MIN_PRIORITY} is returned, otherwise the returned value indicates the priority
     * this algorithm has for the diagram type. Algorithms with higher priority are privileged
     * over those with lower priority or no support when a diagram of specific type is encountered.
     * 
     * @param diagramType diagram type identifier
     * @return associated priority, or {@link #MIN_PRIORITY} if the diagram type is not supported
     */
    public int getDiagramSupport(final String diagramType) {
        Integer result = supportedDiagrams.get(diagramType);
        if (result != null) {
            return result;
        }
        return MIN_PRIORITY;
    }
    
    /**
     * Checks if the given diagram type is supported at all. This is equivalent to checking whether
     * {@link #getDiagramSupport(String)} returns something greater than {@link #MIN_PRIORITY}.
     * 
     * @param diagramType the diagram type to check.
     * @return {@code true} if the algorithm supports the given diagram type.
     */
    public boolean supportsFeature(final String diagramType) {
        return getDiagramSupport(diagramType) > MIN_PRIORITY;
    }
    
    /**
     * Sets support for the given graph feature. If the priority is less or equal to
     * {@link #MIN_PRIORITY}, the feature is treated as not supported.
     * 
     * @param graphFeature the graph feature
     * @param priority priority value, or {@link #MIN_PRIORITY} if the feature is not supported
     */
    public void setFeatureSupport(final GraphFeature graphFeature, final int priority) {
        if (priority > MIN_PRIORITY) {
            supportedFeatures.put(graphFeature, priority);
        } else {
            supportedFeatures.remove(graphFeature);
        }
    }
    
    /**
     * Returns a support value for the given graph feature. If the feature is not supported,
     * {@link #MIN_PRIORITY} is returned, otherwise the returned value indicates the priority
     * this algorithm has for the graph feature. Meta layout methods that automatically select
     * a suitable algorithm for a graph may consider priority information on graph features.
     * Algorithms with higher priority are privileged over those with lower priority with
     * respect to the features that are actually contained in the graph.
     * 
     * @param graphFeature the graph feature
     * @return associated priority, or {@link #MIN_PRIORITY} if the feature is not supported
     */
    public int getFeatureSupport(final GraphFeature graphFeature) {
        Integer result = supportedFeatures.get(graphFeature);
        if (result != null) {
            return result;
        }
        return MIN_PRIORITY;
    }
    
    /**
     * Checks if the given graph feature is supported at all. This is equivalent to checking whether
     * {@link #getFeatureSupport(GraphFeature)} returns something greater than {@link #MIN_PRIORITY}.
     * 
     * @param graphFeature the graph feature to check.
     * @return {@code true} if the algorithm supports the given feature.
     */
    public boolean supportsFeature(final GraphFeature graphFeature) {
        return getFeatureSupport(graphFeature) > MIN_PRIORITY;
    }
    
    /**
     * Adds a detailed description for a given {@link GraphFeature}. It should describe how well the
     * feature is supported by the algorithm and if there are any restrictions to its usage.
     * This setter does not perform any sanity checks whether the given feature is supported all or the
     * like. If the the description is {@code null} or the emtpy string, possibly existing descriptions
     * are removed.
     * 
     * @param feature the feature to describe.
     * @param featureDescription the features' description. May be {@code null}.
     */
    public void setSupportedFeatureDescription(final GraphFeature feature,
            final String featureDescription) {
        
        if (featureDescription != null && !"".equals(featureDescription)) {
            supportedFeaturesDescriptions.put(feature, featureDescription);
        } else {
            supportedFeaturesDescriptions.remove(feature);
        }
    }

    /**
     * Gets the detailed description of the given {@link GraphFeature} how well the feature is supported.
     * May be {@code null}.
     * 
     * @param feature the feature to get the description for.
     * @return the description if set, {@code null} otherwise.
     */
    public String getSupportedFeatureDescription(final GraphFeature feature) {
        return supportedFeaturesDescriptions.get(feature);
    }

    /**
     * {@inheritDoc}
     */
    public void setId(final String theid) {
        assert theid != null;
        this.id = theid;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the user-friendly name of the layout algorithm. If the name is {@code null} or empty,
     * it is replaced by a default string.
     *
     * @param thename the name to set
     */
    public void setName(final String thename) {
        if (thename == null || thename.length() == 0) {
            this.name = DEFAULT_LAYOUTER_NAME;
        } else {
            this.name = thename;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setDescription(final String thedescription) {
        if (thedescription == null) {
            this.description = "";
        } else {
            this.description = thedescription;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Create a pool for instances of the layout algorithm. The pool can be accessed with
     * {@link #getInstancePool()} in order to create instances of the layout algorithm.
     *
     * @param providerFactory a factory for layout providers
     */
    public void createPool(final IFactory<AbstractLayoutProvider> providerFactory) {
        this.providerPool = new InstancePool<AbstractLayoutProvider>(providerFactory);
    }

    /**
     * Returns an instance pool for layout providers. If multiple threads execute the layout
     * algorithm in parallel, each thread should use its own instance of the algorithm.
     *
     * @return a layout provider instance pool
     */
    public InstancePool<AbstractLayoutProvider> getInstancePool() {
        return providerPool;
    }

    /**
     * Sets the type identifier. This is usually done while reading data from the 'layoutProviders'
     * extension point.
     *
     * @param thetype the type identifier to set
     */
    public void setType(final String thetype) {
        this.type = thetype;
    }

    /**
     * Returns the layout type identifier. Layout types are represented by {@link LayoutTypeData}
     * and can be defined in the 'layoutProviders' extension point.
     *
     * @return the type identifier
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the category identifier. This is usually done while reading data from the respective
     * extension point.
     *
     * @param thecategory the category identifier to set
     */
    public void setCategory(final String thecategory) {
        if (thecategory == null) {
            this.category = "";
        } else {
            this.category = thecategory;
        }
    }

    /**
     * Returns the category identifier. Categories are used to group layout algorithms according
     * to the library in which they are contained and can be defined in the 'layoutProviders'
     * extension point.
     *
     * @return the category identifier
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns the preview image data.
     * 
     * @return the preview image
     */
    public Object getPreviewImage() {
        return imageData;
    }

    /**
     * Sets the preview image data.
     * 
     * @param thepreviewImage the preview image to set
     */
    public void setPreviewImage(final Object thepreviewImage) {
        this.imageData = thepreviewImage;
    }
    
}
