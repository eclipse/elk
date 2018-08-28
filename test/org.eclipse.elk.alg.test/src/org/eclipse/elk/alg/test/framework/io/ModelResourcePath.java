/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.io;

import java.io.File;

import com.google.common.base.Strings;

/**
 * Path to a resource inside ELK's models repository. To turn the path into an absolute path, the system property
 * {@code MODELS_REPO} needs to be set to the path where the models repository is checked out.
 */
public class ModelResourcePath extends AbstractResourcePath {

    /**
     * Creates a new instance that points to the given resource in ELK's models repository.
     */
    public ModelResourcePath(final String filePath) {
        this.setRelativePathSpec(filePath);
        resolveRelativePathSpec();
    }

    /**
     * Constructs the absolute path.
     */
    @Override
    public String getPath() {
        String path = System.getProperty("MODELS_REPO");
        if (path == null) {
            path = System.getenv("MODELS_REPO");
        }
        
        if (path == null) {
            System.err.println("The system property or environment variable \"MODELS_REPO\" needs to be set "
                    + "to refer to resources in bundles.");
            return "";
            
        } else if (!Strings.isNullOrEmpty(getRelativePathSpec())) {
            if (!path.endsWith(File.separator)) {
                path += File.separator;
            }
            
            // at this point the path specifies the point at which the filePath starts
            if (getRelativePathSpec().startsWith(File.separator)) {
                setRelativePathSpec(getRelativePathSpec().substring(File.separator.length()));
            }

            path += getRelativePathSpec();
        }
        return path;
    }

    /**
     * This method checks if an Object and the instance of AbsoluteResourcePath are equal.
     * 
     * @param o
     *            The Object for that the equality is checked
     * @return Specifies, whether the input equals the instance the method is called on.
     */
    @Override
    public boolean equals(final Object o) {
        if (o instanceof ModelResourcePath) {
            ModelResourcePath path = (ModelResourcePath) o;
            if (this.getRelativePathSpec().equals(path.getRelativePathSpec())
                    && this.isRecursive() == path.isRecursive() && this.isDirectory() == path.isDirectory()) {
                
                if (this.getFilter() != null) {
                    return (this.getFilter().equals(path.getFilter()));
                } else {
                    return (path.getFilter() == null);
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
