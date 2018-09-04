/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.notification.Failure;

import com.google.common.base.Joiner;

/**
 * Represents the result of a layout test. This function has some methods to print the result or store it in a file. The
 * algorithm, graph name, class name, method name, processor name (if white box test) and the failures are printed /
 * stored on separate lines.
 */
class LayoutTestResult {

    /** Whether the test is a black box test. */
    private boolean isBlackBox;
    /** The failures that occurred. */
    private List<Failure> failures = new ArrayList<>();
    /** The assumption failures that occurred. */
    private List<Failure> assumptionFailures = new ArrayList<>();
    /** The name of the graph. */
    private String graphName;
    /** The ID of the layout algorithm. */
    private String algorithm;
    /** The name of the test class. */
    private String className;
    /** The name of the test method. */
    private String methodName;
    /** The name of the processor. */
    private String processorName = "";
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessor Methods

    /**
     * Returns whether the test is a black box test.
     */
    public boolean isBlackBox() {
        return isBlackBox;
    }

    /**
     * Sets whether the test is a black box test.
     */
    public void setIsBlackBox(final boolean isBlackBox) {
        this.isBlackBox = isBlackBox;
    }

    /**
     * Returns the failures that occurred.
     */
    public List<Failure> getFailures() {
        return failures;
    }

    /**
     * Sets the failures that occurred.
     */
    public void setFailures(final List<Failure> failures) {
        this.failures = failures;
    }

    /**
     * Returns the assumption failures that occurred.
     */
    public List<Failure> getAssumptionFailures() {
        return assumptionFailures;
    }

    /**
     * Sets the assumption failures that occurred.
     */
    public void setAssumptionFailures(final List<Failure> assumptionFailures) {
        this.assumptionFailures = assumptionFailures;
    }

    /**
     * Returns the name of the graph.
     */
    public String getGraphName() {
        return graphName;
    }

    /**
     * Sets the name of the graph.
     */
    public void setGraphName(final String graphName) {
        this.graphName = graphName;
    }

    /**
     * Returns the ID of the layout algorithm.
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the ID of the layout algorithm.
     */
    public void setAlgorithm(final String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Returns the name of the test class.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the name of the test class.
     */
    public void setClassName(final String className) {
        this.className = className;
    }

    /**
     * Returns the name of the test method.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the name of the test method.
     */
    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    /**
     * Returns the name of the processor.
     */
    public String getProcessorName() {
        return processorName;
    }

    /**
     * Sets the name of the processor.
     */
    public void setProcessorName(final String phaseName) {
        this.processorName = phaseName;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Output

    /**
     * Returns a list of strings that represent this result.
     */
    private List<String> toStringList() {
        List<String> lines = new ArrayList<>();
        if (isBlackBox) {
            lines.add("Black Box test: " + className);
            lines.add("Algorithm: " + algorithm);
        } else {
            lines.add("White Box test: " + className);
            lines.add("Algorithm: " + algorithm + " Processor: " + processorName);
        }

        lines.add("Graph: " + graphName);
        lines.add("Test Class: " + className);
        lines.add("Test Method: " + methodName);
        if (failures.size() > 0 || assumptionFailures.size() > 0) {
            lines.add("FAILED!");
            for (Failure failure : failures) {
                lines.add(failure.toString());
            }
            for (Failure failure : assumptionFailures) {
                lines.add(failure.toString());
            }
        }
        lines.add("");
        return lines;
    }
    
    @Override
    public String toString() {
        return Joiner.on("\n").join(toStringList());
    }

    /**
     * Adds the results to the given file.
     */
    public void addToFile(final File file) {
        Path path = Paths.get(file.getAbsolutePath());
        try {
            Files.write(path, toStringList(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the results to the given file if the test failed.
     */
    public void addIfFailed(final File file) {
        if (!failures.isEmpty() || !assumptionFailures.isEmpty()) {
            addToFile(file);
        }
    }

    /**
     * Adds the results to the given file if the test succeeded.
     */
    public void addIfNotFailed(final File file) {
        if (failures.isEmpty() && assumptionFailures.isEmpty()) {
            addToFile(file);
        }
    }

    /**
     * Prints the result to the console.
     */
    public void printResult() {
        System.out.print(this);
    }

    /**
     * Prints the result to the console if the test failed.
     */
    public void printIfFailed() {
        if (!failures.isEmpty() || !assumptionFailures.isEmpty()) {
            printResult();
        }
    }

    /**
     * Prints the result to the console if the test succeeded.
     */
    public void ptintIfNotFailed() {
        if (failures.isEmpty() && assumptionFailures.isEmpty()) {
            printResult();
        }
    }

}
