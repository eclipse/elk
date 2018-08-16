/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.math.DoubleMath;

/**
 * Finds connected components of self loops and adds them to the {@link InternalProperties#SELFLOOP_COMPONENTS}
 * property of the node.
 *
 * <dl>
 *   <dt>Precondition:</dt>
 *     <dd>A layered graph.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>All ports are grouped into connected {@link SelfLoopComponent}s.</dd>
 *     <dd>The components are added to the {@link InternalProperties#SELFLOOP_COMPONENTS} property of the node.</dd>
 *     <dd>Some ports are removed from the node: all ports only having self loops connected to them.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 2.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>None.</dd>
 * </dl>
 */
public final class SelfLoopPreProcessor implements ILayoutProcessor<LGraph> {

    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Self-Loop pre-processing.", 1);
        
        // process all nodes
        for (final LNode node : layeredGraph.getLayerlessNodes()) {
            if (node.getType() == NodeType.NORMAL) {
                // create a self loop node representation
                SelfLoopNode slNode = new SelfLoopNode(node);
                node.setProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION, slNode);
                
                // calculate the SelfLoopComponents of the node and save them to it's properties
                SelfLoopComponent.createSelfLoopComponents(slNode);
                
                // pre-process labels
                preprocessLabels(slNode);
                
                // hide the ports which are non useful for the crossing minimization
                hidePorts(node);
            }
        }

        monitor.done();
    }
    
    /** A small number for double approximation. */
    private static final double EPSILON = 1e-6;

    /**
     * For each component the labels of the contained edges are collected and put together to one label. This joint
     * label is stored in a SelfLoopLabel.
     */
    public void preprocessLabels(final SelfLoopNode slNode) {
        double labelLabelSpacing = LGraphUtil.getIndividualOrInherited(
                slNode.getNode(), LayeredOptions.SPACING_LABEL_LABEL);
        
        for (SelfLoopComponent component : slNode.getSelfLoopComponents()) {
            // Retrieve all labels attached to edges that belong to this component
            List<LLabel> labels = component.getConnectedEdges().stream()
                .flatMap(edge -> edge.getEdge().getLabels().stream())
                .sorted(new Comparator<LLabel>() {
                    @Override
                    public int compare(final LLabel o1, final LLabel o2) {
                        return DoubleMath.fuzzyCompare(o1.getSize().x, o2.getSize().x, EPSILON);
                    }
                })
                .collect(Collectors.toList());

            // If there are no labels, there is no need for us to do anything
            if (labels.isEmpty()) {
                continue;
            }
            
            // Since the labels will be stacked above one another, we calculate the width and the height required
            double maxWidth = 0.0;
            double height = 0.0;
            for (LLabel label : labels) {
                KVector size = label.getSize();
                maxWidth = Math.max(size.x, maxWidth);
                height += size.y;
            }
            
            // Add label-label spacing between each pair of labels
            height += Math.max(0, labelLabelSpacing * (labels.size() - 1));

            // Create a SelfLoopLabel for this component
            SelfLoopLabel label = new SelfLoopLabel();
            label.getLabels().addAll(labels);
            label.setHeight(height);
            label.setWidth(maxWidth);
            component.setSelfLoopLabel(label);
        }
    }

    /**
     * Collect and remove ports that are only self-loop ports.
     */
    private void hidePorts(final LNode node) {
        List<LPort> selfLoopPorts = new ArrayList<LPort>();

        // check each port if has to be hidden
        for (LPort port : node.getPorts()) {
            Iterable<LEdge> edges = port.getConnectedEdges();

            // check whether an port belongs only to self-loops
            boolean containsNonSelfloop = false;
            for (LEdge edge : edges) {
                if (!edge.isSelfLoop()) {
                    containsNonSelfloop = true;
                    break;
                }
            }

            // ports that belong only to self-loops are collected
            if (!containsNonSelfloop) {
                selfLoopPorts.add(port);
            }
        }

        // remove collected ports from node
        node.getPorts().removeAll(selfLoopPorts);
    }

}
