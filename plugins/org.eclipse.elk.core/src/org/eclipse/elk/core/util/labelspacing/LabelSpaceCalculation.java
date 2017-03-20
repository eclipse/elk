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
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.util.adapters.GraphAdapters.LabelAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.NodeAdapter;

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
     * placed at the top or at the bottom, the top or bottom padding are set. If it is centered
     * vertically, the left or right padding are set if the labels are horizontally aligned leftwards
     * or rightwards. If they are centered in both directions, no padding are set. If they are placed
     * outside the node, no padding are set.
     * 
     * @param node
     *            the node whose labels are to be placed.
     * @param labelSpacing
     *            the default label spacing.
     * @return the adjusted padding.
     */
    public static ElkPadding calculateRequiredNodeLabelSpace(final NodeAdapter<?> node,
            final double labelSpacing) {

        ElkPadding nodeLabelPadding = node.getProperty(CoreOptions.NODE_LABELS_PADDING);

        return calculateRequiredNodeLabelSpace(node, labelSpacing, nodeLabelPadding,
                new HashMap<LabelLocation, LabelGroup>(), new ElkPadding(node.getPadding()));
    }

    /**
     * Calculates the space required to accommodate the node labels (if any) and sets
     * {@link #requiredNodeLabelSpace} as well as {@link #nodeLabelsBoundingBox}. If inside labels are
     * placed at the top or at the bottom, the top or bottom padding are set. If it is centered
     * vertically, the left or right padding are set if the labels are horizontally aligned leftwards
     * or rightwards. If they are centered in both directions, no padding are set. If they are placed
     * outside the node, no padding are set.
     * 
     * @param node
     *            the node whose labels are to be placed.
     * @param labelSpacing
     *            the default label spacing.
     * @param nodeLabelPadding
     *            the additional padding for node labels on this node
     * @param labelGroupsBoundingBoxes
     *            map of locations to corresponding bounding boxes.
     * @param padding
     *            the padding to adjust.
     * @return the adjusted padding.
     */
    public static ElkPadding calculateRequiredNodeLabelSpace(final NodeAdapter<?> node,
            final double labelSpacing, final ElkPadding nodeLabelPadding,
            final Map<LabelLocation, LabelGroup> labelGroupsBoundingBoxes, final ElkPadding padding) {

        // Check if there are any labels
        if (!node.getLabels().iterator().hasNext()) {
            return padding;
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
            final ElkRectangle boundingBox = retrieveLabelGroupsBoundingBox(labelGroupsBoundingBoxes, labelPlacement);
            boundingBox.width = Math.max(boundingBox.width, label.getSize().x);
            boundingBox.height += label.getSize().y + labelSpacing;
        }
        
        // We need to count different label placement boxes towards different kinds of padding, depending on whether
        // or not H_PRIORITY is set on the node itself (see H_PRIORITY documentation)
        boolean hPrio = nodeLabelPlacement.contains(NodeLabelPlacement.H_PRIORITY);
        
        // Calculate the node label space required inside the node (only label groups on the inside
        // are relevant here).
        for (final Entry<LabelLocation, LabelGroup> entry : labelGroupsBoundingBoxes.entrySet()) {
            final ElkRectangle boundingBox = entry.getValue();
            
            // From each existing label group, remove the last superfluous label spacing
            // (the mere existence of a label group implies that it contains at least one label)
            boundingBox.height -= labelSpacing;
            switch (entry.getKey()) {
            case IN_T_L:
                if (hPrio) {
                    padding.left = Math.max(
                            padding.left,
                            boundingBox.width + labelSpacing + nodeLabelPadding.left);
                } else {
                    padding.top = Math.max(
                            padding.top,
                            boundingBox.height + labelSpacing + nodeLabelPadding.top);
                }
                break;
                
            case IN_T_C:
                padding.top = Math.max(
                        padding.top,
                        boundingBox.height + labelSpacing + nodeLabelPadding.top);
                break;
                
            case IN_T_R:
                if (hPrio) {
                    padding.right = Math.max(
                            padding.right,
                            boundingBox.width + labelSpacing + nodeLabelPadding.right);
                } else {
                    padding.top = Math.max(
                            padding.top,
                            boundingBox.height + labelSpacing + nodeLabelPadding.top);
                }
                break;
                
            case IN_C_L:
                padding.left = Math.max(
                        padding.left,
                        boundingBox.width + labelSpacing + nodeLabelPadding.left);
                break;
                
            case IN_C_R:
                padding.right = Math.max(
                        padding.right,
                        boundingBox.width + labelSpacing + nodeLabelPadding.right);
                break;
                
            case IN_B_L:
                if (hPrio) {
                    padding.left = Math.max(
                            padding.left,
                            boundingBox.width + labelSpacing + nodeLabelPadding.left);
                } else {
                    padding.bottom = Math.max(
                            padding.bottom,
                            boundingBox.height + labelSpacing + nodeLabelPadding.bottom);
                }
                break;
                
            case IN_B_C:
                padding.bottom = Math.max(
                        padding.bottom,
                        boundingBox.height + labelSpacing + nodeLabelPadding.bottom);
                break;
                
            case IN_B_R:
                if (hPrio) {
                    padding.right = Math.max(
                            padding.right,
                            boundingBox.width + labelSpacing + nodeLabelPadding.right);
                } else {
                    padding.bottom = Math.max(
                            padding.bottom,
                            boundingBox.height + labelSpacing + nodeLabelPadding.bottom);
                }
                break;
                
            default:
                // In all other cases, no specific action is required
            }
        }

        // Add node label padding that aren't set yet
        // This happens if e.g. a top inset is set but no top label is present
        padding.top    = Math.max(padding.top, nodeLabelPadding.top);
        padding.left   = Math.max(padding.left, nodeLabelPadding.left);
        padding.right  = Math.max(padding.right, nodeLabelPadding.right);
        padding.bottom = Math.max(padding.bottom, nodeLabelPadding.bottom);

        return padding;
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
    private static ElkRectangle retrieveLabelGroupsBoundingBox(
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
