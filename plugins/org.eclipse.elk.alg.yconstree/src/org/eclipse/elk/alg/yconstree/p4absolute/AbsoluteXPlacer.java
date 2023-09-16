/*******************************************************************************
 * Copyright (c) 2023 claas and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.yconstree.p4absolute;

import org.eclipse.elk.alg.yconstree.InternalProperties;
import org.eclipse.elk.alg.yconstree.YconstreeLayoutPhases;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * @author claas
 *
 */
public class AbsoluteXPlacer implements ILayoutPhase<YconstreeLayoutPhases, ElkNode> {
    
    private IElkProgressMonitor pm;
    
    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutProcessor#process(java.lang.Object, org.eclipse.elk.core.util.IElkProgressMonitor)
     */
    @Override
    public void process(ElkNode graph, IElkProgressMonitor progressMonitor) {
        // TODO Auto-generated method stub
        pm = progressMonitor;
        pm.begin("AbsolutPlacer", 1);
        
        try {
            if (!graph.getChildren().isEmpty()){
                ElkNode parent = graph.getProperty(InternalProperties.ROOT_NODE);
                
                // first, move the root
                parent.setX(parent.getX() - findMinimalX(parent));
                // a little offset
                parent.setX(parent.getX() + 10.0);
                // now we update the whole tree to absolute X 
                absoluteTreeCoords(parent);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        pm.done();
        
    }
    
    
    private double findMinimalX(ElkNode t) {
        int numOfChildren = t.getOutgoingEdges().size();
        if (numOfChildren == 0) {
            return t.getX();
        } else {
            double minSubtreeX = 0.0;
            double testX = 0.0;
            for (int i = 0; i < numOfChildren; i++) {
                testX = findMinimalX((ElkNode) t.getOutgoingEdges().get(i).getTargets().get(0));
                minSubtreeX = (testX < minSubtreeX) ? testX : minSubtreeX;
            }
            return minSubtreeX + t.getX();
        }
    }
    
    private void absoluteTreeCoords(ElkNode t) {
        int numOfChildren = t.getOutgoingEdges().size();
        // a little offset
        t.setY(t.getY() + 10.0);
        if (numOfChildren > 0) {
            ElkNode child;
            for (int i = 0; i < numOfChildren; i++) {
                child = (ElkNode) t.getOutgoingEdges().get(i).getTargets().get(0);
                child.setX(child.getX() + t.getX());
                absoluteTreeCoords(child);
            }
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
