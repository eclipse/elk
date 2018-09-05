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
package org.eclipse.elk.alg.layered.options;

/**
 * Describes what interactive layout phases should take as reference point for comparison
 * of node positions.
 * 
 * @author cds
 */
public enum InteractiveReferencePoint {
    
    /** the node's center point. */
    CENTER,
    /** the node's top left corner. */
    TOP_LEFT;
    
}
