/*******************************************************************************
 * Copyright (c) 2009, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Base class for implementations of progress monitors. This class performs execution time measurement, logs debug
 * output, keeps track of the amount of completed work, and handles sub-tasks properly.
 */
public class BasicProgressMonitor implements IElkProgressMonitor {
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties

    /** the parent monitor. */
    private BasicProgressMonitor parentMonitor;
    /** list of child monitors. */
    private final List<IElkProgressMonitor> children = new LinkedList<IElkProgressMonitor>();
    /** the maximal number of hierarchy levels for which progress is reported. */
    private final int maxLevels;
    
    /** the name of the associated task. */
    private String taskName;
    /** indicates whether the monitor has been closed. */
    private boolean closed = false;
    /** the number of work units that can be completed in total. */
    private float totalWork;
    /** the amount of work that is completed. */
    private float completedWork = 0.0f;
    /** the number of work units that will be consumed after completion of the currently active child task. */
    private float currentChildWork = -1;
    
    /** whether logging should be enabled (if not, logging objects does nothing). */
    private boolean recordLogs = false;
    /** list of log messages (only non-null if logs are enabled). */
    private List<String> logMessages = null;
    /** list of logged graphs (only non-null if logs are enabled). */
    private List<LoggedGraph> logGraphs = null;
    /** whether the execution time shall be measured when the task is done. */
    private boolean recordExecutionTime = false;
    /** the start time of the associated task, in nanoseconds. */
    private long startTime;
    /** the total time of the associated task, in seconds. */
    private double totalTime;

    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Creation

    /**
     * Creates a progress monitor with infinite number of hierarchy levels.
     */
    public BasicProgressMonitor() {
        this(-1);
    }

    /**
     * Creates a progress monitor with the given maximal number of hierarchy levels. If the
     * number is negative, the hierarchy levels are infinite. Otherwise progress is
     * reported to parent monitors only up to the specified number of levels.
     *
     * @param themaxLevels the maximal number of hierarchy levels for which progress is
     *     reported
     */
    public BasicProgressMonitor(final int themaxLevels) {
        this.maxLevels = themaxLevels;
    }

    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration
    
    /**
     * Enable or disable logging on this monitor. This method should be called right after the monitor has been created
     * and allows for method chaining.
     * 
     * @param enabled {@code true} if log messages and graphs should be saved.
     * @return this progress monitor.
     */
    public BasicProgressMonitor withLogging(final boolean enabled) {
        recordLogs = enabled;
        
        if (recordLogs) {
            logMessages = new ArrayList<>();
            logGraphs = new ArrayList<>();
        } else {
            logMessages = null;
            logGraphs = null;
        }
        
        return this;
    }

    /**
     * Enable or disable execution time measurement on this monitor. This method should be called right after the
     * monitor has been created and allows for method chaining.
     * 
     * @param enabled {@code true} if execution times should be measured.
     * @return this progress monitor.
     */
    public BasicProgressMonitor withExecutionTimeMeasurement(final boolean enabled) {
        recordExecutionTime = enabled;
        return this;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Work

    @Override
    public final boolean begin(final String name, final float thetotalWork) {
        if (closed) {
            throw new IllegalStateException("The task is already done.");
        } else if (this.taskName != null) {
            return false;
        } else {
            if (name == null) {
                throw new NullPointerException();
            }
            this.taskName = name;
            this.totalWork = thetotalWork;
            doBegin(name, thetotalWork, parentMonitor == null, maxLevels);
            if (recordExecutionTime) {
                // elkjs-exclude-start
                startTime = System.nanoTime();
                // elkjs-exclude-end
            }
            return true;
        }
    }

    /**
     * Invoked when a task begins, to be overridden by subclasses. This implementation does nothing.
     *
     * @param name task name
     * @param newTotalWork total amount of work for the new task
     * @param topInstance if true, this progress monitor is the top instance
     * @param maxHierarchyLevels the maximal number of reported hierarchy levels, or -1 for infinite levels
     */
    protected void doBegin(final String name, final float newTotalWork,
            final boolean topInstance, final int maxHierarchyLevels) {
    }
    
    @Override
    public final void worked(final float work) {
        if (work > 0 && !closed) {
            internalWorked(work);
        }
    }

    /**
     * Sets the current work counters of this monitor and all parent monitors.
     *
     * @param work amount of work that has been completed
     */
    private void internalWorked(final float work) {
        if (totalWork > 0 && completedWork < totalWork) {
            completedWork += work;
            doWorked(completedWork, totalWork, parentMonitor == null);
            if (parentMonitor != null && parentMonitor.currentChildWork > 0 && maxLevels != 0) {
                parentMonitor.internalWorked(work / totalWork * parentMonitor.currentChildWork);
            }
        }
    }

    /**
     * Invoked when work is done for this progress monitor, to be overridden by
     * subclasses. This implementation does nothing.
     *
     * @param thecompletedWork total number of work that is done for this task
     * @param thetotalWork total number of work that is targeted for completion
     * @param topInstance if true, this progress monitor is the top instance
     */
    protected void doWorked(final float thecompletedWork, final float thetotalWork,
            final boolean topInstance) {
    }
    
    /** factor for nanoseconds. */
    private static final double NANO_FACT = 1e-9;

    @Override
    public final void done() {
        if (taskName == null) {
            throw new IllegalStateException("The task has not begun yet.");
        }
        if (!closed) {
            if (recordExecutionTime) {
                // elkjs-exclude-start
                totalTime = (System.nanoTime() - startTime) * NANO_FACT;
                // elkjs-exclude-end
            }
            if (completedWork < totalWork) {
                internalWorked(totalWork - completedWork);
            }
            doDone(parentMonitor == null, maxLevels);
            closed = true;
        }
    }

    /**
     * Invoked when a task ends, to be overridden by subclasses. This
     * implementation does nothing.
     *
     * @param topInstance if true, this progress monitor is the top instance
     * @param maxHierarchyLevels the maximal number of reported hierarchy levels, or -1
     *     for infinite levels
     */
    protected void doDone(final boolean topInstance, final int maxHierarchyLevels) {
    }
    
    @Override
    public boolean isRunning() {
        return taskName != null && !closed;
    }

    /**
     * This implementation always returns {@code false}.
     *
     * @return {@code false}
     */
    public boolean isCanceled() {
        return false;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Sub-Tasks

    @Override
    public final IElkProgressMonitor subTask(final float work) {
        if (!closed) {
            BasicProgressMonitor subMonitor = doSubTask(work, maxLevels);
            children.add(subMonitor);
            subMonitor.parentMonitor = this;
            currentChildWork = work;
            return subMonitor;
        } else {
            return null;
        }
    }

    /**
     * Invoked when a sub-task is created, to be overridden by subclasses. Regarding debug logging, the new instance
     * should be configured just like the instance the method is called on. This implementation creates a new
     * {@code BasicProgressMonitor} instance.
     *
     * @param work amount of work that is completed in the current monitor
     *         instance when the sub-task ends
     * @param maxHierarchyLevels the maximal number of reported hierarchy levels for the parent
     *         progress monitor, or -1 for infinite levels
     * @return a new progress monitor instance
     */
    protected BasicProgressMonitor doSubTask(final float work, final int maxHierarchyLevels) {
        int newMaxHierarchyLevels = Math.max(0, maxHierarchyLevels - 1);
        return new BasicProgressMonitor(newMaxHierarchyLevels)
                .withLogging(recordLogs)
                .withExecutionTimeMeasurement(recordExecutionTime);
    }

    @Override
    public final List<IElkProgressMonitor> getSubMonitors() {
        return children;
    }

    @Override
    public final IElkProgressMonitor getParentMonitor() {
        return parentMonitor;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Debugging
    
    @Override
    public boolean isLoggingEnabled() {
        return recordLogs;
    }

    @Override
    public void log(final Object object) {
        if (recordLogs && object != null) {
            logMessages.add(object.toString());
        }
    }

    @Override
    public List<String> getLogs() {
        return logMessages;
    }
    
    @Override
    public void logGraph(final ElkNode graph, final String tag) {
        if (recordLogs && graph != null) {
            ElkNode nodeCopy = EcoreUtil.copy(graph);
            logGraphs.add(new LoggedGraph(nodeCopy, tag, LoggedGraph.Type.ELK));
        }
    }
    
    @Override
    public void logGraph(final Object object, final String tag, final LoggedGraph.Type graphType) {
        if (recordLogs && object != null && graphType != null) {
            logGraphs.add(new LoggedGraph(object, tag, graphType));
        }
    }

    @Override
    public List<LoggedGraph> getLoggedGraphs() {
        return logGraphs;
    }
    
    @Override
    public boolean isExecutionTimeMeasured() {
        return recordExecutionTime;
    }

    @Override
    public final double getExecutionTime() {
        return totalTime;
    }

}
