/*******************************************************************************
 * Copyright (c) 2016 alan and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    alan - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Iterables;

/**
 * @author alan
 *
 */
public class LayerSweepTypeDecider {

    private GraphData graphData;

    /**
     * Create Decider for which layer sweep.
     */
    public LayerSweepTypeDecider(final GraphData gd) {
        this.graphData = gd;
    }

    /*
     * In order to decide whether to sweep into the graph or not, we compare the number of paths to nodes whose position
     * is decided on by random decisions to the number of paths to nodes whose position depends on cross-hierarchy
     * edges. By calculating (pathsToRandom - pathsToHierarchical) / allPaths, this value will always be between -1
     * (many cross hierarchical paths) and +1 (many random paths). By setting the boundary
     * CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS, we can choose how likely it is to be hierarchical or more bottom
     * up.
     */
    /**
     * Decide whether to use bottom up or cross-hierarchical sweep method.
     * 
     * @return decision
     */
    public boolean useBottomUp() {
        if (bottomUpForced() || rootNode() || fewerThanTwoInOutEdges()) {
            return true;
        }
        if (graphData.crossMinDeterministic()) {
            return false;
        }

        float boundary = graphData.lGraph().getProperty(LayeredOptions.CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS);
        if (boundary < -1) {
            // TODO-alan for testing purposes. Remove!
            return new Random().nextBoolean();
        }

        int pathsToRandom = 0;
        int pathsToHierarchical = 0;

        List<LNode> nsPortDummies = new ArrayList<>();
        Iterable<LNode> nodes = Iterables.concat(graphData.lGraph());
        for (LNode node : nodes) {
            if (isNorthSouthDummy(node)) {
                nsPortDummies.add(node);
                continue;
            }
            NodeInfo currentNode = nodeInfoFor(node);
            if (isExternalPortDummy(node)) {
                currentNode.hierarchicalInfluence = 1;
                if (isEasternDummy(node)) {
                    pathsToHierarchical += currentNode.connectedEdges;
                }
            } else {
                if (hasNoWesternPorts(node)) {
                    currentNode.randomInfluence = 1;
                }
                if (hasNoEasternPorts(node)) {
                    pathsToRandom += currentNode.connectedEdges;
                }
            }
            for (LEdge edge : node.getOutgoingEdges()) {
                pathsToRandom += currentNode.randomInfluence;
                pathsToHierarchical += currentNode.hierarchicalInfluence;
                transferInfoToTarget(currentNode, edge);
            }
            for (LPort port : node.getPorts(PortSide.NORTH)) {
                LNode nsDummy = port.getProperty(InternalProperties.PORT_DUMMY);
                if (nsDummy != null) {
                    pathsToRandom += currentNode.randomInfluence;
                    pathsToHierarchical += currentNode.hierarchicalInfluence;
                    transferInfoTo(currentNode, nsDummy);
                }
            }
        }

        for (LNode node : nsPortDummies) {
            NodeInfo currentNode = nodeInfoFor(node);
            for (LEdge edge : node.getOutgoingEdges()) {
                pathsToRandom += currentNode.randomInfluence;
                pathsToHierarchical += currentNode.hierarchicalInfluence;
                transferInfoToTarget(currentNode, edge);
            }
        }

        // float boundary = lGraph.getProperty(LayeredOptions.CROSSING_MINIMIZATION_HIERARCHICAL_SWEEPINESS);
        double allPaths = pathsToRandom + pathsToHierarchical;
        double normalized = allPaths == 0 ? Double.MAX_VALUE : (pathsToRandom - pathsToHierarchical) / allPaths;

        return normalized > boundary;
    }

    private void transferInfoToTarget(final NodeInfo currentNode, final LEdge edge) {
        LNode target = targetNode(edge);
        transferInfoTo(currentNode, target);
    }

    private void transferInfoTo(final NodeInfo currentNode, final LNode target) {
        NodeInfo targetNodeInfo = nodeInfoFor(target);
        targetNodeInfo.transfer(currentNode);
        targetNodeInfo.connectedEdges++;
    }

    /**
     * For a single node, collects number of paths to nodes with random or hierarchical nodes.
     * 
     * @author alan
     *
     */
    static class NodeInfo {
        private int connectedEdges = 0;
        private int hierarchicalInfluence;
        private int randomInfluence;

        public void transfer(final NodeInfo nodeInfo) {
            hierarchicalInfluence += nodeInfo.hierarchicalInfluence;
            randomInfluence += nodeInfo.randomInfluence;
            connectedEdges += nodeInfo.connectedEdges;
        }
    }

    private boolean fewerThanTwoInOutEdges() {
        return graphData.parent().getPorts(PortSide.EAST).size() < 2
                && graphData.parent().getPorts(PortSide.WEST).size() < 2;
    }

    private boolean rootNode() {
        return !graphData.hasParent();
    }

    private Boolean bottomUpForced() {
        return graphData.lGraph().getProperty(LayeredOptions.CROSSING_MINIMIZATION_BOTTOM_UP);
    }

    private LNode targetNode(final LEdge edge) {
        return edge.getTarget().getNode();
    }

    private boolean hasNoEasternPorts(final LNode node) {
        return node.getPorts(PortSide.EAST).isEmpty();
    }

    private boolean hasNoWesternPorts(final LNode node) {
        return node.getPorts(PortSide.WEST).isEmpty();
    }

    private boolean isExternalPortDummy(final LNode node) {
        return node.getType() == NodeType.EXTERNAL_PORT;
    }

    private boolean isNorthSouthDummy(final LNode node) {
        return node.getType() == NodeType.NORTH_SOUTH_PORT;
    }

    private boolean isEasternDummy(final LNode node) {
        return originPort(node).getSide() == PortSide.EAST;
    }

    private LPort originPort(final LNode node) {
        return (LPort) node.getProperty(InternalProperties.ORIGIN);
    }

    private NodeInfo nodeInfoFor(final LNode n) {
        return graphData.nodeInfo()[n.getLayer().id][n.id];
    }
}
