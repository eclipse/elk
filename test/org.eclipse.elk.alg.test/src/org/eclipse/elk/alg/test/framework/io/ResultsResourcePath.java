/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.io;

import java.io.File;

/**
 * The location of a results file relative to the path specified by the {@code RESULTS_PATH} system property.
 */
public class ResultsResourcePath extends AbstractResourcePath {

    /**
     * Creates a new instance.
     * 
     * @param relativePath
     *            path to the resulting file, relative to the {@code RESULTS_PATH} system property.
     */
    public ResultsResourcePath(final String relativePath) {
        this.setRelativePathSpec(relativePath);
        resolveRelativePathSpec();
    }

    /**
     * Constructs the absolute path to the file.
     */
    @Override
    public String getPath() {
        String path = System.getProperty("RESULTS_PATH");

        if (path == null) {
            return "";
        } else if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        
        // at this point the path specifies the point at which the  starts
        if (getRelativePathSpec().startsWith(File.separator)) {
            setRelativePathSpec(getRelativePathSpec().substring(File.separator.length()));
        }

        path += getRelativePathSpec();

        return path;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof BundleResourcePath) {
            BundleResourcePath path = (BundleResourcePath) o;
            if (this.getRelativePathSpec().equals(path.getRelativePathSpec())) {
                if (this.getRelativePathSpec().equals(path.getRelativePathSpec())
                        && this.isRecursive() == path.isRecursive()
                        && this.isDirectory() == path.isDirectory()) {
                    
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
        //SUPPRESS CHECKSTYLE PREVIOUS 10 MagicNumber
        
        return result;
    }
}
