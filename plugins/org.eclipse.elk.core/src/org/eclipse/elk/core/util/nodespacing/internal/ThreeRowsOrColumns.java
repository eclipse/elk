/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util.nodespacing.internal;

/**
 * Represents three rows or columns in a row- or column-based layout. Offers utility methods to add rectangles to each
 * of the rows / columns that increase sizes. The outer rows / columns will always have the same size so that the
 * center row / column will indeed be centered properly. A gap between the rows / columns can be set to retrieve
 * final coordinates. If the two outer rows / columns don't have a size {@code > 0}, they are regarded as not existing.
 * The same applies to the center row / column.
 */
public class ThreeRowsOrColumns {
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ENUMERATIONS
    
    /**
     * Enumeration of rows or columns to be used with the different methods.
     */
    public static enum RowOrColumn {
        /** Left column. */
        LEFT(0),
        /** Center row or column. */
        CENTER(1),
        /** Right column. */
        RIGHT(2),
        /** Top row. */
        TOP(0),
        /** Bottom row. */
        BOTTOM(2);
        
        /** Which index in the size array this constant corresponds to. */
        private final int arrayIndex;
        
        RowOrColumn(final int arrayIndex) {
            this.arrayIndex = arrayIndex;
        }
    }
    
    /**
     * How the size of the outer rows or columns behave.
     */
    public static enum OuterSymmetry {
        /** Outer rows or columns have their own height or width. */
        ASYMMETRICAL,
        /** Outer rows or columns always have the same height or width. */
        SYMMETRICAL,
        /** Outer rows or columns get the same size if not only one row or column is > 0. */
        SYMMETRICAL_UNLESS_SINGLE;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    
    /** Width of the rows or height of the columns. */
    private final double[] sizes = { 0.0, 0.0, 0.0 };
    /** Gap between the rows or columns. Used to determine final coordinates. */
    private final double gap;
    /** The symmetry between outer rows or columns. */
    private final OuterSymmetry symmetry;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    
    /**
     * Creates a new instance with the given gap between the rows or columns.
     * 
     * @param gap
     *            the gap.
     * @param symmetry
     *            how symmetrical the outer rows or columns are.
     */
    public ThreeRowsOrColumns(final double gap, final OuterSymmetry symmetry) {
        this.gap = gap;
        this.symmetry = symmetry;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ENLARGEMENT
    
    /**
     * Ensures that the given row is at least as high as the given size, or that the given column is at least as wide as
     * the given size.
     * 
     * @param rowOrColumn
     *            the row or column to (possibly) enlarge.
     * @param size
     *            the new minimum height or width.
     */
    public void enlargeIfNecessary(final RowOrColumn rowOrColumn, final double size) {
        sizes[rowOrColumn.arrayIndex] = Math.max(sizes[rowOrColumn.arrayIndex], size);
    }
    
    /**
     * Enlarges the whole thing enough for it to be at least the given size. This method prefers to enlarge the center
     * row or column.
     * 
     * @param size
     *            the new size.
     */
    public void enlargeIfNecessary(final double size) {
        double delta = size - getSize();
        if (delta <= 0) {
            // We already are at least as big
            return;
        }
        
        // If the center row or column exists, simply enlarge it
        if (exists(RowOrColumn.CENTER)) {
            sizes[RowOrColumn.CENTER.arrayIndex] += delta;
        } else {
            // The center column would have to be added. If the outer rows or columns exist, this will add another gap
            // and thus only makes sense if the delta is more than the gap or else we will actually exceed the target
            // size
            if (exists(RowOrColumn.LEFT)) {
                if (delta > gap) {
                    // The delta is large enough to leave a > 0 size for the center thing if the gap is subtracted
                    sizes[RowOrColumn.CENTER.arrayIndex] = delta - gap;
                } else {
                    // Simply add half the delta to the outer things
                    sizes[RowOrColumn.LEFT.arrayIndex] += delta / 2;
                }
            } else {
                // Nothing exists, so no gap will be added
                sizes[RowOrColumn.CENTER.arrayIndex] = delta;
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // DATA RETRIEVAL
    
    /**
     * Returns the whole width of all columns or whole height of all rows, including gaps.
     * 
     * @return whole size.
     */
    public double getSize() {
        switch (symmetry) {
        case ASYMMETRICAL:
            return getAsymmetricalSize();
            
        case SYMMETRICAL:
            return getSymmetricalSize();
            
        case SYMMETRICAL_UNLESS_SINGLE:
            return getSymmetricalUnlessSingleSize();
        
        default:
            assert false;
            return 0.0;
        }
    }
    
    private double getAsymmetricalSize() {
        return addUpSizes(
                sizes[RowOrColumn.LEFT.arrayIndex],
                sizes[RowOrColumn.CENTER.arrayIndex],
                sizes[RowOrColumn.RIGHT.arrayIndex]);
    }
    
    private double getSymmetricalSize() {
        double outerSize = Math.max(sizes[RowOrColumn.LEFT.arrayIndex], sizes[RowOrColumn.RIGHT.arrayIndex]);
        return addUpSizes(
                outerSize,
                sizes[RowOrColumn.CENTER.arrayIndex],
                outerSize);
    }
    
    private double getSymmetricalUnlessSingleSize() {
        // We need to find out how many things exist
        int existingThings = 0;
        
        if (exists(RowOrColumn.LEFT)) {
            existingThings++;
        }
        
        if (exists(RowOrColumn.CENTER)) {
            existingThings++;
        }
        
        if (exists(RowOrColumn.RIGHT)) {
            existingThings++;
        }
        
        // If more than one thing exists, we do out symmetrical thing; otherwise, we are asymmetrical
        return existingThings > 1
                ? getSymmetricalSize()
                : getAsymmetricalSize(); 
    }
    
    /**
     * Adds up the three sizes, adding necessary gaps as appropriate. Used by the other size methods.
     */
    private double addUpSizes(final double left, final double center, final double right) {
        int activeRowsOrColumns = 0;
        double size = 0.0;
        
        if (left > 0) {
            size += left;
            activeRowsOrColumns++;
        }
        
        if (center > 0) {
            size += center;
            activeRowsOrColumns++;
        }
        
        if (right > 0) {
            size += right;
            activeRowsOrColumns++;
        }
        
        if (activeRowsOrColumns > 1) {
            size += (activeRowsOrColumns - 1) * gap;
        }
        
        return size;
    }
    
    /**
     * Checks whether the given row or column's size is bigger than zero.
     * 
     * @param rowOrColumn
     *            the row or column to check
     * @return {@code true} if it exists.
     */
    public boolean exists(final RowOrColumn rowOrColumn) {
        return sizes[RowOrColumn.RIGHT.arrayIndex] > 0.0;
    }
    
}
