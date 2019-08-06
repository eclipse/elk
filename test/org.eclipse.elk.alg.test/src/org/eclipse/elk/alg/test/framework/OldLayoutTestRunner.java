/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.elk.alg.test.PlainJavaInitialization;
import org.eclipse.elk.alg.test.framework.FailFakeRunner.FailReason;
import org.eclipse.elk.alg.test.framework.FailFakeRunner.NoExecutionFailure;
import org.eclipse.elk.alg.test.framework.annotations.Algorithm;
import org.eclipse.elk.alg.test.framework.annotations.Algorithms;
import org.eclipse.elk.alg.test.framework.annotations.TestAfterProcessor;
import org.eclipse.elk.alg.test.framework.annotations.TestAfterProcessors;
import org.eclipse.elk.alg.test.framework.annotations.TestBeforeProcessor;
import org.eclipse.elk.alg.test.framework.annotations.TestBeforeProcessors;
import org.eclipse.elk.alg.test.framework.io.ResultsResourcePath;
import org.eclipse.elk.core.util.Pair;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * The runner that manages the execution of all tests on the layout algorithms. This test runner has to be referenced in
 * the {@link RunWith} annotation and can be constructed with a configuration class or directly with a test class. If it
 * is started with a configuration class several test classes can be executed with the same layout test runner.
 */
public class OldLayoutTestRunner extends ParentRunner<Runner> {

    /**
     * List of all the test runners that have to be executed. There exists one runner for each algorithm in combination
     * with blackbox tests and one runner for each algorithm in combination with whitebox tests.
     */
    private List<Runner> children = new ArrayList<Runner>();
    /** The listener that collects the test results. */
    private LayoutRunListener listener;
    /** Whether to store the results. */
    private boolean shouldStoreResults = false;
    /** Whether to store the graphs of failed tests. */
    private boolean shouldStoreGraphs = false;

    /**
     * Creates a new instance for the given test class.
     * 
     * @param testClass
     *            a class that configures a test run and specifies the test classes which should be executed or a test
     *            class.
     * @throws InitializationError
     *             represents an error that occurred during initialization
     */
    public OldLayoutTestRunner(final Class<?> testClass) throws InitializationError {
        super(testClass);
        
        PlainJavaInitialization.initializePlainJavaLayout();
        
        loadSingleTestClass(testClass, null);
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Test Loading

    /**
     * Loads the given test class which, instead of referencing other test classes, contains tests itself. Creates an
     * appropriate test runner.
     */
    private void loadSingleTestClass(final Class<?> testClass, final List<String> allAlgorithmIds) {
        // Determine whether we're dealing with a black box or with a white box test
        List<Class<?>> blackBoxTestClasses = new ArrayList<Class<?>>();
        List<Class<?>> whiteBoxTestClasses = new ArrayList<Class<?>>();
        sortInWhiteAndBlack(Lists.newArrayList(testClass), whiteBoxTestClasses, blackBoxTestClasses);
        
        boolean isWhitebox = !whiteBoxTestClasses.isEmpty();
        
        // Find the algorithms the tests in the test class should test
        Set<String> algorithmIds = new TreeSet<>();
        
        // Single algorithm annotation
        Algorithm algorithm = testClass.getAnnotation(Algorithm.class);
        if (algorithm != null) {
            if (algorithm.value().equals("*")) {
                algorithmIds.addAll(allAlgorithmIds);
            } else {
                algorithmIds.add(algorithm.value());
            }
        }
        
        // Multiple algorithm annotations
        Algorithms algorithms = testClass.getAnnotation(Algorithms.class);
        if (algorithms != null) {
            for (Algorithm alg : algorithms.value()) {
                if (alg.value().equals("*")) {
                    algorithmIds.addAll(allAlgorithmIds);
                    break;
                } else {
                    algorithmIds.add(alg.value());
                }
            }
        }
        
        // If no algorithms were specified, add a dummy entry
        if (algorithmIds.isEmpty()) {
            algorithmIds.add("");
        }
        
        // Create test runners
        TestClass testClassWrapper = new TestClass(testClass);
        for (String alg : algorithmIds) {
            try {
                if (isWhitebox) {
                    children.add(new WhiteBoxRunner(alg, Lists.newArrayList(testClassWrapper)));
                } else {
                    children.add(new BlackBoxRunner(alg, Lists.newArrayList(testClassWrapper), shouldStoreGraphs));
                }
            } catch (InitializationError e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Builds a map that maps algorithm IDs to the list of test classes that refer to those algorithms.
     */
    private Multimap<String, Class<?>> buildTestsForAlgorithmsMap(final List<Class<?>> testClasses,
            final List<String> allAlgorithmIds) {

        Multimap<String, Class<?>> classesForAlgorithms = HashMultimap.create();

        for (Class<?> klass : testClasses) {
            // Single algorithm annotation
            Algorithm algorithm = klass.getAnnotation(Algorithm.class);
            if (algorithm != null) {
                if (algorithm.value().equals("*")) {
                    for (String id : allAlgorithmIds) {
                        classesForAlgorithms.put(id, klass);
                    }
                } else {
                    classesForAlgorithms.put(algorithm.value(), klass);
                }
            }
            
            // Multiple algorithm annotations
            Algorithms algorithms = klass.getAnnotation(Algorithms.class);
            if (algorithms != null) {
                for (Algorithm alg : algorithms.value()) {
                    if (alg.value().equals("*")) {
                        for (String id : allAlgorithmIds) {
                            classesForAlgorithms.put(id, klass);
                        }
                    } else {
                        classesForAlgorithms.put(alg.value(), klass);
                    }
                }
            }
            
            if (algorithm == null && algorithms == null) {
                // No algorithm specified, so add a dummy
                classesForAlgorithms.put("", klass);
            }
        }
        
        return classesForAlgorithms;
    }

    /**
     * Sorts the given collection of test classes into the appropriate of the two lists of white box and black box
     * test classes.
     */
    private void sortInWhiteAndBlack(final Collection<Class<?>> testClasses, final List<Class<?>> whiteTestClasses,
            final List<Class<?>> blackTestClasses) {
        
        for (Class<?> testClass : testClasses) {
            // Check if the class has an annotation typical of white box tests
            boolean isWhitebox = testClass.getAnnotation(TestBeforeProcessor.class) != null
                    || testClass.getAnnotation(TestBeforeProcessors.class) != null
                    || testClass.getAnnotation(TestAfterProcessor.class) != null
                    || testClass.getAnnotation(TestAfterProcessors.class) != null;
            
            // If we don't already know that it's a white box test, look through the methods for typical annotations
            if (!isWhitebox) {
                for (Method method : testClass.getDeclaredMethods()) {
                    isWhitebox = method.getAnnotation(TestBeforeProcessor.class) != null
                            || method.getAnnotation(TestBeforeProcessors.class) != null
                            || method.getAnnotation(TestAfterProcessor.class) != null
                            || method.getAnnotation(TestAfterProcessors.class) != null;
                    
                    if (isWhitebox) {
                        // We've already established that it's a white box test, so break out of it
                        break;
                    }
                }
            }
            
            // Add to the appropriate list
            if (isWhitebox) {
                whiteTestClasses.add(testClass);
            } else {
                blackTestClasses.add(testClass);
            }
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Result Output

    /**
     * This method groups the results, prints them to the console, and optionally stores them in a file.
     */
    private void groupPrintStoreResults(final List<LayoutTestResult> results) {
        // TODO This method deserves a clean up
        
        // Special failures
        boolean randomInputFailure = false;
        boolean noExecution = false;
        
        // The results in lines ready for output
        List<String> lines = new ArrayList<>();
        List<String> failedLines = new ArrayList<>();

        while (!results.isEmpty()) {
            LayoutTestResult currResult = results.get(0);
            
            List<String> succeededGraphs = new ArrayList<>();
            List<Pair<String, Pair<List<Failure>, List<Failure>>>> failedGraphs = new ArrayList<>();
            
            // Look for special failures
            while (!results.isEmpty() && (currResult.getAlgorithm().equals("RANDOM")
                    || currResult.getAlgorithm().equals("NOEXECUTION"))) {
                
                if (currResult.getAlgorithm().equals("RANDOM")) {
                    randomInputFailure = true;
                } else if (currResult.getAlgorithm().equals("NOEXECUTION")) {
                    noExecution = true;
                }

                results.remove(0);
                if (!results.isEmpty()) {
                    currResult = results.get(0);
                }
            }
            
            // Look for other tests with the same algorithm, test class, test method and phase
            if (!results.isEmpty()) {
                results.remove(0);
                
                if (!currResult.getFailures().isEmpty()
                        || !currResult.getAssumptionFailures().isEmpty()) {
                    
                    failedGraphs.add(Pair.of(currResult.getGraphName(),
                            Pair.of(currResult.getFailures(), currResult.getAssumptionFailures())));
                } else {
                    succeededGraphs.add(currResult.getGraphName());
                }
                
                Iterator<LayoutTestResult> resultsIterator = results.iterator();
                while (resultsIterator.hasNext()) {
                    LayoutTestResult res = resultsIterator.next();
                    if (res.getAlgorithm().equals("RANDOM")) {
                        randomInputFailure = true;
                    } else if (res.getAlgorithm().equals("NOEXECUTION")) {
                        noExecution = true;
                    } else {
                        if (res.isBlackBox() == currResult.isBlackBox()
                                && res.getAlgorithm().contentEquals(currResult.getAlgorithm())
                                && res.getMethodName().equals(currResult.getMethodName())
                                && res.getProcessorName().equals(currResult.getProcessorName())
                                && res.getClassName().equals(currResult.getClassName())) {
                            
                            resultsIterator.remove();
                            if ((!res.getFailures().isEmpty()) || (!res.getAssumptionFailures().isEmpty())) {
                                // the graph has to be added to the failed graphs
                                int index = -1;
                                for (Pair<String, Pair<List<Failure>, List<Failure>>> pair : failedGraphs) {
                                    if (pair.getFirst().equals(res.getGraphName())) {
                                        index = failedGraphs.indexOf(pair);
                                        break;
                                    }
                                }
                                // the failed graph is not yet in th eList of failed graphs
                                if (index == -1) {
                                    failedGraphs.add(Pair.of(res.getGraphName(),
                                            Pair.of(res.getFailures(), res.getAssumptionFailures())));
                                } else {
                                    // the graph is already in the list and the failure has to be added, if it is not
                                    // yet present
                                    boolean contained = false;
                                    for (Failure failure : res.getFailures()) {
                                        for (Failure failure2 : failedGraphs.get(index).getSecond().getFirst()) {
                                            if (failure.getMessage() != null) {
                                                if (failure.getMessage().equals(failure2.getMessage())) {
                                                    contained = true;
                                                }
                                            } else {
                                                contained = true;
                                            }
                                        }
                                        if (!contained) {
                                            failedGraphs.get(index).getSecond().getFirst().add(failure);
                                        }
                                    }
                                    contained = false;
                                    for (Failure failure : res.getAssumptionFailures()) {
                                        for (Failure failure2 : failedGraphs.get(index).getSecond().getFirst()) {
                                            if (failure.getMessage() != null) {
                                                if (failure.getMessage().equals(failure2.getMessage())) {
                                                    contained = true;
                                                }
                                            } else {
                                                contained = true;
                                            }
                                        }
                                        if (!contained) {
                                            failedGraphs.get(index).getSecond().getSecond().add(failure);
                                        }
                                    }
                                }
                            } else {
                                if (!succeededGraphs.contains(res.getGraphName())) {
                                    succeededGraphs.add(res.getGraphName());
                                }
                            }
                        }
                    }
                }
                // add the grouped results to the output
                List<String> actLines;
                if (failedGraphs.isEmpty()) {
                    actLines = lines;
                } else {
                    actLines = failedLines;
                }

                actLines.add(currResult.getAlgorithm());
                if (!currResult.isBlackBox()) {
                    actLines.add(currResult.getProcessorName());
                }
                actLines.add(currResult.getMethodName());
                if (!failedGraphs.isEmpty()) {
                    actLines.add("FAILED on graphs: ");
                    for (Pair<String, Pair<List<Failure>, List<Failure>>> pair : failedGraphs) {
                        actLines.add("  " + pair.getFirst());
                        for (Failure failure : pair.getSecond().getFirst()) {
                            actLines.add("    " + failure.getMessage());
                        }
                        for (Failure failure : pair.getSecond().getSecond()) {
                            actLines.add("    " + failure.getMessage());
                        }
                    }
                }
                
                if (!succeededGraphs.isEmpty()) {
                    actLines.add("SUCCEEDED on graphs: ");
                    for (String string : succeededGraphs) {
                        actLines.add("    " + string);
                    }
                }
                actLines.add("");
            }
        }
        
        // add the special failures to he output
        if (randomInputFailure) {
            failedLines.add("FAILURE: An analysis test hast to have no random graphs as input.");
        }
        
        if (noExecution) {
            failedLines.add("FAILURE: A white box test has not been executed.");
        }
        
        printAndStoreResults(lines, failedLines);
    }

    /**
     * This method prints the results on the console and stores them, if wanted.
     */
    public void printAndStoreResults(final List<String> lines, final List<String> failedLines) {
        // Print the results to the console
        for (String string : lines) {
            System.out.println(string);
        }
        for (String string : failedLines) {
            System.out.println(string);
        }

        // Possibly store the results in a file
        if (shouldStoreResults) {
            List<String> allLines = failedLines;
            allLines.addAll(lines);
            
            ResultsResourcePath resultResource = new ResultsResourcePath("results.txt");
            
            try {
                ResultsResourcePath.ensureResultsPathExists();
                Files.write(
                        Paths.get(resultResource.getFile().getAbsolutePath()),
                        allLines,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Test Runner Methods

    @Override
    public void run(final RunNotifier notifier) {
        // add the listener for to the notifier that collects the layout test results
        List<LayoutTestResult> results = new ArrayList<>();
        listener = new LayoutRunListener(results);
        notifier.addListener(listener);
        notifier.fireTestRunStarted(getDescription());

        // run the Tests
        super.run(notifier);

        // print how often the white box tests were executed
        List<WhiteBoxTestDescription> whiteBoxchildren = listener.getAtomicWhiteBoxChildren();
        for (WhiteBoxTestDescription desc : whiteBoxchildren) {
            System.out.println(
                    desc.getNameWithoutGraph() + " has been executed " + desc.getActualExecutions() + " times.");
            
            if (desc.isFailIfNotExecuted() && desc.getExpectedExecutions() > 0 && desc.getActualExecutions() == 0) {
                BlockJUnit4ClassRunner runner = null;
                try {
                    runner = new FailFakeRunner(NoExecutionFailure.class, desc.getNameWithoutGraph(), " ", " ",
                            FailReason.UNUSED_WHITE_BOX_TEXT);
                } catch (InitializationError e) {
                    e.printStackTrace();
                }
                
                if (runner != null) {
                    runner.run(notifier);
                }
            }
        }

        // print the results and write them back
        if (!results.isEmpty()) {
            groupPrintStoreResults(results);
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return children;
    }

    @Override
    protected Description describeChild(final Runner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(final Runner child, final RunNotifier notifier) {
        child.run(notifier);
    }

}
