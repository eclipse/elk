package org.eclipse.elk.alg.layered.p3order;

import org.eclipse.elk.alg.layered.graph.LNode;

public interface SweepPortDistributor {

    /**
     * Distribute ports in one layer. To be used in the context of layer sweep.
     * 
     * @param nodeOrder
     *            the current order of the nodes
     * @param currentIndex
     *            the index of the layer the node is in
     * @param isForwardSweep
     *            whether we are sweeping forward or not.
     */
    void distributePortsWhileSweeping(LNode[][] nodeOrder, int currentIndex,
            boolean isForwardSweep);

}
