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
package org.eclipse.elk.alg.layered.p3order.counting;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.p3order.counting.AbstractInitializer.IInitializable;
import org.eclipse.elk.core.options.PortSide;

/**
 * Counts all crossings in a graph. Must be initialized using {@link AbstractInitializer#init(java.util.List)}!
 *
 * @author alan
 */
public final class AllCrossingsCounter implements IInitializable {

    private CrossingsCounter crossingCounter;
    private NorthSouthEdgeAllCrossingsCounter northSouthEdgeCrossingCounter;
    private boolean[] hasHyperEdgesEastOfIndex;
    private HyperedgeCrossingsCounter hyperedgeCrossingsCounter;
    private AbstractInitializer initializer;

    /** Returns crossings counter. Must be initialized using {@link AbstractInitializer#init(java.util.List)}! */
    public AllCrossingsCounter(final LNode[][] graph) {
        initializer = new Initializer(graph);
    }

    /**
     * Count all crossings.
     * 
     * @param currentOrder
     * @return
     */
    public int countAllCrossings(final LNode[][] currentOrder) {
        if (currentOrder.length == 0) {
            return 0;
        }
        int crossings = crossingCounter.countInLayerCrossingsOnSide(currentOrder[0], PortSide.WEST);
        crossings += crossingCounter.countInLayerCrossingsOnSide(currentOrder[currentOrder.length - 1], PortSide.EAST);
        for (int layerIndex = 0; layerIndex < currentOrder.length; layerIndex++) {
            crossings += countCrossingsAt(layerIndex, currentOrder);
        }
        return crossings;
    }

    private int countCrossingsAt(final int layerIndex, final LNode[][] currentOrder) {
        int totalCrossings = 0;
        LNode[] leftLayer = currentOrder[layerIndex];
        if (layerIndex < currentOrder.length - 1) {
            LNode[] rightLayer = currentOrder[layerIndex + 1];
            if (hasHyperEdgesEastOfIndex[layerIndex]) {
                totalCrossings = hyperedgeCrossingsCounter.countCrossings(leftLayer, rightLayer);
            } else {
                totalCrossings = crossingCounter.countCrossingsBetweenLayers(leftLayer, rightLayer);
            }
        }

        totalCrossings += northSouthEdgeCrossingCounter.countCrossings(leftLayer);
        return totalCrossings;
    }

    /** Defines what needs to be initialized traversing the graph. */
    private final class Initializer extends AbstractInitializer {
        private int[] inLayerEdgeCounts;
        private boolean[] hasNorthSouthPorts;
        private int nPorts;

        private Initializer(final LNode[][] graph) {
            super(graph);
            inLayerEdgeCounts = new int[graph.length];
            hasNorthSouthPorts = new boolean[graph.length];
            hasHyperEdgesEastOfIndex = new boolean[graph.length];
            nPorts = 0;
        }

        @Override
        public void initAtNodeLevel(final int l, final int n) {
            LNode node = getNodeOrder()[l][n];
            hasNorthSouthPorts[l] |= node.getType() == NodeType.NORTH_SOUTH_PORT;
        }

        @Override
        public void initAtPortLevel(final int l, final int n, final int p) {
            LPort port = port(l, n, p);
            port.id = nPorts++;
            if (port.getOutgoingEdges().size() + port.getIncomingEdges().size() > 1) {
                if (port.getSide() == PortSide.EAST) {
                    hasHyperEdgesEastOfIndex[l] = true;
                } else if (port.getSide() == PortSide.WEST && l > 0) {
                    hasHyperEdgesEastOfIndex[l - 1] = true;
                }
            }
        }

        @Override
        public void initAtEdgeLevel(final int l, final int n, final int p, final int e) { 
            LEdge edge = edge(l, n, p, e);
            LPort port = port(l, n, p);
            if (edge.getSource() == port
                    && edge.getSource().getNode().getLayer() == edge.getTarget().getNode().getLayer()) {
                inLayerEdgeCounts[l]++;
            }
        } 
        
        @Override
        public void initAfterTraversal() {
            int[] portPos = new int[nPorts];
            hyperedgeCrossingsCounter = new HyperedgeCrossingsCounter(inLayerEdgeCounts, hasNorthSouthPorts, portPos);
            northSouthEdgeCrossingCounter = new NorthSouthEdgeAllCrossingsCounter(portPos);
            crossingCounter = new CrossingsCounter(portPos);
        }
    }

    @Override
    public AbstractInitializer initializer() {
        return initializer;
    }
}
