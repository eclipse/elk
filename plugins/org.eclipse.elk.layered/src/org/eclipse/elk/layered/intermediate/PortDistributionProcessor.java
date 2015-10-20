/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.layered.intermediate;

import java.util.Random;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.layered.ILayoutProcessor;
import org.eclipse.elk.layered.graph.LGraph;
import org.eclipse.elk.layered.graph.LNode;
import org.eclipse.elk.layered.graph.LPort;
import org.eclipse.elk.layered.p3order.AbstractPortDistributor;
import org.eclipse.elk.layered.p3order.LayerTotalPortDistributor;
import org.eclipse.elk.layered.p3order.NodeRelativePortDistributor;
import org.eclipse.elk.layered.properties.InternalProperties;

/**
 * Distributes the ports of nodes without fixed port order and raises the port constraints accordingly.
 * The port order depends on the connected ports in other layers. Also, the actual port distribution
 * mechanism used is determined randomly.
 * 
 * <dl>
 *   <dt>Preconditions:</dt>
 *     <dd>a layered graph.</dd>
 *   <dt>Postconditions:</dt>
 *     <dd>All nodes have port constraints set to at least {@code FIXED_ORDER}.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 4.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>None.</dd>
 * </dl>
 * 
 * @see NodeRelativePortDistributor
 * @see LayerTotalPortDistributor
 * @author cds
 */
public class PortDistributionProcessor implements ILayoutProcessor {

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Port distribution", 1);
        
        // The port distributor needs an array representation of the graph
        LNode[][] graphArray = layeredGraph.toNodeArray();
        
        // We need to setup port IDs
        int portCount = 0;
        for (LNode[] layer : graphArray) {
            for (LNode node : layer) {
                for (LPort port : node.getPorts()) {
                    port.id = portCount++;
                }
            }
        }
        
        // Randomly determine which port distributor implementation to use
        Random random = layeredGraph.getProperty(InternalProperties.RANDOM);
        AbstractPortDistributor portDistributor = random.nextBoolean()
                ? new NodeRelativePortDistributor(new float[portCount])
                : new LayerTotalPortDistributor(new float[portCount]);
        portDistributor.distributePorts(graphArray);
        
        progressMonitor.done();
    }

}
