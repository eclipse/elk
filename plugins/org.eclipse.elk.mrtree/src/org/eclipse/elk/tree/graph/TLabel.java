/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.tree.graph;

/**
 * A label in the T graph.
 * 
 * @author sor
 * @author sgu
 */
public class TLabel extends TShape {

    /** the serial version UID. */
    private static final long serialVersionUID = 1L;

    /** the edge this label is associated to. */
    private TEdge edge;
    /** label text. */
    private String text;

    /**
     * Create a new label. The label is also put into the edge's list of labels.
     * 
     * @param text
     *            the text of the new label
     * @param tedge
     *            edge corresponding to this label
     */
    public TLabel(final TEdge tedge, final String text) {
        this.edge = tedge;
        this.text = text;
        edge.getLabels().add(this);
    }

    /**
     * {@inheritDoc}
     */
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
    public TEdge getEdge() {
        return edge;
    }

}
