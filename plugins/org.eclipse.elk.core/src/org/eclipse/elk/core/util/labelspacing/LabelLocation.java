/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util.labelspacing;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.core.options.NodeLabelPlacement;

import com.google.common.collect.ImmutableList;

/**
 * Enumeration over all possible label placements.
 *
 * @author csp
 * @see NodeLabelPlacement
 */
public enum LabelLocation {
    /** Outside top left. */
    OUT_T_L(ImmutableList.of(EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_LEFT)),
            TextAlignment.LEFT),
    /** Outside top center. */
    OUT_T_C(ImmutableList.of(
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.CENTER),
    /** Outside top right. */
    OUT_T_R(ImmutableList.of(EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_RIGHT)),
            TextAlignment.RIGHT),
    /** Outside bottom left. */
    OUT_B_L(ImmutableList.of(EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_LEFT)),
            TextAlignment.LEFT),
    /** Outside bottom center. */
    OUT_B_C(ImmutableList.of(
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.CENTER),
    /** Outside bottom right. */
    OUT_B_R(ImmutableList.of(EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_RIGHT)),
            TextAlignment.RIGHT),
    /** Outside left top. */
    OUT_L_T(ImmutableList.of(EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.RIGHT),
    /** Outside left center. */
    OUT_L_C(ImmutableList.of(
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.V_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.RIGHT),
    /** Outside left bottom. */
    OUT_L_B(ImmutableList.of(EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.RIGHT),
    /** Outside right top. */
    OUT_R_T(ImmutableList.of(EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.LEFT),
    /** Outside right center. */
    OUT_R_C(ImmutableList.of(
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.V_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.LEFT),
    /** Outside right bottom. */
    OUT_R_B(ImmutableList.of(EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.LEFT),
    /** Inside top left. */
    IN_T_L(ImmutableList.of(
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_LEFT),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.LEFT),
    /** Inside top center. */
    IN_T_C(ImmutableList.of(
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.CENTER),
    /** Inside top right. */
    IN_T_R(ImmutableList.of(
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_RIGHT),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.RIGHT),
    /** Inside center left. */
    IN_C_L(ImmutableList.of(
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_LEFT),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.LEFT),
    /** Inside center center. */
    IN_C_C(ImmutableList.of(
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.CENTER),
    /** Inside center right. */
    IN_C_R(ImmutableList.of(
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_RIGHT),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.RIGHT),
    /** Inside bottom left. */
    IN_B_L(ImmutableList.of(
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_LEFT),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.LEFT),
    /** Inside bottom center. */
    IN_B_C(ImmutableList.of(
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.CENTER),
    /** Inside bottom right. */
    IN_B_R(ImmutableList.of(
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_RIGHT),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.H_PRIORITY)),
            TextAlignment.RIGHT),
    /** Undefined or not decidable. */
    UNDEFINED(ImmutableList.<EnumSet<NodeLabelPlacement>>of(),
            null);

    /* The corresponding placements to this location. */
    private final List<? extends Set<NodeLabelPlacement>> assignedPlacements;
    /* The horizontal text alignment for this location. */
    private final TextAlignment horizontalAlignment;

    /**
     * Creates a new location with valid {@link NodeLabelPlacement} for this location.
     *
     * @param assignedPlacements
     *            the valid {@link NodeLabelPlacement}s for this location.
     * @param horizontalAlignment
     *            the horizontal text alignment for this location.
     */
    private LabelLocation(final List<? extends Set<NodeLabelPlacement>> assignedPlacements,
            final TextAlignment horizontalAlignment) {
        this.assignedPlacements = assignedPlacements;
        this.horizontalAlignment = horizontalAlignment;
    }

    /**
     * Converts a set of {@link NodeLabelPlacement}s to a {@link LabelLocation} if possible.
     * 
     * @param labelPlacement
     *            the set of placements to convert.
     * @return the corresponding location. If no valid combination is given,
     *         {@code LabelLocation.UNDEFINED} is returned.
     */
    public static LabelLocation fromNodeLabelPlacement(
            final EnumSet<NodeLabelPlacement> labelPlacement) {
        for (final LabelLocation location : LabelLocation.values()) {
            if (location.assignedPlacements.contains(labelPlacement)) {
                return location;
            }
        }
        return LabelLocation.UNDEFINED;
    }

    /**
     * Returns the horizontal text alignment for this location.
     * 
     * @return the horizontalAlignment
     */
    public TextAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

}