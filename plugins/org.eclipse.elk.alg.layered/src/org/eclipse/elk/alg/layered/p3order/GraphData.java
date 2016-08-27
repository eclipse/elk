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
import java.util.List;

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
    /** Raw graph data. */
    private LGraph lGraph;

    /** Saved node orders. */
    private LNode[][] currentNodeOrder;
    private SweepCopy currentlyBestNodeAndPortOrder;
    private SweepCopy bestNodeNPortOrder;

    /** Processing type information. */
    private boolean processRecursively;
    private CrossMinType crossMinType;

    /** Hierarchy access. */
    private List<LGraph> childGraphs;
    private boolean hasExternalPorts;
    private boolean hasParent;
    private GraphData parentGraphData;
    private LNode parent;

    /** Pre-initialized auxiliary objects. */
    private ICrossingMinimizationHeuristic crossMinimizer;
    private ISweepPortDistributor portDistributor;
    private AllCrossingsCounter crossingsCounter;

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

        int graphSize = graph.getLayers().size();
        crossMinType = cMT;
        childGraphs = Lists.newArrayList();

        // Per-node information for crossing counting, crossing minimization
        int[][] nodePositions = new int[graphSize][];
        NodeInfo[][] nodeInfo = new NodeInfo[graphSize][];
        currentNodeOrder = new LNode[graphSize][];
        BarycenterState[][] barycenterStates = new BarycenterState[graph.getLayers().size()][];
        Multimap<LNode, LNode> layoutUnits = LinkedHashMultimap.create();

        // Per-layer information for crossings counting
        boolean[] hasNorthSouthPorts = new boolean[graphSize];
        int[] inLayerEdgeCount = new int[graphSize];
        boolean[] hasHyperEdges = new boolean[graphSize];

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
                        if (edge.getTarget().getNode().getLayer() == edge.getSource().getNode().getLayer()) {
                            inLayerEdgeCount[layerIndex]++;
                        }
                    }
                    if (port.getOutgoingEdges().size() + port.getIncomingEdges().size() > 1) {
                        if (port.getSide() == PortSide.EAST) {
                            hasHyperEdges[layerIndex] = true;
                        } else if (port.getSide() == PortSide.WEST && layerIndex > 0) {
                            hasHyperEdges[layerIndex - 1] = true;
                        }
                    }
                }

                LNode layoutUnit = node.getProperty(InternalProperties.IN_LAYER_LAYOUT_UNIT);
                if (layoutUnit != null) {
                    layoutUnits.put(layoutUnit, node);
                }
            }
        }

        crossingsCounter =
                new AllCrossingsCounter(inLayerEdgeCount, hasNorthSouthPorts, hasHyperEdges, new int[portId]);

        portDistributor = initPortDistributor(nodePositions, portId);

        initCrossingsMinimizer(graph, cMT, barycenterStates, layoutUnits, portDistributor);

        initHierarchyInformation(graph, graphs, nodeInfo);
    }

    private void initHierarchyInformation(final LGraph graph, final List<GraphData> graphs,
            final NodeInfo[][] nodeInfo) {
        parent = lGraph.getProperty(InternalProperties.PARENT_LNODE);
        hasParent = parent != null;
        if (hasParent) {
            parentGraphData = graphs.get(parent.getGraph().id);
        }
        hasExternalPorts =
                graph.getProperty(InternalProperties.GRAPH_PROPERTIES).contains(GraphProperties.EXTERNAL_PORTS);
        processRecursively = new LayerSweepTypeDecider(this, nodeInfo).useBottomUp();
    }

    private void initCrossingsMinimizer(final LGraph graph, final CrossMinType cMT,
            final BarycenterState[][] barycenterStates, final Multimap<LNode, LNode> layoutUnits,
            final ISweepPortDistributor portDist) {
        if (cMT == CrossMinType.BARYCENTER) {
            crossMinimizer = new BarycenterHeuristic(barycenterStates,
                    new ForsterConstraintResolver(barycenterStates, layoutUnits),
                    graph.getProperty(InternalProperties.RANDOM),
                    (AbstractBarycenterPortDistributor) portDist);
        } else {
            crossMinimizer =
                    new GreedySwitchHeuristic(lGraph.getProperty(LayeredOptions.CROSSING_MINIMIZATION_GREEDY_SWITCH));
        }
    }

    private ISweepPortDistributor initPortDistributor(final int[][] nodePositions, final int portId) {
        // if (crossMinAlwaysImproves() && !childGraphs.isEmpty()) {
        // portDistributor = new GreedyPortDistributor(portPos, childNumPorts);} else //TODO-alan
        if (lGraph.getProperty(InternalProperties.RANDOM).nextBoolean()) {
            return new NodeRelativePortDistributor(new float[portId], nodePositions);
        } else {
            return new LayerTotalPortDistributor(new float[portId], nodePositions);
        }
    }

    private LGraph nestedGraphOf(final LNode node) {
        return node.getProperty(InternalProperties.NESTED_LGRAPH);
    }

    private boolean hasNestedGraph(final LNode node) {
        return nestedGraphOf(node) != null;
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
        return crossingsCounter;
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
    public ISweepPortDistributor portDistributor() {
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
        return hasExternalPorts;
    }

    @Override
    public String toString() {
        return lGraph.toString();
    }

    /**
     * @return Copy of node order for currently best sweep.
     */
    public SweepCopy getBestSweep() {
        return crossMinimizer.isDeterministic() ? currentlyBestNodeAndPortOrder() : bestNodeNPortOrder();
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
     * TODO-alan change double cross-min type greedy switch.
     * 
     * @return
     */
    public boolean crossMinAlwaysImproves() {
        return crossMinType == CrossMinType.GREEDY_SWITCH
                && !lGraph.getProperty(LayeredOptions.CROSSING_MINIMIZATION_GREEDY_SWITCH).isOneSided();
    }

}
