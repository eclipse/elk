/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework;

/**
 * Describes a white box test.
 */
class WhiteBoxTestDescription {
    
    /** A test name. */
    private String nameWithoutGraph;
    /** Number of expected test executions. */
    private int expectedExecutions;
    /** Number of actual test executions. */
    private int actualExecutions;
    /** Whether a test failed. */
    private boolean fail;
    
    /**
     * Creates a new instance initialized with the given values.
     */
    WhiteBoxTestDescription(final String nameWithoutGraph, final int actualExecutions, final int expectedExecutions,
            final boolean fail) {
        
        this.nameWithoutGraph = nameWithoutGraph;
        this.actualExecutions = actualExecutions;
        this.expectedExecutions = expectedExecutions;
        this.fail = fail;
    }
    
    /**
     * Returns the test name.
     */
    protected String getNameWithoutGraph() {
        return nameWithoutGraph;
    }

    /**
     * Sets the test name.
     */
    protected void setNameWithoutGraph(final String nameWithoutGraph) {
        this.nameWithoutGraph = nameWithoutGraph;
    }

    /**
     * Returns the number of actual executions.
     */
    protected int getActualExecutions() {
        return actualExecutions;
    }

    /**
     * Sets the number of actual executions.
     */
    protected void setActualExecutions(final int actualExecutions) {
        this.actualExecutions = actualExecutions;
    }

    /**
     * Returns the number of actual test executions.
     */
    protected int getExpectedExecutions() {
        return expectedExecutions;
    }

    /**
     * Sets the number of actual test executions.
     */
    protected void setExpectedExecutions(final int expectedExecutions) {
        this.expectedExecutions = expectedExecutions;
    }

    /**
     * Returns whether a test failed.
     */
    protected boolean isFail() {
        return fail;
    }

    /**
     * Sets whether a test failed.
     */
    protected void setFail(final boolean fail) {
        this.fail = fail;
    }
    
}