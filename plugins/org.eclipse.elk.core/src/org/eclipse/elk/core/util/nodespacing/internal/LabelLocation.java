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
package org.eclipse.elk.core.util.nodespacing.internal;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.util.nodespacing.internal.ThreeRowsOrColumns.RowOrColumn;

import com.google.common.collect.Lists;

/**
 * Enumeration over all possible label placements and associated things.
 *
 * @author csp
 * @see NodeLabelPlacement
 */
@SuppressWarnings("unchecked")
public enum LabelLocation {
    /** Outside top left. */
    OUT_T_L(LabelAlignment.LEFT,
            RowOrColumn.TOP,
            RowOrColumn.LEFT,
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_LEFT)),
            
    /** Outside top center. */
    OUT_T_C(LabelAlignment.CENTER,
            RowOrColumn.TOP,
            RowOrColumn.CENTER,
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),
    
    /** Outside top right. */
    OUT_T_R(LabelAlignment.RIGHT,
            RowOrColumn.TOP,
            RowOrColumn.RIGHT,
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_RIGHT)),
    
    /** Outside bottom left. */
    OUT_B_L(LabelAlignment.LEFT,
            RowOrColumn.BOTTOM,
            RowOrColumn.LEFT,
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_LEFT)),
    
    /** Outside bottom center. */
    OUT_B_C(LabelAlignment.CENTER,
            RowOrColumn.BOTTOM,
            RowOrColumn.CENTER,
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),
    
    /** Outside bottom right. */
    OUT_B_R(LabelAlignment.RIGHT,
            RowOrColumn.BOTTOM,
            RowOrColumn.RIGHT,
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_RIGHT)),

    /** Outside left top. */
    OUT_L_T(LabelAlignment.RIGHT,
            RowOrColumn.TOP,
            RowOrColumn.LEFT,
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_PRIORITY)),
    
    /** Outside left center. */
    OUT_L_C(LabelAlignment.RIGHT,
            RowOrColumn.CENTER,
            RowOrColumn.LEFT,
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.V_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),
    
    /** Outside left bottom. */
    OUT_L_B(LabelAlignment.RIGHT,
            RowOrColumn.BOTTOM,
            RowOrColumn.LEFT,
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_PRIORITY)),
    
    /** Outside right top. */
    OUT_R_T(LabelAlignment.LEFT,
            RowOrColumn.TOP,
            RowOrColumn.RIGHT,
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_PRIORITY)),
    
    /** Outside right center. */
    OUT_R_C(LabelAlignment.LEFT,
            RowOrColumn.CENTER,
            RowOrColumn.RIGHT,
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.V_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),
    
    /** Outside right bottom. */
    OUT_R_B(LabelAlignment.LEFT,
            RowOrColumn.BOTTOM,
            RowOrColumn.RIGHT,
            EnumSet.of(
                    NodeLabelPlacement.OUTSIDE,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_PRIORITY)),
    
    /** Inside top left. */
    IN_T_L(LabelAlignment.LEFT,
            RowOrColumn.TOP,
            RowOrColumn.LEFT,
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_LEFT),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.H_PRIORITY)),
    
    /** Inside top center. */
    IN_T_C(LabelAlignment.CENTER,
            RowOrColumn.TOP,
            RowOrColumn.CENTER,
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),
    
    /** Inside top right. */
    IN_T_R(LabelAlignment.RIGHT,
            RowOrColumn.TOP,
            RowOrColumn.RIGHT,
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_RIGHT),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_TOP,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.H_PRIORITY)),
    
    /** Inside center left. */
    IN_C_L(LabelAlignment.LEFT,
            RowOrColumn.CENTER,
            RowOrColumn.LEFT,
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_LEFT),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.H_PRIORITY)),

    /** Inside center center. */
    IN_C_C(LabelAlignment.CENTER,
            RowOrColumn.CENTER,
            RowOrColumn.CENTER,
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),

    /** Inside center right. */
    IN_C_R(LabelAlignment.RIGHT,
            RowOrColumn.CENTER,
            RowOrColumn.RIGHT,
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_RIGHT),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_CENTER,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.H_PRIORITY)),

    /** Inside bottom left. */
    IN_B_L(LabelAlignment.LEFT,
            RowOrColumn.BOTTOM,
            RowOrColumn.LEFT,
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_LEFT),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_LEFT,
                    NodeLabelPlacement.H_PRIORITY)),

    /** Inside bottom center. */
    IN_B_C(LabelAlignment.CENTER,
            RowOrColumn.BOTTOM,
            RowOrColumn.CENTER,
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_CENTER),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_CENTER,
                    NodeLabelPlacement.H_PRIORITY)),

    /** Inside bottom right. */
    IN_B_R(LabelAlignment.RIGHT,
            RowOrColumn.BOTTOM,
            RowOrColumn.RIGHT,
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_RIGHT),
            EnumSet.of(
                    NodeLabelPlacement.INSIDE,
                    NodeLabelPlacement.V_BOTTOM,
                    NodeLabelPlacement.H_RIGHT,
                    NodeLabelPlacement.H_PRIORITY)),
    
    /** Undefined or not decidable. */
    UNDEFINED(null, null, null);
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fields

    /** The corresponding placements to this location. */
    private final List<? extends Set<NodeLabelPlacement>> assignedPlacements;
    /** The horizontal text alignment for this location. */
    private final LabelAlignment horizontalAlignment;
    /** The appropriate label row in a {@link ThreeRowsOrColumns} instance. */
    private final RowOrColumn threeRowsRow;
    /** The appropriate label column in a {@link ThreeRowsOrColumns} instance. */
    private final RowOrColumn threeColumnsColumn;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction

    /**
     * Creates a new location with the {@link NodeLabelPlacement}s that lead to this location.
     *
     * @param horizontalAlignment
     *            the horizontal label alignment for this location.
     * @param row
     *            the appropriate row in a {@link ThreeRowsOrColumns} instance.
     * @param column
     *            the appropriate column in a {@link ThreeRowsOrColumns} instance.
     * @param assignedPlacements
     *            the valid {@link NodeLabelPlacement}s for this location.
     */
    LabelLocation(final LabelAlignment horizontalAlignment, final RowOrColumn row, final RowOrColumn column,
            final Set<NodeLabelPlacement>... assignedPlacements) {
        
        this.horizontalAlignment = horizontalAlignment;
        this.threeRowsRow = row;
        this.threeColumnsColumn = column;
        this.assignedPlacements = Lists.newArrayList(assignedPlacements);
    }

    /**
     * Converts a set of {@link NodeLabelPlacement}s to a {@link LabelLocation} if possible.
     * 
     * @param labelPlacement
     *            the set of placements to convert.
     * @return the corresponding location. If no valid combination is given,
     *         {@code LabelLocation.UNDEFINED} is returned.
     */
    public static LabelLocation fromNodeLabelPlacement(final Set<NodeLabelPlacement> labelPlacement) {
        for (final LabelLocation location : LabelLocation.values()) {
            if (location.assignedPlacements.contains(labelPlacement)) {
                return location;
            }
        }
        return LabelLocation.UNDEFINED;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors

    /**
     * Returns the horizontal text alignment for this location.
     * 
     * @return the horizontalAlignment
     */
    public LabelAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * Returns the appropriate row in a {@link ThreeRowsOrColumns} instance.
     * 
     * @return the row.
     */
    public RowOrColumn getThreeRowsRow() {
        return threeRowsRow;
    }

    /**
     * Returns the appropriate column in a {@link ThreeRowsOrColumns} instance.
     * 
     * @return the column.
     */
    public RowOrColumn getThreeColumnsColumn() {
        return threeColumnsColumn;
    }
    
}