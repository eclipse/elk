/*******************************************************************************
 * Copyright (c) 2024 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.rectpacking.intermediate;

import java.util.Comparator;

import org.eclipse.elk.graph.ElkNode;

/**
 * Node size comparator to compare nodes by their size
 *
 */
public class NodeSizeComparator implements Comparator<ElkNode> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(ElkNode node0, ElkNode node1) {
        double area0 = node0.getWidth() * node0.getHeight();
        double area1 = node1.getWidth() * node1.getHeight();
        
        return Double.compare(area1, area0);
    }

}
