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

import org.eclipse.elk.core.math.KVector;

public class GridContainerCell extends ContainerCell {
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties
    
    /** A container cell can include gaps between its children when calculating its preferred size. */
    private final double gap;
    /** A container cell consists of a table of cells that make up its content. */
    private final Cell[][] cells = new Cell[ContainerArea.values().length][ContainerArea.values().length];
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
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // (Container) Cell Methods

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.nodespacing.internal.cells.Cell#getMinimumWidth()
     */
    @Override
    public double getMinimumWidth() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.nodespacing.internal.cells.Cell#getMinimumHeight()
     */
    @Override
    public double getMinimumHeight() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.nodespacing.internal.cells.ContainerCell#layoutChildrenHorizontally()
     */
    @Override
    public void layoutChildrenHorizontally() {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.elk.core.util.nodespacing.internal.cells.ContainerCell#layoutChildrenVertically()
     */
    @Override
    public void layoutChildrenVertically() {
        // TODO Auto-generated method stub
        
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Child Manipulation
    
    /**
     * Make the width of child columns and the height or child rows symmetrical.
     */
    public void establishOuterChildSymmetry() {
        // TODO
    }
    
}
