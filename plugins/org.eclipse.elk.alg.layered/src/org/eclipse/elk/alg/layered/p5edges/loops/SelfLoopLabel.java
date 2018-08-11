/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.p5edges.loops.labeling.SelfLoopLabelPosition;

/**
 * A list of labels that belong to a {@link SelfLoopComponent}.
 */
public class SelfLoopLabel {

    /** The list of labels this self loop label consists of. */
    private List<LLabel> labels = new ArrayList<>();
    /** The combined height of all labels. */
    private double height;
    /** The maximum width over all labels. */
    private double width;
    /** The candidate positions for this label. */
    private List<SelfLoopLabelPosition> candidatePositions = new ArrayList<>();
    /** The position chosen for this label. */
    private SelfLoopLabelPosition labelPosition;

    /**
     * Return the list of labels represented by this self loop label.
     */
    public List<LLabel> getLabels() {
        return labels;
    }

    /**
     * Returns the combined height of all labels represented by this self loop label.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the combined height of all labels represented by this self loop label.
     */
    public void setHeight(final double height) {
        this.height = height;
    }

    /**
     * Returns the maximum width over all labels represented by this self loop label.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the maximum width over all labels represented by this self loop label.
     */
    public void setWidth(final double width) {
        this.width = width;
    }
    
    /**
     * Returns the list of label candidate positions.
     */
    public List<SelfLoopLabelPosition> getCandidatePositions() {
        return candidatePositions;
    }

    /**
     * Returns this self loop label's position. This is {@code null} until it is initialized by someone.
     */
    public SelfLoopLabelPosition getLabelPosition() {
        return labelPosition;
    }

    /**
     * Sets this self loop label's position. This must be one of the candidate label positions available to this label.
     */
    public void setLabelPosition(final SelfLoopLabelPosition labelPosition) {
        assert candidatePositions.contains(labelPosition);
        
        this.labelPosition = labelPosition;
    }

    @Override
    public String toString() {
        return "Label: " + labels;
    }
    
}
