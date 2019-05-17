/*******************************************************************************
 * Copyright (c) 2016, 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christoph Daniel Schulze - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.debug.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Lists;

/**
 * Keeps information about a layout execution. The information are derived from {@link IElkProgressMonitor} instances.
 * Since progress monitors can form a hierarchy, so can executions. Once constructed based on a progress monitor, an
 * execution is immutable.
 */
public final class ExecutionInfo {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fields

    /** The name of this execution. */
    private String name;
    /** Our parent execution, if any. */
    private ExecutionInfo parent;
    /** Unmodifiable list of child executions. */
    private List<ExecutionInfo> children;

    /** Whether execution times were measured. */
    private boolean executionTimeMeasured;
    /** Amount of time spent in this execution and its child executions. */
    private double executionTimeIncludingChildren;
    /** Amount of time spent in this execution locally, without its child executions. */
    private double executionTimeLocal;

    /** List of log messages. */
    private List<String> logMessages;
    /** Whether there are any descendants that have a non-empty list of log messages. */
    private boolean hasDescendantsWithLogs;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction

    /**
     * Private constructor.
     */
    private ExecutionInfo() {

    }

    /**
     * Create an Execution from a given {@link IElkProgressMonitor} without a parent monitor.
     * 
     * @param monitor
     *            that holds information about execution time.
     * @return the execution with its children.
     */
    public static ExecutionInfo fromProgressMonitor(final IElkProgressMonitor monitor) {
        return fromProgressMonitor(monitor, null);
    }

    /**
     * Create a child execution for the given parent.
     * 
     * @param monitor
     *            that holds information about execution time.
     * @param parent
     *            for the child created from the given monitor.
     * @return a child execution with its children.
     */
    private static ExecutionInfo fromProgressMonitor(final IElkProgressMonitor monitor,
            final ExecutionInfo parent) {

        ExecutionInfo execution = new ExecutionInfo();

        // Basic properties
        execution.name = monitor.getTaskName() != null ? monitor.getTaskName() : "Unnamed";
        execution.executionTimeMeasured = monitor.isExecutionTimeMeasured();
        execution.executionTimeIncludingChildren = monitor.getExecutionTime();
        execution.parent = parent;

        // Log messages
        List<String> log = Lists.newArrayList(monitor.getLogs());
        execution.logMessages = Collections.unmodifiableList(log);

        // We compute the local execution time by starting with the total execution time and subtracting child
        // execution times as we iterate over them
        execution.executionTimeLocal = execution.executionTimeIncludingChildren;

        // Add executions for child monitors
        List<ExecutionInfo> childExecutions = new ArrayList<>(monitor.getSubMonitors().size());
        for (IElkProgressMonitor child : monitor.getSubMonitors()) {
            ExecutionInfo childExecution = ExecutionInfo.fromProgressMonitor(child, execution);
            childExecutions.add(childExecution);
            
            execution.executionTimeLocal -= child.getExecutionTime();
            execution.hasDescendantsWithLogs |=
                    childExecution.hasLogMessages() || childExecution.hasDescendantsWithLogMessages();
        }
        execution.children = childExecutions;

        // Ensure the local execution time does not drop below zero due to double arithmetic
        execution.executionTimeLocal = Math.max(execution.executionTimeLocal, 0.0);

        return execution;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors

    /**
     * Returns the name of this execution that should be displayed.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the parent execution.
     * 
     * @return the parent, or {@code null} if there is none.
     */
    public ExecutionInfo getParent() {
        return parent;
    }

    /**
     * Returns the children of this execution. Can be modified.
     */
    public List<ExecutionInfo> getChildren() {
        return children;
    }
    
    /**
     * Returns whether execution times were measured. That is, if calls to {@link #getExecutionTimeLocal()} and
     * {@link #getExecutionTimeIncludingChildren()} return anything meaningful.
     */
    public boolean isExecutionTimeMeasured() {
        return executionTimeMeasured;
    }

    /**
     * Returns how much time this execution took to execute, including its child executions.
     */
    public double getExecutionTimeIncludingChildren() {
        return executionTimeIncludingChildren;
    }

    /**
     * Returns how much time this execution took to execute locally, without child executions.
     */
    public double getExecutionTimeLocal() {
        return executionTimeLocal;
    }
    
    /**
     * Returns the list of log messages.
     * 
     * @return the list of log messages, as an unmodifiable list.
     */
    public List<String> getLogMessages() {
        return logMessages;
    }
    
    /**
     * Returns whether or not this execution info contains at least one log message.
     */
    public boolean hasLogMessages() {
        return !logMessages.isEmpty();
    }
    
    /**
     * Returns whether or not any descendants of this execution would return {@code true} upon a call to
     * {@link #hasLogMessages()}.
     */
    public boolean hasDescendantsWithLogMessages() {
        return hasDescendantsWithLogs;
    }

}
