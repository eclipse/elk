/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  
 *******************************************************************************/
package org.eclipse.elk.alg.topdownpacking;

import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.ILayoutPhaseFactory;

/**
 * @author mka
 *
 */
public enum WhitespaceEliminationStrategy
        implements ILayoutPhaseFactory<TopdownPackingPhases, GridElkNode> {
    WHITESPACE_ELIMINATOR;

    /**
     * {@inheritDoc}
     */
    @Override
    public ILayoutPhase<TopdownPackingPhases, GridElkNode> create() {
        switch (this) {
        case WHITESPACE_ELIMINATOR:
            return new WhitespaceEliminator();
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the whitespace eliminator" + this.toString());
        }
    }

}
