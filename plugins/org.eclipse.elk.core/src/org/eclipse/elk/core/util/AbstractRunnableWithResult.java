/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
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
 * A simple standard implementation of {@link RunnableWithResult}.
 * 
 * @kieler.design 2014-04-17 reviewed by cds, chsch, tit, uru
 * @param <T> the type of the result
 * @author chsch
 */
public abstract class AbstractRunnableWithResult<T> implements RunnableWithResult<T> {
    
    private T result = null;
    
    /**
     * {@inheritDoc}
     */
    public final T getResult() {
        return this.result;
    }
    
    /**
     * Setter to be called by subclasses' run methods.
     * 
     * @param theResult the result to be delivered to the caller of the {@link Runnable}.
     */
    protected final void setResult(final T theResult) {
        this.result = theResult;
    }
    
}
