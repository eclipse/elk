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
package org.eclipse.elk.alg.sequence.graph;

import java.util.List;

import org.eclipse.elk.core.math.KVector;

import com.google.common.collect.Lists;

/**
 * The graph representation for sequence diagrams.
 */
public final class SGraph extends SGraphElement {
    private static final long serialVersionUID = -7952451128297135991L;
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties
    
    /** The size of the diagram. This is modified during the layout process. */
    private KVector size = new KVector();
    
    /** The list of lifelines in the sequence diagram. It is not intended to have a special order. */
    // TODO: Why not make the order significant starting at a certain point in the algorithm?
    private List<SLifeline> lifelines = Lists.newArrayList();
    /** The list of comments in the sequence diagram. It is not intended to have a special order. */
    // TODO: Why not make the order significant starting at a certain point in the algorithm?
    private List<SComment> comments = Lists.newArrayList();
    /** The list of areas in the sequence diagram. */
    private List<SArea> areas = Lists.newArrayList();
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors
    
    /**
     * Returns the size of the diagram.
     * 
     * @return the size.
     */
    public KVector getSize() {
        return size;
    }

    /**
     * Returns the list of lifelines in the diagram.
     * 
     * @return the list of lifelines.
     */
    public List<SLifeline> getLifelines() {
        return lifelines;
    }

    /**
     * Returns the list of comments in the diagram.
     * 
     * @return the list of comments.
     */
    public List<SComment> getComments() {
        return comments;
    }

    /**
     * Returns the list of areas in the diagram.
     * 
     * @return the list of areas.
     */
    public List<SArea> getAreas() {
        return areas;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Lifeline Management

    /**
     * Add a lifeline to the diagram.
     * 
     * @param lifeline
     *            the new lifeline.
     */
    public void addLifeline(final SLifeline lifeline) {
        this.lifelines.add(lifeline);
    }

    /**
     * Remove a lifeline from the diagram.
     * 
     * @param lifeline
     *            the lifeline to be removed.
     */
    public void removeLifeline(final SLifeline lifeline) {
        lifelines.remove(lifeline);
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Stuff that Does Things (tm)
    
    @Override
    public String toString() {
        String ret = "SGraph: ( ";
        for (SLifeline lifeline : this.lifelines) {
            ret += lifeline.getName() + " ";
        }
        ret += ")";
        return ret;
    }
}
