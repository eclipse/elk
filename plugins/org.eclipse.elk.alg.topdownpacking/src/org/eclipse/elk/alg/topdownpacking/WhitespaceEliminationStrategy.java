/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://rtsys.informatik.uni-kiel.de/kieler
 * 
 * Copyright 2022 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 */
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