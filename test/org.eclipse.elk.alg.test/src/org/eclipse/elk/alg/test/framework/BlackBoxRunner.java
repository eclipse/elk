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

import org.eclipse.elk.alg.test.framework.annotations.Analysis;
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
public class BlackBoxRunner extends SomeBoxRunner {

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
    public BlackBoxRunner(final String algorithmId, final List<TestClass> testClasses,
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
        boolean analysisFailed = false;
        
        for (TestMapping testMapping : getTestMappings()) {
            // TODO @BeforeLayout
            
            // Execute layout
            IElkProgressMonitor progressMonitor = new BasicProgressMonitor();
            layoutEngine.layout(testMapping.getGraph(), progressMonitor);

            // Execute the tests
            for (TestClass testClass : testMapping.getTestClasses()) {
                ActualTestRunner runner = testMapping.getRunnerForClass(testClass);
                runner.setGraph(testMapping.getGraph());
                
                if (testClass.getJavaClass().isAnnotationPresent(Analysis.class)) {
                    // TODO Analysis tests
//                    analysisFailed = analysisFailed || runAnalysis(runner, testClass, testMapping, notifier);
                } else {
                    runner.run(notifier);
                }
            }
            
            // TODO @AfterLayout
        }
        
        // at this point all the tests have their results written in temp files and executed without failures. Now the
        // temporary results have to be written back to the original files.
        // TODO should just be called if !analysisFailed and without analysisFailed as parameter
        for (TestMapping testMapping : getTestMappings()) {
            for (TestClass testClass : testMapping.getTestClasses()) {
                if (testClass.getJavaClass().isAnnotationPresent(Analysis.class)) {
//                    storeAnalysisResults(testClass, testMapping, analysisFailed, notifier);
                }
            }
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Analysis Execution

//    /**
//     * The analysis test is executed with a temporary results file and it is checked, whether the test failed.
//     * 
//     * @param runner
//     *            the test runner
//     * @param testClass
//     *            the test class
//     * @param testMapping
//     *            the test mapping containing the test input
//     * @param notifier
//     *            the notifier the test runner has to be executed with
//     * @return Whether the analysis test failed.
//     */
//    private boolean runAnalysis(final ActualTestRunner runner, final TestClass testClass, final TestMapping testMapping,
//            final RunNotifier notifier) {
//
//        boolean failed = false;
//        boolean actFailed = false;
//
//        List<AnalysisConfiguration> analysisConfigs = new ArrayList<>();
//        analysisConfigs.clear();
//        List<Object> objList = TestUtil.loadAnnotMethod(testClass, AnalysisConfig.class);
//        for (Object o : objList) {
//            if (o instanceof AnalysisConfiguration) {
//                analysisConfigs.add((AnalysisConfiguration) o);
//            }
//        }
//        String graphName = testMapping.getGraphName();
//        // A graph given to an analysis needs to have a name, otherwise it is
//        // not possible to store and compare the results
//        if (graphName != "" && !testMapping.isRandom()) {
//            for (AnalysisConfiguration config : analysisConfigs) {
//                // erzeuge tmp files als kopie von alten, lasse test darauf
//                // laufen und dahin zurück schreiben
//                Path oldResultsPath = Paths.get(config.getOldResultsFile().getPath());
//                Path tempResultsFile = Paths.get(
//                        oldResultsPath.toString().substring(0, (oldResultsPath.toString().length() - ".txt".length()))
//                                + "tmp" + ".txt");
//                if (Files.exists(oldResultsPath, LinkOption.NOFOLLOW_LINKS)
//                        && !Files.exists(tempResultsFile, LinkOption.NOFOLLOW_LINKS)) {
//                    try {
//                        Files.copy(oldResultsPath, tempResultsFile);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (!Files.exists(tempResultsFile, LinkOption.NOFOLLOW_LINKS)) {
//                    try {
//                        Files.createFile(tempResultsFile);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                config.setOldResultsFile(new ResultsResourcePath(config.getOldResultsFile().getFilePath().substring(0,
//                        (config.getOldResultsFile().getFilePath().length() - ".txt".length())) + "tmp" + ".txt"));
//                runner.setAnalysisVariables(graphName, algorithm, config, actFailed);
//                runner.run(notifier);
//                failed = failed || runner.isFailed();
//            }
//        }
//
//        return failed;
//
//    }
//
//    /**
//     * Here the temporary stored results are written back to the original file. The temporary files are deleted
//     * afterwards.
//     * 
//     * @param testClass
//     *            the actual test class
//     * @param testMapping
//     *            the actual test mapping
//     */
//    private void storeAnalysisResults(final TestClass testClass, final TestMapping testMapping, final boolean failed,
//            final RunNotifier notifier) {
//        List<AnalysisConfiguration> analysisConfigs = new ArrayList<>();
//        List<Object> objList = TestUtil.loadAnnotMethod(testClass, AnalysisConfig.class);
//        for (Object o : objList) {
//            if (o instanceof AnalysisConfiguration) {
//                analysisConfigs.add((AnalysisConfiguration) o);
//            }
//        }
//        String graphName = testMapping.getGraphName();
//
//        // An analysis is not sensible for random graphs.
//        // Therefore a runner is called on a class with a test that will fail
//        // and this will cause a JUnit test failure
//        if (testMapping.isRandom()) {
//            BlockJUnit4ClassRunner runner = null;
//            try {
//                runner = new FailFakeRunner(RandomInputFailure.class, algorithm, testClass.getJavaClass().getName(),
//                        graphName, true);
//            } catch (InitializationError e) {
//                e.printStackTrace();
//            }
//            if (runner != null) {
//                runner.run(notifier);
//            }
//        } else if (graphName != "") {
//            for (AnalysisConfiguration config : analysisConfigs) {
//                Path oldResultsFile = Paths.get(config.getOldResultsFile().getPath());
//                Path tempResultsFile = Paths.get(
//                        oldResultsFile.toString().substring(0, (oldResultsFile.toString().length() - ".tmp".length()))
//                                + "tmp" + ".txt");
//                // check whether the temporary results file still exists. If
//                // several analysis use the same result file the temporary file
//                // may be written back and deleted before.
//                if (Files.exists(tempResultsFile, LinkOption.NOFOLLOW_LINKS)) {
//                    if (!failed) {
//                        if (Files.exists(oldResultsFile, LinkOption.NOFOLLOW_LINKS)) {
//                            try {
//                                Files.delete(oldResultsFile);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        // copy the temporary result file to the original file
//                        try {
//                            Files.copy(tempResultsFile, oldResultsFile);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    // delete the temporary file
//                    try {
//                        Files.delete(tempResultsFile);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

}
