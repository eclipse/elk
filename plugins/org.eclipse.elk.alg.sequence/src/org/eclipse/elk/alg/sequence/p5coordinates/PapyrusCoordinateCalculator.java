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

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.sequence.ISequenceLayoutProcessor;
import org.eclipse.elk.alg.sequence.LayoutContext;
import org.eclipse.elk.alg.sequence.SequenceLayoutConstants;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SGraphElement;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.p6export.PapyrusExporter;
import org.eclipse.elk.alg.sequence.properties.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.properties.MessageType;
import org.eclipse.elk.alg.sequence.properties.NodeType;
import org.eclipse.elk.alg.sequence.properties.SequenceArea;
import org.eclipse.elk.alg.sequence.properties.SequenceDiagramOptions;
import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KNode;

/**
 * Calculates coordinates for all objects in a sequence diagram. The coordinates are calculated such
 * that the {@link org.eclipse.elk.alg.sequence.p6export.PapyrusExporter} knows how to interpret
 * them.
 * 
 * <p>
 * The division between this coordinate calculator and the {@link KGraphCoordinateCalculator} is
 * mighty unfortunate. At some point, the {@link PapyrusExporter} should be changed to work with the
 * results produced by the {@link KGraphCoordinateCalculator}.
 * </p>
 * 
 * @author grh
 */
public class PapyrusCoordinateCalculator implements ISequenceLayoutProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Calculate coordinates", 1);
        
        // Initialize graph size
        context.sgraph.getSize().x = 0;
        context.sgraph.getSize().y = 0;

        // Assign vertical position to SMessages
        calculateMessageYCoords(context);

        // Arrange comments that are connected to a message or lifeline
        arrangeConnectedComments(context);

        // Position of the next lifeline (at first, of the first lifeline)
        double xPos = context.borderSpacing;

        // The height of all "normal-sized" (not affected by create or delete messages) lifelines
        double lifelinesHeight =
                context.lifelineHeader + context.sgraph.getSize().y + context.messageSpacing;

        // Set position for lifelines/nodes
        for (SLifeline lifeline : context.lifelineOrder) {
            // Dummy lifelines don't need any layout
            if (lifeline.isDummy()) {
                continue;
            }

            // Calculate the spacing between this lifeline and its successor. Place comments.
            double thisLifelinesSpacing = calculateLifelineSpacing(context, xPos, lifeline);

            // Set position and height for the lifeline. This may be overridden if there are create-
            // or delete-messages involved.
            lifeline.getPosition().y = context.lifelineYPos;
            lifeline.getPosition().x = xPos;
            lifeline.getSize().y = lifelinesHeight;

            // Apply maximum comment width to new xPos
            xPos += lifeline.getSize().x + thisLifelinesSpacing;
            // Reset the graph's horizontal size
            if (context.sgraph.getSize().x < xPos) {
                context.sgraph.getSize().x = xPos;
            }
        }

        // Arrange unconnected comments (after the last lifeline)
        arrangeUnconnectedComments(context);

        // Handle areas (interactions / combined fragments / interaction operands)
        List<SequenceArea> areas = context.sgraph.getProperty(SequenceDiagramOptions.AREAS);
        // Check containments (hierarchy) of areas
        checkAreaContainment(areas);
        // Calculate the areas positions
        calculateAreaPosition(context, areas);

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
    private void calculateMessageYCoords(final LayoutContext context) {
        // Position of first layer of messages
        double layerpos = context.lifelineYPos + context.lifelineHeader + context.messageSpacing;

        // Iterate the layers of nodes that represent messages
        for (int i = 0; i < context.lgraph.getLayers().size(); i++) {
            // Iterate the nodes of the layer
            for (LNode node : context.lgraph.getLayers().get(i).getNodes()) {
                // Get the corresponding message
                SMessage message = (SMessage) node.getProperty(InternalProperties.ORIGIN);

                // Skip dummyNodes
                if (message == null) {
                    continue;
                }

                SLifeline lifeline = node.getProperty(InternalSequenceProperties.BELONGS_TO_LIFELINE);
                
                // This property is set only for split messages
                if (lifeline != null) {
                    // Consider messages that have different layers for source and target
                    // nodes/heights
                    if (message.getTarget() == lifeline) {
                        message.setTargetYPos(layerpos);
                    } else {
                        message.setSourceYPos(layerpos);
                    }
                    continue;
                }

                if (message.isLayerPositionSet()) {
                    // Skip iteration message if it was already set
                    continue;
                }

                int sourceXPos = message.getSource().getHorizontalSlot();
                int targetXPos = message.getTarget().getHorizontalSlot();

                // If the message crosses at least one lifeline, check for overlappings
                if (Math.abs(sourceXPos - targetXPos) > 1) {
                    // Check overlappings with any other node in the layer
                    for (LNode otherNode : context.lgraph.getLayers().get(i).getNodes()) {
                        // Get the corresponding message
                        SMessage otherMessage =
                                (SMessage) otherNode.getProperty(InternalProperties.ORIGIN);
                        try {
                            int otherSourcePos = otherMessage.getSource().getHorizontalSlot();
                            int otherTargetPos = otherMessage.getTarget().getHorizontalSlot();

                            // If the other message starts or ends between the start and the end
                            // of the tested message, there is an overlapping
                            if (doMessagesOverlap(sourceXPos, targetXPos, otherSourcePos,
                                    otherTargetPos)) {
                                
                                if (otherMessage.isLayerPositionSet()) {
                                    // If the other message was already set, this message has to
                                    // be placed in another layer
                                    layerpos += context.messageSpacing;
                                    break;
                                } else if (Math.abs(otherSourcePos - otherTargetPos) <= 1) {
                                    // If the other message was not set and it is a short one,
                                    // the other message has to be set here
                                    otherMessage.setLayerYPos(layerpos);
                                    layerpos += context.messageSpacing;
                                    break;
                                }
                            }
                        } catch (NullPointerException n) {
                            // Ignore
                        }

                    }
                }
                // Set the vertical position of the message
                message.setLayerYPos(layerpos);

                // Handle selfloops
                if (message.getSource() == message.getTarget()) {
                    message.setSourceYPos(layerpos - context.messageSpacing / 2);
                }
            }
            layerpos += context.messageSpacing;
        }
    }

    /**
     * Check if two messages are overlap.
     * 
     * @param msg1source
     *            the source position of the first message
     * @param msg1target
     *            the target position of the first message
     * @param msg2source
     *            the source position of the second message
     * @param msg2target
     *            the target position of the second message
     * @return {@code true} if the messages are overlapping
     */
    private boolean doMessagesOverlap(final int msg1source, final int msg1target,
            final int msg2source, final int msg2target) {

        return isBetween(msg1source, msg2source, msg2target)
                || isBetween(msg1target, msg2source, msg2target)
                || isBetween(msg2source, msg1source, msg1target)
                || isBetween(msg2target, msg1source, msg1target);
    }

    /**
     * Checks if {@code bound1 < x < bound2}.
     * 
     * @param x the value to check.
     * @param bound1 lower bound.
     * @param bound2 upper bound.
     * @return {@code true} if x is between bound1 and bound2.
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
            // Get random connected message and lifeline if existing
            // This may be optimized if there is more than one connection
            for (SGraphElement element : comment.getAttachedTo()) {
                if (element instanceof SMessage) {
                    message = (SMessage) element;
                } else if (element instanceof SLifeline) {
                    lifeline = (SLifeline) element;
                }
            }

            comment.setMessage(message);
            comment.setLifeline(lifeline);

            /* If the comment is attached to a message, determine if it should be drawn near the
             * beginning or near the end of the message. If the comment is attached to a message and
             * one of the message's lifelines, it should be drawn near that lifeline (this is the
             * case for time observations for example).
             */
            if (message != null) {
                SLifeline right, left;
                if (message.getSource().getHorizontalSlot() < message.getTarget()
                        .getHorizontalSlot()) {
                    // Message leads rightwards
                    right = message.getTarget();
                    left = message.getSource();
                } else {
                    // Message leads leftwards or is self-loop
                    right = message.getSource();
                    left = message.getTarget();
                }
                if (lifeline == right) {
                    // Find lifeline left to "right" and attach comment to that lifeline because
                    // comments are drawn right of the connected lifeline.
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
        double commentNextYPos = context.lifelineHeader + context.lifelineYPos;

        for (SComment comment : context.sgraph.getComments()) {
            if (comment.getMessage() == null) {
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
            context.sgraph.getSize().x += context.lifelineSpacing + commentMaxExtraWidth;
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Lifelines

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
     * @return the width of the widest comment
     */
    private double calculateLifelineSpacing(final LayoutContext context, final double xPos,
            final SLifeline lifeline) {
        
        // Initialize spacing with the normal lifeline spacing
        double spacing = context.lifelineSpacing;

        // Check, if there are labels longer than the available space
        for (SMessage message : lifeline.getIncomingMessages()) {
            if (message.getLabelWidth() > spacing + lifeline.getSize().x) {
                spacing = SequenceLayoutConstants.LABELMARGIN + message.getLabelWidth()
                        - lifeline.getSize().x;
            }
        }
        for (SMessage message : lifeline.getOutgoingMessages()) {
            if (message.getLabelWidth() > spacing + lifeline.getSize().x) {
                spacing = SequenceLayoutConstants.LABELMARGIN + message.getLabelWidth()
                        - lifeline.getSize().x;
            }
            // Labels of create messages should not overlap the target's header
            if (message.getProperty(SequenceDiagramOptions.MESSAGE_TYPE) == MessageType.CREATE) {
                if (message.getLabelWidth() + SequenceLayoutConstants.LABELMARGIN
                        > spacing + lifeline.getSize().x / 2) {
                    
                    spacing = SequenceLayoutConstants.LABELMARGIN + message.getLabelWidth()
                            - message.getTarget().getSize().x / 2;
                }
            } 
            // Selfloops need a little more space
            if (message.getSource() == message.getTarget()) {
                if (message.getLabelWidth() + SequenceLayoutConstants.LABELMARGIN
                        + context.messageSpacing / 2 > spacing + lifeline.getSize().x / 2) {
                    
                    spacing = SequenceLayoutConstants.LABELMARGIN + message.getLabelWidth()
                            - lifeline.getSize().x / 2;
                }
            }
        }

        // Get the list of comments attached to the current lifeline
        List<SComment> comments = lifeline.getComments();

        // Return if there are no comments attached
        if (comments == null) {
            return spacing;
        }

        // Check maximum size of comments attached to the lifeline
        for (SComment comment : comments) {
            if (comment.getSize().x > spacing) {
                spacing = comment.getSize().x;
            }
        }

        // HashMap that organizes which comment belongs to which message. This is important
        // if there are more than one comments at a message.
        HashMap<SMessage, SComment> hash = new HashMap<SMessage, SComment>(comments.size());
        for (SComment comment : comments) {
            if (comment.getLifeline() == lifeline) {
                SMessage message = comment.getMessage();

                // Place comment in the center of the message if it is smaller than
                // lifelineSpacing
                double commentXPos = xPos + lifeline.getSize().x;
                if (comment.getSize().x < spacing) {
                    commentXPos += (spacing - comment.getSize().x) / 2;
                }

                // Place comment above the message
                double commentYPos = message.getSourceYPos() + context.lifelineHeader
                        + context.lifelineYPos - (comment.getSize().y + context.messageSpacing);

                comment.getPosition().x = commentXPos;
                comment.getPosition().y = commentYPos;

                if (hash.containsKey(message)) {
                    // Handle conflicts (reset yPos if necessary)
                    SComment upper = comment;
                    SComment lower = hash.get(message);
                    NodeType nodeType = comment.getProperty(SequenceDiagramOptions.NODE_TYPE);
                    
                    // If comment is Observation, place it nearer to the message
                    if (nodeType == NodeType.DURATION_OBSERVATION
                            || nodeType == NodeType.TIME_OBSERVATION) {
                        
                        upper = lower;
                        lower = comment;
                    }

                    // Place lower comment first
                    commentYPos = message.getSourceYPos() + context.lifelineHeader + context.lifelineYPos
                            - (lower.getSize().y + context.messageSpacing);
                    lower.getPosition().y = commentYPos;

                    // Place upper comment near to lower one
                    double uYpos = lower.getPosition().y - upper.getSize().y
                            - context.messageSpacing / 2;
                    upper.getPosition().y = uYpos;
                } else {
                    hash.put(message, comment);
                }
            }
        }
        return spacing;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Areas

    /**
     * Check the hierarchy of areas. This is necessary to avoid overlapping borders.
     * 
     * @param areas
     *            the list of areas
     */
    private void checkAreaContainment(final List<SequenceArea> areas) {
        if (areas != null) {
            for (SequenceArea area : areas) {
                for (SequenceArea otherArea : areas) {
                    if (area != otherArea && area.getMessages().containsAll(otherArea.getMessages())) {
                        // Check if upper left corner is more upper and left than the other
                        // area's corner
                        if (area.getPosition().y < otherArea.getPosition().y
                                && area.getPosition().x < otherArea.getPosition().x) {
                            
                            area.getContainedAreas().add(otherArea);
                        }
                    }
                }
            }
        }
    }

    /**
     * Calculate the position of the areas (interactionUse, combined fragment).
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @param areas
     *            the list of areas in the graph
     */
    private void calculateAreaPosition(final LayoutContext context, final List<SequenceArea> areas) {
        if (areas != null) {
            // Set size and position of area
            for (SequenceArea area : areas) {
                if (area.getMessages().size() > 0) {
                    setAreaPositionByMessages(area);
                } else {
                    setAreaPositionByLifelinesAndMessage(context, area);
                }
                KNode areaNode = (KNode) area.getLayoutNode();
                KShapeLayout areaLayout = areaNode.getData(KShapeLayout.class);

                // Check if there are contained areas
                int containmentDepth = checkHierarchy(area);
                // If so, an offset has to be calculated in order not to have overlapping borders
                int containmentSpacing = (int) (containmentDepth * context.containmentOffset);

                areaLayout.setXpos((float) (area.getPosition().x - SequenceLayoutConstants.TWENTY
                        - context.lifelineSpacing / 2 - containmentSpacing));
                areaLayout.setWidth((float) (area.getSize().x + SequenceLayoutConstants.FOURTY
                        + context.lifelineSpacing + 2 * containmentSpacing));

                areaLayout.setYpos((float) (area.getPosition().y + context.lifelineHeader
                        - context.messageSpacing / 2 - containmentSpacing));
                areaLayout.setHeight((float) (area.getSize().y + context.messageSpacing 
                        + 2 * containmentSpacing));

                // Handle interaction operands
                if (area.getSubAreas().size() > 0) {
                    // Reset area yPos and height if subAreas exists (to have a "header" that isn't
                    // occupied by any subArea)
                    areaLayout.setYpos((float) (area.getPosition().y - context.messageSpacing / 2));
                    areaLayout.setHeight((float)
                            (area.getSize().y + context.messageSpacing + context.lifelineHeader));

                    double lastPos = 0;
                    KShapeLayout lastLayout = null;
                    for (SequenceArea subArea : area.getSubAreas()) {
                        KNode subAreaNode = (KNode) subArea.getLayoutNode();
                        KShapeLayout subAreaLayout = subAreaNode.getData(KShapeLayout.class);

                        subAreaLayout.setXpos(0);
                        subAreaLayout.setWidth((float) (area.getSize().x
                                + SequenceLayoutConstants.FOURTY + context.lifelineSpacing - 2));
                        
                        if (subArea.getMessages().size() > 0) {
                            // Calculate and set y-position by the area's messages
                            setAreaPositionByMessages(subArea);
                            subAreaLayout.setYpos((float) (subArea.getPosition().y
                                    - area.getPosition().y + context.lifelineHeader
                                    - context.messageSpacing / 2));
                        } else {
                            // Calculate and set y-position by the available space
                            subAreaLayout.setYpos((float) lastPos);
                            // FIXME if subarea is empty, it appears first in the list
                        }

                        // Reset last subArea's height to fit
                        if (lastLayout != null) {
                            lastLayout.setHeight(subAreaLayout.getYpos() - lastLayout.getYpos());
                        }
                        lastPos = subAreaLayout.getYpos() + subAreaLayout.getHeight();
                        lastLayout = subAreaLayout;
                    }
                    // Reset last subArea's height to fit
                    if (lastLayout != null) {
                        lastLayout.setHeight((float)
                                (areaLayout.getHeight() - lastLayout.getYpos() - context.areaHeader));
                    }
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
    private void setAreaPositionByMessages(final SequenceArea area) {
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
    private void setAreaPositionByLifelinesAndMessage(final LayoutContext context,
            final SequenceArea area) {
        
        // Set xPos and width according to the involved lifelines
        double minXPos = Double.MAX_VALUE;
        double maxXPos = 0;
        
        for (Object lifelineObj : area.getLifelines()) {
            SLifeline lifeline = (SLifeline) lifelineObj;
            KNode node = (KNode) lifeline.getProperty(InternalProperties.ORIGIN);
            KShapeLayout layout = node.getData(KShapeLayout.class);
            double lifelineCenter = layout.getXpos() + layout.getWidth() / 2;
            
            minXPos = Math.min(minXPos, lifelineCenter);
            maxXPos = Math.max(maxXPos, lifelineCenter);
        }
        
        area.getPosition().x = minXPos;
        area.getSize().x = maxXPos - minXPos;

        // Set yPos and height according to the next message's yPos
        if (area.getNextMessage() != null) {
            Object messageObj = area.getNextMessage();
            SMessage message = (SMessage) messageObj;
            KEdge edge = (KEdge) message.getProperty(InternalProperties.ORIGIN);
            KEdgeLayout layout = edge.getData(KEdgeLayout.class);
            
            double messageYPos;
            if (layout.getSourcePoint().getY() < layout.getTargetPoint().getY()) {
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
     *            the {@link SequenceArea}
     * @return the maximum depth of hierarchy
     */
    private int checkHierarchy(final SequenceArea area) {
        if (area.getContainedAreas().size() > 0) {
            int maxLevel = 0;
            for (SequenceArea subArea : area.getContainedAreas()) {
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

}
