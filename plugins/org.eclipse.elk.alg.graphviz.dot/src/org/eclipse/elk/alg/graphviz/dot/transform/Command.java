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
 * Definition of available Graphviz commands.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public enum Command {
    
    /** invalid command. */
    INVALID,
    /** command for Dot layout. */
    DOT,
    /** command for Neato layout. */
    NEATO,
    /** command for Twopi layout. */
    TWOPI,
    /** command for Fdp layout. */
    FDP,
    /** command for Circo layout. */
    CIRCO;

    /**
     * Parse the given string into a command, ignoring case.
     * 
     * @param string a string
     * @return the corresponding command, or {@code INVALID} if the string
     *     does not hold a valid command
     */
    public static Command parse(final String string) {
        try {
            return valueOf(string.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return INVALID;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
    
}
