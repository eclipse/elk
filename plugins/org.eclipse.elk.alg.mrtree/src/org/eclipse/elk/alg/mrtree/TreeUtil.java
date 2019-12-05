/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.mrtree;

import java.util.Collections;
import java.util.Iterator;

import org.eclipse.elk.alg.mrtree.graph.TNode;

import com.google.common.collect.Iterables;

/**
 * Utility class for Mr Tree.
 * 
 * @author sgu
 */
public final class TreeUtil {

    /**
     * Hidden constructor to avoid instantiation.
     */
    private TreeUtil() {
    }
    
    /**
     * This method returns the leftmost node at the deepest level. This is implemented using a
     * postorder walk of the subtree under given level.
     * 
     * @param currentlevel
     *            a list of nodes at level one
     * @return the leftmost node at the deepest level.
     */
    public static TNode getLeftMost(final Iterable<TNode> currentlevel) {
        return getLeftMost(currentlevel, -1);
    }

    /**
     * This method returns the leftmost node at the given level. This is implemented using a
     * postorder walk of the subtree under given level, depth levels down. Depth here refers to the
     * level below where the leftmost descendant is being found.
     * 
     * If given level is negative it returns the leftmost node at the deepest level.
     * 
     * @param currentlevel
     *            a list of nodes at level one
     * @param depth
     *            the depth to search for
     * @return the leftmost descendant at depth levels down
     */
    public static TNode getLeftMost(final Iterable<TNode> currentlevel, final int depth) {
        if (0 < Iterables.size(currentlevel)) {
            int d = depth;

            // the leftmost descendant at depth levels down
            if (1 < d) {
                d--;
                // build empty iterator
                Iterable<TNode> nextLevel = new Iterable<TNode>() {

                    public Iterator<TNode> iterator() {
                        return Collections.emptyIterator();
                    }
                };

                for (TNode cN : currentlevel) {
                    // append the children of the current node to the next level
                    nextLevel = Iterables.concat(nextLevel, cN.getChildren());
                }
                return getLeftMost(nextLevel, d);
            }

            // the leftmost node at the deepest level
            if (d < 0) {
                // build empty iterator
                Iterable<TNode> nextLevel = new Iterable<TNode>() {

                    public Iterator<TNode> iterator() {
                        return Collections.emptyIterator();
                    }
                };

                for (TNode cN : currentlevel) {
                    // append the children of the current node to the next level
                    nextLevel = Iterables.concat(nextLevel, cN.getChildren());
                }

                //
                if (0 < Iterables.size(nextLevel)) {
                    return getLeftMost(nextLevel, d);
                }
            }
        }
        // return the leftmost node at the current level
        return Iterables.getFirst(currentlevel, null);
    }

}
