/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util.labelspacing;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.util.adapters.GraphAdapters.LabelAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.NodeAdapter;
import org.eclipse.elk.core.util.nodespacing.Rectangle;

/**
 * Utility class for node label space calculation.
 *
 * @author csp
 */
public final class LabelSpaceCalculation {
    
    // Prevent instantiation.
    private LabelSpaceCalculation() {
    }

    /**
     * Calculates the space required to accommodate the node labels (if any) and sets
     * {@link #requiredNodeLabelSpace} as well as {@link #nodeLabelsBoundingBox}. If inside labels are
     * placed at the top or at the bottom, the top or bottom insets are set. If it is centered
     * vertically, the left or right insets are set if the labels are horizontally aligned leftwards
     * or rightwards. If they are centered in both directions, no insets are set. If they are placed
     * outside the node, no insets are set.
     * 
     * @param node
     *            the node whose labels are to be placed.
     * @param labelSpacing
     *            the default label spacing.
     * @return the adjusted insets.
     */
    public static ElkPadding calculateRequiredNodeLabelSpace(final NodeAdapter<?> node,
            final double labelSpacing) {

        ElkPadding nodeLabelInsets = node.getProperty(CoreOptions.NODE_LABELS_PADDING);

        return calculateRequiredNodeLabelSpace(node, labelSpacing, nodeLabelInsets,
                new HashMap<LabelLocation, LabelGroup>(), new ElkPadding(node.getInsets()));
    }

    /**
     * Calculates the space required to accommodate the node labels (if any) and sets
     * {@link #requiredNodeLabelSpace} as well as {@link #nodeLabelsBoundingBox}. If inside labels are
     * placed at the top or at the bottom, the top or bottom insets are set. If it is centered
     * vertically, the left or right insets are set if the labels are horizontally aligned leftwards
     * or rightwards. If they are centered in both directions, no insets are set. If they are placed
     * outside the node, no insets are set.
     * 
     * @param node
     *            the node whose labels are to be placed.
     * @param labelSpacing
     *            the default label spacing.
     * @param nodeLabelInsets
     *            the additional insets for node labels on this node
     * @param labelGroupsBoundingBoxes
     *            map of locations to corresponding bounding boxes.
     * @param insets
     *            the insets to adjust.
     * @return the adjusted insets.
     */
    public static ElkPadding calculateRequiredNodeLabelSpace(final NodeAdapter<?> node,
            final double labelSpacing, final ElkPadding nodeLabelInsets,
            final Map<LabelLocation, LabelGroup> labelGroupsBoundingBoxes, final ElkPadding insets) {

        // Check if there are any labels
        if (!node.getLabels().iterator().hasNext()) {
            return insets;
        }
        
        // Retrieve the node's label placement policy
        final Set<NodeLabelPlacement> nodeLabelPlacement = node.getProperty(CoreOptions.NODE_LABELS_PLACEMENT);
        final LabelLocation nodeLabelLocation = LabelLocation.fromNodeLabelPlacement(nodeLabelPlacement);
        
        // Compute a bounding box for each location where labels should be placed.
        // The size is calculated from the size of all labels stacked vertically at that location.
        for (final LabelAdapter<?> label : node.getLabels()) {
            LabelLocation labelPlacement =
                    LabelLocation.fromNodeLabelPlacement(label.getProperty(CoreOptions.NODE_LABELS_PLACEMENT));
            
            // If no valid placement is set on the label, use the node's placement policy.
            if (labelPlacement == LabelLocation.UNDEFINED) {
                labelPlacement = nodeLabelLocation;
            }
            
            // Save the location of this label in its id field for later use.
            label.setVolatileId(labelPlacement.ordinal());
            
            // Create or retrieve the label group for the current label.
            final Rectangle boundingBox = retrieveLabelGroupsBoundingBox(labelGroupsBoundingBoxes, labelPlacement);
            boundingBox.width = Math.max(boundingBox.width, label.getSize().x);
            boundingBox.height += label.getSize().y + labelSpacing;
        }
        
        // We need to count different label placement boxes towards different kinds of insets, depending on whether
        // or not H_PRIORITY is set on the node itself (see H_PRIORITY documentation)
        boolean hPrio = nodeLabelPlacement.contains(NodeLabelPlacement.H_PRIORITY);
        
        // Calculate the node label space required inside the node (only label groups on the inside
        // are relevant here).
        for (final Entry<LabelLocation, LabelGroup> entry : labelGroupsBoundingBoxes.entrySet()) {
            final Rectangle boundingBox = entry.getValue();
            
            // From each existing label group, remove the last superfluous label spacing
            // (the mere existence of a label group implies that it contains at least one label)
            boundingBox.height -= labelSpacing;
            switch (entry.getKey()) {
            case IN_T_L:
                if (hPrio) {
                    insets.left = Math.max(
                            insets.left,
                            boundingBox.width + labelSpacing + nodeLabelInsets.left);
                } else {
                    insets.top = Math.max(
                            insets.top,
                            boundingBox.height + labelSpacing + nodeLabelInsets.top);
                }
                break;
                
            case IN_T_C:
                insets.top = Math.max(
                        insets.top,
                        boundingBox.height + labelSpacing + nodeLabelInsets.top);
                break;
                
            case IN_T_R:
                if (hPrio) {
                    insets.right = Math.max(
                            insets.right,
                            boundingBox.width + labelSpacing + nodeLabelInsets.right);
                } else {
                    insets.top = Math.max(
                            insets.top,
                            boundingBox.height + labelSpacing + nodeLabelInsets.top);
                }
                break;
                
            case IN_C_L:
                insets.left = Math.max(
                        insets.left,
                        boundingBox.width + labelSpacing + nodeLabelInsets.left);
                break;
                
            case IN_C_R:
                insets.right = Math.max(
                        insets.right,
                        boundingBox.width + labelSpacing + nodeLabelInsets.right);
                break;
                
            case IN_B_L:
                if (hPrio) {
                    insets.left = Math.max(
                            insets.left,
                            boundingBox.width + labelSpacing + nodeLabelInsets.left);
                } else {
                    insets.bottom = Math.max(
                            insets.bottom,
                            boundingBox.height + labelSpacing + nodeLabelInsets.bottom);
                }
                break;
                
            case IN_B_C:
                insets.bottom = Math.max(
                        insets.bottom,
                        boundingBox.height + labelSpacing + nodeLabelInsets.bottom);
                break;
                
            case IN_B_R:
                if (hPrio) {
                    insets.right = Math.max(
                            insets.right,
                            boundingBox.width + labelSpacing + nodeLabelInsets.right);
                } else {
                    insets.bottom = Math.max(
                            insets.bottom,
                            boundingBox.height + labelSpacing + nodeLabelInsets.bottom);
                }
                break;
                
            default:
                // In all other cases, no specific action is required
            }
        }

        // Add node label insets that aren't set yet
        // This happens if e.g. a top inset is set but no top label is present
        insets.top    = Math.max(insets.top, nodeLabelInsets.top);
        insets.left   = Math.max(insets.left, nodeLabelInsets.left);
        insets.right  = Math.max(insets.right, nodeLabelInsets.right);
        insets.bottom = Math.max(insets.bottom, nodeLabelInsets.bottom);

        return insets;
    }

    /**
     * Returns the bounding box of all node labels placed at the specified location. If there is no
     * bounding box for the location yet, a new one is added and returned.
     *
     * @param labelGroupsBoundingBoxes
     *            map of already existing bounding boxes.
     * @param location
     *            the location for which to retrieve the bounding box.
     * @return the corresponding bounding box.
     */
    private static Rectangle retrieveLabelGroupsBoundingBox(
            final Map<LabelLocation, LabelGroup> labelGroupsBoundingBoxes,
            final LabelLocation location) {
        if (!labelGroupsBoundingBoxes.containsKey(location)) {
            LabelGroup boundingBox = new LabelGroup();
            labelGroupsBoundingBoxes.put(location, boundingBox);
            return boundingBox;
        } else {
            return labelGroupsBoundingBoxes.get(location);
        }
    }
}
