/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.position;

import org.eclipse.elk.alg.layered.graph.LNode;

/**
 * The interface for all self loop port positioners. Each is supposed to adjust the ports of a node according to the
 * port constraints.
 */
public interface ISelfLoopPortPositioner {

    /**
     * Positions the ports of the given node.
     * 
     * @param node
     *            the node whose ports are to be positioned.
     */
    void position(LNode node);

}
