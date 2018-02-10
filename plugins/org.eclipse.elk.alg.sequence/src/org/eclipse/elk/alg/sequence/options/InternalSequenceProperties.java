/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.options;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Internal properties used by the layout algorithm.
 */
public final class InternalSequenceProperties {

    /** The original object from which a graph element was created. */
    public static final IProperty<Object> ORIGIN = new Property<Object>("origin");

    /** The lifeline to which an element of the SGraph belongs. */
    public static final IProperty<SLifeline> BELONGS_TO_LIFELINE = new Property<>(
            "org.eclipse.elk.alg.sequence.belongsToLifeline");

    /** The node in the layered graph that corresponds to a message. */
    public static final IProperty<LNode> LAYERED_NODE = new Property<>(
            "org.eclipse.elk.alg.sequence.layeredNode");

    /** The ElkEdge that connects the comment to another element of the diagram. */
    public static final IProperty<ElkEdge> COMMENT_CONNECTION = new Property<>(
            "org.eclipse.elk.alg.sequence.commentConnection");
    
    /** The ElkNode that represents the destruction event for a lifeline. */
    public static final IProperty<ElkNode> DESTRUCTION_NODE = new Property<>(
            "org.eclipse.elk.alg.sequence.destruction");


    private InternalSequenceProperties() {
        // Hide the constructor
    }
}
