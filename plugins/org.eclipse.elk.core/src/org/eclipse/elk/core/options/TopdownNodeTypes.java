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

/**
 * For general purpose topdown layout these node types specify how a node should be handled.
 * These properties only have an effect when {@link CoreOptions#TOPDOWN_LAYOUT} is set to true.
 * 
 * @author mka
 *
 */
public enum TopdownNodeTypes {
    
    /**
     * A parallel node is a node whose layout is not scaled down to fit a fixed size. The parallel node's own 
     * size must be set according to the pre-computed required size of the contained layout.
     */
    PARALLEL_NODE,
    
    /**
     * A hierarchical node is a node whose layout will be scaled down to fit the fixed size of the hierarchical node.
     * The fixed size of the node is defined by {@link CoreOptions#TOPDOWN_HIERARCHICAL_NODE_WIDTH} and 
     * {@link CoreOptions#TOPDOWN_HIERARCHICAL_NODE_ASPECT_RATIO}.
     */
    HIERARCHICAL_NODE,
    
    /**
     * The root node is like a parallel node, but it needs to be specifically marked, because for parallel nodes their
     * sizes are set during the layout of a hierarchical node and the root node has no parent hierarchical node. 
     * Therefore its size has to be set individually.
     */
    ROOT_NODE

}
