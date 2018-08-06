/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.calculators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNodeSide;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopOpposingSegment;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.loops.labeling.SelfLoopLabelPosition;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.Iterables;

/**
 *
 */
public final class SelfLoopOffsetCalculator {

    private static final double EDGE_DISTANCE = 10;
    private static final double LABEL_SPACING = 5;

    /**
     * No instantiation.
     */
    private SelfLoopOffsetCalculator() {
    }

    /**
     * Calculate the offset the components must not cross.
     */
    public static void calculatePortLabelOffsets(final SelfLoopNode slNode) {
        for (SelfLoopNodeSide side : slNode.getSides()) {
            ArrayList<SelfLoopComponent> sideComponentDependencies = new ArrayList<>(side.getComponentDependencies());
            double maximumLabelOffsetOfNode = 0;
            if (!sideComponentDependencies.isEmpty()) {
                maximumLabelOffsetOfNode =
                        calculatePortLabelOffsets(side, sideComponentDependencies, side.getMaximumPortLevel() + 1);
            }
            side.setMaximumLabelOffset(maximumLabelOffsetOfNode);
        }
    }

    /**
     * TODO Document.
     */
    private static double calculatePortLabelOffsets(final SelfLoopNodeSide side,
            final List<SelfLoopComponent> components, final int previousLevel) {

        double maximumOffsetForNextComponent = 0;
        double maximumOffsetOfComponent = 0;

        for (SelfLoopComponent component : components) {
            if (component.getPorts().size() > 1) {
                SelfLoopLabel label = component.getLabel();
                SelfLoopLabelPosition position = label.getRelativeLabelPosition();

                // get the components side level by finding a port from this side
                SelfLoopPort portOfSide = findPortOfSide(component, side.getSide());
                int level = portOfSide.getMaximumLevel();

                List<SelfLoopComponent> sideDependencies = component.getDependencyComponents().get(side);

                if (sideDependencies == null || sideDependencies.isEmpty()) {
                    // there are no dependencies for this component maximumOffsetOfComponent remains the same
                    if (level + 1 == previousLevel) {
                        SelfLoopEdge edge = Iterables.get(component.getConnectedEdges(), 0);
                        SelfLoopOpposingSegment segment = side.getOpposingSegments().get(edge);
                        if (position != null && position.getSide() == side.getSide() && segment == null) {
                            double offset = getSimpleLabelOffset(label, side.getSide());
                            maximumOffsetForNextComponent = Math.max(maximumOffsetForNextComponent, offset);
                        }
                    }

                } else {
                    double componentHeight = calculatePortLabelOffsets(side, sideDependencies, level);
                    maximumOffsetOfComponent = Math.max(maximumOffsetOfComponent, componentHeight);
                    if (level + 1 == previousLevel) {
                        maximumOffsetForNextComponent = Math.max(maximumOffsetForNextComponent,
                                getSimpleLabelOffset(label, side.getSide()) + maximumOffsetOfComponent);

                    }
                }

                final double direction = SplinesMath.portSideToDirection(side.getSide());
                if (position != null && side.getSide() == position.getSide()) {
                    position.getPosition().add(new KVector(direction).scale(maximumOffsetOfComponent));
                }
                
                for (SelfLoopPort port : component.getPorts()) {
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
    private static double getSimpleLabelOffset(final SelfLoopLabel label, final PortSide portSide) {
        if (portSide == PortSide.NORTH || portSide == PortSide.SOUTH) {
            double difference = label.getHeight() - EDGE_DISTANCE + LABEL_SPACING;
            return difference > 0 ? difference : 0;
        } else {
            double difference = label.getWidth() - EDGE_DISTANCE + LABEL_SPACING;
            return difference > 0 ? difference : 0;
        }
    }

    /**
     * Find a port of a component from a certain side or {@code null} if none could be found.
     */
    private static SelfLoopPort findPortOfSide(final SelfLoopComponent component, final PortSide side) {
        for (SelfLoopPort port : component.getPorts()) {
            if (port.getPortSide() == side) {
                return port;
            }
        }
        return null;
    }

    /**
     * TODO Document.
     */
    public static void calculateOpposingSegmentLabelOffsets(final SelfLoopNode nodeRep) {
        for (SelfLoopNodeSide side : nodeRep.getSides()) {
            List<SelfLoopOpposingSegment> segments =
                    new ArrayList<>(new HashSet<>(side.getOpposingSegments().values()));
            
            // sort segments descending by level
            segments.sort((seg1, seg2) -> Integer.compare(seg1.getLevel(), seg2.getLevel()));

            double previousOffset = side.getMaximumLabelOffset();
            double nextOffset = 0;
            for (SelfLoopOpposingSegment segment : segments) {
                // get component and it's potential label position
                SelfLoopComponent component = segment.getComponent();
                SelfLoopLabel label = component.getLabel();
                SelfLoopLabelPosition position = label.getRelativeLabelPosition();

                segment.setLabelOffset(previousOffset);

                // only positions that are placed at the same side as the segments are important
                if (position != null && position.getSide() == side.getSide()) {
                    final double direction = SplinesMath.portSideToDirection(side.getSide());
                    position.getPosition().add(new KVector(direction).scale(previousOffset));

                    // update
                    nextOffset = getSimpleLabelOffset(label, side.getSide());
                    previousOffset = previousOffset + nextOffset;
                }
            }

            side.setMaximumLabelOffset(Math.max(side.getMaximumLabelOffset(), nextOffset));
        }
    }

}
