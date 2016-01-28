/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered;

import org.eclipse.elk.alg.layered.graph.LGraph;

/**
 * A layout phase is a special kind of layout processor that encapsulates an
 * implementation of one of the algorithm's five main phases. A layout phase
 * also specifies a configuration for the intermediate layout processors that
 * it wants to have executed in between layout phases. (think dependencies)
 *
 * @see LayeredLayoutProvider
 * @author cds
 * @kieler.design 2012-08-10 chsch grh
 * @kieler.rating yellow 2014-11-09 review KI-56 by chsch, als
 */
public interface ILayoutPhase extends ILayoutProcessor {
    
    /**
     * Returns which intermediate processors this layout phase needs to have executed in which slot. The
     * result can vary depending on the properties of the given layered graph. This method is called
     * once for the given graph before it is actually laid out to assemble the list of processors
     * required to layout the graph. 
     * 
     * @param graph the layered graph to be processed. The configuration may vary depending on certain
     *              properties of the graph.
     * @return intermediate processing configuration. May be {@code null}.
     */
    IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(final LGraph graph);
    
}
