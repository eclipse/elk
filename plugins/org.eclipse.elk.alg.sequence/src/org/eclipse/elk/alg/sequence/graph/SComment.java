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

/**
 * Represents a comment-like object in the sequence graph. Comment-like objects are comments (duh...), constraints,
 * time observations, and duration observations.
 */
public final class SComment extends SShape {
    
    private static final long serialVersionUID = 2543686433908319587L;
    
    /** The element that the comment is attached to. */
    private SGraphElement attachment;
    /** If the comment is attached to anything, this is the lifeline it will be drawn next to. */
    private SLifeline referenceLifeline;
    /** If the comment is attached to anything, this is the message it will be drawn next to. */
    private SMessage referenceMessage;


    /**
     * The graph element the comment is attached to.
     * 
     * @return the (possibly {@code null}) attached element.
     */
    public SGraphElement getAttachment() {
        return attachment;
    }
    
    /**
     * Set the element to which the comment is attached.
     * 
     * @param element
     *            the new element.
     */
    public void setAttachment(SGraphElement element) {
        this.attachment = element;
    }

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
            List<SComment> comments = lifeline.getComments();
            // TODO: If the comments are converted to a set instead of a list, we can skip contains
            if (!comments.contains(this)) {
                comments.add(this);
            }
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

        // Add comment to the new message's comments list
        if (message != null) {
            List<SComment> comments = message.getComments();
            // TODO: If the comments are converted to a set instead of a list, we can skip contains
            if (!comments.contains(this)) {
                comments.add(this);
            }
        }
    }
}
