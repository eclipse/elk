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

import java.util.Set;

import org.eclipse.elk.alg.layered.compaction.oned.CNode;
import org.eclipse.elk.alg.layered.compaction.oned.CompareFuzzy;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.Direction;

import com.google.common.collect.Sets;

/**
 * Representation of a combination of intersecting vertical segments of {@link LEdge}s in the
 * constraint graph. The {@link LEdge} segments are handled as one element during the compaction.
 * 
 * @see CNode
 */
public final class CLEdge extends CNode {
    /** the associated horizontal spacing. */
    private double horizontalSpacing;
    /** the associated horizontal spacing. */
    private double verticalSpacing;
    /** specifies the bend points belonging to this vertical edge segment. */
    private KVectorChain bends;
    /** junction points affected by manipulation of this segment. */
    private KVectorChain juctionPoints;
    /** referring to the {@link LEdge}s this segment is a part of. */
    public Set<LEdge> originalLEdges = Sets.newHashSet(); // SUPPRESS CHECKSTYLE VisibilityModifier

    /**
     * The constructor adds a {@link VerticalSegment} to the list and appends its bend and junction
     * points.
     * 
     * @param vSeg
     *            a single {@link VerticalSegment}
     * @param layeredGraph
     *            the containing layered graph
     */
    public CLEdge(final VerticalSegment vSeg, final LGraph layeredGraph) {
        // getting the spacing properties
        horizontalSpacing = layeredGraph.getProperty(LayeredOptions.SPACING_EDGE_EDGE_BETWEEN_LAYERS);
        verticalSpacing = layeredGraph.getProperty(LayeredOptions.SPACING_EDGE_EDGE);
        
        bends = new KVectorChain();
        juctionPoints = new KVectorChain();
        hitbox = new ElkRectangle(vSeg.x1, vSeg.y1, 0, vSeg.y2 - vSeg.y1);
        parentNode = vSeg.parentNode;
        cGroupOffset.x = vSeg.relativePosition;
        
        addSegment(vSeg);
    }

    /**
     * Adds an intersecting {@link VerticalSegment} to the {@link CLEdge} and merges the hitboxes.
     * The {@link org.eclipse.elk.alg.layered.compaction.oned.Quadruplet CompactionLock} is set for
     * selfloops and {@link Direction}s that fewer different {@link LPort}s are connected in to
     * avoid prolonging more {@link LEdge}s than shortening.
     * 
     * @param vSeg
     *            the single vertical segment
     */
    public void addSegment(final VerticalSegment vSeg) {
        bends.addAll(vSeg.bend1, vSeg.bend2);
        juctionPoints.addAll(vSeg.junctionPoints);
        
        spacingIgnore.up |= vSeg.blockTopSpacing;
        spacingIgnore.down |= vSeg.blockBottomSpacing;

        // updating the hitbox to span over the new segment
        double newY1 = Math.min(hitbox.y, vSeg.y1);
        double newY2 = Math.max(hitbox.y + hitbox.height, vSeg.y2);
        hitbox.setRect(vSeg.x1, newY1, 0, newY2 - newY1);

        originalLEdges.add(vSeg.lEdge);
    
        // edges are locked if they belong to selfloops
        if (vSeg.lEdge.getSource().getNode() == vSeg.lEdge.getTarget().getNode()) {
            lock.set(true, true, true, true);
        }
        // segments belonging to multiple edges should be locked in the direction that fewer different
        // ports are connected in
        Set<LPort> inc = Sets.newHashSet();
        Set<LPort> out = Sets.newHashSet();
        for (LEdge e : originalLEdges) {
            inc.add(e.getSource());
            out.add(e.getTarget());
        }
        
        int difference = inc.size() - out.size();
        if (difference < 0) {
            lock.set(true, Direction.LEFT);
            lock.set(false, Direction.RIGHT);
        } else if (difference > 0) {
            lock.set(false, Direction.LEFT);
            lock.set(true, Direction.RIGHT);
        }
    }

    /**
     * Checks whether this edge representation intersects another vertical segment.
     * 
     * @param vSeg
     *            the other segment
     * @return {@code true} if the segments intersect
     */
    public boolean intersects(final VerticalSegment vSeg) {
        return CompareFuzzy.eq(this.hitbox.x, vSeg.x1)
                && !(CompareFuzzy.lt(hitbox.y + hitbox.height, vSeg.y1) 
                     || CompareFuzzy.lt(vSeg.y2, hitbox.y));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void applyElementPosition() {
        for (KVector b : bends) {
            b.x = hitbox.x;
        }
        for (KVector j : juctionPoints) {
            j.x = hitbox.x;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getElementPosition() {
        return bends.getFirst().x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getHorizontalSpacing() {
      return horizontalSpacing;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public double getVerticalSpacing() {
        return verticalSpacing;
    }
    
    @Override
    public String toString() {
        return originalLEdges.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDebugSVG() {
        return "<line x1=\"" + this.hitbox.x + "\" y1=\"" + this.hitbox.y + "\" x2=\""
                + (this.hitbox.x + this.hitbox.width) + "\" y2=\""
                + (this.hitbox.y + this.hitbox.height) + "\" stroke=\""
                + (this.reposition ? "blue" : "red") + "\" stroke-width=\"3px\"/>";
    }
}
