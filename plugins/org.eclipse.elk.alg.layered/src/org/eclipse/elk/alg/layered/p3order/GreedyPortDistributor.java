/*******************************************************************************
 * Copyright (c) 2016 2014, 2015 alan and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    alan - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.BetweenLayerEdgeTwoNodeCrossingsCounter;
import org.eclipse.elk.alg.layered.p3order.counting.AbstractInitializer;
import org.eclipse.elk.alg.layered.p3order.counting.CrossingsCounter;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.LayeredOptions;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.Pair;

import com.google.common.collect.Lists;

/**
 * Distribute ports greedily on a single node. TODO-alan make one-sided/two-sided
 * 
 * @author alan TODO-alan does not work in first layer.
 */
public class GreedyPortDistributor implements ISweepPortDistributor {

    private CrossingsCounter crossingsCounter;
    private int[] portPos;
    private BetweenLayerEdgeTwoNodeCrossingsCounter hierarchicalCrossingsCounter;
    private AbstractInitializer initializer;

    /** Return new GreedyPortDistributor. */
    public GreedyPortDistributor(final LNode[][] currentNodeOrder) {
        this.initializer = new Initializer(currentNodeOrder);
    }

    @Override
    public boolean distributePortsWhileSweeping(final LNode[][] nodeOrder, final int currentIndex,
            final boolean isForwardSweep) {
        if (isForwardSweep && currentIndex > 0) {
            initForLayers(nodeOrder[currentIndex - 1], nodeOrder[currentIndex], portPos);
        } else if (!isForwardSweep && currentIndex < nodeOrder.length - 1) {
            initForLayers(nodeOrder[currentIndex], nodeOrder[currentIndex + 1], portPos);
        } else {
            crossingsCounter = new CrossingsCounter(portPos);
            crossingsCounter.initPortPositionsForInLayerCrossings(nodeOrder[currentIndex],
                    isForwardSweep ? PortSide.WEST : PortSide.EAST);
        }
        PortSide side = isForwardSweep ? PortSide.WEST : PortSide.EAST;
        boolean improved = false;
        for (LNode node : nodeOrder[currentIndex]) {
            List<LPort> ports = node.getPorts(side);
            boolean useHierarchicalCrossCounter =
                    !ports.isEmpty() && node.getProperty(InternalProperties.COMPOUND_NODE);
            if (useHierarchicalCrossCounter) {
                LNode[][] innerGraph = node.getProperty(InternalProperties.NESTED_LGRAPH).toNodeArray();
                hierarchicalCrossingsCounter =
                        new BetweenLayerEdgeTwoNodeCrossingsCounter(innerGraph,
                        isForwardSweep ? 0 : innerGraph.length - 1);
            }
            improved |= distributePorts(node, side, useHierarchicalCrossCounter);
        }
        return improved;
    }

    /**
     * Distribute ports greedily on a single node.
     */
    private boolean distributePorts(final LNode node, final PortSide side, final boolean useHierarchicalCrosscounter) {
        if (node.getProperty(LayeredOptions.PORT_CONSTRAINTS).isOrderFixed()) {
            return false;
        }
        List<LPort> ports = node.getPorts(side);
        if (side == PortSide.SOUTH || side == PortSide.WEST) {
            ports = Lists.reverse(ports);
        }
        boolean improved = false;
        boolean continueSwitching;
        do {
            continueSwitching = false;
            for (int i = 0; i < ports.size() - 1; i++) {
                LPort upperPort = ports.get(i);
                LPort lowerPort = ports.get(i + 1);
                if (switchingDecreasesCrossings(upperPort, lowerPort, node, useHierarchicalCrosscounter)) {
                    improved = true;
                    switchPorts(ports, node, i, i + 1);
                    continueSwitching = true;
                }
            }
        } while (continueSwitching);
        return improved;
    }

    /**
     * Initialize crossings counter for given layers and side.
     */
    private void initForLayers(final LNode[] leftLayer, final LNode[] rightLayer, final int[] portPositions) {
        crossingsCounter = new CrossingsCounter(portPositions);
        crossingsCounter.initForCountingBetweenOnSide(leftLayer, rightLayer);
    }

    private boolean switchingDecreasesCrossings(final LPort upperPort, final LPort lowerPort, final LNode node,
            final boolean useHierarchicalCrosscounter) {
        Pair<Integer, Integer> originalNSwitchedCrossings =
                crossingsCounter.countCrossingsBetweenPortsInBothOrders(upperPort, lowerPort);
        int upperLowerCrossings = originalNSwitchedCrossings.getFirst();
        int lowerUpperCrossings = originalNSwitchedCrossings.getSecond();
        if (useHierarchicalCrosscounter) {
            LNode upperNode = upperPort.getProperty(InternalProperties.PORT_DUMMY);
            LNode lowerNode = lowerPort.getProperty(InternalProperties.PORT_DUMMY);
            hierarchicalCrossingsCounter.countBothSideCrossings(upperNode, lowerNode);
            upperLowerCrossings += hierarchicalCrossingsCounter.getUpperLowerCrossings();
            lowerUpperCrossings += hierarchicalCrossingsCounter.getLowerUpperCrossings();
        }
        return upperLowerCrossings > lowerUpperCrossings;
    }

    private void switchPorts(final List<LPort> ports, final LNode node, final int topPort, final int bottomPort) {
        crossingsCounter.switchPorts(ports.get(topPort), ports.get(bottomPort));
        LPort lower = ports.get(bottomPort);
        ports.set(bottomPort, ports.get(topPort));
        ports.set(topPort, lower);
    }

    @Override
    public AbstractInitializer initializer() {
        return initializer;
    }

    /** Defines what needs to be initialized traversing the graph. */
    private final class Initializer extends AbstractInitializer {
        private int nPorts;

        private Initializer(final LNode[][] graph) {
            super(graph);
            nPorts = 0;
        }

        @Override
        public void initAtNodeLevel(final int l, final int n) {
            LNode node = nodeOrder()[l][n];
            nPorts += node.getPorts().size();
        }

        public void initAfterTraversal() {
            portPos = new int[nPorts];
        }
    }

}
