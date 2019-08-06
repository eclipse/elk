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
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.testing.TestController;
import org.eclipse.elk.core.testing.TestController.ILayoutExecutionListener;
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
    private final class RunExperimentStatement extends Statement implements ILayoutExecutionListener {

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
                testController.addLayoutExecutionListener(this);
            }

            // Let the recursive graph layout engine run the layout algorithm (if we have a test controller, that will
            // automatically cause white box tests to run)
            RecursiveGraphLayoutEngine layoutEngine = new RecursiveGraphLayoutEngine();
            layoutEngine.layout(realizedExperimentalObject, testController, new BasicProgressMonitor());
            
            // Stop listening to the test controller
            if (testController != null) {
                testController.removeLayoutExecutionListener(this);
            }

            // Run blackbox tests now that the layout algorithm has finished
            runBlackboxTests();

            // Cleanly handle all whitebox tests that did not execute because their processors didn't run
            handleUnexecutedWhiteboxTests();
        }

        /**
         * Runs all blackbox tests after the layout algorithm has finished. By this time, all whitebox tests will have
         * finished.
         */
        private void runBlackboxTests() {
            for (FrameworkMethod bbTest : parentRunner.getBlackboxTests()) {
                runTest(bbTest, realizedExperimentalObject);
            }
        }

        private void runTest(final FrameworkMethod test, final Object graph) {
            Description testDescription = describeChild(test);

            // Only run the test if it's not currently ignored
            if (isIgnored(test)) {
                notifier.fireTestIgnored(testDescription);
            } else {
                Statement bbTestStatement = new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        Object testInstance = createTest();
                        test.invokeExplosively(testInstance, graph);
                    }
                };

                runLeaf(bbTestStatement, testDescription, notifier);
            }
        }

        /**
         * Simulates successful or failing test runs for all whitebox tests that did not execute.
         */
        private void handleUnexecutedWhiteboxTests() {
            for (FrameworkMethod wbTest : parentRunner.getWhiteboxTests()) {
                if (!executedWhiteboxTests.contains(wbTest)) {
                    // This whitebox test was not executed, so we'll simulate a test run for the notifier
                    Description wbTestDescription = describeChild(wbTest);
                    notifier.fireTestStarted(wbTestDescription);

                    // The test must fail if it was supposed to be executed
                    if (wbTest.getAnnotation(FailIfNotExecuted.class) != null) {
                        notifier.fireTestFailure(new Failure(wbTestDescription,
                                new Exception("Test was not executed, but was supposed to.")));
                    }

                    notifier.fireTestFinished(wbTestDescription);
                }
            }
        }
        

        @Override
        public void layoutProcessorReady(final ILayoutProcessor<?> processor, final Object graph,
                final boolean isRoot) {
            
            // Retrieve all whitebox test that want to run before this processor
            for (FrameworkMethod wbTest : parentRunner.getWhiteboxBeforeTests().get((Class<? extends ILayoutProcessor<?>>) processor.getClass())) {
                // Only run the test if this is either the root graph (all whitebox tests want to be run on the root
                // graph) or if the test is not restricted to run only on the root
                if (isRoot || !parentRunner.getWhiteboxOnlyOnRoot().contains(wbTest)) {
                    runTest(wbTest, graph);
                    executedWhiteboxTests.add(wbTest);
                }
            }
        }

        @Override
        public void layoutProcessorFinished(final ILayoutProcessor<?> processor, final Object graph,
                final boolean isRoot) {
            
            // Retrieve all whitebox test that want to run after this processor
            for (FrameworkMethod wbTest : parentRunner.getWhiteboxAfterTests().get((Class<? extends ILayoutProcessor<?>>) processor.getClass())) {
                // Only run the test if this is either the root graph (all whitebox tests want to be run on the root
                // graph) or if the test is not restricted to run only on the root
                if (isRoot || !parentRunner.getWhiteboxOnlyOnRoot().contains(wbTest)) {
                    runTest(wbTest, graph);
                    executedWhiteboxTests.add(wbTest);
                }
            }
        }

    }

}
