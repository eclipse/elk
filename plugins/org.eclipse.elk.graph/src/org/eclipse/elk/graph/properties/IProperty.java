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

/**
 * Interface for property identifiers. Properties have a type and a default value, and
 * they have an internal mechanism for identification, which should be compatible
 * with their {@link java.lang.Object#equals(Object)} and {@link java.lang.Object#hashCode()}
 * implementations.
 *
 * @kieler.design 2011-01-17 reviewed by haf, cmot, soh
 * @kieler.rating proposed yellow 2012-07-10 msp
 * @param <T> type of the property
 * @author msp
 */
public interface IProperty<T> {
    
    /**
     * Returns the default value of this property.
     * 
     * @return the default value, or {@code null} if the property has no default value
     */
    T getDefault();
    
    /**
     * Returns an identifier string for this property.
     * 
     * @return an identifier
     */
    String getId();

}
