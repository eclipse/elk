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
package org.eclipse.elk.alg.sequence.p5xcoordinates;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SExecution;
import org.eclipse.elk.alg.sequence.graph.SLabel;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.options.MessageType;
import org.eclipse.elk.alg.sequence.options.SequenceDiagramOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Calculates x coordinates for many objects in a sequence diagram.
 */
public class XCoordinateCalculator implements ILayoutPhase<SequencePhases, LayoutContext> {
    
    /** The minimum length messages must have. */
    private static final double MIN_MESSAGE_LENGTH = 15;
    
    /** Saves for each message how much of its length is unusable for labels. */
    private Map<SMessage, Double> unusableMessageSpace;
    

    @Override
    public LayoutProcessorConfiguration<SequencePhases, LayoutContext> getLayoutProcessorConfiguration(
            final LayoutContext graph) {
        
        return LayoutProcessorConfiguration.create();
    }

    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Calculate x coordinates", 1);
        
        if (context.sgraph.getLifelines().isEmpty()) {
            context.sgraph.getSize().x = context.sgraph.getPadding().left + context.sgraph.getPadding().right;
        } else {
            unusableMessageSpace = new HashMap<>();
            processLifelines(context);
            unusableMessageSpace = null;
        }

        progressMonitor.done();
    }

    private void processLifelines(final LayoutContext context) {
        assert !context.sgraph.getLifelines().isEmpty();
        
        // We always keep two lifelines around: a right and a left one. In the beginning, there is only the right one.
        Iterator<SLifeline> llIterator = context.sgraph.getLifelines().iterator();
        SLifeline leftLL = null;
        SLifeline rightLL = llIterator.next();
        
        // Check how much space we need to the left of the first lifeline and place it right there. Note that the
        // variable will always point to the actual lifeline part of the most recently placed lifeline
        double currX = spaceLeftwards(context, rightLL);
        rightLL.getPosition().x = currX - rightLL.getSize().x / 2;
        
        // For each pair of subsequent lifelines, find out how close they should be placed and then place the right
        // lifeline accordingly. In each iteration, the left lifeline has already been placed and currX starts out
        // at the left lifeline's x coordinate.
        while (llIterator.hasNext()) {
            // Advance to the next pair of lifelines
            leftLL = rightLL;
            rightLL = llIterator.next();
            
            // Compute the minimum amount of space we require from one lifeline to the next (we don't mean their
            // headers here, but the actual lifeline: the space required from one dashed line to the next)
            double minSpacing = minimumSpaceBetween(context, leftLL, rightLL);
            
            // Invoke label management on all comments and message labels with the appropriate target width and check
            // how much space we will really need. Apply that space to place the right lifeline
            currX += actualSpaceBetween(context, leftLL, rightLL, minSpacing);
            rightLL.getPosition().x = currX - rightLL.getSize().x / 2;
        }
        
        // Check how much space we need to the right of the last lifeline
        currX += spaceRightwards(context, rightLL);

        // Place any non-specific comments
        if (context.sgraph.getComments().stream().anyMatch(this::isNonSpecificComment)) {
            currX += context.lifelineSpacing;
            currX = placeNonSpecificComments(context, currX);
        }
        
        // Set the graph's size
        context.sgraph.getSize().x = currX + context.sgraph.getPadding().right;
        
        // By now, all lifelines were placed at their final coordinates. Place all of their incident elements
        context.sgraph.getLifelines().stream().forEach(ll -> applyLifelineCoordinates(context, ll));
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Space Around Set of Lifelines
    
    /**
     * Returns the amount of space to be left leftwards of the actual lifeline part of the given lifeline. That
     * lifeline is assumed to be the leftmost lifeline.
     */
    private double spaceLeftwards(final LayoutContext context, final SLifeline sLifeline) {
        assert context.sgraph.getLifelines().get(0) == sLifeline;
        
        return context.sgraph.getPadding().left + sLifeline.getSize().x / 2;
    }
    
    /**
     * Returns the amount of space to be left rightwards of the actual lifeline part of the given lifeline. That
     * lifeline is assumed to be the rightmost lifeline. The space will only include what is necessary to cover the
     * lifeline's elements, not any spacing required to place non-specific comments or padding to be left to the
     * interaction's right border.
     */
    private double spaceRightwards(final LayoutContext context, final SLifeline sLifeline) {
        assert context.sgraph.getLifelines().get(context.sgraph.getLifelines().size() - 1) == sLifeline;
        
        double space = sLifeline.getSize().x / 2;
        
        for (SMessage outMsg : sLifeline.getOutgoingMessages()) {
            if (outMsg.isSelfMessage()) {
                int incidentExecutions =
                        Math.max(sourceExecutionCount(outMsg), targetExecutionCount(outMsg));
                double msgWidth = incidentExecutions * context.executionWidth / 2 + context.selfMessageWidth;
                
                if (outMsg.getLabel() != null) {
                    msgWidth += outMsg.getLabel().getSize().x;
                    
                    if (!hasInlineLabel(outMsg)) {
                        msgWidth += context.labelSpacing;
                    }
                }
                
                space = Math.max(space, msgWidth);
            }
        }
        
        return space;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Minimum Space Between Lifelines
    
    /**
     * Computes the amount of space required between the actual lifeline parts of the given two lifelines. This will
     * influence the leftmost x coordinate where the right lifeline might conceivably be placed. It will be at least as
     * large as required to adhere to lifeline spacings taking the lifeline headers into account.
     */
    private double minimumSpaceBetween(final LayoutContext context, final SLifeline leftLL, final SLifeline rightLL) {
        // This is the minimum space we will definitely need. The elements on the lifelines may increase that.
        double minSpacing = (leftLL.getSize().x + rightLL.getSize().x) / 2 + context.lifelineSpacing;
        
        // Process all messages relevant to the space required between the two lifelines. Those are messages that leave
        // the left lifeline rightwards, messages that leave the right lifeline leftwards, and messages that enter the
        // right lifeline from the left (which haven't already been processed as messages that leave the left lifeline
        // rightwards)
        for (SMessage outMsg : leftLL.getOutgoingMessages()) {
            if (pointsRightwards(outMsg) || outMsg.isSelfMessage()) {
                minSpacing = Math.max(minSpacing, minOutgoingRightwardsMessageLength(context, outMsg));
            }
        }

        for (SMessage outMsg : rightLL.getOutgoingMessages()) {
            if (pointsLeftwards(outMsg)) {
                minSpacing = Math.max(minSpacing, minOutgoingLeftwardsMessageLength(context, outMsg));
            }
        }
        
        for (SMessage inMsg : rightLL.getIncomingMessages()) {
            if (pointsRightwards(inMsg)) {
                minSpacing = Math.max(minSpacing, minIncomingRightwardsMessageLength(context, inMsg));
            }
        }
        
        return minSpacing;
    }
    
    /**
     * Computes the minimum length required for an outgoing rightwards-pointing message. Given a pair of lifelines, the
     * message is assumed to leave the left one.
     */
    private double minOutgoingRightwardsMessageLength(final LayoutContext context, final SMessage outMsg) {
        double minMsgLength = 0;
        
        // Pre-compute information about the message
        boolean isCreateMessage = outMsg.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) == MessageType.CREATE;
        
        // If the message is incident to any execution specifications, those need space (only half of an execution
        // will ever stick leftwards out of the message's target lifeline)
        minMsgLength += sourceExecutionCount(outMsg) * context.executionWidth / 2;
        if (outMsg.connectsAdjacentLifelines()) {
            minMsgLength += Math.signum(targetExecutionCount(outMsg)) * context.executionWidth / 2;
        }
        
        // TODO Space required for comments
        
        // Space required for the message itself
        if (outMsg.isSelfMessage()) {
            minMsgLength = context.selfMessageWidth;
            
        } else {
            // If it's not a self message, but connects adjacent lifelines, it needs a minimum length
            if (outMsg.connectsAdjacentLifelines()) {
                minMsgLength += MIN_MESSAGE_LENGTH;
            }
        }
        
        // If the message is a create message, we need to take the lifeline header into account
        if (outMsg.connectsAdjacentLifelines() && isCreateMessage) {
            minMsgLength += outMsg.getTargetLifeline().getSize().x / 2;
        }
        
        // Space required for a label, provided there is one
        if (outMsg.getLabel() != null) {
            minMsgLength += 2 * context.labelSpacing;
        }
        
        // We have only computed the space necessary for all non-label components of a message. The rest will be
        // available to labels. Remember that info for later.
        unusableMessageSpace.put(outMsg, minMsgLength);
        
        return minMsgLength;
    }
    
    /**
     * Computes the minimum length required for an outgoing leftwards-pointing message. Given a pair of lifelines, the
     * message is assumed to leave the right one.
     */
    private double minOutgoingLeftwardsMessageLength(final LayoutContext context, final SMessage outMsg) {
        double minMsgLength = 0;

        // Pre-compute information about the message
        boolean isCreateMessage = outMsg.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) == MessageType.CREATE;
        
        // If the message is incident to any execution specifications, those need space (only half of an execution
        // will ever stick leftwards out of the message's source lifeline)
        minMsgLength += targetExecutionCount(outMsg) * context.executionWidth / 2;
        if (outMsg.connectsAdjacentLifelines()) {
            minMsgLength += Math.signum(sourceExecutionCount(outMsg)) * context.executionWidth / 2;
        }
        
        // TODO Space required for comments
        
        // Space required for the message itself
        assert !outMsg.isSelfMessage();
        if (outMsg.connectsAdjacentLifelines()) {
            minMsgLength += MIN_MESSAGE_LENGTH;
        }
        
        // If the message is a create message, we need to take the lifeline header into account
        if (outMsg.connectsAdjacentLifelines() && isCreateMessage) {
            minMsgLength += outMsg.getTargetLifeline().getSize().x / 2;
        }
        
        // Space required for a label, provided there is one
        if (outMsg.getLabel() != null) {
            minMsgLength += 2 * context.labelSpacing;
        }
        
        // We have only computed the space necessary for all non-label components of a message. The rest will be
        // available to labels. Remember that info for later.
        unusableMessageSpace.put(outMsg, minMsgLength);
        
        return minMsgLength;
    }

    /**
     * Computes the minimum length required for an incoming rightwards-pointing message. Given a pair of lifelines, the
     * message is assumed to enter the right one.
     */
    private double minIncomingRightwardsMessageLength(final LayoutContext context, final SMessage inMsg) {
        // TODO Implement?
        // Implementing this method properly gains importance if we allow labels to be placed near their target lifeline
        return 0;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actual Space Between Lifelines
    
    /**
     * Takes the minimum space that was computed for the two lifelines and checks if everything will really fit in
     * there by invoking label management. If not, more space will be required. That space is returned by this method
     * and will need to be applied when placing the right lifeline. 
     */
    private double actualSpaceBetween(final LayoutContext context, final SLifeline leftLL, final SLifeline rightLL,
            final double minSpacing) {
        
        // This is the actual space we'll need. We start off with the minimum space calculated previously and invoke
        // label management as we go through the elements
        double actSpacing = minSpacing;
        
        // Process all messages relevant to the space required between the two lifelines. Those are messages that leave
        // the left lifeline rightwards, messages that leave the right lifeline leftwards, and messages that enter the
        // right lifeline from the left (which haven't already been processed as messages that leave the left lifeline
        // rightwards)
        for (SMessage outMsg : leftLL.getOutgoingMessages()) {
            if (pointsRightwards(outMsg) || outMsg.isSelfMessage()) {
                actSpacing = Math.max(actSpacing, actOutgoingMessageLength(context, outMsg, minSpacing));
            }
        }

        for (SMessage outMsg : rightLL.getOutgoingMessages()) {
            if (pointsLeftwards(outMsg)) {
                actSpacing = Math.max(actSpacing, actOutgoingMessageLength(context, outMsg, minSpacing));
            }
        }
        
        for (SMessage inMsg : rightLL.getIncomingMessages()) {
            if (pointsRightwards(inMsg)) {
                actSpacing = Math.max(actSpacing, actIncomingRightwardsMessageLength(context, inMsg, minSpacing));
            }
        }
        
        return actSpacing;
    }
    
    /**
     * Invokes label management on things with the aim of not exceeding the minimum space and returns the space we will
     * actually need for the message.
     */
    private double actOutgoingMessageLength(final LayoutContext context, final SMessage outMsg,
            final double minSpacing) {
        
        // No label, no problem
        if (outMsg.getLabel() == null) {
            return minSpacing;
        }
        
        double spaceForLabel = minSpacing - unusableMessageSpace.get(outMsg);
        assert spaceForLabel >= 0;

        // Apply label management, if installed
        if (context.labelManager != null) {
            SLabel slabel = outMsg.getLabel();
            KVector newSize = context.labelManager.manageLabelSize(
                    slabel.getProperty(InternalSequenceProperties.ORIGIN),
                    spaceForLabel);
            if (newSize != null) {
                slabel.getSize().set(newSize);
            }
        }
        
        // Check if the minimum space needs to be increased (will be > 0 if we need more space)
        double delta = outMsg.getLabel().getSize().x - spaceForLabel;
        return Math.max(minSpacing, minSpacing + delta);
    }

    /**
     * Invokes label management on things with the aim of not exceeding the minimum space and returns the space we will
     * actually need for the message.
     */
    private double actIncomingRightwardsMessageLength(final LayoutContext context, final SMessage inMsg,
            final double minSpacing) {
        
        // TODO Implement?
        // Implementing this method properly gains importance if we allow labels to be placed near their target lifeline
        // Also: Target comments
        return 0;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Non-Specific Comments
    
    /**
     * Returns whether the given comment is non-specific.
     */
    private boolean isNonSpecificComment(final SComment sComment) {
        return sComment.getLifeline() == null && sComment.getReferenceMessage() == null;
    }
    
    /**
     * Places any non-specific comments at the given x coordinate below one another. Returns the x coordinate of the
     * widest comment's right border.
     */
    private double placeNonSpecificComments(final LayoutContext context, final double xPos) {
        double maxX = xPos;
        
        for (SComment sComment : context.sgraph.getComments()) {
            // We're only interested in non-specific comments
            if (!isNonSpecificComment(sComment)) {
                continue;
            }
            
            sComment.getPosition().x = xPos;
            maxX = Math.max(maxX, sComment.getPosition().x + sComment.getSize().x);
        }
        
        return maxX;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Coordinate Application

    /**
     * Assumes that lifeline has its final x coordinates and places all of its elements.
     */
    private void applyLifelineCoordinates(final LayoutContext context, final SLifeline sLifeline) {
        double currX = sLifeline.getPosition().x;
        
        // Place executions
        placeExecutions(context, sLifeline);
        
        // Place outgoing messages
        for (SMessage sMessage : sLifeline.getOutgoingMessages()) {
            // Set x coordinate
            sMessage.getSourcePosition().x = currX + sLifeline.getSize().x / 2;
            
            // Check if the message has an inline label
            SLabel sLabel = sMessage.getLabel();
            
            if (pointsRightwards(sMessage)) {
                sMessage.getSourcePosition().x += sourceExecutionCount(sMessage) * context.executionWidth / 2;
            } else if (pointsLeftwards(sMessage)) {
                sMessage.getSourcePosition().x -=
                        Math.signum(sourceExecutionCount(sMessage)) * context.executionWidth / 2;
            } else if (sMessage.isSelfMessage()) {
                double topLeftX = currX
                        + sLifeline.getSize().x / 2
                        + sourceExecutionCount(sMessage) * context.executionWidth / 2;
                double bottomLeftX = currX
                        + sLifeline.getSize().x / 2
                        + targetExecutionCount(sMessage) * context.executionWidth / 2;
                double rightX = Math.max(topLeftX, bottomLeftX) + context.selfMessageWidth;
                
                // If the message has an inline label, its bend points need to be shifted accordingly
                if (hasInlineLabel(sMessage)) {
                    rightX += sLabel.getSize().x / 2;
                }
                
                sMessage.getSourcePosition().x = topLeftX;
                sMessage.getTargetPosition().x = bottomLeftX;
                
                // The message's bend points will have a y coordinate assigned to them in the Y coordinates phase
                sMessage.getBendPoints().add(new KVector(rightX, 0));
                sMessage.getBendPoints().add(new KVector(rightX, 0));
            }
            
            // Place message label
            if (sLabel != null) {
                if (pointsRightwards(sMessage)) {
                    sLabel.getPosition().x = sMessage.getSourcePosition().x + context.labelSpacing;
                } else if (pointsLeftwards(sMessage)) {
                    sLabel.getPosition().x = sMessage.getSourcePosition().x - context.labelSpacing - sLabel.getSize().x;
                } else if (sMessage.isSelfMessage()) {
                    if (hasInlineLabel(sMessage)) {
                        sLabel.getPosition().x = sMessage.getBendPoints().get(0).x - sLabel.getSize().x / 2;
                    } else {
                        sLabel.getPosition().x = sMessage.getBendPoints().get(0).x + context.labelSpacing;
                    }
                }
            }
        }
        
        // Place incoming messages
        for (SMessage sMessage : sLifeline.getIncomingMessages()) {
            // Self messages have already been processed as outgoing messages
            if (sMessage.isSelfMessage()) {
                continue;
            }
            
            // Set x coordinate
            if (sMessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) == MessageType.CREATE) {
                sMessage.getTargetPosition().x = currX;
            } else {
                sMessage.getTargetPosition().x = currX + sLifeline.getSize().x / 2;
                
                if (pointsRightwards(sMessage)) {
                    sMessage.getTargetPosition().x -=
                            Math.signum(targetExecutionCount(sMessage)) * context.executionWidth / 2;
                } else if (pointsLeftwards(sMessage)) {
                    sMessage.getTargetPosition().x +=
                            targetExecutionCount(sMessage) * context.executionWidth / 2;
                }
            }
        }

        // TODO Place comments
    }

    /**
     * Set x position and width of all executions.
     */
    private void placeExecutions(final LayoutContext context, final SLifeline sLifeline) {
        // This is where we'll place level-0 executions
        double baseX = sLifeline.getPosition().x + sLifeline.getSize().x / 2 - context.executionWidth / 2;
        
        for (SExecution sExecution : sLifeline.getExcecutions()) {
            sExecution.getPosition().x = baseX + sExecution.getSlot() * context.executionWidth / 2;
            sExecution.getSize().x = context.executionWidth;
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utilities
    
    private boolean pointsRightwards(final SMessage msg) {
        return msg.getSourceLifeline().getHorizontalSlot() < msg.getTargetLifeline().getHorizontalSlot();
    }
    
    private boolean pointsLeftwards(final SMessage msg) {
        return msg.getSourceLifeline().getHorizontalSlot() > msg.getTargetLifeline().getHorizontalSlot();
    }
    
    private boolean hasInlineLabel(final SMessage msg) {
        SLabel sLabel = msg.getLabel();
        return sLabel != null && sLabel.getProperty(SequenceDiagramOptions.EDGE_LABELS_INLINE);
    }
    
    private int sourceExecutionCount(final SMessage msg) {
        int count = msg.getSourceExecutions().size();
        
        // If the message is a self message and has, in fact, executions, we need to dig more deeply
        if (count > 0 && msg.isSelfMessage()) {
            long startsExecutions = msg.getSourceExecutions().stream()
                    .filter(exec -> startsExecution(msg, exec))
                    .count();
            count -= startsExecutions;
        }
        
        return count;
    }
    
    private boolean startsExecution(final SMessage msg, final SExecution exec) {
        // A message starts an execution if no message is part of the execution that is on a layer above the message
        int minLayer = exec.getMessages().stream()
            .map(execMsg -> execMsg.getProperty(InternalSequenceProperties.LAYERED_NODE))
            .mapToInt(lnode -> lnode.getLayer().getIndex())
            .min()
            .orElse(-1);
        assert minLayer > -1;
        
        return minLayer == msg.getProperty(InternalSequenceProperties.LAYERED_NODE).getLayer().getIndex();
    }
    
    private int targetExecutionCount(final SMessage msg) {
        int count = msg.getTargetExecutions().size();
        
        // If the message is a self message and has, in fact, executions, we need to dig more deeply
        if (count > 0 && msg.isSelfMessage()) {
            long endsExecutions = msg.getTargetExecutions().stream()
                    .filter(exec -> endsExecution(msg, exec))
                    .count();
            count -= endsExecutions;
        }
        
        return count;
    }
    
    private boolean endsExecution(final SMessage msg, final SExecution exec) {
        // A message ends an execution if no message is part of the execution that is on a layer below the message
        int maxLayer = exec.getMessages().stream()
            .map(execMsg -> execMsg.getProperty(InternalSequenceProperties.LAYERED_NODE))
            .mapToInt(lnode -> lnode.getLayer().getIndex())
            .max()
            .orElse(-1);
        assert maxLayer > -1;
        
        return maxLayer == msg.getProperty(InternalSequenceProperties.LAYERED_NODE).getLayer().getIndex();
    }
}
