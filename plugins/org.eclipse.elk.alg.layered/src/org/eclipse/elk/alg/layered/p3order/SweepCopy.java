/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Lists;

/**
 * Stores node and port order for a sweep.
 * 
 * @author alan
 */
class SweepCopy {
    /** Saves a copy of the node order. */
    private final LNode[][] nodeOrder;
    /** Saves a copy of the orders of the ports on each node, because they are reordered in each sweep. */
    private final List<List<List<LPort>>> portOrders;

    /**
     * Copies on construction.
     * 
     * @param nodeOrderIn
     */
    SweepCopy(final LNode[][] nodeOrderIn) {
        nodeOrder = deepCopy(nodeOrderIn);
        portOrders = new ArrayList<>();
        for (LNode[] lNodes : nodeOrderIn) {
            List<List<LPort>> layer = new ArrayList<>();
            portOrders.add(layer);
            for (LNode node : lNodes) {
                layer.add(new ArrayList<>(node.getPorts()));
            }
        }
    }

    SweepCopy(final SweepCopy sc) {
        nodeOrder = deepCopy(sc.nodeOrder);
        portOrders = new ArrayList<>(sc.portOrders);
    }

    private LNode[][] deepCopy(final LNode[][] currentlyBestNodeOrder) {
        if (currentlyBestNodeOrder == null) {
            return null;
        }
        final LNode[][] result = new LNode[currentlyBestNodeOrder.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = Arrays.copyOf(currentlyBestNodeOrder[i], currentlyBestNodeOrder[i].length);
        }
        return result;
    }

    /**
     * Returns the copy of the node orders. WARNING: Do not change, or the copy will be invalid.
     * 
     * @return the nodeOrder
     */
    public LNode[][] nodes() {
        return nodeOrder;
    }

    /**
     * @param lGraph
     */
    public void transferNodeAndPortOrdersToGraph(final LGraph lGraph) {
        
        // the 'NORTH_OR_SOUTH_PORT' option allows the crossing minimizer to decide 
        // the side a corresponding dummy node is placed on in order to reduce the number of crossings
        // as a consequence the configured port side may not be valid anymore and has to be corrected
        List<LNode> northSouthPortDummies = Lists.newArrayList();

        // iterate the layers
        List<Layer> layers = lGraph.getLayers();
        for (int i = 0; i < layers.size(); i++) {
            List<LNode> nodes = layers.get(i).getNodes();
            northSouthPortDummies.clear();
            
            // iterate and order the nodes within the layer
            for (int j = 0; j < nodes.size(); j++) {
                LNode node = nodeOrder[i][j];
                // use the id field to remember the order within the layer
                node.id = j;
                if (node.getType() == NodeType.NORTH_SOUTH_PORT) {
                    northSouthPortDummies.add(node);
                }
                
                lGraph.getLayers().get(i).getNodes().set(j, node);
                // order ports as computed
                node.getPorts().clear();
                node.getPorts().addAll(portOrders.get(i).get(j));
            }
            
            // assert that the port side is set properly
            for (LNode node : northSouthPortDummies) {
                assertCorrectPortSides(node);
                // the order of the ports may have changed, update the port index cache
                node.cachePortSides();
            }
        }
    }

    private void assertCorrectPortSides(final LNode node) {
        assert node.getType() == NodeType.NORTH_SOUTH_PORT;

        LNode origin = node.getProperty(InternalProperties.IN_LAYER_LAYOUT_UNIT);

        // a north south port dummy has exactly one port
        List<LPort> dummyPorts = node.getPorts();
        LPort dummyPort = dummyPorts.get(0);

        // find the corresponding port on the regular node
        for (LPort port : origin.getPorts()) {
            if (port.equals(dummyPort.getProperty(InternalProperties.ORIGIN))) {
                // switch the port's side if necessary
                if ((port.getSide() == PortSide.NORTH) && (node.id > origin.id)) {
                    port.setSide(PortSide.SOUTH);
                } else if ((port.getSide() == PortSide.SOUTH) && (origin.id > node.id)) {
                    port.setSide(PortSide.NORTH);
                }
                break;
            }
        }
    }
    
}