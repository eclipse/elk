/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LPort;

/**
 * Stores node and port order for a sweep.
 * 
 * @author alan
 */
class SweepCopy {
    /** Do not change after setting! */
    private final LNode[][] nodeOrder;
    private final List<List<List<LPort>>> portOrders;
    private boolean lastSweepDirection;

    /**
     * Copies on construction.
     * 
     * @param nodeOrderIn
     */
    SweepCopy(final LNode[][] nodeOrderIn) {
        nodeOrder = deepCopy(nodeOrderIn);
        portOrders = new ArrayList<>();
        for (LNode[] lNodes : nodeOrderIn) {
            List<List<LPort>> layer = new ArrayList<>();
            portOrders.add(layer);
            for (LNode node : lNodes) {
                layer.add(new ArrayList<>(node.getPorts()));
            }
        }
    }

    SweepCopy(final SweepCopy sc) {
        nodeOrder = deepCopy(sc.nodeOrder);
        lastSweepDirection = sc.lastSweepDirection;
        portOrders = new ArrayList<>(sc.portOrders);
    }

    public void setSavedPortOrdersToNodes() {
        for (int i = 0; i < nodeOrder.length; i++) {
            LNode[] lNodes = nodeOrder[i];
            for (int j = 0; j < lNodes.length; j++) {
                LNode node = lNodes[j];
                node.getPorts().clear();
                node.getPorts().addAll(portOrders.get(i).get(j));
            }
        }
    }

    private LNode[][] deepCopy(final LNode[][] currentlyBestNodeOrder) {
        if (currentlyBestNodeOrder == null) {
            return null;
        }
        final LNode[][] result = new LNode[currentlyBestNodeOrder.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] =
                    Arrays.copyOf(currentlyBestNodeOrder[i], currentlyBestNodeOrder[i].length);
        }
        return result;
    }

    /**
     * @return the nodeOrder
     */
    public LNode[][] nodes() {
        return nodeOrder;
    }
}