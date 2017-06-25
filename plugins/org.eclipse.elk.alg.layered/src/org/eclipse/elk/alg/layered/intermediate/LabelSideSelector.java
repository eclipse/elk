/*******************************************************************************
 * Copyright (c) 2012, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.EdgeLabelSideSelection;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.nodespacing.LabelSide;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * <p>
 * This intermediate processor is used to select the side of port and edge labels. It is chosen
 * between the sides {@code UP} and {@code DOWN} based on different strategies selected by a layout
 * option.
 * </p>
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>a properly layered graph with fixed port orders.</dd>
 *     <dd>center edge labels are not attached to edges, but to label dummy nodes.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>the placement side is chosen for each label, and each label is annotated accordingly.
 *       (in the case of center edge labels, the representing dummy node is annotated with the
 *       placement side.)</dd>
 *     <dd>label dummy nodes have their ports placed such that they extend above or below their edge,
 *       depending on the side of their label.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 4.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link HyperedgeDummyMerger}</dd>
 *     <dd>{@link InLayerConstraintProcessor}</dd>
 *     <dd>{@link SubgraphOrderingProcessor}</dd>
 * </dl>
 * 
 * @author jjc
 * @kieler.design proposed by cds
 * @kieler.rating proposed yellow by cds
 */
public final class LabelSideSelector implements ILayoutProcessor<LGraph> {

    private static final LabelSide DEFAULT_LABEL_SIDE = LabelSide.BELOW;

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        EdgeLabelSideSelection mode = layeredGraph.getProperty(LayeredOptions.EDGE_LABELS_SIDE_SELECTION);
        monitor.begin("Label side selection (" + mode + ")", 1);
        
        // Calculate all label sides depending on the given strategy
        Iterable<LNode> nodes = Iterables.concat(layeredGraph);
        
        switch (mode) {
        case ALWAYS_UP:
            alwaysUp(nodes);
            break;
        case ALWAYS_DOWN:
            alwaysDown(nodes);
            break;
        case DIRECTION_UP:
            directionUp(nodes);
            break;
        case DIRECTION_DOWN:
            directionDown(nodes);
            break;
        case SMART:
            smart(nodes);
            break;
        }

        // Iterate over all ports and check that all ports with labels have label sides assigned.
        // Also, move the ports of label dummy nodes such that the dummy occupies the space its label
        // will later occupy.
        for (Layer layer : layeredGraph.getLayers()) {
            for (LNode lNode : layer.getNodes()) {
                // Assign port label sides
                for (LPort port : lNode.getPorts()) {
                    for (LLabel label : port.getLabels()) {
                        if (label.getProperty(InternalProperties.LABEL_SIDE) == LabelSide.UNKNOWN) {
                            label.setProperty(InternalProperties.LABEL_SIDE, DEFAULT_LABEL_SIDE);
                        }
                    }
                }
                
                // If this is a label dummy node, move the ports if necessary
                if (lNode.getType() == NodeType.LABEL) {
                    if (lNode.getProperty(InternalProperties.LABEL_SIDE) == LabelSide.ABOVE) {
                        LEdge originEdge = (LEdge) lNode.getProperty(InternalProperties.ORIGIN);
                        double thickness = originEdge.getProperty(LayeredOptions.EDGE_THICKNESS);
                        double portPos = lNode.getSize().y - Math.ceil(thickness / 2);
                        for (LPort port : lNode.getPorts()) {
                            port.getPosition().y = portPos;
                        }
                        
                    }
                }
            }
        }

        monitor.done();
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////
    // Placement Strategies

    /**
     * Places all labels above their respective edge.
     * 
     * @param nodes all nodes of the graph
     */
    private void alwaysUp(final Iterable<LNode> nodes) {
        for (LNode node : nodes) {
            if (node.getType() == NodeType.LABEL) {
                node.setProperty(InternalProperties.LABEL_SIDE, LabelSide.ABOVE);
            }
            
            for (LEdge edge : node.getOutgoingEdges()) {
                applyLabelSide(edge, LabelSide.ABOVE);
            }
        }
    }

    /**
     * Places all labels below their respective edge.
     * 
     * @param nodes all nodes of the graph
     */
    private void alwaysDown(final Iterable<LNode> nodes) {
        for (LNode node : nodes) {
            if (node.getType() == NodeType.LABEL) {
                node.setProperty(InternalProperties.LABEL_SIDE, LabelSide.BELOW);
            }
            
            for (LEdge edge : node.getOutgoingEdges()) {
                applyLabelSide(edge, LabelSide.BELOW);
            }
        }
    }

    /**
     * Places all labels above their respective edge if it points right, and below if it points left.
     * 
     * @param nodes all nodes of the graph
     */
    private void directionUp(final Iterable<LNode> nodes) {
        for (LNode node : nodes) {
            if (node.getType() == NodeType.LABEL) {
                LabelSide side = doesEdgePointRight(node) ? LabelSide.ABOVE : LabelSide.BELOW;
                node.setProperty(InternalProperties.LABEL_SIDE, side);
            }
            
            for (LEdge edge : node.getOutgoingEdges()) {
                LabelSide side = doesEdgePointRight(edge) ? LabelSide.ABOVE : LabelSide.BELOW;
                applyLabelSide(edge, side);
            }
        }
    }

    /**
     * Places all labels below their respective edge if it points right, and above if it points left.
     * 
     * @param nodes all nodes of the graph
     */
    private void directionDown(final Iterable<LNode> nodes) {
        for (LNode node : nodes) {
            if (node.getType() == NodeType.LABEL) {
                LabelSide side = doesEdgePointRight(node) ? LabelSide.BELOW : LabelSide.ABOVE;
                node.setProperty(InternalProperties.LABEL_SIDE, side);
            }
            
            for (LEdge edge : node.getOutgoingEdges()) {
                LabelSide side = doesEdgePointRight(edge) ? LabelSide.BELOW : LabelSide.ABOVE;
                applyLabelSide(edge, side);
            }
        }
    }

    /**
     * Chooses label sides depending on certain patterns.
     * 
     * TODO: The smart label side selection strategy currently does not work for center edge labels.
     *       In fact, it could generally use an overhaul.
     * 
     * @param nodes all nodes of the graph
     */
    private void smart(final Iterable<LNode> nodes) {
        Map<LNode, LabelSide> nodeMarkers = Maps.newHashMap();
        for (LNode node : nodes) {
            List<LPort> eastPorts = getPortsBySide(node, PortSide.EAST);
            for (LPort eastPort : eastPorts) {
                for (LEdge edge : eastPort.getOutgoingEdges()) {
                    LabelSide chosenSide = LabelSide.ABOVE;
                    LNode targetNode = edge.getTarget().getNode();
                    
                    if (targetNode.getType() == NodeType.LONG_EDGE
                            || targetNode.getType() == NodeType.LABEL) {
                        
                        targetNode =
                                targetNode.getProperty(InternalProperties.LONG_EDGE_TARGET).getNode();
                    }
                    
                    // Markers make sure that no overlaps will be created
                    if (nodeMarkers.containsKey(targetNode)) {
                        chosenSide = nodeMarkers.get(targetNode);
                    } else {
                        // Patterns are applied, the only current pattern is a node with
                        // two outgoing edges
                        if (eastPorts.size() == 2) {
                            if (eastPort == eastPorts.get(0)) {
                                chosenSide = LabelSide.ABOVE;
                            } else {
                                chosenSide = LabelSide.BELOW;
                            }
                        } else {
                            chosenSide = LabelSide.ABOVE;
                        }
                        
                        nodeMarkers.put(targetNode, chosenSide);
                    }
                    
                    for (LLabel label : edge.getLabels()) {
                        label.setProperty(InternalProperties.LABEL_SIDE, chosenSide);
                    }
                    
                    for (LLabel portLabel : edge.getSource().getLabels()) {
                        portLabel.setProperty(InternalProperties.LABEL_SIDE, chosenSide);
                    }
                    
                    for (LLabel portLabel : edge.getTarget().getLabels()) {
                        portLabel.setProperty(InternalProperties.LABEL_SIDE, chosenSide);
                    }
                }
            }
        }
    }

    /**
     * Get all ports on a certain side of a node. They are sorted descending by their position on
     * the node.
     * 
     * @param node
     *            The node to consider
     * @param portSide
     *            The chosen side
     * @return A list of all ports on the chosen side of the node
     */
    private List<LPort> getPortsBySide(final LNode node, final PortSide portSide) {
        List<LPort> result = Lists.newArrayList();
        
        for (LPort port : node.getPorts(portSide)) {
            result.add(port);
        }
        
        Collections.sort(result, new Comparator<LPort>() {
            public int compare(final LPort o1, final LPort o2) {
                if (o1.getPosition().y < o2.getPosition().y) {
                    return -1;
                } else if (o1.getPosition().y == o2.getPosition().y) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        return result;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    
    /**
     * Applies the given label side to all labels of the given edge and its source and target port.
     * 
     * @param edge the edge to apply the label side to.
     * @param side the label side to apply.
     */
    private void applyLabelSide(final LEdge edge, final LabelSide side) {
        for (LLabel label : edge.getLabels()) {
            label.setProperty(InternalProperties.LABEL_SIDE, side);
        }
        
        for (LLabel portLabel : edge.getSource().getLabels()) {
            portLabel.setProperty(InternalProperties.LABEL_SIDE, side);
        }
        
        for (LLabel portLabel : edge.getTarget().getLabels()) {
            portLabel.setProperty(InternalProperties.LABEL_SIDE, side);
        }
    }
    
    /**
     * Checks if the given particular edge will point right or left in the final drawing. The edge can
     * either be a regular edge or part of a long edge. In the latter case, this edge is assumed to
     * point left if the original long edge target is left of the original long edge source, and if this
     * edge is marked to have been reversed.
     * 
     * @param edge the edge to check.
     * @return {@code true} if the edge will point right in the final drawing.
     */
    private boolean doesEdgePointRight(final LEdge edge) {
        // cds: This method formerly used a much more complicated implementation that took the original
        // long edge source and target into account. However, this short implementation should be quite
        // enough...
        return !edge.getProperty(InternalProperties.REVERSED);
    }
    
    /**
     * Checks if the given label dummy node is part of an edge segment that will point right in the
     * final drawing. We assume that this is the case if at least one of the incident edges points
     * rightwards according to {@link #doesEdgePointRight(LEdge)}.
     * 
     * @param labelDummy the label dummy to check.
     * @return {@code true} if this label dummy node is part of an edge that will point right in the
     *         final drawing.
     */
    private boolean doesEdgePointRight(final LNode labelDummy) {
        assert labelDummy.getType() == NodeType.LABEL;
        assert labelDummy.getIncomingEdges().iterator().hasNext();
        assert labelDummy.getOutgoingEdges().iterator().hasNext();
        
        // Find incoming and outgoing edge
        LEdge incoming = labelDummy.getIncomingEdges().iterator().next();
        LEdge outgoing = labelDummy.getOutgoingEdges().iterator().next();
        
        return doesEdgePointRight(incoming) || doesEdgePointRight(outgoing);
    }

}
