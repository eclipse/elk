/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence;

/**
 * All phases the sequence diagram layouter is divided into.
 */
public enum SequencePhases {
    
    /** Determines the horizontal order of lifelines. */
    P1_LIFELINE_SORTING,
    
    /** Allocates vertical space for things. */
    P2_SPACE_ALLOCATION,
    
    /** Breaks cycles. */
    P3_CYCLE_BREAKING,
    
    /** Assigns messages to layers, which will later determine their Y coordinate. */
    P4_MESSAGE_LAYERING,
    
    /** Assigns x coordinates to things. */
    P5_X_COORDINATE_ASSIGNMENT,
    
    /** Assigns y coordinates to things. */
    P6_Y_COORDINATE_ASSIGNMENT;
    
}
