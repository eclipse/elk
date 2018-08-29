/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.analysis;

import org.eclipse.elk.alg.test.framework.io.AbstractResourcePath;

/**
 * Configuration of an analysis test. This class contains all necessary information to run an analysis
 * test on a graph. The analysis has to return an array of results.
 */
public class AnalysisConfiguration {

    /** The analysis to be run. */
    private Class<? extends IAnalysis> analysis;
    /** Number of values contained in the result. */
    private int resultLength;
    /** The maximum positive deviation for a result to still be considered acceptable. */
    private Object[] allowedDeviationPos;
    /** The maximum negative deviation for a result to still be considered acceptable. */
    private Object[] allowedDeviationNeg;
    /** Specifies for each result value whether it should be compared against new results to spot regressions. */
    private boolean[] isRelevantPos;
    /** Specifies for each result value whether it should be compared against new results to spot regressions. */
    private boolean[] isRelevantNeg;
    /** The file in which the old results are stored with which to compare new results to spot regressions. */
    private AbstractResourcePath oldResultsFile;
    /** Whether to store the new results in the file as compare values for next tests. */
    private boolean shouldStoreResults;

    /**
     * Creates a new instance for the given analysis.
     */
    public AnalysisConfiguration(final Class<? extends IAnalysis> analysis, final int resultLength) {
        this.analysis = analysis;
        this.resultLength = resultLength;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration
    
    /**
     * Configures the given allowed positive and negative deviations.
     */
    public AnalysisConfiguration withAllowedDeviations(final Object[] positive, final Object[] negative) {
        if (positive.length != resultLength || negative.length != resultLength) {
            throw new IllegalArgumentException("Deviation specifications must match expected result length.");
        }
        
        this.allowedDeviationPos = positive;
        this.allowedDeviationNeg = negative;
        
        return this;
    }
    
    /**
     * Specifies for each value in the analysis result whether it should be checked for exceeding reference values in
     * the positive or negative direction. If a value exceeds the threshold, the analysis fails.
     */
    public AnalysisConfiguration withRelevantDeviations(final boolean[] positive, final boolean[] negative) {
        if (positive.length != resultLength || negative.length != resultLength) {
            throw new IllegalArgumentException("Deviation specifications must match expected result length.");
        }
        
        this.isRelevantPos = positive;
        this.isRelevantNeg = negative;
        
        return this;
    }
    
    /**
     * Specifies the given file as the file to load previous analysis results from.
     */
    public AnalysisConfiguration withResultsFile(final AbstractResourcePath file) {
        this.oldResultsFile = file;
        return this;
    }
    
    /**
     * Specifies whether results of new analysis runs should be stored in the results file.
     */
    public AnalysisConfiguration withStoreResults(final boolean store) {
        this.shouldStoreResults = store;
        return this;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Getters

    /**
     * Returns the analysis to be run.
     */
    public Class<? extends IAnalysis> getAnalysis() {
        return analysis;
    }

    /**
     * Returns the expected length of the results computed by the analysis.
     */
    public int getResultLength() {
        return resultLength;
    }

    /**
     * Returns the maximum positive deviation for a result to still be considered acceptable.
     */
    public Object[] getAllowedPositiveDeviations() {
        return allowedDeviationPos;
    }

    /**
     * 
     * Returns the maximum negative deviation for a result to still be considered acceptable.
     */
    public Object[] getAllowedNegativeDeviations() {
        return allowedDeviationNeg;
    }

    /**
     * Returns an array that specifies for each value whether it should be tested for deviating from reference values.
     */
    public boolean[] getRelevantPositiveDeviations() {
        return isRelevantPos;
    }

    /**
     * 
     * Returns an array that specifies for each value whether it should be tested for deviating from reference values.
     */
    public boolean[] getRelevantNegativeDeviations() {
        return isRelevantNeg;
    }

    /**
     * Returns the path to a file that contains results new results should be compared against.
     */
    public AbstractResourcePath getOldResultsFile() {
        return oldResultsFile;
    }

    /**
     * Whether new results are stored in the results file.
     */
    public boolean isStoringResults() {
        return shouldStoreResults;
    }

}
