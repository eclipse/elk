/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.options;

/**
 * Enumeration to distinguish the possible ways to distribute the selfLoops around a node.
 * 
 * @author tit
 */
public enum SelfLoopPlacementStrategy {
    /** Distributes the loops equally around the node. */
    EQUALLY_DISTRIBUTED,
    /** Stacks all loops to the north side of the node. */
    NORTH_STACKED,
    /** Loops are placed sequentially (next to each other) to the north side of the node. */
    NORTH_SEQUENCE;
}
