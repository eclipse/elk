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
package org.eclipse.elk.core.util.nodespacing.internal.cells;

import java.util.Arrays;

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.ElkRectangle;

/**
 * A container cell that lays its children out along a strip. The strip can be horizontal (in which case the children
 * can be interpreted as columns) or vertical (in which case the children can be interpreted as rows). A strip
 * container will always make its outer cells the larger of their minimum sizes. Whatever remains of its content area
 * will be given to the center cell, which will always be at least its minimum size, even if that causes it to
 * overlap with the outer cells.
 */
public class StripContainerCell extends ContainerCell {
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties
    
    /** Whether we lay children out in rows or columns. */
    private final Strip containerMode;
    /** A container cell can include gaps between its children when calculating its preferred size. */
    private final double gap;
    /** A container cell consists of a number of cells that make up its content. */
    private final Cell[] cells = new Cell[ContainerArea.values().length];
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructors
    
    /**
     * Creates a new instance with the given settings.
     * 
     * @param mode
     *            whether to lay out children as rows or as columns.
     * @param gap
     *            the gap inserted between each pair of consecutive cells.
     */
    public StripContainerCell(final Strip mode, final double gap) {
        this.containerMode = mode;
        this.gap = gap;
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Getters / Setters

    /**
     * Whether this container lays out its children as rows or as columns.
     */
    public Strip getContainerMode() {
        return containerMode;
    }

    /**
     * Returns the gap inserted between each pair of consecutive children.
     */
    public double getGap() {
        return gap;
    }
    
    /**
     * Returns the cell placed at the container's given area.
     * 
     * @param area
     *            the area whose cell to retrieve.
     * @return the cell, which may by {@code null}.
     */
    public Cell getCell(final ContainerArea area) {
        return cells[area.ordinal()];
    }
    
    /**
     * Sets the cell in the container's given area.
     * 
     * @param area
     *            the container's area.
     * @param cell
     *            the new cell to be placed in the area.
     */
    public void setCell(final ContainerArea area, final Cell cell) {
        cells[area.ordinal()] = cell;
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // (Container) Cell Methods

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.nodespacing.internal.cells.Cell#getMinimumWidth()
     */
    @Override
    public double getMinimumWidth() {
        double width = 0;
        
        if (containerMode == Strip.VERTICAL) {
            // Take the maximum of the child cells
            width = Arrays.stream(cells)
                    .filter(cell -> cell != null && cell.isContributingToMinimumWidth())
                    .mapToDouble(cell -> cell.getMinimumWidth())
                    .max()
                    .orElse(0);
        } else {
            // Minimum widths of the center cell and the outer cells
            double minWidthCenterCell = minWidthOfCell(cells[1], true);
            double minWidthOuterCells = Math.max(minWidthOfCell(cells[0], true), minWidthOfCell(cells[2], true));
            
            // We need to distinguish which cells exist
            if (minWidthOuterCells == 0) {
                width = minWidthCenterCell;
            } else {
                if (minWidthCenterCell != 0) {
                    width = minWidthCenterCell + 2 * (minWidthOuterCells + gap);
                } else {
                    width = 2 * minWidthOuterCells + gap;
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
        
        if (containerMode == Strip.VERTICAL) {
            // Minimum heights of the center cell and the outer cells
            double minHeightCenterCell = minHeightOfCell(cells[1], true);
            double minHeightOuterCells = Math.max(minHeightOfCell(cells[0], true), minHeightOfCell(cells[2], true));
            
            // We need to distinguish which cells exist
            if (minHeightOuterCells == 0) {
                height = minHeightCenterCell;
            } else {
                if (minHeightCenterCell != 0) {
                    height = minHeightCenterCell + 2 * (minHeightOuterCells + gap);
                } else {
                    height = 2 * minHeightOuterCells + gap;
                }
            }
        } else {
            // Take the maximum of the child cells
            height = Arrays.stream(cells)
                    .filter(cell -> cell != null && cell.isContributingToMinimumHeight())
                    .mapToDouble(cell -> cell.getMinimumHeight())
                    .max()
                    .orElse(0);
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
        
        if (containerMode == Strip.VERTICAL) {
            // Each child cell begins at our left border (plus padding) and is as large as our content area
            double xPos = cellRectangle.x + cellPadding.left;
            double width = cellRectangle.width - cellPadding.left - cellPadding.right;
            
            for (Cell childCell : cells) {
                applyHorizontalLayout(childCell, xPos, width);
            }
        } else {
            double minWidthLeftCell = minWidthOfCell(cells[0], false);
            double minWidthCenterCell = minWidthOfCell(cells[1], false);
            double minWidthRightCell = minWidthOfCell(cells[2], false);
            double minWidthOuterCells = Math.max(minWidthLeftCell, minWidthRightCell);
            
            // Left cell is left-aligned with our content area, right cell is right-aligned
            applyHorizontalLayout(cells[0],
                    cellRectangle.x + cellPadding.left,
                    minWidthOuterCells);
            applyHorizontalLayout(cells[2],
                    cellRectangle.width - cellPadding.right - minWidthOuterCells,
                    minWidthOuterCells);
            
            // Size of the content area and size of the available space in the content area
            double contentAreaWidth = cellRectangle.width - cellPadding.left - cellPadding.right;
            
            double contentAreaFreeWidth = contentAreaWidth;
            if (minWidthOuterCells > 0) {
                contentAreaFreeWidth -= 2 * (minWidthOuterCells + gap);
            }
            
            // If the available space is larger than the current size of the center cell, enlarge that thing
            minWidthCenterCell = Math.max(minWidthCenterCell, contentAreaFreeWidth);

            // Place the center cell, possibly enlarging it in the process
            applyHorizontalLayout(cells[1],
                    cellRectangle.x + cellPadding.left + (contentAreaWidth - minWidthCenterCell) / 2,
                    minWidthCenterCell);
        }
        
        // Layout container cells recursively
        for (Cell childCell : cells) {
            if (childCell instanceof ContainerCell) {
                ((ContainerCell) childCell).layoutChildrenHorizontally();
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.nodespacing.internal.cells.ContainerCell#layoutChildrenVertically()
     */
    @Override
    public void layoutChildrenVertically() {
        ElkRectangle cellRectangle = getCellRectangle();
        ElkPadding cellPadding = getPadding();
        
        if (containerMode == Strip.VERTICAL) {
            double minHeightLeftCell = minHeightOfCell(cells[0], false);
            double minHeightCenterCell = minHeightOfCell(cells[1], false);
            double minHeightRightCell = minHeightOfCell(cells[2], false);
            double minHeightOuterCells = Math.max(minHeightLeftCell, minHeightRightCell);
            
            // Top cell is top-aligned with our content area, bottom cell is bottom-aligned
            applyVerticalLayout(cells[0],
                    cellRectangle.y + cellPadding.top,
                    minHeightOuterCells);
            applyVerticalLayout(cells[2],
                    cellRectangle.height - cellPadding.bottom - minHeightOuterCells,
                    minHeightOuterCells);
            
            // Size of the content area and size of the available space in the content area
            double contentAreaHeight = cellRectangle.height - cellPadding.top - cellPadding.bottom;
            
            double contentAreaFreeHeight = contentAreaHeight;
            if (minHeightOuterCells > 0) {
                contentAreaFreeHeight -= 2 * (minHeightOuterCells + gap);
            }
            
            // If the available space is larger than the current size of the center cell, enlarge that thing
            minHeightCenterCell = Math.max(minHeightCenterCell, contentAreaFreeHeight);

            // Place the center cell, possibly enlarging it in the process
            applyVerticalLayout(cells[1],
                    cellRectangle.y + cellPadding.top + (contentAreaHeight - minHeightCenterCell) / 2,
                    minHeightCenterCell);
        } else {
            // Each child cell begins at our top border (plus padding) and is as large as our content area
            double yPos = cellRectangle.y + cellPadding.top;
            double height = cellRectangle.height - cellPadding.top - cellPadding.bottom;
            
            for (Cell childCell : cells) {
                applyVerticalLayout(childCell, yPos, height);
            }
        }
        
        // Layout container cells recursively
        for (Cell childCell : cells) {
            if (childCell instanceof ContainerCell) {
                ((ContainerCell) childCell).layoutChildrenVertically();
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utility Classes

    /**
     * The mode of a {@link StripContainerCell} describes whether its child cells are to be regarded as rows or as
     * columns.
     */
    public static enum Strip {
        /** In a vertical strip, the container's child cells are its rows. */
        VERTICAL,
        /** In a horizontal strip, the container's child cells are its columns. */
        HORIZONTAL;
    }
    
}
