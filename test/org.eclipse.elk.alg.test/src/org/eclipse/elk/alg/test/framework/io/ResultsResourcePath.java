/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.io;

import com.google.common.base.Strings;

/**
 * The location of a results file relative to the path specified by the {@code RESULTS_PATH} system property.
 */
public class ResultsResourcePath extends AbstractPropertyDependentResourcePath {
    
    /** The system property or environment variable that contains our base path. */
    public static final String PATH_PROPERTY = "RESULTS_PATH";

    /**
     * Creates a new instance that points to the given resource.
     * 
     * @param relativePath
     *            path to the resulting file, relative to the {@code RESULTS_PATH} system property.
     */
    public ResultsResourcePath(final String relativePath) {
        if (Strings.isNullOrEmpty(relativePath)) {
            throw new IllegalArgumentException("The file path cannot be empty.");
        }
        
        initialize(basePathForProperty(PATH_PROPERTY), relativePath);
    }

}
