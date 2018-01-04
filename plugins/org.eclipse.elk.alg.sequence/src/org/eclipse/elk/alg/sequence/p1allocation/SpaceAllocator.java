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
package org.eclipse.elk.alg.sequence.p1allocation;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SGraphElement;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.CoordinateSystem;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.options.SequenceArea;
import org.eclipse.elk.alg.sequence.properties.SequenceDiagramOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Allocates vertical space for various objects by introducing dummy nodes in the LGraph. Space is
 * required wherever messages cannot be allowed to be placed. This includes space required for message
 * comments or for headers of combined fragments.
 * 
 * @author grh
 * @author cds
 */
public final class SpaceAllocator implements ILayoutPhase<SequencePhases, LayoutContext> {

    @Override
    public LayoutProcessorConfiguration<SequencePhases, LayoutContext> getLayoutProcessorConfiguration(
            final LayoutContext graph) {
        
        return LayoutProcessorConfiguration.create();
    }

    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Space Allocation", 1);
        
        allocateSpaceForAreaHeaders(context);
        allocateSpaceForComments(context);
        allocateSpaceForEmptyAreas(context);
        
        progressMonitor.done();
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Space Allocation

    /**
     * Allocate space for the header of combined fragments and interaction uses.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void allocateSpaceForAreaHeaders(final LayoutContext context) {
        // Add dummy nodes before the first messages of combined fragments to have enough space
        // above the topmost message of the area
        List<SequenceArea> areas = context.sgraph.getProperty(SequenceDiagramOptions.AREAS);
        if (areas == null || areas.isEmpty()) {
            return;
        }
        
        for (SequenceArea area : areas) {
            // Find the uppermost message contained in the combined fragment. In Papyrus mode, we do
            // that using message y coordinates. In KGraph mode, there's not really a good solution
            // for finding the best message (actually, I'm not even convinced that the solution in
            // Papyrus mode always works). There, we simply try to select a source in the layered
            // subgraph induced by the set of nodes whose messages are part of the fragment
            SMessage uppermostMessage = null;
            
            if (context.coordinateSystem == CoordinateSystem.PAPYRUS) {
                double minYPos = Double.MAX_VALUE;
                for (Object messageObj : area.getMessages()) {
                    SMessage message = (SMessage) messageObj;
                    if (message.getSourceYPos() < minYPos) {
                        minYPos = message.getSourceYPos();
                        uppermostMessage = message;
                    }
                }
            } else {
                // Run through the messages and find a source in the layered subgraph induced by the
                // nodes that are messages in this area
                for (Object msgObj : area.getMessages()) {
                    SMessage msg = (SMessage) msgObj;
                    
                    // Find out if any of the messages predecessors in the layered graph are part of
                    // the fragment. If not, we have found our best guess for the uppermost message
                    LNode msgNode = msg.getProperty(InternalSequenceProperties.LAYERED_NODE);
                    boolean isUppermost = true;
                    for (LEdge incomingEdge : msgNode.getIncomingEdges()) {
                        Object predecessor = incomingEdge.getSource().getNode().getProperty(
                                InternalProperties.ORIGIN);
                        
                        if (area.getMessages().contains(predecessor)) {
                            isUppermost = false;
                            break;
                        }
                    }
                    
                    if (isUppermost) {
                        uppermostMessage = msg;
                        break;
                    }
                }
            }
            
            // If we were able to find an uppermost message, insert a dummy node to reserve space
            if (uppermostMessage != null) {
                LNode node = uppermostMessage.getProperty(InternalSequenceProperties.LAYERED_NODE);
                createLGraphDummyNode(context.lgraph, node, true);
            }
        }
    }

    /**
     * Allocate space for placing the comments near to their attached elements.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void allocateSpaceForComments(final LayoutContext context) {
        for (SComment comment : context.sgraph.getComments()) {
            // Check to which kind of object the comment is attached
            SMessage attachedMess = null;
            for (SGraphElement element : comment.getAttachedTo()) {
                if (element instanceof SMessage) {
                    attachedMess = (SMessage) element;
                }
            }
            
            if (attachedMess != null) {
                // Get height of the comment and calculate number of dummy nodes needed
                double height = comment.getSize().y;
                int dummys = (int) Math.ceil(height / context.messageSpacing);
                
                // Add dummy nodes in the layered graph
                LNode lnode = attachedMess.getProperty(InternalSequenceProperties.LAYERED_NODE);
                if (lnode != null) {
                    for (int i = 0; i < dummys; i++) {
                        createLGraphDummyNode(context.lgraph, lnode, true);
                    }
                    comment.setMessage(attachedMess);
                    attachedMess.getComments().add(comment);
                }
            }
        }
    }

    /**
     * Add dummy nodes to the layered graph in order to allocate space for empty areas.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void allocateSpaceForEmptyAreas(final LayoutContext context) {
        List<SequenceArea> areas = context.sgraph.getProperty(SequenceDiagramOptions.AREAS);
        if (areas != null) {
            for (SequenceArea area : areas) {
                if (area.getMessages().size() == 0) {
                    Object nextMess = area.getNextMessage();
                    if (nextMess != null) {
                        LNode node = ((SMessage) nextMess)
                                .getProperty(InternalSequenceProperties.LAYERED_NODE);
                        if (node != null) {
                            // Create two dummy nodes before node to have enough space for the empty
                            // area
                            createLGraphDummyNode(context.lgraph, node, true);
                            createLGraphDummyNode(context.lgraph, node, true);
                        }
                    }
                }
            }
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Utility Methods

    /**
     * Creates a dummy node in the layered graph, that is placed near the given node. Every
     * connected edge of the original node is redirected to the dummy node.
     * 
     * @param lgraph
     *            the layered graph
     * @param node
     *            the node, that gets a predecessor
     * @param beforeNode
     *            if true, the dummy will be inserted before the node, behind the node otherwise
     */
    private void createLGraphDummyNode(final LGraph lgraph, final LNode node, final boolean beforeNode) {
        LNode dummy = new LNode(lgraph);
        
        LPort dummyIn = new LPort();
        dummyIn.setNode(dummy);
        
        LPort dummyOut = new LPort();
        dummyOut.setNode(dummy);
        
        LPort newPort = new LPort();
        newPort.setNode(node);

        LEdge dummyEdge = new LEdge();

        // To avoid concurrent modification, two lists are needed
        if (beforeNode) {
            List<LEdge> incomingEdges = new LinkedList<LEdge>();
            
            for (LEdge edge : node.getIncomingEdges()) {
                incomingEdges.add(edge);
            }
            
            for (LEdge edge : incomingEdges) {
                edge.setTarget(dummyIn);
            }
            
            dummyEdge.setSource(dummyOut);
            dummyEdge.setTarget(newPort);
        } else {
            List<LEdge> outgoingEdges = new LinkedList<LEdge>();
            
            for (LEdge edge : node.getOutgoingEdges()) {
                outgoingEdges.add(edge);
            }
            
            for (LEdge edge : outgoingEdges) {
                edge.setSource(dummyOut);
            }
            
            dummyEdge.setTarget(dummyIn);
            dummyEdge.setSource(newPort);
        }
        
        lgraph.getLayerlessNodes().add(dummy);
    }

}
