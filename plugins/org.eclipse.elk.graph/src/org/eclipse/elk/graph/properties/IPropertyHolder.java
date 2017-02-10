/*******************************************************************************
 * Copyright (c) 2009, 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.graph.properties;

import java.util.Map;

/**
 * Interface for holders of property values.
 *
 * @kieler.design 2011-01-17 reviewed by haf, cmot, soh
 * @kieler.rating proposed yellow 2012-07-10 msp
 * @author msp
 */
public interface IPropertyHolder {
    
    /**
     * Sets a property value. No type checking is performed while setting, so
     * users of this method must take care that the right object types are generated.
     * 
     * @param <T> type of property
     * @param property the property to set
     * @param value the new value
     * @return <code>this</code> {@link IPropertyHolder} for convenience
     */
    <T> IPropertyHolder setProperty(IProperty<? super T> property, T value);
    
    /**
     * Retrieves a property value. If the property is not set, its default value shall be taken,
     * which is taken from the given property instance.
     * 
     * @param <T> type of property
     * @param property the property to get
     * @return the current value, or the default value if the property is not set
     */
    <T> T getProperty(IProperty<T> property);
    
    /**
     * Checks whether a value is configured for the given property. If not, the next call to
     * {@link #getProperty(IProperty)} will return the property's default value and set the
     * value to the default value for this property holder. After that, all further calls to
     * {@link #hasProperty(IProperty)} will return {@code true} for that property.
     * 
     * @param property the property.
     * @return {@code true} or {@code false} as a value is or is not set for the property. 
     */
    boolean hasProperty(IProperty<?> property);
    
    /**
     * Copy all properties from another property holder to this one.
     * 
     * @param holder another property holder
     * @return <code>this</code> {@link IPropertyHolder} for convenience
     */
    IPropertyHolder copyProperties(IPropertyHolder holder);
    
    /**
     * Returns a map of all assigned properties with associated values.
     * 
     * @return a map of all properties
     */
    Map<IProperty<?>, Object> getAllProperties();

}
