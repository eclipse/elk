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

import org.eclipse.elk.core.math.KVector;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Lifeline representation for SGraphs. Lifelines can be compared to other lifelines on the basis of
 * their x coordinate. This is used in the interactive lifeline sorter.
 * 
 * @author grh
 * @kieler.design 2012-11-20 cds, msp
 * @kieler.rating yellow 2012-12-11 cds, ima
 */
public final class SLifeline extends SGraphElement implements Comparable<SLifeline> {
    private static final long serialVersionUID = 1309361361029991404L;
    
    /** The owning graph. */
    private SGraph graph;
    /** The name of the lifeline. */
    private String name = "L";
    /** Whether this is a dummy lifeline or not. */
    private boolean dummy = false;
    /**
     * Lifelines are ordered one after another at the top of the diagram. This is the position of
     * the lifeline in this line.
     */
    private int horizontalSlot;
    /** The coordinates of the lifeline in the diagram. */
    private KVector position = new KVector();
    /** The size of the lifeline. */
    private KVector size = new KVector();
    /**
     * The list of connected messages. A lifeline may be the source or the target of a message. Both
     * kind of messages, incoming and outgoing, are collected in this list. This list is sorted
     * top-down according to the corresponding connection point of the message.
     */
    private List<SMessage> messages = Lists.newArrayList();
    /** The list of comments that are drawn near to this lifeline. */
    private List<SComment> comments = Lists.newArrayList(); // TODO: Convert to a Set?
    

    /**
     * Get the SGraph to which the lifeline belongs.
     * 
     * @return the owning SGraph
     */
    public SGraph getGraph() {
        return graph;
    }

    /**
     * Set the SGraph to which the lifeline belongs.
     * 
     * @param graph
     *            the new owning SGraph
     */
    public void setGraph(final SGraph graph) {
        this.graph = graph;
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
     * Determines whether this shall be a dummy lifeline or not.
     * 
     * @param dummy {@code true} if this lifeline should be a dummy lifeline.
     */
    public void setDummy(final boolean dummy) {
        this.dummy = dummy;
    }

    /**
     * Add a message to the list of messages. The list of messages is sorted according to their
     * vertical connection point to the lifeline. This method requires that the message's source /
     * target has already been set which is normally done by the SMessage's constructor.
     * 
     * @param msg
     *            the message to add
     */
    public void addMessage(final SMessage msg) {
        // Get the position of the message at this lifeline
        double messageYPos = msg.getSource() == this ? msg.getSourceYPos() : msg.getTargetYPos();
        
        // Insert the message just before the first message with a greater y position
        for (int i = 0; i < messages.size(); i++) {
            // Get the position of the current message in the list
            SMessage currMsg = messages.get(i);
            double currYPos =
                    currMsg.getSource() == this ? currMsg.getSourceYPos() : currMsg.getTargetYPos();
            
            // Since messages are already sorted, the place of the first message with greater
            // position is the right place for the message.
            if (messageYPos < currYPos) {
                messages.add(i, msg);
                return;
            }
        }
        
        // Add the message at the end if none of the existing messages has greater position
        messages.add(msg);
    }

    /**
     * Get the list of outgoing messages of the lifeline. The list is sorted top-down according to
     * the vertical position of the connection points of the messages.
     * 
     * @return an iterable of outgoing messages
     */
    public Iterable<SMessage> getOutgoingMessages() {
        final SLifeline lifeline = this;
        return Iterables.filter(messages, new Predicate<SMessage>() {
            private HashSet<SMessage> selfloops = new HashSet<SMessage>();
            public boolean apply(final SMessage message) {
                // Workaround for selfloop messages that are returned twice if just checking the source
                if (message.getSource() == message.getTarget()) {
                    if (selfloops.contains(message)) {
                        return false;
                    } else {
                        selfloops.add(message);
                        return true;
                    }
                }
                return message.getSource() == lifeline;
            }
        });
    }

    /**
     * Get the number of outgoing messages.
     * 
     * @return the number of outgoing messages
     */
    public int getNumberOfOutgoingMessages() {
        int ret = 0;
        for (SMessage message : messages) {
            if (message.getSource() == this) {
                ret++;
            }
        }
        return ret;
    }

    /**
     * Get the list of incoming messages of the lifeline. The list is sorted top-down according to
     * the vertical position of the connection points of the messages.
     * 
     * @return an iterable of incoming messages
     */
    public Iterable<SMessage> getIncomingMessages() {
        final SLifeline lifeline = this;
        return Iterables.filter(messages, new Predicate<SMessage>() {
            private HashSet<SMessage> selfloops = new HashSet<SMessage>();
            public boolean apply(final SMessage message) {
                // Workaround for selfloop messages that are returned twice if just checking the target
                if (message.getSource() == message.getTarget()) {
                    if (selfloops.contains(message)) {
                        return false;
                    } else {
                        selfloops.add(message);
                        return true;
                    }
                }
                return message.getTarget() == lifeline;
            }
        });
    }

    /**
     * Get the number of incoming messages. The list is sorted top-down according to the vertical
     * position of the connection points of the messages.
     * 
     * @return the number of incoming messages
     */
    public int getNumberOfIncomingMessages() {
        int ret = 0;
        for (SMessage message : messages) {
            if (message.getTarget() == this) {
                ret++;
            }
        }
        return ret;
    }

    /**
     * Get the list of all messages connected to the lifeline. The list is sorted top-down according
     * to the vertical position of the connection points of the messages.
     * 
     * @return the list of messages
     */
    public List<SMessage> getMessages() {
        return messages;
    }

    /**
     * Get the list of comments that will be drawn near to this lifeline.
     * 
     * @return the list of comments
     */
    public List<SComment> getComments() {
        return comments;
    }

    /**
     * Set the list of comments that will be drawn near to this lifeline.
     * 
     * @param comments
     *            the new list of comments
     */
    public void setComments(final List<SComment> comments) {
        this.comments = comments;
    }

    /**
     * Get the name of the lifeline.
     * 
     * @return the name of the lifeline
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name of the lifeline.
     * 
     * @param name
     *            the new name of the lifeline
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the number of the horizontal slot of the lifeline.
     * 
     * @return the number of the slot
     */
    public int getHorizontalSlot() {
        return horizontalSlot;
    }

    /**
     * Set the number of the horizontal slot of the lifeline.
     * 
     * @param horizontalSlot
     *            the new slot number
     */
    public void setHorizontalSlot(final int horizontalSlot) {
        this.horizontalSlot = horizontalSlot;
    }

    /**
     * Get the position of the lifeline.
     * 
     * @return the Vector with the position
     */
    public KVector getPosition() {
        return position;
    }

    /**
     * Get the size of the lifeline.
     * 
     * @return the Vector with the size
     */
    public KVector getSize() {
        return size;
    }

    /**
     * Compare lifelines by their horizontal position. This is used by the interactive lifeline
     * sorter to sort the lifelines by this value. {@inheritDoc}
     */
    public int compareTo(final SLifeline other) {
        if (getPosition().x < other.getPosition().x) {
            return -1;
        } else if (getPosition().x > other.getPosition().x) {
            return 1;
        }
        return 0;
    }
}
