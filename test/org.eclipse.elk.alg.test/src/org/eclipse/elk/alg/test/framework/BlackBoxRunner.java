/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

/**
 * Executes black box tests. These tests are executed on the graphs after a completed layout run.
 */
class BlackBoxRunner extends SomeBoxRunner {

    /**
     * Creates a new instance.
     * 
     * @param algorithmId
     *            the ID of the tested algorithm.
     * @param testClasses
     *            the test classes with all tests that should be executed by this runner.
     * @param storeFailedGraphs
     *            whether graphs of failed tests should be stored.
     * @throws InitializationError
     *             if an error occurred during initialization.
     */
    BlackBoxRunner(final String algorithmId, final List<TestClass> testClasses,
            final boolean storeFailedGraphs) throws InitializationError {
        
        super(algorithmId, testClasses, storeFailedGraphs);
    }
    

    @Override
    public Description getDescription() {
        // Collect annotations from all test classes this runner will be touching
        List<Annotation> annotations = new ArrayList<Annotation>();
        for (TestMapping testMapping : getTestMappings()) {
            for (TestClass testClass : testMapping.getTestClasses()) {
                annotations.addAll(Arrays.asList(testClass.getAnnotations()));
            }
        }
        
        // As representation for the algorithm not the complete name but just the last part of the name should be used.
        String[] algorithmParts = getAlgorithmId().split(Pattern.quote("."));
        Description description = Description.createSuiteDescription(
                "Black box test runner for " + algorithmParts[algorithmParts.length - 1],
                annotations.toArray());
        
        // Add all child runners
        for (TestMapping testMapping : getTestMappings()) {
            for (TestClass testClass : testMapping.getTestClasses()) {
                description.addChild(testMapping.getRunnerForClass(testClass).getDescription());
            }
        }
        
        return description;
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Test Execution

    @Override
    public void run(final RunNotifier notifier) {
        notifier.fireTestRunStarted(getDescription());
        
        RecursiveGraphLayoutEngine layoutEngine = new RecursiveGraphLayoutEngine();
        
        for (TestMapping testMapping : getTestMappings()) {
            // TODO @BeforeLayout
            
            // Execute layout
            IElkProgressMonitor progressMonitor = new BasicProgressMonitor();
            layoutEngine.layout(testMapping.getGraph(), progressMonitor);

            // Execute the tests
            for (TestClass testClass : testMapping.getTestClasses()) {
                ActualTestRunner runner = testMapping.getRunnerForClass(testClass);
                runner.setGraph(testMapping.getGraph());
                
                runner.run(notifier);
            }
            
            // TODO @AfterLayout
        }
    }

}
