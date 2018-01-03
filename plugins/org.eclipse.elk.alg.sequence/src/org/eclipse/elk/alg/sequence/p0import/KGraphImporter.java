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
package org.eclipse.elk.alg.sequence.p0import;

import java.util.List;
import java.util.Map;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.sequence.ISequenceLayoutProcessor;
import org.eclipse.elk.alg.sequence.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SComment;
import org.eclipse.elk.alg.sequence.graph.SGraph;
import org.eclipse.elk.alg.sequence.graph.SGraphElement;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.alg.sequence.graph.SMessage;
import org.eclipse.elk.alg.sequence.properties.InternalSequenceProperties;
import org.eclipse.elk.alg.sequence.properties.MessageType;
import org.eclipse.elk.alg.sequence.properties.NodeType;
import org.eclipse.elk.alg.sequence.properties.SequenceArea;
import org.eclipse.elk.alg.sequence.properties.SequenceDiagramOptions;
import org.eclipse.elk.alg.sequence.properties.SequenceExecution;
import org.eclipse.elk.alg.sequence.properties.SequenceExecutionType;
import org.eclipse.elk.core.klayoutdata.KEdgeLayout;
import org.eclipse.elk.core.klayoutdata.KLayoutData;
import org.eclipse.elk.core.klayoutdata.KShapeLayout;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Turns the KGraph of a layout context into an SGraph and an LGraph.
 * 
 * @author grh
 * @kieler.design 2012-11-20 cds, msp
 * @kieler.rating proposed yellow grh
 */
public final class KGraphImporter implements ISequenceLayoutProcessor {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Graph import", 1);
        
        context.sgraph = importGraph(context.kgraph);
        context.lgraph = createLayeredGraph(context.sgraph);
        
        progressMonitor.done();
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // SGraph Creation
    
    /** A map from KNodes in the layout graph to the lifelines created for them. */
    private Map<KNode, SLifeline> lifelineMap = Maps.newHashMap();
    /** A map from KEdges in the layout graph to messages created for them in the SGraph. */
    private Map<KEdge, SMessage> messageMap = Maps.newHashMap();
    /** A map from element IDs to the corresponding executions. */
    private Map<Integer, SequenceExecution> executionIdMap = Maps.newHashMap();
    /** A map from element IDs to the corresponding sequence area. */
    private Map<Integer, SequenceArea> areaIdMap = Maps.newHashMap();
    
    
    /**
     * Builds a PGraph out of a given KGraph by associating every KNode to a PLifeline and every
     * KEdge to a PMessage.
     * 
     * @param topNode
     *            the surrounding interaction node.
     * @return the built SGraph
     */
    private SGraph importGraph(final KNode topNode) {
        // Create a graph object
        SGraph sgraph = new SGraph();
        
        // Create... well, as it says: sequence areas...
        createSequenceAreas(topNode, sgraph);

        // Create lifelines
        for (KNode node : topNode.getChildren()) {
            NodeType nodeType = node.getData(KShapeLayout.class).getProperty(
                    SequenceDiagramOptions.NODE_TYPE);
            
            if (nodeType == NodeType.LIFELINE) {
                createLifeline(sgraph, node);
            }
        }

        // Walk through lifelines (create their messages) and comments
        for (KNode node : topNode.getChildren()) {
            NodeType nodeType = node.getData(KShapeLayout.class).getProperty(
                    SequenceDiagramOptions.NODE_TYPE);
            
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
    private void createSequenceAreas(final KNode topNode, final SGraph sgraph) {
        // Initialize the list of areas (fragments and such)
        List<SequenceArea> areas = Lists.newArrayList();
        
        // Find nodes that represent areas
        for (KNode node : topNode.getChildren()) {
            KLayoutData layoutData = node.getData(KLayoutData.class);
            NodeType nodeType = layoutData.getProperty(SequenceDiagramOptions.NODE_TYPE);
            
            if (nodeType == NodeType.COMBINED_FRAGMENT || nodeType == NodeType.INTERACTION_USE) {
                SequenceArea area = new SequenceArea(node);
                areas.add(area);
                areaIdMap.put(layoutData.getProperty(SequenceDiagramOptions.ELEMENT_ID), area);
            }
        }
        
        // Now that all areas have been created, find nodes that represent nested areas
        for (KNode node : topNode.getChildren()) {
            KLayoutData layoutData = node.getData(KLayoutData.class);
            NodeType nodeType = layoutData.getProperty(SequenceDiagramOptions.NODE_TYPE);
            
            if (nodeType == NodeType.COMBINED_FRAGMENT || nodeType == NodeType.INTERACTION_USE) {
                int parentId = layoutData.getProperty(SequenceDiagramOptions.PARENT_AREA_ID);
                
                if (parentId != -1) {
                    SequenceArea parentArea = areaIdMap.get(parentId);
                    SequenceArea childArea = areaIdMap.get(
                            layoutData.getProperty(SequenceDiagramOptions.ELEMENT_ID));
                    
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
     * Creates the SLifeline for the given KNode, sets up its properties, and looks through its children
     * to setup destructions and executions.
     * 
     * @param sgraph
     *            the Sequence Graph
     * @param klifeline
     *            the KNode to create a lifeline for
     */
    private void createLifeline(final SGraph sgraph, final KNode klifeline) {
        KShapeLayout klayout = klifeline.getData(KShapeLayout.class);
        
        assert klayout.getProperty(SequenceDiagramOptions.NODE_TYPE) == NodeType.LIFELINE;
        
        // Node is lifeline
        SLifeline slifeline = new SLifeline();
        if (klifeline.getLabels().size() > 0) {
            slifeline.setName(klifeline.getLabels().get(0).getText());
        }
        
        slifeline.setProperty(InternalProperties.ORIGIN, klifeline);
        lifelineMap.put(klifeline, slifeline);
        sgraph.addLifeline(slifeline);

        // Copy layout information to lifeline
        slifeline.getPosition().x = klayout.getXpos();
        slifeline.getPosition().y = klayout.getYpos();
        slifeline.getSize().x = klayout.getWidth();
        slifeline.getSize().y = klayout.getHeight();
        
        // Iterate through the lifeline's children to collect destructions and executions
        List<SequenceExecution> executions = Lists.newArrayList();
        
        for (KNode kchild : klifeline.getChildren()) {
            KShapeLayout kchildLayout = kchild.getData(KShapeLayout.class);
            NodeType kchildNodeType = kchildLayout.getProperty(SequenceDiagramOptions.NODE_TYPE);
            
            if (kchildNodeType.isExecutionType()) {
                // Create a new sequence execution for this thing
                SequenceExecution execution = new SequenceExecution(kchild);
                execution.setType(SequenceExecutionType.fromNodeType(kchildNodeType));
                executions.add(execution);
                executionIdMap.put(
                        kchildLayout.getProperty(SequenceDiagramOptions.ELEMENT_ID), execution);
            } else if (kchildNodeType == NodeType.DESTRUCTION_EVENT) {
                slifeline.setProperty(SequenceDiagramOptions.DESTRUCTION_NODE, kchild);
            }
        }
        
        slifeline.setProperty(SequenceDiagramOptions.EXECUTIONS, executions);
        
        // Check if the lifeline has any empty areas
        List<Integer> areaIds = klayout.getProperty(SequenceDiagramOptions.AREA_IDS);
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
     *            the KNode to search its outgoing edges
     */
    private void createOutgoingMessages(final SGraph sgraph, final KNode klifeline) {
        for (KEdge kedge : klifeline.getOutgoingEdges()) {
            SLifeline sourceLL = lifelineMap.get(kedge.getSource());
            SLifeline targetLL = lifelineMap.get(kedge.getTarget());

            // Lost-messages and messages to the surrounding interaction don't have a lifeline, so
            // create dummy lifeline
            if (targetLL == null) {
                SLifeline sdummy = new SLifeline();
                sdummy.setDummy(true);
                sdummy.setGraph(sgraph);
                targetLL = sdummy;
            }

            // Create message object
            SMessage smessage = new SMessage(sourceLL, targetLL);
            smessage.setProperty(InternalProperties.ORIGIN, kedge);

            KEdgeLayout kedgelayout = kedge.getData(KEdgeLayout.class);
            smessage.setSourceYPos(kedgelayout.getSourcePoint().getY());
            smessage.setTargetYPos(kedgelayout.getTargetPoint().getY());

            // Read size of the attached labels
            double maxLabelLength = 0;
            for (KLabel klabel : kedge.getLabels()) {
                KShapeLayout klabelLayout = klabel.getData(KShapeLayout.class);
                if (klabelLayout.getWidth() > maxLabelLength) {
                    maxLabelLength = klabelLayout.getWidth();
                }
            }
            smessage.setLabelWidth(maxLabelLength);

            // Add message to the source and the target lifeline's list of messages
            sourceLL.addMessage(smessage);
            targetLL.addMessage(smessage);

            // Put edge and message into the edge map
            messageMap.put(kedge, smessage);
            
            // Check if the edge connects to executions
            List<Integer> sourceExecutionIds =
                    kedgelayout.getProperty(SequenceDiagramOptions.SOURCE_EXECUTION_IDS);
            smessage.setProperty(SequenceDiagramOptions.SOURCE_EXECUTION_IDS, sourceExecutionIds);
            for (Integer execId : sourceExecutionIds) {
                SequenceExecution sourceExecution = executionIdMap.get(execId);
                if (sourceExecution != null) {
                    sourceExecution.addMessage(smessage);
                }
            }
            
            List<Integer> targetExecutionIds =
                    kedgelayout.getProperty(SequenceDiagramOptions.TARGET_EXECUTION_IDS);
            smessage.setProperty(SequenceDiagramOptions.TARGET_EXECUTION_IDS, sourceExecutionIds);
            for (Integer execId : targetExecutionIds) {
                SequenceExecution targetExecution = executionIdMap.get(execId);
                if (targetExecution != null) {
                    targetExecution.addMessage(smessage);
                }
            }

            // Append the message type of the edge to the message
            MessageType messageType = kedgelayout.getProperty(SequenceDiagramOptions.MESSAGE_TYPE);
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
            for (Integer areaId : kedgelayout.getProperty(SequenceDiagramOptions.AREA_IDS)) {
                SequenceArea area = areaIdMap.get(areaId);
                if (area != null) {
                    area.getMessages().add(smessage);
                    area.getLifelines().add(smessage.getSource());
                    area.getLifelines().add(smessage.getTarget());
                }
                // TODO Possibly throw an exception in the else case
            }
            
            // Check if this message has an empty area that is to be placed directly above it
            int upperEmptyAreaId = kedgelayout.getProperty(
                    SequenceDiagramOptions.UPPER_EMPTY_AREA_ID);
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
     *            the KNode to search its incoming edges.
     */
    private void createIncomingMessages(final SGraph sgraph, final KNode klifeline) {
        for (KEdge kedge : klifeline.getIncomingEdges()) {
            KEdgeLayout kedgelayout = kedge.getData(KEdgeLayout.class);

            SLifeline sourceLL = lifelineMap.get(kedge.getSource());
            
            // We are only interested in messages that don't come from a lifeline
            if (sourceLL != null) {
                continue;
            }
            
            // TODO consider connections to comments and constraints!
            
            // Create dummy lifeline as source since the message has no source lifeline
            // TODO We could think about using a single dummy lifeline for all found messages
            SLifeline sdummy = new SLifeline();
            sdummy.setDummy(true);
            sdummy.setGraph(sgraph);
            sourceLL = sdummy;
            
            SLifeline targetLL = lifelineMap.get(kedge.getTarget());

            // Create message object
            SMessage smessage = new SMessage(sourceLL, targetLL);
            smessage.setProperty(InternalProperties.ORIGIN, kedge);
            smessage.setTargetYPos(kedgelayout.getTargetPoint().getY());

            // Add the message to the source and target lifeline's list of messages
            sourceLL.addMessage(smessage);
            targetLL.addMessage(smessage);

            // Put edge and message into the edge map
            messageMap.put(kedge, smessage);

            // Append the message type of the edge to the message
            MessageType messageType = kedgelayout.getProperty(SequenceDiagramOptions.MESSAGE_TYPE);
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
            List<Integer> targetExecutionIds =
                    kedgelayout.getProperty(SequenceDiagramOptions.TARGET_EXECUTION_IDS);
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
    private void createCommentLikeNode(final SGraph sgraph, final KNode node) {
        KShapeLayout commentLayout = node.getData(KShapeLayout.class);

        // Get the node's type
        NodeType nodeType = commentLayout.getProperty(SequenceDiagramOptions.NODE_TYPE);

        // Create comment object
        SComment comment = new SComment();
        comment.setProperty(InternalProperties.ORIGIN, node);
        comment.setProperty(SequenceDiagramOptions.NODE_TYPE, nodeType);
        comment.setProperty(SequenceDiagramOptions.ATTACHED_ELEMENT_TYPE,
                commentLayout.getProperty(SequenceDiagramOptions.ATTACHED_ELEMENT_TYPE));
        
        // Attach connected edge to comment
        if (!node.getOutgoingEdges().isEmpty()) {
            comment.setProperty(InternalSequenceProperties.COMMENT_CONNECTION,
                    node.getOutgoingEdges().get(0));
        }

        // Copy all the entries of the list of attached elements to the comment object
        List<Object> attachedTo = commentLayout.getProperty(
                SequenceDiagramOptions.ATTACHED_OBJECTS);
        if (attachedTo != null) {
            List<SGraphElement> attTo = comment.getAttachedTo();
            for (Object att : attachedTo) {
                if (att instanceof KNode) {
                    attTo.add(lifelineMap.get(att));
                } else if (att instanceof KEdge) {
                    attTo.add(messageMap.get(att));
                }
            }
        }

        // Copy layout information
        comment.getPosition().x = commentLayout.getXpos();
        comment.getPosition().y = commentLayout.getYpos();
        comment.getSize().x = commentLayout.getWidth();
        comment.getSize().y = commentLayout.getHeight();

        // Handle time observations
        if (nodeType == NodeType.TIME_OBSERVATION) {
            comment.getSize().x = 
                    sgraph.getProperty(SequenceDiagramOptions.TIME_OBSERVATION_WIDTH);

            // Find lifeline that is next to the time observation
            SLifeline nextLifeline = null;
            double smallestDistance = Double.MAX_VALUE;
            for (SLifeline lifeline : sgraph.getLifelines()) {
                double distance = Math.abs((lifeline.getPosition().x + lifeline.getSize().x / 2)
                        - (commentLayout.getXpos() + commentLayout.getWidth() / 2));
                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    nextLifeline = lifeline;
                }
            }

            // Find message on the calculated lifeline that is next to the time observation
            SMessage nextMessage = null;
            smallestDistance = Double.MAX_VALUE;
            for (SMessage message : nextLifeline.getMessages()) {
                KEdge edge = (KEdge) message.getProperty(InternalProperties.ORIGIN);
                KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
                double distance;
                if (message.getSource() == nextLifeline) {
                    distance = Math.abs((edgeLayout.getSourcePoint().getY())
                            - (commentLayout.getYpos() + commentLayout.getHeight() / 2));
                } else {
                    distance = Math.abs((edgeLayout.getTargetPoint().getY())
                            - (commentLayout.getYpos() + commentLayout.getHeight() / 2));
                }
                
                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    nextMessage = message;
                }
            }

            // Set both, lifeline and message of the comment to indicate that it should be drawn
            // near to the event
            comment.setLifeline(nextLifeline);
            comment.setMessage(nextMessage);
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
