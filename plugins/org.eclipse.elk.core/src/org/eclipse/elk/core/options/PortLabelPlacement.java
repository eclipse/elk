/*******************************************************************************
 * Copyright (c) 2012, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.options;

/**
 * Definition of port label placement strategies. The chosen strategy determines whether port
 * labels are placed inside or outside of the respective node.
 *
 * @author jjc
 */
public enum PortLabelPlacement {
    
    /** Port labels are placed outside of the node, beside the edge. */
    OUTSIDE,
    /** Port labels are placed inside of the node, next to the port. */
    INSIDE,
    /** Port labels are left on the position the user chose. */
    FIXED;

}
