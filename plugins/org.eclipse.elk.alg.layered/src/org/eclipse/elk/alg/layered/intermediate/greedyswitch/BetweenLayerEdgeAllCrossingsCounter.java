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

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;

/**
 * Abstract super class for counting all between-layer edge crossings. Subclasses must implement
 * {@link #countCrossings(LNode[], LNode[])} which counts crossings between two layers. They can use
 * the {@code port.id} fields and the {@code portPos} array (accessible through
 * {@link #getPortPos()}. TODO-alan this class and it's children are only needed by the old
 * greedyswitchprocessor and hopefully will die some day.
 * 
 * @author alan
 */
public abstract class BetweenLayerEdgeAllCrossingsCounter {

    /**
     * Port position array used for counting the number of edge crossings.
     */
    private final int[] portPos;
    private final boolean assumeFixedPortOrder;

    /**
     * @param assumeFixedPortOrder
     *            whether or not to assume all port orders fixed.
     * @param numPorts
     *            number of ports in complete graph.
     */
    protected BetweenLayerEdgeAllCrossingsCounter(final boolean assumeFixedPortOrder,
            final int numPorts) {
        this.assumeFixedPortOrder = assumeFixedPortOrder;
        // Initialize the port positions and ranks arrays
        portPos = new int[numPorts];
    }

    /**
     * Calculate the number of crossings between the two given layers.
     *
     * @param leftLayer
     *            the left layer
     * @param rightLayer
     *            the right layer
     * @return the number of edge crossings
     */
    public abstract int countCrossings(LNode[] leftLayer, LNode[] rightLayer);

    /**
     * @return the portPos
     */
    protected final int[] getPortPos() {
        return portPos;
    }

    /**
     * @return whether or not to assume all port orders fixed.
     */
    public boolean isAssumeFixedPortOrder() {
        return assumeFixedPortOrder;
    }
}
