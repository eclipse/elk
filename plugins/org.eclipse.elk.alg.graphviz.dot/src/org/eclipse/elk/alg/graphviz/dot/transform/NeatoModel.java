/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.graphviz.dot.transform;

/**
 * Definition of Neato distance computation models.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public enum NeatoModel {
    
    /** Use length of the shortest path. */
    SHORTPATH,
    /** Use the circuit resistance model. */
    CIRCUIT,
    /** Use the subset model. */
    SUBSET;
    
    /**
     * Parse the given string into a Neato model, ignoring case.
     * 
     * @param string a string
     * @return the corresponding model
     */
    public static NeatoModel parse(final String string) {
        return valueOf(string.toUpperCase());
    }
    
    /**
     * Return the literal value as understood by Graphviz.
     * 
     * @return the literal value
     */
    public String literal() {
        return super.toString().toLowerCase();
    }
    
}
