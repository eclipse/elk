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
 * Enumeration of in-layer constraint types. In-layer constraints divide a layer into three
 * parts: the normal part, a top part and a bottom part. This constraint can be set on nodes
 * to define in which part they may appear.
 * 
 * @see org.eclipse.elk.layered.intermediate.InLayerConstraintProcessor
 *        InLayerConstraintProcessor
 * @author cds
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public enum InLayerConstraint {
    
    /** no constraint on in-layer placement. */
    NONE,
    /** float node to the top of the layer, along with other nodes posessing this constraint. */
    TOP,
    /** float node to the bottom of the layer, along with other nodes posessing this constraint. */
    BOTTOM;
    
}