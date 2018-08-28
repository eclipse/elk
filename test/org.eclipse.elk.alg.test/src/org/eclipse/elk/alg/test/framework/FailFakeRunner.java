/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * If a white box test is not executed or a random input is used for an analysis test, a JUnit test should fail. This
 * causes the tests on the build server to fail and is a much stronger indication that something went wrong than an
 * error message printed to the console. For this purpose extra tests have to be executed and this special runner is
 * used to execute the tests and set their descriptions.
 */
public class FailFakeRunner extends BlockJUnit4ClassRunner {

    /** The reason for the failure of the test. */
    private FailReason reason;
    /** The Id of the layout algorithm. */
    private String algorithm;
    /** The name of the test class. */
    private String testClass;
    /** The name of the graph. */
    private String graphName;

    /**
     * Creates a new instance.
     * 
     * @param klass
     *            the fake test class.
     * @param algorithm
     *            the name of the tested layout algorithm.
     * @param testClass
     *            the name of test class.
     * @param graphName
     *            the name of the graph.
     * @param reason
     *            the reason for the test's failure.
     * @throws InitializationError
     *             if an error occurs during initialization.
     */
    public FailFakeRunner(final Class<?> klass, final String algorithm, final String testClass, final String graphName,
            final FailReason reason) throws InitializationError {
        
        super(klass);
        this.reason = reason;
        this.algorithm = algorithm;
        this.testClass = testClass;
        this.graphName = graphName;
    }

    @Override
    protected Description describeChild(final FrameworkMethod method) {
        String lineSeparator = System.getProperty("line.separator");
        
        String uniqueId = lineSeparator
                + reason.reason() + lineSeparator
                + "algorithm:" + algorithm + lineSeparator
                + "test class: " + testClass + lineSeparator
                + "graph:" + graphName + lineSeparator
                + "test method: " + testName(method) + lineSeparator
                + reason.description() + lineSeparator;
        
        return Description.createTestDescription(getTestClass().getJavaClass(), uniqueId, method.getAnnotations());
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Reasons for Test Failures
    
    /**
     * The possible reasons for test failures.
     */
    public static enum FailReason {
        /** Random input was fed to an analysis test. */
        RANDOM_ANALYSIS_INPUT,
        /** A white box test has not been executed. */
        UNUSED_WHITE_BOX_TEXT;

        /**
         * Returns a short reason that can be printed to cause panic.
         */
        public String reason() {
            switch (this) {
            case RANDOM_ANALYSIS_INPUT:
                return "RANDOM";
            case UNUSED_WHITE_BOX_TEXT:
                return "NOEXECUTION";
            default:
                assert false;
                return null;
            }
        }
        
        /**
         * Returns a user-readable description of this thing.
         */
        public String description() {
            switch (this) {
            case RANDOM_ANALYSIS_INPUT:
                return "Random input was fed into an analysis test.";
            case UNUSED_WHITE_BOX_TEXT:
                return "A white box test should have been executed, but wasn't.";
            default:
                assert false;
                return null;
            }
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Failing Tests
    
    /**
     * This test is executed to cause a test failure. This should be done if a white box test has not been executed.
     * This can be happen if the test should be executed before or after processors which are not executed.
     */
    public static class NoExecutionFailure {
        /**
         * This test fails in every case.
         */
        @Test
        public void failTest() {
            Assert.assertTrue("An white box test should be executed at least once.", false);
        }
    }
    
    /**
     * This test is executed to cause a test failure. This should be done if an analisys test has received random input.
     */
    public class RandomInputFailure {
        /**
         * This test fails in every case.
         */
        @Test
        public void failTest() {
            Assert.assertTrue("The input of an analysis cannot be a random graph.", false);
        }
    }

}
