/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.elk.alg.radial.sorting;

import java.util.List;

import org.eclipse.elk.graph.ElkNode;

/**
 * An interface for all ways of sorting the nodes of one radius.
 */
public interface IRadialSorter {

    /**
     * Sort a list of nodes according to some criteria.
     * 
     * @param nodes
     *            A list of nodes which are supposed to be placed on the same radius.
     */
    void sort(List<ElkNode> nodes);

    /**
     * Initialize the sorter by using the root node if necessary.
     * 
     * @param root
     *            The root node of the graph.
     */
    void initialize(ElkNode root);
}
