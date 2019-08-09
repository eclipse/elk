/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.core.math.KVector;

/**
 * A list of labels that belong to a {@link SelfLoopComponent}.
 */
public class SelfLoopLabel {

    /** The list of labels this self loop label consists of. */
    private final List<LLabel> labels = new ArrayList<>();
    /** This label's size. */
    private final KVector size = new KVector();
    /** The candidate positions for this label. */
    private final List<SelfLoopLabelPosition> candidatePositions = new ArrayList<>();
    /** The position chosen for this label. */
    private SelfLoopLabelPosition labelPosition;

    /**
     * Return the list of labels represented by this self loop label.
     */
    public List<LLabel> getLabels() {
        return labels;
    }
    
    /**
     * Returns the size of this self loop label. Modify to change the size.
     * 
     * @return the size.
     */
    public KVector getSize() {
        return size;
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
