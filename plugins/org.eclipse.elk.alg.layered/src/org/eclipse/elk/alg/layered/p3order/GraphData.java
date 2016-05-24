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
import org.eclipse.elk.alg.layered.p3order.LayerSweepCrossingMinimizer.CrossMinType;
import org.eclipse.elk.alg.layered.p3order.LayerSweepTypeDecider.NodeInfo;
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
 * Collects data needed for cross minimization and port distribution.
 *
 * @author alan
 *
 */
public class GraphData {
    private LGraph lGraph;
    private LNode[][] currentNodeOrder;
    private SweepCopy currentlyBestNodeAndPortOrder;
    private SweepCopy bestNodeNPortOrder;
    private ICrossingMinimizationHeuristic crossMinimizer;
    private SweepPortDistributor portDistributor;
    private boolean processRecursively;
    private LNode parent;
    private boolean hasParent;
    private AllCrossingsCounter crossCounter;
    private List<LGraph> childGraphs;
    private boolean externalPorts;
    private CrossMinType crossMinType;
    private Map<Integer, Integer> childNumPorts;
    private GraphData parentGraphData;
    private NodeInfo[][] nodeInfo;

    /**
     * Create object collecting information about a graph.
     * 
     * @param graph
     *            The graph
     * @param cMT
     *            The CrossMinimizer
     */
    public GraphData(final LGraph graph, final CrossMinType cMT, final List<GraphData> graphs) {
        lGraph = graph;
        crossMinType = cMT;
        childNumPorts = new HashMap<>();
        childGraphs = Lists.newArrayList();
        externalPorts = graph.getProperty(InternalProperties.GRAPH_PROPERTIES)
                .contains(GraphProperties.EXTERNAL_PORTS);
        int graphSize = graph.getLayers().size();
        currentNodeOrder = new LNode[graphSize][];
        int[][] nodePositions = new int[graphSize][];
        nodeInfo = new NodeInfo[graphSize][];
        boolean[] hasNorthSouthPorts = new boolean[graphSize];
        int[] inLayerEdgeCount = new int[graphSize];
        Multimap<LNode, LNode> layoutUnits = LinkedHashMultimap.create();
        BarycenterState[][] barycenterStates = new BarycenterState[graph.getLayers().size()][];

        // Visit complete graph and collect all necessary information.
        int portId = 0;
        for (int layerIndex = 0; layerIndex < graphSize; layerIndex++) {
            Layer layer = graph.getLayers().get(layerIndex);
            layer.id = layerIndex;
            List<LNode> nodes = layer.getNodes();
            nodePositions[layerIndex] = new int[nodes.size()];
            nodeInfo[layerIndex] = new NodeInfo[nodes.size()];
            currentNodeOrder[layerIndex] = new LNode[nodes.size()];
            barycenterStates[layerIndex] = new BarycenterState[layer.getNodes().size()];

            for (int nodeIndex = 0; nodeIndex < nodes.size(); nodeIndex++) {
                LNode node = nodes.get(nodeIndex);
                node.id = nodeIndex;
                barycenterStates[layerIndex][nodeIndex] = new BarycenterState(node);
                nodePositions[layerIndex][nodeIndex] = nodeIndex;
                nodeInfo[layerIndex][nodeIndex] = new NodeInfo();
                currentNodeOrder[layerIndex][nodeIndex] = node;

                boolean hasNestedGraph = hasNestedGraph(node);
                if (hasNestedGraph) {
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

        initParentInfo(graphs, portId);

        int[] portPos = new int[portId];
        crossCounter =
                new AllCrossingsCounter(inLayerEdgeCount, hasNorthSouthPorts, getHyperedges(currentNodeOrder), portPos);
        initNodePortDistributors(graph, cMT, nodePositions, layoutUnits, barycenterStates, portId, portPos);

        processRecursively = new LayerSweepTypeDecider(this).useBottomUp();
    }

    private void initParentInfo(final List<GraphData> graphs, final int portId) {
        parent = lGraph.getProperty(InternalProperties.PARENT_LNODE);
        hasParent = parent != null;
        if (hasParent) {
            parentGraphData = graphs.get(parent.getGraph().id);
            if (crossMinAlwaysImproves()) {
                parentGraphData.childNumPorts.put(lGraph.id, portId);
            }
        }
    }

    private void initNodePortDistributors(final LGraph graph, final CrossMinType cMT, final int[][] nodePositions,
            final Multimap<LNode, LNode> layoutUnits, final BarycenterState[][] barycenterStates, final int portId,
            final int[] portPos) {
        Random random = graph.getProperty(InternalProperties.RANDOM);
        // if (crossMinAlwaysImproves() && !childGraphs.isEmpty()) {
        // portDistributor = new GreedyPortDistributor(portPos, childNumPorts);} else //TODO-alan
        if (random.nextBoolean()) {
            portDistributor = new NodeRelativePortDistributor(new float[portId], nodePositions);
        } else {
            portDistributor = new LayerTotalPortDistributor(new float[portId], nodePositions);
        }

        switch (cMT) {
        case BARYCENTER:
            IConstraintResolver constraintResolver =
                    new ForsterConstraintResolver(barycenterStates, layoutUnits);
            crossMinimizer = new BarycenterHeuristic(barycenterStates, constraintResolver,
                    random, (AbstractBarycenterPortDistributor) portDistributor);
            break;
        case GREEDY_SWITCH:
            crossMinimizer =
                    new GreedySwitchHeuristic(lGraph.getProperty(LayeredOptions.CROSSING_MINIMIZATION_GREEDY_SWITCH));
            break;
        default:
            throw new UnsupportedOperationException("This heuristic is not implemented yet.");
        }
    }

    private LGraph nestedGraphOf(final LNode node) {
        return node.getProperty(InternalProperties.NESTED_LGRAPH);
    }

    private boolean hasNestedGraph(final LNode node) {
        return nestedGraphOf(node) != null;
    }

    private boolean[] getHyperedges(final LNode[][] nodeOrder) {
        boolean[] hasHyperedges = new boolean[nodeOrder.length];
        for (int layerIndex = 0; layerIndex < nodeOrder.length - 1; layerIndex++) {
            LNode[] leftLayer = nodeOrder[layerIndex];
            hasHyperedges[layerIndex] |= checkForHyperedges(leftLayer, PortSide.EAST);
            LNode[] rightLayer = nodeOrder[layerIndex + 1];
            hasHyperedges[layerIndex] |= checkForHyperedges(rightLayer, PortSide.WEST);
        }
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
     * @return the processRecursively
     */
    public boolean dontSweepInto() {
        return processRecursively;
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
     * @return child graphs
     */
    public Collection<LGraph> childGraphs() {
        return childGraphs;
    }

    /**
     * @return whether this graph's parent node has external ports.
     */
    public boolean hasExternalPorts() {
        return externalPorts;
    }

    @Override
    public String toString() {
        return lGraph.toString();
    }

    /**
     * @return Copy of node order for currently best sweep.
     */
    public SweepCopy getBestSweep() {
        return crossMinimizer.isDeterministic() ? currentlyBestNodeAndPortOrder()
                : bestNodeNPortOrder();
    }

    /**
     * @return the parentGraphData
     */
    public GraphData parentGraphData() {
        return parentGraphData;
    }

    /**
     * @return
     */
    public boolean crossMinDeterministic() {
        return crossMinimizer.isDeterministic();
    }

    /**
     * @return
     */
    public boolean crossMinAlwaysImproves() {
        return crossMinType == CrossMinType.GREEDY_SWITCH
                && !lGraph.getProperty(LayeredOptions.CROSSING_MINIMIZATION_GREEDY_SWITCH).isOneSided();
    }

    /**
     * @return
     */
    public NodeInfo[][] nodeInfo() {
        // TODO Auto-generated method stub
        return nodeInfo;
    }

}
