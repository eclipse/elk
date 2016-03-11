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

import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.eclipse.elk.alg.layered.ILayoutPhase;
import org.eclipse.elk.alg.layered.IntermediateProcessingConfiguration;
import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.layered.p3order.BarycenterHeuristic.BarycenterState;
import org.eclipse.elk.alg.layered.p3order.constraints.ForsterConstraintResolver;
import org.eclipse.elk.alg.layered.p3order.constraints.IConstraintResolver;
import org.eclipse.elk.alg.layered.p3order.counting.AbstractCrossingsCounter;
import org.eclipse.elk.alg.layered.p3order.counting.BarthJuengerMutzelCrossingsCounter;
import org.eclipse.elk.alg.layered.p3order.counting.HyperedgeCrossingsCounter;
import org.eclipse.elk.alg.layered.properties.GraphProperties;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.PortType;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Crossing minimization module that performs one or more sweeps over the layers while applying a
 * two-layer crossing minimization heuristic on each pair of layers. Inspired by
 * <ul>
 * <li>Kozo Sugiyama, Shojiro Tagawa, and Mitsuhiko Toda. Methods for visual understanding of
 * hierarchical system structures. IEEE Transactions on Systems, Man and Cybernetics, 11(2):109–125,
 * February 1981.
 * <li>Michael Forster. A fast and simple heuristic for constrained two-level crossing reduction. In
 * <i>Graph Drawing</i>, volume 3383 of LNCS, pp. 206-216. Springer, 2005.</li>
 * </ul>
 * <p>This is the main layer sweep crossing minimization class that makes use of a number of further
 * classes. This one implements the actual layer sweep logic.</p>
 * 
 * <dl>
 * <dt>Precondition:</dt>
 * <dd>The graph has a proper layering, i.e. all long edges have been splitted; all nodes have at
 * least fixed port sides.</dd>
 * <dt>Postcondition:</dt>
 * <dd>The order of nodes in each layer and the order of ports in each node are optimized to yield
 * as few edge crossings as possible</dd>
 * </dl>
 * 
 * @author msp
 * @author cds
 * @author ima
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public final class LayerSweepCrossingMinimizer implements ILayoutPhase {

    /** intermediate processing configuration. */
    private static final IntermediateProcessingConfiguration INTERMEDIATE_PROCESSING_CONFIGURATION =
            IntermediateProcessingConfiguration.createEmpty()
                    .addBeforePhase3(IntermediateProcessorStrategy.LONG_EDGE_SPLITTER)
                    .addBeforePhase4(IntermediateProcessorStrategy.PORT_DISTRIBUTER)
                    .addBeforePhase4(IntermediateProcessorStrategy.GREEDY_SWITCH)
                    .addBeforePhase4(IntermediateProcessorStrategy.IN_LAYER_CONSTRAINT_PROCESSOR)
                    .addAfterPhase5(IntermediateProcessorStrategy.LONG_EDGE_JOINER);

    /**
     * {@inheritDoc}
     */
    public IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(
            final LGraph graph) {
        IntermediateProcessingConfiguration configuration =
                IntermediateProcessingConfiguration.fromExisting(INTERMEDIATE_PROCESSING_CONFIGURATION);
        
        if (graph.getProperty(InternalProperties.GRAPH_PROPERTIES).contains(
                GraphProperties.NON_FREE_PORTS)) {
            
            configuration.addBeforePhase3(IntermediateProcessorStrategy.PORT_LIST_SORTER);
        }
        
        return configuration;
    }
    
    /**
     * Array of port ranks used for sorting nodes and ports.
     */
    private float[] portRanks;
    /**
     * Complete node order of the best layer sweep.
     */
    private LNode[][] bestSweep;
    /**
     * Complete node order of the current layer sweep.
     */
    private LNode[][] curSweep;
    /**
     * Complete node order of the previous layer sweep.
     */
    private LNode[][] prevSweep;
    /**
     * Crossings counter for normal edges.
     */
    private BarthJuengerMutzelCrossingsCounter normalCrossingsCounter;
    /**
     * Crossings counter for hyperedges.
     */
    private HyperedgeCrossingsCounter hyperedgeCrossingsCounter;
    /**
     * Both of the two previous crossing counters use the same 
     * code to count in-layer crossings.
     */
    private AbstractCrossingsCounter inlayerCrossingsCounter;
    /**
     * Whether the layers contain hyperedges or not.
     */
    private boolean[] hasHyperedgesEast;
    private boolean[] hasHyperedgesWest;
    /**
     * Layout units represented by a single node.
     */
    private final Multimap<LNode, LNode> layoutUnits = LinkedListMultimap.create();
    
    /**
     * Initialize all data for the layer sweep crossing minimizer.
     * 
     * @param layeredGraph a layered graph
     */
    private void initialize(final LGraph layeredGraph) {
        int layerCount = layeredGraph.getLayers().size();

        // Remember the best, current and previous sweep; they basically save the node oder
        // per layer for the different sweeps of the algorithm
        bestSweep = new LNode[layerCount][];
        curSweep = new LNode[layerCount][];
        prevSweep = new LNode[layerCount][];

        int[] inLayerEdgeCount = new int[layerCount];
        boolean[] hasNorthSouthPorts = new boolean[layerCount];
        hasHyperedgesEast = new boolean[layerCount];
        hasHyperedgesWest = new boolean[layerCount];

        int nodeCount = 0;
        int portCount = 0;

        // Iterate through the layers, initializing port and node IDs, collecting
        // the nodes into the current sweep and building the layout unit map
        ListIterator<Layer> layerIter = layeredGraph.getLayers().listIterator();
        while (layerIter.hasNext()) {
            Layer layer = layerIter.next();

            int layerIndex = layerIter.previousIndex();
            int layerNodeCount = layer.getNodes().size();
            // Empty layers are not allowed!
            assert layerNodeCount > 0;

            // Initialize this layer's node arrays in the different sweeps
            bestSweep[layerIndex] = new LNode[layerNodeCount];
            prevSweep[layerIndex] = new LNode[layerNodeCount];
            curSweep[layerIndex] = new LNode[layerNodeCount];
            inLayerEdgeCount[layerIndex] = 0;
            hasNorthSouthPorts[layerIndex] = false;

            ListIterator<LNode> nodeIter = layer.getNodes().listIterator();
            while (nodeIter.hasNext()) {
                LNode node = nodeIter.next();

                // Register layout unit
                curSweep[layerIndex][nodeIter.previousIndex()] = node;
                node.id = nodeCount++;
                LNode layoutUnit = node.getProperty(InternalProperties.IN_LAYER_LAYOUT_UNIT);
                if (layoutUnit != null) {
                    layoutUnits.put(layoutUnit, node);
                }

                // Count in-layer edges
                for (LPort port : node.getPorts()) {
                    port.id = portCount++;
                    for (LEdge edge : port.getOutgoingEdges()) {
                        if (edge.getTarget().getNode().getLayer() == layer) {
                            inLayerEdgeCount[layerIndex]++;
                        }
                    }
                    if (port.getSide() == PortSide.EAST) {
                        if (port.getOutgoingEdges().size() + port.getIncomingEdges().size() > 1) {
                            hasHyperedgesEast[layerIndex] = true;
                        }
                    } else if (port.getSide() == PortSide.WEST) {
                        if (port.getOutgoingEdges().size() + port.getIncomingEdges().size() > 1) {
                            hasHyperedgesWest[layerIndex] = true;
                        }
                    } else {
                        // North and south port should not have any connected edges at this moment.
                        assert port.getOutgoingEdges().isEmpty() && port.getIncomingEdges().isEmpty();
                    }
                }
                
                // Count north/south dummy nodes
                if (node.getType() == NodeType.NORTH_SOUTH_PORT) {
                    inLayerEdgeCount[layerIndex]++;
                    hasNorthSouthPorts[layerIndex] = true;
                }
            }
        }
        
        // Check whether every (or no) combination of layers, considering both
        //  forward and backward sweeps, involves hyperedges
        // If neither is the case, we need a counting algorithm for both 
        //  hyperedges and straightline edges
        boolean allLayerCombinationsHaveHyperedges = true;
        boolean noLayerCombinationHasHyperedges = true;
        for (int i = 0; i < hasHyperedgesWest.length - 1; i++) {
            boolean b = hasHyperedgesEast[i] || hasHyperedgesWest[i + 1];
            allLayerCombinationsHaveHyperedges &= b;
            noLayerCombinationHasHyperedges &= !b;
        }
        
        // Initialize the port positions and ranks arrays
        portRanks = new float[portCount];
        int[] portPos = new int[portCount];
        
        // Create the crossings counter modules
        if (!allLayerCombinationsHaveHyperedges) {
            normalCrossingsCounter = new BarthJuengerMutzelCrossingsCounter(inLayerEdgeCount,
                    hasNorthSouthPorts, portPos);
            inlayerCrossingsCounter = normalCrossingsCounter;
        }
        
        if (!noLayerCombinationHasHyperedges) {
            hyperedgeCrossingsCounter = new HyperedgeCrossingsCounter(inLayerEdgeCount,
                    hasNorthSouthPorts, portPos);
            inlayerCrossingsCounter = hyperedgeCrossingsCounter;
        }
    }

    /**
     * Releases all created data so the GC can reap them.
     */
    private void dispose() {
        portRanks = null;
        bestSweep = null;
        curSweep = null;
        prevSweep = null;
        normalCrossingsCounter = null;
        hyperedgeCrossingsCounter = null;
        hasHyperedgesEast = null;
        hasHyperedgesWest = null;
        layoutUnits.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Layer sweep crossing minimization", 1);

        // Fetch the graph's randomizer.
        Random random = layeredGraph.getProperty(InternalProperties.RANDOM);

        // Find the number of layers. If there's only one, no crossing minimization is necessary.
        int layerCount = layeredGraph.getLayers().size();
        if (layerCount < 2) {
            monitor.done();
            return;
        }
        
        // Initialize the algorithm
        initialize(layeredGraph);
        int bestSweepCrossings = Integer.MAX_VALUE;

        // Determine the requested number of runs
        int runCount = layeredGraph.getProperty(LayeredOptions.THOROUGHNESS);

        // Initialize barycenter states used by the barycenter heuristic and forster 
        // assign ids to layers and nodes
        BarycenterState[][] barycenterStates = new BarycenterState[layeredGraph.getLayers().size()][];
        int i = 0;
        for (Layer layer : layeredGraph.getLayers()) {
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
        
        // Initialize the compound graph layer crossing minimizer
        IConstraintResolver constraintResolver =
                new ForsterConstraintResolver(barycenterStates, layoutUnits);
        ICrossingMinimizationHeuristic crossminHeuristic =
                new BarycenterHeuristic(barycenterStates, constraintResolver, random, portRanks);
        
        // Create port distributors
        NodeRelativePortDistributor nodeRelativePortDistributor
            = NodeRelativePortDistributor.create(portRanks);
        LayerTotalPortDistributor layerTotalPortDistributor =
                LayerTotalPortDistributor.create(portRanks);
        AbstractPortDistributor portDistributor;

        // Perform the requested number of runs, each consisting of several sweeps
        for (int run = 0; run < runCount && bestSweepCrossings > 0; run++) {
            // Each run is randomly determined to be a forward or a backward run
            boolean forward = random.nextBoolean();
            int fixedLayerIndex = forward ? 0 : layerCount - 1;
            LNode[] fixedLayer = curSweep[fixedLayerIndex];
            
            // Randomly choose a port distribution method for this run
            portDistributor = random.nextBoolean()
                    ? nodeRelativePortDistributor : layerTotalPortDistributor;

            // The fixed layer is randomized
            minimizeCrossings(fixedLayer, crossminHeuristic, forward, false, true);

            // Reset last and current run crossing counters
            int curSweepCrossings = Integer.MAX_VALUE;
            int prevSweepCrossings = Integer.MAX_VALUE;

            // Do alternating forward and backward sweeps as long as the number of crossings
            // decreases with respect to the previous sweep.
            boolean firstSweep = true;
            do {
                // The formerly current sweep is now the previous sweep
                copySweep(curSweep, prevSweep);
                prevSweepCrossings = curSweepCrossings;
                curSweepCrossings = 0;
                
                // count in-layer crossings
                curSweepCrossings += inlayerCrossingsCounter.countCrossings(fixedLayer, fixedLayerIndex);
                
                if (forward) {
                    // Perform a forward sweep
                    for (int layerIndex = 1; layerIndex < layerCount; layerIndex++) {
                        LNode[] freeLayer = curSweep[layerIndex];

                        portDistributor.calculatePortRanks(fixedLayer, PortType.OUTPUT);
                        minimizeCrossings(freeLayer, crossminHeuristic, true, !firstSweep, false);

                        // in-layer crossings
                        curSweepCrossings +=
                                inlayerCrossingsCounter.countCrossings(freeLayer, layerIndex);
                        // between-layers crossings
                        if (hasHyperedgesWest[layerIndex] || hasHyperedgesEast[layerIndex - 1]) {
                            curSweepCrossings += 
                                    hyperedgeCrossingsCounter.countCrossings(fixedLayer, freeLayer);
                        } else {
                            curSweepCrossings += 
                                    normalCrossingsCounter.countCrossings(fixedLayer, freeLayer);
                        }
                        

                        fixedLayer = freeLayer;
                    }
                    fixedLayerIndex = layerCount - 1;
                } else {
                    // Perform a backward sweep
                    for (int layerIndex = layerCount - 2; layerIndex >= 0; layerIndex--) {
                        LNode[] freeLayer = curSweep[layerIndex];

                        portDistributor.calculatePortRanks(fixedLayer, PortType.INPUT);
                        minimizeCrossings(freeLayer, crossminHeuristic, false, !firstSweep, false);

                        // in-layer crossings
                        curSweepCrossings +=
                                inlayerCrossingsCounter.countCrossings(freeLayer, layerIndex);
                        // between-layers crossings
                        if (hasHyperedgesEast[layerIndex] || hasHyperedgesWest[layerIndex + 1]) {
                            curSweepCrossings += 
                                    hyperedgeCrossingsCounter.countCrossings(freeLayer, fixedLayer);
                        } else {
                            curSweepCrossings += 
                                    normalCrossingsCounter.countCrossings(freeLayer, fixedLayer);
                        }

                        fixedLayer = freeLayer;
                    }
                    fixedLayerIndex = 0;
                }

                // Switch the sweep direction
                firstSweep = false;
                forward = !forward;
            } while (curSweepCrossings < prevSweepCrossings && curSweepCrossings > 0);

            // Compare the current result with the best one
            if (curSweepCrossings < bestSweepCrossings || prevSweepCrossings < bestSweepCrossings) {
                // Restore the previous sweep's ordering if it has become worse
                if (curSweepCrossings <= prevSweepCrossings) {
                    copySweep(curSweep, bestSweep);
                    bestSweepCrossings = curSweepCrossings;
                } else {
                    copySweep(prevSweep, bestSweep);
                    bestSweepCrossings = prevSweepCrossings;
                }
            }
        }

        // Apply the ordering to the original layered graph
        ListIterator<Layer> layerIter = layeredGraph.getLayers().listIterator();
        while (layerIter.hasNext()) {
            Layer layer = layerIter.next();
            LNode[] nodes = bestSweep[layerIter.previousIndex()];
            ListIterator<LNode> nodeIter = layer.getNodes().listIterator();
            while (nodeIter.hasNext()) {
                nodeIter.next();
                nodeIter.set(nodes[nodeIter.previousIndex()]);
            }
        }
        
        // In the old days, the ports were distributed at this point in time. This has been moved to a
        // separate processor, the PortDistributionProcessor.

        dispose();
        monitor.done();
    }
    
    /**
     * Minimize crossings between the given layer and its preceding or subsequent layer.
     * 
     * @param layer the layer that is to be reordered
     * @param heuristic the crossing minimization heuristic
     * @param forward if true the preceding layer is taken as fixed layer, otherwise the subsequent
     *          layer is taken
     * @param preOrdered whether the nodes of the given layer are already ordered
     * @param randomize whether to randomize all node positions
     */
    private void minimizeCrossings(final LNode[] layer,
            final ICrossingMinimizationHeuristic heuristic,
            final boolean forward, final boolean preOrdered, final boolean randomize) {
        
        List<LNode> nodes = Lists.newArrayList(layer);
        
        // minimize crossings in the given layer
        heuristic.minimizeCrossings(nodes, preOrdered, randomize, forward);
        
        // apply the new ordering
        int index = 0;
        for (LNode nodeGroup : nodes) {
                layer[index++] = nodeGroup;
        }
    }
    

    // /////////////////////////////////////////////////////////////////////////////
    // Utility Methods

    /**
     * Copy the content of the source node array to the target node array.
     * 
     * @param source
     *            a layered graph
     * @param dest
     *            a node array to copy the graph into
     */
    private static void copySweep(final LNode[][] source, final LNode[][] dest) {
        for (int i = 0; i < dest.length; i++) {
            for (int j = 0; j < dest[i].length; j++) {
                dest[i][j] = source[i][j];
            }
        }
    }
    
}
