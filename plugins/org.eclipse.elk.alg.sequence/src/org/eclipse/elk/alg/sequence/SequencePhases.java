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
    
    /** Allocates vertical space for things. */
    P1_SPACE_ALLOCATION,
    
    /** Breaks cycles. */
    P2_CYCLE_BREAKING,
    
    /** Assigns messages to layers, which will later determine their Y coordinate. */
    P3_MESSAGE_LAYERING,
    
    /** Determines the horizontal order of lifelines. */
    P4_LIFELINE_SORTING,
    
    /** Assigns coordinates to things. */
    P5_COORDINATE_ASSIGNMENT;
    
}
