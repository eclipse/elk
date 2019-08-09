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

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopRoutingDirection;
import org.eclipse.elk.alg.layered.p5edges.oldloops.routing.ISelfLoopRouter;
import org.eclipse.elk.alg.layered.p5edges.oldloops.routing.OrthogonalSelfLoopRouter;
import org.eclipse.elk.alg.layered.p5edges.oldloops.routing.PolylineSelfLoopRouter;
import org.eclipse.elk.alg.layered.p5edges.oldloops.routing.SplineSelfLoopRouter;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.options.EdgeRouting;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Routes self-loops. The connected components of self-loops that are holding more than one loop are sorted, so that the
 * loop with the smallest label is drawn first.
 * 
 * <dl>
 * <dt>Preconditions:</dt>
 * <dd>A layered graph.</dd>
 * <dd>Connected components of all self-loops must have been stored in the
 * {@link InternalProperties#SELFLOOP_COMPONENTS} property of the nodes.</dd>
 * <dd>The ports of the components must be part of the port list of the node the component is laying on.</dd>
 * <dt>Postconditions:</dt>
 * <dd>Bend points for all self-loops are calculated.</dd>
 * <dt>Slots:</dt>
 * <dd>Before phase 4.</dd>
 * <dt>Same-slot dependencies:</dt>
 * <dd>{@link OldSelfLoopPlacer}</dd>
 * </dl>
 */
public final class OldSelfLoopBendpointCalculator implements ILayoutProcessor<LGraph> {

    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("SelfLoop Bendpoint Calculation", 1);

        EdgeRouting routingStyle = layeredGraph.getProperty(LayeredOptions.EDGE_ROUTING);
        ISelfLoopRouter loopRouter = determineLoopRouter(routingStyle);

        layeredGraph.getLayers().stream()
            .flatMap(layer -> layer.getNodes().stream())
            .filter(node -> node.getType() == NodeType.NORMAL)
            .filter(node -> node.hasProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION))
            .forEach(node -> processNode(node, loopRouter, routingStyle));

        monitor.done();
    }

    /**
     * Returns the self loop router to be used for the given graph.
     */
    private ISelfLoopRouter determineLoopRouter(final EdgeRouting routing) {
        switch (routing) {
        case SPLINES:
            return new SplineSelfLoopRouter();
        case POLYLINE:
            return new PolylineSelfLoopRouter();
        case ORTHOGONAL:
        default:
            return new OrthogonalSelfLoopRouter();
        }
    }

    private void processNode(final LNode node, final ISelfLoopRouter loopRouter, final EdgeRouting routingStyle) {
        SelfLoopNode slNode = node.getProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION);
        assert slNode != null;
        
        for (SelfLoopComponent component : slNode.getSelfLoopComponents()) {
            for (SelfLoopEdge edge : component.getConnectedEdges()) {
                loopRouter.routeSelfLoop(edge, slNode);
            }

            calculateJunctionPoints(component, routingStyle);
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
                            int lastBendPoint = routingStyle == EdgeRouting.POLYLINE ? bendpoints.size() - 2
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
