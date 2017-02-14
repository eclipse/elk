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
package org.eclipse.elk.alg.mrtree.options;

/**
 * Layout option for the choice of the weighting for the node order.
 *
 * @author sor
 * @author sgu
 */
public enum OrderWeighting {
    
    /** Chooses the number of descendants for the weighting. */
    DESCENDANTS,
    /** Chooses maximal number of descendants at the same level. */
    FAN;
    
}
