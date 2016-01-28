/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
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
import java.util.Iterator;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.p4nodes.LinearSegmentsNodePlacer.LinearSegment;
import org.eclipse.elk.alg.layered.p5edges.OrthogonalRoutingGenerator.Dependency;
import org.eclipse.elk.alg.layered.p5edges.OrthogonalRoutingGenerator.HyperNode;
import org.eclipse.elk.alg.layered.properties.InternalProperties;

/**
 * A utility class for debugging of KLay Layered.
 * 
 * @author msp
 * @author cds
 */
public final class DotDebugUtil {
    
    /**
     * Hidden constructor to avoid instantiation.
     */
    private DotDebugUtil() { }


    /**
     * Output a representation of the given graph in dot format. The following conventions are used:
     * <ul>
     * <li>Standard nodes are drawn as rectangles.</li>
     * <li>Dummy nodes are drawn as ellipses.</li>
     * <li>Nodes have a color that depends on their node type. (yellow for {@code LONG_EDGE},
     * turquoise for {@code ODD_PORT_SIDE}, dark blue for {@code NORTH_SOUTH_PORT})</li>
     * </ul>
     * 
     * @param lgraph
     *            the layered graph
     * @param slotIndex
     *            the slot before whose execution the graph is written.
     * @param name
     *            the name the slot before whose execution the graph is written.
     */
    public static void writeDebugGraph(final LGraph lgraph, final int slotIndex, final String name) {
        try {
            Writer writer = createWriter(lgraph, slotIndex, name);
                    
            // Begin the digraph
            writer.write("digraph {\n");
            
            // Digraph options
            writer.write("    rankdir=LR;\n");
            
            // Write layerless nodes and edges
            writeLayer(writer, -1, lgraph.getLayerlessNodes());
            
            // Go through the layers
            int layerNumber = -1;
            for (Layer layer : lgraph) {
                layerNumber++;
                
                // Write the nodes and edges
                writeLayer(writer, layerNumber, layer.getNodes());
            }
            
            // Close the digraph. And the writer.
            writer.write("}\n");
            writer.close();
        } catch (IOException exception) {
            // Ignore the exception
        }
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
     */
    public static void writeDebugGraph(final LGraph layeredGraph,
            final List<LinearSegment> segmentList, final List<List<LinearSegment>> outgoingList) {

        try {
            Writer writer = createWriter(layeredGraph);
            writer.write("digraph {\n");

            Iterator<LinearSegment> segmentIterator = segmentList.iterator();
            Iterator<List<LinearSegment>> successorsIterator = outgoingList.iterator();

            while (segmentIterator.hasNext()) {
                LinearSegment segment = segmentIterator.next();
                List<LinearSegment> successors = successorsIterator.next();

                writer.write("  " + segment.hashCode() + "[label=\"" + segment + "\"]\n");

                for (LinearSegment successor : successors) {
                    writer.write("  " + segment.hashCode() + "->" + successor.hashCode() + "\n");
                }
            }

            writer.write("}\n");
            writer.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    /**
     * Writes a debug graph for the given list of hypernodes.
     * 
     * @param layeredGraph the layered graph
     * @param layerIndex the currently processed layer's index
     * @param hypernodes a list of hypernodes
     * @param debugPrefix prefix of debug output files
     * @param label a label to append to the output files
     */
    public static void writeDebugGraph(final LGraph layeredGraph, final int layerIndex,
            final List<HyperNode> hypernodes, final String debugPrefix, final String label) {
        
        try {
            Writer writer = createWriter(layeredGraph, layerIndex, debugPrefix, label);
            writer.write("digraph {\n");
            
            // Write hypernode information
            for (HyperNode hypernode : hypernodes) {
                writer.write("  " + hypernode.hashCode() + "[label=\""
                        + hypernode.toString() + "\"]\n");
            }
            
            // Write dependency information
            for (HyperNode hypernode : hypernodes) {
                for (Dependency dependency : hypernode.getOutgoing()) {
                    writer.write("  " + hypernode.hashCode() + "->" + dependency.getTarget().hashCode()
                            + "[label=\"" + dependency.getWeight() + "\"]\n");
                }
            }
            
            writer.write("}\n");
            writer.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    /**
     * Writes the given list of nodes and their edges.
     * 
     * @param writer writer to write to.
     * @param layerNumber the layer number. {@code -1} for layerless nodes.
     * @param nodes the nodes in the layer.
     * @throws IOException if anything goes wrong with the writer.
     */
    private static void writeLayer(final Writer writer, final int layerNumber, final List<LNode> nodes)
            throws IOException {
        
        if (nodes.isEmpty()) {
            return;
        }
        
        // Go through the layer's nodes
        int nodeNumber = -1;
        for (LNode node : nodes) {
            nodeNumber++;
            
            // The node's name in the output is its hash code (unique!)
            writer.write("        " + node.hashCode());
            
            // Options time!
            StringBuffer options = new StringBuffer();
            
            // Label
            options.append("label=\"");
            if (node.getType() == NodeType.NORMAL) {
                // Normal nodes display their name, if any
                if (node.getName() != null) {
                    options.append(node.getName().replace("\"", "\\\"") + " ");
                }
            } else {
                // Dummy nodes show their name (if set), or their node ID
                if (node.getName() != null) {
                    options.append(node.getName().replace("\"", "\\\"") + " ");
                } else {
                    options.append("n_" + node.id + " ");
                }
                if (node.getType() == NodeType.NORTH_SOUTH_PORT) {
                    Object origin = node.getProperty(InternalProperties.ORIGIN);
                    if (origin instanceof LNode) {
                        options.append("(" + ((LNode) origin).toString() + ")");
                    }
                }
            }
            options.append("(" + layerNumber + "," + nodeNumber + ")\",");
            
            // Node type
            if (node.getType() == NodeType.NORMAL) {
                options.append("shape=box,");
            } else {
                options.append("style=\"rounded,filled\",");
                
                String color = node.getType().getColor();
                if (color != null) {
                    options.append("color=\"" + color + "\",");
                }
            }
            
            // Print options, if any
            options.deleteCharAt(options.length() - 1);
            if (options.length() > 0) {
                writer.write("[" + options + "]");
            }
            
            // End the node line
            writer.write(";\n");
        }
        
        // Write the edges
        for (LNode node : nodes) {
            // Go through all edges and output those that have this node as their source
            for (LPort port : node.getPorts()) {
                for (LEdge edge : port.getOutgoingEdges()) {
                    writer.write("    " + node.hashCode() + " -> "
                            + edge.getTarget().getNode().hashCode());
                    writer.write(";\n");
                }
            }
        }
    }

    /**
     * Creates a writer for debug output.
     * 
     * @param layeredGraph
     *            the layered graph.
     * @return a file writer for debug output.
     * @throws IOException
     *             if creating the output file fails.
     */
    private static Writer createWriter(final LGraph layeredGraph) throws IOException {
        String path = getDebugOutputPath();
        new File(path).mkdirs();

        String debugFileName = getDebugOutputFileBaseName(layeredGraph) + "linseg-dep";
        return new FileWriter(new File(path + File.separator + debugFileName + ".dot"));
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
     * @return file writer.
     * @throws IOException
     *             if anything goes wrong.
     */
    private static Writer createWriter(final LGraph graph, final int slotIndex, final String name)
            throws IOException {

        String path = getDebugOutputPath();
        new File(path).mkdirs();

        String debugFileName = getDebugOutputFileBaseName(graph) + "fulldebug-slot"
                        + String.format("%1$02d", slotIndex) + "-" + name;
        return new FileWriter(new File(path + File.separator + debugFileName + ".dot"));
    }
    
    /**
     * Create a writer for debug output.
     * 
     * @param layeredGraph the layered graph
     * @param layerIndex the currently processed layer's index
     * @param debugPrefix prefix of debug output files
     * @param label a label to append to the output files
     * @return a file writer for debug output
     * @throws IOException if creating the output file fails
     */
    private static Writer createWriter(final LGraph layeredGraph, final int layerIndex,
            final String debugPrefix, final String label) throws IOException {
        String path = getDebugOutputPath();
        new File(path).mkdirs();
        
        String debugFileName = getDebugOutputFileBaseName(layeredGraph)
                + debugPrefix + "-l" + layerIndex + "-" + label;
        return new FileWriter(new File(path + File.separator + debugFileName + ".dot"));
    }

    /**
     * Returns the path for debug output graphs.
     * 
     * @return the path for debug output graphs, without trailing separator.
     */
    private static String getDebugOutputPath() {
        String path = System.getProperty("user.home");
        if (path.endsWith(File.separator)) {
            path += "tmp" + File.separator + "klay";
        } else {
            path += File.separator + "tmp" + File.separator + "klay";
        }

        return path;
    }

    /**
     * Returns the beginning of the file name used for debug output graphs while layouting the given
     * layered graph. This will look something like {@code "143293-"}.
     * 
     * @param graph
     *            the graph to return the base debug file name for.
     * @return the base debug file name for the given graph.
     */
    private static String getDebugOutputFileBaseName(final LGraph graph) {
        return Integer.toString(graph.hashCode()
                & ((1 << (Integer.SIZE / 2)) - 1))
                + "-";
    }

}
