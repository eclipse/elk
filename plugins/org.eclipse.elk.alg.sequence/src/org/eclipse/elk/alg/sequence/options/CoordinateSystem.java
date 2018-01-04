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
package org.eclipse.elk.alg.sequence.options;

/**
 * The available coordinate systems. The selected system influences how coordinates are computed.
 * 
 * @author cds
 */
public enum CoordinateSystem {

    /**
     * All coordinates are to be interpreted in the KGraph coordinate system. With this strategy, the
     * computed layout can be correctly drawn by KLighD.
     */
    KGRAPH,
    
    /**
     * Coordinates are computed such that the Papyrus sequence diagram layouter can make sense of them.
     */
    PAPYRUS;
    

    /**
     * Returns the enumeration value related to the given ordinal.
     * 
     * @param i
     *            ordinal value
     * @return the related enumeration value
     */
    public static CoordinateSystem valueOf(final int i) {
        return values()[i];
    }
    
}
