/*******************************************************************************
 * Copyright (c) 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered;

/**
 * All layout phase enumerations implement this interface to act as a factory for their layout
 * phases. The actual type of the instance returned depends on the enumeration constant this method
 * is called on. (This is why the {@link #create()} method doesn't take any arguments.)
 * 
 * <p>Note that there is no similar interface to create {@link ILayoutProcessor} instances. This is
 * because intermediate processors are all listed in a single enumeration, while layout phases are
 * distributed over five enumerations.</p>
 * 
 * @author cds
 * @kieler.design proposed by cds
 * @kieler.rating proposed yellow by cds
 */
public interface ILayoutPhaseFactory {
    
    /**
     * Returns an implementation of {@link ILayoutPhase}. The actual implementation returned depends
     * on the actual type that implements this method.
     * 
     * @return layout phase.
     */
    ILayoutPhase create();
    
}
