/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    spoenemann - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util;

/**
 * Interface for indicators of task cancelation. Possibly long-running tasks such as layout algorithms
 * should query the indicator from time to time in order to allow users to abort the process.
 */
public interface IElkCancelIndicator {

    /**
     * Returns whether cancellation of the task has been requested.
     * 
     * @return true if cancellation has been requested
     */
    boolean isCanceled();

}
