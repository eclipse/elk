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
package org.eclipse.elk.alg.layered;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.p4nodes.LinearSegmentsNodePlacer.LinearSegment;
import org.eclipse.elk.alg.layered.p5edges.OrthogonalRoutingGenerator.HyperNode;
import org.eclipse.elk.core.util.ElkUtil;

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

    /**
     * Creates a writer for debug output.
     * 
     * @param layeredGraph
     *            the layered graph.
     * @param extension
     *            file extension, including leading dot
     * @return a file writer for debug output.
     * @throws IOException
     *             if creating the output file fails.
     */
    static Writer createWriter(final LGraph layeredGraph, final String extension) throws IOException {
        String path = ElkUtil.debugFolderPath("layered");
        new File(path).mkdirs();

        String debugFileName = getDebugOutputFileBaseName(layeredGraph) + "linseg-dep" + extension;
        return new FileWriter(new File(path + debugFileName));
    }
    
    /**
     * Creates a writer for the given graph. The file name to be written to is assembled from the
     * graph's hash code and the slot index.
     * 
     * @param graph
     *            the graph to be written.
     * @param slotIndex
     *            the slot before whose execution the graph is written.
     * @param name
     *            the name the slot before whose execution the graph is written.
     * @param extension
     *            file extension, including leading dot
     * @return file writer.
     * @throws IOException
     *             if anything goes wrong.
     */
    static Writer createWriter(final LGraph graph, final int slotIndex, final String name, final String extension)
            throws IOException {

        String path = ElkUtil.debugFolderPath("layered");
        new File(path).mkdirs();

        String debugFileName = getDebugOutputFileBaseName(graph) + "fulldebug-slot"
                        + String.format("%1$02d", slotIndex) + "-" + name + extension;
        return new FileWriter(new File(path + debugFileName));
    }
    
    /**
     * Create a writer for debug output.
     * 
     * @param layeredGraph
     *            the layered graph
     * @param layerIndex
     *            the currently processed layer's index
     * @param debugPrefix
     *            prefix of debug output files
     * @param label
     *            a label to append to the output files
     * @param extension
     *            file extension, including leading dot
     * @return a file writer for debug output
     * @throws IOException
     *             if creating the output file fails
     */
    static Writer createWriter(final LGraph layeredGraph, final int layerIndex, final String debugPrefix,
            final String label, final String extension) throws IOException {
        
        String path = ElkUtil.debugFolderPath("layered");
        new File(path).mkdirs();
        
        String debugFileName = getDebugOutputFileBaseName(layeredGraph)
                + debugPrefix + "-l" + layerIndex + "-" + label + extension;
        return new FileWriter(new File(path + debugFileName));
    }

    /**
     * Returns the beginning of the file name used for debug output graphs while layouting the given
     * layered graph. This will look something like {@code "143293-"}.
     * 
     * @param graph
     *            the graph to return the base debug file name for.
     * @return the base debug file name for the given graph.
     */
    static String getDebugOutputFileBaseName(final LGraph graph) {
        return Integer.toString(graph.hashCode()
                & ((1 << (Integer.SIZE / 2)) - 1))
                + "-";
    }

}
