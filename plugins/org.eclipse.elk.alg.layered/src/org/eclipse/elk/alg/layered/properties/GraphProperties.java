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
package org.eclipse.elk.alg.layered.properties;

/**
 * An enumeration of properties a graph may have. These can be used as part of an {@code EnumSet} to
 * base decisions on graph properties. For example, self-loop processing may be skipped if the graph
 * doesn't contain self-loops in the first place.
 * 
 * <p>An {@code EnumSet} for this enumeration can be attached to a graph via the
 * {@link Properties#GRAPH_PROPERTIES} property.</p>
 * 
 * <p>{@link org.eclipse.elk.core.options.GraphFeature GraphFeature} serves a similar purpose in KIML.
 * However, since this enumeration is more specific to what KLay Layered supports, the two should stay
 * separate.
 * 
 * @author cds
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public enum GraphProperties {
    
    /** The graph contains comment boxes. */
    COMMENTS,
    /** The graph contains dummy nodes representing external ports. */
    EXTERNAL_PORTS,
    /** The graph contains hyperedges. */
    HYPEREDGES,
    /** The graph contains hypernodes (nodes that are marked as such). */
    HYPERNODES,
    /** The graph contains ports that are not free for positioning. */
    NON_FREE_PORTS,
    /** The graph contains ports on the northern or southern side. */
    NORTH_SOUTH_PORTS,
    /** The graph contains self-loops. */
    SELF_LOOPS,
    /** The graph contains node labels. */
    CENTER_LABELS,
    /** The graph contains head or tail edge labels. */
    END_LABELS;
    
}
