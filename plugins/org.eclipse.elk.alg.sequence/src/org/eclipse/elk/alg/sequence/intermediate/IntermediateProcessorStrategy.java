/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.intermediate;

import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.alg.ILayoutProcessorFactory;

/**
 * Definition of available intermediate layout processors for the sequence diagram layouter. This enumeration also
 * serves as a factory for intermediate layout processors.
 */
public enum IntermediateProcessorStrategy implements ILayoutProcessorFactory<LayoutContext> {
    
    /*
     * In this enumeration, intermediate layout processors are listed by the earliest slot in which
     * they can sensibly be used. The order in which they are listed is determined by the
     * dependencies on other processors.
     */

    // Before Phase 1
    
    /** Creates and initializes the layered graph based on the sequence graph. */
    LAYERED_GRAPH_CREATOR,


    // Before Phase 2


    // Before Phase 3


    // Before Phase 4


    // Before Phase 5
    
    /** Computes nesting slots for executions that will later deterine their horizontal offset. */
    EXECUTION_SLOT_CALCULATOR;
    
    
    // Before Phase 6


    // After Phase 6
    
    

    @Override
    public ILayoutProcessor<LayoutContext> create() {
        switch (this) {
        case EXECUTION_SLOT_CALCULATOR:
            return new ExecutionSlotCalculator();
        case LAYERED_GRAPH_CREATOR:
            return new LayeredGraphCreator();
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the layout processor " + toString());
        }
    }
    
}
