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
 */
public final class SMessage extends SGraphElement {
    
    private static final long serialVersionUID = 6326794211792613083L;
    
    /** The value of a position that has not been determined yet. */
    public static final double POSITION_UNDEFINED = -1.0;
    /** The value of a message layer that has not been determined yet. */
    public static final int MESSAGE_LAYER_UNDEFINED = -1;
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties
    
    /** The source lifeline of the message. */
    private final SLifeline source;
    /** The target lifeline of the message. This is set initially and cannot be modified. */
    private final SLifeline target;
    /** The vertical position of the source point of the message. */
    private double sourceYPos = POSITION_UNDEFINED;
    /** The vertical position of the target point of the message. */
    private double targetYPos = POSITION_UNDEFINED;
    /** The number of the layer that the message is in. */
    // TODO: What layer are asynchronous messages in?
    private int messageLayer = -1;
    /** Whether this message's layer has a y position set that has been applied to this message. */
    private boolean messageLayerPositionSet = false;
    /** The width of the message's label. This is important for handling long labels. */
    private SLabel label;
    /** The list of comments that will be drawn near to this message. */
    // TODO: Convert to a Set?
    private List<SComment> comments = Lists.newArrayList();
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction

    /**
     * Creates a new message connecting the given two lifelines. Does not add the message to the lifelines yet
     * since that will usually require the position of its end points to be set as well.
     * 
     * @param source
     *            the source lifeline.
     * @param target
     *            the target lifeline.
     */
    public SMessage(final SLifeline source, final SLifeline target) {
        this.source = source;
        this.target = target;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors

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
     * Get the vertical position at the source lifeline of the message.
     * 
     * @return the vertical position at the source lifeline.
     */
    public double getSourceYPos() {
        return sourceYPos;
    }

    /**
     * Set the vertical position at the source lifeline of the message.
     * 
     * @param sourceYPos
     *            the new vertical position at the source lifeline.
     */
    public void setSourceYPos(final double sourceYPos) {
        this.sourceYPos = sourceYPos;
        
        // Update the graph's size if the message's y position is greater than the graph's size.
        // This is done in order to find the new vertical size of the diagram on the fly. Lifeline
        // sizes are adjusted later.
        // TODO: This should be done in a processor at the end of the algorithm
        if (source.getGraph().getSize().y < sourceYPos) {
            source.getGraph().getSize().y = sourceYPos;
        }
    }

    /**
     * Get the vertical position at the target lifeline of the message.
     * 
     * @return the vertical position at the target lifeline.
     */
    public double getTargetYPos() {
        return targetYPos;
    }

    /**
     * Set the vertical position at the target lifeline of the message.
     * 
     * @param targetYPos
     *            the new vertical position at the target lifeline.
     */
    public void setTargetYPos(final double targetYPos) {
        this.targetYPos = targetYPos;
        
        // Update the graph's size if the message's y position is greater than the graph's size.
        // This is done in order to find the new vertical size of the diagram on the fly. Lifeline
        // sizes are adjusted later.
        // TODO: This should be done in a processor at the end of the algorithm
        if (target.getGraph().getSize().y < targetYPos) {
            target.getGraph().getSize().y = targetYPos;
        }
    }

    /**
     * Get the numer of the layer the message is part of.
     * 
     * @return the layer number.
     */
    public int getMessageLayer() {
        return messageLayer;
    }

    /**
     * Set the numer of the layer the message is part of.
     * 
     * @param messageLayer
     *            the new layer number.
     */
    public void setMessageLayer(final int messageLayer) {
        this.messageLayer = messageLayer;
    }

    /**
     * Check whether the message's layer has already had a position assigned to it.
     * 
     * @return {@code true} if the layer was already set.
     */
    public boolean isMessageLayerPositionSet() {
        return messageLayerPositionSet;
    }

    /**
     * Set the y coordinate of both the source and the target position and mark the position of the message's layer
     * as having been set.
     * 
     * @param yPosition
     *            the new vertical position.
     */
    public void setMessageLayerYPos(final double yPosition) {
        setSourceYPos(yPosition);
        setTargetYPos(yPosition);
        
        messageLayerPositionSet = true;
    }

    /**
     * Get the label attached to the message.
     * 
     * @return the label.
     */
    public SLabel getLabel() {
        return label;
    }

    /**
     * Set the label attached to the message.
     * 
     * @param label
     *            the new label.
     */
    public void setLabel(final SLabel label) {
        this.label = label;
    }

    /**
     * Get the list of comments that will be drawn near to this message.
     * 
     * @return the list of comments.
     */
    public List<SComment> getComments() {
        return comments;
    }
}
