/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p3order.constraints;

import java.util.List;

import org.eclipse.elk.alg.layered.graph.LNode;

/**
 * Detects and resolves violated constraints.
 */
public interface IConstraintResolver {

    /**
     * Detects and resolves violated constraints.
     * 
     * @param nodes
     *            the nodes sorted by their barycenter values
     */
    void processConstraints(final List<LNode> nodes);

}
