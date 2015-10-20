/*******************************************************************************
 * Copyright (c) 2012, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util;

import org.eclipse.elk.core.LayoutMetaDataService;
import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.IPropertyHolder;
import org.eclipse.elk.graph.properties.IPropertyValueProxy;
import org.eclipse.elk.graph.properties.Property;

/**
 * A proxy class for lazy resolving of layout options.
 *
 * @author msp
 */
public final class LayoutOptionProxy implements IPropertyValueProxy {
    
    /** the serialized layout option value. */
    private String value;
    
    /**
     * Create a layout option proxy for the given value.
     * 
     * @param value the serialized layout option value
     */
    public LayoutOptionProxy(final String value) {
        this.value = value;
    }
    
    /**
     * Create a layout option proxy with given key and value strings.
     * 
     * @param propertyHolder the property holder in which to store the new value
     * @param key the layout option identifier string
     * @param value the serialized value
     */
    public static void setProxyValue(final IPropertyHolder propertyHolder, final String key,
            final String value) {
        IProperty<LayoutOptionProxy> property = new Property<LayoutOptionProxy>(key);
        LayoutOptionProxy proxy = new LayoutOptionProxy(value);
        propertyHolder.setProperty(property, proxy);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T resolveValue(final IProperty<T> property) {
        LayoutOptionData optionData;
        if (property instanceof LayoutOptionData) {
            optionData = (LayoutOptionData) property;
        } else {
            optionData = LayoutMetaDataService.getInstance().getOptionData(property.getId());
        }
        if (optionData != null) {
            return (T) optionData.parseValue(value);
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return value;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof LayoutOptionProxy) {
            LayoutOptionProxy other = (LayoutOptionProxy) object;
            return this.value == null ? other.value == null : this.value.equals(other.value);
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        if (value != null) {
            return value.hashCode();
        }
        return 0;
    }

}
