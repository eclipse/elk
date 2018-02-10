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
import java.util.Map;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SArea;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SExecution;
import org.eclipse.elk.alg.sequence.graph.SGraph;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.options.MessageType;
import org.eclipse.elk.alg.sequence.options.NodeType;
import org.eclipse.elk.alg.sequence.options.SequenceDiagramOptions;
import org.eclipse.elk.alg.sequence.options.SequenceExecutionType;
import org.eclipse.elk.core.UnsupportedGraphException;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import com.google.common.collect.Maps;

/**
 * Implements the import functionality of {@link ElkGraphTransformer} by turning an input ELK graph into a
 * {@link LayoutContext}. We create both an {@link SGraph} as well as an {@link LGraph}.
 */
public final class ElkGraphImporter {

    /**
     * Imports the given ELK graph and computes the corresponding {@link LayoutContext} object.
     * 
     * @param elkGraph
     *            the graph to be imported.
     * @return the corresponding layout context.
     */
    public LayoutContext importGraph(final ElkNode elkGraph) {
        LayoutContext context = new LayoutContext(elkGraph);

        importSGraph(context);
        createLayeredGraph(context);

        return context;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // SGraph Creation

    /** Maps {@link ElkNode}s to the {@link SLifeline}s they were turned into. */
    private Map<ElkNode, SLifeline> lifelineMap = Maps.newHashMap();
    /** Maps {@link ElkEdge}s to the {@link SMessage}s they were turned into. */
    private Map<ElkEdge, SMessage> messageMap = Maps.newHashMap();
    /** A map from element IDs to the corresponding lifelines. */
    private Map<Integer, SLifeline> lifelineIdMap = Maps.newHashMap();
    /** A map from element IDs to the corresponding executions. */
    private Map<Integer, SExecution> executionIdMap = Maps.newHashMap();
    /** A map from element IDs to the corresponding sequence area. */
    private Map<Integer, SArea> areaIdMap = Maps.newHashMap();

    /**
     * Builds a PGraph out of a given KGraph by associating every ElkNode to a PLifeline and every ElkEdge to a
     * PMessage.
     * 
     * @param context
     *            the layout context we're currently building.
     * @return the built SGraph
     */
    private void importSGraph(final LayoutContext context) {
        // Create a graph object
        SGraph sgraph = new SGraph();
        context.sgraph = sgraph;

        // Apply relevant properties
        if (context.elkgraph.hasProperty(SequenceDiagramOptions.INTERACTION_PADDING)) {
            sgraph.getPadding().copy(context.elkgraph.getProperty(SequenceDiagramOptions.INTERACTION_PADDING));
        } else {
            sgraph.getPadding().copy(context.elkgraph.getProperty(SequenceDiagramOptions.PADDING));
        }

        // Create... well, sequence areas.
        createSequenceAreas(context);

        // We need to import all lifelines first before importing messages and things
        for (ElkNode node : context.elkgraph.getChildren()) {
            if (node.getProperty(SequenceDiagramOptions.TYPE_NODE) == NodeType.LIFELINE) {
                createLifeline(node, context);
            }
        }

        // Executions and destruction events are added to existing lifelines as they are imported
        createExecutions(context);

        // Walk through lifelines to create their stuff
        for (ElkNode node : context.elkgraph.getChildren()) {
            if (node.getProperty(SequenceDiagramOptions.TYPE_NODE) == NodeType.LIFELINE) {
                // Create SMessages for each of the outgoing edges
                createOutgoingMessages(node, context);

                // Handle found messages (incoming messages)
                createMessagesFromNonLifelineOrigin(node, context);
            }
        }

        // Create comment-like objects that may be attached to things we have created in previous iterations
        for (ElkNode node : context.elkgraph.getChildren()) {
            switch (node.getProperty(SequenceDiagramOptions.TYPE_NODE)) {
            case COMMENT:
            case CONSTRAINT:
            case DURATION_OBSERVATION:
            case TIME_OBSERVATION:
                createCommentLikeNode(node, context);
                break;
            }
        }

        // Reset graph size to zero before layouting
        sgraph.getSize().x = 0;
        sgraph.getSize().y = 0;

        // Clear maps
        lifelineMap.clear();
        lifelineIdMap.clear();
        messageMap.clear();
        executionIdMap.clear();
    }

    //////////////////////////////////////////////////////////////
    // Areas

    /**
     * Creates all sequence areas for the given graph.
     * 
     * @param context
     *            the layout context we're currently building.
     */
    private void createSequenceAreas(final LayoutContext context) {
        // Find nodes that represent areas
        for (ElkNode node : context.elkgraph.getChildren()) {
            NodeType nodeType = node.getProperty(SequenceDiagramOptions.TYPE_NODE);

            if (nodeType == NodeType.COMBINED_FRAGMENT || nodeType == NodeType.INTERACTION_USE) {
                SArea area = new SArea();
                area.setProperty(InternalSequenceProperties.ORIGIN, node);

                context.sgraph.getAreas().add(area);
                areaIdMap.put(node.getProperty(SequenceDiagramOptions.ID_ELEMENT), area);
            }
        }

        // Now that all areas have been created, set up their parent-child relationships
        for (ElkNode node : context.elkgraph.getChildren()) {
            NodeType nodeType = node.getProperty(SequenceDiagramOptions.TYPE_NODE);

            if (nodeType == NodeType.COMBINED_FRAGMENT || nodeType == NodeType.INTERACTION_USE) {
                if (node.hasProperty(SequenceDiagramOptions.ID_PARENT_AREA)) {
                    int parentAreaId = node.getProperty(SequenceDiagramOptions.ID_PARENT_AREA);
                    int childAreaId = node.getProperty(SequenceDiagramOptions.ID_ELEMENT);

                    SArea parentArea = areaIdMap.get(parentAreaId);
                    SArea childArea = areaIdMap.get(childAreaId);

                    if (parentArea != null && childArea != null) {
                        parentArea.getContainedAreas().add(childArea);
                    } else {
                        // The child area must exist, so the parent area must be the problem
                        throw new UnsupportedGraphException("Parent area ID " + parentAreaId + " configured for area "
                                + childAreaId + " does not exist.");
                    }
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////
    // Lifelines

    /**
     * Turns the given {@link ElkNode} into an {@link SLifeline}, sets up its properties, and looks through its children
     * to setup destructions and executions.
     * 
     * @param klifeline
     *            the ndoe to turn into a lifeline.
     * @param context
     *            the layout context we're currently building.
     */
    private void createLifeline(final ElkNode klifeline, final LayoutContext context) {
        assert klifeline.getProperty(SequenceDiagramOptions.TYPE_NODE) == NodeType.LIFELINE;

        SLifeline slifeline = SLifeline.createLifeline(context.sgraph);

        // Setup the name
        // TODO: This should eventually import proper labels.
        if (klifeline.getLabels().size() > 0) {
            slifeline.setName(klifeline.getLabels().get(0).getText());
        }

        slifeline.setProperty(InternalSequenceProperties.ORIGIN, klifeline);
        lifelineMap.put(klifeline, slifeline);
        lifelineIdMap.put(klifeline.getProperty(SequenceDiagramOptions.ID_ELEMENT), slifeline);

        // Copy layout information to lifeline
        slifeline.getPosition().x = klifeline.getX();
        slifeline.getPosition().y = klifeline.getY();
        slifeline.getSize().x = klifeline.getWidth();
        slifeline.getSize().y = klifeline.getHeight();

        // Check if the lifeline has any empty areas
        if (klifeline.hasProperty(SequenceDiagramOptions.ID_AREAS))
            for (Integer areaId : klifeline.getProperty(SequenceDiagramOptions.ID_AREAS)) {
                SArea area = areaIdMap.get(areaId);
                if (area != null) {
                    area.getLifelines().add(slifeline);
                } else {
                    throw new UnsupportedGraphException("Area " + areaId + " does not exist");
                }
            }
    }

    //////////////////////////////////////////////////////////////
    // Executions

    /**
     * Creates all executions and destruction events for the given graph.
     * 
     * @param context
     *            the layout context we're currently building.
     */
    private void createExecutions(final LayoutContext context) {
        for (ElkNode kchild : context.elkgraph.getChildren()) {
            NodeType kchildNodeType = kchild.getProperty(SequenceDiagramOptions.TYPE_NODE);

            if (kchildNodeType.isExecutionType()) {
                // Create a new sequence execution for this thing
                SExecution sexecution = new SExecution();
                sexecution.setProperty(InternalSequenceProperties.ORIGIN, kchild);
                sexecution.setType(SequenceExecutionType.fromNodeType(kchildNodeType));
                executionIdMap.put(kchild.getProperty(SequenceDiagramOptions.ID_ELEMENT), sexecution);

                getParentLifeline(kchild).getExcecutions().add(sexecution);

            } else if (kchildNodeType == NodeType.DESTRUCTION_EVENT) {
                getParentLifeline(kchild).setProperty(InternalSequenceProperties.DESTRUCTION_NODE, kchild);
            }
        }
    }

    /**
     * Returns the lifeline the element represented by the given node says it belongs to or throws an exception if the
     * lifeline is improperly specified.
     * 
     * @param node
     *            the node.
     * @return the parent lifeline.
     * @throws UnsupportedGraphException
     *             if the parent lifeline is invalid or not specified.
     */
    private SLifeline getParentLifeline(final ElkNode node) {
        if (!node.hasProperty(SequenceDiagramOptions.ID_PARENT_LIFELINE)) {
            throw new UnsupportedGraphException(
                    "Executions and destruction events must specify the ID of the lifeline they belong to.");
        }

        SLifeline lifeline = lifelineIdMap.get(node.getProperty(SequenceDiagramOptions.ID_PARENT_LIFELINE));

        if (lifeline == null) {
            throw new UnsupportedGraphException(
                    "Executions and destruction events must specify the ID of the lifeline they belong to.");
        }

        return lifeline;
    }

    //////////////////////////////////////////////////////////////
    // Messages

    /**
     * Walk through the lifeline's outgoing edges and create {@link SMessage}s for each of them.
     * 
     * @param klifeline
     *            the lifeline's representation in the ELK graph.
     * @param context
     *            the layout context we're currently building.
     */
    private void createOutgoingMessages(final ElkNode klifeline, final LayoutContext context) {
        for (ElkEdge kmessage : klifeline.getOutgoingEdges()) {
            // We only support edges that have a single edge section and are simple edges
            if (kmessage.getSections().size() != 1) {
                throw new UnsupportedGraphException("Edges must have a single edge section.");
            }

            if (kmessage.isHyperedge()) {
                throw new UnsupportedGraphException("Edges must have a single source and a single target.");
            }

            SLifeline sourceLL = lifelineMap.get(kmessage.getSources().get(0));
            SLifeline targetLL = lifelineMap.get(kmessage.getTargets().get(0));

            // Lost-messages and messages to the surrounding interaction don't have a lifeline, so create dummy
            // lifeline
            // TODO: Use a single dummy lifeline to put all of these dummies in?
            if (targetLL == null) {
                SLifeline sdummy = SLifeline.createDummyLifeline(context.sgraph);
                targetLL = sdummy;
            }

            // Create message object and apply its coordinates
            SMessage smessage = new SMessage(sourceLL, targetLL);
            smessage.setProperty(InternalSequenceProperties.ORIGIN, kmessage);

            messageMap.put(kmessage, smessage);

            ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(kmessage, false, false);
            smessage.setSourceYPos(edgeSection.getStartY());
            smessage.setTargetYPos(edgeSection.getEndY());

            // Add message to the source and the target lifeline's list of messages
            sourceLL.addMessage(smessage);
            targetLL.addMessage(smessage);

            // Read size of the attached labels
            smessage.setLabelWidth(
                    kmessage.getLabels().stream().mapToDouble(klabel -> klabel.getWidth()).max().orElse(0.0));

            // Check if the edge connects to executions
            if (kmessage.hasProperty(SequenceDiagramOptions.ID_SOURCE_EXECUTIONS)) {
                for (Integer execId : kmessage.getProperty(SequenceDiagramOptions.ID_SOURCE_EXECUTIONS)) {
                    SExecution sourceExecution = executionIdMap.get(execId);
                    if (sourceExecution != null) {
                        sourceExecution.addMessage(smessage);
                    } else {
                        throw new UnsupportedGraphException("Execution " + execId + " does not exist");
                    }
                }
            }

            if (kmessage.hasProperty(SequenceDiagramOptions.ID_TARGET_EXECUTIONS)) {
                for (Integer execId : kmessage.getProperty(SequenceDiagramOptions.ID_TARGET_EXECUTIONS)) {
                    SExecution targetExecution = executionIdMap.get(execId);
                    if (targetExecution != null) {
                        targetExecution.addMessage(smessage);
                    } else {
                        throw new UnsupportedGraphException("Execution " + execId + " does not exist");
                    }
                }
            }

            // Append the message type of the edge to the message (for certain message types)
            MessageType messageType = kmessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE);
            switch (messageType) {
            case ASYNCHRONOUS:
            case CREATE:
            case DELETE:
            case LOST:
            case SYNCHRONOUS:
                smessage.setProperty(SequenceDiagramOptions.TYPE_MESSAGE, messageType);
                break;
            }

            // Outgoing messages to the surrounding interaction are drawn to the right and therefore
            // their target lifeline should have highest position
            // TODO: There might have to be a single dummy lifeline that represents messages to the interaction.
            if (targetLL.isDummy() && messageType != MessageType.LOST) {
                targetLL.setHorizontalSlot(context.sgraph.getLifelines().size() + 1);
            }

            // Check if message is in any area
            for (Integer areaId : kmessage.getProperty(SequenceDiagramOptions.ID_AREAS)) {
                SArea area = areaIdMap.get(areaId);
                if (area != null) {
                    area.getMessages().add(smessage);
                    area.getLifelines().add(smessage.getSource());
                    area.getLifelines().add(smessage.getTarget());
                } else {
                    throw new UnsupportedGraphException("Area " + areaId + " does not exist");
                }
            }

            // Check if this message has an empty area that is to be placed directly above it
            if (kmessage.hasProperty(SequenceDiagramOptions.ID_UPPER_EMPTY_AREA)) {
                int upperEmptyAreaId = kmessage.getProperty(SequenceDiagramOptions.ID_UPPER_EMPTY_AREA);
                SArea upperArea = areaIdMap.get(upperEmptyAreaId);

                if (upperArea != null) {
                    upperArea.setNextMessage(smessage);
                } else {
                    throw new UnsupportedGraphException("Area " + upperEmptyAreaId + " does not exist");
                }
            }
        }
    }

    /**
     * Walk through incoming edges of the given lifeline and check if there are found messages or messages that come
     * from the surrounding interaction. If so, create the corresponding SMessage.
     * 
     * @param klifeline
     *            the lifeline's representation in the ELK graph.
     * @param context
     *            the layout context we're currently building.
     */
    private void createMessagesFromNonLifelineOrigin(final ElkNode klifeline, final LayoutContext context) {
        for (ElkEdge kmessage : klifeline.getIncomingEdges()) {
            // We only support edges that have a single edge section and are simple edges
            if (kmessage.getSections().size() != 1) {
                throw new UnsupportedGraphException("Edges must have a single edge section.");
            }

            if (kmessage.isHyperedge()) {
                throw new UnsupportedGraphException("Edges must have a single source and a single target.");
            }

            if (lifelineMap.containsKey(kmessage.getSources().get(0))) {
                // We are only interested in messages that don't come from a lifeline
                continue;
            }

            // TODO consider connections to comments and constraints?

            SLifeline sourceLL = SLifeline.createDummyLifeline(context.sgraph);
            SLifeline targetLL = lifelineMap.get(kmessage.getTargets().get(0));

            // Create message object and apply its coordinates
            SMessage smessage = new SMessage(sourceLL, targetLL);
            smessage.setProperty(InternalSequenceProperties.ORIGIN, kmessage);

            messageMap.put(kmessage, smessage);

            ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(kmessage, false, false);
            smessage.setTargetYPos(edgeSection.getEndY());

            // Add the message to the source and target lifeline's list of messages
            sourceLL.addMessage(smessage);
            targetLL.addMessage(smessage);

            // Check if the message connects to target executions
            if (kmessage.hasProperty(SequenceDiagramOptions.ID_TARGET_EXECUTIONS)) {
                for (Integer execId : kmessage.getProperty(SequenceDiagramOptions.ID_TARGET_EXECUTIONS)) {
                    SExecution targetExecution = executionIdMap.get(execId);
                    if (targetExecution != null) {
                        targetExecution.addMessage(smessage);
                    } else {
                        throw new UnsupportedGraphException("Execution " + execId + " does not exist");
                    }
                }
            }

            // Append the message type of the edge to the message
            MessageType messageType = kmessage.getProperty(SequenceDiagramOptions.TYPE_MESSAGE);
            switch (messageType) {
            case ASYNCHRONOUS:
            case CREATE:
            case DELETE:
            case FOUND:
            case SYNCHRONOUS:
                smessage.setProperty(SequenceDiagramOptions.TYPE_MESSAGE, messageType);
                break;
            }

            // Since incoming messages come from the left side of the surrounding interaction, give its dummy lifeline
            // position -1
            // TODO: There might have to be a single dummy lifeline that represents messages to the interaction.
            if (targetLL.isDummy() && messageType != MessageType.LOST) {
                targetLL.setHorizontalSlot(context.sgraph.getLifelines().size() + 1);
            }
        }
    }

    //////////////////////////////////////////////////////////////
    // Comment-like Nodes

    /**
     * Create a comment object for comments or constraints (which are handled like comments).
     * 
     * @param node
     *            the node to create a comment object from.
     * @param context
     *            the layout context we're currently building.
     */
    private void createCommentLikeNode(final ElkNode node, final LayoutContext context) {
        // Get the node's type
        NodeType nodeType = node.getProperty(SequenceDiagramOptions.TYPE_NODE);

        // Create comment object
        SComment comment = new SComment();
        comment.setProperty(InternalSequenceProperties.ORIGIN, node);
        comment.setProperty(SequenceDiagramOptions.TYPE_NODE, nodeType);

        // Copy layout information
        comment.getPosition().x = node.getX();
        comment.getPosition().y = node.getY();
        comment.getSize().x = node.getWidth();
        comment.getSize().y = node.getHeight();

        // Attach connected edge to comment
        if (!node.getOutgoingEdges().isEmpty()) {
            comment.setProperty(InternalSequenceProperties.COMMENT_CONNECTION, node.getOutgoingEdges().get(0));
        }

        // Provide the comment with a list of elements it is attached to
        if (node.hasProperty(SequenceDiagramOptions.ID_ATTACHED_ELEMENT)) {
            Integer elementId = node.getProperty(SequenceDiagramOptions.ID_ATTACHED_ELEMENT);

            // Comments can be attached either to lifelines or to messages
            if (lifelineMap.containsKey(elementId)) {
                comment.setAttachment(lifelineMap.get(elementId));
            } else if (messageMap.containsKey(elementId)) {
                comment.setAttachment(messageMap.get(elementId));
            }
        }

        // Handle time observations
        // TODO: This should not be based on coordinate calculations, but on properties set by the layout connector.
        if (nodeType == NodeType.TIME_OBSERVATION) {
            comment.getSize().x = context.sgraph.getProperty(SequenceDiagramOptions.SIZE_TIME_OBSERVATION_WIDTH);

            // Find lifeline that is next to the time observation
            SLifeline nextLifeline = null;
            double smallestDistance = Double.MAX_VALUE;
            for (SLifeline lifeline : context.sgraph.getLifelines()) {
                double distance = Math.abs(
                        (lifeline.getPosition().x + lifeline.getSize().x / 2) - (node.getX() + node.getWidth() / 2));
                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    nextLifeline = lifeline;
                }
            }

            // Find message on the calculated lifeline that is next to the time observation
            SMessage nextMessage = null;
            smallestDistance = Double.MAX_VALUE;
            for (SMessage message : nextLifeline.getMessages()) {
                ElkEdge edge = (ElkEdge) message.getProperty(InternalSequenceProperties.ORIGIN);
                ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(edge, false, false);
                double distance;
                if (message.getSource() == nextLifeline) {
                    distance = Math.abs((edgeSection.getStartY()) - (node.getY() + node.getHeight() / 2));
                } else {
                    distance = Math.abs((edgeSection.getEndY()) - (node.getY() + node.getHeight() / 2));
                }

                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    nextMessage = message;
                }
            }

            // Set both lifeline and message of the comment to indicate that it should be drawn near to the event
            comment.setLifeline(nextLifeline);
            comment.setReferenceMessage(nextMessage);
        }

        context.sgraph.getComments().add(comment);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // LGraph Creation

    /**
     * Builds a layered graph that contains every message as a node. Edges are representations of the relative order of
     * the messages.
     * 
     * @param context
     *            the layout context we're currently building.
     */
    private void createLayeredGraph(final LayoutContext context) {
        LGraph lgraph = new LGraph();
        context.lgraph = lgraph;

        // Build a node for every message.
        int nodeNumber = 0;
        for (SLifeline lifeline : context.sgraph.getLifelines()) {
            for (SMessage message : lifeline.getOutgoingMessages()) {
                LNode node = new LNode(lgraph);
                lgraph.getLayerlessNodes().add(node);

                node.getLabels().add(new LLabel("Node" + nodeNumber++));

                node.setProperty(InternalSequenceProperties.ORIGIN, message);
                message.setProperty(InternalSequenceProperties.LAYERED_NODE, node);
            }

            // Handle found messages (they have no source lifeline)
            for (SMessage message : lifeline.getIncomingMessages()) {
                if (message.getSource().isDummy()) {
                    LNode node = new LNode(lgraph);
                    lgraph.getLayerlessNodes().add(node);

                    node.getLabels().add(new LLabel("Node" + nodeNumber++));

                    node.setProperty(InternalSequenceProperties.ORIGIN, message);
                    message.setProperty(InternalSequenceProperties.LAYERED_NODE, node);
                }
            }
        }

        // Add an edge for every pair of consecutive messages at every lifeline to indicate their relative order
        for (SLifeline lifeline : context.sgraph.getLifelines()) {
            List<SMessage> messages = lifeline.getMessages();
            for (int j = 1; j < messages.size(); j++) {
                // Add an edge from the node belonging to message j-1 to the node belonging to message j
                LNode sourceNode = messages.get(j - 1).getProperty(InternalSequenceProperties.LAYERED_NODE);
                LNode targetNode = messages.get(j).getProperty(InternalSequenceProperties.LAYERED_NODE);

                assert sourceNode != null && targetNode != null;

                if (sourceNode != targetNode) {
                    LPort sourcePort = new LPort();
                    sourcePort.setNode(sourceNode);

                    LPort targetPort = new LPort();
                    targetPort.setNode(targetNode);

                    LEdge edge = new LEdge();

                    edge.setSource(sourcePort);
                    edge.setTarget(targetPort);

                    edge.setProperty(InternalSequenceProperties.BELONGS_TO_LIFELINE, lifeline);
                }
            }
        }
    }

}
