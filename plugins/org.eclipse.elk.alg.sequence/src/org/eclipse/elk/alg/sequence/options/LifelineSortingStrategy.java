/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.options;

import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.p4sorting.InteractiveLifelineSorter;
import org.eclipse.elk.alg.sequence.p4sorting.LayerBasedLifelineSorter;
import org.eclipse.elk.alg.sequence.p4sorting.ShortMessageLifelineSorter;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.ILayoutPhaseFactory;

/**
 * Definition of available lifeline sorting strategies for the sequence diagram layouter.
 */
public enum LifelineSortingStrategy implements ILayoutPhaseFactory<SequencePhases, LayoutContext> {

    /** Sort the lifelines according to their x-coordinates. */
    INTERACTIVE,
    /** Sort the lifelines according to the layers of the associated messages. */
    LAYER_BASED,
    /**
     * Sort the lifelines according to McAllister's solution for the linear arrangement problem that
     * minimizes the total length of messages.
     */
    SHORT_MESSAGES;

    
    @Override
    public ILayoutPhase<SequencePhases, LayoutContext> create() {
        switch (this) {
        case INTERACTIVE:
            return new InteractiveLifelineSorter();
            
        case LAYER_BASED:
            return new LayerBasedLifelineSorter();
            
        case SHORT_MESSAGES:
            return new ShortMessageLifelineSorter();
            
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the lifeline sorter " + this.toString());
        }
    }
    
}
