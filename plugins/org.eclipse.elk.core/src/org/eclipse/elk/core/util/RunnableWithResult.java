/*******************************************************************************
 * Copyright (c) 2012, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util;

/**
 * Extension of {@link Runnable} that may be ask for a result, inspired by
 * {@link org.eclipse.emf.transaction.RunnableWithResult}.
 * 
 * @kieler.design 2014-04-17 reviewed by cds, chsch, tit, uru
 * @kieler.rating 2012-11-02 proposed yellow cds
 * @param <T> the type of the result
 * @author chsch
 */
public interface RunnableWithResult<T> extends Runnable {

    /**
     * Returns a result computed by my {@link Runnable#run()} method.
     * 
     * @return my result, or {@code null} if none
     */
    T getResult();
    
}
