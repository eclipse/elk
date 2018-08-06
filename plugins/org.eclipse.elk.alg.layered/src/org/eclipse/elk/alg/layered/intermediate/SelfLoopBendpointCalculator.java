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
package org.eclipse.elk.alg.layered.intermediate;

import java.util.Iterator;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopRoutingDirection;
import org.eclipse.elk.alg.layered.p5edges.loops.routing.ISelfLoopRouter;
import org.eclipse.elk.alg.layered.p5edges.loops.routing.OrthogonalSelfLoopRouter;
import org.eclipse.elk.alg.layered.p5edges.loops.routing.PolylineSelfLoopRouter;
import org.eclipse.elk.alg.layered.p5edges.loops.routing.SplineSelfLoopRouter;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Routes self-loops. The connected components of self-loops that are holding more than one loop are sorted, so that
 * the loop with the smallest label is drawn first.
 * 
 * <dl>
 *   <dt>Preconditions:</dt>
 *     <dd>A layered graph.</dd>
 *     <dd>Connected components of all self-loops must have been stored in the
 *         {@link InternalProperties#SELFLOOP_COMPONENTS} property of the nodes.</dd>
 *     <dd>The ports of the components must be part of the port list of the node the component is laying on.</dd>
 *   <dt>Postconditions:</dt>
 *     <dd>Bend points for all self-loops are calculated.</dd>
 *     <dd>The {@link InternalProperties#SELF_LOOP_MARGINS} are increased to include all self-loops and their
 *         labels.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 4.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link SelfLoopPlacer}</dd>
 * </dl>
 */
public final class SelfLoopBendpointCalculator implements ILayoutProcessor<LGraph> {

    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("SelfLoop Bendpoint Calculation", 1);
        
        ISelfLoopRouter loopRouter = determineLoopRouter(layeredGraph);
        EdgeRouting routingStyle = layeredGraph.getProperty(LayeredOptions.EDGE_ROUTING);

        for (Layer layer : layeredGraph.getLayers()) {
            for (LNode node : layer.getNodes()) {
                if (node.getType() == NodeType.NORMAL) {
                    SelfLoopNode nodeRep =
                            node.getProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION);
                    List<SelfLoopComponent> components = node.getProperty(InternalProperties.SELFLOOP_COMPONENTS);

                    for (SelfLoopComponent component : components) {
                        for (SelfLoopEdge edge : component.getConnectedEdges()) {
                            loopRouter.routeSelfLoop(edge, nodeRep);
                        }

                        calculateJunctionPoints(component, routingStyle);
                    }
                }
            }
        }

        monitor.done();
    }

    /**
     * Returns the self loop router to be used for the given graph.
     */
    private ISelfLoopRouter determineLoopRouter(final LGraph layeredGraph) {
        switch (layeredGraph.getProperty(LayeredOptions.EDGE_ROUTING)) {
        case ORTHOGONAL:
            return new OrthogonalSelfLoopRouter();
        case SPLINES:
            return new SplineSelfLoopRouter();
        case POLYLINE:
            return new PolylineSelfLoopRouter();
        default:
            return new OrthogonalSelfLoopRouter();
        }
    }

    /**
     * Calculates the junction points for the given self loop component.
     */
    private void calculateJunctionPoints(final SelfLoopComponent component, final EdgeRouting routingStyle) {
        for (SelfLoopPort port : component.getPorts()) {
            // middle port or there exits a junction with a non-loop edge
            if (port.getDirection() == SelfLoopRoutingDirection.BOTH
                    || (port.isNonLoopPort() && component.getPorts().size() != 1)) {
                
                Iterator<LEdge> connectedEdges = port.getLPort().getConnectedEdges().iterator();
                while (connectedEdges.hasNext()) {
                    LEdge edge = connectedEdges.next();
                    KVectorChain junctionPoints = edge.getProperty(LayeredOptions.JUNCTION_POINTS);
                    
                    if (junctionPoints == null) {
                        junctionPoints = new KVectorChain();
                        edge.setProperty(LayeredOptions.JUNCTION_POINTS, junctionPoints);
                    }

                    KVectorChain bendpoints = edge.getBendPoints();
                    if (!bendpoints.isEmpty()) {
                        KVector jpoint = null;
                        if (edge.getTarget() == port.getLPort()) {
                            int lastBendPoint = routingStyle == EdgeRouting.POLYLINE
                                    ? bendpoints.size() - 2
                                    : bendpoints.size() - 1;
                            jpoint = new KVector(bendpoints.get(lastBendPoint));
                        } else {
                            int firstBendPoint = routingStyle == EdgeRouting.POLYLINE ? 1 : 0;
                            jpoint = new KVector(bendpoints.get(firstBendPoint));
                        }
                        junctionPoints.add(jpoint);
                    }
                }
            }
        }

    }
}
