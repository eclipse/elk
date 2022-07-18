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
 * A size approximator is used to dynamically decide a size for a node to be used during topdown layout
 * of hierarchical nodes. This allows the use of a size approximation strategy to minimize white space
 * in the final result.
 */
public enum TopdownSizeApproximator {
    
    /**
     * Computes the square root of the number of children and uses that as a multiplier for the base size
     * of the node. Nodes with no children will have a resulting size of 0, which means any other factors
     * determining the size will be dominant. Uses {@link CoreOptions#TOPDOWN_HIERARCHICAL_NODE_WIDTH} and 
     * {@link CoreOptions#TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO} as the base size.
     */
    COUNT_CHILDREN {
        @Override
        public KVector getSize(final ElkNode node) {
            double size = node.getProperty(CoreOptions.TOPDOWN_HIERARCHICAL_NODE_WIDTH) 
                    * Math.sqrt(node.getChildren().size());
            return new KVector(size, size / node.getProperty(CoreOptions.TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO));
        }
        
    };
    
    /**
     * Returns an approximated required size for a given node.
     * @param node the node
     * @return the size as a vector
     */
    public abstract KVector getSize(ElkNode node);

}
