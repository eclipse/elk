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
 * wants to be in lower routing slot than the target segment. Otherwise, bad things might ensue. What this means depends
 * on the dependency's type:
 * 
 * <ul>
 *   <li>For {@link DependencyType#REGULAR} dependencies, ignoring it will cause the result to deteriorate by the
 *     dependency's weight (which is, for example, the number of additional edge crossings caused by not honouring this
 *     dependency).</li>
 *   <li>For {@link DependencyType#CRITICAL} dependencies, ignoring it will cause edge overlaps, which should be avoided
 *     at all cost.</li>
 * </ul>
 */
public final class HyperEdgeSegmentDependency {
    
    /**
     * Possible types of dependencies between {@link HyperEdgeSegment}s.
     */
    public enum DependencyType {
        /** Regular dependencies are ones that, if ignored, may cause additional crossings. */
        REGULAR,
        /** Critical depencies are ones that, if ignored, result in edge overlaps. */
        CRITICAL;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties
    
    /** the dependency's type. */
    private final DependencyType type;
    /** the source hypernode of this dependency. */
    private HyperEdgeSegment source;
    /** the target hypernode of this dependency. */
    private HyperEdgeSegment target;
    /** the weight of this dependency. */
    private final int weight;
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Initialisation
    
    /**
     * Creates a regular dependency from the given source to the given target and adds it to the dependency lists of
     * those segments.
     */
    public HyperEdgeSegmentDependency(final HyperEdgeSegment source, final HyperEdgeSegment target, final int weight) {
        this(DependencyType.REGULAR, source, target, weight);
    }
    
    /**
     * Creates a dependency of the given type from the given source to the given target and adds it to the dependency
     * lists of those segments.
     */
    public HyperEdgeSegmentDependency(final DependencyType type, final HyperEdgeSegment source,
            final HyperEdgeSegment target, final int weight) {
        
        this.type = type;
        this.weight = weight;
        
        setSource(source);
        setTarget(target);
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Manipulation
    
    /**
     * Removes this dependency.
     */
    public void remove() {
        setSource(null);
        setTarget(null);
    }
    
    /**
     * Reverses this dependency.
     */
    public void reverse() {
        HyperEdgeSegment oldSource = source;
        HyperEdgeSegment oldTarget = target;
        
        setSource(oldTarget);
        setTarget(oldSource);
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Getters and Setters
    
    /**
     * Returns the dependency's type.
     */
    public DependencyType getType() {
        return type;
    }

    /**
     * Return the source segment.
     */
    public HyperEdgeSegment getSource() {
        return source;
    }
    
    /**
     * Sets the source segment, but does not modify that segment's list of outgoing dependencies.
     */
    public void setSource(final HyperEdgeSegment newSource) {
        if (source != null) {
            source.getOutgoingSegmentDependencies().remove(this);
        }
        
        source = newSource;
        
        if (source != null) {
            source.getOutgoingSegmentDependencies().add(this);
        }
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
    public void setTarget(final HyperEdgeSegment newTarget) {
        if (target != null) {
            target.getIncomingSegmentDependencies().remove(this);
        }
        
        target = newTarget;
        
        if (target != null) {
            target.getIncomingSegmentDependencies().add(this);
        }
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
        return source + "->" + target + " (" + type.name() + ")";
    }

}