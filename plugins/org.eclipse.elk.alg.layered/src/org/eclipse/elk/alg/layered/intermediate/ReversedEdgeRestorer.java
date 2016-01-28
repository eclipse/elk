/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Restores the direction of reversed edges. (edges with the property
 * {@link org.eclipse.elk.alg.layered.properties.Properties#REVERSED} set to {@code true})
 * 
 * <p>All edges are traversed to look for reversed edges. If such edges are found,
 * they are restored, the ports they are connected to being restored as well.</p>
 * 
 * <dl>
 *   <dt>Precondition:</dt><dd>a layered graph.</dd>
 *   <dt>Postcondition:</dt><dd>Reversed edges are restored to their original direction.</dd>
 *   <dt>Slots:</dt><dd>After phase 5.</dd>
 *   <dt>Same-slot dependencies:</dt><dd>None.</dd>
 * </dl>
 *
 * @author cds
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating proposed yellow by msp
 */
public final class ReversedEdgeRestorer implements ILayoutProcessor {

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Restoring reversed edges", 1);
        
        // Iterate through the layers
        for (Layer layer : layeredGraph) {
            // Iterate through the nodes
            for (LNode node : layer) {
                // Iterate over all the ports, looking for outgoing edges that should be reversed
                for (LPort port : node.getPorts()) {
                    // Iterate over a copy of the edges to avoid concurrent modification exceptions
                    LEdge[] edgeArray = port.getOutgoingEdges().toArray(
                            new LEdge[port.getOutgoingEdges().size()]);
                    
                    for (LEdge edge : edgeArray) {
                        if (edge.getProperty(InternalProperties.REVERSED)) {
                            edge.reverse(layeredGraph, false);
                        }
                    }
                }
            }
        }
        
        monitor.done();
    }

}
