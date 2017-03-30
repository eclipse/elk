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

import java.util.Set;

import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.adapters.GraphAdapters.LabelAdapter;
import org.eclipse.elk.core.util.nodespacing.internal.NodeLabelLocation;
import org.eclipse.elk.core.util.nodespacing.internal.cells.ContainerArea;
import org.eclipse.elk.core.util.nodespacing.internal.cells.StripContainerCell;
import org.eclipse.elk.core.util.nodespacing.internal.cells.GridContainerCell;
import org.eclipse.elk.core.util.nodespacing.internal.cells.LabelCell;
import org.eclipse.elk.core.util.nodespacing.internal.cells.StripContainerCell.Strip;
import org.eclipse.elk.core.util.nodespacing.internal.contexts.NodeContext;

/**
 * Knows how to take all of a node's labels and create the appropriate grid cells.
 */
public final class NodeLabelCellCreator {
    
    /**
     * No instances required.
     */
    private NodeLabelCellCreator() {
        
    }
    
    
    /**
     * Iterates over all of the node's labels and creates all required cell containers and label cells.
     * 
     * @param nodeContext
     *            the node context to work with.
     */
    public static void createNodeLabelCells(final NodeContext nodeContext) {
        // Make sure all the relevant containers exist
        createNodeLabelCellContainers(nodeContext);
        
        // Handle each of the node's labels
        nodeContext.node.getLabels().forEach(label -> handleNodeLabel(nodeContext, label));
    }
    
    /**
     * Handles the given node label by adding it to the corresponding node label cell. If such a cell doesn't exist
     * yet, it is created and added to the correct container cell.
     */
    private static void handleNodeLabel(final NodeContext nodeContext, final LabelAdapter<?> label) {
        // Find the effective label location
        Set<NodeLabelPlacement> labelPlacement = label.hasProperty(CoreOptions.NODE_LABELS_PLACEMENT)
                ? label.getProperty(CoreOptions.NODE_LABELS_PLACEMENT)
                : nodeContext.nodeLabelPlacement;
        NodeLabelLocation labelLocation = NodeLabelLocation.fromNodeLabelPlacement(labelPlacement);
        
        // If the label has its location fixed, we will ignore it
        if (labelLocation == NodeLabelLocation.UNDEFINED) {
            return;
        }
        
        retrieveNodeLabelCell(nodeContext, labelLocation).addLabel(label);
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Cell Creation and Retrieval

    /**
     * Creates all node label containers.
     */
    private static void createNodeLabelCellContainers(final NodeContext nodeContext) {
        // Inside container
        nodeContext.insideNodeLabelContainer = new GridContainerCell(nodeContext.labelLabelSpacing * 2);
        if (nodeContext.nodeLabelsPadding != null) {
            nodeContext.insideNodeLabelContainer.getPadding().copy(nodeContext.nodeLabelsPadding);
        }
        nodeContext.nodeContainerMiddleRow.setCell(ContainerArea.CENTER, nodeContext.insideNodeLabelContainer);
        
        // Outside containers
        StripContainerCell northContainer = new StripContainerCell(Strip.HORIZONTAL, nodeContext.labelLabelSpacing * 2);
        northContainer.getPadding().bottom = nodeContext.nodeLabelSpacing;
        nodeContext.outsideNodeLabelContainers.put(PortSide.NORTH, northContainer);

        StripContainerCell southContainer = new StripContainerCell(Strip.HORIZONTAL, nodeContext.labelLabelSpacing * 2);
        southContainer.getPadding().top = nodeContext.nodeLabelSpacing;
        nodeContext.outsideNodeLabelContainers.put(PortSide.SOUTH, southContainer);

        StripContainerCell westContainer = new StripContainerCell(Strip.VERTICAL, nodeContext.labelLabelSpacing * 2);
        westContainer.getPadding().right = nodeContext.nodeLabelSpacing;
        nodeContext.outsideNodeLabelContainers.put(PortSide.WEST, westContainer);

        StripContainerCell eastContainer = new StripContainerCell(Strip.VERTICAL, nodeContext.labelLabelSpacing * 2);
        eastContainer.getPadding().left = nodeContext.nodeLabelSpacing;
        nodeContext.outsideNodeLabelContainers.put(PortSide.EAST, eastContainer);
    }
    
    /**
     * Retrieves the node label cell for the given location. If it doesn't exist yet, it is created.
     */
    private static LabelCell retrieveNodeLabelCell(final NodeContext nodeContext,
            final NodeLabelLocation nodeLabelLocation) {
        
        LabelCell nodeLabelCell = nodeContext.nodeLabelCells.get(nodeLabelLocation);
        
        if (nodeLabelCell == null) {
            // The node label cell doesn't exist yet, so create one and add it to the relevant container
            nodeLabelCell = new LabelCell(nodeContext.labelLabelSpacing, nodeLabelLocation);
            nodeContext.nodeLabelCells.put(nodeLabelLocation, nodeLabelCell);
            
            // Find the correct container and add the cell to it
            if (nodeLabelLocation.isInsideLocation()) {
                nodeContext.insideNodeLabelContainer.setCell(
                        nodeLabelLocation.getContainerRow(),
                        nodeLabelLocation.getContainerColumn(),
                        nodeLabelCell);
            } else {
                PortSide outsideSide = nodeLabelLocation.getOutsideSide();
                StripContainerCell containerCell = nodeContext.outsideNodeLabelContainers.get(outsideSide);
                
                switch (outsideSide) {
                case NORTH:
                case SOUTH:
                    nodeLabelCell.setContributesToMinimumHeight(true);
                    containerCell.setCell(nodeLabelLocation.getContainerColumn(), nodeLabelCell);
                    break;
                    
                case WEST:
                case EAST:
                    nodeLabelCell.setContributesToMinimumWidth(true);
                    containerCell.setCell(nodeLabelLocation.getContainerRow(), nodeLabelCell);
                    break;
                }
            }
        }
        
        return nodeLabelCell;
    }
    
}
