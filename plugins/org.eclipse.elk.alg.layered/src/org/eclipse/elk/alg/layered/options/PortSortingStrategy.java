/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.options;

import org.eclipse.elk.alg.layered.intermediate.PortListSorter;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.core.options.PortSide;

/**
 * Strategy with which a node's ports are ordered by the {@link PortListSorter}. Only relevant if the node's
 * {@link PortConstraints} are {@link PortConstraints#FIXED_SIDE}.
 */
public enum PortSortingStrategy {

    /**
     * Distributes the ports to their respective sides but preserves the input order 
     * among the ports of a common side.  
     */
    INPUT_ORDER,
    
    /**
     * Using {@link #INPUT_ORDER} as basis, additionally sort the ports on the {@link PortSide#WEST} and the
     * {@link PortSide#EAST} side according to the individual port's out-degree and in-degree, respectively (that is,
     * the degree in the original input graph).
     */
    PORT_DEGREE
    
}
