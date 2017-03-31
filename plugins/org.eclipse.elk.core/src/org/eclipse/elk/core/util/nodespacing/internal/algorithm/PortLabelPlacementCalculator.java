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

import java.util.Collection;

import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.util.nodespacing.internal.HorizontalLabelAlignment;
import org.eclipse.elk.core.util.nodespacing.internal.NodeContext;
import org.eclipse.elk.core.util.nodespacing.internal.PortContext;
import org.eclipse.elk.core.util.nodespacing.internal.VerticalLabelAlignment;
import org.eclipse.elk.core.util.nodespacing.internal.cellsystem.LabelCell;

/**
 * Knows how to place port labels.
 */
public final class PortLabelPlacementCalculator {

    /**
     * No instance required.
     */
    private PortLabelPlacementCalculator() {
        
    }
    
    
    /**
     * Places port labels for northern and southern ports. If port labels are placed on the inside, the height required
     * for the placement is set as the height of the content area of northern and southern inside port label cells.
     */
    public static void placeHorizontalPortLabels(final NodeContext nodeContext) {
        placePortLabels(nodeContext, PortSide.NORTH);
        placePortLabels(nodeContext, PortSide.SOUTH);
    }
    
    /**
     * Places port labels for eastern and western ports.
     */
    public static void placeVerticalPortLabels(final NodeContext nodeContext) {
        placePortLabels(nodeContext, PortSide.EAST);
        placePortLabels(nodeContext, PortSide.WEST);
    }
    
    /**
     * Places port labels for ports on the given side.
     */
    private static void placePortLabels(final NodeContext nodeContext, final PortSide portSide) {
        // If port labels were not taken into account when calculating the node size or if port placement was set to
        // fixed positions, we don't have an arbitrary amound of freedom to place our labels
        boolean constrainedPlacement = !nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS)
                || nodeContext.portConstraints == PortConstraints.FIXED_POS;
        
        switch (nodeContext.portLabelsPlacement) {
        case INSIDE:
            if (constrainedPlacement) {
                // TODO: Employ the big guns!
                simpleInsidePortLabelPlacement(nodeContext, portSide);
            } else {
                simpleInsidePortLabelPlacement(nodeContext, portSide);
            }
            break;
            
        case OUTSIDE:
            if (constrainedPlacement) {
                // TODO: Employ the big guns!
                simpleOutsidePortLabelPlacement(nodeContext, portSide);
            } else {
                simpleOutsidePortLabelPlacement(nodeContext, portSide);
            }
            break;
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Inside Port Labels
    
    /**
     * Place the port label cells on the node's insides.
     */
    private static void simpleInsidePortLabelPlacement(final NodeContext nodeContext, final PortSide portSide) {
        // For northern and southern port labels, we need to set the inside port label cell's client height later
        double insideNorthOrSouthPortLabelAreaHeight = 0;
        
        // Retrieve the port border offset for the given port side
        double labelBorderOffset = portLabelBorderOffsetForPortSide(nodeContext, portSide);
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            // If the port doesn't have labels, skip
            if (portContext.portLabelCell == null || !portContext.portLabelCell.hasLabels()) {
                continue;
            }
            
            // Retrieve information about the port itself
            KVector portSize = portContext.port.getSize();
            double portBorderOffset = portContext.port.hasProperty(CoreOptions.PORT_BORDER_OFFSET)
                    ? portContext.port.getProperty(CoreOptions.PORT_BORDER_OFFSET)
                    : 0;
            
            // Retrieve the label cell and its rectangle and set the rectangle's size (we will use the rectangle to
            // place the cell relative to the port below)
            LabelCell portLabelCell = portContext.portLabelCell;
            ElkRectangle portLabelCellRect = portLabelCell.getCellRectangle();
            portLabelCellRect.width = portLabelCell.getMinimumWidth();
            portLabelCellRect.height = portLabelCell.getMinimumHeight();
            
            // Calculate the position of the port's label cell
            switch (portSide) {
            case NORTH:
                portLabelCellRect.x = -(portLabelCellRect.width - portSize.x) / 2;
                portLabelCellRect.y = portSize.y + portBorderOffset + labelBorderOffset;
                portLabelCell.setHorizontalAlignment(HorizontalLabelAlignment.CENTER);
                portLabelCell.setVerticalAlignment(VerticalLabelAlignment.TOP);
                break;
                
            case SOUTH:
                portLabelCellRect.x = -(portLabelCellRect.width - portSize.x) / 2;
                portLabelCellRect.y = -portBorderOffset - labelBorderOffset - portLabelCellRect.height;
                portLabelCell.setHorizontalAlignment(HorizontalLabelAlignment.CENTER);
                portLabelCell.setVerticalAlignment(VerticalLabelAlignment.BOTTOM);
                break;
                
            case EAST:
                portLabelCellRect.x = -portBorderOffset - labelBorderOffset - portLabelCellRect.width;
                portLabelCellRect.y = -(portLabelCellRect.height - portSize.y) / 2;
                portLabelCell.setHorizontalAlignment(HorizontalLabelAlignment.RIGHT);
                portLabelCell.setVerticalAlignment(VerticalLabelAlignment.CENTER);
                break;
                
            case WEST:
                portLabelCellRect.x = portSize.x + portBorderOffset + labelBorderOffset;
                portLabelCellRect.y = -(portLabelCellRect.height - portSize.y) / 2;
                portLabelCell.setHorizontalAlignment(HorizontalLabelAlignment.LEFT);
                portLabelCell.setVerticalAlignment(VerticalLabelAlignment.CENTER);
                break;
            }
            
            // If we have a north or south port, update our port label area height
            if (portSide == PortSide.NORTH || portSide == PortSide.SOUTH) {
                insideNorthOrSouthPortLabelAreaHeight = Math.max(
                        insideNorthOrSouthPortLabelAreaHeight,
                        portLabelCellRect.height);
            }
        }
        
        // If we have a northern or southern label area height, apply it
        if (insideNorthOrSouthPortLabelAreaHeight > 0) {
            nodeContext.insidePortLabelCells.get(portSide).getMinimumContentAreaSize().y =
                    insideNorthOrSouthPortLabelAreaHeight;
        }
    }
    
    /**
     * Returns the amount of space port labels are offset from the node border on the given port side. This takes the
     * port border padding into account as well as the port-label spacing.
     */
    private static double portLabelBorderOffsetForPortSide(final NodeContext nodeContext, final PortSide portSide) {
        switch (portSide) {
        case NORTH:
            return nodeContext.nodeContainer.getPadding().top + nodeContext.portLabelSpacing;

        case SOUTH:
            return nodeContext.nodeContainer.getPadding().bottom + nodeContext.portLabelSpacing;

        case EAST:
            return nodeContext.nodeContainer.getPadding().right + nodeContext.portLabelSpacing;

        case WEST:
            return nodeContext.nodeContainer.getPadding().left + nodeContext.portLabelSpacing;
        
        default:
            assert false;
            return 0;
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Outside Port Labels

    /**
     * Place the port label cells outside of the node.
     */
    private static void simpleOutsidePortLabelPlacement(final NodeContext nodeContext, final PortSide portSide) {
        Collection<PortContext> portContexts = nodeContext.portContexts.get(portSide);
        
        // If there are only two ports on a side, we place the first port's label on its other side to make it
        // especially clear which port it belongs to
        boolean portWithSpecialNeeds = portContexts.size() == 2;
        
        for (PortContext portContext : portContexts) {
            // If the port doesn't have labels, skip
            if (portContext.portLabelCell == null || !portContext.portLabelCell.hasLabels()) {
                continue;
            }

            // Retrieve information about the port itself
            KVector portSize = portContext.port.getSize();
            
            // Retrieve the label cell and its rectangle and set the rectangle's size (we will use the rectangle to
            // place the cell relative to the port below)
            LabelCell portLabelCell = portContext.portLabelCell;
            ElkRectangle portLabelCellRect = portLabelCell.getCellRectangle();
            portLabelCellRect.width = portLabelCell.getMinimumWidth();
            portLabelCellRect.height = portLabelCell.getMinimumHeight();
            
            // Calculate the position of the port's label space
            switch (portSide) {
            case NORTH:
                if (portWithSpecialNeeds) {
                    portLabelCellRect.x = -portLabelCellRect.width - nodeContext.portLabelSpacing;
                    portLabelCell.setHorizontalAlignment(HorizontalLabelAlignment.RIGHT);
                } else {
                    portLabelCellRect.x = portSize.x + nodeContext.portLabelSpacing;
                    portLabelCell.setHorizontalAlignment(HorizontalLabelAlignment.LEFT);
                }
                portLabelCellRect.y = -portLabelCellRect.height - nodeContext.portLabelSpacing;
                portLabelCell.setVerticalAlignment(VerticalLabelAlignment.BOTTOM);
                break;
                
            case SOUTH:
                if (portWithSpecialNeeds) {
                    portLabelCellRect.x = -portLabelCellRect.width - nodeContext.portLabelSpacing;
                    portLabelCell.setHorizontalAlignment(HorizontalLabelAlignment.RIGHT);
                } else {
                    portLabelCellRect.x = portSize.x + nodeContext.portLabelSpacing;
                    portLabelCell.setHorizontalAlignment(HorizontalLabelAlignment.LEFT);
                }
                portLabelCellRect.y = portSize.y + nodeContext.portLabelSpacing;
                portLabelCell.setVerticalAlignment(VerticalLabelAlignment.TOP);
                break;
                
            case EAST:
                portLabelCellRect.x = portSize.x + nodeContext.portLabelSpacing;
                if (portWithSpecialNeeds) {
                    portLabelCellRect.y = -portLabelCellRect.height - nodeContext.portLabelSpacing;
                    portLabelCell.setVerticalAlignment(VerticalLabelAlignment.BOTTOM);
                } else {
                    portLabelCellRect.y = portSize.y + nodeContext.portLabelSpacing;
                    portLabelCell.setVerticalAlignment(VerticalLabelAlignment.TOP);
                }
                portLabelCell.setHorizontalAlignment(HorizontalLabelAlignment.LEFT);
                
                break;
                
            case WEST:
                portLabelCellRect.x = -portLabelCellRect.width - nodeContext.portLabelSpacing;
                if (portWithSpecialNeeds) {
                    portLabelCellRect.y = -portLabelCellRect.height - nodeContext.portLabelSpacing;
                    portLabelCell.setVerticalAlignment(VerticalLabelAlignment.BOTTOM);
                } else {
                    portLabelCellRect.y = portSize.y + nodeContext.portLabelSpacing;
                    portLabelCell.setVerticalAlignment(VerticalLabelAlignment.TOP);
                }
                portLabelCell.setHorizontalAlignment(HorizontalLabelAlignment.RIGHT);
                break;
            }
            
            // The next port definitely doesn't have special needs anymore
            portWithSpecialNeeds = false;
        }
    }
    
}
