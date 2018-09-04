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

/**
 * This filter accepts files that contain test classes to be executed by the test framework. Such classes have to have
 * a name that starts or ends with {@code ElkTest}. Classes whose name starts with {@code ElkConfig} configure a test
 * run, but do not contain tests themselves, and are thus not accepted by this filter.
 */
public class ElkTestClassFilter implements FileFilter {

    @Override
    public boolean accept(final File pathname) {
        if (!pathname.exists()) {
            return false;
        } else if (pathname.isDirectory()) {
            // Directories are accepted because the framework looks for tests recursively
            return true;
        } else {
            String fileName = pathname.getName();
            if (fileName.endsWith("ElkTest.class")) {
                return true;
            } else {
                return fileName.endsWith(".class")
                        && fileName.startsWith("ElkTest")
                        && !fileName.startsWith("ElkTestConfig");
            }
        }
    }

}
