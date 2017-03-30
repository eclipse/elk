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
package org.eclipse.elk.core.util.nodespacing.internal.algorithm;

import java.util.Arrays;

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.nodespacing.internal.cells.AtomicCell;
import org.eclipse.elk.core.util.nodespacing.internal.cells.ContainerArea;
import org.eclipse.elk.core.util.nodespacing.internal.contexts.NodeContext;
import org.eclipse.elk.core.util.nodespacing.internal.contexts.PortContext;

/**
 * Sets up the inside port label cells. These are set up even when there are no inside port labels since they also
 * determine how much space we need to place ports along the node borders.
 */
public final class InsidePortLabelCellCreator {
    
    /**
     * No instance required.
     */
    private InsidePortLabelCellCreator() {
        
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Inside Port Label Cells
    
    /**
     * Creates all the inside port label cells. Setting them up is left to the port and port label placement code.
     */
    public static void createInsidePortLabelCells(final NodeContext nodeContext) {
        // Create all inside port label cells
        AtomicCell northCell = new AtomicCell();
        nodeContext.nodeContainer.setCell(ContainerArea.BEGIN, northCell);
        nodeContext.insidePortLabelCells.put(PortSide.NORTH, northCell);

        AtomicCell southCell = new AtomicCell();
        nodeContext.nodeContainer.setCell(ContainerArea.END, southCell);
        nodeContext.insidePortLabelCells.put(PortSide.SOUTH, southCell);

        AtomicCell westCell = new AtomicCell();
        nodeContext.nodeContainerMiddleRow.setCell(ContainerArea.BEGIN, westCell);
        nodeContext.insidePortLabelCells.put(PortSide.WEST, westCell);

        AtomicCell eastCell = new AtomicCell();
        nodeContext.nodeContainerMiddleRow.setCell(ContainerArea.END, eastCell);
        nodeContext.insidePortLabelCells.put(PortSide.EAST, eastCell);
        
        // If we have inside port labels, go ahead and reserve enough space for them
        if (nodeContext.portLabelsPlacement == PortLabelPlacement.INSIDE) {
            setupNorthOrSouthPortLabelCell(nodeContext, PortSide.NORTH);
            setupNorthOrSouthPortLabelCell(nodeContext, PortSide.SOUTH);
            
            setupEastOrWestPortLabelCell(nodeContext, PortSide.EAST);
            setupEastOrWestPortLabelCell(nodeContext, PortSide.WEST);
        }
    }
    
    /**
     * Calculates size and paddings for the cell.
     */
    private static void setupNorthOrSouthPortLabelCell(final NodeContext nodeContext, final PortSide portSide) {
        calculateHeightDueToLabels(nodeContext, portSide);
        setupLeftAndRightPadding(nodeContext, portSide);
    }

    /**
     * Calculates the cell's height to fit the largest label in there. If there actually is a label, also setup the
     * cell's top or bottom padding to ensure enough space between ports and labels.
     */
    private static void calculateHeightDueToLabels(final NodeContext nodeContext, final PortSide portSide) {
        // Retrieve the appropriate cell
        AtomicCell theAppropriateCell = nodeContext.insidePortLabelCells.get(portSide);
        KVector minCellSize = theAppropriateCell.getMinimumContentAreaSize();
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            // Update the maximum label height
            if (portContext.portLabelCell != null) {
                minCellSize.y = Math.max(minCellSize.y, portContext.portLabelCell.getMinimumHeight());
            }
        }
        
        // If the cell has a minimum height by now, that means we actually have labels in there. Which, in turn, means
        // that we need to add a padding to the cell to ensure enough space between ports and their inside labels
        if (minCellSize.y > 0) {
            switch (portSide) {
            case NORTH:
                theAppropriateCell.getPadding().top = nodeContext.portLabelSpacing;
                break;
                
            case SOUTH:
                theAppropriateCell.getPadding().bottom = nodeContext.portLabelSpacing;
                break;
            }
        }
    }

    /**
     * Sets up the cell's left and right padding to match the surrounding port margins.
     */
    private static void setupLeftAndRightPadding(final NodeContext nodeContext, final PortSide portSide) {
        if (nodeContext.surroundingPortMargins != null) {
            ElkPadding padding = nodeContext.insidePortLabelCells.get(portSide).getPadding();
            padding.left = nodeContext.surroundingPortMargins.left;
            padding.right = nodeContext.surroundingPortMargins.right;
        }
    }
    
    /**
     * Does the work for {@link #calculatePortLabelWidth(NodeContext)} for the given port side.
     */
    private static void setupEastOrWestPortLabelCell(final NodeContext nodeContext, final PortSide portSide) {
        calculateWidthDueToLabels(nodeContext, portSide);
        setupTopAndBottomPadding(nodeContext, portSide);
    }

    /**
     * Calculates the cell's height to fit the largest label in there. If there actually is a label, also setup the
     * cell's top or bottom padding to ensure enough space between ports and labels.
     */
    private static void calculateWidthDueToLabels(final NodeContext nodeContext, final PortSide portSide) {
        // Retrieve the appropriate cell
        AtomicCell theAppropriateCell = nodeContext.insidePortLabelCells.get(portSide);
        KVector minCellSize = theAppropriateCell.getMinimumContentAreaSize();
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            // Update the maximum label width
            if (portContext.portLabelCell != null) {
                minCellSize.x = Math.max(minCellSize.x, portContext.portLabelCell.getMinimumWidth());
            }
        }
        
        // If the cell has a minimum width by now, that means we actually have labels in there. Which, in turn, means
        // that we need to add a padding to the cell to ensure enough space between ports and their inside labels
        if (minCellSize.x > 0) {
            switch (portSide) {
            case EAST:
                theAppropriateCell.getPadding().right = nodeContext.portLabelSpacing;
                break;
                
            case WEST:
                theAppropriateCell.getPadding().left = nodeContext.portLabelSpacing;
                break;
            }
        }
    }

    /**
     * Sets up the cell's top and bottom padding to match the surrounding port margins.
     */
    private static void setupTopAndBottomPadding(final NodeContext nodeContext, final PortSide portSide) {
        if (nodeContext.surroundingPortMargins != null) {
            ElkPadding padding = nodeContext.insidePortLabelCells.get(portSide).getPadding();
            padding.top = nodeContext.surroundingPortMargins.top;
            padding.bottom = nodeContext.surroundingPortMargins.bottom;
        }
    }
    
}
