/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate.compaction;

import org.eclipse.elk.alg.layered.compaction.oned.CNode;
import org.eclipse.elk.alg.layered.compaction.oned.CompareFuzzy;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.CoreOptions;

/**
 * Represents a vertical segment on a single {@link LEdge} that is merged with intersecting
 * {@link VerticalSegment}s.
 * <p>
 * There are two types of vertical segments that are handled differently and are distinguished by
 * the presence of a parent node. The usual segment stems from a step in an edge and has no parent
 * node. The other type is the vertical part of an edge that is connected to the north or south side
 * of a node. In this case the node is the parent of the segment and neither impose any constraints on
 * each other because they are supposed to connect and therefore collide.
 * </p>
 * 
 * @see CLEdge
 */
public final class VerticalSegment implements Comparable<VerticalSegment> {
    // SUPPRESS CHECKSTYLE NEXT 16 VisibilityModifier
    /** the node a north/south segment is connected to, otherwise null. */
    public CNode parentNode;
    /** offset to the parent node. */
    public double relativePosition;
    /** the bend points defining this segment. */
    public KVector bend1, bend2;
    /** the junction points of the original {@link LEdge}, that are between the bend points. */
    public KVectorChain junctionPoints = new KVectorChain();
    /** Top left corner and bottom of the vertical edge segment. */
    public double x1, y1, y2;
    /** the parent {@link LEdge}. */
    public LEdge lEdge;
    /** Whether the edge spacing to the top should be ignored during constraint calculation. */
    public boolean blockTopSpacing = true;
    /** Whether the edge spacing to the bottom should be ignored during constraint calculation. */
    public boolean blockBottomSpacing = false;

    /**
     * Creates a new instance setting all fields.
     * 
     * @param bend1
     *            first bend point
     * @param bend2
     *            second bend point
     * @param cNode
     *            parent node
     * @param lEdge
     *            parent edge
     */
    public VerticalSegment(final KVector bend1, final KVector bend2, final CNode cNode,
            final LEdge lEdge) {
        this.bend1 = bend1;
        this.bend2 = bend2;

        // setting coordinates according to bend points
        if (bend1.y < bend2.y) {
            x1 = bend1.x;
            y1 = bend1.y;
            y2 = bend2.y;
        } else {
            x1 = bend2.x;
            y1 = bend2.y;
            y2 = bend1.y;
        }

        // adding junction points
        KVectorChain inJPs = lEdge.getProperty(CoreOptions.JUNCTION_POINTS);
        // this property could be null
        if (inJPs != null) {
            for (KVector jp : inJPs) {
                if (CompareFuzzy.eq(jp.x, bend1.x)) {
                    junctionPoints.add(jp);
                }
            }
        }

        parentNode = cNode;

        // only north/south segments have a parent node
        if (parentNode != null) {
            relativePosition = x1 - cNode.hitbox.x;
        }

        this.lEdge = lEdge;
    }

    /**
     * Checks whether this segment intersects another.
     * 
     * @param o
     *            the other segment
     * @return {@code true} if the segments intersect
     */
    public boolean intersects(final VerticalSegment o) {
        return CompareFuzzy.eq(this.x1, o.x1)
                && !(CompareFuzzy.lt(this.y2, o.y1) || CompareFuzzy.lt(o.y2, this.y1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final VerticalSegment o) {
        int d = Double.compare(this.x1, o.x1);
        if (d == 0) {
            return Double.compare(this.y1, o.y1);
        }
        return d;
    }
}
