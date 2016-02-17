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
package org.eclipse.elk.core.comments;

import org.eclipse.elk.graph.KNode;

/**
 * Determines if a given comment is eligible for attachment to a graph element. There are types of
 * comments that can very well stand on their own, without any need for attachment. An eligibility
 * filter identifies such comments.
 * 
 * <p>
 * Note that if an implementation holds state, all resources should be released once
 * {@link #cleanup()} is called.
 * </p>
 * 
 * <p>
 * If clients implementing this interface don't need any preprocessing or cleanup, this interface
 * can be used as a functional interface.
 * </p>
 */
@FunctionalInterface
public interface IEligibilityFilter {
    
    /**
     * Checks if the given comment can be attached to a graph element.
     * 
     * @param comment
     *            the comment.
     * @return {@code true} if the comment can be attached to graph elements, {@code false} if the
     *         comment is meant to be standalone.
     */
    boolean eligibleForAttachment(KNode comment);
    
    /**
     * Does any preprocessing necessary. This method is called before the first invocation of
     * {@link #eligibleForAttachment(KNode)} for a given graph.
     * 
     * @implSpec The default implementation does nothing.
     * 
     * @param graph
     *            the graph.
     * @param includeHierarchy
     *            if {@code true}, the preprocessing is done not only on the current hierarchy
     *            level, but also on all sub levels. Implementations may choose to behave
     *            differently depending on this value.
     */
    default void preprocess(KNode graph, boolean includeHierarchy) {
    }
    
    /**
     * Does any cleaning necessary to get the implementation ready for the next comment attachment
     * run. This method is called after the last invocation of {@link #eligibleForAttachment(KNode)}
     * for a given graph.
     * 
     * @implSpec The default implementation does nothing.
     */
    default void cleanup() {
    }
    
}
