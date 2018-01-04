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
package org.eclipse.elk.alg.sequence.p3layering;

import org.eclipse.elk.alg.layered.p2layers.NetworkSimplexLayerer;
import org.eclipse.elk.alg.sequence.SequencePhases;
import org.eclipse.elk.alg.sequence.graph.LayoutContext;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.LayoutProcessorConfiguration;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * Uses KLay Layered's {@link NetworkSimplexLayerer} to compute a layering for the messages in the
 * LGraph representation of a sequence diagram. This simply delegates to the network simplex layerer,
 * but needs to be in its own class because the network simplex layerer doesn't implement out layout
 * processor interface.
 * 
 * @author cds
 */
public final class MessageLayerer implements ILayoutPhase<SequencePhases, LayoutContext> {

    @Override
    public LayoutProcessorConfiguration<SequencePhases, LayoutContext> getLayoutProcessorConfiguration(
            final LayoutContext graph) {
        
        return LayoutProcessorConfiguration.create();
    }
    
    @Override
    public void process(final LayoutContext context, final IElkProgressMonitor progressMonitor) {
        NetworkSimplexLayerer layerer = new NetworkSimplexLayerer();
        layerer.process(context.lgraph, progressMonitor);
    }

}
