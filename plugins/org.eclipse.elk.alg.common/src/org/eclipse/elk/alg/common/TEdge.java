/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.common;

import java.util.Objects;

import org.eclipse.elk.core.math.KVector;

/**
 * An undirected edge modeled as two vectors.
 */
public class TEdge {
    // CHECKSTYLEOFF VisibilityModifier
    /** vertex u. */
    public final KVector u;
    /** vertex v. */
    public final KVector v;
    // CHECKSTYLEON VisibilityModifier
    
    /**
     * Creates an edge (u,v).
     * 
     * @param u
     * @param v
     */
    public TEdge(final KVector u, final KVector v) {
        this.u = u;
        this.v = v;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TEdge) {
            TEdge other = (TEdge) obj;
            return (Objects.equals(u, other.u) && Objects.equals(v, other.v)) 
                || (Objects.equals(u, other.v) && Objects.equals(v, other.u));
        } else {
            return false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(u) + Objects.hashCode(v);
    }
}
