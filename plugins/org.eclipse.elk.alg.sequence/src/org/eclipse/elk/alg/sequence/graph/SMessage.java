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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.core.math.KVector;

/**
 * Message representation for SGraphs. Messages are created with their source and target lifeline as
 * arguments. These cannot be changed afterwards. A message has a source and a target point. These
 * points may have different vertical positions since asynchronous messages are not required to be
 * horizontal.
 */
public final class SMessage extends SGraphElement {
    
    private static final long serialVersionUID = 6326794211792613083L;
    
    /** The value of a message layer that has not been determined yet. */
    public static final int MESSAGE_LAYER_UNDEFINED = -1;
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties
    
    /** The source lifeline of the message. */
    private final SLifeline sourceLifeline;
    /** The target lifeline of the message. This is set initially and cannot be modified. */
    private final SLifeline targetLifeline;
    /** The message's source point, relative to the top left corner of the surrounding interaction. */
    private KVector sourcePos = new KVector();
    /** The message's target point, relative to the top left corner of the surrounding interaction. */
    private KVector targetPos = new KVector();
    /** The list of bend points to route the edge. */
    private final List<KVector> bendPoints = new ArrayList<>(2);
    /** The width of the message's label. This is important for handling long labels. */
    private SLabel label;
    /** The list of execution specifications the message originates from. */
    private final List<SExecution> sourceExecutions = new ArrayList<>();
    /** The list of execution specifications the message heads off to. */
    private final List<SExecution> targetExecutions = new ArrayList<>();
    /** The list of comments that will be drawn near to this message. */
    private final List<SComment> comments = new ArrayList<>();
    
    // TODO Remove
    private double sourceYPos = -1;
    private double targetYPos = -1;
    
    
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
        this.sourceLifeline = source;
        this.targetLifeline = target;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors

    /**
     * Get the source lifeline of the message.
     * 
     * @return the source lifeline
     */
    public SLifeline getSourceLifeline() {
        return sourceLifeline;
    }

    /**
     * Get the target lifeline of the message.
     * 
     * @return the target lifeline
     */
    public SLifeline getTargetLifeline() {
        return targetLifeline;
    }
    
    /**
     * Returns the message's source position.
     * 
     * @return the source position that can be modified.
     */
    public KVector getSourcePosition() {
        return sourcePos;
    }
    
    /**
     * Returns the message's target position.
     * 
     * @return the target position that can be modified.
     */
    public KVector getTargetPosition() {
        return targetPos;
    }
    
    /**
     * Returns the message's list of bend points.
     * 
     * @return the list of bend points that can be modified.
     */
    public List<KVector> getBendPoints() {
        return bendPoints;
    }

    /**
     * Get the vertical position at the source lifeline of the message.
     * 
     * @return the vertical position at the source lifeline.
     * @deprecated
     */
    public double getSourceYPos() {
        return sourceYPos;
    }

    /**
     * Set the vertical position at the source lifeline of the message.
     * 
     * @param sourceYPos
     *            the new vertical position at the source lifeline.
     * @deprecated
     */
    public void setSourceYPos(final double sourceYPos) {
        this.sourceYPos = sourceYPos;
    }

    /**
     * Get the vertical position at the target lifeline of the message.
     * 
     * @return the vertical position at the target lifeline.
     * @deprecated
     */
    public double getTargetYPos() {
        return targetYPos;
    }

    /**
     * Set the vertical position at the target lifeline of the message.
     * 
     * @param targetYPos
     *            the new vertical position at the target lifeline.
     * @deprecated
     */
    public void setTargetYPos(final double targetYPos) {
        this.targetYPos = targetYPos;
    }

    /**
     * Returns the list of execution specifications the message originates at.
     */
    public List<SExecution> getSourceExecutions() {
        return sourceExecutions;
    }

    /**
     * Returns the list of execution specifications the message heads off to.
     */
    public List<SExecution> getTargetExecutions() {
        return targetExecutions;
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
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utilities
    
    /**
     * Checks whether this message is a self message.
     * 
     * @return {@code true} if the source lifeline equals the target lifeline.
     */
    public boolean isSelfMessage() {
        return sourceLifeline == targetLifeline;
    }
    
    /**
     * Checks whether the message connects two adjacent lifelines.
     */
    public boolean connectsAdjacentLifelines() {
        return Math.abs(sourceLifeline.getHorizontalSlot() - targetLifeline.getHorizontalSlot()) == 1;
    }
    
    /**
     * Checks if this message spans at least one of the same layers as the other message. This method requires the
     * source and target horizontal slots of both messages to be set.
     */
    public boolean overlaps(final SMessage other) {
        int sourceSlot = this.getSourceLifeline().getHorizontalSlot();
        int targetSlot = this.getTargetLifeline().getHorizontalSlot();
        int otherSourceSlot = other.getSourceLifeline().getHorizontalSlot();
        int otherTargetSlot = other.getTargetLifeline().getHorizontalSlot();
        
        return isBetween(sourceSlot, otherSourceSlot, otherTargetSlot)
                || isBetween(targetSlot, otherSourceSlot, otherTargetSlot)
                || isBetween(otherSourceSlot, sourceSlot, targetSlot)
                || isBetween(otherTargetSlot, sourceSlot, targetSlot);
    }
    
    private boolean isBetween(final int x, final int bound1, final int bound2) {
        // x is between 1 and 2 if it is not smaller than both or greater than both
        return !((x <= bound1 && x <= bound2) || (x >= bound1 && x >= bound2));
    }
    
}
