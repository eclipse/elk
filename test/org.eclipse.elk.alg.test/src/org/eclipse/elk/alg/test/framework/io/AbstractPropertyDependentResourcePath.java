/*******************************************************************************
 * Copyright (c) 2018, 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.io;

/**
 * Subclasses of this class represent resource paths whose base path depends on a system property or environment
 * variable. Subclasses work just like direct subclasses of {@link AbstractResourcePath}, but can use the
 * {@link #basePathForProperty(String)} method to resolve the base path. 
 */
public abstract class AbstractPropertyDependentResourcePath extends AbstractResourcePath {
    
    /**
     * Determines the base path by querying the system property or environment variable with the given name. The
     * returned path will always end with a {@code /}.
     * 
     * @param propertyName
     *            the property name.
     * @return the base path.
     * @throws IllegalStateException
     *             if no system property and environment variable with the given name exists.
     */
    protected static String basePathForProperty(final String propertyName) {
        String path = System.getProperty(propertyName);
        if (path == null) {
            path = System.getenv(propertyName);
        }
        
        if (path == null) {
            throw new IllegalStateException("The system property or environment variable '"
                    + propertyName + "' needs to be set.");
        }
        
        return path;
    }
    
}
