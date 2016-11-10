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

import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkNode;

/**
 * Knows which graph elements comments were explicitly attached to by the user. This of course only
 * makes sense for comment attachment in languages whose editors allow users to explicitly attach
 * comments to things.
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
public interface IExplicitAttachmentProvider {
    
    /**
     * Finds and returns the {@link ElkGraphElement} the given comment was explicitly attached to by
     * the user, if any.
     * 
     * @param comment
     *            the comment whose explicit attachment to find.
     * @return the graph element the comment is explicitly attached to, or {@code null} if there is
     *         none. If the graph element is not {@code null}, it must be in the same hierarchy
     *         level as the comment node.
     */
    ElkGraphElement findExplicitAttachment(ElkNode comment);
    
    /**
     * Does any preprocessing necessary. This method is called before the first invocation of
     * {@link #findExplicitAttachment(ElkNode)} for a given graph.
     * 
     * @implSpec
     * The default implementation does nothing.
     * 
     * @param graph
     *            the graph.
     * @param includeHierarchy
     *            if {@code true}, the preprocessing is done not only on the current hierarchy
     *            level, but also on all sub levels. Implementations may choose to behave
     *            differently depending on this value.
     */
    default void preprocess(ElkNode graph, boolean includeHierarchy) {
    }
    
    /**
     * Does any cleaning necessary to get the implementation ready for the next comment attachment
     * run. This method is called after the last invocation of {@link #boundsFor(KNode)} for a given
     * graph.
     * 
     * @implSpec The default implementation does nothing.
     */
    default void cleanup() {
    }
    
}
