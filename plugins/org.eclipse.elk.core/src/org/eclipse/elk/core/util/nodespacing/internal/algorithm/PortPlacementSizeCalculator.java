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

import java.util.Iterator;

import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.nodespacing.internal.cells.AtomicCell;
import org.eclipse.elk.core.util.nodespacing.internal.contexts.NodeContext;
import org.eclipse.elk.core.util.nodespacing.internal.contexts.PortContext;

/**
 * Calculates the space required to setup port labels.
 */
public final class PortPlacementSizeCalculator {

    /**
     * No instance required.
     */
    private PortPlacementSizeCalculator() {
        
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Horizontal Port Placement
    
    /**
     * Calculates the space required for horizontal port placements. If the port placement is not fixed, this will
     * also setup the left and right padding of the inside port label placement cells to reflect the amount of space
     * to be left around the port placement.
     */
    public static void calculateHorizontalPortPlacementSize(final NodeContext nodeContext) {
        // We need to calculate the space required by the ports even if ports are not part of the size constraints,
        // since we will use that later to place them
        switch (nodeContext.portConstraints) {
        case FIXED_POS:
            // We don't have any freedom at all, so simply calculate where the rightmost port is on each side
            calculateHorizontalNodeSizeRequiredByFixedPosPorts(nodeContext, PortSide.NORTH);
            calculateHorizontalNodeSizeRequiredByFixedPosPorts(nodeContext, PortSide.SOUTH);
            break;
            
        case FIXED_RATIO:
            // We can require the node to be large enough to avoid spacing violations with fixed ratio ports
            // TODO Implement
            break;
            
        default:
            // If we are free to place things, make the node large enough to place everything properly
            calculateHorizontalNodeSizeRequiredByFreePorts(nodeContext, PortSide.NORTH);
            calculateHorizontalNodeSizeRequiredByFreePorts(nodeContext, PortSide.SOUTH);
            break;
        }
    }
    
    /**
     * Sets up inside port label spaces for fixed position ports. If the node size constraints include ports, we also
     * mess with the cell padding.
     */
    private static void calculateHorizontalNodeSizeRequiredByFixedPosPorts(final NodeContext nodeContext,
            final PortSide portSide) {
        
        double rightmostPortBorder = 0.0;
        
        // Check all ports on the correct side
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            rightmostPortBorder = Math.max(
                    rightmostPortBorder,
                    portContext.port.getPosition().x + portContext.port.getSize().x);
        }
        
        // Set the cell size and remove left padding since the cell size itself already includes all the space we need
        AtomicCell cell = nodeContext.insidePortLabelCells.get(portSide);
        cell.getPadding().left = 0;
        cell.getMinimumContentAreaSize().x = rightmostPortBorder;
    }
    
    /**
     * Sets up inside port label spaces for free ports.
     */
    private static void calculateHorizontalNodeSizeRequiredByFreePorts(final NodeContext nodeContext,
            final PortSide portSide) {

        AtomicCell cell = nodeContext.insidePortLabelCells.get(portSide);
        
        // Handle the common case first: if there are no ports, set everything to zero
        if (nodeContext.portContexts.get(portSide).isEmpty()) {
            cell.getPadding().left = 0;
            cell.getPadding().right = 0;
            return;
        }
        
        // A few convenience variables
        boolean includePortLabels = nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS);
        boolean twoPorts = nodeContext.portContexts.get(portSide).size() == 2;
        boolean portLabelsOutside = nodeContext.portLabelsPlacement == PortLabelPlacement.OUTSIDE;
        boolean uniformPortSpacing = nodeContext.sizeOptions.contains(SizeOptions.UNIFORM_PORT_SPACING);
        double width = 0.0;
        
        // How we need to calculate things really depends on which situation we find ourselves in...
        if (!includePortLabels || (twoPorts && portLabelsOutside)) {
            // We ignore port labels or we have only two ports whose labels won't be placed between them. The space
            // between the ports is a function of their combined width, number, and the port-port spacing
            width = portWidthPlusPortPortSpacing(nodeContext, portSide, false, false);
            
        } else if (portLabelsOutside) {
            // Each port contributes its own amount of space, along with its labels (except for the rightmost port).
            // If uniform port spacing is requested, we need to apply the longest label width to every port
            if (uniformPortSpacing) {
                double maxLabelWidth = maximumPortLabelWidth(nodeContext, portSide);
                
                // If there is a maximum label width, setup the port margins accordingly
                if (maxLabelWidth > 0) {
                    setHorizontalPortMargins(nodeContext, portSide, false, maxLabelWidth);
                }
                
                // Sum up the width of all ports including their margins, except for the last part (its label is not
                // part of the port area anymore ans is allowed to overhang)
                width = portWidthPlusPortPortSpacing(nodeContext, portSide, true, false);
                
            } else {
                // Setup the port margins to include port labels
                setHorizontalPortMargins(nodeContext, portSide, false, 0);
                
                // Sum up the space required
                width = portWidthPlusPortPortSpacing(nodeContext, portSide, true, false);
            }
            
        } else if (!portLabelsOutside) {
            // Each port is centered above / below its label, so it contributes either itself and port-port spacing
            // or its label's width plus port-port spacing to the whole requested width
            if (uniformPortSpacing) {
                // Since ports could in theory be bigger than labels
                int ports = nodeContext.portContexts.get(portSide).size() - 1;
                double maxPortOrLabelWidth = maximumPortOrLabelWidth(nodeContext, portSide);
                width = maxPortOrLabelWidth * ports + nodeContext.portPortSpacing * (ports - 1);
                
                // If there is a maximum label width, setup the port margins accordingly
                if (maxPortOrLabelWidth > 0) {
                    setHorizontalPortMargins(nodeContext, portSide, true, maxPortOrLabelWidth);
                }
                
            } else {
                // Setup the port margins to include port labels
                setHorizontalPortMargins(nodeContext, portSide, true, 0);
                
                // Sum up the space required
                width = portWidthPlusPortPortSpacing(nodeContext, portSide, true, true);
            }
            
        } else {
            // I don't think this case should ever be reachable.
            assert false;
        }
        
        // Set the cell size 
        cell.getMinimumContentAreaSize().x = width;
    }

    /**
     * Finds the maximum width of any port label on the given port side or 0 if there weren't any port labels or ports.
     */
    private static double maximumPortLabelWidth(final NodeContext nodeContext, final PortSide portSide) {
        return nodeContext.portContexts.get(portSide).stream()
                .mapToDouble(portContext -> portContext.portLabelCell == null
                        ? 0
                        : portContext.portLabelCell.getMinimumWidth())
                .max()
                .orElse(0);
    }
    
    /**
     * Finds the maximum width of any port or port label on the given port side or 0 if there weren't any port labels
     * or ports.
     */
    private static double maximumPortOrLabelWidth(final NodeContext nodeContext, final PortSide portSide) {
        double maxResult = 0.0;
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            if (portContext.portLabelCell != null) {
                double labelWidth = portContext.portLabelCell.getMinimumWidth();
                
                if (labelWidth > maxResult) {
                    maxResult = labelWidth;
                }
            }
            
            if (portContext.port.getSize().x > maxResult) {
                maxResult = portContext.port.getSize().x;
            }
        }
        
        return maxResult;
    }

    /**
     * Takes all ports on the given side, sums up their width, and inserts port-port spacings between them. The margins
     * of ports can be included as well to include space used for labels.
     */
    private static double portWidthPlusPortPortSpacing(final NodeContext nodeContext, final PortSide portSide,
            final boolean includeMargins, final boolean includeMarginsOfLastPort) {
        
        double result = 0;
        
        Iterator<PortContext> portContextIterator = nodeContext.portContexts.get(portSide).iterator();
        while (portContextIterator.hasNext()) {
            PortContext portContext = portContextIterator.next();
            
            // Add the current port's width to our result
            result += portContext.port.getSize().x;
            
            // If we are to include the margins of the current port, do so
            if (includeMargins && (portContextIterator.hasNext() || includeMarginsOfLastPort)) {
                result += portContext.portMargin.left + portContext.portMargin.right;
            }
            
            // If there is another port after this one, include the required spacing
            if (portContextIterator.hasNext()) {
                result += nodeContext.portPortSpacing;
            }
        }
        
        return result;
    }
    
    /**
     * Sets the port margins such that they include the space required for labels. If {@code uniformLabelWidth > 0},
     * not the width of the port's labels is used, but the uniform width.
     */
    private static void setHorizontalPortMargins(final NodeContext nodeContext, final PortSide portSide,
            final boolean centered, final double uniformLabelWidth) {
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            double labelWidth = 0;
            if (uniformLabelWidth > 0) {
                labelWidth = uniformLabelWidth;
            } else if (portContext.portLabelCell != null) {
                labelWidth = portContext.portLabelCell.getMinimumWidth();
            }
            
            if (labelWidth > 0) {
                if (centered) {
                    double portWidth = portContext.port.getSize().x;
                    if (labelWidth > portWidth) {
                        double overhang = (labelWidth - portWidth) / 2;
                        portContext.portMargin.left = overhang;
                        portContext.portMargin.right = overhang;
                    }
                } else {
                    portContext.portMargin.right = nodeContext.portLabelSpacing + labelWidth;
                }
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Vertical Port Placement
    
    /**
     * Calculates the space required for horizontal port placements. If the port placement is not fixed, this will
     * also setup the top and bottom padding of the inside port label placement cells to reflect the amount of space
     * to be left around the port placement.
     */
    public static void calculateVerticalPortPlacementSize(final NodeContext nodeContext) {
        // We need to calculate the space required by the ports even if ports are not part of the size constraints,
        // since we will use that later to place them
        switch (nodeContext.portConstraints) {
        case FIXED_POS:
            // We don't have any freedom at all, so simply calculate where the bottommost port is on each side
            calculateVerticalNodeSizeRequiredByFixedPosPorts(nodeContext, PortSide.EAST);
            calculateVerticalNodeSizeRequiredByFixedPosPorts(nodeContext, PortSide.WEST);
            break;
            
        case FIXED_RATIO:
            // We can require the node to be large enough to avoid spacing violations with fixed ratio ports
            // TODO Implement
            break;
            
        default:
            // If we are free to place things, make the node large enough to place everything properly
            calculateVerticalNodeSizeRequiredByFreePorts(nodeContext, PortSide.EAST);
            calculateVerticalNodeSizeRequiredByFreePorts(nodeContext, PortSide.WEST);
            break;
        }
    }
    
    /**
     * Sets up inside port label spaces for fixed position ports. If the node size constraints include ports, we also
     * mess with the cell padding.
     */
    private static void calculateVerticalNodeSizeRequiredByFixedPosPorts(final NodeContext nodeContext,
            final PortSide portSide) {
        
        double bottommostPortBorder = 0.0;
        
        // Check all ports on the correct side
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            bottommostPortBorder = Math.max(
                    bottommostPortBorder,
                    portContext.port.getPosition().y + portContext.port.getSize().y);
        }
        
        // Set the cell size and remove top padding since the cell size itself already includes all the space we need
        AtomicCell cell = nodeContext.insidePortLabelCells.get(portSide);
        cell.getPadding().top = 0;
        cell.getMinimumContentAreaSize().y = bottommostPortBorder;
    }
    
    /**
     * Sets up inside port label spaces for free ports.
     */
    private static void calculateVerticalNodeSizeRequiredByFreePorts(final NodeContext nodeContext,
            final PortSide portSide) {

        AtomicCell cell = nodeContext.insidePortLabelCells.get(portSide);
        
        // Handle the common case first: if there are no ports, set everything to zero
        if (nodeContext.portContexts.get(portSide).isEmpty()) {
            cell.getPadding().left = 0;
            cell.getPadding().right = 0;
            return;
        }
        
        // A few convenience variables
        boolean includePortLabels = nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS);
        boolean twoPorts = nodeContext.portContexts.get(portSide).size() == 2;
        boolean portLabelsOutside = nodeContext.portLabelsPlacement == PortLabelPlacement.OUTSIDE;
        boolean uniformPortSpacing = nodeContext.sizeOptions.contains(SizeOptions.UNIFORM_PORT_SPACING);
        double height = 0.0;
        
        // How we need to calculate things really depends on which situation we find ourselves in...
        if (!includePortLabels || (twoPorts && portLabelsOutside)) {
            // We ignore port labels or we have only two ports whose labels won't be placed between them. The space
            // between the ports is a function of their combined height, number, and the port-port spacing
            height = portHeightPlusPortPortSpacing(nodeContext, portSide, false, false);
            
        } else if (portLabelsOutside) {
            // Each port contributes its own amount of space, along with its labels (except for the bottommost port).
            // If uniform port spacing is requested, we need to apply the highest label height to every port
            if (uniformPortSpacing) {
                double maxLabelHeight = maximumPortLabelHeight(nodeContext, portSide);
                
                // If there is a maximum label width, setup the port margins accordingly
                if (maxLabelHeight > 0) {
                    setVerticalPortMargins(nodeContext, portSide, false, maxLabelHeight);
                }
                
                // Sum up the height of all ports including their margins, except for the last part (its label is not
                // part of the port area anymore ans is allowed to overhang)
                height = portHeightPlusPortPortSpacing(nodeContext, portSide, true, false);
                
            } else {
                // Setup the port margins to include port labels
                setVerticalPortMargins(nodeContext, portSide, false, 0);
                
                // Sum up the space required
                height = portHeightPlusPortPortSpacing(nodeContext, portSide, true, false);
            }
            
        } else if (!portLabelsOutside) {
            // Each port is centered left / right of its label, so it contributes either itself and port-port spacing
            // or its label's height plus port-port spacing to the whole requested height
            if (uniformPortSpacing) {
                // Since ports could in theory be bigger than labels
                int ports = nodeContext.portContexts.get(portSide).size() - 1;
                double maxPortOrLabelHeight = maximumPortOrLabelHeight(nodeContext, portSide);
                height = maxPortOrLabelHeight * ports + nodeContext.portPortSpacing * (ports - 1);
                
                // If there is a maximum label height, setup the port margins accordingly
                if (maxPortOrLabelHeight > 0) {
                    setVerticalPortMargins(nodeContext, portSide, true, maxPortOrLabelHeight);
                }
                
            } else {
                // Setup the port margins to include port labels
                setVerticalPortMargins(nodeContext, portSide, true, 0);
                
                // Sum up the space required
                height = portHeightPlusPortPortSpacing(nodeContext, portSide, true, true);
            }
            
        } else {
            // I don't think this case should ever be reachable.
            assert false;
        }
        
        // Set the cell size 
        cell.getMinimumContentAreaSize().y = height;
    }
    
    /**
     * Finds the maximum height of any port label on the given port side or 0 if there weren't any port labels or ports.
     */
    private static double maximumPortLabelHeight(final NodeContext nodeContext, final PortSide portSide) {
        return nodeContext.portContexts.get(portSide).stream()
                .mapToDouble(portContext -> portContext.portLabelCell == null
                        ? 0
                        : portContext.portLabelCell.getMinimumHeight())
                .max()
                .orElse(0);
    }
    
    /**
     * Finds the maximum height of any port or port label on the given port side or 0 if there weren't any port labels
     * or ports.
     */
    private static double maximumPortOrLabelHeight(final NodeContext nodeContext, final PortSide portSide) {
        double maxResult = 0.0;
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            double labelHeight = portContext.portLabelCell.getMinimumHeight();
            
            if (labelHeight > maxResult) {
                maxResult = labelHeight;
            }
            
            if (portContext.port.getSize().y > maxResult) {
                maxResult = portContext.port.getSize().y;
            }
        }
        
        return maxResult;
    }

    /**
     * Takes all ports on the given side, sums up their height, and inserts port-port spacings between them. The margins
     * of ports can be included as well to include space used for labels.
     */
    private static double portHeightPlusPortPortSpacing(final NodeContext nodeContext, final PortSide portSide,
            final boolean includeMargins, final boolean includeMarginsOfLastPort) {
        
        double result = 0;
        
        Iterator<PortContext> portContextIterator = nodeContext.portContexts.get(portSide).iterator();
        while (portContextIterator.hasNext()) {
            PortContext portContext = portContextIterator.next();
            
            // Add the current port's height to our result
            result += portContext.port.getSize().y;
            
            // If we are to include the margins of the current port, do so
            if (includeMargins && (portContextIterator.hasNext() || includeMarginsOfLastPort)) {
                result += portContext.portMargin.top + portContext.portMargin.bottom;
            }
            
            // If there is another port after this one, include the required spacing
            if (portContextIterator.hasNext()) {
                result += nodeContext.portPortSpacing;
            }
        }
        
        return result;
    }
    
    /**
     * Sets the port margins such that they include the space required for labels. If {@code uniformLabelHeight > 0},
     * not the height of the port's labels is used, but the uniform height.
     */
    private static void setVerticalPortMargins(final NodeContext nodeContext, final PortSide portSide,
            final boolean centered, final double uniformLabelHeight) {
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            double labelHeight = 0;
            if (uniformLabelHeight > 0) {
                labelHeight = uniformLabelHeight;
            } else if (portContext.portLabelCell != null) {
                labelHeight = portContext.portLabelCell.getMinimumHeight();
            }
            
            if (labelHeight > 0) {
                if (centered) {
                    double portHeight = portContext.port.getSize().y;
                    if (labelHeight > portHeight) {
                        double overhang = (labelHeight - portHeight) / 2;
                        portContext.portMargin.top = overhang;
                        portContext.portMargin.bottom = overhang;
                    }
                } else {
                    portContext.portMargin.bottom = nodeContext.portLabelSpacing + labelHeight;
                }
            }
        }
    }
    
}
