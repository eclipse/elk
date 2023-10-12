/*******************************************************************************
 * Copyright (c) 2023 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.yconstree.p2yplacement;

import org.eclipse.elk.alg.yconstree.InternalProperties;
import org.eclipse.elk.alg.yconstree.YconstreeLayoutPhases;
import org.eclipse.elk.alg.yconstree.options.YconstreeOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * Node placer to position nodes vertically. Nodes that have a vertical constraint are placed according to that 
 * constraint and other nodes are positioned automatically according to their position in the tree.
 *
 */
public class NodeYPlacer implements ILayoutPhase<YconstreeLayoutPhases, ElkNode> {
    
    // TODO change this to a property
    private final double STANDARD_DISTANCE = 50.0;
    private IElkProgressMonitor myProgressMonitor;
    
    @Override
    public void process(final ElkNode graph, final IElkProgressMonitor progressMonitor) {
        //elkGraph.setX(0.0);
        myProgressMonitor = progressMonitor;
        myProgressMonitor.begin("YPlacer", 1);
        
        try {
            if (!graph.getChildren().isEmpty()) {
                ElkNode parent = graph.getProperty(InternalProperties.ROOT_NODE);
                setYLevels(parent, 0.0);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //      Handle this exception properly
            e.printStackTrace();
        }
        
        myProgressMonitor.done();
    }
    
    /**
     * A Method to set the absolute Y coordinates of the notes.
     * Uses a default distance between the notes.
     * @param node and it's children are getting updated Y-coords.
     * @param minHeight: Node gets minimum this height.
     */
    private void setYLevels(final ElkNode node, double minHeight) {
        // TODO figure out what below todo means and whether it is still relevant
        // TODO: Y-Level_Constraints, remove different heightlevels.
        if (node.hasProperty(YconstreeOptions.VERTICAL_CONSTRAINT)) {
            myProgressMonitor.log("hier hab ich einen Constraint");
            minHeight = node.getProperty(YconstreeOptions.VERTICAL_CONSTRAINT);
        }
        node.setY(minHeight);
        double newMinHeight = minHeight + STANDARD_DISTANCE + node.getHeight();
        for (int i = 0; i < node.getOutgoingEdges().size(); i++) {
            ElkNode child = (ElkNode) node.getOutgoingEdges().get(i).getTargets().get(0);
            setYLevels(child, newMinHeight);
        }
    }

    @Override
    public LayoutProcessorConfiguration<YconstreeLayoutPhases, ElkNode> 
                getLayoutProcessorConfiguration(final ElkNode graph) {
        return null;
    }
    
}
