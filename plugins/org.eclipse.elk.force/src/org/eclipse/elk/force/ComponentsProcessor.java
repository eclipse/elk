/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.force;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.force.graph.FBendpoint;
import org.eclipse.elk.force.graph.FEdge;
import org.eclipse.elk.force.graph.FGraph;
import org.eclipse.elk.force.graph.FLabel;
import org.eclipse.elk.force.graph.FNode;
import org.eclipse.elk.force.properties.Properties;

import com.google.common.collect.Lists;

/**
 * Splits an input graph into connected components and recombines those components after layout.
 * 
 * <h4>Splitting into components</h4>
 * <dl>
 *   <dt>Precondition:</dt><dd>a graph.</dd>
 *   <dt>Postcondition:</dt><dd>a list of graphs that represent the connected components of
 *     the input graph.</dd>
 * </dl>
 * 
 * <h4>Packing components</h4>
 * <dl>
 *   <dt>Precondition:</dt><dd>a list of graphs with complete layout and layer assignment.</dd>
 *   <dt>Postcondition:</dt><dd>a single graph.</dd>
 * </dl>
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public final class ComponentsProcessor {
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Splitting
    
    /**
     * Split the given graph into its connected components.
     * 
     * @param graph an input graph.
     * @return a list of components that can be processed one by one.
     */
    public List<FGraph> split(final FGraph graph) {
        Boolean separate = graph.getProperty(LayoutOptions.SEPARATE_CC);
        if (separate == null || separate.booleanValue()) {
            boolean[] visited = new boolean[graph.getNodes().size()];
            List<FEdge>[] incidence = buildIncidenceLists(graph);
            
            // perform DFS starting on each node, collecting connected components
            List<FGraph> components = new LinkedList<FGraph>();
            for (FNode node : graph.getNodes()) {
                FGraph comp = dfs(node, null, visited, incidence);
                if (comp != null) {
                    comp.copyProperties(graph);
                    components.add(comp);
                }
            }
            
            // redistribute identifier numbers to each component
            if (components.size() > 1) {
                for (FGraph comp : components) {
                    int id = 0;
                    for (FNode node : comp.getNodes()) {
                        node.id = id++;
                    }
                }
            }
            return components;
        }
        return Lists.newArrayList(graph);
    }

    /**
     * Creates and returns the incidence list that for each node lists the incident edges.
     * 
     * @param graph a force graph.
     * 
     */
    @SuppressWarnings("unchecked")
    private List<FEdge>[] buildIncidenceLists(final FGraph graph) {
        int n = graph.getNodes().size();
        List<FEdge>[] incidence = new List[n];
        
        // create incidence lists
        for (FNode node : graph.getNodes()) {
            incidence[node.id] = new LinkedList<FEdge>();
        }
        
        // add edges to incidence lists
        for (FEdge edge : graph.getEdges()) {
            incidence[edge.getSource().id].add(edge);
            incidence[edge.getTarget().id].add(edge);
        }
        
        return incidence;
    }
    
    /**
     * Perform a DFS starting on the given node and collect all nodes that are found in the
     * corresponding connected component.
     * 
     * @param node a node.
     * @param graph a graph representing a connected component, or {@code null}.
     * @param visited boolean indicating for each node whether it was already visited ({@code true})
     *                or not.
     * @param incidence list of incident edges for each node.
     * @return the connected component, or {@code null} if the node was already visited.
     */
    private FGraph dfs(final FNode node, final FGraph graph, final boolean[] visited,
            final List<FEdge>[] incidence) {
        
        if (!visited[node.id]) {
            visited[node.id] = true;
            FGraph component = graph;
            if (component == null) {
                component = new FGraph();
            }
            component.getNodes().add(node);
            for (FEdge edge : incidence[node.id]) {
                if (edge.getSource() != node) {
                    dfs(edge.getSource(), component, visited, incidence);
                }
                if (edge.getTarget() != node) {
                    dfs(edge.getTarget(), component, visited, incidence);
                }
                component.getEdges().add(edge);
                component.getLabels().addAll(edge.getLabels());
            }
            return component;
        }
        return null;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Recombination
    
    /**
     * Pack the given components into a single graph.
     * 
     * @param components a list of components.
     * @return a single graph that contains all components.
     */
    public FGraph recombine(final List<FGraph> components) {
        if (components.size() == 1) {
            return components.get(0);
        } else if (components.size() <= 0) {
            return new FGraph();
        }
        
        // assign priorities and sizes
        for (FGraph graph : components) {
            int priority = 0;
            double minx = Integer.MAX_VALUE, miny = Integer.MAX_VALUE,
                    maxx = Integer.MIN_VALUE, maxy = Integer.MIN_VALUE;
            for (FNode node : graph.getNodes()) {
                Integer p = node.getProperty(LayoutOptions.PRIORITY);
                if (p != null) {
                    priority += p;
                }
                minx = Math.min(minx, node.getPosition().x);
                miny = Math.min(miny, node.getPosition().y);
                maxx = Math.max(maxx, node.getPosition().x + node.getSize().x);
                maxy = Math.max(maxy, node.getPosition().y + node.getSize().y);
            }
            graph.setProperty(Properties.PRIORITY, priority);
            graph.setProperty(Properties.BB_UPLEFT, new KVector(minx, miny));
            graph.setProperty(Properties.BB_LOWRIGHT, new KVector(maxx, maxy));
        }

        // sort the components by their priority and size
        Collections.sort(components, new Comparator<FGraph>() {
            public int compare(final FGraph graph1, final FGraph graph2) {
                int prio = graph2.getProperty(Properties.PRIORITY)
                        - graph1.getProperty(Properties.PRIORITY);
                if (prio == 0) {
                    KVector size1 = graph1.getProperty(Properties.BB_LOWRIGHT).clone().sub(
                            graph1.getProperty(Properties.BB_UPLEFT));
                    KVector size2 = graph2.getProperty(Properties.BB_LOWRIGHT).clone().sub(
                            graph2.getProperty(Properties.BB_UPLEFT));
                    return Double.compare(size1.x * size1.y, size2.x * size2.y);
                }
                return prio;
            }
        });
        
        FGraph result = new FGraph();
        result.copyProperties(components.get(0));
        
        // determine the maximal row width by the maximal box width and the total area
        double maxRowWidth = 0.0f;
        double totalArea = 0.0f;
        for (FGraph graph : components) {
            KVector size = graph.getProperty(Properties.BB_LOWRIGHT).clone().sub(
                    graph.getProperty(Properties.BB_UPLEFT));
            maxRowWidth = Math.max(maxRowWidth, size.x);
            totalArea += size.x * size.y;
        }
        maxRowWidth = Math.max(maxRowWidth, (float) Math.sqrt(totalArea)
                * result.getProperty(Properties.ASPECT_RATIO));
        double spacing = result.getProperty(Properties.SPACING).doubleValue();

        // place nodes iteratively into rows
        double xpos = 0, ypos = 0, highestBox = 0, broadestRow = spacing;
        for (FGraph graph : components) {
            KVector size = graph.getProperty(Properties.BB_LOWRIGHT).clone().sub(
                    graph.getProperty(Properties.BB_UPLEFT));
            if (xpos + size.x > maxRowWidth) {
                // place the graph into the next row
                xpos = 0;
                ypos += highestBox + spacing;
                highestBox = 0;
            }
            moveGraph(result, graph, xpos, ypos);
            broadestRow = Math.max(broadestRow, xpos + size.x);
            highestBox = Math.max(highestBox, size.y);
            xpos += size.x + spacing;
        }
        
        return result;
    }
    
    /**
     * Move the source graph into the destination graph using a specified offset.
     * 
     * @param destGraph the destination graph.
     * @param sourceGraph the source graph.
     * @param offsetx x coordinate offset.
     * @param offsety y coordinate offset.
     */
    private void moveGraph(final FGraph destGraph, final FGraph sourceGraph,
            final double offsetx, final double offsety) {
        
        KVector graphOffset = new KVector(offsetx, offsety);
        graphOffset.sub(sourceGraph.getProperty(Properties.BB_UPLEFT));
        
        for (FNode node : sourceGraph.getNodes()) {
            node.getPosition().add(graphOffset);
            destGraph.getNodes().add(node);
        }
        
        for (FEdge edge : sourceGraph.getEdges()) {
            for (FBendpoint bendpoint : edge.getBendpoints()) {
                bendpoint.getPosition().add(graphOffset);
            }
            destGraph.getEdges().add(edge);
        }
        
        for (FLabel label : sourceGraph.getLabels()) {
            label.getPosition().add(graphOffset);
            destGraph.getLabels().add(label);
        }
    }
    
}
