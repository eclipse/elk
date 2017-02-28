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
package org.eclipse.elk.core.data;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.util.IFactory;
import org.eclipse.elk.core.util.InstancePool;
import org.eclipse.elk.graph.properties.GraphFeature;

import com.google.common.collect.Maps;

/**
 * Data type used to store information for a layout algorithm.
 * 
 * @kieler.design 2011-02-01 reviewed by cmot, soh
 * @kieler.rating yellow 2012-10-09 review KI-25 by chsch, bdu
 * @author msp
 */
public final class LayoutAlgorithmData implements ILayoutMetaData {

    /** identifier of the layout provider. */
    private final String id;
    /** user friendly name of the layout algorithm. */
    private final String name;
    /** runtime instance of the layout algorithm. */
    private final InstancePool<AbstractLayoutProvider> providerPool;
    /** layout category identifier. */
    private final String category;
    /** name of the bundle of which this algorithm is part of. */
    private final String bundle;
    /** detail description. */
    private final String description;
    /** a path to a preview image for displaying in user interfaces. */
    private final String imagePath;
    /** Set of supported graph features. */
    private final Set<GraphFeature> supportedFeatures;
    
    /** Map of known layout options. Keys are option data, values are the default values. */
    private final Map<LayoutOptionData, Object> knownOptions = Maps.newHashMap();
    
    /**
     * Create a layout algorithm data entry.
     */
    public LayoutAlgorithmData(final String aid, final String aname, final String adescription,
            final IFactory<AbstractLayoutProvider> providerFactory, final String acategory, final String abundle,
            final String aimagePath, final Set<GraphFeature> asupportedFeatures) {
        this.id = aid;
        this.name = aname;
        this.description = adescription;
        this.providerPool = new InstancePool<AbstractLayoutProvider>(providerFactory);
        this.category = acategory;
        this.bundle = abundle;
        this.imagePath = aimagePath;
        if (asupportedFeatures == null) {
            this.supportedFeatures = EnumSet.noneOf(GraphFeature.class);
        } else {
            this.supportedFeatures = asupportedFeatures;
        }
    }
    
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
     * Sets the knowledge status of the given layout option. This method should be called only from
     * {@link ILayoutMetaDataProvider} implementations.
     * 
     * @param optionData layout option data
     * @param defaultValue the default value, or {@code null} if none is specified
     */
    public void addKnownOption(final LayoutOptionData optionData, final Object defaultValue) {
        knownOptions.put(optionData, defaultValue);
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
     * Check whether the given graph feature is supported.
     * 
     * @param graphFeature the graph feature to check
     * @return {@code true} if the algorithm supports the given feature
     */
    public boolean supportsFeature(final GraphFeature graphFeature) {
        return supportedFeatures.contains(graphFeature);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getId() {
        return id;
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
    public String getDescription() {
        return description;
    }

    /**
     * Return an instance pool for layout providers. If multiple threads execute the layout
     * algorithm in parallel, each thread should use its own instance of the algorithm.
     *
     * @return a layout provider instance pool
     */
    public InstancePool<AbstractLayoutProvider> getInstancePool() {
        return providerPool;
    }

    /**
     * Returns the layout category identifier. Layout categories are represented by {@link LayoutCategoryData}
     * and can be defined in the 'layoutProviders' extension point.
     *
     * @return the category identifier
     */
    public String getCategoryId() {
        return category;
    }

    /**
     * Returns the bundle name. Bundles are used to group layout algorithms according to the library
     * in which they are contained.
     *
     * @return the bundle name
     */
    public String getBundleName() {
        return bundle;
    }

    /**
     * Returns the preview image path.
     * 
     * @return the preview image path
     */
    public String getPreviewImagePath() {
        return imagePath;
    }

}
