/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

/**
 * Describes a (possible) position of a {@link SelfLoopLabel}, along with a number of properties used internally by the
 * placement algorithms. Each position has a base penalty associated with it: the higher the base penalty, the lower
 * the probability of its label actually being placed there.
 */
public class SelfLoopLabelPosition {
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Inner Classes
    
    /**
     * How different labels will be aligned in this label position.
     */
    public static enum LabelAlignment {
        /** Align all labels at the position's left boundary. */
        LEFT,
        /** Center all labels in the position. */
        CENTERED,
        /** Align all labels at the position's right boundary. */
        RIGHT;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fields

    /** The label that will be placed at this position. */
    private final SelfLoopLabel label;
    /** The side of the node where the labels will be placed at. */
    private PortSide side;
    /** The position relative to the start port position of the component of this side. */
    private final KVector position;
    /** The original position as this object was created. */
    private final KVector originalPosition;
    /** How different labels will be aligned. */
    private LabelAlignment labelAlignment;
    /** The base penalty for a certain position, not taking crossings into account. */
    private double basePenalty;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction

    /**
     * Creates a new instance.
     */
    public SelfLoopLabelPosition(final SelfLoopLabel label, final KVector position) {
        this.label = label;
        this.originalPosition = position.clone();
        this.position = position.clone();
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors

    /**
     * Returns the self loop label this object was created for.
     */
    public SelfLoopLabel getLabel() {
        return label;
    }

    /**
     * Returns the side this label position is on.
     */
    public PortSide getSide() {
        return side;
    }

    /**
     * Sets the side this label position is on.
     */
    public void setSide(final PortSide side) {
        this.side = side;
    }
    
    /**
     * Reset the position to the original position the object was originally created with.
     */
    public void resetPosition() {
        this.position.set(originalPosition);
    }

    /**
     * Returns this position's coordinates.
     */
    public KVector getPosition() {
        return position;
    }

    /**
     * Sets this position's coordinates.
     */
    public KVector getOriginalPosition() {
        return originalPosition;
    }
    
    /**
     * Returns how different labels are to be aligned.
     */
    public LabelAlignment getLabelAlignment() {
        return labelAlignment;
    }

    /**
     * Determines how different labels are to be aligned.
     */
    public void setLabelAlignment(final LabelAlignment labelAlignment) {
        this.labelAlignment = labelAlignment;
    }
    
    /**
     * Returns the base penalty associated with this position.
     */
    public double getBasePenalty() {
        return basePenalty;
    }

    /**
     * Sets the base penalty to be associated with this position.
     */
    public void setBasePenalty(final double penalty) {
        this.basePenalty = penalty;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Miscellaneous

    @Override
    public String toString() {
        return side + " " + position.toString();
    }
    
}
