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
 * Enumeration of possible special message types for sequence diagrams. Whether a message is synchronous or
 * asynchronous is of no interest to the layout algorithm. The algorithm is interested in messages that need to be
 * handled in a special way, which is true for all message types in this enumeration.
 */
public enum MessageType {
    
    /** Create messages. */
    CREATE,
    /** Delete messages. */
    DELETE,
    /** Lost messages. */
    LOST,
    /** Found messages. */
    FOUND;
    
}
