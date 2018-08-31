/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the information about each point of the layout process of the graph of a test mapping at which tests have to
 * be executed. Not at every point all test methods of a test class are executed and therefore the methods have to be
 * specified. For each breakpoint a test runner has to be present.
 */
public class WhiteBoxTestRun {

    /** The test mapping this test run belongs to. */
    private final TestMapping testMapping;

    /** The tests that have to executed before a processor is executed. */
    private final List<WhiteBoxTestBreakpoint> beforeProc = new ArrayList<>();
    /** The tests that have to executed after a processor is executed. */
    private final List<WhiteBoxTestBreakpoint> afterProc = new ArrayList<>();
    /** The tests that have to executed before a processor is executed on the root graph. */
    private final List<WhiteBoxTestBreakpoint> beforeRootProc = new ArrayList<>();
    /** The tests that have to executed after a processor is executed on the root graph. */
    private final List<WhiteBoxTestBreakpoint> afterRootProc = new ArrayList<>();
    
    
    /**
     * Creates a new test run for the given test mapping.
     */
    public WhiteBoxTestRun(final TestMapping testMapping) {
        this.testMapping = testMapping;
    }
    

    /**
     * Returns the test mapping this test run belongs to.
     */
    public TestMapping getTestMapping() {
        return testMapping;
    }

    /**
     * Returns the tests that have to executed before a processor is executed.
     */
    public List<WhiteBoxTestBreakpoint> getBeforeProc() {
        return beforeProc;
    }

    /**
     * Returns the tests that have to executed after a processor is executed.
     */
    public List<WhiteBoxTestBreakpoint> getAfterProc() {
        return afterProc;
    }

    /**
     * Returns the tests that have to executed before a processor is executed on the root graph.
     */
    public List<WhiteBoxTestBreakpoint> getBeforeRootProc() {
        return beforeRootProc;
    }

    /**
     * Returns the tests that have to executed after a processor is executed on the root graph.
     */
    public List<WhiteBoxTestBreakpoint> getAfterRootProc() {
        return afterRootProc;
    }

}
