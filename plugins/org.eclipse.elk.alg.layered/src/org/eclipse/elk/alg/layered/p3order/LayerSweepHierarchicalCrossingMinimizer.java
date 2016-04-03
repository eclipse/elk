/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.eclipse.elk.alg.layered.ILayoutPhase;
import org.eclipse.elk.alg.layered.IntermediateProcessingConfiguration;
import org.eclipse.elk.alg.layered.IntermediateProcessingConfiguration.Slot;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.layered.p3order.counting.PortIterable;
import org.eclipse.elk.alg.layered.properties.GraphProperties;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * This class minimizes crossings by sweeping through the complete hierarchy, i.e. sweeping through
 * nested graphs in nodes. Thereby it is a <i>hierarchical</i> processor and must have access to the
 * root graph.
 *
 * @author alan
 *
 */
public class LayerSweepHierarchicalCrossingMinimizer implements ILayoutPhase {
    private List<GraphData> graphData;
    private Set<GraphData> graphsWhoseNodeOrderChanged;
    private Random random;
    private long randomSeed;
    private final CrossMinType crossMinType;

    /**
     * Creates LayerSweepHierarchicalCrossingMinimizer using barycenter minimizer type.
     *
     */
    public LayerSweepHierarchicalCrossingMinimizer() {
        crossMinType = CrossMinType.BARYCENTER;
    }

    /**
     * Creates LayerSweepHierarchicalCrossingMinimizer using given minimizer type.
     *
     * @param cT
     *            the crossing minimizer 'type'
     */
    public LayerSweepHierarchicalCrossingMinimizer(final CrossMinType cT) {
        crossMinType = cT;
    }

    @Override
    public boolean operatesOnFullHierarchy() {
        return true;
    }

    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Minimize Crossings " + crossMinType, 1);

        if (layeredGraph.getLayers().isEmpty()) {
            progressMonitor.done();
            return;
        }

        boolean processAllGraphsRecursively =
                layeredGraph.getProperty(LayeredOptions.CROSS_MIN_RECURSIVE);

        Iterable<GraphData> graphsToSweepOn =
                initialize(processAllGraphsRecursively, layeredGraph);

        if (crossMinType.isDeterministic()) {
            Consumer<GraphData> minimizingMethod = crossMinType.alwaysImproves()
                    ? gData -> minimizeCrossingsNoCounter(gData)
                    : gData -> minimizeCrossingsWithCounter(gData);
            if (processAllGraphsRecursively) {
                iterateAndMinimize(graphsToSweepOn, minimizingMethod);
            } else {
                minimizingMethod.accept(graphData.get(layeredGraph.id));
            }
        } else {
            iterateAndMinimize(graphsToSweepOn, gData -> compareDifferentRandomizedLayouts(gData));
        }

        setGraphs();

        progressMonitor.done();
    }

    private void iterateAndMinimize(final Iterable<GraphData> graphsToSweepOn,
            final Consumer<GraphData> minimizingMethod) {
        for (GraphData gData : graphsToSweepOn) {
            minimizingMethod.accept(gData);
            if (gData.hasParent()) {
                setPortOrderOnParentGraph(gData);
            }
        }
    }

    private void setPortOrderOnParentGraph(final GraphData gData) {
        if (gData.hasExternalPorts()) {
            SweepCopy bestSweep = gData.getBestSweep();
            sortPortsByDummyPositionsInLastLayer(bestSweep.nodes(), gData.parent(), true);
            sortPortsByDummyPositionsInLastLayer(bestSweep.nodes(), gData.parent(), false);
            gData.parent().setProperty(CoreOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_ORDER);
        }
        gData.setCanSweepIntoThisGraph(false);
    }

    private void compareDifferentRandomizedLayouts(final GraphData gData) {
        // Reset the seed, otherwise copies of hierarchical graphs in different parent nodes are
        // layouted differently.
        random.setSeed(randomSeed);

        // In order to only copy graphs whose node order has changed, save them in a set.
        graphsWhoseNodeOrderChanged.clear();

        int bestCrossings = Integer.MAX_VALUE;
        int thouroughness = gData.lGraph().getProperty(LayeredOptions.THOROUGHNESS);
        for (int i = 0; i < thouroughness; i++) {

            int crossings = minimizeCrossingsWithCounter(gData);
            if (crossings < bestCrossings) {
                bestCrossings = crossings;
                saveAllNodeOrdersOfChangedGraphs();
                if (bestCrossings == 0) {
                    break;
                }
            }
        }
    }

    private int minimizeCrossingsWithCounter(final GraphData gData) {
        boolean isForwardSweep = random.nextBoolean();

        gData.crossMinimizer().setFirstLayerOrder(gData.currentNodeOrder(), isForwardSweep);
        sweepReducingCrossings(gData, isForwardSweep, true);
        int crossingsInGraph = countCurrentNumberOfCrossings();
        int oldNumberOfCrossings;
        do {
            setCurrentlyBestNodeOrders();

            if (crossingsInGraph == 0) {
                return 0;
            }

            isForwardSweep = !isForwardSweep;
            oldNumberOfCrossings = crossingsInGraph;
            sweepReducingCrossings(gData, isForwardSweep, false);
            crossingsInGraph = countCurrentNumberOfCrossings();
        } while (oldNumberOfCrossings > crossingsInGraph);

        return oldNumberOfCrossings;
    }

    private int countCurrentNumberOfCrossings() {
        int totalCrossings = 0;
        for (GraphData gD : graphData) {
            totalCrossings +=
                    gD.crossCounter().countAllCrossings(gD.currentNodeOrder());
        }

        return totalCrossings;
    }

    private void minimizeCrossingsNoCounter(final GraphData gData) {
        boolean isForwardSweep = random.nextBoolean();
        boolean improved = gData.crossMinimizer().setFirstLayerOrder(gData.currentNodeOrder(), isForwardSweep);
        do {
            improved = sweepReducingCrossingsWithNoCounting(gData, isForwardSweep);
            isForwardSweep = !isForwardSweep;
        } while (improved);
        setCurrentlyBestNodeOrders();
    }

    private boolean sweepReducingCrossingsWithNoCounting(final GraphData graph, final boolean forward) {
        LNode[][] nodes = graph.currentNodeOrder();
        int length = nodes.length;

        LNode[] firstLayer = nodes[firstIndex(forward, length)];
        layoutHierarchicalNodes(firstLayer, forward, false);

        boolean improved = false;
        for (int i = firstFree(forward, length); isNotEnd(length, i, forward); i += next(forward)) {
            improved |= graph.crossMinimizer().minimizeCrossings(nodes, i, forward, false);
            graph.portDistributor().distributePortsWhileSweeping(nodes, i, forward);
            layoutHierarchicalNodes(nodes[i], forward, false);
        }

        graphsWhoseNodeOrderChanged.add(graph);

        return improved;
    }

    private boolean sweepReducingCrossings(final GraphData graph, final boolean forward,
            final boolean firstSweep) {
        LNode[][] nodes = graph.currentNodeOrder();
        int length = nodes.length;
        LNode[] firstLayer = nodes[firstIndex(forward, length)];
        boolean improved = layoutHierarchicalNodes(firstLayer, forward, firstSweep);

        for (int i = firstFree(forward, length); isNotEnd(length, i, forward); i += next(forward)) {
            improved |= graph.crossMinimizer().minimizeCrossings(nodes, i, forward, firstSweep);
            graph.portDistributor().distributePortsWhileSweeping(nodes, i, forward);
            improved |= layoutHierarchicalNodes(nodes[i], forward, firstSweep);
        }

        graphsWhoseNodeOrderChanged.add(graph);
        return improved;
    }

    private boolean layoutHierarchicalNodes(final LNode[] layer, final boolean isForwardSweep,
            final boolean isFirstSweep) {
        boolean improved = false;
        for (LNode node : layer) {
            if (hasNestedGraph(node) && !graphData.get(nestedGraphOf(node).id).processRecursively()) {
                improved |= layoutHierarchicalNode(isForwardSweep, node, isFirstSweep);
            }
        }
        return improved;
    }

    private boolean layoutHierarchicalNode(final boolean isForwardSweep, final LNode node,
            final boolean isFirstSweep) {
        LGraph nestedLGraph = nestedGraphOf(node);
        GraphData nestedGraph = graphData.get(nestedLGraph.id);
        LNode[][] nestedGraphNodeOrder = nestedGraph.currentNodeOrder();
        int startIndex = firstIndex(isForwardSweep, nestedGraphNodeOrder.length);
        LNode firstNode = nestedGraphNodeOrder[startIndex][0];

        if (isExternalPortDummy(firstNode)) {
            nestedGraphNodeOrder[startIndex] = sortPortDummiesByPortPositions(node,
                    nestedGraphNodeOrder[startIndex], sideOpposedSweepDirection(isForwardSweep));
        } else {
            nestedGraph.crossMinimizer().setFirstLayerOrder(nestedGraphNodeOrder, isForwardSweep);
        }

        boolean improved = sweepReducingCrossings(nestedGraph, isForwardSweep, isFirstSweep);
        sortPortsByDummyPositionsInLastLayer(nestedGraph.currentNodeOrder(), nestedGraph.parent(),
                isForwardSweep);

        return improved;
    }

    private void sortPortsByDummyPositionsInLastLayer(final LNode[][] nodeOrder, final LNode parent,
            final boolean isForwardSweep) {
        int endIndex = endIndex(isForwardSweep, nodeOrder.length);
        LNode[] lastLayer = nodeOrder[endIndex];
        if (!isExternalPortDummy(lastLayer[0])) {
            return;
        }

        int j = firstIndex(isForwardSweep, lastLayer.length);
        List<LPort> ports = parent.getPorts();
        for (int i = 0; i < ports.size(); i++) {
            LPort port = ports.get(i);
            if (isOnEndOfSweepSide(port, isForwardSweep) && isHierarchical(port)) {
                ports.set(i, originPort(lastLayer[j]));
                j += next(isForwardSweep);
            }
        }
    }

    private LNode[] sortPortDummiesByPortPositions(final LNode parentNode,
            final LNode[] layerCloseToNodeEdge, final PortSide side) {
        Iterable<LPort> ports = PortIterable.inNorthSouthEastWestOrder(parentNode, side);

        LNode[] sortedDummies = new LNode[layerCloseToNodeEdge.length];

        int i = 0;
        for (LPort port : ports) {
            if (isHierarchical(port)) {
                sortedDummies[i++] = dummyNodeFor(port);
            }
        }

        return sortedDummies;
    }

    private void setGraphs() {
        for (GraphData gD : graphData) {
            SweepCopy sweep = crossMinType.isDeterministic() ? gD.currentlyBestNodeAndPortOrder()
                    : gD.bestNodeNPortOrder();
            sweep.setSavedPortOrdersToNodes();
            List<Layer> layers = gD.lGraph().getLayers();
            for (int i = 0; i < layers.size(); i++) {
                List<LNode> nodes = layers.get(i).getNodes();
                for (int j = 0; j < nodes.size(); j++) {
                    LNode node = sweep.nodes()[i][j];
                    gD.lGraph().getLayers().get(i).getNodes().set(j, node);
                }
            }
        }
    }

    private void saveAllNodeOrdersOfChangedGraphs() {
        for (GraphData graph : graphsWhoseNodeOrderChanged) {
            graph.setBestNodeNPortOrder(new SweepCopy(graph.currentlyBestNodeAndPortOrder()));
        }
    }

    private void setCurrentlyBestNodeOrders() {
        for (GraphData graph : graphsWhoseNodeOrderChanged) {
            graph.setCurrentlyBestNodeAndPortOrder(new SweepCopy(graph.currentNodeOrder()));
        }
    }

    private int firstIndex(final boolean isForwardSweep, final int length) {
        return isForwardSweep ? 0 : length - 1;
    }

    private int endIndex(final boolean isForwardSweep, final int length) {
        return isForwardSweep ? length - 1 : 0;
    }

    private int firstFree(final boolean isForwardSweep, final int length) {
        return isForwardSweep ? 1 : length - 2;
    }

    private int next(final boolean isForwardSweep) {
        return isForwardSweep ? 1 : -1;
    }

    private boolean isNotEnd(final int length, final int freeLayerIndex,
            final boolean isForwardSweep) {
        return isForwardSweep ? freeLayerIndex < length : freeLayerIndex >= 0;
    }

    private LGraph nestedGraphOf(final LNode node) {
        return node.getProperty(InternalProperties.NESTED_LGRAPH);
    }

    private Boolean hasNestedGraph(final LNode node) {
        return nestedGraphOf(node) != null;
    }

    private PortSide sideOpposedSweepDirection(final boolean isForwardSweep) {
        return isForwardSweep ? PortSide.WEST : PortSide.EAST;
    }

    private boolean isExternalPortDummy(final LNode firstNode) {
        return firstNode.getType() == NodeType.EXTERNAL_PORT;
    }

    private LPort originPort(final LNode node) {
        return (LPort) node.getProperty(InternalProperties.ORIGIN);
    }

    private boolean isHierarchical(final LPort port) {
        return port.getProperty(InternalProperties.INSIDE_CONNECTIONS);
    }

    private LNode dummyNodeFor(final LPort port) {
        return port.getProperty(InternalProperties.PORT_DUMMY);
    }

    private boolean isOnEndOfSweepSide(final LPort port, final boolean isForwardSweep) {
        return isForwardSweep ? port.getSide() == PortSide.EAST : port.getSide() == PortSide.WEST;
    }

    private Iterable<GraphData> initialize(final boolean processAllGraphsRecursively,
            final LGraph rootGraph) {
        graphData = Lists.newArrayList();
        Deque<GraphData> graphsToSweepOn = Lists.newLinkedList();
        random = rootGraph.getProperty(InternalProperties.RANDOM);
        randomSeed = random.nextLong();
        List<LGraph> graphs = Lists.<LGraph>newArrayList(rootGraph);
        int i = 0;
        while (i < graphs.size()) {
            LGraph graph = graphs.get(i);
            graph.id = i++;
            GraphData gData = new GraphData(graph, crossMinType, processAllGraphsRecursively, graphData);
            graphs.addAll(gData.childGraphs());
            graphData.add(gData);
            if (gData.processRecursively()) {
                graphsToSweepOn.push(gData);
            }
        }

        graphsWhoseNodeOrderChanged = Sets.newHashSet();

        return graphsToSweepOn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(
            final LGraph graph) {
        IntermediateProcessingConfiguration configuration = IntermediateProcessingConfiguration
                .fromExisting(INTERMEDIATE_PROCESSING_CONFIGURATION);


            configuration.addBeforePhase3(IntermediateProcessorStrategy.PORT_LIST_SORTER);

        return configuration;
    }

    /** intermediate processing configuration. */
    private static final IntermediateProcessingConfiguration INTERMEDIATE_PROCESSING_CONFIGURATION =
            IntermediateProcessingConfiguration.createEmpty()
                    .addBeforePhase3(IntermediateProcessorStrategy.LONG_EDGE_SPLITTER)
                    .addBeforePhase4(IntermediateProcessorStrategy.HIERARCHICAL_GREEDY_SWITCH)
                    .addBeforePhase4(IntermediateProcessorStrategy.IN_LAYER_CONSTRAINT_PROCESSOR)
                    .addAll(Slot.AFTER_PHASE_5,
                            Lists.newArrayList(IntermediateProcessorStrategy.LONG_EDGE_JOINER,
                                    IntermediateProcessorStrategy.HIERARCHICAL_NODE_RESIZER));
    
    /** TODO Java 8 api not supported yet. 
     * @param <T>*/
    private interface Consumer<T> { 
        void accept(T input);
    }
}
