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
import java.util.Map;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LGraphUtil;
import org.eclipse.elk.alg.layered.graph.LLabel;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.options.InternalProperties;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopComponent;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopLabel;
import org.eclipse.elk.alg.layered.p5edges.loops.SelfLoopNode;
import org.eclipse.elk.alg.layered.p5edges.loops.labeling.SelfLoopComponentMerger;
import org.eclipse.elk.alg.layered.p5edges.loops.labeling.SelfLoopLabelPosition;
import org.eclipse.elk.alg.layered.p5edges.loops.labeling.SelfLoopLabelPositionEvaluation;
import org.eclipse.elk.alg.layered.p5edges.loops.labeling.SelfLoopLabelPositionGeneration;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.IElkProgressMonitor;

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
 *     <dd>{@link SelfLoopPlacer}</dd>
 * </dl>
 */
public final class SelfLoopLabelPlacer implements ILayoutProcessor<LGraph> {

    @Override
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Self-Loop Label Placement", 1);

        for (Layer layer : layeredGraph.getLayers()) {
            for (LNode node : layer.getNodes()) {
                if (node.getType() == NodeType.NORMAL) {
                    SelfLoopNode slNode = node.getProperty(InternalProperties.SELFLOOP_NODE_REPRESENTATION);

                    // merge loops before generating the positions
                    if (layeredGraph.getProperty(LayeredOptions.EDGE_ROUTING_MERGE_SELF_LOOPS)) {
                        SelfLoopComponentMerger.mergeComponents(slNode);
                    }

                    // Generate possible positions for the labels
                    Map<SelfLoopComponent, List<SelfLoopLabelPosition>> positions =
                            SelfLoopLabelPositionGeneration.generatePositions(slNode);

                    if (!positions.isEmpty()) {
                        // Find the best position for each component
                        SelfLoopLabelPositionEvaluation.evaluatePositions(slNode, positions);
                        
                        // calculate the actual coordinates
                        placeLabels(slNode);
                    }
                }
            }
        }
        
        monitor.done();
    }

    /**
     * Apply the position information to the labels.
     */
    private void placeLabels(final SelfLoopNode slNode) {
        double labelLabelSpacing = LGraphUtil.getIndividualOrInherited(slNode.getNode(),
                LayeredOptions.SPACING_LABEL_LABEL);
        
        for (SelfLoopComponent component : slNode.getSelfLoopComponents()) {
            SelfLoopLabel slLabel = component.getLabel();
            if (slLabel == null || slLabel.getLabels().isEmpty()) {
                continue;
            }
            
            KVector position = slLabel.getRelativeLabelPosition().getPosition();
            
            for (LLabel lLabel : slLabel.getLabels()) {
                double xPos = position.x + (slLabel.getWidth() - lLabel.getSize().x) / 2;
                lLabel.getPosition().set(xPos, position.y);
                
                position.y += lLabel.getSize().y + labelLabelSpacing;
            }
        }
    }

}
