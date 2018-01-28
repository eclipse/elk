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
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SGraph;
import org.eclipse.elk.alg.sequence.graph.SGraphElement;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.options.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.options.MessageType;
import org.eclipse.elk.alg.sequence.options.NodeType;
import org.eclipse.elk.alg.sequence.options.SequenceArea;
import org.eclipse.elk.alg.sequence.options.SequenceDiagramOptions;
import org.eclipse.elk.alg.sequence.options.SequenceExecution;
import org.eclipse.elk.alg.sequence.options.SequenceExecutionType;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Turns the KGraph of a layout context into an SGraph and an LGraph.
 * 
 * @author grh
 */
public final class ElkGraphImporter {
    
    public LayoutContext importGraph(final ElkNode elkGraph) {
        LayoutContext context = new LayoutContext(elkGraph);
        
        context.sgraph = importSGraph(context.kgraph);
        context.lgraph = createLayeredGraph(context.sgraph);
        
        return context;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // SGraph Creation
    
    /** A map from ElkNodes in the layout graph to the lifelines created for them. */
    private Map<ElkNode, SLifeline> lifelineMap = Maps.newHashMap();
    /** A map from ElkEdges in the layout graph to messages created for them in the SGraph. */
    private Map<ElkEdge, SMessage> messageMap = Maps.newHashMap();
    /** A map from element IDs to the corresponding executions. */
    private Map<Integer, SequenceExecution> executionIdMap = Maps.newHashMap();
    /** A map from element IDs to the corresponding sequence area. */
    private Map<Integer, SequenceArea> areaIdMap = Maps.newHashMap();
    
    
    /**
     * Builds a PGraph out of a given KGraph by associating every ElkNode to a PLifeline and every
     * ElkEdge to a PMessage.
     * 
     * @param topNode
     *            the surrounding interaction node.
     * @return the built SGraph
     */
    private SGraph importSGraph(final ElkNode topNode) {
        // Create a graph object
        SGraph sgraph = new SGraph();
        
        // Create... well, as it says: sequence areas...
        createSequenceAreas(topNode, sgraph);

        // Create lifelines
        for (ElkNode node : topNode.getChildren()) {
            NodeType nodeType = node.getProperty(SequenceDiagramOptions.NODE_TYPE);
            
            if (nodeType == NodeType.LIFELINE) {
                createLifeline(sgraph, node);
            }
        }

        // Walk through lifelines (create their messages) and comments
        for (ElkNode node : topNode.getChildren()) {
            NodeType nodeType = node.getProperty(SequenceDiagramOptions.NODE_TYPE);
            
            if (nodeType == NodeType.LIFELINE) {
                // Create SMessages for each of the outgoing edges
                createOutgoingMessages(sgraph, node);

                // Handle found messages (incoming messages)
                createIncomingMessages(sgraph, node);
            } else if (nodeType == NodeType.COMMENT
                    || nodeType == NodeType.CONSTRAINT
                    || nodeType == NodeType.DURATION_OBSERVATION
                    || nodeType == NodeType.TIME_OBSERVATION) {
                
                createCommentLikeNode(sgraph, node);
            }
        }

        // Reset graph size to zero before layouting
        sgraph.getSize().x = 0;
        sgraph.getSize().y = 0;
        
        // Clear maps
        lifelineMap.clear();
        messageMap.clear();
        executionIdMap.clear();

        return sgraph;
    }
    
    
    //////////////////////////////////////////////////////////////
    // Areas
    
    /**
     * Creates all sequence areas for the given graph.
     * 
     * @param topNode
     *            the surrounding interaction node.
     * @param sgraph
     *            the Sequence Graph
     */
    private void createSequenceAreas(final ElkNode topNode, final SGraph sgraph) {
        // Initialize the list of areas (fragments and such)
        List<SequenceArea> areas = Lists.newArrayList();
        
        // Find nodes that represent areas
        for (ElkNode node : topNode.getChildren()) {
            NodeType nodeType = node.getProperty(SequenceDiagramOptions.NODE_TYPE);
            
            if (nodeType == NodeType.COMBINED_FRAGMENT || nodeType == NodeType.INTERACTION_USE) {
                SequenceArea area = new SequenceArea(node);
                areas.add(area);
                areaIdMap.put(node.getProperty(SequenceDiagramOptions.ELEMENT_ID), area);
            }
        }
        
        // Now that all areas have been created, find nodes that represent nested areas
        for (ElkNode node : topNode.getChildren()) {
            NodeType nodeType = node.getProperty(SequenceDiagramOptions.NODE_TYPE);
            
            if (nodeType == NodeType.COMBINED_FRAGMENT || nodeType == NodeType.INTERACTION_USE) {
                int parentId = node.getProperty(SequenceDiagramOptions.PARENT_AREA_ID);
                
                if (parentId != -1) {
                    SequenceArea parentArea = areaIdMap.get(parentId);
                    SequenceArea childArea = areaIdMap.get(node.getProperty(SequenceDiagramOptions.ELEMENT_ID));
                    
                    if (parentArea != null && childArea != null) {
                        parentArea.getContainedAreas().add(childArea);
                    }
                    // TODO Possibly throw an exception in the else case
                }
            }
        }
        
        // Remember the areas
        sgraph.setProperty(SequenceDiagramOptions.AREAS, areas);
    }


    //////////////////////////////////////////////////////////////
    // Lifelines

    /**
     * Creates the SLifeline for the given ElkNode, sets up its properties, and looks through its children
     * to setup destructions and executions.
     * 
     * @param sgraph
     *            the Sequence Graph
     * @param klifeline
     *            the ElkNode to create a lifeline for
     */
    private void createLifeline(final SGraph sgraph, final ElkNode klifeline) {
        assert klifeline.getProperty(SequenceDiagramOptions.NODE_TYPE) == NodeType.LIFELINE;
        
        // Node is lifeline
        SLifeline slifeline = SLifeline.createLifeline(sgraph);
        if (klifeline.getLabels().size() > 0) {
            slifeline.setName(klifeline.getLabels().get(0).getText());
        }
        
        slifeline.setProperty(InternalProperties.ORIGIN, klifeline);
        lifelineMap.put(klifeline, slifeline);

        // Copy layout information to lifeline
        slifeline.getPosition().x = klifeline.getX();
        slifeline.getPosition().y = klifeline.getY();
        slifeline.getSize().x = klifeline.getWidth();
        slifeline.getSize().y = klifeline.getHeight();
        
        // Iterate through the lifeline's children to collect destructions and executions
        List<SequenceExecution> executions = Lists.newArrayList();
        
        for (ElkNode kchild : klifeline.getChildren()) {
            NodeType kchildNodeType = kchild.getProperty(SequenceDiagramOptions.NODE_TYPE);
            
            if (kchildNodeType.isExecutionType()) {
                // Create a new sequence execution for this thing
                SequenceExecution execution = new SequenceExecution(kchild);
                execution.setType(SequenceExecutionType.fromNodeType(kchildNodeType));
                executions.add(execution);
                executionIdMap.put(kchild.getProperty(SequenceDiagramOptions.ELEMENT_ID), execution);
            } else if (kchildNodeType == NodeType.DESTRUCTION_EVENT) {
                slifeline.setProperty(SequenceDiagramOptions.DESTRUCTION_NODE, kchild);
            }
        }
        
        slifeline.setProperty(SequenceDiagramOptions.EXECUTIONS, executions);
        
        // Check if the lifeline has any empty areas
        List<Integer> areaIds = klifeline.getProperty(SequenceDiagramOptions.AREA_IDS);
        for (Integer areaId : areaIds) {
            SequenceArea area = areaIdMap.get(areaId);
            if (area != null) {
                area.getLifelines().add(slifeline);
            }
        }
    }


    //////////////////////////////////////////////////////////////
    // Messages

    /**
     * Walk through the lifeline's outgoing edges and create SMessages for each of them.
     * 
     * @param sgraph
     *            the Sequence Graph
     * @param klifeline
     *            the ElkNode to search its outgoing edges
     */
    private void createOutgoingMessages(final SGraph sgraph, final ElkNode klifeline) {
        for (ElkEdge kedge : klifeline.getOutgoingEdges()) {
            SLifeline sourceLL = lifelineMap.get(kedge.getSources().get(0));
            SLifeline targetLL = lifelineMap.get(kedge.getTargets().get(0));

            // Lost-messages and messages to the surrounding interaction don't have a lifeline, so
            // create dummy lifeline
            if (targetLL == null) {
                SLifeline sdummy = SLifeline.createDummyLifeline(sgraph);
                targetLL = sdummy;
            }

            // Create message object
            SMessage smessage = new SMessage(sourceLL, targetLL);
            smessage.setProperty(InternalProperties.ORIGIN, kedge);
            
            ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(kedge, false, false);
            smessage.setSourceYPos(edgeSection.getStartY());
            smessage.setTargetYPos(edgeSection.getEndY());

            // Read size of the attached labels
            double maxLabelLength = 0;
            for (ElkLabel klabel : kedge.getLabels()) {
                if (klabel.getWidth() > maxLabelLength) {
                    maxLabelLength = klabel.getWidth();
                }
            }
            smessage.setLabelWidth(maxLabelLength);

            // Add message to the source and the target lifeline's list of messages
            sourceLL.addMessage(smessage);
            targetLL.addMessage(smessage);

            // Put edge and message into the edge map
            messageMap.put(kedge, smessage);
            
            // Check if the edge connects to executions
            List<Integer> sourceExecutionIds = kedge.getProperty(SequenceDiagramOptions.SOURCE_EXECUTION_IDS);
            smessage.setProperty(SequenceDiagramOptions.SOURCE_EXECUTION_IDS, sourceExecutionIds);
            for (Integer execId : sourceExecutionIds) {
                SequenceExecution sourceExecution = executionIdMap.get(execId);
                if (sourceExecution != null) {
                    sourceExecution.addMessage(smessage);
                }
            }
            
            List<Integer> targetExecutionIds = kedge.getProperty(SequenceDiagramOptions.TARGET_EXECUTION_IDS);
            smessage.setProperty(SequenceDiagramOptions.TARGET_EXECUTION_IDS, sourceExecutionIds);
            for (Integer execId : targetExecutionIds) {
                SequenceExecution targetExecution = executionIdMap.get(execId);
                if (targetExecution != null) {
                    targetExecution.addMessage(smessage);
                }
            }

            // Append the message type of the edge to the message
            MessageType messageType = kedge.getProperty(SequenceDiagramOptions.MESSAGE_TYPE);
            if (messageType == MessageType.ASYNCHRONOUS
                    || messageType == MessageType.CREATE
                    || messageType == MessageType.DELETE
                    || messageType == MessageType.SYNCHRONOUS
                    || messageType == MessageType.LOST) {
                
                smessage.setProperty(SequenceDiagramOptions.MESSAGE_TYPE, messageType);
            }

            // Outgoing messages to the surrounding interaction are drawn to the right and therefore
            // their target lifeline should have highest position
            if (targetLL.isDummy() && messageType != MessageType.LOST) {
                targetLL.setHorizontalSlot(sgraph.getLifelines().size() + 1);
            }

            // Check if message is in any area
            for (Integer areaId : kedge.getProperty(SequenceDiagramOptions.AREA_IDS)) {
                SequenceArea area = areaIdMap.get(areaId);
                if (area != null) {
                    area.getMessages().add(smessage);
                    area.getLifelines().add(smessage.getSource());
                    area.getLifelines().add(smessage.getTarget());
                }
                // TODO Possibly throw an exception in the else case
            }
            
            // Check if this message has an empty area that is to be placed directly above it
            int upperEmptyAreaId = kedge.getProperty(SequenceDiagramOptions.UPPER_EMPTY_AREA_ID);
            SequenceArea upperArea = areaIdMap.get(upperEmptyAreaId);
            if (upperArea != null) {
                upperArea.setNextMessage(smessage);
            }
        }
    }

    /**
     * Walk through incoming edges of the given lifeline and check if there are found messages
     * or messages that come from the surrounding interaction. If so, create the corresponding
     * SMessage.
     * 
     * @param sgraph
     *            the Sequence Graph
     * @param klifeline
     *            the ElkNode to search its incoming edges.
     */
    private void createIncomingMessages(final SGraph sgraph, final ElkNode klifeline) {
        for (ElkEdge kedge : klifeline.getIncomingEdges()) {
            SLifeline sourceLL = lifelineMap.get(kedge.getSources().get(0));
            
            // We are only interested in messages that don't come from a lifeline
            if (sourceLL != null) {
                continue;
            }
            
            // TODO consider connections to comments and constraints!
            
            // Create dummy lifeline as source since the message has no source lifeline
            // TODO We could think about using a single dummy lifeline for all found messages
            SLifeline sdummy = SLifeline.createDummyLifeline(sgraph);
            sourceLL = sdummy;
            
            SLifeline targetLL = lifelineMap.get(kedge.getTargets().get(0));
            
            ElkEdgeSection edgeSection = ElkGraphUtil.firstEdgeSection(kedge, false, false);

            // Create message object
            SMessage smessage = new SMessage(sourceLL, targetLL);
            smessage.setProperty(InternalProperties.ORIGIN, kedge);
            smessage.setTargetYPos(edgeSection.getEndY());

            // Add the message to the source and target lifeline's list of messages
            sourceLL.addMessage(smessage);
            targetLL.addMessage(smessage);

            // Put edge and message into the edge map
            messageMap.put(kedge, smessage);

            // Append the message type of the edge to the message
            MessageType messageType = kedge.getProperty(SequenceDiagramOptions.MESSAGE_TYPE);
            if (messageType == MessageType.FOUND) {
                smessage.setProperty(SequenceDiagramOptions.MESSAGE_TYPE, MessageType.FOUND);
            } else {
                // Since incoming messages come from the left side of the surrounding
                // interaction, give its dummy lifeline position -1
                sourceLL.setHorizontalSlot(-1);

                if (messageType == MessageType.ASYNCHRONOUS
                        || messageType == MessageType.CREATE
                        || messageType == MessageType.DELETE
                        || messageType == MessageType.SYNCHRONOUS) {
                    
                    smessage.setProperty(SequenceDiagramOptions.MESSAGE_TYPE, messageType);
                }
            }

            // Check if the message connects to a target executions
            List<Integer> targetExecutionIds = kedge.getProperty(SequenceDiagramOptions.TARGET_EXECUTION_IDS);
            smessage.setProperty(SequenceDiagramOptions.TARGET_EXECUTION_IDS, targetExecutionIds);
            for (Integer execId : targetExecutionIds) {
                SequenceExecution targetExecution = executionIdMap.get(execId);
                if (targetExecution != null) {
                    targetExecution.addMessage(smessage);
                }
            }
        }
    }
    
    
    //////////////////////////////////////////////////////////////
    // Comment-like Nodes

    /**
     * Create a comment object for comments or constraints (which are handled like comments).
     * 
     * @param sgraph
     *            the Sequence Graph
     * @param node
     *            the node to create a comment object from
     */
    private void createCommentLikeNode(final SGraph sgraph, final ElkNode node) {
        // Get the node's type
        NodeType nodeType = node.getProperty(SequenceDiagramOptions.NODE_TYPE);

        // Create comment object
        SComment comment = new SComment();
        comment.setProperty(InternalProperties.ORIGIN, node);
        comment.setProperty(SequenceDiagramOptions.NODE_TYPE, nodeType);
        comment.setProperty(SequenceDiagramOptions.ATTACHED_ELEMENT_TYPE,
                node.getProperty(SequenceDiagramOptions.ATTACHED_ELEMENT_TYPE));
        
        // Attach connected edge to comment
        if (!node.getOutgoingEdges().isEmpty()) {
            comment.setProperty(InternalSequenceProperties.COMMENT_CONNECTION, node.getOutgoingEdges().get(0));
        }

        // Copy all the entries of the list of attached elements to the comment object
        List<Object> attachedTo = node.getProperty(SequenceDiagramOptions.ATTACHED_OBJECTS);
        if (attachedTo != null) {
            List<SGraphElement> attTo = comment.getAttachments();
            for (Object att : attachedTo) {
                if (att instanceof ElkNode) {
                    attTo.add(lifelineMap.get(att));
                } else if (att instanceof ElkEdge) {
                    attTo.add(messageMap.get(att));
                }
            }
        }

        // Copy layout information
        comment.getPosition().x = node.getX();
        comment.getPosition().y = node.getY();
        comment.getSize().x = node.getWidth();
        comment.getSize().y = node.getHeight();

        // Handle time observations
        if (nodeType == NodeType.TIME_OBSERVATION) {
            comment.getSize().x = sgraph.getProperty(SequenceDiagramOptions.TIME_OBSERVATION_WIDTH);

            // Find lifeline that is next to the time observation
            SLifeline nextLifeline = null;
            double smallestDistance = Double.MAX_VALUE;
            for (SLifeline lifeline : sgraph.getLifelines()) {
                double distance = Math.abs((lifeline.getPosition().x + lifeline.getSize().x / 2)
                        - (node.getX() + node.getWidth() / 2));
                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    nextLifeline = lifeline;
                }
            }

            // Find message on the calculated lifeline that is next to the time observation
            SMessage nextMessage = null;
            smallestDistance = Double.MAX_VALUE;
            for (SMessage message : nextLifeline.getMessages()) {
                ElkEdge edge = (ElkEdge) message.getProperty(InternalProperties.ORIGIN);
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

            // Set both, lifeline and message of the comment to indicate that it should be drawn
            // near to the event
            comment.setLifeline(nextLifeline);
            comment.setReferenceMessage(nextMessage);
        }

        sgraph.getComments().add(comment);
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // LGraph Creation

    /**
     * Builds a layered graph that contains every message as a node. Edges are representations of
     * the relative order of the messages.
     * 
     * @param sgraph
     *            the given SGraph
     * @param progressMonitor
     *            the progress monitor
     * @return the layeredGraph
     */
    private LGraph createLayeredGraph(final SGraph sgraph) {
        LGraph lgraph = new LGraph();

        // Build a node for every message.
        int i = 0;
        for (SLifeline lifeline : sgraph.getLifelines()) {
            for (SMessage message : lifeline.getOutgoingMessages()) {
                LNode node = new LNode(lgraph);
                node.getLabels().add(new LLabel("Node" + i++));
                node.setProperty(InternalProperties.ORIGIN, message);
                message.setProperty(InternalSequenceProperties.LAYERED_NODE, node);
                lgraph.getLayerlessNodes().add(node);
            }
            // Handle found messages (they have no source lifeline)
            for (SMessage message : lifeline.getIncomingMessages()) {
                if (message.getSource().isDummy()) {
                    LNode node = new LNode(lgraph);
                    node.getLabels().add(new LLabel("Node" + i++));
                    node.setProperty(InternalProperties.ORIGIN, message);
                    message.setProperty(InternalSequenceProperties.LAYERED_NODE, node);
                    lgraph.getLayerlessNodes().add(node);
                }
            }
        }

        // Add an edge for every neighbored pair of messages at every lifeline
        // indicating the relative order of the messages.
        for (SLifeline lifeline : sgraph.getLifelines()) {
            List<SMessage> messages = lifeline.getMessages();
            for (int j = 1; j < messages.size(); j++) {
                // Add an edge from the node belonging to message j-1 to the node belonging to
                // message j
                LNode sourceNode = messages.get(j - 1).getProperty(
                        InternalSequenceProperties.LAYERED_NODE);
                LNode targetNode = messages.get(j).getProperty(
                        InternalSequenceProperties.LAYERED_NODE);
                
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

        return lgraph;
    }
    
}
