/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p5edges.oldloops.labeling;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LPort;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopEdge;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopLabelPosition;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopNodeSide;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopOpposingSegment;
import org.eclipse.elk.alg.layered.p5edges.oldloops.SelfLoopPort;
import org.eclipse.elk.alg.layered.p5edges.oldloops.calculators.SelfLoopOffsetCalculator;
import org.eclipse.elk.core.math.ElkRectangle;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Choose a candidate position for each component label. Upon instantiation, a default assignment is immediately
 * computed that assigns each label to its first candidate position. That assignment can be used as is. If we should
 * try to find a better assignment, call 
 * 
 * <p>
 * The algorithm starts by assigning each label to its first candidate position and calculating that assignment's
 * penalty, which is subject to the number of label-node, label-edge, and label-label crossings. We then start a number
 * of iterations. On each iteration, we assign each label to each candidate position and choose the position that
 * results in the minimum penalty (a rather greedy algorithm). The assignment of labels to candidate positions has an
 * influence on the way the self loops are routed, so crossings have to be recalculated on each iteration. We iterate as
 * long as our algorithm continues to find better assignments.
 * </p>
 * 
 * <p>
 * Note that it would seem sensible to remove candidate positions that overlap the node before running our assignment
 * algorithm. However, due to the fact that the assignment influences the routing of the edges, a candidate position
 * that starts out overlapping its node may actually end up being a viable choice later.
 * </p>
 * 
 * <p>
 * This class contains a high amount of debug output that can be switched on and might be removed at some point.
 * </p>
 */
public final class SelfLoopLabelPositionEvaluator {
    
    /** Penalty applied for label-node crossings. */
    private static final double LABEL_NODE_CROSSING_PENALTY = 100.0;
    /** Penalty applied for label-edge crossings. */
    private static final double LABEL_EDGE_CROSSING_PENALTY = 10.0;
    /** Penalty applied for label-label crossings. */
    private static final double LABEL_LABEL_CROSSING_PENALTY = 40.0;
    
    /** The node whose labels should be assigned to positions. */
    private final SelfLoopNode slNode;
    /** Those of the node's self loop components that actually have a label to be assigned. */
    private final List<SelfLoopComponent> components;
    /** The current assignment's penalty. */
    private double assignmentPenalty;
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction
    
    /**
     * Creates a new instance for the given node.
     *
     * @param slNode the node to evaluate self loop positions for.
     * @param monitor progress monitor for debug output.
     */
    public SelfLoopLabelPositionEvaluator(final SelfLoopNode slNode, final IElkProgressMonitor monitor) {
        this.slNode = slNode;
        
        // We're only interested in components that actually have a label here
        components = slNode.getSelfLoopComponents().stream()
                .filter(c -> c.getSelfLoopLabel() != null)
                .collect(Collectors.toList());

        // initialize a random position constellation and calculate its penalty
        assignDefaultPositions();
        assignmentPenalty = calculatePenalty(monitor);
    }

    /**
     * Assigns each label to the first of its candidate positions.
     */
    private void assignDefaultPositions() {
        for (SelfLoopComponent component : components) {
            SelfLoopLabel slLabel = component.getSelfLoopLabel();
            
            assert slLabel != null;
            assert !slLabel.getCandidatePositions().isEmpty();
            
            slLabel.setLabelPosition(slLabel.getCandidatePositions().get(0));
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Position Evaluation
    
    /**
     * Chooses one {@link SelfLoopLabelPosition} for each self loop label with the aim of minimizing the penalties
     * that result from choosing those positions.
     * 
     * @param monitor progress monitor for debug output.
     */
    public void evaluatePositions(final IElkProgressMonitor monitor) {
        if (monitor.isLoggingEnabled()) {
            KVector nodeSize = slNode.getNode().getSize();
            
            monitor.log("SETUP Node (" + nodeSize.x + ", " + nodeSize.y + ")");
            
            for (SelfLoopComponent component : components) {
                SelfLoopLabel slLabel = component.getSelfLoopLabel();
                
                // Output component labels
                StringJoiner joiner = new StringJoiner(", ");
                for (LLabel llabel : slLabel.getLabels()) {
                    joiner.add(llabel.getText());
                }
                monitor.log(joiner.toString() + " (" + slLabel.getSize().x + ", " + slLabel.getSize().y + ")");
                
                // Output possible positions
                for (SelfLoopLabelPosition position : slLabel.getCandidatePositions()) {
                    monitor.log("    " + position.getPosition().toString());
                    monitor.log("        Base penalty: " + position.getBasePenalty());
                    monitor.log("        Side: " + position.getSide());
                    monitor.log("        Label alignment: " + position.getLabelAlignment());
                }
            }
        }

        
        double previousPenalty = Double.MAX_VALUE;
        int run = 1;
        while (assignmentPenalty < previousPenalty) {
            if (monitor.isLoggingEnabled()) {
                monitor.log("RUN " + run++);
                monitor.log("Previous Penalty: " + assignmentPenalty);
            }
            
            previousPenalty = assignmentPenalty;
            double currMinimum = Double.MAX_VALUE;

            for (SelfLoopComponent component : components) {
                if (monitor.isLoggingEnabled()) {
                    StringJoiner joiner = new StringJoiner(", ");
                    for (LLabel llabel : component.getSelfLoopLabel().getLabels()) {
                        joiner.add(llabel.getText());
                    }
                    monitor.log(joiner.toString());
                }
                
                SelfLoopLabel slLabel = component.getSelfLoopLabel();
                List<SelfLoopLabelPosition> positions = slLabel.getCandidatePositions();
                SelfLoopLabelPosition currMinPosition = slLabel.getLabelPosition();

                // search for the minimum of this component
                for (SelfLoopLabelPosition currPosition : positions) {
                    slLabel.setLabelPosition(currPosition);

                    if (monitor.isLoggingEnabled()) {
                        monitor.log("    " + currPosition.getPosition().toString());
                    }
                    
                    // calculate penalty for current constellation
                    currMinimum = calculatePenalty(monitor);

                    if (monitor.isLoggingEnabled()) {
                        monitor.log("        Penalty: " + currMinimum);
                    }

                    // update minimum
                    if (currMinimum < assignmentPenalty) {
                        if (monitor.isLoggingEnabled()) {
                            monitor.log("    -> chosen");
                        }
                        
                        assignmentPenalty = currMinimum;
                        currMinPosition = currPosition;
                    }
                }

                component.getSelfLoopLabel().setLabelPosition(currMinPosition);
            }

            if (monitor.isLoggingEnabled()) {
                monitor.log("New Penalty: " + assignmentPenalty);
            }
        }

        if (monitor.isLoggingEnabled()) {
            monitor.log("RESULT");
            
            for (SelfLoopComponent component : components) {
                // Output component labels
                StringJoiner joiner = new StringJoiner(", ");
                for (LLabel llabel : component.getSelfLoopLabel().getLabels()) {
                    joiner.add(llabel.getText());
                }
                monitor.log(joiner.toString());
                
                // Output chosen position
                monitor.log("    -> "
                        + component.getSelfLoopLabel().getLabelPosition().getPosition().toString());
            }
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Penalty Calculation

    /**
     * Calculates the penalty of the current self loop label position assignment. This takes label-label and
     * label-edge crossings into account.
     */
    private double calculatePenalty(final IElkProgressMonitor monitor) {
        double totalBasePenalty = 0;
        
        // Reset the previous calculated coordinates since the current assignment of labels to candidate positions
        // may have changed where the self loops are actually routed
        for (SelfLoopComponent component : components) {
            SelfLoopLabelPosition position = component.getSelfLoopLabel().getLabelPosition();
            position.resetPosition();
            totalBasePenalty += position.getBasePenalty();
        }

        // Offset port heights and opposing segment heights
        SelfLoopOffsetCalculator.calculatePortLabelOffsets(slNode);
        SelfLoopOffsetCalculator.calculateOpposingSegmentLabelOffsets(slNode);
        
        // Calculate the different penalties
        List<ElkRectangle> labelRects = createLabelPositionRects();
        
        int labelNodeCrossings = calculateLabelNodeCrossings(labelRects);
        int labelLabelCrossings = calculateLabelLabelCrossings(labelRects);
        int labelEdgeCrossings = calculateLabelEdgeCrossings();

        if (monitor.isLoggingEnabled()) {
            monitor.log("        Label-Node: " + labelNodeCrossings);
            monitor.log("        Label-Label: " + labelLabelCrossings);
            monitor.log("        Label-Edge: " + labelEdgeCrossings);
        }
        
        return LABEL_NODE_CROSSING_PENALTY * labelNodeCrossings
                + LABEL_EDGE_CROSSING_PENALTY * labelEdgeCrossings
                + LABEL_LABEL_CROSSING_PENALTY * labelLabelCrossings
                + totalBasePenalty;
    }
    
    /**
     * Returns a list that contains a rectangle that describes the current label position of each component's label.
     */
    private List<ElkRectangle> createLabelPositionRects() {
        // Turn each chosen label position into a rectangle so we can easily check for overlaps
        List<ElkRectangle> labelRects = new ArrayList<>(components.size());
        
        for (SelfLoopComponent comp : components) {
            SelfLoopLabel slLabel = comp.getSelfLoopLabel();
            SelfLoopLabelPosition slPos = slLabel.getLabelPosition();
            
            ElkRectangle labelRect = new ElkRectangle(
                    slPos.getPosition().x,
                    slPos.getPosition().y,
                    slLabel.getSize().x,
                    slLabel.getSize().y);
            labelRects.add(labelRect);
        }
        
        return labelRects;
    }

    /**
     * Computes the number of label-node crossings for the chosen label positions of the given list of components.
     */
    private int calculateLabelNodeCrossings(final List<ElkRectangle> labelRects) {
        // Construct a rectangle that represents the node itself
        KVector nodeSize = slNode.getNode().getSize();
        ElkRectangle nodeRect = new ElkRectangle(0, 0, nodeSize.x, nodeSize.y);
        
        int labelNodeCrossings = 0;
        for (ElkRectangle labelRect : labelRects) {
            if (nodeRect.intersects(labelRect)) {
                labelNodeCrossings++;
            }
        }
        
        return labelNodeCrossings;
    }

    /**
     * Computes the number of label-label crossings for the chosen label positions of the given list of components.
     * Each crossing is only counted once.
     */
    private int calculateLabelLabelCrossings(final List<ElkRectangle> labelRects) {
        // We count each label-label crossing once by iterating over the rectangles and, for each rectangle, iterating
        // over the remaining rectangles to check whether they overlap
        int labelLabelCrossings = 0;
        for (int currIdx = 0; currIdx < labelRects.size(); currIdx++) {
            ElkRectangle currRect = labelRects.get(currIdx);
            
            for (int otherIdx = currIdx + 1; otherIdx < labelRects.size(); otherIdx++) {
                if (currRect.intersects(labelRects.get(otherIdx))) {
                    labelLabelCrossings++;
                }
            }
        }
        
        return labelLabelCrossings;
    }

    /**
     * Computes the number of labels that cross an edge they don't belong to.
     */
    private int calculateLabelEdgeCrossings() {
        int labelEdgeCrossings = 0;
        
        for (SelfLoopComponent component : components) {
            SelfLoopLabel slLabel = component.getSelfLoopLabel();
            SelfLoopLabelPosition slLabelPos = slLabel.getLabelPosition();

            // calculate if an edge starting at the same side would cross the label position
            for (SelfLoopPort slPort : slNode.getNodeSide(slLabelPos.getSide()).getPorts()) {
                // The port must not belong to the same component and we must detect a crossing
                if (slPort.getComponent() != component && isCrossing(slLabel, component, slPort)) {
                    labelEdgeCrossings++;
                }
            }
        }
        
        return labelEdgeCrossings;
    }
    
    
    private boolean isCrossing(final SelfLoopLabel slLabel, final SelfLoopComponent slLabelComponent,
            final SelfLoopPort slPort) {
        
        // Compute the routing level of the edge the label labels, if possible
        int labelEdgeLevel = computeEdgeLevel(slLabel, slLabelComponent);
        if (labelEdgeLevel < 0) {
            return false;
        }
        
        // Compute the maximum edge level of any edge incident to the port
        int otherEdgeLevel = slPort.getMaximumLevel();
        
        // There can only be a crossing if the label's edge level is below the level the edges leaving that port are
        // heading towards, crossing the poor label on their way there
        if (otherEdgeLevel <= labelEdgeLevel) {
            return false;
        }
        
        // Construct a rectangle that describes the label position
        SelfLoopLabelPosition slLabelPos = slLabel.getLabelPosition();
        ElkRectangle labelRect = new ElkRectangle(
                slLabelPos.getPosition().x,
                slLabelPos.getPosition().y,
                slLabel.getSize().x,
                slLabel.getSize().y);
        
        // Compute the port's anchor position
        LPort lPort = slPort.getLPort();
        KVector portPos = lPort.getPosition().clone().add(lPort.getAnchor());
        
        // Whether there will be an actual overlap depends on whether the port is in the label's area
        switch (slPort.getPortSide()) {
        case NORTH:
        case SOUTH:
            return labelRect.x < portPos.x && portPos.x < labelRect.getMaxX();
            
        case EAST:
        case WEST:
            return labelRect.y < portPos.y && portPos.y < labelRect.getMaxY();
        }
        
        // These statements should be unreachable
        assert false;
        return false;
    }

    /**
     * Tries to compute the edge level of the edge the given label belongs to. This method is trying to be really
     * defensive about the values it deems sensible since all this level stuff seems rather intricate.
     */
    private int computeEdgeLevel(final SelfLoopLabel slLabel, final SelfLoopComponent slComponent) {
        // Check if the label is on the same side as one of the component's ports
        for (SelfLoopPort slPort : slComponent.getPortsOfSide(slLabel.getLabelPosition().getSide())) {
            int maxLevel = slPort.getMaximumLevel();
            
            if (maxLevel >= 0) {
                return maxLevel;
            }
        }
        
        // The label must be one one of the component's segments, which we can reach through the component's edges
        SelfLoopNodeSide slSide = slNode.getNodeSide(slLabel.getLabelPosition().getSide());
        
        int maxSegmentLevel = -1;
        for (SelfLoopEdge slEdge : slComponent.getConnectedEdges()) {
            // Check whether the edge has a segment on the given node side
            SelfLoopOpposingSegment slSegment = slSide.getOpposingSegments().get(slEdge);
            
            if (slSegment != null) {
                maxSegmentLevel = Math.max(maxSegmentLevel, slSegment.getLevel());
            }
        }
        
        return maxSegmentLevel;
    }

}
