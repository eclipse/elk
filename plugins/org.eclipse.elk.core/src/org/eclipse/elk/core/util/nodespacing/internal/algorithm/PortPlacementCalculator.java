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

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortAlignment;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.adapters.GraphAdapters.PortAdapter;
import org.eclipse.elk.core.util.nodespacing.cellsystem.AtomicCell;
import org.eclipse.elk.core.util.nodespacing.internal.NodeContext;
import org.eclipse.elk.core.util.nodespacing.internal.PortContext;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Actually places ports.
 */
public final class PortPlacementCalculator {
    
    /**
     * Copy of the {@link de.cau.cs.kieler.klay.layered.properties.InternalProperties#PORT_RATIO_OR_POSITION}
     * option. For further information see the documentation found there. We added this copy here to
     * allow a generic treatment of spacing calculations for graph elements. See the
     * {@link org.eclipse.elk.core.util.nodespacing} package. [programmatically set]
     * 
     * TODO This should be replaced by a proper property declared in a MELK file.
     */
    public static final IProperty<Double> PORT_RATIO_OR_POSITION = new Property<Double>(
            "portRatioOrPosition", 0.0);
    

    /**
     * No instance required.
     */
    private PortPlacementCalculator() {
        
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Horizontal Port Placement
    
    /**
     * Places horizontal ports.
     */
    public static void placeHorizontalPorts(final NodeContext nodeContext) {
     // How we are going to place the ports depends on their constraints
        switch (nodeContext.portConstraints) {
        case FIXED_POS:
            placeHorizontalFixedPosPorts(nodeContext, PortSide.NORTH);
            placeHorizontalFixedPosPorts(nodeContext, PortSide.SOUTH);
            break;
            
        case FIXED_RATIO:
            placeHorizontalFixedRatioPorts(nodeContext, PortSide.NORTH);
            placeHorizontalFixedRatioPorts(nodeContext, PortSide.SOUTH);
            break;
            
        default:
            placeHorizontalFreePorts(nodeContext, PortSide.NORTH);
            placeHorizontalFreePorts(nodeContext, PortSide.SOUTH);
            break;
        }
    }

    /**
     * Places ports on the northern and southern side for the {@link PortConstraints#FIXED_POS} case.
     */
    private static void placeHorizontalFixedPosPorts(final NodeContext nodeContext, final PortSide portSide) {
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            PortAdapter<?> port = portContext.port;
            KVector portPosition = port.getPosition();
            
            // The port's x coordinate is already fixed anyway, so simply adjust its y coordinate according to its
            // offset, if any
            portPosition.y = calculateHorizontalPortYCoordinate(port);
            
            portContext.port.setPosition(portPosition);
        }
    }

    /**
     * Places ports on the northern and southern side for the {@link PortConstraints#FIXED_RATIO} case.
     */
    private static void placeHorizontalFixedRatioPorts(final NodeContext nodeContext, final PortSide portSide) {
        final double nodeWidth = nodeContext.node.getSize().x;
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            PortAdapter<?> port = portContext.port;
            KVector portPosition = new KVector();
            
            // The x coordinate is a function of the node's width and the port's position ratio
            portPosition.x = nodeWidth * port.getProperty(PORT_RATIO_OR_POSITION);
            portPosition.y = calculateHorizontalPortYCoordinate(port);
            
            portContext.port.setPosition(portPosition);
        }
    }

    /**
     * Places ports on the northern and southern side for the unconstrained cases.
     */
    private static void placeHorizontalFreePorts(final NodeContext nodeContext, final PortSide portSide) {
        // If there are no ports on the given side, abort
        if (nodeContext.portContexts.get(portSide).isEmpty()) {
            return;
        }
        
        // Retrieve the proper inside port label cell, which will give us hints as to where to place our ports
        AtomicCell insidePortLabelCell = nodeContext.insidePortLabelCells.get(portSide);
        ElkRectangle insidePortLabelCellRectangle = insidePortLabelCell.getCellRectangle();
        ElkPadding insidePortLabelCellPadding = insidePortLabelCell.getPadding();
        
        // Note that we don't have to distinguish any cases here because the port margins already include space
        // required for labels, if such space is to be reserved. Yay!
        PortAlignment portAlignment = nodeContext.getPortAlignment(portSide);
        double availableSpace = insidePortLabelCellRectangle.width - insidePortLabelCellPadding.left
                - insidePortLabelCellPadding.right;
        double calculatedPortPlacementWidth = insidePortLabelCell.getMinimumContentAreaSize().x;
        double currentXPos = insidePortLabelCellRectangle.x + insidePortLabelCellPadding.left;
        double spaceBetweenPorts = nodeContext.portPortSpacing;
        
        // If we only have a single port and justified alignment, change the alignment to CENTER
        if (nodeContext.portContexts.get(portSide).size() == 1) {
            portAlignment = PortAlignment.CENTER;
        }
        
        // If there is not enough space to place the ports, we may have to adjust the port spacing to ensure we stay
        // inside the boundaries
        if (availableSpace < calculatedPortPlacementWidth
                && !nodeContext.sizeOptions.contains(SizeOptions.PORTS_OVERHANG)) {
            
            spaceBetweenPorts += (availableSpace - calculatedPortPlacementWidth)
                    / (nodeContext.portContexts.get(portSide).size() - 1);
        } else {
            // Calculate where we need to start placing ports (note that the node size required by the port placement
            // includes left and right surrounding port margins, which changes the formulas a bit from what you'd
            // otherwise expect)
            switch (portAlignment) {
            case BEGIN:
                // There's nothing to do here
                break;
                
            case CENTER:
                currentXPos += (availableSpace - calculatedPortPlacementWidth) / 2;
                break;
                
            case END:
                currentXPos += availableSpace - calculatedPortPlacementWidth;
                break;
                
            case JUSTIFIED:
                // In this case, if there is not enough space available to place the ports, we are allowed to overhang.
                // We thus need to ensure that we're only ever increasing the port spacing here
                double additionalSpaceBetweenPorts = (availableSpace - calculatedPortPlacementWidth)
                        / (nodeContext.portContexts.get(portSide).size() - 1);
                spaceBetweenPorts += Math.max(0, additionalSpaceBetweenPorts);
                break;
            }
        }
        
        // Iterate over all ports and place them
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            PortAdapter<?> port = portContext.port;
            KVector portPosition = new KVector();
            
            portPosition.x = currentXPos + portContext.portMargin.left;
            portPosition.y = calculateHorizontalPortYCoordinate(port);
            
            portContext.port.setPosition(portPosition);
            
            // Update the x coordinate for the next port
            currentXPos += portContext.portMargin.left
                    + port.getSize().x
                    + portContext.portMargin.right
                    + spaceBetweenPorts;
        }
    }

    /**
     * Returns the y coordinate for the given port.
     */
    private static double calculateHorizontalPortYCoordinate(final PortAdapter<?> port) {
        // The y coordinate is set according to the port's offset, if any
        if (port.hasProperty(CoreOptions.PORT_BORDER_OFFSET)) {
            return port.getSide() == PortSide.NORTH
                    ? -port.getSize().y - port.getProperty(CoreOptions.PORT_BORDER_OFFSET)
                    : port.getProperty(CoreOptions.PORT_BORDER_OFFSET);
        } else {
            return port.getSide() == PortSide.NORTH
                    ? -port.getSize().y
                    : 0;
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Vertical Port Placement
    
    /**
     * Places vertical ports.
     */
    public static void placeVerticalPorts(final NodeContext nodeContext) {
        // How we are going to place the ports depends on their constraints
        switch (nodeContext.portConstraints) {
        case FIXED_POS:
            placeVerticalFixedPosPorts(nodeContext, PortSide.EAST);
            placeVerticalFixedPosPorts(nodeContext, PortSide.WEST);
            break;
            
        case FIXED_RATIO:
            placeVerticalFixedRatioPorts(nodeContext, PortSide.EAST);
            placeVerticalFixedRatioPorts(nodeContext, PortSide.WEST);
            break;
            
        default:
            placeVerticalFreePorts(nodeContext, PortSide.EAST);
            placeVerticalFreePorts(nodeContext, PortSide.WEST);
            break;
        }
    }

    /**
     * Places ports on the eastern and western side for the {@link PortConstraints#FIXED_POS} case.
     */
    private static void placeVerticalFixedPosPorts(final NodeContext nodeContext, final PortSide portSide) {
        double nodeWidth = nodeContext.node.getSize().x;
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            PortAdapter<?> port = portContext.port;
            KVector portPosition = port.getPosition();
            
            // The port's y coordinate is already fixed anyway, so simply adjust its x coordinate according to its
            // offset, if any
            portPosition.x = calculateVerticalPortXCoordinate(port, nodeWidth);
            
            portContext.port.setPosition(portPosition);
        }
    }

    /**
     * Places ports on the eastern and western side for the {@link PortConstraints#FIXED_RATIO} case.
     */
    private static void placeVerticalFixedRatioPorts(final NodeContext nodeContext, final PortSide portSide) {
        final KVector nodeSize = nodeContext.node.getSize();
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            PortAdapter<?> port = portContext.port;
            KVector portPosition = new KVector();
            
            // The y coordinate is a function of the node's width and the port's position ratio
            portPosition.x = calculateVerticalPortXCoordinate(port, nodeSize.x);
            portPosition.y = nodeSize.y * port.getProperty(PORT_RATIO_OR_POSITION);
            
            portContext.port.setPosition(portPosition);
        }
    }

    /**
     * Places ports on the eastern and western side for the unconstrained cases.
     */
    private static void placeVerticalFreePorts(final NodeContext nodeContext, final PortSide portSide) {
        // If there are no ports on the given side, abort
        if (nodeContext.portContexts.get(portSide).isEmpty()) {
            return;
        }
        
        // Retrieve the proper inside port label cell, which will give us hints as to where to place our ports
        AtomicCell insidePortLabelCell = nodeContext.insidePortLabelCells.get(portSide);
        ElkRectangle insidePortLabelCellRectangle = insidePortLabelCell.getCellRectangle();
        ElkPadding insidePortLabelCellPadding = insidePortLabelCell.getPadding();
        
        // Note that we don't have to distinguish any cases here because the port margins already include space
        // required for labels, if such space is to be reserved. Yay!
        PortAlignment portAlignment = nodeContext.getPortAlignment(portSide);
        double availableSpace = insidePortLabelCellRectangle.height - insidePortLabelCellPadding.top
                - insidePortLabelCellPadding.bottom;
        double calculatedPortPlacementHeight = insidePortLabelCell.getMinimumContentAreaSize().y;
        double currentYPos = insidePortLabelCellRectangle.y + insidePortLabelCellPadding.top;
        double spaceBetweenPorts = nodeContext.portPortSpacing;
        double nodeWidth = nodeContext.node.getSize().x;
        
        // If we only have a single port and justified alignment, change the alignment to CENTER
        if (nodeContext.portContexts.get(portSide).size() == 1) {
            portAlignment = PortAlignment.CENTER;
        }
        
        // If there is not enough space to place the ports, we may have to adjust the port spacing to ensure we stay
        // inside the boundaries
        if (availableSpace < calculatedPortPlacementHeight
                && !nodeContext.sizeOptions.contains(SizeOptions.PORTS_OVERHANG)) {
            
            spaceBetweenPorts += (availableSpace - calculatedPortPlacementHeight)
                    / (nodeContext.portContexts.get(portSide).size() - 1);
        } else {
            // Calculate where we need to start placing ports (note that the node size required by the port placement
            // includes left and right surrounding port margins, which changes the formulas a bit from what you'd
            // otherwise expect)
            switch (portAlignment) {
            case BEGIN:
                // There's nothing to do here
                break;
                
            case CENTER:
                currentYPos += (availableSpace - calculatedPortPlacementHeight) / 2;
                break;
                
            case END:
                currentYPos += availableSpace - calculatedPortPlacementHeight;
                break;
                
            case JUSTIFIED:
                // In this case, if there is not enough space available to place the ports, we are allowed to overhang.
                // We thus need to ensure that we're only ever increasing the port spacing here
                double additionalSpaceBetweenPorts = (availableSpace - calculatedPortPlacementHeight)
                        / (nodeContext.portContexts.get(portSide).size() - 1);
                spaceBetweenPorts += Math.max(0, additionalSpaceBetweenPorts);
                break;
            }
        }
        
        // Iterate over all ports and place them
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            PortAdapter<?> port = portContext.port;
            KVector portPosition = new KVector();
            
            portPosition.x = calculateVerticalPortXCoordinate(port, nodeWidth);
            portPosition.y = currentYPos + portContext.portMargin.top;
            
            portContext.port.setPosition(portPosition);
            
            // Update the y coordinate for the next port
            currentYPos += portContext.portMargin.top
                    + port.getSize().y
                    + portContext.portMargin.bottom
                    + spaceBetweenPorts;
        }
    }

    /**
     * Returns the x coordinate for the given port.
     */
    private static double calculateVerticalPortXCoordinate(final PortAdapter<?> port, final double nodeWidth) {
        // The x coordinate is set according to the port's offset, if any
        if (port.hasProperty(CoreOptions.PORT_BORDER_OFFSET)) {
            return port.getSide() == PortSide.WEST
                    ? -port.getSize().x - port.getProperty(CoreOptions.PORT_BORDER_OFFSET)
                    : nodeWidth + port.getProperty(CoreOptions.PORT_BORDER_OFFSET);
        } else {
            return port.getSide() == PortSide.WEST
                    ? -port.getSize().x
                    : nodeWidth;
        }
    }

}
