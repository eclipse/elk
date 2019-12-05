/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.alg;

/**
 * Classes that implement this interface can create layout processors.
 * 
 * <p>
 * The usual way is to have an enumeration of all available processors implement this interface and instantiate the
 * correct one depending on which enumeration constant the method was called on.
 * </p>
 * 
 * @param <G>
 *            type of the graph the created processor will operate on.
 * @see ILayoutProcessor
 */
public interface ILayoutProcessorFactory<G> {

    /**
     * Returns an implementation of {@link ILayoutProcessor}. The actual implementation returned depends on the actual
     * type that implements this method.
     * 
     * @return new layout processor.
     */
    ILayoutProcessor<G> create();

}
