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
package org.eclipse.elk.layered.p3order;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.eclipse.elk.layered.graph.LNode;
import org.eclipse.elk.layered.graph.LPort;
import org.eclipse.elk.layered.properties.InternalProperties;

/**
 * Determines the node order of a given free layer. Uses heuristic methods to find an ordering that
 * minimizes edge crossings between the given free layer and a neighboring layer with fixed node
 * order. The barycenter heuristic is used here.
 * 
 * @author msp
 * @author cds
 * @author ima
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public final class BarycenterHeuristic implements ICrossingMinimizationHeuristic {

    /** the array of port ranks. */
    private final float[] portRanks;
    /** the random number generator. */
    private final Random random;
    /** the constraint resolver for ordering constraints. */
    private final IConstraintResolver constraintResolver;

    /**
     * Constructs a Barycenter heuristic for crossing minimization between two layers.
     * 
     * @param constraintResolver
     *            the constraint resolver
     * @param graphRandom
     *            the random number generator
     * @param portRanks
     *            the array of port ranks
     */
    public BarycenterHeuristic(final IConstraintResolver constraintResolver,
            final Random graphRandom, final float[] portRanks) {
        this.constraintResolver = constraintResolver;
        random = graphRandom;
        this.portRanks = portRanks;
    }

    /**
     * {@inheritDoc}
     */
    public void minimizeCrossings(final List<NodeGroup> layer, final boolean preOrdered,
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
            Collections.sort(layer);

            // Resolve ordering constraints
            constraintResolver.processConstraints(layer);
        }
    }

    /**
     * Randomize the order of nodes for the given layer.
     * 
     * @param nodeGroups
     *            a layer
     */
    private void randomizeBarycenters(final List<NodeGroup> nodeGroups) {
        for (NodeGroup nodeGroup : nodeGroups) {
            // Set barycenters only for nodeGroups containing a single node.
            if (nodeGroup.getNodes().length == 1) {
                nodeGroup.barycenter = random.nextDouble();
                nodeGroup.summedWeight = nodeGroup.barycenter;
                nodeGroup.degree = 1;
            }
        }
    }

    /**
     * Try to find appropriate barycenter values for node groups whose barycenter is undefined.
     * 
     * @param nodeGroups
     *            the nodeGroups to fill in barycenters for.
     * @param preOrdered
     *            whether the nodeGroups have been ordered in a previous run.
     */
    private void fillInUnknownBarycenters(final List<NodeGroup> nodeGroups, final boolean preOrdered) {
        // Determine placements for nodes with undefined barycenter value
        if (preOrdered) {
            double lastValue = -1;
            ListIterator<NodeGroup> nodeGroupIterator = nodeGroups.listIterator();

            while (nodeGroupIterator.hasNext()) {
                NodeGroup nodeGroup = nodeGroupIterator.next();
                Double value = nodeGroup.barycenter;

                if (value == null) {
                    // The barycenter is undefined - take the center of the previous value
                    // and the next defined value in the list
                    double nextValue = lastValue + 1;

                    ListIterator<NodeGroup> nextNodeGroupIterator =
                            nodeGroups.listIterator(nodeGroupIterator.nextIndex());
                    while (nextNodeGroupIterator.hasNext()) {
                        Double x = nextNodeGroupIterator.next().barycenter;
                        if (x != null) {
                            nextValue = x;
                            break;
                        }
                    }

                    value = (lastValue + nextValue) / 2;
                    nodeGroup.barycenter = value;
                    nodeGroup.summedWeight = value;
                    nodeGroup.degree = 1;
                }

                lastValue = value;
            }
        } else {
            // No previous ordering - determine random placement for new nodes
            double maxBary = 0;
            for (NodeGroup nodeGroup : nodeGroups) {
                if (nodeGroup.barycenter != null) {
                    maxBary = Math.max(maxBary, nodeGroup.barycenter);
                }
            }

            maxBary += 2;
            for (NodeGroup nodeGroup : nodeGroups) {
                if (nodeGroup.barycenter == null) {
                    double value = random.nextFloat() * maxBary - 1;
                    nodeGroup.barycenter = value;
                    nodeGroup.summedWeight = value;
                    nodeGroup.degree = 1;
                }
            }
        }
    }

    /**
     * Calculate the barycenters of the given node groups.
     * 
     * @param nodeGroups
     *            the nodeGroups
     * @param forward
     *            {@code true} if the current sweep moves forward
     */
    private void calculateBarycenters(final List<NodeGroup> nodeGroups, final boolean forward) {
        // Set all visited flags to false
        for (NodeGroup nodeGroup : nodeGroups) {
            nodeGroup.visited = false;
        }

        for (NodeGroup nodeGroup : nodeGroups) {
            if (nodeGroup.getNodes().length == 1) {
                // Calculate the node groups's new barycenter (may be null)
                calculateBarycenter(nodeGroup, forward);
            }
        }
    }

    /** the amount of random value to add to each calculated barycenter. */
    private static final float RANDOM_AMOUNT = 0.07f;

    /**
     * Calculate the barycenter of the given single-node node group. This method is able to handle
     * in-layer edges, but it may give incorrect results if the in-layer edges form a cycle.
     * However, such cases do not occur in the present implementation.
     * 
     * @param nodeGroup
     *            a node group consisting of a single node
     * @param forward
     *            {@code true} if the current sweep moves forward
     * @param portPos
     *            position array
     * @return a pair containing the summed port positions of the connected ports as the first, and
     *         the number of connected edges as the second entry.
     */
    private void calculateBarycenter(final NodeGroup nodeGroup, final boolean forward) {

        // Check if the node group's barycenter was already computed
        if (nodeGroup.visited) {
            return;
        } else {
            nodeGroup.visited = true;
        }

        nodeGroup.degree = 0;
        nodeGroup.summedWeight = 0.0f;
        nodeGroup.barycenter = null;
        LNode node = nodeGroup.getNode();

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
                        NodeGroup fixedNodeGroup =
                                fixedNode.getProperty(InternalProperties.NODE_GROUP);
                        calculateBarycenter(fixedNodeGroup, forward);

                        // Update this node group's values
                        nodeGroup.degree += fixedNodeGroup.degree;
                        nodeGroup.summedWeight += fixedNodeGroup.summedWeight;
                    }
                } else {
                    nodeGroup.summedWeight += portRanks[fixedPort.id];
                    nodeGroup.degree++;
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
                    NodeGroup associateNodeGroup =
                            associate.getProperty(InternalProperties.NODE_GROUP);
                    calculateBarycenter(associateNodeGroup, forward);

                    // Update this vertex's values
                    nodeGroup.degree += associateNodeGroup.degree;
                    nodeGroup.summedWeight += associateNodeGroup.summedWeight;
                }
            }
        }

        if (nodeGroup.degree > 0) {
            // add a small random perturbation in order to increase diversity of solutions
            nodeGroup.summedWeight += random.nextFloat() * RANDOM_AMOUNT - RANDOM_AMOUNT / 2;
            nodeGroup.barycenter = nodeGroup.summedWeight / nodeGroup.degree;
        }
    }

}
