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
package org.eclipse.elk.alg.layered.intermediate.greedyswitch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.options.PortSide;

/**
 * Crossings counter implementation specialized for hyperedges. It also works for normal edges, but
 * is considerably slower compared to other implementations. For normal edges the computed number of
 * crossings is exact, while for hyperedges it is an estimation. In fact, it is impossible to
 * reliably count the number of crossings at this stage of the layer-based approach. See the
 * following publication for details.
 * <ul>
 * <li>M. Sp&ouml;nemann, C. D. Schulze, U. R&uuml;egg, R. von Hanxleden. Counting crossings for
 * layered hypergraphs, In <i>DIAGRAMS 2014</i>, volume 8578 of LNAI, Springer, 2014.</li>
 * </ul>
 * 
 * @author msp
 */
public class BetweenLayerHyperedgeAllCrossingsCounter extends
        BetweenLayerEdgeAllCrossingsCounter {

    /**
     * Create a hyperedge crossings counter.
     * 
     * @param graph
     *            The current order of the nodes.
     */
    public BetweenLayerHyperedgeAllCrossingsCounter(final LNode[][] graph) {
        super(graph);
    }

    /**
     * Hyperedge representation.
     */
    private static class Hyperedge implements Comparable<Hyperedge> {
        private final List<LEdge> edges = new LinkedList<LEdge>();
        private final List<LPort> ports = new LinkedList<LPort>();
        private int upperLeft;
        private int lowerLeft;
        private int upperRight;
        private int lowerRight;
        private int hashCode;

        /**
         * {@inheritDoc}
         */
        public int compareTo(final Hyperedge other) {
            if (upperLeft < other.upperLeft) {
                return -1;
            } else if (upperLeft > other.upperLeft) {
                return 1;
            } else if (upperRight < other.upperRight) {
                return -1;
            } else if (upperRight > other.upperRight) {
                return 1;
            }
            return hashCode - other.hashCode;
        }
    }

    /**
     * The upper left, lower left, upper right, or lower right corner of a hyperedge.
     */
    private static class HyperedgeCorner implements Comparable<HyperedgeCorner> {
        private final Hyperedge hyperedge;
        private final int position;
        private final int oppositePosition;
        private final Type type;

        HyperedgeCorner(final Hyperedge hyperedge, final int position, final int oppositePosition,
                final Type type) {
            this.hyperedge = hyperedge;
            this.position = position;
            this.oppositePosition = oppositePosition;
            this.type = type;
        }

        /** The corner type. */
        private static enum Type {
            UPPER, LOWER;
        }

        /**
         * {@inheritDoc}
         */
        public int compareTo(final HyperedgeCorner other) {
            if (position < other.position) {
                return -1;
            } else if (position > other.position) {
                return 1;
            } else if (oppositePosition < other.oppositePosition) {
                return -1;
            } else if (oppositePosition > other.oppositePosition) {
                return 1;
            } else if (hyperedge != other.hyperedge) {
                return hyperedge.hashCode - other.hyperedge.hashCode;
            } else if (type == Type.UPPER && other.type == Type.LOWER) {
                return -1;
            } else if (type == Type.LOWER && other.type == Type.UPPER) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * Special crossings counting method for hyperedges. See
     * <ul>
     * <li>M. Sp&ouml;nemann, C. D. Schulze, U. R&uuml;egg, R. von Hanxleden. Counting crossings for
     * layered hypergraphs, In <i>DIAGRAMS 2014</i>, volume 8578 of LNAI, Springer, 2014.</li>
     * </ul>
     * 
     * @param leftLayer
     *            the left layer
     * @param rightLayer
     *            the right layer
     * @return the number of edge crossings
     */
    @Override
    public int countCrossings(final LNode[] leftLayer, final LNode[] rightLayer) {
        // Assign index values to the ports of the left layer
        int sourceCount = 0;
        for (LNode node : leftLayer) {
            if (node.getProperty(LayoutOptions.PORT_CONSTRAINTS).isOrderFixed()) {
                // Assign index values in the order north - east - south - west
                for (LPort port : node.getPorts()) {
                    int portEdges = 0;
                    for (LEdge edge : port.getOutgoingEdges()) {
                        if (node.getLayer() != edge.getTarget().getNode().getLayer()) {
                            portEdges++;
                        }
                    }
                    if (portEdges > 0) {
                        getPortPos()[port.id] = sourceCount++;
                    }
                }

            } else {
                // All ports are assigned the same index value, since their order does not matter
                int nodeEdges = 0;
                for (LPort port : node.getPorts()) {
                    for (LEdge edge : port.getOutgoingEdges()) {
                        if (node.getLayer() != edge.getTarget().getNode().getLayer()) {
                            nodeEdges++;
                        }
                    }
                    getPortPos()[port.id] = sourceCount;
                }
                if (nodeEdges > 0) {
                    sourceCount++;
                }
            }
        }

        // Assign index values to the ports of the right layer
        int targetCount = 0;
        for (LNode node : rightLayer) {
            if (node.getProperty(LayoutOptions.PORT_CONSTRAINTS).isOrderFixed()) {
                // Determine how many input ports there are on the north side
                // (note that the standard port order is north - east - south - west)
                int northInputPorts = 0;
                for (LPort port : node.getPorts()) {
                    if (port.getSide() == PortSide.NORTH) {
                        for (LEdge edge : port.getIncomingEdges()) {
                            if (node.getLayer() != edge.getSource().getNode().getLayer()) {
                                northInputPorts++;
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
                // Assign index values in the order north - west - south - east
                int otherInputPorts = 0;
                ListIterator<LPort> portIter = node.getPorts().listIterator(node.getPorts().size());
                while (portIter.hasPrevious()) {
                    LPort port = portIter.previous();
                    int portEdges = 0;
                    for (LEdge edge : port.getIncomingEdges()) {
                        if (node.getLayer() != edge.getSource().getNode().getLayer()) {
                            portEdges++;
                        }
                    }
                    if (portEdges > 0) {
                        if (port.getSide() == PortSide.NORTH) {
                            getPortPos()[port.id] = targetCount;
                            targetCount++;
                        } else {
                            getPortPos()[port.id] = targetCount + northInputPorts + otherInputPorts;
                            otherInputPorts++;
                        }
                    }
                }
                targetCount += otherInputPorts;

            } else {
                // All ports are assigned the same index value, since their order does not matter
                int nodeEdges = 0;
                for (LPort port : node.getPorts()) {
                    for (LEdge edge : port.getIncomingEdges()) {
                        if (node.getLayer() != edge.getSource().getNode().getLayer()) {
                            nodeEdges++;
                        }
                    }
                    getPortPos()[port.id] = targetCount;
                }
                if (nodeEdges > 0) {
                    targetCount++;
                }
            }
        }

        // Gather hyperedges
        Map<LPort, Hyperedge> port2HyperedgeMap = new HashMap<LPort, Hyperedge>();
        Set<Hyperedge> hyperedgeSet = new HashSet<Hyperedge>();
        for (LNode node : leftLayer) {
            for (LPort sourcePort : node.getPorts()) {
                for (LEdge edge : sourcePort.getOutgoingEdges()) {
                    LPort targetPort = edge.getTarget();
                    if (node.getLayer() != targetPort.getNode().getLayer()) {
                        Hyperedge sourceHE = port2HyperedgeMap.get(sourcePort);
                        Hyperedge targetHE = port2HyperedgeMap.get(targetPort);
                        if (sourceHE == null && targetHE == null) {
                            Hyperedge hyperedge = new Hyperedge();
                            hyperedgeSet.add(hyperedge);
                            hyperedge.edges.add(edge);
                            hyperedge.ports.add(sourcePort);
                            port2HyperedgeMap.put(sourcePort, hyperedge);
                            hyperedge.ports.add(targetPort);
                            port2HyperedgeMap.put(targetPort, hyperedge);
                        } else if (sourceHE == null) {
                            targetHE.edges.add(edge);
                            targetHE.ports.add(sourcePort);
                            port2HyperedgeMap.put(sourcePort, targetHE);
                        } else if (targetHE == null) {
                            sourceHE.edges.add(edge);
                            sourceHE.ports.add(targetPort);
                            port2HyperedgeMap.put(targetPort, sourceHE);
                        } else if (sourceHE == targetHE) {
                            sourceHE.edges.add(edge);
                        } else {
                            sourceHE.edges.add(edge);
                            for (LPort p : targetHE.ports) {
                                port2HyperedgeMap.put(p, sourceHE);
                            }
                            sourceHE.edges.addAll(targetHE.edges);
                            sourceHE.ports.addAll(targetHE.ports);
                            hyperedgeSet.remove(targetHE);
                        }
                    }
                }
            }
        }

        // Determine top and bottom positions for each hyperedge
        Hyperedge[] hyperedges = hyperedgeSet.toArray(new Hyperedge[hyperedgeSet.size()]);
        Layer leftLayerRef = leftLayer[0].getLayer();
        Layer rightLayerRef = rightLayer[0].getLayer();
        for (Hyperedge he : hyperedges) {
            he.upperLeft = sourceCount;
            he.upperRight = targetCount;
            for (LPort port : he.ports) {
                int pos = getPortPos()[port.id];
                if (port.getNode().getLayer() == leftLayerRef) {
                    if (pos < he.upperLeft) {
                        he.upperLeft = pos;
                        he.hashCode = port.hashCode();
                    }
                    if (pos > he.lowerLeft) {
                        he.lowerLeft = pos;
                    }
                } else if (port.getNode().getLayer() == rightLayerRef) {
                    if (pos < he.upperRight) {
                        he.upperRight = pos;
                    }
                    if (pos > he.lowerRight) {
                        he.lowerRight = pos;
                    }
                }
            }
        }

        // Determine the sequence of edge target positions sorted by source and target index
        Arrays.sort(hyperedges);
        int[] southSequence = new int[hyperedges.length];
        int[] compressDeltas = new int[targetCount + 1];
        for (int i = 0; i < hyperedges.length; i++) {
            southSequence[i] = hyperedges[i].upperRight;
            compressDeltas[southSequence[i]] = 1;
        }
        int delta = 0;
        for (int i = 0; i < compressDeltas.length; i++) {
            if (compressDeltas[i] == 1) {
                compressDeltas[i] = delta;
            } else {
                delta--;
            }
        }
        int q = 0;
        for (int i = 0; i < southSequence.length; i++) {
            southSequence[i] += compressDeltas[southSequence[i]];
            q = Math.max(q, southSequence[i] + 1);
        }

        // Build the accumulator tree
        int firstIndex = 1;
        while (firstIndex < q) {
            firstIndex *= 2;
        }
        int treeSize = 2 * firstIndex - 1;
        firstIndex -= 1;
        int[] tree = new int[treeSize];

        // Count the straight-line crossings of the topmost edges
        int crossings = 0;
        for (int element : southSequence) {
            int index = element + firstIndex;
            tree[index]++;
            while (index > 0) {
                if (index % 2 > 0) {
                    crossings += tree[index + 1];
                }
                index = (index - 1) / 2;
                tree[index]++;
            }
        }

        // Create corners for the left side
        HyperedgeCorner[] leftCorners = new HyperedgeCorner[hyperedges.length * 2];
        for (int i = 0; i < hyperedges.length; i++) {
            leftCorners[2 * i] =
                    new HyperedgeCorner(hyperedges[i], hyperedges[i].upperLeft,
                            hyperedges[i].lowerLeft, HyperedgeCorner.Type.UPPER);
            leftCorners[2 * i + 1] =
                    new HyperedgeCorner(hyperedges[i], hyperedges[i].lowerLeft,
                            hyperedges[i].upperLeft, HyperedgeCorner.Type.LOWER);
        }
        Arrays.sort(leftCorners);

        // Count crossings caused by overlapping hyperedge areas on the left side
        int openHyperedges = 0;
        for (HyperedgeCorner leftCorner : leftCorners) {
            switch (leftCorner.type) {
            case UPPER:
                openHyperedges++;
                break;
            case LOWER:
                openHyperedges--;
                crossings += openHyperedges;
                break;
            }
        }

        // Create corners for the right side
        HyperedgeCorner[] rightCorners = new HyperedgeCorner[hyperedges.length * 2];
        for (int i = 0; i < hyperedges.length; i++) {
            rightCorners[2 * i] =
                    new HyperedgeCorner(hyperedges[i], hyperedges[i].upperRight,
                            hyperedges[i].lowerRight, HyperedgeCorner.Type.UPPER);
            rightCorners[2 * i + 1] =
                    new HyperedgeCorner(hyperedges[i], hyperedges[i].lowerRight,
                            hyperedges[i].upperRight, HyperedgeCorner.Type.LOWER);
        }
        Arrays.sort(rightCorners);

        // Count crossings caused by overlapping hyperedge areas on the right side
        openHyperedges = 0;
        for (HyperedgeCorner rightCorner : rightCorners) {
            switch (rightCorner.type) {
            case UPPER:
                openHyperedges++;
                break;
            case LOWER:
                openHyperedges--;
                crossings += openHyperedges;
                break;
            }
        }

        return crossings;
    }
}
