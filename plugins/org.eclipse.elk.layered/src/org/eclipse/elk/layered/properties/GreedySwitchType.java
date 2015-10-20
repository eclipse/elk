/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.layered.properties;

/**
 * Sets the variant of the greedy switch heuristic.
 * 
 * @author alan
 *
 */
public enum GreedySwitchType {

    /** Only consider crossings to one side of the free layer. Calculate crossing matrix on demand. */
    ONE_SIDED(true, false, false),
    /** Consider crossings to both sides of the free layer. Calculate crossing matrix on demand. */
    TWO_SIDED(false, false, false),
    /**
     * Only consider crossings to one side of the free layer. Calculate crossing matrix on demand.
     * Compare all upward and downward sweeps.
     */
    ONE_SIDED_BEST_OF_UP_OR_DOWN(true, true, false),
    /**
     * Consider crossings to both sides of the free layer. Calculate crossing matrix on demand.
     * Compare all upward and downward sweeps.
     */
    TWO_SIDED_BEST_OF_UP_OR_DOWN(false, true, false),
    /**
     * Only consider crossings to one side of the free layer. Calculate crossing matrix on demand.
     * Compare all upward and downward sweeps. Use hyperedge crossings counter for between layer
     * edges
     */
    ONE_SIDED_BEST_OF_UP_OR_DOWN_ORTHOGONAL_HYPEREDGES(true, true, true),
    /**
     * Consider crossings to both sides of the free layer. Calculate crossing matrix on demand.
     * Compare all upward and downward sweeps. Use hyperedge crossings counter for between layer
     * edges.
     */
    TWO_SIDED_BEST_OF_UP_OR_DOWN_ORTHOGONAL_HYPEREDGES(false, true, true),
    /**
     * Only consider crossings to one side of the free layer. Calculate crossing matrix on demand.
     * Use hyperedge crossings counter for between layer edges.
     */
    ONE_SIDED_ORTHOGONAL_HYPEREDGES(true, false, true),
    /** Don't use greedy switch heuristic. */
    OFF(false, false, false);

    private final boolean isOneSided;
    private final boolean useBestOfUpOrDown;
    private final boolean useHperedgeCounter;

    private GreedySwitchType(final boolean isOneSided, final boolean useBestOfUpOrDown,
            final boolean useOrthogonalCounter) {
        this.isOneSided = isOneSided;
        this.useBestOfUpOrDown = useBestOfUpOrDown;
        useHperedgeCounter = useOrthogonalCounter;
    }

    /**
     * Only considers crossings to one side of the free layer.
     * 
     * @return true if only considers two layers.
     */
    public boolean isOneSided() {
        return isOneSided;
    }

    /**
     * Compares top-bottom and bottom->top in layer sweep direction.
     * 
     * @return whether this applies.
     */
    public boolean useBestOfUpOrDown() {
        return useBestOfUpOrDown;
    }

    /**
     * Uses hyperedge crossing count approximization for between-layer edges.
     * 
     * @return whether this applies.
     */
    public boolean useHyperedgeCounter() {
        return useHperedgeCounter;
    }

}
