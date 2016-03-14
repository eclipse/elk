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
import org.eclipse.elk.alg.layered.p3order.counting.CrossingsCounter;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Lists;

/**
 * Distribute ports greedily on a single node.
 *
 * @author alan
 *
 */
public class GreedyPortDistributor {

    private CrossingsCounter crossingsCounter;

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
        crossingsCounter = CrossingsCounter.createAssumingPortOrderFixed(portPositions);
        crossingsCounter.initForCountingBetweenOnSide(leftLayer, rightLayer, sideToCountOn);
    }

    private boolean switchingDecreasesCrossings(final LPort upperPort, final LPort lowerPort, final LNode node) {
        int upperLowerCrossings = crossingsCounter.countCrossingsBetweenPorts(upperPort, lowerPort);
        int lowerUpperCrossings = crossingsCounter.countCrossingsBetweenPorts(lowerPort, upperPort);
        return upperLowerCrossings > lowerUpperCrossings;
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
        return reverseSublist(side, firstIndexForCurrentSide, currentIndex, ports);
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
}
