/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.loops.labeling;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

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
    public static void evaluatePositions(final SelfLoopNode slNode) {
        boolean debugMode = slNode.getNode().getGraph().getProperty(LayeredOptions.DEBUG_MODE);
        
        // We're only interested in components that actually have a label here
        List<SelfLoopComponent> components = slNode.getSelfLoopComponents().stream()
                .filter(c -> c.getSelfLoopLabel() != null)
                .collect(Collectors.toList());
        
        if (debugMode) {
            KVector nodeSize = slNode.getNode().getSize();
            System.out.println("----------------------------------------");
            System.out.println("SETUP");
            System.out.println();
            System.out.println("Node (" + nodeSize.x + ", " + nodeSize.y + ")");
            
            for (SelfLoopComponent component : components) {
                SelfLoopLabel slLabel = component.getSelfLoopLabel();
                
                // Output component labels
                StringJoiner joiner = new StringJoiner(", ");
                for (LLabel llabel : slLabel.getLabels()) {
                    joiner.add(llabel.getText());
                }
                System.out.println(joiner.toString() + " (" + slLabel.getWidth() + ", " + slLabel.getHeight() + ")");
                
                // Output possible positions
                for (SelfLoopLabelPosition position : slLabel.getCandidatePositions()) {
                    System.out.println("    " + position.getPosition().toString());
                    System.out.println("        Base penalty: " + position.getPenalty());
                    System.out.println("        Side: " + position.getSide());
                }
            }
        }

        // initialize a random position constellation
        assignDefaultPositions(components);

        // calculate the current penalty value
        double minPenalty = calculatePenalty(slNode, components, debugMode);
        double previousPenalty = Double.MAX_VALUE;
        
        int run = 1;
        while (minPenalty < previousPenalty) {
            if (debugMode) {
                System.out.println();
                System.out.println();
                System.out.println("RUN " + run++);
                System.out.println();
                System.out.println("Previous Penalty: " + minPenalty);
                System.out.println();
            }
            
            previousPenalty = minPenalty;
            double currMinimum = Double.MAX_VALUE;

            for (SelfLoopComponent component : components) {
                if (debugMode) {
                    StringJoiner joiner = new StringJoiner(", ");
                    for (LLabel llabel : component.getSelfLoopLabel().getLabels()) {
                        joiner.add(llabel.getText());
                    }
                    System.out.println(joiner.toString());
                }
                
                SelfLoopLabel slLabel = component.getSelfLoopLabel();
                List<SelfLoopLabelPosition> positions = slLabel.getCandidatePositions();
                SelfLoopLabelPosition currMinPosition = slLabel.getLabelPosition();

                // search for the minimum of this component
                for (SelfLoopLabelPosition currPosition : positions) {
                    slLabel.setLabelPosition(currPosition);

                    if (debugMode) {
                        System.out.println("    " + currPosition.getPosition().toString());
                    }
                    
                    // calculate penalty for current constellation
                    currMinimum = calculatePenalty(slNode, components, debugMode);

                    if (debugMode) {
                        System.out.println("        Penalty: " + currMinimum);
                    }

                    // update minimum
                    if (currMinimum < minPenalty) {
                        if (debugMode) {
                            System.out.println("    -> chosen");
                        }
                        
                        minPenalty = currMinimum;
                        currMinPosition = currPosition;
                    }
                }

                component.getSelfLoopLabel().setLabelPosition(currMinPosition);
            }

            if (debugMode) {
                System.out.println();
                System.out.println("New Penalty: " + minPenalty);
                System.out.println();
            }
        }

        if (debugMode) {
            System.out.println();
            System.out.println();
            System.out.println("RESULT");
            System.out.println();
            
            for (SelfLoopComponent component : components) {
                // Output component labels
                StringJoiner joiner = new StringJoiner(", ");
                for (LLabel llabel : component.getSelfLoopLabel().getLabels()) {
                    joiner.add(llabel.getText());
                }
                System.out.println(joiner.toString());
                
                // Output chosen position
                System.out.println("    -> "
                        + component.getSelfLoopLabel().getLabelPosition().getPosition().toString());
            }
        }
    }

    /**
     * Assigns each label to the first of its candidate positions.
     */
    private static void assignDefaultPositions(final List<SelfLoopComponent> components) {
        for (SelfLoopComponent component : components) {
            SelfLoopLabel slLabel = component.getSelfLoopLabel();
            
            assert slLabel != null;
            assert !slLabel.getCandidatePositions().isEmpty();
            
            slLabel.setLabelPosition(slLabel.getCandidatePositions().get(0));
        }
    }

    /**
     * Calculates the penalty of the current self loop label position assignment. This takes label-label and
     * label-edge crossings into account.
     */
    private static double calculatePenalty(final SelfLoopNode slNode, final List<SelfLoopComponent> components,
            final boolean debugMode) {

        double preferenceValueSum = 0;
        
        // reset the previous calculated offsets
        for (SelfLoopComponent component : components) {
            SelfLoopLabelPosition position = component.getSelfLoopLabel().getLabelPosition();
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
                SelfLoopLabel firstLabel = component.getSelfLoopLabel();
                SelfLoopLabelPosition firstPosition = firstLabel.getLabelPosition();
                KVector firstPositionVector = firstPosition.getPosition();

                SelfLoopLabel secondLabel = nextPos.getSelfLoopLabel();
                SelfLoopLabelPosition secondPosition = secondLabel.getLabelPosition();
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
            SelfLoopLabel label = component.getSelfLoopLabel();
            SelfLoopLabelPosition currentPosition = label.getLabelPosition();
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
