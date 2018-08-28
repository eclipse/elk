/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.io;

/**
 * An absolute resource path, independent of system properties and whatnot. Should not be used for tests used in
 * automatic builds.
 */
public class AbsoluteResourcePath extends AbstractResourcePath {

    /**
     * Creates a new instance for the given path which is assumed to be absolute.
     */
    public AbsoluteResourcePath(final String filePath) {
        this.setRelativePathSpec(filePath);
        resolveRelativePathSpec();
    }

    /**
     * Returns the path of the file.
     * 
     * @return The path of the file.
     */
    @Override
    public String getPath() {
        return getRelativePathSpec();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof AbsoluteResourcePath) {
            AbsoluteResourcePath path = (AbsoluteResourcePath) o;
            if (this.getRelativePathSpec().equals(path.getRelativePathSpec())) {
                if (this.isRecursive() == path.isRecursive() && this.isDirectory() == path.isDirectory()) {
                    if (this.getFilter() != null) {
                        return (this.getFilter().equals(path.getFilter()));
                    } else {
                        return (path.getFilter() == null);
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = 0;
        result += getRelativePathSpec().hashCode() * 31;
        
        if (isRecursive()) {
            result += Math.pow(31, 2);
        }
        
        if (isDirectory()) {
            result += Math.pow(31, 3);
        }
        
        if (getFilter() != null) {
            result += getFilter().hashCode() * Math.pow(31, 3);
        }
        
        //SUPPRESS CHECKSTYLE PREVIOUS 15 MagicNumber
        return result;
    }

}
