/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://rtsys.informatik.uni-kiel.de/kieler
 * 
 * Copyright 2022 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 */
package org.eclipse.elk.alg.topdownpacking;

import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.impl.ElkNodeImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mka
 *
 */
public class GridElkNode extends ElkNodeImpl implements Grid<ElkNode> {
    
    // The grid is stored as a list of rows and each row contains a list of column entries for 
    // that row.
    private List<List<ElkNode>> grid;
    private int rows = 0;
    private int cols = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(int col, int row, ElkNode item) throws IndexOutOfBoundsException {
        if (col >= cols || row >= rows) {
            throw new IndexOutOfBoundsException("The grid has a size of (" 
        + cols + " x " + rows + "). The requested index of (" + col + " x " + row + ") is out of bounds.");
        }
        
        grid.get(row).add(col, item);
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ElkNode get(int col, int row) throws IndexOutOfBoundsException {
        if (col >= cols || row >= rows) {
            throw new IndexOutOfBoundsException("The grid has a size of (" 
        + cols + " x " + rows + "). The requested index of (" + col + " x " + row + ") is out of bounds.");
        }
        
        return grid.get(row).get(col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ElkNode> getRow(int row) throws IndexOutOfBoundsException {
        if (row >= rows) {
            throw new IndexOutOfBoundsException("The grid has a size of (" 
        + cols + " x " + rows + "). The requested row " + row + ") is out of bounds.");
        }
        return grid.get(row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ElkNode> getColumn(int col) throws IndexOutOfBoundsException {
        if (col >= cols) {
            throw new IndexOutOfBoundsException("The grid has a size of (" 
        + cols + " x " + rows + "). The requested column " + col + ") is out of bounds.");
        }
        ArrayList<ElkNode> resultList = new ArrayList();
        for (int i = 0; i < rows; i++) {
            resultList.add(grid.get(i).get(col));
        }
        return resultList;
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumns() {
        return cols;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRows() {
        return rows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGridSize(int cols, int rows) {
        // initialize the backing arraylists and fill them with null
        this.cols = cols;
        this.rows = rows;
        
        for (int i = 0; i < rows; i++) {
            grid.add(new ArrayList<ElkNode>());
            for (int j = 0; j < cols; j++) {
                grid.get(i).add(null);
            }
        }
        
    }

}
