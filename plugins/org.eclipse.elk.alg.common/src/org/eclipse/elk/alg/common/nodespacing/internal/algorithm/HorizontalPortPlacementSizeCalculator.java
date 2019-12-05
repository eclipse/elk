/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.common.nodespacing.internal.algorithm;

import java.util.Collection;
import java.util.Iterator;
import java.util.PrimitiveIterator;

import org.eclipse.elk.alg.common.nodespacing.cellsystem.AtomicCell;
import org.eclipse.elk.alg.common.nodespacing.internal.NodeContext;
import org.eclipse.elk.alg.common.nodespacing.internal.PortContext;
import org.eclipse.elk.core.options.PortAlignment;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;

import com.google.common.math.DoubleMath;

/**
 * Calculates the space required to setup port labels.
 */
public final class HorizontalPortPlacementSizeCalculator {

    /**
     * No instance required.
     */
    private HorizontalPortPlacementSizeCalculator() {
        
    }
    
    
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
            calculateHorizontalNodeSizeRequiredByFixedRatioPorts(nodeContext, PortSide.NORTH);
            calculateHorizontalNodeSizeRequiredByFixedRatioPorts(nodeContext, PortSide.SOUTH);
            break;
            
        default:
            // If we are free to place things, make the node large enough to place everything properly
            calculateHorizontalNodeSizeRequiredByFreePorts(nodeContext, PortSide.NORTH);
            calculateHorizontalNodeSizeRequiredByFreePorts(nodeContext, PortSide.SOUTH);
            break;
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fixed Position
    
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
                    portContext.portPosition.x + portContext.port.getSize().x);
        }
        
        // Set the cell size and remove left padding since the cell size itself already includes all the space we need
        AtomicCell cell = nodeContext.insidePortLabelCells.get(portSide);
        cell.getPadding().left = 0;
        cell.getMinimumContentAreaSize().x = rightmostPortBorder;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fixed Ratio
    
    /**
     * Sets up inside port label spaces for fixed ratio ports. If the node size constraints include ports, we also
     * mess with the cell padding.
     */
    private static void calculateHorizontalNodeSizeRequiredByFixedRatioPorts(final NodeContext nodeContext,
            final PortSide portSide) {
        
        AtomicCell cell = nodeContext.insidePortLabelCells.get(portSide);
        
        // Fetch the port contexts on the given side and abort if there are none
        Collection<PortContext> portContexts = nodeContext.portContexts.get(portSide);
        if (portContexts.isEmpty()) {
            cell.getPadding().left = 0;
            cell.getPadding().right = 0;
            return;
        }

        boolean includePortLabels = nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS);
        boolean spaceEfficientPortLabels = nodeContext.sizeOptions.contains(SizeOptions.SPACE_EFFICIENT_PORT_LABELS);
        boolean portLabelsInside = nodeContext.portLabelsPlacement == PortLabelPlacement.INSIDE;
        double minWidth = 0;
        
        // Go over all pairs of consecutive ports
        Iterator<PortContext> portContextIterator = portContexts.iterator();
        PortContext previousPortContext = null;
        double previousPortRatio = 0;
        double previousPortWidth = 0;
        
        while (portContextIterator.hasNext()) {
            // Get the next port and find out things about it
            PortContext currentPortContext = portContextIterator.next();
            double currentPortRatio = currentPortContext.port.getProperty(
                    PortPlacementCalculator.PORT_RATIO_OR_POSITION);
            double currentPortWidth = currentPortContext.port.getSize().x;
            
            // If port labels are to be respected, we need to calculate the port's margins to do so (we ignore the
            // first port in this process if space-efficient mode is active and port labels are to be placed outside)
            if (includePortLabels) {
                setHorizontalPortMargins(
                        nodeContext, portSide, portLabelsInside, !portLabelsInside && spaceEfficientPortLabels, 0);
            }
            
            if (previousPortContext == null) {
                // This is the first port, so find out how wide the node needs to be to respect the left surrounding
                // port margins, if any
                if (nodeContext.surroundingPortMargins != null && nodeContext.surroundingPortMargins.left > 0) {
                    minWidth = Math.max(
                            minWidth,
                            minSizeRequiredToRespectSpacing(
                                    nodeContext.surroundingPortMargins.left + currentPortContext.portMargin.left,
                                    0,
                                    currentPortRatio));
                }
            } else {
                double requiredSpace = previousPortWidth + previousPortContext.portMargin.right
                        + nodeContext.portPortSpacing + currentPortContext.portMargin.left;
                minWidth = Math.max(
                        minWidth,
                        minSizeRequiredToRespectSpacing(
                                requiredSpace,
                                previousPortRatio,
                                currentPortRatio));
            }
            
            // Our current port is going to be the previous port during the next iteration
            previousPortContext = currentPortContext;
            previousPortRatio = currentPortRatio;
            previousPortWidth = currentPortWidth;
        }
        
        // if there are right surrounding port margins, apply those as well
        if (nodeContext.surroundingPortMargins != null && nodeContext.surroundingPortMargins.right > 0) {
            double requiredSpace = previousPortWidth + nodeContext.surroundingPortMargins.right;
            
            // We're only interested in the port's right margin if it's label is placed inside
            if (portLabelsInside) {
                requiredSpace += previousPortContext.portMargin.right;
            }
            
            minWidth = Math.max(
                    minWidth,
                    minSizeRequiredToRespectSpacing(
                            requiredSpace,
                            previousPortRatio,
                            1));
        }
        
        // Set the cell size and remove left padding since the cell size itself already includes all the space we need
        cell.getPadding().left = 0;
        cell.getMinimumContentAreaSize().x = minWidth;
    }
    
    /** Fuzzyness allowed to still consider two double values to be equal. */
    private static final double EQUALITY_TOLERANCE = 0.01;
    
    /**
     * Returns the minimum width that will satisfy the given spacing between the two ratios multiplied by the result
     * width.
     * 
     * @param spacing
     *            the spacing to respect.
     * @param firstRatio
     *            position ratio of the first element.
     * @param secondRatio
     *            position ratio of the second element.
     * @return minimum width required to satisfy the spacing.
     */
    static double minSizeRequiredToRespectSpacing(final double spacing, final double firstRatio,
            final double secondRatio) {
        
        assert secondRatio >= firstRatio;
        
        // Some failsafing
        if (DoubleMath.fuzzyEquals(firstRatio, secondRatio, EQUALITY_TOLERANCE)) {
            return 0;
        } else {
            return spacing / (secondRatio - firstRatio);
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Free
    
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
        
        // Set the padding to match the surrounding port space
        cell.getPadding().left = nodeContext.surroundingPortMargins.left;
        cell.getPadding().right = nodeContext.surroundingPortMargins.right;
        
        // A few convenience variables (note that there is no notion of compound node mode here because in the free
        // case, we assume that every port will end up on either the western or the eastern side)
        boolean includePortLabels = nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS);
        boolean twoPorts = nodeContext.portContexts.get(portSide).size() == 2;
        boolean portLabelsOutside = nodeContext.portLabelsPlacement == PortLabelPlacement.OUTSIDE;
        boolean spaceEfficientPortLabels = nodeContext.sizeOptions.contains(SizeOptions.SPACE_EFFICIENT_PORT_LABELS);
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
                double maxLabelWidth = maximumPortLabelWidth(nodeContext, portSide, spaceEfficientPortLabels);
                
                // If there is a maximum label width, setup the port margins accordingly (we do not exclude the first
                // port here, even if space-efficient mode is active, because the whole idea of uniform port spacing
                // is to be symmetric)
                if (maxLabelWidth > 0) {
                    setHorizontalPortMargins(nodeContext, portSide, false, false, maxLabelWidth);
                }
                
                // Sum up the width of all ports including their margins, except for the last part (its label is not
                // part of the port area anymore ans is allowed to overhang)
                width = portWidthPlusPortPortSpacing(nodeContext, portSide, true, false);
                
            } else {
                // Setup the port margins to include port labels (exlude the first port if space-efficient mode is
                // active)
                setHorizontalPortMargins(nodeContext, portSide, false, spaceEfficientPortLabels, 0);
                
                // Sum up the space required
                width = portWidthPlusPortPortSpacing(nodeContext, portSide, true, false);
            }
            
        } else if (!portLabelsOutside) {
            // Each port is centered above / below its label, so it contributes either itself and port-port spacing
            // or its label's width plus port-port spacing to the whole requested width
            if (uniformPortSpacing) {
                // Since ports could in theory be bigger than labels
                int ports = nodeContext.portContexts.get(portSide).size();
                double maxPortOrLabelWidth = maximumPortOrLabelWidth(nodeContext, portSide);
                width = maxPortOrLabelWidth * ports + nodeContext.portPortSpacing * (ports - 1);
                
                // If there is a maximum label width, setup the port margins accordingly
                if (maxPortOrLabelWidth > 0) {
                    setHorizontalPortMargins(nodeContext, portSide, true, false, maxPortOrLabelWidth);
                }
                
            } else {
                // Setup the port margins to include port labels
                setHorizontalPortMargins(nodeContext, portSide, true, false, 0);
                
                // Sum up the space required
                width = portWidthPlusPortPortSpacing(nodeContext, portSide, true, true);
            }
            
        } else {
            // I don't think this case should ever be reachable.
            assert false;
        }

        // For distributed port alignment, we need to surround the ports by a port-port spacing on each side
        if (nodeContext.getPortAlignment(portSide) == PortAlignment.DISTRIBUTED) {
            width += 2 * nodeContext.portPortSpacing;
        }
        
        // Set the cell size 
        cell.getMinimumContentAreaSize().x = width;
    }

    /**
     * Finds the maximum width of any port label on the given port side or 0 if there weren't any port labels or ports.
     */
    private static double maximumPortLabelWidth(final NodeContext nodeContext, final PortSide portSide,
            final boolean ignoreFirstPort) {
        
        // Flag that we will set to false after having processed the first port
        boolean ignore = ignoreFirstPort;
        
        // Find width of port label cells
        PrimitiveIterator.OfDouble labelCellWidths = nodeContext.portContexts.get(portSide).stream()
            .mapToDouble(portContext -> portContext.portLabelCell == null
                    ? 0
                    : portContext.portLabelCell.getMinimumWidth())
            .iterator();
        
        // Find the maximum label width
        double maxLabelWidth = 0;
        while (labelCellWidths.hasNext()) {
            if (ignore) {
                // Ignore the first entry
                labelCellWidths.nextDouble();
                ignore = false;
                continue;
                
            } else {
                double currLabelWidth = labelCellWidths.nextDouble();
                
                // This is not the last port, which we will also always ignore
                if (labelCellWidths.hasNext()) {
                    maxLabelWidth = Math.max(maxLabelWidth, currLabelWidth);
                }
            }
        }
        
        return maxLabelWidth;
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
                maxResult = Math.max(maxResult, labelWidth);
            }
            
            maxResult = Math.max(maxResult, portContext.port.getSize().x);
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
     * not the width of the port's labels is used, but the uniform width. The first port can be excluded, which is
     * used if port labels are placed outside and space-efficient mode is active.
     */
    private static void setHorizontalPortMargins(final NodeContext nodeContext, final PortSide portSide,
            final boolean centered, final boolean excludeFirstPort, final double uniformLabelWidth) {
        
        // We'll reset this flag once we've excluded a port
        boolean exclude = excludeFirstPort;
        
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            if (exclude) {
                // Exclude the first port
                exclude = false;
                continue;
            }
            
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
    
}
