/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core;

/**
 * Thrown when a layout algorithm is executed on a graph that has properties set on it that are not
 * supported by the algorithm.
 *
 * @author cds
 */
public class UnsupportedConfigurationException extends RuntimeException {

    /** the serial version UID. */
    private static final long serialVersionUID = -3617468773969103109L;

    
    /**
     * Create an unsupported graph configuration exception with no parameters.
     */
    public UnsupportedConfigurationException() {
        super();
    }
    
    /**
     * Create an unsupported graph configuration exception with a message.
     * 
     * @param message a message
     */
    public UnsupportedConfigurationException(final String message) {
        super(message);
    }
    
    /**
     * Create an unsupported graph configuration exception with a message and a cause.
     * 
     * @param message a message
     * @param cause a cause
     */
    public UnsupportedConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
