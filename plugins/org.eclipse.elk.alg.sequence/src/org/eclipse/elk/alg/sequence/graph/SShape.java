/*******************************************************************************
 * Copyright (c) 2018 cds and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.graph;

import org.eclipse.elk.core.math.KVector;

/**
 * Abstract superclass of graph elements that have a position and a size.
 */
public abstract class SShape extends SGraphElement {

    private static final long serialVersionUID = -5434991430530158175L;
    
    /** The position of the comment. */
    private KVector position = new KVector();
    /** The size of the comment. */
    private KVector size = new KVector();
    

    /**
     * Get the size of the comment.
     * 
     * @return the KVector with the size
     */
    public final KVector getSize() {
        return size;
    }

    /**
     * Get the position of the comment.
     * 
     * @return the KVector with the position
     */
    public final KVector getPosition() {
        return position;
    }
    
}
