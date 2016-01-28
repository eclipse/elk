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
package org.eclipse.elk.alg.layered.p3order;

import java.util.List;

/**
 * Detects and resolves violated constraints.
 * 
 * @author cds
 * @author ima
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public interface IConstraintResolver {

    /**
     * Detects and resolves violated constraints.
     * 
     * @param nodeGroups
     *            the single-node vertices sorted by their barycenter values
     */
    void processConstraints(final List<NodeGroup> nodeGroups);

}
