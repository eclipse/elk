/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.intermediate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.sequence.SequenceUtils;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Creates and initializes the layered graph. The graph contains a node for each message in the sequence graph. An
 * edge runs from node A to node B if there is a lifeline where A's message directly precedes B's message.
 */
public class LayeredGraphCreator implements ILayoutProcessor<LayoutContext> {

    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Creating the layered graph", 1);
        
        // Build the graph
        LGraph lgraph = new LGraph();
        context.lgraph = lgraph;

        // Build a node for every message and infer the successor constraints between them
        createNodes(context);
        createEdges(context);
        
        if (!context.verticalCompaction) {
            // We need to add additional edges
            createAdditionalEdges(context);
        }
        
        progressMonitor.done();
    }

    /**
     * Creates a node in the layered graph for each message in the sequence graph.
     */
    private void createNodes(final LayoutContext context) {
        int nodeNumber = 0;
        for (SLifeline lifeline : context.sgraph.getLifelines()) {
            // We ignore dummy lifelines since their incident messages are also incident to non-dummy lifelines anyway
            if (lifeline.isDummy()) {
                continue;
            }
            
            // Handle outgoing messages
            for (SMessage message : lifeline.getOutgoingMessages()) {
                LNode node = SequenceUtils.createLNode(context.lgraph);
                node.getLabels().add(new LLabel("Node" + nodeNumber++));

                node.setProperty(InternalSequenceProperties.ORIGIN, message);
                message.setProperty(InternalSequenceProperties.LAYERED_NODE, node);
            }

            // Handle found messages (whose source lifeline is a dummy lifeline)
            for (SMessage message : lifeline.getIncomingMessages()) {
                if (message.getSource().isDummy()) {
                    LNode node = SequenceUtils.createLNode(context.lgraph);
                    node.getLabels().add(new LLabel("Node" + nodeNumber++));

                    node.setProperty(InternalSequenceProperties.ORIGIN, message);
                    message.setProperty(InternalSequenceProperties.LAYERED_NODE, node);
                }
            }
        }
    }

    /**
     * Connects the nodes in the layered graph by edges.
     */
    private void createEdges(final LayoutContext context) {
        for (SLifeline lifeline : context.sgraph.getLifelines()) {
            List<SMessage> messages = lifeline.getMessages();
            for (int j = 1; j < messages.size(); j++) {
                // Add an edge from the node belonging to message j-1 to the node belonging to message j
                LNode sourceNode = messages.get(j - 1).getProperty(InternalSequenceProperties.LAYERED_NODE);
                LNode targetNode = messages.get(j).getProperty(InternalSequenceProperties.LAYERED_NODE);

                assert sourceNode != null && targetNode != null;

                if (sourceNode != targetNode) {
                    connect(sourceNode, targetNode)
                        .setProperty(InternalSequenceProperties.BELONGS_TO_LIFELINE, lifeline);
                }
            }
        }
    }
    
    /**
     * Creates more edges between message nodes to keep them from being compacted vertically.
     */
    private void createAdditionalEdges(final LayoutContext context) {
        // Collect all messages
        List<SMessage> messages = new ArrayList<>();
        
        for (SLifeline lifeline : context.sgraph.getLifelines()) {
            // We ignore dummy lifelines since their incident messages are also incident to non-dummy lifelines anyway
            if (lifeline.isDummy()) {
                continue;
            }
            
            // Handle outgoing messages
            for (SMessage message : lifeline.getOutgoingMessages()) {
                messages.add(message);
            }

            // Handle found messages (whose source lifeline is a dummy lifeline)
            for (SMessage message : lifeline.getIncomingMessages()) {
                if (message.getSource().isDummy()) {
                    messages.add(message);
                }
            }
        }
        
        // Sort the messages ascendingly by y coordinate
        messages.sort((msg1, msg2) -> Double.compare(msg1.getSourceYPos(), msg2.getSourceYPos()));
        
        // Connect the message nodes
        for (int i = messages.size() - 2; i >= 0; i--) {
            connect(
                    (LNode) messages.get(i).getProperty(InternalSequenceProperties.LAYERED_NODE),
                    (LNode) messages.get(i + 1).getProperty(InternalSequenceProperties.LAYERED_NODE));
        }
    }
    
    /**
     * Connects the two nodes by an edge and returns the new edge.
     */
    private LEdge connect(final LNode node1, final LNode node2) {
        LEdge edge = new LEdge();
        
        edge.setSource(node1.getPorts().get(0));
        edge.setTarget(node2.getPorts().get(0));
        
        return edge;
    }

}
