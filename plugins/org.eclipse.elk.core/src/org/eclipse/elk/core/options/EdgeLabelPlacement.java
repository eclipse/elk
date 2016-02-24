/*******************************************************************************
 * Copyright (c) 2009, 2015 Kiel University and others.
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
 * Definition of placement positions for edge labels. To be accessed using
 * {@link CoreOptions#EDGE_LABEL_PLACEMENT}.
 * 
 * @kieler.design 2011-03-14 reviewed by cmot, cds
 * @kieler.rating yellow 2013-01-09 review KI-32 by ckru, chsch
 * @author msp
 */
public enum EdgeLabelPlacement {

    /** undefined label placement. */
    UNDEFINED,
    /** label is centered on the edge. */
    CENTER,
    /** label is at the head (target) of the edge. */
    HEAD,
    /** label is at the tail (source) of the edge. */
    TAIL;
    
}
