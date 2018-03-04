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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.SequenceUtils;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SArea;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

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
    // Space Allocation for Non-Empty Areas
    
    /* The following code needs to make sure that two conditions are met:
     * 
     *   A. Space is reserved for the area's header.
     *   B. No message runs into the area that does not belong there.
     * 
     * To explain how these conditions are met we need to define four sets of messages:
     * 
     *   1. Above message are messages that are not part of the area, but directly precede at least one of the area's
     *      messages.
     *   2. Uppermost messages are all messages inside the area that have no precedessor that is also part of the area.
     *      No message inside the area precedes them.
     *   3. Bottommost messages are the corresponding concept applied to the other direction.
     *   4. Below messages are like above messages, but in the other direction.
     * 
     * Meeting condition A entails introducing a dummy node to reserve a layer of space for the area's header. The
     * dummy node is connected to all uppermost messages to ensure that they are placed below the header.
     * 
     * Meeting condition B entails connecting all above messages to the dummy node to ensure that they cannot move past
     * the header into the area. Similarly, all bottommost messages are connected to all below messages to ensure that
     * below messages cannot sneak past the area's bottom border.
     */

    /**
     * Allocate space for the header of combined fragments and interaction uses.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void allocateSpaceForAreaHeaders(final LayoutContext context) {
        for (SArea area : context.sgraph.getAreas()) {
            Set<LNode> aboveNodes = new HashSet<>();
            Set<LNode> uppermostNodes = new HashSet<>();
            Set<LNode> lowermostNodes = new HashSet<>();
            Set<LNode> belowNodes = new HashSet<>();

            // Go through each of the area's messages and find 
            for (SMessage msg : area.getMessages()) {
                LNode msgNode = msg.getProperty(InternalSequenceProperties.LAYERED_NODE);
                
                boolean hasPredecessorsInArea = false;
                boolean hasSuccessorsInArea = false;
                
                // Iterate through the node's edges and inspect the other end points
                for (LEdge edge : msgNode.getConnectedEdges()) {
                    if (edge.getSource().getNode() == msgNode) {
                        // It's an outgoing edge
                        if (area.getMessages().contains(origin(edge.getTarget().getNode()))) {
                            // The successor is in the area itself
                            hasSuccessorsInArea = true;
                        } else {
                            // The successor is not in the area
                            belowNodes.add(edge.getTarget().getNode());
                        }
                        
                    } else if (edge.getTarget().getNode() == msgNode) {
                        // It's an incoming edge
                        if (area.getMessages().contains(origin(edge.getSource().getNode()))) {
                            // The predecessor is in the area itself
                            hasPredecessorsInArea = true;
                        } else {
                            // The predecessor is not in the area
                            aboveNodes.add(edge.getSource().getNode());
                        }
                    }
                }
                
                // Depending on predecessors and successors in the same area this node may be uppermost, lowermost,
                // both, or none
                if (!hasPredecessorsInArea) {
                    uppermostNodes.add(msgNode);
                }
                
                if (!hasSuccessorsInArea) {
                    lowermostNodes.add(msgNode);
                }
            }
            
            // Insert a dummy node above the area to reserve space for the header
            LNode dummy = SequenceUtils.createLNode(context.lgraph);
            dummy.setProperty(InternalSequenceProperties.ORIGIN, area);
            Set<LNode> dummySet = Sets.newHashSet(dummy);
            
            connect(aboveNodes, dummySet);
            connect(dummySet, uppermostNodes);
            
            // Connect lowermost nodes to below nodes
            connect(lowermostNodes, belowNodes);
            
            // Remember the lowermost nodes
            area.setProperty(InternalSequenceProperties.LOWERMOST_NODES, lowermostNodes);
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Space Allocation for Comments

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
                        insertDummyNodeBefore(lnode);
                    }
                }
            }
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Space Allocation for Empty Areas

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
                        insertDummyNodeBefore(node);
                        insertDummyNodeBefore(node);
                    }
                }
            }
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Dummy Node Stuff

    /**
     * Creates a dummy node in the layered graph that preceeds the given node. Every incoming edge of the original
     * node is redirected to the dummy node.
     * 
     * @param nodes
     *            the nodes that get a new predecessor.
     */
    private void insertDummyNodeBefore(final LNode node) {
        LNode dummy = SequenceUtils.createLNode(node.getGraph());
        LPort dummyPort = dummy.getPorts().get(0);
        
        // Divert original predecessors to dummy node (we could use the other methods below to simply add new
        // connections, but this works just as wel and uses fewer edges)
        for (LEdge edge : Iterables.toArray(node.getIncomingEdges(), LEdge.class)) {
            edge.setTarget(dummyPort);
        }
        
        // Connect the new dummy to the existing node
        LEdge dummyEdge = new LEdge();
        dummyEdge.setSource(dummyPort);
        dummyEdge.setTarget(node.getPorts().get(0));
    }
    
    /**
     * Connects each source to each target.
     */
    private void connect(final Collection<LNode> sources, final Collection<LNode> targets) {
        for (LNode source : sources) {
            LPort sourcePort = source.getPorts().get(0);
            
            for (LNode target : targets) {
                LEdge edge = new LEdge();
                edge.setSource(sourcePort);
                edge.setTarget(target.getPorts().get(0));
            }
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Utility Methods
    
    /**
     * Returns the value of the origin property set on the given node.
     */
    private Object origin(final LNode node) {
        return node.getProperty(InternalSequenceProperties.ORIGIN);
    }

}
