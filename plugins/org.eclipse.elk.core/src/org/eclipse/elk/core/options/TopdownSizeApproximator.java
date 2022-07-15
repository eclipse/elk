/*******************************************************************************
 * Copyright (c) 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.core.options;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.graph.ElkNode;

/**
 * TODO: write javadoc
 */
public enum TopdownSizeApproximator {
    
    /**
     * TODO: write javadoc
     */
    COUNT_CHILDREN {
        @Override
        public KVector getSize(final ElkNode node) {
            // TODO: implement size approximator
            return new KVector(100, 100);
        }
        
    };
    
    /**
     * TODO: write javadoc
     * @param node
     * @return
     */
    public abstract KVector getSize(ElkNode node);

}
