/*******************************************************************************
 * Copyright (c) 2010, 2020 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.orthogonal;

/**
 * A dependency between two {@link HyperEdgeSegment}s. The dependency is to be interpreted like this: the source segment
 * wants to be in lower routing slot than the target segment. Otherwise, this will cause the result to deteriorate by
 * the dependency's weight (which is, for example, the number of additional edge crossings caused by not honouring this
 * dependency).
 */
public final class HyperEdgeSegmentDependency {
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties
    
    /** the source hypernode of this dependency. */
    private HyperEdgeSegment source;
    /** the target hypernode of this dependency. */
    private HyperEdgeSegment target;
    /** the weight of this dependency. */
    private final int weight;
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Initialisation
    
    /**
     * Creates a dependency from the given source to the given target and adds it to the dependency lists of those
     * segments.
     */
    public HyperEdgeSegmentDependency(final HyperEdgeSegment source, final HyperEdgeSegment target, final int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        
        source.getOutgoingSegmentDependencies().add(this);
        target.getIncomingSegmentDependencies().add(this);
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Getters and Setters

    /**
     * Return the source segment.
     */
    public HyperEdgeSegment getSource() {
        return source;
    }
    
    /**
     * Sets the source segment, but does not modify that segment's list of outgoing dependencies.
     */
    public void setSource(final HyperEdgeSegment source) {
        this.source = source;
    }

    /**
     * Return the target segment.
     */
    public HyperEdgeSegment getTarget() {
        return target;
    }
    
    /**
     * Sets the target segment, but does not modify that segment's list of incoming dependencies.
     */
    public void setTarget(final HyperEdgeSegment target) {
        this.target = target;
    }

    /**
     * Returns the weight of this dependency.
     */
    public int getWeight() {
        return weight;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Object Overrides

    @Override
    public String toString() {
        return source + "->" + target;
    }

}