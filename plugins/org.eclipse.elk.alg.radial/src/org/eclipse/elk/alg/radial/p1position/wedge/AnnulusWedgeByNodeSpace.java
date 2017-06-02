/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.radial.p1position.wedge;

import java.util.List;

import org.eclipse.elk.alg.radial.RadialUtil;
import org.eclipse.elk.graph.ElkNode;

/**
 * Determine the annulus wedge size by calculating the diameter of an node.
 */
public class AnnulusWedgeByNodeSpace implements IAnnulusWedgeCriteria {

    /**
     * Determine the space a node needs for its wedge by calculating the diagonal.
     */
    @Override
    public double calculateWedgeSpace(final ElkNode node) {
        List<ElkNode> successors = RadialUtil.getSuccessors(node);
        double height = node.getHeight();
        double width = node.getWidth();
        double nodeSize = Math.sqrt(height * height + width * width);
       
        double childSpace = 0;
        for (ElkNode child : successors) {
            childSpace += calculateWedgeSpace(child);
        }
        return Math.max(childSpace, nodeSize);
    }

}