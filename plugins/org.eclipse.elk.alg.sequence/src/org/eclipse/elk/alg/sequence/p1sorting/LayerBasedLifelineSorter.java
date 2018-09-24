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
package org.eclipse.elk.alg.sequence.p1sorting;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Lists;

/**
 * Lifeline sorting algorithm that sorts the lifelines according to their uppermost outgoing
 * messages. The "source" lifeline is placed leftmost, the successor lifelines following.
 */
public final class LayerBasedLifelineSorter implements ILayoutPhase<SequencePhases, LayoutContext> {

    /** Lifelines that have not been assigned a horizontal slot yet have this value as their slot. */
    private static final int UNPROCESSED = -1;
    
    /** List of lifelines that have already been sorted. */
    private List<SLifeline> sortedLifelines;

    
    @Override
    public LayoutProcessorConfiguration<SequencePhases, LayoutContext> getLayoutProcessorConfiguration(
            final LayoutContext graph) {
        
        return LayoutProcessorConfiguration.create();
    }

    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Layer-based lifeline sorting", 1);
        
        context.sgraph.getLifelines().stream().forEach(ll -> ll.setHorizontalSlot(UNPROCESSED));
        sortedLifelines = Lists.newArrayListWithCapacity(context.sgraph.getLifelines().size());
        
        // Find all LNodes that have no incoming connections. Those represent (locally) uppermost messages.
        List<LNode> uppermostMessageNodes = uppermostMessagesSortedBySuitability(context);
        
        // Iterate over the uppermost message nodes and place their source lifelines (and connected ones)
        for (LNode uppermostMessageNode : uppermostMessageNodes) {
            SMessage uppermostMessage = messageFor(uppermostMessageNode);
            
            if (uppermostMessage.getSourceLifeline().getHorizontalSlot() == UNPROCESSED) {
                // Place the lifeline and follow its outgoing messages
                placeSourceLifelineAndSuccessors(uppermostMessage);
            }
        }
        
        // There may be lifelines left that we need to take care of.
        handleRemainingLifelines(context);
        
        assert context.sgraph.getLifelines().stream().allMatch(ll -> ll.getHorizontalSlot() != UNPROCESSED);
        
        // Add the newly sorted lifelines back to the original list
        context.sgraph.getLifelines().clear();
        context.sgraph.getLifelines().addAll(sortedLifelines);
        
        // Reset
        sortedLifelines = null;

        progressMonitor.done();
    }

    /**
     * For all remaining unprocessed lifelines, one of two conditions is true:
     * <ol>
     * <li>Their upper messages form a dependency cycle, thus yielding no LNodes without incoming edges.</li>
     * <li>They have no messages.</li>
     * </ol>
     * <p>
     * We sort the remaining lifelines descendingly by their number of outgoing edges (breaking ties by sorting
     * descendingly by their number of incoming edges) and work our way through that. This automatically results in
     * empty lifelines being placed to the right.
     * </p>
     */
    private void handleRemainingLifelines(final LayoutContext context) {
        Iterator<SLifeline> remainingLifelines = context.sgraph.getLifelines().stream()
                .filter(ll -> ll.getHorizontalSlot() == UNPROCESSED)
                .sorted(LayerBasedLifelineSorter::compareLifelinesByOutgoingEdges).iterator();
        
        while (remainingLifelines.hasNext()) {
            SLifeline currLifeline = remainingLifelines.next();
            if (currLifeline.getHorizontalSlot() == UNPROCESSED) {
                if (currLifeline.getNumberOfOutgoingMessages() == 0) {
                    // Simply assign it directly
                    assignToNextPosition(currLifeline);
                    
                } else {
                    // Try to find the first message that leads to an unplaced successor
                    SMessage uppermostMessage = uppermostOutgoingMessageToUnprocessedLifeline(currLifeline);
                    if (uppermostMessage == null) {
                        // We didn't find one, so simply use the topmost outgoing message
                        uppermostMessage = currLifeline.getOutgoingMessages().iterator().next();
                    }
                    
                    placeSourceLifelineAndSuccessors(uppermostMessage);
                }
            }
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    // Horizontal Slot Assignment
    
    /**
     * Assign the message's source lifeline to the next free horizontal slot. Then follow the trail of its outgoing
     * messages to place its unplaced successor lifelines.
     */
    private void placeSourceLifelineAndSuccessors(final SMessage message) {
        assert message.getSourceLifeline().getHorizontalSlot() == UNPROCESSED;
        
        assignToNextPosition(message.getSourceLifeline());
        
        SMessage currMsg = message;
        do {
            // The target of this lifeline is set to next position
            SLifeline targetLifeline = currMsg.getTargetLifeline();
            assignToNextPosition(targetLifeline);

            // Find the uppermost outgoing message of the next lifeline
            currMsg = uppermostOutgoingMessageToUnprocessedLifeline(targetLifeline);
        } while (currMsg != null);
    }
    
    /**
     * Assign the given lifeline to the next position.
     */
    private void assignToNextPosition(final SLifeline lifeline) {
        if (lifeline.getHorizontalSlot() == UNPROCESSED) {
            lifeline.setHorizontalSlot(sortedLifelines.size());
            sortedLifelines.add(lifeline);
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    // Finding uppermost messages
    
    /**
     * Returns a list of messages that are (locally) uppermost messages. That is, their respective layered node has no
     * predecessor. The list is sorted descendingly by a property of the source lifelines of the messages: the
     * difference of outgoing minus incoming messages. The idea is to try and get more messages to point rightwards
     * than leftwards.
     */
    private List<LNode> uppermostMessagesSortedBySuitability(LayoutContext context) {
        return context.lgraph.getLayerlessNodes().stream()
                .filter(node -> !node.getIncomingEdges().iterator().hasNext())
                .sorted(LayerBasedLifelineSorter::compareMessageNodesByIncidentMessageDiff)
                .collect(Collectors.toList());
    }
    
    /**
     * Returns the first (uppermost) message leaving the given lifeline that heads to an as-yet unprocessed lifeline,
     * or {@code null} if no such message exists.
     */
    private SMessage uppermostOutgoingMessageToUnprocessedLifeline(final SLifeline lifeline) {
        for (SMessage outgoingMessage : lifeline.getOutgoingMessages()) {
            if (outgoingMessage.getTargetLifeline().getHorizontalSlot() == UNPROCESSED) {
                return outgoingMessage;
            }
        }
        
        return null;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Sorting
    
    /**
     * Sorts two nodes descending by the difference of outgoing minus incoming messages of their source lifelines.
     */
    private static int compareMessageNodesByIncidentMessageDiff(final LNode node1, final LNode node2) {
        SLifeline ll1 = sourceLifelineFor(node1);
        int diff1 = ll1.getNumberOfOutgoingMessages() - ll1.getNumberOfIncomingMessages();

        SLifeline ll2 = sourceLifelineFor(node2);
        int diff2 = ll2.getNumberOfOutgoingMessages() - ll2.getNumberOfIncomingMessages();
        
        return diff2 - diff1;
    }

    /**
     * Sorts lifelines first descendingly by their number of outgoing messages, and then descendingly by their number
     * of incoming messages.
     */
    private static int compareLifelinesByOutgoingEdges(final SLifeline ll1, final SLifeline ll2) {
        // Sort descendingly by number of outgoing messages
        int outgoingMessageDiff = ll2.getNumberOfOutgoingMessages() - ll1.getNumberOfOutgoingMessages();
        
        if (outgoingMessageDiff != 0) {
            return outgoingMessageDiff;
        } else {
            // Sort descendingly by number of incoming messages
            return ll2.getNumberOfIncomingMessages() - ll1.getNumberOfIncomingMessages();
        }
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Utilities

    /**
     * Returns the message represented by the given node.
     */
    private static SMessage messageFor(final LNode messageNode) {
        return (SMessage) messageNode.getProperty(InternalSequenceProperties.ORIGIN);
    }
    
    /**
     * Returns the source lifeline of the message represented by the given node.
     */
    private static SLifeline sourceLifelineFor(final LNode messageNode) {
        return messageFor(messageNode).getSourceLifeline();
    }
    
}
