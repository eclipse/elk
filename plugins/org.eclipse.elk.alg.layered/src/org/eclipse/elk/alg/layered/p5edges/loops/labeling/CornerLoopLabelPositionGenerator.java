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
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.loops.util.SelfLoopBendpointCalculationUtil;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;

/**
 * Generates self loop label positions for corner self loops.
 */
public class CornerLoopLabelPositionGenerator extends AbstractSelfLoopLabelPositionGenerator {

    /**
     * Creates a new instance for the given node.
     */
    public CornerLoopLabelPositionGenerator(final SelfLoopNode slNode) {
        super(slNode);
    }
    

    @Override
    public List<SelfLoopLabelPosition> generatePositions(final SelfLoopComponent component) {
        List<SelfLoopLabelPosition> positions = new ArrayList<>();

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
        SelfLoopLabel label = component.getLabel();
        
        KVector startPosition = startPort.getLPort().getPosition().clone().add(startPort.getLPort().getAnchor());
        KVector endPosition = endPort.getLPort().getPosition().clone().add(endPort.getLPort().getAnchor());

        double directionStart = SplinesMath.portSideToDirection(startPort.getPortSide());
        KVector dirVectorStart = new KVector(directionStart);
        
        double directionEnd = SplinesMath.portSideToDirection(endPort.getPortSide());
        KVector dirVectorEnd = new KVector(directionEnd);
        
        KVector firstBendpoint = startPosition.clone().add(
                dirVectorStart.clone().scale((startPort.getMaximumLevel() * edgeEdgeSpacing) + edgeLabelSpacing));
        KVector secondBendpoint = endPosition.clone().add(
                dirVectorEnd.clone().scale((endPort.getMaximumLevel() * edgeEdgeSpacing) + edgeLabelSpacing));

        KVector cornerPoint = SelfLoopBendpointCalculationUtil.calculateCornerBendPoint(
                firstBendpoint, startPort.getPortSide(), secondBendpoint, endPort.getPortSide());

        SelfLoopLabelPosition firstShortSegment = shortSegmentCenteredPosition(
                label, firstBendpoint, cornerPoint, startPort);
        positions.add(firstShortSegment);

        SelfLoopLabelPosition secondShortSegment = shortSegmentCenteredPosition(
                label, secondBendpoint, cornerPoint, endPort);
        positions.add(secondShortSegment);

        // Side aligned positions
        SelfLoopLabelPosition firstShortSegmentRightBottomAligned = shortSegmentAlignedPosition(
                label, firstBendpoint, cornerPoint, startPort, false);
        positions.add(firstShortSegmentRightBottomAligned);

        SelfLoopLabelPosition secondShortSegmentRightBottomAligned = shortSegmentAlignedPosition(
                label, secondBendpoint, cornerPoint, endPort, false);
        positions.add(secondShortSegmentRightBottomAligned);

        SelfLoopLabelPosition firstShortSegmentLeftTopAligned = shortSegmentAlignedPosition(
                label, firstBendpoint, cornerPoint, startPort, true);
        positions.add(firstShortSegmentLeftTopAligned);

        SelfLoopLabelPosition secondShortSegmentLeftTopAligned = shortSegmentAlignedPosition(
                label, secondBendpoint, cornerPoint, endPort, true);
        positions.add(secondShortSegmentLeftTopAligned);

        return positions;
    }

}
