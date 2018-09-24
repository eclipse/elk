/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.graph;

/**
 * Represents a destruction event on a lifeline.
 */
public class SDestruction extends SShape {
    
    private static final long serialVersionUID = 1326237166924471861L;
    
    /** The lifeline the destruction event destroys. */
    private final SLifeline lifeline;
    
    
    /**
     * Creates a new destruction event for the given lifeline.
     */
    public SDestruction(final SLifeline lifeline) {
        this.lifeline = lifeline;
    }
    
    
    /**
     * Returns the lifeline this destruction event belongs to.
     */
    public SLifeline getLifeline() {
        return lifeline;
    }
    
}
