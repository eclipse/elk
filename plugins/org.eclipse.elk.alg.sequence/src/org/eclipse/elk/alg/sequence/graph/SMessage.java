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

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Message representation for SGraphs. Messages are created with their source and target lifeline as
 * arguments. These cannot be changed afterwards. A message has a source and a target point. These
 * points may have different vertical positions since asynchronous messages are not required to be
 * horizontal.
 * 
 * @author grh
 */
public final class SMessage extends SGraphElement {
    private static final long serialVersionUID = 6326794211792613083L;
    /** The value of a position that has not been determined yet. */
    public static final double POSITION_UNSET = -1.0;
    
    /** The source lifeline of the message. This is set initially and cannot be modified. */
    private SLifeline source;
    /** The target lifeline of the message. This is set initially and cannot be modified. */
    private SLifeline target;
    /** The vertical position of the source point of the message. */
    private double sourceYPos = POSITION_UNSET;
    /** The vertical position of the target point of the message. */
    private double targetYPos = POSITION_UNSET;
    /** The number of the layer that the message is in. */
    private int messageLayer;   // TODO: What layer are asynchronous messages in?
    /** Whether the layer of this message was already set. */
    private boolean isLayerPositionSet = false;
    /** The width of the message's label. This is important for handling long labels. */
    private double labelWidth;
    /** The list of comments that will be drawn near to this message. */
    private List<SComment> comments = Lists.newArrayList();  // TODO: Convert to a Set?

    
    /**
     * Creates a new message connecting the given two lifelines.
     * 
     * @param source
     *            the source lifeline
     * @param target
     *            the target lifeline
     */
    public SMessage(final SLifeline source, final SLifeline target) {
        this.source = source;
        this.target = target;
    }
    

    /**
     * Get the source lifeline of the message.
     * 
     * @return the source lifeline
     */
    public SLifeline getSource() {
        return source;
    }

    /**
     * Get the target lifeline of the message.
     * 
     * @return the target lifeline
     */
    public SLifeline getTarget() {
        return target;
    }

    /**
     * Get the list of comments that will be drawn near to this message.
     * 
     * @return the list of comments
     */
    public List<SComment> getComments() {
        return comments;
    }

    /**
     * Set the list of comments that will be drawn near to this message.
     * 
     * @param comments
     *            the new list of comments
     */
    public void setComments(final List<SComment> comments) {
        this.comments = comments;
    }

    /**
     * Get the width of the label attached to the message.
     * 
     * @return the width of the label
     */
    public double getLabelWidth() {
        return labelWidth;
    }

    /**
     * Set the width of the label attached to the message.
     * 
     * @param labelWidth
     *            the new width of the label
     */
    public void setLabelWidth(final double labelWidth) {
        this.labelWidth = labelWidth;
    }

    /**
     * Get the layer number of the message.
     * 
     * @return the layer number
     */
    public int getMessageLayer() {
        return messageLayer;
    }

    /**
     * Set the layer number of the message.
     * 
     * @param messageLayer
     *            the new number of the layer
     */
    public void setMessageLayer(final int messageLayer) {
        this.messageLayer = messageLayer;
    }

    /**
     * If the layer of this message was already set.
     * 
     * @return {@code true} if the layer was already set
     */
    public boolean isLayerPositionSet() {
        return isLayerPositionSet;
    }

    /**
     * Set the vertical position of the layer of the message. This implicitly sets the source and
     * target y position as well.
     * 
     * @param yPosition
     *            the new vertical position
     */
    public void setLayerYPos(final double yPosition) {
        sourceYPos = yPosition;
        targetYPos = yPosition;
        isLayerPositionSet = true;
        
        // Update the graph's size if the message's y position is greater than the graph's size.
        // This is done in order to find the new vertical size of the diagram on the fly.
        // TODO: What about lifeline sizes?
        if (source.getGraph().getSize().y < yPosition) {
            source.getGraph().getSize().y = yPosition;
        }
    }

    /**
     * Get the vertical position at the source lifeline of the message.
     * 
     * @return the vertical position at the source lifeline
     */
    public double getSourceYPos() {
        return sourceYPos;
    }

    /**
     * Set the vertical position at the source lifeline of the message.
     * 
     * @param sourceYPos
     *            the new vertical position at the source lifeline
     */
    public void setSourceYPos(final double sourceYPos) {
        this.sourceYPos = sourceYPos;
        
        // Update the graph's size if the message's y position is greater than the graph's size.
        // This is done in order to find the new vertical size of the diagram on the fly. Lifeline
        // sizes are adjusted later.
        if (source.getGraph().getSize().y < sourceYPos) {
            source.getGraph().getSize().y = sourceYPos;
        }
    }

    /**
     * Get the vertical position at the target lifeline of the message.
     * 
     * @return the vertical position at the target lifeline
     */
    public double getTargetYPos() {
        return targetYPos;
    }

    /**
     * Set the vertical position at the target lifeline of the message.
     * 
     * @param targetYPos
     *            the new vertical position at the target lifeline
     */
    public void setTargetYPos(final double targetYPos) {
        this.targetYPos = targetYPos;
        
        // Update the graph's size if the message's y position is greater than the graph's size.
        // This is done in order to find the new vertical size of the diagram on the fly.
        // TODO: What about lifeline sizes?
        if (target.getGraph().getSize().y < targetYPos) {
            target.getGraph().getSize().y = targetYPos;
        }
    }
}
