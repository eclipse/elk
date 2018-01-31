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
package org.eclipse.elk.alg.sequence.p4sorting;

import java.util.List;

import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Lifeline sorting algorithm that respects the given order of the lifelines. The lifelines are
 * numbered as they are ordered before.
 */
public final class InteractiveLifelineSorter implements ILayoutPhase<SequencePhases, LayoutContext> {

    @Override
    public LayoutProcessorConfiguration<SequencePhases, LayoutContext> getLayoutProcessorConfiguration(
            final LayoutContext graph) {
        
        return LayoutProcessorConfiguration.create();
    }
    
    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Interactive lifeline sorting", 1);
        
        // Sort the lifelines by their x coordinates
        List<SLifeline> lifelines = context.sgraph.getLifelines();
        java.util.Collections.sort(lifelines);
        
        // Apply lifeline slots
        for (int i = 0; i < lifelines.size(); i++) {
            lifelines.get(i).setHorizontalSlot(i);
        }
        
        progressMonitor.done();
    }

}
