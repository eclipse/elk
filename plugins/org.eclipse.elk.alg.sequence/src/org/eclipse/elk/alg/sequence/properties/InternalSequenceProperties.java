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
package org.eclipse.elk.alg.sequence.properties;

import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.sequence.graph.SLifeline;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Internal properties used by the layout algorithm.
 */
public final class InternalSequenceProperties {

    /** The lifeline to which an element of the SGraph belongs. */
    public static final IProperty<SLifeline> BELONGS_TO_LIFELINE = new Property<SLifeline>(
            "de.cau.cs.kieler.papyrus.sequence.belongsToLifeline");

    /** The node in the layered graph that corresponds to a message. */
    public static final IProperty<LNode> LAYERED_NODE = new Property<LNode>(
            "de.cau.cs.kieler.papyrus.sequence.layeredNode");

    /** The KEdge that connects the comment to another element of the diagram. */
    public static final IProperty<KEdge> COMMENT_CONNECTION = new Property<KEdge>(
            "de.cau.cs.kieler.papyrus.sequence.commentConnection");


    private InternalSequenceProperties() {
        // Hide the constructor
    }
}
