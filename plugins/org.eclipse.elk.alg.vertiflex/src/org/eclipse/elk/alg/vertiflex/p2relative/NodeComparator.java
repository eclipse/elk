/*******************************************************************************
 * Copyright (c) 2023 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.vertiflex.p2relative;

import org.eclipse.elk.graph.ElkNode;

/**
 * Comparator for sorting ElkNodes according to their y positions.
 *
 */
class NodeComparator implements java.util.Comparator<ElkNode> {

    @Override
    public int compare(final ElkNode a, final ElkNode b) {
        // TODO: this should use the node model order as a secondary criterion and if a and b are fuzzily at the 
        //       same y level, the model order should be used instead.
        return (int) (a.getY() - b.getY());
    }
}
