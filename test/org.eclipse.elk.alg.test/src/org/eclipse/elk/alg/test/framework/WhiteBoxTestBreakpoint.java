/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework;

import java.util.List;

import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

/**
 * A point in the layout run of a white box test at which some test methods of a test class have to be executed.
 */
class WhiteBoxTestBreakpoint {

    /** The processor the test should be executed after or before. */
    private Class<? extends ILayoutProcessor<?>> processor;
    /** The test class that contains the test methods. */
    private TestClass testClass;
    /** The methods in the test class that should be executed. */
    private List<FrameworkMethod> methods;
    /** The test runner to execute the test methods. */
    private ActualTestRunner runner;

    /**
     * Creates a new breakpoint.
     * 
     * @param processor
     *            the processor the tests have to be executed before or after.
     * @param testClass
     *            the test class that contains the test methods.
     * @param methods
     *            the methods in the test class that should be executed.
     */
    WhiteBoxTestBreakpoint(final Class<? extends ILayoutProcessor<?>> processor, final TestClass testClass,
            final List<FrameworkMethod> methods) {
        
        this.processor = processor;
        this.testClass = testClass;
        this.methods = methods;
    }

    /**
     * Returns the processor the tests have to be executed before or after.
     */
    public Class<? extends ILayoutProcessor<?>> getProcessor() {
        return processor;
    }

    /**
     * Returns the test class that contains the test methods.
     */
    public TestClass getTestClass() {
        return testClass;
    }

    /**
     * Returns the methods in the test class that should be executed.
     */
    public List<FrameworkMethod> getMethods() {
        return methods;
    }

    /**
     * Returns the test runner to execute the test methods.
     */
    public ActualTestRunner getRunner() {
        return runner;
    }
    
    /**
     * Returns the test runner to execute the test methods.
     */
    public void setRunner(final ActualTestRunner runner) {
        this.runner = runner;
    }

}
