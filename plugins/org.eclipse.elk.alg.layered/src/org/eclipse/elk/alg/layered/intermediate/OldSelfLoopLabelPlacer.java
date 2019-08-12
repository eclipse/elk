/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.intermediate;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopLabelPosition;
import org.eclipse.elk.alg.layered.p5edges.oldloops.OldSelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.oldloops.labeling.SelfLoopComponentMerger;
import org.eclipse.elk.alg.layered.p5edges.oldloops.labeling.SelfLoopLabelPositionEvaluator;
import org.eclipse.elk.alg.layered.p5edges.oldloops.labeling.SelfLoopLabelPositionGeneration;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.util.IElkProgressMonitor;

import com.google.common.collect.Lists;

/**
 * Places self loop labels. To do so, the label placer starts by generating possible candidate positions for each self
 * loop label, each with a penalty to be paid when that position is actually chosen. It then chooses one candidate
 * position for each self loop label such that no labels will overlap and that the overall penalty is hopefully as low
 * as possible.
 * 
 * <dl>
 *   <dt>Preconditions:</dt>
 *     <dd>A layered graph.</dd>
 *     <dd>Self loops are routed.</dd>
 *   <dt>Postcondition:</dt>
 *     <dd>Labels of self loops are placed.</dd>
 *   <dt>Slots:</dt>
 *     <dd>Before phase 4.</dd>
 *   <dt>Same-slot dependencies:</dt>
 *     <dd>{@link OldSelfLoopPlacer}</dd>
 * </dl>
 */
public final class OldSelfLoopLabelPlacer implements ILayoutProcessor<LGraph> {

    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Self-Loop Label Placement", 1);
        
        Direction layoutDirection = layeredGraph.getProperty(LayeredOptions.DIRECTION);

        layeredGraph.getLayers().stream()
            .flatMap(layer -> layer.getNodes().stream())
            .filter(node -> node.getType() == NodeType.NORMAL)
            .filter(node -> node.hasProperty(InternalProperties.SELF_LOOP_NODE_REPRESENTATION))
            .forEach(node -> processNode(node, layoutDirection, monitor));
        
        monitor.done();
    }

    private void processNode(final LNode node, final Direction layoutDirection, final IElkProgressMonitor monitor) {
        OldSelfLoopNode slNode = node.getProperty(InternalProperties.SELF_LOOP_NODE_REPRESENTATION);
        assert slNode != null;

        // merge loops before generating the positions
        if (node.getGraph().getProperty(LayeredOptions.EDGE_ROUTING_MERGE_SELF_LOOPS)) {
            SelfLoopComponentMerger.mergeComponents(slNode);
        }

        // Generate possible positions for the labels. This will set the candidate positions field of each
        // component's self loop label.
        SelfLoopLabelPositionGeneration.generatePositions(slNode);

        // Find the best position for each component
        SelfLoopLabelPositionEvaluator evaluator = new SelfLoopLabelPositionEvaluator(slNode, monitor);
        evaluator.evaluatePositions(monitor);
        
        // Calculate the actual coordinates
        placeLabels(slNode, layoutDirection);
    }

    /**
     * Apply the position information to the labels.
     */
    private void placeLabels(final OldSelfLoopNode slNode, final Direction layoutDirection) {
        boolean verticalLayout = layoutDirection.isVertical();
        double labelLabelSpacing = LGraphUtil.getIndividualOrInherited(slNode.getNode(),
                LayeredOptions.SPACING_LABEL_LABEL);
        
        for (OldSelfLoopComponent component : slNode.getSelfLoopComponents()) {
            OldSelfLoopLabel slLabel = component.getSelfLoopLabel();
            if (slLabel == null) {
                continue;
            }
            
            if (verticalLayout) {
                placeLabelsForVerticalLayout(slLabel, labelLabelSpacing, layoutDirection);
            } else {
                placeLabelsForHorizontalLayout(slLabel, labelLabelSpacing);
            }
        }
    }
    
    private void placeLabelsForHorizontalLayout(final OldSelfLoopLabel slLabel, final double labelLabelSpacing) {
        OldSelfLoopLabelPosition slPosition = slLabel.getLabelPosition();
        KVector currCoordinates = slPosition.getPosition();
        
        for (LLabel lLabel : slLabel.getLabels()) {
            // The x position depends on the label alignment
            double xPos = currCoordinates.x;
            
            switch (slPosition.getLabelAlignment()) {
            case CENTERED:
                xPos += (slLabel.getSize().x - lLabel.getSize().x) / 2;
                break;
                
            case RIGHT:
                xPos += (slLabel.getSize().x - lLabel.getSize().x);
                break;
            }
            
            // Apply position
            lLabel.getPosition().set(xPos, currCoordinates.y);
            
            // Advance y position
            currCoordinates.y += lLabel.getSize().y + labelLabelSpacing;
        }
    }
    
    private void placeLabelsForVerticalLayout(final OldSelfLoopLabel slLabel, final double labelLabelSpacing,
            final Direction layoutDirection) {
        
        OldSelfLoopLabelPosition slPosition = slLabel.getLabelPosition();
        KVector currCoordinates = slPosition.getPosition();
        
        // Due to the way layout directions work, we need to pay attention to the order in which we place labels. While
        // we can simply place them as they come for the DOWN direction, doing the same for the UP direction will
        // reverse the label order in the final result. Thus, in that case we iterate over the reversed label list
        List<LLabel> labels = slLabel.getLabels();
        if (layoutDirection == Direction.UP) {
            labels = Lists.reverse(labels);
        }
        
        for (LLabel lLabel : labels) {
            // The y position depends on the label alignment
            double yPos = currCoordinates.y;
            
            switch (slPosition.getLabelAlignment()) {
            case CENTERED:
                yPos += (slLabel.getSize().y - lLabel.getSize().y) / 2;
                break;
                
            case LEFT:
                yPos += (slLabel.getSize().x - lLabel.getSize().x);
                break;
            }
            
            // Apply position
            lLabel.getPosition().set(currCoordinates.x, yPos);
            
            // Advance x position
            currCoordinates.x += lLabel.getSize().x + labelLabelSpacing;
        }
    }

}
