/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.labeling;

import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopLabel;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

/**
 * Describes a (possible) position of a {@link SelfLoopLabel}, along with a number of properties used internally by the
 * placement algorithms.
 */
public class SelfLoopLabelPosition {

    /** The label that will be placed at this position. */
    private final SelfLoopLabel label;
    /** The side of the node where the labels will be placed at. */
    private PortSide side;
    /** The position relative to the start port position of the component of this side. */
    private final KVector position;
    /** The original position as this object was created. */
    private final KVector originalPosition;
    /** Number of crossings with other labels. */
    private int labelLabelCrossings;
    /** Number of crossings with edges. */
    private int labelEdgeCrossings;
    /** The penalty for a certain position. Higher penalties reduce the likelihood of a position being chosen. */
    private double penalty;

    /**
     * Creates a new instance.
     */
    public SelfLoopLabelPosition(final SelfLoopLabel label, final KVector position) {
        this.label = label;
        this.originalPosition = position.clone();
        this.position = position.clone();
    }

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
        this.position.reset().add(originalPosition);
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
     * Returns the number of crossings with other labels.
     */
    public int getLabelLabelCrossings() {
        return labelLabelCrossings;
    }

    /**
     * Sets the number of crossings with other labels.
     */
    public void setLabelLabelCrossings(final int labelLabelCrossings) {
        this.labelLabelCrossings = labelLabelCrossings;
    }

    /**
     * Returns the number of crossings with edges.
     */
    public int getLabelEdgeCrossings() {
        return labelEdgeCrossings;
    }

    /**
     * Sets the number of crossings with edges.
     */
    public void setLabelEdgeCrossings(final int crossings) {
        this.labelEdgeCrossings = crossings;
    }
    
    /**
     * Returns the penalty associated with this position.
     */
    public double getPenalty() {
        return penalty;
    }

    /**
     * Sets the penalty to be associated with this position.
     */
    public void setPenalty(final double penalty) {
        this.penalty = penalty;
    }

    @Override
    public String toString() {
        return side + " " + position.toString();
    }
    
}
