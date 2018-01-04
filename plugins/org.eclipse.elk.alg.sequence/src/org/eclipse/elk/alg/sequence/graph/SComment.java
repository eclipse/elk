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

import org.eclipse.elk.core.math.KVector;

import com.google.common.collect.Lists;

/**
 * Comment representation for SGraphs.
 * 
 * @author grh
 */
public final class SComment extends SGraphElement {
    private static final long serialVersionUID = 2543686433908319587L;
    
    /**
     * The list of elements that the comment is attached to. A comment may have connections to
     * several different objects.
     */
    private List<SGraphElement> attachedTo = Lists.newArrayList();
    /**
     * The lifeline the comment will be drawn next to. This is only relevant if the comment is attached
     * to any diagram element in the first place.
     */
    private SLifeline lifeline;
    /**
     * The message the comment will be drawn next to. This is only relevant if the comment is attached
     * to any diagram element in the first place.
     */
    private SMessage message;
    /** The size of the comment. */
    private KVector size = new KVector();
    /** The position of the comment. */
    private KVector position = new KVector();


    /**
     * Get the size of the comment.
     * 
     * @return the KVector with the size
     */
    public KVector getSize() {
        return size;
    }

    /**
     * Get the position of the comment.
     * 
     * @return the KVector with the position
     */
    public KVector getPosition() {
        return position;
    }

    /**
     * Get the SGraphElement to which the comment is attached to.
     * 
     * @return the connected element
     */
    public List<SGraphElement> getAttachedTo() {
        return attachedTo;
    }

    /**
     * Get the message near to which the comment will be drawn if existing.
     * 
     * @return the SMessage near to the comment or null if not existing
     */
    public SMessage getMessage() {
        return message;
    }

    /**
     * Set the message near to which the comment will be drawn.
     * 
     * @param message
     *            the new message
     */
    public void setMessage(final SMessage message) {
        // Delete comment from the old message's comments list
        if (this.message != null) {
            this.message.getComments().remove(this);
        }

        this.message = message;

        // Add comment to the new message's comments list
        if (message != null) {
            List<SComment> comments = message.getComments();
            // TODO: If the comments are converted to a set instead of a list, we can skip contains
            if (!comments.contains(this)) {
                comments.add(this);
            }
        }
    }

    /**
     * Get the lifeline near to which the comment will be drawn if existing.
     * 
     * @return the SLifeline near to the comment or null if not existing
     */
    public SLifeline getLifeline() {
        return lifeline;
    }

    /**
     * Set the lifeline near to which the comment will be drawn.
     * 
     * @param lifeline
     *            the new lifeline
     */
    public void setLifeline(final SLifeline lifeline) {
        // Delete comment from the old lifeline's comments list
        if (this.lifeline != null) {
            this.lifeline.getComments().remove(this);
        }

        this.lifeline = lifeline;

        // Add comment to the new lifeline's comments list
        if (lifeline != null) {
            List<SComment> comments = lifeline.getComments();
            // TODO: If the comments are converted to a set instead of a list, we can skip contains
            if (!comments.contains(this)) {
                comments.add(this);
            }
        }
    }
}
