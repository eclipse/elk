/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.labeling;

import java.util.List;

import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopLabelPosition;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopRoutingDirection;
import org.eclipse.elk.alg.layered.p5edges.oldloops.util.SelfLoopBendpointCalculationUtil;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Iterables;

/**
 * Generates self loop label positions for four-corner self loops.
 */
public class FourCornerLoopLabelPositionGenerator extends AbstractSelfLoopLabelPositionGenerator {

    /**
     * Creates a new generator for the given self loop node.
     */
    public FourCornerLoopLabelPositionGenerator(final OldSelfLoopNode slNode) {
        super(slNode);
    }

    @Override
    public void generatePositions(final OldSelfLoopComponent component) {
        List<OldSelfLoopPort> ports = component.getPorts();
        OldSelfLoopPort startPort = ports.get(0);
        OldSelfLoopPort endPort = ports.get(ports.size() - 1);
        
        // Retrieve the spacings active for this node
        double edgeEdgeSpacing = getEdgeEdgeSpacing();
        double edgeLabelSpacing = getEdgeLabelSpacing();
        
        // Generate all the bend points
        KVector startPosition = startPort.getLPort().getPosition().clone().add(startPort.getLPort().getAnchor());
        KVector endPosition = endPort.getLPort().getPosition().clone().add(endPort.getLPort().getAnchor());

        double directionStart = SplinesMath.portSideToDirection(startPort.getPortSide());
        KVector dirVectorStart = new KVector(directionStart);
        KVector firstBend = startPosition.clone().add(dirVectorStart.clone().scale(
                (startPort.getMaximumLevel() * edgeEdgeSpacing) + edgeLabelSpacing));

        double directionEnd = SplinesMath.portSideToDirection(endPort.getPortSide());
        KVector dirVectorEnd = new KVector(directionEnd);
        KVector secondBend = endPosition.clone().add(dirVectorEnd.clone().scale(
                (endPort.getMaximumLevel() * edgeEdgeSpacing) + edgeLabelSpacing));

        OldSelfLoopEdge edge = Iterables.get(component.getConnectedEdges(), 0);
        List<KVector> cornerBends = SelfLoopBendpointCalculationUtil.generateCornerBendpoints(getSelfLoopNode(),
                startPort, endPort, firstBend, secondBend, edge);
        
        // Generate all the segment sides
        // SUPPRESS CHECKSTYLE NEXT MagicNumber
        PortSide[] segmentSides = new PortSide[3];
        PortSide startSide = startPort.getPortSide();
        
        if (startPort.getDirection() == OldSelfLoopRoutingDirection.RIGHT) {
            segmentSides[0] = startSide.right();
            segmentSides[1] = segmentSides[0].right();
            segmentSides[2] = segmentSides[1].right();
        } else {
            segmentSides[0] = startSide.left();
            segmentSides[1] = segmentSides[0].left();
            segmentSides[2] = segmentSides[1].left();
        }
        
        addPositions(component, startPort, endPort, segmentSides, firstBend, cornerBends, secondBend);
    }

    private void addPositions(final OldSelfLoopComponent component, final OldSelfLoopPort startPort,
            final OldSelfLoopPort endPort, final PortSide[] segmentSides, final KVector firstBend,
            final List<KVector> cornerBends, final KVector lastBend) {
        
        OldSelfLoopLabel label = component.getSelfLoopLabel();
        List<OldSelfLoopLabelPosition> positions = label.getCandidatePositions();
        
        // SUPPRESS CHECKSTYLE NEXT 40 MagicNumber
        
        // Full segment 2 (long)
        positions.add(longSegmentPosition(
                label, segmentSides[0], cornerBends.get(1), cornerBends.get(2), Alignment.CENTERED));
        positions.add(longSegmentPosition(
                label, segmentSides[0], cornerBends.get(1), cornerBends.get(2), Alignment.LEFT_OR_TOP));
        positions.add(longSegmentPosition(
                label, segmentSides[0], cornerBends.get(1), cornerBends.get(2), Alignment.RIGHT_OR_BOTTOM));
        
        // Full segment 1 (long)
        positions.add(longSegmentPosition(
                label, segmentSides[0], cornerBends.get(0), cornerBends.get(1), Alignment.CENTERED));
        positions.add(longSegmentPosition(
                label, segmentSides[0], cornerBends.get(0), cornerBends.get(1), Alignment.LEFT_OR_TOP));
        positions.add(longSegmentPosition(
                label, segmentSides[0], cornerBends.get(0), cornerBends.get(1), Alignment.RIGHT_OR_BOTTOM));
        
        // Full segment 3 (long)
        positions.add(longSegmentPosition(
                label, segmentSides[0], cornerBends.get(2), cornerBends.get(3), Alignment.CENTERED));
        positions.add(longSegmentPosition(
                label, segmentSides[0], cornerBends.get(2), cornerBends.get(3), Alignment.LEFT_OR_TOP));
        positions.add(longSegmentPosition(
                label, segmentSides[0], cornerBends.get(2), cornerBends.get(3), Alignment.RIGHT_OR_BOTTOM));

        // Start segment (short)
        positions.add(shortSegmentPosition(
                label, startPort, firstBend, cornerBends.get(0), Alignment.CENTERED, true));
        positions.add(shortSegmentPosition(
                label, startPort, firstBend, cornerBends.get(0), Alignment.LEFT_OR_TOP, true));
        positions.add(shortSegmentPosition(
                label, startPort, firstBend, cornerBends.get(0), Alignment.RIGHT_OR_BOTTOM, true));

        // End segment (short)
        positions.add(shortSegmentPosition(
                label, endPort, lastBend, cornerBends.get(3), Alignment.CENTERED, true));
        positions.add(shortSegmentPosition(
                label, endPort, lastBend, cornerBends.get(3), Alignment.LEFT_OR_TOP, true));
        positions.add(shortSegmentPosition(
                label, endPort, lastBend, cornerBends.get(3), Alignment.RIGHT_OR_BOTTOM, true));
    }
    
}
