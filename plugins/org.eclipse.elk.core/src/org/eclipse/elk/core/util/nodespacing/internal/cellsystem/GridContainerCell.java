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
package org.eclipse.elk.core.util.nodespacing.internal.cellsystem;

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;

/**
 * A container that lays out its child cells in a two-dimensional grid. It's basically not much more than an extension
 * of {@link StripContainerCell} into another dimension, which sounds kind of awesome. Anyway, cells can be added to
 * the container as usual. One special feature is that its center cell can be assigned a custom minimal size which will
 * be used regardless of the actual center cell's minimal size. This grid container can even be configured to only use
 * that custom size as its minimal size. As a final goodie, the grid allows you to retrieve the actual rectangle in the
 * center that is not overlapped by any of the outer grid cells. This may not be equal to the cell rectangle computed
 * for the center cell, which always has at least the cell's minimum size.
 */
public class GridContainerCell extends ContainerCell {
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Constants
    
    /** Number of rows in the grid. */
    private static final int ROWS = ContainerArea.values().length;
    /** Number of columns in the grid. */
    private static final int COLUMNS = ROWS;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties
    
    /** A container cell can include gaps between its children when calculating its preferred size. */
    private final double gap;
    /** A container cell consists of a table of cells that make up its content. */
    private final Cell[][] cells = new Cell[ROWS][COLUMNS];
    /**
     * The center cell of the grid may have a custom minimum size (used to empose a mininum size on the node's
     * client area.
     */
    private KVector centerCellMinimumSize = null;
    /**
     * Whether only the center cell contributes to the minimum size of this cell. If this is true, we actually use
     * the custom center cell minimum size instead of the center cell's actual minimum size, if set.
     */
    private boolean onlyCenterCellContributesToMinimumSize = false;
    /** Rectangle that describes the part of the grid free of outer grid cells. May be empty. */
    private ElkRectangle centerCellRect = new ElkRectangle();
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructors
    
    /**
     * Creates a new instance with the given settings.
     * 
     * @param gap
     *            the gap inserted between each pair of consecutive cells.
     */
    public GridContainerCell(final double gap) {
        this.gap = gap;
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Getters / Setters

    /**
     * Returns the gap inserted between each pair of consecutive children.
     */
    public double getGap() {
        return gap;
    }
    
    /**
     * Returns the cell placed at the container's given area.
     * 
     * @param row
     *            the row.
     * @param col
     *            the column.
     * @return the cell, which may by {@code null}.
     */
    public Cell getCell(final ContainerArea row, final ContainerArea col) {
        return cells[row.ordinal()][col.ordinal()];
    }
    
    /**
     * Sets the cell in the container's given area.
     * 
     * @param row
     *            the row.
     * @param col
     *            the column.
     * @param cell
     *            the new cell to be placed in the area.
     */
    public void setCell(final ContainerArea row, final ContainerArea col, final Cell cell) {
        cells[row.ordinal()][col.ordinal()] = cell;
    }
    
    /**
     * Sets the center cell's minimum size. Regardless of the actuall cell in the grid's center, this minimum size
     * imposes a lower bound on the center cell size.
     */
    public void setCenterCellMinimumSize(final KVector minimumSize) {
        this.centerCellMinimumSize = new KVector(minimumSize);
    }
    
    /**
     * Sets whether when this container is queried for its minimum size, only the center cell will be included in the
     * calculations. If the center cell has a minimum size set and this mode is active, that minimum size will be used
     * regardless of the actuall center cell.
     */
    public void setOnlyCenterCellContributesToMinimumSize(final boolean contribution) {
        this.onlyCenterCellContributesToMinimumSize = contribution;
    }
    
    /**
     * Returns a rectangle that describes the space left free by the outer rows and columns. May well be an empty
     * rectangle. The rectangle's information have only been computed once both {@link #layoutChildrenHorizontally()}
     * and {@link #layoutChildrenVertically()} have been called.
     */
    public ElkRectangle getCenterCellRectangle() {
        return new ElkRectangle(centerCellRect);
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // (Container) Cell Methods

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.nodespacing.internal.cells.Cell#getMinimumWidth()
     */
    @Override
    public double getMinimumWidth() {
        double width = 0;
        
        // If only our center cell contributes to our minimum width, shortcut!
        if (onlyCenterCellContributesToMinimumSize && centerCellMinimumSize != null) {
            width = centerCellMinimumSize.x;
        } else {
            // Minimum widths of the center column and the outer columns
            double minWidthCenterColumn = minWidthOfColumn(ContainerArea.CENTER, true);
            double minWidthOuterColumns = Math.max(
                    minWidthOfColumn(ContainerArea.BEGIN, true),
                    minWidthOfColumn(ContainerArea.END, true));
            
            // We need to distinguish which cells exist
            if (minWidthOuterColumns == 0) {
                width = minWidthCenterColumn;
            } else {
                if (minWidthCenterColumn != 0) {
                    width = minWidthCenterColumn + 2 * (minWidthOuterColumns + gap);
                } else {
                    width = 2 * minWidthOuterColumns + gap;
                }
            }
        }
        
        // If we don't have cells, we don't have width
        return width > 0
                ? width + getPadding().left + getPadding().right
                : 0;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.nodespacing.internal.cells.Cell#getMinimumHeight()
     */
    @Override
    public double getMinimumHeight() {
        double height = 0;
        
        // If only our center cell contributes to our minimum width, shortcut!
        if (onlyCenterCellContributesToMinimumSize && centerCellMinimumSize != null) {
            height = centerCellMinimumSize.y;
        } else {
            // Minimum heights of the center row and the outer rows
            double minHeightCenterRow = minHeightOfRow(ContainerArea.CENTER, true);
            double minHeightOuterRows = Math.max(
                    minHeightOfRow(ContainerArea.BEGIN, true),
                    minHeightOfRow(ContainerArea.END, true));
            
            // We need to distinguish which cells exist
            if (minHeightOuterRows == 0) {
                height = minHeightCenterRow;
            } else {
                if (minHeightCenterRow != 0) {
                    height = minHeightCenterRow + 2 * (minHeightOuterRows + gap);
                } else {
                    height = 2 * minHeightOuterRows + gap;
                }
            }
        }
        
        // If we don't have cells, we don't have height
        return height > 0
                ? height + getPadding().top + getPadding().bottom
                : 0;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.nodespacing.internal.cells.ContainerCell#layoutChildrenHorizontally()
     */
    @Override
    public void layoutChildrenHorizontally() {
        ElkRectangle cellRectangle = getCellRectangle();
        ElkPadding cellPadding = getPadding();
        
        double minWidthLeftColumn = minWidthOfColumn(ContainerArea.BEGIN, false);
        double minWidthCenterColumn = minWidthOfColumn(ContainerArea.CENTER, false);
        double minWidthRightColumn = minWidthOfColumn(ContainerArea.END, false);
        double minWidthOuterColumns = Math.max(minWidthLeftColumn, minWidthRightColumn);
        
        // Left column is left-aligned with our content area, right column is right-aligned
        applyHorizontalLayout(ContainerArea.BEGIN,
                cellRectangle.x + cellPadding.left,
                minWidthOuterColumns);
        applyHorizontalLayout(ContainerArea.END,
                cellRectangle.x + cellRectangle.width - cellPadding.right - minWidthOuterColumns,
                minWidthOuterColumns);
        
        // Size of the content area and size of the available space in the content area
        double contentAreaWidth = cellRectangle.width - cellPadding.left - cellPadding.right;
        
        double contentAreaFreeWidth = contentAreaWidth;
        if (minWidthOuterColumns > 0) {
            contentAreaFreeWidth -= 2 * (minWidthOuterColumns + gap);
        }
        
        // If the available space is larger than the current size of the center cell, enlarge that thing
        minWidthCenterColumn = Math.max(minWidthCenterColumn, contentAreaFreeWidth);

        // Place the center cell, possibly enlarging it in the process
        applyHorizontalLayout(ContainerArea.CENTER,
                cellRectangle.x + cellPadding.left + (contentAreaWidth - minWidthCenterColumn) / 2,
                minWidthCenterColumn);
        
        // Update x coordinate and width of the center cell rectangle
        computeCenterCellRectangleHorizontally(minWidthOuterColumns);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.nodespacing.internal.cells.ContainerCell#layoutChildrenVertically()
     */
    @Override
    public void layoutChildrenVertically() {
        ElkRectangle cellRectangle = getCellRectangle();
        ElkPadding cellPadding = getPadding();
        
        double minHeightTopRow = minHeightOfRow(ContainerArea.BEGIN, false);
        double minHeightCenterRow = minHeightOfRow(ContainerArea.CENTER, false);
        double minHeightBottomRow = minHeightOfRow(ContainerArea.END, false);
        double minHeightOuterRows = Math.max(minHeightTopRow, minHeightBottomRow);
        
        //Top row is top-aligned with our content area, bottom row is bottom-aligned
        applyVerticalLayout(ContainerArea.BEGIN,
                cellRectangle.y + cellPadding.top,
                minHeightOuterRows);
        applyVerticalLayout(ContainerArea.END,
                cellRectangle.y + cellRectangle.height - cellPadding.bottom - minHeightOuterRows,
                minHeightOuterRows);
        
        // Size of the content area and size of the available space in the content area
        double contentAreaHeight = cellRectangle.height - cellPadding.top - cellPadding.bottom;
        
        double contentAreaFreeHeight = contentAreaHeight;
        if (minHeightOuterRows > 0) {
            contentAreaFreeHeight -= 2 * (minHeightOuterRows + gap);
        }
        
        // If the available space is larger than the current size of the center cell, enlarge that thing
        minHeightCenterRow = Math.max(minHeightCenterRow, contentAreaFreeHeight);

        // Place the center cell, possibly enlarging it in the process
        applyVerticalLayout(ContainerArea.CENTER,
                cellRectangle.y + cellPadding.top + (contentAreaHeight - minHeightCenterRow) / 2,
                minHeightCenterRow);
        
        // Update y coordinate and height of the center cell rectangle
        computeCenterCellRectangleVertically(minHeightOuterRows);
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utility Methods
    
    /**
     * Returns the minimum width of the given column.
     */
    private double minWidthOfColumn(final ContainerArea column, final boolean respectContributionFlag) {
        double maxMinWidth = 0;
        for (int row = 0; row < ROWS; row++) {
            maxMinWidth = Math.max(
                    maxMinWidth,
                    minWidthOfCell(cells[row][column.ordinal()], respectContributionFlag));
        }
        
        // If this is the center column, we might have an explicit minimal width for that
        if (column == ContainerArea.CENTER && centerCellMinimumSize != null) {
            maxMinWidth = Math.max(maxMinWidth, centerCellMinimumSize.x);
        }
        
        return maxMinWidth;
    }
    
    /**
     * Returns the minimum height of the given row.
     */
    private double minHeightOfRow(final ContainerArea row, final boolean respectContributionFlag) {
        double maxMinHeight = 0;
        for (int column = 0; column < COLUMNS; column++) {
            maxMinHeight = Math.max(
                    maxMinHeight,
                    minHeightOfCell(cells[row.ordinal()][column], respectContributionFlag));
        }
        
        // If this is the center row, we might have an explicit minimal height for that
        if (row == ContainerArea.CENTER && centerCellMinimumSize != null) {
            maxMinHeight = Math.max(maxMinHeight, centerCellMinimumSize.y);
        }
        
        return maxMinHeight;
    }
    
    /**
     * Calls {@link ContainerCell#applyHorizontalLayout(Cell, double, double)} with the given information on all cells
     * in the given column.
     */
    private void applyHorizontalLayout(final ContainerArea column, final double x, final double width) {
        for (int row = 0; row < ROWS; row++) {
            applyHorizontalLayout(cells[row][column.ordinal()], x, width);
        }
    }
    
    /**
     * Calls {@link ContainerCell#applyVerticalLayout(Cell, double, double)} with the given information on all cells
     * in the given row.
     */
    private void applyVerticalLayout(final ContainerArea row, final double y, final double height) {
        for (int column = 0; column < COLUMNS; column++) {
            applyVerticalLayout(cells[row.ordinal()][column], y, height);
        }
    }
    
    /**
     * Computes the width and the x coordinate of the center cell rectangle.
     */
    private void computeCenterCellRectangleHorizontally(final double outerColumnWidth) {
        ElkRectangle cellRect = this.getCellRectangle();
        ElkPadding cellPadding = this.getPadding();
        
        // If there are outer columns, we need to include the gap
        double effectiveOuterColumnWidth = outerColumnWidth == 0
                ? 0
                : outerColumnWidth + gap;
        
        // Calculate how much space we really have for the grid
        double effectiveGridWidth = cellRect.width - cellPadding.left - cellPadding.right;
        
        // The width is basically what remains when we subtract the grid's padding and the outer columns from the
        // grid's width
        centerCellRect.width = Math.max(
                0,
                effectiveGridWidth - 2 * effectiveOuterColumnWidth);
        centerCellRect.x = cellRect.x + cellPadding.left + (effectiveGridWidth - centerCellRect.width) / 2;
    }
    
    /**
     * Computes the height and the y coordinate of the center cell rectangle.
     */
    private void computeCenterCellRectangleVertically(final double outerRowHeight) {
        ElkRectangle cellRect = this.getCellRectangle();
        ElkPadding cellPadding = this.getPadding();
        
        // If there are outer columns, we need to include the gap
        double effectiveOuterRowHeight = outerRowHeight == 0
                ? 0
                : outerRowHeight + gap;
        
        // Calculate how much space we really have for the grid
        double effectiveGridHeight = cellRect.height - cellPadding.top - cellPadding.bottom;
        
        // The height is basically what remains when we subtract the grid's padding and the outer rows from the
        // grid's height
        centerCellRect.height = Math.max(
                0,
                effectiveGridHeight - 2 * effectiveOuterRowHeight);
        centerCellRect.y = cellRect.y + cellPadding.top + (effectiveGridHeight - centerCellRect.height) / 2;
    }
    
}
