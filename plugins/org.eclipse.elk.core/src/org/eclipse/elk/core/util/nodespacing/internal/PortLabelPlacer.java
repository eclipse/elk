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

import java.util.Collection;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.adapters.GraphAdapters.LabelAdapter;

/**
 * Contains the code necessary to place port labels.
 */
public final class PortLabelPlacer {
    
    /**
     * This class is not meant to be instantiated.
     */
    private PortLabelPlacer() {
        
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // SIMPLE PLACEMENT
    
    /**
     * Places port labels at their default locations, assuming that there will be enough space for them not to overlap.
     */
    public static void simplePortLabelPlacement(final NodeContext nodeContext, final PortSide portSide) {
        // Delegate to the appropriate methods
        if (nodeContext.portLabelsPlacement == PortLabelPlacement.INSIDE) {
            simpleInsidePortLabelPlacement(nodeContext, portSide);
        } else if (nodeContext.portLabelsPlacement == PortLabelPlacement.OUTSIDE) {
            simpleOutsidePortLabelPlacement(nodeContext, portSide);
        }
    }

    
    ///////////////////////////////////////////////////////////
    // Inside Port Label Placement
    
    /**
     * Implementation of {@link #simplePortLabelPlacement(NodeContext, PortSide)} for inside port label placement.
     */
    private static void simpleInsidePortLabelPlacement(final NodeContext nodeContext, final PortSide portSide) {
        for (PortContext portContext : nodeContext.portContexts.get(portSide)) {
            // If the port doesn't have labels, skip
            if (portContext.labelSpace == null || portContext.labelSpace.height == 0) {
                continue;
            }
            
            KVector portSize = portContext.port.getSize();
            double portOffset = portContext.port.hasProperty(CoreOptions.PORT_BORDER_OFFSET)
                    ? portContext.port.getProperty(CoreOptions.PORT_BORDER_OFFSET)
                    : 0;
            
            // Calculate the position of the port's label space
            switch (portSide) {
            case NORTH:
                portContext.labelSpace.x = -(portContext.labelSpace.width - portSize.x) / 2;
                portContext.labelSpace.y = portSize.y + portOffset + nodeContext.insidePortSpace.top
                        + nodeContext.portLabelSpacing;
                portContext.labelAlignment = LabelAlignment.CENTER;
                break;
                
            case SOUTH:
                portContext.labelSpace.x = -(portContext.labelSpace.width - portSize.x) / 2;
                portContext.labelSpace.y = -portOffset - nodeContext.insidePortSpace.bottom
                        - nodeContext.portLabelSpacing - portContext.labelSpace.height;
                portContext.labelAlignment = LabelAlignment.CENTER;
                break;
                
            case EAST:
                portContext.labelSpace.x = -portOffset - nodeContext.insidePortSpace.right
                        - nodeContext.portLabelSpacing - portContext.labelSpace.width;
                portContext.labelSpace.y = -(portContext.labelSpace.height - portSize.y) / 2;
                portContext.labelAlignment = LabelAlignment.RIGHT;
                break;
                
            case WEST:
                portContext.labelSpace.x = portSize.x + portOffset + nodeContext.insidePortSpace.left
                        + nodeContext.portLabelSpacing;
                portContext.labelSpace.y = -(portContext.labelSpace.height - portSize.y) / 2;
                portContext.labelAlignment = LabelAlignment.LEFT;
                break;
            }
            
            // Place the labels
            double yPos = portContext.labelSpace.y;
            for (LabelAdapter<?> label : portContext.port.getLabels()) {
                doPlaceLabel(label, portContext, yPos);
                
                // Update y coordinate
                yPos += label.getSize().y + nodeContext.labelLabelSpacing;
            }
        }
    }


    
    ///////////////////////////////////////////////////////////
    // Outside Port Label Placement
    
    /**
     * Implementation of {@link #simplePortLabelPlacement(NodeContext, PortSide)} for outside port label placement.
     */
    private static void simpleOutsidePortLabelPlacement(final NodeContext nodeContext, final PortSide portSide) {
        Collection<PortContext> portContexts = nodeContext.portContexts.get(portSide);
        
        // If there are only two ports on a side, we place the first port's label on its other side to make it
        // especially clear which port it belongs to
        boolean portWithSpecialNeeds = portContexts.size() == 2;
        
        for (PortContext portContext : portContexts) {
            // If the port doesn't have labels, skip
            if (portContext.labelSpace == null || portContext.labelSpace.height == 0) {
                continue;
            }
            
            KVector portSize = portContext.port.getSize();
            
            // Calculate the position of the port's label space
            switch (portSide) {
            case NORTH:
                if (portWithSpecialNeeds) {
                    portContext.labelSpace.x = -portContext.labelSpace.width - nodeContext.portLabelSpacing;
                    portContext.labelAlignment = LabelAlignment.RIGHT;
                } else {
                    portContext.labelSpace.x = portSize.x + nodeContext.portLabelSpacing;
                    portContext.labelAlignment = LabelAlignment.LEFT;
                }
                portContext.labelSpace.y = -portContext.labelSpace.height - nodeContext.portLabelSpacing;
                break;
                
            case SOUTH:
                if (portWithSpecialNeeds) {
                    portContext.labelSpace.x = -portContext.labelSpace.width - nodeContext.portLabelSpacing;
                    portContext.labelAlignment = LabelAlignment.RIGHT;
                } else {
                    portContext.labelSpace.x = portSize.x + nodeContext.portLabelSpacing;
                    portContext.labelAlignment = LabelAlignment.LEFT;
                }
                portContext.labelSpace.y = portSize.y + nodeContext.portLabelSpacing;
                break;
                
            case EAST:
                portContext.labelSpace.x = portSize.x + nodeContext.portLabelSpacing;
                if (portWithSpecialNeeds) {
                    portContext.labelSpace.y = -portContext.labelSpace.height - nodeContext.portLabelSpacing;
                } else {
                    portContext.labelSpace.y = portSize.y + nodeContext.portLabelSpacing;
                }
                portContext.labelAlignment = LabelAlignment.LEFT;
                
                break;
                
            case WEST:
                portContext.labelSpace.x = -portContext.labelSpace.width - nodeContext.portLabelSpacing;
                if (portWithSpecialNeeds) {
                    portContext.labelSpace.y = -portContext.labelSpace.height - nodeContext.portLabelSpacing;
                } else {
                    portContext.labelSpace.y = portSize.y + nodeContext.portLabelSpacing;
                }
                portContext.labelAlignment = LabelAlignment.RIGHT;
                break;
            }
            
            // Place the labels
            double yPos = portContext.labelSpace.y;
            for (LabelAdapter<?> label : portContext.port.getLabels()) {
                doPlaceLabel(label, portContext, yPos);
                
                // Update y coordinate
                yPos += label.getSize().y + nodeContext.labelLabelSpacing;
            }
            
            // The next port definitely doesn't have special needs anymore
            portWithSpecialNeeds = false;
        }
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UTILITY FUNCTIONS
    
    /**
     * Does the actual work of placing the given label at the given y position, choosing its x position such that it
     * matches the port context's label alignment.
     */
    private static void doPlaceLabel(final LabelAdapter<?> label, final PortContext portContext, final double yPos) {
        double xPos = portContext.labelSpace.x;
        
        switch (portContext.labelAlignment) {
        case CENTER:
            xPos += (portContext.labelSpace.width - label.getSize().x) / 2;
            break;
            
        case RIGHT:
            xPos += portContext.labelSpace.width - label.getSize().x;
            break;
        }

        KVector labelPosition = label.getPosition();
        labelPosition.x = xPos;
        labelPosition.y = yPos;
        label.setPosition(labelPosition);
    }
    
}
