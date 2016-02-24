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
package org.eclipse.elk.alg.layered.intermediate;

import java.util.List;
import java.util.ListIterator;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.Properties;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Lists;

/**
 * Intermediate processor that splits path-like graphs into 
 * multiple sub-paths to improve the overall aspect ratio of the drawing.
 * 
 * <p>
 * This is a rather problem-specific intermediate processor 
 * that works only on a very small subset of graphs, namely 
 * paths that have very few branches.
 * Also, it only works in conjunction with the 
 * {@link org.eclipse.elk.alg.layered.p2layers.LongestPathLayerer}.
 * </p>
 * 
 * <p>
 * What we do is the following. After layering and crossing minimization
 * we determine into how many "rows" we want to split the path.
 * Let n denote the number of nodes of each row. We start moving 
 * the node in layer n+1 into layer 1, node n+2 into layer 2 and so on.
 * While doing this we have to assure that the order within each 
 * layer is correct, have to insert dummy nodes for the edge that now 
 * spans from layer n+1 to layer 1, and have to treat inverted ports
 * specifically.
 * </p>
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>A layering has been calculated by the longest
 *   path layerer and the crossing minimization has finished.</dd>
 *   <dt>Postcondition:</dt><dd>Long paths are split such that the 
 *   aspect ratio is closer to the currently available area.</dd>
 *   <dt>Slots:</dt><dd>Before phase 4.</dd>
 * </dl>
 * 
 * @author uru
 */
public class SausageFolding implements ILayoutProcessor {

    private double spacing = 0;
    private double inLayerSpacing = 0;

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph graph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Sausage Folding", 1);
        
        spacing = graph.getProperty(Properties.SPACING_NODE).doubleValue();
        inLayerSpacing = spacing * graph.getProperty(Properties.IN_LAYER_SPACING_FACTOR);

        // determine the maximal dimensions of layers
        double maxHeight = determineMaximalHeight(graph); // sum of heights of nodes in the layer
        int longestPath = graph.getLayers().size();
        double maxWidth = determineMaximalWidth(graph); // maximum node in one layer
        
        double sumWidth = longestPath * maxWidth;
        
        // since the graph direction may be horizontal (default) or vertical,
        //  the desired aspect ration must be adjusted correspondingly
        //  (Klay internally always assumes left to right, however the
        //  aspect ratio is not adjusted during graph import)
        double desiredAR;
        final Direction dir = graph.getProperty(LayoutOptions.DIRECTION);
        if (dir == Direction.LEFT || dir == Direction.RIGHT || dir == Direction.UNDEFINED) {
            desiredAR = graph.getProperty(Properties.ASPECT_RATIO).doubleValue();
        } else {
            desiredAR = 1 / graph.getProperty(Properties.ASPECT_RATIO);
        }
        
        double currentAR = sumWidth / maxHeight;
        if (desiredAR > currentAR) {
            // nothing to do for us
            progressMonitor.done();
            return;
        }
        
        // figure out a reasonable width and height for the desired aspect ratio
        int rows = 0;
        double dist = Double.MAX_VALUE, lastDist = Double.MAX_VALUE;
        do {
            rows++;
            currentAR = (sumWidth / rows) / (maxHeight * rows);
            lastDist = dist;
            dist = Math.abs(currentAR - desiredAR); 
        } while (currentAR > desiredAR);
        
        // select the width/height combination which is closer to the desired aspect ratio
        if (lastDist < dist) {
            rows--;
        }
        
        // System.out.println("Max Height: " + maxHeight);
        // System.out.println("Max Width: " + maxWidth);
        // System.out.println("LongestPath: " + longestPath);
        // System.out.println("SumWidth: " + sumWidth);
        // System.out.println("Desired AR: " + desiredAR);
        // System.out.println("Current AR:" + currentAR);
        // System.out.println("Rows: " + rows);
        
        int nodesPerRow = longestPath / Math.max(1, rows);
        int index = nodesPerRow;
        int newIndex = index;
        boolean wannaRevert = true;
        
        while (index < longestPath) {

            Layer l = graph.getLayers().get(index);

            // we only allow to reverse an edge when there is only one pair 
            // of nodes that is connected by 1 or more edges
            boolean reversalAllowed = true;
            LNode n1 = null, n2 = null;
            check: 
            for (LNode tgt: l.getNodes()) {
                for (LEdge e : tgt.getIncomingEdges()) {
                    // check for different source
                    if (n1 != null && n1 != tgt) {
                        reversalAllowed = false;
                        break check;
                    }
                    n1 = tgt;
                    // check for different target
                    LNode src = e.getSource().getNode();
                    if (n2 != null && n2 != src) {
                        reversalAllowed = false;
                        break check;
                    }
                    n2 = src;
                }
            }
            
            // start new column (unless we are not allowed to)
            if (wannaRevert && reversalAllowed) {
                newIndex = 0;
                wannaRevert = false;
            }
            
            if (index != newIndex) {
                Layer newLayer = graph.getLayers().get(newIndex);

                for (LNode n : Lists.newArrayList(l.getNodes())) {
                    n.setLayer(newLayer.getNodes().size(), newLayer);

                    if (newIndex == 0) {
                        for (LEdge e : Lists.newArrayList(n.getIncomingEdges())) {
                            e.reverse(graph, true);
                            graph.setProperty(InternalProperties.CYCLIC, true);

                            // insert proper dummy nodes for the newly created long edge
                            insertDummies(graph, e);
                            
                            // handle the newly created inverted ports
                            List<LNode> foo = Lists.newArrayList();
                            createWestPortSideDummies(graph, e.getSource(), e, foo);
                            for (LNode no : foo) {
                                no.setLayer(newLayer.getNodes().size() - 1, newLayer);
                            }
                        }
                    }
                }
            }

            if (newIndex >= nodesPerRow) {
                wannaRevert = true;
            }
            
            newIndex++;
            index++;
        }

        // remove old layers
        ListIterator<Layer> it = graph.getLayers().listIterator();
        while (it.hasNext()) {
            Layer l = it.next();
            if (l.getNodes().isEmpty()) {
                it.remove();
            }
        }
        
        progressMonitor.done();
    }

    private double determineMaximalHeight(final LGraph graph) {
        double maxH = 0;

        for (Layer l : graph.getLayers()) {
            double lH = 0;
            for (LNode n : l.getNodes()) {
                lH += n.getSize().y + n.getMargin().bottom + n.getMargin().top + inLayerSpacing;
            }
            // layers cannot be empty
            lH -= inLayerSpacing;

            maxH = Math.max(maxH, lH);
        }

        return maxH;
    }

    private double determineMaximalWidth(final LGraph graph) {
        double maxW = 0;

        for (Layer l : graph.getLayers()) {
            for (LNode n : l.getNodes()) {
                double nW = n.getSize().x + n.getMargin().right + n.getMargin().left + spacing;
                maxW = Math.max(maxW, nW);
            }

        }
        return maxW;
    }

    /**
     * Inserts long edge dummies for the passed edge.
     */
    private void insertDummies(final LGraph layeredGraph, final LEdge originalEdge) {
        
        LEdge edge = originalEdge;
        LPort targetPort = edge.getTarget();
        LNode src = edge.getSource().getNode();
        LNode tgt = edge.getTarget().getNode();
        
        int srcIndex = src.getLayer().getIndex();
        int tgtIndex = tgt.getLayer().getIndex();
        
        for (int i = srcIndex; i < tgtIndex; i++) {
            
            // Create dummy node
            LNode dummyNode = new LNode(layeredGraph);
            dummyNode.setType(NodeType.LONG_EDGE);
            
            dummyNode.setProperty(InternalProperties.ORIGIN, edge);
            dummyNode.setProperty(LayoutOptions.PORT_CONSTRAINTS,
                    PortConstraints.FIXED_POS);
            
            Layer nextLayer = layeredGraph.getLayers().get(i + 1);
            dummyNode.setLayer(nextLayer);

            // Set thickness of the edge
            float thickness = edge.getProperty(LayoutOptions.EDGE_THICKNESS);
            if (thickness < 0) {
                thickness = 0;
                edge.setProperty(LayoutOptions.EDGE_THICKNESS, thickness);
            }
            dummyNode.getSize().y = thickness;
            double portPos = Math.floor(thickness / 2);

            // Create dummy input and output ports
            LPort dummyInput = new LPort();
            dummyInput.setSide(PortSide.WEST);
            dummyInput.setNode(dummyNode);
            dummyInput.getPosition().y = portPos;

            LPort dummyOutput = new LPort();
            dummyOutput.setSide(PortSide.EAST);
            dummyOutput.setNode(dummyNode);
            dummyOutput.getPosition().y = portPos;

            edge.setTarget(dummyInput);

            // Create a dummy edge
            LEdge dummyEdge = new LEdge();
            dummyEdge.copyProperties(edge);
            dummyEdge.setProperty(LayoutOptions.JUNCTION_POINTS, null);
            dummyEdge.setSource(dummyOutput);
            dummyEdge.setTarget(targetPort);

            setDummyProperties(dummyNode, edge, dummyEdge);
            
            edge = dummyEdge;
        }
        
    }
    
    /**
     * Copied from {@link LongEdgeSplitter}.
     * 
     * Sets the source and target properties on the given dummy node.
     * 
     * @param dummy the dummy node.
     * @param inEdge the edge going into the dummy node.
     * @param outEdge the edge going out of the dummy node.
     */
    private void setDummyProperties(final LNode dummy, final LEdge inEdge, final LEdge outEdge) {
        LNode inEdgeSourceNode = inEdge.getSource().getNode();
        
        if (inEdgeSourceNode.getType() == NodeType.LONG_EDGE) {
            // The incoming edge originates from a long edge dummy node, so we can
            // just copy its properties
            dummy.setProperty(InternalProperties.LONG_EDGE_SOURCE,
                    inEdgeSourceNode.getProperty(InternalProperties.LONG_EDGE_SOURCE));
            dummy.setProperty(InternalProperties.LONG_EDGE_TARGET,
                    inEdgeSourceNode.getProperty(InternalProperties.LONG_EDGE_TARGET));
        } else {
            // The source is the input edge's source port, the target is the output
            // edge's target port
            dummy.setProperty(InternalProperties.LONG_EDGE_SOURCE, inEdge.getSource());
            dummy.setProperty(InternalProperties.LONG_EDGE_TARGET, outEdge.getTarget());
        }
    }
    
    /**
     * Copied from {@link InvertedPortProcessor}.
     * 
     * Creates the necessary dummy nodes for an output port on the west side of a node,
     * provided that the edge connects two different nodes.
     * 
     * @param layeredGraph the layered graph
     * @param westwardPort the offending port.
     * @param edge the edge connected to the port.
     * @param layerNodeList list of unassigned nodes belonging to the layer of the node the
     *                      port belongs to. The new dummy node is added to this list and
     *                      must be assigned to the layer later.
     */
    private void createWestPortSideDummies(final LGraph layeredGraph, final LPort westwardPort,
            final LEdge edge, final List<LNode> layerNodeList) {
        
        if (edge.getTarget().getNode() == westwardPort.getNode()) {
            return;
        }
        
        // Dummy node in the same layer
        LNode dummy = new LNode(layeredGraph);
        dummy.setType(NodeType.LONG_EDGE);
        
        dummy.setProperty(InternalProperties.ORIGIN, edge);
        dummy.setProperty(LayoutOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_POS);
        
        layerNodeList.add(dummy);
        
        LPort dummyInput = new LPort();
        dummyInput.setNode(dummy);
        dummyInput.setSide(PortSide.WEST);
        
        LPort dummyOutput = new LPort();
        dummyOutput.setNode(dummy);
        dummyOutput.setSide(PortSide.EAST);
        
        // Reroute the original edge
        LPort originalTarget = edge.getTarget();
        edge.setTarget(dummyInput);
        
        // Connect the dummy with the original port
        LEdge dummyEdge = new LEdge();
        dummyEdge.copyProperties(edge);
        dummyEdge.setProperty(LayoutOptions.JUNCTION_POINTS, null);
        dummyEdge.setSource(dummyOutput);
        dummyEdge.setTarget(originalTarget);
        
        // Set LONG_EDGE_SOURCE and LONG_EDGE_TARGET properties on the LONG_EDGE dummy
        setLongEdgeSourceAndTarget(dummy, dummyInput, dummyOutput, westwardPort);
    }
    
    /**
     * Copied from {@link InvertedPortProcessor}.
     * 
     * Properly sets the {@link org.eclipse.elk.alg.layered.properties.Properties#LONG_EDGE_SOURCE}
     * and {@link org.eclipse.elk.alg.layered.properties.Properties#LONG_EDGE_TARGET} properties for
     * the given long edge dummy. This is required for the
     * {@link org.eclipse.elk.alg.layered.intermediate.HyperedgeDummyMerger} to work
     * correctly.
     * 
     * @param longEdgeDummy the long edge dummy whose properties to set.
     * @param dummyInputPort the dummy node's input port.
     * @param dummyOutputPort the dummy node's output port.
     * @param oddPort the odd port that prompted the dummy to be created.
     */
    private void setLongEdgeSourceAndTarget(final LNode longEdgeDummy, final LPort dummyInputPort,
            final LPort dummyOutputPort, final LPort oddPort) {
        
        // There's exactly one edge connected to the input and output port
        LPort sourcePort = dummyInputPort.getIncomingEdges().get(0).getSource();
        LNode sourceNode = sourcePort.getNode();
        NodeType sourceNodeType = sourceNode.getType();
        
        LPort targetPort = dummyOutputPort.getOutgoingEdges().get(0).getTarget();
        LNode targetNode = targetPort.getNode();
        NodeType targetNodeType = targetNode.getType();
        
        // Set the LONG_EDGE_SOURCE property
        if (sourceNodeType == NodeType.LONG_EDGE) {
            // The source is a LONG_EDGE node; use its LONG_EDGE_SOURCE
            longEdgeDummy.setProperty(InternalProperties.LONG_EDGE_SOURCE,
                    sourceNode.getProperty(InternalProperties.LONG_EDGE_SOURCE));
        } else {
            // The target is the original node; use it
            longEdgeDummy.setProperty(InternalProperties.LONG_EDGE_SOURCE, sourcePort);
        }

        // Set the LONG_EDGE_TARGET property
        if (targetNodeType == NodeType.LONG_EDGE) {
            // The target is a LONG_EDGE node; use its LONG_EDGE_TARGET
            longEdgeDummy.setProperty(InternalProperties.LONG_EDGE_TARGET,
                    targetNode.getProperty(InternalProperties.LONG_EDGE_TARGET));
        } else {
            // The target is the original node; use it
            longEdgeDummy.setProperty(InternalProperties.LONG_EDGE_TARGET, targetPort);
        }
    }
    
}
