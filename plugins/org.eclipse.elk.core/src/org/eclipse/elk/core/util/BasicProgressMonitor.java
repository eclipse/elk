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

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for implementations of progress monitors. This class performs
 * execution time measurement, keeps track of the amount of completed work, and
 * handles sub-tasks properly.
 *
 * @kieler.design 2014-04-17 reviewed by cds, chsch, tit, uru
 * @kieler.rating 2009-12-11 proposed yellow msp
 * @author msp
 */
public class BasicProgressMonitor implements IElkProgressMonitor {

    /** the parent monitor. */
    private BasicProgressMonitor parentMonitor;
    /** indicates whether the monitor has been closed. */
    private boolean closed = false;
    /** list of child monitors. */
    private final List<IElkProgressMonitor> children = new LinkedList<IElkProgressMonitor>();
    /**
     * the number of work units that will be consumed after completion of the
     * currently active child task.
     */
    private float currentChildWork = -1;
    /** the start time of the associated task, in nanoseconds. */
    private long startTime;
    /** the total time of the associated task, in seconds. */
    private double totalTime;
    /** the name of the associated task. */
    private String taskName;
    /** the amount of work that is completed. */
    private float completedWork = 0.0f;
    /** the number of work units that can be completed in total. */
    private float totalWork;
    /** the maximal number of hierarchy levels for which progress is reported. */
    private final int maxLevels;
    /** whether the execution time shall be measured when the task is done. */
    private final boolean measureExecutionTime;

    /**
     * Creates a progress monitor with infinite number of hierarchy levels.
     */
    public BasicProgressMonitor() {
        this.maxLevels = -1;
        this.measureExecutionTime = true;
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
        this.measureExecutionTime = true;
    }

    /**
     * Creates a progress monitor with the given maximal number of hierarchy levels. If the
     * number is negative, the hierarchy levels are infinite. Otherwise progress is
     * reported to parent monitors only up to the specified number of levels. Furthermore, the
     * second parameter controls whether any execution time measurements shall be performed.
     *
     * @param maxLevels the maximal number of hierarchy levels for which progress is reported
     * @param measureExecutionTime whether the execution time shall be measured when the task is done
     */
    public BasicProgressMonitor(final int maxLevels, final boolean measureExecutionTime) {
        this.maxLevels = maxLevels;
        this.measureExecutionTime = measureExecutionTime;
    }

    /**
     * {@inheritDoc}
     */
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
            if (measureExecutionTime) {
                // elkjs-exclude-start
                startTime = System.nanoTime();
                // elkjs-exclude-end
            }
            return true;
        }
    }

    /**
     * Invoked when a task begins, to be overridden by subclasses. This
     * implementation does nothing.
     *
     * @param name task name
     * @param newTotalWork total amount of work for the new task
     * @param topInstance if true, this progress monitor is the top instance
     * @param maxHierarchyLevels the maximal number of reported hierarchy levels, or -1
     *     for infinite levels
     */
    protected void doBegin(final String name, final float newTotalWork,
            final boolean topInstance, final int maxHierarchyLevels) {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRunning() {
        return taskName != null && !closed;
    }

    /** factor for nanoseconds. */
    private static final double NANO_FACT = 1e-9;

    /**
     * {@inheritDoc}
     */
    public final void done() {
        if (taskName == null) {
            throw new IllegalStateException("The task has not begun yet.");
        }
        if (!closed) {
            if (measureExecutionTime) {
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

    /**
     * {@inheritDoc}
     */
    public final double getExecutionTime() {
        return totalTime;
    }

    /**
     * {@inheritDoc}
     */
    public final List<IElkProgressMonitor> getSubMonitors() {
        return children;
    }

    /**
     * {@inheritDoc}
     */
    public final IElkProgressMonitor getParentMonitor() {
        return parentMonitor;
    }

    /**
     * {@inheritDoc}
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * This implementation always returns {@code false}.
     *
     * @return {@code false}
     */
    public boolean isCanceled() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public final IElkProgressMonitor subTask(final float work) {
        if (!closed) {
            BasicProgressMonitor subMonitor = doSubTask(work, maxLevels, measureExecutionTime);
            children.add(subMonitor);
            subMonitor.parentMonitor = this;
            currentChildWork = work;
            return subMonitor;
        } else {
            return null;
        }
    }

    /**
     * Invoked when a sub-task is created, to be overridden by subclasses. This
     * implementation creates a new {@code BasicProgressMonitor} instance.
     *
     * @param work amount of work that is completed in the current monitor
     *         instance when the sub-task ends
     * @param maxHierarchyLevels the maximal number of reported hierarchy levels for the parent
     *         progress monitor, or -1 for infinite levels
     * @param measureExecTime whether the execution time shall be measured when the task is done
     * @return a new progress monitor instance
     */
    protected BasicProgressMonitor doSubTask(final float work, final int maxHierarchyLevels,
            final boolean measureExecTime) {
        if (maxHierarchyLevels > 0) {
            return new BasicProgressMonitor(maxHierarchyLevels - 1, measureExecTime);
        } else {
            return new BasicProgressMonitor(maxHierarchyLevels, measureExecTime);
        }
    }

    /**
     * {@inheritDoc}
     */
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

}
