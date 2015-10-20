/*******************************************************************************
 * Copyright (c) 2012, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.layered.p4nodes;

import org.eclipse.elk.layered.ILayoutPhase;
import org.eclipse.elk.layered.ILayoutPhaseFactory;
import org.eclipse.elk.layered.p4nodes.bk.BKNodePlacer;

/**
 * Definition of the available node placement strategies for the layered layout approach.
 *
 * @author jjc
 * @author cds
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating proposed yellow by msp
 */
public enum NodePlacementStrategy implements ILayoutPhaseFactory {
    
    /**
     * Very simple and very fast node placement that centers all nodes vertically.
     */
    SIMPLE,
    
    /**
     * Interactive node placer that leaves y coordinates of nodes untouched.
     */
    INTERACTIVE,
    
    /**
     * Node placement algorithm that aligns long edges using linear segments.
     * Nodes are aligned according to the <em>pendulum</em> method, which is similar to
     * the barycenter method for node ordering.
     */
    LINEAR_SEGMENTS,
    
    /**
     * Node placement which groups nodes to blocks which result in straight edges.
     */
    BRANDES_KOEPF;
    

    /**
     * {@inheritDoc}
     */
    public ILayoutPhase create() {
        switch (this) {
        case SIMPLE:
            return new SimpleNodePlacer();
            
        case INTERACTIVE:
            return new InteractiveNodePlacer();
            
        case LINEAR_SEGMENTS:
            return new LinearSegmentsNodePlacer();
            
        case BRANDES_KOEPF:
            return new BKNodePlacer();
            
        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the node placer " + this.toString());
        }
    }
  
}
