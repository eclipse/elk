/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges;

import org.eclipse.elk.alg.layered.ILayoutPhase;
import org.eclipse.elk.alg.layered.ILayoutPhaseFactory;
import org.eclipse.elk.alg.layered.p5edges.splines.SplineEdgeRouter;
import org.eclipse.elk.core.options.EdgeRouting;

/**
 * Factory for edge routers. This factory is necessary since the {@link EdgeRouting} enumeration is
 * defined outside of KLay Layered and can thus not be made into a factory.
 * 
 * @author cds
 * @kieler.design proposed by cds
 * @kieler.rating proposed yellow by cds
 */
public final class EdgeRouterFactory implements ILayoutPhaseFactory {
    
    /** the edge routing this factory uses to decide which implementation to return. */
    private EdgeRouting edgeRoutingStrategy;
    
    
    /**
     * Creates a new factory for the given edge routing strategy. The strategy decides which edge router
     * implementation the factory returns.
     * 
     * @param edgeRoutingStrategy the edge routing strategy.
     * @return the edge router factory.
     */
    public static EdgeRouterFactory factoryFor(final EdgeRouting edgeRoutingStrategy) {
        EdgeRouterFactory factory = new EdgeRouterFactory();
        factory.edgeRoutingStrategy = edgeRoutingStrategy;
        return factory;
    }
    
    /**
     * {@inheritDoc}
     */
    public ILayoutPhase create() {
        switch (edgeRoutingStrategy) {
        case POLYLINE:
            return new PolylineEdgeRouter();
            
        case SPLINES:
            return new SplineEdgeRouter();
            
        default:
            return new OrthogonalEdgeRouter();
        }
    }

}
