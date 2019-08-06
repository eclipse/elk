/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.eclipse.elk.alg.test.framework.io.ResultsResourcePath;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import com.google.common.base.Strings;

//TODO copy from JUnit - License?
//parts of this class are based on JUnit (BlockJUnit4ClassRunner)

/**
 * This test runner runs the actual test methods. It mostly uses methods provided by {@link BlockJUnit4ClassRunner},
 * but some of them have to be adjusted for test methods with more than one parameter. It is used by the
 * {@link WhiteBoxRunner} and {@link BlackBoxRunner} test runners.
 * 
 * @see WhiteBoxRunner
 * @see BlackBoxRunner
 */
class ActualTestRunner extends BlockJUnit4ClassRunner {

    /** The normal number of parameters of a black box test. */
    public static final int BLACK_BOX_PARAMETER_COUNT = 1;
    /** The normal number of parameters of a white box test. */
    public static final int WHITE_BOX_PARAMETER_COUNT = 1;
    /** The number of parameters of a white box test if it has a layout processor as second parameter. */
    public static final int WHITE_BOX_PARAMETER_COUNT_WITH_PROCESSOR = 2;
    /** The normal number of parameters of an analysis test. */
    public static final int ANALYSIS_PARAMETER_COUNT = 4;

    /** The layout graph we're going to test with. */
    private ElkNode graph;
    /** The name of the graph. */
    private String graphName = "";
    /** The ID of the layout algorithm. */
    private String algorithmId = "";
    /** Whether to store a graph after a test failed. */
    private boolean storeGraphOnTestFailure;
    /** Specifies whether the test is a white box test. */
    private boolean isWhitebox = false;
    /** Specifies whether the test is an analysis test. */
    private boolean isAnalysis = false;

    // Variables that are important for white box tests
    
    /** The tested layout processor. */
    private ILayoutProcessor<?> processor;
    /** The name of the tested processor. */
    private String processorName = "";
    /** Whether the test should be executed before or after the processor. */
    private boolean runBefore = false;
    /** The layout graph for a white box test. */
    private Object whiteBoxGraph;
    /** The methods which should be executed. */
    private List<FrameworkMethod> methods;
    /** the descriptions of the test methods. */
    private final Map<FrameworkMethod, Description> methodDescriptions = new ConcurrentHashMap<>();
    /** Whether a test method failed. */
    private boolean failed = false;
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructor

    /**
     * Creates a new instance.
     * 
     * @param klass
     *            the test class.
     * @param graphName
     *            the name of the graph.
     * @param algorithmId
     *            the ID of the algorithm to be tested.
     * @param shouldStore
     *            whether a graph of a failed test should be stored.
     * @throws InitializationError
     *             if there was an error while initializing the runner.
     */
    ActualTestRunner(final Class<?> klass, final String graphName, final String algorithmId,
            final boolean shouldStore) throws InitializationError {
        
        super(klass);
        
        this.graphName = graphName;
        this.algorithmId = algorithmId;
        this.storeGraphOnTestFailure = shouldStore;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessor Methods
    
    /**
     * Sets the graph to run tests with.
     */
    public void setGraph(final ElkNode graph) {
        this.graph = graph;
    }
    
    /**
     * Returns the test graph's name.
     */
    public String getGraphName() {
        return graphName;
    }
    
    /**
     * Returns the ID of the algorithm we're going to test.
     */
    public String getAlgorithmId() {
        return algorithmId;
    }

    /**
     * Returns the processor before or after which tests should run.
     */
    public ILayoutProcessor<?> getProcessor() {
        return processor;
    }

    /**
     * Sets the processor before or after which tests should run.
     */
    public void setProcessor(final ILayoutProcessor<?> processor) {
        this.processor = processor;
    }

    /**
     * Sets the name of the processor to be tested.
     */
    public void setProcessorName(final String name) {
        processorName = name;
    }

    /**
     * Whether the test should be executed before the layout processor.
     */
    public boolean isRunBefore() {
        return runBefore;
    }

    /**
     * Sets whether the test should be executed before the layout processor.
     */
    public void setRunBefore(final boolean runBefore) {
        this.runBefore = runBefore;
    }

    /**
     * Sets the layout graph to be tested in a white box test.
     */
    public void setWhiteBoxGraph(final Object layoutGraph) {
        this.whiteBoxGraph = layoutGraph;
        isWhitebox = true;
    }
    
    /**
     * The list of the methods that should be executed.
     */
    public List<FrameworkMethod> getMethods() {
        return methods;
    }

    /**
     * Sets the list of the methods that should be executed.
     */
    public void setMethods(final List<FrameworkMethod> methods) {
        this.methods = methods;
    }
    
    /**
     * Returns whether the test has failed or not.
     */
    public boolean isFailed() {
        return failed;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Test Running
    
    @Override
    public void run(final RunNotifier notifier) {
        notifier.fireTestStarted(getDescription());
        super.run(notifier);
    }

    @Override
    protected void collectInitializationErrors(final List<Throwable> errors) {
        validateNoNonStaticInnerClass(errors);
        validateConstructor(errors);
        // validateInstanceMethods(errors);
        validateFields(errors);
        
        // TODO fix valiadateMethods for methods with parameter? already working?
        // validateMethods(errors);
    }

    @Override
    protected void validatePublicVoidNoArgMethods(final Class<? extends Annotation> annotation, final boolean isStatic,
            final List<Throwable> errors) {
        
        List<FrameworkMethod> frameworkMethods = getTestClass().getAnnotatedMethods(annotation);
        
        // Check whether the methods have at least one parameter. In the BlockJUnit4ClassRunner, the methods have to
        // have no parameters.
        for (FrameworkMethod eachTestMethod : frameworkMethods) {
            eachTestMethod.validatePublicVoid(isStatic, errors);
            
            // TODO based on org.junit.runners.model.FrameworkMethod
            if (eachTestMethod.getMethod().getParameterTypes().length == 0) {
                errors.add(new Exception(
                        "Method " + eachTestMethod.getMethod().getName() + " should have one parameter"));
            }
        }
    }

    @Override
    protected Statement methodInvoker(final FrameworkMethod method, final Object test) {
        //  The method is invoked with the graph as input; in the BlockJUnit4ClassRunner this is done without an input
        return new InvokeMethodWithGraph(method, test);
    }

    /**
     * Returns the description of the test class. A white box test includes the name of the tested phase.
     * 
     * @return the description
     */
    @Override
    public Description getDescription() {
        String lineSeparator = System.getProperty("line.separator");
        
        // Assemble a unique ID, which, in case of a white box test, includes the name of the tested processor
        String uniqueId = algorithmId + lineSeparator;
        if (!Strings.isNullOrEmpty(processorName)) {
            uniqueId += processorName + lineSeparator;
        }
        uniqueId += getName() + lineSeparator + graphName;
        
        // Create a proper description
        Description description;
        if (Strings.isNullOrEmpty(processorName)) {
            description = Description.createSuiteDescription(getName(), uniqueId, getRunnerAnnotations());
        } else {
            // Use just the actual phase name, not a completely qualified name
            String[] phaseNameParts = processorName.split(Pattern.quote("."));
            description = Description.createSuiteDescription(
                    getName() + ", phase: " + phaseNameParts[phaseNameParts.length - 1], uniqueId,
                    getRunnerAnnotations());
        }
        
        // Add the methods to be tested to the description
        for (FrameworkMethod child : getChildren()) {
            description.addChild(describeChild(child));
        }
        
        return description;
    }

    /**
     * Returns the description of the child method with all important information. As representation for the test class,
     * phase and algorithm not the complete name but just the last part of the name should be used. That means that for
     * example not org.eclipse.elk.layered but just layered should be used.
     * 
     * @param method
     *            the child.
     * @return the description of the child.
     */
    @Override
    protected Description describeChild(final FrameworkMethod method) {
        // Check the method description cache
        if (methodDescriptions.containsKey(method)) {
            return methodDescriptions.get(method);
        }
        
        String lineSeparator = System.getProperty("line.separator");
        
        // If we have a white box test, we will include more information in the description
        String whiteBoxDetails = "";
        if (!Strings.isNullOrEmpty(processorName)) {
            whiteBoxDetails = "phase: " + qualifiedToSimpleName(processorName);
            
            if (runBefore) {
                whiteBoxDetails += "(BEFORE)" + lineSeparator;
            } else {
                whiteBoxDetails += "(AFTER)" + lineSeparator;
            }
        }
        
        // Assemble the unique ID
        String uniqueId = lineSeparator
                + "test method: " + testName(method) + lineSeparator
                + "algorithm:" + qualifiedToSimpleName(algorithmId) + lineSeparator
                + whiteBoxDetails
                + "test class: " + qualifiedToSimpleName(getName()) + lineSeparator
                + "graph: " + graphName;
        
        // Create and cache the description
        Description description = Description.createTestDescription(
                getTestClass().getJavaClass(), uniqueId, method.getAnnotations());
        methodDescriptions.put(method, description);

        return description;
    }
    
    private String qualifiedToSimpleName(final String qualifiedName) {
        String[] parts = qualifiedName.split(Pattern.quote("."));
        return parts.length > 0
                ? parts[parts.length - 1]
                : "";
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utilities

    /**
     * Export the given layout graph in a file. This can be useful as debug output.
     * 
     * @param failedGraph
     *            the layout graph for which tests failed.
     */
    protected void storeResult(final ElkNode failedGraph) {
        ResultsResourcePath outputPath = new ResultsResourcePath(failedGraphPath(failedGraph));
        URI exportUri = URI.createFileURI(outputPath.getFile().getAbsolutePath());
        
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.createResource(exportUri);
        resource.getContents().add(failedGraph);
        
        try {
            resource.save(Collections.emptyMap());
        } catch (IOException e) {
            // ignore the exception
        }
    }
    
    private String failedGraphPath(final ElkNode failedGraph) {
        return "failed-graphs/" + algorithmId + "-" + Integer.toHexString(failedGraph.hashCode()) + ".elkg";
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Classes
    
    /**
     * This class is used to invoke the methods of the test class with the actual graph as input.
     */
    protected class InvokeMethodWithGraph extends Statement {
        private final FrameworkMethod testMethod;
        private final Object target;

        /**
         * The constructor of the class.
         * 
         * @param testMethod
         * @param target
         */
        protected InvokeMethodWithGraph(final FrameworkMethod testMethod, final Object target) {
            this.testMethod = testMethod;
            this.target = target;
        }

        /**
         * A white box tests is called with the lGraph and a black ox test is called with the ElkNode and a analysis
         * test with the needed Information for a analysis. The test methods have to be written always with the right
         * number and types of input parameters.
         */
        @Override
        public void evaluate() throws Throwable {
            if (isWhitebox) {
                if (methods.contains(testMethod)) {
                    // this will prevent that analysis methods in a white box test (a test with specified phases) will
                    // be called with the wrong parameters. A analysis method in a white box test will be ignored. A
                    // test class can be either a white box test or a black box test or an analysis test but never more
                    // than one of these three things.
                    
                    // a white box test with the graph as input
                    if (testMethod.getMethod().getParameterCount() == WHITE_BOX_PARAMETER_COUNT) {
                        Class<?>[] parameterTypes = testMethod.getMethod().getParameterTypes();
                        if (parameterTypes[0].isAssignableFrom(whiteBoxGraph.getClass())) {

                            failed = false;
                            try {
                                testMethod.invokeExplosively(target, parameterTypes[0].cast(whiteBoxGraph));
                            } catch (Throwable e) {
                                failed = true;
                                throw e;
                            }
                        }

                    }
                    
                    // a white box test with the graph and the layout processor as input
                    if (testMethod.getMethod().getParameterCount() == WHITE_BOX_PARAMETER_COUNT_WITH_PROCESSOR) {
                        Class<?>[] parameterTypes = testMethod.getMethod().getParameterTypes();
                        if (parameterTypes[0].isAssignableFrom(whiteBoxGraph.getClass())
                                && parameterTypes[1].equals(ILayoutProcessor.class)) {
                            failed = false;
                            try {
                                testMethod.invokeExplosively(target, parameterTypes[0].cast(whiteBoxGraph), processor);
                            } catch (Throwable e) {
                                failed = true;
                                throw e;
                            }
                        }
                    }
                }
            } else {
                /* a black box test */
                if (testMethod.getMethod().getParameterCount() == BLACK_BOX_PARAMETER_COUNT) {
                    Class<?>[] parameterTypes = testMethod.getMethod().getParameterTypes();
                    if (parameterTypes[0].equals(ElkNode.class)) {
                        failed = false;
                        try {
                            testMethod.invokeExplosively(target, graph);
                        } catch (Throwable e) {
                            failed = true;
                            if (storeGraphOnTestFailure) {
                                storeResult(graph);
                            }
                            throw e;
                        }
                    }
                }
            }
        }
    }
    
}
