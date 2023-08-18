/*******************************************************************************
 * Copyright (c) 2023 claas and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.yconstree.p2yplacement;

import org.eclipse.elk.alg.yconstree.YconstreeLayoutPhases;
import org.eclipse.elk.alg.yconstree.options.YconstreeOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * @author claas
 *
 */
public class NodeYPlacer implements ILayoutPhase<YconstreeLayoutPhases, ElkNode> {
    
    private final double STANDARD_DISTANCE = 50.0;
    private IElkProgressMonitor pm;
    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutProcessor#process(java.lang.Object, org.eclipse.elk.core.util.IElkProgressMonitor)
     */
    @Override
    public void process(final ElkNode graph, IElkProgressMonitor progressMonitor) {
        // TODO Auto-generated method stub
        //elkGraph.setX(0.0);
        pm = progressMonitor;
        pm.begin("YPlacer", 1);
        
        try {
            if (!graph.getChildren().isEmpty()){
                ElkNode parent = graph.getChildren().get(0);
                setYLevels(parent,0.0);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        pm.done();
    }
    
    /**
     * A Method to set the absolute Y coordinates of the notes.
     * Uses a default distance between the notes.
     * @param node and it's children are getting updated Y-coords.
     * @param minHeight: Node gets minimum this height.
     */
    private void setYLevels(ElkNode node, double minHeight) {
        // TODO: Y-Level_Constraints, remove different heightlevels.
        if (node.hasProperty(YconstreeOptions.VERTICAL_CONSTRAINT)) {
            pm.log("hier hab ich einen Constraint");
            minHeight = node.getProperty(YconstreeOptions.VERTICAL_CONSTRAINT);
            //System.out.println("Hab was");
        }
        node.setY(minHeight);
        double newMinHeight = minHeight + STANDARD_DISTANCE + node.getHeight();
        for (int i = 0; i < node.getOutgoingEdges().size(); i++) {
            ElkNode child = (ElkNode) node.getOutgoingEdges().get(i).getTargets().get(0);
            setYLevels(child, newMinHeight);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutPhase#getLayoutProcessorConfiguration(java.lang.Object)
     */
    @Override
    public LayoutProcessorConfiguration<YconstreeLayoutPhases, ElkNode> getLayoutProcessorConfiguration(ElkNode graph) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
