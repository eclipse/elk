/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.options.PortType;
import org.eclipse.elk.alg.layered.p3order.BarycenterHeuristic.BarycenterState;

import com.google.common.collect.Lists;

/**
 * Minimizes crossing with the barycenter method. However, the node order given by the order in the model
 * does not change. I.e. only the dummy nodes are sorted in the already sorted real nodes.
 */
public final class ModelOrderBarycenterHeuristic  implements ICrossingMinimizationHeuristic {

    /** the array of port ranks. */
    private float[] portRanks;
    /** the random number generator. */
    private final Random random;
    /** the constraint resolver for ordering constraints. */
    private ForsterConstraintResolver constraintResolver;
    /** the barycenter values of every node in the graph, indexed by layer.id and node.id. */
    private BarycenterState[][] barycenterState;
    /** The Barycenter PortDistributor is used to ask for the port ranks.*/
    private final AbstractBarycenterPortDistributor portDistributor;
    
    /**
     * Each node has an entry of nodes for which it is bigger.
     */
    private HashMap<LNode, HashSet<LNode>> biggerThan = new HashMap<>();
    /**
     * Each node has an entry of nodes for which it is smaller.
     */
    private HashMap<LNode, HashSet<LNode>> smallerThan = new HashMap<>();

    /**
     * Constructs a model order barycenter heuristic for crossing minimization.
     * After sorting {@link #clearTransitiveOrdering()} should be called. 
     * 
     * @param constraintResolver
     *            the constraint resolver
     * @param random
     *            the random number generator
     * @param portDistributor
     *            calculates the port ranks for the barycenter heuristic.
     * @param graph
     *            current node order
     */
    public ModelOrderBarycenterHeuristic(final ForsterConstraintResolver constraintResolver, final Random random,
            final AbstractBarycenterPortDistributor portDistributor, final LNode[][] graph) {
        this.constraintResolver = constraintResolver;
        this.random = random;
        this.portDistributor = portDistributor;
        barycenterStateComparator = 
                (n1, n2) -> {
                    int transitiveComparison = compareBasedOnTansitiveDependencies(n1, n2);
                    if (transitiveComparison != 0) {
                        return transitiveComparison;
                    }
                    if (n1.hasProperty(InternalProperties.MODEL_ORDER)
                            && n2.hasProperty(InternalProperties.MODEL_ORDER)) {
                        int value = Integer.compare(n1.getProperty(InternalProperties.MODEL_ORDER),
                                n2.getProperty(InternalProperties.MODEL_ORDER));
                        if (value < 0) {
                            updateBiggerAndSmallerAssociations(n1, n2);
                        } else if (value > 0) {
                            updateBiggerAndSmallerAssociations(n2, n1);
                        }
                        return value;
                    }
                    return compareBasedOnBarycenter(n1, n2);
                };
    }

    /**
     * Don't use!
     * Only public to be accessible by a test.
     */
    public void minimizeCrossings(final List<LNode> layer, final boolean preOrdered,
            final boolean randomize, final boolean forward) {

        if (randomize) {
            // Randomize barycenters (we don't need to update the edge count in this case;
            // there are no edges of interest since we're only concerned with one layer)
            randomizeBarycenters(layer);
        } else {
            // Calculate barycenters and assign barycenters to barycenterless node groups
            calculateBarycenters(layer, forward);
            fillInUnknownBarycenters(layer, preOrdered);
        }

        if (layer.size() > 1) {
            // Sort the vertices according to their barycenters
            if (layer.get(0).getGraph().getProperty(LayeredOptions.CROSSING_MINIMIZATION_FORCE_NODE_MODEL_ORDER)) {
                ModelOrderBarycenterHeuristic.insertionSort(layer, barycenterStateComparator,
                        (ModelOrderBarycenterHeuristic) this);
            } else {
                Collections.sort(layer, barycenterStateComparator);
            }

            // Resolve ordering constraints
            if (!layer.get(0).getGraph().getProperty(LayeredOptions.CROSSING_MINIMIZATION_FORCE_NODE_MODEL_ORDER)) {
                constraintResolver.processConstraints(layer);
            }
        }
    }

    /**
     * Randomize the order of nodes for the given layer.
     * 
     * @param nodes
     *            a layer
     */
    protected void randomizeBarycenters(final List<LNode> nodes) {
        for (LNode node : nodes) {
            // Set barycenters only for nodeGroups containing a single node.
            stateOf(node).barycenter = random.nextDouble();
            stateOf(node).summedWeight = stateOf(node).barycenter;
            stateOf(node).degree = 1;
        }
    }

    /**
     * Try to find appropriate barycenter values for node groups whose barycenter is undefined.
     * 
     * @param nodes
     *            the nodeGroups to fill in barycenters for.
     * @param preOrdered
     *            whether the nodeGroups have been ordered in a previous run.
     */
    protected void fillInUnknownBarycenters(final List<LNode> nodes, final boolean preOrdered) {
        // Determine placements for nodes with undefined barycenter value
        if (preOrdered) {
            double lastValue = -1;
            ListIterator<LNode> nodesIterator = nodes.listIterator();

            while (nodesIterator.hasNext()) {
                LNode node = nodesIterator.next();
                Double value = stateOf(node).barycenter;

                if (value == null) {
                    // The barycenter is undefined - take the center of the previous value
                    // and the next defined value in the list
                    double nextValue = lastValue + 1;

                    ListIterator<LNode> nextNodeIterator =
                            nodes.listIterator(nodesIterator.nextIndex());
                    while (nextNodeIterator.hasNext()) {
                        Double x = stateOf(nextNodeIterator.next()).barycenter;
                        if (x != null) {
                            nextValue = x;
                            break;
                        }
                    }

                    value = (lastValue + nextValue) / 2;
                    stateOf(node).barycenter = value;
                    stateOf(node).summedWeight = value;
                    stateOf(node).degree = 1;
                }

                lastValue = value;
            }
        } else {
            // No previous ordering - determine random placement for new nodes
            double maxBary = 0;
            for (LNode node : nodes) {
                if (stateOf(node).barycenter != null) {
                    maxBary = Math.max(maxBary, stateOf(node).barycenter);
                }
            }

            maxBary += 2;
            for (LNode node : nodes) {
                if (stateOf(node).barycenter == null) {
                    double value = random.nextFloat() * maxBary - 1;
                    stateOf(node).barycenter = value;
                    stateOf(node).summedWeight = value;
                    stateOf(node).degree = 1;
                }
            }
        }
    }
    
    /**
     * Calculate the barycenters of the given node groups.
     * 
     * @param nodes
     *            the nodes
     * @param forward
     *            {@code true} if the current sweep moves forward
     */
    protected void calculateBarycenters(final List<LNode> nodes, final boolean forward) {
        // Set all visited flags to false
        for (LNode node : nodes) {
            stateOf(node).visited = false;
        }

        for (LNode node : nodes) {
            // Calculate the node groups's new barycenter (may be null)
            calculateBarycenter(node, forward);
        }
    }

    /** the amount of random value to add to each calculated barycenter. */
    private static final float RANDOM_AMOUNT = 0.07f;

    /**
     * Calculate the barycenter of the given single-node node group. This method is able to handle
     * in-layer edges, but it may give incorrect results if the in-layer edges form a cycle.
     * However, such cases do not occur in the present implementation.
     * 
     * @param node
     *            a node group consisting of a single node
     * @param forward
     *            {@code true} if the current sweep moves forward
     * @param portPos
     *            position array
     * @return a pair containing the summed port positions of the connected ports as the first, and
     *         the number of connected edges as the second entry.
     */
    private void calculateBarycenter(final LNode node, final boolean forward) {

        // Check if the node group's barycenter was already computed
        if (stateOf(node).visited) {
            return;
        } else {
            stateOf(node).visited = true;
        }

        stateOf(node).degree = 0;
        stateOf(node).summedWeight = 0.0f;
        stateOf(node).barycenter = null;

        for (LPort freePort : node.getPorts()) {
            Iterable<LPort> portIterable =
                    forward ? freePort.getPredecessorPorts() : freePort.getSuccessorPorts();
            for (LPort fixedPort : portIterable) {
                // If the node the fixed port belongs to is part of the free layer (thus, if
                // we have an in-layer edge), use that node's barycenter calculation instead
                LNode fixedNode = fixedPort.getNode();

                if (fixedNode.getLayer() == node.getLayer()) {
                    // Self-loops are ignored
                    if (fixedNode != node) {
                        // Find the fixed node's node group and calculate its barycenter
                        calculateBarycenter(fixedNode, forward);

                        // Update this node group's values
                        stateOf(node).degree += stateOf(fixedNode).degree;
                        stateOf(node).summedWeight += stateOf(fixedNode).summedWeight;
                    }
                } else {
                    stateOf(node).summedWeight += portRanks[fixedPort.id];
                    stateOf(node).degree++;
                }
            }
        }

        // Iterate over the node's barycenter associates
        List<LNode> barycenterAssociates =
                node.getProperty(InternalProperties.BARYCENTER_ASSOCIATES);
        if (barycenterAssociates != null) {
            for (LNode associate : barycenterAssociates) {
                // Make sure the associate is in the same layer as this node
                if (node.getLayer() == associate.getLayer()) {
                    // Find the associate's node group and calculate its barycenter
                    calculateBarycenter(associate, forward);

                    // Update this vertex's values
                    stateOf(node).degree += stateOf(associate).degree;
                    stateOf(node).summedWeight += stateOf(associate).summedWeight;
                }
            }
        }

        if (stateOf(node).degree > 0) {
            // add a small random perturbation in order to increase diversity of solutions
            stateOf(node).summedWeight += random.nextFloat() * RANDOM_AMOUNT - RANDOM_AMOUNT / 2;
            stateOf(node).barycenter = stateOf(node).summedWeight / stateOf(node).degree;
        }
    }

    private BarycenterState stateOf(final LNode node) {
        return barycenterState[node.getLayer().id][node.id];
    }
    
    /**
     * Compares two {@link LNode}s based on their barycenter values.
     */
    private Comparator<LNode> barycenterStateComparator;

    @Override
    public boolean minimizeCrossings(final LNode[][] order, final int freeLayerIndex,
            final boolean forwardSweep, final boolean isFirstSweep) {
        if (!isFirstLayer(order, freeLayerIndex, forwardSweep)) {
            LNode[] fixedLayer = order[freeLayerIndex - changeIndex(forwardSweep)];
            portDistributor.calculatePortRanks(fixedLayer, portTypeFor(forwardSweep));
        }

        LNode firstNodeInLayer = order[freeLayerIndex][0];
        boolean preOrdered = !isFirstSweep || isExternalPortDummy(firstNodeInLayer);

        List<LNode> nodes = Lists.newArrayList(order[freeLayerIndex]);
        minimizeCrossings(nodes, preOrdered, false, forwardSweep);
        // apply the new ordering
        int index = 0;
        for (LNode nodeGroup : nodes) {
                order[freeLayerIndex][index++] = nodeGroup;
        }

        return false; // Does not always improve.
    }

    @Override
    public boolean setFirstLayerOrder(final LNode[][] order, final boolean isForwardSweep) {
        int startIndex = startIndex(isForwardSweep, order.length);
        List<LNode> nodes = Lists.newArrayList(
                order[startIndex]);
        minimizeCrossings(nodes, false, true, isForwardSweep);
        int index = 0;
        for (LNode nodeGroup : nodes) {
                order[startIndex][index++] = nodeGroup;
        }

        return false; // Does not always improve
    }

    private boolean isExternalPortDummy(final LNode firstNode) {
        return firstNode.getType() == NodeType.EXTERNAL_PORT;
    }

    private int changeIndex(final boolean dir) {
        return dir ? 1 : -1;
    }

    private PortType portTypeFor(final boolean direction) {
        return direction ? PortType.OUTPUT : PortType.INPUT;
    }

    private int startIndex(final boolean dir, final int length) {
        return dir ? 0 : Math.max(0, length - 1);
    }

    private boolean isFirstLayer(final LNode[][] nodeOrder, final int currentIndex,
            final boolean forwardSweep) {
        return currentIndex == startIndex(forwardSweep, nodeOrder.length);
    }

    @Override
    public boolean alwaysImproves() {
        return false;
    }

    @Override
    public boolean isDeterministic() {
        return false;
    }

    @Override
    public void initAfterTraversal() {
        barycenterState = constraintResolver.getBarycenterStates();
        portRanks = portDistributor.getPortRanks();
    }

    @Override
    public void initAtLayerLevel(final int l, final LNode[][] nodeOrder) {
        nodeOrder[l][0].getLayer().id = l;
    }
    
    /**
     * Compare two nodes based of the already collected transitive dependencies.
     * 
     * @param n1 The first node
     * @param n2 The second node
     * @return Returns -1 if n1 < n2, 0 if no ordering could be found, and 1 if n1 > n2
     */
    private int compareBasedOnTansitiveDependencies(final LNode n1, final LNode n2) {
        if (!biggerThan.containsKey(n1)) {
            biggerThan.put(n1, new HashSet<>());
        } else if (biggerThan.get(n1).contains(n2)) {
            return 1;
        }
        if (!biggerThan.containsKey(n2)) {
            biggerThan.put(n2, new HashSet<>());
        } else if (biggerThan.get(n2).contains(n1)) {
            return -1;
        }
        if (!smallerThan.containsKey(n1)) {
            smallerThan.put(n1, new HashSet<>());
        } else if (smallerThan.get(n1).contains(n2)) {
            return -1;
        }
        if (!smallerThan.containsKey(n2)) {
            smallerThan.put(n2, new HashSet<>());
        } else if (smallerThan.get(n2).contains(n1)) {
            return 1;
        }
        return 0;
    }
    
    /**
     * Order nodes by their barycenter and update their transitive dependencies.
     * @param n1 The first node
     * @param n2 The second node
     * @return -1, 0, 1, see {@link Comparator} for details.
     */
    private int compareBasedOnBarycenter(final LNode n1, final LNode n2) {
        BarycenterState s1 = stateOf(n1);
        BarycenterState s2 = stateOf(n2);
        if (s1.barycenter != null && s2.barycenter != null) {
            int value = s1.barycenter.compareTo(s2.barycenter);
            if (value < 0) {
                updateBiggerAndSmallerAssociations(n1, n2);
            } else if (value > 0) {
                updateBiggerAndSmallerAssociations(n2, n1);
            }
            return value;
        } else if (s1.barycenter != null) {
            updateBiggerAndSmallerAssociations(n1, n2);
            return -1;
        } else if (s2.barycenter != null) {
            updateBiggerAndSmallerAssociations(n2, n1);
            return 1;
        }
        return 0;
    }
    
    /**
     * Taken from {@code ModelOrderNodeComparator}. Safes transitive ordering dependencies of nodes for later use.
     * @param bigger
     * @param smaller
     */
    private void updateBiggerAndSmallerAssociations(final LNode bigger, final LNode smaller) {
        HashSet<LNode> biggerNodeBiggerThan = biggerThan.get(bigger);
        HashSet<LNode> smallerNodeBiggerThan = biggerThan.get(smaller);
        HashSet<LNode> biggerNodeSmallerThan = smallerThan.get(bigger);
        HashSet<LNode> smallerNodeSmallerThan = smallerThan.get(smaller);
        biggerNodeBiggerThan.add(smaller);
        smallerNodeSmallerThan.add(bigger);
        for (LNode verySmall : smallerNodeBiggerThan) {
            biggerNodeBiggerThan.add(verySmall);
            smallerThan.get(verySmall).add(bigger);
            smallerThan.get(verySmall).addAll(biggerNodeSmallerThan);
        }
        

        for (LNode veryBig : biggerNodeSmallerThan) {
            smallerNodeSmallerThan.add(veryBig);
            biggerThan.get(veryBig).add(smaller);
            biggerThan.get(veryBig).addAll(smallerNodeBiggerThan);
        }
    }
    
    /**
     * Sorts the layer by the given comparator with insertion sort to ensure that transitive ordering constraints are
     * respected. The usual quick sorting algorithm used by java does not do well here since the partial ordering
     * defined by the model order and the ordering of the barycenter values is prone to create a conflicting ordering.
     * Insertion sort can deal with that since no real node is moved past another real node if the model order forbids
     * it. However, the placement of dummy nodes and, therefore, long edges might suffer a little.
     * 
     * @param layer The layer to order. It is assumed that all "real" nodes which are here all nodes that have a model
     *      order are already ordered as intended.
     * @param comparator A comparator that uses a full ordering (barycenter values) and a partial ordering, which has
     *      a higher priority, (model order of real nodes) at the same time.
     * @param barycenterHeuristic The instance of this class to reset the transitive ordering associations.
     */
    public static void insertionSort(final List<LNode> layer, final Comparator<LNode> comparator,
            final ModelOrderBarycenterHeuristic barycenterHeuristic) {
        LNode temp;
        for (int i = 1; i < layer.size(); i++) {
            temp = layer.get(i);
            int j = i;
            while (j > 0 && comparator.compare(layer.get(j - 1), temp) > 0) {
                layer.set(j, layer.get(j - 1));
                j--;
            }
            layer.set(j, temp);
        }
        barycenterHeuristic.clearTransitiveOrdering();
    }
    
    /**
     * Clears the transitive ordering.
     */
    public void clearTransitiveOrdering() {
        this.biggerThan = new HashMap<>();
        this.smallerThan = new HashMap<>();
    }

}
