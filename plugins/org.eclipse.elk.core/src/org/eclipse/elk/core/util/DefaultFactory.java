/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util;

/**
 * A factory that uses the default constructor to create instances.
 *
 * @param <T> type of instances that are created by this factory
 * @author msp
 * @kieler.design 2014-04-17 reviewed by cds, chsch, tit, uru
 * @kieler.rating proposed yellow 2012-07-10 msp
 */
public final class DefaultFactory<T> implements IFactory<T> {

    /** the class for which instances shall be created. */
    private final Class<? extends T> clazz;
    
    /**
     * Creates an instance factory for the given class.
     * 
     * @param theclazz the class for which instances shall be created
     */
    public DefaultFactory(final Class<? extends T> theclazz) {
        this.clazz = theclazz;
    }
    
    /**
     * {@inheritDoc}
     */
    public T create() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException exception) {
            throw new WrappedException(exception);
        } catch (IllegalAccessException exception) {
            throw new WrappedException(exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void destroy(final T obj) {
        // do nothing by default, override in subclasses
    }

}
