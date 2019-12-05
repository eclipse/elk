/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.radial;

import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * The internal properties of the radial layouter. Properties which are used throughout the code but doesn't need to be
 * public are stored here.
 */
public final class InternalProperties {

    /** The root node of the graph. */
    public static final IProperty<ElkNode> ROOT_NODE = new Property<ElkNode>("root");
}
