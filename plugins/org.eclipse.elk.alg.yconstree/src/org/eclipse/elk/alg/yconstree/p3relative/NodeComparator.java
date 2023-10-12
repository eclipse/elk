/*******************************************************************************
 * Copyright (c) 2023 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.yconstree.p3relative;

import org.eclipse.elk.graph.ElkNode;

/**
 * @author claas
 *
 */
class NodeComparator implements java.util.Comparator<ElkNode> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(final ElkNode a, final ElkNode b) {
        // TODO Auto-generated method stub
        return (int) (a.getY() - b.getY()); // might be problematic when very small numbers are compared 
    }
}
