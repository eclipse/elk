/*******************************************************************************
 * Copyright (c) 2023 claas and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.yconstree.p1treechecking;

import org.eclipse.elk.alg.yconstree.YconstreeLayoutPhases;
import org.eclipse.elk.core.alg.ILayoutPhase;
import org.eclipse.elk.core.alg.ILayoutPhaseFactory;
import org.eclipse.elk.graph.ElkNode;

/**
 * @author claas
 *
 */
public enum TreeCheckerStrategy implements ILayoutPhaseFactory<YconstreeLayoutPhases, ElkNode> {

    SIMPLE_TREECHECKING;

    @Override
    public ILayoutPhase<YconstreeLayoutPhases, ElkNode> create() {
        switch (this) {
        case SIMPLE_TREECHECKING:
            return new TreeChecker();

        default:
            throw new IllegalArgumentException(
                    "No implementation is available for the node orderer " + this.toString());
        }
    }

}