/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christoph Daniel Schulze - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.debug.views.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Keeps information about a layout execution. The information are derived from {@link IElkProgressMonitor} instances.
 * Since progress monitors can form a hierarchy, so can executions. Once constructed based on a progress monitor, an
 * execution is immutable.
 */
final class Execution {
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fields
    
    /** The name of this execution. */
    private String name;
    /** Amount of time spent in this execution and its child executions. */
    private double executionTimeIncludingChildren;
    /** Amount of time spent in this execution locally, without its child executions. */
    private double executionTimeLocal;
    /** Our parent execution, if any. */
    private Execution parent;
    /** Unmodifiable list of child executions. */
    private List<Execution> children;
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction
    
    /**
     * Private constructor.
     */
    private Execution() {
        
    }
    
    
    public static Execution fromProgressMonitor(final IElkProgressMonitor monitor) {
        Execution execution = new Execution();
        
        execution.name = monitor.getTaskName() != null ? monitor.getTaskName() : "Unnamed";
        execution.executionTimeIncludingChildren = monitor.getExecutionTime();
        execution.parent = null;
        
        // We compute the local execution time by starting with the total execution time and subtracting child
        // execution times as we iterate over them
        execution.executionTimeLocal = execution.executionTimeIncludingChildren;
        
        // Add executions for child monitors
        List<Execution> childExecutions = new ArrayList<>(monitor.getSubMonitors().size());
        for (IElkProgressMonitor child : monitor.getSubMonitors()) {
            childExecutions.add(Execution.fromProgressMonitor(child, execution));
            execution.executionTimeLocal -= child.getExecutionTime();
        }
        execution.children = Collections.unmodifiableList(childExecutions);
        
        // Ensure the local execution time does not drop below zero
        execution.executionTimeLocal = Math.max(execution.executionTimeLocal, 0.0);
        
        return execution;
    }
    
    private static Execution fromProgressMonitor(final IElkProgressMonitor monitor, final Execution parent) {
        Execution execution = Execution.fromProgressMonitor(monitor);
        execution.parent = parent;
        
        return execution;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors

    /**
     * Returns the name of this execution that should be displayed.
     * 
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns how much time this execution took to execute, including its child executions.
     * 
     * @return the execution time.
     */
    public double getExecutionTimeIncludingChildren() {
        return executionTimeIncludingChildren;
    }

    /**
     * Returns how much time this execution took to execute locally, without child executions.
     * 
     * @return the local execution time.
     */
    public double getExecutionTimeLocal() {
        return executionTimeLocal;
    }

    /**
     * Returns the parent execution.
     * 
     * @return the parent, or {@code null} if there is none.
     */
    public Execution getParent() {
        return parent;
    }

    /**
     * Returns the children of this execution.
     * 
     * @return the children, as an unmodifiable list.
     */
    public List<Execution> getChildren() {
        return children;
    }
    
}
