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
package org.eclipse.elk.graph.properties;

import java.lang.reflect.Method;

/**
 * A property that uses a string for identification.
 *
 * @kieler.design 2011-01-17 reviewed by haf, cmot, soh
 * @kieler.rating proposed yellow 2012-07-10 msp
 * @param <T> type of the property
 * @author msp
 */
public class Property<T> implements IProperty<T>, Comparable<IProperty<?>> {
    
    /** identifier of this property. */
    private final String id;
    /** the default value of this property. */
    private T defaultValue;
    
    /**
     * Creates a property with given identifier and {@code null} as default value.
     * 
     * @param theid the identifier
     */
    public Property(final String theid) {
        this.id = theid;
    }
    
    /**
     * Creates a property with given identifier and default value.
     * 
     * @param theid the identifier
     * @param thedefaultValue the default value
     */
    public Property(final String theid, final T thedefaultValue) {
        this(theid);
        this.defaultValue = thedefaultValue;
    }
    
    /**
     * Creates a property using another property as identifier, but changing
     * the default value.
     * 
     * @param other another property
     * @param thedefaultValue the new default value
     */
    public Property(final IProperty<T> other, final T thedefaultValue) {
        this(other.getId(), thedefaultValue);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object obj) {
        if (obj instanceof IProperty<?>) {
            return this.id.equals(((IProperty<?>) obj).getId());
        } else {
            return false;
        }
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
    public String toString() {
        return id;
    }
    
    /**
     * Returns the default value of this property.
     * 
     * @return the default value.
     */
    public T getDefault() {
        // Clone the default value if it's a Cloneable. We need to use reflection for this to work
        // properly (classes implementing Cloneable are not required to make their clone() method
        // public, so we need to check if they have such a method and invoke it via reflection, which
        // results in ugly and unchecked type casting)
        if (defaultValue instanceof Cloneable) {
            try {
                Method cloneMethod = defaultValue.getClass().getMethod("clone");
                @SuppressWarnings("unchecked")
                T clonedDefaultValue = (T) cloneMethod.invoke(defaultValue);
                return clonedDefaultValue;
            } catch (Exception e) {
                // Give up cloning and return the default instance
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
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
    public int compareTo(final IProperty<?> other) {
        return id.compareTo((String) other.getId());
    }

}
