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

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.adapters.GraphAdapters.LabelAdapter;

/**
 * Knows how to place node labels.
 */
public final class NodeLabelPlacer {
    
    /**
     * This class is not meant to be instantiated.
     */
    private NodeLabelPlacer() {
        
    }
    
    
    /**
     * Calculates the effective node padding and places the node's labels.
     */
    public static void placeNodeLabels(final NodeContext nodeContext) {
        calculateEffectivePadding(nodeContext);
        
        ElkPadding effectiveNodePadding = nodeContext.effectiveNodePadding;
        KVector nodeSize = nodeContext.node.getSize();
        
        // Calculate how much space we have left to place things on the inside
        ElkRectangle insideNodeLabelSpace = new ElkRectangle();
        insideNodeLabelSpace.x = effectiveNodePadding.left;
        insideNodeLabelSpace.y = effectiveNodePadding.top;
        insideNodeLabelSpace.width = nodeSize.x - effectiveNodePadding.left - effectiveNodePadding.right;
        insideNodeLabelSpace.height = nodeSize.y - effectiveNodePadding.top - effectiveNodePadding.bottom;
        
        // Tell the rows and columns to enlarge themselves, if necessary
        enlargeLabelRowsAndColumns(nodeContext, insideNodeLabelSpace);
        
        // Place node labels
        placeInsideNodeLabels(nodeContext, insideNodeLabelSpace);
        placeOutsideNodeLabels(nodeContext);
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // SIZE CALCULATIONS

    /**
     * Calculates the effective node padding.
     */
    private static void calculateEffectivePadding(final NodeContext nodeContext) {
        // First, include the space required for ports extending into the node's insides
        nodeContext.effectiveNodePadding.copy(nodeContext.insidePortSpace);
        
        // For each side, if inside port label space was reserved, add that to the effective padding
        ElkRectangle northInsidePortLabelSpace = nodeContext.insidePortLabelAreas.get(PortSide.NORTH);
        if (northInsidePortLabelSpace != null && northInsidePortLabelSpace.height > 0) {
            nodeContext.effectiveNodePadding.top += northInsidePortLabelSpace.height + nodeContext.portLabelSpacing;
        }

        ElkRectangle southInsidePortLabelSpace = nodeContext.insidePortLabelAreas.get(PortSide.SOUTH);
        if (southInsidePortLabelSpace != null && southInsidePortLabelSpace.height > 0) {
            nodeContext.effectiveNodePadding.bottom += southInsidePortLabelSpace.height + nodeContext.portLabelSpacing;
        }

        ElkRectangle eastInsidePortLabelSpace = nodeContext.insidePortLabelAreas.get(PortSide.EAST);
        if (eastInsidePortLabelSpace != null && eastInsidePortLabelSpace.width > 0) {
            nodeContext.effectiveNodePadding.right += eastInsidePortLabelSpace.width + nodeContext.portLabelSpacing;
        }

        ElkRectangle westInsidePortLabelSpace = nodeContext.insidePortLabelAreas.get(PortSide.WEST);
        if (westInsidePortLabelSpace != null && westInsidePortLabelSpace.width > 0) {
            nodeContext.effectiveNodePadding.left += westInsidePortLabelSpace.width + nodeContext.portLabelSpacing;
        }
        
        // Add the inside node label spacing
        nodeContext.effectiveNodePadding.add(nodeContext.nodeLabelsPadding);
    }

    /**
     * Tells all of the {@link ThreeRowsOrColumns} instances to enlarge themselves.
     */
    private static void enlargeLabelRowsAndColumns(final NodeContext nodeContext,
            final ElkRectangle insideNodeLabelSpace) {
        
        KVector nodeSize = nodeContext.node.getSize();
        
        nodeContext.insideNodeLabelColumns.enlargeIfNecessary(insideNodeLabelSpace.width);
        nodeContext.insideNodeLabelRows.enlargeIfNecessary(insideNodeLabelSpace.height);
        
        nodeContext.outsideTopNodeLabelColumns.enlargeIfNecessary(nodeSize.x);
        nodeContext.outsideBottomNodeLabelColumns.enlargeIfNecessary(nodeSize.x);
        nodeContext.outsideLeftNodeLabelRows.enlargeIfNecessary(nodeSize.y);
        nodeContext.outsideRightNodeLabelRows.enlargeIfNecessary(nodeSize.y);
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INSIDE NODE LABEL PLACEMENT
    
    private static void placeInsideNodeLabels(final NodeContext nodeContext, final ElkRectangle insideNodeLabelSpace) {
        KVector topLeft = calculateInsideNodeLabelTopLeftPoint(nodeContext, insideNodeLabelSpace);
        
        // Assign rectangle widths and heights
        nodeContext.insideNodeLabelColumns.applyToRectangles(true, topLeft.x,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_T_L).labelPlacementArea,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_T_C).labelPlacementArea,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_T_R).labelPlacementArea);
        nodeContext.insideNodeLabelColumns.applyToRectangles(true, topLeft.x,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_C_L).labelPlacementArea,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_C_C).labelPlacementArea,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_C_R).labelPlacementArea);
        nodeContext.insideNodeLabelColumns.applyToRectangles(true, topLeft.x,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_B_L).labelPlacementArea,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_B_C).labelPlacementArea,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_B_R).labelPlacementArea);

        nodeContext.insideNodeLabelRows.applyToRectangles(false, topLeft.y,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_T_L).labelPlacementArea,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_C_L).labelPlacementArea,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_B_L).labelPlacementArea);
        nodeContext.insideNodeLabelRows.applyToRectangles(false, topLeft.y,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_T_C).labelPlacementArea,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_C_C).labelPlacementArea,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_B_C).labelPlacementArea);
        nodeContext.insideNodeLabelRows.applyToRectangles(false, topLeft.y,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_T_R).labelPlacementArea,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_C_R).labelPlacementArea,
                nodeContext.labelLocationContexts.get(LabelLocation.IN_B_R).labelPlacementArea);
        
        // LET THEM LABELS BE PLACED!!!
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.IN_T_L));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.IN_T_C));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.IN_T_R));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.IN_C_L));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.IN_C_C));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.IN_C_R));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.IN_B_L));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.IN_B_C));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.IN_B_R));
    }


    private static KVector calculateInsideNodeLabelTopLeftPoint(final NodeContext nodeContext,
            final ElkRectangle insideNodeLabelSpace) {
        
        // See how much space we will need to place all labels without overlaps
        double requiredWidth = nodeContext.insideNodeLabelColumns.getSize();
        double requiredHeight = nodeContext.insideNodeLabelRows.getSize();
        
        // Center point of the available inside node label space
        return new KVector(
                insideNodeLabelSpace.x + (insideNodeLabelSpace.width - requiredWidth) / 2,
                insideNodeLabelSpace.y + (insideNodeLabelSpace.height - requiredHeight) / 2);
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // OUTSIDE NODE LABEL PLACEMENT
    
    private static void placeOutsideNodeLabels(final NodeContext nodeContext) {
        KVector nodeSize = nodeContext.node.getSize();
        
        LabelLocationContext tlContext = nodeContext.labelLocationContexts.get(LabelLocation.OUT_T_L);
        tlContext.labelPlacementArea.width = tlContext.labelSpace.x;
        tlContext.labelPlacementArea.height = tlContext.labelSpace.y;
        tlContext.labelPlacementArea.x = 0;
        tlContext.labelPlacementArea.y = -nodeContext.nodeLabelSpacing - tlContext.labelPlacementArea.height;
        
        LabelLocationContext tcContext = nodeContext.labelLocationContexts.get(LabelLocation.OUT_T_C);
        tcContext.labelPlacementArea.width = tcContext.labelSpace.x;
        tcContext.labelPlacementArea.height = tcContext.labelSpace.y;
        tcContext.labelPlacementArea.x = (nodeSize.x - tcContext.labelPlacementArea.width) / 2;
        tcContext.labelPlacementArea.y = -nodeContext.nodeLabelSpacing - tcContext.labelPlacementArea.height;
        
        LabelLocationContext trContext = nodeContext.labelLocationContexts.get(LabelLocation.OUT_T_R);
        trContext.labelPlacementArea.width = trContext.labelSpace.x;
        trContext.labelPlacementArea.height = trContext.labelSpace.y;
        trContext.labelPlacementArea.x = nodeSize.x - trContext.labelPlacementArea.width;
        trContext.labelPlacementArea.y = -nodeContext.nodeLabelSpacing - trContext.labelPlacementArea.height;
        
        LabelLocationContext blContext = nodeContext.labelLocationContexts.get(LabelLocation.OUT_B_L);
        blContext.labelPlacementArea.width = blContext.labelSpace.x;
        blContext.labelPlacementArea.height = blContext.labelSpace.y;
        blContext.labelPlacementArea.x = 0;
        blContext.labelPlacementArea.y = nodeSize.y + nodeContext.nodeLabelSpacing;
        
        LabelLocationContext bcContext = nodeContext.labelLocationContexts.get(LabelLocation.OUT_B_C);
        bcContext.labelPlacementArea.width = bcContext.labelSpace.x;
        bcContext.labelPlacementArea.height = bcContext.labelSpace.y;
        bcContext.labelPlacementArea.x = (nodeSize.x - bcContext.labelPlacementArea.width) / 2;
        bcContext.labelPlacementArea.y = nodeSize.y + nodeContext.nodeLabelSpacing;
        
        LabelLocationContext brContext = nodeContext.labelLocationContexts.get(LabelLocation.OUT_B_R);
        brContext.labelPlacementArea.width = brContext.labelSpace.x;
        brContext.labelPlacementArea.height = brContext.labelSpace.y;
        brContext.labelPlacementArea.x = nodeSize.x - brContext.labelPlacementArea.width;
        brContext.labelPlacementArea.y = nodeSize.y + nodeContext.nodeLabelSpacing;
        
        LabelLocationContext ltContext = nodeContext.labelLocationContexts.get(LabelLocation.OUT_L_T);
        ltContext.labelPlacementArea.width = ltContext.labelSpace.x;
        ltContext.labelPlacementArea.height = ltContext.labelSpace.y;
        ltContext.labelPlacementArea.x = -nodeContext.nodeLabelSpacing - ltContext.labelPlacementArea.width;
        ltContext.labelPlacementArea.y = 0;
        
        LabelLocationContext lcContext = nodeContext.labelLocationContexts.get(LabelLocation.OUT_L_T);
        lcContext.labelPlacementArea.width = lcContext.labelSpace.x;
        lcContext.labelPlacementArea.height = lcContext.labelSpace.y;
        lcContext.labelPlacementArea.x = -nodeContext.nodeLabelSpacing - lcContext.labelPlacementArea.width;
        lcContext.labelPlacementArea.y = (nodeSize.y - lcContext.labelPlacementArea.height) / 2;
        
        LabelLocationContext lbContext = nodeContext.labelLocationContexts.get(LabelLocation.OUT_L_T);
        lbContext.labelPlacementArea.width = lbContext.labelSpace.x;
        lbContext.labelPlacementArea.height = lbContext.labelSpace.y;
        lbContext.labelPlacementArea.x = -nodeContext.nodeLabelSpacing - lbContext.labelPlacementArea.width;
        lbContext.labelPlacementArea.y = nodeSize.y - lbContext.labelPlacementArea.height;
        
        LabelLocationContext rtContext = nodeContext.labelLocationContexts.get(LabelLocation.OUT_R_T);
        rtContext.labelPlacementArea.width = rtContext.labelSpace.x;
        rtContext.labelPlacementArea.height = rtContext.labelSpace.y;
        rtContext.labelPlacementArea.x = nodeSize.x + nodeContext.nodeLabelSpacing;
        rtContext.labelPlacementArea.y = 0;
        
        LabelLocationContext rcContext = nodeContext.labelLocationContexts.get(LabelLocation.OUT_R_T);
        rcContext.labelPlacementArea.width = rcContext.labelSpace.x;
        rcContext.labelPlacementArea.height = rcContext.labelSpace.y;
        rcContext.labelPlacementArea.x = nodeSize.x + nodeContext.nodeLabelSpacing;
        rcContext.labelPlacementArea.y = (nodeSize.y - rcContext.labelPlacementArea.height) / 2;
        
        LabelLocationContext rbContext = nodeContext.labelLocationContexts.get(LabelLocation.OUT_R_T);
        rbContext.labelPlacementArea.width = rbContext.labelSpace.x;
        rbContext.labelPlacementArea.height = rbContext.labelSpace.y;
        rbContext.labelPlacementArea.x = nodeSize.x + nodeContext.nodeLabelSpacing;
        rbContext.labelPlacementArea.y = nodeSize.y - rbContext.labelPlacementArea.height;

        // LET THEM LABELS BE PLACED!!!
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.OUT_T_L));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.OUT_T_C));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.OUT_T_R));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.OUT_B_L));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.OUT_B_C));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.OUT_B_R));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.OUT_L_T));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.OUT_L_C));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.OUT_L_B));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.OUT_R_T));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.OUT_R_C));
        placeLabelLocationLabels(nodeContext.labelLocationContexts.get(LabelLocation.OUT_R_B));
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ACTUAL LABEL PLACEMENT CODE
    
    /**
     * Actually places all labels in the given location.
     */
    private static void placeLabelLocationLabels(final LabelLocationContext labelLocationContext) {
        // Alignments
        HorizontalLabelAlignment horizontalAlignment = labelLocationContext.labelLocation.getHorizontalAlignment();
        VerticalLabelAlignment verticalAlignment = labelLocationContext.labelLocation.getVerticalAlignment();
        
        // Find out where we will start placing labels
        double y = labelLocationContext.labelPlacementArea.y;
        
        switch (verticalAlignment) {
        case CENTER:
            y += (labelLocationContext.labelPlacementArea.height - labelLocationContext.labelSpace.y) / 2;
            break;
            
        case BOTTOM:
            y += labelLocationContext.labelPlacementArea.height - labelLocationContext.labelSpace.y;
            break;
        }
        
        // Place them labels!
        for (LabelAdapter<?> label : labelLocationContext.labels) {
            KVector labelSize = label.getSize();
            KVector labelPosition = new KVector(labelLocationContext.labelPlacementArea.x, y);
            
            switch (horizontalAlignment) {
            case CENTER:
                labelPosition.x += (labelLocationContext.labelPlacementArea.width - labelSize.x) / 2;
                break;
                
            case RIGHT:
                labelPosition.x += labelLocationContext.labelPlacementArea.width - labelSize.x;
                break;
            }
            
            label.setPosition(labelPosition);
            
            // Update y coordinate for the next label
            y += labelSize.y + labelLocationContext.nodeContext.labelLabelSpacing;
        }
    }
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
