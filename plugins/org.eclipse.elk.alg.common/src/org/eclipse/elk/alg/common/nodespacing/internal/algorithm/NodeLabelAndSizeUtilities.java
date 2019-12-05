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

import java.util.EnumSet;

import org.eclipse.elk.alg.common.nodespacing.internal.NodeContext;
import org.eclipse.elk.alg.common.nodespacing.internal.PortContext;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.ElkUtil;

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
        double nodeHeight = nodeContext.nodeSize.y;
        
        for (PortContext portContext : nodeContext.portContexts.get(PortSide.SOUTH)) {
            KVector portPosition = portContext.portPosition;
            portPosition.y += nodeHeight;
        }
    }
    
    /**
     * Calculates and stores the node padding, if requested by layout options.
     */
    public static void setNodePadding(final NodeContext nodeContext) {
        if (!nodeContext.sizeOptions.contains(SizeOptions.COMPUTE_PADDING)) {
            return;
        }
        
        ElkRectangle nodeRect = nodeContext.nodeContainer.getCellRectangle();
        ElkRectangle clientArea = nodeContext.insideNodeLabelContainer.getCenterCellRectangle();
        ElkPadding nodePadding = new ElkPadding();
        
        // The following code assumes that the client area rectangle lies fully inside the node rectangle, which should
        // always be the case because of how the client area rectangle is computed
        nodePadding.left = clientArea.x - nodeRect.x;
        nodePadding.top = clientArea.y - nodeRect.y;
        nodePadding.right = (nodeRect.x + nodeRect.width) - (clientArea.x + clientArea.width);
        nodePadding.bottom = (nodeRect.y + nodeRect.height) - (clientArea.y + clientArea.height);
        
        nodeContext.node.setPadding(nodePadding);
    }

    /**
     * Applies the calculated node size to the node and the calculated port positions to the ports.
     */
    public static void applyStuff(final NodeContext nodeContext) {
        nodeContext.applyNodeSize();
        nodeContext.portContexts.values().stream().forEach(pc -> pc.applyPortPosition());
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
        if (nodeContext.sizeConstraints.contains(SizeConstraint.MINIMUM_SIZE)) {
            if (!nodeContext.sizeOptions.contains(SizeOptions.MINIMUM_SIZE_ACCOUNTS_FOR_PADDING)) {
                return getMinimumNodeOrClientAreaSize(nodeContext);
            }
        }
        
        return null;
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
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utilities
    
    /** Size constraints that only contain port labels should not cause a node to resize. */
    private static final EnumSet<SizeConstraint> EFFECTIVELY_FIXED_SIZE_CONSTRAINTS =
            EnumSet.of(SizeConstraint.PORT_LABELS);
    
    /**
     * Checks if the size constraints, even if not empty, should cause the node not to be resized. This is the case if
     * the constraints are either empty or contain only {@link SizeConstraint#PORT_LABELS}.
     */
    public static boolean areSizeConstraintsFixed(final NodeContext nodeContext) {
        return nodeContext.sizeConstraints.isEmpty()
                || (nodeContext.sizeConstraints.equals(EFFECTIVELY_FIXED_SIZE_CONSTRAINTS));
    }
    
    /**
     * Checks whether compound node mode should be active for the given part. Depending on the configuration, the user
     * may want us to only offset port labels if their port actually has a connection to the inside. That's basically
     * what this method is all about.
     */
    public static boolean effectiveCompoundNodeMode(final PortContext portContext) {
        if (!portContext.parentNodeContext.treatAsCompoundNode) {
            // Compound node mode was already off, so spare us the hassle
            return false;
            
        } else if (portContext.parentNodeContext.node.getProperty(CoreOptions.PORT_LABELS_NEXT_TO_PORT_IF_POSSIBLE)) {
            // Compound node more is active _and_ the presence of connections to the inside actually makes a difference
            return portContext.port.hasCompoundConnections();
            
        } else {
            // Compound node mode is active, but we treat all ports as if they had connections to the inside
            return true;
        }
    }
    
}
