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
package org.eclipse.elk.alg.sequence.properties;

/**
 * The type of sequence execution.
 * 
 * @author dja
 */
public enum SequenceExecutionType {
    
    /** It's an execution. */
    EXECUTION,
    /** It's a duration.*/
    DURATION,
    /** It's a time constraint. */
    TIME_CONSTRAINT;
    
    
    /**
     * Returns the sequence execution type that belongs to the given node type.
     * 
     * @param nodeType
     *            the node type.
     * @return the corresponding sequence execution type, or {@code null} if there is none.
     */
    public static SequenceExecutionType fromNodeType(final NodeType nodeType) {
        switch (nodeType) {
        case BEHAVIOUR_EXEC_SPECIFICATION:
        case ACTION_EXEC_SPECIFICATION:
            return EXECUTION;
            
        case DURATION_CONSTRAINT:
            return DURATION;
            
        case TIME_CONSTRAINT:
            return TIME_CONSTRAINT;
        
        default:
            return null;
        }
    }
    
}
