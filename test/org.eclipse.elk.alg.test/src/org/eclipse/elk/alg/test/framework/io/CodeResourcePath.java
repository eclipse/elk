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
 * Path to a resource inside ELK's code repository. To turn the path into an absolute path, the system property
 * {@code ELK_REPO} needs to be set to the path where the models repository is checked out.
 */
public class CodeResourcePath extends AbstractPropertyDependentResourcePath {
    
    /** The system property or environment variable that contains our base path. */
    public static final String PATH_PROPERTY = "ELK_REPO";

    /**
     * Creates a new instance that points to the given resource in ELK's code repository.
     */
    public CodeResourcePath(final String filePath) {
        if (Strings.isNullOrEmpty(filePath)) {
            throw new IllegalArgumentException("The file path cannot be empty.");
        }
        
        initialize(basePathForProperty(PATH_PROPERTY), filePath);
    }

}
