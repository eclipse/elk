/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.tree.intermediate;

import org.eclipse.elk.tree.ILayoutProcessor;

/**
 * Definition of available intermediate layout processors for the tree layouter. This enumeration
 * also serves as a factory for intermediate layout processors.
 * 
 * @author sor
 * @author sgu
 */
public enum IntermediateProcessorStrategy {

    /**
     * In this enumeration, intermediate layout processors are listed by the earliest slot in which
     * they can sensibly be used. The order in which they are listed is determined by the
     * dependencies on other processors.
     */

    // Before Phase 2
    /** Determine the root of a given graph. */
    ROOT_PROC,
    /** Compute the fanout of each node in a given graph. */
    FAN_PROC,
    
    // Before Phase 2 and 3
    /** Determine the local neighbors of each node in a given graph. */
    NEIGHBORS_PROC,

    // Before Phase 3
    /** Determine the height of each level in the given graph. */
    LEVEL_HEIGHT,

    // Before Phase 4
    /** Set the coordinates for each node in a given graph. */
    NODE_POSITION_PROC,
    
    // After Phase 4
    /** Reinsert edges that were removed for treeifying. */
    DETREEIFYING_PROC;

    /**
     * Creates an instance of the layout processor described by this instance.
     * 
     * @return the layout processor.
     */
    public ILayoutProcessor create() {

        switch (this) {

        case ROOT_PROC:
            return new RootProcessor();

        case FAN_PROC:
            return new FanProcessor();

        case NEIGHBORS_PROC:
            return new NeighborsProcessor();

        case LEVEL_HEIGHT:
            return new LevelHeightProcessor();

        case NODE_POSITION_PROC:
            return new NodePositionProcessor();
            
        case DETREEIFYING_PROC:
            return new Untreeifyer();

        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the layout processor " + this.toString());
        }
    }
}
