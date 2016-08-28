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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.intermediate.greedyswitch.BetweenLayerEdgeTwoNodeCrossingsCounter;
import org.eclipse.elk.alg.layered.p3order.counting.AbstractInitializer;
import org.eclipse.elk.alg.layered.p3order.counting.CrossingsCounter;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.core.options.PortSide;
import org.eclipse.elk.core.util.Pair;

import com.google.common.collect.Lists;

/**
 * Distribute ports greedily on a single node. TODO-alan this is currently broken :-(
 * 
 * @author alan TODO-alan does not work in first layer.
 */
public class GreedyPortDistributor implements ISweepPortDistributor {

    private CrossingsCounter crossingsCounter;
    private final int[] portPos;
    private final Map<Integer, Integer> childNumPorts;

    /** Return new GreedyPortDistributor. */
    public GreedyPortDistributor(final int[] portPos, final Map<Integer, Integer> childNumPorts) {
        this.portPos = portPos;
        this.childNumPorts = childNumPorts;
    }

    /**
     * Distribute ports greedily on a single node.
     */
    public void distributePorts(final LNode node, final PortSide side) {
        List<LPort> ports = portListViewInNorthSouthEastWestOrder(node, side);
        boolean continueSwitching;
        do {
            continueSwitching = false;
            for (int i = 0; i < ports.size() - 1; i++) {
                LPort upperPort = ports.get(i);
                LPort lowerPort = ports.get(i + 1);
                if (switchingDecreasesCrossings(upperPort, lowerPort, node)) {
                    switchPorts(ports, node, i, i + 1);
                    continueSwitching = true;
                }
            }
        } while (continueSwitching);
    }

    @Override
    public void distributePortsWhileSweeping(final LNode[][] nodeOrder, final int currentIndex,
            final boolean isForwardSweep) {
        int leftIndex = isForwardSweep ? currentIndex - 1 : currentIndex;
        int rightIndex = isForwardSweep ? currentIndex : currentIndex + 1;
        PortSide side = isForwardSweep ? PortSide.WEST : PortSide.EAST;
        initForLayers(nodeOrder[leftIndex], nodeOrder[rightIndex], side, portPos);
        for (LNode node : nodeOrder[currentIndex]) {
            distributePorts(node, side);
        }
    }

    /**
     * Initialize crossings counter for given layers and side.
     *
     * @param leftLayer
     *            The western layer.
     * @param rightLayer
     *            The eastern layer.
     * @param sideToCountOn
     *            The side to count the crossings on.
     * @param portPositions
     *            The positions of the ports.
     */
    public void initForLayers(final LNode[] leftLayer, final LNode[] rightLayer, final PortSide sideToCountOn,
            final int[] portPositions) {
        crossingsCounter = new CrossingsCounter(portPositions);
        crossingsCounter.initForCountingBetweenOnSide(leftLayer, rightLayer, sideToCountOn);
    }

    private boolean switchingDecreasesCrossings(final LPort upperPort, final LPort lowerPort, final LNode node) {
        Pair<Integer, Integer> originalNSwitchedCrossings =
                crossingsCounter.countCrossingsBetweenPortsInBothOrders(upperPort, lowerPort);
        int upperLowerCrossings = originalNSwitchedCrossings.getFirst();
        int lowerUpperCrossings = originalNSwitchedCrossings.getSecond();
        if (isHierarchical(upperPort) && isHierarchical(lowerPort)) {
            LNode[][] innerGraph = node.getProperty(InternalProperties.NESTED_LGRAPH).toNodeArray();
            // TODO-alan cache?
            BetweenLayerEdgeTwoNodeCrossingsCounter counter =
                    BetweenLayerEdgeTwoNodeCrossingsCounter.createAssumingPortOrderFixed(innerGraph,
                            upperPort.getSide() == PortSide.EAST ? innerGraph.length - 1 : 0);
            LNode upperNode = upperPort.getProperty(InternalProperties.PORT_DUMMY);
            LNode lowerNode = lowerPort.getProperty(InternalProperties.PORT_DUMMY);
            counter.countBothSideCrossings(upperNode, lowerNode);
            upperLowerCrossings += counter.getUpperLowerCrossings();
            lowerUpperCrossings += counter.getLowerUpperCrossings();
        }
        return upperLowerCrossings > lowerUpperCrossings;
    }

    private boolean isHierarchical(final LPort port) {
        return port.getProperty(InternalProperties.INSIDE_CONNECTIONS);
    }

    private void switchPorts(final List<LPort> ports, final LNode node, final int topPort, final int bottomPort) {
        LPort lower = ports.get(bottomPort);
        ports.set(bottomPort, ports.get(topPort));
        ports.set(topPort, lower);
        crossingsCounter.switchPorts(ports.get(topPort), ports.get(bottomPort));
    }

    // TODO-alan potentially slow and is cached
    private List<LPort> portListViewInNorthSouthEastWestOrder(final LNode node, final PortSide side) {
        int firstIndexForCurrentSide = 0;
        PortSide currentSide = PortSide.NORTH;
        int currentIndex = 0;
        List<LPort> ports = node.getPorts();
        for (; currentIndex < ports.size(); currentIndex++) {
            LPort port = ports.get(currentIndex);
            if (port.getSide() != currentSide) {
                if (firstIndexForCurrentSide != currentIndex && currentSide == side) {
                    return reverseSublist(side, firstIndexForCurrentSide, currentIndex, ports);
                }
                currentSide = port.getSide();
                firstIndexForCurrentSide = currentIndex;
            }
        }
        if (currentSide == side) {
            return reverseSublist(side, firstIndexForCurrentSide, currentIndex, ports);
        } else {
            return Collections.emptyList();
        }
    }

    private List<LPort> reverseSublist(final PortSide side, final int firstIndexForCurrentSide, final int currentIndex,
            final List<LPort> ports) {
        List<LPort> ps = ports.subList(firstIndexForCurrentSide, currentIndex);
        if (side == PortSide.SOUTH || side == PortSide.WEST) {
            return Lists.reverse(ps);
        } else {
            return ps;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.elk.alg.layered.p3order.counting.AbstractInitializer.IInitializable#initializer()
     */
    @Override
    public AbstractInitializer initializer() {
        // TODO-alan Auto-generated method stub
        return null;
    }

}
