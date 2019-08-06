/*******************************************************************************
 * Copyright (c) 2018, 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.io;

/**
 * An absolute resource path, independent of system properties and whatnot. Should not be used for tests used in
 * automatic builds. However, this is what other paths can resolve to.
 */
public class AbsoluteResourcePath extends AbstractResourcePath {

    /**
     * Creates a new instance for the given path which is assumed to be absolute.
     */
    public AbsoluteResourcePath(final String filePath) {
        initialize(null, filePath);
    }

}
