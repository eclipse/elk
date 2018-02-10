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
package org.eclipse.elk.alg.sequence.p5coordinates;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.sequence.SequenceLayoutConstants;
import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SArea;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.options.MessageType;
import org.eclipse.elk.alg.sequence.options.NodeType;
import org.eclipse.elk.alg.sequence.options.SequenceDiagramOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;

/**
 * Calculates coordinates for many objects in a sequence diagram.
 * 
 * <p>The coordinate system is interpreted as follows. The point (0, 0) (the upper left corner) is the top left corner
 * of the interaction's content. All coordinates we calculate here are relative to that point, and the graph's size
 * will only span the content as well. Y coordinate 0 is where all lifelines start (unless they are created through a
 * create message). The export code is responsible for including any paddings.</p>
 */
public class CoordinateCalculator implements ILayoutPhase<SequencePhases, LayoutContext> {

    @Override
    public LayoutProcessorConfiguration<SequencePhases, LayoutContext> getLayoutProcessorConfiguration(
            final LayoutContext graph) {
        
        return LayoutProcessorConfiguration.create();
    }

    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Calculate coordinates", 1);
        
        // Initialize graph size
        KVector sgraphSize = context.sgraph.getSize();
        
        sgraphSize.x = 0;
        sgraphSize.y = 0;

        // Assign vertical position to SMessages. This sets the graph's height to accomodate all messages.
        calculateMessageYPositions(context);
        
        // Lifelines should extend a small bit beyond their lowermost incident message
        sgraphSize.y += context.messageSpacing / 2;

        // Arrange comments that are connected to a message or lifeline
        arrangeConnectedComments(context);
        
        // Calculate where we need to place the first non-dummy lifeline
        double xPos = calculateFirstLifelineXPosition(context);
        
        // Set position for lifelines/nodes
        boolean foundNonDummyLifeline = false;
        for (SLifeline lifeline : context.sgraph.getLifelines()) {
            // Dummy lifelines don't need any layout
            if (lifeline.isDummy()) {
                continue;
            }
            
            foundNonDummyLifeline = true;

            // Calculate the spacing between this lifeline and its successor. Place comments.
            double thisLifelineSpacing = calculateLifelineSpacingAndPlaceComments(context, xPos, lifeline);

            // Set position and height for the lifeline. This may be overridden if there are create or delete
            // messages involved.
            lifeline.getPosition().y = 0;
            lifeline.getPosition().x = xPos;
            lifeline.getSize().y = sgraphSize.y;

            // Advance x pointer and apply to graph size
            xPos += lifeline.getSize().x + thisLifelineSpacing;
            sgraphSize.x = xPos;
        }
        
        // The graph's width may now include a lifeline spacing too much on the right side
        if (foundNonDummyLifeline) {
            sgraphSize.x -= context.lifelineSpacing;
        }
        
        // Arrange unconnected comments (after the last lifeline)
        arrangeUnconnectedComments(context);

        // Handle areas (interactions / combined fragments / interaction operands)
        calculateAreaPosition(context);

        progressMonitor.done();
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Messages

    /**
     * Apply the layering to the SGraph and check for message overlappings.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void calculateMessageYPositions(final LayoutContext context) {
        // Position of first layer of messages
        double layerPos = context.lifelineHeaderHeight + context.messageSpacing;

        // Iterate the layers of nodes that represent messages
        for (Layer layer : context.lgraph.getLayers()) {
            // Iterate the nodes of the layer
            for (LNode node : layer) {
                // Get the corresponding message and skip dummy nodes (which don't have a message)
                if (!node.hasProperty(InternalSequenceProperties.ORIGIN)) {
                    continue;
                }
                
                SMessage message = (SMessage) node.getProperty(InternalSequenceProperties.ORIGIN);
                
                // If the message is represented by a single node, that node won't have a property to say that it
                // belongs to a given lifeline. If it is represented by two nodes, however, those nodes will specify
                // which lifeline each belongs to.
                SLifeline lifeline = node.getProperty(InternalSequenceProperties.BELONGS_TO_LIFELINE);
                if (lifeline != null) {
                    // It is in fact represented by two nodes. Thus, set the position of the message's source or
                    // target, depending on what the current node represents
                    if (message.getTarget() == lifeline) {
                        message.setTargetYPos(layerPos);
                    } else {
                        message.setSourceYPos(layerPos);
                    }
                    continue;
                }
                
                // Starting here we know that the message is represented by a single node. If the message already had
                // its position set before we encountered its node in our for loop, simply continue. (this case can
                // happen if we set a node's position in the loop below)
                if (message.isMessageLayerPositionSet()) {
                    continue;
                }

                // Check which lifeline numbers the message spans
                int sourceSlot = message.getSource().getHorizontalSlot();
                int targetSlot = message.getTarget().getHorizontalSlot();

                if (Math.abs(sourceSlot - targetSlot) > 1) {
                    // There are lifelines between the message's source and target. It might overlap other messages,
                    // so we need to assign non-conflicting positions to those messages
                    for (LNode otherNode : layer) {
                        SMessage otherMessage = (SMessage) otherNode.getProperty(InternalSequenceProperties.ORIGIN);
                        
                        int otherSourceSlot = otherMessage.getSource().getHorizontalSlot();
                        int otherTargetSlot = otherMessage.getTarget().getHorizontalSlot();

                        // If the other message starts or ends between the start and the end
                        // of the tested message, there is an overlapping
                        if (overlap(sourceSlot, targetSlot, otherSourceSlot, otherTargetSlot)) {
                            if (otherMessage.isMessageLayerPositionSet()) {
                                // If the other message was already placed, we need to advance the current y pointer
                                layerPos += context.messageSpacing;
                                break;
                            } else if (Math.abs(otherSourceSlot - otherTargetSlot) <= 1) {
                                // If the other message has not been placed yet and is a short one, it will be placed
                                // here (this is why we check whether the message's coordinates have already been set
                                // previously)
                                otherMessage.setMessageLayerYPos(layerPos);
                                layerPos += context.messageSpacing;
                                break;
                            }
                        }
                    }
                }
                
                // Set the vertical position of the message
                message.setMessageLayerYPos(layerPos);

                // Make sure the source of a self-loop is moved upwards a bit
                if (message.getSource() == message.getTarget()) {
                    message.setSourceYPos(layerPos - context.messageSpacing / 2);
                }
            }
            
            // Advance to the next message routing slot
            layerPos += context.messageSpacing;
        }
    }

    /**
     * Check if two messages overlap.
     * 
     * @param msg1source
     *            the source position of the first message
     * @param msg1target
     *            the target position of the first message
     * @param msg2source
     *            the source position of the second message
     * @param msg2target
     *            the target position of the second message
     * @return {@code true} if the messages overlap.
     */
    private boolean overlap(final int msg1source, final int msg1target,
            final int msg2source, final int msg2target) {

        return isBetween(msg1source, msg2source, msg2target)
                || isBetween(msg1target, msg2source, msg2target)
                || isBetween(msg2source, msg1source, msg1target)
                || isBetween(msg2target, msg1source, msg1target);
    }

    /**
     * Checks if {@code bound1 < x < bound2}.
     */
    private boolean isBetween(final int x, final int bound1, final int bound2) {
        // x is between 1 and 2 if it is not smaller than both or greater than both
        return !((x <= bound1 && x <= bound2) || (x >= bound1 && x >= bound2));
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Comments

    /**
     * Preprocessor that does some organizing stuff for connected comment objects.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void arrangeConnectedComments(final LayoutContext context) {
        for (SComment comment : context.sgraph.getComments()) {
            SMessage message = null;
            SLifeline lifeline = null;
            
            // Get random connected message and lifeline if existing. This may be optimized if there is more than
            // one connection
            if (comment.getAttachment() instanceof SMessage) {
                message = (SMessage) comment.getAttachment();
            } else if (comment.getAttachment() instanceof SLifeline) {
                lifeline = (SLifeline) comment.getAttachment();
            }

            comment.setReferenceMessage(message);
            comment.setLifeline(lifeline);

            // If the comment is attached to a message, determine if it should be drawn near the beginning or near the
            // end of the message. If the comment is attached to a message and one of the message's lifelines, it
            // should be drawn near that lifeline (this is the case for time observations for example).
            if (message != null) {
                SLifeline right, left;
                
                if (message.getSource().getHorizontalSlot() < message.getTarget().getHorizontalSlot()) {
                    // Message leads rightwards
                    left = message.getSource();
                    right = message.getTarget();
                } else {
                    // Message leads leftwards or is self-loop
                    left = message.getTarget();
                    right = message.getSource();
                }
                
                if (lifeline == right) {
                    // Find lifeline left to "right" and attach comment to that lifeline because comments are drawn
                    // right of the connected lifeline.
                    int position = right.getHorizontalSlot();
                    for (SLifeline ll : context.sgraph.getLifelines()) {
                        if (ll.getHorizontalSlot() == position - 1) {
                            comment.setLifeline(ll);
                            break;
                        }
                    }
                } else {
                    comment.setLifeline(left);
                }
            }
        }
    }

    /**
     * Calculate positions for comments that are not connected to any element. They will be drawn
     * after the last lifeline.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void arrangeUnconnectedComments(final LayoutContext context) {
        // The width of the widest comment that has to be placed after the last lifeline
        double commentMaxExtraWidth = 0;
        
        // The vertical position of the next comment that is drawn after the last lifeline
        double commentNextYPos = context.lifelineHeaderHeight;

        for (SComment comment : context.sgraph.getComments()) {
            if (comment.getReferenceMessage() == null) {
                // Unconnected comments
                if (comment.getSize().x > commentMaxExtraWidth) {
                    commentMaxExtraWidth = comment.getSize().x;
                }
                
                // Set position of unconnected comments next to the last lifeline
                comment.getPosition().x = context.sgraph.getSize().x;
                comment.getPosition().y = commentNextYPos;
                commentNextYPos += comment.getSize().y + context.messageSpacing;
            }
        }

        if (commentMaxExtraWidth > 0) {
            context.sgraph.getSize().x += commentMaxExtraWidth + context.lifelineSpacing;
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Lifelines
    
    /**
     * Calculate the x coordinate of the first non-dummy lifeline. This is usually zero, but may need to be larger if
     * the lifeline has incoming found messages.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @return the first lifeline's x coordinate.
     */
    private double calculateFirstLifelineXPosition(final LayoutContext context) {
        // Find the first non-dummy lifeline
        Optional<SLifeline> firstLifelineOptional = context.sgraph.getLifelines().stream()
                .filter(ll -> !ll.isDummy())
                .findFirst();
        
        if (firstLifelineOptional.isPresent()) {
            boolean hasFoundMessages = StreamSupport
                    .stream(firstLifelineOptional.get().getIncomingMessages().spliterator(), false)
                    .anyMatch(msg -> msg.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) == MessageType.FOUND);
            
            if (hasFoundMessages) {
                // This is the only case where we need extra space
                return context.lifelineSpacing;
            }
        }
        
        return 0.0;
    }

    /**
     * Calculate the spacing between the current lifeline and its successor. This is done by
     * considering the width of the comments between them and by the width of the widest label
     * attached to a message between them.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @param xPos
     *            the horizontal position where the last lifeline was placed
     * @param lifeline
     *            the current lifeline
     * @return the width of the lifeline plus comments.
     */
    private double calculateLifelineSpacingAndPlaceComments(final LayoutContext context, final double xPos,
            final SLifeline lifeline) {
        
        double spacing = context.lifelineSpacing;

        // Check, if there are labels longer than the available space
        // TODO This code assumes that message labels are simply placed on the lifeline, which is strange...
        for (SMessage message : lifeline.getIncomingMessages()) {
            if (message.getLabelWidth() > lifeline.getSize().x + spacing) {
                spacing = context.labelSpacing + message.getLabelWidth() - lifeline.getSize().x;
            }
        }
        
        for (SMessage message : lifeline.getOutgoingMessages()) {
            if (message.getLabelWidth() > lifeline.getSize().x + spacing) {
                spacing = context.labelSpacing + message.getLabelWidth() - lifeline.getSize().x;
            }
            
            // Labels of create messages should not overlap the target's header
            if (message.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) == MessageType.CREATE) {
                // TODO Shouldn't this be dependent on the target lifeline's size, not on this lifeline?
                if (message.getLabelWidth() + context.labelSpacing > spacing + lifeline.getSize().x / 2) {
                    
                    spacing = context.labelSpacing + message.getLabelWidth()
                            - message.getTarget().getSize().x / 2;
                }
            } 
            
            // Selfloops need a little more space
            if (message.getSource() == message.getTarget()) {
                if (message.getLabelWidth() + context.labelSpacing
                        + context.messageSpacing / 2 > spacing + lifeline.getSize().x / 2) {
                    
                    spacing = context.labelSpacing + message.getLabelWidth() - lifeline.getSize().x / 2;
                }
            }
        }

        // Get the list of comments attached to the current lifeline

        // Return if there are no comments attached
        List<SComment> comments = lifeline.getComments();
        if (comments.isEmpty()) {
            return spacing;
        }

        // Check maximum size of comments attached to the lifeline
        for (SComment comment : comments) {
            if (comment.getSize().x > spacing) {
                spacing = comment.getSize().x;
            }
        }

        // HashMap that organizes which comment belongs to which message. This is important if there are more than
        // one comments at a message.
        // TODO This will only work for up to two comments, won't it?
        HashMap<SMessage, SComment> messageCommentMap = new HashMap<SMessage, SComment>(comments.size());
        for (SComment comment : comments) {
            if (comment.getLifeline() == lifeline) {
                SMessage message = comment.getReferenceMessage();

                // Place comment centered above the message
                KVector commentPos = comment.getPosition();
                
                commentPos.x = xPos + lifeline.getSize().x + (spacing - comment.getSize().x) / 2;
                commentPos.y = message.getSourceYPos() - (comment.getSize().y + context.messageSpacing);

                if (messageCommentMap.containsKey(message)) {
                    // Handle conflicts (reset yPos if necessary)
                    SComment upper = comment;
                    SComment lower = messageCommentMap.get(message);
                    NodeType nodeType = comment.getProperty(SequenceDiagramOptions.TYPE_NODE);
                    
                    // If comment is Observation, place it nearer to the message
                    if (nodeType == NodeType.DURATION_OBSERVATION || nodeType == NodeType.TIME_OBSERVATION) {
                        upper = lower;
                        lower = comment;
                    }

                    // Place lower comment first
                    commentPos.y = message.getSourceYPos() - (lower.getSize().y + context.messageSpacing);
                    lower.getPosition().y = commentPos.y;

                    // Place upper comment near to lower one
                    double uYpos = lower.getPosition().y - upper.getSize().y - context.messageSpacing / 2;
                    upper.getPosition().y = uYpos;
                    
                } else {
                    messageCommentMap.put(message, comment);
                }
            }
        }
        
        return spacing;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Areas

    /**
     * Calculate the position of the areas (interactionUse, combined fragment).
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void calculateAreaPosition(final LayoutContext context) {
        // Set size and position of area
        for (SArea area : context.sgraph.getAreas()) {
            if (area.getMessages().size() > 0) {
                setAreaPositionByMessages(area);
            } else {
                setAreaPositionByLifelinesAndMessage(context, area);
            }

            // Check if there are contained areas
            int containmentDepth = checkHierarchy(area);
            
            // If so, an offset has to be calculated in order not to have overlapping borders
            int containmentSpacing = (int) (containmentDepth * context.containmentOffset);
            
            KVector areaPos = area.getPosition();
            KVector areaSize = area.getSize();

            areaPos.x = area.getPosition().x - context.lifelineSpacing / 2 - containmentSpacing;
            areaSize.x = area.getSize().x + context.lifelineSpacing + 2 * containmentSpacing;

            // TODO This needs to be handled better
            areaPos.y = area.getPosition().y - (containmentDepth + 1) * context.areaHeaderHeight
                    - SequenceLayoutConstants.TWENTY;
            areaSize.y = area.getSize().y + (containmentDepth + 1) * context.areaHeaderHeight + containmentSpacing
                    + SequenceLayoutConstants.FOURTY;
            
            // The area might have a label that needs to be positioned as well
            calculateAreaLabelPosition(context, area);

            // Handle interaction operands
            // TODO Review this
            if (area.getSections().size() > 0) {
                // Reset area yPos and height (to have a "header" that isn't occupied by any subArea)
                areaPos.y = area.getPosition().y - context.messageSpacing / 2;
                areaSize.y = area.getSize().y + context.messageSpacing + context.lifelineHeaderHeight;

                double lastPos = 0;
                SArea lastSubArea = null;
                for (SArea subArea : area.getSections()) {
                    subArea.getPosition().x = 0;
                    subArea.getSize().x = areaSize.x + SequenceLayoutConstants.FOURTY + context.lifelineSpacing - 2;
                    
                    if (subArea.getMessages().size() > 0) {
                        // Calculate and set y-position by the area's messages
                        setAreaPositionByMessages(subArea);
                        subArea.getSize().y = subArea.getPosition().y
                                - area.getPosition().y + context.lifelineHeaderHeight
                                - context.messageSpacing / 2;
                    } else {
                        // Calculate and set y-position by the available space
                        // FIXME if subarea is empty, it appears first in the list
                        subArea.getPosition().y = lastPos;
                    }

                    // Reset last subArea's height to fit
                    if (lastSubArea != null) {
                        lastSubArea.getSize().y = subArea.getPosition().y - lastSubArea.getPosition().y;
                    }
                    lastPos = subArea.getPosition().y + subArea.getSize().y;
                    lastSubArea = subArea;
                }
                
                // Reset last subArea's height to fit
                if (lastSubArea != null) {
                    lastSubArea.getSize().y = areaSize.y - areaPos.y - context.areaHeaderHeight;
                }
            }
        }
    }


    /**
     * Searches all the contained edges and sets the area's position and size such that it is a
     * bounding box for the contained messages.
     * 
     * @param area
     *            the SequenceArea
     * @param factor
     *            the edgeYpos factor, that is necessary to compute the real position of the
     *            SMessage
     */
    private void setAreaPositionByMessages(final SArea area) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = 0;
        double maxY = 0;
        
        // Compute the bounding box of all contained messages
        for (Object messObj : area.getMessages()) {
            if (messObj instanceof SMessage) {
                // Compute new y coordinates
                SMessage message = (SMessage) messObj;
                
                double sourceYPos = message.getSourceYPos();
                minY = Math.min(minY, sourceYPos);
                maxY = Math.max(maxY, sourceYPos);
                
                double targetYPos = message.getTargetYPos();
                minY = Math.min(minY, targetYPos);
                maxY = Math.max(maxY, targetYPos);
                
                // Compute new x coordinates
                SLifeline sourceLL = message.getSource();
                double sourceXPos = sourceLL.getPosition().x + sourceLL.getSize().x / 2;
                minX = Math.min(minX, sourceXPos);
                maxX = Math.max(maxX, sourceXPos);
                
                SLifeline targetLL = message.getTarget();
                double targetXPos = targetLL.getPosition().x + targetLL.getSize().x / 2;
                minX = Math.min(minX, targetXPos);
                maxX = Math.max(maxX, targetXPos);
            }
        }
        
        area.getPosition().x = minX;
        area.getPosition().y = minY;
        area.getSize().x = maxX - minX;
        area.getSize().y = maxY - minY;
    }

    /**
     * Sets the areas position such that it is a bounding box for the involved lifelines in x
     * direction and above the next message.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout
     *            run.
     * @param area
     *            the sequence area
     */
    private void setAreaPositionByLifelinesAndMessage(final LayoutContext context, final SArea area) {
        // Set xPos and width according to the involved lifelines
        double minXPos = Double.MAX_VALUE;
        double maxXPos = 0;
        
        for (SLifeline lifeline : area.getLifelines()) {
            double lifelineCenter = lifeline.getPosition().x + lifeline.getSize().x / 2;
            
            minXPos = Math.min(minXPos, lifelineCenter);
            maxXPos = Math.max(maxXPos, lifelineCenter);
        }
        
        area.getPosition().x = minXPos;
        area.getSize().x = maxXPos - minXPos;

        // Set yPos and height according to the next message's yPos
        if (area.getNextMessage() != null) {
            SMessage message = area.getNextMessage();
            
            double messageYPos;
            if (message.getSourceYPos() < message.getTargetYPos()) {
                messageYPos = message.getSourceYPos();
            } else {
                messageYPos = message.getTargetYPos();
            }
            
            area.getSize().y = context.messageSpacing;
            area.getPosition().y = messageYPos - area.getSize().y - context.messageSpacing;
        }
    }

    /**
     * Check recursively if an area has contained areas and return the maximum depth.
     * 
     * @param area
     *            the {@link SArea}
     * @return the maximum depth of hierarchy
     */
    private int checkHierarchy(final SArea area) {
        if (area.getContainedAreas().size() > 0) {
            int maxLevel = 0;
            for (SArea subArea : area.getContainedAreas()) {
                int level = checkHierarchy(subArea);
                if (level > maxLevel) {
                    maxLevel = level;
                }
            }
            return maxLevel + 1;
        } else {
            return 0;
        }
    }

    /**
     * Positions an area's label, if any.
     * 
     * @param area
     *            the area whose label to position. The corresponding layout node needs to have its size
     *            calculated already.
     */
    private void calculateAreaLabelPosition(final LayoutContext context, final SArea area) {
        // TODO Labels should be properly represented in the SGraph
        ElkNode areaNode = (ElkNode) area.getProperty(InternalSequenceProperties.ORIGIN);
        if (areaNode.getLabels().isEmpty()) {
            return;
        }
        
        ElkLabel areaLabel = areaNode.getLabels().get(0);
        
        areaLabel.setY(context.labelSpacing);
        areaLabel.setX(areaNode.getWidth() - areaNode.getWidth() - context.labelSpacing);
    }
}
