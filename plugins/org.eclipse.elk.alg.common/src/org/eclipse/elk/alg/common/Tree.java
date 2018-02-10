/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.common;

import java.util.List;

import com.google.common.collect.Lists;

/**
* A tree of T.
* @param <T> the type
*/
public class Tree<T> {
   // CHECKSTYLEOFF VisibilityModifier
   /** The contained node. */
   public final T node;
   /** The subtrees. */
   public List<Tree<T>> children;
   // CHECKSTYLEON VisibilityModifier
   
   /**
    * Constructor.
    * @param n the contained node
    */
   public Tree(final T n) {
       node = n;
       children = Lists.newArrayList();
   }
}