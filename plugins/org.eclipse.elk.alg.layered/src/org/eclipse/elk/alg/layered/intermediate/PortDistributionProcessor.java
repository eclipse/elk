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
package org.eclipse.elk.alg.layered.intermediate;

import java.util.List;
import java.util.Random;

import org.eclipse.elk.alg.layered.ILayoutProcessor;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.p3order.AbstractPortDistributor;
import org.eclipse.elk.alg.layered.p3order.LayerTotalPortDistributor;
import org.eclipse.elk.alg.layered.p3order.NodeRelativePortDistributor;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Distributes the ports of nodes without fixed port order and raises the port constraints accordingly.
 * The port order depends on the connected ports in other layers. Also, the actual port distribution
 * mechanism used is determined randomly. 
 * Also handles ports with the 
 * {@link de.cau.cs.kieler.klay.layered.properties.LayeredOptions#NORTH_OR_SOUTH_PORT 
 * Properties#NORTH_OR_SOUTH_PORT} property.
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
        
        // Setup of node ID's and port ID's to identify nodes and ports
        // according to their position in the graph.
        // Node ID's reflect the order of nodes in the graph from top
        // to bottom per layer. I.e. the topmost, leftmost node in the graph
        // has node ID 0.
        // Port ID's reflect the order of ports in the graph. For each
        // node in the order of node ID's, its ports get assigned increasing
        // ID's in the order of the sides north - west - south - east.
        // The order of the ports per side has been determined to minimize
        // the number of edge crossings.
        int portCount = 0;
        int nodeCount = 0;
        for (LNode[] layer : graphArray) {
            for (LNode node : layer) {
                node.id = nodeCount++;
                for (LPort port : node.getPorts()) {
                    port.id = portCount++;
                }
            }
        }
        
        // LayerSweep has determined the position of nodes.
        // Dummy nodes created for north/south ports on nodes
        // with port constraint {@link PortConstraints#NORTH_OR_SOUTH_PORT} may have
        // been placed on the opposite side of the port they
        // were created for. We now reposition such ports accordingly
        // in the whole graph.
        for (Layer layer : layeredGraph) {
            switchPortSides(layer);
        }
        
        // Randomly determine which port distributor implementation to use
        Random random = layeredGraph.getProperty(InternalProperties.RANDOM);
        AbstractPortDistributor portDistributor = random.nextBoolean()
                ? new NodeRelativePortDistributor(new float[portCount])
                : new LayerTotalPortDistributor(new float[portCount]);
        portDistributor.distributePorts(graphArray);
        
        progressMonitor.done();
    }

    /**
     * Ports on north/south side may switch sides if the node they belong to has at most 
     * port constraint {@link de.cau.cs.kieler.kiml.options.PortConstraints#FIXED_SIDE FIXED_SIDE}.
     * This means that the dummy nodes created for such ports may not have been placed on the 
     * same side as the port. 
     * In such a case, switch the port to the opposite side.  
     * 
     * @param layer
     *            The current layer, sorted by best sweep.
     */
    private void switchPortSides(final Layer layer) {
        for (LNode node : layer) {
            // get dummy and its origin
            if (node.getType() == NodeType.NORTH_SOUTH_PORT) {

                // the layout unit is represented by the regular node
                LNode origin = node.getProperty(InternalProperties.IN_LAYER_LAYOUT_UNIT);
                assert origin.getType() == NodeType.NORMAL;
                
                // the dummy node has least one port (there may be two if an odd port 
                // has both an incoming and an outgoing edge, however the origin is the same)
                assert node.getPorts().size() >= 1;
                
                List<LPort> dummyPorts = node.getPorts();
                LPort dummyPort = dummyPorts.get(0);
                
                // the origin of this dummy port is the actual port in the graph 
                LPort originalPort = (LPort) dummyPort.getProperty(InternalProperties.ORIGIN);
                
                // switch the port's side if necessary
                if ((originalPort.getSide() == PortSide.NORTH) && (node.id > origin.id)) {
                    originalPort.setSide(PortSide.SOUTH);
                } else if ((originalPort.getSide() == PortSide.SOUTH) && (origin.id > node.id)) {
                    originalPort.setSide(PortSide.NORTH);
                }
            }
        }
    }
    
}

