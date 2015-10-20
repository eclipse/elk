/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.layered;

import java.util.List;

import org.eclipse.elk.layered.graph.LGraph;
import org.eclipse.elk.layered.p4nodes.LinearSegmentsNodePlacer.LinearSegment;
import org.eclipse.elk.layered.p5edges.OrthogonalRoutingGenerator.HyperNode;

/**
 * A utility class for debug output of KLay Layered.
 * Currently it delegates to both {@link JsonDebugUtil} and {@link DotDebugUtil}.
 * 
 * @author csp
 */
public final class DebugUtil {

    private DebugUtil() {
    };
    
    /**
     * Output a representation of the given graph in dot and JSON format.
     * 
     * @param lgraph
     *            the layered graph
     * @param slotIndex
     *            the slot before whose execution the graph is written.
     * @param name
     *            the name the slot before whose execution the graph is written.
     * @see {@link DotDebugUtil#writeDebugGraph(LGraph, int)}
     * @see {@link JsonDebugUtil#writeDebugGraph(LGraph, int)}
     */
    public static void writeDebugGraph(final LGraph lgraph, final int slotIndex, final String name) {
        DotDebugUtil.writeDebugGraph(lgraph, slotIndex, name);
        JsonDebugUtil.writeDebugGraph(lgraph, slotIndex, name);
    }

    /**
     * Writes a debug graph for the given linear segments and their dependencies.
     * 
     * @param layeredGraph
     *            the layered graph.
     * @param segmentList
     *            the list of linear segments.
     * @param outgoingList
     *            the list of successors for each linear segment.
     * @see {@link DotDebugUtil#writeDebugGraph(LGraph, List, List)}
     * @see {@link JsonDebugUtil#writeDebugGraph(LGraph, List, List)}
     */
    public static void writeDebugGraph(final LGraph layeredGraph,
            final List<LinearSegment> segmentList, final List<List<LinearSegment>> outgoingList) {
        DotDebugUtil.writeDebugGraph(layeredGraph, segmentList, outgoingList);
        JsonDebugUtil.writeDebugGraph(layeredGraph, segmentList, outgoingList);
    }

    /**
     * Writes a debug graph for the given list of hypernodes.
     * 
     * @param layeredGraph
     *            the layered graph
     * @param layerIndex
     *            the currently processed layer's index
     * @param hypernodes
     *            a list of hypernodes
     * @param debugPrefix
     *            prefix of debug output files
     * @param label
     *            a label to append to the output files
     * @see {@link DotDebugUtil#writeDebugGraph(LGraph, int, List, String, String)}
     * @see {@link JsonDebugUtil#writeDebugGraph(LGraph, int, List, String, String)}
     */
    public static void writeDebugGraph(final LGraph layeredGraph, final int layerIndex,
            final List<HyperNode> hypernodes, final String debugPrefix, final String label) {
        DotDebugUtil.writeDebugGraph(layeredGraph, layerIndex, hypernodes, debugPrefix, label);
        JsonDebugUtil.writeDebugGraph(layeredGraph, layerIndex, hypernodes, debugPrefix, label);
    }

}
