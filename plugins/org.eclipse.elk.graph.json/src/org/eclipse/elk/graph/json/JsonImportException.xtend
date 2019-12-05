/*******************************************************************************
 * Copyright (c) 2017, 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.json

/**
 * Exception indicating that something is wrong with 
 * a passed json.
 */
class JsonImportException extends RuntimeException {
    new() {
        super()
    }

    new(String message) {
        super(message)
    }
    
    new(Throwable throwable) {
        super(throwable)
    }
    
    new(String message, Throwable throwable) {
        super(message, throwable)
    }
}