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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.elk.alg.test.framework.annotations.TestAfterProcessor;
import org.eclipse.elk.alg.test.framework.annotations.TestAfterProcessors;
import org.eclipse.elk.alg.test.framework.annotations.TestBeforeProcessor;
import org.eclipse.elk.alg.test.framework.annotations.TestBeforeProcessors;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.testing.TestController;
import org.eclipse.elk.core.testing.TestController.ILayoutPostProcessorListener;
import org.eclipse.elk.core.testing.TestController.ILayoutPreProcessorListener;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import com.google.common.collect.Lists;

/**
 * This runner is used to execute white box tests.
 */
class WhiteBoxRunner extends SomeBoxRunner {
    
    /** Each of the test runs holds the information for one execution of the layout algorithm. */
    private List<WhiteBoxTestRun> testRuns = new ArrayList<>();
    /** The description of the test runner. */
    private Description description;
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Initialization

    /**
     * Creates a new instance initialized from the test classes. The graphs are loaded, the runners are built, and the
     * description is created.
     * 
     * @param algorithm
     *            The layout algorithm that should be tested
     * @param testClasses
     *            The test classes with tests for the algorithm
     * @throws InitializationError
     *             An error occurred while initializing the runner
     */
    WhiteBoxRunner(final String algorithm, final List<TestClass> testClasses) throws InitializationError {
        super(algorithm, testClasses, false);
        
        List<Annotation> annotations = new ArrayList<Annotation>();
        for (TestClass testClass : testClasses) {
            annotations.addAll(Arrays.asList(testClass.getAnnotations()));
        }
        
        // As representation for algorithm not the complete name but just the last part of the name should be used.
        String[] algorithmParts = algorithm.split(Pattern.quote("."));
        description = Description.createSuiteDescription(
                "White box test runner for " + algorithmParts[algorithmParts.length - 1], annotations.toArray());
        
        // Initialize everything
        buildBreakpoints();
        buildRunners();
        buildDescription();
    }

    /**
     * Creates all the breakpoint and adds them to the breakpoints list.
     */
    private void buildBreakpoints() {
        for (TestMapping testMapping : getTestMappings()) {
            // Create a test run
            WhiteBoxTestRun testRun = new WhiteBoxTestRun(testMapping);
            testRuns.add(testRun);
            
            for (TestClass testClass : testMapping.getTestClasses()) {
                // Evaluates the class's processor annotations and adds breakpoints to the test run accordingly
                evaluateClassAnnotations(testClass, testRun);
                
                // Evaluates methods that have processor annotations themselves and adds breakpoints
                evaluateMethodAnnotations(testClass, testRun);
            }
        }
    }

    /**
     * Builds a runner for each combination of {@link ILayoutProcessor} and related test method. After that the runner
     * is accessible in breakpoints.
     */
    private void buildRunners() {
        for (WhiteBoxTestRun testRun : testRuns) {
            TestMapping testMapping = testRun.getTestMapping();
            List<TestClass> testClasses = testMapping.getTestClasses();

            buildRunners(testRun, testClasses, testRun.getBeforeRootProc());
            buildRunners(testRun, testClasses, testRun.getBeforeProc());
            buildRunners(testRun, testClasses, testRun.getAfterProc());
            buildRunners(testRun, testClasses, testRun.getAfterRootProc());
        }
    }

    private void buildRunners(final WhiteBoxTestRun testRun, final List<TestClass> testClasses,
            final List<WhiteBoxTestBreakpoint> breakpoints) {
        
        for (WhiteBoxTestBreakpoint breakpoint : breakpoints) {
            for (TestClass testClass : testClasses) {
                ActualTestRunner actRunner = testRun.getTestMapping().getRunnerForClass(testClass);
                try {
                    ActualTestRunner newRunner = new ActualTestRunner(breakpoint.getTestClass().getJavaClass(),
                            actRunner.getGraphName(), actRunner.getAlgorithmId(), false);

                    newRunner.setProcessorName(breakpoint.getProcessor().getName());
                    newRunner.setMethods(breakpoint.getMethods());
                    newRunner.setRunBefore(false);
                    
                    breakpoint.setRunner(newRunner);
                } catch (InitializationError e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Builds the description for the test runner.
     */
    private void buildDescription() {
        for (WhiteBoxTestRun tr : testRuns) {
            // We basically take all of the actual test runners and add their descriptions as children of this one
            Stream.of(tr.getBeforeRootProc(), tr.getBeforeProc(), tr.getAfterProc(), tr.getAfterRootProc())
                .flatMap(list -> list.stream())
                .forEach(breakPoint -> description.addChild(breakPoint.getRunner().getDescription()));
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Test Run Setup

    /**
     * Returns the subset of test methods that don't have specific processor annotations. ({@link TestBeforeProcessor},
     * {@link TestBeforeProcessors}, {@link TestAfterProcessor}, or {@link TestAfterProcessors}).
     */
    private List<FrameworkMethod> getProcessorIndependentMethods(final TestClass testClass) {
        // Look through all test methods and throw out those that have RunBefore* or RunAfter* annotations
        List<FrameworkMethod> testMethods = testClass.getAnnotatedMethods(Test.class);
        List<FrameworkMethod> result = Lists.newArrayListWithCapacity(testMethods.size());
        
        for (FrameworkMethod method : testMethods) {
            boolean processorDependent = Arrays.stream(method.getAnnotations())
                .anyMatch(ann -> ann instanceof TestBeforeProcessor
                        || ann instanceof TestBeforeProcessors
                        || ann instanceof TestAfterProcessor
                        || ann instanceof TestAfterProcessors);
            
            if (!processorDependent) {
                result.add(method);
            }
        }
        
        return testMethods;
    }

    /**
     * Checks the class for processor annotations and creates breakpoints for all those test methods that don't
     * themselves specify processors to run before or after.
     */
    private void evaluateClassAnnotations(final TestClass testClass, final WhiteBoxTestRun testRun) {
        List<FrameworkMethod> processorIndependentTests = getProcessorIndependentMethods(testClass);
        
        TestBeforeProcessor beforeProcessor = testClass.getAnnotation(TestBeforeProcessor.class);
        if (beforeProcessor != null) {
            WhiteBoxTestBreakpoint breakpoint = new WhiteBoxTestBreakpoint(
                    beforeProcessor.processor(), testClass, processorIndependentTests);
            
            if (beforeProcessor.onRootOnly()) {
                testRun.getBeforeRootProc().add(breakpoint);
            } else {
                testRun.getBeforeProc().add(breakpoint);
            }
        }
        
        TestBeforeProcessors beforeProcessors = testClass.getAnnotation(TestBeforeProcessors.class);
        if (beforeProcessors != null) {
            TestBeforeProcessor[] procs = beforeProcessors.value();
            for (TestBeforeProcessor runBeforeProcessor : procs) {
                WhiteBoxTestBreakpoint breakpoint = new WhiteBoxTestBreakpoint(
                        runBeforeProcessor.processor(), testClass, processorIndependentTests);
                
                if (runBeforeProcessor.onRootOnly()) {
                    testRun.getBeforeRootProc().add(breakpoint);
                } else {
                    testRun.getBeforeProc().add(breakpoint);
                }
            }
        }
        
        TestAfterProcessor afterProcessor = testClass.getAnnotation(TestAfterProcessor.class);
        if (afterProcessor != null) {
            WhiteBoxTestBreakpoint breakpoint = new WhiteBoxTestBreakpoint(
                    afterProcessor.processor(), testClass, processorIndependentTests);
            
            if (afterProcessor.onRootOnly()) {
                testRun.getAfterRootProc().add(breakpoint);
            } else {
                testRun.getAfterProc().add(breakpoint);
            }
        }
        
        TestAfterProcessors afterProcessors = testClass.getAnnotation(TestAfterProcessors.class);
        if (afterProcessors != null) {
            TestAfterProcessor[] procs = afterProcessors.value();
            for (TestAfterProcessor runAfterProcessor : procs) {
                WhiteBoxTestBreakpoint breakpoint = new WhiteBoxTestBreakpoint(
                        runAfterProcessor.processor(), testClass, processorIndependentTests);
                
                if (runAfterProcessor.onRootOnly()) {
                    testRun.getAfterRootProc().add(breakpoint);
                } else {
                    testRun.getAfterProc().add(breakpoint);
                }
            }
        }
    }

    /**
     * Evaluates the processor annotations of test methods and creates appropriate break points.
     */
    private void evaluateMethodAnnotations(final TestClass testClass, final WhiteBoxTestRun testRun) {
        for (FrameworkMethod frameworkMethod : testClass.getAnnotatedMethods(Test.class)) {
            TestBeforeProcessor beforeProcessor = frameworkMethod.getAnnotation(TestBeforeProcessor.class);
            if (beforeProcessor != null) {
                updateOrCreateBreakpoint(
                        beforeProcessor.onRootOnly() ? testRun.getBeforeRootProc() : testRun.getBeforeProc(),
                        beforeProcessor.processor(),
                        testClass,
                        frameworkMethod);
            }

            TestBeforeProcessors beforeProcessors = frameworkMethod.getAnnotation(TestBeforeProcessors.class);
            if (beforeProcessors != null) {
                for (TestBeforeProcessor processor : beforeProcessors.value()) {
                    updateOrCreateBreakpoint(
                            processor.onRootOnly() ? testRun.getBeforeRootProc() : testRun.getBeforeProc(),
                            processor.processor(),
                            testClass,
                            frameworkMethod);
                }
            }
            
            TestAfterProcessor afterProcessor = frameworkMethod.getAnnotation(TestAfterProcessor.class);
            if (afterProcessor != null) {
                updateOrCreateBreakpoint(
                        afterProcessor.onRootOnly() ? testRun.getAfterRootProc() : testRun.getAfterProc(),
                        afterProcessor.processor(),
                        testClass,
                        frameworkMethod);
            }

            TestAfterProcessors afterProcessors = frameworkMethod.getAnnotation(TestAfterProcessors.class);
            if (afterProcessors != null) {
                for (TestAfterProcessor processor : afterProcessors.value()) {
                    updateOrCreateBreakpoint(
                            processor.onRootOnly() ? testRun.getAfterRootProc() : testRun.getAfterProc(),
                            processor.processor(),
                            testClass,
                            frameworkMethod);
                }
            }
        }
    }
    
    
    /**
     * If there already is a breakpoint for the given processor in the given list of break points, the method is added
     * to that breakpoint. Otherwise, a new breakpoint is created and added to the list.
     */
    private void updateOrCreateBreakpoint(final List<WhiteBoxTestBreakpoint> breakpoints,
            final Class<? extends ILayoutProcessor<?>> processor, final TestClass testClass,
            final FrameworkMethod method) {
        
        for (WhiteBoxTestBreakpoint breakpoint : breakpoints) {
            if (breakpoint.getProcessor().equals(processor)) {
                breakpoint.getMethods().add(method);
                return;
            }
        }
        
        // We haven't found a breakpoint, so create one
        WhiteBoxTestBreakpoint breakpoint = new WhiteBoxTestBreakpoint(
                processor, testClass, Lists.newArrayList(method));
        breakpoints.add(breakpoint);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Test Runner

    /**
     * This method creates a test controller, adds the listeners to it and starts the layout of the graph. The tests
     * are triggered by the listener and the listener is notified by the layout algorithm.
     */
    @Override
    public void run(final RunNotifier notifier) {
        notifier.fireTestRunStarted(getDescription());
        
        for (WhiteBoxTestRun testRun : testRuns) {
            TestMapping testMapping = testRun.getTestMapping();

            TestController testController = new TestController(getAlgorithmId());
            
            testRun.getBeforeRootProc().stream()
                .forEach(bp -> registerListener(bp, testController, notifier, true));
            testRun.getBeforeProc().stream()
                .forEach(bp -> registerListener(bp, testController, notifier, true));
            testRun.getAfterProc().stream()
                .forEach(bp -> registerListener(bp, testController, notifier, false));
            testRun.getAfterProc().stream()
                .forEach(bp -> registerListener(bp, testController, notifier, false));

            // Let the RecursiveGraphLayoutEngine layout the graph
            RecursiveGraphLayoutEngine engine = new RecursiveGraphLayoutEngine();
            try {
                engine.layout(testMapping.getGraph(), testController, new BasicProgressMonitor());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void registerListener(final WhiteBoxTestBreakpoint breakpoint, final TestController testController,
            final RunNotifier notifier, final boolean before) {
        
        if (before) {
            ILayoutPreProcessorListener listener = new LayoutPreProcessorListener(breakpoint.getRunner(), notifier);
            testController.addPreProcessorRunListener(listener, breakpoint.getProcessor(), false);
        } else {
            ILayoutPostProcessorListener listener = new LayoutPostProcessorListener(breakpoint.getRunner(), notifier);
            testController.addPostProcessorRunListener(listener, breakpoint.getProcessor(), false);
        }
    }

    /**
     * Provides a description with the test classes as children.
     * 
     * @return The description of the test runner.
     */
    @Override
    public Description getDescription() {
        return description;
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Inner Classes

    /**
     * Listener waiting for a processor to become ready for execution.
     */
    private final class LayoutPreProcessorListener implements ILayoutPreProcessorListener {

        /** The runner that has to be used to execute the tests. */
        private ActualTestRunner runner;
        /** The notifier that has to be notified by the test runner. */
        private RunNotifier notifier;

        /**
         * Creates a new instance for the given runner and notifier.
         */
        private LayoutPreProcessorListener(final ActualTestRunner runner, final RunNotifier notifier) {
            this.runner = runner;
            this.notifier = notifier;
        }

        @Override
        public void layoutProcessorReady(final Object graph, final ILayoutProcessor<?> processor) {
            System.out.print("Run Test methods ");
            System.out.print(runner.getMethods().stream()
                .map(method -> method.getName())
                .collect(Collectors.joining(", ")));
            System.out.println(" before the processor " + processor.getClass().getName() + " executes.");
            
            runner.setWhiteBoxGraph(graph);
            runner.setProcessor(processor);
            runner.setProcessorName(processor.getClass().getName());
            runner.run(notifier);
            
            // update description
            Description childDescription = runner.getDescription();
            description.addChild(childDescription);
        }
    }

    /**
     * Listener waiting for a processor to finish execution.
     */
    private final class LayoutPostProcessorListener implements ILayoutPostProcessorListener {

        /** The runner that has to be used to execute the tests. */
        private ActualTestRunner runner;
        /** The notifier that has to be notified by the test runner. */
        private RunNotifier notifier;

        /**
         * Creates a new instance for the given runner and notifier.
         */
        private LayoutPostProcessorListener(final ActualTestRunner runner, final RunNotifier notifier) {
            this.runner = runner;
            this.notifier = notifier;
        }

        @Override
        public void layoutProcessorFinished(final Object graph, final ILayoutProcessor<?> processor) {
            System.out.print("Run Test methods ");
            System.out.print(runner.getMethods().stream()
                .map(method -> method.getName())
                .collect(Collectors.joining(", ")));
            System.out.println(" after the processor " + processor.getClass().getName() + " executes.");
            
            runner.setWhiteBoxGraph(graph);
            runner.setProcessor(processor);
            runner.setProcessorName(processor.getClass().getName());
            runner.run(notifier);
            
            // update description
            Description childDescription = runner.getDescription();
            description.addChild(childDescription);
        }

    }

}
