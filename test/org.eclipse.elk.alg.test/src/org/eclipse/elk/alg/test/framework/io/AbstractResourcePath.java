/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Path to a resource to be used during tests. Resource paths can specify all files to be found in a directory (either
 * directly or recursively) or an actual file. They are based on a path specification relative to some base directory.
 * That base directory can be anything: a bundle's directory, a path specified through a system property, etc. If a
 * path specifies a whole number of files, they can be filtered.
 * 
 * <p>
 * In their constructor, subclasses will usually want to configure the relative path specification by calling
 * {@link #setRelativePathSpec(String)} and then let this class resolve the specification by calling
 * {@link #resolveRelativePathSpec()} to update the recursive and directory flags.
 * </p>
 * 
 * TODO: Document the relative path spec format.
 */
public abstract class AbstractResourcePath {

    /** The {@link File} that represents the actual resource. */
    private File file;
    /** The path specification, relative to a base path specified through a system property. */
    private String relativePathSpec;
    /** Whether the resource path is a directory. */
    private boolean isDirectory = false;
    /** Whether files in the resource path should be looked up recursively. */
    private boolean isRecursive = false;
    /** The filter for looking up files in directories. */
    private FileFilter filter = null;
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Abstract Methods
    
    /**
     * Returns the complete absolute path.
     * 
     * @return the complete path.
     */
    public abstract String getPath();
    
    @Override
    public abstract boolean equals(Object o);
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors

    /**
     * Returns the {@link File} that corresponds to this resource path. The file may describe an actual file or a
     * directory.
     */
    public File getFile() {
        String completePath = getPath();
        file = new File(completePath);
        return file;
    }

    /**
     * Returns this resource path's path specification relative to the base directory.
     */
    public String getRelativePathSpec() {
        return relativePathSpec;
    }

    /**
     * Sets the path specification relative to the base directory.
     */
    protected void setRelativePathSpec(final String filePath) {
        this.relativePathSpec = filePath;
    }

    /**
     * Returns the filter used to find files in directories.
     * 
     * @return the active file filter. May be {@code null}.
     */
    public FileFilter getFilter() {
        return filter;
    }
    
    /**
     * Sets the filter that should be used to look for files in directories. A {@code null} filter accepts all files.
     */
    protected void setFilter(final FileFilter filter) {
        this.filter = filter;
    }

    /**
     * A method chaining version of {@link #setFilter(FileFilter)}.
     */
    public AbstractResourcePath withFilter(final FileFilter fileFilter) {
        setFilter(fileFilter);
        return this;
    }

    /**
     * Returns whether the specified path leads to a directory.
     */
    public boolean isDirectory() {
        return isDirectory;
    }

    /**
     * Returns whether to look for files recursively. This is only meaningful if {@link #isDirectory()} returns
     * {@code true}.
     */
    public boolean isRecursive() {
        return isRecursive;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Path Resolving

    /**
     * Resolves the relative path specification. If the path specification specifies a directory, this method sets the
     * corresponding flags and removes the directory ending from the specification.
     */
    protected void resolveRelativePathSpec() {
        if (relativePathSpec.endsWith("/**")) {
            isDirectory = true;
            relativePathSpec = relativePathSpec.substring(0, relativePathSpec.length() - "/**".length());
        } else if (relativePathSpec.endsWith("/**/")) {
            isDirectory = true;
            isRecursive = true;
            relativePathSpec = relativePathSpec.substring(0, relativePathSpec.length() - "/**/".length());
        }
        
        insertFileSeparators();
    }

    /**
     * The path specification can be specified with "/" as file separator. This is more comfortable than the usage of
     * {@link File#separator}. This method replaces the "/" with {@link File#separator}.
     */
    private void insertFileSeparators() {
        List<String> allParts = new ArrayList<>();
        
        // look for "//"
        String[] firstParts = relativePathSpec.split("//");
        
        // look for "/"
        String[] secondParts;
        
        for (String string : firstParts) {
            secondParts = string.split("/");
            for (String string1 : secondParts) {
                allParts.add(string1);
            }
        }
        
        if (allParts.size() > 1) {
            relativePathSpec = allParts.stream().collect(Collectors.joining(File.separator));
        }
    }
}
