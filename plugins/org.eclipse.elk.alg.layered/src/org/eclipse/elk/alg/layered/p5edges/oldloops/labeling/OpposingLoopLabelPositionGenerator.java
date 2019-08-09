/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.labeling;

import java.util.List;

import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopLabelPosition;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopRoutingDirection;
import org.eclipse.elk.alg.layered.p5edges.oldloops.util.SelfLoopBendpointCalculationUtil;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Iterables;

/**
 * Generates self loop label positions for opposing-side self loops.
 */
public class OpposingLoopLabelPositionGenerator extends AbstractSelfLoopLabelPositionGenerator {

    /**
     * Creates a new instance for the given node.
     */
    public OpposingLoopLabelPositionGenerator(final SelfLoopNode slNode) {
        super(slNode);
    }

    @Override
    public void generatePositions(final SelfLoopComponent component) {
        List<SelfLoopPort> ports = component.getPorts();
        SelfLoopPort startPort = ports.get(0);
        SelfLoopPort endPort = ports.get(ports.size() - 1);

        // Retrieve the spacings active for this node
        double edgeEdgeSpacing = getEdgeEdgeSpacing();
        double edgeLabelSpacing = getEdgeLabelSpacing();

        // Generate all the bend points
        KVector startPosition = startPort.getLPort().getPosition().clone().add(startPort.getLPort().getAnchor());
        KVector endPosition = endPort.getLPort().getPosition().clone().add(endPort.getLPort().getAnchor());

        double directionStart = SplinesMath.portSideToDirection(startPort.getPortSide());
        KVector dirVectorStart = new KVector(directionStart);
        
        double directionEnd = SplinesMath.portSideToDirection(endPort.getPortSide());
        KVector dirVectorEnd = new KVector(directionEnd);
        
        KVector firstBend = startPosition.clone().add(dirVectorStart.clone().scale(
                (startPort.getMaximumLevel() * edgeEdgeSpacing) + edgeLabelSpacing));
        KVector secondBend = endPosition.clone().add(dirVectorEnd.clone().scale(
                (endPort.getMaximumLevel() * edgeEdgeSpacing) + edgeLabelSpacing));

        SelfLoopEdge edge = Iterables.get(component.getConnectedEdges(), 0);
        List<KVector> cornerBends = SelfLoopBendpointCalculationUtil.generateCornerBendpoints(
                getSelfLoopNode(), startPort, endPort, firstBend, secondBend, edge);

        // Find the long segment's side
        PortSide longSegmentSide = startPort.getPortSide();
        if (startPort.getDirection() == SelfLoopRoutingDirection.RIGHT) {
            longSegmentSide = longSegmentSide.right();
        } else {
            longSegmentSide = longSegmentSide.left();
        }

        addPositions(component, startPort, endPort, longSegmentSide, firstBend, cornerBends, secondBend);
    }

    private void addPositions(final SelfLoopComponent component, final SelfLoopPort startPort,
            final SelfLoopPort endPort, final PortSide longSegmentSide, final KVector firstBend,
            final List<KVector> cornerBends, final KVector lastBend) {
        
        SelfLoopLabel label = component.getSelfLoopLabel();
        List<SelfLoopLabelPosition> positions = label.getCandidatePositions();
        
        // SUPPRESS CHECKSTYLE NEXT 40 MagicNumber
        
        // Full (long)
        positions.add(longSegmentPosition(
                label, longSegmentSide, cornerBends.get(0), cornerBends.get(1), Alignment.CENTERED));
        positions.add(longSegmentPosition(
                label, longSegmentSide, cornerBends.get(0), cornerBends.get(1), Alignment.LEFT_OR_TOP));
        positions.add(longSegmentPosition(
                label, longSegmentSide, cornerBends.get(0), cornerBends.get(1), Alignment.RIGHT_OR_BOTTOM));

        // Start segment (short)
        positions.add(shortSegmentPosition(
                label, startPort, firstBend, cornerBends.get(0), Alignment.CENTERED, true));
        positions.add(shortSegmentPosition(
                label, startPort, firstBend, cornerBends.get(0), Alignment.LEFT_OR_TOP, true));
        positions.add(shortSegmentPosition(
                label, startPort, firstBend, cornerBends.get(0), Alignment.RIGHT_OR_BOTTOM, true));

        // End segment (short)
        positions.add(shortSegmentPosition(
                label, endPort, lastBend, cornerBends.get(1), Alignment.CENTERED, true));
        positions.add(shortSegmentPosition(
                label, endPort, lastBend, cornerBends.get(1), Alignment.LEFT_OR_TOP, true));
        positions.add(shortSegmentPosition(
                label, endPort, lastBend, cornerBends.get(1), Alignment.RIGHT_OR_BOTTOM, true));
    }
    
}
