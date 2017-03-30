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

import org.eclipse.elk.core.options.PortLabelPlacement;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.adapters.GraphAdapters.PortAdapter;
import org.eclipse.elk.core.util.nodespacing.internal.cells.LabelCell;
import org.eclipse.elk.core.util.nodespacing.internal.contexts.NodeContext;
import org.eclipse.elk.core.util.nodespacing.internal.contexts.PortContext;

/**
 * Creates port context objects and assigns volatile IDs to all ports. Also, unless port labels are fixed, the labels
 * are added to the port context label cells.
 */
public final class PortContextCreator {
    
    /**
     * No instance required.
     */
    private PortContextCreator() {
        
    }
    
    
    /**
     * Creates and initializes port context objects for each of the node's ports. This also involves setting up the
     * label cell associated with each port, although the node label placement therein
     * @param nodeContext
     */
    public static void createPortContexts(final NodeContext nodeContext) {
        int volatileId = 0;
        for (PortAdapter<?> port : nodeContext.node.getPorts()) {
            if (port.getSide() == PortSide.UNDEFINED) {
                throw new IllegalArgumentException("Label and node size calculator can only be used with ports that "
                        + "have port sides assigned.");
            }
            
            port.setVolatileId(volatileId++);
            createPortContext(nodeContext, port);
        }
    }
    
    /**
     * Creates a port context for the given adapter and initializes it properly.
     */
    private static void createPortContext(final NodeContext nodeContext, final PortAdapter<?> port) {
        PortContext portContext = new PortContext(nodeContext, port);
        nodeContext.portContexts.put(port.getSide(), portContext);
        
        // If the port has labels and if port labels are to be placed, we need to remember them
        if (nodeContext.portLabelsPlacement != PortLabelPlacement.FIXED) {
            portContext.portLabelCell = new LabelCell(nodeContext.labelLabelSpacing);
            port.getLabels().forEach(label -> portContext.portLabelCell.addLabel(label));
        }
    }
    
}
