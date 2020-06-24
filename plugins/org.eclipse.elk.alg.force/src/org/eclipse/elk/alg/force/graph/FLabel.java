/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.force.graph;

import org.eclipse.elk.alg.force.options.ForceOptions;
import org.eclipse.elk.core.math.KVector;

/**
 * A label in the force graph.
 * 
 * @author tmn
 * @author owo
 * @author msp
 */
public final class FLabel extends FParticle {
    
    /** the serial version UID. */
    private static final long serialVersionUID = 9047772256368142239L;
    
    /** the edge this label is associated to. */
    private FEdge edge;
    /** label text. */
    private String text;
    
    /**
     * Create a new label. The label is also put into the edge's list of labels.
     * 
     * @param text the text of the new label
     * @param fedge edge corresponding to this label
     */
    public FLabel(final FEdge fedge, final String text) {
        this.edge = fedge;
        this.text = text;
        edge.getLabels().add(this);
    }
    
    @Override
    public String toString() {
        if (text == null || text.length() == 0) {
            return "l[" + edge.toString() + "]";
        } else {
            return "l_" + text;
        }
    }
        
    /**
     * Returns the text of this label.
     * 
     * @return text of this label
     */
    public String getText() {
        return text;
    }
    
    /**
     * Returns the associated edge.
     * 
     * @return edge this label is associated to.
     */
    public FEdge getEdge() {
        return edge;
    }
    
    /**
     * Refresh the label position, that is, place it in the center of the edge.
     */
    public void refreshPosition() { 
        double spacing = edge.getProperty(ForceOptions.SPACING_EDGE_LABEL).doubleValue();

        KVector src = edge.getSource().getPosition();
        KVector tgt = edge.getTarget().getPosition();

        // TODO add support for head and tail labels
        KVector pos = getPosition();
        if (src.x >= tgt.x) {
            if (src.y >= tgt.y) {
                // CASE1, src top left, tgt bottom right
                pos.x = tgt.x + ((src.x - tgt.x) / 2) + spacing;
                pos.y = tgt.y + ((src.y - tgt.y) / 2) - spacing;
            } else {
                // CASE2, src bottom left, tgt top right
                pos.x = tgt.x + ((src.x - tgt.x) / 2) + spacing;
                pos.y = src.y + ((tgt.y - src.y) / 2) + spacing;
            }
        } else {
            if (src.y >= tgt.y) {
                // CASE2, src top right, tgt bottom left
                pos.x = src.x + ((tgt.x - src.x) / 2) + spacing;
                pos.y = tgt.y + ((src.y - tgt.y) / 2) + spacing;
            } else {
                // CASE1, src bottom right, tgt top left
                pos.x = src.x + ((tgt.x - src.x) / 2) + spacing;
                pos.y = src.y + ((tgt.y - src.y) / 2) - spacing;
            }
        }
    }

}
