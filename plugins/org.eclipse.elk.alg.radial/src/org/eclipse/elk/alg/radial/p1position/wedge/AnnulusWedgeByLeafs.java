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

import org.eclipse.elk.alg.radial.RadialUtil;
import org.eclipse.elk.graph.ElkNode;

/**
 * Determine the annulus wedge size by the number of leafs a node has.
 */
public class AnnulusWedgeByLeafs implements IAnnulusWedgeCriteria {

    /**
     * Determine the space a node needs for its wedge by calculating the number of leaves.
     */
    @Override
    public double calculateWedgeSpace(final ElkNode node) {
        return RadialUtil.getNumberOfLeaves(node);
    }

}