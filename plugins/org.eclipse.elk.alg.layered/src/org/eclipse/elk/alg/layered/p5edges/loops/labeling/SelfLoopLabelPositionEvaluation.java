/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.labeling;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.loops.calculators.SelfLoopOffsetCalculator;
import org.eclipse.elk.alg.layered.p5edges.splines.SplinesMath;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.PortSide;

/**
 * Choose a candidate position for each component label.
 */
public final class SelfLoopLabelPositionEvaluation {
    
    
    private static final double EDGE_DISTANCE = 10;
    /** Penalty applied for label-edge crossings. */
    private static final double LABEL_EDGE_CROSSING_PENALTY = 10.0;
    /** Penalty applied for label-label crossings. */
    private static final double LABEL_LABEL_CROSSING_PENALTY = 40.0;

    
    /**
     * No instantiation.
     */
    private SelfLoopLabelPositionEvaluation() {
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Position Evaluation
    
    /**
     * Chooses one {@link SelfLoopLabelPosition} for each self loop label with the aim of minimizing the penalties
     * that result from choosing those positions.
     */
    public static void evaluatePositions(final SelfLoopNode slNode,
            final Map<SelfLoopComponent, List<SelfLoopLabelPosition>> positionsComponentMap) {
        
        boolean debugMode = slNode.getNode().getGraph().getProperty(LayeredOptions.DEBUG_MODE);
        List<SelfLoopComponent> components = slNode.getSelfLoopComponents();
        
        if (debugMode) {
            KVector nodeSize = slNode.getNode().getSize();
            System.out.println("----------------------------------------");
            System.out.println("SETUP");
            System.out.println();
            System.out.println("Node (" + nodeSize.x + ", " + nodeSize.y + ")");
            
            for (SelfLoopComponent component : components) {
                if (!positionsComponentMap.containsKey(component)) {
                    continue;
                }
                
                SelfLoopLabel slLabel = component.getLabel();
                
                // Output component labels
                StringJoiner joiner = new StringJoiner(", ");
                for (LLabel llabel : component.getComponentLabels()) {
                    joiner.add(llabel.getText());
                }
                System.out.println(joiner.toString() + " (" + slLabel.getWidth() + ", " + slLabel.getHeight() + ")");
                
                // Output possible positions
                for (SelfLoopLabelPosition position : positionsComponentMap.get(component)) {
                    System.out.println("    " + position.getPosition().toString() + "     p " + position.getPenalty());
                }
            }
        }

        // initialize a random position constellation
        HashMap<SelfLoopComponent, SelfLoopLabelPosition> minimumConstellation =
                initializeConstellation(positionsComponentMap);

        // calculate the current penalty value
        double minimumPenalty = calculatePenalty(slNode, components, positionsComponentMap, debugMode);
        double previousPenalty = Double.MAX_VALUE;
        
        int run = 1;
        while (minimumPenalty < previousPenalty) {
            if (debugMode) {
                System.out.println();
                System.out.println();
                System.out.println("RUN " + run++);
                System.out.println();
                System.out.println("Previous Penalty: " + minimumPenalty);
                System.out.println();
            }
            
            previousPenalty = minimumPenalty;
            double currentMinimum = Double.MAX_VALUE;
            HashMap<SelfLoopComponent, SelfLoopLabelPosition> currentMinimumConstellation =
                    new HashMap<>(minimumConstellation);

            for (SelfLoopComponent component : components) {
                if (!positionsComponentMap.containsKey(component)) {
                    continue;
                }
                
                List<SelfLoopLabelPosition> positions = positionsComponentMap.get(component);

                if (debugMode) {
                    StringJoiner joiner = new StringJoiner(", ");
                    for (LLabel llabel : component.getComponentLabels()) {
                        joiner.add(llabel.getText());
                    }
                    System.out.println(joiner.toString());
                }

                // search for the minimum of this component
                for (SelfLoopLabelPosition currentPosition : positions) {
                    component.getLabel().setRelativeLabelPosition(currentPosition);
                    currentMinimumConstellation.put(component, currentPosition);

                    if (debugMode) {
                        System.out.println("    " + currentPosition.getPosition().toString());
                    }
                    
                    // calculate penalty for current constellation
                    currentMinimum = calculatePenalty(slNode, components, positionsComponentMap, debugMode);

                    if (debugMode) {
                        System.out.println("        Penalty: " + currentMinimum);
                    }

                    // update minimum
                    if (currentMinimum < minimumPenalty) {
                        if (debugMode) {
                            System.out.println("    -> chosen");
                        }
                        
                        minimumPenalty = currentMinimum;
                        minimumConstellation = new HashMap<>(currentMinimumConstellation);
                    }
                }

                component.getLabel().setRelativeLabelPosition(minimumConstellation.get(component));
            }

            if (debugMode) {
                System.out.println();
                System.out.println("New Penalty: " + minimumPenalty);
                System.out.println();
            }
        }

        if (debugMode) {
            System.out.println();
            System.out.println();
            System.out.println("RESULT");
            System.out.println();
            
            for (SelfLoopComponent component : positionsComponentMap.keySet()) {
                // Output component labels
                StringJoiner joiner = new StringJoiner(", ");
                for (LLabel llabel : component.getComponentLabels()) {
                    joiner.add(llabel.getText());
                }
                System.out.println(joiner.toString());
                
                // Output chosen position
                System.out.println("    -> " + minimumConstellation.get(component).getPosition().toString());
            }
        }
    }

    private static HashMap<SelfLoopComponent, SelfLoopLabelPosition> initializeConstellation(
            final Map<SelfLoopComponent, List<SelfLoopLabelPosition>> positionsComponentMap) {

        HashMap<SelfLoopComponent, SelfLoopLabelPosition> minimumConstellation = new HashMap<>();
        
        for (SelfLoopComponent component : positionsComponentMap.keySet()) {
            // take a "random" position from the components candidate positions
            List<SelfLoopLabelPosition> componentPositions = positionsComponentMap.get(component);
            SelfLoopLabelPosition currentPosition = componentPositions.get(0);
            component.getLabel().setRelativeLabelPosition(currentPosition);
            minimumConstellation.put(component, currentPosition);
        }
        
        return minimumConstellation;
    }

    private static double calculatePenalty(final SelfLoopNode slNode, final List<SelfLoopComponent> components,
            final Map<SelfLoopComponent, List<SelfLoopLabelPosition>> positionsComponentMap, final boolean debugMode) {

        double preferenceValueSum = 0;
        
        // reset the previous calculated offsets
        for (SelfLoopComponent component : components) {
            SelfLoopLabelPosition position = component.getLabel().getRelativeLabelPosition();
            position.resetPosition();
            preferenceValueSum += position.getPenalty();
        }

        // offset port heights and opposing segment heights
        SelfLoopOffsetCalculator.calculatePortLabelOffsets(slNode);
        SelfLoopOffsetCalculator.calculateOpposingSegmentLabelOffsets(slNode);

        // calculate the different penalties
        int labelLabelCrossings = calculateLabelLabelCrossings(components);
        int labelEdgeCrossings = calculateLabelEdgeCrossings(slNode, components);

        if (debugMode) {
            System.out.println("        Label-Label: " + labelLabelCrossings);
            System.out.println("        Label-Edge: " + labelEdgeCrossings);
        }
        
        return LABEL_EDGE_CROSSING_PENALTY * labelEdgeCrossings
                + LABEL_LABEL_CROSSING_PENALTY * labelLabelCrossings
                + preferenceValueSum;
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Crossing Calculation

    private static int calculateLabelLabelCrossings(final Collection<SelfLoopComponent> components) {
        // receive all candidate positions
        Set<SelfLoopComponent> nonVisitedComponents = new HashSet<>(components);
        int labelLabelCrossings = 0;

        // label-label crossings
        for (SelfLoopComponent component : components) {
            nonVisitedComponents.remove(component);
            for (SelfLoopComponent nextPos : nonVisitedComponents) {
                SelfLoopLabel firstLabel = component.getLabel();
                SelfLoopLabelPosition firstPosition = firstLabel.getRelativeLabelPosition();
                KVector firstPositionVector = firstPosition.getPosition();

                SelfLoopLabel secondLabel = nextPos.getLabel();
                SelfLoopLabelPosition secondPosition = secondLabel.getRelativeLabelPosition();
                KVector secondPositionVector = secondPosition.getPosition();

                if ((firstPositionVector.x <= secondPositionVector.x
                        && secondPositionVector.x < firstPositionVector.x + firstLabel.getWidth())
                        || (secondPositionVector.x <= firstPositionVector.x
                                && firstPositionVector.x < secondPositionVector.x + secondLabel.getWidth())) {

                    if ((firstPositionVector.y <= secondPositionVector.y
                            && secondPositionVector.y < firstPositionVector.y + firstLabel.getHeight())
                            || (secondPositionVector.y <= firstPositionVector.y
                                    && firstPositionVector.y < secondPositionVector.y + secondLabel.getHeight())) {
                        labelLabelCrossings++;
                    }
                }

            }

        }
        return labelLabelCrossings;
    }

    private static int calculateLabelEdgeCrossings(final SelfLoopNode nodeRep,
            final Collection<SelfLoopComponent> components) {
        
        int labelEdgeCrossings = 0;
        for (SelfLoopComponent component : components) {
            SelfLoopLabel label = component.getLabel();
            SelfLoopLabelPosition currentPosition = label.getRelativeLabelPosition();
            KVector currentPosVector = currentPosition.getPosition();
            PortSide positionSide = currentPosition.getSide();

            // calculate if an edge starting at the same side would cross the label position
            for (SelfLoopPort port : nodeRep.getNodeSide(positionSide).getPorts()) {
                boolean portCrossing = isCrossing(port, currentPosVector, label, positionSide);
                if (portCrossing) {
                    currentPosition.setLabelEdgeCrossings(currentPosition.getLabelEdgeCrossings() + 1);
                    labelEdgeCrossings++;
                }
            }
        }
        return labelEdgeCrossings;
    }

    private static boolean isCrossing(final SelfLoopPort port, final KVector currentPosition, final SelfLoopLabel label,
            final PortSide side) {
        
        KVector portPosition = port.getLPort().getPosition();
        double offset = port.getOtherEdgeOffset();
        double direction = SplinesMath.portSideToDirection(side);

        KVector sourcePos = portPosition.clone().add(port.getLPort().getAnchor());

        int sourceLevel = port.getMaximumLevel();
        double otherEdgeOffset = port.getOtherEdgeOffset();

        // calculate the two bend points
        KVector portBendPoint = sourcePos.clone().add(new KVector(direction).scale(sourceLevel * EDGE_DISTANCE));
        portBendPoint.add(new KVector(direction).scale(otherEdgeOffset));

        double width = label.getWidth();
        double height = label.getHeight();
        double labelWidthPoint = currentPosition.x + width;
        double labelHeightPoint = currentPosition.y + height;

        if (side == PortSide.NORTH || side == PortSide.SOUTH) {
            // TODO find fuzzy compare DoubleMath.fuzzyCompare(a, b, tolerance)
            if (currentPosition.x <= portPosition.x && portPosition.x <= labelWidthPoint) {
                if (labelHeightPoint > portBendPoint.y) {
                    return true;
                }
            }
            
        } else {
            if (currentPosition.y <= portPosition.y && portPosition.y <= labelHeightPoint) {
                if (labelWidthPoint < portBendPoint.x || offset == 0) {
                    return true;
                }
            }
        }
        
        return false;
    }

}
