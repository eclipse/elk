/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.elk.alg.test.framework.annotations.FailIfNotExecuted;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.testing.TestController;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.graph.ElkNode;
import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * A test runner for a given experimental object. This test runner will perform layout on the experimental object and
 * call the appropriate test methods during and after the layout algorithm's execution.
 */
public class ExperimentRunner extends ParentRunner<FrameworkMethod> {
    
    /** ID of the next {@link ExperimentRunner} to be created. */
    private static int nextRunnerId = 0;
    /** ID of this runner, used to disambiguate descriptions of test methods. */
    private int runnerId;

    /** The runner that created us. We'll use that to enumerate our tests. */
    private final LayoutTestRunner parentRunner;
    /** The experimental object we'll be performing tests on. */
    private final ExperimentalObject experimentalObject;
    /** The concrete graph for the experimental object. Created when this runner is started. */
    private ElkNode realizedExperimentalObject;
    /** Descriptions for our test methods. */
    private Map<FrameworkMethod, Description> testDescriptions = new HashMap<>();

    /** Set of whitebox tests that have been executed. Important for {@link FailIfNotExecuted} support. */
    private Set<FrameworkMethod> executedWhiteboxTests = new HashSet<>();

    /**
     * Creates a new test runner that will perform tests on the given experimental object.
     * 
     * @throws InitializationError
     *             this shouldn't happen since any initialization problems should already have been caught by
     *             {@link LayoutTestRunner}.
     */
    public ExperimentRunner(final LayoutTestRunner parentRunner, final ExperimentalObject experimentalObject)
            throws InitializationError {

        super(parentRunner.getTestClass().getJavaClass());
        
        this.runnerId = ++nextRunnerId;

        this.parentRunner = parentRunner;
        this.experimentalObject = experimentalObject;

        // Our parent makes all tests available to use. Now we just have to create descriptions for them
        Stream.concat(parentRunner.getBlackboxTests().stream(), parentRunner.getWhiteboxTests().stream())
                .forEach(test -> describeChild(test));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Getters
    
    /**
     * Returns the experimental object for this runner.
     */
    public ExperimentalObject getExperimentalObject() {
        return experimentalObject;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // ParentRunner Child Management

    @Override
    protected String getName() {
        return experimentalObject.toString();
    }

    @Override
    protected boolean isIgnored(final FrameworkMethod child) {
        return child.getAnnotation(Ignore.class) != null;
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        return new ArrayList<>(testDescriptions.keySet());
    }

    @Override
    protected Description describeChild(final FrameworkMethod child) {
        if (!testDescriptions.containsKey(child)) {
            // Since other experiment runners will build descriptions for the same methods that we build descriptions
            // for, we need to disambiguate them.
            Description childDescription = Description.createTestDescription(getTestClass().getJavaClass(),
                    child.getName() + " (" + runnerId + ")", child.getAnnotations());
            testDescriptions.put(child, childDescription);
        }

        return testDescriptions.get(child);
    }

    @Override
    protected void runChild(final FrameworkMethod child, final RunNotifier notifier) {
        // We don't do anything here because this method is not called due to the way our implementation works
    }
    
    @Override
    protected Statement childrenInvoker(final RunNotifier notifier) {
        // Usually, each test method would be run by a statement. However, this is
        Statement statement = new RunExperimentStatement(notifier);
        statement = withExperimentalObjectCreator(statement);
        return statement;
    }

    /**
     * Wraps the statement in an instance of {@link ExperimentalObjectCreator}.
     */
    private Statement withExperimentalObjectCreator(final Statement statement) {
        return new ExperimentalObjectCreator(statement);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Actual Test Running
    
    private Object createTest() throws Throwable {
        return getTestClass().getOnlyConstructor().newInstance();
    }
    
    /**
     * Has the experimental object create the actual test graph.
     */
    private final class ExperimentalObjectCreator extends Statement {
        
        /** Next statement to be evaluated. */
        private final Statement nextStatement;
        
        private ExperimentalObjectCreator(final Statement nextStatement) {
            this.nextStatement = nextStatement;
        }

        @Override
        public void evaluate() throws Throwable {
            Object test = createTest();
            realizedExperimentalObject = getExperimentalObject().realize(test);
            
            nextStatement.evaluate();
        }
        
    }
    
    /**
     * Statement in charge of running the actual experiment.
     */
    private final class RunExperimentStatement extends Statement {

        /** The notifier we'll be notifying. */
        private RunNotifier notifier;
        
        private RunExperimentStatement(final RunNotifier notifier) {
            this.notifier = notifier;
        }
        
        @Override
        public void evaluate() throws Throwable {
            // Create a test controller if we have whitebox tests and the layout algorithm
            TestController testController = null;
            
            if (!parentRunner.getWhiteboxTests().isEmpty()) {
                testController = new TestController(experimentalObject.getLayoutAlgorithm().getAlgorithmId());
                
                // TODO Register listeners
            }
            
            // Let the recursive graph layout engine run the layout algorithm (if we have a test controller, that will
            // automatically cause white box tests to run)
            RecursiveGraphLayoutEngine layoutEngine = new RecursiveGraphLayoutEngine();
            layoutEngine.layout(realizedExperimentalObject, testController, new BasicProgressMonitor());
            
            // Run blackbox tests and make sure that whitebox tests that wanted to run, but weren't, fail
            runBlackboxTests();
            failUnrunWhiteboxTests();
        }
        
        private void runBlackboxTests() {
            for (FrameworkMethod bbTest : parentRunner.getBlackboxTests()) {
                Description bbTestDescription = describeChild(bbTest);
                
                // Only run the test if it's not currently ignored
                if (isIgnored(bbTest)) {
                    notifier.fireTestIgnored(bbTestDescription);
                } else {
                    Statement bbTestStatement = new Statement() {
                        @Override
                        public void evaluate() throws Throwable {
                            Object test = createTest();
                            bbTest.invokeExplosively(test, realizedExperimentalObject);
                        }
                    };
                    
                    runLeaf(bbTestStatement, bbTestDescription, notifier);
                }
            }
        }
        
        private void failUnrunWhiteboxTests() {
            for (FrameworkMethod wbTest : parentRunner.getWhiteboxTests()) {
                FailIfNotExecuted failAnnotation = wbTest.getAnnotation(FailIfNotExecuted.class);
                if (failAnnotation != null && !executedWhiteboxTests.contains(wbTest)) {
                    Description wbTestDescription = describeChild(wbTest);
                    notifier.fireTestFailure(new Failure(wbTestDescription, new Exception("Test was not executed.")));
                }
            }
        }
        
    }

}
