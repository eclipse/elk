/*******************************************************************************
 * Copyright (c) 2023 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.vertiflex.p5edgerouting;

import org.eclipse.elk.alg.vertiflex.InternalProperties;
import org.eclipse.elk.alg.vertiflex.VertiFlexLayoutPhases;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.eclipse.emf.common.util.EList;

/**
 * An edge router that draws straight edges between nodes.
 *
 */
public class Edgerouter implements ILayoutPhase<VertiFlexLayoutPhases, ElkNode> {
    
    private IElkProgressMonitor myProgressMonitor;
    
    
    @Override
    public void process(final ElkNode graph, final IElkProgressMonitor progressMonitor) {
        myProgressMonitor = progressMonitor;
        myProgressMonitor.begin("EdgeRouter", 1);
        
        if (!graph.getChildren().isEmpty()) {
            ElkNode parent = graph.getProperty(InternalProperties.ROOT_NODE);
            
            routeEdges(parent);
            setCanvas(graph);
        }

        myProgressMonitor.done();
    }

    @Override
    public LayoutProcessorConfiguration<VertiFlexLayoutPhases, ElkNode> getLayoutProcessorConfiguration(
            final ElkNode graph) {
        return null;
    }
    
    
    
    private void routeEdges(final ElkNode node) {
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
    
    
    private void setCanvas(final ElkNode graph) {
        ElkNode parent = graph.getChildren().get(0);
        graph.setHeight(parent.getProperty(InternalProperties.MAX_Y) 
                - parent.getProperty(InternalProperties.MIN_Y) + 20.0); //TODO remove magic numbers
        graph.setWidth(parent.getProperty(InternalProperties.MAX_X) 
                - parent.getProperty(InternalProperties.MIN_X) + 20.0);
    }

}
