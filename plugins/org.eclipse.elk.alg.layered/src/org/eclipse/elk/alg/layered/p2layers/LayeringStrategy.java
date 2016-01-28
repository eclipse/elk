/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p2layers;

import org.eclipse.elk.alg.layered.ILayoutPhase;
import org.eclipse.elk.alg.layered.ILayoutPhaseFactory;

/**
 * Enumeration of and factory for the different available layering strategies.
 * 
 * @author pdo
 * @author cds
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating yellow 2012-11-13 review KI-33 by grh, akoc
 */
public enum LayeringStrategy implements ILayoutPhaseFactory {

    /**
     * All nodes will be layered with minimal edge length by using the network-simplex-algorithm.
     */
    NETWORK_SIMPLEX,
    /**
     * All nodes will be layered according to the longest path to any sink.
     */
    LONGEST_PATH,
    /**
     * Nodes are put into layers according to their relative position. The actual positions
     * as given in the input diagram are considered here. This means that if the user moves
     * a node, that movement is reflected in the layering of the graph.
     */
    INTERACTIVE;

    
    /**
     * {@inheritDoc}
     */
    public ILayoutPhase create() {
        switch (this) {
        case NETWORK_SIMPLEX:
            return new NetworkSimplexLayerer();
            
        case LONGEST_PATH:
            return new LongestPathLayerer();
            
        case INTERACTIVE:
            return new InteractiveLayerer();
            
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the layerer " + this.toString());
        }
    }

}
