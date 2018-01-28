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
package org.eclipse.elk.alg.sequence.graph;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A lifeline is one of the central components of a sequence diagram. This 
 * Lifeline representation for SGraphs. Lifelines can be compared to other lifelines on the basis of
 * their x coordinate. This is used in the interactive lifeline sorter.
 */
public final class SLifeline extends SShape implements Comparable<SLifeline> {
    
    private static final long serialVersionUID = 1309361361029991404L;
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties
    
    /** The owning graph. */
    private final SGraph graph;
    /** Whether this is a dummy lifeline or not. */
    private final boolean dummy;
    /** The name of the lifeline. */
    private String name = "L";
    /**
     * Lifelines are ordered one after another at the top of the diagram. This is the position of
     * the lifeline in this line.
     */
    // TODO: This should be replaced by the lifeline's position in the graph's list of lifelines.
    private int horizontalSlot;
    /**
     * The list of connected messages. A lifeline may be the source or the target of a message. Both
     * kind of messages, incoming and outgoing, are collected in this list. This list is sorted
     * top-down according to the corresponding connection point of the message. Self messages appear
     * twice.
     */
    private List<SMessage> messages = Lists.newArrayList();
    /** The number of outgoing messages. */
    private int outgoingMessageCount = 0;
    /** The number of incoming messages. */
    private int incomingMessageCount = 0;
    /** The list of comments that are drawn near to this lifeline. */
    // TODO: Convert to a Set?
    private List<SComment> comments = Lists.newArrayList();
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction
    
    /**
     * Creates a new instance.
     * 
     * @param graph
     *            the graph the lifeline belongs to.
     * @param dummy
     *            {@code true} if the lifeline is a dummy lifeline.
     */
    private SLifeline(final SGraph graph, final boolean dummy) {
        this.graph = graph;
        this.dummy = dummy;
    }
    
    /**
     * Creates a new lifeline with the given parent graph. Adds the lifeline to the graph.
     * 
     * @param graph
     *            the lifeline's parent graph.
     * @return the new lifeline.
     */
    public static SLifeline createLifeline(final SGraph graph) {
        SLifeline lifeline = new SLifeline(graph, false);
        graph.addLifeline(lifeline);
        return lifeline;
    }
    
    /**
     * Creates a new dummy lifeline with the given parent graph. Adds the lifeline to the graph.
     * 
     * @param graph
     *            the lifeline's parent graph.
     * @return the new lifeline.
     */
    public static SLifeline createDummyLifeline(final SGraph graph) {
        SLifeline lifeline = new SLifeline(graph, true);
        graph.addLifeline(lifeline);
        return lifeline;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors

    /**
     * The graph the lifeline belongs to.
     * 
     * @return the parent graph.
     */
    public SGraph getGraph() {
        return graph;
    }
    
    /**
     * Checks whether this lifeline is a dummy lifeline.
     * 
     * @return {@code true} if this is a dummy lifeline.
     */
    public boolean isDummy() {
        return this.dummy;
    }

    /**
     * Get the lifeline's name.
     * 
     * @return the lifeline's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the lifeline's name.
     * 
     * @param name
     *            the lifeline's new name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the number of the horizontal slot of the lifeline.
     * 
     * @return the number of the slot.
     */
    public int getHorizontalSlot() {
        return horizontalSlot;
    }

    /**
     * Set the number of the horizontal slot of the lifeline.
     * 
     * @param horizontalSlot
     *            the new slot number.
     */
    public void setHorizontalSlot(final int horizontalSlot) {
        this.horizontalSlot = horizontalSlot;
    }

    /**
     * Get the list of all messages connected to the lifeline. The list is sorted top-down according
     * to the vertical position of the connection points of the messages.
     * 
     * @return the list of messages.
     */
    public List<SMessage> getMessages() {
        return messages;
    }

    /**
     * Get the list of comments that will be drawn near to this lifeline.
     * 
     * @return the list of comments.
     */
    public List<SComment> getComments() {
        return comments;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Message Management

    /**
     * Add a message to the list of messages. The list of messages is sorted according to their
     * vertical connection point on the lifeline. This method requires that the message's source /
     * target has already been set, and that the y positions of its end points are already set.
     * 
     * @param newMsg
     *            the message to add.
     */
    public void addMessage(final SMessage newMsg) {
        // Count the message (both cases may be true in the case of self message)
        if (newMsg.getSource() == this) {
            outgoingMessageCount++;
        }
        
        if (newMsg.getTarget() == this) {
            incomingMessageCount++;
        }
        
        // Get the position of the message at this lifeline
        double newMsgYPos = newMsg.getSource() == this ? newMsg.getSourceYPos() : newMsg.getTargetYPos();
        
        // Insert the message just before the first message with a greater y position
        ListIterator<SMessage> iterator = messages.listIterator();
        while (iterator.hasNext()) {
            SMessage currMsg = iterator.next();
            double currMsgYPos = currMsg.getSource() == this ? currMsg.getSourceYPos() : currMsg.getTargetYPos();
            
            if (newMsgYPos < currMsgYPos) {
                iterator.previous();
                iterator.add(newMsg);
                return;
            }
        }
        
        // If we get here we haven't added the message yet
        messages.add(newMsg);
    }

    /**
     * Get the list of outgoing messages of the lifeline. The list is sorted top-down according to
     * the vertical position of the connection points of the messages.
     * 
     * @return an iterable of outgoing messages.
     */
    public Iterable<SMessage> getOutgoingMessages() {
        return Iterables.filter(messages, new Predicate<SMessage>() {
            private HashSet<SMessage> selfMessages = new HashSet<SMessage>();
            public boolean apply(final SMessage message) {
                // Workaround for self messages that are returned twice if just checking the source
                if (message.getSource() == message.getTarget()) {
                    if (selfMessages.contains(message)) {
                        return false;
                    } else {
                        selfMessages.add(message);
                        return true;
                    }
                }
                return message.getSource() == SLifeline.this;
            }
        });
    }

    /**
     * Get the number of outgoing messages.
     * 
     * @return the number of outgoing messages
     */
    public int getNumberOfOutgoingMessages() {
        return outgoingMessageCount;
    }

    /**
     * Get the list of incoming messages of the lifeline. The list is sorted top-down according to
     * the vertical position of the connection points of the messages.
     * 
     * @return an iterable of incoming messages.
     */
    public Iterable<SMessage> getIncomingMessages() {
        return Iterables.filter(messages, new Predicate<SMessage>() {
            private HashSet<SMessage> selfMessages = new HashSet<SMessage>();
            public boolean apply(final SMessage message) {
                // Workaround for self messages that are returned twice if just checking the target
                if (message.getSource() == message.getTarget()) {
                    if (selfMessages.contains(message)) {
                        return false;
                    } else {
                        selfMessages.add(message);
                        return true;
                    }
                }
                return message.getTarget() == SLifeline.this;
            }
        });
    }

    /**
     * Get the number of incoming messages.
     * 
     * @return the number of incoming messages.
     */
    public int getNumberOfIncomingMessages() {
        return incomingMessageCount;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Comparable

    /**
     * Compare lifelines by their horizontal position. This is used by the interactive lifeline
     * sorter to sort the lifelines by this value.
     */
    @Override
    public int compareTo(final SLifeline other) {
        if (getPosition().x < other.getPosition().x) {
            return -1;
        } else if (getPosition().x > other.getPosition().x) {
            return 1;
        }
        return 0;
    }
}