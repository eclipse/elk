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
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.sequence.ISequenceLayoutProcessor;
import org.eclipse.elk.alg.sequence.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Lists;

/**
 * Lifelinesorting algorithm that sorts the lifelines according to their uppermost outgoing
 * messages. The "source" lifeline is placed leftmost, the successor lifelines following.
 * 
 * @author grh
 * @kieler.design proposed grh
 * @kieler.rating proposed yellow grh
 */
public final class LayerBasedLifelineSorter implements ISequenceLayoutProcessor {
    /** The next position a lifeline will be placed in. */
    private int nextPosition;
    /** List of lifelines to be processed. */
    private List<SLifeline> unprocessedLifelines = Lists.newArrayList();
    /** List of lifelines that have already been sorted. */
    private List<SLifeline> sortedLifelines;

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Layer based lifeline sorting", 1);

        unprocessedLifelines.addAll(context.sgraph.getLifelines());
        sortedLifelines = new LinkedList<SLifeline>();

        if (context.lgraph.getLayers().size() == 0) {
            // Abort, if no layers are set (e.g. outer node)
            context.lifelineOrder = unprocessedLifelines;
            return;
        }

        // Add the layerIndex Property to messages
        addLayerToMessages(context.lgraph);

        nextPosition = 0;

        while (!unprocessedLifelines.isEmpty()) {
            // Find the message with the uppermost position whose source has not been set
            SMessage uppermostMessage = findUppermostMessage(context.lgraph);
            if (uppermostMessage == null) {
                // Left lifelines are not connected by any message => assign positions arbitrarily
                assignToNextPosition(unprocessedLifelines.get(0));
                continue;
            }
            SLifeline x = uppermostMessage.getSource();

            // Append Lifeline to the ordered lifelines list
            assignToNextPosition(x);

            do {
                // The target of this lifeline is set to next position
                x = uppermostMessage.getTarget();

                // Append Lifeline to the ordered lifelines list
                assignToNextPosition(x);

                // Find the uppermost outgoing message of the next lifeline
                uppermostMessage = findUppermostOutgoingMessage(context.lgraph, x);
            } while (uppermostMessage != null);
        }
        
        context.lifelineOrder = sortedLifelines;

        progressMonitor.done();
    }

    /**
     * Place the given lifeline to the next position.
     * 
     * @param lifeline
     *            the next lifeline to be placed
     */
    private void assignToNextPosition(final SLifeline lifeline) {
        if (!sortedLifelines.contains(lifeline)) {
            sortedLifelines.add(lifeline);
            lifeline.setHorizontalSlot(nextPosition);
            nextPosition++;
            unprocessedLifelines.remove(lifeline);
        }
    }

    /**
     * Annotate the messages with a layer number.
     * 
     * @param lgraph
     *            the layered graph
     */
    private void addLayerToMessages(final LGraph lgraph) {
        for (Layer layer : lgraph.getLayers()) {
            int layerIndex = layer.getIndex();
            for (LNode node : layer.getNodes()) {
                SMessage message = (SMessage) node.getProperty(InternalProperties.ORIGIN);
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
        for (Layer layer : lgraph.getLayers()) {
            for (LNode node : layer.getNodes()) {
                SMessage message = (SMessage) node.getProperty(InternalProperties.ORIGIN);
                if (message != null && unprocessedLifelines.contains(message.getSource())) {
                    candidates.add(node);
                }
            }
            if (candidates.size() == 1) {
                // If there is only one candidate, return it
                return (SMessage) candidates.get(0).getProperty(InternalProperties.ORIGIN);
            } else if (!candidates.isEmpty()) {
                // If there are more candidates, check, which ones source has the best in/out
                // relation
                SMessage bestOne = (SMessage) candidates.get(0).getProperty(InternalProperties.ORIGIN);
                int bestRelation = Integer.MIN_VALUE;
                for (LNode node : candidates) {
                    SMessage candidate = (SMessage) node.getProperty(InternalProperties.ORIGIN);
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
                // check if target lifeline was already set
                if (unprocessedLifelines.contains(outgoingMessage.getTarget())) {
                    uppermostMessage = outgoingMessage;
                    bestLayer = outgoingMessage.getMessageLayer();
                }
            }
        }
        return uppermostMessage;
    }
}
