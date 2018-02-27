/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.intermediate;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
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
                LNode node = new LNode(context.lgraph);
                context.lgraph.getLayerlessNodes().add(node);

                node.getLabels().add(new LLabel("Node" + nodeNumber++));

                node.setProperty(InternalSequenceProperties.ORIGIN, message);
                message.setProperty(InternalSequenceProperties.LAYERED_NODE, node);
            }

            // Handle found messages (whose source lifeline is a dummy lifeline)
            for (SMessage message : lifeline.getIncomingMessages()) {
                if (message.getSource().isDummy()) {
                    LNode node = new LNode(context.lgraph);
                    context.lgraph.getLayerlessNodes().add(node);

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
                    LPort sourcePort = new LPort();
                    sourcePort.setNode(sourceNode);

                    LPort targetPort = new LPort();
                    targetPort.setNode(targetNode);

                    LEdge edge = new LEdge();

                    edge.setSource(sourcePort);
                    edge.setTarget(targetPort);

                    edge.setProperty(InternalSequenceProperties.BELONGS_TO_LIFELINE, lifeline);
                }
            }
        }
    }

}
