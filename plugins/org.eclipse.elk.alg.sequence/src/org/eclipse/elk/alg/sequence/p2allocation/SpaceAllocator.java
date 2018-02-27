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
package org.eclipse.elk.alg.sequence.p2allocation;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SArea;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Iterables;

/**
 * Allocates vertical space for various objects by introducing dummy nodes into the layered graph. Space is
 * required wherever messages cannot be allowed to be placed. This includes space required for message comments or for
 * headers of combined fragments.
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
        // TODO: This method would ideally add dummies for nested fragments as well
        
        // Add dummy nodes before the first messages of combined fragments to have enough space
        // above the topmost message of the area
        for (SArea area : context.sgraph.getAreas()) {
            // Find the uppermost message contained in the combined fragment. It will be that of all messages
            // contained in the area which has no predecessor in the layered graph which is itself part of the
            // area
            SMessage uppermostMessage = null;
            
            for (SMessage msg : area.getMessages()) {
                // Find out if any of the messages predecessors in the layered graph are part of
                // the fragment. If not, we have found our best guess for the uppermost message
                LNode msgNode = msg.getProperty(InternalSequenceProperties.LAYERED_NODE);
                boolean isUppermost = true;
                for (LEdge incomingEdge : msgNode.getIncomingEdges()) {
                    Object predecessor = incomingEdge.getSource().getNode().getProperty(
                            InternalSequenceProperties.ORIGIN);
                    
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
            if (comment.getReferenceMessage() != null) {
                // Get height of the comment and calculate number of dummy nodes needed
                double height = comment.getSize().y;
                int dummys = (int) Math.ceil(height / context.messageSpacing);
                
                // Add dummy nodes in the layered graph
                LNode lnode = comment.getReferenceMessage().getProperty(InternalSequenceProperties.LAYERED_NODE);
                if (lnode != null) {
                    for (int i = 0; i < dummys; i++) {
                        createLGraphDummyNode(context.lgraph, lnode, true);
                    }
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
        for (SArea area : context.sgraph.getAreas()) {
            if (area.getMessages().size() == 0) {
                SMessage nextMsg = area.getNextMessage();
                if (nextMsg != null) {
                    LNode node = nextMsg.getProperty(InternalSequenceProperties.LAYERED_NODE);
                    if (node != null) {
                        // Create two dummy nodes before node to have enough space for the empty area
                        createLGraphDummyNode(context.lgraph, node, true);
                        createLGraphDummyNode(context.lgraph, node, true);
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
     *            if {@code true}, the dummy will be inserted before the node, behind the node otherwise
     */
    private void createLGraphDummyNode(final LGraph lgraph, final LNode node, final boolean beforeNode) {
        // Create the new dummy node, along with ports
        LNode dummy = new LNode(lgraph);
        lgraph.getLayerlessNodes().add(dummy);
        
        LPort dummyIn = new LPort();
        dummyIn.setNode(dummy);
        
        LPort dummyOut = new LPort();
        dummyOut.setNode(dummy);
        
        // Don't bother with existing ports, simply create a new one and be done with it
        LPort newPort = new LPort();
        newPort.setNode(node);
        
        // This edge will connect the dummy with the node before / after which it is going to be inserted
        LEdge dummyEdge = new LEdge();

        // To avoid concurrent modification, two lists are needed
        if (beforeNode) {
            // Divert original predecessors to dummy node
            for (LEdge edge : Iterables.toArray(node.getIncomingEdges(), LEdge.class)) {
                edge.setTarget(dummyIn);
            }
            
            // Connect the node to the new dummy
            dummyEdge.setSource(dummyOut);
            dummyEdge.setTarget(newPort);
            
        } else {
            // Divert original successors to dummy node
            for (LEdge edge : Iterables.toArray(node.getOutgoingEdges(), LEdge.class)) {
                edge.setSource(dummyOut);
            }
            
            // Connect the node to the new dummy
            dummyEdge.setTarget(dummyIn);
            dummyEdge.setSource(newPort);
        }
    }

}
