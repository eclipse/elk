/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.rectpacking.p3whitespaceelimination;

import org.eclipse.elk.alg.rectpacking.RectPackingLayoutPhases;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.ILayoutPhaseFactory;
import org.eclipse.elk.graph.ElkNode;

/**
 * Strategy factory for whitespace elimination.
 *
 */
public enum WhiteSpaceEliminationStrategy implements ILayoutPhaseFactory<RectPackingLayoutPhases, ElkNode> {
    EQUAL_BETWEEN_STRUCTURES,
    TO_ASPECT_RATIO;

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutPhaseFactory#create()
     */
    @Override
    public ILayoutPhase<RectPackingLayoutPhases, ElkNode> create() {
        switch (this) {
        case EQUAL_BETWEEN_STRUCTURES:
            return new EqualWhitespaceEliminator();
        case TO_ASPECT_RATIO:
            return new ToAspectratioNodeExpander();
        default:
            return null;
        }
    }

}
