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
package org.eclipse.elk.core.util.nodespacing.internal;

import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.options.SizeConstraint;
import org.eclipse.elk.core.options.SizeOptions;
import org.eclipse.elk.core.util.ElkUtil;

/**
 * Knows how to calculate the final width and height of a node based on size constraints and all the placement data
 * calculated so far.
 */
public final class FinalNodeSizeCalculator {

    /**
     * This class is not meant to be instantiated.
     */
    private FinalNodeSizeCalculator() {
        
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NODE WIDTH
    
    /**
     * Sets the node's width based on whatever the size constraints are.
     */
    public static void setNodeWidth(final NodeContext nodeContext) {
        if (nodeContext.sizeConstraints.isEmpty()) {
            // "When Little Jimmy discovered that there was no actual work to be done, he felt glad to be alive and
            // went to torture helpless rabbits." -- Anonymous
            return;
        }
        
        double width = 0.0;
        
        // TODO Take client area into account
        
        if (nodeContext.sizeConstraints.contains(SizeConstraint.NODE_LABELS)) {
            // The inside node label columns give us an idea of how large our node needs to be
            if (nodeContext.insideNodeLabelColumns.countExistingThings() > 0) {
                width = nodeContext.insideNodeLabelColumns.getSize();
            }
            
            if (width > 0) {
                width += nodeContext.nodeLabelsPadding.left + nodeContext.nodeLabelsPadding.right;
            }
            
            if (!nodeContext.sizeOptions.contains(SizeOptions.OUTSIDE_NODE_LABELS_OVERHANG)) {
                width = Math.max(width, nodeContext.outsideTopNodeLabelColumns.getSize());
                width = Math.max(width, nodeContext.outsideBottomNodeLabelColumns.getSize());
            }
        }
        
        // If we have inside eastern / western inside port labels, we need to reserve space for those
        if (nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS)) {
            ElkRectangle eastInsidePortLabelArea = nodeContext.insidePortLabelAreas.get(PortSide.EAST);
            if (eastInsidePortLabelArea != null && eastInsidePortLabelArea.width > 0) {
                width += eastInsidePortLabelArea.width + nodeContext.portLabelSpacing;
            }
            
            ElkRectangle westInsidePortLabelArea = nodeContext.insidePortLabelAreas.get(PortSide.WEST);
            if (westInsidePortLabelArea != null && westInsidePortLabelArea.width > 0) {
                width += westInsidePortLabelArea.width + nodeContext.portLabelSpacing;
            }
        }
        
        // If we should reserve space for ports, ...
        if (nodeContext.sizeConstraints.contains(SizeConstraint.PORTS)) {
            // ...include space reserved for ports set into the node
            width += nodeContext.insidePortSpace.left + nodeContext.insidePortSpace.right;
            
            // ...make sure horizontal ports have enough space to be placed
            double widthRequiredByPorts = Math.max(
                    nodeContext.nodeWidthRequiredByNorthPorts, nodeContext.nodeWidthRequiredBySouthPorts);
            width = Math.max(width, widthRequiredByPorts);
        }
        
        // If we have a minimum node width, apply that
        if (nodeContext.sizeConstraints.contains(SizeConstraint.MINIMUM_SIZE)
                && !nodeContext.sizeOptions.contains(SizeOptions.MINIMUM_SIZE_ACCOUNTS_FOR_PADDING)) {
            
            // Retrieve the minimum size
            KVector minSize = new KVector(nodeContext.node.getProperty(CoreOptions.NODE_SIZE_MINIMUM));
            
            // If we are instructed to revert to a default minimum size, we check whether we need to revert to that
            if (nodeContext.sizeOptions.contains(SizeOptions.DEFAULT_MINIMUM_SIZE)) {
                if (minSize.x <= 0) {
                    minSize.x = ElkUtil.DEFAULT_MIN_WIDTH;
                }
            }
            
            // Only apply the minimum size if there actually is one
            if (minSize.x > 0) {
                width = Math.max(width, minSize.x);
            }
        }
        
        // Apply new width
        nodeContext.node.setSize(new KVector(width, nodeContext.node.getSize().y));
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NODE HEIGHT
    
    /**
     * Sets the node's height based on whatever the size constraints are.
     */
    public static void setNodeHeight(final NodeContext nodeContext) {
        if (nodeContext.sizeConstraints.isEmpty()) {
            // "As they finally climbed the last of the hills and looked upon what they had craved to reach for so
            // long, they found the land to be barren." -- Anonymous
            return;
        }
        
        double height = 0.0;
        
        // We might need to update the node height required by eastern and western ports to avoid collisions with
        // northern and southern inside port labels
        updateNodeHeightRequiredByEastWestPorts(nodeContext);
        
        if (nodeContext.sizeConstraints.contains(SizeConstraint.NODE_LABELS)) {
            // The inside node label rows give us an idea of how large our node needs to be
            if (nodeContext.insideNodeLabelRows.countExistingThings() > 0) {
                height = nodeContext.insideNodeLabelRows.getSize();
            }
            
            if (height > 0) {
                height += nodeContext.nodeLabelsPadding.top + nodeContext.nodeLabelsPadding.bottom;
            }
            
            if (!nodeContext.sizeOptions.contains(SizeOptions.OUTSIDE_NODE_LABELS_OVERHANG)) {
                height = Math.max(height, nodeContext.outsideLeftNodeLabelRows.getSize());
                height = Math.max(height, nodeContext.outsideRightNodeLabelRows.getSize());
            }
        }
        
        // If we have inside northern / southern inside port labels, we need to reserve space for those
        if (nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS)) {
            ElkRectangle northInsidePortLabelArea = nodeContext.insidePortLabelAreas.get(PortSide.NORTH);
            if (northInsidePortLabelArea != null && northInsidePortLabelArea.height > 0) {
                height += northInsidePortLabelArea.height + nodeContext.portLabelSpacing;
            }
            
            ElkRectangle southInsidePortLabelArea = nodeContext.insidePortLabelAreas.get(PortSide.SOUTH);
            if (southInsidePortLabelArea != null && southInsidePortLabelArea.height > 0) {
                height += southInsidePortLabelArea.height + nodeContext.portLabelSpacing;
            }
        }
        
        // If we should reserve space for ports, ...
        if (nodeContext.sizeConstraints.contains(SizeConstraint.PORTS)) {
            // ...include space reserved for ports set into the node
            height += nodeContext.insidePortSpace.top + nodeContext.insidePortSpace.bottom;
            
            // ...make sure vertical ports have enough space to be placed
            double heightRequiredByPorts = Math.max(
                    nodeContext.nodeHeightRequiredByEastPorts, nodeContext.nodeHeightRequiredByWestPorts);
            height = Math.max(height, heightRequiredByPorts);
        }
        
        // If we have a minimum node height, apply that
        if (nodeContext.sizeConstraints.contains(SizeConstraint.MINIMUM_SIZE)
                && !nodeContext.sizeOptions.contains(SizeOptions.MINIMUM_SIZE_ACCOUNTS_FOR_PADDING)) {
            
            // Retrieve the minimum size
            KVector minSize = new KVector(nodeContext.node.getProperty(CoreOptions.NODE_SIZE_MINIMUM));
            
            // If we are instructed to revert to a default minimum size, we check whether we need to revert to that
            if (nodeContext.sizeOptions.contains(SizeOptions.DEFAULT_MINIMUM_SIZE)) {
                if (minSize.y <= 0) {
                    minSize.y = ElkUtil.DEFAULT_MIN_HEIGHT;
                }
            }
            
            // Only apply the minimum size if there actually is one
            if (minSize.y > 0) {
                height = Math.max(height, minSize.y);
            }
        }
        
        // Apply new height
        nodeContext.node.setSize(new KVector(nodeContext.node.getSize().x, height));
    }


    /**
     * The node height required for east / west ports so far has not taken into account that we might need to avoid
     * collisions with northern / southern inside port labels. We might need to change that. We also update the top
     * and bottom port placement margins if we need to.
     */
    private static void updateNodeHeightRequiredByEastWestPorts(final NodeContext nodeContext) {
        // If we are not free to place ports the way we want to anyway, stop right there
        if (nodeContext.portConstraints.isRatioFixed() || nodeContext.portConstraints.isPosFixed()) {
            return;
        }
        
        // Likewise, if we are not to include port labels in our placement considerations, stop
        if (!nodeContext.sizeConstraints.contains(SizeConstraint.PORT_LABELS)) {
            return;
        }
        
        // North
        double requiredNorthPadding = nodeContext.insidePortSpace.top;
        
        ElkRectangle northInsidePortLabelArea = nodeContext.insidePortLabelAreas.get(PortSide.NORTH);
        if (northInsidePortLabelArea != null && northInsidePortLabelArea.height > 0) {
            requiredNorthPadding += northInsidePortLabelArea.height + nodeContext.portLabelSpacing;
        }
        
        double topPortMarginsDelta = requiredNorthPadding - nodeContext.surroundingPortMargins.top;
        if (topPortMarginsDelta > 0) {
            nodeContext.nodeHeightRequiredByEastPorts += topPortMarginsDelta;
            nodeContext.nodeHeightRequiredByWestPorts += topPortMarginsDelta;
            nodeContext.surroundingPortMargins.top = requiredNorthPadding;
        }
        
        // South
        double requiredSouthPadding = nodeContext.insidePortSpace.bottom;
        
        ElkRectangle southInsidePortLabelArea = nodeContext.insidePortLabelAreas.get(PortSide.SOUTH);
        if (southInsidePortLabelArea != null && southInsidePortLabelArea.height > 0) {
            requiredSouthPadding += southInsidePortLabelArea.height + nodeContext.portLabelSpacing;
        }
        
        double bottomPortMarginsDelta = requiredSouthPadding - nodeContext.surroundingPortMargins.bottom;
        if (bottomPortMarginsDelta > 0) {
            nodeContext.nodeHeightRequiredByEastPorts += bottomPortMarginsDelta;
            nodeContext.nodeHeightRequiredByWestPorts += bottomPortMarginsDelta;
            nodeContext.surroundingPortMargins.bottom = requiredSouthPadding;
        }
    }
    
}
