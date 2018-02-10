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

import org.eclipse.elk.alg.sequence.SequenceLayoutConstants;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SArea;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SExecution;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.options.LabelAlignmentStrategy;
import org.eclipse.elk.alg.sequence.options.MessageType;
import org.eclipse.elk.alg.sequence.options.SequenceDiagramOptions;
import org.eclipse.elk.alg.sequence.options.SequenceExecutionType;
import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.math.ElkPadding;
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
 * Applies the layout results back to the original ELK graph.
 * 
 * TODO Apply size and position of all areas.
 * 
 * <p><em>Note:</em> This exporter does a lot of things, several of which (such as placing destruction nodes) should
 * arguably be done in phase 5 of the algorithm. Someone might want to sort that at some point.</p>
 */
public final class ElkGraphExporter {
    
    /** The offset that results from the interaction's padding, to be added to all coordinates. */
    private KVector offset = null;

    public void applyLayout(final LayoutContext context) {
        // The padding will be applied in this step, so calculate the resulting offset as a KVector that we can
        // easily add to things
        ElkPadding sgraphPadding = context.sgraph.getPadding();
        offset = new KVector(sgraphPadding.left, sgraphPadding.top);
        
        // Place things (in placing things, other things are placed as well, which is nice)
        placeLifelines(context);
        placeAreas(context);
        placeComments(context);
        
        // Apply padding to the graph's size
        KVector sgraphSize = context.sgraph.getSize();
        sgraphSize.x += sgraphPadding.left + sgraphPadding.right;
        sgraphSize.y += sgraphPadding.top + sgraphPadding.bottom;

        // Set size and position of surrounding interaction
        ElkUtil.resizeNode(context.elkgraph,
                context.sgraph.getSize().x,
                context.sgraph.getSize().y,
                false,
                false);
        
        // Place it such that it will be centered in the diagram
        ElkMargin interactionMargins = context.elkgraph.getProperty(SequenceDiagramOptions.MARGINS);
        context.elkgraph.setLocation(interactionMargins.left, interactionMargins.top);
        
        // Reset offset
        offset = null;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Lifelines

    /**
     * Places all lifelines.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void placeLifelines(final LayoutContext context) {
        for (SLifeline slifeline : context.sgraph.getLifelines()) {
            // Dummy lifelines don't need any layout
            if (slifeline.isDummy()) {
                continue;
            }

            // Handle messages of the lifeline and their labels
            double lowestMessageCoordinate = applyMessageCoordinates(slifeline, context);

            // Apply execution coordinates and adjust positions of messages attached to these executions
            applyExecutionCoordinates(context, slifeline);

            // Place destruction if existing (this may change the lifeline's height, since the desctruction event will
            // be placed directly below the last incident message)
            ElkNode kdestruction = slifeline.getProperty(InternalSequenceProperties.DESTRUCTION_NODE);
            if (kdestruction != null) {
                // Calculate the lifeline's new height
                double heightDelta = lowestMessageCoordinate + context.messageSpacing
                        - (slifeline.getPosition().y + slifeline.getSize().y);
                slifeline.getSize().y += heightDelta;
                
                KVector destructionPosition = new KVector(
                        slifeline.getPosition().x + (slifeline.getSize().x - kdestruction.getWidth()) / 2,
                        slifeline.getPosition().y + slifeline.getSize().y - kdestruction.getHeight());
                destructionPosition.add(offset);
                kdestruction.setLocation(destructionPosition.x, destructionPosition.y);
            }

            // Apply height and coordinates
            ElkNode klifeline = (ElkNode) slifeline.getProperty(InternalSequenceProperties.ORIGIN);
            klifeline.setX(slifeline.getPosition().x + offset.x);
            klifeline.setY(slifeline.getPosition().y + offset.y);
            klifeline.setHeight(slifeline.getSize().y);
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Messages

    /**
     * Apply the calculated coordinates of the messages that are connected to the given lifeline. If
     * there are any incoming create or destroy messages, the lifeline's height and y coordinate may be
     * changed here as well.
     * 
     * @param slifeline
     *            the lifeline whose messages are to be placed.
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @return y coordinate of the lowest message incident to the lifeline.
     */
    private double applyMessageCoordinates(final SLifeline slifeline, final LayoutContext context) {
        double lowestMsgYCoord = 0;
        
        for (SMessage message : slifeline.getOutgoingMessages()) {
            applyOutgoingMessageCoordinates(slifeline, message, context);
            lowestMsgYCoord = Math.max(lowestMsgYCoord, message.getSourceYPos());
        }

        for (SMessage message : slifeline.getIncomingMessages()) {
            applyIncomingMessageCoordinates(slifeline, message, context);
            lowestMsgYCoord = Math.max(lowestMsgYCoord, message.getTargetYPos());
        }
        
        return lowestMsgYCoord;
    }

    /**
     * Applies the source coordinates of the given message starting at the given lifeline.
     * 
     * @param slifeline
     *            the lifeline the message starts at.
     * @param smessage
     *            the message whose coordinates to apply.
     * @param context
     *            layout context of the current layout run.
     */
    private void applyOutgoingMessageCoordinates(final SLifeline slifeline, final SMessage smessage,
            final LayoutContext context) {
        
        assert slifeline == smessage.getSource();
        
        // Clear the bend points of all edges (this is safe to do here since we will only be adding
        // bend points for self loops, which we encounter first as outgoing messages, so we're not
        // clearing bend points set by the incoming message handling)
        ElkEdge kmessage = (ElkEdge) smessage.getProperty(InternalSequenceProperties.ORIGIN);
        ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(kmessage, false, true);
        
        // Compute the horizontal center of the lifeline to be used later
        double llCenter = slifeline.getPosition().x + slifeline.getSize().x / 2;
        
        // Apply source point position
        edgeSection.setStartLocation(llCenter + offset.x, smessage.getSourceYPos() + offset.y);
        
        // Check if the message connects to executions
        for (SExecution sexecution : slifeline.getExcecutions()) {
            if (sexecution.getMessages().contains(smessage)) {
                // Adjust the execution's vertical extent. This must be done with consideration for
                // self loops: If an execution is created by a self-loop, it needs to start at the
                // loop's lower border. If it is ended by a self-loop, it needs to end at the upper
                // border. For regular messages, upper and lower border are the same.
                double topYPos = upperSequencePositionForMessage(smessage, true, context);
                double bottYPos = lowerSequencePositionForMessage(smessage, true, context);
                
                if (sexecution.getPosition().y == 0) {
                    // If this is the first message to encounter this execution, initialize the
                    // execution's y coordinate and height
                    sexecution.getPosition().y = topYPos;
                    sexecution.getSize().y = 0;
                } else {
                    // The execution already has a position and size; adjust.
                    if (topYPos < sexecution.getPosition().y) {
                        double delta = sexecution.getPosition().y - topYPos;
                        sexecution.getPosition().y = topYPos;
                        sexecution.getSize().y += delta;
                    }
                    
                    if (bottYPos > sexecution.getPosition().y + sexecution.getSize().y) {
                        sexecution.getSize().y = bottYPos - sexecution.getPosition().y;
                    }
                }
            }
        }

        // Lost messages end between their source and the next lifeline
        MessageType messageType = smessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE);
        if (messageType == MessageType.LOST) {
            edgeSection.setEndLocation(
                    slifeline.getPosition().x + slifeline.getSize().x + context.lifelineSpacing / 2 + offset.x,
                    smessage.getTargetYPos() + offset.y);
            
            // A lost message is supposed to have a target dummy node in the ELK Graph; set its position
            ElkNode kdummy = ElkGraphUtil.getTargetNode(kmessage);
            kdummy.setX(edgeSection.getEndX());
            kdummy.setY(edgeSection.getEndY() - kdummy.getHeight() / 2);
        }
        
        // Specify bend points for self loops
        if (smessage.getSource() == smessage.getTarget()) {
            ElkGraphUtil.createBendPoint(edgeSection,
                    llCenter + context.messageSpacing / 2 + offset.x,
                    edgeSection.getStartY());
        }

        // Walk through the labels and adjust their position
        placeLabels(context, smessage, kmessage);
    }

    /**
     * Applies the target coordinates of the given message ending at the given lifeline.
     * 
     * @param slifeline
     *            the lifeline the message ends at.
     * @param smessage
     *            the message whose coordinates to apply.
     * @param context
     *            layout context of the current layout run.
     */
    private void applyIncomingMessageCoordinates(final SLifeline slifeline, final SMessage smessage,
            final LayoutContext context) {
        
        assert slifeline == smessage.getTarget();
        
        // Compute the horizontal center of the lifeline to be used later
        double llCenter = slifeline.getPosition().x + slifeline.getSize().x / 2;
        
        // Apply target point position
        ElkEdge kmessage = (ElkEdge) smessage.getProperty(InternalSequenceProperties.ORIGIN);
        
        assert kmessage.getSections().size() == 1;
        ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(kmessage, false, true);
        edgeSection.setEndLocation(
                llCenter + offset.x,
                smessage.getTargetYPos() + offset.y);
        
        // We need to adjust the lifeline's position / size if we have a create or delete message
        MessageType messageType = smessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE);
        if (messageType == MessageType.CREATE) {
            // Set lifeline's yPos to the yPos of the create-message and modify lifeline height accordingly
            double delta = smessage.getTargetYPos() - context.lifelineHeaderHeight / 2 - slifeline.getPosition().y;
            slifeline.getPosition().y += delta;
            slifeline.getSize().y -= delta;
            
            // Reset x-position of create message because it leads to the header and not the line
            edgeSection.setEndX(slifeline.getPosition().x + offset.x);
            
        } else if (messageType == MessageType.DELETE) {
            // If the lifeline extends beyond the message target position, shorten the lifeline
            if (slifeline.getPosition().y + slifeline.getSize().y + offset.y > edgeSection.getEndY()) {
                slifeline.getSize().y = edgeSection.getEndY() - slifeline.getPosition().y - offset.y;
            }
        }

        // Check if the message connects to executions
        for (SExecution sexecution : slifeline.getExcecutions()) {
            if (sexecution.getMessages().contains(smessage)) {
                // Adjust the execution's vertical extent. This must be done with consideration for
                // self loops: If an execution is created by a self-loop, it needs to start at the
                // loop's lower border. If it is ended by a self-loop, it needs to end at the upper
                // border. For regular messages, upper and lower border are the same.
                double topYPos = upperSequencePositionForMessage(smessage, false, context);
                double bottYPos = lowerSequencePositionForMessage(smessage, false, context);
                
                if (sexecution.getPosition().y == 0) {
                    // If this is the first message to encounter this execution, initialize the
                    // execution's y coordinate and height
                    sexecution.getPosition().y = bottYPos;
                    sexecution.getSize().y = 0;
                    
                } else {
                    // The execution already has a position and size; adjust.
                    if (topYPos < sexecution.getPosition().y) {
                        double delta = sexecution.getPosition().y - topYPos;
                        sexecution.getPosition().y = topYPos;
                        sexecution.getSize().y += delta;
                    }
                    
                    if (bottYPos > sexecution.getPosition().y + sexecution.getSize().y) {
                        sexecution.getSize().y = bottYPos - sexecution.getPosition().y;
                    }
                }
            }
        }

        // Found messages start between their target and the previous lifeline
        if (messageType == MessageType.FOUND) {
            edgeSection.setStartLocation(
                    slifeline.getPosition().x - context.lifelineSpacing / 2 + offset.x,
                    smessage.getSourceYPos() + offset.y);
            
            // A found message is supposed to have a source dummy node in the KGraph; set its position
            ElkNode kdummy = ElkGraphUtil.getSourceNode(kmessage);
            kdummy.setX(edgeSection.getStartX() - kdummy.getWidth());
            kdummy.setY(edgeSection.getStartY() - kdummy.getHeight() / 2);
            
            // Found messages now need to have their label placed
            placeLabels(context, smessage, kmessage);
        }
        
        // Specify bend points for self loops
        if (smessage.getSource() == smessage.getTarget()) {
            ElkGraphUtil.createBendPoint(edgeSection,
                    llCenter + context.messageSpacing / 2 + offset.x,
                    edgeSection.getEndY());
        }
    }
    
    private double upperSequencePositionForMessage(final SMessage smessage, final boolean outgoing,
            final LayoutContext context) {
        
        if (smessage.getSource() != smessage.getTarget()) {
            return outgoing ? smessage.getSourceYPos() : smessage.getTargetYPos();
        } else {
            return smessage.getSourceYPos() + context.messageSpacing / 2;
        }
    }
    
    private double lowerSequencePositionForMessage(final SMessage smessage, final boolean outgoing,
            final LayoutContext context) {
        
        if (smessage.getSource() != smessage.getTarget()) {
            return outgoing ? smessage.getSourceYPos() : smessage.getTargetYPos();
        } else {
            return smessage.getSourceYPos();
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Labels

    /**
     * Place the label(s) of the given message.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @param smessage
     *            the message whose labels to place
     * @param kmessage
     *            the edge representing the message in the original graph
     */
    private void placeLabels(final LayoutContext context, final SMessage smessage, final ElkEdge kmessage) {
        MessageType msgType = smessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE);
        SLifeline msgSource = smessage.getSource();
        SLifeline msgTarget = smessage.getTarget();
        
        if (msgSource.getHorizontalSlot() < msgTarget.getHorizontalSlot() || msgType == MessageType.LOST) {
            // Message points rightwards
            kmessage.getLabels().stream()
                .forEach(label -> placeRightPointingMessageLabels(context, smessage, label));
            
        } else if (msgSource.getHorizontalSlot() > msgTarget.getHorizontalSlot() || msgType == MessageType.FOUND) {
            // Message points leftwards
            kmessage.getLabels().stream()
                .forEach(label -> placeLeftPointingMessageLabels(context, smessage, label));
            
        } else {
            // Message is a self-loop
            for (ElkLabel label : kmessage.getLabels()) {
                ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(kmessage, false, false);
                double xPos;
                
                if (edgeSection.getBendPoints().size() > 0) {
                    ElkBendPoint firstBend = edgeSection.getBendPoints().get(0);
                    xPos = firstBend.getX();
                } else {
                    // This shouldn't happen, in fact
                    xPos = edgeSection.getStartX();
                }
                
                label.setY(smessage.getSourceYPos() + context.labelSpacing + offset.y);
                label.setX(xPos + context.labelSpacing / 2);
                
                ensureGraphIsWideEnough(context, label.getX() + label.getWidth());
            }
        }
    }


    /**
     * Place a label of the given rightwards pointing message.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @param smessage
     *            the message whose label to place
     * @param klabel
     *            layout of the label to be placed where the layout information will be stored
     */
    private void placeRightPointingMessageLabels(final LayoutContext context, final SMessage smessage,
            final ElkLabel klabel) {
        
        SLifeline srcLL = smessage.getSource();
        double llCenter = srcLL.getPosition().x + srcLL.getSize().x / 2;
        
        // Labels are placed above messages pointing rightwards
        // TODO: Make label placement strategy configurable
        klabel.setY(smessage.getSourceYPos() - klabel.getHeight() - 2 + offset.y);
        
        // For the horizontal alignment, we need to check which alignment strategy to use
        LabelAlignmentStrategy alignment = context.labelAlignment;

        MessageType msgType = smessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE);
        if (isRightmostLifeline(srcLL) && alignment == LabelAlignmentStrategy.SOURCE_CENTER) {
            // This is a lost message; fall back to source placement
            alignment = LabelAlignmentStrategy.SOURCE;
            
        } else if (msgType == MessageType.CREATE) {
            // Create messages always use SOURCE placement to avoid overlapping the target lifeline header
            alignment = LabelAlignmentStrategy.SOURCE;
        }
        
        // Actually calculate the horizontal position
        switch (alignment) {
        case SOURCE_CENTER:
            // Place label centered between the source lifeline and the next lifeline (which must exist at this point)
            assert context.sgraph.getLifelines().size() > srcLL.getHorizontalSlot() + 1;
            SLifeline nextLL = context.sgraph.getLifelines().get(srcLL.getHorizontalSlot() + 1);
            
            double center = (llCenter + nextLL.getPosition().x + nextLL.getSize().x / 2) / 2;
            klabel.setX(center - klabel.getWidth() / 2 + offset.x);
            break;
            
        case SOURCE:
            // Place label near the source lifeline
            klabel.setX(llCenter + context.labelSpacing + offset.x);
            break;
            
        case CENTER:
            // Place label at the center of the message
            double targetCenter = smessage.getTarget().getPosition().x + smessage.getTarget().getSize().x / 2;
            klabel.setX((llCenter + targetCenter) / 2 - klabel.getWidth() / 2 + offset.x);
            break;
        }
        
        ensureGraphIsWideEnough(context, klabel.getX() + klabel.getWidth());
    }


    /**
     * Place a label of the given leftwards pointing message.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @param smessage
     *            the message whose label to place
     * @param klabel
     *            layout of the label to be placed where the layout information will be stored
     */
    private void placeLeftPointingMessageLabels(final LayoutContext context, final SMessage smessage,
            final ElkLabel klabel) {

        SLifeline srcLL = smessage.getSource();
        double llCenter = srcLL.getPosition().x + srcLL.getSize().x / 2;

        // Labels are placed below messages pointing leftwards
        // TODO: Make label placement strategy configurable
        klabel.setY(smessage.getSourceYPos() + 2 + offset.y);
        
        // For the horizontal alignment, we need to check which alignment strategy to use
        LabelAlignmentStrategy alignment = context.labelAlignment;

        if (isLeftmostLifeline(srcLL) && alignment == LabelAlignmentStrategy.SOURCE_CENTER) {
            // This is a found message; fall back to source placement
            alignment = LabelAlignmentStrategy.SOURCE;
        }
        
        // Actually calculate the horizontal position
        switch (alignment) {
        case SOURCE_CENTER:
            // Place label centered between the source and the previous lifeline (which must exist at this point)
            assert srcLL.getHorizontalSlot() > 0;
            SLifeline prevLL = context.sgraph.getLifelines().get(srcLL.getHorizontalSlot() - 1);
            
            double center = (llCenter + prevLL.getPosition().x + prevLL.getSize().x / 2) / 2;
            klabel.setX(center - klabel.getWidth() / 2 + offset.x);
            break;
            
        case SOURCE:
            // Place label near the source lifeline
            klabel.setX(llCenter - klabel.getWidth() - context.labelSpacing + offset.x);
            break;
            
        case CENTER:
            // Place label at the center of the message
            double targetCenter = smessage.getTarget().getPosition().x + smessage.getTarget().getSize().x / 2;
            klabel.setX((llCenter + targetCenter) / 2 - klabel.getWidth() / 2 + offset.x);
            break;
        }
        
        ensureGraphIsWideEnough(context, klabel.getX() + klabel.getWidth());
    }
    
    private boolean isLeftmostLifeline(final SLifeline slifeline) {
        return slifeline.getHorizontalSlot() == 0;
    }
    
    private boolean isRightmostLifeline(final SLifeline slifeline) {
        return slifeline.getHorizontalSlot() == slifeline.getGraph().getLifelines().size() - 1;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Executions

    /**
     * Apply execution coordinates and adjust positions of messages attached to these executions.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @param slifeline
     *            the lifeline whose executions are to be placed.
     */
    private void applyExecutionCoordinates(final LayoutContext context, final SLifeline slifeline) {
        if (slifeline.getExcecutions().isEmpty()) {
            return;
        }
        
        // Set xPos, maxXPos and height / maxYPos
        arrangeExecutions(context, slifeline);

        // Self-messages need to be processed later, once all executions are placed
        Multimap<SMessage, SExecution> selfMessages = HashMultimap.create();
        
        // Walk through the lifeline's executions
        for (SExecution sexecution : slifeline.getExcecutions()) {
            // TODO We need to calculate proper sizes and positions for these guys
            if (sexecution.getType() == SequenceExecutionType.DURATION
                    || sexecution.getType() == SequenceExecutionType.TIME_CONSTRAINT) {
                
                sexecution.getPosition().y += SequenceLayoutConstants.TWENTY;
            }

            // Apply calculated coordinates to the execution
            ElkNode kexecution = (ElkNode) sexecution.getProperty(InternalSequenceProperties.ORIGIN);
            kexecution.setX(sexecution.getPosition().x + offset.x);
            kexecution.setY(sexecution.getPosition().y + offset.y);
            kexecution.setWidth(sexecution.getSize().x);
            kexecution.setHeight(sexecution.getSize().y);
            
            ensureGraphIsWideEnough(context, kexecution.getX() + kexecution.getWidth());

            // Walk through execution's messages and adjust their position
            for (SMessage smessage : sexecution.getMessages()) {
                // Self-message are processed later
                if (smessage.getSource() == smessage.getTarget()) {
                    selfMessages.put(smessage, sexecution);
                    continue;
                }
                
                // Check if the message points rightwards
                boolean messagePointsRightwards =
                        smessage.getSource().getHorizontalSlot() < smessage.getTarget().getHorizontalSlot();

                ElkEdge kmessage = (ElkEdge) smessage.getProperty(InternalSequenceProperties.ORIGIN);
                ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(kmessage, false, false);
                
                // x coordinate for messages attached to the left side of the execution
                double newXPos = sexecution.getPosition().x + offset.x;
                
                if (smessage.getSource() == slifeline) {
                    if (messagePointsRightwards) {
                        newXPos += sexecution.getSize().x;
                    }
                    
                    double delta = newXPos - edgeSection.getStartX();
                    offsetX(edgeSection, true, false, false, delta, context);
                    
                    // TODO Labels positioned at the source should be offset as well
                    
                } else if (smessage.getTarget() == slifeline) {
                    if (!messagePointsRightwards) {
                        newXPos += sexecution.getSize().x;
                    }
                    double delta = newXPos - edgeSection.getEndX();
                    offsetX(edgeSection, false, false, true, delta, context);
                }
            }
        }
        
        applyExecutionCoordinatesToSelfMessages(slifeline, selfMessages, context);
    }

    /**
     * Set x position and width of all executions and check for minimum height.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @param slifeline
     *            the lifeline the executions belong to.
     */
    private void arrangeExecutions(final LayoutContext context, final SLifeline slifeline) {
        List<SExecution> sexecutions = slifeline.getExcecutions();
        
        // All executions are initially centered in their lifeline (the width of some of them is set later)
        double x = slifeline.getPosition().x + (slifeline.getSize().x - context.executionWidth) / 2;
        sexecutions.stream().forEach(exec -> exec.getPosition().x = x);

        // If there are multiple executions, some may have to be shifted horizontally if they overlap
        if (sexecutions.size() > 1) {
            for (SExecution sexecution : sexecutions) {
                if (sexecution.getType() == SequenceExecutionType.DURATION
                        || sexecution.getType() == SequenceExecutionType.TIME_CONSTRAINT) {
                    
                    continue;
                }
                
                int slot = 0;
                for (SExecution otherExecution : sexecutions) {
                    if (sexecution != otherExecution) {
                        // The "less OR EQUAL" when calculating "bottomOverlaps" is significant here:
                        // several executions can end at the same message
                        boolean topOverlaps = sexecution.getPosition().y > otherExecution.getPosition().y;
                        boolean bottomOverlaps = sexecution.getPosition().y + sexecution.getSize().y
                                <= otherExecution.getPosition().y + otherExecution.getSize().y;
                        
                        if (topOverlaps && bottomOverlaps) {
                            slot++;
                        }
                    }
                }
                
                // Shift execution position
                sexecution.getPosition().x += slot * context.executionWidth / 2;
            }
        }

        // Check minimum height of executions and set width
        for (SExecution sexecution : sexecutions) {
            KVector size = sexecution.getSize();
            
            size.y = Math.max(size.y, context.minExecutionHeight);
            
            if (sexecution.getType() == SequenceExecutionType.EXECUTION) {
                sexecution.getSize().x = context.executionWidth;
            }
        }
    }
    
    /**
     * Takes a bunch of self messages and offsets their source, target, and end points to accomodate
     * for the executions they're incident to. Each message in the list is assumed to be a self
     * message incident to at least one execution. All these executions as well as their lifeline
     * are assumed to have already been placed.
     * 
     * @param slifeline
     *            the lifeline whose self-messages we are processing.
     * @param selfMessages
     *            the self messages to be offset. This is a multimap that maps each self-loop
     *            message to those executions it is officially incident to.
     * @param context
     *            the layout context that contains all relevant information for the current layout
     *            run.
     */
    private void applyExecutionCoordinatesToSelfMessages(final SLifeline slifeline,
            final Multimap<SMessage, SExecution> selfMessages, final LayoutContext context) {
        
        double lifelineXCenter = slifeline.getSize().x / 2;
        
        for (final SMessage smessage : selfMessages.keySet()) {
            assert smessage.getSource() == slifeline && smessage.getTarget() == slifeline;
            
            // Retrieve message layout info
            ElkEdge kmessage = (ElkEdge) smessage.getProperty(InternalSequenceProperties.ORIGIN);
            ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(kmessage, false, false);
            
            // We iterate over all connected executions and check for both the source and the target
            // point if they are in the execution's vertical area. For each of the points, we remember
            // the required offset and apply it. The two bend points are offset by the maximum.
            double sourceOffset = 0;
            double targetOffset = 0;
            
            for (SExecution sexecution : selfMessages.get(smessage)) {
                // The execution's coordinates and the bend points share the same coordinate system
                double execTopYPos = sexecution.getPosition().y + offset.y;
                double execBotYPos = execTopYPos + sexecution.getSize().y;
                
                // Check if the source point is in the execution's area
                if (edgeSection.getStartY() >= execTopYPos && edgeSection.getStartY() <= execBotYPos) {
                    sourceOffset = Math.max(sourceOffset,
                            sexecution.getPosition().x + sexecution.getSize().x - lifelineXCenter);
                }
                
                // Check if the target point is in the execution's area
                if (edgeSection.getEndY() >= execTopYPos && edgeSection.getEndY() <= execBotYPos) {
                    targetOffset = Math.max(targetOffset,
                            sexecution.getPosition().x + sexecution.getSize().x - lifelineXCenter);
                }
            }
            
            // Apply offsets
            double maxOffset = Math.max(sourceOffset, targetOffset);
            
            offsetX(edgeSection, true, false, false, sourceOffset, context);
            offsetX(edgeSection, false, true, false, maxOffset, context);
            offsetX(edgeSection, false, false, true, targetOffset, context);
            
            offsetLabelsX(kmessage.getLabels(), maxOffset, context);
        }
    }
    
    /**
     * Adds the given delta to the start or the target of the given edge section. Also ensures the graph is wide enough
     * to accomodate the new point.
     * 
     * @param section
     *            the edge section to offset.
     * @param offsetStartPoint
     *            {@code true} if the start point should be offset.
     * @param offsetBentPoints
     *            {@code true} if the bend points should be offset.
     * @param offsetEndPoint
     *            {@code true} if the end point should be offset.
     * @param delta
     *            the amount to add to the X coordinate.
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void offsetX(final ElkEdgeSection section, final boolean offsetStartPoint, final boolean offsetBendPoints,
            final boolean offsetEndPoint, final double delta, final LayoutContext context) {
        
        if (offsetStartPoint) {
            section.setStartX(section.getStartX() + delta);
            ensureGraphIsWideEnough(context, section.getStartX());
        }
        
        if (offsetBendPoints) {
            for (ElkBendPoint point : section.getBendPoints()) {
                point.setX(point.getX() + delta);
                ensureGraphIsWideEnough(context, point.getX());
            }
        }
        
        if (offsetEndPoint) {
            section.setEndX(section.getEndX() + delta);
            ensureGraphIsWideEnough(context, section.getEndX());
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
    // Areas
    
    /**
     * Place the areas according to their calculated coordinates.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void placeAreas(final LayoutContext context) {
        for (SArea sarea : context.sgraph.getAreas()) {
            ElkNode karea = (ElkNode) sarea.getProperty(InternalSequenceProperties.ORIGIN);
            
            karea.setLocation(sarea.getPosition().x + offset.x, sarea.getPosition().y + offset.y);
            karea.setDimensions(sarea.getSize().x, sarea.getSize().y);
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Comments

    /**
     * Place the comment objects (comments, constraints) according to their calculated coordinates.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void placeComments(final LayoutContext context) {
        for (SComment scomment : context.sgraph.getComments()) {
            ElkNode kcomment = (ElkNode) scomment.getProperty(InternalSequenceProperties.ORIGIN);
            
            kcomment.setLocation(
                    scomment.getPosition().x + offset.x,
                    scomment.getPosition().y + offset.y);
            
            if (scomment.getReferenceMessage() != null) {
                // Set coordinates for the connection of the comment
                double edgeSourceXPos, edgeSourceYPos, edgeTargetXPos, edgeTargetYPos;
                
                if (scomment.getAttachment() != null) {
                    if (scomment.getAttachment() instanceof SLifeline) {
                        // Connections to lifelines or executions are drawn horizontally
                        SLifeline slifeline = scomment.getLifeline();
                        
                        edgeSourceXPos = scomment.getPosition().x;
                        edgeSourceYPos = scomment.getPosition().y + scomment.getSize().y / 2;
                        edgeTargetXPos = slifeline.getPosition().x + slifeline.getSize().x / 2;
                        edgeTargetYPos = edgeSourceYPos;
                        
                    } else {
                        // Connections to messages are drawn vertically
                        edgeSourceXPos = scomment.getPosition().x + scomment.getSize().x / 2;
                        edgeTargetXPos = edgeSourceXPos;
                        
                        ElkEdge edge = (ElkEdge) scomment.getReferenceMessage().getProperty(InternalSequenceProperties.ORIGIN);
                        ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(edge, false, false);
                        edgeSourceYPos = scomment.getPosition().y + scomment.getSize().y;
                        edgeTargetYPos = (edgeSection.getEndY() + edgeSection.getStartY()) / 2;
                    }

                    // Apply connection coordinates to layout
                    ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(
                            scomment.getProperty(InternalSequenceProperties.COMMENT_CONNECTION), true, true);
                    edgeSection.setStartLocation(edgeSourceXPos, edgeSourceYPos);
                    edgeSection.setEndLocation(edgeTargetXPos, edgeTargetYPos);
                }
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Utility Methods
    
    /**
     * Ensures that the context's SGraph's width is wide enough to accomodate an element that extends to the given x
     * coordinate.
     * 
     * @param context
     *            the layout context.
     * @param rightmostCoordinate
     *            x coordinate of the right border of the element that needs to fit inside the graph, with offset
     *            already applied.
     */
    private void ensureGraphIsWideEnough(final LayoutContext context, final double rightmostCoordinate) {
        KVector graphSize = context.sgraph.getSize();
        graphSize.x = Math.max(graphSize.x, rightmostCoordinate + context.sgraph.getPadding().right);
    }

}
