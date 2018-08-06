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
 * The possible ways to distribute self loops around a node.
 */
public enum SelfLoopDistributionStrategy {
    
    /** Distributes the loops equally around the node. */
    EQUALLY,
    /** Puts all loops to the north side of the node. */
    NORTH,
    /** Loops are distributed over the north and the south side of the node. */
    NORTH_SOUTH;
    
}
