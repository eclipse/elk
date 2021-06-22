/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.core.service.util;

import java.util.concurrent.ExecutorService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.elk.core.service.ElkServicePlugin;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.IElkCancelIndicator;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.Maybe;

/**
 * An operation that can be tracked with a progress bar. This operation can be called from any
 * thread, UI or non-UI, and adds only very little overhead for progress tracking compared to the
 * usual synchronous tracking of the workbench progress service. Just implement
 * {@link #execute(IProgressMonitor)} and call {@link #runMonitored()}.
 * 
 * <p>
 * In case no workbench is set up and, thus, the operation shall run off-screen call
 * {@link #runUnmonitored()}.
 * </p>
 * 
 * @author msp
 */
public abstract class MonitoredOperation {

    /** maximal number of recursion levels for which progress is displayed. */
    private static final int MAX_PROGRESS_LEVELS = 4;
    
    /** The executor service used to perform operations. */
    protected final ExecutorService executorService;
    /** Timestamp of the operation's starting time. */
    protected long timestamp;
    /** Whether the operation has been canceled. */
    protected boolean isCanceled;
    /** Additional cancel indicator to query. */
    protected final IElkCancelIndicator cancelIndicator;
    
    /**
     * Create a monitored operation with the given executor service.
     * 
     * @param service an executor service for performing operations
     */
    public MonitoredOperation(final ExecutorService service) {
        this(service, null);
    }
    
    /**
     * Create a monitored operation with the given executor service and cancel indicator.
     * 
     * @param service an executor service for performing operations
     * @param cancelIndicator an indicator queried regularly for cancelation
     */
    public MonitoredOperation(final ExecutorService service, final IElkCancelIndicator cancelIndicator) {
        if (service == null) {
            throw new NullPointerException();
        }
        this.executorService = service;
        this.cancelIndicator = cancelIndicator;
    }
    
    /**
     * Determine whether the operation has been canceled.
     */
    protected boolean isCanceled() {
        return isCanceled || (cancelIndicator != null && cancelIndicator.isCanceled());
    }
    
    /**
     * Execute the monitored operation.
     * 
     * @param monitor the progress monitor for the operation
     * @return a status indicating success or failure
     */
    protected abstract IStatus execute(IElkProgressMonitor monitor);
    
    /**
     * Executed in the UI thread before the operation starts. The default
     * implementation does nothing.
     */
    protected void preUIexec() {
    }
    
    /**
     * Executed in the UI thread after the operation has ended. The default
     * implementation does nothing.
     */
    protected void postUIexec() {
    }

    /**
     * Factory method providing {@link IElkProgressMonitor IElkProgressMonitors}.
     * May be overridden in order to contributed specialized implementations.
     *
     * @return a new {@link IElkProgressMonitor} instance each time it is called
     */
    protected IElkProgressMonitor createMonitor() {
        return new CancelableProgressMonitor();
    }
    
    /**
     * Run the operation. If the current thread is the UI thread, the actual operation
     * is executed in a new thread that runs in parallel. Otherwise the operation is
     * executed directly and a handler for the progress monitor is executed in the
     * parallel UI thread. In either case the method returns only after execution of
     * the operation is done.
     */
    public void runMonitored() {
        timestamp = System.currentTimeMillis();
        // Run monitored
    }
    
    /**
     * Run the operation without any progress monitor. If the current thread is the UI thread,
     * the actual operation is executed in a new thread that runs in parallel. Otherwise the
     * operation is executed directly and a handler for the progress monitor is executed in the
     * parallel UI thread. In either case the method returns only after execution of
     * the operation is done.
     * 
     */
    public void runUnmonitored() {
        timestamp = System.currentTimeMillis();
        runOffscreen();
    }
    
    /**
     * Return the timestamp of the operation's start, or 0 if it has not started yet.
     * 
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Cancel the operation. This flag can be queried with the
     * {@link IElkProgressMonitor#isCanceled()} method of the progress monitor passed in
     * {@link #execute(IElkProgressMonitor)}.
     */
    public void cancel() {
        isCanceled = true;
    }


    /**
     * Run the operation without switching to any other thread.
     * 
     * @author chsch
     */
    private void runOffscreen() {
        final Maybe<IStatus> status = new Maybe<IStatus>();
        
        try {
            // execute the preparation prior to the actual operation 
            preUIexec();
        } catch (Throwable throwable) {
            status.set(new Status(IStatus.ERROR, ElkServicePlugin.PLUGIN_ID,
                    "Error in monitored operation running offscreen during prepration", throwable));
        }

        if (status.get() == null && !isCanceled()) {
            // execute the actual operation without progress monitor
            status.set(execute(createMonitor()));
        }

        if (status.get() != null && status.get().getSeverity() == IStatus.OK && !isCanceled()) {
            // execute the post processing code after the actual operation
            try {
                postUIexec();
            } catch (Throwable throwable) {
                status.set(new Status(IStatus.ERROR, ElkServicePlugin.PLUGIN_ID,
                        "Error in monitored operation running offscreen during post processing",
                        throwable));
            }
        }

        handleStatus(status);
    }
    
    /**
     * Handle a status object.
     * 
     * @param status a status
     */
    protected void handleStatus(final Maybe<IStatus> status) {
    }
    
    
    /**
     * A progress monitor that can be canceled with the operation's {@link #cancel()} method.
     */
    protected class CancelableProgressMonitor extends BasicProgressMonitor {
        
        /**
         * Create a cancelable progress monitor.
         */
        public CancelableProgressMonitor() {
            super(0);
        }
        
        @Override
        public boolean isCanceled() {
            return MonitoredOperation.this.isCanceled();
        }
        
    }
}
