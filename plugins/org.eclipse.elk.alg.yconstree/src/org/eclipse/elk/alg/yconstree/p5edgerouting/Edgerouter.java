/*******************************************************************************
 * Copyright (c) 2023 claas and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.yconstree.p5edgerouting;

import org.eclipse.elk.alg.yconstree.InternalProperties;
import org.eclipse.elk.alg.yconstree.YconstreeLayoutPhases;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

/**
 * @author claas
 *
 */
public class Edgerouter implements ILayoutPhase<YconstreeLayoutPhases, ElkNode> {
    
    private IElkProgressMonitor pm;
    
    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutProcessor#process(java.lang.Object, org.eclipse.elk.core.util.IElkProgressMonitor)
     */
    @Override
    public void process(ElkNode graph, IElkProgressMonitor progressMonitor) {
        // TODO Auto-generated method stub
        pm = progressMonitor;
        pm.begin("EdgeRouter", 1);
        
        try {
            if (!graph.getChildren().isEmpty()){
                ElkNode parent = graph.getChildren().get(0);
                
                routeEdges(parent);
                setCanvas(graph);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        pm.done();
    }

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutPhase#getLayoutProcessorConfiguration(java.lang.Object)
     */
    @Override
    public LayoutProcessorConfiguration<YconstreeLayoutPhases, ElkNode> getLayoutProcessorConfiguration(ElkNode graph) {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    
    private void routeEdges(ElkNode node) {
        for (ElkEdge edge : ElkGraphUtil.allOutgoingEdges(node)) {
            ElkNode target = ElkGraphUtil.connectableShapeToNode(edge.getTargets().get(0));
            ElkEdgeSection section = ElkGraphUtil.firstEdgeSection(edge, true, true);
            
            
            double startX = node.getX() + node.getWidth() / 2;
            double startY = node.getY() + node.getHeight();
            double endX = target.getX() + target.getWidth() / 2;
            double endY = target.getY();
            
            section.setStartLocation(startX, startY);
            section.setEndLocation(endX, endY);
            routeEdges(target);
        }
    }
    
    private void setCanvas(ElkNode graph) {
        ElkNode parent = graph.getChildren().get(0);
        graph.setHeight(parent.getProperty(InternalProperties.MAX_Y) - parent.getProperty(InternalProperties.MIN_Y) + 20.0);
        graph.setWidth(parent.getProperty(InternalProperties.MAX_X) - parent.getProperty(InternalProperties.MIN_X) + 20.0);
    }

}
