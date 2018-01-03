/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.p4sorting;

/**
 * Definition of available lifeline sorting strategies for the sequence diagram layouter.
 * 
 * @author grh
 * @kieler.design proposed grh
 * @kieler.rating proposed yellow grh
 */
public enum LifelineSortingStrategy {

    /** Sort the lifelines according to their x-coordinates. */
    INTERACTIVE,
    
    /** Sort the lifelines according to the layers of the associated messages. */
    LAYER_BASED,
    
    /**
     * Sort the lifelines according to McAllisters solution for the linear arrangement problem that
     * minimizes the total length of messages.
     */
    SHORT_MESSAGES;

    
    /**
     * Returns the enumeration value related to the given ordinal.
     * 
     * @param i
     *            ordinal value
     * @return the related enumeration value
     */
    public static LifelineSortingStrategy valueOf(final int i) {
        return values()[i];
    }
    
}
