/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.rectpacking.p4whitespaceelimination;

import org.eclipse.elk.alg.rectpacking.RectPackingLayoutPhases;
import org.eclipse.elk.alg.rectpacking.options.InternalProperties;
import org.eclipse.elk.alg.rectpacking.options.RectPackingOptions;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * @author sdo
 *
 */
public class EqualBetweenStructuresWhitespaceEliminator implements ILayoutPhase<RectPackingLayoutPhases, ElkNode> {

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutProcessor#process(java.lang.Object, org.eclipse.elk.core.util.IElkProgressMonitor)
     */
    @Override
    public void process(ElkNode graph, IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Equal Whitespace Eliminator", 1);
//        if (expandNodes && expandToAspectRatio) {
//            double aspectRatio = totalWidth / height;
//            if (aspectRatio < this.aspectRatio) {
//                totalWidth = height * this.aspectRatio;
//            } else {
//                additionalHeight += (totalWidth / this.aspectRatio) - height;
//            }
//        }
        
        RectangleExpansion.expand(graph.getProperty(InternalProperties.ROWS),
                graph.getProperty(InternalProperties.DRAWING_WIDTH),
                graph.getProperty(InternalProperties.ADDITIONAL_HEIGHT),
                graph.getProperty(RectPackingOptions.SPACING_NODE_NODE));
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.elk.core.alg.ILayoutPhase#getLayoutProcessorConfiguration(java.lang.Object)
     */
    @Override
    public LayoutProcessorConfiguration<RectPackingLayoutPhases, ElkNode> getLayoutProcessorConfiguration(
            ElkNode graph) {
        return null;
    }

}
