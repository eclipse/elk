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
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.nodespacing.internal.contexts.NodeContext;
import org.eclipse.elk.core.util.nodespacing.internal.contexts.PortContext;

/**
 * Various little methods that didn't quite fit into any of the other classes.
 */
public final class NodeLabelAndSizeUtilities {

    /**
     * No instance required.
     */
    private NodeLabelAndSizeUtilities() {
        
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Algorithm Phase Implementation Fragments
    
    /**
     * If the client area should have a minimum size, the inside node label container is setup accordingly. This does
     * not yet set any size contribution constraints, but only applies the minimum size itself.
     */
    public static void setupMinimumClientAreaSize(final NodeContext nodeContext) {
        KVector minSize = getMinimumClientAreaSize(nodeContext);
        if (minSize != null) {
            nodeContext.insideNodeLabelContainer.setCenterCellMinimumSize(minSize);
        }
    }
    
    /**
     * Sets up the padding of the main node cell to account for ports that extend inside the node.
     */
    public static void setupNodePaddingForPortsWithOffset(final NodeContext nodeContext) {
        ElkPadding nodeCellPadding = nodeContext.nodeContainer.getPadding();
        
        for (PortContext portContext : nodeContext.portContexts.values()) {
            // If the port extends into the node, ensure the inside port space is enough
            if (portContext.port.hasProperty(CoreOptions.PORT_BORDER_OFFSET)) {
                double portBorderOffset = portContext.port.getProperty(CoreOptions.PORT_BORDER_OFFSET);
                
                if (portBorderOffset < 0) {
                    // The port does extend into the node, by -portBorderOffset
                    switch (portContext.port.getSide()) {
                    case NORTH:
                        nodeCellPadding.top = Math.max(nodeCellPadding.top, -portBorderOffset);
                        break;
                        
                    case SOUTH:
                        nodeCellPadding.bottom = Math.max(nodeCellPadding.bottom, -portBorderOffset);
                        break;
                        
                    case EAST:
                        nodeCellPadding.right = Math.max(nodeCellPadding.right, -portBorderOffset);
                        break;
                        
                    case WEST:
                        nodeCellPadding.left = Math.max(nodeCellPadding.left, -portBorderOffset);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Offsets all southern ports, previously placed relative to vertical coordinate 0, by the node's size to place
     * them along the node's southern border.
     */
    public static void offsetSouthernPortsByNodeSize(final NodeContext nodeContext) {
        double nodeHeight = nodeContext.node.getSize().y;
        
        for (PortContext portContext : nodeContext.portContexts.get(PortSide.SOUTH)) {
            KVector portPosition = portContext.port.getPosition();
            portPosition.y += nodeHeight;
            portContext.port.setPosition(portPosition);
        }
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Minimum Size Things
    
    /**
     * Returns the client area's minimum size if size constraints are configured such that the client area has a
     * minimum size. Otherwise, returns {@code null}.
     */
    public static KVector getMinimumClientAreaSize(final NodeContext nodeContext) {
        if (nodeContext.sizeConstraints.contains(SizeConstraint.MINIMUM_SIZE)
                && nodeContext.sizeOptions.contains(SizeOptions.MINIMUM_SIZE_ACCOUNTS_FOR_PADDING)) {
            
            return getMinimumNodeOrClientAreaSize(nodeContext);
            
        } else {
            return null;
        }
    }
    
    /**
     * Returns the node's minimum size if size constraints are configured such that the node as a whole has a minimum
     * size. Otherwise, returns {@code null}.
     */
    public static KVector getMinimumNodeSize(final NodeContext nodeContext) {
        if (nodeContext.sizeConstraints.contains(SizeConstraint.MINIMUM_SIZE)
                && !nodeContext.sizeOptions.contains(SizeOptions.MINIMUM_SIZE_ACCOUNTS_FOR_PADDING)) {
            
            return getMinimumNodeOrClientAreaSize(nodeContext);
            
        } else {
            return null;
        }
    }
    
    /**
     * Returns the minimum size configured for the node, without regard for size constraints.
     */
    public static KVector getMinimumNodeOrClientAreaSize(final NodeContext nodeContext) {
        // Retrieve the minimum size
        KVector minSize = new KVector(nodeContext.node.getProperty(CoreOptions.NODE_SIZE_MINIMUM));
        
        // If we are instructed to revert to a default minimum size, we check whether we need to revert to that
        if (nodeContext.sizeOptions.contains(SizeOptions.DEFAULT_MINIMUM_SIZE)) {
            if (minSize.x <= 0) {
                minSize.x = ElkUtil.DEFAULT_MIN_WIDTH;
            }

            if (minSize.y <= 0) {
                minSize.y = ElkUtil.DEFAULT_MIN_HEIGHT;
            }
        }
        
        return minSize;
    }
    
}
