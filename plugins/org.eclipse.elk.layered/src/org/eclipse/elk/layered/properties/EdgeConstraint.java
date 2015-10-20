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
package org.eclipse.elk.layered.properties;

/**
 * Enumeration of edge constraints. Edge constraints can be set on ports to constrain the
 * type of edges incident to that port.
 * 
 * @see org.eclipse.elk.layered.intermediate.EdgeAndLayerConstraintEdgeReverser
 *        EdgeAndLayerConstraintEdgeReverser
 * @author cds
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public enum EdgeConstraint {
    
    /** no constraint on incident edges. */
    NONE,
    /** node may have only incoming edges. */
    INCOMING_ONLY,
    /** node may have only outgoing edges. */
    OUTGOING_ONLY;
    
}