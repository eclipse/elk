/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.tree;

import org.eclipse.elk.tree.graph.TGraph;

/**
 * A layout phase is a special kind of layout processor that encapsulates an implementation of one
 * of the algorithm's four main phases. A layout phase also specifies a configuration for the
 * intermediate layout processors that it wants to have executed in between layout phases (remember
 * possible dependencies).
 * 
 * @author sor
 * @author sgu
 * @author cds
 */
public interface ILayoutPhase extends ILayoutProcessor {

    /**
     * Returns the intermediate layout processors this phase depends on.
     * 
     * @param tGraph
     *            the tree graph to be processed. The configuration may vary depending on certain
     *            properties of the graph.
     * @return intermediate processing configuration. May be {@code null}.
     */
    IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(final TGraph tGraph);

}
