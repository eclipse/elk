/*******************************************************************************
 * Copyright (c) 2010, 2019 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.orthogonal.direction;

import org.eclipse.elk.alg.layered.graph.LEdge;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.p5edges.orthogonal.HyperEdgeSegment;
import org.eclipse.elk.alg.layered.p5edges.orthogonal.OrthogonalRoutingGenerator;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

/**
 * Routing strategy for routing layers from south to north.
 */
class SouthToNorthRoutingStrategy extends BaseRoutingDirectionStrategy {

    @Override
    public double getPortPositionOnHyperNode(final LPort port) {
        return port.getNode().getPosition().x + port.getPosition().x + port.getAnchor().x;
    }

    @Override
    public PortSide getSourcePortSide() {
        return PortSide.NORTH;
    }

    @Override
    public PortSide getTargetPortSide() {
        return PortSide.SOUTH;
    }

    @Override
    public void calculateBendPoints(final HyperEdgeSegment hyperNode, final double startPos, final double edgeSpacing) {
        // Calculate coordinates for each port's bend points
        double y = startPos - hyperNode.getRoutingSlot() * edgeSpacing;

        for (LPort port : hyperNode.getPorts()) {
            double sourcex = port.getAbsoluteAnchor().x;

            for (LEdge edge : port.getOutgoingEdges()) {

                if (!edge.isSelfLoop()) {
                    LPort target = edge.getTarget();
                    double targetx = target.getAbsoluteAnchor().x;
                    if (Math.abs(sourcex - targetx) > OrthogonalRoutingGenerator.TOLERANCE) {
                        KVector point1 = new KVector(sourcex, y);
                        edge.getBendPoints().add(point1);
                        addJunctionPointIfNecessary(edge, hyperNode, point1, false);

                        KVector point2 = new KVector(targetx, y);
                        edge.getBendPoints().add(point2);
                        addJunctionPointIfNecessary(edge, hyperNode, point2, false);
                    }
                }
            }
        }
    }
    
}