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

import java.util.List;

/**
 * Interface for monitors of progress of a job.
 * 
 * @kieler.design 2014-04-17 reviewed by cds, chsch, tit, uru
 * @kieler.rating 2009-12-11 proposed yellow msp
 * @author msp
 */
public interface IElkProgressMonitor {

    /** constant indicating an unknown amount of work. */
    float UNKNOWN_WORK = -1;

    /**
     * Notifies that the task will begin after this method has been called.
     * 
     * @param name readable name of the new task
     * @param totalWork total amount of work units, or <code>UNKNOWN_WORK</code>
     *            if this is not specified
     * @return true if the task has not begun before, false otherwise
     */
    boolean begin(String name, float totalWork);
    
    /**
     * Returns true if the task has already begun and is not done yet.
     * 
     * @return true if the task is running
     */
    boolean isRunning();

    /**
     * Notifies that the current task is done and closes the monitor. This method may be called
     * multiple times after the task has begun, without any effect after the first time.
     */
    void done();

    /**
     * Notifies that the given number of work units has been completed. This
     * method will have no effect is the monitor is closed.
     * 
     * @param work number of work units
     */
    void worked(float work);

    /**
     * Returns whether cancellation of the task has been requested.
     * 
     * @return true if cancellation has been requested
     */
    boolean isCanceled();

    /**
     * Creates a new sub-task that will complete the given amount of work units
     * when it is done. The sub-task begins when {@link #begin(String, int)} is
     * called for the new progress monitor instance, and it ends when
     * {@link #done()} is called for that instance.
     * 
     * @param work number of work units that are completed in the current
     *            monitor instance when the sub-task is done
     * @return a progress monitor for the new sub-task, or null if the monitor
     *         is closed
     */
    IElkProgressMonitor subTask(float work);

    /**
     * Returns a list of all monitors associated with direct sub-tasks.
     * 
     * @return list of sub-task monitors
     */
    List<IElkProgressMonitor> getSubMonitors();

    /**
     * Returns the parent monitor. The parent monitor is the one for which a
     * call to {@link #subTask(int)} resulted in the current monitor instance.
     * 
     * @return the parent monitor, or null if there is none
     */
    IElkProgressMonitor getParentMonitor();

    /**
     * Returns the name of the task associated with this progress monitor.
     * 
     * @return task name
     */
    String getTaskName();

    /**
     * Returns the measured execution time for the task associated with this monitor.
     * This is optional: implementations may just return 0 in order to avoid the additional
     * overhead of measuring the execution time.
     * 
     * @return number of seconds used for execution
     */
    double getExecutionTime();

}
