/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops;

/**
 * The direction in which the edges leave a port.
 */
public enum SelfLoopRoutingDirection {

    /** A port whose edges are routed leftwards. */
    LEFT,
    /** A port whose edges are routed rightwards. */
    RIGHT,
    /** A port whose edges are routed in both directions. */
    BOTH;
    
}
