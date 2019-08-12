/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.calculators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopLabelPosition;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNodeSide;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopOpposingSegment;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Iterables;

/**
 *
 */
public final class SelfLoopOffsetCalculator {

    /**
     * No instantiation.
     */
    private SelfLoopOffsetCalculator() {
    }

    /**
     * Calculate the offset the components must not cross.
     */
    public static void calculatePortLabelOffsets(final OldSelfLoopNode slNode) {
        // Retrieve proper spacings
        double edgeEdgeSpacing = LGraphUtil.getIndividualOrInherited(
                slNode.getNode(), LayeredOptions.SPACING_EDGE_EDGE);
        double edgeLabelSpacing = LGraphUtil.getIndividualOrInherited(
                slNode.getNode(), LayeredOptions.SPACING_EDGE_LABEL);
        
        for (OldSelfLoopNodeSide side : slNode.getSides()) {
            ArrayList<OldSelfLoopComponent> sideComponentDependencies = new ArrayList<>(side.getComponentDependencies());
            double maximumLabelOffsetOfNode = 0;
            if (!sideComponentDependencies.isEmpty()) {
                maximumLabelOffsetOfNode = calculatePortLabelOffsets(side, sideComponentDependencies,
                        side.getMaximumPortLevel() + 1, edgeEdgeSpacing, edgeLabelSpacing);
            }
            side.setMaximumLabelOffset(maximumLabelOffsetOfNode);
        }
    }

    /**
     * TODO Document.
     */
    private static double calculatePortLabelOffsets(final OldSelfLoopNodeSide side,
            final List<OldSelfLoopComponent> components, final int previousLevel, final double edgeEdgeSpacing,
            final double edgeLabelSpacing) {

        double maximumOffsetForNextComponent = 0;
        double maximumOffsetOfComponent = 0;

        for (OldSelfLoopComponent component : components) {
            if (component.getPorts().size() > 1) {
                OldSelfLoopLabel slLabel = component.getSelfLoopLabel();
                OldSelfLoopLabelPosition slLabelPos = slLabel == null ? null : slLabel.getLabelPosition();

                // get the components side level by finding a port from this side
                OldSelfLoopPort portOfSide = findPortOfSide(component, side.getSide());
                int level = portOfSide.getMaximumLevel();

                List<OldSelfLoopComponent> sideDependencies = component.getDependencyComponents().get(side);

                if (sideDependencies == null || sideDependencies.isEmpty()) {
                    // there are no dependencies for this component maximumOffsetOfComponent remains the same
                    if (level + 1 == previousLevel) {
                        OldSelfLoopEdge edge = Iterables.get(component.getConnectedEdges(), 0);
                        OldSelfLoopOpposingSegment segment = side.getOpposingSegments().get(edge);
                        if (slLabelPos != null && slLabelPos.getSide() == side.getSide() && segment == null) {
                            double offset = getSimpleLabelOffset(
                                    slLabel, side.getSide(), edgeEdgeSpacing, edgeLabelSpacing);
                            maximumOffsetForNextComponent = Math.max(maximumOffsetForNextComponent, offset);
                        }
                    }

                } else {
                    double componentHeight = calculatePortLabelOffsets(side, sideDependencies, level,
                            edgeEdgeSpacing, edgeLabelSpacing);
                    maximumOffsetOfComponent = Math.max(maximumOffsetOfComponent, componentHeight);
                    if (level + 1 == previousLevel) {
                        maximumOffsetForNextComponent = Math.max(maximumOffsetForNextComponent,
                                getSimpleLabelOffset(slLabel, side.getSide(), edgeEdgeSpacing, edgeLabelSpacing)
                                + maximumOffsetOfComponent);

                    }
                }

                final double direction = SplinesMath.portSideToDirection(side.getSide());
                if (slLabelPos != null && side.getSide() == slLabelPos.getSide()) {
                    slLabelPos.getPosition().add(new KVector(direction).scale(maximumOffsetOfComponent));
                }
                
                for (OldSelfLoopPort port : component.getPorts()) {
                    if (port.getPortSide() == side.getSide()) {
                        port.setOtherEdgeOffset(maximumOffsetOfComponent);
                    }
                }
            }
        }

        return maximumOffsetForNextComponent;
    }

    /**
     * TODO Document.
     */
    private static double getSimpleLabelOffset(final OldSelfLoopLabel slLabel, final PortSide portSide,
            final double edgeEdgeSpacing, final double edgeLabelSpacing) {
        
        double labelHeight = 0;
        double labelWidth = 0;
        
        if (slLabel != null) {
            labelHeight = slLabel.getSize().y;
            labelWidth = slLabel.getSize().x;
        }
        
        double difference;
        if (portSide == PortSide.NORTH || portSide == PortSide.SOUTH) {
            difference = labelHeight - edgeEdgeSpacing + edgeLabelSpacing;
        } else {
            difference = labelWidth - edgeEdgeSpacing + edgeLabelSpacing;
        }
        return Math.max(0, difference);
    }

    /**
     * Find a port of a component from a certain side or {@code null} if none could be found.
     */
    private static OldSelfLoopPort findPortOfSide(final OldSelfLoopComponent component, final PortSide side) {
        for (OldSelfLoopPort port : component.getPorts()) {
            if (port.getPortSide() == side) {
                return port;
            }
        }
        return null;
    }

    /**
     * TODO Document.
     */
    public static void calculateOpposingSegmentLabelOffsets(final OldSelfLoopNode slNode) {
        // Retrieve proper spacings
        double edgeEdgeSpacing = LGraphUtil.getIndividualOrInherited(
                slNode.getNode(), LayeredOptions.SPACING_EDGE_EDGE);
        double edgeLabelSpacing = LGraphUtil.getIndividualOrInherited(
                slNode.getNode(), LayeredOptions.SPACING_EDGE_LABEL);
        
        for (OldSelfLoopNodeSide side : slNode.getSides()) {
            List<OldSelfLoopOpposingSegment> segments =
                    new ArrayList<>(new HashSet<>(side.getOpposingSegments().values()));
            
            // sort segments descending by level
            segments.sort((seg1, seg2) -> Integer.compare(seg1.getLevel(), seg2.getLevel()));

            double previousOffset = side.getMaximumLabelOffset();
            double nextOffset = 0;
            for (OldSelfLoopOpposingSegment segment : segments) {
                // get component and it's potential label position
                OldSelfLoopComponent component = segment.getComponent();
                OldSelfLoopLabel slLabel = component.getSelfLoopLabel();

                segment.setLabelOffset(previousOffset);

                // only positions that are placed at the same side as the segments are important
                if (slLabel != null && slLabel.getLabelPosition().getSide() == side.getSide()) {
                    final double direction = SplinesMath.portSideToDirection(side.getSide());
                    slLabel.getLabelPosition().getPosition().add(new KVector(direction).scale(previousOffset));

                    // update
                    nextOffset = getSimpleLabelOffset(slLabel, side.getSide(), edgeEdgeSpacing, edgeLabelSpacing);
                    previousOffset = previousOffset + nextOffset;
                }
            }

            side.setMaximumLabelOffset(Math.max(side.getMaximumLabelOffset(), nextOffset));
        }
    }

}
