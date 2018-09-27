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
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.EdgeLabelSideSelection;
import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.SequenceUtils;
import org.eclipse.elk.alg.sequence.SequenceUtils.AreaNestingTreeNode;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SArea;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SDestruction;
import org.eclipse.elk.alg.sequence.graph.SExecution;
import org.eclipse.elk.alg.sequence.graph.SLabel;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.options.LabelSideSelection;
import org.eclipse.elk.alg.sequence.options.MessageCommentAlignment;
import org.eclipse.elk.alg.sequence.options.MessageType;
import org.eclipse.elk.alg.sequence.options.SequenceDiagramOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.math.ElkMath;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Calculates y coordinates for all elements of the diagram. This will also determine the height of lifelines,
 * fragments, executions, and the graph itself.
 */
public class YCoordinateCalculator implements ILayoutPhase<SequencePhases, LayoutContext> {
    
    /** The lowest Y coordinate at which we can still find an element that was already placed. */
    private double lowestY = 0;
    /** The lowermost nodes of each area that we haven't encountered yet. */
    private Multimap<SArea, LNode> lowermostAreaNodes;
    

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
        
        // Initialize the set of area nodes that we haven't encountered yet. Once we have encountered the last of an
        // area's lowermost nodes at a sublayer, we can close that area
        initLowermostAreaNodes(context);
        
        // Place everything; this is the meat of the algorithm
        lowestY += context.messageSpacing * 0.5;
        theMeatOfTheAlgorithm(context);
        
        // Now that pretty much everything is placed, compute y coordinates for executions and non-specific comments
        placeExecutions(context);
        placeNonSpecificComments(context); 
        
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
        // Find out which areas need to be closed below this sub layer
        List<SArea> areasToBeClosed = computeAreasToBeClosed(subLayer);
        
        // Find the height of things that should be placed above the set of messages and place the messages, noting how
        // far down they extend
        double heightAboveMessages = heightAboveMessages(context, subLayer);
        double lowestMessageY = placeMessages(context, subLayer, lowestY + heightAboveMessages);
        
        // Place area headers
        double lowestAreaHeaderY = placeAreaHeaders(context, subLayer, lowestY);
        
        // Close areas
        double lowestAreaFooterY = placeAreaFooters(context, areasToBeClosed, lowestMessageY);
        
        lowestY = ElkMath.maxd(lowestMessageY, lowestAreaHeaderY, lowestAreaFooterY) + context.messageSpacing;
    }
    
    private double placeMessages(final LayoutContext context, final List<LNode> subLayer, final double messageY) {
        double bottomMostY = messageY;
        double topMostY = messageY;
        
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
            
            // Place label
            placeMessageLabel(context, sMessage);
            
            if (sMessage.getLabel() != null) {
                SLabel sLabel = sMessage.getLabel();
                bottomMostY = Math.max(bottomMostY, sLabel.getPosition().y + sLabel.getSize().y);
                topMostY = Math.min(topMostY, sLabel.getPosition().y);
            }
            
            // Place comments
            placeMessageComments(context, sMessage, topMostY - context.labelSpacing);
            
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
    
    /**
     * Places area headers starting at the given y coordinate. We let areas extend into the message spacing below the
     * sublayer to avoid too much whitespace.
     */
    private double placeAreaHeaders(final LayoutContext context, final List<LNode> subLayer, final double areaTopY) {
        double bottomMostY = areaTopY;
        
        for (LNode lNode : subLayer) {
            SArea sArea = SequenceUtils.originObjectFor(lNode, SArea.class);
            if (sArea == null) {
                // Only interested in area header nodes, thank you very much
                continue;
            }
            
            sArea.getPosition().y = areaTopY;
            
            ElkPadding areaPadding = SequenceUtils.getAreaPadding(sArea, context);
            bottomMostY = Math.max(bottomMostY, sArea.getPosition().y + areaPadding.top - context.messageSpacing);
        }
        
        return bottomMostY;
    }
    
    /**
     * Closes the given list of areas by setting their height accordingly. The y coordinate is the lowermost coordinate
     * occupied by a message on the sublayer the areas are closed in.
     */
    private double placeAreaFooters(final LayoutContext context, final List<SArea> areas, final double areaY) {
        double bottomMostY = areaY;
        
        List<AreaNestingTreeNode> nestingRoots = SequenceUtils.computeAreaNestings(areas);
        
        for (AreaNestingTreeNode root : nestingRoots) {
            bottomMostY = Math.max(bottomMostY, placeAreaTreeFooters(context, root, areaY));
        }
        
        return bottomMostY;
    }
    
    /**
     * Places the area tree rooted at the given tree node and returns the lowermost y coordinate used while doing so.
     */
    private double placeAreaTreeFooters(final LayoutContext context, final AreaNestingTreeNode areas,
            final double areaY) {
        
        double bottomMostY = areaY;
        
        // Place children (and their children, and their children, and...)
        for (AreaNestingTreeNode child : areas.children) {
            bottomMostY = Math.max(bottomMostY, placeAreaTreeFooters(context, child, areaY));
        }
        
        // Place this area
        ElkPadding areaPadding = SequenceUtils.getAreaPadding(areas.sArea, context);
        bottomMostY += areaPadding.bottom;
        areas.sArea.getSize().y = bottomMostY - areas.sArea.getPosition().y;
        return bottomMostY;
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Area Utilities
    
    /**
     * Initializes {@link #lowermostAreaNodes} to contain the lowermost area nodes of each area we have.
     */
    private void initLowermostAreaNodes(final LayoutContext context) {
        lowermostAreaNodes = HashMultimap.create();
        
        for (SArea sArea : context.sgraph.getAreas()) {
            if (sArea.hasProperty(InternalSequenceProperties.LOWERMOST_NODES)) {
                lowermostAreaNodes.putAll(sArea, sArea.getProperty(InternalSequenceProperties.LOWERMOST_NODES));
            }
        }
    }
    
    /**
     * Goes through the nodes in the sublayer and updates the lowermost nodes of our open areas that are contained
     * therein. If an area ceases to have open nodes, it must be closed and is thus added to the list.
     */
    private List<SArea> computeAreasToBeClosed(final List<LNode> subLayer) {
        List<SArea> result = new ArrayList<>();
        
        // We'll be modifying the map as we're iterating over it, so iterate over copies
        for (SArea sArea : lowermostAreaNodes.keySet().toArray(new SArea[0])) {
            for (LNode lNode : subLayer) {
                lowermostAreaNodes.remove(sArea, lNode);
            }
            
            if (!lowermostAreaNodes.containsKey(sArea)) {
                result.add(sArea);
            }
        }
        
        return result;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Execution Placement
    
    /**
     * Calls {@link #placeExecution(LayoutContext, SLifeline, SExecution)} on all executions.
     */
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
    // Comment Placement
    
    /**
     * Places all non-specific comments and updates {@link #lowestY} accordingly.
     */
    private void placeNonSpecificComments(final LayoutContext context) {
        double currY = context.sgraph.getPadding().top - context.labelSpacing;
        
        for (SComment sComment : context.sgraph.getComments()) {
            if (sComment.isNonSpecific()) {
                currY += context.labelSpacing;
                sComment.getPosition().y = currY;
                currY += sComment.getSize().y;
                
                lowestY = Math.max(lowestY, currY);
            }
        }
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
     * Computes how much space we need above messages to place comments and labels.
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
    
}
