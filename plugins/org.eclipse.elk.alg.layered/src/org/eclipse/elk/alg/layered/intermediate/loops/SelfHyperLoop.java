/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.loops;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LLabel;

/**
 * A self loop hyperedge consisting of at least one self loop edge.
 */
public class SelfHyperLoop {

    /** The {@link SelfLoopHolder} which owns this instance. */
    private final SelfLoopHolder slHolder;
    /** Set of edges that belong to this instance. */
    private final Set<SelfLoopEdge> slEdges = new HashSet<>();
    /** This hyper loop's labels. */
    private SelfHyperLoopLabels slLabels = null;

    /**
     * Creates a new instance owned by the given {@link SelfLoopHolder}.
     */
    SelfHyperLoop(final SelfLoopHolder slHolder) {
        this.slHolder = slHolder;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors

    /**
     * Returns the {@link SelfLoopHolder} which owns this instance.
     */
    public SelfLoopHolder getSLHolder() {
        return slHolder;
    }

    /**
     * Adds the given edge to this instance and sets everything up accordingly, unless the edge was already part of this
     * instance.
     */
    public void addSelfLoopEdge(final SelfLoopEdge slEdge) {
        if (slEdges.add(slEdge)) {
            slEdge.setSLHyperLoop(this);
            
            // Check if we need to take care of any edge labels
            List<LLabel> lLabels = slEdge.getLEdge().getLabels();
            if (!lLabels.isEmpty()) {
                if (slLabels == null) {
                    slLabels = new SelfHyperLoopLabels(this);
                }
                
                slLabels.getLLabels().addAll(lLabels);
            }
        }
    }

    /**
     * Returns the list of edges that belong to this instance.
     */
    public Set<SelfLoopEdge> getSLEdges() {
        return slEdges;
    }
}
