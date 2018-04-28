/*******************************************************************************
 * Copyright (c) 2014, 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.common.nodespacing;

import org.eclipse.elk.core.util.adapters.ElkGraphAdapters;
import org.eclipse.elk.core.util.adapters.GraphAdapters.GraphAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.NodeAdapter;

/**
 * Entry points to apply several methods for node dimension calculation, including positioning of
 * labels, ports, etc.
 */
public final class NodeMicroLayout {

    /**
     * Private constructor - utility class.
     */
    private NodeMicroLayout() { }

    /**
     * Performs micro layout for all of the graph's nodes. That is, this method sorts ports, 
     * positions labels and ports, computes the nodes' sizes and their margins.
     * 
     * @see {@link #sortPortLists(GraphAdapter)}
     * @see {@link #positionLabelsAndPortsAndComputeNodeSizes(GraphAdapter)}
     * @see {@link #calculateNodeMargins(GraphAdapter)}
     * 
     * @param adapter
     *            an instance of an adapter for the passed graph's type.
     * @param <T>
     *            the graphs type, e.g. a root ElkNode (see {@link ElkGraphAdapters}.
     */
    public static <T> void executeAll(final GraphAdapter<T> adapter) {
        sortPortLists(adapter);
        positionLabelsAndPortsAndComputeNodeSizes(adapter);
        calculateNodeMargins(adapter);
    }
    
    /**
     * Positions the labels and ports of the graph's nodes. Make sure that the port lists
     * are sorted properly.
     * 
     * @see #sortPortLists(GraphAdapter)
     * 
     * @param adapter
     *            an instance of an adapter for the passed graph's type.
     * @param <T>
     *            the graphs type, e.g. a root ElkNode (see {@link ElkGraphAdapters}.
     */
    public static <T> void positionLabelsAndPortsAndComputeNodeSizes(final GraphAdapter<T> adapter) {
        LabelAndPortPositionerAndNodeSizeCalculator.process(adapter);
    }

    /**
     * Calculates node margins for the nodes of the passed graph.
     * 
     * <p>
     *   If certain functionality has to be excluded, e.g., the sizes of ports should be excluded from
     *   the calculated margins, use {@link #getNodeMarginCalculator(GraphAdapter)} to retrieve an
     *   instance of a {@link NodeMarginCalculator} that can be configured further.
     * </p>
     * 
     * @param adapter
     *            an instance of an adapter for the passed graph's type.
     * @param <T>
     *            the graphs type, e.g. a root ElkNode (see {@link ElkGraphAdapters}.
     */
    public static <T> void calculateNodeMargins(final GraphAdapter<T> adapter) {
        NodeMarginCalculator calcu = new NodeMarginCalculator(adapter);
        calcu.process();
    }

    /**
     * <p>
     *   Returns a configurable {@link NodeMarginCalculator} that can be executed using the
     *   {@link NodeMarginCalculator#process()} method.
     * </p>
     * 
     * <p>
     *   Note that {@link #calculateNodeMargins(GraphAdapter)} can be used if no detailed
     *   configuration is required.
     * </p>
     * 
     * @param adapter
     *            an instance of an adapter for the passed graph's type.
     * @param <T>
     *            the graphs type, e.g. a root ElkNode (see {@link ElkGraphAdapters}.
     * @return an instance of a {@link NodeMarginCalculator} that can be configured to specific
     *         needs.
     */
    public static <T> NodeMarginCalculator getNodeMarginCalculator(final GraphAdapter<T> adapter) {
        return new NodeMarginCalculator(adapter);
    }

    /**
     * Sorts the port lists of all nodes of the graph clockwise. More precisely, ports are sorted by
     * side (north, east, south, west) in clockwise order, beginning at the top left corner.
     * 
     * @param adapter
     *            an instance of an adapter for the passed graph's type.
     * @param <T>
     *            the graphs type, e.g. a root KNode
     */
    public static <T> void sortPortLists(final GraphAdapter<T> adapter) {
        // Iterate through the nodes of all layers
        for (NodeAdapter<?> node : adapter.getNodes()) {
            node.sortPortList();
        }
    }

}
