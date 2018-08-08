/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.labeling;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.loops.util.SelfLoopBendpointCalculationUtil;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;

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
    public List<SelfLoopLabelPosition> generatePositions(final SelfLoopComponent component) {
        List<SelfLoopLabelPosition> positions = new ArrayList<SelfLoopLabelPosition>();

        List<SelfLoopPort> ports = component.getPorts();
        SelfLoopPort startPort = ports.get(0);
        SelfLoopPort endPort = ports.get(ports.size() - 1);

        positions = generatePositions(component, startPort, endPort);
        
        return positions;
    }

    private List<SelfLoopLabelPosition> generatePositions(final SelfLoopComponent component,
            final SelfLoopPort startPort, final SelfLoopPort endPort) {
        
        // Retrieve the spacings active for this node
        double edgeEdgeSpacing = getEdgeEdgeSpacing();
        double edgeLabelSpacing = getEdgeLabelSpacing();
        
        List<SelfLoopLabelPosition> positions = new ArrayList<>();
        
        KVector startPosition = startPort.getLPort().getPosition().clone().add(startPort.getLPort().getAnchor());
        KVector endPosition = endPort.getLPort().getPosition().clone().add(endPort.getLPort().getAnchor());

        double directionStart = SplinesMath.portSideToDirection(startPort.getPortSide());
        KVector dirVectorStart = new KVector(directionStart);
        
        double directionEnd = SplinesMath.portSideToDirection(endPort.getPortSide());
        KVector dirVectorEnd = new KVector(directionEnd);
        
        KVector firstBendpoint = startPosition.clone().add(dirVectorStart.clone().scale(
                (startPort.getMaximumLevel() * edgeEdgeSpacing) + edgeLabelSpacing));
        KVector secondBendpoint = endPosition.clone().add(dirVectorEnd.clone().scale(
                (endPort.getMaximumLevel() * edgeEdgeSpacing) + edgeLabelSpacing));

        SelfLoopEdge edge = Iterables.get(component.getConnectedEdges(), 0);
        List<KVector> cornerPoints = SelfLoopBendpointCalculationUtil.generateCornerBendpoints(
                getSelfLoopNode(), startPort, endPort, firstBendpoint, secondBendpoint, edge);

        KVector cornerPoint1 = cornerPoints.get(0);
        KVector cornerPoint2 = cornerPoints.get(1);

        // Centered Positions
        List<SelfLoopLabelPosition> centeredPositions = createCenteredPositions(startPort, endPort, firstBendpoint,
                secondBendpoint, cornerPoint1, cornerPoint2, component.getLabel());
        positions.addAll(centeredPositions);

        // Side aligned positions
        List<SelfLoopLabelPosition> alignedPositions = createSideAlignedPositions(startPort, endPort, firstBendpoint,
                secondBendpoint, cornerPoint1, cornerPoint2, component.getLabel());
        positions.addAll(alignedPositions);

        return positions;
    }

    private List<SelfLoopLabelPosition> createCenteredPositions(final SelfLoopPort startPort,
            final SelfLoopPort endPort, final KVector firstBendpoint, final KVector secondBendpoint,
            final KVector cornerPoint1, final KVector cornerPoint2, final SelfLoopLabel label) {
        
        List<SelfLoopLabelPosition> positions = new ArrayList<>();

        SelfLoopLabelPosition firstShortSegment = shortSegmentCenteredPosition(
                label, firstBendpoint, cornerPoint1, startPort);
        positions.add(firstShortSegment);

        SelfLoopLabelPosition secondShortSegment = shortSegmentCenteredPosition(
                label, secondBendpoint, cornerPoint2, endPort);
        positions.add(secondShortSegment);

        SelfLoopLabelPosition firstLongSegment = longSegmentCenteredPosition(
                label, cornerPoint1, cornerPoint2, startPort);
        positions.add(firstLongSegment);

        return positions;

    }

    private List<SelfLoopLabelPosition> createSideAlignedPositions(final SelfLoopPort startPort,
            final SelfLoopPort endPort, final KVector firstBendpoint, final KVector secondBendpoint,
            final KVector cornerPoint1, final KVector cornerPoint2, final SelfLoopLabel label) {
        
        List<SelfLoopLabelPosition> positions = new ArrayList<>();

        // Right-aligned
        SelfLoopLabelPosition firstShortSegmentRightBottomAligned = shortSegmentAlignedPosition(
                label, firstBendpoint, cornerPoint1, startPort, false);
        positions.add(firstShortSegmentRightBottomAligned);

        SelfLoopLabelPosition secondShortSegmentRightBottomAligned = shortSegmentAlignedPosition(
                label, secondBendpoint, cornerPoint2, endPort, false);
        positions.add(secondShortSegmentRightBottomAligned);

        SelfLoopLabelPosition firstLongSegmentRightBottomAligned = longSegmentAlignedPosition(
                label, cornerPoint1, cornerPoint2, startPort, false);
        positions.add(firstLongSegmentRightBottomAligned);

        // Left-aligned
        SelfLoopLabelPosition firstShortSegmentLeftTopAligned = shortSegmentAlignedPosition(
                label, firstBendpoint, cornerPoint1, startPort, true);
        positions.add(firstShortSegmentLeftTopAligned);

        SelfLoopLabelPosition secondShortSegmentLeftTopAligned = shortSegmentAlignedPosition(
                label, secondBendpoint, cornerPoint2, endPort, true);
        positions.add(secondShortSegmentLeftTopAligned);

        SelfLoopLabelPosition firstLongSegmentLeftTopAligned = longSegmentAlignedPosition(
                label, cornerPoint1, cornerPoint2, startPort, true);
        positions.add(firstLongSegmentLeftTopAligned);

        return positions;
    }
    
}
