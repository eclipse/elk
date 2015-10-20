/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.layered.properties;

/**
 * Strategy to distribute wide nodes over multiple layers.
 * 
 * @author uru
 */
public enum WideNodesStrategy {

    /**
     * Splits wide nodes prior to the crossing minimization phase. Note that this can lead to
     * edge-node crossings.
     */
    AGGRESSIVE,
    /**
     * Splits wide nodes after the crossing minimization phase, guaranteeing that no edge-node
     * crossings are introduced.
     */
    CAREFUL,
    /** Do not handle wide nodes specially. */
    OFF

}
