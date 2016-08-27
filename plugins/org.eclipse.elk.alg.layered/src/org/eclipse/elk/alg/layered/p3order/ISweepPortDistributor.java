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
package org.eclipse.elk.alg.layered.p3order;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.p3order.counting.AbstractInitializer;
import org.eclipse.elk.alg.layered.p3order.counting.AbstractInitializer.IInitializable;

/**
 * PortDistributor to be used while sweeping in phase 3.
 * <p>
 * Must be initialized using {@link AbstractInitializer#init(java.util.List)}!
 * </p>
 * @author alan
 *
 */
public interface ISweepPortDistributor extends IInitializable {

    /**
     * Distribute ports in one layer. To be used in the context of layer sweep.
     *
     * @param order
     *            the current order of the nodes
     * @param freeLayerIndex
     *            the index of the layer the node is in
     * @param isForwardSweep
     *            whether we are sweeping forward or not.
     */
    void distributePortsWhileSweeping(LNode[][] order, int freeLayerIndex,
            boolean isForwardSweep);

}
