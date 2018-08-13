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
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

/**
 * Generates self loop label positions for same-side self loops.
 */
public class SideLoopLabelPositionGenerator extends AbstractSelfLoopLabelPositionGenerator {
    
    /**
     * Creates a new instance for the given node.
     */
    public SideLoopLabelPositionGenerator(final SelfLoopNode slNode) {
        super(slNode);
    }
    

    @Override
    public void generatePositions(final SelfLoopComponent component) {
        // Retrieve the spacings active for this node
        double edgeEdgeSpacing = getEdgeEdgeSpacing();
        double edgeLabelSpacing = getEdgeLabelSpacing();
        
        List<SelfLoopPort> ports = component.getPorts();
        SelfLoopPort startPort = ports.get(0);
        SelfLoopPort endPort = ports.get(ports.size() - 1);

        // The loop's start and end points
        KVector startPoint = startPort.getLPort().getPosition().clone().add(startPort.getLPort().getAnchor());
        KVector endPoint = endPort.getLPort().getPosition().clone().add(endPort.getLPort().getAnchor());

        // The first bend point (go out from start position in port side direction)
        KVector dirVectorStart = new KVector(SplinesMath.portSideToDirection(startPort.getPortSide()));
        KVector firstBend = startPoint.clone().add(dirVectorStart.clone().scale(
                (startPort.getMaximumLevel() * edgeEdgeSpacing) + edgeLabelSpacing));
        
        // The second bend point (go out from end position in port side direction)
        KVector dirVectorEnd = new KVector(SplinesMath.portSideToDirection(endPort.getPortSide()));
        KVector secondBend = endPoint.clone().add(dirVectorEnd.clone().scale(
                (endPort.getMaximumLevel() * edgeEdgeSpacing) + edgeLabelSpacing));

        // Generate the actual label candidate positions
        addPositions(component.getSelfLoopLabel(), firstBend, secondBend, startPort, endPort);
    }

    private void addPositions(final SelfLoopLabel label, final KVector firstBend,
            final KVector secondBend, final SelfLoopPort startPort, final SelfLoopPort endPort) {
        
        List<SelfLoopLabelPosition> positions = label.getCandidatePositions();

        KVector startPoint = startPort.getLPort().getPosition().clone().add(startPort.getLPort().getAnchor());
        KVector endPoint = endPort.getLPort().getPosition().clone().add(endPort.getLPort().getAnchor());
        
        // Main segment (short)
        positions.add(shortSegmentPosition(
                label, startPort, PortSide.NORTH, firstBend, secondBend, Alignment.CENTERED, false));
        positions.add(shortSegmentPosition(
                label, startPort, PortSide.NORTH, firstBend, secondBend, Alignment.LEFT_OR_TOP, false));
        positions.add(shortSegmentPosition(
                label, startPort, PortSide.NORTH, firstBend, secondBend, Alignment.RIGHT_OR_BOTTOM, false));
        
        // First segment (outer)
        positions.add(outerSegmentPosition(
                label, startPort.getPortSide(), PortSide.WEST, startPoint, firstBend, true, Alignment.CENTERED));
        positions.add(outerSegmentPosition(
                label, startPort.getPortSide(), PortSide.WEST, startPoint, firstBend, true, Alignment.LEFT_OR_TOP));
        positions.add(outerSegmentPosition(
                label, startPort.getPortSide(), PortSide.WEST, startPoint, firstBend, true, Alignment.RIGHT_OR_BOTTOM));
        
        // Last segment (outer)
        positions.add(outerSegmentPosition(
                label, endPort.getPortSide(), PortSide.EAST, endPoint, secondBend, false, Alignment.CENTERED));
        positions.add(outerSegmentPosition(
                label, endPort.getPortSide(), PortSide.EAST, endPoint, secondBend, false, Alignment.LEFT_OR_TOP));
        positions.add(outerSegmentPosition(
                label, endPort.getPortSide(), PortSide.EAST, endPoint, secondBend, false, Alignment.RIGHT_OR_BOTTOM));
    }
}
