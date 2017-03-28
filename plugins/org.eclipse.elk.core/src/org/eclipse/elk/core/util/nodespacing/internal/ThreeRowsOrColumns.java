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

import org.eclipse.elk.core.math.ElkRectangle;

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
    /** Gap between the rows or columns. Used to determine the required size. */
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
     * row or column. It should probably only be called if all calls to {@link #enlargeIfNecessary(RowOrColumn, double)}
     * have been made.
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
        
        // We have different cases depending on which of the cells exists
        if (exists(RowOrColumn.CENTER)) {
            // The simplest case: the center cell exists. Simply enlarge that and leave everything else untouched
            sizes[RowOrColumn.CENTER.arrayIndex] += delta;
            
        } else if (!exists(RowOrColumn.LEFT) && !exists(RowOrColumn.CENTER) && !exists(RowOrColumn.RIGHT)) {
            // None of the cells exist, so simply create the center cell
            sizes[RowOrColumn.CENTER.arrayIndex] = delta;
            
        } else if (exists(RowOrColumn.LEFT) && exists(RowOrColumn.RIGHT)) {
            // Both of the outer cells exist; add half the delta to both outer cells, which will work for all symmetry
            // modes
            sizes[RowOrColumn.LEFT.arrayIndex] += delta / 2;
            sizes[RowOrColumn.RIGHT.arrayIndex] += delta / 2;
            
        } else if (exists(RowOrColumn.LEFT)) {
            // Only the left cell exists. What happens depends on the symmetry mode
            if (symmetry == OuterSymmetry.SYMMETRICAL) {
                // Only add half the delta; the other half will appear in the right cell
                sizes[RowOrColumn.LEFT.arrayIndex] += delta / 2;
            } else {
                sizes[RowOrColumn.LEFT.arrayIndex] += delta;
            }
            
        } else if (exists(RowOrColumn.RIGHT)) {
            // Only the right cell exists. What happens depends on the symmetry mode
            if (symmetry == OuterSymmetry.SYMMETRICAL) {
                // Only add half the delta; the other half will appear in the left cell
                sizes[RowOrColumn.RIGHT.arrayIndex] += delta / 2;
            } else {
                sizes[RowOrColumn.RIGHT.arrayIndex] += delta;
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INDIVIDUAL SIZES
    
    /**
     * Returns the height or width of the given row or column, respectively.
     * 
     * @param rowOrColumn
     *            the row or column.
     * @return the height or width.
     */
    public double getSize(final RowOrColumn rowOrColumn) {
        // The center row or column always works directly
        if (rowOrColumn == RowOrColumn.CENTER || symmetry == OuterSymmetry.ASYMMETRICAL) {
            return sizes[rowOrColumn.arrayIndex];
        }
        
        // The outer ones require a bit more work
        switch (symmetry) {
        case SYMMETRICAL:
            return getSymmetricalSize(rowOrColumn);
            
        case SYMMETRICAL_UNLESS_SINGLE:
            return getSymmetricalUnlessSingleSize(rowOrColumn);
        
        default:
            assert false;
            return 0.0;
        }
    }
    
    /**
     * Implements {@link #getSize(RowOrColumn)} for the symmetrical case.
     */
    private double getSymmetricalSize(final RowOrColumn rowOrColumn) {
        assert rowOrColumn != RowOrColumn.CENTER;
        
        return Math.max(
                sizes[RowOrColumn.LEFT.arrayIndex],
                sizes[RowOrColumn.RIGHT.arrayIndex]);
    }
    
    /**
     * Implements {@link #getSize(RowOrColumn)} for the symmetrical case, unless only a single cell is filled.
     */
    private double getSymmetricalUnlessSingleSize(final RowOrColumn rowOrColumn) {
        assert rowOrColumn != RowOrColumn.CENTER;
        
        // If more than one thing exists, we do out symmetrical thing; otherwise, we are asymmetrical
        return countExistingThings() > 1
                ? getSymmetricalSize(rowOrColumn)
                : sizes[rowOrColumn.arrayIndex]; 
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // TOTAL SIZE
    
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
    
    /**
     * Implements {@link #getSize()} for the asymmetrical case.
     */
    private double getAsymmetricalSize() {
        return addUpSizes(
                sizes[RowOrColumn.LEFT.arrayIndex],
                sizes[RowOrColumn.CENTER.arrayIndex],
                sizes[RowOrColumn.RIGHT.arrayIndex]);
    }
    
    /**
     * Implements {@link #getSize()} for the symmetrical case.
     */
    private double getSymmetricalSize() {
        double outerSize = Math.max(sizes[RowOrColumn.LEFT.arrayIndex], sizes[RowOrColumn.RIGHT.arrayIndex]);
        return addUpSizes(
                outerSize,
                sizes[RowOrColumn.CENTER.arrayIndex],
                outerSize);
    }
    
    /**
     * Implements {@link #getSize()} for the symmetrical case, unless only a single cell is filled.
     */
    private double getSymmetricalUnlessSingleSize() {
        // If more than one thing exists, we do out symmetrical thing; otherwise, we are asymmetrical
        return countExistingThings() > 1
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


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // APPLICATION TO RECTANGLES
    
    /**
     * Applies the settings of this object to the given three rectangles.
     * 
     * @param row
     *            if {@code true}, the rectangles represent a row of things and will be assigned widths and x
     *            coordinates. Otherwise, they will be assigned heights and y coordinates.
     * @param startCoordinate
     *            coordinate of the first rectangle.
     * @param rect1
     *            first rectangle.
     * @param rect2
     *            second rectangle.
     * @param rect3
     *            third rectangle.
     */
    public void applyToRectangles(final boolean row, final double startCoordinate, final ElkRectangle rect1,
            final ElkRectangle rect2, final ElkRectangle rect3) {
        
        if (row) {
            applyToRowOfRectangles(startCoordinate, rect1, rect2, rect3);
        } else {
            applyToColumnOfRectangles(startCoordinate, rect1, rect2, rect3);
        }
    }

    /**
     * Implements {@link #applyToRectangles(boolean, double, ElkRectangle, ElkRectangle, ElkRectangle)} for the row
     * case.
     */
    private void applyToRowOfRectangles(final double startCoordinate, final ElkRectangle rect1,
            final ElkRectangle rect2, final ElkRectangle rect3) {
        
        double x = startCoordinate;
        
        rect1.x = x;
        if (exists(RowOrColumn.LEFT)) {
            rect1.width = getSize(RowOrColumn.LEFT);
            x += rect1.width + gap;
        } else {
            rect1.width = 0;
        }
        
        rect2.x = x;
        if (exists(RowOrColumn.CENTER)) {
            rect2.width = getSize(RowOrColumn.CENTER);
            x += rect2.width + gap;
        } else {
            rect2.width = 0;
        }
        
        rect3.x = x;
        if (exists(RowOrColumn.RIGHT)) {
            rect3.width = getSize(RowOrColumn.RIGHT);
        } else {
            rect3.width = 0;
        }
    }

    /**
     * Implements {@link #applyToRectangles(boolean, double, ElkRectangle, ElkRectangle, ElkRectangle)} for the column
     * case.
     */
    private void applyToColumnOfRectangles(final double startCoordinate, final ElkRectangle rect1,
            final ElkRectangle rect2, final ElkRectangle rect3) {
        
        double y = startCoordinate;
        
        rect1.y = y;
        if (exists(RowOrColumn.TOP)) {
            rect1.height = getSize(RowOrColumn.TOP);
            y += rect1.height + gap;
        } else {
            rect1.height = 0;
        }
        
        rect2.y = y;
        if (exists(RowOrColumn.CENTER)) {
            rect2.height = getSize(RowOrColumn.CENTER);
            y += rect2.height + gap;
        } else {
            rect2.height = 0;
        }
        
        rect3.y = y;
        if (exists(RowOrColumn.BOTTOM)) {
            rect3.height = getSize(RowOrColumn.BOTTOM);
        } else {
            rect3.height = 0;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UTILITIES
    
    /**
     * Counts how many rows or columns exist in this thing.
     * 
     * @return number of existing rows or columns.
     */
    public int countExistingThings() {
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
        
        return existingThings;
    }
    
    /**
     * Checks whether the given row or column's size is bigger than zero.
     * 
     * @param rowOrColumn
     *            the row or column to check
     * @return {@code true} if it exists.
     */
    public boolean exists(final RowOrColumn rowOrColumn) {
        return sizes[rowOrColumn.arrayIndex] > 0.0;
    }
    
}
