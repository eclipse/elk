/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.alg.test.framework.annotations.FailIfNotExecuted;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * This listener collects the results of the layout tests.
 */
class LayoutRunListener extends RunListener {

    /** The index of the algorithm in the description of a white or a black box test. */
    private static final int ALGORITHM_INDEX = 1;

    /** The index of the class name in the description of a black box test. */
    private static final int BLACK_CLASS_NAME_INDEX = 2;
    /** The index of the graph name in the description of a black box test. */
    private static final int BLACK_GRAPH_NAME_INDEX = 3;
    /** The index of the method name in the description of a black box test. */
    private static final int BLACK_METHOD_NAME_INDEX = 4;

    /** The index of the phase name in the description of a white box test. */
    private static final int WHITE_PHASE_NAME_INDEX = 2;
    /** The index of the class name in the description of a white box test. */
    private static final int WHITE_CLASS_NAME_INDEX = 3;
    /** The index of the graph name in the description of a white box test. */
    private static final int WHITE_GRAPH_NAME_INDEX = 4;
    /** The index of the method name in the description of a white box test. */
    private static final int WHITE_METHOD_NAME_INDEX = 5;

    /** The length of a white box test description. */
    private static final int WHITE_DESCRIPTION_LENGTH = 6;
    /** The length of a black box test description. */
    private static final int BLACK_DESCRIPTION_LENGTH = 5;
    /** The length of a random input or no execution failure test description. */
    private static final int FAILURE_DESCRIPTION_LENGTH = 7;

    /** The results of the test. */
    private List<LayoutTestResult> results;
    /** The result of the test running. */
    private LayoutTestResult currentResult;
    /** Whether the test is a black box test. */
    private boolean isBlackBox = true;
    /** A list of the test methods. */
    private List<Description> atomicChildren = new ArrayList<>();
    /** A list of the white box test methods. */
    private List<WhiteBoxTestDescription> atomicChildrenWhite = new ArrayList<>();
    
    
    /**
     * Creates a new instance that adds the test results to the given list.
     */
    LayoutRunListener(final List<LayoutTestResult> results) {
        super();
        this.results = results;
    }

    /**
     * Returns the white box test methods.
     */
    public List<WhiteBoxTestDescription> getAtomicWhiteBoxChildren() {
        return atomicChildrenWhite;
    }

    @Override
    public void testRunStarted(final Description description) throws Exception {
        // Check whether it is a blackbox or whitebox runner
        String name = description.getDisplayName();
        if (name.startsWith("Black")) {
            isBlackBox = true;
        } else if (name.startsWith("White")) {
            isBlackBox = false;
        } else {
            atomicChildren.clear();
            
            // Build a list of atomic descriptions
            Deque<Description> descriptionQueue = new LinkedList<>();
            descriptionQueue.add(description);
            
            while (!descriptionQueue.isEmpty()) {
                Description currDesc = descriptionQueue.poll();
                
                if (currDesc.isTest()) {
                    atomicChildren.add(currDesc);
                } else {
                    descriptionQueue.addAll(currDesc.getChildren());
                }
            }
            
            for (Description atomicChild : atomicChildren) {
                name = atomicChild.getDisplayName();
                
                // Parsing the description text in this way is hideous; we should improve this
                String lineSeparator = System.getProperty("line.separator");
                String[] splitName = name.split(lineSeparator);
                if (splitName.length == WHITE_DESCRIPTION_LENGTH) {
                    String nameWithoutGraph = splitName[ALGORITHM_INDEX] + lineSeparator
                            + splitName[WHITE_PHASE_NAME_INDEX] + lineSeparator
                            + splitName[WHITE_CLASS_NAME_INDEX] + lineSeparator
                            + splitName[WHITE_METHOD_NAME_INDEX];
                    
                    int index = -1;
                    for (int i = 0; i < atomicChildrenWhite.size(); i++) {
                        if (atomicChildrenWhite.get(i).getNameWithoutGraph().equals(nameWithoutGraph)) {
                            index = i;
                        }
                    }
                    
                    if (index > -1) {
                        atomicChildrenWhite.get(index).setActualExecutions(
                                atomicChildrenWhite.get(index).getActualExecutions() + 1);
                        
                    } else {
                        FailIfNotExecuted failAnnot = atomicChild.getAnnotation(FailIfNotExecuted.class);
                        boolean fail = failAnnot == null
                                ? true
                                : failAnnot.value();
                        atomicChildrenWhite.add(new WhiteBoxTestDescription(nameWithoutGraph, 1, 0, fail));
                    }
                }
            }
        }
    }

    @Override
    public void testStarted(final Description description) throws Exception {
        String name = description.getDisplayName();
        
        String lineSeparator = System.getProperty("line.separator");
        String[] params = name.split(lineSeparator);
        if (params.length == BLACK_DESCRIPTION_LENGTH || params.length == WHITE_DESCRIPTION_LENGTH) {
            currentResult = new LayoutTestResult();
            currentResult.setIsBlackBox(isBlackBox);
            currentResult.setAlgorithm(params[ALGORITHM_INDEX]);
            
            // split with "," and get graph name, algorithm, test class and test method
            if (isBlackBox) {
                currentResult.setClassName(params[BLACK_CLASS_NAME_INDEX]);
                currentResult.setGraphName(params[BLACK_GRAPH_NAME_INDEX]);
                currentResult.setMethodName(params[BLACK_METHOD_NAME_INDEX]);
            } else {
                currentResult.setProcessorName(params[WHITE_PHASE_NAME_INDEX]);
                currentResult.setClassName(params[WHITE_CLASS_NAME_INDEX]);
                currentResult.setGraphName(params[WHITE_GRAPH_NAME_INDEX]);
                currentResult.setMethodName(params[WHITE_METHOD_NAME_INDEX]);
            }
            results.add(currentResult);
            
        } else if (params.length == FAILURE_DESCRIPTION_LENGTH) {
            if (params[1].equals("RANDOM")) {
                currentResult = new LayoutTestResult();
                currentResult.setAlgorithm("RANDOM");
                currentResult.setClassName("RANDOM");
                currentResult.setGraphName("RANDOM");
                currentResult.setMethodName("RANDOM");
                results.add(currentResult);
            } else if (params[1].equals("NOEXECUTION")) {
                currentResult = new LayoutTestResult();
                currentResult.setAlgorithm("NOEXECUTION");
                currentResult.setClassName("NOEXECUTION");
                currentResult.setGraphName("NOEXECUTION");
                currentResult.setMethodName("NOEXECUTION");
                results.add(currentResult);
            }
        }
        
        // for a white box test it is counted on how many graphs it is executed in order to be able to find tests that
        // had not been executed
        if (params.length == WHITE_DESCRIPTION_LENGTH) {
            String nameWithoutGraph = params[ALGORITHM_INDEX] + lineSeparator
                    + params[WHITE_PHASE_NAME_INDEX] + lineSeparator
                    + params[WHITE_CLASS_NAME_INDEX] + lineSeparator
                    + params[WHITE_METHOD_NAME_INDEX];
            
            for (WhiteBoxTestDescription pair : atomicChildrenWhite) {
                if (pair.getNameWithoutGraph().equals(nameWithoutGraph)) {
                    pair.setExpectedExecutions(pair.getExpectedExecutions() + 1);
                }
            }
        }
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        currentResult.getFailures().add(failure);
    }

    @Override
    public void testAssumptionFailure(final Failure failure) {
        currentResult.getAssumptionFailures().add(failure);
    }
}
