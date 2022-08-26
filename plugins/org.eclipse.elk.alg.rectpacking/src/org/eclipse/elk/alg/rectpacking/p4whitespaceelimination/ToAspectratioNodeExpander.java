/*******************************************************************************
 * Copyright (c) 2022 sdo and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.rectpacking.p4whitespaceelimination;

import org.eclipse.elk.alg.rectpacking.options.InternalProperties;
import org.eclipse.elk.alg.rectpacking.options.RectPackingOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * @author sdo
 *
 */
public class ToAspectratioNodeExpander extends EqualBetweenStructuresWhitespaceEliminator {

    @Override
    public void process(ElkNode graph, IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("To Aspect Ratio Whitesapce Eliminator", 1);
        double width = graph.getProperty(InternalProperties.DRAWING_WIDTH);
        double height = graph.getProperty(InternalProperties.DRAWING_HEIGHT);
        double desiredAspeceRatio = graph.getProperty(RectPackingOptions.ASPECT_RATIO);
        double additionalHeight = graph.getProperty(InternalProperties.ADDITIONAL_HEIGHT);
        double aspectRatio = width / height;
        if (aspectRatio < desiredAspeceRatio) {
            width = height * desiredAspeceRatio;
            graph.setProperty(InternalProperties.DRAWING_WIDTH, width);
        } else {
            additionalHeight += (width / desiredAspeceRatio) - height;
            graph.setProperty(InternalProperties.ADDITIONAL_HEIGHT, additionalHeight);
        }
        super.process(graph, progressMonitor);
    }

}
