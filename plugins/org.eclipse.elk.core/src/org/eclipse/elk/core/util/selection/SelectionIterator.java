/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util.selection;

import java.util.Set;

import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KPort;
import org.eclipse.emf.common.util.AbstractTreeIterator;

/**
 * This class can be used to implement Iterators for selections. The SelectionIterator is used in
 * {@link de.cau.cs.kieler.kiml.util.KimlUtil} to get the {@link KGraphElement KGraphElements}
 * connected to a selected edge.
 * 
 * @see DefaultSelectionIterator
 */
public abstract class SelectionIterator extends AbstractTreeIterator<KGraphElement> {

    private static final long serialVersionUID = 478793714459586388L;

    /**
     * The list of already visited ports. Used to break infinite loops.
     */
    protected Set<KPort> visited; // SUPPRESS CHECKSTYLE VisibilityModifier

    /**
     * Creates a {@link SelectionIterator} which needs to be configured afterwards by adding the set
     * of visited nodes. The iterator won't include the starting object.
     * 
     * @param edge
     *            The object to iterate from
     */
    public SelectionIterator(final KEdge edge) {
        super(edge, false);
    }

    /**
     * Attach a set of {@link KPort KPorts} to the iterator to be used as a set of visited ports.
     * Can be used to share a set of nodes across multiple iterators (target and source iterator).
     * 
     * @param visitedSet
     *            The set of nodes to be used as visited set.
     */
    public void attachVisitedSet(final Set<KPort> visitedSet) {
        this.visited = visitedSet;
    }
}
