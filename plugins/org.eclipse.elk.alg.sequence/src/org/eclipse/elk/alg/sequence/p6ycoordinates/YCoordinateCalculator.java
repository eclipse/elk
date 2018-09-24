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
package org.eclipse.elk.alg.sequence.p6ycoordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.elk.alg.common.nodespacing.NodeLabelAndSizeCalculator;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.EdgeLabelSideSelection;
import org.eclipse.elk.alg.sequence.SequenceLayoutConstants;
import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.SequenceUtils;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SArea;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SDestruction;
import org.eclipse.elk.alg.sequence.graph.SExecution;
import org.eclipse.elk.alg.sequence.graph.SGraphAdapters;
import org.eclipse.elk.alg.sequence.graph.SLabel;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.options.LabelAlignmentStrategy;
import org.eclipse.elk.alg.sequence.options.LabelSideSelection;
import org.eclipse.elk.alg.sequence.options.MessageCommentAlignment;
import org.eclipse.elk.alg.sequence.options.MessageType;
import org.eclipse.elk.alg.sequence.options.NodeType;
import org.eclipse.elk.alg.sequence.options.SequenceDiagramOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.math.ElkMath;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;

import com.google.common.collect.Lists;

/**
 * Calculates y coordinates for all elements of the diagram. This will also determine the height of lifelines,
 * fragments, executions, and the graph itself.
 * 
 * TODO Executions need a minimum height
 */
public class YCoordinateCalculator implements ILayoutPhase<SequencePhases, LayoutContext> {
    
    /** The lowest Y coordinate at which we can still find an element that was already placed. */
    private double lowestY = 0;
    

    @Override
    public LayoutProcessorConfiguration<SequencePhases, LayoutContext> getLayoutProcessorConfiguration(
            final LayoutContext graph) {
        
        return LayoutProcessorConfiguration.create();
    }

    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Calculate y coordinates", 1);
        
        // Set all lifelines to begin at the top padding to start with (lifelines created through create messages will
        // be moved down later)
        initLifelinePositions(context);
        
        // Place everything; this is the meat of the algorithm
        lowestY += context.messageSpacing * 0.5;
        theMeatOfTheAlgorithm(context);
        
        // Now that pretty much everything is placed, compute y coordinates for the executions as well
        placeExecutions(context);
        
        // Set the height of lifelines and the graph's height
        lowestY += context.messageSpacing * 0.5;
        
        computeLifelineHeight(context);
        context.sgraph.getSize().y = lowestY + context.sgraph.getPadding().bottom;
        
        // Finally, compute coordinates for destruction events
        placeDestructions(context);

        progressMonitor.done();
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Lifeline Placement
    
    /**
     * Places all lifelines at the top, even ones created through create messages. They will be moved downwards once
     * their create message is placed.
     */
    private void initLifelinePositions(final LayoutContext context) {
        final double topPadding = context.sgraph.getPadding().top;
        
        context.sgraph.getLifelines().stream()
            .forEach(ll -> ll.getPosition().y = topPadding);
        
        lowestY = topPadding + context.lifelineHeaderHeight;
    }
    
    /**
     * Computes the height of all lifelines. This expects {@link #lowestY} to be set to the point lifelines should
     * extend to unless destroyed previously (in which case their height has already been computed)
     */
    private void computeLifelineHeight(final LayoutContext context) {
        for (SLifeline sLifeline : context.sgraph.getLifelines()) {
            if (sLifeline.getDestruction() != null) {
                double lowestIncidentThingY = lowestIncidentThingY(context, sLifeline);
                sLifeline.getSize().y = lowestIncidentThingY + context.messageSpacing - sLifeline.getPosition().y;
                
            } else {
                sLifeline.getSize().y = lowestY - sLifeline.getPosition().y;
            }
        }
    }
    
    /**
     * Computes the position of the lowermost thing incident to the given lifeline.
     */
    private double lowestIncidentThingY(final LayoutContext context, final SLifeline sLifeline) {
        double lowestY = sLifeline.getPosition().y + context.lifelineHeaderHeight;
        
        for (SMessage inMsg : sLifeline.getIncomingMessages()) {
            lowestY = Math.max(lowestY, inMsg.getTargetPosition().y);
        }
        
        for (SMessage outMsg : sLifeline.getOutgoingMessages()) {
            lowestY = Math.max(lowestY, outMsg.getSourcePosition().y);
        }
        
        return lowestY;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Layer Placement
    
    /**
     * Computes y coordinates for all of the diagram's elements.
     */
    private void theMeatOfTheAlgorithm(final LayoutContext context) {
        for (Layer layer : context.lgraph.getLayers()) {
            // We partition the nodes into sub layers that can be placed completely independent of each other, which we
            // then process
            partitionIntoSubLayers(layer)
                .stream()
                .forEach(subLayer -> placeSubLayer(context, subLayer));
        }
    }
    
    /**
     * Partitions the given layer into sublayers. All nodes end up in the same sublayer, except for one case. If a node
     * represents a message that spans layers a through b, that is incompatible with a node that represents a message
     * that spans a non-empty subset of those layers.
     * 
     * <p>
     * The algorithm here is currently greedy: instead of trying to get away with as few sub-layers as possible, we
     * iterate over all sublayers and insert it into the first it's compatible with.
     * </p>
     */
    private List<List<LNode>> partitionIntoSubLayers(final Layer layer) {
        List<List<LNode>> subLayers = new ArrayList<>();
        
        for (LNode lNode : layer) {
            SMessage sMessage = SequenceUtils.originObjectFor(lNode, SMessage.class);
            
            if (sMessage == null) {
                // The node doesn't represent a message, so it can be put into the first sub layer
                firstSubLayer(subLayers).add(lNode);
            } else {
                // The node does represent a message; find the first sub layer it's compatible with
                firstCompatibleSubLayer(subLayers, lNode).add(lNode);
            }
        }
        
        return subLayers;
    }

    /**
     * Returns the first sub layer in the list. Creates one if it doesn't exist yet.
     */
    private List<LNode> firstSubLayer(final List<List<LNode>> existingSubLayers) {
        if (existingSubLayers.isEmpty()) {
            return newSubLayer(existingSubLayers);
        } else {
            return existingSubLayers.get(0);
        }
    }
    
    /**
     * Creates, adds and returns a new sub layer.
     */
    private List<LNode> newSubLayer(final List<List<LNode>> existingSubLayers) {
        List<LNode> newSubLayer = new ArrayList<>();
        existingSubLayers.add(newSubLayer);
        return newSubLayer;
    }
    
    /**
     * Finds and returns the first sub layer in the list of sub layers the given message node is compatible with. If no
     * such layer exists, it is created and added to the list of sub layers.
     */
    private List<LNode> firstCompatibleSubLayer(final List<List<LNode>> subLayers, final LNode lNode) {
        SMessage sMessage = SequenceUtils.originObjectFor(lNode, SMessage.class);
        assert sMessage != null;
        
        for (List<LNode> subLayer : subLayers) {
            // Check if there is any message our message is incompatible with
            boolean isCompatible = subLayer.stream()
                .map(otherLNode -> SequenceUtils.originObjectFor(otherLNode, SMessage.class))
                .filter(otherSMessage -> otherSMessage != null)
                .noneMatch(otherSMessage -> otherSMessage.overlaps(sMessage));
            
            if (isCompatible) {
                return subLayer;
            }
        }
        
        // We haven't found a compatible sub layer
        return newSubLayer(subLayers);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Sub Layer Placement
    
    /**
     * Places the nodes of a sub layer, assuming that no nodes are in conflict. Makes things easier.
     */
    private void placeSubLayer(final LayoutContext context, final List<LNode> subLayer) {
        // TODO Place areas
        
        // Find the height of things that should be placed above the set of messages
        double heightAboveMessages = heightAboveMessages(context, subLayer);
        
        lowestY = placeMessages(context, subLayer, lowestY + heightAboveMessages);
        lowestY += context.messageSpacing;
    }
    
    private double placeMessages(final LayoutContext context, final List<LNode> subLayer, final double messageY) {
        double bottomMostY = messageY;
        
        for (LNode lNode : subLayer) {
            SMessage sMessage = SequenceUtils.originObjectFor(lNode, SMessage.class);
            if (sMessage == null) {
                // Only interested in message nodes, thank you very much
                continue;
            }
            
            // Place the message itself
            // TODO What about split messages?
            if (sMessage.isSelfMessage()) {
                assert sMessage.getBendPoints().size() == 2;
                sMessage.getSourcePosition().y = messageY - 0.3 * context.messageSpacing;
                sMessage.getTargetPosition().y = messageY + 0.3 * context.messageSpacing;
                sMessage.getBendPoints().get(0).y = sMessage.getSourcePosition().y;
                sMessage.getBendPoints().get(1).y = sMessage.getTargetPosition().y;
                
            } else {
                sMessage.getSourcePosition().y = messageY;
                sMessage.getTargetPosition().y = messageY;
            }
            
            // Place related elements
            placeMessageLabel(context, sMessage);
            placeMessageComments(context, sMessage, 0);
            
            if (sMessage.getLabel() != null) {
                SLabel sLabel = sMessage.getLabel();
                bottomMostY = Math.max(bottomMostY, sLabel.getPosition().y + sLabel.getSize().y);
            }
            
            // If this is a create message, the target lifeline must be moved accordingly
            if (sMessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) == MessageType.CREATE) {
                SLifeline sTargetLifeline = sMessage.getTargetLifeline();
                sTargetLifeline.getPosition().y = messageY - context.lifelineHeaderHeight / 2;
            }
        }
        
        return bottomMostY;
    }
    
    /**
     * Places the label of this message, if any, and returns the lowermost coordinate that can be occupied by comments.
     */
    private double placeMessageLabel(final LayoutContext context, final SMessage sMessage) {
        // Retrieve the message's label, if any
        SLabel sLabel = sMessage.getLabel();
        if (sLabel == null) {
            return sMessage.getSourcePosition().y - context.labelSpacing;
        }
        
        if (sMessage.isSelfMessage()) {
            // Label must be centered vertically
            sLabel.getPosition().y =
                    (sMessage.getSourcePosition().y + sMessage.getTargetPosition().y - sLabel.getSize().y) / 2;
            
        } else {
            if (sLabel.getProperty(SequenceDiagramOptions.EDGE_LABELS_INLINE)) {
                // Label must be centered vertically
                sLabel.getPosition().y = sMessage.getSourcePosition().y - sLabel.getSize().y / 2;
                
            } else {
                switch (context.sgraph.getProperty(SequenceDiagramOptions.LABEL_SIDE)) {
                case ALWAYS_UP:
                    sLabel.getPosition().y = sMessage.getSourcePosition().y - context.labelSpacing - sLabel.getSize().y;
                    break;
                case ALWAYS_DOWN:
                    sLabel.getPosition().y = sMessage.getSourcePosition().y + context.labelSpacing;
                    break;
                }
            }
        }
        
        // We can start placing labels either directly above the message or above its label
        return Math.min(
                sMessage.getSourcePosition().y - context.labelSpacing,
                sLabel.getPosition().y - context.labelSpacing);
    }
    
    /**
     * Places the message's comments, starting at the given coordinate.
     */
    private void placeMessageComments(final LayoutContext context, final SMessage sMessage, final double lowerY) {
        // We remember the coordinate the next comment can extend to for source and target comments separately
        double sourceCommentY = lowerY;
        double targetCommentY = lowerY;
        
        // We iterate over the reversed list of comments since we place not top-to-bottom, but bottom-to-top
        for (SComment sComment : Lists.reverse(sMessage.getComments())) {
            if (sComment.getAlignment() == MessageCommentAlignment.SOURCE) {
                sComment.getPosition().y = sourceCommentY - sComment.getSize().y;
                sourceCommentY -= sComment.getSize().y + context.labelSpacing;
                
            } else if (sComment.getAlignment() == MessageCommentAlignment.TARGET) {
                sComment.getPosition().y = targetCommentY - sComment.getSize().y;
                targetCommentY -= sComment.getSize().y + context.labelSpacing;
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Execution Placement
    
    private void placeExecutions(final LayoutContext context) {
        for (SLifeline sLifeline : context.sgraph.getLifelines()) {
            for (SExecution sExec : sLifeline.getExcecutions()) {
                placeExecution(context, sLifeline, sExec);
            }
        }
    }
    
    /**
     * Determines coordinates for the given execution which belongs to the given lifeline.
     */
    private void placeExecution(final LayoutContext context, final SLifeline sLifeline, final SExecution exec) {
        double topY = Double.MAX_VALUE;
        double bottomY = Double.MIN_VALUE;
        
        for (SMessage sMsg : exec.getMessages()) {
            if (sMsg.isSelfMessage()) {
                // If self messages create an execution, the execution starts at the end of the message. If they
                // end an execution, it ends at the start of the message
                topY = Math.min(topY, sMsg.getTargetPosition().y);
                bottomY = Math.max(bottomY, sMsg.getSourcePosition().y);
                
            } else if (sMsg.getSourceLifeline() == sLifeline) {
                double sourceY = sMsg.getSourcePosition().y;
                topY = Math.min(topY, sourceY);
                bottomY = Math.max(bottomY, sourceY);
                
            } else if (sMsg.getTargetLifeline() == sLifeline) {
                double targetY = sMsg.getTargetPosition().y;
                topY = Math.min(topY, targetY);
                bottomY = Math.max(bottomY, targetY);
            }
        }
        
        // Apply coordinates and size
        exec.getPosition().y = topY;
        exec.getSize().y = Math.max(context.minExecutionHeight, bottomY - topY);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Destruction
    
    private void placeDestructions(final LayoutContext context) {
        for (SLifeline sLifeline : context.sgraph.getLifelines()) {
            SDestruction sDestruction = sLifeline.getDestruction();
            if (sDestruction != null) {
                sDestruction.getPosition().y = sLifeline.getPosition().y + sLifeline.getSize().y
                        - (sDestruction.getSize().y / 2);
            }
        }
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utilities
    
    /**
     * Computs how much space we need above messages to place comments and labels.
     */
    private double heightAboveMessages(final LayoutContext context, final List<LNode> subLayer) {
        // We may need to place comments
        double commentHeight = withSpacing(maxCommentHeight(context, subLayer), context.labelSpacing);
        
        // If labels are supposed to be placed above their messages or inline, we need to take them into account
        double labelHeight = maxMessageLabelHeightAbove(context, subLayer);
        
        return commentHeight + labelHeight;
    }
    
    /**
     * Finds the maximum height of comments that need to be stacked.
     */
    private double maxCommentHeight(final LayoutContext context, final List<LNode> subLayer) {
        double maxCommentHeight = 0;
        
        for (LNode lNode : subLayer) {
            // TODO Consider other elements that can have comments as well
            SMessage sMessage = SequenceUtils.originObjectFor(lNode, SMessage.class);
            
            if (sMessage != null) {
                // We're stacking comments at the message's source and its target, so we need to compute the height of
                // those two stacks separately
                double sourceCommentHeight = 0;
                double targetCommentHeight = 0;
                
                for (SComment sComment : sMessage.getComments()) {
                    if (sComment.getAlignment() == MessageCommentAlignment.SOURCE) {
                        sourceCommentHeight = addCommentHeight(context, sourceCommentHeight, sComment.getSize().y);
                    } else if (sComment.getAlignment() == MessageCommentAlignment.TARGET) {
                        targetCommentHeight = addCommentHeight(context, targetCommentHeight, sComment.getSize().y);
                    }
                }
                
                maxCommentHeight = ElkMath.maxd(maxCommentHeight, sourceCommentHeight, targetCommentHeight);
            }
        }
        
        return maxCommentHeight;
    }
    
    /**
     * Returns the new comment stack height that will result from placing a new comment with the given height in the
     * stack.
     */
    private double addCommentHeight(final LayoutContext context, final double commentStackHeight,
            final double newCommentHeight) {
        
        // If there already are comments in the stack, we need to include spacing
        return commentStackHeight > 0
                ? commentStackHeight + context.labelSpacing + newCommentHeight
                : newCommentHeight;
    }
    
    /**
     * Calculates the amount of space above a message taken up by its labels. A label takes up space above a message if
     * either label side selection is set to {@link EdgeLabelSideSelection#ALWAYS_UP} or if a label is an inside label.
     * The height includes the spacing to the edge.
     */
    private double maxMessageLabelHeightAbove(final LayoutContext context, final List<LNode> subLayer) {
        boolean above =
                context.sgraph.getProperty(SequenceDiagramOptions.LABEL_SIDE) == LabelSideSelection.ALWAYS_UP;
        
        return subLayer.stream()
                .map(lNode -> SequenceUtils.originObjectFor(lNode, SMessage.class))
                .filter(sMessage -> sMessage != null && sMessage.getLabel() != null)
                .map(sMessage -> sMessage.getLabel())
                .mapToDouble(sLabel -> {
                    double height = sLabel.getSize().y;
                    if (sLabel.getProperty(SequenceDiagramOptions.EDGE_LABELS_INLINE)) {
                        return height / 2;
                    } else if (above) {
                        return height + context.labelSpacing;
                    } else {
                        return 0;
                    }
                })
                .max()
                .orElse(0);
    }
    
    /**
     * Returns {@code x} if it is equal to zero, or {@code x + spacing} if it isn't.
     */
    private double withSpacing(final double x, final double spacing) {
        return x == 0 ? x : x + spacing;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    

    public void oldProcess(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
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
        
        // Set position for lifelines/nodes. To do that, we iterate over the lifelines and always keep two: the
        // "current" and the "next". The current's position is set and spacings are calculated with regard to the next
        Iterator<SLifeline> lifelineIterator = context.sgraph.getLifelines().stream()
                .filter(ll -> !ll.isDummy()).iterator();
        
        if (lifelineIterator.hasNext()) {
            SLifeline currLifeline = null;
            SLifeline nextLifeline = lifelineIterator.next();
            
            while (nextLifeline != null) {
                currLifeline = nextLifeline;
                nextLifeline = lifelineIterator.hasNext() ? lifelineIterator.next() : null;
                
                // Calculate the spacing between this lifeline and its successor. Place comments.
                // TODO This code currently does not consider the space required for found message of the successor
                double thisLifelineSpacing = calculateLifelineSpacingAndPlaceComments(
                        context, xPos, currLifeline, nextLifeline);
                
                // Set position and height for the lifeline. This may be overridden if there are create or delete
                // messages involved.
                currLifeline.getPosition().y = 0;
                currLifeline.getPosition().x = xPos;
                currLifeline.getSize().y = sgraphSize.y;
                
                // Advance x pointer and apply to graph size
                xPos += currLifeline.getSize().x + thisLifelineSpacing;
                sgraphSize.x = xPos;
                
            }
            
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
                // Get the corresponding message and skip dummy nodes
                SMessage message = SequenceUtils.originObjectFor(node, SMessage.class);
                
                if (message == null) {
                    continue;
                }
                
                // If the message is represented by a single node, that node won't have a property to say that it
                // belongs to a given lifeline. If it is represented by two nodes, however, those nodes will specify
                // which lifeline each belongs to.
                SLifeline lifeline = node.getProperty(InternalSequenceProperties.BELONGS_TO_LIFELINE);
                if (lifeline != null) {
                    // It is in fact represented by two nodes. Thus, set the position of the message's source or
                    // target, depending on what the current node represents
                    if (message.getTargetLifeline() == lifeline) {
                        message.setTargetYPos(layerPos);
                    } else {
                        message.setSourceYPos(layerPos);
                    }
                    continue;
                }
                
                // Starting here we know that the message is represented by a single node. If the message already had
                // its position set before we encountered its node in our for loop, simply continue. (this case can
                // happen if we set a node's position in the loop below)
                if (false /*message.isMessageLayerPositionSet()*/) {
                    continue;
                }

                // Check which lifeline numbers the message spans
                int sourceSlot = message.getSourceLifeline().getHorizontalSlot();
                int targetSlot = message.getTargetLifeline().getHorizontalSlot();

                if (Math.abs(sourceSlot - targetSlot) > 1) {
                    // There are lifelines between the message's source and target. It might overlap other messages,
                    // so we need to assign non-conflicting positions to those messages
                    // TODO: We should compute a conflict graph for the messages in each layer and try to get away
                    //       with as few communication lines as possible.
                    for (LNode otherNode : layer) {
                        SMessage otherMessage = (SMessage) otherNode.getProperty(InternalSequenceProperties.ORIGIN);
                        
                        int otherSourceSlot = otherMessage.getSourceLifeline().getHorizontalSlot();
                        int otherTargetSlot = otherMessage.getTargetLifeline().getHorizontalSlot();

                        // If the other message starts or ends between the start and the end
                        // of the tested message, there is an overlapping
                        if (overlap(sourceSlot, targetSlot, otherSourceSlot, otherTargetSlot)) {
                            if (true /*otherMessage.isMessageLayerPositionSet()*/) {
                                // If the other message was already placed, we need to advance the current y pointer
                                layerPos += context.messageSpacing;
                                break;
                            } else if (Math.abs(otherSourceSlot - otherTargetSlot) <= 1) {
                                // If the other message has not been placed yet and is a short one, it will be placed
                                // here (this is why we check whether the message's coordinates have already been set
                                // previously)
//                                otherMessage.setMessageLayerYPos(layerPos);
                                layerPos += context.messageSpacing;
                                break;
                            }
                        }
                    }
                }
                
                // Set the vertical position of the message
//                message.setMessageLayerYPos(layerPos);

                // Self-loops are sort of centered around the vertical position of their layer such that their source
                // and target are half a message spacing apart
                if (message.getSourceLifeline() == message.getTargetLifeline()) {
                    double offset = context.messageSpacing / 4;
                    
                    message.setSourceYPos(layerPos - offset);
                    message.setTargetYPos(layerPos + offset);
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
        for (SComment scomment : context.sgraph.getComments()) {
            SLifeline refLl = scomment.getLifeline();
            SMessage refMsg = scomment.getReferenceMessage();
            
            // If the comment is attached to a message, determine if it should be drawn near the beginning or near the
            // end of the message. If the comment is attached to a message and one of the message's lifelines, it
            // should be drawn near that lifeline (this is the case for time observations for example).
            if (refMsg != null) {
                SLifeline right, left;
                
                if (refMsg.getSourceLifeline().getHorizontalSlot() < refMsg.getTargetLifeline().getHorizontalSlot()) {
                    // Message leads rightwards
                    left = refMsg.getSourceLifeline();
                    right = refMsg.getTargetLifeline();
                } else {
                    // Message leads leftwards or is self-loop
                    left = refMsg.getTargetLifeline();
                    right = refMsg.getSourceLifeline();
                }
                
                if (refLl == right) {
                    // Find lifeline left to "right" and attach comment to that lifeline because internally we always
                    // attach comments to the lifeline they should be placed to the right of
                    int position = right.getHorizontalSlot();
                    for (SLifeline ll : context.sgraph.getLifelines()) {
                        if (ll.getHorizontalSlot() == position - 1) {
                            scomment.setLifeline(ll);
                            break;
                        }
                    }
                    
                    scomment.setAlignment(MessageCommentAlignment.RIGHT);
                    
                } else {
                    scomment.setLifeline(left);
                    scomment.setAlignment(MessageCommentAlignment.LEFT);
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
        double commentXPos = context.sgraph.getSize().x + context.lifelineSpacing;
        double commentYPos = 0;

        for (SComment scomment : context.sgraph.getComments()) {
            if (scomment.getReferenceMessage() == null && scomment.getLifeline() == null) {
                // By default, we strip down unconnected comments to 80 pixels
                manageCommentLabelsAndSize(scomment, 80, context);
                
                scomment.getPosition().x = commentXPos;
                scomment.getPosition().y = commentYPos;
                
                commentYPos += scomment.getSize().y + context.messageSpacing;
                commentMaxExtraWidth = Math.max(commentMaxExtraWidth, scomment.getSize().x);
            }
        }

        if (commentMaxExtraWidth > 0) {
            context.sgraph.getSize().x += commentMaxExtraWidth + context.lifelineSpacing;
        }
    }
    
    /**
     * Invokes label management on the given comment and has the label and node size calculator calculate its size.
     * 
     * @param scomment
     *            the comment.
     * @param targetWidth
     *            the maximum width a comment should have.
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     */
    private void manageCommentLabelsAndSize(final SComment scomment, final double targetWidth,
            final LayoutContext context) {
        
        // Label management
        if (context.labelManager != null && scomment.getLabel() != null) {
            SLabel slabel = scomment.getLabel();
            KVector newSize = context.labelManager.manageLabelSize(
                    slabel.getProperty(InternalSequenceProperties.ORIGIN),
                    targetWidth);
            if (newSize != null) {
                slabel.getSize().set(newSize);
            }
        }
        
        // Size calculation
        Set<SizeConstraint> sizeConstraints = scomment.getProperty(SequenceDiagramOptions.NODE_SIZE_CONSTRAINTS);
        if (!sizeConstraints.isEmpty()) {
            NodeLabelAndSizeCalculator.process(context.sgraphAdapter,
                    SGraphAdapters.adaptComment(scomment, context.sgraphAdapter), true, false);
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
            SLifeline firstLifeline = firstLifelineOptional.get();
            
            // Calculate the length of the longest found message
            double longestFoundMessage = 0;
            for (SMessage smessage : firstLifeline.getIncomingMessages()) {
                if (smessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) != MessageType.FOUND) {
                    // We're only interested in found messages
                    continue;
                }
                
                // Shorten the label such that it fits within the lifeline's space
                manageMessageLabel(smessage, firstLifeline.getSize().x / 2, context);
                longestFoundMessage = Math.max(
                        longestFoundMessage,
                        SequenceUtils.calculateLostFoundMessageLength(smessage, firstLifeline, context));
            }
            
            return Math.max(0, longestFoundMessage - firstLifeline.getSize().x / 2);
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
     * @param currLifeline
     *            the current lifeline
     * @param nextLifeline
     *            the next lifeline, used to check how much space of that we can use.
     * @return the width of the lifeline plus comments.
     */
    private double calculateLifelineSpacingAndPlaceComments(final LayoutContext context, final double xPos,
            final SLifeline currLifeline, final SLifeline nextLifeline) {
        
        assert !currLifeline.isDummy();
        assert nextLifeline == null || !nextLifeline.isDummy();
        
        final double spaceBetweenLifelines = currLifeline.getSize().x / 2
                + (nextLifeline != null ? nextLifeline.getSize().x / 2 : 0)
                + context.lifelineSpacing;
        double requiredSpace = 0;
        
        final double messageLabelTargetWidth = spaceBetweenLifelines - 2 * context.labelSpacing;
        final boolean sourceLabels = context.labelAlignment == LabelAlignmentStrategy.SOURCE;

        // Check if our successor has outgoing messages with labels
        if (nextLifeline != null) {
            for (SMessage smessage : nextLifeline.getOutgoingMessages()) {
                // We are only interested in certain kinds of messages
                if (smessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) == MessageType.FOUND) {
                    manageMessageLabel(smessage, messageLabelTargetWidth, context);
                    double messageLength = SequenceUtils.calculateLostFoundMessageLength(
                            smessage, nextLifeline, context);
                    requiredSpace = Math.max(requiredSpace, messageLength);
                    
                } else if (sourceLabels && smessage.getTargetLifeline().getHorizontalSlot() < nextLifeline.getHorizontalSlot()) {
                    // The message runs leftwards and we use source label placement, which means that we need to reserve
                    // enough space
                    manageMessageLabel(smessage, messageLabelTargetWidth, context);
                    requiredSpace = Math.max(requiredSpace, SequenceUtils.calculateMessageLength(smessage, context));
                }
            }
        }
        
        for (SMessage smessage : currLifeline.getOutgoingMessages()) {
            // We are only interested certain kinds of messages
            if (smessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) == MessageType.LOST) {
                manageMessageLabel(smessage, messageLabelTargetWidth, context);
                double messageLength = SequenceUtils.calculateLostFoundMessageLength(
                        smessage, currLifeline, context);
                requiredSpace = Math.max(requiredSpace, messageLength);
                
            } else if (smessage.getSourceLifeline() == smessage.getTargetLifeline()) {
                // Self message
                manageMessageLabel(smessage, messageLabelTargetWidth - context.messageSpacing / 2, context);
                double messageWidth = smessage.getLabel().getSize().x + context.labelSpacing
                        + context.messageSpacing / 2;
                requiredSpace = Math.max(requiredSpace, messageWidth);
                
            } else if (smessage.getTargetLifeline() == nextLifeline) {
                // The message connects directly to the successor lifeline
                manageMessageLabel(smessage, messageLabelTargetWidth, context);
                double messageLength = SequenceUtils.calculateMessageLength(smessage, context);
                
                if (smessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE) == MessageType.CREATE) {
                    // Labels of create messages should not overlap the target's header
                    requiredSpace = Math.max(requiredSpace, messageLength + nextLifeline.getSize().x / 2);
                } else {
                    // A regular message
                    requiredSpace = Math.max(requiredSpace, messageLength);
                }
                
            } else if (sourceLabels) {
                // The message connects to some other lifeline, but its label will be placed here
                manageMessageLabel(smessage, messageLabelTargetWidth, context);
                requiredSpace = Math.max(requiredSpace, SequenceUtils.calculateMessageLength(smessage, context));
            }
        }

        // If there are no comments attached to this lifeline, we're done here
        Set<SComment> comments = currLifeline.getComments();
        if (comments.isEmpty()) {
            return Math.max(0, requiredSpace - spaceBetweenLifelines) + context.lifelineSpacing;
        }

        // Check maximum size of comments attached to the lifeline
        double maxCommentWidth = 0;
        for (SComment comment : comments) {
            manageCommentLabelsAndSize(comment, Math.max(requiredSpace, spaceBetweenLifelines), context);
            maxCommentWidth = Math.max(maxCommentWidth, comment.getSize().x + 2 * context.labelSpacing);
        }
        requiredSpace = Math.max(requiredSpace, maxCommentWidth);
        
        // By now, the required space might be smaller than the actual space that we will have available between the
        // two lifelines
        double actualSpace = requiredSpace;
        if (nextLifeline != null) {
            actualSpace = Math.max(requiredSpace,
                    (currLifeline.getSize().x + nextLifeline.getSize().x) / 2 + context.lifelineSpacing);
        }

        // HashMap that organizes which comment belongs to which message. This is important if there are more than
        // one comments at a message.
        HashMap<SMessage, SComment> messageCommentMap = new HashMap<SMessage, SComment>(comments.size());
        for (SComment comment : comments) {
            if (comment.getLifeline() == currLifeline) {
                SMessage message = comment.getReferenceMessage();

                // Place comment above the message
                KVector commentPos = comment.getPosition();
                commentPos.y = message.getSourceYPos() - (comment.getSize().y + context.messageSpacing);
                
                // x position depends on the comment's alignment
                switch (comment.getAlignment()) {
                case LEFT:
                    commentPos.x = xPos + currLifeline.getSize().x / 2 + context.labelSpacing;
                    break;
                    
                case CENTER:
                    commentPos.x = xPos + currLifeline.getSize().x / 2 + context.labelSpacing
                            + (actualSpace - comment.getSize().x - 2 * context.labelSpacing) / 2;
                    break;
                    
                case RIGHT:
                    // The required space might be smaller than the actual space
                    commentPos.x = xPos + currLifeline.getSize().x / 2
                            + actualSpace - context.labelSpacing - comment.getSize().x;
                    break;
                }
                
                // If another comment for the message has already been placed, handled conflicts by shifting the
                // comment vertically
                if (messageCommentMap.containsKey(message)) {
                    SComment upper = comment;
                    SComment lower = messageCommentMap.get(message);
                    NodeType nodeType = comment.getProperty(SequenceDiagramOptions.TYPE_NODE);
                    
                    // If comment is Observation, place it nearer to the message
                    if (nodeType == NodeType.DURATION_OBSERVATION || nodeType == NodeType.TIME_OBSERVATION) {
                        upper = lower;
                        lower = comment;
                    }

                    // Place lower comment first
                    commentPos.y = message.getSourceYPos() - (lower.getSize().y + context.messageSpacing / 2);
                    lower.getPosition().y = commentPos.y;

                    // Place upper comment near to lower one
                    double uYpos = lower.getPosition().y - upper.getSize().y - context.labelSpacing;
                    upper.getPosition().y = uYpos;
                    
                } else {
                    messageCommentMap.put(message, comment);
                }
            }
        }
        
        return Math.max(0, requiredSpace - spaceBetweenLifelines) + context.lifelineSpacing;
    }
    
    /**
     * Applies label management to the label of the given message, if installed.
     */
    private void manageMessageLabel(final SMessage smessage, final double targetWidth, final LayoutContext context) {
        if (context.labelManager != null && smessage.getLabel() != null) {
            SLabel slabel = smessage.getLabel();
            KVector newSize = context.labelManager.manageLabelSize(
                    slabel.getProperty(InternalSequenceProperties.ORIGIN),
                    targetWidth);
            if (newSize != null) {
                slabel.getSize().set(newSize);
            }
        }
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
        // We might be updating the SGraph's size if we ifind an area which contains a CREATE message
        KVector sgraphSize = context.sgraph.getSize();
        
        // The minimum x coordinate assigned to an area (we will only be interested in negative coordinates later,
        // so it suffices to intialize it with zero here)
        double minXPos = 0;
        
        // Set size and position of each area to cover just its content
        for (SArea area : context.sgraph.getAreas()) {
            if (area.getMessages().size() > 0) {
                setAreaPositionAndSizeByMessages(area, context);
            } else {
                setAreaPositionAndSizeByLifelinesAndMessage(context, area);
            }
        }
        
        // The rest of this code tries to fix overlaps and things
        for (SArea area : context.sgraph.getAreas()) {
            KVector areaPos = area.getPosition();
            KVector areaSize = area.getSize();
            ElkPadding areaPadding = paddingFor(area, context);
            
            // Check if there are contained areas
            HierarchyDepth hierarchyDepth = calculateHierarchyDepth(area);

            areaPos.x = area.getPosition().x
                    - hierarchyDepth.left * areaPadding.left;
            areaSize.x = area.getSize().x
                    + hierarchyDepth.left * areaPadding.left
                    + hierarchyDepth.right * areaPadding.right;

            areaPos.y = area.getPosition().y
                    - hierarchyDepth.top * areaPadding.top;
            areaSize.y = area.getSize().y
                    + hierarchyDepth.top * areaPadding.top
                    + hierarchyDepth.bottom * areaPadding.bottom;
            
            // Make sure the SGraph is large enough to contain the area
            sgraphSize.x = Math.max(sgraphSize.x, areaPos.x + areaSize.x);
            
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
                        setAreaPositionAndSizeByMessages(subArea, context);
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
                    lastSubArea.getSize().y = areaSize.y - areaPos.y - areaPadding.top;
                }
            }
            
            minXPos = Math.min(minXPos, areaPos.x);
        }
        
        // If the minimum x position is negative, we need to set the graph's offset and enlarge the graph accordingly
        if (minXPos < 0) {
            KVector graphOffset = context.sgraph.getOffset();
            graphOffset.x = Math.max(graphOffset.x, -minXPos);
            
            context.sgraph.getSize().x += -minXPos;
        }
    }


    /**
     * Searches all the contained edges and sets the area's position and size such that it is a
     * bounding box for the contained messages.
     * 
     * @param area
     *            the SequenceArea
     */
    private void setAreaPositionAndSizeByMessages(final SArea area, final LayoutContext context) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = 0;
        double maxY = 0;
        
        // Compute the bounding box of all contained messages
        for (SMessage smessage : area.getMessages()) {
            // Extract common message properties
            MessageType smessageType = smessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE);
            SLifeline sourceLL = smessage.getSourceLifeline();
            SLifeline targetLL = smessage.getTargetLifeline();
            
            // Compute new y coordinates
            double sourceYPos = smessage.getSourceYPos();
            minY = Math.min(minY, sourceYPos);
            maxY = Math.max(maxY, sourceYPos);
            
            double targetYPos = smessage.getTargetYPos();
            
            if (smessageType == MessageType.CREATE) {
                // If a create message is the only message in an area, this can lead the area to run through the
                // created lifeline's header
                targetYPos += context.lifelineHeaderHeight / 2;
            }
            
            minY = Math.min(minY, targetYPos);
            maxY = Math.max(maxY, targetYPos);
            
            // Compute new x coordinates
            double sourceXPos = sourceLL.getPosition().x + sourceLL.getSize().x / 2;
            
            if (smessageType == MessageType.FOUND) {
                // Found message don't have their source position properly set
                sourceXPos = targetLL.getPosition().x - context.lifelineSpacing / 2;
            }
            
            minX = Math.min(minX, sourceXPos);
            maxX = Math.max(maxX, sourceXPos);
            
            double targetXPos = targetLL.getPosition().x + targetLL.getSize().x / 2;
            
            if (smessage.isSelfMessage()) {
                // The message is a self message, we need to take that into account
                targetXPos += context.messageSpacing / 2;
                
                if (smessage.getLabel() != null) {
                    targetXPos += context.labelSpacing / 2 + smessage.getLabel().getSize().x;
                }
            
            } else if (smessageType == MessageType.LOST) {
                // Lost message don't have their target position properly set
                targetXPos = sourceLL.getPosition().x + sourceLL.getSize().x + context.lifelineSpacing / 2;
            
            } else if (smessageType == MessageType.CREATE) {
                // The area must be extended to contain the created lifeline's header
                targetXPos = targetLL.getPosition().x + targetLL.getSize().x;
            }
            
            minX = Math.min(minX, targetXPos);
            maxX = Math.max(maxX, targetXPos);
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
    private void setAreaPositionAndSizeByLifelinesAndMessage(final LayoutContext context, final SArea area) {
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
     * Retrieves the padding that applies to the given area.
     * 
     * @param sarea
     *            the area whose padding to retrieve.
     * @param context
     *            the layout context that contains all relevant information for the current layout run.
     * @return the padding.
     */
    private ElkPadding paddingFor(final SArea sarea, final LayoutContext context) {
        ElkNode karea = (ElkNode) sarea.getProperty(InternalSequenceProperties.ORIGIN);
        
        if (karea.hasProperty(SequenceDiagramOptions.AREAS_PADDING)) {
            return karea.getProperty(SequenceDiagramOptions.AREAS_PADDING);
        } else {
            return context.areaPadding;
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
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Area Hierarchy
    
    /**
     * The hierarchy depth decides how far out the borders of an area have to be moved to not overlap with child areas.
     * Since child areas need not be as large as the parent are and can touch only a subset of its parent's borders,
     * a separate hierarchy level is required for each side. A hierarchy depth of 1 is normal for each side, each
     * child may add to that.
     */
    private static class HierarchyDepth {
        private int top = 1;
        private int right = 1;
        private int bottom = 1;
        private int left = 1;
    }
    
    /**
     * Check recursively if an area has contained areas and return the maximum depth. An area without contained areas
     * has hierarchy depth 1.
     * 
     * @param area
     *            the {@link SArea}
     * @return the maximum depth of hierarchy
     */
    private HierarchyDepth calculateHierarchyDepth(final SArea area) {
        HierarchyDepth depth = new HierarchyDepth();
        
        KVector areaPos = area.getPosition();
        KVector areaSize = area.getSize();
        
        for (SArea subArea : area.getContainedAreas()) {
            KVector subAreaPos = subArea.getPosition();
            KVector subAreaSize = subArea.getSize();
            
            boolean topBordersTouch = areaPos.y == subAreaPos.y;
            boolean rightBordersTouch = areaPos.x + areaSize.x == subAreaPos.x + subAreaSize.x;
            boolean bottomBordersTouch = areaPos.y + areaSize.y == subAreaPos.y + subAreaSize.y;
            boolean leftBordersTouch = areaPos.x == subAreaPos.x;
            
            if (topBordersTouch || rightBordersTouch || bottomBordersTouch || leftBordersTouch) {
                // We actually need to calculate the area's hierarchy depth
                HierarchyDepth subDepth = calculateHierarchyDepth(subArea);
                
                if (topBordersTouch) {
                    depth.top = Math.max(depth.top, 1 + subDepth.top);
                }
                
                if (rightBordersTouch) {
                    depth.right = Math.max(depth.right, 1 + subDepth.right);
                }
                
                if (bottomBordersTouch) {
                    depth.bottom = Math.max(depth.bottom, 1 + subDepth.bottom);
                }
                
                if (leftBordersTouch) {
                    depth.left = Math.max(depth.left, 1 + subDepth.left);
                }
            }
        }
        
        return depth;
    }
}
