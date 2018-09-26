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
import java.util.Set;

import org.eclipse.elk.alg.common.nodespacing.NodeLabelAndSizeCalculator;
import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SDestruction;
import org.eclipse.elk.alg.sequence.graph.SExecution;
import org.eclipse.elk.alg.sequence.graph.SGraphAdapters;
import org.eclipse.elk.alg.sequence.graph.SLabel;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.options.MessageCommentAlignment;
import org.eclipse.elk.alg.sequence.options.MessageType;
import org.eclipse.elk.alg.sequence.options.SequenceDiagramOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Calculates x coordinates for many objects in a sequence diagram.
 */
public class XCoordinateCalculator implements ILayoutPhase<SequencePhases, LayoutContext> {
    
    /** The minimum length messages must have. */
    private static final double MIN_MESSAGE_LENGTH = 15;
    /** Minimum width of comments not specific to any particular diagram element. */
    private static final double MIN_NON_SPECIFIC_COMMENT_WIDTH = 30;
    
    /** Saves for each incoming message how much of its length is unusable for labels. */
    private Map<SMessage, Double> unusableIncomingMessageSpace;
    /** Saves for each outgoing message how much of its length is unusable for labels. */
    private Map<SMessage, Double> unusableOutgoingMessageSpace;
    

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
            unusableIncomingMessageSpace = new HashMap<>();
            unusableOutgoingMessageSpace = new HashMap<>();
            
            processLifelines(context);
            
            unusableIncomingMessageSpace = null;
            unusableOutgoingMessageSpace = null;
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
        if (context.sgraph.getComments().stream().anyMatch(c -> c.isNonSpecific())) {
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
                    applyLabelManagement(context, outMsg.getLabel(), MIN_MESSAGE_LENGTH);
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
        // the left lifeline pointing rightwards or enter it pointing leftwards as well as messages that leave the
        // right lifeline pointing leftwards or enter it pointing rightwards.
        for (SMessage outMsg : leftLL.getOutgoingMessages()) {
            if (pointsRightwards(outMsg) || outMsg.isSelfMessage()) {
                minSpacing = Math.max(minSpacing, minOutgoingMessageLength(context, outMsg));
            }
        }

        for (SMessage outMsg : rightLL.getOutgoingMessages()) {
            if (pointsLeftwards(outMsg)) {
                minSpacing = Math.max(minSpacing, minOutgoingMessageLength(context, outMsg));
            }
        }
        
        for (SMessage inMsg : leftLL.getIncomingMessages()) {
            if (pointsLeftwards(inMsg) && !inMsg.connectsAdjacentLifelines()) {
                minSpacing = Math.max(minSpacing, minIncomingMessageLength(context, inMsg));
            }
        }
        
        for (SMessage inMsg : rightLL.getIncomingMessages()) {
            if (pointsRightwards(inMsg) && !inMsg.connectsAdjacentLifelines()) {
                minSpacing = Math.max(minSpacing, minIncomingMessageLength(context, inMsg));
            }
        }
        
        return minSpacing;
    }
    
    /**
     * Computes the minimum length required for an outgoing rightwards-pointing message. Given a pair of lifelines, the
     * message is assumed to leave the left one if it's a right-pointing message, or the right one if it's a
     * left-pointing message.
     */
    private double minOutgoingMessageLength(final LayoutContext context, final SMessage outMsg) {
        // We calculate two kinds of space: the space between adjacent lifelines that the message spans, but needs to
        // be left free of labels and comments (surroundingSpace) and the space required around or between labels or
        // comments (labelingSpace). The two will be merged into the min message length at the end
        double surroundingSpace = 0;
        double labelingSpace = 0;
        
        // Pre-compute information about the message
        boolean isCreateMessage = outMsg.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) == MessageType.CREATE;
        
        // If the message is incident to any execution specifications, those need space (only half of an execution
        // will ever stick leftwards out of the message's target lifeline)
        surroundingSpace += sourceExecutionCount(outMsg) * context.executionWidth / 2;
        if (outMsg.connectsAdjacentLifelines()) {
            surroundingSpace += Math.signum(targetExecutionCount(outMsg)) * context.executionWidth / 2;
        }
        
        // Space required for the message itself
        if (outMsg.isSelfMessage()) {
            // Self messages need a certain width to be routed
            surroundingSpace += context.selfMessageWidth;
            
        } else if (outMsg.connectsAdjacentLifelines() && isCreateMessage) {
            // If the message is a create message that creates the adjacent lifeline, we need to take its lifeline
            // header into account
            surroundingSpace += outMsg.getTargetLifeline().getSize().x / 2;
        }
        
        // Space required for a label, provided there is one
        if (outMsg.getLabel() != null) {
            labelingSpace += 2 * context.labelSpacing;
        }
        
        // Find out how many different types of comments we have and reserve space around and between them
        labelingSpace = Math.max(labelingSpace, (commentTypeCount(outMsg) + 1) * context.labelSpacing);
        
        // We have only computed the space necessary for all non-label components of a message. The rest will be
        // available to labels. Remember that info for later.
        unusableOutgoingMessageSpace.put(outMsg, surroundingSpace + labelingSpace);
        
        // The value we return must ensure that there is enough space left beyond the surrounding space
        return surroundingSpace + Math.max(labelingSpace, MIN_MESSAGE_LENGTH);
    }

    /**
     * Computes the minimum length required for an incoming message that doesn't connect adjacent lifelines. Given a
     * pair of lifelines, the message is assumed to enter the right one if it's a right-pointing message, and enter the
     * left one if it's a left-pointing message.
     */
    private double minIncomingMessageLength(final LayoutContext context, final SMessage inMsg) {
        // We calculate two kinds of space: the space between adjacent lifelines that the message spans, but needs to
        // be left free of labels and comments (surroundingSpace) and the space required around or between labels or
        // comments (labelingSpace). The two will be merged into the min message length at the end
        double surroundingSpace = 0;
        double labelingSpace = 0;
        
        // Pre-compute information about the message
        boolean isCreateMessage = inMsg.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) == MessageType.CREATE;
        
        if (inMsg.connectsAdjacentLifelines() && isCreateMessage) {
            // If the message is a create message, we need to take the lifeline header into account
            surroundingSpace += inMsg.getTargetLifeline().getSize().x / 2;
        }
        
        // If there is a target comment, we need to reserve space around it
        boolean targetCommentExists = inMsg.getComments().stream()
                .anyMatch(c -> c.getAlignment() == MessageCommentAlignment.TARGET);
        if (targetCommentExists) {
            labelingSpace += 2 * context.labelSpacing;
        }
        
        // We have only computed the space necessary for all non-label components of a message. The rest will be
        // available to labels. Remember that info for later.
        unusableIncomingMessageSpace.put(inMsg, surroundingSpace + labelingSpace);
        
        return surroundingSpace + labelingSpace;
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
        // the left lifeline pointing rightwards or enter it pointing leftwards as well as messages that leave the
        // right lifeline pointing leftwards or enter it pointing rightwards.
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
        
        for (SMessage inMsg : leftLL.getIncomingMessages()) {
            if (pointsLeftwards(inMsg) && !inMsg.connectsAdjacentLifelines()) {
                actSpacing = Math.max(actSpacing, actIncomingMessageLength(context, inMsg, minSpacing));
            }
        }
        
        for (SMessage inMsg : rightLL.getIncomingMessages()) {
            if (pointsRightwards(inMsg) && !inMsg.connectsAdjacentLifelines()) {
                actSpacing = Math.max(actSpacing, actIncomingMessageLength(context, inMsg, minSpacing));
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
        
        double spaceAvailableForText = minSpacing - unusableOutgoingMessageSpace.get(outMsg);
        assert spaceAvailableForText >= 0;
        
        double spaceRequiredForText = 0;

        // Apply label management to label
        if (outMsg.getLabel() != null) {
            applyLabelManagement(context, outMsg.getLabel(), spaceAvailableForText);
            spaceRequiredForText = Math.max(spaceRequiredForText, outMsg.getLabel().getSize().x);
        }
        
        // Apply label management to comments
        int commentTypeCount = commentTypeCount(outMsg);
        double spaceAvailableForComment = spaceAvailableForText;
        if (commentTypeCount > 1) {
            // Space between adjacent comments
            spaceAvailableForComment = (spaceAvailableForComment - context.labelSpacing) / 2;
        }
        
        double maxSourceCommentWidth = 0;
        double maxTargetCommentWidth = 0;
        for (SComment sComment : outMsg.getComments()) {
            // We're interested in source comments and, if the message connects adjacent lifelines, target comments
            if (sComment.getAlignment() == MessageCommentAlignment.SOURCE) {
                applyLabelManagement(context, sComment, spaceAvailableForComment);
                maxSourceCommentWidth = Math.max(maxSourceCommentWidth, sComment.getSize().x);
            } else if (outMsg.connectsAdjacentLifelines()) {
                applyLabelManagement(context, sComment, spaceAvailableForComment);
                maxTargetCommentWidth = Math.max(maxTargetCommentWidth, sComment.getSize().x);
            }
        }
        
        // If there are both source and target comments, we need to take the space between them into account as well
        double commentSpacing = maxSourceCommentWidth > 0 && maxTargetCommentWidth > 0 ? context.labelSpacing : 0;
        spaceRequiredForText = Math.max(spaceRequiredForText,
                maxSourceCommentWidth + maxTargetCommentWidth + commentSpacing);
        
        // Check if the minimum space needs to be increased (will be > 0 if we need more space)
        double delta = spaceRequiredForText - spaceAvailableForText;
        return Math.max(minSpacing, minSpacing + delta);
    }

    /**
     * Invokes label management on things with the aim of not exceeding the minimum space and returns the space we will
     * actually need for the message.
     */
    private double actIncomingMessageLength(final LayoutContext context, final SMessage inMsg,
            final double minSpacing) {
        
        double spaceAvailableForText = minSpacing - unusableIncomingMessageSpace.get(inMsg);
        assert spaceAvailableForText >= 0;
        
        double spaceRequiredForText = 0;

        // Apply label management to target comments
        for (SComment sComment : inMsg.getComments()) {
            if (sComment.getAlignment() == MessageCommentAlignment.TARGET) {
                applyLabelManagement(context, sComment, spaceAvailableForText);
                spaceRequiredForText = Math.max(spaceRequiredForText, sComment.getSize().x);
            }
        }
        
        // Check if the minimum space needs to be increased (will be > 0 if we need more space)
        double delta = spaceRequiredForText - spaceAvailableForText;
        return Math.max(minSpacing, minSpacing + delta);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Non-Specific Comments
    
    /**
     * Places any non-specific comments at the given x coordinate below one another. Returns the x coordinate of the
     * widest comment's right border.
     */
    private double placeNonSpecificComments(final LayoutContext context, final double xPos) {
        double maxX = xPos;
        
        for (SComment sComment : context.sgraph.getComments()) {
            // We're only interested in non-specific comments
            if (!sComment.isNonSpecific()) {
                continue;
            }
            
            sComment.getPosition().x = xPos;
            applyLabelManagement(context, sComment, MIN_NON_SPECIFIC_COMMENT_WIDTH);
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
            
            // Place message source comments
            for (SComment sComment : sMessage.getComments()) {
                if (sComment.getAlignment() == MessageCommentAlignment.SOURCE) {
                    if (pointsRightwards(sMessage)) {
                        sComment.getPosition().x = sMessage.getSourcePosition().x + context.labelSpacing;
                    } else if (pointsLeftwards(sMessage)) {
                        sComment.getPosition().x = sMessage.getSourcePosition().x - context.labelSpacing
                                - sComment.getSize().x;
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
            
            // Place message target comments
            for (SComment sComment : sMessage.getComments()) {
                if (sComment.getAlignment() == MessageCommentAlignment.TARGET) {
                    if (pointsRightwards(sMessage)) {
                        sComment.getPosition().x = sMessage.getTargetPosition().x - context.labelSpacing
                                - sComment.getSize().x;
                    } else if (pointsLeftwards(sMessage)) {
                        sComment.getPosition().x = sMessage.getTargetPosition().x + context.labelSpacing;
                    }
                }
            }
        }
        
        // Place the destruction event, if any
        SDestruction sDestruction = sLifeline.getDestruction();
        if (sDestruction != null) {
            sDestruction.getPosition().x = currX + (sLifeline.getSize().x - sDestruction.getSize().x) / 2; 
        }

        // TODO Place lifeline comments that don't refer to particular messages
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
    
    /**
     * Checks whether the given message points rightwards. Self messages don't.
     */
    private boolean pointsRightwards(final SMessage msg) {
        return msg.getSourceLifeline().getHorizontalSlot() < msg.getTargetLifeline().getHorizontalSlot();
    }

    /**
     * Checks whether the given message points leftwards. Self messages don't.
     */
    private boolean pointsLeftwards(final SMessage msg) {
        return msg.getSourceLifeline().getHorizontalSlot() > msg.getTargetLifeline().getHorizontalSlot();
    }
    
    /**
     * Checks whether the message has an inline label. Not having a label at all means not having an inline label.
     */
    private boolean hasInlineLabel(final SMessage msg) {
        SLabel sLabel = msg.getLabel();
        return sLabel != null && sLabel.getProperty(SequenceDiagramOptions.EDGE_LABELS_INLINE);
    }
    
    /**
     * Checks how many executions the given message's source is incident to. This will influence the amount of space
     * between its source point and its source lifeline. If a message starts an execution, it will only do so at its
     * target point. Such executions are thus not counted.
     */
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

    /**
     * Checks whether the given message starts the given execution. A message starts an execution if no message is part
     * of the execution that is on a layer above the message
     */
    private boolean startsExecution(final SMessage msg, final SExecution exec) {
        int minLayer = exec.getMessages().stream()
            .map(execMsg -> execMsg.getProperty(InternalSequenceProperties.LAYERED_NODE))
            .mapToInt(lnode -> lnode.getLayer().getIndex())
            .min()
            .orElse(-1);
        assert minLayer > -1;
        
        return minLayer == msg.getProperty(InternalSequenceProperties.LAYERED_NODE).getLayer().getIndex();
    }

    /**
     * Checks how many executions the given message's target is incident to. This will influence the amount of space
     * between its target point and its target lifeline. If a message ends an execution, it will only do so at its
     * source point. Such executions are thus not counted.
     */
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

    /**
     * Checks whether the given message ends the given execution. A message ends an execution if no message is part of
     * the execution that is on a layer below the message
     */
    private boolean endsExecution(final SMessage msg, final SExecution exec) {
        int maxLayer = exec.getMessages().stream()
            .map(execMsg -> execMsg.getProperty(InternalSequenceProperties.LAYERED_NODE))
            .mapToInt(lnode -> lnode.getLayer().getIndex())
            .max()
            .orElse(-1);
        assert maxLayer > -1;
        
        return maxLayer == msg.getProperty(InternalSequenceProperties.LAYERED_NODE).getLayer().getIndex();
    }
    
    /**
     * Returns the number of different types of comments (source and target). Target comments are only counted if the
     * message connects adjacent lifelines.
     */
    private int commentTypeCount(final SMessage msg) {
        boolean sourceComment = false;
        boolean targetComment = false;
        for (SComment sComment : msg.getComments()) {
            sourceComment |= sComment.getAlignment() == MessageCommentAlignment.SOURCE;
            targetComment |= sComment.getAlignment() == MessageCommentAlignment.TARGET
                    && msg.connectsAdjacentLifelines();
        }
        
        return (sourceComment ? 1 : 0) + (targetComment ? 1 : 0);
    }
    
    /**
     * Applies label management to the given label. Nothing is returned since the label's size is updated directly. If
     * there is no label manager, nothing happens.
     */
    private void applyLabelManagement(final LayoutContext context, final SLabel sLabel, final double targetWidth) {
        if (context.labelManager != null) {
            KVector newSize = context.labelManager.manageLabelSize(
                    sLabel.getProperty(InternalSequenceProperties.ORIGIN),
                    targetWidth);
            if (newSize != null) {
                sLabel.getSize().set(newSize);
            }
        }
    }

    /**
     * Applies label management to the given comment. Nothing is returned since the comment's size is updated directly.
     * If there is no label manager, we at least invoke node size calculation on the comment.
     */
    private void applyLabelManagement(final LayoutContext context, final SComment sComment, final double targetWidth) {
        if (context.labelManager != null && sComment.getLabel() != null) {
            SLabel sLabel = sComment.getLabel();
            KVector newSize = context.labelManager.manageLabelSize(
                    sLabel.getProperty(InternalSequenceProperties.ORIGIN),
                    targetWidth);
            if (newSize != null) {
                sLabel.getSize().set(newSize);
            }
        }
        
        // Size calculation
        Set<SizeConstraint> sizeConstraints = sComment.getProperty(SequenceDiagramOptions.NODE_SIZE_CONSTRAINTS);
        if (!sizeConstraints.isEmpty()) {
            NodeLabelAndSizeCalculator.process(context.sgraphAdapter,
                    SGraphAdapters.adaptComment(sComment, context.sgraphAdapter), true, false);
        }
    }
}
