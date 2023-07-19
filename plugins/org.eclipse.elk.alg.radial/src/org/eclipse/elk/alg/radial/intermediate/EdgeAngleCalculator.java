/*******************************************************************************
 * Copyright (c) 2023 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.radial.intermediate;

import org.eclipse.elk.alg.radial.InternalProperties;
import org.eclipse.elk.alg.radial.options.RadialOptions;
import org.eclipse.elk.core.alg.ILayoutProcessor;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;

/**
 * Calculates the angles of outgoing edges so that they can be used as an input by subsequent child 
 * layouts. Only makes sense when used in a top-down layout.
 *
 */
public class EdgeAngleCalculator implements ILayoutProcessor<ElkNode> {

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutProcessor#process(java.lang.Object, org.eclipse.elk.core.util.IElkProgressMonitor)
     */
    @Override
    public void process(ElkNode graph, IElkProgressMonitor progressMonitor) {

        ElkNode root = graph.getProperty(InternalProperties.ROOT_NODE);
        for (ElkEdge edge : root.getOutgoingEdges()) {
            
            KVector start = new KVector(edge.getSections().get(0).getStartX(), edge.getSections().get(0).getStartY());
            KVector end = new KVector(edge.getSections().get(0).getEndX(), edge.getSections().get(0).getEndY());
            
            KVector edgeVector = end.add(start.scale(-1, -1));
            double angle = Math.atan2(edgeVector.y, edgeVector.x);
            
            edge.getTargets().get(0).setProperty(RadialOptions.TARGET_ANGLE, angle);
        }

    }

}
