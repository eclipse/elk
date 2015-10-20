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
package org.eclipse.elk.layered.intermediate.greedyswitch;

import org.eclipse.elk.layered.graph.LNode;
import org.eclipse.elk.layered.graph.LPort;

/**
 * Abstract super class for counting all between-layer edge crossings. Subclasses must implement
 * {@link #countCrossings(LNode[], LNode[])} which counts crossings between two layers. They can use
 * the {@code port.id} fields and the {@code portPos} array (accessible through {@link #getPortPos()}.
 * 
 * @author alan
 */
public abstract class BetweenLayerEdgeAllCrossingsCounter {

    /**
     * Port position array used for counting the number of edge crossings.
     */
    private int[] portPos;

    /**
     * @param graph
     *            a graph
     */
    public BetweenLayerEdgeAllCrossingsCounter(final LNode[][] graph) {
        initialize(graph);
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

    private void initialize(final LNode[][] graph) {
        int portCount = 0;
        for (LNode[] layer : graph) {
            for (LNode node : layer) {
                for (LPort port : node.getPorts()) {
                    port.id = portCount++;
                }
            }
        }

        // Initialize the port positions and ranks arrays
        portPos = new int[portCount];
    }

    /**
     * @return the portPos
     */
    protected final int[] getPortPos() {
        return portPos;
    }
}
