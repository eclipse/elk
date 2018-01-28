/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.options;

/**
 * Enumeration of possible message types for sequence diagrams.
 */
public enum MessageType {
    
    /** Standard messages for sequence diagrams. */
    ASYNCHRONOUS,
    /** Synchronous messages. */
    SYNCHRONOUS,
    /** Reply messages. */
    REPLY,
    /** Create messages. */
    CREATE,
    /** Delete messages. */
    DELETE,
    /** Lost messages. */
    LOST,
    /** Found messages. */
    FOUND;
    
}
