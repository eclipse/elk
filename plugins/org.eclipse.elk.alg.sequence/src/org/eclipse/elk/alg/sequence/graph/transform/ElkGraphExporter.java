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
package org.eclipse.elk.alg.sequence.graph.transform;

import java.util.List;

import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.sequence.SequenceLayoutConstants;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SGraph;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.options.LabelAlignment;
import org.eclipse.elk.alg.sequence.options.MessageType;
import org.eclipse.elk.alg.sequence.options.SequenceExecution;
import org.eclipse.elk.alg.sequence.options.SequenceExecutionType;
import org.eclipse.elk.alg.sequence.properties.SequenceDiagramOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Applies the layout results back to the original KGraph using the standard KGraph coordinate system.
 * 
 * @author cds
 */
public final class ElkGraphExporter {

    public void applyLayout(final LayoutContext context) {
        // Set position for lifelines/nodes
        for (SLifeline lifeline : context.lifelineOrder) {
            // Dummy lifelines don't need any layout
            if (lifeline.isDummy()) {
                continue;
            }

            ElkNode node = (ElkNode) lifeline.getProperty(InternalProperties.ORIGIN);

            // Handle messages of the lifeline and their labels
            double lowestMessageCoordinate = applyMessageCoordinates(context, lifeline);

            // Apply execution coordinates and adjust positions of messages attached to these executions
            applyExecutionCoordinates(context, lifeline);

            // Place destruction if existing (this may change the lifeline's height, since the
            // desctruction event will be placed directly below the last incident message)
            ElkNode destruction = lifeline.getProperty(SequenceDiagramOptions.DESTRUCTION_NODE);
            if (destruction != null) {
                // Calculate the lifeline's new height
                double heightDelta = lowestMessageCoordinate + context.messageSpacing
                        - (lifeline.getPosition().y + lifeline.getSize().y);
                lifeline.getSize().y += heightDelta;
                
                double destructionXPos = lifeline.getSize().x / 2 - destruction.getWidth() / 2;
                double destructionYPos = lifeline.getSize().y - destruction.getHeight();
                destruction.setLocation(destructionXPos, destructionYPos);
            }

            // Set position and height for the lifeline.
            node.setY(lifeline.getPosition().y);
            node.setX(lifeline.getPosition().x);
            node.setHeight(lifeline.getSize().y);
        }

        // Place all comments
        placeComments(context.sgraph);

        // Set size and position of surrounding interaction
        ElkUtil.resizeNode(context.kgraph,
                context.sgraph.getSize().x,
                context.sgraph.getSize().y,
                false,
                false);
        
        context.kgraph.setLocation(context.borderSpacing, context.borderSpacing);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Messages

    /**
     * Apply the calculated coordinates of the messages that are connected to the given lifeline. If
     * there are any incoming create or destroy messages, the lifeline's height and y coordinate may be
     * changed here as well.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @param lifeline
     *            the lifeline whose messages are handled
     * @return y coordinate of the lowest message incident to the lifeline.
     */
    private double applyMessageCoordinates(final LayoutContext context, final SLifeline lifeline) {
        double lowestMsgYCoord = 0;
        
        for (SMessage message : lifeline.getOutgoingMessages()) {
            applyOutgoingMessageCoordinates(lifeline, message, context);
            
            lowestMsgYCoord = Math.max(lowestMsgYCoord, message.getSourceYPos());
        }

        for (SMessage message : lifeline.getIncomingMessages()) {
            applyIncomingMessageCoordinates(lifeline, message, context);
            
            lowestMsgYCoord = Math.max(lowestMsgYCoord, message.getTargetYPos());
        }
        
        return lowestMsgYCoord;
    }

    /**
     * Applies the source coordinates of the given message starting at the given lifeline.
     * 
     * @param lifeline the lifeline the message starts at.
     * @param message the message whose coordinates to apply.
     * @param context layout context of the current layout run.
     */
    private void applyOutgoingMessageCoordinates(final SLifeline lifeline, final SMessage message,
            final LayoutContext context) {
        
        assert lifeline == message.getSource();
        
        ElkEdge edge = (ElkEdge) message.getProperty(InternalProperties.ORIGIN);

        MessageType messageType = message.getProperty(SequenceDiagramOptions.MESSAGE_TYPE);
        
        // Compute the horizontal center of the lifeline to be used later
        double llCenter = lifeline.getPosition().x + lifeline.getSize().x / 2;
        
        // Clear the bend points of all edges (this is safe to do here since we will only be adding
        // bend points for self loops, which we encounter first as outgoing messages, so we're not
        // clearing bend points set by the incoming message handling)
        ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(edge, true, true);
        
        // Apply source point position
        edgeSection.setStartLocation(llCenter, message.getSourceYPos());
        
        // Check if the message connects to executions
        List<SequenceExecution> executions = lifeline.getProperty(
                SequenceDiagramOptions.EXECUTIONS);
        if (executions != null) {
            for (SequenceExecution execution : executions) {
                if (execution.getMessages().contains(message)) {
                    // Adjust the execution's vertical extend. This must be done with consideration for
                    // self loops: If an execution is created by a self-loop, it needs to start at the
                    // loop's lower border. If it is ended by a self-loop, it needs to end at the upper
                    // border. For regular messages, upper and lower border are the same.
                    double topYPos = upperSequencePositionForMessage(message, true, context);
                    double bottYPos = lowerSequencePositionForMessage(message, true, context);
                    
                    if (execution.getPosition().y == 0) {
                        // If this is the first message to encounter this execution, initialize the
                        // execution's y coordinate and height
                        execution.getPosition().y = topYPos;
                        execution.getSize().y = 0;
                    } else {
                        // The execution already has a position and size; adjust.
                        if (topYPos < execution.getPosition().y) {
                            double delta = execution.getPosition().y - topYPos;
                            execution.getPosition().y = topYPos;
                            execution.getSize().y += delta;
                        }
                        
                        if (bottYPos > execution.getPosition().y + execution.getSize().y) {
                            execution.getSize().y = bottYPos - execution.getPosition().y;
                        }
                    }
                }
            }
        }

        // Lost messages end between their source and the next lifeline
        if (messageType == MessageType.LOST) {
            edgeSection.setEndLocation(
                    lifeline.getPosition().x + lifeline.getSize().x + context.lifelineSpacing / 2,
                    message.getTargetYPos());
            
            // A lost message is supposed to have a target dummy node in the KGraph; set its position
            ElkNode dummy = ElkGraphUtil.getTargetNode(edge);
            dummy.setX(edgeSection.getEndX());
            dummy.setY(edgeSection.getEndY() - dummy.getHeight() / 2);
        }
        
        // Specify bend points for self loops
        if (message.getSource() == message.getTarget()) {
            ElkGraphUtil.createBendPoint(edgeSection,
                    llCenter + context.messageSpacing / 2,
                    edgeSection.getStartY());
        }

        // Walk through the labels and adjust their position
        placeLabels(context, message, edge);
    }

    /**
     * Applies the target coordinates of the given message ending at the given lifeline.
     * 
     * @param lifeline the lifeline the message ends at.
     * @param message the message whose coordinates to apply.
     * @param context layout context of the current layout run.
     */
    private void applyIncomingMessageCoordinates(final SLifeline lifeline, final SMessage message,
            final LayoutContext context) {
        
        assert lifeline == message.getTarget();
        
        ElkEdge edge = (ElkEdge) message.getProperty(InternalProperties.ORIGIN);

        MessageType messageType = message.getProperty(SequenceDiagramOptions.MESSAGE_TYPE);
        
        // Compute the horizontal center of the lifeline to be used later
        double llCenter = lifeline.getPosition().x + lifeline.getSize().x / 2;
        
        // Apply target point position
        ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(edge, false, false);
        edgeSection.setEndLocation(
                llCenter,
                message.getTargetYPos());
        
        if (messageType == MessageType.CREATE) {
            // Set lifeline's yPos to the yPos of the create-message and modify lifeline height
            // accordingly
            double delta = message.getTargetYPos() - context.lifelineHeader / 2
                    - lifeline.getPosition().y;
            
            lifeline.getPosition().y += delta;
            lifeline.getSize().y -= delta;
            
            // Reset x-position of create message because it leads to the header and not the line
            edgeSection.setEndX(lifeline.getPosition().x);
        } else if (messageType == MessageType.DELETE) {
            // If the lifeline extends beyond the message target position, shorten the lifeline
            if (lifeline.getPosition().y + lifeline.getSize().y > edgeSection.getEndY()) {
                lifeline.getSize().y = edgeSection.getEndY() - lifeline.getPosition().y;
            }
        }

        // Check if the message connects to executions
        List<SequenceExecution> executions = lifeline.getProperty(
                SequenceDiagramOptions.EXECUTIONS);
        if (executions != null) {
            for (SequenceExecution execution : executions) {
                if (execution.getMessages().contains(message)) {
                    // Adjust the execution's vertical extend. This must be done with consideration for
                    // self loops: If an execution is created by a self-loop, it needs to start at the
                    // loop's lower border. If it is ended by a self-loop, it needs to end at the upper
                    // border. For regular messages, upper and lower border are the same.
                    double topYPos = upperSequencePositionForMessage(message, false, context);
                    double bottYPos = lowerSequencePositionForMessage(message, false, context);
                    
                    if (execution.getPosition().y == 0) {
                        // If this is the first message to encounter this execution, initialize the
                        // execution's y coordinate and height
                        execution.getPosition().y = bottYPos;
                        execution.getSize().y = 0;
                    } else {
                        // The execution already has a position and size; adjust.
                        if (topYPos < execution.getPosition().y) {
                            double delta = execution.getPosition().y - topYPos;
                            execution.getPosition().y = topYPos;
                            execution.getSize().y += delta;
                        }
                        
                        if (bottYPos > execution.getPosition().y + execution.getSize().y) {
                            execution.getSize().y = bottYPos - execution.getPosition().y;
                        }
                    }
                }
            }
        }

        // Found messages start between their target and the previous lifeline
        if (messageType == MessageType.FOUND) {
            edgeSection.setStartLocation(
                    lifeline.getPosition().x - context.lifelineSpacing / 2,
                    message.getSourceYPos());
            
            // A found message is supposed to have a source dummy node in the KGraph; set its position
            ElkNode dummy = ElkGraphUtil.getSourceNode(edge);
            dummy.setX(edgeSection.getStartX() - dummy.getWidth());
            dummy.setY(edgeSection.getStartY() - dummy.getHeight() / 2);
            
            // Found messages now need to have their label placed
            placeLabels(context, message, edge);
        }
        
        // Specify bend points for self loops
        if (message.getSource() == message.getTarget()) {
            ElkGraphUtil.createBendPoint(edgeSection,
                    llCenter + context.messageSpacing / 2,
                    edgeSection.getEndY());
        }
    }
    
    private double upperSequencePositionForMessage(final SMessage message,
            final boolean outgoing, final LayoutContext context) {
        
        if (message.getSource() != message.getTarget()) {
            return outgoing ? message.getSourceYPos() : message.getTargetYPos();
        } else {
            return message.getSourceYPos() + context.messageSpacing / 2;
        }
    }
    
    private double lowerSequencePositionForMessage(final SMessage message, final boolean outgoing,
            final LayoutContext context) {
        
        if (message.getSource() != message.getTarget()) {
            return outgoing ? message.getSourceYPos() : message.getTargetYPos();
        } else {
            return message.getSourceYPos();
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Labels

    /**
     * Place the label(s) of the given message.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @param message
     *            the message whose labels to place
     * @param edge
     *            the edge representing the message in the original graph
     */
    private void placeLabels(final LayoutContext context, final SMessage message, final ElkEdge edge) {
        // If the message is a lost / found message, its direction will not depend on the
        // target / source lifeline's index in the ordered lifeline list
        MessageType messageType = message.getProperty(SequenceDiagramOptions.MESSAGE_TYPE);
        
        for (ElkLabel label : edge.getLabels()) {
            SLifeline messageTarget = message.getTarget();
            SLifeline messageSource = message.getSource();
            
            if (messageType == MessageType.LOST) {
                placeRightPointingMessageLabels(context, message, label);
            } else if (messageSource.getHorizontalSlot() < messageTarget.getHorizontalSlot()) {
                placeRightPointingMessageLabels(context, message, label);
            } else if (messageSource.getHorizontalSlot() > messageTarget.getHorizontalSlot()) {
                placeLeftPointingMessageLabels(context, message, label);
            } else {
                // The message is a self loop, so place labels to its right
                ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(edge, false, false);
                double xPos;
                if (edgeSection.getBendPoints().size() > 0) {
                    ElkBendPoint firstBend = edgeSection.getBendPoints().get(0);
                    xPos = firstBend.getX();
                } else {
                    xPos = edgeSection.getStartX();
                }
                label.setY(message.getSourceYPos() + SequenceLayoutConstants.LABELSPACING);
                label.setX(xPos + SequenceLayoutConstants.LABELMARGIN / 2);
            }
            
            // Labels may cause the graph's width to get wider. Compensate!
            ensureGraphIsWideEnough(context, label.getX() + label.getWidth());
        }
    }


    /**
     * Place a label of the given rightwards pointing message.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @param message
     *            the message whose label to place
     * @param labelLayout
     *            layout of the label to be placed where the layout information will be stored
     */
    private void placeRightPointingMessageLabels(final LayoutContext context, final SMessage message,
            final ElkLabel labelLayout) {
        
        SLifeline srcLifeline = message.getSource();
        double llCenter = srcLifeline.getPosition().x + srcLifeline.getSize().x / 2;
        
        // Labels are placed above messages pointing rightwards
        labelLayout.setY(message.getSourceYPos() - labelLayout.getHeight() - 2);
        
        // For the horizontal alignment, we need to check which alignment strategy to use
        LabelAlignment alignment = context.labelAlignment;
        
        if (alignment == LabelAlignment.SOURCE_CENTER
                && srcLifeline.getHorizontalSlot() + 1 == context.lifelineOrder.size()) {
            
            // This is a lost message; fall back to source placement
            alignment = LabelAlignment.SOURCE;
        } else if (message.getProperty(SequenceDiagramOptions.MESSAGE_TYPE) == MessageType.CREATE) {
            // Create messages always use SOURCE placement to avoid overlapping the target lifeline
            // header
            alignment = LabelAlignment.SOURCE;
        }
        
        // Actually calculate the horizontal position
        switch (alignment) {
        case SOURCE_CENTER:
            // Place label centered between the source lifeline and the next lifeline
            SLifeline nextLL = context.lifelineOrder.get(srcLifeline.getHorizontalSlot() + 1);
            double center = (llCenter + nextLL.getPosition().x + nextLL.getSize().x / 2) / 2;
            labelLayout.setX(center - labelLayout.getWidth() / 2);
            break;
        case SOURCE:
            // Place label near the source lifeline
            labelLayout.setX(llCenter + SequenceLayoutConstants.LABELSPACING);
            break;
        case CENTER:
            // Place label at the center of the message
            double targetCenter = message.getTarget().getPosition().x + message.getTarget().getSize().x / 2;
            labelLayout.setX((llCenter + targetCenter) / 2 - labelLayout.getWidth() / 2);
            break;
        }
    }


    /**
     * Place a label of the given leftwards pointing message.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @param message
     *            the message whose label to place
     * @param labelLayout
     *            layout of the label to be placed where the layout information will be stored
     */
    private void placeLeftPointingMessageLabels(final LayoutContext context, final SMessage message,
            final ElkLabel labelLayout) {

        SLifeline srcLifeline = message.getSource();
        double llCenter = srcLifeline.getPosition().x + srcLifeline.getSize().x / 2;

        // Labels are placed below messages pointing leftwards
        labelLayout.setY(message.getSourceYPos() + 2);
        
        // For the horizontal alignment, we need to check which alignment strategy to use
        LabelAlignment alignment = context.labelAlignment;
        
        if (alignment == LabelAlignment.SOURCE_CENTER && srcLifeline.getHorizontalSlot() == 0) {
            // This is a found message; fall back to source placement
            alignment = LabelAlignment.SOURCE;
        }
        
        // Actually calculate the horizontal position
        switch (alignment) {
        case SOURCE_CENTER:
            // Place label centered between the source lifeline and the previous lifeline
            SLifeline lastLL = context.lifelineOrder.get(srcLifeline.getHorizontalSlot() - 1);
            double center = (llCenter + lastLL.getPosition().x + lastLL.getSize().x / 2) / 2;
            labelLayout.setX(center - labelLayout.getWidth() / 2);
            break;
        case SOURCE:
            // Place label near the source lifeline
            labelLayout.setX(llCenter - labelLayout.getWidth() - SequenceLayoutConstants.LABELSPACING);
            break;
        case CENTER:
            // Place label at the center of the message
            double targetCenter = message.getTarget().getPosition().x + message.getTarget().getSize().x / 2;
            labelLayout.setX((llCenter + targetCenter) / 2 - labelLayout.getWidth() / 2);
            break;
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Executions

    /**
     * Apply execution coordinates and adjust positions of messages attached to these executions.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @param lifeline
     *            the lifeline whose executions are to be placed.
     */
    private void applyExecutionCoordinates(final LayoutContext context, final SLifeline lifeline) {
        List<SequenceExecution> executions = lifeline.getProperty(
                SequenceDiagramOptions.EXECUTIONS);
        if (executions == null || executions.isEmpty()) {
            return;
        }
        
        // Set xPos, maxXPos and height / maxYPos
        arrangeExecutions(executions, lifeline);

        // Self-messages need to be processed later, once all executions are placed
        Multimap<SMessage, SequenceExecution> selfMessages = HashMultimap.create();
        
        // Walk through the lifeline's executions
        for (SequenceExecution execution : executions) {
            // TODO We need to calculate proper sizes for these guys
            if (execution.getType() == SequenceExecutionType.DURATION
                    || execution.getType() == SequenceExecutionType.TIME_CONSTRAINT) {
                
                execution.getPosition().y += SequenceLayoutConstants.TWENTY;
            }

            // Apply calculated coordinates to the execution
            ElkNode executionNode = (ElkNode) execution.getOrigin();
            executionNode.setX(execution.getPosition().x);
            executionNode.setY(execution.getPosition().y - lifeline.getPosition().y);
            executionNode.setWidth(execution.getSize().x);
            executionNode.setHeight(execution.getSize().y);
            
            ensureGraphIsWideEnough(context, executionNode.getX() + executionNode.getWidth());

            // Walk through execution's messages and adjust their position
            for (Object messObj : execution.getMessages()) {
                SMessage smessage = (SMessage) messObj;
                
                // Self-message are processed later
                if (smessage.getSource() == smessage.getTarget()) {
                    selfMessages.put(smessage, execution);
                    continue;
                }
                
                // Check if the message points rightwards
                boolean toRight = smessage.getSource().getHorizontalSlot()
                        < smessage.getTarget().getHorizontalSlot();

                ElkEdge edge = (ElkEdge) smessage.getProperty(InternalProperties.ORIGIN);
                ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(edge, false, false);
                
                // x coordinate for messages attached to the left side of the execution
                double newXPos = lifeline.getPosition().x + execution.getPosition().x;
                
                if (smessage.getSource() == lifeline) {
                    if (toRight) {
                        newXPos += execution.getSize().x;
                    }
                    double delta = newXPos - edgeSection.getStartX();
                    offsetX(edgeSection, true, delta, context);
                    
                    // TODO Labels positioned at the source should be offset as well
                }
                
                if (smessage.getTarget() == lifeline) {
                    if (!toRight) {
                        newXPos += execution.getSize().x;
                    }
                    double delta = newXPos - edgeSection.getEndX();
                    offsetX(edgeSection, false, delta, context);
                }
            }
        }
        
        applyExecutionCoordinatesToSelfMessages(lifeline, selfMessages, context);
    }

    /**
     * Set x position and width of an execution and check for minimum height.
     * 
     * @param executions
     *            List of {@link SequenceExecution} at the given {@link SLifeline}
     * @param lifeline
     *            the lifeline the executions belong to.
     */
    private void arrangeExecutions(final List<SequenceExecution> executions, final SLifeline lifeline) {
        // All executions are initially centered in their lifeline
        for (SequenceExecution execution : executions) {
            execution.getPosition().x =
                    (lifeline.getSize().x - SequenceLayoutConstants.EXECUCTION_WIDTH) / 2;
        }

        // If there are multiple executions, some may have to be shifted horizontally if they overlap
        if (executions.size() > 1) {
            for (SequenceExecution execution : executions) {
                if (execution.getType() == SequenceExecutionType.DURATION
                        || execution.getType() == SequenceExecutionType.TIME_CONSTRAINT) {
                    
                    continue;
                }
                
                int slot = 0;
                for (SequenceExecution otherExecution : executions) {
                    if (execution != otherExecution) {
                        // The "less OR EQUAL" when calculating "bottomOverlaps" is significant here:
                        // in KGraph mode, several executions can end at the same message
                        boolean topOverlaps = execution.getPosition().y > otherExecution.getPosition().y;
                        boolean bottomOverlaps = execution.getPosition().y + execution.getSize().y
                                <= otherExecution.getPosition().y + otherExecution.getSize().y;
                        
                        if (topOverlaps && bottomOverlaps) {
                            slot++;
                        }
                    }
                }
                
                // Shift execution position
                execution.getPosition().x += slot * SequenceLayoutConstants.EXECUCTION_WIDTH / 2;
            }
        }

        // Check minimum height of executions and set width
        for (SequenceExecution execution : executions) {
            if (execution.getSize().y < SequenceLayoutConstants.MIN_EXECUTION_HEIGHT) {
                execution.getSize().y = SequenceLayoutConstants.MIN_EXECUTION_HEIGHT;
            }
            
            if (execution.getType() == SequenceExecutionType.EXECUTION) {
                execution.getSize().x = SequenceLayoutConstants.EXECUCTION_WIDTH;
                
                // If an execution has no messages, place it at the bottom of its lifeline
                if (execution.getMessages().isEmpty()) {
                    execution.getSize().y = SequenceLayoutConstants.MIN_EXECUTION_HEIGHT;
                }
            }
        }
    }
    
    /**
     * Takes a bunch of self messages and offsets their source, target, and end points to accomodate
     * for the executions they're incident to. Each message in the list is assumed to be a self
     * message incident to at least one execution. All these executions as well as their lifeline
     * are assumed to have already been placed.
     * 
     * @param lifeline
     *            the lifeline whose self-messages we are processing.
     * @param selfMsgs
     *            the self messages to be offset. This is a multimap that maps each self-loop
     *            message to those executions it is officially incident to.
     * @param context
     *            the layout context that contains all relevant information for the current layout
     *            run.
     */
    private void applyExecutionCoordinatesToSelfMessages(final SLifeline lifeline,
            final Multimap<SMessage, SequenceExecution> selfMsgs, final LayoutContext context) {
        
        double lifelineXCenter = lifeline.getSize().x / 2;
        
        for (final SMessage selfMsg : selfMsgs.keySet()) {
            assert selfMsg.getSource() == lifeline : "Message source not expected lifeline";
            assert selfMsg.getTarget() == lifeline : "Message target not expected lifeline";
            
            // Retrieve message layout info
            ElkEdge selfMsgEdge = (ElkEdge) selfMsg.getProperty(InternalProperties.ORIGIN);
            ElkEdgeSection selfMsgEdgeSection = ElkGraphUtil.firstEdgeSection(selfMsgEdge, false, false);
            
            // We iterate over all connected executions and check for both the source and the target
            // point if they are in the execution's vertical area. For each of the points, we remember
            // the required offset and apply it. The two bend points are offset by the maximum.
            double sourceOffset = 0;
            double targetOffset = 0;
            
            for (SequenceExecution execution : selfMsgs.get(selfMsg)) {
                // The execution's coordinates and the bend points share the same coordinate system
                double execTopYPos = execution.getPosition().y;
                double execBotYPos = execTopYPos + execution.getSize().y;
                
                // Check if the source point is in the execution's area
                if (selfMsgEdgeSection.getStartY() >= execTopYPos && selfMsgEdgeSection.getStartY() <= execBotYPos) {
                    sourceOffset = Math.max(sourceOffset,
                            execution.getPosition().x + execution.getSize().x - lifelineXCenter);
                }
                
                // Check if the target point is in the execution's area
                if (selfMsgEdgeSection.getEndY() >= execTopYPos && selfMsgEdgeSection.getEndY() <= execBotYPos) {
                    targetOffset = Math.max(targetOffset,
                            execution.getPosition().x + execution.getSize().x - lifelineXCenter);
                }
            }
            
            // Apply offsets
            offsetX(selfMsgEdgeSection, true, sourceOffset, context);
            offsetX(selfMsgEdgeSection, false, targetOffset, context);
            
            double maxOffset = Math.max(sourceOffset, targetOffset);
            offsetX(selfMsgEdgeSection.getBendPoints(), maxOffset, context);
            offsetLabelsX(selfMsgEdge.getLabels(), maxOffset, context);
        }
    }
    
    /**
     * Adds the given delta to the given point's X coordinate. Also ensures the graph is wide enough
     * to accomodate the new point.
     * 
     * @param point
     *            the point to offset.
     * @param delta
     *            the amount to add to the X coordinate.
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void offsetX(final ElkEdgeSection section, final boolean start, final double delta,
            final LayoutContext context) {
        
        if (start) {
            section.setStartX(section.getStartX() + delta);
            ensureGraphIsWideEnough(context, section.getStartX());
        } else {
            section.setEndX(section.getEndX() + delta);
            ensureGraphIsWideEnough(context, section.getEndX());
        }
    }
    
    /**
     * Calls {@link #offsetX(KPoint, double)} on every point in the given list. Also 
     * 
     * @param points
     *            the points to offset.
     * @param delta
     *            the amount to add to the X coordinate.
     * @param context
     *            the layout context that contains all relevant information for the current layout
     *            run.
     */
    private void offsetX(final List<ElkBendPoint> points, final double delta, final LayoutContext context) {
        for (ElkBendPoint point : points) {
            point.setX(point.getX() + delta);
            ensureGraphIsWideEnough(context, point.getX());
        }
    }
    
    /**
     * Adds the given delta to the X position of all labels in the given list.
     * 
     * @param labels
     *            the labels to offset.
     * @param delta
     *            the amount to add to the X coordinate.
     * @param context
     *            the layout context that contains all relevant information for the current layout
     *            run.
     */
    private void offsetLabelsX(final List<ElkLabel> labels, final double delta, final LayoutContext context) {
        for (ElkLabel label : labels) {
            label.setX(label.getX() + delta);
            ensureGraphIsWideEnough(context, label.getX() + label.getWidth());
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Comments

    /**
     * Place the comment objects (comments, constraints) according to their calculated coordinates.
     * 
     * @param graph
     *            the Sequence Graph
     */
    private void placeComments(final SGraph graph) {
        for (SComment comment : graph.getComments()) {
            ElkNode origin = (ElkNode) comment.getProperty(InternalProperties.ORIGIN);
            origin.setLocation(comment.getPosition().x, comment.getPosition().y);
            if (comment.getMessage() != null) {
                // Connected comments

                // Set coordinates for the connection of the comment
                double edgeSourceXPos, edgeSourceYPos, edgeTargetXPos, edgeTargetYPos;
                String attachedElement = comment.getProperty(
                        SequenceDiagramOptions.ATTACHED_ELEMENT_TYPE);
                if (attachedElement.toLowerCase().startsWith("lifeline")
                        || attachedElement.toLowerCase().contains("execution")) {
                    
                    // Connections to lifelines or executions are drawn horizontally
                    SLifeline lifeline = comment.getLifeline();
                    edgeSourceXPos = comment.getPosition().x;
                    edgeSourceYPos = comment.getPosition().y + comment.getSize().y / 2;
                    edgeTargetXPos = lifeline.getPosition().x + lifeline.getSize().x / 2;
                    edgeTargetYPos = edgeSourceYPos;
                } else {
                    // Connections to messages are drawn vertically
                    edgeSourceXPos = comment.getPosition().x + comment.getSize().x / 2;
                    edgeTargetXPos = edgeSourceXPos;
                    
                    ElkEdge edge = (ElkEdge) comment.getMessage().getProperty(InternalProperties.ORIGIN);
                    ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(edge, false, false);
                    edgeSourceYPos = comment.getPosition().y + comment.getSize().y;
                    edgeTargetYPos = (edgeSection.getEndY() + edgeSection.getStartY()) / 2;
                }

                // Apply connection coordinates to layout
                ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(
                        comment.getProperty(InternalSequenceProperties.COMMENT_CONNECTION), true, true);
                edgeSection.setStartLocation(edgeSourceXPos, edgeSourceYPos);
                edgeSection.setEndLocation(edgeTargetXPos, edgeTargetYPos);
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Utility Methods
    
    /**
     * Ensures that the context's SGraph's width is wide enough to accomodate an element that
     * extends to the given x coordinate.
     * 
     * @param context
     *            the layout context.
     * @param rightmostElement
     *            x coordinate of the right border of the element that needs to fit inside the
     *            graph.
     */
    private void ensureGraphIsWideEnough(final LayoutContext context, final double rightmostElement) {
        KVector graphSize = context.sgraph.getSize();
        graphSize.x = Math.max(graphSize.x, rightmostElement + context.borderSpacing);
    }

}
