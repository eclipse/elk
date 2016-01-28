/*******************************************************************************
 * Copyright (c) 2013, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.tree.graph;

import org.eclipse.elk.graph.properties.MapPropertyHolder;

/**
 * Abstract superclass for the layers, nodes, ports, and edges of a tree graph.
 * 
 * @author sor
 * @author sgu
 */
public abstract class TGraphElement extends MapPropertyHolder {

    /** the serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Identifier value, may be arbitrarily used by algorithms. */
    public int id;  // SUPPRESS CHECKSTYLE VisibilityModifier

    /**
     * Create a graph element.
     * 
     * @param id the element identifier
     */
    public TGraphElement(final int id) {
        super();
        this.id = id;
    }

    /**
     * Implicit super constructor.
     * 
     */
    public TGraphElement() {
    }

}
