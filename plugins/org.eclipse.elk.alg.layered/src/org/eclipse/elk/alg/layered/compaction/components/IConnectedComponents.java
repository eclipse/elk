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
package org.eclipse.elk.alg.layered.compaction.components;

import java.util.List;

/**
 * A set of {@link IComponent}s. 
 * 
 * @param <N>
 *            the type of the contained nodes
 * @param <E>
 *            the type of the contained edges
 */
public interface IConnectedComponents<N, E> extends Iterable<IComponent<N, E>> {

    /**
     * @return the components
     */
    List<IComponent<N, E>> getComponents();

    /**
     * @return whether any of the components contains external extensions
     */
    boolean isContainsExternalExtensions();

}
