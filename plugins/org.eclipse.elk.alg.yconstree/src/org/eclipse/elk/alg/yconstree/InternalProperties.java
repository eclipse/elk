/*******************************************************************************
 * Copyright (c) 2023 claas and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *******************************************************************************/
package org.eclipse.elk.alg.yconstree;

import org.eclipse.elk.alg.yconstree.p3relative.OutlineNode;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.PortConstraints;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * @author claas
 *
 */
public class InternalProperties {
    /**
     * Defines the left outline of this subtree.
     */
    public static final IProperty<OutlineNode> LEFT_OUTLINE = new Property<OutlineNode>("LEFT_OUTLINE");
    
    /**
     * Defines the right outline of this subtree.
     */
    public static final IProperty<OutlineNode> RIGHT_OUTLINE = new Property<OutlineNode>("RIGHT_OUTLINE");
    
    /**
     * Defines the maximum depth (as a coordinate) 
     */
    public static final IProperty<Double> MAX_DEPTH = new Property<Double>("MAX_DEPTH");

}
