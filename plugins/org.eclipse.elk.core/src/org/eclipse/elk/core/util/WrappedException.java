/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
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
 * A runtime exception that can be used to wrap checked exceptions. Use this where it is
 * appropriate to forward an error to the next point where it can be handled (i.e. displayed
 * to the user) without the need to explicitly declare the error in every method signature.
 *
 * @author msp
 * @kieler.design 2014-04-17 reviewed by cds, chsch, tit, uru
 * @kieler.rating proposed yellow 2012-11-02 cds
 */
public class WrappedException extends RuntimeException {

    /** the serial version UID. */
    private static final long serialVersionUID = -1630132187697677735L;
    
    /**
     * Create a wrapped exception.
     * 
     * @param cause the error that caused this exception
     */
    public WrappedException(final Throwable cause) {
        super(cause);
    }
    
    /**
     * Create a wrapped exception with additional message.
     * 
     * @param cause the error that caused this exception
     * @param message an additional message for information
     */
    public WrappedException(final Throwable cause, final String message) {
        super(message, cause);
    }

}
