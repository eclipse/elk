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
import java.util.Iterator;

import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.nodespacing.internal.NodeContext;
import org.eclipse.elk.core.util.nodespacing.internal.PortContext;
import org.eclipse.elk.core.util.nodespacing.internal.cellsystem.AtomicCell;

/**
 * Calculates the space required to setup port labels.
 */
public final class VerticalPortPlacementSizeCalculator {

    /**
     * No instance required.
     */
    private VerticalPortPlacementSizeCalculator() {
        
    }
    
    
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
            calculateVerticalNodeSizeRequiredByFixedRatioPorts(nodeContext, PortSide.EAST);
            calculateVerticalNodeSizeRequiredByFixedRatioPorts(nodeContext, PortSide.WEST);
            break;
            
        default:
            // If we are free to place things, make the node large enough to place everything properly
            calculateVerticalNodeSizeRequiredByFreePorts(nodeContext, PortSide.EAST);
            calculateVerticalNodeSizeRequiredByFreePorts(nodeContext, PortSide.WEST);
            break;
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fixed Position
    
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
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fixed Ratio
    
    /**
     * Sets up inside port label spaces for fixed ratio ports. If the node size constraints include ports, we also
     * mess with the cell padding.
     */
    private static void calculateVerticalNodeSizeRequiredByFixedRatioPorts(final NodeContext nodeContext,
            final PortSide portSide) {
        
        AtomicCell cell = nodeContext.insidePortLabelCells.get(portSide);
        
        // Fetch the port contexts on the given side and abort if there are none
        Collection<PortContext> portContexts = nodeContext.portContexts.get(portSide);
        if (portContexts.isEmpty()) {
            cell.getPadding().top = 0;
            cell.getPadding().bottom = 0;
            return;
        }

        boolean includePortLabels = nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS);
        boolean portLabelsInside = nodeContext.portLabelsPlacement == PortLabelPlacement.INSIDE;
        double minHeight = 0;
        
        // Go over all pairs of consecutive ports
        Iterator<PortContext> portContextIterator = portContexts.iterator();
        PortContext previousPortContext = null;
        double previousPortRatio = 0;
        double previousPortHeight = 0;
        
        while (portContextIterator.hasNext()) {
            // Get the next port and find out things about it
            PortContext currentPortContext = portContextIterator.next();
            double currentPortRatio = currentPortContext.port.getProperty(
                    PortPlacementCalculator.PORT_RATIO_OR_POSITION);
            double currentPortHeight = currentPortContext.port.getSize().y;
            
            // If port labels are to be respected, we need to calculate the port's margins to do so
            if (includePortLabels) {
                setVerticalPortMargins(nodeContext, portSide, portLabelsInside, 0);
            }
            
            if (previousPortContext == null) {
                // This is the first port, so find out how high the node needs to be to respect the top surrounding
                // port margins, if any
                if (nodeContext.surroundingPortMargins != null && nodeContext.surroundingPortMargins.top > 0) {
                    minHeight = Math.max(
                            minHeight,
                            HorizontalPortPlacementSizeCalculator.minSizeRequiredToRespectSpacing(
                                    nodeContext.surroundingPortMargins.top + currentPortContext.portMargin.top,
                                    0,
                                    currentPortRatio));
                }
            } else {
                double requiredSpace = previousPortHeight + previousPortContext.portMargin.bottom
                        + nodeContext.portPortSpacing + currentPortContext.portMargin.top;
                minHeight = Math.max(
                        minHeight,
                        HorizontalPortPlacementSizeCalculator.minSizeRequiredToRespectSpacing(
                                requiredSpace,
                                previousPortRatio,
                                currentPortRatio));
            }
            
            // Our current port is going to be the previous port during the next iteration
            previousPortContext = currentPortContext;
            previousPortRatio = currentPortRatio;
            previousPortHeight = currentPortHeight;
        }
        
        // if there are bottom surrounding port margins, apply those as well
        if (nodeContext.surroundingPortMargins != null && nodeContext.surroundingPortMargins.bottom > 0) {
            // We're using the port's bare width here because we don't care about label sizes on the bottommost port
            double requiredSpace = previousPortHeight + nodeContext.surroundingPortMargins.bottom;
            
            // We're only interested in the port's bottom margin if it's label is placed inside
            if (portLabelsInside) {
                requiredSpace += previousPortContext.portMargin.bottom;
            }
            
            minHeight = Math.max(
                    minHeight,
                    HorizontalPortPlacementSizeCalculator.minSizeRequiredToRespectSpacing(
                            requiredSpace,
                            previousPortRatio,
                            1));
        }
        
        // Set the cell size and remove top padding since the cell size itself already includes all the space we need
        cell.getPadding().top = 0;
        cell.getMinimumContentAreaSize().y = minHeight;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Free
    
    /**
     * Sets up inside port label spaces for free ports.
     */
    private static void calculateVerticalNodeSizeRequiredByFreePorts(final NodeContext nodeContext,
            final PortSide portSide) {

        AtomicCell cell = nodeContext.insidePortLabelCells.get(portSide);
        
        // Handle the common case first: if there are no ports, set everything to zero
        if (nodeContext.portContexts.get(portSide).isEmpty()) {
            cell.getPadding().top = 0;
            cell.getPadding().bottom = 0;
            return;
        }
        
        // Set the padding to match the surrounding port space
        cell.getPadding().top = nodeContext.surroundingPortMargins.top;
        cell.getPadding().bottom = nodeContext.surroundingPortMargins.bottom;
        
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
