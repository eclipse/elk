/*******************************************************************************
 * Copyright (c) 2019 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.intermediate.loops.routing.AbstractSelfLoopRouter;
import org.eclipse.elk.alg.layered.intermediate.loops.routing.OrthogonalSelfLoopRouter;
import org.eclipse.elk.alg.layered.intermediate.loops.routing.PolylineSelfLoopRouter;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Restores self loop ports and computes everything required to decide where the self loops will be routed. The
 * information will later be used to compute the actual routes.
 * 
 * <dl>
 * <dt>Preconditions:</dt>
 *   <dd>Self loops have had routing slots assigned by {@link SelfLoopOrderer}.</dd>
 * <dt>Postconditions:</dt>
 *   <dd>All self loop edges are routed, but not yet restored.</dd>
 *   <dd>Node margins are extended by the space required to route self loops.</dd>
 * <dt>Slots:</dt>
 *   <dd>Before phase 4.</dd>
 * <dt>Same-slot dependencies:</dt>
 *   <dd>{@link InnermostNodeMarginCalculator}</dd>
 * </dl>
 */
public class SelfLoopRouter implements ILayoutProcessor<LGraph> {
    
    @Override
    public void process(final LGraph graph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Self-Loop routing", 1);
        
        AbstractSelfLoopRouter router = routerForGraph(graph);
        
        // Process every node that actually has self loops
        graph.getLayers().stream()
            .flatMap(layer -> layer.getNodes().stream())
            .filter(lNode -> lNode.getType() == NodeType.NORMAL)
            .filter(lNode -> lNode.hasProperty(InternalProperties.SELF_LOOP_HOLDER))
            .map(lNode -> lNode.getProperty(InternalProperties.SELF_LOOP_HOLDER))
            .forEach(slHolder -> router.routeSelfLoops(slHolder));
        
        progressMonitor.done();
    }

    private AbstractSelfLoopRouter routerForGraph(final LGraph graph) {
        switch (graph.getProperty(LayeredOptions.EDGE_ROUTING)) {
        case POLYLINE:
            return new PolylineSelfLoopRouter();
            
        case SPLINES:
            // TODO Implement
            return null;
            
        default:
            return new OrthogonalSelfLoopRouter();
        }
    }

}
