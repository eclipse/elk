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

import java.util.Set;

import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.util.adapters.GraphAdapters.LabelAdapter;

/**
 * Knows how to setup label groups by calculating the amount of space port and node labels will require.
 */
public final class LabelGroupCalculator {

    /**
     * This class is not meant to be instantiated.
     */
    private LabelGroupCalculator() {
        
    }
    
    
    /**
     * Distributes node labels to their respective label location context and calculates the size of each node and
     * port label space.
     */
    public static void setupNodeAndPortLabelGroups(final NodeContext nodeContext) {
        setupNodeLabelGroups(nodeContext);
        setupPortLabelGroups(nodeContext);
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NODE LABELS

    /**
     * Adds each label to the correct label location context and calculate the size of the context's label space
     * rectangle.
     */
    private static void setupNodeLabelGroups(final NodeContext nodeContext) {
        // Go over all of the node's labels (even if the node's default node label placement is to not place the
        // buggers, individual labels may have a proper node label location assigned, so we always need to iterate
        // over them all)
        for (LabelAdapter<?> nodeLabel : nodeContext.node.getLabels()) {
            // Find the effective label location
            Set<NodeLabelPlacement> labelPlacement = nodeLabel.hasProperty(CoreOptions.NODE_LABELS_PLACEMENT)
                    ? nodeLabel.getProperty(CoreOptions.NODE_LABELS_PLACEMENT)
                    : nodeContext.nodeLabelPlacement;
            LabelLocation labelLocation = LabelLocation.fromNodeLabelPlacement(labelPlacement);
            
            // If the label has its location fixed, we will ignore it
            if (labelLocation == LabelLocation.UNDEFINED) {
                continue;
            }
            
            // Retrieve the associated label location context and add the label to it
            LabelLocationContext labelLocationContext = nodeContext.labelLocationContexts.get(labelLocation);
            labelLocationContext.labels.add(nodeLabel);
            
            // Ensure there is a label space rectangle
            if (labelLocationContext.labelSpace == null) {
                labelLocationContext.labelSpace = new ElkRectangle();
            }
            
            // Enlarge the rectangle
            KVector nodeLabelSize = nodeLabel.getSize();
            labelLocationContext.labelSpace.height += nodeLabelSize.y;
            labelLocationContext.labelSpace.width = Math.max(labelLocationContext.labelSpace.width, nodeLabelSize.x);
            
            // If this is not the first label, we need some label-to-label space
            if (labelLocationContext.labels.size() > 1) {
                labelLocationContext.labelSpace.y += nodeContext.labelLabelSpacing;
            }
        }
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PORT LABELS
    
    /**
     * Initializes {@link PortContext#labelSpace} for each of the node's ports.
     */
    private static void setupPortLabelGroups(final NodeContext nodeContext) {
     // If port labels are fixed instead of being inside or outside, we simply ignore all labels by not inviting
        // them to our little party over here
        if (nodeContext.portLabelsPlacement == PortLabelPlacement.FIXED) {
            return;
        }
        
        nodeContext.portContexts.values().forEach(portContext -> setupPortLabelGroup(portContext));
    }
    
    /**
     * Initializes {@link PortContext#labelSpace} for the port.
     */
    private static void setupPortLabelGroup(final PortContext portContext) {
        ElkRectangle labelRectangle = new ElkRectangle();
        int labelCount = 0;
        
        // Iterate over the port's labels and add the required space to the label rectangle (space between labels
        // will be added afterwards)
        for (LabelAdapter<?> label : portContext.port.getLabels()) {
            labelCount++;
            
            KVector labelSize = label.getSize();
            labelRectangle.height += labelSize.y;
            labelRectangle.width = Math.max(labelRectangle.width, labelSize.x);
        }
        
        // Include label-label space if there are labels between which there could be space...
        if (labelCount > 1) {
            labelRectangle.height += portContext.parentNodeContext.labelLabelSpacing * (labelCount - 1);
        }
        
        portContext.labelSpace = labelRectangle;
    }

}
