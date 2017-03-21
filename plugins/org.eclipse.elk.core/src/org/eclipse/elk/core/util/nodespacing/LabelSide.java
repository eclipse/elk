/*******************************************************************************
 * Copyright (c) 2014, 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util.nodespacing;

import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Enumeration for the definition of a side of the edge to place the (edge) label to.
 * 
 * @author jjc
 */
public enum LabelSide {
    /** The label's placement side hasn't been decided yet. */
    UNKNOWN,
    /** The label is placed above the edge. */
    ABOVE,
    /** The label is placed below the edge. */
    BELOW;
    

    /**
     * Property set on edge and port labels by layout algorithms depending on which side they decide is
     * appropriate for any given label.
     */
    public static final IProperty<LabelSide> LABEL_SIDE = new Property<LabelSide>(
            "org.eclipse.elk.labelSide", LabelSide.UNKNOWN);
}
