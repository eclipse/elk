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

import java.util.List;
import java.util.ListIterator;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Processor that inserts dummy nodes into edges that have center labels to reserve space for them.
 * At most one dummy node is created for each edge.
 * 
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>an unlayered acyclic graph.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>dummy nodes are inserted that represent center labels.</dd>
 *     <dd>center labels are removed from their respective edges and attached to the dummy nodes.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 2.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>None.</dd>
 * </dl>
 * 
 * @author jjc
 * @author cds
 * @kieler.rating yellow proposed cds
 */
public final class LabelDummyInserter implements ILayoutProcessor {
    
    /** Predicate that checks for center labels. */
    private static final Predicate<LLabel> CENTER_LABEL = new Predicate<LLabel>() {
        public boolean apply(final LLabel label) {
            return label.getProperty(LayeredOptions.EDGE_LABELS_PLACEMENT)
                    == EdgeLabelPlacement.CENTER;
        }
    };
    
    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Label dummy insertions", 1);
        
        List<LNode> newDummyNodes = Lists.newArrayList();
        
        double labelSpacing = layeredGraph.getProperty(LayeredOptions.SPACING_EDGE_LABEL);
        Direction layoutDirection = layeredGraph.getProperty(LayeredOptions.DIRECTION);

        for (LNode node : layeredGraph.getLayerlessNodes()) {
            for (LPort port : node.getPorts()) {
                for (LEdge edge : port.getOutgoingEdges()) {
                    // Ignore self-loops for the moment (see KIPRA-1073)
                    if (edge.getSource().getNode() != edge.getTarget().getNode()
                            && Iterables.any(edge.getLabels(), CENTER_LABEL)) {
                    
                        // Remember the list of edge labels represented by the dummy node
                        List<LLabel> representedLabels = Lists.newArrayListWithCapacity(
                                edge.getLabels().size());
                        
                        // Create dummy node
                        LNode dummyNode = new LNode(layeredGraph);
                        dummyNode.setType(NodeType.LABEL);
                        
                        dummyNode.setProperty(InternalProperties.ORIGIN, edge);
                        dummyNode.setProperty(InternalProperties.REPRESENTED_LABELS, representedLabels);
                        dummyNode.setProperty(LayeredOptions.PORT_CONSTRAINTS,
                                PortConstraints.FIXED_POS);
                        dummyNode.setProperty(InternalProperties.LONG_EDGE_SOURCE, edge.getSource());
                        dummyNode.setProperty(InternalProperties.LONG_EDGE_TARGET, edge.getTarget());
                        
                        newDummyNodes.add(dummyNode);
                        
                        // Actually split the edge
                        LongEdgeSplitter.splitEdge(edge, dummyNode);
                        
                        // Set thickness of the edge and place ports at its center
                        float thickness = edge.getProperty(LayeredOptions.EDGE_THICKNESS);
                        if (thickness < 0) {
                            thickness = 0;
                            edge.setProperty(LayeredOptions.EDGE_THICKNESS, thickness);
                        }
                        double portPos = Math.floor(thickness / 2);

                        // Apply port positions
                        for (LPort dummyPort : dummyNode.getPorts()) {
                            dummyPort.getPosition().y = portPos;
                        }
                        
                        // Determine the size of the dummy node and move labels over to it
                        KVector dummySize = dummyNode.getSize();
                        
                        ListIterator<LLabel> iterator = edge.getLabels().listIterator();
                        while (iterator.hasNext()) {
                            LLabel label = iterator.next();
                            
                            if (label.getProperty(LayeredOptions.EDGE_LABELS_PLACEMENT)
                                    == EdgeLabelPlacement.CENTER) {
                                
                                // The way we stack labels depends on the layout direction
                                if (layoutDirection.isVertical()) {
                                    dummySize.x += label.getSize().x + labelSpacing;
                                    dummySize.y = Math.max(dummySize.y, label.getSize().y);
                                } else {
                                    dummySize.x = Math.max(dummySize.x, label.getSize().x);
                                    dummySize.y += label.getSize().y + labelSpacing;
                                }
                                
                                // Move the label over to the dummy node's REPRESENTED_LABELS property
                                representedLabels.add(label);
                                iterator.remove();
                            }
                        }
                        
                        // Determine the final dummy node size
                        if (layoutDirection.isVertical()) {
                            dummySize.x -= labelSpacing;
                            dummySize.y += labelSpacing + thickness;
                        } else {
                            dummySize.y += labelSpacing + thickness;
                        }
                    }
                }
            }
        }

        // Add created dummies to graph
        layeredGraph.getLayerlessNodes().addAll(newDummyNodes);
        
        monitor.done();
    }

}
