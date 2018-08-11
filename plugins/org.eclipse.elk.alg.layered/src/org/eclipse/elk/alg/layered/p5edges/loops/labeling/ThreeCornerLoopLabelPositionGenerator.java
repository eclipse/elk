/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.labeling;

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
 * Generates self loop label positions for three-corner self loops.
 */
public class ThreeCornerLoopLabelPositionGenerator extends AbstractSelfLoopLabelPositionGenerator {

    /**
     * Creates a new instance for the given node.
     */
    public ThreeCornerLoopLabelPositionGenerator(final SelfLoopNode slNode) {
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

        KVector cornerBend1 = cornerBends.get(0);
        KVector cornerBend2 = cornerBends.get(1);
        KVector cornerBend3 = cornerBends.get(2);
        
        SelfLoopLabel label = component.getSelfLoopLabel();
        List<SelfLoopLabelPosition> positions = label.getCandidatePositions();

        // Centered positions
        positions.add(shortSegmentCenteredPosition(label, firstBend, cornerBend1, startPort));
        positions.add(shortSegmentCenteredPosition(label, secondBend, cornerBend3, endPort));
        positions.add(longSegmentCenteredPosition(label, cornerBend1, cornerBend2, startPort));
        positions.add(longSegmentCenteredPosition(label, cornerBend3, cornerBend2, endPort));
        
        // Side aligned positions
        positions.add(shortSegmentAlignedPosition(label, firstBend, cornerBend1, startPort, false));
        positions.add(shortSegmentAlignedPosition(label, secondBend, cornerBend3, endPort, false));
        positions.add(longSegmentAlignedPosition(label, cornerBend1, cornerBend2, startPort, false));
        positions.add(longSegmentAlignedPosition(label, cornerBend3, cornerBend2, endPort, false));
        positions.add(shortSegmentAlignedPosition(label, firstBend, cornerBend1, startPort, true));
        positions.add(shortSegmentAlignedPosition(label, secondBend, cornerBend3, endPort, true));
        positions.add(longSegmentAlignedPosition(label, cornerBend1, cornerBend2, startPort, true));
        positions.add(longSegmentAlignedPosition(label, cornerBend3, cornerBend2, endPort, true));
    }

}
