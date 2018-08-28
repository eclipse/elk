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
 * The path to a resource which is part of an Eclipse bundle. Such resources are always in the main ELK repository, the
 * path of which is expected to be stored in the system property {@code ELK_REPO}. The actual location is described as
 * a bundle ID and a relative path in that bundle.
 */
public class BundleResourcePath extends AbstractResourcePath {

    /** The ID of the bundle. */
    private String bundleId;

    /**
     * Creates a new instance for a resource in the given bundle.
     * 
     * @param bundleId
     *            the bundle's ID. This does not need to include any path relative to the ELK repository's root. The
     *            class automatically determines whether to look for it in the {@code plugins} or in the {@code tests}
     *            folder based on the bundle ID.
     * @param filePath
     *            path to the resource, relative to the bundle's root folder.
     */
    public BundleResourcePath(final String bundleId, final String filePath) {
        this.bundleId = bundleId;
        this.setRelativePathSpec(filePath);
        resolveRelativePathSpec();
    }

    @Override
    public String getPath() {
        String path = System.getProperty("ELK_REPO");
        if (path == null) {
            path = System.getenv("ELK_REPO");
        }

        if (path == null) {
            System.err.println("The system property or environment variable \"ELK_REPO\" needs to be set "
                    + "to refer to resources in bundles.");
            return "";
        }
        
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }

        // Bundles with an ID ending with "test" are located in the directory test the others are in plugins. Other
        // directories are not relevant for the test framework.
        if (!Strings.isNullOrEmpty(bundleId)) {
            if (bundleId.endsWith("test")) {
                path += "test" + File.separator;
            } else {
                path += "plugins" + File.separator;
            }
        }
        
        // At this point the path specifies the point at which the directory with the bundle should be located. The
        // directory containing the bundle has the bundleId as its name.
        path += bundleId + File.separator;
        
        // At this point the path specifies the point at which the filePath starts.
        if (getRelativePathSpec().startsWith(File.separator)) {
            setRelativePathSpec(getRelativePathSpec().substring(File.separator.length()));
        }

        return path + getRelativePathSpec();
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
        if (o instanceof BundleResourcePath) {
            BundleResourcePath path = (BundleResourcePath) o;
            if (this.isRecursive() == path.isRecursive() && this.isDirectory() == path.isDirectory()
                    && this.bundleId.equals(path.bundleId)) {
                
                if (this.getRelativePathSpec().equals(path.getRelativePathSpec())) {
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
        
        result += bundleId.hashCode() * Math.pow(31, 4);
        //SUPPRESS CHECKSTYLE PREVIOUS 15 MagicNumber
        return result;
    }

}
