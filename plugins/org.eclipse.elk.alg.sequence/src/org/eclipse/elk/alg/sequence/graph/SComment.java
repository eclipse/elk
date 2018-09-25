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

import org.eclipse.elk.alg.sequence.options.MessageCommentAlignment;

/**
 * Represents a comment-like object in the sequence graph. Comment-like objects are comments (duh...), constraints,
 * time observations, and duration observations.
 */
public final class SComment extends SShape {
    
    private static final long serialVersionUID = 2543686433908319587L;
    
    /** If the comment is attached to anything, this is the lifeline it will be drawn next to. */
    private SLifeline referenceLifeline;
    /** If the comment is attached to anything, this is the message it will be drawn next to. */
    private SMessage referenceMessage;
    /** Where a comment will be aligned along a message. */
    private MessageCommentAlignment alignment = MessageCommentAlignment.SOURCE;
    /** The label that contains the comment's text, if we know about any. */
    private SLabel label;


    /**
     * Get the lifeline next to which the comment will be placed.
     * 
     * @return the reference lifeline, or {@code null} if there is none.
     */
    public SLifeline getLifeline() {
        return referenceLifeline;
    }

    /**
     * Set the lifeline next to which the comment will be placed.
     * 
     * @param lifeline
     *            the new lifeline
     */
    public void setLifeline(final SLifeline lifeline) {
        // Delete comment from the old lifeline's comments list
        if (this.referenceLifeline != null) {
            this.referenceLifeline.getComments().remove(this);
        }

        this.referenceLifeline = lifeline;

        // Add comment to the new lifeline's comments list
        if (lifeline != null) {
            lifeline.getComments().add(this);
        }
    }

    /**
     * Get the message next to which the comment will be placed.
     * 
     * @return the reference message, or {@code null} if there is none.
     */
    public SMessage getReferenceMessage() {
        return referenceMessage;
    }

    /**
     * Set the message next to which the comment will be placed.
     * 
     * @param message
     *            the new message.
     */
    public void setReferenceMessage(final SMessage message) {
        // Delete comment from the old message's comments list
        if (this.referenceMessage != null) {
            this.referenceMessage.getComments().remove(this);
        }

        this.referenceMessage = message;

        // Add comment to the new message's set of comments
        if (message != null) {
            message.getComments().add(this);
        }
    }
    
    /**
     * Returns the comment's alignment if {@link #getReferenceMessage()} returns a non-{@code null} value.
     * 
     * @return the alignment.
     */
    public MessageCommentAlignment getAlignment() {
        return alignment;
    }

    /**
     * Determines how the comment should be aligned if it refers to a message.
     * 
     * @param alignment
     *            the alignment.
     */
    public void setAlignment(final MessageCommentAlignment alignment) {
        this.alignment = alignment;
    }

    /**
     * Returns this comment's label, if any.
     * 
     * @return the label.
     */
    public SLabel getLabel() {
        return label;
    }

    /**
     * Sets this comment's label.
     * 
     * @param label
     *            the label.
     */
    public void setLabel(final SLabel label) {
        this.label = label;
    }
    
}
