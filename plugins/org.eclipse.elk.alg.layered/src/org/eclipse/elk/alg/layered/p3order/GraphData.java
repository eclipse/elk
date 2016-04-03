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
package org.eclipse.elk.alg.layered.p3order;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.GreedySwitchHeuristic;
import org.eclipse.elk.alg.layered.p3order.BarycenterHeuristic.BarycenterState;
import org.eclipse.elk.alg.layered.p3order.constraints.ForsterConstraintResolver;
import org.eclipse.elk.alg.layered.p3order.constraints.IConstraintResolver;
import org.eclipse.elk.alg.layered.p3order.counting.AllCrossingsCounter;
import org.eclipse.elk.alg.layered.properties.GraphProperties;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Collects data needed for cross minimization for each of the contained graphs.
 *
 * @author alan
 *
 */
class GraphData {
    private final LGraph lGraph;
    private final LNode[][] currentNodeOrder;
    private final int[][] nodePositions;
    private SweepCopy currentlyBestNodeAndPortOrder;
    private SweepCopy bestNodeNPortOrder;

    private ICrossingMinimizationHeuristic crossMinimizer;
    private final SweepPortDistributor portDistributor;

    private final boolean processRecursively;
    private final LNode parent;
    private final boolean hasParent;
    private boolean canSweepIntoThisGraph;

    private final AllCrossingsCounter crossCounter;

    private final List<LGraph> childGraphs;
    private final boolean externalPorts;
    private final CrossMinType crossMinType;
    private final Map<Integer, Integer> childNumPorts;

    /**
     * Create object collecting info about compound graph.
     *
     * @param graph
     *            The graph
     * @param crossMinType
     *            The CrossMinimizer
     * @param processAllGraphsRecursively
     *            forces all graphs to be layouted recursively.
     */
    GraphData(final LGraph graph, final CrossMinType crossMinType,
            final boolean processAllGraphsRecursively, final List<GraphData> graphs) {
        lGraph = graph;
        this.crossMinType = crossMinType;
        childNumPorts = new HashMap<>();
        childGraphs = Lists.newArrayList();

        currentNodeOrder = graph.toNodeArray();

        externalPorts = graph.getProperty(InternalProperties.GRAPH_PROPERTIES)
                .contains(GraphProperties.EXTERNAL_PORTS);

        int graphSize = graph.getLayers().size();
        Double[][] barycenters = new Double[graphSize][];
        nodePositions = new int[graphSize][];
        boolean[] hasNorthSouthPorts = new boolean[graphSize];
        int[] inLayerEdgeCount = new int[graphSize];
        Multimap<LNode, LNode> layoutUnits = LinkedHashMultimap.create();

        int portId = 0;
        int layerId = 0;
        for (int layerIndex = 0; layerIndex < graphSize; layerIndex++) {
            Layer layer = graph.getLayers().get(layerIndex);
            layer.id = layerId++;
            List<LNode> nodes = layer.getNodes();
            nodePositions[layerIndex] = new int[nodes.size()];
            barycenters[layerIndex] = new Double[nodes.size()];
            int nodeId = 0;
            for (int nodeIndex = 0; nodeIndex < nodes.size(); nodeIndex++) {
                LNode node = nodes.get(nodeIndex);
                node.id = nodeId++;
                nodePositions[node.getLayer().id][node.id] = nodeIndex;

                if (hasNestedGraph(node)) {
                    LGraph nestedGraph = nestedGraphOf(node);
                    childGraphs.add(nestedGraph);
                }

                if (node.getType() == NodeType.NORTH_SOUTH_PORT) {
                    hasNorthSouthPorts[layerIndex] = true;
                }

                for (LPort port : node.getPorts()) {
                    port.id = portId++;
                    for (LEdge edge : port.getOutgoingEdges()) {
                        if (edge.getTarget().getNode().getLayer() == edge.getSource().getNode()
                                .getLayer()) {
                            inLayerEdgeCount[layerIndex]++;
                        }
                    }
                }

                LNode layoutUnit = node.getProperty(InternalProperties.IN_LAYER_LAYOUT_UNIT);
                if (layoutUnit != null) {
                    layoutUnits.put(layoutUnit, node);
                }
            }
        }

        canSweepIntoThisGraph = true;
        parent = lGraph.getProperty(InternalProperties.PARENT_LNODE);
        hasParent = parent != null;
        if (hasParent && crossMinType == CrossMinType.TWO_SIDED_GREEDY_SWITCH) {
            graphs.get(parent.getGraph().id).childNumPorts.put(lGraph.id, portId);
        }
        // When processing a deterministic cross minimizer, we mostly want to sweep into the
        // graph except when explicitly set not to do so.
        processRecursively = processAllGraphsRecursively
                || !crossMinType.isDeterministic() && assessWhetherToProcessRecursively();

        int[] portPos = new int[portId];
        crossCounter = AllCrossingsCounter.createAssumingFixedPortOrder(inLayerEdgeCount,
                hasNorthSouthPorts, portPos, getHyperedges(currentNodeOrder));

        float[] portRanks = new float[portId];
        Random random = graph.getProperty(InternalProperties.RANDOM);
        if (crossMinType.alwaysImproves() && !childGraphs.isEmpty()) {
            portDistributor = new GreedyPortDistributor(portPos, childNumPorts);
        } else if (random.nextBoolean()) {
            portDistributor = NodeRelativePortDistributor.createPortOrderFixedInOtherLayers(portRanks, nodePositions);
        } else {
            portDistributor = LayerTotalPortDistributor.createPortOrderFixedInOtherLayers(portRanks, nodePositions);
        }

        // Initialize the compound graph layer crossing minimizer
        switch (crossMinType) {
        case BARYCENTER:
            // Initialize barycenter states used by the barycenter heuristic and forster
            // assign ids to layers and nodes
            BarycenterState[][] barycenterStates = new BarycenterState[graph.getLayers().size()][];
            int i = 0;
            for (Layer layer : graph.getLayers()) {
                layer.id = i;
                barycenterStates[i] = new BarycenterState[layer.getNodes().size()];
                int j = 0;
                for (LNode node : layer.getNodes()) {
                    node.id = j;
                    barycenterStates[i][j] = new BarycenterState(node);
                    j++;
                }
                i++;
            }
            IConstraintResolver constraintResolver =
                    new ForsterConstraintResolver(barycenterStates, layoutUnits);
            crossMinimizer = new BarycenterHeuristic(barycenterStates, constraintResolver,
                    random, (AbstractBarycenterPortDistributor) portDistributor);
            break;
        case ONE_SIDED_GREEDY_SWITCH:
        case TWO_SIDED_GREEDY_SWITCH:
            crossMinimizer =
                    new GreedySwitchHeuristic(lGraph.getProperty(LayeredOptions.CROSSING_MINIMIZATION_GREEDY_SWITCH));
            break;
        default:
            throw new UnsupportedOperationException("This heuristic is not implemented yet.");
        }
    }

    private boolean assessWhetherToProcessRecursively() {
        float recursiveBoundary = lGraph.getProperty(LayeredOptions.RECURSIVE_BOUNDARY);
        double val =
                (calculateConnectivity(PortSide.WEST) + calculateConnectivity(PortSide.EAST)) / 2;
        return val < recursiveBoundary;
    }

    private double calculateConnectivity(final PortSide side) {
        int numOutsideEdges = 0;
        int numUnconnectedEdges = 0;
        for (LNode[] layer : currentNodeOrder) {
            boolean isHierarchicalDummyLayer = isExternalPortDummy(layer[0]);
            for (LNode node : layer) {
                if (isHierarchicalDummyLayer) {
                    if (hasPortsOnSide(node, side.opposed())) {
                        numOutsideEdges++;
                    }
                } else if (!hasPortsOnSide(node, side)) {
                    numUnconnectedEdges++;
                }
            }
        }
        if (numOutsideEdges < 2) {
            return 0;
        } else if (numUnconnectedEdges == 0) {
            return 1;
        } else {
            return (double) numOutsideEdges / (double) numUnconnectedEdges;
        }
    }

    private boolean isExternalPortDummy(final LNode firstNode) {
        return firstNode.getType() == NodeType.EXTERNAL_PORT;
    }

    private boolean hasPortsOnSide(final LNode node, final PortSide side) {
        return !node.getPorts(side).isEmpty();
    }

    private LGraph nestedGraphOf(final LNode node) {
        return node.getProperty(InternalProperties.NESTED_LGRAPH);
    }

    private Boolean hasNestedGraph(final LNode node) {
        return nestedGraphOf(node) != null;
    }

    private boolean[] getHyperedges(final LNode[][] nodeOrder) {
        boolean[] hasHyperedges = new boolean[nodeOrder.length];
        // for (int layerIndex = 0; layerIndex < nodeOrder.length - 1; layerIndex++) {
        // LNode[] leftLayer = nodeOrder[layerIndex];
        // hasHyperedges[layerIndex] |= checkForHyperedges(leftLayer, PortSide.EAST);
        // LNode[] rightLayer = nodeOrder[layerIndex + 1];
        // hasHyperedges[layerIndex] |= checkForHyperedges(rightLayer, PortSide.WEST);
        // }
        return hasHyperedges;
    }

    private boolean checkForHyperedges(final LNode[] layer, final PortSide side) {
        for (LNode node : layer) {
            for (LPort port : node.getPorts()) {
                if (port.getSide() == side) {
                    if (port.getOutgoingEdges().size() + port.getIncomingEdges().size() > 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return the lGraph
     */
    public LGraph lGraph() {
        return lGraph;
    }

    /**
     * @return the currentNodeOrder
     */
    public LNode[][] currentNodeOrder() {
        return currentNodeOrder;
    }

    /**
     * @return the nodePositions
     */
    public int[][] nodePositions() {
        return nodePositions;
    }

    /**
     * @return the currentlyBestNodeAndPortOrder
     */
    public SweepCopy currentlyBestNodeAndPortOrder() {
        return currentlyBestNodeAndPortOrder;
    }

    /**
     * @param currentlyBestNodeAndPortOrder
     *            the currentlyBestNodeAndPortOrder to set
     */
    public void setCurrentlyBestNodeAndPortOrder(final SweepCopy currentlyBestNodeAndPortOrder) {
        this.currentlyBestNodeAndPortOrder = currentlyBestNodeAndPortOrder;
    }

    /**
     * @return the bestNodeNPortOrder
     */
    public SweepCopy bestNodeNPortOrder() {
        return bestNodeNPortOrder;
    }

    /**
     * @param bestNodeNPortOrder
     *            the bestNodeNPortOrder to set
     */
    public void setBestNodeNPortOrder(final SweepCopy bestNodeNPortOrder) {
        this.bestNodeNPortOrder = bestNodeNPortOrder;
    }

    /**
     * @return the crossCounter
     */
    public AllCrossingsCounter crossCounter() {
        return crossCounter;
    }

    /**
     * @return the crossMinimizer
     */
    public ICrossingMinimizationHeuristic crossMinimizer() {
        return crossMinimizer;
    }

    /**
     * @return the portDistributor
     */
    public SweepPortDistributor portDistributor() {
        return portDistributor;
    }

    /**
     * @return the processRecursively
     */
    public boolean processRecursively() {
        return processRecursively;
    }

    /**
     * @return the parent
     */
    public LNode parent() {
        return parent;
    }

    /**
     * @return the hasParent
     */
    public boolean hasParent() {
        return hasParent;
    }

    /**
     * @return the canSweepIntoThisGraph
     */
    public boolean canSweepIntoThisGraph() {
        return canSweepIntoThisGraph;
    }

    /**
     * @param canSweepIntoThisGraph
     *            the canSweepIntoThisGraph to set
     */
    public void setCanSweepIntoThisGraph(final boolean canSweepIntoThisGraph) {
        this.canSweepIntoThisGraph = canSweepIntoThisGraph;
    }

    public Collection<LGraph> childGraphs() {
        return childGraphs;
    }

    public boolean hasExternalPorts() {
        return externalPorts;
    }

    @Override
    public String toString() {
        return lGraph.toString();
    }

    public SweepCopy getBestSweep() {
        return crossMinType.isDeterministic() ? currentlyBestNodeAndPortOrder()
                : bestNodeNPortOrder();
    }
}
