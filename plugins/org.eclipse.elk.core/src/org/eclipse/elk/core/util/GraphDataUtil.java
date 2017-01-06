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
package org.eclipse.elk.core.util;

import java.util.Collections;
import java.util.Map;

import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.util.internal.LayoutOptionProxy;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;

/**
 * Utilities for interacting with graphs based on metadata from the extension services.
 */
public final class GraphDataUtil {
    
    private GraphDataUtil() { }
    
    /**
     * Set a layout option using a serialized key / value pair.
     * 
     * @param graphElement the graph data instance to modify
     * @param id the layout option identifier
     * @param value the value for the layout option
     */
    public static void setOption(final ElkGraphElement graphElement, final String id, final String value) {
        LayoutOptionData optionData = LayoutMetaDataService.getInstance().getOptionData(id);
        if (optionData != null) {
            Object obj = optionData.parseValue(value);
            if (obj != null) {
                graphElement.setProperty(optionData, obj);
            }
        }
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
