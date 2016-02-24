/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    spoenemann - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.service.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.klayoutdata.KLayoutData;
import org.eclipse.elk.core.service.internal.LayoutOptionProxy;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.EMapPropertyHolder;
import org.eclipse.elk.graph.KGraphData;
import org.eclipse.elk.graph.KGraphPackage;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.PersistentEntry;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.emf.common.util.AbstractTreeIterator;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EContentsEList.FeatureFilter;
import org.eclipse.emf.ecore.util.EContentsEList.FeatureIteratorImpl;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

/**
 * Utilites for interacting with graphs based on metadata from the extension services.
 */
public final class ElkServiceUtil {
    
    private ElkServiceUtil() { }
    
    /**
     * Set a layout option using a serialized key / value pair.
     * 
     * @param graphData the graph data instance to modify
     * @param id the layout option identifier
     * @param value the value for the layout option
     */
    public static void setOption(final KGraphData graphData, final String id,
            final String value) {
        LayoutOptionData optionData = LayoutMetaDataService.getInstance().getOptionData(id);
        if (optionData != null) {
            Object obj = optionData.parseValue(value);
            if (obj != null) {
                graphData.setProperty(optionData, obj);
            }
        }
    }
    
    /**
     * Calls {@link #loadDataElements(KNode, boolean, IProperty...)} with {@code clearProperties} set to
     * {@code false}.
     * 
     * @param graph
     *            the root element of the graph to load elements of.
     * @param knownProps
     *            a set of additional properties that are known, hence should be parsed properly
     */
    public static void loadDataElements(final KNode graph, final IProperty<?>... knownProps) {
        loadDataElements(graph, PREDICATE_IS_KLAYOUTDATA, false, knownProps);
    }
    
    /**
     * Loads all {@link org.eclipse.elk.graph.properties.IProperty IProperty} of
     * {@link KLayoutData} elements of a KGraph by deserializing {@link PersistentEntry} tuples.
     * Values are parsed using layout option data obtained from the {@link LayoutMetaDataService}.
     * Options that cannot be resolved immediately (e.g. because the extension points have not been
     * read yet) are stored as {@link LayoutOptionProxy}.
     * 
     * @param graph
     *            the root element of the graph to load elements of.
     * @param clearProperties
     *            {@code true} if the properties of a property holder should be cleared before
     *            repopulating them based on persistent entries. Required if the removal of a
     *            persistent entry should result in the removal of the correspondind property. This
     *            is for example the case in our Xtext-based KGT editor.
     * @param knownProps
     *            a set of additional properties that are known, hence should be parsed properly
     */
    public static void loadDataElements(final KNode graph, final boolean clearProperties,
            final IProperty<?>... knownProps) {
        
        loadDataElements(graph, PREDICATE_IS_KLAYOUTDATA, clearProperties, knownProps);
    }

    /**
     * A predicate returning true if the passed element is an instance 
     * of {@link IPropertyHolder}.
     */
    public static final Predicate<EMapPropertyHolder> PREDICATE_IS_KLAYOUTDATA =
            new Predicate<EMapPropertyHolder>() {
                public boolean apply(final EMapPropertyHolder input) {
                    return input instanceof KLayoutData;
                }
            };
            
    /**
     * Calls {@link #loadDataElements(KNode, Predicate, boolean, IProperty...)} with
     * {@code clearProperties} set to {@code false}.
     * 
     * @param graph
     *            the root element of the graph to load elements of.
     * @param handledTypes
     *            a predicate checking if we desire to load data elements for a certain subclass of
     *            {@link EMapPropertyHolder}.
     * @param knownProps
     *            a set of additional properties that are known, hence should be parsed properly.
     * 
     * @return the graph itself
     */
    public static KNode loadDataElements(final KNode graph,
            final Predicate<EMapPropertyHolder> handledTypes, final IProperty<?>... knownProps) {

        return loadDataElements(graph, handledTypes, false, knownProps);
    }
    
    /**
     * A tree iterator that skips properties of {@link EMapPropertyHolder}s. For an explanation of
     * why this is necessary, see the implementation of
     * {@link ElkUtil#loadDataElements(KNode, Predicate, boolean, IProperty...)}.
     * 
     * @author cds
     */
    private static class PropertiesSkippingTreeIterator extends AbstractTreeIterator<EObject> {
        /** Bogus serial version ID. */
        private static final long serialVersionUID = 1L;

        /**
         * {@inheritDoc}.
         */
        PropertiesSkippingTreeIterator(final Object object, final boolean includeRoot) {
            super(object, includeRoot);
        }

        @Override
        protected Iterator<? extends EObject> getChildren(final Object object) {
            // We know that the object is an EObject; get an iterator over its content
            Iterator<EObject> iterator = ((EObject) object).eContents().iterator();
            
            // The iterator will usually be a FeatureIteratorImpl that we can set a feature filter on
            if (iterator instanceof FeatureIteratorImpl) {
                ((FeatureIteratorImpl<EObject>) iterator).filter(new FeatureFilter() {
                    public boolean isIncluded(final EStructuralFeature eStructuralFeature) {
                        // We include everything but properties (layout options)
                        if (eStructuralFeature.getContainerClass().equals(EMapPropertyHolder.class)) {
                            return eStructuralFeature.getFeatureID()
                                    != KGraphPackage.EMAP_PROPERTY_HOLDER__PROPERTIES;
                        } else {
                            return true;
                        }
                    }
                });
            }
            
            return iterator;
        }
    }
    
    /**
     * Loads all {@link org.eclipse.elk.graph.properties.IProperty IProperty} of elements
     * that pass the test performed by the {@code handledTypes} predicate of a KGraph by
     * deserializing {@link PersistentEntry} tuples. Values are parsed using layout option data
     * obtained from the {@link LayoutMetaDataService}. Options that cannot be resolved immediately
     * (e.g. because the extension points have not been read yet) are stored as
     * {@link LayoutOptionProxy}.
     * 
     * @param graph
     *            the root element of the graph to load elements of.
     * @param handledTypes
     *            a predicate checking if we desire to load data elements for a certain subclass of
     *            {@link EMapPropertyHolder}.
     * @param clearProperties
     *            {@code true} if the properties of a property holder should be cleared before
     *            repopulating them based on persistent entries. Required if the removal of a
     *            persistent entry should result in the removal of the correspondind property. This
     *            is for example the case in our Xtext-based KGT editor.
     * @param knownProps
     *            a set of additional properties that are known, hence should be parsed properly.
     * 
     * @return the graph itself
     */
    public static KNode loadDataElements(final KNode graph,
            final Predicate<EMapPropertyHolder> handledTypes, final boolean clearProperties,
            final IProperty<?>... knownProps) {

        Map<String, IProperty<?>> knowPropsMap = Maps.newHashMap();
        for (IProperty<?> p : knownProps) {
            knowPropsMap.put(p.getId(), p);
        }

        LayoutMetaDataService dataService = LayoutMetaDataService.getInstance();
        
        /* This is basically the same as graph.eAllContents(). However, using the latter would cause a
         * ConcurrentModificationException. The reason we're walking through the graph here is that we
         * rebuild properties based on persistent entries. But with eAllContents(), we would also iterate
         * over the properties we're modifying. That's where the exception comes in. To avoid that, we're
         * using a special tree iterator that skips over properties of EMapPropertyHolders. Magic!
         * (KIPRA-1541)
         */
        TreeIterator<EObject> iterator = new PropertiesSkippingTreeIterator(graph, false);
        while (iterator.hasNext()) {
            EObject eObject = iterator.next();
            if (eObject instanceof EMapPropertyHolder
                    && handledTypes.apply((EMapPropertyHolder) eObject)) {
                
                final EMapPropertyHolder holder = (EMapPropertyHolder) eObject;
                
                if (clearProperties && holder.getProperties() != null) {
                    holder.getProperties().clear();
                }
                
                for (PersistentEntry persistentEntry : holder.getPersistentEntries()) {
                    loadDataElement(dataService, holder, persistentEntry.getKey(),
                            persistentEntry.getValue(), knowPropsMap);
                }
            }
        }
        
        return graph;
    }

    /**
     * Configures the {@link org.eclipse.elk.graph.properties.IProperty layout option} given by
     * {@code key} and {@code value} in the given {@link IPropertyHolder}, if {@code key}
     * denominates a registered Layout Option; configures a {@link LayoutOptionProxy} otherwise.<br>
     * <br>
     * Extracted that part into a dedicated method in order to be able to re-use it, e.g. in
     * KLighD's ExpansionAwareLayoutOptionData.
     * 
     * @author chsch (extractor)
     * 
     * @param dataService
     *            the current {@link LayoutMetaDataService}
     * @param propertyHolder
     *            the {@link IPropertyHolder} to be configured
     * @param id
     *            the layout option's id
     * @param value
     *            the desired option value
     */
    public static void loadDataElement(final LayoutMetaDataService dataService,
            final IPropertyHolder propertyHolder, final String id, final String value) {
        Map<String, IProperty<?>> empty = Collections.emptyMap();
        loadDataElement(dataService, propertyHolder, id, value, empty);
    }
      
    /**
     * Configures the {@link org.eclipse.elk.graph.properties.IProperty layout option} given by
     * {@code key} and {@code value} in the given {@link IPropertyHolder}, if {@code key}
     * denominates a registered Layout Option; configures a {@link LayoutOptionProxy} otherwise.<br>
     * <br>
     * Extracted that part into a dedicated method in order to be able to re-use it, e.g. in
     * KLighD's ExpansionAwareLayoutOptionData.
     * 
     * @author chsch (extractor)
     * 
     * @param dataService
     *            the current {@link LayoutMetaDataService}
     * @param propertyHolder
     *            the {@link IPropertyHolder} to be configured
     * @param id
     *            the layout option's id
     * @param value
     *            the desired option value
     * @param knownProps
     *            a map with additional properties that are known, hence should be parsed properly.
     *            Only floats, integers, and boolean values can be parsed here.
     */
    public static void loadDataElement(final LayoutMetaDataService dataService,
            final IPropertyHolder propertyHolder, final String id, final String value,
            final Map<String, ? extends IProperty<?>> knownProps) {
        if (id != null && value != null) {
            // try to get the layout option from the data service.
            final LayoutOptionData layoutOptionData = dataService.getOptionDataBySuffix(id);

            // if we have a valid layout option, parse its value.
            if (layoutOptionData != null) {
                Object layoutOptionValue = layoutOptionData.parseValue(value);
                if (layoutOptionValue != null) {
                    propertyHolder.setProperty(layoutOptionData, layoutOptionValue);
                }
            } else {
                
                // is it an explicitly known property?
                if (knownProps.containsKey(id)) {
                    // id compare ok because Property's hashcode delegates to the id's hashcode    
                    Object parsed = parseSimpleDatatypes(value);
                    @SuppressWarnings("unchecked")
                    IProperty<Object> unchecked = (IProperty<Object>) knownProps.get(id);
                    propertyHolder.setProperty(unchecked, parsed);

                    // REMARK: A better solution would be to create a 'Typed'Property
                    // which knows about it's type such that we are able 
                    // to test the type of the parsed value here 
                    
                } else {
                    // the layout option could not be resolved, so create a proxy
                    LayoutOptionProxy.setProxyValue(propertyHolder, id, value);
                }
            }
        }
    }

    private static Object parseSimpleDatatypes(final String value) {
        try {
            return Float.valueOf(value);
        } catch (NumberFormatException ne) {
            // silent
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ne) {
            // silent
        }
        
        // Boolean#valueOf() returns false for every string that is
        // not "true" (case insensitive). Thus we have to explicitly
        // test for both literals.
        if (value.toLowerCase().equals(Boolean.FALSE.toString())) {
            return false;
        } else if (value.toLowerCase().equals(Boolean.TRUE.toString())) {
            return true;
        }

        return value;
    }

}
