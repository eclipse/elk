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
package org.eclipse.elk.alg.sequence.p4sorting;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
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

    /** List of lifelines to be processed. */
    private List<SLifeline> unprocessedLifelines;
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

        // Abort, if no layers are set (e.g. outer node)
        if (context.lgraph.getLayers().size() == 0) {
            return;
        }
        
        unprocessedLifelines = context.sgraph.getLifelines();
        sortedLifelines = Lists.newArrayListWithCapacity(unprocessedLifelines.size());

        // Add the layerIndex Property to messages
        addLayerToMessages(context.lgraph);

        while (!unprocessedLifelines.isEmpty()) {
            // Find the message with the uppermost position whose source has not been set
            SMessage uppermostMessage = findUppermostMessage(context.lgraph);
            if (uppermostMessage == null) {
                // Left lifelines are not connected by any message => assign positions arbitrarily
                assignToNextPosition(unprocessedLifelines.get(0));
                continue;
            }
            
            // Append Lifeline to the ordered lifelines list
            SLifeline sourceLifeline = uppermostMessage.getSource();
            assignToNextPosition(sourceLifeline);

            do {
                // The target of this lifeline is set to next position
                SLifeline targetLifeline = uppermostMessage.getTarget();
                assignToNextPosition(targetLifeline);

                // Find the uppermost outgoing message of the next lifeline
                uppermostMessage = findUppermostOutgoingMessage(context.lgraph, targetLifeline);
            } while (uppermostMessage != null);
        }
        
        // Add the newly sorted lifelines back to the original list
        assert context.sgraph.getLifelines().isEmpty();
        context.sgraph.getLifelines().addAll(sortedLifelines);
        
        // Reset
        unprocessedLifelines = null;
        sortedLifelines = null;

        progressMonitor.done();
    }

    /**
     * Annotate the messages with a layer number.
     * 
     * TODO This would be a good candidate for an intermediate processor.
     * 
     * @param lgraph
     *            the layered graph
     */
    private void addLayerToMessages(final LGraph lgraph) {
        List<Layer> layers = lgraph.getLayers();
        
        for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++) {
            for (LNode node : layers.get(layerIndex)) {
                SMessage message = (SMessage) node.getProperty(InternalSequenceProperties.ORIGIN);
                if (message != null) {
                    message.setMessageLayer(layerIndex);
                }
            }
        }
    }
    
    /**
     * Select lifeline x with outgoing message m_0 in the uppermost layer. If there are different
     * messages in that layer, select lifeline with best incoming/outgoing relation (more outgoing
     * messages are desirable).
     * 
     * @param lgraph
     *            the layered graph
     * @return the uppermost message
     */
    private SMessage findUppermostMessage(final LGraph lgraph) {
        List<LNode> candidates = new LinkedList<LNode>();
        for (Layer layer : lgraph) {
            for (LNode node : layer) {
                SMessage message = (SMessage) node.getProperty(InternalSequenceProperties.ORIGIN);
                if (message != null && unprocessedLifelines.contains(message.getSource())) {
                    candidates.add(node);
                }
            }
            
            if (candidates.size() == 1) {
                // If there is only one candidate, return it
                return (SMessage) candidates.get(0).getProperty(InternalSequenceProperties.ORIGIN);
                
            } else if (!candidates.isEmpty()) {
                // If there are more candidates, check, which one's source has the best in/out relation
                SMessage bestOne = (SMessage) candidates.get(0).getProperty(InternalSequenceProperties.ORIGIN);
                int bestRelation = Integer.MIN_VALUE;
                
                for (LNode node : candidates) {
                    SMessage candidate = (SMessage) node.getProperty(InternalSequenceProperties.ORIGIN);
                    int relation = candidate.getSource().getNumberOfOutgoingMessages()
                            - candidate.getSource().getNumberOfIncomingMessages();
                    
                    if (relation > bestRelation) {
                        bestRelation = relation;
                        bestOne = candidate;
                    }
                }
                
                return bestOne;
            }
        }
        
        // The left lifelines are not connected by any message
        return null;
    }

    /**
     * Find the uppermost message of the given lifeline that is pointing at a lifeline that was not
     * already set.
     * 
     * @param lgraph
     *            the layered graph
     * @param lifeline
     *            the current lifeline
     * @return the uppermost outgoing message
     */
    private SMessage findUppermostOutgoingMessage(final LGraph lgraph, final SLifeline lifeline) {
        SMessage uppermostMessage = null;
        int bestLayer = lgraph.getLayers().size();
        
        for (SMessage outgoingMessage : lifeline.getOutgoingMessages()) {
            if (outgoingMessage.getMessageLayer() < bestLayer) {
                // Check if target lifeline was already set
                if (unprocessedLifelines.contains(outgoingMessage.getTarget())) {
                    uppermostMessage = outgoingMessage;
                    bestLayer = outgoingMessage.getMessageLayer();
                }
            }
        }
        
        return uppermostMessage;
    }

    /**
     * Place the given lifeline to the next position.
     * 
     * @param lifeline
     *            the next lifeline to be placed
     */
    private void assignToNextPosition(final SLifeline lifeline) {
        if (!sortedLifelines.contains(lifeline)) {
            lifeline.setHorizontalSlot(sortedLifelines.size());
            sortedLifelines.add(lifeline);
            unprocessedLifelines.remove(lifeline);
        }
    }
}
